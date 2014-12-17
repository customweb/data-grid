package com.customweb.grid.jpa.plugin.filter;

public class FilterException extends Exception {

	private static final long serialVersionUID = 6652488194240318967L;

	public FilterException() {
		super();
	}

	public FilterException(String message) {
		super(message);
	}

	public FilterException(String message, Throwable cause) {
		super(message, cause);
	}

	public FilterException(Throwable cause) {
		super(cause);
	}
}
