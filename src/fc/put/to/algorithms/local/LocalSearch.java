package fc.put.to.algorithms.local;

import fc.put.to.Vertex;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by inf109711 on 11.10.2016.
 */
public class LocalSearch {
    private List<Vertex> vertices;
    private List<Vertex> beginigSolution;

    public void run(){
        List<Vertex> edges = beginigSolution.stream()
                                            .filter(isNotNull())
                                            .collect(Collectors.toList());

    }

    private void checkOtherEdges(Vertex from) {
        Vertex to = beginigSolution.get(from.getId());
        Integer deltaCost = -from.getCostToVertex(to).getValue();

        while (true){
            Vertex from1 = beginigSolution.get(to.getId());
            Vertex to1 = beginigSolution.get(from1.getId());
            deltaCost -= from1.getCostToVertex(to1).getValue();
            deltaCost += from.getCostToVertex(to1).getValue();
        }
    }

    private Predicate<Vertex> isNotNull(){
        return v -> v != null;
    }
}
