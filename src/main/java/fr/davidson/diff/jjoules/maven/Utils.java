package fr.davidson.diff.jjoules.maven;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * 30/11/2020
 */
public class Utils {


    public static Node findSpecificNodeFromGivenRoot(Node startingPoint, String nodeName) {
        Node currentChild = startingPoint;
        while (currentChild != null && !nodeName.equals(currentChild.getNodeName())) {
            currentChild = currentChild.getNextSibling();
        }
        return currentChild;
    }

    public static Node findOrCreateGivenNode(Document document, Node root, String nodeToFind) {
        final Node existingProfiles = findSpecificNodeFromGivenRoot(root.getFirstChild(), nodeToFind);
        if (existingProfiles != null) {
            return existingProfiles;
        } else {
            final Element profiles = document.createElement(nodeToFind);
            root.appendChild(profiles);
            return profiles;
        }
    }

    public static Element createDependency(Document document,
                                     String groupIdValue,
                                     String artifactIdValue,
                                     String versionValue) {
        final Element dependency = document.createElement(DEPENDENCY);

        final Element groupId = document.createElement(GROUP_ID);
        groupId.setTextContent(groupIdValue);
        dependency.appendChild(groupId);

        final Element artifactId = document.createElement(ARTIFACT_ID);
        artifactId.setTextContent(artifactIdValue);
        dependency.appendChild(artifactId);

        final Element version = document.createElement(VERSION);
        version.setTextContent(versionValue);
        dependency.appendChild(version);
        return dependency;
    }

    public static final String PROJECT = "project";

    public static final String PLUGIN = "plugin";

    public static final String GROUP_ID = "groupId";

    public static final String ARTIFACT_ID = "artifactId";

    public static final String VERSION = "version";

    public static final String DEPENDENCIES = "dependencies";

    public static final String DEPENDENCY = "dependency";


}
