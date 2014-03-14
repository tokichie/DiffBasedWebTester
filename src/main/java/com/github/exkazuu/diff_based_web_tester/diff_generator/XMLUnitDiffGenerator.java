package com.github.exkazuu.diff_based_web_tester.diff_generator;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

/**
 * Generate diff using XMLUnit library {@see http://xmlunit.sourceforge.net/}
 */
public class XMLUnitDiffGenerator extends HtmlDiffGenerator {
	@Override
	public String generateDiffContent(String input1, String input2, String lineSeparator) {
		StringBuilder builder = new StringBuilder();
		try {
			DetailedDiff myDiff = new DetailedDiff(new Diff(input1, input2));
			List<Difference> allDifferences = myDiff.getAllDifferences();
			for (Difference difference: allDifferences) {
				builder.append(difference.getDescription()).append("[A=")
						.append(difference.getControlNodeDetail().getValue())
						.append(",B=")
						.append(difference.getTestNodeDetail().getValue())
						.append("]")
						.append(System.lineSeparator());
			}
		} catch (IOException | SAXException e) {
			System.err.println("XMLUnit diff error");
			e.printStackTrace();
		}
		return builder.toString();
	}
}
