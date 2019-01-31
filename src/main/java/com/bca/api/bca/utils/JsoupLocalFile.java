package com.bca.api.bca.utils;

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
import java.nio.file.FileSystems;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.DocumentException;

public class JsoupLocalFile {
    private static final String OUTPUT_FILE = "test18.pdf";
    private static final String UTF_8 = "UTF-8";
    
    public static void main(String[] args) throws IOException, DocumentException, com.lowagie.text.DocumentException {
    	
    	// String fileName = "/Users/andresudi/eclipse-workspace/bca/index.html";
    	String fileName = "index.html";
    	
    	// Parse the html tag into string format
        Document doc = Jsoup.parse(new File(fileName), "utf-8"); 
        
//        Element divTag = doc.getElementById("formLogin"); 
//        Elements inputElements = divTag.getElementsByClass("form-group");
//        Element email = doc.getElementById("email");
//        Element password = doc.getElementById("password");
//        
//        Elements emailContainer = doc.getElementsByClass("email_container");
        Elements els = doc.body().getAllElements();
  
        changeText("%recipient.name%", "Andre", els);
        changeText("%recipient.duration%","20", els);
        changeText("%recipient.token%", "aiueo", els);
        
        File input = new File(fileName);
        PrintWriter writer = new PrintWriter(input, "UTF-8");
        writer.write(doc.html()) ;
        writer.flush();
        writer.close();   
        
        // Flying Saucer needs XHTML - not just normal HTML. To make our life
        // easy, we use JTidy to convert the rendered Thymeleaf template to
        // XHTML. Note that this might no work for very complicated HTML. But
        // it's good enough for a simple letter.
        String xHtml = convertToXhtml(doc.toString());
        
        ITextRenderer renderer = new ITextRenderer();

        // FlyingSaucer has a working directory. If you run this test, the working directory
        // will be the root folder of your project. However, all files (HTML, CSS, etc.) are
        // located under "/src/test/resources". So we want to use this folder as the working
        // directory.
        String baseUrl = FileSystems
                .getDefault()
                .getPath("src", "test")
                .toUri()
                .toURL()
                .toString();
		renderer.setDocumentFromString(xHtml, baseUrl);
		renderer.layout();
        
	    OutputStream outputStream = new FileOutputStream(OUTPUT_FILE);
        renderer.createPDF(outputStream);
        outputStream.close();
        System.out.println(doc.toString());
        
        changeText("Andre", "%recipient.name%", els);
        changeText("20", "%recipient.duration%", els);
        changeText("aiueo", "%recipient.token%", els);
        
        File input1 = new File(fileName);
        PrintWriter writer1 = new PrintWriter(input1, "UTF-8");
        writer1.write(doc.html()) ;
        writer1.flush();
        writer1.close(); 
        
        System.out.println("======================");
        System.out.println(doc.toString());
        
        System.out.println("sukses");
        
//        generateEmailPdf();
//        generateSigningPdf();
    }
    
//    public static void generateEmailPdf() {
//    	
//    }
//    
//    public static void generateSigningPdf() {
//    	
//    }
    
    public static String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(UTF_8);
        tidy.setOutputEncoding(UTF_8);
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(UTF_8);
    }
    
    public static void changeText(String currentString, String replaceString, Elements els) {
        for (Element e : els) {
        	List<TextNode> tnList = e.textNodes();
        	for (TextNode tn : tnList) {
        		String orig = tn.text();
        		tn.text(orig.replace(currentString, replaceString));
        	}
        }
    }
}
