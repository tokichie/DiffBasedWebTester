package com.github.exkazuu.diff_based_web_tester.diff_generator.xdiff;

import com.github.exkazuu.diff_based_web_tester.diff_generator.HtmlFormatter;
import com.google.common.io.Files;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

public class XDiffGenerator {
    private static final String TMP_FILE_NAME_1 = "tmp1.txt";
    private static final String TMP_FILE_NAME_2 = "tmp2.txt";
    private static final String TMP_OUT_FILE_NAME = "tmp_out.txt";

    public String generateDiffContent(String input1, String input2, String lineSeparator) {
        applyXdiff(input1, input2, lineSeparator);
        DOMParser parser = new DOMParser();
        try {
            File outFile = new File(TMP_OUT_FILE_NAME);
            parser.parse(new InputSource(new FileInputStream(outFile)));
            Document document = parser.getDocument();
            DepthFirstSearcher s = new DepthFirstSearcher();
            s.search(document.getDocumentElement());
            outFile.delete();
            return s.result();
        } catch (SAXException e) {

        } catch (IOException e) {

        }
        return null;
    }

    private void applyXdiff(String input1, String input2, String lineSeparator) {
        String formatted1 = HtmlFormatter.format(input1);
        String formatted2 = HtmlFormatter.format(input2);
        File tmp1 = new File(TMP_FILE_NAME_1);
        File tmp2 = new File(TMP_FILE_NAME_2);
        try {
            Files.write(formatted1.getBytes(), tmp1);
            Files.write(formatted2.getBytes(), tmp2);
            new XDiff(TMP_FILE_NAME_1, TMP_FILE_NAME_2, TMP_OUT_FILE_NAME);
            tmp1.delete();
            tmp2.delete();
        } catch (IOException e) {

        }
    }

    private class DepthFirstSearcher {
        private Stack<Node> stack = new Stack<>();
        private StringBuilder builder = new StringBuilder();

        private void search(Node node) {
            if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                ProcessingInstruction instruction = (ProcessingInstruction) node;
                String line = instructionToString(instruction);
                if (line != null) {
                    builder.append(line).append(System.lineSeparator());
                }
            }

            NodeList children = node.getChildNodes();
            for (int i = children.getLength() - 1; i >= 0; i--) {
                stack.push(children.item(i));
            }
            if (stack.isEmpty()) {
                return;
            }
            Node next = stack.pop();
            search(next);
        }

        private String instructionToString(ProcessingInstruction instruction) {
            Element parent = parentElement(instruction);
            String type = instruction.getTarget().toUpperCase();
            parent.removeChild(instruction);
            String operationTarget = instruction.getData().split("\\s")[0];
            switch (type) {
                case "INSERT":
                case "DELETE":
                    if (operationTarget.equalsIgnoreCase(parent.getTagName())) {
                        return type + ":" + elementToString(parent).replace(System.lineSeparator(), "");
                    } else {
                        return type + " " + operationTarget + ":" + parent.getAttribute(operationTarget);
                    }
                case "UPDATE":
                    String fromData = instruction.getData().split("\\s", 2)[1];
                    if (operationTarget.equals("FROM")) {
                        return type + ":[oldValue=" + fromData + ", newValue="
                                + elementToString(parent).replace(System.lineSeparator(),"")
                                + "]";
                    } else {
                        return type + " " + operationTarget + ":[oldValue=" + fromData
                                + ", newValue=" + parent.getAttribute(operationTarget) + "]";
                    }
                default:
                    return null;
            }
        }

        private String elementToString(Element element) {
            StringBuilder builder = new StringBuilder();
            buildNodeString(element, builder);
            return builder.toString();
        }

        private void buildNodeString(Node node, StringBuilder builder) {
            if (node.getNodeType() == Node.TEXT_NODE) {
                builder.append(node.getNodeValue());
            } else if (node.getNodeType() == Node.ELEMENT_NODE) {
                builder.append("<").append(node.getNodeName()).append(">");
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    buildNodeString(children.item(i), builder);
                }
                builder.append("</").append(node.getNodeName()).append(">");
            }
        }

        private Element parentElement(Node node) {
            while (!(node instanceof Element)) {
                node = node.getParentNode();
            }
            return (Element) node;
        }

        public String result() {
            return builder.toString();
        }
    }
}