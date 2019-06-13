package paysys.utils;

import lombok.Getter;

/**
 * Information for error response
 */
@Getter
public class ErrorResponseInfo {
    /**
     * Information about error
     */
    private String error;

    /**
     * Constructor by error's information
     *
     * @param error Information about error
     */
    private ErrorResponseInfo(String error) {
        this.error = error;
    }

    /**
     * Creates new {@code ErrorResponseInfo} by error's information
     *
     * @param error Information about error
     * @return Information for error response
     */
    public static ErrorResponseInfo of(String error) {
        return new ErrorResponseInfo(error);
    }
}
