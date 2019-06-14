package paysys.service.operation;

import org.junit.Assert;
import org.junit.Test;
import paysys.classifier.OperationStatusClassifier;
import paysys.classifier.OperationTypeClassifier;
import paysys.domain.Operation;
import paysys.repository.OperationMemoryRepository;
import paysys.repository.OperationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationServiceImplTest {

    @Test
    public void setOperationError() {
        String errMessage = "Error";
        OperationService operationService = new OperationServiceImpl(new OperationMemoryRepository());
        Operation savedOperation = operationService.save(Operation.ofNewAddMoney(1L, BigDecimal.TEN));
        operationService.setOperationError(savedOperation.getId(), errMessage);
        savedOperation = operationService.getById(savedOperation.getId());
        Assert.assertTrue(savedOperation.getMessage().equals(errMessage)
                && savedOperation.getStatus().equals(OperationStatusClassifier.ERROR));
    }

    @Test
    public void setOperationSuccess() {
        OperationService operationService = new OperationServiceImpl(new OperationMemoryRepository());
        Operation savedOperation = operationService.save(Operation.ofNewAddMoney(1L, BigDecimal.TEN));
        operationService.setOperationSuccess(savedOperation.getId());
        savedOperation = operationService.getById(savedOperation.getId());
        assertEquals(savedOperation.getStatus(), OperationStatusClassifier.SUCCESS);
    }

    @Test
    public void setOperationProcessing() {
        OperationService operationService = new OperationServiceImpl(new OperationMemoryRepository());
        Operation savedOperation = operationService.save(Operation.ofNewAddMoney(1L, BigDecimal.TEN));
        operationService.setOperationProcessing(savedOperation.getId());
        savedOperation = operationService.getById(savedOperation.getId());
        assertEquals(savedOperation.getStatus(), OperationStatusClassifier.PROCESSING);
    }

    @Test
    public void getById_ID1_Exist() {
        LocalDateTime nowDt = LocalDateTime.now();
        Operation expected = new Operation(1L, nowDt, 1L, OperationStatusClassifier.CREATE,
                OperationTypeClassifier.TRANSFER, BigDecimal.TEN, null, null, null,
                null);
        OperationRepository operationRepository = mock(OperationRepository.class);
        when(operationRepository.getById(1L)).thenReturn(new Operation(1L, nowDt, 1L,
                OperationStatusClassifier.CREATE, OperationTypeClassifier.TRANSFER, BigDecimal.TEN, null,
                null, null, null));
        OperationService operationService = new OperationServiceImpl(operationRepository);

        Operation actual = operationService.getById(expected.getId());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getById_ID2_NotExist() {
        OperationRepository operationRepository = mock(OperationRepository.class);
        when(operationRepository.getById(1L)).thenReturn(new Operation(1L, LocalDateTime.now(), 1L,
                OperationStatusClassifier.CREATE, OperationTypeClassifier.TRANSFER, BigDecimal.TEN, null,
                null, null, null));
        OperationService operationService = new OperationServiceImpl(operationRepository);

        Operation actual = operationService.getById(2L);
        Assert.assertNull(actual);
    }
}