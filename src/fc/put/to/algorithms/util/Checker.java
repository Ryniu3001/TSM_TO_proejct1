package fc.put.to.algorithms.util;

import fc.put.to.Vertex;
import fc.put.to.algorithms.local.LSResult;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by inf109782 on 25.10.2016.
 */
public class Checker {

    private final int vertexCount;
    private List<LSResult> lsResults;

    public Checker(List<LSResult> lsResults, int vertexCount) {
        this.lsResults = lsResults;
        this.vertexCount = vertexCount;
    }

    public void compareBestSolution() {
        LSResult best = lsResults.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get();

        List<Integer> vertices = lsResults.stream().map(result -> checkVertices(best.getCycle(), result.getCycle())).collect(Collectors.toList());
        List<Integer> edges = lsResults.stream().map(result -> checkEdges(best.getCycle(), result.getCycle())).collect(Collectors.toList());

        printResultsInt(vertices, edges);
    }

    private void printResultsInt(List<Integer> vertices, List<Integer> edges) {
        System.out.println("wartosc");
        lsResults.stream().forEach(x -> System.out.print(x.getCost() + ","));
        System.out.println("vertices");
        vertices.stream().forEach(x -> System.out.print(x + ","));
        System.out.println("edges");
        edges.stream().forEach(x -> System.out.print(x + ","));
    }

    public void compareAllSolutions() {
        List<Double> vertices = IntStream.range(0, lsResults.size())
                .sequential()
                .mapToObj(i -> compareVertices(this.lsResults.get(i)))
                .collect(Collectors.toList());

        List<Double> edges = IntStream.range(0, lsResults.size())
                .sequential()
                .mapToObj(i -> compareEdges(this.lsResults.get(i)))
                .collect(Collectors.toList());

        printResults(vertices, edges);
    }

    private void printResults(List<Double> vertices, List<Double> edges) {
        System.out.println("wartosc");
        lsResults.stream().forEach(x -> System.out.print(x.getCost() + ","));
        System.out.println("vertices");
        vertices.stream().forEach(x -> System.out.print(x + ","));
        System.out.println("edges");
        edges.stream().forEach(x -> System.out.print(x + ","));
    }

    private double compareVertices(LSResult result) {
        List<Integer> collect = IntStream.range(0, lsResults.size())
                .sequential().mapToObj(v -> checkVertices(result.getCycle(), lsResults.get(v).getCycle()))
                .collect(Collectors.toList());

        return getAvarage(result, collect);
    }

    private double compareEdges(LSResult result) {
        List<Integer> collect = IntStream.range(0, lsResults.size())
                .sequential().mapToObj(v -> checkEdges(result.getCycle(), lsResults.get(v).getCycle()))
                .collect(Collectors.toList());

        return getAvarage(result, collect);
    }

    private double getAvarage(LSResult result, List<Integer> collect) {
        int sum = collect.stream().mapToInt(a -> a).sum();
        sum -= result.getCycle().size();

        double avarage = (double) sum / lsResults.size();
        return avarage;
    }

    private int checkVertices(List<Vertex> list1, List<Vertex> list2) {
        int result = 0;
        for (Vertex vertex : list1) {
            if (list2.contains(vertex)) {
                result++;
            }
        }

        return result;
    }

    private int checkEdges(List<Vertex> list1, List<Vertex> list2) {
        int result = 0;
        boolean[][] matrix1 = createMatrix(list1);
        boolean[][] matrix2 = createMatrix(list2);
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[i].length; j++) {
                if (matrix1[i][j] == true && matrix1[i][j] == matrix2[i][j]) {
                    result++;
                }
            }
        }

        return result;
    }

    private boolean[][] createMatrix(List<Vertex> list) {
        final int size = list.size();
        boolean[][] matrix = new boolean[vertexCount][vertexCount];
        for (int i = 0; i < size - 1; i++) {
            Vertex vertex = list.get(i);
            Vertex next = list.get(i + 1);
            matrix[vertex.getId()][next.getId()] = true;
        }

        return matrix;
    }
}
