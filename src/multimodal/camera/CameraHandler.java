package multimodal.camera;

/* 
 * PROJECT: NyARToolkit Java3d sample program.
 * --------------------------------------------------------------------------------
 * The MIT License
 * Copyright (c) 2008 nyatla
 * airmail(at)ebony.plala.or.jp
 * http://nyatla.jp/nyartoolkit/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
//package jp.nyatla.nyartoolkit.java3d.sample;
import java.awt.BorderLayout;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.java3d.utils.J3dNyARParam;
import jp.nyatla.nyartoolkit.java3d.utils.NyARMultipleMarkerBehaviorHolder;
import com.sun.j3d.utils.universe.SimpleUniverse;
import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;
import multimodal.camera.observer.Observee;
import multimodal.camera.observer.ObserverCamera;

/**
 * Java3Dã‚µãƒ³ãƒ—ãƒ«ãƒ—ãƒ­ã‚°ãƒ©ãƒ 
 * å�˜ä¸€ãƒžãƒ¼ã‚«ãƒ¼è¿½è·¡ç”¨ã�®Behaviorã‚’ä½¿ã�£ã�¦ã€�èƒŒæ™¯ã�¨ï¼‘å€‹ã�®ãƒžãƒ¼ã‚«ãƒ¼ã�«é€£å‹•ã�—ã�Ÿ
 * TransformGroupã‚’å‹•ã�‹ã�—ã�¾ã�™ã€‚
 *
 */
public class CameraHandler extends JFrame implements ObserverCamera {

    private static final long serialVersionUID = -8472866262481865377L;
    private static int PATT_ID = 0;

    private final String CARCODE_FILE1 = "./Data/patt.hiro";
    private int PATT_HIRO_ID;

    private final String CARCODE_FILE2 = "./Data/patt.kanji";
    private int PATT_KANJI_ID;

    private final String CARCODE_FILE3 = "./Data/patt.black";
    private int PATT_BLACK_ID;

    private final String CARCODE_FILE4 = "./Data/patt.green";
    private int PATT_GREEN_ID;

    private final String CARCODE_FILE5 = "./Data/patt.red";
    private int PATT_RED_ID;

    private final String CARCODE_FILE6 = "./Data/patt.blue";
    private int PATT_BLUE_ID;

    private final String CARCODE_FILE7 = "./Data/patt.white";
    private int PATT_WHITE_ID;

    private final String PARAM_FILE = "./Data/camera_para.dat";

    private static float TAILLE_MARKER = 0.08f;

    //NyARToolkité–¢ä¿‚
    private NyARMultipleMarkerBehaviorHolder nya_behavior;
    private J3dNyARParam ar_param;

    //universeé–¢ä¿‚
    private Canvas3D canvas;
    private Locale locale;
    private VirtualUniverse universe;

    //Ivy
    private Ivy bus;
    private String x, y, couleur;
    private Vector3f vecteurPosition;

