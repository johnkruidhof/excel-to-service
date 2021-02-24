package com.example.exceltoservice.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceTypeResponse extends ServiceType {

    @Builder
    public ServiceTypeResponse(String type,
                               ResDataStatus resDataStatus,
                               List<ResponseFout> errors,
                               LinkedHashMap<String, ResData> data) {
        super(type);
        this.resDataStatus = resDataStatus;
        this.errors = errors;
        this.data = data;
    }

    private ResDataStatus resDataStatus;
    private List<ResponseFout> errors;
    private LinkedHashMap<String, ResData> data;
}
