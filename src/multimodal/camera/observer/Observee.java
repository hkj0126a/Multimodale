package multimodal.camera.observer;

import java.util.List;
import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.swing.JFrame;
import jp.nyatla.nyartoolkit.java3d.utils.NyARMultipleMarkerBehaviorListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hakje
 */
public class Observee implements NyARMultipleMarkerBehaviorListener {

    private List<ObserverCamera> observers;

    public Observee() {
        observers = new ArrayList();
    }
    
    public void addObserver(ObserverCamera ob) {
        observers.add(ob);
    }
    
    public void removeObserver(ObserverCamera ob) {
        observers.remove(ob);
    }

    @Override
    public void onUpdate(int i_marker, Transform3D td) {
        for(ObserverCamera o : observers) {
            o.update(i_marker, td);
        }
    }
}
