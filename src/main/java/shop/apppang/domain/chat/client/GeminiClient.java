package shop.apppang.domain.chat.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent";

    private final RestClient restClient = RestClient.create();
    private final String apiKey;

    public GeminiClient(@Value("${gemini.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @param systemPrompt 역할·상품목록 등 지시사항
     * @param contents     대화 턴들 [{role:"user"/"model", parts:[{text:...}]}]
     * @return AI 답변 텍스트
     */
    public String generate(String systemPrompt, List<Map<String, Object>> contents) {
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))),
                "contents", contents
        );

        GeminiResponse response = restClient.post()
                .uri(API_URL + "?key=" + apiKey)
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(GeminiResponse.class);

        if (response == null
                || response.candidates() == null
                || response.candidates().isEmpty()) {
            return "죄송해요, 답변을 생성하지 못했어요. 다시 시도해주세요.";
        }
        return response.candidates().get(0).content().parts().get(0).text();
    }
}