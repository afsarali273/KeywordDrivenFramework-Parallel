package com.k.pojo;

import com.k.excel_model.TestCasePojo;
import com.k.excel_model.TestSuitePojo;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatusUpdate {

    public int passCount;
    public int failCount;
    public int totalRunCount;
    public double passPerc;
    public double failPerc;


    public List<TestSuitePojo> updateStatus(List<TestSuitePojo> suites) {

        suites.stream().forEach(suite -> {
            suite.getTestCaseList().forEach(testCase -> {
                if (isTestCaseFailed(testCase)) {
                    testCase.setResult("Failed");
                } else {
                    testCase.setResult("Passed");
                }
            });

            if (isSuiteFailed(suite)) {
                suite.setResult("Failed");
            } else {
                suite.setResult("Passed");
            }
        });
        return suites;
    }

    private boolean isSuiteFailed(TestSuitePojo suite) {
        AtomicBoolean isFailed = new AtomicBoolean(false);
        suite.getTestCaseList().stream().forEach(x -> {
            isFailed.set(isTestCaseFailed(x));
        });

        return isFailed.get();
    }

    private boolean isTestCaseFailed(TestCasePojo tesCase) {
        return tesCase.getTestSteps().stream().anyMatch(steps -> steps.getResult().equals("Failed"));
    }

    public StatusUpdate testStatus(List<TestSuitePojo> suites) {
        StatusUpdate update = new StatusUpdate();

        suites.stream().forEach(suite -> {
            suite.getTestCaseList().forEach(testCase -> {
                if (isTestCaseFailed(testCase)) {
                    update.failCount++;
                } else {
                    update.passCount++;
                }
            });
        });
        update.passPerc = ((double) update.passCount / (double) (update.failCount + update.passCount))*100;
        update.failPerc = ((double) update.failCount / (double) (update.failCount + update.passCount))*100;
        return update;
    }
}
