package com.agileengine;

import org.jsoup.nodes.Element;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.*;

import static junit.framework.TestCase.assertEquals;

public class BasicCasesTest {

    File original = new File("samples/original/origin.html");

    @Test
    public void case1test() {
        HtmlFuzzySearch systemUnderTest = new HtmlFuzzySearch();
        assertEquals(" > html > body > div > div > div > div > div > div > a", systemUnderTest.process(original, new File("./samples/sample-1-evil-gemini.html")));
    }

    @Test
    public void case1test2() {
        HtmlFuzzySearch systemUnderTest = new HtmlFuzzySearch();
        assertEquals(" > html > body > div > div > div > div > div > div > div > a", systemUnderTest.process(original, new File("./samples/sample-2-container-and-clone.html")));
    }

    @Test
    public void case1test3() {
        HtmlFuzzySearch systemUnderTest = new HtmlFuzzySearch();
        assertEquals(" > html > body > div > div > div > div > div > div > a", systemUnderTest.process(original, new File("./samples/sample-3-the-escape.html")));
    }

    @Test
    public void case1test4() {
        HtmlFuzzySearch systemUnderTest = new HtmlFuzzySearch();
        assertEquals(" > html > body > div > div > div > div > div > div > a", systemUnderTest.process(original, new File("./samples/sample-4-the-mash.html")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingTargetElement() {
        HtmlFuzzySearch systemUnderTest = new HtmlFuzzySearch();
       systemUnderTest.process(new File("./samples/sample-4-the-mash.html"), new File("./samples/sample-4-the-mash.html"));
    }

    @Test
    public void selectElementFromSimilarTest() {
        Element element1 = Mockito.mock(Element.class);
        Element element2 = Mockito.mock(Element.class);
        Element element3 = Mockito.mock(Element.class);
        Element element4 = Mockito.mock(Element.class);
        Element element5 = Mockito.mock(Element.class);
        Mockito.when(element1.toString()).thenReturn("1");
        Mockito.when(element2.toString()).thenReturn("2");
        Mockito.when(element3.toString()).thenReturn("3");
        Mockito.when(element4.toString()).thenReturn("4");
        Mockito.when(element5.toString()).thenReturn("2");
        List<Element> elements = new ArrayList<>(Arrays.asList(element1, element2, element3, element4, element5));
        HtmlFuzzySearch htmlFuzzySearch = new HtmlFuzzySearch();
        assertEquals(Optional.of(element2), htmlFuzzySearch.mostFrequentElement(elements));
    }

}
