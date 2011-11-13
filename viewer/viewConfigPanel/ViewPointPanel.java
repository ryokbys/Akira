package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import data.*;
import tools.*;
import viewer.renderer.*;
import viewer.*;

public class ViewPointPanel extends JPanel implements ActionListener{

  private Controller ctrl;
  private ViewConfig vconf;
  public ViewPointPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    createPanel();
  }
  public void actionPerformed( ActionEvent e ){
    if( e.getSource() == loadButton ){
      if(ctrl.getActiveRW()==null)return;
      float[] mvm = ctrl.getActiveRW().vp.mvm;
      String m1 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                mvm[0], mvm[1], mvm[2], mvm[3] );
      String m2 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                mvm[4], mvm[5], mvm[6], mvm[7] );
      String m3 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                mvm[8], mvm[9], mvm[10], mvm[11] );
      String m4 = String.format("%15.8f %15.8f %15.8f %15.8f",
                                mvm[12], mvm[13], mvm[14], mvm[15] );
      outArea.append("----------\n");
      outArea.append(m1+"\n");
      outArea.append(m2+"\n");
      outArea.append(m3+"\n");
      outArea.append(m4+"\n");
      outArea.append("----------\n");
    }
  }

  private JSpinner spEyeX,spEyeY,spEyeZ;
  private JSpinner spNear,spFar,spFovy;
  private JTextArea outArea;
  private JButton loadButton;
  private void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);

    spEyeX = new JSpinner(new SpinnerNumberModel(0.0, 0., null, 0.1));
    spEyeY = new JSpinner(new SpinnerNumberModel(0.0, 0., null, 0.1));
    spEyeZ = new JSpinner(new SpinnerNumberModel(10.0, 0., null, 0.1));
    spNear = new JSpinner(new SpinnerNumberModel(0.1, 0., null, 0.1));
    spFar  = new JSpinner(new SpinnerNumberModel(1.0, 0., null, 0.1));
    spFovy = new JSpinner(new SpinnerNumberModel(40.0, 0., null, 0.1));

    spEyeX.setFocusable(false);
    spEyeY.setFocusable(false);
    spEyeZ.setFocusable(false);
    spNear.setFocusable(false);
    spFar.setFocusable(false);
    spFovy.setFocusable(false);

    spEyeX.setPreferredSize(new Dimension(60, 25));
    spEyeY.setPreferredSize(new Dimension(60, 25));
    spEyeZ.setPreferredSize(new Dimension(60, 25));
    spNear.setPreferredSize(new Dimension(60, 25));
    spFar.setPreferredSize(new Dimension(60, 25));
    spFovy.setPreferredSize(new Dimension(60, 25));

    JLabel lEye=new JLabel("Eye");
    JLabel lNear=new JLabel("Near");
    JLabel lFar=new JLabel("Far");
    JLabel lFovy=new JLabel("Fovy");


    outArea = new JTextArea();
    outArea.setEditable(false);
    outArea.setLineWrap(true);
    outArea.setCaretPosition(outArea.getText().length());
    JScrollPane sp=new JScrollPane(outArea);

    loadButton=new JButton("load Matrix");
    loadButton.setFocusable(false);
    loadButton.addActionListener( this );

    SpringLayout layout = new SpringLayout();
    setLayout( layout );

    layout.putConstraint( SpringLayout.NORTH, lEye, 10, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.WEST,  lEye, 10, SpringLayout.WEST, this );
    layout.putConstraint( SpringLayout.NORTH, spEyeX, 0, SpringLayout.NORTH, lEye);
    layout.putConstraint( SpringLayout.WEST,  spEyeX, 5, SpringLayout.EAST, lEye);
    layout.putConstraint( SpringLayout.NORTH, spEyeY, 0, SpringLayout.NORTH, spEyeX);
    layout.putConstraint( SpringLayout.WEST,  spEyeY, 5, SpringLayout.EAST, spEyeX);
    layout.putConstraint( SpringLayout.NORTH, spEyeZ, 0, SpringLayout.NORTH, spEyeY);
    layout.putConstraint( SpringLayout.WEST,  spEyeZ, 0, SpringLayout.EAST, spEyeY);

    layout.putConstraint( SpringLayout.NORTH, lNear, 15, SpringLayout.SOUTH, lEye);
    layout.putConstraint( SpringLayout.WEST,  lNear, 0, SpringLayout.WEST, lEye);
    layout.putConstraint( SpringLayout.NORTH, spNear, 0, SpringLayout.NORTH, lNear);
    layout.putConstraint( SpringLayout.WEST,  spNear, 0, SpringLayout.EAST, lNear);

    layout.putConstraint( SpringLayout.NORTH, lFar, 0, SpringLayout.NORTH, spNear);
    layout.putConstraint( SpringLayout.WEST,  lFar, 10, SpringLayout.EAST, spNear);
    layout.putConstraint( SpringLayout.NORTH, spFar, 0, SpringLayout.NORTH, lFar);
    layout.putConstraint( SpringLayout.WEST,  spFar, 0, SpringLayout.EAST, lFar);

    layout.putConstraint( SpringLayout.NORTH, lFovy, 0, SpringLayout.NORTH, spFar);
    layout.putConstraint( SpringLayout.WEST,  lFovy, 10, SpringLayout.EAST, spFar);
    layout.putConstraint( SpringLayout.NORTH, spFovy, 0, SpringLayout.NORTH, lFovy);
    layout.putConstraint( SpringLayout.WEST,  spFovy, 0, SpringLayout.EAST, lFovy);



    layout.putConstraint( SpringLayout.SOUTH, loadButton, -10, SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.WEST, loadButton, 10, SpringLayout.EAST,spFovy);

    layout.putConstraint( SpringLayout.SOUTH, sp, -10, SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.NORTH, sp, 10, SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.EAST,  sp, -10, SpringLayout.EAST, this);
    layout.putConstraint( SpringLayout.WEST,  sp, 10, SpringLayout.EAST, loadButton);



    add(spEyeX);
    add(spEyeY);
    add(spEyeZ);
    add(spNear);
    add(spFar);
    add(spFovy);
    add(lEye);
    add(lNear);
    add(lFar);
    add(lFovy);
    add(sp);
    add(loadButton);

  }

}
