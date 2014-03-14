package com.github.exkazuu.diff_based_web_tester.diff_generator;

import com.github.exkazuu.diff_based_web_tester.diff_generator.HtmlDiffGenerator;
import com.github.exkazuu.diff_based_web_tester.diff_generator.HtmlFormatter;
import com.github.exkazuu.diff_based_web_tester.diff_generator.MyersDiffGenerator;
import com.github.exkazuu.diff_based_web_tester.diff_generator.ThreeDMDiffGenerator;
import com.github.exkazuu.diff_based_web_tester.diff_generator.xdiff.XDiffGenerator;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.github.exkazuu.diff_based_web_tester.diff_generator.DebugUtil;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class TestCase {
	private FirefoxDriver driver;
	private List<HtmlDiffGenerator> generatos;

	@Before
	public void before() {
		driver = new FirefoxDriver();
		generatos = Lists.newArrayList();
		generatos.add(new MyersDiffGenerator());
		generatos.add(new XDiffGenerator());
		generatos.add(new ThreeDMDiffGenerator());
	}

	@After
	public void after() {
		driver.quit();
	}

	@Test
	public void testGoogle() throws InterruptedException {
		compareDiffAlgorithmsByUrls("https://www.google.co.jp/?#q=abc",
				"https://www.google.co.jp/?#q=def");
	}

	@Test
	public void testGithub() throws InterruptedException {
		compareDiffAlgorithmsByUrls("https://github.com/erikhuda/thor",
				"https://github.com/junit-team/junit");
	}

	@Test
	public void testTwitter() throws InterruptedException {
		compareDiffAlgorithmsByUrls("https://twitter.com/john",
				"https://twitter.com/bob");
	}

	@Test
	public void testTodoMvc() throws InterruptedException {
		String html1 = retrieveHtml("http://todomvc.com/architecture-examples/backbone/");
		driver.findElement(By.id("new-todo")).sendKeys("test\n");
		sleep();
		String html2 = retrieveHtml();

		compareDiffAlgorithmsByHtmls(html1, html2);
	}

	private void compareDiffAlgorithmsByUrls(String url1, String url2) {
		String html1 = retrieveHtml(url1);
		String html2 = retrieveHtml(url2);

		compareDiffAlgorithmsByHtmls(html1, html2);
	}

	private void compareDiffAlgorithmsByHtmls(String html1, String html2) {
		DebugUtil.writeLogFile("_raw1.html", html1);
		DebugUtil.writeLogFile("_raw2.html", html2);
		
		assertThat(html1, is(not(html2)));
		
		for (HtmlDiffGenerator g : generatos) {
			String diff = g.generateDiffContent(html1, html2);
			DebugUtil.writeLogFile("_" + g.getClass().getSimpleName() + ".txt",
					diff);
			System.out.println(g.getClass().getSimpleName() + ": "
					+ diff.length());
		}
	}

	private String retrieveHtml(String url) {
		driver.get(url);
		sleep();
		return retrieveHtml();
	}

	private String retrieveHtml() {
		return HtmlFormatter.format(driver.getPageSource());
	}

	private void sleep() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}