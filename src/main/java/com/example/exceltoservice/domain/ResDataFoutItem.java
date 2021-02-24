package com.example.exceltoservice.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResDataFoutItem extends ResData {

    @Builder(builderMethodName = "BBuilder")
    public ResDataFoutItem(int from, int maxLength, String pattern) {
        super(from, maxLength);
        this.pattern = pattern;
    }

    private String pattern;
}
