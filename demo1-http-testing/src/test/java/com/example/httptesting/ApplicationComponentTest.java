package com.example.httptesting;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.example.httptesting.Application.API_KEY;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ApplicationComponentTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(9000), false);

    private Application underTest;

    @Before
    public void setUp() {
        underTest = new Application();
    }

    @Test
    public void test42Echoed() throws IOException {
        stubFor(makeStub("42"));

        underTest.callUrls();

        verify(1, makeExpectedRequest("42"));
    }

    @Test
    public void test21Echoed() throws IOException {
        stubFor(makeStub("21"));

        underTest.callUrls();

        verify(1, makeExpectedRequest("21"));
    }


    private MappingBuilder makeStub(String value) {
        return request(RequestMethod.GET.getName(), urlEqualTo("/echo/" + value))
            .withHeader("X-Api-Key", equalTo(API_KEY))
            .willReturn(aResponse().withBody(value.getBytes()));
    }

    private RequestPatternBuilder makeExpectedRequest(String value) {
        return getRequestedFor(urlEqualTo("/echo/" + value))
            .withHeader("X-Api-Key", equalTo(API_KEY));
    }

}
