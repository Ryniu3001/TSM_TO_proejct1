package fc.put.to;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by marcin on 04.10.16.
 */
public class Vertex {

    private Integer id;
    private List<Cost> costList;
    private Boolean visited;


    public Vertex(Integer id){
        this.id = id;
        costList = new ArrayList<>();
        visited = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Cost> getCostList() {
        return costList;
    }

    public void setCostList(List<Cost> costList) {
        this.costList = costList;
    }

    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }

    public void addDistance(Integer to, Integer cost){
        Cost c = new Cost(to, cost);
        this.costList.add(c);
    }

    public Cost getCostToVertex(Vertex target){
        Optional<Cost> cost = this.getCostList().stream().filter(c -> c.getTarget() == target.getId()).findFirst();
        if (cost.isPresent())
            return cost.get();
        else
            throw new NullPointerException("Brak docelowego wierzcholka.");
    }
    @Override
    public String toString(){
        return this.id.toString();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean same = false;
        if (object != null && object instanceof Vertex)
        {
            same = this.id == ((Vertex) object).getId();
        }
        return same;
    }

    public class Cost implements Comparable<Cost> {

        private Integer target;
        private Integer value;

        public Cost(int target, int value){
            this.target = target;
            this.value = value;
        }

        public Integer getTarget() {
            return target;
        }

        public Integer getValue() {
            return value;
        }

        @Override
        public int compareTo(Cost o) {
            return this.value - o.getValue();
        }
    }
}
