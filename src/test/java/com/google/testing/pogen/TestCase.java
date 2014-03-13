package com.google.testing.pogen;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.github.exkazuu.diff_based_web_tester.diff_generator.HtmlDiffGenerator;
import com.github.exkazuu.diff_based_web_tester.diff_generator.MyersDiffGenerator;
import com.google.common.collect.Lists;

public class TestCase {
  private FirefoxDriver driver;
  private List<HtmlDiffGenerator> generatos;

  @Before
  public void before() {
    driver = new FirefoxDriver();
    generatos = Lists.newArrayList();
    generatos.add(new MyersDiffGenerator());
  }

  @After
  public void after() {
    driver.quit();
  }

  @Test
  public void testGoogle() throws InterruptedException {
    compareDiffAlgorithms("https://www.google.co.jp/?#q=abc", "https://www.google.co.jp/?#q=def");
  }

  @Test
  public void testGithub() throws InterruptedException {
    compareDiffAlgorithms("https://github.com/erikhuda/thor", "https://github.com/junit-team/junit");
  }

  private void compareDiffAlgorithms(String url1, String url2) {
    String html1 = retrieveHtml(url1);
    String html2 = retrieveHtml(url2);

    assertThat(html1, is(not(html2)));

    for (HtmlDiffGenerator g : generatos) {
      String diff = g.generateDiffContent(html1, html2);
      System.out.println(g.getClass().getSimpleName() + ": " + diff.length());
    }
  }

  private String retrieveHtml(String url) {
    driver.get(url);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }
    String newLien = System.lineSeparator();
    return driver.getPageSource().replace("<", newLien + "<").replace(">", ">" + newLien);
  }
}
