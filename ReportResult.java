package operations;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIResults;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ReportResult extends BaseSetup {
    public static TestLinkAPIResults reportOutput;
    public static String DEVKEY;
    public static URL testLinkUrl;

    public static void reportResult() throws Exception {
        updateExecutionStatus();
        getExecutionId();
        uploadScreenshot(TakeScreenshot.displayedAreaImagePath);
    }

    public static void updateExecutionStatus() throws Exception {
        DEVKEY = properties.getProperty("tldevkey");
        testLinkUrl = new URL("https://testlink.example.com/lib/api/xmlrpc/v1/xmlrpc.php");
        TestLinkAPIClient api = new TestLinkAPIClient(DEVKEY, testLinkUrl.toString());
        String testPlan = "Automation Plan";
        String testProject = myProject
        String build = "Build1";

        reportOutput = api.reportTestCaseResult(testProject, testPlan, testCaseId, build, testNote, testResult);
    }

    public static Integer getExecutionId(){
        String executionResult = reportOutput.toString();
        String executionIdValue = executionResult.split("[=\\,]")[2];
        return Integer.parseInt(executionIdValue);
    }

    public static void uploadScreenshot(String imagePath){
        File attachmentFile = new File(imagePath);

        String fileContent = null;
        try {
            byte[] byteArray = FileUtils.readFileToByteArray(attachmentFile);
            fileContent = new String(Base64.encodeBase64(byteArray));
        } catch (IOException e) {
            e.printStackTrace( System.err );
            System.exit(-1);
        }

        Integer executionId = getExecutionId();
        System.out.println("Execution id:" +executionId);
        System.out.println("Uploading file: "+imagePath);

        TestLinkAPI api = new TestLinkAPI(testLinkUrl,DEVKEY);
        api.uploadExecutionAttachment(
                executionId, //executionId
                "Execution for "+testCaseId, //title
                "Automation execution for"+testCaseId, //description
                System.currentTimeMillis()+".png", //fileName
                "image/png", //fileType
                fileContent); //content

        System.out.println("Attachment uploaded");
    }
}
