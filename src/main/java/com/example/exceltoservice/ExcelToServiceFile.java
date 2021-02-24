package com.example.exceltoservice;

import com.example.exceltoservice.convert.ExcelToSFConverter;
import com.example.exceltoservice.convert.ExcelToSFConverterConfig;
import org.apache.commons.cli.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelToServiceFile {
	
	public static void main( String[] args ) throws Exception
    {
		Options options = new Options();
		options.addOption("sys", "system", true, "The target system code (f.i. HAS).");
		options.addOption("fcf", "foutCodeField", true, "The name of the fout code field w.o. number).");
		options.addOption("fof", "foutOmsField", true, "The name of the fout omschrijving field w.o. number).");
		options.addOption("ftf", "foutTypeField", true, "The name of the fout type field w.o. number).");
		options.addOption("sf", "statusField", true, "The name of the field which indicates the status).");
		options.addOption("s", "source", true, "The source file which should be converted.");
		options.addOption("f", "format", true, "The format into which the file should be converted [JSON, YAML].");
		options.addOption("fr", "firstRow", true, "The first row to read");
		options.addOption("lr", "lastRow", true, "The last row to read");
		options.addOption("nc", "nameCol", true, "The column that contains the field name");
		options.addOption("lc", "lengthCol", true, "The column that contains the field length");
		options.addOption("pc", "positionCol", true, "The column that contains the field position");
		options.addOption("vc", "valueCol", true, "The column that contains the default field value");
		options.addOption("rc", "resultCol", true, "The column that contains a x if the field should be return in the data object");
		options.addOption("d", "destination", true, "The destination directory where the service.json/service.yaml should be created.");
		options.addOption(new Option("pretty", "To render output as pretty formatted json/yaml."));
		options.addOption("?", "help", true, "This help text.");

		CommandLineParser parser = new BasicParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch(ParseException e) {
			help(options);
			return;
		}

		if(cmd.hasOption("?")) {
			help(options);
			return;
		}

		ExcelToSFConverterConfig config = ExcelToSFConverterConfig.create(cmd);
		String valid = config.valid();
		if(valid!=null) {
			System.out.println(valid);
			help(options);
			return;
		}

		String json = ExcelToSFConverter.convert(config);
		if(config.isWriteToFile()) {
			writeToFile(json, config.getDestinationFile());
		} else {
			System.out.println(json);
		}
    }
	
	private static void help(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar excel-to-json.jar", options);
	}

	private static void writeToFile(String json, String filename) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(filename);
		byte[] strToBytes = json.getBytes();
		outputStream.write(strToBytes);

		outputStream.close();
	}
}
