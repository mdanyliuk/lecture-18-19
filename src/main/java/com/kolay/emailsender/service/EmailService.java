package com.kolay.emailsender.service;

import com.kolay.emailsender.data.MessageData;
import com.kolay.emailsender.dto.MessageCreateDto;

public interface EmailService {

    String createMessage(MessageCreateDto dto);

    void sendMessage(MessageData messageData);

    void sendMessagesWithErrorStatus();
}
