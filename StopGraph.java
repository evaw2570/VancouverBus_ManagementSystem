import java.util.List;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;

public class StopGraph<T extends Comparable<T>> {
    public enum State {
        UNVISITED, VISITED, COMPLETE
    };

    private ArrayList<Stop> stops;
    private ArrayList<Edge> edges;

    public StopGraph() {
        stops = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public void add(T from, T to, double cost) {
        Edge temp = findEdge(from, to);
        if (temp != null) {
            // System.out.println("Edge " + from + "," + to + " already exists. Changing
            // cost.");
            // temp.cost = cost;
        } else {
            // this will also create the stops
            Edge e = new Edge(from, to, cost);
            edges.add(e);
        }
    }

    private Stop findStop(T v) {
        for (Stop each : stops) {
            if (each.value.compareTo(v) == 0)
                return each;
        }
        return null;
    }

    private Edge findEdge(Stop v1, Stop v2) {
        for (Edge each : edges) {
            if (each.from.equals(v1) && each.to.equals(v2)) {
                return each;
            }
        }
        return null;
    }

    private Edge findEdge(T from, T to) {
        for (Edge each : edges) {
            if (each.from.value.equals(from) && each.to.value.equals(to)) {
                return each;
            }
        }
        return null;
    }

    private void clearStates() {
        for (Stop each : stops) {
            each.state = State.UNVISITED;
        }
    }

    public boolean isConnected() {
        for (Stop each : stops) {
            if (each.state != State.COMPLETE)
                return false;
        }
        return true;
    }

    public boolean DepthFirstSearch() {
        if (stops.isEmpty())
            return false;

        clearStates();

        Stop root = stops.get(0);
        if (root == null)
            return false;

        DepthFirstSearch(root);
        return isConnected();
    }

    private void DepthFirstSearch(Stop v) {
        v.state = State.VISITED;

        for (Stop each : v.outgoing) {
            if (each.state == State.UNVISITED) {
                DepthFirstSearch(each);
            }
        }
        v.state = State.COMPLETE;
    }

    public boolean BreadthFirstSearch() {
        if (stops.isEmpty())
            return false;
        clearStates();

        Stop root = stops.get(0);
        if (root == null)
            return false;

        Queue<Stop> queue = new LinkedList<>();
        queue.add(root);
        root.state = State.COMPLETE;

        while (!queue.isEmpty()) {
            root = queue.peek();
            for (Stop each : root.outgoing) {

                if (each.state == State.UNVISITED) {
                    each.state = State.COMPLETE;
                    queue.add(each);
                }
            }
            queue.remove();
        }
        return isConnected();
    }

    public boolean BreadthFirstSearch(T v1) {
        if (stops.isEmpty())
            return false;
        clearStates();

        Stop root = findStop(v1);
        if (root == null)
            return false;

        Queue<Stop> queue = new LinkedList<>();
        queue.add(root);
        root.state = State.COMPLETE;

        while (!queue.isEmpty()) {
            root = queue.peek();
            for (Stop each : root.outgoing) {
                if (each.state == State.UNVISITED) {
                    each.state = State.COMPLETE;
                    queue.add(each);
                }
            }
            queue.remove();
        }
        return isConnected();
    }

    private boolean Dijkstra(T v1) {
        if (stops.isEmpty())
            return false;

        resetDistances();

        Stop source = findStop(v1);
        if (source == null)
            return false;

        source.minDistance = 0;
        PriorityQueue<Stop> pq = new PriorityQueue<>();
        pq.add(source);

        while (!pq.isEmpty()) {
            Stop u = pq.poll();

            for (Stop v : u.outgoing) {
                Edge e = findEdge(u, v);
                if (e == null)
                    return false;
                double totalDistance = u.minDistance + e.cost;
                if (totalDistance < v.minDistance) {
                    pq.remove(v);
                    v.minDistance = totalDistance;

                    v.previous = u;
                    pq.add(v);
                }
            }
        }
        return true;
    }

    private List<String> getShortestPath(Stop target) {
        List<String> path = new ArrayList<String>();

        if (target.minDistance == Integer.MAX_VALUE) {
            path.add("No path found");
            return path;
        }

        for (Stop v = target; v != null; v = v.previous) {
            path.add(v.value + " : cost : " + v.minDistance);
        }

        Collections.reverse(path);
        return path;
    }

    private void resetDistances() {
        for (Stop each : stops) {
            each.minDistance = Integer.MAX_VALUE;
            each.previous = null;
        }
    }

    public List<String> getPath(T from, T to) {
        boolean test = Dijkstra(from);
        if (test == false)
            return null;
        List<String> path = getShortestPath(findStop(to));
        return path;
    }

    @Override
    public String toString() {
        String retval = "";
        for (Stop each : stops) {
            retval += each.toString() + "\n";
        }
        return retval;
    }

    public String edgesToString() {
        String retval = "";
        for (Edge each : edges) {
            retval += each + "\n";
        }
        return retval;
    }

    class Stop implements Comparable<Stop> {
        T value;

        Stop previous = null;
        double minDistance = Integer.MAX_VALUE;

        List<Stop> incoming;
        List<Stop> outgoing;
        State state;

        public Stop(T value) {
            this.value = value;
            incoming = new ArrayList<>();
            outgoing = new ArrayList<>();
            state = State.UNVISITED;
        }

        @Override
        public int compareTo(Stop other) {
            return Double.compare(minDistance, other.minDistance);
        }

        public void addIncoming(Stop vert) {
            incoming.add(vert);
        }

        public void addOutgoing(Stop vert) {
            outgoing.add(vert);
        }

        @Override
        public String toString() {
            String retval = "";
            retval += "Stop: " + value + " : ";
            retval += " In: ";
            for (Stop each : incoming)
                retval += each.value + " ";
            retval += "Out: ";
            for (Stop each : outgoing)
                retval += each.value + " ";
            return retval;
        }
    }

    class Edge {
        Stop from;
        Stop to;
        double cost;

        public Edge(T v1, T v2, double cost) {
            from = findStop(v1);
            if (from == null) {
                from = new Stop(v1);
                stops.add(from);
            }
            to = findStop(v2);
            if (to == null) {
                to = new Stop(v2);
                stops.add(to);
            }
            this.cost = cost;

            from.addOutgoing(to);
            to.addIncoming(from);
        }

        @Override
        public String toString() {
            return "Edge From: " + from.value + " to: " + to.value + " cost: " + cost;
        }
    }
}