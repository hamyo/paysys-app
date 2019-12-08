package paysys.service.operation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.junit.Test;
import paysys.check.AccountExistsCheck;
import paysys.check.BalanceMoreOrEqualCheck;
import paysys.domain.Operation;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransferCreateActorTest extends TestKit {
    private static ActorSystem system = ActorSystem.create();

    public TransferCreateActorTest() {
        super(system);
    }

    @Test
    public void transferSum10() {
        Operation expectedOp = Operation.ofNewTransfer(1L, BigDecimal.TEN, 1L, 2L, null)
                .copyWithId(1L);
        OperationService operationService = mock(OperationService.class);
        when(operationService.save(expectedOp)).thenReturn(expectedOp);
        AccountExistsCheck accountExistsCheck = mock(AccountExistsCheck.class);
        when(accountExistsCheck.check(1L)).thenReturn("");
        BalanceMoreOrEqualCheck balanceCheck = mock(BalanceMoreOrEqualCheck.class);
        when(balanceCheck.check(1L, BigDecimal.TEN)).thenReturn("");

        final Props props = TransferCreateActor.props(operationService, balanceCheck, accountExistsCheck, super.getTestActor());
        final ActorRef subject = system.actorOf(props);
        subject.tell(expectedOp, getRef());
        expectMsg(expectedOp);
    }
}