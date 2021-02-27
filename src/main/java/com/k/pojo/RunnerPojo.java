package com.k.pojo;

import com.k.excel_model.TestCasesPojo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RunnerPojo {

    public String testCaseId;

    public String status;

    public List<TestCasesPojo> testStepsList;
}
