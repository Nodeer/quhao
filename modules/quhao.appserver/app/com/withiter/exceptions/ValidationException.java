package com.withiter.exceptions;

import play.data.validation.Validation;

/**
 * All data invalidate issue should throw this exception
 * 
 */
public class ValidationException extends RuntimeException {
	@Override
	public String getMessage() {
		Validation validation = Validation.current();
		return validation.errorsMap().toString();
	}
}
