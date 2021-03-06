package com.k.excel_model;

import com.k.utils.excel.ExcelColumn;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestCasePojo {

    @ExcelColumn(name = "SuiteName")
    private String suiteName;

    @ExcelColumn(name = "TestCaseId")
    private String testCaseId;

    @ExcelColumn(name = "TestCaseName")
    private String testCaseName;

    @ExcelColumn(name = "Result")
    private String result;

    @ExcelColumn(name = "Run")
    private String runMode;

    List<TestStepPojo> testSteps;
}
