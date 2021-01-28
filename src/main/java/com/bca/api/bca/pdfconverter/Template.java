package com.bca.api.bca.pdfconverter;

public enum Template {
	ACTIVATION_TEMPLATE("_email-activation-code-email-baru.html", "Verifikasi Pembaruan Email"),
	REGISTRATION_TEMPLATE("_email-registration-investor.html", "Verifikasi Email KlikCair");
	
	private final String filename;
	private final String subject;
	
	Template(String filename, String subject){
		this.filename = filename;
		this.subject = subject;
	}

	public String getFilename() {
		return filename;
	}

	public String getSubject() {
		return subject;
	}
}
