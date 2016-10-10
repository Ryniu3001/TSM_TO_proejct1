package fc.put.to.algorithms.greedy;

import fc.put.to.Constants;
import fc.put.to.Vertex;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by marcin on 07.10.16.
 */
public class Drawer {
    private List<VertexWithCoord> vertices;

    private Graph graph;
    private Integer edId = 0;

    public Drawer(){
        vertices = new ArrayList<>();
    }

    public void draw(List<List<Vertex>> incidenceList){
        readFileWithCoordinates();
        this.graph = new MultiGraph("Hello World!");
        this.vertices.forEach(this::drawNewVertex);
        Vertex from = incidenceList.stream().filter(vertices1 -> vertices1.size() == 1).findFirst().get().get(0);
        Vertex previous = from;

        for (int i=0; i<50; i++) {
            Vertex to ;
            if (incidenceList.get(from.getId()).get(0).getId() != previous.getId())
                to = incidenceList.get(from.getId()).get(0);
            else
                to = incidenceList.get(from.getId()).get(1);
            drawEdges(from,to);
            previous = from;
            from = to;
        }
        graph.addAttribute("ui.stylesheet", "node { size: 7px; z-index: 0;}");
        graph.addAttribute("ui.stylesheet", "node { text-style: bold; text-size: 12;}");

        Viewer viewer = graph.display();
        viewer.disableAutoLayout();
    }

    private void drawNewVertex(VertexWithCoord v){
        Node n = this.graph.addNode(v.id.toString());
        n.setAttribute("x", v.x);
        n.setAttribute("y", v.y);
        n.addAttribute("ui.label", v.id.toString());
    }

    private void drawEdges(Vertex from, Vertex to){
        Edge e = graph.addEdge(edId.toString(), from.getId(), to.getId());
        edId++;
    }

    private void readFileWithCoordinates(){
        File file = new File(Constants.FILE_PATH_TSP);
        try (Stream<String> stream = Files.lines(file.toPath())) {

            stream.forEach(s -> this.vertices.add(new VertexWithCoord(s)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class VertexWithCoord{

        public VertexWithCoord(String lineOfFile){
            String[] line = lineOfFile.split(" ");
            this.id = Integer.parseInt(line[0])-1;
            this.x = Integer.parseInt(line[1]);
            this.y = Integer.parseInt(line[2]);
        }

        private Integer id;
        private int x;
        private int y;
    }
}
