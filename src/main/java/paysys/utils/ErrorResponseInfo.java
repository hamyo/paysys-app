package paysys.utils;

import lombok.Getter;

@Getter
public class ErrorResponseInfo {
    private String error;

    protected ErrorResponseInfo(String error) {
        this.error = error;
    }

    public static ErrorResponseInfo of(String error) {
        return new ErrorResponseInfo(error);
    }
}
