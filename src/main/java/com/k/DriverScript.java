package com.k;

import com.k.DriverAction.ActionEngine;
import com.k.gmail.GmailServices;
import com.k.pojo.ExcelObject;
import com.k.pojo.RunnerPojo;
import com.k.reporting.HtmlReport;
import com.k.utils.config.ConfigFileReader;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;


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
        List<RunnerPojo> testList = ExcelObject.getTestStepsList();//.parallelStream().collect(Collectors.toList());
        config = ConfigFileReader.getInstance().readConfig();
        int threadCount = config.getParallelThreadCount();
        System.out.println("Thread count is : =========  " + threadCount); // 5

        ExecutorService threadExecutor = new DriverScript().threadExecutor(testList, threadCount);

        waitTillChildProcessCompleted(threadExecutor);

        new HtmlReport(testList).createHtmlReport();
        GmailServices.getInstance().sendEmailReport();

    }

    public void driverScript(RunnerPojo testCase) {
        System.out.println("Executing Test for : " + testCase.getTestCaseId());
        ActionEngine actionEngine = new ActionEngine();

        testCase.getTestStepsList().forEach(steps -> {
            try {
                System.out.println("Executing Step : " + steps.getDescription());
                actionEngine.performAction(steps.getPageName(), steps.getElementName(), steps.getActionKeyword(), steps.getDataSet());
                steps.setResult("Passed");
            } catch (Exception e) {
                    steps.setResult("Failed");
                    System.out.println("Error occurred : " + e.getMessage());
            }
        });
    }

    private ExecutorService threadExecutor(List<RunnerPojo> testList, int threadSize) {
        ExecutorService exec = Executors.newFixedThreadPool(threadSize);
        testList.forEach(tests -> {
            exec.submit(() -> {
                driverScript(tests);
            });
        });
        exec.shutdown();
        return exec;
    }
}
