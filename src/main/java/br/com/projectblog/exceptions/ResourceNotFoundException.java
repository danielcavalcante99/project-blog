package br.com.projectblog.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1731434106607832506L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
