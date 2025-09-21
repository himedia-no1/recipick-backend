package com.no1.recipick.crawler.service.message;

import com.no1.recipick.crawler.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlingMessageListener {

    private final JobLauncher jobLauncher;
    private final Job recipeCrawlingJob;

    @RabbitListener(queues = RabbitMQConfig.CRAWLING_START_QUEUE)
    public void startCrawling(String message) {
        log.info("Received crawling start message: {}", message);

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(recipeCrawlingJob, jobParameters);
            log.info("Recipe crawling job started successfully");

        } catch (Exception e) {
            log.error("Failed to start recipe crawling job", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.CRAWLING_STOP_QUEUE)
    public void stopCrawling(String message) {
        log.info("Received crawling stop message: {}", message);
        // TODO: 실행 중인 Job 중지 로직 구현
        // JobOperator를 사용하여 실행 중인 Job을 중지할 수 있음
    }
}