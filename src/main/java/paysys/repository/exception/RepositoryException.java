package paysys.repository.exception;

/**
 * Exception for repository error
 */
public class RepositoryException extends RuntimeException {
    /**
     * Repository's entity
     */
    protected String entity;

    /**
     * Constructs a new exception by message
     *
     * @param message Error message
     * @param entity  Entity's name
     */
    public RepositoryException(String message, String entity) {
        super(message);
        this.entity = entity;
    }

    /**
     * Returns the detail message string of this exception.
     *
     * @return detail message string
     */
    @Override
    public String getMessage() {
        return String.format("%s(entity: %s)", super.getMessage(), entity);
    }
}
