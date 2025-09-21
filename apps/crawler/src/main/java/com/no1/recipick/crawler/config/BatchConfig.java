package com.no1.recipick.crawler.config;

import com.no1.recipick.crawler.batch.step.RecipeProcessor;
import com.no1.recipick.crawler.batch.step.RecipeReader;
import com.no1.recipick.crawler.batch.step.RecipeWriter;
import com.no1.recipick.crawler.domain.entity.Recipe;
import com.no1.recipick.crawler.dto.ProcessedRecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RecipeReader recipeReader;
    private final RecipeProcessor recipeProcessor;
    private final RecipeWriter recipeWriter;

    @Bean
    public Job recipeCrawlingJob() {
        return new JobBuilder("recipeCrawlingJob", jobRepository)
                .start(crawlingStep())
                .build();
    }

    @Bean
    public Step crawlingStep() {
        return new StepBuilder("crawlingStep", jobRepository)
                .<Recipe, ProcessedRecipeDto>chunk(10, transactionManager)
                .reader(recipeReader)
                .processor(recipeProcessor)
                .writer(recipeWriter)
                .build();
    }
}