package com.k.excel_model;

import com.k.utils.excel.ExcelPOJOUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.util.List;

public class ExcelReader {

    private static ExcelReader EXCEL_READER;

    private ExcelReader() {
    }

    public static ExcelReader getInstance() {
        if (EXCEL_READER == null)
            EXCEL_READER = new ExcelReader();
        return EXCEL_READER;
    }

    private static final String EXCEL_PATH = "./src/main/resources/Excel";
    private static final String EXCEL_NAME = "TestDataSheet";


    public <T> List<T> getFilePojo(String sheetName) throws Exception {
        Workbook workbook = WorkbookFactory.create(new FileInputStream(EXCEL_PATH + "/" + EXCEL_NAME + ".xlsx"));
        Sheet sheet = workbook.getSheet(sheetName);
        return (List<T>) ExcelPOJOUtils.sheetToPOJO(sheet, getPojoClass(sheetName));
    }

    private <T> Class<?> getPojoClass(String sheetName) {
        switch (sheetName) {
            case "Suite":
                return TestSuitePojo.class;
            case "TestCases":
                return SuiteDetails.class;
            case "TestSteps":
                return TestStepPojo.class;
        }
        return null;
    }

}
