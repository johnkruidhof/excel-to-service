package com.example.exceltoservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {

    private ServiceTypeRequest serviceTypeRequest;
    private ServiceTypeResponse serviceTypeResponse;

}
