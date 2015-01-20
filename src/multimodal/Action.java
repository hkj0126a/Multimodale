/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimodal;

/**
 *
 * @author nathan
 */
public class Action {
    private ModalEnum modal;
    private String command;
    
    
    public void Action () {
        setModal(ModalEnum.NONE);
        setCommand("");
    }
    
    public void Action(ModalEnum m, String cmd) {
        setModal(m);
        setCommand(cmd);
    }

    /**
     * @return the modal
     */
    public ModalEnum getModal() {
        return modal;
    }

    /**
     * @param modal the modal to set
     */
    public void setModal(ModalEnum modal) {
        this.modal = modal;
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }
    
    
}
