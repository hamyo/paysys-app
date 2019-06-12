package paysys.service.operation;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import paysys.check.AccountExistsCheck;
import paysys.check.BalanceMoreOrEqualCheck;
import paysys.domain.Operation;
import paysys.utils.AppException;

@Slf4j
public class TransferCreateActor extends AbstractActor {
    private TransferCreateActor(OperationService operationService, BalanceMoreOrEqualCheck balanceMoreOrEqualCheck,
                                AccountExistsCheck accountExistsCheck, ActorRef processActor) {
        this.operationService = operationService;
        this.balanceMoreOrEqualCheck = balanceMoreOrEqualCheck;
        this.accountExistsCheck = accountExistsCheck;
        this.processActor = processActor;
    }

    static public Props props(OperationService operationService, BalanceMoreOrEqualCheck balanceMoreOrEqualCheck,
                              AccountExistsCheck accountExistsCheck, ActorRef processActor) {
        return Props.create(TransferCreateActor.class, () -> new TransferCreateActor(operationService,
                balanceMoreOrEqualCheck, accountExistsCheck, processActor));
    }

    private OperationService operationService;
    private BalanceMoreOrEqualCheck balanceMoreOrEqualCheck;
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
        String checkResult = check(op);
        if (StringUtils.isNotEmpty(checkResult)) {
            throw new AppException(checkResult);
        }

        return operationService.save(op);
    }

    String check(Operation op) {
        String checkResult = accountExistsCheck.check(op.getSenderId());
        if (StringUtils.isNotEmpty(checkResult)) {
            return checkResult;
        } else {
            checkResult = accountExistsCheck.check(op.getReceverId());
            if (StringUtils.isNotEmpty(checkResult)) {
                return checkResult;
            } else {
                return balanceMoreOrEqualCheck.check(op.getSenderId(), op.getSum());
            }
        }
    }
}
