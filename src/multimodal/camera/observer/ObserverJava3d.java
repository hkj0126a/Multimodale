package multimodal.camera.observer;


import javax.media.j3d.Transform3D;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hakje
 */
public interface ObserverJava3d {
    public abstract void update(int i_marker, Transform3D td);
}
