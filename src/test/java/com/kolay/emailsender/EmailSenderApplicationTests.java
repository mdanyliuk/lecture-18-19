package com.kolay.emailsender;

import com.kolay.emailsender.config.TestElasticsearchConfiguration;
import com.kolay.emailsender.data.MessageData;
import com.kolay.emailsender.data.MessageStatus;
import com.kolay.emailsender.dto.MessageCreateDto;
import com.kolay.emailsender.repository.MessageRepository;
import com.kolay.emailsender.service.EmailService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.after;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@ContextConfiguration(classes = {EmailSenderApplication.class, TestElasticsearchConfiguration.class})
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmailSenderApplicationTests {

	@Value("${kafka.topic.emailCreate}")
	private String emailCreateTopic;

	@Autowired
	KafkaOperations<String, MessageCreateDto> kafkaOperations;

	@MockBean
	JavaMailSender javaMailSender;

	@SpyBean
	EmailService spyEmailService;

	@SpyBean
	MessageRepository spyMessageRepository;

	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	@BeforeEach
	public void beforeEach() {
		elasticsearchOperations.indexOps(MessageData.class).createMapping();
	}

	@AfterEach
	public void afterEach() {
		elasticsearchOperations.indexOps(MessageData.class).delete();
	}

	@Test
	@Order(1)
	void testSuccessSend() {
		doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
		MessageCreateDto dto = createDto();
		kafkaOperations.send(emailCreateTopic, UUID.randomUUID().toString(), dto);

		verify(spyEmailService, after(5000)).createMessage(any());
		verify(spyMessageRepository, after(3000).times(2)).save(any());

		assertThat(elasticsearchOperations.count(Query.findAll(), MessageData.class)).isEqualTo(1L);
		MessageData msg = elasticsearchOperations.searchOne(Query.findAll(), MessageData.class).getContent();
		assertThat(msg.getBody()).isEqualTo("body");
		assertThat(msg.getStatus()).isEqualTo(MessageStatus.SUCCESS);
		assertThat(msg.getErrorMessage()).isEqualTo("");
	}

	@Test
	@Order(2)
	void testErrorSend() {
		doThrow(MailSendException.class).when(javaMailSender).send(any(SimpleMailMessage.class));
		MessageCreateDto dto = createDto();
		kafkaOperations.send(emailCreateTopic, UUID.randomUUID().toString(), dto);

		verify(spyEmailService, after(5000)).createMessage(any());
		verify(spyMessageRepository, after(3000).times(2)).save(any());

		assertThat(elasticsearchOperations.count(Query.findAll(), MessageData.class)).isEqualTo(1L);
		MessageData msg = elasticsearchOperations.searchOne(Query.findAll(), MessageData.class).getContent();
		assertThat(msg.getBody()).isEqualTo("body");
		assertThat(msg.getStatus()).isEqualTo(MessageStatus.ERROR);
		assertThat(msg.getErrorMessage()).contains("MailSendException");
	}

	private MessageCreateDto createDto() {
		return MessageCreateDto.builder()
				.subject("subject")
				.to("to")
				.body("body")
				.build();
	}

}
