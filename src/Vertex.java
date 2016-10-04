import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcin on 04.10.16.
 */
public class Vertex {

    Integer id;
    List<Cost> costList;


    public Vertex(Integer id){
        this.id = id;
        costList = new ArrayList<>();
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

    public void addDistance(Integer to, Integer cost){
        Cost c = new Cost(to, cost);
        this.costList.add(c);
    }

    public class Cost {

        Integer target;
        Integer value;

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
    }
}
