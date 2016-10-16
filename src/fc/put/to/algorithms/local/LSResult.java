package fc.put.to.algorithms.local;

import fc.put.to.Vertex;

import java.util.List;

/**
 * Created by marcin on 12.10.16.
 */
public class LSResult {
    private Integer cost;
    private List<Vertex> cycle;
    private double time;

    public LSResult(Integer cost, List<Vertex> cycle, double time) {
        this.cost = cost;
        this.cycle = cycle;
        this.time = time;
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

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public String toString(){
        return "Koszt: " + cost + "\n" + cycle;
    }
}
