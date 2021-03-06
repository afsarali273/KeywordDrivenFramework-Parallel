package com.k.excel_model;

import com.k.utils.excel.ExcelColumn;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TestStepPojo {

    @ExcelColumn(name = "TestId")
    private String testCaseId;

    @ExcelColumn(name = "Description")
    private String description;

    @ExcelColumn(name = "PageName")
    private String pageName;

    @ExcelColumn(name = "ElementName")
    private String elementName;

    @ExcelColumn(name = "ActionKeyword")
    private String actionKeyword;

    @ExcelColumn(name = "Dataset")
    private String dataSet;

    @ExcelColumn(name = "Result")
    private String result;

    private String errorMessage;

}
