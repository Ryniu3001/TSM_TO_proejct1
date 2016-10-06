package fc.put.to;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Vertex> vertexList = Parser.readFile();
/*        GreedyCycle gc = new GreedyCycle(vertexList);
        gc.run();*/


/*        long start = System.nanoTime();
        vertexList.stream().forEach(value -> new GreedyCycle(vertexList, value));

        long end = System.nanoTime();
        System.out.println("Time taken(ms): " + (end - start) / 1.0e6);*/



    }
}
