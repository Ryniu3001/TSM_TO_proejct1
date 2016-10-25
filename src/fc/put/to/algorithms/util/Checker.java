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

    private List<LSResult> lsResults;

    public Checker(List<LSResult> lsResults) {
        this.lsResults = lsResults;
    }

    public void run() {
        List<Double> result = IntStream.range(0, lsResults.size())
                .sequential()
                .mapToObj(i -> sth(this.lsResults.get(i), i))
                .collect(Collectors.toList());

        LSResult best = lsResults.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get();
        
    }

    private Double sth(LSResult result, int i) {
        System.out.println("sth " + i);
        return IntStream.range(i + 1, lsResults.size())
                .sequential().map(v -> checkVertices(result.getCycle(), lsResults.get(v).getCycle()))
                .average()
                .getAsDouble();

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
            for (int j = 0; j < matrix1[i].length; j++) {
                if (matrix1[i][j] == matrix2[i][j]) {
                    result++;
                }
            }
        }

        return result;
    }

    private boolean[][] createMatrix(List<Vertex> list) {
        final int size = list.size();
        boolean[][] matrix = new boolean[size][size];
        for (int i = 0; i < size - 1; i++) {
            Vertex vertex = list.get(i);
            Vertex next = list.get(i + 1);
            matrix[vertex.getId()][next.getId()] = true;
        }

        return matrix;
    }
}
