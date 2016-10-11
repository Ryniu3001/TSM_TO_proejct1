package fc.put.to.algorithms.greedy;

import fc.put.to.Vertex;

import java.util.ArrayList;
import java.util.List;
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
    protected final List<Vertex> vertices;
    protected List<Vertex> incidenceList;
    private Integer cost;

    //Pola wynikowe
    private Integer bestCost;
    private Integer avgCost;
    private Integer maxCost;
    private List<Vertex> bestIncidenceList;
    private Vertex startingVertex;

    public GreedyCycle(List<Vertex> vertices){
        this.vertices = vertices;
        initialize();
        this.bestCost = null;
    }

    private void initialize(){
        incidenceList = new ArrayList<>(100);
        IntStream.rangeClosed(0, 99).forEach(v -> incidenceList.add(null));
        vertices.forEach(vertex -> vertex.setVisited(false));
        cost = 0;
    }
    public void run(){
        this.maxCost = 0;
        this.avgCost = 0;
        vertices.stream().forEach(this::findHamiltonianPath);
        this.avgCost = this.avgCost / (vertices.size());
        printResult();
    }

    private void findHamiltonianPath(Vertex initialVertex) {
        initialVertex.setVisited(true);
        IntStream.rangeClosed(0, 48).forEach(i -> nextStep(i));
        this.maxCost = this.cost > this.maxCost ? this.cost : this.maxCost;
        this.avgCost += this.cost;
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
    protected void nextStep(int i){

        if (i==0) {                             //pierwszy przebieg (wszystkie wierzcholki o stopniu 0)
            Vertex from = vertices.stream().filter(v -> v.getVisited()).findFirst().get();
            Vertex nn =findNearestNeighbour(from);
            makeEdge(from, nn);
            makeEdge(nn, from);
        }else{
            Connection bestConnection = getBestConnection();
            addNewVertexToCycle(bestConnection);

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
    protected void addNewVertexToCycle(Connection bc) {
        Vertex newVertex = bc.to;
        //removeEdge(bc.from1, bc.from2);
        cost -= bc.from1.getCostToVertex(bc.from2).getValue();
        makeEdge(bc.from1, newVertex);
        makeEdge(newVertex, bc.from2);
    }

    private void removeEdge(Vertex v1, Vertex v2){
        if (incidenceList.get(v1.getId()).getId() == v2.getId())
            incidenceList.set(v1.getId(), null);
        else
            System.out.println("ERROR");

    }

    protected void makeEdge(Vertex v1, Vertex v2){
        incidenceList.set(v1.getId(), v2);
        v2.setVisited(true);
        cost += v1.getCostToVertex(v2).getValue();
    }

    /**
     * Wyznacza najlepsze mozliwe polaczenia (optymalizuje cykl) z kazdego wierzcholka
     * (usuniecie krawedzi i dodanie dwóch nowych dla zachowania cyklu)
     * a nastepnie zwraca to o najmniejszym koszcie.
     *
     * @return
     */
    protected Connection getBestConnection(){
        List<Connection> possibleConn = new ArrayList<>();
        Vertex from1 = this.incidenceList.stream().filter(v -> v != null).findFirst().get();
        Vertex startingPoint = from1;
        Vertex from2 = this.incidenceList.get(from1.getId());
        Connection best;
        do{
            Vertex finalFrom = from1;
            Vertex finalFrom1 = from2;
            possibleConn.add(vertices.stream()
                    .filter(v -> !v.getVisited())
                    .map(v -> getCostOfAddingTwoEdges(finalFrom, finalFrom1, v))
                    .min((o1, o2) -> o1.cost - o2.cost).get());

            Vertex previous = from1;
            from1 = from2;
            Vertex next = this.incidenceList.get(from1.getId());

            if (next != previous)
                from2 = next;
            else
                break;

        }while(from1.getId() != startingPoint.getId());
        best = chooseBestConnectionFromList(possibleConn);
        return best;
    }
    protected Connection chooseBestConnectionFromList(List<Connection> list){
        return list.stream().min((o1, o2) -> {
            return o1.cost - o2.cost == 0 ? 1 : o1.cost - o2.cost;      //w przypadku rownych traktuj drugi jako mniejszy
        }).get();
    }
    /**
     * Zwraca koszt dodania dwóch krawędzi from1 - to oraz from2 - to
     */
    protected Connection getCostOfAddingTwoEdges(Vertex from1, Vertex from2, Vertex to){
        Connection conn = new Connection();
        conn.from1 = from1;
        conn.from2 = from2;
        conn.cost = 0 - from1.getCostToVertex(from2).getValue();
        conn.to = to;
        conn.cost += from1.getCostToVertex(to).getValue() + from2.getCostToVertex(to).getValue();
        return conn;
    }

    protected Vertex findNearestNeighbour(Vertex v){
        int nearest = v.getCostList().get(0).getTarget(); //metoda wywolywana gdy w grafie jest 1 wierzcholek wiec nie trzeba sprawdzac czy cel juz byl odwiedzony
        return vertices.get(nearest);
    }

    protected Predicate<Vertex.Cost> isUnvisited() {
        return v -> !vertices.get(v.getTarget()).getVisited();
    }
    protected Predicate<Vertex> isVvisited() {
        return v -> v.getVisited();
    }

    protected void printResult(){
        System.out.println("Min: " + this.bestCost);
        System.out.println("Avg: " + this.avgCost);
        System.out.println("Max: " + this.maxCost);
        Integer cost = 0;
        printBestCycle(this.startingVertex, cost);
        System.out.println();
    }


    private void printBestCycle(Vertex v, Integer cost){
        System.out.print(v.getId() + " ");
        Vertex next = this.bestIncidenceList.get(v.getId());
        cost += v.getCostToVertex(next).getValue();
        if (next.getId() == this.startingVertex.getId()) {
            System.out.println(next.getId() + " ");
            return;
        }
        printBestCycle(next, cost);
    }
    public Integer getBestCost() {
        return bestCost;
    }

    public List<Vertex> getBestIncidenceList() {
        return bestIncidenceList;
    }
    /**
     * Wierzcholki from1 i from2 to takie pomiedzy ktorymi usunieto łuk, w celu dodania łuków from1-to, oraz from2-to
     * Koszt jest to zmiana kosztu grafu po wykonaniu powyzszych operacji
     */
    public class Connection {

        Vertex from2;
        Vertex from1;
        Vertex to;
        Integer cost;
    }
}
