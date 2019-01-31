package com.bca.api.bca.api;

public class Status {
	private Integer code;
	private String message;
	
	public Status() {
		this.code = null;
		this.message = null;
	}
	
	public Status(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public void setStatusCode(Integer code) {
		this.code = code;
	}
	
	public void setStatusMessage(String description) {
		this.message = description;
	}
	
	public Integer getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}
}
