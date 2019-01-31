package com.bca.api.bca.resources;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hash {
	private final static Logger logger = LoggerFactory.getLogger(Hash.class);

	private static String getHash(String txt, String hashType) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance(hashType);
			byte[] array = md.digest(txt.getBytes());
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}

			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());
		}

		return null;
	}

	public static String md5(String txt) {
		return Hash.getHash(txt, "MD5");
	}

	public static String sha1(String txt) {
		return Hash.getHash(txt, "SHA1");
	}

	public static String sha256(String txt) {
		return Hash.getHash(txt, "SHA-256");
	}
}
