package fr.davidson.diff.jjoules.class_instrumentation.duplications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class XMLReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLReader.class);

    public static Map<String, Double> readAllXML(final String pathFolder) {
        final Map<String, Double> timePerTest = new HashMap<>();
        Arrays.stream(Objects.requireNonNull(new File(pathFolder).list()))
                .filter(pathname -> pathname.endsWith(".xml"))
                .forEach(pathname -> readXML(pathFolder + "/" + pathname, timePerTest));
        return timePerTest;
    }

    public static void readXML(final String path, final Map<String, Double> timePerTest) {
        try {
            File file = new File(path);
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(file);
            final Node root = doc.getDocumentElement();
            Node currentChild = root.getFirstChild();
            while (currentChild != null) {
                if (currentChild.getNodeName() != null && "testcase".equals(currentChild.getNodeName())) {
                    final NamedNodeMap attributes = currentChild.getAttributes();
                    final String className = attributes.getNamedItem("classname").getNodeValue();
                    final String testName = attributes.getNamedItem("name").getNodeValue();
                    final Double time = Double.parseDouble(attributes.getNamedItem("time").getNodeValue());
                    timePerTest.put(className + "#" + testName, time);
                }
                currentChild = currentChild.getNextSibling();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
