package com.github.exkazuu.diff_based_web_tester.diff_generator;

public abstract class HtmlDiffGenerator {
  public abstract String generateDiffContent(String input1, String input2);
}
