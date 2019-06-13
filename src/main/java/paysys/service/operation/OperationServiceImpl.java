package paysys.service.operation;

import lombok.NonNull;
import paysys.classifier.OperationStatusClassifier;
import paysys.domain.Operation;
import paysys.repository.OperationRepository;
import paysys.utils.AppException;

/**
 * Service for work with operation
 */
public class OperationServiceImpl implements OperationService {
    /**
     * Operations repository
     */
    private OperationRepository operationRepository;

    /**
     * Constructor for service
     *
     * @param operationRepository Operations repository
     */
    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    /**
     * Sets operation's status to error with message
     *
     * @param id      Operation's id
     * @param message Error message
     */
    @Override
    public void setOperationError(@NonNull Long id, String message) {
        this.setOperationStatus(id, OperationStatusClassifier.ERROR, message);
    }

    /**
     * Sets operation's status to success
     *
     * @param id Operation's id
     */
    @Override
    public void setOperationSuccess(@NonNull Long id) {
        this.setOperationStatus(id, OperationStatusClassifier.SUCCESS, null);
    }

    /**
     * Sets operation's status to processing
     *
     * @param id Operation's id
     */
    @Override
    public void setOperationProcessing(@NonNull Long id) {
        this.setOperationStatus(id, OperationStatusClassifier.PROCESSING, null);
    }

    /**
     * Sets operation's status
     *
     * @param id      Operation's id
     * @param status  Operation's status
     * @param message Operation's message
     */
    private void setOperationStatus(@NonNull Long id, @NonNull OperationStatusClassifier status, String message) {
        Operation op = operationRepository.getById(id);
        if (op == null) {
            throw new AppException("");
        }
        op.setStatus(status);
        op.setMessage(message);
        operationRepository.update(op);
    }

    /**
     * Save new operation in repository
     *
     * @param operation New operation
     * @return Saved operation
     */
    @Override
    public Operation save(@NonNull Operation operation) {
        return operationRepository.save(operation);
    }

    /**
     * Get operation by Id
     *
     * @param id Id of needing operation
     * @return {@code Operation} object
     */
    @Override
    public Operation getById(@NonNull Long id) {
        return operationRepository.getById(id);
    }
}
