package com.bca.api.bca.api;

import com.bca.api.bca.response.SuccessCodes;

public class APIResponse {
	private Status status;
	private Object result;
	
	public APIResponse() {
		this.status = new Status();
		this.result = null;
	}
	
	public APIResponse(Status status, Object data) {
		this.status = status;
		this.result = data;
	}
	
	public APIResponse(Object data) {
		this.status = new Status();
		this.result = data;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setStatus(SuccessCodes code) {
		this.status.setStatusCode(code.getId());
		this.status.setStatusMessage(code.getMessage());
	}
	
	public void setData(Object data) {
		this.result = data;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public Object getResult() {
		return this.result;
	}
	
}
