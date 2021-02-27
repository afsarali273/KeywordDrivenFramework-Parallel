package com.k.pojo;

import com.k.excel_model.ExcelReader;
import com.k.excel_model.SuiteDetails;
import com.k.excel_model.TestCasesPojo;
import com.k.excel_model.TestSuitePojo;

import java.util.*;

public class ExcelObject {

    private String SUITE = "Suite";
    private String SUITE_DETAILS = "SuiteDetails";
    private static String TESTCASES = "TestCases";


    public static List<RunnerPojo> getTestStepsList() {
        try {
           // List<TestSuitePojo> testSuitePojoList = ExcelReader.getInstance().getFilePojo(SUITE);
           // List<SuiteDetails> suiteDetailsList = ExcelReader.getInstance().getFilePojo(SUITE_DETAILS);
            List<TestCasesPojo> testCasesPojoList = ExcelReader.getInstance().getFilePojo(TESTCASES);

            List<RunnerPojo> runnerPojos = new ArrayList<>();

            List<TestCasesPojo> testList=null;

            Map<String,List<TestCasesPojo>> map = new LinkedHashMap<>();


            for(TestCasesPojo tests: testCasesPojoList){

                if(map.containsKey(tests.getTestCaseId())){

                    testList.add(tests);
                }else {
                    testList = new ArrayList<>();
                    testList.add(tests);
                }
                map.putIfAbsent(tests.getTestCaseId(),testList);
            }
            map.entrySet().stream().forEach( v -> {
                RunnerPojo pojo = new RunnerPojo();
                pojo.setTestCaseId(v.getKey());
                pojo.setTestStepsList(v.getValue());
                runnerPojos.add(pojo);
            });
            return runnerPojos;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
       List<RunnerPojo> runnerPojo =   new ExcelObject().getTestStepsList();

        runnerPojo.stream().forEach(x-> {
            System.out.println("TestCaseID  : " +x.getTestCaseId());

            x.getTestStepsList().stream()
                    .forEach(y->
                            System.out.println("Description : "+y.getDescription()));

            System.out.println("=================== ");

        });



    }
}
