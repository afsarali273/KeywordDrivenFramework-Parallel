package com.k;

import com.k.DriverAction.ActionEngine;
import com.k.excel_model.TestCasePojo;
import com.k.excel_model.TestSuitePojo;
import com.k.pojo.ExcelMapper;
import com.k.pojo.StatusUpdate;
import com.k.reporting.HtmlReport;
import com.k.utils.config.ConfigFileReader;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriverScript {
    static ConfigFileReader config;
    static void waitTillChildProcessCompleted(ExecutorService threadExecutor){
        int count =0;
        while(!threadExecutor.isTerminated()){
            try {
                Thread.sleep(5000);
                count++;
                System.out.println("Waiting : "+count * 5 +" sec");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

      List<TestSuitePojo> suites =  ExcelMapper.readExcelPojo();
        config = ConfigFileReader.getInstance().readConfig();
        int threadCount = config.getParallelThreadCount();
        System.out.println("Thread count is : =========  " + threadCount); // 5

        ExecutorService threadExecutor = new DriverScript().threadExecutor(suites, threadCount);

        waitTillChildProcessCompleted(threadExecutor);

        //Reporting
        new StatusUpdate().updateStatus(suites);
        new HtmlReport(suites).createHtmlReport();
       // GmailServices.getInstance().sendEmailReport();

    }

    public void driverScript(TestCasePojo testCase) {
        System.out.println("Executing Test for : " + testCase.getTestCaseId());
        ActionEngine actionEngine = new ActionEngine();

        testCase.getTestSteps().forEach(steps -> {
            try {
                System.out.println("Executing Step : " + steps.getDescription());
                actionEngine.performAction(steps.getPageName(), steps.getElementName(), steps.getActionKeyword(), steps.getDataSet());
                steps.setResult("Passed");
            } catch (Exception e) {
                    steps.setResult("Failed");
                    steps.setErrorMessage(e.getMessage());
                    System.out.println("Error occurred : " + e.getMessage());
            }
        });
    }

    private ExecutorService threadExecutor(List<TestSuitePojo> testSuites, int threadSize) {
        ExecutorService exec = Executors.newFixedThreadPool(threadSize);
        testSuites.forEach(testcases -> {

            testcases.getTestCaseList().forEach( tests->{
                exec.submit(() -> {
                    driverScript(tests);
                });

            });
        });
        exec.shutdown();
        return exec;
    }
}
