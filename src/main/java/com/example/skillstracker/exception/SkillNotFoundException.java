package com.example.skillstracker.exception;

public class SkillNotFoundException extends RuntimeException {
	
	/**
	 * Default
	 */
	private static final long serialVersionUID = 1L;

	public SkillNotFoundException(Long id) {
	    super("Could not find skill " + id);
	  }

}
