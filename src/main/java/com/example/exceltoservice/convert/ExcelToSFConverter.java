package com.example.exceltoservice.convert;

import com.example.exceltoservice.domain.ResData;
import com.example.exceltoservice.domain.ResDataFoutItem;
import com.example.exceltoservice.domain.ResDataStatus;
import com.example.exceltoservice.domain.ResponseFout;
import com.example.exceltoservice.domain.RowData;
import com.example.exceltoservice.domain.RowDataEmpty;
import com.example.exceltoservice.domain.RowDataSource;
import com.example.exceltoservice.domain.RowDataValue;
import com.example.exceltoservice.domain.ServiceType;
import com.example.exceltoservice.domain.ServiceTypeRequest;
import com.example.exceltoservice.domain.ServiceTypeResponse;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class ExcelToSFConverter {

	public static final String SYSTEM = "HAS";
	public static final String FOUTKODE = "foutkode";
	public static final String FOUTOMS = "foutoms";
	public static final String FOUTTYPE = "fouttype";
	public static final String STATUS = "resultStatus";
	public static final List<String> DATA = List.of("id", "received", "filler");

	private final com.example.exceltoservice.convert.ExcelToSFConverterConfig config;
	
	public static String convert(com.example.exceltoservice.convert.ExcelToSFConverterConfig config) throws InvalidFormatException, IOException {
		return new ExcelToSFConverter(config).convert();
	}

	public String convert() throws InvalidFormatException, IOException {
		LinkedHashMap<String, RowDataSource> list = readFile();
		return convertToOutputString(list);
	}

	public LinkedHashMap<String, RowDataSource> readFile()
		throws InvalidFormatException, IOException {

		InputStream inp = new FileInputStream(config.getSourceFile());
		Workbook wb = WorkbookFactory.create(inp);

		int firstRow = config.getFirstRow();
		int lastRow = config.getLastRow();
		int nameCol = config.getNameCol();
		int lengthCol = config.getLengthCol();
		int positionCol = config.getPositionCol();
		int valueCol = config.getValueCol();
		LinkedHashMap<String, RowDataSource> list = new LinkedHashMap<>();

		// Only read the first sheet
		Sheet sheet = wb.getSheetAt(0);
		if (sheet != null) {
			for (int j = firstRow; j <= lastRow; j++) {
				Row row = sheet.getRow(j);

				if (row == null) {
					continue;
				}

				RowDataSource rowData = new RowDataSource();
				for (int k = 0; k <= row.getLastCellNum(); k++) {

					Cell cell = row.getCell(k);
					if (cell != null) {
						if (k == nameCol) { // Name
							rowData.setName(cleanString(cell.getStringCellValue()));
						} else if (k == lengthCol) { // Length
							rowData.setMaxLength((int) cell.getNumericCellValue());
						} else if (k == positionCol) { // Position
							rowData.setPosition((int) cell.getNumericCellValue());
						} else if (k == valueCol) { // Value
							rowData.setValue(cleanString(cell.getStringCellValue()));
						}
					}
				}
				list.put(rowData.getName(), rowData);
			}
		}

		return list;
	}

	public String convertToOutputString(LinkedHashMap<String, RowDataSource> list) throws IOException {
		ObjectMapper mapper = (config.getFormat().equals("YAML")) ?
				new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)) :
				new ObjectMapper(new JsonFactory());

		String outputString;

		LinkedHashMap<String, RowData> properties = new LinkedHashMap<>();
		list.forEach((key, rowDataSource) -> {
			RowData rowData = (rowDataSource.getValue() == null) ?
					RowDataEmpty.builder().length(rowDataSource.getMaxLength()).empty(true).build() :
					RowDataValue.builder().length(rowDataSource.getMaxLength()).value(rowDataSource.getValue()).build();

			properties.put(key, rowData);
		});

		List<ResponseFout> responseFoutList = getFoutList(list);
		ResDataStatus resDataStatus = getStatus(list);
		LinkedHashMap<String, ResData> dataList = getDataList(list);

		LinkedHashMap<String, ServiceType> service = new LinkedHashMap<>();
		service.put(String.format("%s%s", SYSTEM, "REQ"), ServiceTypeRequest.builder()
				.type("object")
				.properties(properties)
				.build());

		service.put(String.format("%s%s", SYSTEM, "RES"), ServiceTypeResponse.builder()
				.type("object")
				.resDataStatus(resDataStatus)
				.errors(responseFoutList)
				.data(dataList)
				.build());

		outputString = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(service);

		return outputString;
	}

	private String cleanString(String str) {
		return str.replace("\n", "").replace("\r", "");
	}

	private static List<ResponseFout> getFoutList(LinkedHashMap<String, RowDataSource> list) {
		List<ResponseFout> responseFoutList = new ArrayList<>();

		for (int i = 1; i <= 10; i++) {
			ResDataFoutItem itemCode = getFoutItem(list, i, FOUTKODE);
			ResDataFoutItem itemOms = getFoutItem(list, i, FOUTOMS);
			ResDataFoutItem itemType = getFoutItem(list, i, FOUTTYPE);

			if (itemCode == null || itemOms == null || itemType == null) {
				continue;
			}
			responseFoutList.add(ResponseFout.builder()
					.fout_code(itemCode)
					.fout_oms(itemOms)
					.fout_type(itemType)
					.build());
		}

		return responseFoutList;
	}

	private static ResDataFoutItem getFoutItem(LinkedHashMap<String, RowDataSource> list, int i, String fieldName) {
		String key = String.format("%s%s", fieldName, i);
		if (list.containsKey(key)) {
			RowDataSource source = list.get(key);

			return ResDataFoutItem.BBuilder()
					.from(source.getPosition())
					.maxLength(source.getMaxLength())
					.pattern("[a-zA-Z0-9]")
					.build();
		}
		return null;
	}

	private static LinkedHashMap<String, ResData> getDataList(LinkedHashMap<String, RowDataSource> list) {
		LinkedHashMap<String, ResData> dataList = new LinkedHashMap<>();

		DATA.forEach(s -> {
			ResData resData = getDataItem(list, s);
			if (resData != null) dataList.put(s, resData);
		});

		return dataList;
	}

	private static ResData getDataItem(LinkedHashMap<String, RowDataSource> list, String fieldName) {
		if (list.containsKey(fieldName)) {
			RowDataSource source = list.get(fieldName);
			return ResData.builder()
					.from(source.getPosition())
					.maxLength(source.getMaxLength())
					.build();
		}
		return null;
	}

	private static ResDataStatus getStatus(LinkedHashMap<String, RowDataSource> list) {
		if (list.containsKey(STATUS)) {
			RowDataSource source = list.get(STATUS);
			return ResDataStatus.BBuilder()
					.from(source.getPosition())
					.maxLength(source.getMaxLength())
					.ok(List.of("00"))
					.build();
		}
		return null;
	}
}
