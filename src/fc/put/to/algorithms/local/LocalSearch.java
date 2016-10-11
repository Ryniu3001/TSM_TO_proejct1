package fc.put.to.algorithms.local;

import fc.put.to.Constants;
import fc.put.to.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Created by inf109711 on 11.10.2016.
 */
public class LocalSearch {
    private final List<Vertex> vertices;
    private final List<Vertex> beginigSolution;
    private final Vertex beginigVertex;

    public LocalSearch(List<Vertex> vertices, List<Vertex> beginigSolution, Vertex startingVertex){
        this.beginigSolution = beginigSolution;
        this.vertices = vertices;
        this.beginigVertex = startingVertex;
    }

    public void run(){
        List<Vertex> cycleList = getCycleList();
        VertexReplacement globalMinVr = IntStream.range(0, cycleList.size() - 1)
                 .mapToObj(i -> this.getBestVertexReplacement(i, cycleList))
                 .min((o1, o2) -> o1.deltaCost - o2.deltaCost)
                 .get();
        System.out.println("Best Vertex Replacement: " + globalMinVr);


    }

    /**
     * Zwraca najelszpe zastąpienie wierzchołka o indeksie podanym w parametrze
     * @param index indeks zastępywanego wierzchołka na liście cycleList
     * @param cycleList lista kolejnych wierzchołków w cyklu
     * @return
     */
    private VertexReplacement getBestVertexReplacement(int index, List<Vertex> cycleList){
        Vertex from = cycleList.get(index);
        Integer previous = index - 1;
        if (previous < 0)
            previous = cycleList.size() - 2;
        Vertex nextVertex = cycleList.get(index + 1);
        Integer deltaCost = -from.getCostToVertex(nextVertex).getValue();
        deltaCost -= cycleList.get(previous).getCostToVertex(from).getValue();

        Vertex prev = cycleList.get(previous);
        VertexReplacement minVr =  IntStream.range(0, this.beginigSolution.size())
                                            .filter(i -> this.beginigSolution.get(i) == null)
                                            .mapToObj(i -> getCostOfAddEdges(from, prev, vertices.get(i)))
                                            .min((o1, o2) -> o1.deltaCost - o2.deltaCost)
                                            .get();
        minVr.deltaCost += deltaCost;
        return minVr;
    }

     private VertexReplacement getCostOfAddEdges(Vertex from1, Vertex from2, Vertex to){
         VertexReplacement vr = new VertexReplacement();
         vr.deltaCost = from1.getCostToVertex(to).getValue() + from2.getCostToVertex(to).getValue();
         vr.vertex = from1;
         return vr;
     }

    private List<Vertex> getCycleList(){
        List<Vertex> cycleList = new ArrayList<>();
        cycleList.add(this.beginigVertex);
        IntStream.range(0, Constants.LENGHT_OF_SOLUTION).forEach(value -> {
            cycleList.add(this.beginigSolution.get(cycleList.get(cycleList.size()-1).getId()));
        });
        return cycleList;
    }
    private Predicate<Vertex> isNotNull(){
        return v -> v != null;
    }

    private class VertexReplacement{
        private Vertex vertex;
        private Integer deltaCost;

        @Override
        public String toString(){
            return vertex.getId().toString() + " " + deltaCost.toString();
        }
    }
}
