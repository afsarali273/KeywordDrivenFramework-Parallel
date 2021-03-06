package com.k.pojo;

import com.k.excel_model.ExcelReader;
import com.k.excel_model.TestCasePojo;
import com.k.excel_model.TestStepPojo;
import com.k.excel_model.TestSuitePojo;

import java.util.*;
import java.util.stream.Collectors;

public class ExcelMapper {

    private static String SUITE = "Suite";
    private static String TESTCASES = "TestCases";


    private static List<TestStepPojo> getListOfTestSteps() {

        List<TestStepPojo> testStepsList = new ArrayList<>();
        try {
            List<String> sheetNames = ExcelReader.getInstance().getTestSheetNames();
            sheetNames.forEach(sheetName -> {
                System.out.println("Sheet Name : " + sheetName);
                List<TestStepPojo> testStepPojoList = null;
                try {
                    testStepPojoList = ExcelReader.getInstance().getFilePojo(sheetName);
                    testStepsList.addAll(testStepPojoList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return testStepsList;

    }

    public static List<TestSuitePojo> readExcelPojo() {

        List<TestSuitePojo> testSuitePojoList = null;
        try {
            testSuitePojoList = ExcelReader.getInstance().getFilePojo(SUITE);

            List<TestCasePojo> testCasePojoList = ExcelReader.getInstance().getFilePojo(TESTCASES);

            List<TestStepPojo> testStepPojoList = getListOfTestSteps();

            testCasePojoList.stream().forEach(testCase -> {

                List<TestStepPojo> stepsList = new ArrayList<>();
                testStepPojoList.forEach(steps -> {

                    if (steps.getTestCaseId().contains(testCase.getTestCaseId())) {
                        stepsList.add(steps);
                    }
                });
                testCase.setTestSteps(stepsList);
            });

            testSuitePojoList.stream().filter(x -> x.getRunMode().contains("Yes")).forEach(y -> {
                // Better use Switch case
                if (y.getSuiteName().equalsIgnoreCase("Smoke")) { // Create Enum from Suites
                    y.setTestCaseList(testCasePojoList.stream()
                            .filter(x -> x.getSuiteName().contains("Smoke"))
                            .filter(x -> x.getRunMode().contains("Yes"))
                            .collect(Collectors.toList()));
                } else {
                    y.setTestCaseList(testCasePojoList.stream()
                            .filter(x -> x.getSuiteName().contains("Regression"))
                            .filter(x -> x.getRunMode().contains("Yes"))
                            .collect(Collectors.toList()));
                }

            });
        } catch (Exception e) {
            System.out.println("Something went wrong while reading Excel file : \n " + e.getMessage());
        }

        return testSuitePojoList.stream().filter(x -> x.getRunMode().contains("Yes")).collect(Collectors.toList());

    }

    public static void main(String[] args) throws Exception {
        getListOfTestSteps();

    }
}
