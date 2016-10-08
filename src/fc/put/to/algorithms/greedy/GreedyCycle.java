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
    protected final List<Vertex> vertices;
    private List<List<Vertex>> incidenceList;
    private Integer cost;

    //Pola wynikowe
    private Integer bestCost;
    private Integer avgCost;
    private Integer maxCost;
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
        this.maxCost = 0;
        this.avgCost = 0;
        vertices.stream().forEach(this::findHamiltonianPath);
        this.avgCost = this.avgCost / (vertices.size());
        System.out.println("DONE ");
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
    private void nextStep(int i){

        if (i==0) {                             //pierwszy przebieg (wszystkie wierzcholki o stopniu 0)
            Vertex from = vertices.stream().filter(v -> v.getVisited()).findFirst().get();
            Vertex nn =findNearestNeighbour(from);
            makeEdge(from, nn);
            makeEdge(nn, from);
        }
        else{
/*            Optional<Connection> bestConnection = vertices.stream()
                    .filter(isVvisited())
                    .map(this::getBestConnection)
                    .min((o1, o2) -> o1.cost - o2.cost);*/

           // System.out.println("Add: " + bestConnection.get().from1.getId() + " - " + bestConnection.get().to.getId() + " R: " + bestConnection.get().from2.getId());
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
    private void addNewVertexToCycle(Connection bc) {
        Vertex newVertex = bc.to;
        List<Vertex> list = incidenceList.get(bc.from1.getId());
        if (list.size() != 2) System.out.println("Cos jest nie tak z grafem! Wierzcholek " + bc.from1.getId() + " ma stopien rozny od 2!"); //TODO: throw exception
        makeEdge(bc.from1, newVertex);
        removeEdge(bc.from1, bc.from2);
        makeEdge(bc.from2, newVertex);
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

    private Connection getBestConnection(){
        Connection tmpConnection;
        Vertex from1 = this.incidenceList.stream().filter(v -> v.size() == 2).findFirst().get().get(0);
        Vertex startingPoint = from1;
        Vertex from2 = this.incidenceList.get(from1.getId()).get(0);
        Connection best = new Connection();
        do{
            Vertex finalFrom = from1;
            Vertex finalFrom1 = from2;
            tmpConnection = vertices.stream()
                    .filter(v -> !v.getVisited())
                    .map(v -> getCostOfAddingTwoEdges(finalFrom, finalFrom1, v))
                    .min((o1, o2) -> o1.cost - o2.cost).get();
            if (best.cost == null || best.cost >= tmpConnection.cost){
                best.from1 = from1;
                best.to = tmpConnection.to;
                best.from2 = from2;
                best.cost = tmpConnection.cost;
            }

            Vertex previous = from1;
            from1 = from2;
            Optional<Vertex> next = this.incidenceList.get(from1.getId()).stream()
                                                                        .filter(v -> v.getId() != previous.getId())
                                                                        .findFirst();
            if (next.isPresent())
                from2 = next.get();
            else
                break;

        }while(from1.getId() != startingPoint.getId());




        return best;
    }

    /**
     * Zwraca koszt dodania dwóch krawędzi from1 - to oraz from2 - to
     */
    private Connection getCostOfAddingTwoEdges(Vertex from1, Vertex from2, Vertex to){
        Connection conn = new Connection();
        conn.cost = 0 - from1.getCostToVertex(from2).getValue();
        conn.to = to;
        conn.cost += from1.getCostToVertex(to).getValue() + from2.getCostToVertex(to).getValue();
        return conn;
    }

    protected Vertex findNearestNeighbour(Vertex v){
        int nearest = v.getCostList().get(0).getTarget(); //metoda wywolywana gdy w grafie jest 1 wierzcholek wiec nie trzeba sprawdzac czy cel juz byl odwiedzony
        return vertices.get(nearest);
    }

    private Predicate<Vertex.Cost> isUnvisited() {
        return v -> !vertices.get(v.getTarget()).getVisited();
    }
    private Predicate<Vertex> isVvisited() {
        return v -> v.getVisited();
    }

    private void printResult(){
        System.out.println("Min: " + this.bestCost);
        System.out.println("Avg: " + this.avgCost);
        System.out.println("Max: " + this.maxCost);
        printBestCycle();
        long l = this.bestIncidenceList.stream().filter(v -> v.size() == 2).count();
        System.out.println(l);
    }


    private void printBestCycle(){
        int cost = 0;
        int from = this.startingVertex.getId();
        int previous = -1;

        for (int i=0; i<this.bestIncidenceList.stream().filter(v -> v.size() == 2).count(); i++) {
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

        //cost += vertices.get(from1).getCostToVertex(vertices.get(this.startingVertex.getId())).getValue();

        System.out.println(from);
        System.out.println(cost);
    }
    public Integer getBestCost() {
        return bestCost;
    }

    public List<List<Vertex>> getBestIncidenceList() {
        return bestIncidenceList;
    }

    public class Connection {
        Vertex from2;  //wierzcholek ktoremu usunieto krawedz z wierzcholkiem from1
        Vertex from1;
        Vertex to;
        Integer cost;                    //sumaryczny koszt usuniecia jednej i dodania dwoch krawedzi
    }
}
