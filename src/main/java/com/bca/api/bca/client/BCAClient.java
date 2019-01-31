package com.bca.api.bca.client;

import com.bca.api.bca.credentials.*;
import com.bca.api.bca.resources.Hash;
import com.google.common.net.HttpHeaders;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Base64;
import java.util.Map;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ProcessingException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BCAClient {
	private static Client client;
	private final static Logger logger = LoggerFactory.getLogger(BCAClient.class);

	public BCAClient(Client client) {
		this.client = client;
	}

	public static String getToken() {
		Response response = null;

		try {
			// hit api get token of BCA
			WebTarget target = client.target(Credentials.host + Credentials.oath2);

			// to hash clientID and clientSecret with base64
			String plainCreds = Credentials.clientID + ":" + Credentials.clientSecret;
			String base64Creds = Base64.getEncoder().encodeToString(plainCreds.getBytes());

			String authHeader = "Basic " + base64Creds;

			// to set headers
			MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
			headers.add(HttpHeaders.AUTHORIZATION, authHeader);
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);

			// to set form
			Form form = new Form();
			form.param("grant_type", "client_credentials");
			Entity<Form> entity = Entity.form(form);

			// to get the response
			response = target.request().headers(headers).post(entity);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());
		}

		String responseString = response.readEntity(String.class);
		JSONObject json = new JSONObject(responseString);

		if (response.getStatus() != 200) {
			logger.error(response.getStatus() + responseString);
			throw new RuntimeException(json.getJSONObject("ErrorMessage").getString("Indonesian"));
		}

		// to get the access_token value
		return json.getString("access_token");
	}

	// Generate BCA Timestamp base on ISO 8601 Format
	public static String formatTimestamp(Timestamp timestamp) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		return dateFormat.format(timestamp);
	}

	public static byte[] sha256(String value) {
		return DigestUtils.sha256(value.getBytes());
	}

	// To make a stringToSign as a requirement to get signature
	public static String stringToSignFormat(String httpMethod, String urlAPI, String accessToken, String payload,
			String timestamp) {

		String shaPayload = Hash.sha256(payload.replaceAll("\\s", ""));

		System.out.println("payload replaceAll ===> " + payload.replaceAll("\\s", ""));

		String stringToSign = httpMethod + ":" + urlAPI + ":" + accessToken + ":" + shaPayload.toLowerCase() + ":"
				+ timestamp;

		return stringToSign;
	}

	// To hash the API secret and string to Sign o get a signature
	public static String signature(String apiSecret, String stringToSign) {

		byte[] result = HmacUtils.hmacSha256(apiSecret, stringToSign);

		return new String(Hex.encodeHex(result));
	}

}
