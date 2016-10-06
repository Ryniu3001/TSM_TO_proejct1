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
        vertices.stream().forEach(this::findHamiltonianPath);
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
        Optional<BestConnection> bestConnection = vertices.stream()
                .filter(isVvisited())
                .map(this::getBestConnection)
                .min((o1, o2) -> o1.cost.getValue() - o2.cost.getValue());

        if (/*incidenceList.stream().allMatch(v -> v.size() == 0)*/ i==0)                             //pierwszy przebieg (wszystkie wierzcholki o stopniu 0)
            makeEdge(bestConnection.get().from, vertices.get(bestConnection.get().cost.getTarget()));
        else if (/*incidenceList.stream().filter(v -> v.size() == 1).count() == 2*/ i==1){                       //2 wierzcholki o stopniu 1
            Vertex vertexToMakeCycle = incidenceList.get(bestConnection.get().from.getId()).get(0);
            makeEdge(bestConnection.get().from, vertices.get(bestConnection.get().cost.getTarget()));
            makeEdge(vertexToMakeCycle, vertices.get(bestConnection.get().cost.getTarget()));           //cykl z 3 wierzcholkow
        }else{
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
        Vertex newVertex = vertices.get(bc.cost.getTarget());
        List<Vertex> list = incidenceList.get(bc.from.getId());
        if (list.size() != 2) System.out.println("Cos jest nie tak z grafem! Wierzcholek " + bc.from.getId() + " ma stopien rozny od 2!"); //TODO: throw exception
        Optional<Vertex> best = list.stream().min((o1, o2) -> o1.getCostToVertex(newVertex).getValue()
                                                            - o2.getCostToVertex(newVertex).getValue());
        makeEdge(bc.from, newVertex);
        removeEdge(bc.from, best.get());
        makeEdge(best.get(), newVertex);
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

    private BestConnection getBestConnection(Vertex vertex){
        BestConnection bc = new BestConnection();
        bc.from = vertex;
        Optional<Vertex.Cost> bestCost = vertex.getCostList()
                .stream()
                .filter(isUnvisited())
                .findFirst();
        bc.cost = bestCost.get();
        return bc;
    }


    private Predicate<Vertex.Cost> isUnvisited() {
        return v -> !vertices.get(v.getTarget()).getVisited();
    }
    private Predicate<Vertex> isVvisited() {
        return v -> v.getVisited();
    }

    public Integer getBestCost() {
        return bestCost;
    }

    public List<List<Vertex>> getBestIncidenceList() {
        return bestIncidenceList;
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

    public class BestConnection{
        Vertex from;
        Vertex.Cost cost;
    }
}
