package com.example.skillstracker.exception;

public class PersonNotFoundException extends RuntimeException {

	/**
	 * Default
	 */
	private static final long serialVersionUID = 1L;

	public PersonNotFoundException(Long id) {
	    super("Could not find person " + id);
	  }
}
