package de.blazemcworld.fireflow.code;

import de.blazemcworld.fireflow.code.widget.LineElement;
import de.blazemcworld.fireflow.code.widget.NodeWidget;
import de.blazemcworld.fireflow.code.widget.Widget;
import de.blazemcworld.fireflow.code.widget.WireWidget;
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

        HashMap<Widget, Vec> sizeCache = new HashMap<>();
        List<Double> xChanges = new ArrayList<>();
        List<Double> yChanges = new ArrayList<>();

        for (Widget w : editor.rootWidgets) {
            if (!(w instanceof NodeWidget)) continue;

            Vec size = sizeCache.computeIfAbsent(w, Widget::getSize);
            Vec pos = w.getPos();
            xChanges.add(pos.x() + 0.25);
            xChanges.add(pos.x() - size.x() - 0.25);
            yChanges.add(pos.y() + 0.25);
            yChanges.add(pos.y() - size.y() - 0.25);
        }

        List<LineElement> wires = new ArrayList<>();
        for (Widget w : editor.rootWidgets) {
            if (!(w instanceof WireWidget ww)) continue;
            if (!ww.isValid()) continue;
            wires.add(ww.line);
            xChanges.add(ww.line.from.x() + 0.125);
            xChanges.add(ww.line.from.x() - 0.125);
            yChanges.add(ww.line.from.y() + 0.125);
            yChanges.add(ww.line.from.y() - 0.125);

            xChanges.add(ww.line.to.x() + 0.125);
            xChanges.add(ww.line.to.x() - 0.125);
            yChanges.add(ww.line.to.y() + 0.125);
            yChanges.add(ww.line.to.y() - 0.125);
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
            for (double xChange : xChanges) {
                Vec v = new Vec(xChange, current.y(), 0);
                if (!isValid(current, v, sizeCache)) continue;
                List<Vec> newPath = new ArrayList<>(path);
                newPath.add(v);
                double p = computePenalty(newPath, wires) + v.distanceSquared(end);
                if (paths.containsKey(v) && penalty.get(paths.get(v)) <= p) continue;
                paths.put(v, newPath);
                todo.add(v);
                penalty.put(newPath, p);
            }

            for (double yChange : yChanges) {
                Vec v = new Vec(current.x(), yChange, 0);
                if (!isValid(current, v, sizeCache)) continue;
                List<Vec> newPath = new ArrayList<>(path);
                newPath.add(v);
                double p = computePenalty(newPath, wires) + v.distanceSquared(end);
                if (paths.containsKey(v) && penalty.get(paths.get(v)) <= p) continue;
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

    private int computePenalty(List<Vec> path, List<LineElement> wires) {
        int penalty = 0;

        for (int i = 1; i < path.size(); i++) {
            Vec from = path.get(i - 1);
            Vec to = path.get(i);

            if (from.y() == to.y()) {
                if (i == 1 || i == path.size() - 1) continue;
            }
            penalty++;
        }

        penalty *= 100;

        for (LineElement w : wires) {
            for (int i = 1; i < path.size(); i++) {
                Vec from = path.get(i - 1);
                Vec to = path.get(i);
                if (from.x() == to.x() && w.from.x() == w.to.x() && from.x() == w.from.x()) {
                    double pathMin = Math.min(from.y(), to.y());
                    double pathMax = Math.max(from.y(), to.y());
                    double wireMin = Math.min(w.from.y(), w.to.y());
                    double wireMax = Math.max(w.from.y(), w.to.y());

                    if (pathMax >= wireMin && pathMin <= wireMax) {
                        penalty++;
                    }
                } else if (from.y() == to.y() && w.from.y() == w.to.y() && from.y() == w.from.y()) {
                    double pathMin = Math.min(from.x(), to.x());
                    double pathMax = Math.max(from.x(), to.x());
                    double wireMin = Math.min(w.from.x(), w.to.x());
                    double wireMax = Math.max(w.from.x(), w.to.x());

                    if (pathMax >= wireMin && pathMin <= wireMax) {
                        penalty++;
                    }
                }
            }
        }

        return penalty * 100;
    }

    private boolean isValid(Vec from, Vec to, HashMap<Widget, Vec> sizeCache) {
        Vec maxPath = from.max(to);
        Vec minPath = from.min(to);
        for (Widget w : editor.rootWidgets) {
            if (!(w instanceof NodeWidget)) continue;

            Vec maxWall = w.getPos();
            Vec minWall = maxWall.sub(sizeCache.computeIfAbsent(w, Widget::getSize));
            if (minPath.x() > maxWall.x() || minPath.y() > maxWall.y()) continue;
            if (maxPath.x() < minWall.x() || maxPath.y() < minWall.y()) continue;
            return false;
        }
        return true;
    }
}
