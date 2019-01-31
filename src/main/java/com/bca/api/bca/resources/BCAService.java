package com.bca.api.bca.resources;

import com.bca.api.bca.client.BCAClient;
import com.bca.api.bca.credentials.*;
import com.bca.api.bca.api.*;
import com.bca.api.bca.response.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import com.bca.api.bca.api.APIResponse;
import com.bca.api.bca.payload.BalanceInformation;
import com.bca.api.bca.payload.VirtualAccount;
import com.bca.api.bca.utils.Utils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.HttpHeaders;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.client.Client;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/bca")
public class BCAService {
	private static Client client;
	private final static Logger logger = LoggerFactory.getLogger(BCAClient.class);

	public BCAService(Client client) {
		this.client = client;
	}

	// get token from BCAClient class
	String accessToken = BCAClient.getToken();

	@GET
	@Path("balance-information")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBalanceInformation() {

		// call the APIResponse class
		APIResponse result = new APIResponse();

		// initiate response result with null value
		Response response = null;

		// call the Timestamp ISO 8601 method
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String formatTimeISO = BCAClient.formatTimestamp(timestamp);

		// BCA End point that wants to called
		String relativeUrl = "/banking/v3/corporates/" + BalanceInformation.CorporateID + "/accounts/"
				+ BalanceInformation.AccountNumber;

		// input the requestBody or payload
		JSONObject payload = new JSONObject();

		payload.put("CorporateID", BalanceInformation.CorporateID);
		payload.put("AccountNumber", BalanceInformation.AccountNumber);

		// Get stringToSign as requirement to get signature
		String stringToSign = BCAClient.stringToSignFormat("GET", relativeUrl, accessToken, payload.toString(),
				formatTimeISO);

		// Get the signature as requirement to get response from BCA endpoint
		String signature = BCAClient.signature(Credentials.apiSecret, stringToSign);

		// input headers
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		headers.add("X-BCA-Key", Credentials.apiKey);
		headers.add("X-BCA-Timestamp", formatTimeISO);
		headers.add("X-BCA-Signature", signature);

		// target Endpoint
		WebTarget target = client.target(Credentials.host + relativeUrl);

		response = target.request().headers(headers).method("GET", Entity.text(payload.toString()));

		// print the result/response from BCA endpoint
		String responseString = response.readEntity(String.class);
		JSONObject json = new JSONObject(responseString);

		// to make a result in JSON format
		result.setData(json.toMap());
		result.setStatus(SuccessCodes.BALANCE_INFORMATION);

		if (response.getStatus() != 200) {

			logger.error(response.getStatus() + responseString);
			throw new RuntimeException(json.getJSONObject("ErrorMessage").getString("Indonesian"));
		}

		return Response.status(SuccessCodes.BALANCE_INFORMATION.getStatus()).entity(result).build();
	}

	@POST
	@Path("fund-transfer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response fundTransfer(String inputJson) throws JsonParseException, IOException {

		// call the APIResponse class
		APIResponse result = new APIResponse();

		// initiate response result with null value
		Response response = null;

		// call the Timestamp ISO 8601 method
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String formatTimeISO = BCAClient.formatTimestamp(timestamp);

		// BCA End point that wants to called, Inquiry by Request Id
		String relativeUrl = "/banking/corporates/transfers";

		JsonNode objectFormat = Utils.parseJson(inputJson);

		// change format from, e.g -> ""/BCAAPI2016"/" to "BCAAPI2016"
		String CorporateID = Utils.payloadFormat(objectFormat, "CorporateID");
		String SourceAccountNumber = Utils.payloadFormat(objectFormat, "SourceAccountNumber");
		String TransactionID = Utils.payloadFormat(objectFormat, "TransactionID");
		String ReferenceID = Utils.payloadFormat(objectFormat, "ReferenceID");
		String CurrencyCode = Utils.payloadFormat(objectFormat, "CurrencyCode");
		String Amount = Utils.payloadFormat(objectFormat, "Amount");
		String BeneficiaryAccountNumber = Utils.payloadFormat(objectFormat, "BeneficiaryAccountNumber");
		String Remark1 = Utils.payloadFormat(objectFormat, "Remark1");
		String Remark2 = Utils.payloadFormat(objectFormat, "Remark2");

		// input request body
		JSONObject payload = new JSONObject();
		payload.put("CorporateID", CorporateID);
		payload.put("SourceAccountNumber", SourceAccountNumber);
		payload.put("TransactionID", TransactionID);
		payload.put("TransactionDate", formatTimeISO.split("T")[0]);
		payload.put("ReferenceID", ReferenceID);
		payload.put("CurrencyCode", CurrencyCode);
		payload.put("Amount", Amount);
		payload.put("BeneficiaryAccountNumber", BeneficiaryAccountNumber);
		payload.put("Remark1", Remark1);
		payload.put("Remark2", Remark2);

		// Get stringToSign as requirement to get signature
		String stringToSign = BCAClient.stringToSignFormat("POST", relativeUrl, accessToken, payload.toString(),
				formatTimeISO);

		// Get the signature as requirement to get response from BCA endpoint
		String signature = BCAClient.signature(Credentials.apiSecret, stringToSign);

		// input headers
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		headers.add(HttpHeaders.ORIGIN, "https://sandbox.bca.co.id");
		headers.add("X-BCA-Key", Credentials.apiKey);
		headers.add("X-BCA-Timestamp", formatTimeISO);
		headers.add("X-BCA-Signature", signature);

		// target Endpoint
		WebTarget target = client.target(Credentials.host + relativeUrl);

		response = target.request().headers(headers)
				.post(Entity.entity(payload.toString(), MediaType.APPLICATION_JSON));

		String responseString = response.readEntity(String.class);
		JSONObject json = new JSONObject(responseString);

		// to make a result in JSON format
		result.setData(json.toMap());
		result.setStatus(SuccessCodes.FUND_TRANSFER);

		if (response.getStatus() != 200) {

			logger.error(response.getStatus() + responseString);
			throw new RuntimeException(json.getJSONObject("ErrorMessage").getString("Indonesian"));
		}
		return Response.status(SuccessCodes.FUND_TRANSFER.getStatus()).entity(result).build();
	}

