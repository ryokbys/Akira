package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import data.*;
import tools.*;
import viewer.*;
import viewer.renderer.*;

public class RadialDistributionPanel extends JPanel implements ActionListener,
                                                               ChangeListener{


  private RDCanvas rdCanv;
  private Controller ctrl;
  public RadialDistributionPanel(Controller ctrl){
    this.ctrl=ctrl;
    createPanel();
  }



  private MyRender myRender;
  private Container pane;
  private JLabel lSlice;
  private JSpinner spSliceVal;
  private SpinnerNumberModel spSliceValModel;
  private JLabel lRcut;
  private JSpinner spRcut;
  private SpinnerNumberModel spnmRcut;
  private JButton btnCal;
  private JButton btnPause;
  private JButton btnResume;
  private JButton btnWrite;
  private JButton btnClear;
  private JProgressBar pbCal;

  private void createPanel(){
    //slice
    lSlice = new JLabel();
    lSlice.setText( "Slice" );

    //
    spSliceValModel=new SpinnerNumberModel((double)ctrl.vconf.rdSlice, 0.1, null, 0.1);
    spSliceVal = new JSpinner(spSliceValModel);
    spSliceVal.setPreferredSize(new Dimension(70, 30));
    spSliceVal.setFocusable(false);
    spSliceVal.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e){
          JSpinner s = (JSpinner)e.getSource();
          ctrl.vconf.rdSlice=((Double)s.getValue()).floatValue();
          requestFocusInWindow();
        }});
    requestFocusInWindow();


    lRcut=new JLabel();
    lRcut.setText( "rcut" );

    //
    spnmRcut=new SpinnerNumberModel((double)ctrl.vconf.rdCut, 0.1, null, 1);
    spRcut = new JSpinner(spnmRcut);
    spRcut.setPreferredSize(new Dimension(70, 30));
    spRcut.setFocusable(false);
    spRcut.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e){
          JSpinner s = (JSpinner)e.getSource();
          ctrl.vconf.rdCut=((Double)s.getValue()).floatValue();
          requestFocusInWindow();
        }});


    //canvas
    rdCanv= new RDCanvas();
    rdCanv.setPreferredSize(new Dimension(500, 150));
    rdCanv.setBackground(Color.white);

    //calculate button
    btnCal=new JButton("Cal.");
    btnCal.setFocusable(false);
    btnCal.addActionListener( this );
    //clear button
    btnClear=new JButton("Clear");
    btnClear.setFocusable(false);
    btnClear.addActionListener( this );
    //write data
    btnWrite=new JButton("Write");
    btnWrite.setFocusable(false);
    btnWrite.addActionListener( this );
    //stop data
    btnPause=new JButton("Pause");
    btnPause.setFocusable(false);
    btnPause.addActionListener( this );
    //resume data
    btnResume=new JButton("Resume");
    btnResume.setFocusable(false);
    btnResume.addActionListener( this );

    //progressbar
    pbCal=new JProgressBar();
    pbCal.setFocusable(false);
    pbCal.setPreferredSize(new Dimension(200,25));
    pbCal.setStringPainted(true);
    pbCal.setMinimum(0);


    SpringLayout layout = new SpringLayout();
    this.setLayout( layout );

    //value
    layout.putConstraint( SpringLayout.NORTH, spRcut, 10,SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.EAST, spRcut, -10,SpringLayout.EAST, this);
    layout.putConstraint( SpringLayout.NORTH, lRcut, 0,SpringLayout.NORTH, spRcut);
    layout.putConstraint( SpringLayout.EAST, lRcut, 0,SpringLayout.WEST, spRcut);

    layout.putConstraint( SpringLayout.NORTH, spSliceVal, 0,SpringLayout.NORTH, lRcut);
    layout.putConstraint( SpringLayout.EAST, spSliceVal, -5,SpringLayout.WEST, lRcut);
    layout.putConstraint( SpringLayout.NORTH, lSlice, 0, SpringLayout.NORTH, spSliceVal);
    layout.putConstraint( SpringLayout.EAST, lSlice, 0,SpringLayout.WEST, spSliceVal);

    //btn
    layout.putConstraint( SpringLayout.NORTH, btnResume, 10, SpringLayout.SOUTH, spRcut);
    layout.putConstraint( SpringLayout.EAST, btnResume, -10, SpringLayout.EAST, this);
    layout.putConstraint( SpringLayout.NORTH, btnPause , 0, SpringLayout.NORTH, btnResume);
    layout.putConstraint( SpringLayout.EAST, btnPause , 0, SpringLayout.WEST, btnResume);
    layout.putConstraint( SpringLayout.NORTH, btnCal, 0, SpringLayout.NORTH, btnPause);
    layout.putConstraint( SpringLayout.EAST, btnCal, 0,SpringLayout.WEST, btnPause);

    //prograss bar
    layout.putConstraint( SpringLayout.NORTH, pbCal, 5,SpringLayout.SOUTH, btnCal);
    layout.putConstraint( SpringLayout.EAST, pbCal, -10,SpringLayout.EAST, this);
    layout.putConstraint( SpringLayout.WEST, pbCal, 10,SpringLayout.EAST, rdCanv);

    //btn
    layout.putConstraint( SpringLayout.SOUTH, btnClear, -10, SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.EAST, btnClear, -10, SpringLayout.EAST, this);
    layout.putConstraint( SpringLayout.SOUTH, btnWrite, 0, SpringLayout.SOUTH, btnClear);
    layout.putConstraint( SpringLayout.EAST, btnWrite, 0,SpringLayout.WEST, btnClear);


    //canvas
    layout.putConstraint( SpringLayout.SOUTH, rdCanv, -10, SpringLayout.SOUTH, this );
    layout.putConstraint( SpringLayout.NORTH, rdCanv, 10, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.EAST, rdCanv, -10,SpringLayout.WEST, btnCal);
    layout.putConstraint( SpringLayout.WEST, rdCanv, 10,SpringLayout.WEST, this );


    this.add(lSlice);
    this.add(spSliceVal);
    this.add(lRcut);
    this.add(spRcut);

    this.add(btnClear);
    this.add(btnWrite);
    this.add(btnCal);
    this.add(btnPause);
    this.add(btnResume);
    this.add(pbCal);
    this.add(rdCanv);

    requestFocusInWindow();
  }


  public void stateChanged( ChangeEvent ce ){
    requestFocusInWindow();
  }

  public void actionPerformed( ActionEvent e ){
    if( e.getSource() == btnCal ){
      myRender = new MyRender();
      myRender.start();
    }else if( e.getSource() == btnPause ){
      try{
        myRender.wait();
      }catch (InterruptedException ie){
        System.out.println(ie);
      }
    }else if( e.getSource() == btnResume ){
      myRender.notify();
    }else if( e.getSource() == btnClear ){
      nc=0;
      for(int i=0;i<ncmax;i++) count[i]=0;
      rdCanv.paint(rdCanv.getGraphics());
      pbCal.setValue(0);
    }else if( e.getSource() == btnWrite ){
      write();
    }

    requestFocusInWindow();
  }

  private viewer.renderer.Atoms atoms;
  class MyRender extends Thread {
    public void run(){
      atoms=ctrl.getActiveRW().atoms;
      pbCal.setValue(0);
      pbCal.setMaximum(atoms.n-1);
      calRadialDis();
      rdCanv.setData(ncmax,nc,count,ctrl.vconf.rdSlice,ctrl.vconf.rdCut);
      //rdCanv.paint(rdCanv.getGraphics());
      rdCanv.repaint();
      pbCal.setValue(atoms.n-1);
    }
  }




  //max of slice count array
  int ncmax=10000;
  float[] count = new float[ncmax];
  //sampling distance
  int nc=0;
  private void calRadialDis(){
    int idr;
    int nout;
    //#th frame
    float[] d = new float[3];
    float[] dd = new float[3];
    float dr, r2,volume;
    float diameter=1.f;



    for(int i=0;i<3;i++){
      if(diameter<atoms.h[i][i])diameter=atoms.h[i][i];
    }

    float radius=diameter*0.5f;

    //# of slice
    float slice=ctrl.vconf.rdSlice;
    nc=(int)(radius/slice);
    if(nc>ncmax) System.out.println("nc overflow");
    //System.out.println(String.format( "%d %d %f %f",ifrm,nc,slice,radius) );

    //init
    for(int i=0;i<ncmax;i++) count[i]=0;

    //volume
    volume=
      atoms.h[0][0]*(atoms.h[1][1]*atoms.h[2][2]-atoms.h[2][1]*atoms.h[1][2])
      +atoms.h[1][1]*(atoms.h[1][2]*atoms.h[2][0]-atoms.h[2][0]*atoms.h[1][2])
      +atoms.h[2][2]*(atoms.h[1][0]*atoms.h[2][1]-atoms.h[2][1]*atoms.h[1][0]);

    //factor
    float fac=2.f/(atoms.n*(atoms.n-1))/(4.f*3.1415f)*volume*slice;

    //refresh interval
    nout=atoms.n/100;
    if(nout<1)nout=1;

    //brute force

     for(int i=0;i<atoms.n-1;i++){
       int itag=atoms.tag[i]-1;
       if(!ctrl.vconf.tagOnOff[itag])continue;
       if(atoms.vtag[i]<0)continue;

        for(int j=i+1;j<atoms.n;j++){
          int jtag=atoms.tag[j]-1;
          if(!ctrl.vconf.tagOnOff[jtag])continue;

          if(atoms.vtag[j]<0)continue;

        //consider PBC
        for(int k=0;k<3;k++){
          dd[k]=0.f;
          for(int l=0;l<3;l++)dd[k]+=atoms.hinv[k][l]*(atoms.r[j][l]-atoms.r[i][l]);
          if(dd[k]>0.5f)dd[k]-=1.f;
          if(dd[k]<-0.5f)dd[k]+=1.f;
        }
        //convert physical unit
        for(int k=0;k<3;k++){
          d[k]=0.f;
          for(int l=0;l<3;l++)d[k]+=atoms.h[k][l]*dd[l];
        }
        //NOT CONSIDER BOUNDARY
        /*
         * for(int k=0;k<3;k++)
         *     d[k]=atoms.p[j][k]-atoms.p[i][k];
         */

        r2 = d[0]*d[0] + d[1]*d[1] + d[2]*d[2];
        dr = (float)Math.sqrt( r2 );
        if(dr>ctrl.vconf.rdCut)continue;
        idr=(int)(dr/slice);

        if(idr>0)count[idr]+= fac/(float)idr/(float)idr;
        /* which means
         * count[idr]+= 2.f/slice/(idr*slice)/(idr*slice)
         *     *volume/(atoms.n*(atoms.n-1))/(4.f*3.1415f);
         */
      }
      //update progress bar
      if(i%nout==0 )pbCal.setValue(i);
    }
  }





  int filenum=0;
  private void write(){
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    String filename=ctrl.getActiveRW().getFileDirectory()+File.separator+String.format("%03dRD.d",filenum);
    filenum++;

    try {
      //open
      fw = new FileWriter( filename );
      bw = new BufferedWriter( fw );
      pw = new PrintWriter( bw );

      //write
      for(int i=1;i<nc;i++)
        pw.println( String.format( "%f %f",i*ctrl.vconf.rdSlice,count[i]) );

      //close
      pw.close();
      bw.close();
      fw.close();
    }
    catch ( IOException e ){
    }
  }





}

