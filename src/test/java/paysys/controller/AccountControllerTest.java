package paysys.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.junit.Assert;
import org.junit.Test;
import paysys.check.AccountExistsCheck;
import paysys.check.BalanceMoreOrEqualCheck;
import paysys.classifier.OperationStatusClassifier;
import paysys.domain.Account;
import paysys.domain.Operation;
import paysys.repository.AccountMemoryRepository;
import paysys.repository.AccountRepository;
import paysys.repository.OperationMemoryRepository;
import paysys.repository.OperationRepository;
import paysys.service.account.AccountService;
import paysys.service.account.AccountServiceImpl;
import paysys.service.operation.*;
import paysys.utils.PropertiesUtils;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountControllerTest {

    @Test
    public void create_Email() {
        AccountService accountService = mock(AccountService.class);
        Account expected = new Account(1L, BigDecimal.ZERO, "address@gmial.com");
        when(accountService.create("address@gmial.com")).thenReturn(expected);
        ActorSystem system = mock(ActorSystem.class);
        Configuration configuration = mock(Configuration.class);
        AccountController accountController = new AccountController(accountService, system, configuration);
        Response resp = accountController.create("address@gmial.com");
        Object actual = resp.getEntity();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void get_ID1_Exists() {
        AccountService accountService = mock(AccountService.class);
        Account expected = new Account(1L, BigDecimal.TEN, "address@gmial.com");
        when(accountService.getById(1L)).thenReturn(expected);
        ActorSystem system = mock(ActorSystem.class);
        Configuration configuration = mock(Configuration.class);
        AccountController accountController = new AccountController(accountService, system, configuration);
        Response resp = accountController.get(1L);
        Object actual = resp.getEntity();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void get_ID1_NotExists() {
        AccountService accountService = mock(AccountService.class);
        when(accountService.getById(1L)).thenReturn(null);
        ActorSystem system = mock(ActorSystem.class);
        Configuration configuration = mock(Configuration.class);
        AccountController accountController = new AccountController(accountService, system, configuration);
        Response resp = accountController.get(1L);
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }

    @Test
    public void transfer() {
        AccountRepository accountRepository = new AccountMemoryRepository();
        AccountService accountService = new AccountServiceImpl(accountRepository);
        OperationRepository opRepo = new OperationMemoryRepository();
        OperationService operationService = new OperationServiceImpl(opRepo);
        AccountExistsCheck accountExistsCheck = mock(AccountExistsCheck.class);
        BalanceMoreOrEqualCheck balanceMoreOrEqualCheck = mock(BalanceMoreOrEqualCheck.class);

        ActorSystem system = ActorSystem.create("paysys");
        ActorRef processAddMoney = system.actorOf(AddMoneyProcessingActor.props(operationService, accountService), "processAddMoney");
        ActorRef processActor = system.actorOf(TransferProcessingActor.props(operationService, accountService,
                processAddMoney), "processTransfer");
        system.actorOf(TransferCreateActor.props(operationService, balanceMoreOrEqualCheck, accountExistsCheck,
                processActor), "createTransfer");

        Account sender = accountService.create("address@gmial.com");
        accountService.increaseAccountBalance(sender.getId(), BigDecimal.TEN);
        Account receiver = accountService.create("address@gmial.com");

        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PropertiesUtils.getOperationTimeoutParamName())).thenReturn(5L);
        AccountController accountController = new AccountController(accountService, system, configuration);
        Response resp = accountController.transfer(sender.getId(), BigDecimal.TEN, receiver.getId());
        Assert.assertEquals(OperationStatusClassifier.CREATE, ((Operation) resp.getEntity()).getStatus());
//        try {
//            // A pause is needed because the operation is processed asynchronously.
//            Thread.currentThread().sleep(5000);
//        } catch (Exception ex) {
//            throw new RuntimeException("Thread sleep error", ex);
//        }
//        Account actualSender = accountService.getById(sender.getId());
//        Account actualReceiver = accountService.getById(receiver.getId());
//        Assert.assertTrue(actualSender.getBalance().compareTo(BigDecimal.ZERO) == 0
//                && actualReceiver.getBalance().compareTo(BigDecimal.TEN) == 0);
    }

    @Test
    public void addMoney() {
        AccountRepository accountRepository = new AccountMemoryRepository();
        AccountService accountService = new AccountServiceImpl(accountRepository);
        OperationRepository opRepo = new OperationMemoryRepository();
        OperationService operationService = new OperationServiceImpl(opRepo);
        AccountExistsCheck accountExistsCheck = mock(AccountExistsCheck.class);

        ActorSystem system = ActorSystem.create("paysys");
        ActorRef processAddMoney = system.actorOf(AddMoneyProcessingActor.props(operationService, accountService), "processAddMoney");
        system.actorOf(AddMoneyCreateActor.props(operationService, accountExistsCheck, processAddMoney), "createAddMoney");

        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PropertiesUtils.getOperationTimeoutParamName())).thenReturn(5L);

        Account account = accountService.create("address@gmial.com");
        AccountController accountController = new AccountController(accountService, system, configuration);
        Response resp = accountController.addMoney(account.getId(), BigDecimal.TEN);
        Assert.assertEquals(OperationStatusClassifier.CREATE, ((Operation) resp.getEntity()).getStatus());
//        try {
//            Account expected = new Account(account.getId(), BigDecimal.TEN, account.getEmail());
//            // A pause is needed because the operation is processed asynchronously.
//            Thread.currentThread().sleep(5000);
//        } catch (Exception ex) {
//            throw new RuntimeException("Thread sleep error", ex);
//        }
//        Account actual = accountService.getById(account.getId());
//        Assert.assertEquals(expected, actual);
    }
}