/*
 * AKIRAView key Control
 */

package viewer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

import tools.*;
import viewer.*;
import viewer.LF.*;
import viewer.renderer.*;

class KeyController extends KeyAdapter{

  private Controller ctrl;
  //constructor
  public KeyController(Controller ctrl){
    this.ctrl=ctrl;
  }

  //main method
  public void keyPressed( KeyEvent ke ){
    float value=ctrl.vconf.ControllerValue;
    RenderingWindow rw=ctrl.getActiveRW();

    if(rw==null)return;

    //when caps_lock is on, atoms are invisible
    if(ctrl.javaVer>=1.6)
      rw.visibleAtoms=!Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);

    switch ( ke.getKeyCode() ){
    case KeyEvent.VK_ESCAPE:
      if(ke.getSource() instanceof RenderingWindow){
        RenderingWindow rww = (RenderingWindow)ke.getSource();
        //if(ctrl.isEnjoyMode) new Dissolver().dissolveExit(rww);
        rww.dispose();
      }else{
        ctrl.myExit();
      }
      break;
    case KeyEvent.VK_F1:
      break;
    case KeyEvent.VK_F2:
      break;
    case KeyEvent.VK_F3:
      break;
    case KeyEvent.VK_F4:
      break;
    case KeyEvent.VK_F5:
      break;
    case KeyEvent.VK_F6:
      break;
    case KeyEvent.VK_F7:
      break;
    case KeyEvent.VK_F8:
      break;

    case KeyEvent.VK_UP:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
        //half
        if( ke.isShiftDown() ){
          //translate
          rw.setObjectCenter(0,value*0.5f,0);
        }else{
          //rotation
          rw.setObjectRotate(0, value*0.5f,0);
        }
      }else if( ke.isAltDown() ){
        //double
        if( ke.isShiftDown()){
          //translate
          rw.setObjectCenter(0,value*2.f,0);
        }else{
          //rotation
          rw.setObjectRotate(0, value*2.f,0);
        }
      }else if( ke.isShiftDown() ){
        //translate
        rw.setObjectCenter(0,value,0);
      }else{
        //rotation
        rw.setObjectRotate(0, value,0);
      }
      break;
    case KeyEvent.VK_DOWN:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
        //half
        if( ke.isShiftDown() ){
          //translate
          rw.setObjectCenter(0,-value*0.5f,0);
        }else{
          //rotation
          rw.setObjectRotate(0, -value*0.5f,0);
        }
      }else if( ke.isAltDown() ){
        //double
        if( ke.isShiftDown() ){
          //translate
          rw.setObjectCenter(0,-value*2.f,0);
        }else{
          //rotation
          rw.setObjectRotate(0, -value*2.f,0);
        }
      }else if( ke.isShiftDown() ){
        //translate
        rw.setObjectCenter(0,-value,0);
      }else{
        //rotation
        rw.setObjectRotate(0, -value,0);
      }
      break;
    case KeyEvent.VK_RIGHT:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
        //half
        if( ke.isShiftDown()){
          //translate
          rw.setObjectCenter(value*0.5f,0,0);
        }else{
          //rotation
          rw.setObjectRotate(-value*0.5f,0,0);
        }
      }else if( ke.isAltDown() ){
        //twice
        if( ke.isShiftDown() ){
          //translate
          rw.setObjectCenter(value*2f,0,0);
        }else{
          //rotation
          rw.setObjectRotate(-value*2f,0,0);
        }
      }else if( ke.isShiftDown() ){
        //translate
        rw.setObjectCenter(value,0,0);
      }else{
        //rotation
        rw.setObjectRotate(-value,0,0);
      }
      break;
    case KeyEvent.VK_LEFT:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
        //half
        if( ke.isShiftDown() && ke.isMetaDown() ){
          //translate
          rw.setObjectCenter(-value*0.5f,0,0);
        }else{
          //rotation
          rw.setObjectRotate(value*0.5f,0,0);
        }
      }else if( ke.isAltDown() ){
        //double
        if( ke.isShiftDown() && ke.isAltDown() ){
          //twice translate
          rw.setObjectCenter(-value*2f,0,0);
        }else{
          //rotation
          rw.setObjectRotate(value*2f,0,0);
        }
      }else if( ke.isShiftDown() ){
        //translate
        rw.setObjectCenter(-value,0,0);
      }else{
        //rotation
        rw.setObjectRotate(value,0,0);
      }
      break;

    case KeyEvent.VK_PAGE_UP:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.addEye(0,value,0);
        rw.addCenter(0,value,0);
      }
      break;
    case KeyEvent.VK_PAGE_DOWN:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.addEye(0,-value,0);
        rw.addCenter(0,-value,0);
      }
      break;
    case KeyEvent.VK_HOME:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.addEye(value,0,0);
        rw.addCenter(value,0,0);
      }
      break;
    case KeyEvent.VK_END:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.addEye(-value,0,0);
        rw.addCenter(-value,0,0);
      }
      break;

    case KeyEvent.VK_0:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(0);
      }
      break;
    case KeyEvent.VK_1:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(1);
      }
      break;
    case KeyEvent.VK_2:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(2);
      }
      break;
    case KeyEvent.VK_3:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(3);
      }
      break;
    case KeyEvent.VK_4:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(4);
      }
      break;
    case KeyEvent.VK_5:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(5);
      }
      break;
    case KeyEvent.VK_6:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(6);
      }
      break;
    case KeyEvent.VK_7:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(7);
      }
      break;
    case KeyEvent.VK_8:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(8);
      }
      break;
    case KeyEvent.VK_9:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.setrenderingAtomDataIndex(9);
      }
      break;

    case KeyEvent.VK_AT:
      break;

    case KeyEvent.VK_A:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
        rw.setVisibleAxis();
      }else if( ke.isShiftDown() ){
      }else{
      }
      break;

    case KeyEvent.VK_B:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
        rw.setVisibleBox();
      }else if( ke.isShiftDown() ){
        rw.setBondsRenderingType();
      }else{
        rw.setVisibleBonds();
      }
      break;
    case KeyEvent.VK_C:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
      }
      break;
    case KeyEvent.VK_D:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
        rw.decrementFPS();
      }else{
        rw.incrementFPS();
      }
      break;
    case KeyEvent.VK_E:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
      }
      break;
    case KeyEvent.VK_F:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
      }
      break;
    case KeyEvent.VK_G:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
      }
      break;

    case KeyEvent.VK_H:
      if( ke.isControlDown() ){
      }else if( ke.isAltDown() && ke.isShiftDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
        rw.saveViewPoint();
      }else if( ke.isShiftDown() ){
        rw.setVPSavedHome();
      }else{
        rw.setVPHome();
      }
      break;

    case KeyEvent.VK_I:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        ctrl.setFrontViewConfigWin(0);
      }
      break;

    case KeyEvent.VK_J:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
      }
      break;
    case KeyEvent.VK_K:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
      }
      break;
    case KeyEvent.VK_L:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
        rw.setAtomLabelType();
      }else{
        rw.setAtomLabelVisible();
      }
      break;

    case KeyEvent.VK_M:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
        rw.isTrackBallMode=!rw.isTrackBallMode;
        if(rw.isTrackBallMode){
          System.out.println("TrackBall mode on");
        }else{
          System.out.println("TrackBall mode off");
        }
      }else{
        rw.setViewMode();
      }
      break;

    case KeyEvent.VK_N:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.incrementFrame();
      }
      break;
    case KeyEvent.VK_O:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
        String str = (new MyOpen()).getOpenFilename();
        ctrl.createRenderingWindow(str);
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
      }
      break;

    case KeyEvent.VK_P:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
        ctrl.vconf.isSelectionInfo=!ctrl.vconf.isSelectionInfo;
      }else{
        rw.decrementFrame();
      }
      break;

    case KeyEvent.VK_Q:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        if(ke.getSource() instanceof RenderingWindow){
          RenderingWindow rww = (RenderingWindow)ke.getSource();
          //if(ctrl.isEnjoyMode) new Dissolver().dissolveExit(rww);
          rww.dispose();
        }else{
          ctrl.myExit();
        }
      }
      break;

    case KeyEvent.VK_R:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
        rw.setVisibleAtoms();
      }else{
        rw.setAtomsRenderingType();
      }
      break;

    case KeyEvent.VK_S:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.startstopAnimating();
      }
      break;

    case KeyEvent.VK_T:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
        rw.setVisibleTime();
      }else if( ke.isShiftDown() ){
        rw.setVisibleLegend();
      }else{
      }
      break;
    case KeyEvent.VK_V:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
        rw.setVectorRenderingType();
      }else{
        rw.setVisibleVectors();
      }
      break;
    case KeyEvent.VK_W:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
        rw.writeSequentialImage();
      }else{
        rw.writeImage();
        rw.repaint();
      }
      break;
    case KeyEvent.VK_X:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.resetRotation('x');
      }
      break;
    case KeyEvent.VK_Y:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
        rw.resetRotation('y');
      }
      break;

    case KeyEvent.VK_Z:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
        rw.resetRotation('z');
      }else if( ke.isShiftDown() ){
        //zoom out
        rw.keyZoom(1);
      }else{
        //zoom in
        rw.keyZoom(-1);
      }
      break;

    case KeyEvent.VK_SLASH:
      if( ke.isControlDown() ){
      }else if( ke.isMetaDown() ){
      }else if( ke.isAltDown() ){
      }else if( ke.isShiftDown() ){
      }else{
      }
      break;

    default:
      break;
    }

  }

  public void keyReleased( KeyEvent ke ){
  }
  public void keyTyped( KeyEvent ke ){
  }
}
