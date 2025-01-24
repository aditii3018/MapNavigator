package com.example.mapnavigator;

import java.util.*;

public class Graph {
    private final Map<String, Map<String, Integer>> edges = new HashMap<>();
    public void addEdge(String source, String destination, int weight) {
        edges.computeIfAbsent(source, k -> new HashMap<>()).put(destination, weight);
        edges.computeIfAbsent(destination, k -> new HashMap<>()).put(source, weight);
    }
    public Map<String, Integer> getEdges(String node) {
        return edges.getOrDefault(node, new HashMap<>());
    }
    public Set<String> getNodes() {
        return edges.keySet();
    }
    public boolean deleteEdge(String source, String destination) {
        if (edges.containsKey(source) && edges.get(source).containsKey(destination)) {
            edges.get(source).remove(destination);
            edges.get(destination).remove(source);
            return true;
        }
        return false;
    }
    public List<String> dijkstraShortestPath(String source, String destination) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> nodes = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        for (String node : edges.keySet()) {
            if (node.equals(source)) {
                distances.put(node, 0);
            } else {
                distances.put(node, Integer.MAX_VALUE);
            }
            nodes.add(node);
        }
        while (!nodes.isEmpty()) {
            String closest = nodes.poll();
            if (closest.equals(destination)) {
                List<String> path = new ArrayList<>();
                for (String at = destination; at != null; at = previous.get(at)) {
                    path.add(at);
                }
                Collections.reverse(path);
                return path;
            }

            if (distances.get(closest) == Integer.MAX_VALUE) {
                break;
            }
            for (Map.Entry<String, Integer> neighbor : edges.get(closest).entrySet()) {
                int alt = distances.get(closest) + neighbor.getValue();
                if (alt < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), alt);
                    previous.put(neighbor.getKey(), closest);
                    nodes.remove(neighbor.getKey());
                    nodes.add(neighbor.getKey());
                }
            }
        }
        return new ArrayList<>();
    }
    public List<List<String>> findAllPaths(String source, String destination) {
        List<List<String>> allPaths = new ArrayList<>();
        findAllPathsUtil(source, destination, new HashSet<>(), new ArrayList<>(), allPaths);
        return allPaths;
    }
    private void findAllPathsUtil(String current, String destination, Set<String> visited, List<String> path, List<List<String>> allPaths) {
        visited.add(current);
        path.add(current);
        if (current.equals(destination)) {
            allPaths.add(new ArrayList<>(path));
        } else {
            for (String neighbor : edges.get(current).keySet()) {
                if (!visited.contains(neighbor)) {
                    findAllPathsUtil(neighbor, destination, visited, path, allPaths);
                }
            }
        }
        path.remove(path.size() - 1);
        visited.remove(current);
    }
}
