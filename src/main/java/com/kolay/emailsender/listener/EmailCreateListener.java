package com.kolay.emailsender.listener;

import com.kolay.emailsender.dto.MessageCreateDto;
import com.kolay.emailsender.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailCreateListener {

    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.topic.emailCreate}")
    public String emailCreate(MessageCreateDto dto) {
        return emailService.createMessage(dto);
    }

}
