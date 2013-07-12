package viewer.viewConfigPanel;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import info.clearthought.layout.*;
import java.util.*;
import java.io.*;

import viewer.*;
import tools.*;
import viewer.renderer.*;

public class StatusPanel extends JPanel implements ActionListener,ChangeListener{

  /* accesser starts*/

  public void updateStatusString(RenderingWindow rw){
    float[][] h= rw.atoms.hmat;
    lNatm.setText(String.format("# of atoms: %d/%d",rw.getVisibleNAtoms()
                                ,rw.getNAtoms()));
    lBox1.setText(String.format("%7.1f %7.1f %7.1f",h[0][0],h[0][1],h[0][2]));
    lBox2.setText(String.format("%7.1f %7.1f %7.1f",h[1][0],h[1][1],h[1][2]));
    lBox3.setText(String.format("%7.1f %7.1f %7.1f",h[2][0],h[2][1],h[2][2]));

    lFrame.setText(String.format("Frame number: %d/%d",rw.currentFrame+1,rw.totalFrame));
    slFrame.setMaximum(rw.totalFrame-1);
    if(!rw.isAnimating())slFrame.setValue(rw.currentFrame);
    lFPS.setText(String.format("Frame per second: %d",rw.vconf.fps));
    slFPS.setValue(rw.vconf.fps);
    slFPS.setMinimum(rw.vconf.fpsMin);
    slFPS.setMaximum(rw.vconf.fpsMax);



  }
  /* accesser ends*/

  public void actionPerformed( ActionEvent ae){
    if( ae.getSource() == popoutButton ){
      vconf.isPopStatus=!vconf.isPopStatus;
      vconf.isPopStatusAlwaysTop=cbAlwaysTop.isSelected();
      updateStatusFrame();
    }

  }

  // called when the event happens
  public void stateChanged(ChangeEvent ce){
    if( ce.getSource() == slFPS ){
      ctrl.setFPS(slFPS.getValue());
    }else if( ce.getSource() == slFrame ){
      ctrl.setFrame(slFrame.getValue());
    }else if( ce.getSource() == cbAlwaysTop ){
      vconf.isPopStatusAlwaysTop=cbAlwaysTop.isSelected();
      statusFrame.setAlwaysOnTop(vconf.isPopStatusAlwaysTop);
    }
  }




  private Controller ctrl;
  private ViewConfig vconf;
  //constructor
  public StatusPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    create();

