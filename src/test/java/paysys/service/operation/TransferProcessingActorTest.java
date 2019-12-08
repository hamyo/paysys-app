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

public class TransferProcessingActorTest extends TestKit {
    private static ActorSystem system = ActorSystem.create();

    public TransferProcessingActorTest() {
        super(system);
    }

    @Test
    public void transferSum10() {
        Operation operation = Operation.ofNewTransfer(1L, BigDecimal.TEN, 1L, 2L, null)
                .copyWithId(1L);
        Operation recOperation = Operation.ofNewTransfer(2L, BigDecimal.TEN, 1L, 2L, operation.getId());

        OperationService operationService = mock(OperationService.class);
        when(operationService.getById(1L)).thenReturn(operation);
        when(operationService.save(recOperation)).thenReturn(recOperation.copyWithId(2L));
        AccountService accountService = mock(AccountService.class);

        final Props props = TransferProcessingActor.props(operationService, accountService, super.getTestActor());
        final ActorRef subject = system.actorOf(props);
        subject.tell(operation.getId(), getRef());
        expectMsg(OperationStatusClassifier.SUCCESS);
    }

}