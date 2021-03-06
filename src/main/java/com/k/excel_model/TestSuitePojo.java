package com.k.excel_model;

import com.k.utils.excel.ExcelColumn;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class TestSuitePojo {

    @ExcelColumn(name = "SuiteName")
    private String suiteName;

    @ExcelColumn(name = "RunMode")
    private String runMode;

    @ExcelColumn(name = "Result")
    private String result;

    List<TestCasePojo> testCaseList;
    
}
