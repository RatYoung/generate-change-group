package edu.nju.cs.inform.core.relation;

import edu.nju.cs.inform.core.type.LinksList;
import edu.nju.cs.inform.core.type.SingleLink;
import edu.nju.cs.inform.core.relation.graph.CallEdge;
import edu.nju.cs.inform.core.relation.graph.CodeEdge;
import edu.nju.cs.inform.core.relation.graph.CodeVertex;
import edu.nju.cs.inform.core.relation.graph.EdgeType;
import edu.nju.cs.inform.core.relation.info.RelationPair;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

import java.util.*;

/**
 * Created by niejia on 15/3/2.
 */
public class CallRelationGraph extends RelationGraph {

    public Graph<CodeVertex, CodeEdge> dirGraph;

    private Map<CodeEdge, Double> callEdgeScoreMap;
    private List<Double> callEdgeScoreValues;

    private TreeMap<String, Double> vertexIDF;


    public CallRelationGraph(RelationInfo relationInfo) {
        super(relationInfo);

        dirGraph = new DirectedSparseGraph<>();
        idCodeVertexMap = new LinkedHashMap<>();
        nameCodeVertexMap = new LinkedHashMap<>();

        callEdgeScoreMap = new HashMap<>();

        vertexIDF = new TreeMap<>();



        constructGraph(relationInfo);
        if (relationInfo.isPruning()) {
            pruning(relationInfo.getCallEdgeScoreThreshold());
        }

        computeVertexIDF();
    }

    private void constructGraph(RelationInfo relationInfo) {
        for (Integer i : relationInfo.getVertexes().keySet()) {
            CodeVertex cv = new CodeVertex(i, relationInfo.getVertexNameById(i));
            idCodeVertexMap.put(i, cv);
            nameCodeVertexMap.put(cv.getName(), cv);
            dirGraph.addVertex(cv);
        }

        for (RelationPair callPair : relationInfo.getCallRelationPairList()) {
            Integer id = edgeFactory.create();

            CodeVertex source = idCodeVertexMap.get(callPair.getKey());
            CodeVertex target = idCodeVertexMap.get(callPair.getValue());

            CallEdge callEdge = new CallEdge(id, EdgeType.Call, source, target);
            callEdge.setCallRelationList(relationInfo.getCallRelationListForRelationPair(callPair));

            dirGraph.addEdge(callEdge, source, target);
        }
    }

    private void pruning(double callEdgeScoreThreshold) {

        int orginCallNum = dirGraph.getEdgeCount();
//        System.out.println("--Origin Call Edge Num: " + orginCallNum);

        removeCallEdgesBelowCallEdgeScoreThreshold(callEdgeScoreThreshold);

        int afterCallNum = dirGraph.getEdgeCount();
//        System.out.println("--After Removed Call Edge Num: " + afterCallNum);

//        System.out.println(1.0 * afterCallNum / orginCallNum);
    }


        @Override
    public int getVertexesNum() {
        return dirGraph.getVertexCount();
    }

    @Override
    public Map<Integer, CodeVertex> getVertexes() {
        return idCodeVertexMap;
    }

    @Override
    public List<CodeVertex> getNeighbours(String vertexName) {
        return getNeighboursByCall(vertexName);
    }

    @Override
    public int getNeighboursNum(String vertexName) {
        return getNeighbours(vertexName).size();
    }

    @Override
    public Collection<CodeEdge> getEdges(String vertexName) {
        List<CodeEdge> edges = new ArrayList<>();
        for (CodeEdge e : getInEdges(vertexName)) {
            edges.add(e);
        }

        for (CodeEdge e : getOutEdges(vertexName)) {
            edges.add(e);
        }

        return edges;
    }

    public List<CodeEdge> getInEdges(String vertexName) {
        List<CodeEdge> classEdgeList = new ArrayList<>();
        for (CodeEdge e : dirGraph.getInEdges(nameCodeVertexMap.get(vertexName))) {
            classEdgeList.add(e);
        }

        return classEdgeList;
    }

    public List<CodeEdge> getOutEdges(String vertexName) {
        List<CodeEdge> classEdgeList = new ArrayList<>();
        for (CodeEdge e : dirGraph.getOutEdges(nameCodeVertexMap.get(vertexName))) {
            classEdgeList.add(e);
        }
        return classEdgeList;
    }

    @Override
    public List<CodeEdge> getCallEdges() {
        List callEdges = new ArrayList();
        for (CodeEdge edge : dirGraph.getEdges()) {
            callEdges.add(edge);
        }
        return callEdges;
    }

    public List<CodeVertex> getNeighboursByCall(String vertexName) {
        List<CodeVertex> vertexesList = new ArrayList<>();
        CodeVertex vertex = nameCodeVertexMap.get(vertexName);

        if (vertex == null) {
//            System.out.println(vertexName + " haha");
            return vertexesList;
        }
        for (CodeVertex v : getFathersByCall(vertex.getName())) {
            vertexesList.add(v);
        }

        for (CodeVertex v : getChildrenByCall(vertex.getName())) {
            vertexesList.add(v);
        }
        return vertexesList;
    }


    public List<CodeVertex> getChildrenByCall(String vertexName) {
        List<CodeVertex> vertexesList = new ArrayList<>();
        CodeVertex vertex = nameCodeVertexMap.get(vertexName);

        if (dirGraph.getOutEdges(vertex) == null) {
            return vertexesList;
        }

        for (CodeEdge edge : dirGraph.getOutEdges(vertex)) {
            vertexesList.add(dirGraph.getDest(edge));
        }
        return vertexesList;
    }

