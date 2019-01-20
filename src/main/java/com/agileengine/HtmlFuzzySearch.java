package com.agileengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class looks for Html elements similar to one given as a target element. The class operates with original
 * file and so called sample file.
 *
 * Similarity determined by the number of matching traits between the target element from the original file
 * and the ones found in sample file.
 *
 * Additionally, this implementation could be enhanced by so called Levinstein distance. This will account for
 * very close but not exact matches. Distance could act as a sensitivity valve.
 *
 */
public class HtmlFuzzySearch {
    private static Logger LOGGER = LoggerFactory.getLogger(HtmlFuzzySearch.class);
    private static String CHARSET_NAME = "utf8";
    private final String targetElementId = "make-everything-ok-button";

    public String process(File original, File sample) {
        Optional<Element> originalTargetElement = findElementById(original, targetElementId);
        String originalTagName = originalTargetElement.orElseThrow(()->new IllegalArgumentException("Target element is mossing in original file")).tagName();
        Map<String, String> originalTagAttributes = originalTargetElement.map(teg ->
                teg.attributes().asList().stream().collect(Collectors.toMap(Attribute::getKey, Attribute::getValue))).get();
        originalTagAttributes.remove("id");

        List<Element> similarElements = findAllSimilarElementsByAttributes(sample, originalTagName, originalTagAttributes);
        similarElements.addAll(findAllSimilarElementsByText(sample, originalTargetElement.get().text(), originalTagName)
                .orElseThrow(()->new IllegalArgumentException("No similar element was found")));

        Optional<Element> requiredItem = mostFrequentElement(similarElements);

        StringBuilder pathConcatenated = new StringBuilder();
        Element element = requiredItem.orElseThrow(()->new IllegalArgumentException("No similar element was found"));
        while (element != null) {
            pathConcatenated.insert(0, element.nodeName());
            pathConcatenated.insert(0, " > ");
            element = element.parent();
            element.classNames();
            if (element.parent() == null) {
                break;
            }
        }
        return pathConcatenated.toString();
    }

    /**
     * This subroutine picks the element which occurs most often. In case of multiple elements with maximum count
     * selection is arbitrary.
     *
     * @param similarElements
     * @return element with the most matching traits
     */
    protected Optional<Element> mostFrequentElement(List<Element> similarElements) {
        Map<String, Integer> map = new HashMap<>();
        for (Element element : similarElements) {
            Integer matchCount = map.get(element.toString());
            map.put(element.toString(), matchCount == null ? 1 : ++matchCount);
        }
        Map.Entry<String, Integer> max = null;
        for (Map.Entry<String, Integer> integerEntry : map.entrySet()) {
            if (max == null || integerEntry.getValue() > max.getValue())
                max = integerEntry;
        }
        for (Element element : similarElements) {
            if (element.toString().equals(max.getKey())) {
                return Optional.of(element);
            }
        }
        return Optional.empty();

    }

    /**
     * Populates a list with elements that match original attributes. This often results in
     * several occurrences of a particular element. Later on elements are rated based on their frequency
     * of occurrence in the returned list.
     *
     * @param sample file
     * @param originalTagName
     * @param originalTagAttributes
     * @return list beefed with elements
     */
    private List<Element> findAllSimilarElementsByAttributes(File sample, String originalTagName, Map<String, String> originalTagAttributes) {
        String cssQuery;
        List<Element> elements = new ArrayList<>();
        for (String key : originalTagAttributes.keySet()) {
            cssQuery = originalTagName + "[" + key + "=" + originalTagAttributes.get(key) + "]";
            elements.addAll(new ArrayList<>(findElementsByQuery(sample, cssQuery).orElseThrow(()->new IllegalArgumentException("No similar element was found"))));
        }
        return elements;
    }

    private static Optional<Element> findElementById(File htmlFile, String targetElementId) {
        try {
            Document document = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            return Optional.ofNullable(document.getElementById(targetElementId));
        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private static Optional<Elements> findElementsByQuery(File htmlFile, String cssQuery) {
        try {
            Document document = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());
            return Optional.ofNullable(document.select(cssQuery));

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private static Optional<List<Element>> findAllSimilarElementsByText(File htmlFile, String text, String tagName) {
        try {
            Document document = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());
            List<Element> similarElements = new ArrayList<>();
            Elements elements = document.select(tagName);
            for (Element element : elements) {
                if (element.text().equals(text)) {
                    similarElements.add(element);
                }
            }
            return Optional.of(similarElements);
        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }
}
