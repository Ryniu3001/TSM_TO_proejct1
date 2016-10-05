package fc.put.to;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by marcin on 04.10.16.
 */
public class Parser {

    /**
     * Read the whole file into memory and parse it.
     * Returns a list of vertices, each witch sorted list of costs
     */
    public static List<Vertex> readFile() {
        File fXmlFile = new File("/home/marcin/Pobrane/kroA100.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("graph");
        Node graph = nList.item(0);
        NodeList vertexList = graph.getChildNodes();
        List<Vertex> vertexes = new ArrayList<>();
        int id = 0;
        for (int j = 0; j < vertexList.getLength(); j++) {
            Node vertex = vertexList.item(j);
            if (vertex.getNodeType() == Node.ELEMENT_NODE) {
                vertexes.add(parseVertex(vertex, id++));
            }
        }
        vertexes.forEach(v -> Collections.sort(v.getCostList()));
        return vertexes;
    }

    private static Vertex parseVertex(Node v, int id) {
        NodeList edgeList = v.getChildNodes();
        Vertex vertex = new Vertex(id);
        for (int i = 0; i < edgeList.getLength(); i++) {
            Node edge = edgeList.item(i);
            if (edge.getNodeType() == Node.ELEMENT_NODE) {
                Integer toVertex = Integer.parseInt(edge.getTextContent());
                Integer distance = parseCost(edge.getAttributes().getNamedItem("cost").getTextContent());
                vertex.addDistance(toVertex, distance);
            }
        }
        return vertex;
    }

    private static Integer parseCost(String c){
        Double cost = Double.parseDouble(c);
        return new Long(Math.round(cost)).intValue();
    }

}
