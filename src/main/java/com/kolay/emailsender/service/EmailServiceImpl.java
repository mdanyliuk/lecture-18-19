package com.kolay.emailsender.service;

import com.kolay.emailsender.data.MessageData;
import com.kolay.emailsender.data.MessageStatus;
import com.kolay.emailsender.dto.MessageCreateDto;
import com.kolay.emailsender.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final MessageRepository messageRepository;

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public String createMessage(MessageCreateDto dto) {
        MessageData messageData = messageRepository.save(convertDtoToData(dto));
        sendMessage(messageData);
        return messageData.getId();
    }

    @Override
    public void sendMessage(MessageData messageData) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(messageData.getTo());
        msg.setSubject(messageData.getSubject());
        msg.setText(messageData.getBody());
        try {
            emailSender.send(msg);
            messageData.setStatus(MessageStatus.SUCCESS);
            messageData.setErrorMessage("");
        } catch (Exception ex) {
            messageData.setErrorMessage(ex.getClass().getCanonicalName() + ": " + ex.getMessage());
            messageData.setStatus(MessageStatus.ERROR);
        }
        messageRepository.save(messageData);
    }

    @Override
    @Scheduled(fixedRate = 300000)
    @Async
    public void sendMessagesWithErrorStatus() {
        List<MessageData> messageDataList = messageRepository.findByStatus(MessageStatus.ERROR);
        for (MessageData messageData : messageDataList) {
            sendMessage(messageData);
        }
    }

    private MessageData convertDtoToData(MessageCreateDto dto) {
        MessageData messageData = new MessageData();
        messageData.setSubject(dto.getSubject());
        messageData.setTo(dto.getTo());
        messageData.setBody(dto.getBody());
        messageData.setStatus(MessageStatus.NEW);
        messageData.setErrorMessage("");
        return messageData;
    }
}
