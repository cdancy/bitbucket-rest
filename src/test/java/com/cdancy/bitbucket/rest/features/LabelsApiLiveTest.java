package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.BaseBitbucketApiLiveTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "live", testName = "LabelsApiLiveTest", singleThreaded = true)
public class LabelsApiLiveTest extends BaseBitbucketApiLiveTest {

    @Test
    public void testLabel() {
        assertThat(api().list(null).values()).isEmpty();
    }

    private LabelsApi api() {
        return api.labelsApi();
    }
}
