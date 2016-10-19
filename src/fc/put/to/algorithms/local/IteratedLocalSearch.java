package fc.put.to.algorithms.local;

import fc.put.to.Vertex;
import fc.put.to.algorithms.nn.GraspNearestNeighbor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IteratedLocalSearch extends LocalSearch {

    public IteratedLocalSearch(List<Vertex> vertices) {
        super(vertices);
    }

    @Override
    public LSResult run(){
        long start = System.currentTimeMillis();
        GraspNearestNeighbor gnn = new GraspNearestNeighbor(vertices);
        this.cycleList = gnn.findSolution(vertices.get((new Random()).nextInt(100)));
        LSResult actualResult = super.run();
        System.out.println("Początkowy koszt " + calculateCost(actualResult.getCycle()));
        boolean stopLoop = false;
        do{
            perturbation();
            perturbation();
            perturbation();

            LSResult result = super.run();
            if (result.getCost().intValue() < actualResult.getCost().intValue()) {
                actualResult = result;
            }else{
                this.cycleList = actualResult.getCycle(); //przywracamy poprzednie (lepsze) rozwiazanie i na nim dalej dzialamy
            }
           long stop = System.currentTimeMillis();
            if (stop - start > 1e5)
                stopLoop = true;
        }while(!stopLoop);

        System.out.println(actualResult);
        validateCycle(actualResult.getCycle());
        return null;
    }

    private void perturbation(){
        VertexReplacement vertexReplacements = getRandomVertexReplacement();    //wylosowanie dwoch wierzcholkow do zamiany
        List<EdgesReplacement> edgeReplacements = IntStream.range(0, cycleList.size() - 3)  //wszystkie mozliwe pary krawedzi do usuniecia
                .mapToObj(i -> this.getPossibleEdgesPairs(i, cycleList))                    //szybciej byloby losowac niz generowac...
                .flatMap(vr -> vr.stream())
                .collect(Collectors.toList());

        if ((new Random()).nextBoolean()){
            vertexReplacements = calculateDeltaCostVR(vertexReplacements);
            swapVertices(vertexReplacements.vertexIndex, vertexReplacements.forVertex);
        }else{
            EdgesReplacement randomEr = edgeReplacements.get((new Random().nextInt(edgeReplacements.size())));
            Collections.reverse(cycleList.subList(randomEr.edge1To, randomEr.edge2From + 1));
        }
    }

    /**
     * Zwraca liste mozliwych krawedzi do usuniecia z ktorych jedna krawedz zaczyna sie na pocyzji index
     */
    protected List<EdgesReplacement> getPossibleEdgesPairs(int index, List<Vertex> cycleList){
        List<EdgesReplacement> minEr = IntStream.range(index + 2, cycleList.size() - 1)
                .mapToObj(i -> new EdgesReplacement(index, index + 1, i, i + 1, null))
                .collect(Collectors.toList());
        return minEr;
    }

    /**
     * Losuje jakie wierzcholki zamienic miejscami
     * @return
     */
    private VertexReplacement getRandomVertexReplacement() {
        int index = (new Random()).nextInt(this.cycleList.size() - 1);
        List<Integer> freeVertices = IntStream.range(0, this.vertices.size())
                .filter(i -> !cycleList.contains(vertices.get(i)))
                .boxed()
                .collect(Collectors.toList());
        int secondVertex = freeVertices.get((new Random()).nextInt(freeVertices.size()));
        VertexReplacement vr = new VertexReplacement();
        vr.vertexIndex = index;
        vr.forVertex = vertices.get(secondVertex);
        return vr;
    }

    /**
     * Zwraca obiekt wejsciowy uzupleniony o delte kosztu cyklu (przydatne zeby nie liczyc za kazdym razem kosztu calego cyklu)
     */
    private VertexReplacement calculateDeltaCostVR(VertexReplacement vr){
        int index = vr.vertexIndex;
        Vertex from = this.cycleList.get(index);
        Integer previous = index - 1;
        if (previous < 0)
            previous = cycleList.size() - 2;
        Vertex nextVertex = cycleList.get(index + 1);
        Integer deltaCost = -from.getCostToVertex(nextVertex).getValue();
        deltaCost -= cycleList.get(previous).getCostToVertex(from).getValue();
        vr = getCostOfAddEdges(previous , index + 1, vr.forVertex, cycleList);
        vr.deltaCost += deltaCost;
        return vr;
    }

    private void validateCycle(List<Vertex> cycle){
        int size = cycle.size();
        Set<Vertex> vSet = new HashSet<>(cycle);
        if (vSet.size() + 1 != size )
            System.out.println("Błąd");
    }
}