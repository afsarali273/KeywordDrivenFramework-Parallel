package com.k;

import com.k.DriverAction.ActionEngine;
import com.k.pojo.ExcelObject;
import com.k.pojo.RunnerPojo;
import com.k.utils.config.ConfigFileReader;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DriverScript {

    public static void main(String[] args) {
        List<RunnerPojo> testList = ExcelObject.getTestStepsList();//.parallelStream().collect(Collectors.toList());
        int threadCount = ConfigFileReader.getInstance().readConfig().getParallelThreadCount();
        System.out.println("Thread count is : =========  " + threadCount);

        new DriverScript().threadExecutor(testList, threadCount);

//        testList.forEach(x->{
//
//            System.out.println("Test Name: "+x.getTestCaseId());
//            System.out.println("Test Status : "+x.getStatus());
//
//            System.out.println("======= Test Steps ===== ");
//
//            x.getTestStepsList().forEach(y-> {
//
//                System.out.println("Step Name: "+y.getDescription());
//                System.out.println("Test caseId : "+y.getTestCaseId());
//                System.out.println("Step Status: "+y.getResult());
//
//                System.out.println("\n ============ ==========");
//            });
//
//        });

        }

    public void driverScript(RunnerPojo testCase) {
        System.out.println("Executing Test for : " + testCase.getTestCaseId());
        ActionEngine actionEngine = new ActionEngine();

        testCase.getTestStepsList().forEach(steps -> {
            try {
//                if(testCase.getStatus().contains("Failed")){
//                    throw new Exception("Failed already");
//                }
                System.out.println("Executing Step : " + steps.getDescription());
                actionEngine.performAction(steps.getPageName(), steps.getElementName(), steps.getActionKeyword(), steps.getDataSet());
            }catch (Exception e){
                if(e.getMessage().contains("Failed already"))
                    steps.setResult("Skipped");
                else
                    steps.setResult("Failed");
                testCase.setStatus("Failed");
                System.out.println("Error occurred : "+e.getMessage());
                e.printStackTrace();
            }
        });


    }

    private void threadExecutor(List<RunnerPojo> testList, int threadSize) {
        ExecutorService exec = Executors.newFixedThreadPool(threadSize);
        testList.forEach(tests -> {
            exec.submit(() -> {
                driverScript(tests);
            });
        });
        exec.shutdown();
    }
}
