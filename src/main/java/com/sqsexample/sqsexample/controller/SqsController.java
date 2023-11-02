package com.sqsexample.sqsexample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@RestController
public class SqsController {
    private static final String QUEUE_PREFIX = "Sqs-queue-test";
    private static final SqsClient SQS_CLIENT = SqsClient.builder().region(Region.EU_WEST_2).build();
    private static String queueUrl;

    @GetMapping("/createQueue")
    public void createQueue() {
        String queueName = QUEUE_PREFIX + System.currentTimeMillis();

        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(queueName)
                .build();

        SQS_CLIENT.createQueue(createQueueRequest);

        GetQueueUrlResponse getQueueUrlResponse = SQS_CLIENT.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
        queueUrl = getQueueUrlResponse.queueUrl();
    }

    @GetMapping("/listQueues")
    public String listQueues() {
        ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder()
                .queueNamePrefix(QUEUE_PREFIX)
                .build();

        ListQueuesResponse listQueuesResponse = SQS_CLIENT.listQueues(listQueuesRequest);
        StringBuilder sb = new StringBuilder();

        listQueuesResponse.queueUrls().forEach(sb::append);
        return sb.toString();
    }

    @GetMapping("/sendMessage")
    public void sendMessage(@RequestParam("text") String message) {
        SendMessageRequest messageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
        SQS_CLIENT.sendMessage(messageRequest);
    }
}
