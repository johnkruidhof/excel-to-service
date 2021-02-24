package com.example.exceltoservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseFout {

    private ResDataFoutItem fout_code;
    private ResDataFoutItem fout_oms;
    private ResDataFoutItem fout_type;
}
