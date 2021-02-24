package com.example.exceltoservice.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RowDataValue extends RowData {

    @Builder
    public RowDataValue(Integer length, String value) {
        super(length);
        this.value = value;
    }

    private String value;
}
