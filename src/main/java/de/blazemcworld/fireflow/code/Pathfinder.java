package de.blazemcworld.fireflow.code;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.blazemcworld.fireflow.code.widget.NodeWidget;
import de.blazemcworld.fireflow.code.widget.Widget;
import net.minestom.server.coordinate.Vec;

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
        xChanges.add(end.x());
        yChanges.add(end.y());

        xChanges.sort(Double::compareTo);
        yChanges.sort(Double::compareTo);

        List<Vec> todo = new ArrayList<>();
        HashMap<Vec, List<Vec>> paths = new HashMap<>();
        todo.add(start);
        paths.put(start, new ArrayList<>());

        Vec lambdaEnd = end;

        while (todo.size() > 0) {
            computeLimit--;
            if (computeLimit <= 0) break;
            todo.sort(Comparator.comparingDouble(v -> paths.get(v).size() * 10000 + v.distanceSquared(lambdaEnd)));

            Vec current = todo.remove(0);

            List<Vec> path = paths.get(current);
            if (current.equals(end)) {
                path.add(0, start);
                path.add(current);
                return path;
            }
            if (path.size() >= lengthLimit) continue;
            List<Vec> newPath = new ArrayList<>(path);
            newPath.add(current);

            for (int j = 0; j < xChanges.size(); j++) {
                Vec v = new Vec(xChanges.get(j), current.y(), 0);
                if (!isValid(current, v, sizeCache)) continue;
                if (paths.containsKey(v) && paths.get(v).size() <= newPath.size()) continue;
                paths.put(v, newPath);
                todo.add(v);
            }

            for (int j = 0; j < yChanges.size(); j++) {
                Vec v = new Vec(current.x(), yChanges.get(j), 0);
                if (!isValid(current, v, sizeCache)) continue;
                if (paths.containsKey(v) && paths.get(v).size() <= newPath.size()) continue;
                paths.put(v, newPath);
                todo.add(v);
            }
        }

        List<Vec> path = new ArrayList<>();
        path.add(start);
        path.add(start.withX(Math.round(start.x() + end.x() * 4) / 8));
        path.add(end.withX(Math.round(start.x() + end.x() * 4) / 8));
        path.add(end);
        return path;
    }

    private boolean isValid(Vec from, Vec to, HashMap<Widget, Vec> sizeCache) {
        for (Widget w : editor.rootWidgets) {
            if (!(w instanceof NodeWidget)) continue;

            Vec maxWall = w.getPos();
            Vec minWall = maxWall.sub(sizeCache.computeIfAbsent(w, Widget::getSize));
            Vec maxPath = from.max(to);
            Vec minPath = from.min(to);
            if (minPath.x() > maxWall.x() || minPath.y() > maxWall.y()) continue;
            if (maxPath.x() < minWall.x() || maxPath.y() < minWall.y()) continue;
            return false;
        }
        return true;
    }
}
