package paysys.service.operation;

import lombok.NonNull;
import paysys.classifier.OperationStatusClassifier;
import paysys.domain.Operation;
import paysys.repository.OperationRepository;
import paysys.utils.AppException;

public class OperationServiceImpl implements OperationService {
    private OperationRepository operationRepository;

    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public void setOperationError(@NonNull Long id, String message) {
        this.setOperationStatus(id, OperationStatusClassifier.ERROR, message);
    }

    @Override
    public void setOperationSuccess(@NonNull Long id) {
        this.setOperationStatus(id, OperationStatusClassifier.SUCCESS, null);
    }

    @Override
    public void setOperationProcessing(@NonNull Long id) {
        this.setOperationStatus(id, OperationStatusClassifier.PROCESSING, null);
    }

    private void setOperationStatus(@NonNull Long id, @NonNull OperationStatusClassifier status, String message) {
        Operation op = operationRepository.getById(id);
        if (op == null) {
            throw new AppException("");
        }
        op.setStatus(status);
        op.setMessage(message);
        operationRepository.update(op);
    }

    @Override
    public Operation save(@NonNull Operation op) {
        return operationRepository.save(op);
    }

    @Override
    public Operation getById(Long id) {
        return operationRepository.getById(id);
    }
}
