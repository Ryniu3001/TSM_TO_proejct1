package fc.put.to.algorithms.greedy;

import fc.put.to.Vertex;

import java.util.List;
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

    @Override
    protected Connection chooseBestConnectionFromList(List<Connection> list){
        list = list.stream().sorted((o1, o2) -> o1.cost - o2.cost == 0 ? 1 : o1.cost - o2.cost).limit(3).collect(Collectors.toList());
        Random r = new Random();
        return  list.get(r.nextInt(list.size() < 3 ? list.size() : 3));
    }
}
