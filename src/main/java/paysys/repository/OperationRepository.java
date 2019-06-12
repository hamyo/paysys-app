package paysys.repository;

import paysys.domain.Operation;

/**
 * Implementing this interface allows an object to be the repository for {@code Operation} object
 */
public interface OperationRepository {

    /**
     * Save account in repository
     *
     * @param operation Account for saving
     * @return Saved operation
     */
    Operation save(Operation operation);

    /**
     * Get operation by Id
     *
     * @param id Id of needing operation
     * @return {@code Operation} object
     */
    Operation getById(Long id);

    /**
     * Update operation's data in repository
     *
     * @param operation Operation for updating
     */
    void update(Operation operation);
}
