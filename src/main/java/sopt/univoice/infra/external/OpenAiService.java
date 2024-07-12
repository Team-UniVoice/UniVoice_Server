package sopt.univoice.infra.external;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String summarizeText(String text) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(OPENAI_API_URL);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + openaiApiKey);

            JSONObject json = new JSONObject();
            json.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", "You are an assistant that summarizes text in Korean to 150 characters or less."));
            messages.put(new JSONObject().put("role", "user").put("content", "다음 텍스트를 150자 이내로 요약해 주세요: \"" + text + "\""));
            json.put("messages", messages);
            json.put("max_tokens", 200);
            json.put("temperature", 0.7);

            StringEntity entity = new StringEntity(json.toString());
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("API Response: " + responseBody);

                JSONObject responseJson = new JSONObject(responseBody);

                if (responseJson.has("choices")) {
                    return responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
                } else {
                    throw new IOException("API response does not contain 'choices' key.");
                }
            }
        }
    }
}
