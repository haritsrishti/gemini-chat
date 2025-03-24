package com.ai.gemini_chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class QnAService {
    private static final Logger logger = LoggerFactory.getLogger(QnAService.class);

    @Value("${gemini.api.url}")
    private String geminiApiURL;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    public QnAService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<String> getAnswer(String question) {
        String requestBody = """
            {
                "contents": [
                    {
                        "parts": [
                            {
                                "text": "%s"
                            }
                        ]
                    }
                ]
            }
            """.formatted(question);

        String fullUrl = geminiApiURL + geminiApiKey;
        logger.debug("Calling Gemini API with URL: {}", fullUrl);
        logger.debug("Request body: {}", requestBody);

        return webClient.post()
                .uri(fullUrl)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36") // Mimic PowerShell
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(body -> {
                                    logger.error("Gemini API error: Status {}, Body: {}", clientResponse.statusCode(), body);
                                    return new WebClientResponseException(
                                            clientResponse.statusCode().value(),
                                            clientResponse.statusCode().toString(),
                                            null, body.getBytes(), null);
                                }))
                .bodyToMono(String.class);
    }
}