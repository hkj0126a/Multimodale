/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimodal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nathan
 */
public class Action {

//    private ModalEnum modal;
    private ActionEnum actionEnCours;
//    private String command;
    private String parameters[];
    private boolean isComplete;
//    private Map<ActionEnum, String> actionComplementaires;

    public Action() {
//        setModal(ModalEnum.NONE);
//        setCommand("");
        parameters = new String[2];
        parameters[0] = "";
        parameters[1] = "";
        actionEnCours = ActionEnum.NULL;
//        actionComplementaires = new HashMap();
//        fillActionComplementaires();
    }

//    public void fillActionComplementaires() {
//        actionComplementaires.put(ActionEnum.VOIX_ICI, "clic");
//        actionComplementaires.put(ActionEnum.VOIX_CETTECOULEUR, "clic");
//        actionComplementaires.put(ActionEnum.COMMECELA, "cam");
//    }

//    public String getCommandeCorrespondant(ActionEnum action) {
//        return actionComplementaires.get(action);
//    }

//    public List<ActionEnum> getActionCorrespondant(String commande) {
//        List<ActionEnum> actionsCorrespondants = new ArrayList();
//        for (ActionEnum action : actionComplementaires.keySet()) {
//            //Si on est dans "CETTECOULEUR", on va s'attendre à retourner un clic
//            if (actionEnCours.equals(action) && actionComplementaires.get(action).equals(commande)) {
//                actionsCorrespondants.add(action);
//            }
//        }
//        return actionsCorrespondants;
//    }

//    public void Action(ModalEnum m, String cmd) {
//        setModal(m);
//        setCommand(cmd);
//    }

    public void init() {
        actionEnCours = ActionEnum.NULL;
        parameters = new String[2];
        parameters[0] = "";
        parameters[1] = "";
    }

//    /**
//     * @return the modal
//     */
//    public ModalEnum getModal() {
//        return modal;
//    }
//
//    /**
//     * @param modal the modal to set
//     */
//    public void setModal(ModalEnum modal) {
//        this.modal = modal;
//    }
//
//    /**
//     * @return the command
//     */
//    public String getCommand() {
//        return command;
//    }
//
//    /**
//     * @param command the command to set
//     */
//    public void setCommand(String command) {
//        this.command = command;
//    }

//    public void getComplementaryAction(String action) {
//        //ici, rectangle, comme cela
//    }
//
//    public void getComplementaryAction(ActionEnum actionMultimodale) {
//        //clic, geste
//    }

    public void setIsComplete(boolean set) {
        isComplete = set;
    }

    public boolean isComplete() {
        return isComplete;
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
        if(parameters == null) {
            parameters = new String[2];
        }
        if (parameters[0] != null && !actionEnCours.equals(ActionEnum.NULL)) {
            setIsComplete(true);
        } else {
            setIsComplete(false);
        }
    }
}