	@POST
	@Path("fund-transfer/domestic")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response domesticTransfer(String inputJson) throws JsonParseException, IOException {

		// call the APIResponse class
		APIResponse result = new APIResponse();

		// initiate response result with null value
		Response response = null;

		// call the Timestamp ISO 8601 method
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String formatTimeISO = BCAClient.formatTimestamp(timestamp);

		// BCA End point that wants to called, Inquiry by Request Id
		String relativeUrl = "/banking/corporates/transfers/domestic";

		JsonNode objectFormat = Utils.parseJson(inputJson);

		// change format from, e.g -> ""/BCAAPI2016"/" to "BCAAPI2016"
		String TransactionID = Utils.payloadFormat(objectFormat, "TransactionID");
		String ReferenceID = Utils.payloadFormat(objectFormat, "ReferenceID");
		String SourceAccountNumber = Utils.payloadFormat(objectFormat, "SourceAccountNumber");
		String BeneficiaryAccountNumber = Utils.payloadFormat(objectFormat, "BeneficiaryAccountNumber");
		String BeneficiaryBankCode = Utils.payloadFormat(objectFormat, "BeneficiaryBankCode");
		String BeneficiaryName = Utils.payloadFormat(objectFormat, "BeneficiaryName");
		String Amount = Utils.payloadFormat(objectFormat, "Amount");
		String TransferType = Utils.payloadFormat(objectFormat, "TransferType");
		String BeneficiaryCustType = Utils.payloadFormat(objectFormat, "BeneficiaryCustType");
		String BeneficiaryCustResidence = Utils.payloadFormat(objectFormat, "BeneficiaryCustResidence");
		String CurrencyCode = Utils.payloadFormat(objectFormat, "CurrencyCode");
		String Remark1 = Utils.payloadFormat(objectFormat, "Remark1");
		String Remark2 = Utils.payloadFormat(objectFormat, "Remark2");

		// input request body
		JSONObject payload = new JSONObject();
		payload.put("TransactionID", TransactionID);
		payload.put("TransactionDate", "2018-05-03");
		payload.put("ReferenceID", ReferenceID);
		payload.put("SourceAccountNumber", SourceAccountNumber);
		payload.put("BeneficiaryAccountNumber", BeneficiaryAccountNumber);
		payload.put("BeneficiaryBankCode", BeneficiaryBankCode);
		payload.put("BeneficiaryName", BeneficiaryName);
		payload.put("Amount", Amount);
		payload.put("TransferType", TransferType);
		payload.put("BeneficiaryCustType", BeneficiaryCustType);
		payload.put("BeneficiaryCustResidence", BeneficiaryCustResidence);
		payload.put("CurrencyCode", CurrencyCode);
		payload.put("Remark1", Remark1);
		payload.put("Remark2", Remark2);

		// Get stringToSign as requirement to get signature
		String stringToSign = BCAClient.stringToSignFormat("POST", relativeUrl, accessToken, payload.toString(),
				formatTimeISO);

		// Get the signature as requirement to get response from BCA endpoint
		String signature = BCAClient.signature(Credentials.apiSecret, stringToSign);

		// input headers
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		headers.add(HttpHeaders.ORIGIN, "https://sandbox.bca.co.id");
		headers.add("X-BCA-Key", Credentials.apiKey);
		headers.add("X-BCA-Timestamp", formatTimeISO);
		headers.add("X-BCA-Signature", signature);
		headers.add("ChannelID", "95051");
		headers.add("CredentialID", "BCAAPI");

		// target Endpoint
		WebTarget target = client.target(Credentials.host + relativeUrl);

		response = target.request().headers(headers)
				.post(Entity.entity(payload.toString(), MediaType.APPLICATION_JSON));

		String responseString = response.readEntity(String.class);
		JSONObject json = new JSONObject(responseString);
		json.put("TransactionDate", formatTimeISO.split("T")[0]);
		// json.remove("TransactionDate");
		// json.put("TransactionDate", formatTimeISO.split("T")[0]);

		// to make a result in JSON format
		result.setData(json.toMap());
		result.setStatus(SuccessCodes.FUND_TRANSFER_DOMESTIC);

		if (response.getStatus() != 200) {

			logger.error(response.getStatus() + responseString);
			throw new RuntimeException(json.getJSONObject("ErrorMessage").getString("Indonesian"));
		}

		return Response.status(SuccessCodes.FUND_TRANSFER_DOMESTIC.getStatus()).entity(result).build();
	}

