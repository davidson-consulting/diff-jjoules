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

    public static Node findNodeWithSpecificChild(Document document,
                                                 Node parent,
                                                 String nodeNameChild,
                                                 String nodeChildValue,
                                                 String groupId,
                                                 String artifactId,
                                                 String version) {
        Node current = parent.getFirstChild();
        while (current != null) {
            final Node artifactIdNode = Utils.findSpecificNodeFromGivenRoot(current, nodeNameChild);
            if (artifactIdNode != null && nodeChildValue.equals(artifactIdNode.getNodeValue())) {
                return current;
            }
            current = current.getNextSibling();
        }
        final Element plugin = createPlugin(document, groupId, artifactId, version);
        parent.appendChild(plugin);
        return plugin;
    }

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

    public static Element createElement(Document document,
                                           String elementNodeName,
                                           String groupIdValue,
                                           String artifactIdValue,
                                           String versionValue) {
        final Element element = document.createElement(elementNodeName);

        final Element groupId = document.createElement(GROUP_ID);
        groupId.setTextContent(groupIdValue);
        element.appendChild(groupId);

        final Element artifactId = document.createElement(ARTIFACT_ID);
        artifactId.setTextContent(artifactIdValue);
        element.appendChild(artifactId);

        if (!versionValue.isEmpty()) {
            final Element version = document.createElement(VERSION);
            version.setTextContent(versionValue);
            element.appendChild(version);
        }
        return element;
    }

    public static Element createPlugin(Document document,
                                           String groupIdValue,
                                           String artifactIdValue,
                                           String versionValue) {
        return Utils.createElement(document, PLUGIN, groupIdValue, artifactIdValue, versionValue);
    }

    public static Element createDependency(Document document,
                                     String groupIdValue,
                                     String artifactIdValue,
                                     String versionValue) {
        return Utils.createElement(document, DEPENDENCY, groupIdValue, artifactIdValue, versionValue);
    }

    public static final String PROJECT = "project";

    public static final String PLUGIN = "plugin";

    public static final String GROUP_ID = "groupId";

    public static final String ARTIFACT_ID = "artifactId";

    public static final String VERSION = "version";

    public static final String DEPENDENCIES = "dependencies";

    public static final String DEPENDENCY = "dependency";


}
