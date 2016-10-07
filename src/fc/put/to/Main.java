package fc.put.to;

import fc.put.to.algorithms.greedy.GreedyCycle;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Vertex> vertexList = Parser.readFile();
        GreedyCycle gc = new GreedyCycle(vertexList);
        gc.run();

    }
}
