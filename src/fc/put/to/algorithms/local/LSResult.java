package fc.put.to.algorithms.local;

import fc.put.to.Vertex;

import java.util.List;

/**
 * Created by marcin on 12.10.16.
 */
public class LSResult {
    private Integer cost;
    private List<Vertex> cycle;

    public LSResult(Integer cost, List<Vertex> cycle) {
        this.cost = cost;
        this.cycle = cycle;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public List<Vertex> getCycle() {
        return cycle;
    }

    public void setCycle(List<Vertex> cycle) {
        this.cycle = cycle;
    }

    @Override
    public String toString(){
        return "Koszt: " + cost + "\n" + cycle;
    }
}
