package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.domain.support.SupportZipStatus;
import com.cdancy.bitbucket.rest.domain.support.SupportZipDetails;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "SupportApiLiveTest", singleThreaded = true)
public class SupportApiLiveTest extends BaseBitbucketApiLiveTest{
    private SupportApi api() {
        return api.supportApi();
    }

    @Test
    public void testSupportZipCreation() {
        SupportZipDetails details = api().createSupportZip();
        assertIntegrity(details);
    }

    @Test
    public void testSupportZipStatus() {
        SupportZipDetails details = api().createSupportZip();
        SupportZipStatus supportZip = api().getSupportZipStatus(details.taskId());
        assertIntegrity(supportZip);
    }

    private void assertIntegrity(SupportZipStatus supportZip) {
        assertTaskId(supportZip.taskId());
        assertTaskProgressMessage(supportZip.progressMessage());
        assertTaskProgressPercentage(supportZip.progressPercentage());
        assertTaskStatus(supportZip.status());
        Assert.assertNotNull(supportZip.fileName());
    }

    private void assertIntegrity(SupportZipDetails supportZipTask) {
        assertTaskId(supportZipTask.taskId());
        assertTaskProgressPercentage(supportZipTask.progressPercentage());
        assertTaskProgressMessage(supportZipTask.progressMessage());
        assertTaskStatus(supportZipTask.status());
    }

    private void assertTaskProgressMessage(String progressMessage) {
        Assert.assertNotNull(progressMessage);
    }

    private void assertTaskStatus(String status) {
        Assert.assertTrue(!status.isEmpty());
    }

    private void assertTaskId(String taskId) {
        Assert.assertNotNull(taskId);
    }

    private void assertTaskProgressPercentage(Integer progressPercentage) {
        Assert.assertNotNull(progressPercentage);
    }
}
