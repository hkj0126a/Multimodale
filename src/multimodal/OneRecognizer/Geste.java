/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimodal.OneRecognizer;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author hakje
 */
public class Geste {

    private List<Point> myInitialPoints;
    private List<Point> myPoints;
    private int longueurTrace;
    private int squareSize;
    private final static int NB_POINTS = 50;
    private final static int INFINITY = 5000000;
    private String command;
    private boolean isLearned = false;

    public Geste() {
        myInitialPoints = new ArrayList<>();
        myPoints = new ArrayList<>();
        squareSize = NB_POINTS;
    }

    public Geste(List<Point> initialPoints, int squareSizep) {
        myInitialPoints = initialPoints;
        myPoints = new ArrayList<>();
        squareSize = squareSizep;
    }

    public Geste(List<Point> initialPoints, int squareSizep, String cmd) {
        this(initialPoints, squareSizep);
        command = cmd;
        isLearned = true;
    }

    public void addPoint(int x, int y) {
        myInitialPoints.add(new Point(x, y));
    }

    public void compute() {
        pathLength();
        //echantillonage
        //resample();
        myPoints = resample(myInitialPoints, NB_POINTS);
        
        //rotation
        myPoints = rotateToZero();
        //recadrage
        myPoints = scaleToSquare(myPoints, squareSize);
    }

    List<Point> resample(List<Point> points, int n) {
        double I = pathLength(points) / ((double) n - 1.0);
        double D = 0.0;
        List<Point> newpoints = new ArrayList<>();
        Stack stack = new Stack();
        for (int i = 0; i < points.size(); i++) {
            stack.push(points.get(points.size() - 1 - i));
        }

        while (!stack.empty()) {
            Point pt1 = (Point) stack.pop();

            if (stack.empty()) {
                newpoints.add(pt1);
                continue;
            }
            
            Point pt2 = (Point) stack.peek();
            double d = pt1.distance(pt2);
            if ((D + d) >= I) {
                int qx = (int)(pt1.x + ((I - D) / d) * (pt2.x - pt1.x));
                int qy = (int)(pt1.y + ((I - D) / d) * (pt2.y - pt1.y));
                
                Point q = new Point(qx, qy);
                
                newpoints.add(q);
                stack.push(q);
                D = 0.0;
            } else {
                D += d;
            }
        }

        if (newpoints.size() == (n - 1)) {
            newpoints.add(points.get(points.size()-1));
        }
        return newpoints;

    }

    public void pathLength() {
        longueurTrace = 0;
        for (int i = 1; i < myInitialPoints.size(); i++) {
            Point prevPoint = myInitialPoints.get(i - 1);
            Point currentPoint = myInitialPoints.get(i);
            longueurTrace += currentPoint.distance(prevPoint.x, prevPoint.y);
        }
    }

    public List<Point> rotateToZero() {
        Point c = Centroid(getMyPoints());
        double angle = Math.atan2(c.y - getMyPoints().get(0).y, c.x - getMyPoints().get(0).x);
        List<Point> myResamplePointsRotated = new ArrayList<>();

        myResamplePointsRotated = rotateBy(getMyPoints(), -angle);

        return myResamplePointsRotated;
    }

    Point Centroid(List<Point> points) {
        Point centriod = new Point(0, 0);
        for (int i = 1; i < points.size(); i++) {
            centriod.x += points.get(i).x;
            centriod.y += points.get(i).y;
        }
        centriod.x /= points.size();
        centriod.y /= points.size();
        return centriod;
    }

    public List<Point> rotateBy(List<Point> points, double angle) {
        Point c = Centroid(points);
        List<Point> newPoints = new ArrayList<>();

        for (Point pt : points) {
            int x = (int) ((pt.x - c.x) * Math.cos(angle)
                    - (pt.y - c.y)
                    * Math.sin(angle) + c.x);
            int y = (int) ((pt.x - c.x) * Math.sin(angle)
                    - (pt.y - c.y)
                    * Math.cos(angle) + c.y);
            newPoints.add(new Point(x, y));
        }

        return newPoints;
    }

