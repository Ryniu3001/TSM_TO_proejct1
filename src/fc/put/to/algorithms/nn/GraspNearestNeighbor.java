package fc.put.to.algorithms.nn;

import fc.put.to.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by maciej on 08.10.16.
 */
public class GraspNearestNeighbor extends NearestNeighbor {

    private static final List<Integer> top3 = new ArrayList<Integer>() {{
        add(0);
        add(1);
        add(2);
    }};

    public GraspNearestNeighbor(List<Vertex> vertices) {
        super(vertices);
    }

    @Override
    protected Vertex nearestNeighbor(List<Vertex> currentSolution, Vertex vertex) {
        List<Vertex.Cost> costList = vertex.getCostList().stream()
                .filter(c -> isOnList(currentSolution, c) == false)
                .collect(Collectors.toList());

        Collections.shuffle(top3);

        for (int index : top3) {
            Vertex.Cost cost = costList.get(index);
            if (isOnList(currentSolution, cost) == false) {
                return vertices.get(cost.getTarget());
            }
        }

        throw new IllegalStateException("Can't find nearest vertex");
    }
}
