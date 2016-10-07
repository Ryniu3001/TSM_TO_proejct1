package fc.put.to.algorithms.greedy;

import fc.put.to.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Created by marcin on 05.10.16.
 */
public class GreedyCycle {
    /**
     * Lista wierzcholkow, dla kazdego wierzcholka zaweira posortowaną
     * liste kosztów dotarcia do pozostałych wierzchołków.
     */
    private final List<Vertex> vertices;
    private List<List<Vertex>> incidenceList;
    private Integer cost;

    //Pola wynikowe
    private Integer bestCost;
    private List<List<Vertex>> bestIncidenceList;
    private Vertex startingVertex;

    public GreedyCycle(List<Vertex> vertices){
        this.vertices = vertices;
        initialize();
        this.bestCost = null;
    }

    private void initialize(){
        incidenceList = new ArrayList<>();
        IntStream.rangeClosed(0, 99).forEach(v -> incidenceList.add(new ArrayList<>()));
        vertices.forEach(vertex -> vertex.setVisited(false));
        cost = 0;
    }
    public void run(){
        //long start = System.nanoTime();
        vertices.stream().forEach(this::findHamiltonianPath);
        //long end = System.nanoTime();
        //System.out.println("Time taken(ms): " + (end - start) / 1.0e6);
        System.out.println("DONE ");
        printResult();
    }

    private void findHamiltonianPath(Vertex initialVertex) {
        initialVertex.setVisited(true);
        IntStream.rangeClosed(0, 48).forEach(i -> nextStep(i));
        if (bestCost == null || bestCost > cost){
            startingVertex = initialVertex;
            bestCost = cost;
            bestIncidenceList = incidenceList;
        }
        initialize();
    }

    /**
     * Następna iteracja przyłączenia nowego wierzchołka do grafu
     */
    private void nextStep(int i){

        if (i==0) {                             //pierwszy przebieg (wszystkie wierzcholki o stopniu 0)
            Vertex from = vertices.stream().filter(v -> v.getVisited()).findFirst().get();
            Vertex nn =findNearestNeighbour(from);
            makeEdge(from, nn);
            makeEdge(nn, from);
        }
        else{
            Optional<BestConnection> bestConnection = vertices.stream()
                    .filter(isVvisited())
                    .map(this::getBestConnection)
                    .min((o1, o2) -> o1.cost - o2.cost);
            addNewVertexToCycle(bestConnection.get());
        }
    }

    /**
     * Paramter bc zawiera najlepsze połączenie z istniejącego wierzchoiłka w cyklu do nowego wierzchołka.
     * Niech będą to wierzchołki X i Y.
     * Metoda łączy te dwa wierzchołki, przez co stopień wierzchołka X wzrasta do 3, dlatego też
     * następnie usuwana jest krawędz między wierzchołkiem X a wierzchołkiem który ma najniższy koszt do Y - nazwijmy go Z.
     * Aby zamknąć cykl dodawana jest krawędź X - Z.
     * @param bc
     */
    private void addNewVertexToCycle(BestConnection bc) {
        Vertex newVertex = bc.to;
        List<Vertex> list = incidenceList.get(bc.from.getId());
        if (list.size() != 2) System.out.println("Cos jest nie tak z grafem! Wierzcholek " + bc.from.getId() + " ma stopien rozny od 2!"); //TODO: throw exception
        makeEdge(bc.from, newVertex);
        removeEdge(bc.from, bc.removedConnectionVertex);
        makeEdge(bc.removedConnectionVertex, newVertex);
    }

    private void removeEdge(Vertex v1, Vertex v2){
        //System.out.println("REMOVE: " + v1.getId() + " - " + v2.getId());
        incidenceList.get(v1.getId()).remove(v2);
        incidenceList.get(v2.getId()).remove(v1);
        cost -= v1.getCostToVertex(v2).getValue();
    }

