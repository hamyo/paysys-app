package paysys.utils;

import lombok.NonNull;

/**
 * The {@code ExceptionUtils} is a helper for exceptions
 */
public abstract class ExceptionUtils {
    /**
     * Internal error text
     */
    private static final String INTERNAL_ERROR_TEXT = "Внутренняя ошибка";

    /**
     * Get error message for api consumers
     *
     * @param ex Exception
     * @return Message for consumers
     */
    public static String getErrorMessageForConsumers(@NonNull Exception ex) {
        return ex instanceof AppException ? ex.getMessage() : INTERNAL_ERROR_TEXT;
    }
}
