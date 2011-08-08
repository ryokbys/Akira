package viewer.renderer;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

import viewer.*;

public class VolumeSlicer extends JFrame implements ActionListener,
                                                   ChangeListener,
                                                   KeyListener{
  int nx=30,ny=30,nSplit=4;
  //accesser starts
  public void setData(float[][] pVec,ArrayList<Float> point2D,float[] datarange){
    myCanv.setData(pVec, point2D,datarange);
    myCanv.setResolution(nx,ny,nSplit);
    myCanv.repaint();
  }

  public void setVisible(){
    if(isVisible()) setVisible( false );
    else setVisible( true );
  }
  //accesser ends


  //constructor
  ColorTable ctable;
  public VolumeSlicer(ViewConfig vconf,ColorTable ctable){
    this.ctable=ctable;
    createPanel();
    this.addKeyListener(this);
    this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    this.setBounds(vconf.rect2DPlot);
    this.setTitle("2D plot");
  }

  MyCanvas myCanv;
  JButton btnContour;
  JButton btnRawData;
  JButton btnDebug;
  JButton btnDrawtype;
  public void createPanel(){
    //canvas
    myCanv= new MyCanvas(ctable);
    myCanv.setPreferredSize(new Dimension(500, 500));
    myCanv.setBackground(Color.white);

    btnContour=new JButton("contour");
    btnContour.addActionListener( this );
    btnContour.setFocusable(false);

    btnRawData=new JButton("data");
    btnRawData.addActionListener( this );
    btnRawData.setFocusable(false);

    btnDebug=new JButton("debug");
    btnDebug.addActionListener( this );
    btnDebug.setFocusable(false);

    btnDrawtype=new JButton("draw type");
    btnDrawtype.addActionListener( this );
    btnDrawtype.setFocusable(false);


    JLabel lNX = new JLabel("Resolution: X");
    JSpinner spNX = new JSpinner(new SpinnerNumberModel(nx, 1, null, 5));
    spNX.setFocusable(false);
    spNX.setPreferredSize(new Dimension(65, 25));
    spNX.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e){
          JSpinner s = (JSpinner)e.getSource();
          nx=((Integer)s.getValue()).intValue();
          myCanv.setResolution(nx,ny,nSplit);
          myCanv.repaint();
        }});

    JLabel lNY = new JLabel("Resolution: Y");

    JSpinner spNY = new JSpinner(new SpinnerNumberModel(ny, 1, null, 5));
    spNY.setFocusable(false);
    spNY.setPreferredSize(new Dimension(65, 25));
    spNY.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e){
          JSpinner s = (JSpinner)e.getSource();
          ny=((Integer)s.getValue()).intValue();
          myCanv.setResolution(nx,ny,nSplit);
          myCanv.repaint();
        }});

    JLabel lSplit = new JLabel("# of split");
    JSpinner spNSplit = new JSpinner(new SpinnerNumberModel(nSplit, 1, null, 1));
    spNSplit.setFocusable(false);
    spNSplit.setPreferredSize(new Dimension(65, 25));
    spNSplit.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e){
          JSpinner s = (JSpinner)e.getSource();
          nSplit=((Integer)s.getValue()).intValue();
          myCanv.setResolution(nx,ny,nSplit);
          myCanv.repaint();
        }});

    JLabel lPointSize = new JLabel("Point size");
    JSpinner spPointSize = new JSpinner(new SpinnerNumberModel(9, 0, null, 1));
    spPointSize.setFocusable(false);
    spPointSize.setPreferredSize(new Dimension(65, 25));
    spPointSize.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e){
          JSpinner s = (JSpinner)e.getSource();
          myCanv.setPointSize((float)((Integer)s.getValue()).intValue());
          myCanv.repaint();
        }});


    JPanel jp=new JPanel();

    //add
    SpringLayout layout = new SpringLayout();
    jp.setLayout(layout);

    layout.putConstraint(SpringLayout.NORTH, lNX, 5, SpringLayout.NORTH, jp);
    layout.putConstraint(SpringLayout.WEST, lNX, 5, SpringLayout.WEST, jp);
    layout.putConstraint(SpringLayout.NORTH, lNY, 20, SpringLayout.SOUTH, lNX);
    layout.putConstraint(SpringLayout.WEST, lNY, 0, SpringLayout.WEST, lNX);
    layout.putConstraint(SpringLayout.NORTH, lSplit, 20, SpringLayout.SOUTH, lNY);
    layout.putConstraint(SpringLayout.WEST, lSplit, 0, SpringLayout.WEST, lNY);

    layout.putConstraint(SpringLayout.NORTH, lPointSize, 20, SpringLayout.SOUTH, lSplit);
    layout.putConstraint(SpringLayout.WEST, lPointSize, 0, SpringLayout.WEST, lSplit);

    layout.putConstraint(SpringLayout.NORTH, spNX, 0, SpringLayout.NORTH, lNX);
    layout.putConstraint(SpringLayout.WEST, spNX, 5, SpringLayout.EAST, lNX);
    layout.putConstraint(SpringLayout.NORTH, spNY, 0, SpringLayout.NORTH, lNY);
    layout.putConstraint(SpringLayout.WEST, spNY, 0, SpringLayout.WEST, spNX);
    layout.putConstraint(SpringLayout.NORTH, spNSplit, 0, SpringLayout.NORTH, lSplit);
    layout.putConstraint(SpringLayout.WEST, spNSplit, 0, SpringLayout.WEST, spNX);

    layout.putConstraint(SpringLayout.NORTH, spPointSize, 0, SpringLayout.NORTH, lPointSize);
    layout.putConstraint(SpringLayout.WEST, spPointSize, 0, SpringLayout.WEST, spNX);



    layout.putConstraint(SpringLayout.SOUTH, myCanv, -10, SpringLayout.SOUTH, jp);
    layout.putConstraint(SpringLayout.NORTH, myCanv, 5, SpringLayout.NORTH, jp);
    layout.putConstraint(SpringLayout.EAST, myCanv, -20, SpringLayout.EAST, jp);
    layout.putConstraint(SpringLayout.WEST, myCanv, 30, SpringLayout.EAST, spNX);

    layout.putConstraint(SpringLayout.NORTH,btnContour, 10, SpringLayout.SOUTH, spPointSize);
    layout.putConstraint(SpringLayout.WEST,btnContour, 0, SpringLayout.WEST, lSplit);
    layout.putConstraint(SpringLayout.NORTH,btnRawData, 5, SpringLayout.SOUTH, btnContour);
    layout.putConstraint(SpringLayout.WEST,btnRawData, 0, SpringLayout.WEST, btnContour);
    layout.putConstraint(SpringLayout.NORTH,btnDrawtype, 5, SpringLayout.SOUTH, btnRawData);
    layout.putConstraint(SpringLayout.WEST,btnDrawtype, 0, SpringLayout.WEST, btnRawData);
    layout.putConstraint(SpringLayout.NORTH,btnDebug, 5, SpringLayout.SOUTH, btnDrawtype);
    layout.putConstraint(SpringLayout.WEST,btnDebug, 0, SpringLayout.WEST, btnDrawtype);

    jp.add(lNX);
    jp.add(spNX);
    jp.add(lNY);
    jp.add(spNY);
    jp.add(lSplit);
    jp.add(spNSplit);
    jp.add(lPointSize);
    jp.add(spPointSize);

    jp.add(btnContour);
    jp.add(btnRawData);
    jp.add(btnDebug);
    jp.add(btnDrawtype);
    jp.add(myCanv);

    add(jp);

    requestFocusInWindow();
  }

  public void stateChanged( ChangeEvent ce ){
  }

  public void actionPerformed( ActionEvent ae ){
    if( ae.getSource() == btnRawData){
      myCanv.visibleRawData();
    }else if( ae.getSource() == btnContour){
      myCanv.visibleContour();
    }else if( ae.getSource() == btnDrawtype){
      myCanv.changeDrawtype();
    }else if( ae.getSource() == btnDebug){
      myCanv.changeDebug();
    }

    myCanv.repaint();
    requestFocusInWindow();
  }


  public void keyReleased( KeyEvent ke ){
  }
  public void keyTyped( KeyEvent ke ){
  }
  public void keyPressed( KeyEvent ke ){
    switch ( ke.getKeyCode() ){
    case KeyEvent.VK_ESCAPE:
      System.exit(0);
    }
  }

}

