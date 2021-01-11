package com.cdancy.bitbucket.rest.features;

import com.cdancy.bitbucket.rest.BaseBitbucketMockTest;
import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.domain.labels.LabelsPage;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = "unit", testName = "LabelsApiMockTest")
public class LabelsApiMockTest extends BaseBitbucketMockTest {

    public void testListAllLabels() throws IOException {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/labels-list.json")).setResponseCode(201));

        try (final BitbucketApi baseApi = api(server.url("/").url())) {
            final LabelsPage labelsPage = baseApi.labelsApi().list(null);
            assertThat(labelsPage).isNotNull();
            assertThat(labelsPage.errors().isEmpty()).isTrue();
            assertThat(labelsPage.values().size() > 0).isTrue();

            assertThat(labelsPage.values().get(0).name()).isEqualTo("labelName");

        } finally {
            server.shutdown();
        }
    }

    public void testListAllLabelError() throws IOException {
        final MockWebServer server = mockWebServer();

        server.enqueue(new MockResponse().setBody(payloadFromResource("/errors.json")).setResponseCode(401));

        try (final BitbucketApi baseApi = api(server.url("/").url())) {
            final LabelsPage labelsPage = baseApi.labelsApi().list(null);
            assertThat(labelsPage).isNotNull();
            assertThat(labelsPage.values().size() > 0).isFalse();

            assertThat(labelsPage.errors()).hasSizeGreaterThan(0);

        } finally {
            server.shutdown();
        }
    }
}
