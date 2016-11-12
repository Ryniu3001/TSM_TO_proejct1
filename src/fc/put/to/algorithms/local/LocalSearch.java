package fc.put.to.algorithms.local;

import fc.put.to.Vertex;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Created by inf109711 on 11.10.2016.
 */
public class LocalSearch {
    protected final List<Vertex> vertices;
    protected List<Vertex> cycleList;


    public LocalSearch(List<Vertex> vertices, List<Vertex> beginigSolution) {
        this.cycleList = beginigSolution;
        this.vertices = vertices;
    }

    protected LocalSearch(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public LSResult run() {
       // System.out.println(this.beginigVertex.getId());
        long start = System.nanoTime();
        VertexReplacement globalMinVr;
        EdgesReplacement globalMinEr;
        do {
            globalMinVr = IntStream.range(0, cycleList.size() - 1)
                    .mapToObj(i -> this.getBestVertexReplacement(i, cycleList))
                    .min((o1, o2) -> o1.deltaCost - o2.deltaCost)
                    .get();
            //System.out.println("Best Vertex Replacement: " + globalMinVr);

            globalMinEr = IntStream.range(0, cycleList.size() - 3)
                    .mapToObj(i -> this.getBestEdgeReplacement(i, cycleList))
                    .min((o1, o2) -> o1.deltaCost - o2.deltaCost)
                    .get();
            //System.out.println("Best Edge replacement: " + globalMinEr.deltaCost);

            if (globalMinEr.deltaCost <= globalMinVr.deltaCost) {
                Collections.reverse(cycleList.subList(globalMinEr.edge1To, globalMinEr.edge2From + 1));
            } else {
                swapVertices(globalMinVr.vertexIndex, globalMinVr.forVertex);
            }
        }while(globalMinEr.deltaCost < 0 || globalMinVr.deltaCost < 0);
        long end = System.nanoTime();
        return new LSResult(calculateCost(this.cycleList), this.cycleList, (end - start) / 1e6);
    }

    protected void swapVertices(Integer fromIndex, Vertex toVertex){
        if (fromIndex == 0 || fromIndex == this.cycleList.size() - 1){
            cycleList.set(0, toVertex);
            cycleList.set(this.cycleList.size() - 1, toVertex);
        }else{
            cycleList.set(fromIndex, toVertex);
        }
    }

    /**
     * Zwraca najelszpe zastąpienie wierzchołka o indeksie podanym w parametrze
     *
     * @param index     indeks zastępywanego wierzchołka na liście cycleList
     * @param cycleList lista kolejnych wierzchołków w cyklu
     * @return
     */
    protected VertexReplacement getBestVertexReplacement(int index, List<Vertex> cycleList) {
        Vertex from = cycleList.get(index);
        Integer previous = index - 1;
        if (previous < 0)
            previous = cycleList.size() - 2;
        Vertex nextVertex = cycleList.get(index + 1);
        Integer deltaCost = -from.getCostToVertex(nextVertex).getValue();
        deltaCost -= cycleList.get(previous).getCostToVertex(from).getValue();

        Vertex prev = cycleList.get(previous);
        Integer finalPrevious = previous;
        VertexReplacement minVr = IntStream.range(0, this.vertices.size())
                .filter(i -> !cycleList.contains(vertices.get(i)))  //wybierz wierzchołki spoza cyklu
                .mapToObj(i -> getCostOfAddEdges(finalPrevious , index + 1, vertices.get(i),cycleList))
                .min((o1, o2) -> o1.deltaCost - o2.deltaCost)
                .get();
        minVr.deltaCost += deltaCost;
        return minVr;
    }

    protected EdgesReplacement getBestEdgeReplacement(int index, List<Vertex> cycleList){
        EdgesReplacement minEr = IntStream.range(index + 2, cycleList.size() - 1)
                .mapToObj(i -> getCostOfEdgesReplacement(index, index + 1, i, i + 1, cycleList))
                .min((o1, o2) -> o1.deltaCost - o2.deltaCost)
                .get();

        return minEr;
    }

    /**
     *
     * @param prev wierzcholek incydentny z tym ktory chcemy usunac z cyklu
     * @param next drugi wierzcholek incydentny z tym ktory chcemy usunac z cyklu
     * @param to wierzcholek ktory chcemy dolaczyc do cyklu
     * @param cycleList
     * @return
     */
    protected VertexReplacement getCostOfAddEdges(Integer prev, Integer next, Vertex to, List<Vertex> cycleList) {
        VertexReplacement vr = new VertexReplacement();
        vr.deltaCost = cycleList.get(prev).getCostToVertex(to).getValue()
                        + cycleList.get(next).getCostToVertex(to).getValue();
        vr.vertexIndex = next - 1;
        vr.forVertex = to;
        return vr;
    }

    protected EdgesReplacement getCostOfEdgesReplacement(Integer e1s, Integer e1e, Integer e2s, Integer e2e, List<Vertex> cycleList){
        int delta = 0;
            delta -= cycleList.get(e1s).getCostToVertex(cycleList.get(e1e)).getValue();
            delta -= cycleList.get(e2s).getCostToVertex(cycleList.get(e2e)).getValue();
            delta += cycleList.get(e1s).getCostToVertex(cycleList.get(e2s)).getValue();
            delta += cycleList.get(e1e).getCostToVertex(cycleList.get(e2e)).getValue();
        return new EdgesReplacement(e1s, e1e, e2s, e2e, delta);
    }

    private Predicate<Vertex> isNotNull() {
        return v -> v != null;
    }

    protected Integer calculateCost(List<Vertex> list){
        Integer cost = IntStream.range(1, list.size())
                .sequential()
                .mapToObj(val -> list.get(val-1).getCostToVertex(list.get(val)).getValue())
                .reduce(0, (cost1, cost2) -> cost1 + cost2);
        return cost;
    }

    protected class VertexReplacement {
        protected Integer vertexIndex;
        protected Vertex forVertex;
        protected Integer deltaCost;

        @Override
        public String toString() {
            return vertexIndex.toString() + " " + deltaCost.toString();
        }
    }

    protected class EdgesReplacement {
        public EdgesReplacement(Integer edge1From, Integer edge1To, Integer edge2From, Integer edge2To, Integer deltaCost) {
            this.edge1From = edge1From;
            this.edge1To = edge1To;
            this.edge2From = edge2From;
            this.edge2To = edge2To;
            this.deltaCost = deltaCost;
        }

        public EdgesReplacement(Integer edge1From, Integer edge1To, Integer edge2From, Integer edge2To) {
            this.edge1From = edge1From;
            this.edge1To = edge1To;
            this.edge2From = edge2From;
            this.edge2To = edge2To;
        }

        protected Integer edge1From;
        protected Integer edge1To;
        protected Integer edge2From;
        protected Integer edge2To;
        protected Integer deltaCost;
    }
}