    // i_markers represents an ARCodeIndex  
    public void update(int i_markers, Transform3D i_transform3d) {
//        System.out.println(" " + i_markers);

        String deplacementX = "0";
        String deplacementY = "0";
        String couleur = "";
        if (vecteurPosition.equals(new Vector3f(0, 0, 0))) {
            i_transform3d.get(vecteurPosition);
        } else {
            Vector3f vecteurNouvellePosition = new Vector3f(0, 0, 0);
            i_transform3d.get(vecteurNouvellePosition);
            int intDepX = (int) ((-vecteurNouvellePosition.x + vecteurPosition.x) * 1000);
            int intDepY = (int) ((-vecteurNouvellePosition.y + vecteurPosition.y) * 1000);
            deplacementX = Integer.toString(intDepX);
            deplacementY = Integer.toString(intDepY);
            vecteurPosition = vecteurNouvellePosition;
        }

        //déplacement dans tout les cas (l'utilisation des paramètres dépendra de l'état de la machine à état
        //Faire un état "Déplacement caméra" dans Modal fusion pour permettre le déplacement 'fluide'?
        switch (i_markers) {
            case 0:
//                couleur = "hiro";
                break;
            case 1:
//                couleur = "kanji";
                break;
            case 2:
                couleur = "black";
                break;
            case 3:
                couleur = "green";
                break;
            case 4:
                couleur = "red";
                break;
            case 5:
                couleur = "blue";
                break;
            case 6:
                couleur = "white";
                break;
            default:
                break;
        }
        setParamIvyMsg(deplacementX, deplacementY, couleur);
        try {
            bus.sendMsg("CAMERA x=" + x + " y=" + y + " couleur=" + couleur);
        } catch (IvyException ex) {
            Logger.getLogger(CameraHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Starting the Behaviour
    public void startCapture() throws Exception {
        nya_behavior.start();

    }

    public CameraHandler() throws Exception {
        super("Java3D Example NyARToolkit-Java3D - M2Pro IHM");

        initIvyBus();
        setParamIvyMsg("", "", "");
        vecteurPosition = new Vector3f(0, 0, 0);
        //NyARToolkitã�®æº–å‚™
        NyARCode ar_codes[];

        ar_codes = new NyARCode[7];
        //Hiro
        ar_codes[0] = new NyARCode(16, 16);
        ar_codes[0].loadARPattFromFile(CARCODE_FILE1);
        PATT_HIRO_ID = PATT_ID;
        PATT_ID++;

        //kanji
        ar_codes[1] = new NyARCode(16, 16);
        ar_codes[1].loadARPattFromFile(CARCODE_FILE2);
        PATT_KANJI_ID = PATT_ID;
        PATT_ID++;

        ar_codes[2] = new NyARCode(16, 16);
        ar_codes[2].loadARPattFromFile(CARCODE_FILE3);
        PATT_BLACK_ID = PATT_ID;
        PATT_ID++;
        
        ar_codes[3] = new NyARCode(16, 16);
        ar_codes[3].loadARPattFromFile(CARCODE_FILE4);
        PATT_GREEN_ID = PATT_ID;
        PATT_ID++;
        
        ar_codes[4] = new NyARCode(16, 16);
        ar_codes[4].loadARPattFromFile(CARCODE_FILE5);
        PATT_RED_ID = PATT_ID;
        PATT_ID++;
        
        ar_codes[5] = new NyARCode(16, 16);
        ar_codes[5].loadARPattFromFile(CARCODE_FILE6);
        PATT_BLUE_ID = PATT_ID;
        PATT_ID++;
        
        ar_codes[6] = new NyARCode(16, 16);
        ar_codes[6].loadARPattFromFile(CARCODE_FILE7);
        PATT_WHITE_ID = PATT_ID;
        PATT_ID++;
        
        double marker_width[];
        marker_width = new double[7];
        marker_width[0] = TAILLE_MARKER;
        marker_width[1] = TAILLE_MARKER;        
        marker_width[2] = TAILLE_MARKER;
        marker_width[3] = TAILLE_MARKER;
        marker_width[4] = TAILLE_MARKER;
        marker_width[5] = TAILLE_MARKER;
        marker_width[6] = TAILLE_MARKER;

        ar_param = new J3dNyARParam();
        ar_param.loadARParamFromFile(PARAM_FILE);
        ar_param.changeScreenSize(320, 240);

        //localeã�®ä½œæˆ�ã�¨locateã�¨viewã�®è¨­å®š
        universe = new VirtualUniverse();
        locale = new Locale(universe);
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        View view = new View();
        ViewPlatform viewPlatform = new ViewPlatform();
        view.attachViewPlatform(viewPlatform);
        view.addCanvas3D(canvas);
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());

        //è¦–ç•Œã�®è¨­å®š(ã‚«ãƒ¡ãƒ©è¨­å®šã�‹ã‚‰å�–å¾—)
        Transform3D camera_3d = ar_param.getCameraTransform();
        view.setCompatibilityModeEnable(true);
        view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
        view.setLeftProjection(camera_3d);

        //è¦–ç‚¹è¨­å®š(0,0,0ã�‹ã‚‰ã€�Yè»¸ã‚’180åº¦å›žè»¢ã�—ã�¦Z+æ–¹å�‘ã‚’å�‘ã��ã‚ˆã�†ã�«ã�™ã‚‹ã€‚)
        TransformGroup viewGroup = new TransformGroup();
        Transform3D viewTransform = new Transform3D();
        viewTransform.rotY(Math.PI);
        viewTransform.setTranslation(new Vector3d(0.0, 0.0, 0.0));
        viewGroup.setTransform(viewTransform);
        viewGroup.addChild(viewPlatform);
        BranchGroup viewRoot = new BranchGroup();
        viewRoot.addChild(viewGroup);
        locale.addBranchGraph(viewRoot);

        //ãƒ�ãƒƒã‚¯ã‚°ãƒ©ã‚¦ãƒ³ãƒ‰ã�®ä½œæˆ�
        Background background = new Background();
        BoundingSphere bounds = new BoundingSphere();
        bounds.setRadius(10.0);
        background.setApplicationBounds(bounds);
        background.setImageScaleMode(Background.SCALE_FIT_ALL);
        background.setCapability(Background.ALLOW_IMAGE_WRITE);
        BranchGroup root = new BranchGroup();
        root.addChild(background);

        TransformGroup transformGroups[];
        transformGroups = new TransformGroup[2];

        //NyARToolkitã�®Behaviorã‚’ä½œã‚‹ã€‚(ãƒžãƒ¼ã‚«ãƒ¼ã‚µã‚¤ã‚ºã�¯ãƒ¡ãƒ¼ãƒˆãƒ«ã�§æŒ‡å®šã�™ã‚‹ã�“ã�¨)
        nya_behavior = new NyARMultipleMarkerBehaviorHolder(ar_param, 30f, ar_codes, marker_width, 7);

        //Behaviorã�«é€£å‹•ã�™ã‚‹ã‚°ãƒ«ãƒ¼ãƒ—ã‚’ã‚»ãƒƒãƒˆ
        nya_behavior.setTransformGroup(transformGroups[0], PATT_HIRO_ID);
        nya_behavior.setTransformGroup(transformGroups[1], PATT_KANJI_ID);
        nya_behavior.setBackGround(background);

        //å‡ºæ�¥ã�Ÿbehaviorã‚’ã‚»ãƒƒãƒˆ
        root.addChild(nya_behavior.getBehavior());
        Observee observee = new Observee();
        nya_behavior.setUpdateListener(observee);

        //è¡¨ç¤ºãƒ–ãƒ©ãƒ³ãƒ�ã‚’Locateã�«ã‚»ãƒƒãƒˆ
        locale.addBranchGraph(root);

        //ã‚¦ã‚¤ãƒ³ãƒ‰ã‚¦ã�®è¨­å®š
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

        observee.addObserver(this);
    }

    private void initIvyBus() {
        bus = new Ivy("IvyControler", "IvyControler Ready", null);
        try {
            bus.start("127.255.255.255:2010");
        } catch (IvyException ex) {
            Logger.getLogger(CameraHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setParamIvyMsg(String paramX, String paramY, String col) {
        x = paramX;
        y = paramY;
        couleur = col;
    }
}