    public List<CodeVertex> getFathersByCall(String vertexName) {
        List<CodeVertex> vertexesList = new ArrayList<>();
        CodeVertex vertex = nameCodeVertexMap.get(vertexName);

        // attention !!
        if (dirGraph.getInEdges(vertex) == null) {
            return vertexesList;
        }

        for (CodeEdge edge : dirGraph.getInEdges(vertex)) {
            vertexesList.add(dirGraph.getEndpoints(edge).getFirst());
        }
        return vertexesList;
    }

    public void searhNeighbourVertexByCall(CodeVertex codeVertex, List<CodeVertex> connectedVertexes) {
        searhNeighbourVertex(codeVertex, connectedVertexes, dirGraph);
    }

    private void searhNeighbourVertex(CodeVertex codeVertex, List<CodeVertex> connectedVertexes, Graph graph) {
        if (graph.getNeighbors(codeVertex) == null) {
            System.out.println(codeVertex.getName());
        }

        List<CodeVertex> nbs = new ArrayList<>(graph.getNeighbors(codeVertex));
        if (nbs.size() == 0) return;

        for (CodeVertex n : nbs) {
            if (!connectedVertexes.contains(n)) {
                connectedVertexes.add(n);
            }
        }
    }

    private void normalizeCallEdge(Map<CodeEdge, Double> codeEdgeScoreMap) {
        callEdgeScoreValues = new ArrayList<>();
        for (Double v : codeEdgeScoreMap.values()) {
            callEdgeScoreValues.add(v);

        }

        Collections.sort(callEdgeScoreValues, Collections.reverseOrder());

        Double max = callEdgeScoreValues.get(0);
        Double min = callEdgeScoreValues.get(callEdgeScoreValues.size() - 1);

        for (CodeEdge edge : codeEdgeScoreMap.keySet()) {
            Double value = codeEdgeScoreMap.get(edge);
            codeEdgeScoreMap.put(edge, ((value - min) / (max - min)));
        }
    }

    private void removeCallEdgesBelowCallEdgeScoreThreshold(double callEdgeScoreThreshold) {
        callEdgeScoreMap = new HashMap<>();
        for (CodeEdge codeEdge : dirGraph.getEdges()) {
            CallEdge callEdge = (CallEdge) codeEdge;
            callEdgeScoreMap.put(callEdge, computeCallEdgeScore(callEdge));

//            System.out.println(computeCallEdgeScore(callEdge));
        }

//        normalizeCallEdge(callEdgeScoreMap);


        for (CodeEdge callEdge : callEdgeScoreMap.keySet()) {
            if (callEdgeScoreMap.get(callEdge) < callEdgeScoreThreshold) {
                dirGraph.removeEdge(callEdge);
            }
        }
    }

    private double computeCallEdgeScore(CallEdge callEdge) {
        int callerOutNum = (dirGraph.getOutEdges(callEdge.getSource())).size();
        int calleeInNum = (dirGraph.getInEdges(callEdge.getTarget())).size();
        return callEdge.getCallRelationSize() * 2.0 / (callerOutNum + calleeInNum);
    }

    public void searhNeighbourConnectedGraphByCall(String vertexName, List<CodeVertex> connectedVertexes) {
        CodeVertex vertex = getCodeVertexByName(vertexName);
        if (vertex == null) {
//            System.out.println("Can't find vertex for artifact: " + vertexName);
            return;
        }
        searhNeighbourConnectedGraphByCall(vertex, connectedVertexes);
    }

    public void searhNeighbourConnectedGraphByCall(CodeVertex codeVertex, List<CodeVertex> connectedVertexes) {
        searhGraphVertexBelongsTo(codeVertex, connectedVertexes, dirGraph);
        connectedVertexes.remove(codeVertex);
    }

    private void searhGraphVertexBelongsTo(CodeVertex codeVertex, List<CodeVertex> connectedVertexes, Graph graph) {
        if (!connectedVertexes.contains(codeVertex)) {
            connectedVertexes.add(codeVertex);
        }

        if (graph.getNeighbors(codeVertex) == null) {
            System.out.println(codeVertex.getName());
        }
        List<CodeVertex> nbs = new ArrayList<>(graph.getNeighbors(codeVertex));
        if (nbs.size() == 0) return;

        for (CodeVertex n : nbs) {
            if (!connectedVertexes.contains(n)) {
                searhGraphVertexBelongsTo(n, connectedVertexes, graph);
            }
        }
    }

    public void computeVertexIDF() {
        Collection<CodeVertex> vertexs = dirGraph.getVertices();
        int vertexNum = dirGraph.getVertexCount();
        for (CodeVertex cv : vertexs) {
//            int neighboursNum = getNeighboursNum(cv.getName());

            int neighboursNum = getFathersByCall(cv.getName()).size();
            if (neighboursNum <= 0) {
                vertexIDF.put(cv.getName(), 0.0);
            } else {
                vertexIDF.put(cv.getName(), Math.log(vertexNum / neighboursNum));
            }
        }
    }

    public void showMethodIDF() {
        System.out.println("---------------------------------");
        LinksList list = new LinksList();
        System.out.println("IDF");
        for (String vertex : vertexIDF.keySet()) {
            list.add(new SingleLink("IDF", vertex, vertexIDF.get(vertex)));
        }

        Collections.sort(list, Collections.reverseOrder());
        System.out.println(list);
        System.out.println("---------------------------------");
    }

    public boolean isMethodAboveThreshold(String method) {
        if (vertexIDF.get(method) == null) {
//            System.out.println(("Can't find idf for " + method));
//            System.out.println("Has it no call relation ?");
            return true;
        } else {
            return vertexIDF.get(method) > -1;
        }
    }
}

