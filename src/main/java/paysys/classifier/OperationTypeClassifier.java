package paysys.classifier;

public enum OperationTypeClassifier {
    /**
     * Money transfer
     */
    TRANSFER(1),
    /**
     * Money adding to balance
     */
    ADD_MONEY(2);
    /**
     * Operation type code
     */
    private int code;

    OperationTypeClassifier(int code) {
        this.code = code;
    }
}
