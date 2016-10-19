package fc.put.to.algorithms.local;

import fc.put.to.Vertex;
import fc.put.to.algorithms.nn.GraspNearestNeighbor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by inf109711 on 18.10.2016.
 */
public class MultipleLocalSearch {
    public LSResult run(List<Vertex> vertexList){
        List<LSResult> bestResults = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            GraspNearestNeighbor graspNearestNeighbor = new GraspNearestNeighbor(vertexList);
            List<LSResult> gResult = vertexList.stream()
                    .map(v -> new LocalSearch(vertexList, graspNearestNeighbor.findSolution(v)).run())
                    .collect(Collectors.toList());
            bestResults.add(gResult.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get());
        });
        System.out.println(bestResults.size());
        return bestResults.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get();
    }
}
