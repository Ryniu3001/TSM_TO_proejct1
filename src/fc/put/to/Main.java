package fc.put.to;

import fc.put.to.algorithms.greedy.GraspGreedyCycle;
import fc.put.to.algorithms.greedy.GreedyCycle;
import fc.put.to.algorithms.local.LSResult;
import fc.put.to.algorithms.local.LocalSearch;

import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<Vertex> vertexList = Parser.readFile();

        System.out.println("Greedy Cycle:");
        GreedyCycle gc = new GreedyCycle(vertexList);
        gc.run();
        long start = System.nanoTime();
        List<LSResult> result = vertexList.stream()
                .map(v -> new LocalSearch(vertexList, GreedyCycle.incidenceToCycleList(gc.getAllIncidenceMap().get(v), v)).run())
                .collect(Collectors.toList());
        long end = System.nanoTime();
        System.out.println("Time [ms]: " + (end - start) / 1e6);
        printLCResult(result);
        System.out.println();

        //Local Search for GRASP GC
        System.out.println("GRASP Greedy Cycle:");
        GraspGreedyCycle ggc = new GraspGreedyCycle(vertexList);
        ggc.run();
        start = System.nanoTime();
        List<LSResult> gResult = vertexList.stream()
                .map(v -> new LocalSearch(vertexList, GreedyCycle.incidenceToCycleList(ggc.getAllIncidenceMap().get(v), v)).run())
                .collect(Collectors.toList());
        end = System.nanoTime();
        System.out.println("Time [ms]: " + (end - start) / 1e6);
        printLCResult(gResult);
        System.out.println();

/*        NearestNeighbor nearestNeighbor = new NearestNeighbor(vertexList);
        nearestNeighbor.run();

        GraspNearestNeighbor graspNearestNeighbor = new GraspNearestNeighbor(vertexList);
        graspNearestNeighbor.run();*/


        //new RandomSolution(vertexList).run();
/*
        Drawer drawer = new Drawer();
        drawer.drawNN(gResult.getCycle());*/
    }

    private static void printLCResult(List<LSResult> list){
        System.out.println("Local Search:");
        System.out.println("Min: " + list.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get());
        System.out.println("Avg: " + list.stream().mapToInt(LSResult::getCost).average().getAsDouble());
        System.out.println("Max: " + list.stream().min((o1, o2) -> o1.getCost() + o2.getCost()).get());
    }
}
