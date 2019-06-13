package paysys;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import paysys.check.AccountExistsCheck;
import paysys.check.BalanceMoreOrEqualCheck;
import paysys.repository.AccountMemoryRepository;
import paysys.repository.AccountRepository;
import paysys.repository.OperationMemoryRepository;
import paysys.repository.OperationRepository;
import paysys.service.account.AccountService;
import paysys.service.account.AccountServiceImpl;
import paysys.service.operation.*;

/**
 * Implementation of injection binder with convenience methods for
 * binding definitions.
 */
public class ApplicationBinder extends AbstractBinder {

    /**
     * Implement to provide binding definitions using the exposed binding
     * methods.
     */
    @Override
    protected void configure() {
        // repository
        AccountRepository accRepo = new AccountMemoryRepository();
        OperationRepository opRepo = new OperationMemoryRepository();
        bind(opRepo).to(OperationRepository.class);
        bind(accRepo).to(AccountRepository.class);

        // service
        AccountService accountService = new AccountServiceImpl(accRepo);
        bind(accountService).to(AccountService.class);
        OperationService operationService = new OperationServiceImpl(opRepo);

        // check
        BalanceMoreOrEqualCheck balanceMoreOrEqualCheck = new BalanceMoreOrEqualCheck(accRepo);
        AccountExistsCheck accountExistsCheck = new AccountExistsCheck(accRepo);

        // actors
        ActorSystem system = ActorSystem.create("paysys");

        ActorRef processAddMoney = system.actorOf(AddMoneyProcessingActor.props(operationService, accountService), "processAddMoney");
        system.actorOf(AddMoneyCreateActor.props(operationService, accountExistsCheck, processAddMoney), "createAddMoney");

        ActorRef processActor = system.actorOf(TransferProcessingActor.props(operationService, accountService,
                processAddMoney), "processTransfer");
        system.actorOf(TransferCreateActor.props(operationService, balanceMoreOrEqualCheck, accountExistsCheck,
                processActor), "createTransfer");

        bind(system).to(ActorSystem.class);
    }
}