    public List<Point> scaleToSquare(List<Point> points, int size) {
        Rectangle B = BoundingBox(points);
        List<Point> newpoints = new ArrayList<>();

        for (Point pt : points) {
            int qx = (int) (pt.x * (size / B.Width));
            int qy = (int) (pt.y * (size / B.Height));

            newpoints.add(new Point(qx, qy));

        }
        for (int i = 0; i < points.size(); i++) {

        }
        return newpoints;
    }

    Rectangle BoundingBox(List<Point> points) {
        float minX = INFINITY;
        float maxX = -INFINITY;
        float minY = INFINITY;
        float maxY = -INFINITY;

        for (Point pt : points) {
            minX = Math.min(pt.x, minX);
            maxX = Math.max(pt.x, maxX);
            minY = Math.min(pt.y, minY);
            maxY = Math.max(pt.y, maxY);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public List<Point> translateToOrigin(List<Point> points) {
        Point c = Centroid(points);
        List<Point> newpoints = new ArrayList<>();

        for (Point pt : points) {
            int qx = pt.x - c.x;
            int qy = pt.y - c.y;
            newpoints.add(new Point(qx, qy));
        }
        return newpoints;
    }

    public void recognize(Point[] points, Object templates) {

    }

    /**
     * @return the myPoints
     */
    public List<Point> getMyPoints() {
        return myPoints;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String cmd) {
        command = cmd;
    }

    public boolean isLearned() {
        return isLearned;
    }

    private double pathLength(List<Point> points) {
        double longueur = 0;
        for (int i = 1; i < points.size(); i++) {
            Point prevPoint = points.get(i - 1);
            Point currentPoint = points.get(i);
            longueur += currentPoint.distance(prevPoint.x, prevPoint.y);
        }
        
        return longueur;
    }

    private class Rectangle {

        float X;
        float Y;
        float Width;
        float Height;

        Rectangle(float x, float y, float width, float height) {
            X = x;
            Y = y;
            Width = width;
            Height = height;
        }
    }

}

/*
package multimodal.OneRecognizer;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;


public class Geste {

    private List<Point> myInitialPoints;
    private List<Point> myPoints;
    private int longueurTrace;
    private int squareSize;
    private final static int NB_POINTS = 50;
    private final static int INFINITY = 5000000;
    private String command;
    private boolean isLearned = false;

    public Geste(List<Point> initialPoints, int squareSizep) {
        myInitialPoints = initialPoints;
        myPoints = new ArrayList<>();
        squareSize = squareSizep;
    }

    public Geste(List<Point> initialPoints, int squareSizep, String cmd) {
        this(initialPoints, squareSizep);
        command = cmd;
        isLearned = true;
    }

    public void addPoint(int x, int y) {
        myInitialPoints.add(new Point(x, y));
    }

    public void compute() {
        pathLength();
        //echantillonage
        resample();
        //rotation
        myPoints.add(Centroid(myPoints));
        myPoints = rotateToZero();
        //recadrage
        Rectangle B = BoundingBox(myPoints);
        myPoints = scaleToSquare(myPoints, squareSize);
        //Centrage
        int panelSize = 200;
        myPoints = translateToOrigin(myPoints, panelSize);
        Point p = new Point((int) B.X, (int) B.Y);
        myPoints.add(p);
        p = new Point((int) (B.X + B.Width), (int) (B.Y + B.Height));
        myPoints.add(p);
        p = new Point((int) B.X, (int) (B.Y + B.Height));
        myPoints.add(p);
        p = new Point((int) (B.X + B.Width), (int) B.Y);
        myPoints.add(p);
    }

    public void resample() {
        int distanceBetweenToPoints = longueurTrace / NB_POINTS - 1;
        int distanceParcourue = 0;
        getMyPoints().add(myInitialPoints.get(0));
        double d = 0;

        for (int i = 1; i < myInitialPoints.size(); i++) {
            Point prevPoint = myInitialPoints.get(i - 1);
            Point currentPoint = myInitialPoints.get(i);
            d = currentPoint.distance(prevPoint.x, prevPoint.y);
            if (distanceParcourue + d >= distanceBetweenToPoints) {
                int x = (int) (prevPoint.x + ((distanceBetweenToPoints - distanceParcourue) / d)
                        * (currentPoint.x - prevPoint.x));
                int y = (int) (prevPoint.y + ((distanceBetweenToPoints - distanceParcourue) / d)
                        * (currentPoint.y - prevPoint.y));
                Point suivant = new Point(x, y);
                getMyPoints().add(suivant);
                myInitialPoints.add(i, suivant);
                distanceParcourue = 0;
            } else {
                distanceParcourue += d;
            }

        }

    }

    public void pathLength() {
        longueurTrace = 0;
        for (int i = 1; i < myInitialPoints.size(); i++) {
            Point prevPoint = myInitialPoints.get(i - 1);
            Point currentPoint = myInitialPoints.get(i);
            longueurTrace += currentPoint.distance(prevPoint.x, prevPoint.y);

        }
    }

    public List<Point> rotateToZero() {
        Point c = Centroid(getMyPoints());
        double angle = Math.atan2(c.y - getMyPoints().get(0).y, c.x - getMyPoints().get(0).x);
        List<Point> myResamplePointsRotated = new ArrayList<>();

        myResamplePointsRotated = rotateBy(getMyPoints(), -angle);

        return myResamplePointsRotated;
    }

    Point Centroid(List<Point> points) {
        Point centriod = new Point(0, 0);
        for (int i = 0; i < points.size(); i++) {
            centriod.x += points.get(i).x;
            centriod.y += points.get(i).y;
        }
        centriod.x /= points.size();
        centriod.y /= points.size();
        return centriod;
    }

    public List<Point> rotateBy(List<Point> points, double angle) {
        Point c = Centroid(points);
        List<Point> newPoints = new ArrayList<>();

        for (Point pt : points) {
            int x = (int) ((pt.x - c.x) * Math.cos(angle)
                    - (pt.y - c.y)
                    * Math.sin(angle) + c.x);
            int y = (int) ((pt.x - c.x) * Math.sin(angle)
                    - (pt.y - c.y)
                    * Math.cos(angle) + c.y);
            newPoints.add(new Point(x, y));
        }
        return newPoints;
    }

    public List<Point> scaleToSquare(List<Point> points, int size) {
        Rectangle B = BoundingBox(points);
        List<Point> newpoints = new ArrayList<>();

        for (Point pt : points) {
            int qx = (int) (pt.x * (size / B.Width));
            int qy = (int) (pt.y * (size / B.Height));

            newpoints.add(new Point(qx, qy));

        }

        return newpoints;
    }

    Rectangle BoundingBox(List<Point> points) {
        float minX = INFINITY;
        float maxX = -INFINITY;
        float minY = INFINITY;
        float maxY = -INFINITY;

        for (Point pt : points) {
            minX = Math.min(pt.x, minX);
            maxX = Math.max(pt.x, maxX);
            minY = Math.min(pt.y, minY);
            maxY = Math.max(pt.y, maxY);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public List<Point> translateToOrigin(List<Point> points, int panelSize) {
        Point c = Centroid(points);
        List<Point> newpoints = new ArrayList<>();

        for (Point pt : points) {
            int qx = pt.x - c.x;
            int qy = pt.y - c.y;
            newpoints.add(new Point(qx + panelSize, qy + panelSize));
        }
        return newpoints;
    }

    public void recognize(Point[] points, Object templates) {

    }


    public List<Point> getMyPoints() {
        return myPoints;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String cmd) {
        command = cmd;
    }

    public boolean isLearned() {
        return isLearned;
    }

    private class Rectangle {

        float X;
        float Y;
        float Width;
        float Height;

        Rectangle(float x, float y, float width, float height) {
            X = x;
            Y = y;
            Width = width;
            Height = height;
        }
    }

}
*/
