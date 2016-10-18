package fc.put.to.algorithms.local;

import fc.put.to.Vertex;
import fc.put.to.algorithms.greedy.GraspGreedyCycle;
import fc.put.to.algorithms.greedy.GreedyCycle;
import fc.put.to.algorithms.nn.GraspNearestNeighbor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by inf109711 on 18.10.2016.
 */
public class MultipleLocalSearch {
    public LSResult run(List<Vertex> vertexList){
        List<LSResult> bestResults = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GraspGreedyCycle ggc = new GraspGreedyCycle(vertexList);
            ggc.run();
            List<LSResult> gResult = vertexList.stream()
                    .map(v -> new LocalSearch(vertexList, GreedyCycle.incidenceToCycleList(ggc.getAllIncidenceMap().get(v), v)).run())
                    .collect(Collectors.toList());
            bestResults.add(gResult.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get());
        }
        return bestResults.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get();
    }
}
