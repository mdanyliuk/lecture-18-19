package com.kolay.emailsender.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName="messages")
public class MessageData {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String subject;

    @Field(type = FieldType.Text)
    private String to;

    @Field(type = FieldType.Text)
    private String body;

    @Field(type = FieldType.Text)
    private MessageStatus status;

    @Field(type = FieldType.Text)
    private String errorMessage;
}
