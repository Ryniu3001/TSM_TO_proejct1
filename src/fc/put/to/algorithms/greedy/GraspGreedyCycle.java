package fc.put.to.algorithms.greedy;

import fc.put.to.Vertex;

import java.util.List;
import java.util.Random;

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
}
