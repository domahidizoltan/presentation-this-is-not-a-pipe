package com.example.httptesting;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Application {

    private static final String BASE_URL = "http://127.0.0.1:9000/echo/";
    static final String API_KEY = "Answer to the Ultimate Question of Life, the Universe, and Everything";
    private final OkHttpClient client = new OkHttpClient();

    public void callUrls() throws IOException {
        System.out.println("Server responded with:");
        System.out.println(getEcho("42"));
        System.out.println(getEcho("21"));
    }

    public String getEcho(String value) throws IOException {
        Request request = new Request.Builder()
            .url(BASE_URL + value)
            .header("X-Api-Key", API_KEY)
            .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static void main(String[] args) throws IOException {
        Application application = new Application();
        application.callUrls();
    }

}
