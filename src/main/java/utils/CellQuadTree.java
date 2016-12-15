package utils;

import model.Cell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by xakep666 on 15.12.16.
 * <p>
 * Quandrants tree to store cell. Simplifies collision detection
 */
public class CellQuadTree {
    private static final int dims = 4;
    private int level;
    @NotNull
    private Rectangle range;
    private int maxNodes;
    private int maxLevel;
    @NotNull
    private volatile List<Cell> nodes = new CopyOnWriteArrayList<>();
    @NotNull
    private volatile List<CellQuadTree> branches = new CopyOnWriteArrayList<>();

    private CellQuadTree(@NotNull CellQuadTree parent, @NotNull Rectangle range, int maxNodes, int maxLevel) {
        this.range = range;
        this.maxNodes = maxNodes;
        this.maxLevel = maxLevel;
        this.level = parent.level + 1;
    }

    public CellQuadTree(@NotNull Rectangle range, int maxNodes, int maxLevel) {
        this.range = range;
        this.maxNodes = maxNodes;
        this.maxLevel = maxLevel;
        this.level = 1;
    }

    private static List<Rectangle> splitRect(@NotNull Rectangle rect) {
        int newW = rect.width / 2, newH = rect.height / 2;
        int x = rect.x, y = rect.y;
        List<Rectangle> ret = new ArrayList<>(dims);
        ret.add(0, new Rectangle(x - newW, y - newH, newW, newH));
        ret.add(1, new Rectangle(x + newW, y + newW, newW, newH));
        ret.add(2, new Rectangle(x - newW, y + newH, newW, newH));
        ret.add(3, new Rectangle(x + newW, y + newW, newW, newH));
        return ret;
    }

    public synchronized boolean add(@NotNull Cell cell) {
        return add(cell, true);
    }

    private boolean add(@NotNull Cell cell, boolean split) {
        if (!range.intersects(cell.getRange())) return false;

        if (cell.getNode() != null) return false;

        if (!branches.isEmpty()) {
            for (int i = 0; i < dims; i++) {
                if (branches.get(i).add(cell, true)) return true;
            }
            return false;
        } else {
            nodes.add(cell);
            cell.setNode(this);
            if (split) split();
            return true;
        }
    }

    public synchronized boolean add(@NotNull List<Cell> cells) {
        return add(cells, true);
    }

    private boolean add(@NotNull List<Cell> cells, boolean split) {
        boolean result = true;
        for (Cell cell : cells) {
            result &= add(cell, split);
        }
        return result;
    }

    public synchronized void remove(@NotNull Cell cell) {
        remove(cell, true);
    }

    private void remove(@NotNull Cell cell, boolean merge) {
        if (cell.getNode() == null) return;

        if (cell.getNode().nodes.contains(cell))
            cell.getNode().nodes.remove(cell);

        if (merge) merge();
        cell.setNode(null);
    }

    public void remove(@NotNull List<Cell> cells) {
        remove(cells, true);
    }

    private void remove(@NotNull List<Cell> cells, boolean merge) {
        cells.forEach(c -> remove(c, merge));
    }

    public synchronized void update(@NotNull Cell cell) {
        remove(cell, false);
        add(cell, false);
    }

    private void split() {
        if (!branches.isEmpty()) return;
        if (nodes.size() < maxNodes || level >= maxLevel - 1) return;

        //split to 4 rectangles
        List<Rectangle> splittedRects = splitRect(range);
        List<CellQuadTree> newBranches = splittedRects.stream()
                .map(r -> new CellQuadTree(this, r, maxNodes, maxLevel))
                .collect(Collectors.toList());
        nodes.forEach(node -> {
            for (int i = 0; i < splittedRects.size(); i++) {
                if (splittedRects.get(i).intersects(node.getRange())) {
                    newBranches.get(i).nodes.add(node);
                    node.setNode(newBranches.get(i));
                    break;
                }
            }
        });
        clear();
        branches = newBranches;
    }

    private void merge() {
        if (branches.isEmpty()) return;
        List<Cell> cells = getNodes();
        if (cells.size() >= maxNodes) return;

        clear();
        add(cells, false);
    }

    @NotNull
    private List<Cell> getNodes() {
        if (branches.isEmpty()) return new ArrayList<>(nodes);
        List<Cell> ret = new ArrayList<>();
        branches.forEach(cqt -> ret.addAll(cqt.getNodes()));
        return ret;
    }

    private void clear() {
        nodes.clear();
        branches.forEach(CellQuadTree::clear);
        branches.clear();
    }

    @NotNull
    public synchronized List<Cell> query(@NotNull Rectangle range, @Nullable Function<Cell, Boolean> predicate) {
        List<Cell> cells = new ArrayList<>();
        if (!branches.isEmpty()) {
            branches.forEach(b -> cells.addAll(b.query(range, predicate)));
        } else {
            nodes.forEach(node -> {
                if (range.intersects(node.getRange())) {
                    if (predicate == null) cells.add(node);
                    else if (predicate.apply(node)) cells.add(node);
                }
            });
        }
        return cells;
    }
}
