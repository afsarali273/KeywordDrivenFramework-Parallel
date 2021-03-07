package com.k.reporting;

import com.k.excel_model.TestSuitePojo;
import com.k.pojo.StatusUpdate;
import com.k.utils.config.ConfigFileReader;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
        dom.select("#detailedResult>tbody").after(getRow());
        dom.select("#failedTests>tbody").after(getFailedTestRow());
        dom.select("#mainSummaryTable>tbody").after(getMainSummaryStatus());
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

    public String getFailedTestRow(){

        StringBuilder builder = new StringBuilder();
        this.testSuites.stream().forEach(suite -> {
            suite.getTestCaseList().stream().filter( x-> x.getResult().contains("Failed"))
                    .forEach( testCase -> {
                       String testName =  testCase.getTestCaseName();
                       String testId = testCase.getTestCaseId();
                       String result =  testCase.getResult();
                        builder.append(" <tr>\n")
                                .append( "            <td>\n")
                                .append("             "+testId+"\n")
                                .append( "            </td>\n")
                                .append( "            <td>\n")
                                .append("             "+testName+"\n")
                                .append( "            </td>\n")
                                .append( "            <td>\n" )
                                .append( "                2 min\n")
                                .append( "            </td>\n" )
                                .append( "            <td>"+result+"</td>\n")
                                .append( "        </tr>\n");
                });
        });

        AtomicBoolean isFailed = new AtomicBoolean(false);
        this.testSuites.stream().forEach(suite -> {
            if (suite.getTestCaseList().stream()
                    .anyMatch(x -> x.getResult().contains("Failed"))) {
                isFailed.set(true);
            }
        });

        if(!isFailed.get())
            builder.append("<td colspan=\"4\"> N/A</td>");
        return builder.toString();

    }

    private  String getMainSummaryStatus(){
        StringBuilder builder = new StringBuilder();
        StatusUpdate statusUpdate = new StatusUpdate();
        statusUpdate = statusUpdate.testStatus(this.testSuites);
        String applicationName = ConfigFileReader.getInstance().applicationName;
        builder.append("        <tr>\n" +
                "            <td> "+applicationName+"</td>\n" +
                "            <td> "+ getDateTime() +"</td>\n" +
                "            <td> "+(statusUpdate.passCount + statusUpdate.failCount)+"</td>\n" +
                "            <td> "+statusUpdate.passCount+"</td>\n" +
                "            <td> "+statusUpdate.failCount+"</td>\n" +
                "            <td style=\"color: green;font-size: large\"> "+statusUpdate.passPerc+"%</td>\n" +
                "            <td style=\"color: red;font-size: large\"> "+statusUpdate.failPerc+"%</td>\n" +
                "        </tr>\n");

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

    private String getDateTime(){

        String pattern = "E, dd MMM yyyy HH:mm:ss z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
       return  simpleDateFormat.format(new Date());

    }

}
