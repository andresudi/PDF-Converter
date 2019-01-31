package com.bca.api.bca.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	public static JsonNode parseJson(String inputJson) throws JsonParseException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
		JsonFactory factory = mapper.getFactory();
		JsonParser parser = factory.createParser(inputJson);
		JsonNode jsonObj = mapper.readTree(parser);
		
		return jsonObj;
	}
	
	public static String payloadFormat(JsonNode objectFormat, String name) {
		
		String p1 = objectFormat.get(name).toString();
		String[] p2 = p1.split("\"");
		
		return p2[1];
	}
}

