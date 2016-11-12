package fc.put.to.algorithms.hybrid;

import fc.put.to.Constants;
import fc.put.to.Main;
import fc.put.to.Vertex;
import fc.put.to.algorithms.local.LSResult;
import fc.put.to.algorithms.local.LocalSearch;
import fc.put.to.algorithms.util.Checker;

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
        int i = 0;
        Random generator = new Random();
        while (i++ < 1000) {
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
        }
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
}
