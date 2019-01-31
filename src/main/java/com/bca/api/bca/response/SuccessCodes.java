package com.bca.api.bca.response;

import javax.ws.rs.core.Response;

public enum SuccessCodes {
	
	BALANCE_INFORMATION(1, Response.Status.OK, "balance information"),
	FUND_TRANSFER(2, Response.Status.OK, "sukses transfer rekening bca"),
	FUND_TRANSFER_DOMESTIC(3, Response.Status.OK, "sukses transfer ke bank lain"),
	VIRTUAL_ACCOUNT_INFORMATION(4, Response.Status.OK, "informasi saldo virtual account");
	
	private final Integer id;
	private final String message;
	private final Response.Status status;
	
	SuccessCodes(int id, Response.Status status, String message){
		this.id = id;
		this.message = message;
		this.status = status;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public Response.Status getStatus(){
		return this.status;
	}
}
