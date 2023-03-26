package com.kolay.emailsender.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class MessageCreateDto {

    private String subject;
    private String to;
    private String body;
}
