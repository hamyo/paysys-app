package paysys.service.operation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;
import paysys.classifier.OperationStatusClassifier;
import paysys.domain.Operation;
import paysys.service.account.AccountService;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddMoneyProcessingActorTest extends TestKit {
    private static ActorSystem system = ActorSystem.create();

    public AddMoneyProcessingActorTest() {
        super(system);
    }

    @Test
    public void test_ACCOUNT1_SUM10_Success() {
        Operation operation = Operation.ofNewAddMoney(1L, BigDecimal.TEN).copyWithId(1L);
        OperationService operationService = mock(OperationService.class);
        when(operationService.getById(1L)).thenReturn(operation);
        AccountService accountService = mock(AccountService.class);

        final Props props = AddMoneyProcessingActor.props(operationService, accountService);
        final ActorRef subject = system.actorOf(props);
        subject.tell(operation.getId(), getRef());
        expectMsg(OperationStatusClassifier.SUCCESS);
    }
}