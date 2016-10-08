package fc.put.to;

import fc.put.to.algorithms.greedy.GraspGreedyCycle;
import fc.put.to.algorithms.nn.NearestNeighbor;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Vertex> vertexList = Parser.readFile();
/*        GreedyCycle gc = new GreedyCycle(vertexList);
        gc.run();
*/
        GraspGreedyCycle gc = new GraspGreedyCycle(vertexList);
        gc.run();

        NearestNeighbor nearestNeighbor = new NearestNeighbor(vertexList);
        nearestNeighbor.run();

        //Drawer drawer = new Drawer();
        //drawer.draw(gc.getBestIncidenceList());
    }
}
