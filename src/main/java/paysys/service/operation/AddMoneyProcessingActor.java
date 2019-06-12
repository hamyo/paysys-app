package paysys.service.operation;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import paysys.classifier.OperationStatusClassifier;
import paysys.domain.Account;
import paysys.domain.Operation;
import paysys.repository.AccountRepository;
import paysys.repository.OperationRepository;
import paysys.service.account.AccountService;
import paysys.utils.AppException;
import paysys.utils.ExceptionUtils;

@Slf4j
public class AddMoneyProcessingActor extends AbstractActor {
    private AddMoneyProcessingActor(OperationService operationService, AccountService accountService) {
        this.operationService = operationService;
        this.accountService = accountService;
    }

    static public Props props(OperationService operationService, AccountService accountService) {
        return Props.create(AddMoneyProcessingActor.class, () -> new AddMoneyProcessingActor(operationService,
                accountService));
    }

    private OperationService operationService;
    private AccountService accountService;


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

    void handleMessage(Long operationId) {
        operationService.setOperationProcessing(operationId);
        Operation operation = operationService.getById(operationId);
        accountService.increaseAccountBalance(operation.getAccountId(), operation.getSum());
        operationService.setOperationSuccess(operationId);
    }



}
