package com.k.DriverAction;

import com.k.Enum.ActionEnum;
import com.k.utils.config.FileReader;
import org.apache.commons.lang3.StringUtils;

public class ActionEngine {
    private ActionKeywords keywords;

    public ActionEngine() {
        this.keywords = new ActionKeywords();
    }

    public void performAction(String pageName, String elementName, String actionType, String testData) {
        String locator = null;

        if (StringUtils.isNotEmpty(actionType) && StringUtils.isNotEmpty(elementName))
            locator = readPageObject(pageName, elementName);

        if(StringUtils.isEmpty(actionType))
            actionType = "skip";

        switch (ActionEnum.valueOf(actionType)) {
            case click:
                keywords.click(locator);
                break;
            case input:
                keywords.input(locator, testData);
                break;
            case navigate:
                keywords.navigate(testData);
                break;
            case waitFor:
                keywords.waitFor(testData);
                break;
            case openBrowser:
                keywords.openBrowser(testData);
                break;
            case closeBrowser:
                keywords.closeBrowser();
                break;
            case isDisplayed:
                keywords.isDisplayed(locator);
                break;
            case skip:
                //Do nothing..
                break;
            default:
                System.out.println("Invalid Action type .. Please implement "+actionType+" - action type method first");
        }

    }


    private String readPageObject(String pageName, String elementName) {
        return new FileReader().strReadIniFile(pageName, elementName);
    }
}
