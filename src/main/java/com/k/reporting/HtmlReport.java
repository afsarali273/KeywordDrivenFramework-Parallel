package com.k.reporting;

import com.k.excel_model.TestSuitePojo;
import com.k.gmail.GmailServices;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HtmlReport {

    private List<TestSuitePojo> testSuites;

    public HtmlReport(List<TestSuitePojo> testSuites) {
        this.testSuites = testSuites;
    }

    public void createHtmlReport() {
        String htmlTemplatePath = "./src/main/resources/report/report.html";
        Document doc = null;
        try {
            doc = Jsoup.parse(new File(htmlTemplatePath), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements dom = doc.children();
        dom.select("table>tbody").after(getRow());
       // GmailServices.getInstance().setHtml(dom.html());
        generateHtmlReport(dom.html());
    }

    public String getRow() {
        StringBuilder builder = new StringBuilder();
        AtomicInteger i = new AtomicInteger();
        this.testSuites.stream().forEach(testCases -> {

            testCases.getTestCaseList().forEach(tests -> {
                String testCaseId = tests.getTestCaseName(); //TestCase Name

                builder.append("<tr>\n")
                        .append("            <td class=\"testName\" colspan=\"6\"><span>" + testCaseId + " </span></td>\n")
                        .append("        </tr>");
                tests.getTestSteps().forEach(steps -> {

                    String stepsDesc = steps.getDescription();
                    String stepStatus = steps.getResult();
                    String status = stepStatus.contains("Passed") ? "statusPass" : "statusFail";
                    String errorMessage = StringUtils.isNotEmpty(steps.getErrorMessage()) ? steps.getErrorMessage() : " ";
                    builder
                            .append("<tr>\n")
                            .append("            <td><span> " + (i.incrementAndGet()) + "</span></td>\n")
                            .append("            <td><span>" + steps.getTestCaseId() + "</span></td>\n")
                            .append("            <td><span>" + stepsDesc + "</span></td>\n")
                            .append("            <td><span>1 min</span></td>\n")
                            .append("            <td class=" + status + "><span>" + stepStatus + "</span></td>\n")
                            .append("            <td style=\"width: 10%;color: red;\"><span>" + errorMessage + "</span></td>\n")
                            .append("        </tr>");
                });

            });

        });
        return builder.toString();
    }

    private void generateHtmlReport(String htmlString) {
        try {
            File folder = new File("./output");
            if (!folder.exists()) {
                folder.mkdir();
            }
            File htmlFile = new File("./output/HtmlReport.html");
            htmlFile.createNewFile();
            FileOutputStream oFile = new FileOutputStream(htmlFile, false);
            oFile.write(("<html>\n" + htmlString + "\n</html>").getBytes());
            oFile.close();
        } catch (Exception e) {
            System.out.println("Error occurred while creating html report : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
