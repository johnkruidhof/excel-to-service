package com.example.exceltoservice.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResDataStatus extends ResData {

    @Builder(builderMethodName = "BBuilder")
    public ResDataStatus(int from, int maxLength, List<String> ok) {
        super(from, maxLength);
        this.ok = ok;
    }

    private List<String> ok;
}

