package paysys.service.operation;

import paysys.domain.Operation;

public interface OperationService {
    void setOperationError(Long id, String message);

    void setOperationSuccess(Long id);

    void setOperationProcessing(Long id);

    Operation save(Operation op);

    Operation getById(Long id);
}
