package paysys.service.operation;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import paysys.check.AccountExistsCheck;
import paysys.domain.Operation;
import paysys.utils.AppException;

@Slf4j
public class AddMoneyCreateActor extends AbstractActor {
    private AddMoneyCreateActor(OperationService operationService, AccountExistsCheck accountExistsCheck,
                                ActorRef processActor) {
        this.operationService = operationService;
        this.accountExistsCheck = accountExistsCheck;
        this.processActor = processActor;
    }

    static public Props props(OperationService operationService, AccountExistsCheck accountExistsCheck,
                              ActorRef processActor) {
        return Props.create(AddMoneyCreateActor.class, () -> new AddMoneyCreateActor(operationService,
                accountExistsCheck, processActor));
    }

    private OperationService operationService;
    private AccountExistsCheck accountExistsCheck;
    private ActorRef processActor;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Operation.class, op -> {
                    try {
                        Operation savedOp = handleMessage(op);
                        getSender().tell(savedOp, getSelf());
                        processActor.tell(savedOp.getId(), ActorRef.noSender());
                    } catch (Exception ex) {
                        log.error(String.format("Error processing message(%s).", op), ex);
                        getSender().tell(new akka.actor.Status.Failure(ex), getSelf());
                        throw ex;
                    }
                })
                .build();
    }

    Operation handleMessage(Operation op) {
        String res = check(op);
        if (StringUtils.isNotEmpty(res)) {
            throw new AppException(res);
        }

        return operationService.save(op);
    }

    String check(Operation op) {
        return accountExistsCheck.check(op.getAccountId());
    }
}
