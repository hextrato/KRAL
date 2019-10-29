package com.hextrato.kral.core.util.parser;

import org.w3c.dom.*;

import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import java.io.*;

public class XMLParser {

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document document;

	XMLParser(String file) throws Exception {
		this.createDocumentBuilder();
		this.validateDocument(file);
		this.createDocument(file);
	}
	
	private void createDocumentBuilder() throws Exception {
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
	}
	
	private void createDocument(String file) throws Exception {
		document = builder.parse(new File( file ));	
	}

	private void validateDocument(String file) throws Exception {
		Schema schema = null;
		try {
		  String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		  SchemaFactory factory = SchemaFactory.newInstance(language);
		  schema = factory.newSchema(new File(file));
		} catch (Exception e) {
		    e.printStackTrace();
		}
		Validator validator = schema.newValidator();
		validator.validate(new DOMSource(document));
	}
	
	public Element getRootElement() {
		return document.getDocumentElement();
	}
	
}
