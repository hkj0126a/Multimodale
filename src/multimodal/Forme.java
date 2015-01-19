package multimodal;

import java.awt.Point;
import java.awt.geom.Point2D;

public class Forme {

    private FormeEnum myForme = FormeEnum.NOTHING;
    private String x;
    private String y;
    private int longueur;
    private int hauteur;
    private String couleurFond;
    private String couleurContour;

    public Forme() {
        clearForme();
    }

    public void clearForme() {
        x = "0";
        y = "0";
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
     * @return the x
     */
    public String getX() {
        return x;

    }

    /**
     * @return the y
     */
    public String getY() {
        return y;
    }

    /**
     * @return the x
     */
    public void setX(String _x) {
        x = _x;
    }

    /**
     * @return the y
     */
    public void setY(String _y) {
        y = _y;
    }

    public void setPosition(String x, String y) {
        setX(x);
        setY(y);
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

    @Override
    public String toString() {
        if (myForme != FormeEnum.NOTHING) {
            String forme = (myForme == FormeEnum.ELLIPSE) ? "Ellipse" : "Rectangle";
            return "Palette:Creer" + forme + " x=" + getX() + " y=" + getY()
                    + " longueur=" + longueur + " hauteur=" + hauteur + " couleurFond=" + couleurFond
                    + " couleurContour=" + couleurContour;
        }
        else {
            return null;
        }
    }

}
