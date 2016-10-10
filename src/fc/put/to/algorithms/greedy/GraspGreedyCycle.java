package fc.put.to.algorithms.greedy;

import fc.put.to.Constants;
import fc.put.to.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by marcin on 08.10.16.
 */
public class GraspGreedyCycle extends GreedyCycle {
    public GraspGreedyCycle(List<Vertex> vertices) {
        super(vertices);
    }

    @Override
    protected Vertex findNearestNeighbour(Vertex v){
        Random r = new Random();
        int nearest = v.getCostList().get(r.nextInt(3)).getTarget();
        return vertices.get(nearest);
    }
    /**
     * Następna iteracja przyłączenia nowego wierzchołka do grafu
     */
    @Override
    protected void nextStep(int i){

        if (i==0) {                             //pierwszy przebieg (wszystkie wierzcholki o stopniu 0)
            Vertex from = vertices.stream().filter(v -> v.getVisited()).findFirst().get();
            Vertex nn =findNearestNeighbour(from);
            makeEdge(from, nn);
            makeEdge(nn, from);
        }else{
            if (Constants.VERSION_OF_GC == 1) {
                List<Connection> bestConnections = vertices.stream()
                    .filter(isVvisited())
                    .map(this::getBestConnection2)
                    .sorted((o1, o2) -> o1.cost - o2.cost)
                    .collect(Collectors.toList());
                Random r = new Random();
                Connection randomNearest = bestConnections.get(bestConnections.size() <= 2 ? r.nextInt(bestConnections.size()) : r.nextInt(3));
                addNewVertexToCycle(randomNearest);
            }else {
                Connection randomNearest = getBestConnection();
                addNewVertexToCycle(randomNearest);
            }
        }
    }

    /**
     * Zwraca najlepsze połączenie optymalizując koszt cyklu (dodania dwóch krawędzi o usunięcia jednej)
     * @return
     */
    @Override
    protected Connection getBestConnection(){
        List<Connection> possibleConn = new ArrayList<>();
        Vertex from1 = this.incidenceList.stream().filter(v -> v.size() == 1).findFirst().get().get(0);
        Vertex startingPoint = from1;
        Vertex from2 = this.incidenceList.get(from1.getId()).get(0);
        Connection best ;
        do{
            Vertex finalFrom = from1;
            Vertex finalFrom1 = from2;
            Optional<Connection> connProposition = vertices.stream()               //najlepsze zmiany w grafie przy usunięciu każdej krawędzi
                    .filter(v -> !v.getVisited())
                    .map(v -> getCostOfAddingTwoEdges(finalFrom, finalFrom1, v))
                    .min((o1, o2) -> o1.cost - o2.cost);
            possibleConn.add(connProposition.get());
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
        best = chooseBestConnectionFromList(possibleConn);
        return best;
    }

    @Override
    protected Connection chooseBestConnectionFromList(List<Connection> list){
        list = list.stream().sorted((o1, o2) -> o1.cost - o2.cost).limit(3).collect(Collectors.toList());
        Random r = new Random();
        return  list.get(r.nextInt(list.size() < 3 ? list.size() : 3));
    }
}
