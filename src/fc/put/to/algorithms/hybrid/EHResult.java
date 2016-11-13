package fc.put.to.algorithms.hybrid;

import fc.put.to.algorithms.local.LSResult;

import java.util.Map;

/**
 * Created by marcin on 13.11.16.
 */
public class EHResult {

    private Map<Integer, Long> iterToTimeMap;
    private Map<Integer, Integer> iterToCostMap;
    private LSResult lsResult;

    public EHResult(Map<Integer, Long> iterToTimeMap, Map<Integer, Integer> iterToCostMap, LSResult lsResult) {
        this.iterToTimeMap = iterToTimeMap;
        this.iterToCostMap = iterToCostMap;
        this.lsResult = lsResult;
    }

    public Map<Integer, Long> getIterToTimeMap() {
        return iterToTimeMap;
    }

    public void setIterToTimeMap(Map<Integer, Long> iterToTimeMap) {
        this.iterToTimeMap = iterToTimeMap;
    }

    public Map<Integer, Integer> getIterToCostMap() {
        return iterToCostMap;
    }

    public void setIterToCostMap(Map<Integer, Integer> iterToCostMap) {
        this.iterToCostMap = iterToCostMap;
    }

    public LSResult getLsResult() {
        return lsResult;
    }

    public void setLsResult(LSResult lsResult) {
        this.lsResult = lsResult;
    }
}