///////////////////////////////////////////////////////////////////
class RDCanvas extends JPanel{
  int ncmax;
  int nc;
  float[] count;
  float max;
  float slice;
  float rcut;
  public void setData(int ncmax,int nc, float[] count,float slice, float rcut){
    this.ncmax=ncmax;
    this.nc=nc;
    this.count=count;
    max=-1.f;
    for(int i=1;i<nc;i++) if(count[i]>max)max=count[i];
    this.slice=slice;
    this.rcut=rcut;
  }

  public void paintComponent(Graphics g) {
    int width=getWidth();
    int height=getHeight();

    int dis=height/10*2;
    int base=height-dis;
    int top=base-dis;

    //refresh
    g.setColor(Color.white);
    g.fillRect(0,0,width,height);
    //g.clearRect(0,0,width,height);

    //base line
    g.setColor(Color.black);
    g.drawLine(0,base,width,base);

    //unit line
    g.setColor(Color.red);
    g.drawLine(0,base-(int)(top/max),width,base-(int)(top/max));

    //g(r)

    int n=0;
    for(int i=0;i<nc;i++){
      n=i;
      if(i*slice>rcut)break;
    }
    for(int i=0;i<n;i++){
      g.setColor(Color.blue);
      int x1=(int)(width*i/(float)n);
      int y1=base-(int)(top*count[i]/max);
      int x2=(int)(width*(i+1)/(float)n);
      int y2=base-(int)(top*count[i+1]/max);
      g.drawLine(x1,y1,x2,y2);

      //x-tics
      g.setColor(Color.black);
      int r=3;
      if(i%5==0){
        String str=String.format("%.4f",i*slice);
        int offset=12;
        Graphics2D g2 = (Graphics2D)g;
        g2.drawString(str, x1, base+offset);

        g.drawLine(x1,base+r*2,x1,base-r*2);
      }else{
        g.drawLine(x1,base+r,x1,base-r);
      }

    }


  }


}
