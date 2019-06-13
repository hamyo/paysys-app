package paysys.service.operation;

import paysys.domain.Operation;

/**
 * Implementing this interface allows an object to work with {@code Operation}
 */
public interface OperationService {
    /**
     * Sets operation's status to error with message
     * @param id Operation's id
     * @param message Error message
     */
    void setOperationError(Long id, String message);

    /**
     * Sets operation's status to success
     * @param id Operation's id
     */
    void setOperationSuccess(Long id);

    /**
     * Sets operation's status to processing
     * @param id Operation's id
     */
    void setOperationProcessing(Long id);

    /**
     * Save operation in repository
     * @param operation Operation for saving
     * @return Saved operation
     */
    Operation save(Operation operation);

    /**
     * Get operation by Id
     * @param id Id of needing operation
     * @return {@code Operation} object
     */
    Operation getById(Long id);
}