	@GET
	@Path("virtual-account/requestId")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInfoVirtualAccountByRequestId() {

		// call the APIResponse class
		APIResponse result = new APIResponse();

		// initiate response result with null value
		Response response = null;

		// call the Timestamp ISO 8601 method
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String formatTimeISO = BCAClient.formatTimestamp(timestamp);

		// BCA End point that wants to called, Inquiry by Request Id
		String relativeUrl = "/va/payments?" + "CompanyCode=" + VirtualAccount.CompanyCode + "&" + "RequestID="
				+ VirtualAccount.RequestID;

		// input the requestBody or payload
		JSONObject payload = new JSONObject();
		payload.put("CompanyCode", VirtualAccount.CompanyCode);
		payload.put("RequestID", VirtualAccount.RequestID);

		// Get stringToSign as requirement to get signature
		String stringToSign = BCAClient.stringToSignFormat("GET", relativeUrl, accessToken, payload.toString(),
				formatTimeISO);

		// Get the signature as requirement to get response from BCA endpoint
		String signature = BCAClient.signature(Credentials.apiSecret, stringToSign);

		// input headers
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		headers.add("X-BCA-Key", Credentials.apiKey);
		headers.add("X-BCA-Timestamp", formatTimeISO);
		headers.add("X-BCA-Signature", signature);

		// target Endpoint
		WebTarget target = client.target(Credentials.host + relativeUrl);

		response = target.request().headers(headers).method("GET", Entity.text(payload.toString()));

		String responseString = response.readEntity(String.class);
		JSONObject json = new JSONObject(responseString);

		// to make a result in JSON format
		result.setData(json.toMap());
		result.setStatus(SuccessCodes.VIRTUAL_ACCOUNT_INFORMATION);

		if (response.getStatus() != 200) {

			logger.error(response.getStatus() + responseString);
			throw new RuntimeException(json.getJSONObject("ErrorMessage").getString("Indonesian"));
		}

		return Response.status(SuccessCodes.VIRTUAL_ACCOUNT_INFORMATION.getStatus()).entity(result).build();
	}

	@GET
	@Path("virtual-account/customerNumber")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInfoVirtualAccountByCustomerNumber() {

		// call the APIResponse class
		APIResponse result = new APIResponse();

		// initiate response result with null value
		Response response = null;

		// call the Timestamp ISO 8601 method
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String formatTimeISO = BCAClient.formatTimestamp(timestamp);

		// BCA End point that wants to called, Inquiry by Request Id
		String relativeUrl = "/va/payments?" + "CompanyCode=" + VirtualAccount.CompanyCode + "&" + "CustomerNumber="
				+ VirtualAccount.CustomerNumber;

		// input the requestBody or payload
		JSONObject payload = new JSONObject();
		payload.put("CompanyCode", VirtualAccount.CompanyCode);
		payload.put("CustomerNumber", VirtualAccount.CustomerNumber);

		// Get stringToSign as requirement to get signature
		String stringToSign = BCAClient.stringToSignFormat("GET", relativeUrl, accessToken, payload.toString(),
				formatTimeISO);

		// Get the signature as requirement to get response from BCA endpoint
		String signature = BCAClient.signature(Credentials.apiSecret, stringToSign);

		// input headers
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		headers.add("X-BCA-Key", Credentials.apiKey);
		headers.add("X-BCA-Timestamp", formatTimeISO);
		headers.add("X-BCA-Signature", signature);

		// target Endpoint
		WebTarget target = client.target(Credentials.host + relativeUrl);

		response = target.request().headers(headers).method("GET", Entity.text(payload.toString()));

		// to make a result in JSON format
		String responseString = response.readEntity(String.class);
		JSONObject json = new JSONObject(responseString);

		result.setData(json.toMap());
		result.setStatus(SuccessCodes.VIRTUAL_ACCOUNT_INFORMATION);

		if (response.getStatus() != 200) {

			logger.error(response.getStatus() + responseString);
			throw new RuntimeException(json.getJSONObject("ErrorMessage").getString("Indonesian"));
		}

		return Response.status(SuccessCodes.VIRTUAL_ACCOUNT_INFORMATION.getStatus()).entity(result).build();
	}

}