///////////////////////////////////////////////////////////////////
class MyCanvas extends Canvas{
  ColorTable ctable;
  public MyCanvas(ColorTable ctable){
    this.ctable=ctable;
  }
  float pointSize=9.f;
  public void setPointSize(float p){
    this.pointSize=p;
  }

  int drawType=1;
  public void changeDrawtype(){
    drawType++;
    if(drawType>2)drawType=0;
  }
  boolean isDebug=false;
  public void changeDebug(){
    isDebug=!isDebug;
  }
  boolean isVisibleRawData=true;
  public void visibleRawData(){
    isVisibleRawData=!isVisibleRawData;
  }
  boolean isVisibleContour=false;
  public void visibleContour(){
    isVisibleContour=!isVisibleContour;
  }

  ArrayList<Float> pp= new ArrayList<Float>();
  float[][] range=new float[3][2];
  float p,q;
  public void setData(float[][] pVec,ArrayList<Float> point2D,float[] datarange){
    //determine 2d primitive vector
    p=pVec[0][0]*pVec[1][0]+pVec[0][1]*pVec[1][1]+pVec[0][2]*pVec[1][2];

    q=(float)Math.sqrt(1-p*p);
    //q=-(float)Math.sqrt(1-p*p);
    float[][] e={
      {1.f,0.f},
      {p,q}
    };


    range[0][0]=100000.f;
    range[0][1]=-100000.f;
    range[1][0]=100000.f;
    range[1][1]=-100000.f;
    range[2][0]=datarange[0];
    range[2][1]=datarange[1];

    pp.clear();
    for(int i=0;i<point2D.size()/3;i++){
      float x=point2D.get(3*i)*e[0][0]+point2D.get(3*i+1)*e[1][0];
      float y=point2D.get(3*i)*e[0][1]+point2D.get(3*i+1)*e[1][1];
      float z=point2D.get(3*i+2);
      pp.add(x);
      pp.add(y);
      pp.add(z);
      if(range[0][0]>x)range[0][0]=x;
      if(range[0][1]<x)range[0][1]=x;
      if(range[1][0]>y)range[1][0]=y;
      if(range[1][1]<y)range[1][1]=y;
    }
  }

