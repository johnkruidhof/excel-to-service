package com.example.exceltoservice.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RowDataEmpty extends RowData {

    @Builder
    public RowDataEmpty(Integer length, boolean empty) {
        super(length);
        this.empty = empty;
    }

    private boolean empty;
}