    private void makeEdge(Vertex v1, Vertex v2){
        //System.out.println("EDGE: " + v1.getId() + " - " + v2.getId());
        incidenceList.get(v1.getId()).add(v2);
        incidenceList.get(v2.getId()).add(v1);
        v2.setVisited(true);
        cost += v1.getCostToVertex(v2).getValue();
    }

    /**
     * Znajduje nalepsze polaczenie z wiercholka vertex do nastepnego, biorac pod uwage (minimalizując)sumaryczny koszt usunięcia
     * jednej krawędzi oraz dodania dwóch nowych krawędzi do nowego wierzchołka.
     * @param vertex
     * @return
     */
    private BestConnection getBestConnection(Vertex vertex){
        BestConnection bc = new BestConnection();
        bc.from = vertex;
        Vertex removedEdgeVertex = incidenceList.get(vertex.getId()).get(0);        //zawsze bierzemy ten z 0 indeksu do usunięcia krawędzi
        int cost = 0 - removedEdgeVertex.getCostToVertex(vertex).getValue();        //zmniejszamy koszt o koszt usuniętej krawędzi
        Optional<Vertex.Cost> bestCost = vertex.getCostList()
                .stream()
                .filter(isUnvisited())
                .min((o1, o2) -> getCostOfAddingTwoEdges(vertex, removedEdgeVertex, vertices.get(o1.getTarget()))
                                    - getCostOfAddingTwoEdges(vertex, removedEdgeVertex, vertices.get(o2.getTarget())));
        cost += bestCost.get().getValue();
        cost += removedEdgeVertex.getCostToVertex(vertices.get(bestCost.get().getTarget())).getValue();
        bc.to = vertices.get(bestCost.get().getTarget());
        bc.removedConnectionVertex = removedEdgeVertex;
        bc.cost = cost;
        return bc;
    }

    /**
     * Zwraca koszt dodania dwóch krawędzi from1 - to oraz from2 - to
     */
    private int getCostOfAddingTwoEdges(Vertex from1, Vertex from2, Vertex to){
        int cost = 0;
        cost += from1.getCostToVertex(to).getValue() + from2.getCostToVertex(to).getValue();
        return cost;
    }

    private Vertex findNearestNeighbour(Vertex v){
        int nearest = v.getCostList().stream()
                                        .min((o1, o2) -> o1.getValue() - o2.getValue())
                                        .get()
                                        .getTarget();
        return vertices.get(nearest);
    }

    private Predicate<Vertex.Cost> isUnvisited() {
        return v -> !vertices.get(v.getTarget()).getVisited();
    }
    private Predicate<Vertex> isVvisited() {
        return v -> v.getVisited();
    }

    private void printResult(){
        System.out.println("Koszt: " + this.bestCost);
        printBestCycle();
        long l = this.bestIncidenceList.stream().filter(v -> v.size() == 2).count();
        System.out.println(l);
    }


    private void printBestCycle(){
        int cost = 0;
        int from = this.startingVertex.getId();
        int previous = -1;

        for (int i=0; i<50; i++) {
            System.out.print(from + " ");
            int to = -1;
            if (bestIncidenceList.get(from).get(0).getId() != previous)
                to = bestIncidenceList.get(from).get(0).getId();
            else
                to = bestIncidenceList.get(from).get(1).getId();

            cost += vertices.get(from).getCostToVertex(vertices.get(to)).getValue();
            previous = from;
            from = to;
        }

        //cost += vertices.get(from).getCostToVertex(vertices.get(this.startingVertex.getId())).getValue();

        System.out.println(from);
        System.out.println(cost);
    }
    public Integer getBestCost() {
        return bestCost;
    }

    public List<List<Vertex>> getBestIncidenceList() {
        return bestIncidenceList;
    }

    public class BestConnection{
        Vertex removedConnectionVertex;  //wierzcholek ktoremu usunieto krawedz z wierzcholkiem from
        Vertex from;
        Vertex to;
        Integer cost;
    }
}
