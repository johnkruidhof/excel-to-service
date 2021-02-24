package com.example.exceltoservice.convert;

import lombok.Data;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@Data
public class ExcelToSFConverterConfig {

	private String sourceFile;
	private String format;
	private boolean pretty = false;
	private int firstRow = 0;
	private int lastRow = 0; // 0 -> no limit
	private int nameCol = 0;
	private int lengthCol = 1;
	private int positionCol = 2;
	private int valueCol = 3;
	private String destinationFile;
	private boolean writeToFile = false;


	public static ExcelToSFConverterConfig create(CommandLine cmd) {
		List<String> fl = List.of("YAML", "JSON");

		ExcelToSFConverterConfig config = new ExcelToSFConverterConfig();

		config.sourceFile = (cmd.hasOption("s")) ? cmd.getOptionValue("s") : null;

		config.format = (cmd.hasOption("f") && (fl.contains(cmd.getOptionValue("f").toUpperCase()))) ?
				cmd.getOptionValue("f").toUpperCase() : "YAML";

		if (cmd.hasOption("fr")) {
			config.setFirstRow(setRowColValue(Integer.parseInt(cmd.getOptionValue("fr"))));
		}

		if (cmd.hasOption("lr")) {
			config.setLastRow(setRowColValue(Integer.parseInt(cmd.getOptionValue("lr"))));
		}

		if (cmd.hasOption("nc")) {
			config.setNameCol(setRowColValue(Integer.parseInt(cmd.getOptionValue("nc"))));
		}

		if (cmd.hasOption("lc")) {
			config.setLengthCol(setRowColValue(Integer.parseInt(cmd.getOptionValue("lc"))));
		}

		if (cmd.hasOption("pc")) {
			config.setPositionCol(setRowColValue(Integer.parseInt(cmd.getOptionValue("pc"))));
		}

		if (cmd.hasOption("vc")) {
			config.setValueCol(setRowColValue(Integer.parseInt(cmd.getOptionValue("vc"))));
		}

		if(cmd.hasOption("d")) {
			config.setDestinationFile(cmd.getOptionValue("d"));
			config.setWriteToFile(true);
		}
		
		config.pretty = cmd.hasOption("pretty");

		return config;
	}
	
	public String valid() {
		if(sourceFile==null) {
			return "Source file may not be empty.";
		}
		File file = new File(sourceFile);
		if(!file.exists()) {
			return "Source file does not exist.";
		}
		if(!file.canRead()) {
			return "Source file is not readable.";
		}
		if(destinationFile != null) {
			file = new File(destinationFile);
			if(!file.isDirectory()) {
				return "Destination path is not a valid directory.";
			}
			String fileName = (format.equals("YAML")) ? "service.yaml": "service.json";
			this.setDestinationFile(Paths.get(this.getDestinationFile(),fileName).toString());
		}
		return null;
	}

	private static int setRowColValue(int value) {
		return (value > 0) ? value - 1 : 0;
	}
}
