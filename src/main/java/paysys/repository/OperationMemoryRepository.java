package paysys.repository;

import lombok.NonNull;
import paysys.domain.Account;
import paysys.domain.Operation;
import paysys.repository.exception.NotFoundException;
import paysys.repository.exception.RepositoryException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository for {@code Operation} object. Operations are kept in Map in memory
 */
public class OperationMemoryRepository implements OperationRepository {
    /**
     * Stored operations. Key is operation's id.
     */
    private Map<Long, Operation> operations = new ConcurrentHashMap<>();

    /**
     * Сounter for id
     */
    private AtomicLong seq = new AtomicLong(1);


    /**
     * Save operation in repository
     *
     * @param operation Operation for saving
     * @return Copy of saved operation with id
     */
    @Override
    public Operation save(@NonNull Operation operation) {
        Long newId = seq.getAndIncrement();
        Operation result = operation.copyWithId(newId);
        Operation storedOperation = result.copy();
        operations.put(storedOperation.getId(), storedOperation);
        return result;
    }

    /**
     * Get operation by Id
     *
     * @param id Id of needing operation
     * @return {@code Operation} object. if operation was not found return {@code null}
     */
    @Override
    public Operation getById(@NonNull Long id) {
        Operation operation = operations.get(id);
        return operation == null ? null : operation.copy();
    }

    /**
     * Update operation's data in repository
     *
     * @param operation Operation for updating
     * @throws RepositoryException if operation has no id
     */
    @Override
    public void update(@NonNull Operation operation) {
        if (operation.getId() == null) {
            throw new RepositoryException("Cannot save changes - no identifier", Operation.class.getName());
        }
        Operation storeOperation = operations.get(operation.getId());
        if (storeOperation == null) {
            throw new NotFoundException(Account.class.getName(), String.valueOf(operation.getId()));
        }
        synchronized (storeOperation) {
            storeOperation.setStatus(operation.getStatus());
            storeOperation.setMessage(operation.getMessage());
        }
    }
}
