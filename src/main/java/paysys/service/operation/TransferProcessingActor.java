package paysys.service.operation;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;
import paysys.domain.Operation;
import paysys.service.account.AccountService;
import paysys.utils.ExceptionUtils;

/**
 * Process operation for transfer money from one account to another
 */
@Slf4j
public class TransferProcessingActor extends AbstractActor {
    /**
     * @param operationService     Operation service
     * @param accountService       Account service
     * @param receiverProcessActor Actor for further processing receiver's operation
     */
    private TransferProcessingActor(OperationService operationService, AccountService accountService,
                                    ActorRef receiverProcessActor) {
        this.operationService = operationService;
        this.accountService = accountService;
        this.receiverProcessActor = receiverProcessActor;
    }

    /**
     * @param operationService     Operation service
     * @param accountService       Account service
     * @param receiverProcessActor Actor for further processing receiver's operation
     * @return ActorRef configuration object
     */
    static public Props props(OperationService operationService, AccountService accountService,
                              ActorRef receiverProcessActor) {
        return Props.create(TransferProcessingActor.class, () -> new TransferProcessingActor(operationService,
                accountService, receiverProcessActor));
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
     * Actor for further processing receiver's operation
     */
    private ActorRef receiverProcessActor;

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
                        Operation receiverOperation = handleMessage(id);

                        getSender().tell(true, getSelf());
                        receiverProcessActor.tell(receiverOperation.getId(), ActorRef.noSender());
                    } catch (Exception ex) {
                        log.error(String.format("Error processing message (id=%s).", id), ex);
                        getSender().tell(new akka.actor.Status.Failure(ex), getSelf());
                        operationService.setOperationError(id, ExceptionUtils.getErrorMessageForConsumers(ex));
                        throw ex;
                    }
                })
                .build();
    }

    /**
     * Message's handling
     *
     * @param id Id of handling operation
     */
    Operation handleMessage(Long id) {
        operationService.setOperationProcessing(id);
        Operation operation = operationService.getById(id);
        accountService.decreaseAccountBalance(operation.getSenderId(), operation.getSum());
        operationService.setOperationSuccess(id);

        // create operation for receiver
        Operation receiverOperation = Operation.ofNewTransfer(operation.getReceverId(),
                operation.getSum(), operation.getSenderId(), operation.getReceverId(), operation.getId());
        return operationService.save(receiverOperation);
    }
}
