package paysys.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import paysys.classifier.OperationStatusClassifier;
import paysys.classifier.OperationTypeClassifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account's operation
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Operation {
    protected Long id;
    private LocalDateTime createTime;
    private Long accountId;
    private OperationStatusClassifier status;
    private OperationTypeClassifier type;
    private BigDecimal sum;
    private String message;
    private Long senderId;
    private Long receverId;
    private Long parentOperationId;

    protected Operation(LocalDateTime createTime, Long accountId, OperationStatusClassifier status,
                        OperationTypeClassifier type, BigDecimal sum) {
        this.createTime = createTime;
        this.accountId = accountId;
        this.status = status;
        this.type = type;
        this.sum = sum;
    }

    protected Operation(LocalDateTime createTime, Long accountId, OperationStatusClassifier status,
                        OperationTypeClassifier type, BigDecimal sum, Long senderId, Long receverId, Long parentOperationId) {
        this.createTime = createTime;
        this.accountId = accountId;
        this.status = status;
        this.type = type;
        this.sum = sum;
        this.senderId = senderId;
        this.receverId = receverId;
        this.parentOperationId = parentOperationId;
    }

    public static Operation ofNewAddMoney(Long accountId, BigDecimal sum) {
        return new Operation(LocalDateTime.now(), accountId, OperationStatusClassifier.CREATE,
                OperationTypeClassifier.ADD_MONEY, sum);
    }

    public static Operation ofNewTransfer(Long accountId, BigDecimal sum, Long senderId, Long receverId,
                                          Long parentOperationId) {
        return new Operation(LocalDateTime.now(), accountId, OperationStatusClassifier.CREATE,
                OperationTypeClassifier.TRANSFER, sum, senderId, receverId, parentOperationId);
    }

    public Operation copy() {
        return copyWithId(this.id);
    }

    public Operation copyWithId(Long id) {
        return new Operation(id, this.createTime, this.accountId, this.status, this.type,
                this.sum, this.message, this.senderId, this.receverId, this.parentOperationId);

    }

    public void setStatus(OperationStatusClassifier status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
