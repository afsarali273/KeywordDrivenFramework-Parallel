package com.k.Enum;

public enum ActionEnum {
    openBrowser("openBrowser"),
    navigate("navigate"),
    click("click"),
    input("input")  ,
    waitFor("waitFor"),
    closeBrowser("closeBrowser"),
    isDisplayed("isDisplayed"),
    skip("skip");

    private String value;

    ActionEnum(String s) {
        this.value = s;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
