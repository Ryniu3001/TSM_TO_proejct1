package fc.put.to.algorithms.nn;

import fc.put.to.Constants;
import fc.put.to.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by maciej on 08.10.16.
 */
public class NearestNeighbor {

    private final List<Vertex> vertices;

    //Pola wynikowe
    private int bestCost = Integer.MAX_VALUE;
    private int sumCost;
    private int maxCost;
    private int bestIncidenceIndex;
    private List<List<Vertex>> bestIncidenceList = new ArrayList<>(100);

    public NearestNeighbor(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public void run() {
        System.out.println("NN started");
        for (Vertex vertex : vertices) {
            sortCostList(vertex);
        }
        System.out.println("NN sorted");

        for (Vertex vertex : vertices) {
            List<Vertex> solution = findSolution(vertex);
            int result = getResult(solution);
            setResults(result);
            bestIncidenceList.add(solution);
        }

        System.out.println("NN finished");
        printResult();
    }

    private void setResults(int result) {
        boolean isBest = result < bestCost;
        bestCost = isBest ? result : bestCost;
        maxCost = result > maxCost ? result : maxCost;
        sumCost += result;
        bestIncidenceIndex = isBest ? bestIncidenceList.size() : bestIncidenceIndex;
    }

    private int getResult(List<Vertex> solution) {
        int result = 0;
        for (int i = 0; i < solution.size() - 2; i++) {
            result += solution.get(i).getCostToVertex(solution.get(i + 1)).getValue();
        }

        return result;
    }

    private void sortCostList(Vertex vertex) {
        vertex.getCostList()
                .stream()
                .sorted((o1, o2) -> o1.getValue() - o2.getValue() == 0 ? 1 : o1.getValue() - o2.getValue())
                .collect(Collectors.toList());
    }

    private List<Vertex> findSolution(Vertex vertex) {
        List<Vertex> solution = new ArrayList<>(Constants.LENGHT_OF_SOLUTION);
        solution.add(vertex);
        for (int i = 1; i < Constants.LENGHT_OF_SOLUTION; i++) {
            Vertex nextVertex = nearestNeighbor(solution, vertex);
            solution.add(nextVertex);
            vertex = nextVertex;
        }

        return solution;
    }

    private Vertex nearestNeighbor(List<Vertex> currentSolution, Vertex vertex) {
        for (Vertex.Cost cost : vertex.getCostList()) {
            if (isOnList(currentSolution, cost) == false) {
                return vertices.get(cost.getTarget());
            }
        }

        throw new IllegalStateException("Can't find nearest vertex");
    }

    private boolean isOnList(List<Vertex> vertices, Vertex.Cost cost) {
        for (Vertex vertex : vertices) {
            if (vertex.getId().equals(cost.getTarget())) {
                return true;
            }
        }

        return false;
    }

    private void printResult() {
        System.out.println("Min: " + this.bestCost);
        System.out.println("Avg: " + this.sumCost / bestIncidenceList.size());
        System.out.println("Max: " + this.maxCost);
        printBestCycle();
        long checkSum = this.bestIncidenceList.stream().filter(v -> v.size() == Constants.LENGHT_OF_SOLUTION).count();
        System.out.println("Check sum: " + checkSum);
    }

    private void printBestCycle() {
        List<Vertex> bestSolution = bestIncidenceList.get(bestIncidenceIndex);
        System.out.print("Best solution: ");
        for (Vertex vertex : bestSolution) {
            System.out.print(vertex.getId() + " ");
        }
        System.out.println();
    }
}
