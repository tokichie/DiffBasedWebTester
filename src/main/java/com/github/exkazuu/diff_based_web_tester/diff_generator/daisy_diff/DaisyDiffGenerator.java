package com.github.exkazuu.diff_based_web_tester.diff_generator.daisy_diff;

import com.github.exkazuu.diff_based_web_tester.diff_generator.HtmlDiffGenerator;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.outerj.daisy.diff.DaisyDiff;
import org.outerj.daisy.diff.HtmlCleaner;
import org.outerj.daisy.diff.XslFilter;
import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.HtmlSaxDiffOutput;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class DaisyDiffGenerator extends HtmlDiffGenerator {

    @Override
    public String generateDiffContent(String input1, String input2) {
        return generateDiffContent(input1, input2, "\n");
    }

    @Override
    public String generateDiffContent(String input1, String input2, String lineSeparator) {
            boolean htmlDiff = true;
            boolean htmlOut = false;
            String outputFile = "daisydiff.htm";
            String[] css = new String[]{};

            InputStream oldStream = new ByteArrayInputStream(input1.getBytes());
            InputStream newStream = new ByteArrayInputStream(input2.getBytes());

            try {
                SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();

                TransformerHandler result = tf.newTransformerHandler();
                result.setResult(new StreamResult(new File(outputFile)));


                XslFilter filter = new XslFilter();

                ContentHandler postProcess = result;
                postProcess.startDocument();
                postProcess.startElement("", "diffreport", "diffreport",
                        new AttributesImpl());
                postProcess.startElement("", "diff", "diff",
                        new AttributesImpl());
                System.out.print(".");


                InputStreamReader oldReader = null;
                BufferedReader oldBuffer = null;

                InputStreamReader newISReader = null;
                BufferedReader newBuffer = null;
                try {
                    oldReader = new InputStreamReader(oldStream);
                    oldBuffer = new BufferedReader(oldReader);

                    newISReader = new InputStreamReader(newStream);
                    newBuffer = new BufferedReader(newISReader);
                    DaisyDiff.diffTag(oldBuffer, newBuffer, postProcess);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    oldBuffer.close();
                    newBuffer.close();
                    oldReader.close();
                    newISReader.close();
                }


                System.out.print(".");
                postProcess.endElement("", "diff", "diff");
                postProcess.endElement("", "diffreport", "diffreport");
                postProcess.endDocument();
            } catch (Throwable e) {
                e.printStackTrace();
                if (e.getCause() != null) {
                    e.getCause().printStackTrace();
                }
                if (e instanceof SAXException) {
                    ((SAXException) e).getException().printStackTrace();
                }
            } finally {
                try {
                    if(oldStream != null) oldStream.close();
                } catch (IOException e) {
                    //ignore this exception
                }
                try {
                    if(newStream != null) newStream.close();
                } catch (IOException e) {
                    //ignore this exception
                }
            }


        return "";
    }

    private static void doCSS(String[] css, ContentHandler handler) throws SAXException {
        handler.startElement("", "css", "css",
                new AttributesImpl());
        for(String cssLink : css){
            AttributesImpl attr = new AttributesImpl();
            attr.addAttribute("", "href", "href", "CDATA", cssLink);
            attr.addAttribute("", "type", "type", "CDATA", "text/css");
            attr.addAttribute("", "rel", "rel", "CDATA", "stylesheet");
            handler.startElement("", "link", "link",
                    attr);
            handler.endElement("", "link", "link");
        }

        handler.endElement("", "css", "css");

    }
}
