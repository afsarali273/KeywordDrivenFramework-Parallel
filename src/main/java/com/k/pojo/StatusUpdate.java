package com.k.pojo;

import com.k.excel_model.TestCasePojo;
import com.k.excel_model.TestSuitePojo;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatusUpdate {


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

}
