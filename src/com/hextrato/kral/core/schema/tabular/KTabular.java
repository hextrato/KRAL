package com.hextrato.kral.core.schema.tabular;

import com.hextrato.kral.core.KRAL;
import com.hextrato.kral.core.data.abstracts.AMetaNamedObject;
import com.hextrato.kral.core.schema.KSchema;
import com.hextrato.kral.core.util.exception.KException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class KTabular extends AMetaNamedObject {

	private KSchema _schema = null;
	public KSchema getSchema() { return this._schema; }

	public KTabular (KSchema schema) throws KException {
		if (schema == null) throw new KException("Invalid null schema");
		// this._name = name;
		this._schema = schema;
		this.properties().declare(__INTERNAL_PROPERTY_SCHEMA__, "String");
		this.properties().set(__INTERNAL_PROPERTY_SCHEMA__, schema.getName());
	}
	
	private KAttributeSet _attributeSet = new KAttributeSet(this);
	public KAttributeSet attributes() { return _attributeSet; }

	private KRecordSet _recordSet = new KRecordSet(this);
	public KRecordSet records() { return _recordSet; }
	
	//
	// IMPORT EXPORT
	// 
	public void importFromCSV(String filePath, String fileName) throws KException {
        try {
        	String fileFullPath = filePath + File.separator + fileName;
        	BufferedReader reader = Files.newBufferedReader(Paths.get(fileFullPath));
        	CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim() /*.withIgnoreHeaderCase()*/);
        	
        	Map<String,Integer> csvHeaders = csvParser.getHeaderMap();
        	String[] attribs = new String[csvHeaders.size()];
        	boolean hasSplit = false;
        	boolean hasUID   = false;
        	int i = 0;
        	for (String aHeader : csvHeaders.keySet()) {
        		// System.out.println(aHeader);
        		if (aHeader.equals(__INTERNAL_PROPERTY_SPLIT__)) hasSplit = true; 
        		else if (aHeader.equals(__INTERNAL_PROPERTY_UID__)) hasUID = true; 
        		else {
            		attribs[i] = aHeader;
            		if (!this.attributes().theList().containsKey(aHeader))
            			this.attributes().create(aHeader, "String");
            		i++;
        		}
        	}
        	for (CSVRecord csvRecord : csvParser) {
        		if (hasSplit) this.getSchema().splits().setCurrent(csvRecord.get(__INTERNAL_PROPERTY_SPLIT__));
        		if (hasUID)   
        			this.records().create(csvRecord.get(__INTERNAL_PROPERTY_UID__));
        		else
        			this.records().create();
        		KRecord record = this.records().getRecord();
        		for (String attrib : attribs) if (attrib != null) {
        			//System.out.println(attrib);
        			record.setAttributeValue(attrib, csvRecord.get(attrib));
        		}
        	}
        	csvParser.close();
        	reader.close();
        } catch (IOException e) {
        	KRAL.error(e.getMessage());
        	System.exit(1);
        	throw new KException (e.getMessage());
        }		

	}
	public void exportToCSV(String filePath, String fileName) throws KException {
        try {
        	String fileFullPath = filePath + File.separator + fileName;
        	BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileFullPath));
        	String[] attribs = new String[attributes().theList().size()+2];
        	String[] datarow = new String[attributes().theList().size()+2];
        	attribs[0] = __INTERNAL_PROPERTY_SPLIT__;
        	attribs[1] = __INTERNAL_PROPERTY_UID__;
        	int i = 2;
        	for (String attributeID : attributes().theList().keySet() ) {
        		attribs[i] = attributes().get(attributeID).getName();
        		i++;
        	}
        	// CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Student Name", "Fees"));
        	CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(attribs));
            //Writing records in the generated CSV file
        	for (String recordUID : records().theList().keySet() ) {
        		 KRecord record = this.records().getRecord(recordUID);
        		 datarow[0] = record.getSplit().getName();
        		 datarow[1] = record.getUID();
        		 for (i = 2; i < attribs.length; i++) {
        			 datarow[i] = record.getAttributeValue(attribs[i]);
        		 }
        		 csvPrinter.printRecord(Arrays.asList(datarow));
        	}
            /*
            csvPrinter.printRecord("Akshay Sharma", 1000);
            csvPrinter.printRecord("Rahul Gupta", 2000);
            csvPrinter.printRecord("Jay Karn", 3000);
            */
            //Writing records in the form of a list
            //csvPrinter.printRecord(Arrays.asList("Dev Bhatia", 4000));
            
            csvPrinter.flush();
            csvPrinter.close();
            writer.close();
        } catch (IOException e) {
        	KRAL.error(e.getMessage());
        	throw new KException (e.getMessage());
        }		
	}

	//
	// EXPORT
	//

	public void hextract (BufferedWriter bf) throws KException {
        try {
			bf.write( String.format("tabular %s create", this.getName()) );
			bf.newLine();
			this.attributes().hextract(bf);		// OK
			this.records().hextract(bf);		// ?
        } catch (IOException e) {
        	throw new KException(e.getMessage());
        }
	}

}
