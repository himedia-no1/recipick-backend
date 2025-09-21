package com.no1.recipick.crawler.batch.step;

import com.no1.recipick.crawler.domain.entity.Recipe;
import com.no1.recipick.crawler.domain.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecipeReader implements ItemReader<Recipe> {

    private final RecipeRepository recipeRepository;
    private RepositoryItemReader<Recipe> repositoryItemReader;

    @Override
    public Recipe read() throws Exception {
        if (repositoryItemReader == null) {
            initializeReader();
        }

        Recipe recipe = repositoryItemReader.read();
        if (recipe != null) {
            log.debug("Reading recipe with searchIdentity: {}", recipe.getSearchIdentity());
        }

        return recipe;
    }

    private void initializeReader() {
        repositoryItemReader = new RepositoryItemReaderBuilder<Recipe>()
                .name("recipeReader")
                .repository(recipeRepository)
                .methodName("findByIsValidFalseOrderById")
                .pageSize(10)
                .arguments()
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();

        log.info("Initialized RecipeReader for invalid recipes");
    }
}