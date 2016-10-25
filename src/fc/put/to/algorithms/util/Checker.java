package fc.put.to.algorithms.util;

import fc.put.to.Vertex;

import java.util.List;

/**
 * Created by inf109782 on 25.10.2016.
 */
public class Checker {

    public int checkVertices(List<Vertex> list1, List<Vertex> list2) {
        int result = 0;
        for (Vertex vertex : list1) {
            if (list2.contains(vertex)) {
                result++;
            }
        }

        return result;
    }

    public int checkEdges(List<Vertex> list1, List<Vertex> list2) {
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
