package com.delivery.exception;

public class AddressChangeNotAllowedException extends RuntimeException {

    public AddressChangeNotAllowedException(String message) {
        super(message);
    }
}
