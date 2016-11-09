package fc.put.to.algorithms.util;

import fc.put.to.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by maciej on 09.11.16.
 */
public class Checker {

    private final List<Vertex> allVertices;

    public Checker(List<Vertex> allVertices) {
        this.allVertices = allVertices;
    }

    public List<List<Vertex>> getEqualsFragments(List<Vertex> vertices1, List<Vertex> vertices2) {
        print(vertices1, vertices2);

        boolean[][] matrix = getMatrix(vertices1, vertices2);

        List<List<Vertex>> result = new ArrayList<>();
        Set<Integer> verticesIds = new HashSet<>();
        for (int i = 0; i < matrix.length - 1; i++) {
            int counter = 0;
            List<Vertex> equalsFragment = new ArrayList<>();
            for (int j = i + 1; j < matrix.length; j++) {
                if (matrix[i][j] && verticesIds.contains(j) == false) {
                    boolean addOnBegining = shouldAddOnBegining(counter);
                    if (verticesIds.contains(i) == false) {
                        add(verticesIds, i, equalsFragment, addOnBegining);
                    }

                    add(verticesIds, j, equalsFragment, addOnBegining);
                    findAndAddNext(equalsFragment, addOnBegining, matrix, j, verticesIds);

                    counter++;
                }
            }
            if (equalsFragment.isEmpty() == false) {
                result.add(equalsFragment);
            }
        }


        return result;
    }

    private void findAndAddNext(List<Vertex> equalsFragment, boolean addOnBegining, boolean[][] matrix, int lastAdded, Set<Integer> verticesIds) {
        for (int i = 0; i < 6; i++) {// TODO matrix[lastAdded].length; i++) {
            if (matrix[lastAdded][i] && verticesIds.contains(i) == false) {
                add(verticesIds, i, equalsFragment, addOnBegining);
                findAndAddNext(equalsFragment, addOnBegining, matrix, i, verticesIds);
                return;
            }
        }
    }

    private boolean[][] getMatrix(List<Vertex> vertices1, List<Vertex> vertices2) {
        final int vertexCount = allVertices.size();
        boolean[][] matrix = new boolean[vertexCount][vertexCount];
        boolean[][] matrix1 = createMatrix(vertices1);
        boolean[][] matrix2 = createMatrix(vertices2);
        for (int i = 0; i < matrix1.length - 1; i++) {
            for (int j = i + 1; j < matrix2.length; j++) {
                if (matrix1[i][j] && matrix1[i][j] == matrix2[i][j]) {
                    matrix[i][j] = true;
                    matrix[j][i] = true;
                }
            }
        }
        return matrix;
    }

    private void add(Set<Integer> verticesIds, int id, List<Vertex> equalsFragment, boolean addOnBegining) {
        add(equalsFragment, allVertices.get(id), addOnBegining);
        verticesIds.add(id);
    }

    private boolean shouldAddOnBegining(int counter) {
        switch (counter) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                throw new IllegalStateException("Whata fuck");
        }
    }

    private void add(List<Vertex> list, Vertex vertex, boolean addOnBegining) {
        if (addOnBegining) {
            addFirst(list, vertex);
        } else {
            addLast(list, vertex);
        }
    }

    private void addFirst(List<Vertex> list, Vertex vertex) {
        list.add(0, vertex);
    }

    private void addLast(List<Vertex> list, Vertex vertex) {
        list.add(vertex);
    }

    private void print(List<Vertex> vertices1, List<Vertex> vertices2) {
        vertices1.forEach(x -> System.out.print(x.getId() + ", "));
        System.out.println();
        vertices2.forEach(x -> System.out.print(x.getId() + ", "));
        System.out.println();
        System.out.println();
    }

    private boolean[][] createMatrix(List<Vertex> list) {
        final int vertexCount = allVertices.size();
        final int size = list.size();
        boolean[][] matrix = new boolean[vertexCount][vertexCount];
        for (int i = 0; i < size - 1; i++) {
            Vertex vertex = list.get(i);
            Vertex next = list.get(i + 1);
            matrix[vertex.getId()][next.getId()] = true;
            matrix[next.getId()][vertex.getId()] = true; // we must create mirror view
        }

        return matrix;
    }
}
