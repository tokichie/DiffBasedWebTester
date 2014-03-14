package com.github.exkazuu.diff_based_web_tester.diff_generator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlFormatter {
	public static String format(String content) {
		{
			String str = content.replaceAll("\\s*<", "\n<")
					.replaceAll(">\\s*", ">\n")
					.replaceAll("\\s*\\n[\\n\\s]*", "\n").trim();
			DebugUtil.writeLogFile("_before.html", str);
		}

		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(new ByteArrayInputStream(content
					.getBytes())));
			Document document = parser.getDocument();

			DOMImplementation domImpl = document.getImplementation();
			DOMImplementationLS factory = (DOMImplementationLS) domImpl;

			LSSerializer serializer = factory.createLSSerializer();
			DOMConfiguration config = serializer.getDomConfig();
			config.setParameter("format-pretty-print", Boolean.TRUE);
			config.setParameter("element-content-whitespace", Boolean.FALSE);

			LSOutput lsOutput = factory.createLSOutput();
			lsOutput.setEncoding("UTF-8");
			StringWriter stringWriter = new StringWriter();
			lsOutput.setCharacterStream(stringWriter);
			serializer.write(document, lsOutput);
			String str = stringWriter.toString().replaceAll("\\s*<", "\n<")
					.trim();
			DebugUtil.writeLogFile("_after.html", str);
			return str;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
