package fc.put.to;

import fc.put.to.algorithms.greedy.Drawer;
import fc.put.to.algorithms.greedy.GraspGreedyCycle;
import fc.put.to.algorithms.greedy.GreedyCycle;
import fc.put.to.algorithms.local.LSResult;
import fc.put.to.algorithms.local.LocalSearch;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Vertex> vertexList = Parser.readFile();
        GreedyCycle gc = new GreedyCycle(vertexList);
        gc.run();

        long start = System.nanoTime();
        LSResult result = vertexList.stream()
                .map(v -> {
                    LocalSearch ls = new LocalSearch(vertexList, GreedyCycle.incidenceToCycleList(gc.getAllIncidenceMap().get(v), v));
                    return ls.run();
                })
                .min((o1, o2) -> o1.getCost() - o2.getCost())
                .get();
        long end = System.nanoTime();
        System.out.println((end - start) / 1e9 + " s");
        System.out.println(result);
        System.out.println();

        GraspGreedyCycle ggc = new GraspGreedyCycle(vertexList);
        ggc.run();
        start = System.nanoTime();
        LSResult gResult = vertexList.stream()
                .map(v -> {
                    LocalSearch ls = new LocalSearch(vertexList, GreedyCycle.incidenceToCycleList(ggc.getAllIncidenceMap().get(v), v));
                    return ls.run();
                })
                .min((o1, o2) -> o1.getCost() - o2.getCost())
                .get();
        end = System.nanoTime();
        System.out.println((end - start) / 1e9 + " s");
        System.out.println(result);
        System.out.println();

/*        NearestNeighbor nearestNeighbor = new NearestNeighbor(vertexList);
        nearestNeighbor.run();

        GraspNearestNeighbor graspNearestNeighbor = new GraspNearestNeighbor(vertexList);
        graspNearestNeighbor.run();*/


        //new RandomSolution(vertexList).run();

        Drawer drawer = new Drawer();
        drawer.drawNN(gResult.getCycle());
    }
}
