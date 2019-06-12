package paysys.repository.exception;

/**
 * Exception when repository's entity is not found.
 */
public class NotFoundException extends RepositoryException {
    /**
     * Key value
     */
    private String key;

    /**
     * Constructs a new exception by message
     * @param entity Entity's name
     * @param key Key value
     */
    public NotFoundException(String entity, String key) {
        super("Object not found", entity);
        this.key = key;
    }

    /**
     * Returns the detail message string of this exception.
     * @return  detail message string
     */
    @Override
    public String getMessage() {
        return String.format("%s(entity: %s, key: %s)", super.getMessage(), entity, key);
    }
}