    updateStatusFrame();
  }

  //global variables
  private JLabel lNatm,lBox1,lBox2,lBox3;
  private JLabel lFPS,lFrame;
  private JSlider slFrame,slFPS;
  private JButton popoutButton;
  private JCheckBox cbAlwaysTop;


  private JPanel sPanel;
  private JScrollPane stdOutErrPane;
  private JLabel stdOutErrLabel=new JLabel("STDOUT, STDERR is popouted");
  public JFrame statusFrame=new JFrame("Status Frame");

  public void create(){

    this.addKeyListener(ctrl.keyCtrl);

    statusFrame.addWindowListener(new java.awt.event.WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          vconf.rectStatusWin = statusFrame.getBounds();
          vconf.isPopStatus=false;
          updateStatusFrame();
        }
      });

    //General panel
    setFocusable( false );

    //-----left: num. of atoms, box matrix
    lNatm= new JLabel("Num. of atoms: ");
    lNatm.setFocusable(false);
    JLabel lBoxMat= new JLabel("Box matrix:");
    lBoxMat.setFocusable(false);
    lBox1=new JLabel("");
    lBox1.setFocusable(false);
    lBox2=new JLabel("");
    lBox2.setFocusable(false);
    lBox3=new JLabel("");
    lBox3.setFocusable(false);



    //-----center: frame No.
    lFrame= new JLabel("Frame No.: ");
    lFrame.setFocusable(false);
    slFrame = new JSlider();
    slFrame.setFocusable(false);
    slFrame.addChangeListener(this);
    slFrame.setMajorTickSpacing(1);
    slFrame.setPaintTicks(true);
    slFrame.setSnapToTicks(true);
    slFrame.setValue(0);
    slFrame.setMinimum(0);
    slFrame.setMaximum(10);

    lFPS= new JLabel("FPS: ");
    lFPS.setFocusable(false);
    slFPS = new JSlider();
    slFPS.setFocusable(false);
    slFPS.addChangeListener(this);
    slFPS.setValue(0);
    slFPS.setMinimum(0);
    slFPS.setMaximum(10);
    slFPS.setMajorTickSpacing(5);
    slFPS.setMinorTickSpacing(1);
    slFPS.setPaintTicks(true);
    slFPS.setSnapToTicks(true);

    popoutButton = new JButton( "Popout Status" );
    popoutButton.addActionListener( this );
    popoutButton.setFocusable(false);

    cbAlwaysTop =new JCheckBox("Always Top",vconf.isPopStatusAlwaysTop);
    cbAlwaysTop.setFocusable(false);
    cbAlwaysTop.addChangeListener(this);

    // top: num of atoms, box matrix
    JPanel topPanel= new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS));
    topPanel.add(lNatm);
    topPanel.add(lBoxMat);
    topPanel.add(lBox1);
    topPanel.add(lBox2);
    topPanel.add(lBox3);
    // middle: frame slider and fps slider
    JPanel bottomPanel= new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel,BoxLayout.Y_AXIS));
    bottomPanel.add(lFrame);
    bottomPanel.add(slFrame);
    bottomPanel.add(lFPS);
    bottomPanel.add(slFPS);

    // outout area
    JTextArea outArea = new JTextArea();
    outArea.setEditable(false);
    outArea.setLineWrap(true);
    outArea.setCaretPosition(outArea.getText().length());
    outArea.setFocusable(false);
    stdOutErrPane = new JScrollPane(outArea,
                                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    stdOutErrPane.setFocusable(false);
    sPanel= new JPanel();
    sPanel.setLayout(new GridLayout(1, 1));
    sPanel.add(stdOutErrPane);


    setLayout(new GridLayout(1, 1));
    //print streamを乗っ取る．かならずこの順で乗っ取る．
    System.setErr(new PrintStream(new JTextAreaOutputStream(System.out,outArea)));
    System.setOut(new PrintStream(new JTextAreaOutputStream(System.out,outArea)));

    topPanel.setPreferredSize(new Dimension(200, 100));
    bottomPanel.setPreferredSize(new Dimension(200, 100));

    //-----constants for TableLayout
    double f= TableLayout.FILL;
    double p= TableLayout.PREFERRED;
    double hb= 10;//10px
    //double colSizeTL[]= {hb,p,hb,p,hb,f,p,hb};
    double colSizeTL[]= {hb,f,hb};
    //double rowSizeTL[]= {hb,f,p,p,hb};
    double rowSizeTL[]= {hb,p,p,p,hb};
    

    setLayout(new TableLayout(colSizeTL,rowSizeTL));

    this.add(topPanel,    "1,1,l,b");
    this.add(bottomPanel,   "1,2,l,b");
    //this.add(sPanel,"5,1,5,3");
    this.add(cbAlwaysTop,  "1,3,l,b");
    //this.add(popoutButton, "6,3,r,b");

  }

  public class JTextAreaOutputStream extends OutputStream {
    private JTextArea ta;
    private PrintStream system;
    public JTextAreaOutputStream(PrintStream p,JTextArea t){
      super();
      ta = t;
      system=p;
    }
    public void write(int i){
      char[] chars = new char[1];
      chars[0] = (char) i;
      String s = new String(chars);
      ta.append(s);
      system.write(i);
      ta.setCaretPosition(ta.getText().length());
    }
    public void write(char[] buf, int off, int len){
      String s = new String(buf, off, len);
      ta.append(s);
      ta.setCaretPosition(ta.getText().length());
      //ps.write(buf,off,len);
    }
  }

  void updateStatusFrame(){
    if(vconf.isPopStatus){
      //frame popout
      statusFrame.setBounds(vconf.rectStatusWin);
      sPanel.remove(stdOutErrPane);
      sPanel.add(stdOutErrLabel);
      statusFrame.add(stdOutErrPane);
      statusFrame.setAlwaysOnTop(vconf.isPopStatusAlwaysTop);
      statusFrame.setVisible(true);
    }else{
      //set vcWin
      //vconf.rectStatusWin = statusFrame.getBounds();
      statusFrame.setVisible(false);
      statusFrame.remove(stdOutErrPane);
      sPanel.remove(stdOutErrLabel);
      sPanel.add(stdOutErrPane);
    }
    this.repaint();
  }

}
