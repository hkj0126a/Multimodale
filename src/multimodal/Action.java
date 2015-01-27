/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimodal;

import java.awt.Point;

/**
 *
 * @author nathan
 */
public class Action {

    private ActionEnum actionEnCours;
    private String parameters[];
    private Point reference;
    private Point destination;
    private boolean isComplete;

    public Action() {
        init();
    }

    public void init() {
        actionEnCours = ActionEnum.NULL;
        parameters = new String[2];
        parameters[0] = "";
        parameters[1] = "";
        reference = new Point(0, 0);
        destination = new Point(0, 0);
    }

    public void setIsComplete(boolean set) {
        isComplete = set;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isDeplacementValide() {
        System.out.println("Is deplacement valide ref =" + reference.x + " " + reference.y + " dest " + destination.x + destination.y);
        if ((reference.x == 0 && reference.x == 0) || (destination.x == 0 && destination.y == 0)) {
            return false;
        } else {
            return true;
        }
    }

    public void setActionEnCours(ActionEnum actionEnCours) {
        this.actionEnCours = actionEnCours;
        updateIsComplete();
    }

    public ActionEnum getActionEnCours() {
        return actionEnCours;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
        updateIsComplete();
    }

    private void updateIsComplete() {
//        if (parameters == null) {
//            parameters = new String[2];
//        }
        if (parameters[0] != null && !actionEnCours.equals(ActionEnum.NULL)) {
            setIsComplete(true);
        } else {
            setIsComplete(false);
        }
    }

    public Point getReference() {
        return reference;
    }

    public void setReference(Point reference) {
        this.reference = reference;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;

    }

    public int getDistanceX() {
        return destination.x - reference.x;
    }

    public int getDistanceY() {
        return destination.y - reference.y;
    }
}
