package pp.muza.universe.extensions.detector;

import pp.muza.universe.body.Body;

import java.util.*;

public class BodyGraph {

    private final List<Set<Body>> clusters = new ArrayList<>();
    private final Map<Body, Set<Body>> bodyMap = new HashMap<>();
    private int size = 0;

    public BodyGraph() {
    }

    public int getSize() {
        return size;
    }

    public int getClustersCount() {
        return clusters.size();
    }

    public Collection<Body> getCluster(int i) {
        // return immutable copy
        return Collections.unmodifiableCollection(clusters.get(i));
    }

    public Collection<Body> getBodySet(Body body) {
        // return immutable copy
        Set<Body> set = bodyMap.get(body);
        return Collections.unmodifiableCollection(set);
    }

    public void clear() {
        clusters.clear();
        bodyMap.clear();
    }

    public boolean addBodyPair(Body body1, Body body2) {
        boolean result = false;
        assert body1 != null;
        assert body2 != null;
        Set<Body> set1 = bodyMap.get(body1);
        Set<Body> set2 = bodyMap.get(body2);
        if (set1 == null && set2 == null) {
            Set<Body> set = new HashSet<>();
            set.add(body1);
            size++;
            if (set.add(body2)) {
                size++;
                result = true;
            }
            clusters.add(set);
            bodyMap.put(body1, set);
            bodyMap.put(body2, set);
        } else if (set1 == null) {
            if (set2.add(body1)) {
                size++;
                result = true;
            }
            bodyMap.put(body1, set2);
        } else if (set2 == null) {
            if (set1.add(body2)) {
                size++;
                result = true;
            }
            bodyMap.put(body2, set1);
        } else if (set1 != set2) {
            set1.addAll(set2);
            clusters.remove(set2);
            for (Body body : set2) {
                if (bodyMap.put(body, set1) != set1) {
                    size++;
                    result = true;
                }
            }
        } else if ((set1 == set2) && (set1 != null)) {
            if (set1.add(body1)) {
                size++;
                result = true;
            }
            if (set1.add(body2)) {
                size++;
                result = true;
            }
        } else {
            throw new IllegalStateException("Unexpected state");
        }
        return result;
    }
}
