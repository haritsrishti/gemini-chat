package com.ai.gemini_chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/qna")
public class AIController {
    private final QnAService qnAService;

    public AIController(QnAService qnAService) {
        this.qnAService = qnAService;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> askQuestion(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");
        if (question == null || question.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Question cannot be null or empty"));
        }
        return qnAService.getAnswer(question)
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.just(ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString())))
                .onErrorResume(Throwable.class, e ->
                        Mono.just(ResponseEntity.status(500).body("Unexpected error: " + e.getMessage())))
                .defaultIfEmpty(ResponseEntity.badRequest().body("No answer received"));
    }
}