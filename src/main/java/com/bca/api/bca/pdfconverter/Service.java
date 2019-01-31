package com.bca.api.bca.pdfconverter;

import java.io.IOException;
import java.util.EnumMap;

import static com.itextpdf.text.pdf.BaseFont.EMBEDDED;
import static com.itextpdf.text.pdf.BaseFont.IDENTITY_H;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.DocumentException;
import com.bca.api.bca.pdfconverter.Template;

public class Service {

	private static String filePath = "templates/email/";
	private static EnumMap<Template, Document> templates;
	private static final String UTF_8 = "UTF-8";
	private static final String OUTPUT_FILE_PATH = "templates/test";
	private Integer numIncrement = 0;

	public Service() throws IOException {
		this.loadTemplates();
	}

	public static void main(String[] args) throws IOException, com.lowagie.text.DocumentException {
		Service service = new Service();

		service.activationTemplatePDF("Andre", "20", "abcd");
		service.registrationTemplatePDF("andre@mail", "google.com", "1", "abcde");

	}

	public void activationTemplatePDF(String name, String duration, String token)
			throws IOException, com.lowagie.text.DocumentException {
		Document doc = templates.get(Template.ACTIVATION_TEMPLATE);
		Elements els = doc.body().getAllElements();

		replaceSpecificString("%recipient.name%", name, els);
		replaceSpecificString("%recipient.duration%", duration, els);
		replaceSpecificString("%recipient.token%", token, els);

		// Flying Saucer needs XHTML - not just normal HTML. To make our life
		// easy, we use JTidy to convert the rendered Thymeleaf template to
		// XHTML. Note that this might no work for very complicated HTML. But
		// it's good enough for a simple letter.
		String xHtml = convertToXhtml(doc.toString());

		ITextRenderer renderer = new ITextRenderer();

		// FlyingSaucer has a working directory. If you run this test, the working
		// directory
		// will be the root folder of your project. However, all files (HTML, CSS, etc.)
		// are
		// located under "/src/test/resources". So we want to use this folder as the
		// working
		// directory.
		String baseUrl = FileSystems.getDefault().getPath("templates").toUri().toURL().toString();
		renderer.setDocumentFromString(xHtml, baseUrl);
		renderer.layout();

		String OutputFile = OUTPUT_FILE_PATH + "_activation_template_" + name + ".pdf";

		OutputStream outputStream = new FileOutputStream(OutputFile);
		renderer.createPDF(outputStream);
		outputStream.close();
	}

	public void registrationTemplatePDF(String email, String base, String id, String token)
			throws com.lowagie.text.DocumentException, IOException {
		Document doc = templates.get(Template.REGISTRATION_TEMPLATE);
		Elements els = doc.body().getAllElements();

		replaceSpecificString("%recipient.email%", email, els);
		replaceSpecificString("%recipient.baseUrl%", base, els);
		replaceSpecificString("%recipient.id%", id, els);
		replaceSpecificString("%recipient.token%", token, els);

		String xHtml = convertToXhtml(doc.toString());
		ITextRenderer renderer = new ITextRenderer();

		String baseUrl = FileSystems.getDefault().getPath("templates").toUri().toURL().toString();
		renderer.setDocumentFromString(xHtml, baseUrl);
		renderer.layout();

		String OutputFile = OUTPUT_FILE_PATH + "_registration_template_" + email + ".pdf";

		OutputStream outputStream = new FileOutputStream(OutputFile);
		renderer.createPDF(outputStream);
		outputStream.close();
	}

	public void loadTemplates() throws IOException {
		templates = new EnumMap<>(Template.class);
		for (Template temp : Template.values()) {
			Document doc = Jsoup.parse(new File(filePath + temp.getFilename()), "utf-8");
			templates.put(temp, doc);
		}
	}

	public String convertToXhtml(String html) throws UnsupportedEncodingException {
		Tidy tidy = new Tidy();
		tidy.setInputEncoding(UTF_8);
		tidy.setOutputEncoding(UTF_8);
		tidy.setXHTML(true);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		tidy.parseDOM(inputStream, outputStream);
		return outputStream.toString(UTF_8);
	}

	public void replaceSpecificString(String currentString, String replaceString, Elements els) {
		for (Element e : els) {
			List<TextNode> tnList = e.textNodes();
			for (TextNode tn : tnList) {
				String orig = tn.text();
				tn.text(orig.replace(currentString, replaceString));
			}
		}
	}

}
