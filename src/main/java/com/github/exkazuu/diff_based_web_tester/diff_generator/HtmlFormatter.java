package com.github.exkazuu.diff_based_web_tester.diff_generator;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class HtmlFormatter {
    public static String format(String content) {
        DOMParser parser = new DOMParser();
        try {
            parser.parse(new InputSource(new ByteArrayInputStream(content.getBytes())));
            Document document = parser.getDocument();
            OutputFormat format = new OutputFormat(document);
            format.setIndenting(true);
            ByteOutputStream stream = new ByteOutputStream();
            XMLSerializer serializer = new XMLSerializer(stream, format);
            serializer.serialize(document);
            return stream.toString();
        } catch (SAXException e) {

        } catch (IOException e) {

        }
        return null;
    }
}
