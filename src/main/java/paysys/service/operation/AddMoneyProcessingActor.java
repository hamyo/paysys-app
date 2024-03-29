package paysys.service.operation;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import lombok.extern.slf4j.Slf4j;
import paysys.classifier.OperationStatusClassifier;
import paysys.domain.Operation;
import paysys.service.account.AccountService;
import paysys.utils.ExceptionUtils;

/**
 * Process operation for adding money to account
 */
@Slf4j
public class AddMoneyProcessingActor extends AbstractActor {

    /**
     * Constructor for actor
     *
     * @param operationService Operation service
     * @param accountService   Account service
     */
    private AddMoneyProcessingActor(OperationService operationService, AccountService accountService) {
        this.operationService = operationService;
        this.accountService = accountService;
    }

    /**
     * Creates ActorRef configuration object
     *
     * @param operationService Operation service
     * @param accountService   Account service
     * @return ActorRef configuration object
     */
    static public Props props(OperationService operationService, AccountService accountService) {
        return Props.create(AddMoneyProcessingActor.class, () -> new AddMoneyProcessingActor(operationService,
                accountService));
    }

    /**
     * Operation service
     */
    private OperationService operationService;
    /**
     * Account service
     */
    private AccountService accountService;

    /**
     * Creates a receive
     *
     * @return Receive
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Long.class, id -> {
                    try {
                        handleMessage(id);
                        getSender().tell(OperationStatusClassifier.SUCCESS, getSelf());
                    } catch (Exception ex) {
                        log.error(String.format("Error processing message (id=%s).", id), ex);
                        operationService.setOperationError(id, ExceptionUtils.getErrorMessageForConsumers(ex));
                        getSender().tell(new Status.Failure(ex), getSelf());
                        throw ex;
                    }
                })
                .build();
    }

    /**
     * Message's handling
     *
     * @param operationId Id of handling operation
     */
    private void handleMessage(Long operationId) {
        operationService.setOperationProcessing(operationId);
        Operation operation = operationService.getById(operationId);
        accountService.increaseAccountBalance(operation.getAccountId(), operation.getSum());
        operationService.setOperationSuccess(operationId);
    }


}
