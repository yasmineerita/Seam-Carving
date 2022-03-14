package seamcarving;
import astar.AStarGraph;
import astar.WeightedEdge;
import edu.princeton.cs.algs4.Picture;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AStarSeamCarver implements SeamCarver {
    private Picture picture;
    private boolean vertical = true;
    private WeightedGraph graph;




    public AStarSeamCarver(Picture picture) {
        if (picture == null) {
            throw new NullPointerException("Picture cannot be null.");
        }
        this.picture = new Picture(picture);
        this.graph = new WeightedGraph();
    }

    public Picture picture() {
        return new Picture(picture);
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public Color get(int x, int y) {
        return picture.get(x, y);
    }

    public int[] findHorizontalSeam() {
        double[][] disTo = new double[width()][height()];
        int[][] edgeTo = new int[width()][height()];
        Point point;
        int[] seam = new int[width()];
        double minEnergy = Double.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i <= width() - 1; i++) {
            for (int j = 0; j <= height() - 1; j++) {
                point = new Point(i, j);
                vertical = false;
                if (disTo[i][j] == 0.0) {
                    disTo[i][j] = energy(i, j);
                }
                for (WeightedEdge<Point> neighbor : graph.neighbors(point)) {
                    Point p = neighbor.from();
                    Point q = neighbor.to();

                    if (disTo[q.x][q.y] == 0.0){
                        disTo[q.x][q.y] = disTo[p.x][p.y] + energy(q.x, q.y);
                        edgeTo[q.x][q.y] = p.y;
                    } else {
                        if (disTo[p.x][p.y] + energy(q.x, q.y) < disTo[q.x][q.y]) {
                            disTo[q.x][q.y] = disTo[p.x][p.y] + energy(q.x, q.y);
                            edgeTo[q.x][q.y] = p.y;
                        }
                    }
                }
            }
        }
        for (int i = 0; i <= height() - 1; i++){
            if (disTo[width()-1][i] < minEnergy){
                minEnergy = disTo[width()-1][i];
                minIndex = i;
            }
        }
        for (int i = width() - 1; i >= 0; i--){
            seam[i] = minIndex;
            minIndex = edgeTo[i][minIndex];
        }
        return seam;

    }

    public int[] findVerticalSeam() {
        double[][] disTo = new double[width()][height()];
        int[][] edgeTo = new int[width()][height()];
        Point point;
        int[] seam = new int[height()];
        double minEnergy = Double.MAX_VALUE;
        int minIndex = 0;
        for (int j = 0; j <= height() - 1; j++) {
            for (int i = 0; i <= width() - 1; i++) {
                point = new Point(i, j);
                if (disTo[i][j] == 0.0) {
                    disTo[i][j] = energy(i, j);
                }
                vertical = true;
                for (WeightedEdge<Point> neighbor : graph.neighbors(point)) {
                    Point p = neighbor.from();
                    Point q = neighbor.to();
                    if (disTo[q.x][q.y] == 0.0){
                        disTo[q.x][q.y] = disTo[p.x][p.y] + energy(q.x, q.y);
                        edgeTo[q.x][q.y] = p.x;
                    } else {
                        if (disTo[p.x][p.y] + energy(q.x, q.y) < disTo[q.x][q.y]) {
                            disTo[q.x][q.y] = disTo[p.x][p.y] + energy(q.x, q.y);
                            edgeTo[q.x][q.y] = p.x;
                        }
                    }
                }
            }
        }
            for (int i = 0; i <= width() - 1; i++){
                if (disTo[i][height()-1] < minEnergy){
                    minEnergy = disTo[i][height()-1];
                    minIndex = i;
                }
            }
            for (int i = height() - 1; i >= 0; i--){
                seam[i] = minIndex;
                minIndex = edgeTo[minIndex][i];
        }
            return seam;

    }

    private class WeightedGraph implements AStarGraph<Point> {
        private Point[][] point;

        public WeightedGraph() {
            this.point = new Point[width()][height()];

            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    point[i][j] = new Point(i, j);
                }
            }
        }
        @Override
        public List<WeightedEdge<Point>> neighbors(Point pixel) {
            List<WeightedEdge<Point>> list = new ArrayList<>();
            //vertical
            Point leftPixel = null;
            Point rightPixel = null;
            Point downPixel = null;
            //horizontal
            Point upPixel = null;
            Point bottomPixel = null;
            Point nextPixel = null;

            if (inBounds(pixel.x, pixel.y)) {
                if (vertical && pixel.y != height() - 1) {
                    downPixel = point[pixel.x][pixel.y + 1];
                    WeightedEdge<Point> downNeighbor =
                            new WeightedEdge<>(pixel, downPixel, energy(pixel.x, pixel.y + 1));
                    list.add(downNeighbor);
                    if (pixel.x != 0) {
                        leftPixel = point[pixel.x - 1][pixel.y + 1];
                        WeightedEdge<Point> leftNeighbor =
                                new WeightedEdge<>(pixel, leftPixel, energy(pixel.x - 1, pixel.y + 1));
                        list.add(leftNeighbor);
                    }
                    if (pixel.x != width() - 1) {
                        rightPixel = point[pixel.x + 1][pixel.y + 1];
                        WeightedEdge<Point> rightNeighbor =
                                new WeightedEdge<>(pixel, rightPixel, energy(pixel.x + 1, pixel.y + 1));
                        list.add(rightNeighbor);
                    }
                } else if (!vertical && pixel.x != width() - 1){
                    nextPixel = point[pixel.x + 1][pixel.y];
                    WeightedEdge<Point> nextNeighbor =
                            new WeightedEdge<>(pixel, nextPixel, energy(pixel.x + 1, pixel.y));
                    list.add(nextNeighbor);
                    if (pixel.y != 0) {
                        upPixel = point[pixel.x + 1][pixel.y - 1];
                        WeightedEdge<Point> upNeighbor =
                                new WeightedEdge<>(pixel, upPixel, energy(pixel.x + 1, pixel.y - 1));
                        list.add(upNeighbor);
                    }
                    if (pixel.y != height() - 1) {
                        bottomPixel = point[pixel.x + 1][pixel.y + 1];
                        WeightedEdge<Point> bottomNeighbor =
                                new WeightedEdge<>(pixel, bottomPixel, energy(pixel.x + 1, pixel.y + 1));
                        list.add(bottomNeighbor);
                    }
                }
            }
            return list;
        }
        @Override
        public double estimatedDistanceToGoal(Point s, Point goal) {
            return 0;
        }
    }
    private class Point {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }


    }
}
