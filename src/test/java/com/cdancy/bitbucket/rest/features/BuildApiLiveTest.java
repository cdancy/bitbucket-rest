package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import com.cdancy.bitbucket.rest.domain.admin.UserPage;
import com.cdancy.bitbucket.rest.domain.build.StatusPage;
import com.cdancy.bitbucket.rest.domain.build.Summary;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "BuildApiLiveTest")
public class BuildApiLiveTest extends BaseBitbucketApiLiveTest {

    @Test
    public void testGetStatusByNonExistentCommit() {
        StatusPage statusPage = api().status(randomString(),0,100);
        assertThat(statusPage).isNotNull();
        assertThat(statusPage.size() == 0).isTrue();
    }

    @Test
    public void testGetSummaryByNonExistentCommit() {
        Summary statusPage = api().summary(randomString());
        assertThat(statusPage).isNotNull();
        assertThat(statusPage.successful() == 0).isTrue();
        assertThat(statusPage.inProgress() == 0).isTrue();
        assertThat(statusPage.failed() == 0).isTrue();
    }

    private BuildApi api() {
        return api.buildApi();
    }
}
