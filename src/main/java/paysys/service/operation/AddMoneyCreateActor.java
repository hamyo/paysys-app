package paysys.service.operation;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import paysys.check.AccountExistsCheck;
import paysys.domain.Operation;
import paysys.utils.AppException;

/**
 * Creates new operation for adding money to account
 */
@Slf4j
public class AddMoneyCreateActor extends AbstractActor {
    /**
     * Constructor for actor
     *
     * @param operationService   Operation service
     * @param accountExistsCheck Account exist check
     * @param processActor       Actor for further processing
     */
    private AddMoneyCreateActor(OperationService operationService, AccountExistsCheck accountExistsCheck,
                                ActorRef processActor) {
        this.operationService = operationService;
        this.accountExistsCheck = accountExistsCheck;
        this.processActor = processActor;
    }

    /**
     * Creates ActorRef configuration object
     *
     * @param operationService   Operation service
     * @param accountExistsCheck Account exist check
     * @param processActor       Actor for further processing
     * @return ActorRef configuration object
     */
    static public Props props(OperationService operationService, AccountExistsCheck accountExistsCheck,
                              ActorRef processActor) {
        return Props.create(AddMoneyCreateActor.class, () -> new AddMoneyCreateActor(operationService,
                accountExistsCheck, processActor));
    }

    /**
     * Operation service
     */
    private OperationService operationService;

    /**
     * Account exist check
     */
    private AccountExistsCheck accountExistsCheck;

    /**
     * Actor for further processing
     */
    private ActorRef processActor;

    /**
     * Creates a receive
     *
     * @return Receive
     */
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

    /**
     * Message's handling
     *
     * @param operation Handling operation
     * @return Saved operation
     */
    private Operation handleMessage(Operation operation) {
        String res = check(operation);
        if (StringUtils.isNotEmpty(res)) {
            throw new AppException(res);
        }

        return operationService.save(operation);
    }

    /**
     * Internal check
     *
     * @param operation Handling operation
     * @return Check's error
     */
    private String check(Operation operation) {
        return accountExistsCheck.check(operation.getAccountId());
    }
}
