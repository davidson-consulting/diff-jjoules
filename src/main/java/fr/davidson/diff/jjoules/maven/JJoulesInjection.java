package fr.davidson.diff.jjoules.maven;

import fr.davidson.diff.jjoules.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 30/11/2020
 */
public class JJoulesInjection {

    private static final Logger LOGGER = LoggerFactory.getLogger(JJoulesInjection.class);

    private static final String POM_FILE = "pom.xml";

    private static final String JJOULED_POM_FILE = "pom_jjouled.xml";

    private final String absolutePathToProjectRoot;

    public JJoulesInjection(String absolutePathToProjectRoot) {
        this.absolutePathToProjectRoot = absolutePathToProjectRoot;
    }

    public String inject() {
        LOGGER.info("Injecting JUnit-Jjoules in {}", this.absolutePathToProjectRoot + "/" + POM_FILE);
        try {
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            final Document document = docBuilder.parse(this.absolutePathToProjectRoot + "/" + POM_FILE);
            final Node root = Utils.findSpecificNodeFromGivenRoot(document.getFirstChild(), Utils.PROJECT);
            this.addJJoulesDependencies(document, root);
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final DOMSource source = new DOMSource(document);
            LOGGER.info("Output new POM file in {}", this.absolutePathToProjectRoot + "/" + JJOULED_POM_FILE);
            final String newPomFilename = this.absolutePathToProjectRoot + "/" + JJOULED_POM_FILE;
            final StreamResult result = new StreamResult(new File(newPomFilename));
            transformer.transform(source, result);

            return newPomFilename;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addJJoulesDependencies(Document document, Node root) {
        final Node dependencies = Utils.findOrCreateGivenNode(document, root, Utils.DEPENDENCIES);
        final Element dependency = Utils.createDependency(document,
                "org.powerapi.jjoules",
                "junit-jjoules",
                "1.0-SNAPSHOT"
        );
        dependencies.appendChild(dependency);
    }

}
