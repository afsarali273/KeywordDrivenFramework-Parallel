package com.k.pojo;

import com.k.excel_model.ExcelReader;
import com.k.excel_model.TestStepPojo;

import java.util.*;

public class ExcelMapper {

    private static String SUITE = "Suite";
    private static String TEST_STEPS = "TestSteps";
    private static String TESTCASES = "TestCases";


    public static List<TestCasePojo> getTestCasesAndStepsMapperList() {
        try {
           // List<TestSuitePojo> testSuitePojoList = ExcelReader.getInstance().getFilePojo(SUITE);
           // List<SuiteDetails> suiteDetailsList = ExcelReader.getInstance().getFilePojo(SUITE_DETAILS);
            List<TestStepPojo> testStepPojoList = ExcelReader.getInstance().getFilePojo(TEST_STEPS);

            List<TestCasePojo> testCasePojos = new ArrayList<>();

            List<TestStepPojo> testList=null;

            Map<String,List<TestStepPojo>> map = new LinkedHashMap<>();


            for(TestStepPojo tests: testStepPojoList){

                if(map.containsKey(tests.getTestCaseId())){

                    testList.add(tests);
                }else {
                    testList = new ArrayList<>();
                    testList.add(tests);
                }
                map.putIfAbsent(tests.getTestCaseId(),testList);
            }
            map.entrySet().stream().forEach( v -> {
                TestCasePojo pojo = new TestCasePojo();
                pojo.setTestCaseId(v.getKey());
                pojo.setTestStepsList(v.getValue());
                testCasePojos.add(pojo);
            });
            return testCasePojos;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
       List<TestCasePojo> testCasePojo =   new ExcelMapper().getTestCasesAndStepsMapperList();

        testCasePojo.stream().forEach(x-> {
            System.out.println("TestCaseID  : " +x.getTestCaseId());

            x.getTestStepsList().stream()
                    .forEach(y->
                            System.out.println("Description : "+y.getDescription()));

            System.out.println("=================== ");

        });



    }
}
