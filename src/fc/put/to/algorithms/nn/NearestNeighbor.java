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

    protected final List<Vertex> vertices;

    //Pola wynikowe
    private int bestCost = Integer.MAX_VALUE;
    private int sumCost;
    private int maxCost;
    private int bestIncidenceIndex;
    private List<List<Vertex>> bestIncidenceList = new ArrayList<>(100);

    public NearestNeighbor(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Vertex> getBestSolution() {
        return bestIncidenceList.get(bestIncidenceIndex);
    }

    public void run() {
        sortCostListForAllVertices();
        for (Vertex vertex : vertices) {
            List<Vertex> solution = findSolution(vertex);
            int result = getTotalCost(solution);
            setResults(result);
            bestIncidenceList.add(solution);
        }

        printResult();
    }

    private void sortCostListForAllVertices() {
        for (Vertex vertex : vertices) {
            sortCostList(vertex);
        }
    }

    private void sortCostList(Vertex vertex) {
        vertex.getCostList()
                .stream()
                .sorted((o1, o2) -> o1.getValue() - o2.getValue() == 0 ? 1 : o1.getValue() - o2.getValue())
                .collect(Collectors.toList());
    }

    public List<Vertex> findSolution(Vertex vertex) {
        Vertex firstVertex = vertex;
        List<Vertex> solution = new ArrayList<>(Constants.LENGHT_OF_SOLUTION);
        solution.add(vertex);
        for (int i = 1; i < Constants.LENGHT_OF_SOLUTION; i++) {
            Vertex nextVertex = nearestNeighbor(solution, vertex);
            solution.add(nextVertex);
            vertex = nextVertex;
        }

        for (Vertex.Cost cost : vertex.getCostList()) {
            if (firstVertex.getId().equals(cost.getTarget())) {
                solution.add(firstVertex);
                break;
            }
        }

        return solution;
    }

    protected Vertex nearestNeighbor(List<Vertex> currentSolution, Vertex vertex) {
        for (Vertex.Cost cost : vertex.getCostList()) {
            if (isOnList(currentSolution, cost) == false) {
                return vertices.get(cost.getTarget());
            }
        }

        throw new IllegalStateException("Can't find nearest vertex");
    }

    protected boolean isOnList(List<Vertex> vertices, Vertex.Cost cost) {
        for (Vertex vertex : vertices) {
            if (vertex.getId().equals(cost.getTarget())) {
                return true;
            }
        }

        return false;
    }

    private int getTotalCost(List<Vertex> solution) {
        int totalCost = 0;
        for (int i = 0; i < solution.size() - 1; i++) {
            totalCost += solution.get(i).getCostToVertex(solution.get(i + 1)).getValue();
        }

        return totalCost;
    }

    private void setResults(int result) {
        boolean isBest = result < bestCost;
        bestCost = isBest ? result : bestCost;
        maxCost = result > maxCost ? result : maxCost;
        sumCost += result;
        bestIncidenceIndex = isBest ? bestIncidenceList.size() : bestIncidenceIndex;
    }

    private void printResult() {
        System.out.println("Min: " + this.bestCost);
        System.out.println("Avg: " + this.sumCost / bestIncidenceList.size());
        System.out.println("Max: " + this.maxCost);
        printBestCycle();
        //printCheckSum();
        System.out.println();
    }

    private void printBestCycle() {
        List<Vertex> bestSolution = bestIncidenceList.get(bestIncidenceIndex);
        for (Vertex vertex : bestSolution) {
            System.out.print(vertex.getId() + " ");
        }
        System.out.println();
    }

    private void printCheckSum() {
        long checkSum = this.bestIncidenceList.stream().filter(v -> v.size() == Constants.LENGHT_OF_SOLUTION + 1).count();
        System.out.println("Check sum: " + checkSum);
    }
}
