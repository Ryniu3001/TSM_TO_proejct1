package fc.put.to;

import fc.put.to.algorithms.greedy.GraspGreedyCycle;
import fc.put.to.algorithms.greedy.GreedyCycle;
import fc.put.to.algorithms.local.LSResult;
import fc.put.to.algorithms.local.LocalSearch;
import fc.put.to.algorithms.nn.GraspNearestNeighbor;
import fc.put.to.algorithms.nn.NearestNeighbor;
import fc.put.to.algorithms.random.RandomSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<Vertex> vertexList = Parser.readFile();

        //nn(vertexList);
        //nnGrasp(vertexList);

        //greedyCycle(vertexList);
        //greedyCycleGrasp(vertexList);

        random(vertexList);

        nnLocal(vertexList);
        nnGraspLocal(vertexList);

        randomLocal(vertexList);
    }

    private static void randomLocal(List<Vertex> vertexList) {
        RandomSolution randomSolution = new RandomSolution(vertexList);
        randomSolution.run();

        LSResult lsResult = new LocalSearch(vertexList, randomSolution.getBestSolution()).run();
        List<LSResult> gResults = new ArrayList<>(1);
        gResults.add(lsResult);
        printLCResult(gResults);
    }

    private static void nnGraspLocal(List<Vertex> vertexList) {
        GraspNearestNeighbor graspNearestNeighbor = new GraspNearestNeighbor(vertexList);
        List<LSResult> gResult = vertexList.stream()
                .map(v -> new LocalSearch(vertexList, graspNearestNeighbor.findSolution(v)).run())
                .collect(Collectors.toList());
        printLCResult(gResult);
    }

    private static void nnLocal(List<Vertex> vertexList) {
        NearestNeighbor nearestNeighbor = new NearestNeighbor(vertexList);
        List<LSResult> gResult = vertexList.stream()
                .map(v -> new LocalSearch(vertexList, nearestNeighbor.findSolution(v)).run())
                .collect(Collectors.toList());
        printLCResult(gResult);
    }

    private static void random(List<Vertex> vertexList) {
        RandomSolution randomSolution = new RandomSolution(vertexList);
        randomSolution.run();
    }

    private static void nnGrasp(List<Vertex> vertexList) {
        GraspNearestNeighbor graspNearestNeighbor = new GraspNearestNeighbor(vertexList);
        graspNearestNeighbor.run();
    }

    private static void nn(List<Vertex> vertexList) {
        NearestNeighbor nearestNeighbor = new NearestNeighbor(vertexList);
        nearestNeighbor.run();
    }

    private static void greedyCycleGrasp(List<Vertex> vertexList) {
        System.out.println("GRASP Greedy Cycle:");
        GraspGreedyCycle ggc = new GraspGreedyCycle(vertexList);
        ggc.run();

        List<LSResult> gResult = vertexList.stream()
                .map(v -> new LocalSearch(vertexList, GreedyCycle.incidenceToCycleList(ggc.getAllIncidenceMap().get(v), v)).run())
                .collect(Collectors.toList());
        printLCResult(gResult);
    }

    private static void greedyCycle(List<Vertex> vertexList) {
        System.out.println("Greedy Cycle:");
        GreedyCycle gc = new GreedyCycle(vertexList);
        gc.run();

        List<LSResult> result = vertexList.stream()
                .map(v -> new LocalSearch(vertexList, GreedyCycle.incidenceToCycleList(gc.getAllIncidenceMap().get(v), v)).run())
                .collect(Collectors.toList());

        printLCResult(result);
    }

    private static void printLCResult(List<LSResult> list) {
        System.out.println("Local Search:");
        System.out.println("Min: " + list.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get());
        System.out.println("Avg: " + list.stream().mapToInt(LSResult::getCost).average().getAsDouble());
        System.out.println("Max: " + list.stream().min((o1, o2) -> o1.getCost() + o2.getCost()).get());
        System.out.print("Min time [ms]: " + list.stream().mapToDouble(LSResult::getTime).min().getAsDouble());
        System.out.print(", Avg time [ms]: " + list.stream().mapToDouble(LSResult::getTime).average().getAsDouble());
        System.out.println(", Max time [ms]: " + list.stream().mapToDouble(LSResult::getTime).max().getAsDouble());
    }
}
