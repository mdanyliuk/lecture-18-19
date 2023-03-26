package com.kolay.emailsender.repository;

import com.kolay.emailsender.data.MessageData;
import com.kolay.emailsender.data.MessageStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<MessageData, String> {

    List<MessageData> findByStatus(MessageStatus messageStatus);
}
