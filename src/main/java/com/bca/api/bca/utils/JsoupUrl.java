package com.bca.api.bca.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupUrl { 
    public static void main(String[] args) throws IOException {
    	//String url = "https://todo-andre.firebaseapp.com/";
    	Document doc = Jsoup.connect("https://todo-andre.firebaseapp.com/").get();
    	System.out.println(doc);
    	
//    	Document urlString = Jsoup.connect("https://github.com/").get().html();
//    	System.out.println(urlString);
    	
    	Element email = doc.getElementById("email");
    	Element password = doc.getElementById("password");
    	
        System.out.println("before add id email: " + email);
        System.out.println("before add id pass: " + password);
        
        File input = new File("https://todo-andre.firebaseapp.com/");
        PrintWriter writer = new PrintWriter(input, "UTF-8");
        email.attr("value", "haha@mail.com");
        password.attr("value", "123");
        writer.write(doc.html() ) ;
        writer.flush();
        writer.close();
        
        System.out.println("===============================");
        System.out.println("after add id email: " + email);
        System.out.println("after add id pass: " + password);
    }
}
