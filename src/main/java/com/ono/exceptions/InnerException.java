package com.ono.exceptions;

import com.ono.enums.StatusCode;

/**
 * Created by amosli on 14/02/2017.
 */
public class InnerException extends RuntimeException {
    private StatusCode statusCode;

    public InnerException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }


}
