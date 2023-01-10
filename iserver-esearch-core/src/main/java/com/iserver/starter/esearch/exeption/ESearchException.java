package com.iserver.starter.esearch.exeption;

/**
 * 异常
 *
 * @author Alay
 * @date 2023-01-10 18:14
 */
public class ESearchException extends RuntimeException {


    public ESearchException(String message) {
        super(message);
    }


    public ESearchException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
