/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivytp.scribble;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hakje
 */
public class Trait {

    private List<Point> coordonnees;

    public Trait() {
        coordonnees = new ArrayList();
    }

    public List<Point> getCoordonnees() {
        return coordonnees;
    }

    public void setCoordonnees(List<Point> coordonnees) {
        this.coordonnees = coordonnees;
    }

    public void addCoordonnee(int x, int y) {
        this.coordonnees.add(new Point(x, y));
    }

    @Override
    public String toString() {
        String output = new String("");
        for (Point p : coordonnees) {
            output += p.x + " " + p.y + ", ";
            
        }
        output += " break";
        return output;
    }
}
