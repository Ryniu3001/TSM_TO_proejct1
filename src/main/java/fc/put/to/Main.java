package fc.put.to;

import fc.put.to.algorithms.greedy.Draw;

public class Main {

    public static void main(String[] args) {
/*        List<Vertex> vertexList = Parser.readFile();
        GreedyCycle gc = new GreedyCycle(vertexList);
        gc.run();*/

        Draw d = new Draw();
        d.draw();

/*        long start = System.nanoTime();
        vertexList.stream().forEach(value -> new GreedyCycle(vertexList, value));

        long end = System.nanoTime();
        System.out.println("Time taken(ms): " + (end - start) / 1.0e6);*/



    }
}
