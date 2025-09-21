package com.no1.recipick.crawler.service.crawler;

import com.no1.recipick.crawler.dto.CrawledRecipeDto;
import com.no1.recipick.crawler.dto.RecipeStepDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class HtmlParsingService {

    private static final String BASE_URL = "https://www.10000recipe.com/recipe/";
    private static final int TIMEOUT_MS = 10000;

    public CrawledRecipeDto parseRecipe(String searchIdentity) {
        try {
            String url = BASE_URL + searchIdentity;
            log.info("Crawling recipe from URL: {}", url);

            Document document = Jsoup.connect(url)
                    .timeout(TIMEOUT_MS)
                    .get();

            return CrawledRecipeDto.builder()
                    .name(extractName(document))
                    .description(extractDescription(document))
                    .imageUrl(extractImageUrl(document))
                    .serving(extractServing(document))
                    .time(extractTime(document))
                    .level(extractLevel(document))
                    .ingredients(extractIngredients(document))
                    .steps(extractSteps(document))
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse recipe for searchIdentity: {}", searchIdentity, e);
            throw new RuntimeException("Recipe parsing failed", e);
        }
    }

    private String extractName(Document document) {
        Element nameElement = document.selectFirst("div.best_tit b[style*=color:#74b243]");
        return nameElement != null ? nameElement.text().trim() : null;
    }

    private String extractDescription(Document document) {
        Element descElement = document.selectFirst("div.view2_summary.st3 h3");
        return descElement != null ? descElement.text().trim() : null;
    }

    private String extractImageUrl(Document document) {
        Element imgElement = document.selectFirst("div.centeredcrop img#main_thumbs");
        return imgElement != null ? imgElement.attr("src") : null;
    }

    private String extractServing(Document document) {
        Element servingElement = document.selectFirst("div.view2_summary_info span.view2_summary_info1");
        return servingElement != null ? servingElement.text().trim() : null;
    }

    private String extractTime(Document document) {
        Element timeElement = document.selectFirst("div.view2_summary_info span.view2_summary_info2");
        return timeElement != null ? timeElement.text().trim() : null;
    }

    private String extractLevel(Document document) {
        Element levelElement = document.selectFirst("div.view2_summary_info span.view2_summary_info3");
        return levelElement != null ? levelElement.text().trim() : null;
    }

    private List<String> extractIngredients(Document document) {
        List<String> ingredients = new ArrayList<>();

        Elements ingredientElements = document.select("div.ready_ingre3 div.ingre_list_name a");
        for (Element element : ingredientElements) {
            String ingredient = element.text().trim();
            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient);
            }
        }

        log.debug("Extracted {} ingredients", ingredients.size());
        return ingredients;
    }

    private List<RecipeStepDto> extractSteps(Document document) {
        List<RecipeStepDto> steps = new ArrayList<>();

        Elements stepDivs = document.select("div[id^=stepDiv]");
        for (Element stepDiv : stepDivs) {
            String description = extractStepDescription(stepDiv);
            String imageUrl = extractStepImageUrl(stepDiv);

            if (description != null && !description.isEmpty()) {
                steps.add(RecipeStepDto.of(description, imageUrl));
            }
        }

        log.debug("Extracted {} recipe steps", steps.size());
        return steps;
    }

    private String extractStepDescription(Element stepDiv) {
        Element descElement = stepDiv.selectFirst("div[id^=stepdescr] div.media-body, div.media-body");
        if (descElement != null) {
            return descElement.text().trim();
        }
        return null;
    }

    private String extractStepImageUrl(Element stepDiv) {
        Element imgElement = stepDiv.selectFirst("div[id^=stepimg] img, div.media-right img");
        if (imgElement != null) {
            return imgElement.attr("src");
        }
        return null;
    }
}