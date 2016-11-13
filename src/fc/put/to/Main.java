package fc.put.to;

import fc.put.to.algorithms.greedy.GraspGreedyCycle;
import fc.put.to.algorithms.greedy.GreedyCycle;
import fc.put.to.algorithms.hybrid.EHResult;
import fc.put.to.algorithms.hybrid.EvolutionHybrid;
import fc.put.to.algorithms.local.IteratedLocalSearch;
import fc.put.to.algorithms.local.LSResult;
import fc.put.to.algorithms.local.LocalSearch;
import fc.put.to.algorithms.local.MultipleLocalSearch;
import fc.put.to.algorithms.nn.GraspNearestNeighbor;
import fc.put.to.algorithms.nn.NearestNeighbor;
import fc.put.to.algorithms.random.RandomSolution;
import fc.put.to.algorithms.util.Checker;
import fc.put.to.algorithms.util.SimilarityChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        List<Vertex> vertexList = Parser.readFile();
        //multipleLocalSearch(vertexList);
        //iteratedLocalSearch(vertexList);
        //nn(vertexList);
        //nnGrasp(vertexList);

        //greedyCycle(vertexList);

        //random(vertexList);

        //nnLocal(vertexList);
        //nnGraspLocal(vertexList);

        //randomLocal(vertexList);

        //checkingSimilarity(vertexList);
        hybridEvol(vertexList);
    }

    private static void hybridEvol(List<Vertex> vertexList){
        EvolutionHybrid hybridEvol = new EvolutionHybrid(vertexList);
        List<EHResult> results = IntStream.range(0, 10).sequential().mapToObj(i -> hybridEvol.run()).collect(Collectors.toList());

        EHResult best = results.stream()
                                .min((o1, o2) -> o1.getLsResult().getCost() - o2.getLsResult().getCost())
                                .get();
        int bestCost = best.getLsResult().getCost();
        best = results.stream()
                        .filter(ehr -> ehr.getLsResult().getCost().intValue() == bestCost)
                        .min((o1, o2) -> o1.getIterToCostMap().size() - o2.getIterToCostMap().size())
                        .get();
        hybridEvol.saveToFile("iterToCost", best.getIterToCostMap());
        hybridEvol.saveToFile("iterToTime", best.getIterToTimeMap());
        System.out.println("Min: " + best.getLsResult());
        System.out.println("Avg: " + results.stream()
                                            .mapToInt(v -> v.getLsResult().getCost()).average()
                                            .getAsDouble());
        System.out.println("Max: " + results.stream()
                                            .max((o1, o2) -> o1.getLsResult().getCost() - o2.getLsResult().getCost())
                                            .get()
                                            .getLsResult());
    }

    private static void hybryde(List<Vertex> vertexList) {
        Random random = new Random();
        RandomSolution randomSolution = new RandomSolution(vertexList);
        //List<Vertex> solution1 = randomSolution.findSolution(vertexList.get(random.nextInt(vertexList.size())));
        //List<Vertex> solution2 = randomSolution.findSolution(vertexList.get(random.nextInt(vertexList.size())));

        List<Vertex> solution1 = new ArrayList<Vertex>() {{
            add(vertexList.get(0));
            add(vertexList.get(1));
            add(vertexList.get(2));
            add(vertexList.get(3));
            add(vertexList.get(4));
            add(vertexList.get(5));
            add(vertexList.get(0));
        }};

        List<Vertex> solution2 = new ArrayList<Vertex>() {{
            add(vertexList.get(0));
            add(vertexList.get(1));
            add(vertexList.get(2));
            add(vertexList.get(4));
            add(vertexList.get(3));
            add(vertexList.get(5));
            add(vertexList.get(0));
        }};


        List<List<Vertex>> equalsFragments = new Checker(vertexList).getEqualsFragments(solution1, solution2);
        for (int i = 0; i < equalsFragments.size(); i++) {
            for (int j = 0; j < equalsFragments.get(i).size(); j++) {
                System.out.print(equalsFragments.get(i).get(j).getId() + ", ");
            }
            System.out.println();
        }
    }

    private static void checkingSimilarity(List<Vertex> vertexList) {
        RandomSolution randomSolution = new RandomSolution(vertexList);
        Random random = new Random();
        List<LSResult> results = IntStream.range(0, 1000)
                .sequential()
                .mapToObj(i -> new LocalSearch(vertexList, randomSolution.findSolution(vertexList.get(random.nextInt(vertexList.size())))).run())
                .collect(Collectors.toList());

        SimilarityChecker checker = new SimilarityChecker(results, vertexList.size());
        checker.compareAllSolutions();
        System.out.println("-------------------");
        checker.compareBestSolution();
    }

    private static void multipleLocalSearch(List<Vertex> vertexList) {
        MultipleLocalSearch mls = new MultipleLocalSearch();
        long start = System.currentTimeMillis();
        LSResult result = mls.run(vertexList);
        long stop = System.currentTimeMillis();
        System.out.println("Time [s]: " + (stop - start) / 1e3);
        System.out.println(result);
    }

    private static void iteratedLocalSearch(List<Vertex> vertices) {

        IteratedLocalSearch ils = new IteratedLocalSearch(vertices);
        ils.run();
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

    public static void printLCResult(List<LSResult> list) {
        System.out.println("Local Search:");
        System.out.println("Min: " + list.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get());
        System.out.println("Avg: " + list.stream().mapToInt(LSResult::getCost).average().getAsDouble());
        System.out.println("Max: " + list.stream().min((o1, o2) -> o1.getCost() + o2.getCost()).get());
        System.out.print("Min time [ms]: " + list.stream().mapToDouble(LSResult::getTime).min().getAsDouble());
        System.out.print(", Avg time [ms]: " + list.stream().mapToDouble(LSResult::getTime).average().getAsDouble());
        System.out.println(", Max time [ms]: " + list.stream().mapToDouble(LSResult::getTime).max().getAsDouble());
    }
}
