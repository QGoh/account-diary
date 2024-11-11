package com.accountdiary;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
public class ApiHandler {
    @Inject
    private OkHttpClient okHttpClient;

    private final String URL = "";

    public void postRequest(DiaryItem diaryItem) {
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", GSON.toJson(diaryItem));

        if (diaryItem.getScreenshotFile() != null)
        {
            requestBodyBuilder.addFormDataPart("file", "image.png",
                    RequestBody.create(MediaType.parse("image/png"), diaryItem.getScreenshotFile()));
        }

        MultipartBody requestBody = requestBodyBuilder.build();

        HttpUrl u = HttpUrl.parse(URL);
        if (u == null)
        {
            log.info("Malformed webhook url {}", URL);
        }

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                log.debug("Error submitting webhook", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                response.close();
            }
        });
    }
}
