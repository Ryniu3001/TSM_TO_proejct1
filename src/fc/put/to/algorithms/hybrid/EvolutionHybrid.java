package fc.put.to.algorithms.hybrid;

import fc.put.to.Constants;
import fc.put.to.Main;
import fc.put.to.Vertex;
import fc.put.to.algorithms.local.LSResult;
import fc.put.to.algorithms.local.LocalSearch;
import fc.put.to.algorithms.util.Checker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EvolutionHybrid {

    private List<LSResult> population;
    private List<Vertex> vertexList;

    public EvolutionHybrid(List<LSResult> population, List<Vertex> vertexList){
        if (population.size() != 20)
            System.out.println("Nieprawidlowa wielkość populacji");
        this.population = population;
        this.vertexList = vertexList;
    }

    public void run(){
        int loopCounter = 0;
        Map<Integer, Long> iterToTimeMap = new HashMap<>();
        Map<Integer, Integer> iterToCostMap = new HashMap<>();
        Random generator = new Random();
        boolean stopLoop = false;
        Integer minAt1100 = 0;
        long start = System.currentTimeMillis();
        while (!stopLoop) {
            long innerStart = System.currentTimeMillis();
            loopCounter ++;
            int solution1 = generator.nextInt(this.population.size());
            int solution2;
            do {
                solution2 = generator.nextInt(this.population.size());
            } while (solution1 == solution2);

            LSResult result = recombination(population.get(solution1).getCycle(), population.get(solution2).getCycle());
            Integer worstInPopulation = IntStream.range(0, this.population.size())
                    .boxed()
                    .max((o1, o2) -> this.population.get(o1).getCost() - this.population.get(o2).getCost())
                    .get();
            if (result.getCost() < this.population.get(worstInPopulation).getCost()
                    && !this.population.stream().map(LSResult::getCost).collect(Collectors.toList()).contains(result.getCost())){
                this.population.remove(worstInPopulation.intValue());
                this.population.add(result);
            }

            if (loopCounter == 1100)
                minAt1100 = this.population.stream().min((o1, o2) -> o1.getCost() - o2.getCost()).get().getCost();

            long stop = System.currentTimeMillis();
            iterToTimeMap.put(loopCounter, stop - innerStart);
            iterToCostMap.put(loopCounter, result.getCost());
            if (stop - start > 57000)
                stopLoop = true;
        }
        System.out.println("Min koszt po 1100 iteracjach: " + minAt1100);
        System.out.println("Iteracji: " + loopCounter);
        saveToFile("iterToCost", iterToCostMap);
        saveToFile("iterToTime", iterToTimeMap);
        Main.printLCResult(this.population);
    }

    private LSResult recombination(List<Vertex> cycle1, List<Vertex> cycle2){
        List<List<Vertex>> equalsFragments = new Checker(vertexList).getEqualsFragments(cycle1, cycle2);
        equalsFragments = this.fillUpTo50Vertices(equalsFragments);
        List<Vertex> newCycle = this.createNewCycle(equalsFragments);
        LSResult result = new LocalSearch(vertexList, newCycle).run();
        return  result;
    }

    private List<Vertex> createNewCycle(List<List<Vertex>> equalsFragments){
        Random generator = new Random();
        List<Vertex> newCycle = new ArrayList<>(Constants.LENGHT_OF_SOLUTION + 1);
        while (equalsFragments.size() > 0){
            int elementId = generator.nextInt(equalsFragments.size());
            List<Vertex> fragment = equalsFragments.get(elementId);
            if (fragment.size() > 1 && generator.nextBoolean())
                Collections.reverse(fragment);
            newCycle.addAll(fragment);
            equalsFragments.remove(elementId);
        }

        newCycle.add(newCycle.get(0));

        return newCycle;
    }

    /**
     * Uzupełnia wspólne fragmenty o losowe wierzchołki.
     * @param equalsFragments
     * @return
     */
    private List<List<Vertex>> fillUpTo50Vertices(List<List<Vertex>> equalsFragments){
        Set<Integer> vertexSet = equalsFragments.stream().flatMap(List::stream).map(Vertex::getId).collect(Collectors.toSet());
        Random generator = new Random();
        while (vertexSet.size() != 50) {
            int newVertexID = generator.nextInt(100);
            if (!vertexSet.contains(newVertexID)){
                vertexSet.add(newVertexID);
                List<Vertex> newVertexList = new ArrayList<>();
                newVertexList.add(vertexList.get(newVertexID));
                equalsFragments.add(newVertexList);
            }
        }
        return equalsFragments;
    }

    private void saveToFile(String filename, Map o) {
        List<String> mapInList = new ArrayList<>();
        for (int i = 1; i <= o.size(); i++){
            mapInList.add(i + " " + o.get(i));
        }
        try {
            Files.write(Paths.get(filename), mapInList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
