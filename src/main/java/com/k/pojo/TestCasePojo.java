package com.k.pojo;

import com.k.excel_model.TestStepPojo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestCasePojo {

    public String testCaseId;

    public String status;

    public List<TestStepPojo> testStepsList;
}
