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
		int resultCol = config.getResultCol();
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
						} else if (k == resultCol) { // Value
							rowData.setResult(cleanString(cell.getStringCellValue()).equalsIgnoreCase("X"));
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
		service.put(String.format("%s%s", config.getSysteem(), "REQ"), ServiceTypeRequest.builder()
				.type("object")
				.properties(properties)
				.build());

		service.put(String.format("%s%s", config.getSysteem(), "RES"), ServiceTypeResponse.builder()
				.type("object")
				.status(resDataStatus)
				.errors(responseFoutList)
				.data(dataList)
				.build());

		outputString = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(service);

		return outputString;
	}

	private String cleanString(String str) {
		return str.replace("\n", "").replace("\r", "");
	}

	private List<ResponseFout> getFoutList(LinkedHashMap<String, RowDataSource> list) {
		List<ResponseFout> responseFoutList = new ArrayList<>();

		String foutCodeField = config.getFoutCodeField();
		String foutOmsField = config.getFoutOmsField();
		String foutTypeField = config.getFoutTypeField();

		for (int i = 1; i <= 10; i++) {
			ResDataFoutItem itemCode = getFoutItem(list, i, foutCodeField);
			ResDataFoutItem itemOms = getFoutItem(list, i, foutOmsField);
			ResDataFoutItem itemType = getFoutItem(list, i, foutTypeField);

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

		list.forEach((key, rowDataSource) -> {
			if (rowDataSource.isResult()) {
				dataList.put(key, ResData.builder()
						.from(rowDataSource.getPosition())
						.maxLength(rowDataSource.getMaxLength())
						.build());
			}
		});

		return dataList;
	}

	private ResDataStatus getStatus(LinkedHashMap<String, RowDataSource> list) {

		String statusField = config.getStatusField();

		if (list.containsKey(statusField)) {
			RowDataSource source = list.get(statusField);
			return ResDataStatus.BBuilder()
					.from(source.getPosition())
					.maxLength(source.getMaxLength())
					.ok(List.of("00"))
					.build();
		}
		return null;
	}
}
