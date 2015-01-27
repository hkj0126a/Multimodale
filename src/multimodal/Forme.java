package multimodal;

import java.awt.Point;
import java.awt.geom.Point2D;

public class Forme {

    private FormeEnum myForme = FormeEnum.NOTHING;
    private String name;
    private String x;
    private String y;
    private int width;
    private int height;
    private String backgroundColor;
    private String strokeColor;
    private boolean isComplete;
    private Commande typeCommande;

    public Forme() {
        clearForme();
    }

    public void clearForme() {
        myForme = FormeEnum.NOTHING;
        typeCommande = Commande.NULL;
        x = "0";
        y = "0";
        width = 100;
        height = 50;
        backgroundColor = "white";
        strokeColor = "black";
        setName("");
        isComplete = false;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void updateIsComplete() {
        if (typeCommande.equals(Commande.DEPLACEMENT)) {
            if (/*myForme.equals(FormeEnum.NOTHING) ||*/ name.equals("")) {
                isComplete = false;
            } else {
                isComplete = true;
            }
        } else if (x.equals("0") && y.equals("0") || myForme.equals(FormeEnum.NOTHING)) {
            isComplete = false;
        } else {
            isComplete = true;
        }

        System.out.println("update complete? " + isComplete + " " + x + " " + y + " " + myForme);
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
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
    public int getWidth() {
        return width;
    }

    /**
     * @param longueur the longueur to set
     */
    public void setWidth(int longueur) {
        this.width = longueur;
    }

    /**
     * @return the hauteur
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param hauteur the hauteur to set
     */
    public void setHeight(int hauteur) {
        this.height = hauteur;
    }

    /**
     * @return the couleurFond
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param couleurFond the couleurFond to set
     */
    public void setBackgroundColor(String couleurFond) {
        this.backgroundColor = couleurFond;
    }

    /**
     * @return the couleurContour
     */
    public String getStrokeColor() {
        return strokeColor;
    }

    /**
     * @param couleurContour the couleurContour to set
     */
    public void setStrokeColor(String couleurContour) {
        this.strokeColor = couleurContour;
    }

    public String commandToCreateFormePatern() {
        if (myForme != FormeEnum.NOTHING) {
            String forme = (myForme == FormeEnum.ELLIPSE) ? "Ellipse" : "Rectangle";
            return "Palette:Creer" + forme + " x=" + getX() + " y=" + getY()
                    + " longueur=" + width + " hauteur=" + height + " couleurFond=" + backgroundColor
                    + " couleurContour=" + strokeColor;
        } else {
            return null;
        }
    }

    public String commandToMoveFormePatern() {
        if (/*myForme != FormeEnum.NOTHING*/ getName() != "") {

            return "Palette:DeplacerObjet nom=" + getName() + " x=" + getX() + " y=" + getY();
        } else {
            return null;
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public Commande getTypeCommande() {
        return typeCommande;
    }

    public void setTypeCommande(Commande typeCommande) {
        this.typeCommande = typeCommande;
    }

}