  float[] targetVal={0.6f,0.3f,-0.3f,-0.6f};
  int nx=30;
  int ny=30;
  public void setResolution(int nx,int ny,int nSplit){
    this.nx=nx;
    this.ny=ny;
    targetVal=null;
    targetVal=new float[nSplit];
    for(int i=0;i<targetVal.length;i++){
      targetVal[i]=(range[2][1]-range[2][0])*i/(float)nSplit;
    }
  }

  public void paint(Graphics g){
    int width=getWidth();
    int height=getHeight();

    Graphics2D g2 = (Graphics2D)g;
    //refresh
    g2.setBackground(Color.WHITE);
    g2.clearRect(0,0,width,height);

    //antialias on
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);



    float[] voxel=new float[nx*ny];
    int[] count=new int[nx*ny];
    float dx=width/(float)nx;
    float dy=height/(float)ny;


    if(isDebug){
      g2.setStroke(new BasicStroke(4.0f));
      g2.setPaint(Color.BLUE);
      float scale=20.f;
      g2.draw(new Line2D.Float(width/2,height/2,width/2+1.f*scale,height/2+0.f));
      g2.draw(new Line2D.Float(width/2,height/2,width/2+p*scale,height/2+q*scale));
    }

    for(int i=0;i<pp.size()/3;i++){
      float x0=(pp.get(3*i)-range[0][0])/(range[0][1]-range[0][0])*width;
      float y0=(pp.get(3*i+1)-range[1][0])/(range[1][1]-range[1][0])*height;

      //draw raw data
      if(isVisibleRawData){
        float[] cl=ctable.getColor(pp.get(3*i+2));
        g2.setPaint(new Color(cl[0],cl[1],cl[2]));
        g2.fill(new Rectangle2D.Float(x0,(float)height-y0,pointSize, pointSize));
      }

      //averaging
      int ix=(int)(x0/dx);
      int iy=(int)(y0/dy);
      if(ix<0)ix=0;
      if(iy<0)iy=0;
      if(ix>=nx)ix=nx-1;
      if(iy>=ny)iy=ny-1;
      int ir=iy+ny*ix;
      voxel[ir]+=pp.get(3*i+2);
      count[ir]++;
    }

