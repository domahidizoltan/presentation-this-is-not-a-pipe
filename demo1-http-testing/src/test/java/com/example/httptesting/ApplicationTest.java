package com.example.httptesting;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import java.io.IOException;

import static com.example.httptesting.Application.API_KEY;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class ApplicationTest {

    public static void main(String[] args) {
        WireMockServer mockServer = new WireMockServer(options().port(9000));
        mockServer.start();

        mockServer.addStubMapping(makeStub("42"));
        mockServer.addStubMapping(makeStub("21"));

        try {
            new Application().callUrls();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mockServer.stop();
        }
    }

    private static StubMapping makeStub(String value) {
        return request(RequestMethod.GET.getName(), urlEqualTo("/echo/" + value))
            .withHeader("X-Api-Key", equalTo(API_KEY))
            .willReturn(aResponse().withBody(value.getBytes()))
            .build();
    }

}
