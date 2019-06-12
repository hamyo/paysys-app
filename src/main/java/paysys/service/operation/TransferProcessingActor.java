package paysys.service.operation;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;
import paysys.domain.Operation;
import paysys.service.account.AccountService;
import paysys.utils.ExceptionUtils;

@Slf4j
public class TransferProcessingActor extends AbstractActor {
    private TransferProcessingActor(OperationService operationService, AccountService accountService,
                                    ActorRef receiverProcessActor) {
        this.operationService = operationService;
        this.accountService = accountService;
        this.receiverProcessActor = receiverProcessActor;
    }

    static public Props props(OperationService operationService, AccountService accountService,
                              ActorRef receiverProcessActor) {
        return Props.create(TransferProcessingActor.class, () -> new TransferProcessingActor(operationService,
                accountService, receiverProcessActor));
    }

    private OperationService operationService;
    private AccountService accountService;
    private ActorRef receiverProcessActor;

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

    Operation handleMessage(Long id) {
        operationService.setOperationProcessing(id);
        Operation operation = operationService.getById(id);
        accountService.decreaseAccountBalance(operation.getSenderId(), operation.getSum());
        operationService.setOperationSuccess(id);

        Operation receiverOperation = Operation.ofNewTransfer(operation.getReceverId(),
                operation.getSum(), operation.getSenderId(), operation.getReceverId(), operation.getId());
        return operationService.save(receiverOperation);
    }
}
