package fc.put.to;

import fc.put.to.algorithms.greedy.GreedyCycle;
import fc.put.to.algorithms.local.LocalSearch;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Vertex> vertexList = Parser.readFile();
        GreedyCycle gc = new GreedyCycle(vertexList);
        gc.run();

        LocalSearch ls = new LocalSearch(vertexList, gc.getBestIncidenceList(), gc.getStartingVertex());
        ls.run();
/*        GraspGreedyCycle gc = new GraspGreedyCycle(vertexList);
        gc.run();


        NearestNeighbor nearestNeighbor = new NearestNeighbor(vertexList);
        nearestNeighbor.run();

        GraspNearestNeighbor graspNearestNeighbor = new GraspNearestNeighbor(vertexList);
        graspNearestNeighbor.run();*/


        //new RandomSolution(vertexList).run();

//        Drawer drawer = new Drawer();
//        drawer.drawNN(nearestNeighbor.getBestSolution());
    }
}
