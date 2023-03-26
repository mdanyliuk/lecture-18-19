package com.kolay.emailsender.controller;

import com.kolay.emailsender.dto.MessageCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    @Value("${kafka.topic.emailCreate}")
    private String emailCreateTopic;

    private final KafkaOperations<String, MessageCreateDto> kafkaOperations;

    @PostMapping
    public void createEmail(@RequestBody MessageCreateDto dto) {
        kafkaOperations.send(emailCreateTopic, UUID.randomUUID().toString(), dto);
    }
}
