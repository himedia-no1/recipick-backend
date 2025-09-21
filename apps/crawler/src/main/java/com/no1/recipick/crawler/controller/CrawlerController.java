package com.no1.recipick.crawler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crawler")
@RequiredArgsConstructor
@Slf4j
public class CrawlerController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/start")
    public String startCrawling() {
        try {
            rabbitTemplate.convertAndSend("recipe.crawling.start", "start crawling");
            log.info("Crawling start message sent to queue");
            return "Crawling job started successfully";
        } catch (Exception e) {
            log.error("Failed to start crawling", e);
            return "Failed to start crawling: " + e.getMessage();
        }
    }

    @PostMapping("/stop")
    public String stopCrawling() {
        try {
            rabbitTemplate.convertAndSend("recipe.crawling.stop", "stop crawling");
            log.info("Crawling stop message sent to queue");
            return "Crawling job stop signal sent";
        } catch (Exception e) {
            log.error("Failed to stop crawling", e);
            return "Failed to stop crawling: " + e.getMessage();
        }
    }
}