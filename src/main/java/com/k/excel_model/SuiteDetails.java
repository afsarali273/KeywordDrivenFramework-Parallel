package com.k.excel_model;

import com.k.utils.excel.ExcelColumn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuiteDetails {

    @ExcelColumn(name = "SuiteName")
    private String suiteName;

    @ExcelColumn(name = "TestCaseId")
    private String testCaseId;

    @ExcelColumn(name = "TestCaseName")
    private String testCaseName;

    @ExcelColumn(name = "Result")
    private String result;
}
