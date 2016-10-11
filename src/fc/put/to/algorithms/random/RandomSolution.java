package fc.put.to.algorithms.random;

import fc.put.to.Constants;
import fc.put.to.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by inf109782 on 11.10.2016.
 */
public class RandomSolution {

    private final List<Vertex> vertices;
    private final Random random;
    //Pola wynikowe
    private int bestCost = Integer.MAX_VALUE;
    private int sumCost;
    private int maxCost;
    private int bestIncidenceIndex;
    private List<List<Vertex>> bestIncidenceList = new ArrayList<>(100);

    public RandomSolution(List<Vertex> vertices) {
        this.vertices = vertices;
        random = new Random(System.currentTimeMillis());
    }

    public List<Vertex> getBestSolution() {
        return bestIncidenceList.get(bestIncidenceIndex);
    }

    public void run() {
        System.out.println("Random started");
        for (Vertex vertex : vertices) {
            List<Vertex> solution = findSolution(vertex);
            int result = getTotalCost(solution);
            setResults(result);
            bestIncidenceList.add(solution);
        }

        System.out.println("Random finished");
        printResult();
    }

    private List<Vertex> findSolution(Vertex vertex) {
        Vertex firstVertex = vertex;
        List<Vertex> solution = new ArrayList<>(Constants.LENGHT_OF_SOLUTION);
        solution.add(vertex);
        for (int i = 1; i < Constants.LENGHT_OF_SOLUTION; i++) {
            List <Vertex.Cost> costList = getCostList(solution, vertex);
            int index = random.nextInt(costList.size());
            Vertex nextVertex = vertices.get(costList.get(index).getTarget());
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

    private List<Vertex.Cost> getCostList(List<Vertex> currentSolution, Vertex vertex) {
        return vertex.getCostList().stream()
                .filter(c -> isOnList(currentSolution, c) == false)
                .collect(Collectors.toList());
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
        printCheckSum();
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
