package com.example.exceltoservice.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceTypeRequest extends ServiceType {

    @Builder
    public ServiceTypeRequest(String type, LinkedHashMap<String, RowData> properties) {
        super(type);
        this.properties = properties;
    }

    private LinkedHashMap<String, RowData> properties;
}
