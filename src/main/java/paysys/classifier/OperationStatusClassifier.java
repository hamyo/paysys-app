package paysys.classifier;

/**
 * Operation's statuses
 */
public enum OperationStatusClassifier {

    /**
     * Operation created
     */
    CREATE(1),

    /**
     * Operation is being processed
     */
    PROCESSING(2),

    /**
     * Operation finished successfully
     */
    SUCCESS(3),

    /**
     * Operation finished with error
     */
    ERROR(4);

    /**
     * Operation status code
     */
    private int code;

    OperationStatusClassifier(int code) {
        this.code = code;
    }
}
