package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.BitbucketClient;
import com.cdancy.bitbucket.rest.domain.support.SupportZipTask;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "SupportApiLiveTest", singleThreaded = true)
public class SupportApiLiveTest {
    @BeforeClass
    private SupportApi api() {
        BitbucketClient client = BitbucketClient.builder()
            .endPoint("http://127.0.0.1:7990")
            .credentials("engadmin:Pega2014")
            .build();

        return client.api().supportApi();
    }

    @Test
    public void testSupportZipCreation() {
        SupportZipTask supportZipTask = api().createSupportZip();
        assertIntegrity(supportZipTask);
    }

    private void assertIntegrity(SupportZipTask supportZipTask) {
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
