package fc.put.to.algorithms.local;

import fc.put.to.Vertex;
import fc.put.to.algorithms.nn.GraspNearestNeighbor;
import fc.put.to.algorithms.random.RandomSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by inf109711 on 18.10.2016.
 */
public class MultipleLocalSearch2 {
    public List<LSResult> run(List<Vertex> vertexList){
        List<LSResult> bestResults = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            RandomSolution randomSolution = new RandomSolution(vertexList);
            List<LSResult> results = vertexList.stream()
                    .map(v -> new LocalSearch(vertexList, randomSolution.findSolution(v)).run())
                    .collect(Collectors.toList());
            bestResults.addAll(results);
        });
        System.out.println(bestResults.size());
        return bestResults;
    }
}
