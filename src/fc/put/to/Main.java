package fc.put.to;

import fc.put.to.algorithms.greedy.GreedyCycle;
import fc.put.to.algorithms.local.LocalSearch;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Vertex> vertexList = Parser.readFile();
        GreedyCycle gc = new GreedyCycle(vertexList);
        gc.run();

        long start = System.nanoTime();
        vertexList.stream().forEach(v -> {
            LocalSearch ls = new LocalSearch(vertexList, GreedyCycle.incidenceToCycleList(gc.getAllIncidenceMap().get(v), v), v);
            ls.run();
        });


        long end = System.nanoTime();
        System.out.println((end - start) / 1e9);
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
