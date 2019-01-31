package com.bca.api.bca.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PDF {
	public static void main(String[] args) throws Exception {
		 
        String inputFilePath = "/Users/andresudi/Downloads/Zeugnisse 1.pdf"; // Existing file
        String outputFilePath = "/Users/andresudi/Downloads/ModifiedZeugnisse 1.pdf"; // New file

        OutputStream fos = new FileOutputStream(new File(outputFilePath));

        PdfReader pdfReader = new PdfReader(inputFilePath);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);


        // loop on all the PDF pages
        // i is the pdfPageNumber
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

            //getOverContent() allows you to write content on TOP of existing pdf content.
            //getUnderContent() allows you to write content on BELOW of existing pdf content.
               
               PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);

               // Add text in existing PDF
               pdfContentByte.beginText();
               pdfContentByte.setFontAndSize(BaseFont.createFont
                                                 (BaseFont.TIMES_BOLD, //Font name
                                                   BaseFont.CP1257, //Font encoding
                                                  BaseFont.EMBEDDED //Font embedded
                                                  )
                                                  , 12); // set font and size
               pdfContentByte.setTextMatrix(35, 760); // set x and y co-ordinates
                                          //0, 800 will write text on TOP LEFT of pdf page
                                          //0, 0 will write text on BOTTOM LEFT of pdf page
               pdfContentByte.showText("Newly added text - pdf hahahha"); // add the text
               System.out.println("Text added in "+outputFilePath);
               
               pdfContentByte.endText();
        }

        pdfStamper.close(); //close pdfStamper

        System.out.println("Modified PDF created in >> "+ outputFilePath);

 }
}
