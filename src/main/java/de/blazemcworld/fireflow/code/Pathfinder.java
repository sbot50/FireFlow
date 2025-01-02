package de.blazemcworld.fireflow.code;

import de.blazemcworld.fireflow.code.widget.NodeWidget;
import de.blazemcworld.fireflow.code.widget.Widget;
import it.unimi.dsi.fastutil.Pair;
import net.minestom.server.coordinate.Vec;

import java.util.*;

public class Pathfinder {

    private final CodeEditor editor;

    public Pathfinder(CodeEditor editor) {
        this.editor = editor;
    }

    public List<Vec> findPath(Vec start, Vec end, int lengthLimit, int computeLimit) {
        start = start.withZ(0);
        end = end.withZ(0);

        HashMap<Widget, Vec> sizes = new HashMap<>();
        List<Double> xChanges = new ArrayList<>();
        List<Double> yChanges = new ArrayList<>();

        for (Widget w : editor.rootWidgets) {
            if (!(w instanceof NodeWidget)) continue;
            sizes.put(w, w.getSize());

            Vec size = sizes.computeIfAbsent(w, Widget::getSize);
            Vec pos = w.getPos();
            xChanges.add(pos.x() + 0.25);
            xChanges.add(pos.x() - size.x() - 0.25);
            yChanges.add(pos.y() + 0.25);
            yChanges.add(pos.y() - size.y() - 0.25);
        }

        xChanges.add(end.x());
        yChanges.add(end.y());

        xChanges = new ArrayList<>(new HashSet<>(xChanges));
        yChanges = new ArrayList<>(new HashSet<>(yChanges));

        xChanges.sort(Double::compareTo);
        yChanges.sort(Double::compareTo);

        List<Vec> todo = new ArrayList<>();
        HashMap<Vec, List<Vec>> paths = new HashMap<>();
        HashMap<List<Vec>, Double> penalty = new HashMap<>();
        todo.add(start);
        paths.put(start, new ArrayList<>(List.of(start)));
        penalty.put(paths.get(start), 0.0);

        while (!todo.isEmpty()) {
            computeLimit--;
            if (computeLimit <= 0) break;
            todo.sort(Comparator.comparingDouble(v -> penalty.get(paths.get(v))));
            Vec current = todo.removeFirst();

            List<Vec> path = paths.get(current);
            if (current.equals(end)) return path;

            if (path.size() >= lengthLimit) continue;

            Pair<Double, Double> minMax = validXRange(current, sizes);
            for (double xChange : xChanges) {
                if (minMax.left() > xChange || minMax.right() < xChange) continue;
                Vec v = new Vec(xChange, current.y(), 0);
                List<Vec> newPath = new ArrayList<>(path);
                newPath.add(v);
                double p = path.size() * 10000 + v.distanceSquared(end);
                if (paths.containsKey(v) && penalty.get(paths.get(v)) <= p) continue;
                paths.put(v, newPath);
                todo.add(v);
                penalty.put(newPath, p);
            }

            minMax = validYRange(current, sizes);
            for (double yChange : yChanges) {
                if (minMax.left() > yChange || minMax.right() < yChange) continue;
                Vec v = new Vec(current.x(), yChange, 0);
                double p = path.size() * 10000 + v.distanceSquared(end);
                if (paths.containsKey(v) && penalty.get(paths.get(v)) <= p) continue;
                List<Vec> newPath = new ArrayList<>(path);
                newPath.add(v);
                paths.put(v, newPath);
                todo.add(v);
                penalty.put(newPath, p);
            }
        }

        List<Vec> path = new ArrayList<>();
        path.add(start);
        path.add(start.withX(Math.round((start.x() + end.x()) * 4) / 8.0));
        path.add(end.withX(Math.round((start.x() + end.x()) * 4) / 8.0));
        path.add(end);
        return path;
    }

    private Pair<Double, Double> validXRange(Vec current, HashMap<Widget, Vec> sizes) {
        double min = Double.NEGATIVE_INFINITY;
        double max = Double.POSITIVE_INFINITY;
        for (Widget w : editor.rootWidgets) {
            if (!(w instanceof NodeWidget)) continue;

            Vec maxWall = w.getPos();
            Vec minWall = maxWall.sub(sizes.get(w));
            if (current.y() > maxWall.y() || current.y() < minWall.y()) continue;
            if (current.x() < minWall.x()) max = Math.min(max, minWall.x());
            if (current.x() > maxWall.x()) min = Math.max(min, maxWall.x());
        }
        return Pair.of(min, max);
    }

    private Pair<Double, Double> validYRange(Vec current, HashMap<Widget, Vec> sizes) {
        double min = Double.NEGATIVE_INFINITY;
        double max = Double.POSITIVE_INFINITY;
        for (Widget w : editor.rootWidgets) {
            if (!(w instanceof NodeWidget)) continue;

            Vec maxWall = w.getPos();
            Vec minWall = maxWall.sub(sizes.get(w));
            if (current.x() > maxWall.x() || current.x() < minWall.x()) continue;
            if (current.y() < minWall.y()) max = Math.min(max, minWall.y());
            if (current.y() > maxWall.y()) min = Math.max(min, maxWall.y());
        }
        return Pair.of(min, max);
    }
}
