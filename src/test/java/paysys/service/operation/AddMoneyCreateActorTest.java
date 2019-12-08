package paysys.service.operation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;
import paysys.check.AccountExistsCheck;
import paysys.domain.Operation;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddMoneyCreateActorTest extends TestKit {
    private static ActorSystem system = ActorSystem.create();

    public AddMoneyCreateActorTest() {
        super(system);
    }

    @Test
    public void addSum10ToAccount() {
        Operation expectedOp = Operation.ofNewAddMoney(1L, BigDecimal.TEN).copyWithId(1L);
        OperationService operationService = mock(OperationService.class);
        when(operationService.save(expectedOp)).thenReturn(expectedOp);
        AccountExistsCheck accountExistsCheck = mock(AccountExistsCheck.class);
        when(accountExistsCheck.check(1L)).thenReturn("");

        final Props props = AddMoneyCreateActor.props(operationService, accountExistsCheck, super.getTestActor());
        final ActorRef subject = system.actorOf(props);
        subject.tell(expectedOp, getRef());
        expectMsg(expectedOp);
    }
}