    for(int i=0;i<nx;i++)
      for(int j=0;j<ny;j++)
        if(count[i]!=0)voxel[i]/=(float)count[i];

    //contour
    if(isVisibleContour){
      for(int ix=0;ix<nx;ix++){
        for(int iy=0;iy<ny;iy++){
          //plus
          int ixp=ix+1;
          int iyp=iy+1;
          //minus
          int ixm=ix-1;
          int iym=iy-1;

          /*
           * order of vertex
           *       3-----2
           *       |     |
           *       |  p  |
           *       |     |
           * 1-----0-----1
           * |     |
           * |  m  |
           * |     |
           * 2-----3
           */
          float x0=ix*dx;
          float y0=iy*dy;
          float xp1=ixp*dx;
          float yp1=iy*dy;
          float xp2=ixp*dx;
          float yp2=iyp*dy;
          float xp3=ix*dx;
          float yp3=iyp*dy;
          float xm1=ixm*dx;
          float ym1=iy*dy;
          float xm2=ixm*dx;
          float ym2=iym*dy;
          float xm3=ix*dx;
          float ym3=iym*dy;

          //grid for debug
          if(isDebug){
            g2.setStroke(new BasicStroke(1.0f));
            g2.setPaint(Color.BLACK);
            g2.draw(new Line2D.Float(x0,height-y0,xp1,height-yp1));
            g2.draw(new Line2D.Float(x0,height-y0,xp3,height-yp3));
            g2.draw(new Line2D.Float(xp1,height-yp1,xp3,height-yp3));
          }

          g2.setStroke(new BasicStroke(2.0f));
          if(drawType==0){ //most accurate rendering?
            if(ixp<nx && iyp<ny){
              float[][] point={
                {width*ix/(float)nx,height*(ny-iy)/(float)ny,voxel[iy+ny*ix]},
                {width*ixp/(float)nx,height*(ny-iy)/(float)ny,voxel[iy+ny*ixp]},
                {width*ixp/(float)nx,height*(ny-iyp)/(float)ny,voxel[iyp+ny*ixp]},
                {width*ix/(float)nx,height*(ny-iyp)/(float)ny,voxel[iyp+ny*ix]}
              };
              for(int i=0;i<targetVal.length;i++){
                float[] cl=ctable.getColor(targetVal[i]);
                g2.setPaint(new Color(cl[0],cl[1],cl[2]));
                drawContour2(g2,targetVal[i],point);
              }
            }
          }else{ //standard type
            if(ixp<nx && iyp<ny){
              float[][] point1={{x0,height-y0,voxel[iy+ny*ix]},
                                {xp1,height-yp1,voxel[iy+ny*ixp]},
                                {xp3,height-yp3,voxel[iyp+ny*ix]}};
              for(int i=0;i<targetVal.length;i++){
                float[] cl=ctable.getColor(targetVal[i]);
                g2.setPaint(new Color(cl[0],cl[1],cl[2]));
                drawContour(g2,targetVal[i],point1);
              }
              if(drawType==2){
                float[][] point2={{x0,height-y0,voxel[iy+ny*ix]},
                                  {xp1,height-yp1,voxel[iy+ny*ixp]},
                                  {xp2,height-yp2,voxel[iyp+ny*ixp]}};
                for(int i=0;i<targetVal.length;i++){
                  float[] cl=ctable.getColor(targetVal[i]);
                  g2.setPaint(new Color(cl[0],cl[1],cl[2]));
                  drawContour(g2,targetVal[i],point2);
                }
              }
            }
            if(ixm>0  && iym>0 ){
              float[][] point1={{x0,height-y0,voxel[iy+ny*ix]},
                                {xm1,height-ym1,voxel[iy+ny*ixm]},
                                {xm3,height-ym3,voxel[iym+ny*ix]}};
              for(int i=0;i<targetVal.length;i++){
                float[] cl=ctable.getColor(targetVal[i]);
                g2.setPaint(new Color(cl[0],cl[1],cl[2]));
                drawContour(g2,targetVal[i],point1);
              }
              if(drawType==2){
                float[][] point2={{x0,height-y0,voxel[iy+ny*ix]},
                                  {xm1,height-ym1,voxel[iy+ny*ixm]},
                                  {xm2,height-ym2,voxel[iym+ny*ixm]}};
                for(int i=0;i<targetVal.length;i++){
                  float[] cl=ctable.getColor(targetVal[i]);
                  g2.setPaint(new Color(cl[0],cl[1],cl[2]));
                  drawContour(g2,targetVal[i],point2);
                }
              }
            }
          }//end of if
        }//end of iy
      }//end of ix
    }
  }

  /**
   * draws line segment of contour
   */
  void drawContour(Graphics2D g2,float targetVal,float[][] point){
    //search triangle(point)
    boolean[] flags = {
      (point[0][2] <= targetVal && targetVal <= point[1][2])
      || (point[1][2] <= targetVal && targetVal <= point[0][2]),
      (point[1][2] <= targetVal && targetVal <= point[2][2])
      || (point[2][2] <= targetVal && targetVal <= point[1][2]),
      (point[2][2] <= targetVal && targetVal <= point[0][2])
      || (point[0][2] <= targetVal && targetVal <= point[2][2])};

    float ratio,x1,y1,x2,y2;
    //P0 and P1
    if(flags[0] && flags[1]){
      if(point[1][2] != point[0][2] && point[2][2] != point[1][2]){
        ratio = (targetVal - point[0][2]) / (point[1][2] - point[0][2]);
        x1 = ratio * (point[1][0] - point[0][0]) + point[0][0];
        y1 = ratio * (point[1][1] - point[0][1]) + point[0][1];
        ratio = (targetVal - point[1][2]) / (point[2][2] - point[1][2]);
        x2 = ratio * (point[2][0] - point[1][0]) + point[1][0];
        y2 = ratio * (point[2][1] - point[1][1]) + point[1][1];
        g2.draw(new Line2D.Float(x1,y1,x2,y2));
      }
      /*
       * else{
       *   x1=point[0][0];
       *   y1=point[0][1];
       *   x2=point[1][0];
       *   y2=point[1][1];
       * g2.draw(new Line2D.Float(x1,y1,x2,y2));
       * }
       */
    }
    //P1 and P2
    if(flags[1] && flags[2]){
      if( point[2][2] != point[1][2] && point[2][2] != point[0][2]){
        ratio = (targetVal - point[1][2]) / (point[2][2] - point[1][2]);
        x1 = ratio * (point[2][0] - point[1][0]) + point[1][0];
        y1 = ratio * (point[2][1] - point[1][1]) + point[1][1];
        ratio = (targetVal - point[2][2]) / (point[0][2] - point[2][2]);
        x2 = ratio * (point[0][0] - point[2][0]) + point[2][0];
        y2 = ratio * (point[0][1] - point[2][1]) + point[2][1];
        g2.draw(new Line2D.Float(x1,y1,x2,y2));
      }
      /*
       * else{
       *   x1=point[1][0];
       *   y1=point[1][1];
       *   x2=point[2][0];
       *   y2=point[2][1];
       * g2.draw(new Line2D.Float(x1,y1,x2,y2));
       * }
       */
    }
    //P2 and P0
    if(flags[2] && flags[0]){
      if( point[0][2] != point[2][2] && point[1][2] != point[0][2]){
        ratio = (targetVal - point[2][2]) / (point[0][2] - point[2][2]);
        x1 = ratio * (point[0][0] - point[2][0]) + point[2][0];
        y1 = ratio * (point[0][1] - point[2][1]) + point[2][1];
        ratio = (targetVal - point[0][2]) / (point[1][2] - point[0][2]);
        x2 = ratio * (point[1][0] - point[0][0]) + point[0][0];
        y2 = ratio * (point[1][1] - point[0][1]) + point[0][1];
        g2.draw(new Line2D.Float(x1,y1,x2,y2));
      }
      /*
       * else{
       *   x1=point[2][0];
       *   y1=point[2][1];
       *   x2=point[0][0];
       *   y2=point[0][1];
       *   g2.draw(new Line2D.Float(x1,y1,x2,y2));
       * }
       */
    }
  }


  //vertex
  /*
   * 3-----2
   * |\   /|
   * |  4  |
   * |/   \|
   * 0-----1
   *
   */
  int[][] triangleVertex={
    {0,1,4},
    {1,2,4},
    {2,3,4},
    {3,0,4},
  };
  void drawContour2(Graphics2D g2,float targetVal,float[][] point){
    //search 4 triangle in square

    for(int itriangle=0;itriangle<4;itriangle++){
      int i0=triangleVertex[itriangle][0];
      int i1=triangleVertex[itriangle][1];

      //opposite corner
      int iop=i1+1;
      if(iop>3)iop=0;

      float[] cntrPoint={(point[i0][0]+point[iop][0])*0.5f,
                         (point[i0][1]+point[iop][1])*0.5f,
                         (point[i0][2]+point[iop][2])*0.5f};

      boolean[] flags = {
        (point[i0][2] <= targetVal && targetVal <= point[i1][2])
        || (point[i1][2] <= targetVal && targetVal <= point[i0][2]),
        (point[i1][2] <= targetVal && targetVal <= cntrPoint[2])
        || (cntrPoint[2] <= targetVal && targetVal <= point[i1][2]),
        (cntrPoint[2] <= targetVal && targetVal <= point[i0][2])
        || (point[i0][2] <= targetVal && targetVal <= cntrPoint[2])};

      float ratio,x1,y1,x2,y2;
      // Pi0 and Pi1
      if(flags[0] && flags[1]){
        if(point[i1][2] != point[i0][2] && cntrPoint[2] != point[i1][2]){
          ratio = (targetVal - point[i0][2]) / (point[i1][2] - point[i0][2]);
          x1 = ratio * (point[i1][0] - point[i0][0]) + point[i0][0];
          y1 = ratio * (point[i1][1] - point[i0][1]) + point[i0][1];
          ratio = (targetVal - point[i1][2]) / (cntrPoint[2] - point[i1][2]);
          x2 = ratio * (cntrPoint[0] - point[i1][0]) + point[i1][0];
          y2 = ratio * (cntrPoint[1] - point[i1][1]) + point[i1][1];
          g2.draw(new Line2D.Float(x1,y1,x2,y2));
        }
        /*
         * else{
         *   x1=point[i0][0];
         *   y1=point[i0][1];
         *   x2=point[i1][0];
         *   y2=point[i1][1];
         * g2.draw(new Line2D.Float(x1,y1,x2,y2));
         * }
         */
      }
      // Pi1 and Pcenter
      if(flags[1] && flags[2]){
        if( cntrPoint[2] != point[i1][2] && cntrPoint[2] != point[i0][2]){
          ratio = (targetVal - point[i1][2]) / (cntrPoint[2] - point[i1][2]);
          x1 = ratio * (cntrPoint[0] - point[i1][0]) + point[i1][0];
          y1 = ratio * (cntrPoint[1] - point[i1][1]) + point[i1][1];
          ratio = (targetVal - cntrPoint[2]) / (point[i0][2] - cntrPoint[2]);
          x2 = ratio * (point[i0][0] - cntrPoint[0]) + cntrPoint[0];
          y2 = ratio * (point[i0][1] - cntrPoint[1]) + cntrPoint[1];
          g2.draw(new Line2D.Float(x1,y1,x2,y2));
        }
        /*
         * else{
         *   x1=point[i1][0];
         *   y1=point[i1][1];
         *   x2=cntrPoint[0];
         *   y2=cntrPoint[1];
         *   g2.draw(new Line2D.Float(x1,y1,x2,y2));
         * }
         */
      }

      // Pcenter and Pi0
      if(flags[2] && flags[0]){
        if( point[i0][2] != cntrPoint[2] && point[i1][2] != point[i0][2]){
          ratio = (targetVal - cntrPoint[2]) / (point[i0][2] - cntrPoint[2]);
          x1 = ratio * (point[i0][0] - cntrPoint[0]) + cntrPoint[0];
          y1 = ratio * (point[i0][1] - cntrPoint[1]) + cntrPoint[1];
          ratio = (targetVal - point[i0][2]) / (point[i1][2] - point[i0][2]);
          x2 = ratio * (point[i1][0] - point[i0][0]) + point[i0][0];
          y2 = ratio * (point[i1][1] - point[i0][1]) + point[i0][1];
          g2.draw(new Line2D.Float(x1,y1,x2,y2));
        }
        /*
         * else{
         *   x1=point[i0][0];
         *   y1=point[i0][1];
         *   x2=cntrPoint[0];
         *   y2=cntrPoint[1];
         *   g2.draw(new Line2D.Float(x1,y1,x2,y2));
         * }
         */
      }
    }
  }
}
