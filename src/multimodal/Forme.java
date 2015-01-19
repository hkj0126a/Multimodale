package multimodal;

import java.awt.Point;
import java.awt.geom.Point2D;


public class Forme {

    private FormeEnum myForme = FormeEnum.RECTANGLE;
    private Point2D position;
    private String color;
    private int longueur;
    private int hauteur;
    private String couleurFond;
    private String couleurContour;
        
    public Forme () {
        clearForme();
    }
    
    public void clearForme () {
        position = new Point2D.Double();
        position.setLocation(0, 0);
        longueur = 100;
        hauteur = 50;
        couleurFond = "white";
        couleurContour = "black";
    }
    
    /**
     * @return the myForme
     */
    public FormeEnum getMyForme() {
        return myForme;
    }

    /**
     * @param myForme the myForme to set
     */
    public void setMyForme(FormeEnum myForme) {
        this.myForme = myForme;
    }

    /**
     * @return the position
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Point2D position) {
        this.position = position;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the longueur
     */
    public int getLongueur() {
        return longueur;
    }

    /**
     * @param longueur the longueur to set
     */
    public void setLongueur(int longueur) {
        this.longueur = longueur;
    }

    /**
     * @return the hauteur
     */
    public int getHauteur() {
        return hauteur;
    }

    /**
     * @param hauteur the hauteur to set
     */
    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
    }

    /**
     * @return the couleurFond
     */
    public String getCouleurFond() {
        return couleurFond;
    }

    /**
     * @param couleurFond the couleurFond to set
     */
    public void setCouleurFond(String couleurFond) {
        this.couleurFond = couleurFond;
    }

    /**
     * @return the couleurContour
     */
    public String getCouleurContour() {
        return couleurContour;
    }

    /**
     * @param couleurContour the couleurContour to set
     */
    public void setCouleurContour(String couleurContour) {
        this.couleurContour = couleurContour;
    }
    
    
}
