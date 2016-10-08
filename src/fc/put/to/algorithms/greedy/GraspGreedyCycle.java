package fc.put.to.algorithms.greedy;

import fc.put.to.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Override
    protected Connection getBestConnection(){
        List<Connection> possibleConn = new ArrayList<>();
        Vertex from1 = this.incidenceList.stream().filter(v -> v.size() == 2).findFirst().get().get(0);
        Vertex startingPoint = from1;
        Vertex from2 = this.incidenceList.get(from1.getId()).get(0);
        Connection best = new Connection();
        do{
            Vertex finalFrom = from1;
            Vertex finalFrom1 = from2;
            List<Connection> connPropositions = vertices.stream()               //najlepsze zmiany w grafie przy usunięciu każdej krawędzi
                    .filter(v -> !v.getVisited())
                    .map(v -> getCostOfAddingTwoEdges(finalFrom, finalFrom1, v))
                    .sorted((o1, o2) -> o1.cost - o2.cost)
                    .collect(Collectors.toList());
            IntStream.range(0,3).forEach(v -> possibleConn.add(connPropositions.get(v)));       //wybieramy 3 najlepsze zmiany
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
