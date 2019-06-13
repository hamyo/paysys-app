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
@EqualsAndHashCode(exclude = {"createTime"})
@Getter
@AllArgsConstructor
public class Operation {
    /**
     * Operation's identifier
     */
    protected Long id;
    /**
     * Creation's date with time
     */
    private LocalDateTime createTime;
    /**
     * Account's identifier
     */
    private Long accountId;
    /**
     * Operation's status
     */
    private OperationStatusClassifier status;
    /**
     * Operation's type
     */
    private OperationTypeClassifier type;
    /**
     * Operation's sum
     */
    private BigDecimal sum;
    /**
     * Operation's message
     */
    private String message;
    /**
     * Sender account identifier
     */
    private Long senderId;
    /**
     * Receiver account identifier
     */
    private Long receverId;
    /**
     * Parent operation's identifier
     */
    private Long parentOperationId;

    /**
     * Constructor for new operation
     *
     * @param createTime Creation's date with time
     * @param accountId  Account's identifier
     * @param status     Operation's status
     * @param type       Operation's type
     * @param sum        Operation's sum
     */
    protected Operation(LocalDateTime createTime, Long accountId, OperationStatusClassifier status,
                        OperationTypeClassifier type, BigDecimal sum) {
        this.createTime = createTime;
        this.accountId = accountId;
        this.status = status;
        this.type = type;
        this.sum = sum;
    }

    /**
     * Constructor for new operation
     *
     * @param createTime        Creation's date with time
     * @param accountId         Account's identifier
     * @param status            Operation's status
     * @param type              Operation's type
     * @param sum               Operation's sum
     * @param senderId          Sender account identifier
     * @param receverId         Receiver account identifier
     * @param parentOperationId Parent operation's identifier
     */
    protected Operation(LocalDateTime createTime, Long accountId, OperationStatusClassifier status,
                        OperationTypeClassifier type, BigDecimal sum, Long senderId, Long receverId,
                        Long parentOperationId) {
        this.createTime = createTime;
        this.accountId = accountId;
        this.status = status;
        this.type = type;
        this.sum = sum;
        this.senderId = senderId;
        this.receverId = receverId;
        this.parentOperationId = parentOperationId;
    }

    /**
     * Creates new operation for adding money to account
     *
     * @param accountId Account's identifier
     * @param sum       Operation's sum
     * @return New operation
     */
    public static Operation ofNewAddMoney(Long accountId, BigDecimal sum) {
        return new Operation(LocalDateTime.now(), accountId, OperationStatusClassifier.CREATE,
                OperationTypeClassifier.ADD_MONEY, sum);
    }

    /**
     * Creates new operation for transfer money from one account to another
     *
     * @param accountId         Account's identifier
     * @param sum               Operation's sum
     * @param senderId          Sender account identifier
     * @param receverId         Receiver account identifier
     * @param parentOperationId Parent operation's identifier
     * @return New operation
     */
    public static Operation ofNewTransfer(Long accountId, BigDecimal sum, Long senderId, Long receverId,
                                          Long parentOperationId) {
        return new Operation(LocalDateTime.now(), accountId, OperationStatusClassifier.CREATE,
                OperationTypeClassifier.TRANSFER, sum, senderId, receverId, parentOperationId);
    }

    /**
     * Copies data to new object
     *
     * @return New operation
     */
    public Operation copy() {
        return copyWithId(this.id);
    }

    /**
     * Copies data to new object with specified ID
     *
     * @param id Operation's id
     * @return New object with specified ID
     */
    public Operation copyWithId(Long id) {
        return new Operation(id, this.createTime, this.accountId, this.status, this.type,
                this.sum, this.message, this.senderId, this.receverId, this.parentOperationId);

    }

    /**
     * Sets new status for operation
     *
     * @param status New status
     */
    public void setStatus(OperationStatusClassifier status) {
        this.status = status;
    }

    /**
     * Sets message for operation
     *
     * @param message Message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
