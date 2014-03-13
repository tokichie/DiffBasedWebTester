package com.google.testing.pogen;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestCase {
  private FirefoxDriver driver;

  @Before
  public void before() {
    driver = new FirefoxDriver();
  }

  @After
  public void after() {
    driver.quit();
  }

  @Test
  public void test() throws InterruptedException {
    driver.get("https://www.google.co.jp/?#q=abc");
    Thread.sleep(1000);
    String html1 = driver.getPageSource();

    driver.get("https://www.google.co.jp/?#q=def");
    Thread.sleep(1000);
    String html2 = driver.getPageSource();

    System.out.println(html1);
    System.out.println();
    System.out
        .println("--------------------------------------------------------------------------------");
    System.out.println();

    assertThat(html1, is(not(html2)));
  }
}
