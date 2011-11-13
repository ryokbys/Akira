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

public class NeighborAnalysisPanel extends JPanel implements ActionListener{

  private Controller ctrl;
  private ViewConfig vconf;
  public NeighborAnalysisPanel(Controller ctrl){
    this.ctrl=ctrl;
    this.vconf=ctrl.vconf;
    createPanel();
  }

  private JButton cnaButton,ringButton,aveButton,threadRingButton,sfButton;
  private JSpinner spRcut;
  private JTextArea outArea;
  private JCheckBox cbShowRing;
  private JSpinner spRingRangeMax;
  private JComboBox cmbRingType;
  private JCheckBox cbPBC;

  private static float[] mulH( float[][] h,float[] in ){
    float[] out = new float[3];
    for(int k=0; k<3; k++)
      out[k] = (h[k][0]*in[0] +h[k][1]*in[1] +h[k][2]*in[2]);
    return out;
  }

  private void createPanel(){
    this.addKeyListener(ctrl.keyCtrl);

    cbShowRing =new JCheckBox("Show Ring",vconf.isShowRing);
    cbShowRing.setFocusable(false);
    cbShowRing.addActionListener( this );
    cbShowRing.addKeyListener(ctrl.keyCtrl);

    cbPBC =new JCheckBox("PBC?",vconf.isPBC);
    cbPBC.setFocusable(false);
    cbPBC.addActionListener( this );
    cbPBC.addKeyListener(ctrl.keyCtrl);

    spRingRangeMax=new JSpinner(new SpinnerNumberModel(vconf.ringRangeMax, 3, null, 1));
    spRingRangeMax.setFocusable(false);
    spRingRangeMax.setPreferredSize(new Dimension(45, 23));
    spRingRangeMax.addKeyListener(ctrl.keyCtrl);


    String[] str={"Normal","SiO2"};
    cmbRingType= new JComboBox(str);
    cmbRingType.setSelectedIndex(vconf.ringCalType);
    cmbRingType.addActionListener(this);
    cmbRingType.addKeyListener(ctrl.keyCtrl);


    cnaButton=new JButton("CNA");
    cnaButton.setFocusable(false);
    cnaButton.addActionListener( this );
    cnaButton.addKeyListener(ctrl.keyCtrl);

    ringButton=new JButton("Ring");
    ringButton.setFocusable(false);
    ringButton.addActionListener( this );
    ringButton.addKeyListener(ctrl.keyCtrl);

    threadRingButton=new JButton("Thread Ring");
    threadRingButton.setFocusable(false);
    threadRingButton.addActionListener( this );
    threadRingButton.addKeyListener(ctrl.keyCtrl);


    aveButton=new JButton("Average");
    aveButton.setFocusable(false);
    aveButton.addActionListener( this );
    aveButton.addKeyListener(ctrl.keyCtrl);

    sfButton=new JButton("Structure Factor");
    sfButton.setFocusable(false);
    sfButton.addActionListener( this );
    sfButton.addKeyListener(ctrl.keyCtrl);

    spRcut = new JSpinner(new SpinnerNumberModel((double)ctrl.vconf.neighborAnalysisRcut, 0.1, null, 0.2));
    spRcut.setPreferredSize(new Dimension(60, 23));
    spRcut.setFocusable(false);
    spRcut.addKeyListener(ctrl.keyCtrl);

    //text area
    outArea = new JTextArea();
    outArea.setEditable(false);
    outArea.setLineWrap(true);
    outArea.setCaretPosition(outArea.getText().length());
    JScrollPane sp=new JScrollPane(outArea);
    sp.addKeyListener(ctrl.keyCtrl);

    SpringLayout layout = new SpringLayout();
    this.setLayout( layout );

    //value
    JLabel rcutLabel=new JLabel("Rcut");
    layout.putConstraint( SpringLayout.NORTH, rcutLabel, 10,SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST,  rcutLabel, 10,SpringLayout.WEST, this);
    layout.putConstraint( SpringLayout.NORTH, spRcut, 0,SpringLayout.NORTH, rcutLabel);
    layout.putConstraint( SpringLayout.WEST,  spRcut, 5,SpringLayout.EAST, rcutLabel);

    JLabel ringCalLabel=new JLabel("Type");
    layout.putConstraint( SpringLayout.NORTH, ringCalLabel, 5,SpringLayout.SOUTH, spRcut);
    layout.putConstraint( SpringLayout.WEST,  ringCalLabel, 0,SpringLayout.WEST, rcutLabel);
    layout.putConstraint( SpringLayout.NORTH, cmbRingType, 0,SpringLayout.NORTH, ringCalLabel);
    layout.putConstraint( SpringLayout.WEST,  cmbRingType, 0,SpringLayout.EAST, ringCalLabel);

    //PBC
    layout.putConstraint( SpringLayout.NORTH, cbPBC, 5,SpringLayout.SOUTH, cmbRingType);
    layout.putConstraint( SpringLayout.WEST,  cbPBC, 0,SpringLayout.WEST,ringCalLabel);

    //cna
    layout.putConstraint( SpringLayout.NORTH, cnaButton, 10,SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.WEST,  cnaButton, 10,SpringLayout.EAST,cmbRingType);
    //ave
    layout.putConstraint( SpringLayout.NORTH, aveButton, 0,SpringLayout.NORTH, cnaButton);
    layout.putConstraint( SpringLayout.WEST,  aveButton, 5,SpringLayout.EAST,cnaButton);

    layout.putConstraint( SpringLayout.NORTH, sfButton, 5,SpringLayout.SOUTH, cnaButton);
    layout.putConstraint( SpringLayout.WEST,  sfButton, 0,SpringLayout.WEST,cnaButton);

    //ring
    layout.putConstraint( SpringLayout.NORTH, ringButton, 5,SpringLayout.SOUTH, sfButton);
    layout.putConstraint( SpringLayout.WEST,  ringButton, 0,SpringLayout.WEST,sfButton);
    layout.putConstraint( SpringLayout.NORTH, cbShowRing, 5,SpringLayout.SOUTH, ringButton);
    layout.putConstraint( SpringLayout.WEST,  cbShowRing, 5,SpringLayout.WEST,ringButton);
    layout.putConstraint( SpringLayout.NORTH, threadRingButton, 0,SpringLayout.NORTH, ringButton);
    layout.putConstraint( SpringLayout.WEST,  threadRingButton, 5,SpringLayout.EAST,ringButton);



    //range
    JLabel ringRangeMaxLabel=new JLabel("Ring Length Max");
    layout.putConstraint( SpringLayout.NORTH, ringRangeMaxLabel, 2,SpringLayout.SOUTH, cbShowRing);
    layout.putConstraint( SpringLayout.WEST,  ringRangeMaxLabel, 0,SpringLayout.WEST, cbShowRing);
    layout.putConstraint( SpringLayout.NORTH, spRingRangeMax, 0,SpringLayout.NORTH, ringRangeMaxLabel);
    layout.putConstraint( SpringLayout.WEST,  spRingRangeMax, 0,SpringLayout.EAST, ringRangeMaxLabel);






    layout.putConstraint( SpringLayout.SOUTH, sp, -10,SpringLayout.SOUTH, this);
    layout.putConstraint( SpringLayout.NORTH, sp, 10,SpringLayout.NORTH, this);
    layout.putConstraint( SpringLayout.EAST,  sp, -10,SpringLayout.EAST, this);
    layout.putConstraint( SpringLayout.WEST,  sp, 10,SpringLayout.EAST, threadRingButton);

    add(ringCalLabel);
    add(cmbRingType);
    add(rcutLabel);
    add(spRcut);
    add(sp);
    add(cnaButton);
    add(ringButton);
    add(threadRingButton);
    add(aveButton);
    add(cbShowRing);
    add(spRingRangeMax);
    add(ringRangeMaxLabel);
    add(cbPBC);
    add(sfButton);

    requestFocusInWindow();
  }


  public void actionPerformed( ActionEvent e ){
    vconf.neighborAnalysisRcut=((Double)spRcut.getValue()).floatValue();
    vconf.isShowRing=cbShowRing.isSelected();
    vconf.ringRangeMax=((Integer)spRingRangeMax.getValue()).intValue();
    vconf.ringCalType=cmbRingType.getSelectedIndex();
    vconf.isPBC=cbPBC.isSelected();

    if( e.getSource() == cnaButton ){
      CNAStatistic();
    }else if( e.getSource() == sfButton ){
      structureFactor();
    }else if( e.getSource() == threadRingButton ){
      long start = System.currentTimeMillis();
      ringStatisticParallel();
      long end = System.currentTimeMillis();

      ArrayList<String> sorted=deleteDuplication(rings);
      printInfo(String.format(" Elapsed time: %10.2f sec",0.001f*(end-start)));
      if(ctrl.getActiveRW()!=null)ctrl.getActiveRW().drawRing( sorted );
    }else if( e.getSource() == ringButton ){
      long start = System.currentTimeMillis();
      ringStatistic();
      long end = System.currentTimeMillis();

      ArrayList<String> sorted=deleteDuplication(rings);
      printInfo(String.format(" Elapsed time: %10.2f sec",0.001f*(end-start)));
      if(ctrl.getActiveRW()!=null)ctrl.getActiveRW().drawRing( sorted );
    }else if( e.getSource() == aveButton ){
      average();
    }
    requestFocusInWindow();
  }

  private void printInfo(String str){
    outArea.append(str+"\n");
    System.out.println(str);
  }


  /**
   * Common Neighbor Analysis
   */
  private void CNAStatistic(){
    if(ctrl.getActiveRW()==null)return;
    viewer.renderer.Atoms atoms =ctrl.getActiveRW().getAtoms();
    ArrayList<ArrayList<Integer>> lspr
      = PairList.makePairList(atoms,ctrl.vconf.neighborAnalysisRcut,true,vconf.isPBC);

    ////
    int neighborMax=20;
    int nLMax= 20;
    int nMMax= 20;
    ////
    int[][][] idc=new int[3][neighborMax][atoms.n];

    printInfo(String.format("** Common Neighbor Analysis"));

    for(int i=0;i<atoms.n;i++){
      if(atoms.tag[i]==Const.VOLUME_DATA_TAG)break;
      ArrayList<Integer> iList = lspr.get(i);
      //i-j
      for(int jj=0; jj<iList.size(); jj++){
        int j= iList.get(jj);
        if(j<i)continue;//j>i only
        ArrayList<Integer> jList = lspr.get(j);

        //---L: count num. of common neighbors
        //i,jのpair-listを照らし合わせて，共通して存在しているものを保存．
        int nL= 0;
        int[] icommon= new int[nLMax];
        for(int iin=0; iin<iList.size(); iin++){
          int in=iList.get(iin);//i-neighbor
          if(in==j)continue;
          for(int jjn=0; jjn<jList.size(); jjn++){
            int jn=jList.get(jjn);//j-neighbor
            if(in==jn){//in(=jn)は，iにもjにも含まれる
              nL++;
              icommon[nL]=in;
              break;
            }
          }//jn
        }//in

        //---M: count num. of bonds between common neighbors
        //i,jに共通する原子(icommon)のうち，あるペア(ic1, ic2)がボンドを成すかどうか;
        //ic2がic1のneighbor listにあればtrue
        int nM=0;
        int[][] ibond=new int[2][nMMax];
        for(int iic1=0; iic1<nL; iic1++){
          int ic1=icommon[iic1];
          ArrayList<Integer> ic1List = lspr.get(ic1);
          for(int iic2=0; iic2<nL; iic2++){
            int ic2=icommon[iic2];
            if(ic2<=ic1)continue;

            for(int jjc1=0; jjc1<ic1List.size(); jjc1++){
              int jc1=ic1List.get(jjc1);//list of ic1
              if(jc1==ic1){
                nM++;
                ibond[0][nM]=ic1;
                ibond[1][nM]=ic2;
                break;
              }
            }
          }//jjl
        }//iil

        //---N: count max num of continuous bonds
        //ボンド(ibond)から連結しているものを探す．
        //imボンドとjmボンドが連結していたら，それぞれの連結数をインクリメントする
        int[] nb=new int[nMMax];//
        for(int im=0;im<nM-1;im++){
          int ib1=ibond[0][im];
          int ib2=ibond[1][im];
          for(int jm=im+1;jm<nM;jm++){
            int jb1=ibond[0][jm];
            int jb2=ibond[1][jm];
            if(ib1==jb1 || ib1==jb2 || ib2==jb1 || ib2==jb2){
              nb[im]++;
              nb[jm]++;
            }
          }//jm
        }//im
        //search max nb
        int nN=0;
        for(int im=0;im<nM;im++)if(nN<nb[im])nN=nb[im];


        printInfo(String.format(" %d-%d: %d %d %d",i,j,nL,nM,nN));


        //store (L,M,N) to i
        idc[0][jj][i]=nL;
        idc[1][jj][i]=nM;
        idc[2][jj][i]=nN;
        //store (L,M,N) to j, too
        for(int kk=0; kk<jList.size(); kk++){
          if(jList.get(kk)==j){
            idc[0][kk][j]=nL;
            idc[1][kk][j]=nM;
            idc[2][kk][j]=nN;
          }
        }

      }//jj
    }//i




  }//CNA

  /**
   * average length, angle between nearest-neighbors
   */
  int filenum=0;
  private void average(){
    try {
      filenum++;
      FileWriter fw = new FileWriter(String.format("%03d-aveLength.d",filenum));
      BufferedWriter bw = new BufferedWriter( fw );
      PrintWriter pw = new PrintWriter( bw );
      pw.println( "#length");

      FileWriter fw2 = new FileWriter(String.format("%03d-aveAngle.d",filenum));
      BufferedWriter bw2 = new BufferedWriter( fw2 );
      PrintWriter pw2 = new PrintWriter( bw2 );
      pw2.println( "#species of angle center, angle[deg]");

      if(ctrl.getActiveRW()==null)return;
      viewer.renderer.Atoms atoms =ctrl.getActiveRW().getAtoms();

      int maxNeighbor=4;//because of Si

      ArrayList<ArrayList<Integer>> lspr=
        PairList.makeKthNearestPairList(atoms,ctrl.vconf.neighborAnalysisRcut,maxNeighbor,true,vconf.isPBC);

      double aveLength=0.;
      int nLength=0;
      double[] aveAngle=new double[atoms.nTagMax];
      int[] nAngle=new int[atoms.nTagMax];
      for(int i=0;i<atoms.nTagMax;i++){
        aveAngle[i]=0.0;
        nAngle[i]=0;
      }

      for(int i=0;i<atoms.n;i++){
        if(atoms.tag[i]==Const.VOLUME_DATA_TAG)break;
        ArrayList<Integer> iList = lspr.get(i);
        for(int jj=0;jj<iList.size();jj++){
          int j=iList.get(jj);
          if(vconf.ringCalType==1 && atoms.tag[i]==atoms.tag[j])continue;
          //length
          float drij[]=new float[3];
          for(int l=0;l<3;l++)drij[l]=atoms.r[j][l]-atoms.r[i][l];

          //added by Mizukoshi
          if(vconf.isPBC){
            float[] sdrij=mulH(atoms.hinv,drij);
            for(int l=0;l<3;l++){
              if(sdrij[l]>0.5f) sdrij[l]=sdrij[l]-1.0f;
              if(sdrij[l]<-0.5f) sdrij[l]=sdrij[l]+1.0f;
            }
            drij=mulH(atoms.h,sdrij);
          }
          //
          double rij2=drij[0]*drij[0]+drij[1]*drij[1]+drij[2]*drij[2];

          double rij=Math.sqrt(rij2);
          nLength++;
          aveLength+=rij;
          pw.println( String.format("%e", rij) );

          for(int kk=0;kk<iList.size();kk++){
            int k=iList.get(kk);
            if(k<=j)continue;
            if(atoms.tag[i]==atoms.tag[k])continue;

            float drik[]=new float[3];
            for(int l=0;l<3;l++)drik[l]=atoms.r[k][l]-atoms.r[i][l];

            //added by Mizukoshi
            if(vconf.isPBC){
              float[] sdrik=mulH(atoms.hinv,drik);
              for(int l=0;l<3;l++){
                if(sdrik[l]>0.5f) sdrik[l]-=1.0f;
                if(sdrik[l]<-0.5f) sdrik[l]+=1.0f;
              }
              drik=mulH(atoms.h,sdrik);
            }
            //
            double rik2=drik[0]*drik[0]+drik[1]*drik[1]+drik[2]*drik[2];

            double rik=Math.sqrt(rik2);

            //angle
            double cos=(drij[0]*drik[0]+drij[1]*drik[1]+drij[2]*drik[2])/(rij*rik);
            double angle=Math.acos(cos)*180.0/Math.PI;
            nAngle[atoms.tag[i]-1]++;
            aveAngle[atoms.tag[i]-1]+=angle;
            pw2.println( String.format("%d %e",atoms.tag[i], angle) );
          }//k

        }//j
      }//i


      printInfo(String.format("** Averaging"));
      printInfo(String.format(" %d-averaged length: %f",nLength,aveLength/nLength));
      if(vconf.ringCalType==0){
        double a=0.;
        int n=0;
        for(int l=0;l<nAngle.length;l++){
          a+=aveAngle[l];
          n+=nAngle[l];
          printInfo(String.format(" %d-averaged angle: %f [deg]",n,a/n));
        }
      }else if(vconf.ringCalType==1){
        for(int l=0;l<nAngle.length;l++){
          printInfo(String.format(" %d-averaged %d-centerd angle: %f [deg]",nAngle[l],l+1,aveAngle[l]/nAngle[l]));
        }
      }

      pw.close();
      bw.close();
      fw.close();
      pw2.close();
      bw2.close();
      fw2.close();
    }catch ( IOException ioe ){
    }

  }//average

  /**
   * recursive method for search n-ring
   */
  private void trackSPRing(String path,
                           int now,
                           int nowLength,
                           int ntmpTracedPoint,
                           int[] tmpTracedPoint,
                           ArrayList<Integer> tracedList,
                           int start,int searchMaxLength,
                           ArrayList<ArrayList<Integer>> lspr,
                           viewer.renderer.Atoms atoms){

    if(nowLength>2 && now==start){
      //check
      for(int i=0;i<ntmpTracedPoint-2;i++)
        for(int j=i+1;j<ntmpTracedPoint-1;j++)
          if(tmpTracedPoint[i]==tmpTracedPoint[j])return;

      int length=nowLength-1;
      rings.add(String.format("%d:",length)+path);
      return;
    }
    if(nowLength>searchMaxLength)return;



    tmpTracedPoint[ntmpTracedPoint]=now;

    //next vertex
    nextsearch:
    for(int ll=0;ll<lspr.get(now).size();ll++){
      int next=lspr.get(now).get(ll);
      //check
      if(nowLength<3 && next==start)continue;
      //skip deleted points
      for(int i=0;i<tracedList.size();i++)if(next==tracedList.get(i))continue nextsearch;

      //skip already traced
      for(int i=1;i<=ntmpTracedPoint;i++)if(next==tmpTracedPoint[i])continue nextsearch;

      //for SiO2
      if(vconf.ringCalType==1 && atoms.tag[next]==2 && atoms.tag[now]==2){
        //System.out.println("hit!!");
        continue;
      }


      //switch backtracking type
      if(vconf.ringCalType==0){
        //normal
        trackSPRing(path+String.format("-%d",next),
                    next,
                    nowLength+1,
                    ntmpTracedPoint+1,
                    tmpTracedPoint,
                    tracedList,
                    start,searchMaxLength,lspr,atoms);
      }else if(vconf.ringCalType==1){
        //for SiO2
        if(atoms.tag[next]==2){
          //ignore O
          trackSPRing(path+String.format("-%d",next),
                      next,
                      nowLength,
                      ntmpTracedPoint+1,
                      tmpTracedPoint,
                      tracedList,
                      start,searchMaxLength,lspr,atoms);
        }else{
          trackSPRing(path+String.format("-%d",next),
                      next,
                      nowLength+1,
                      ntmpTracedPoint+1,
                      tmpTracedPoint,
                      tracedList,
                      start,searchMaxLength,lspr,atoms);
        }
      }
    }//ll

  }//trackSPRing

  ArrayList<String> rings=new ArrayList<String>();
  /**
   * threading search n-ring
   */
  private void ringStatisticParallel(){
    rings.clear();


    if(ctrl.getActiveRW()==null)return;
    viewer.renderer.Atoms atoms =ctrl.getActiveRW().getAtoms();

    int maxNeighbor=4;//because of Si
    ArrayList<ArrayList<Integer>> lspr
      = PairList.makeKthNearestPairList(atoms,ctrl.vconf.neighborAnalysisRcut,maxNeighbor,true,vconf.isPBC);

    int searchMaxLength=vconf.ringRangeMax;
    ArrayList<Integer> tracedList= new ArrayList<Integer>();
    //Collections.synchronizedList(tracedList);
    Collections.synchronizedList(rings);

    for(int start=0;start<atoms.n;start++){
      if(atoms.tag[start]==Const.VOLUME_DATA_TAG)break;
      if(vconf.ringCalType==1 && atoms.tag[start]==2){
        //tracedList.add(start);
        continue;
      }

      //thread do
      ThreadTraceRing[] th=new ThreadTraceRing[lspr.get(start).size()];
      for(int ll=0;ll<lspr.get(start).size();ll++){
        int next=lspr.get(start).get(ll);
        th[ll]=new ThreadTraceRing(start,next,tracedList,searchMaxLength,lspr,atoms);
        th[ll].start();
      }

      //wait for all threads end
      for(int ll=0;ll<lspr.get(start).size();ll++){
        try{
          th[ll].join();
        }catch(Exception e){}
      }
      tracedList.add(start);
    }
  }
  /**
   * the class for thread search n-ring
   */
  private class ThreadTraceRing extends Thread{
    String path;
    int nowLength,ntmpTracedPoint,next,start;
    int[] tmpTracedPoint;
    ArrayList<Integer> tracedList;
    int searchMaxLength;
    ArrayList<ArrayList<Integer>> lspr;
    viewer.renderer.Atoms atoms;

    ThreadTraceRing(int start,int next,
                    ArrayList<Integer> tracedList,int searchMaxLength,
                    ArrayList<ArrayList<Integer>> lspr,viewer.renderer.Atoms atoms){
      path=String.format("%d-%d",start,next);
      this.start=start;
      this.next=next;
      this.nowLength=1;
      this.ntmpTracedPoint=1;
      this.tmpTracedPoint=new int[searchMaxLength*3];
      this.tmpTracedPoint[0]=start;
      this.tracedList=tracedList;
      this.searchMaxLength=searchMaxLength;
      this.lspr=lspr;
      this.atoms=atoms;
    }
    public void run(){
      if(vconf.ringCalType==0){
        //normal
        trackSPRing(path,
                    next,
                    nowLength,
                    ntmpTracedPoint,
                    tmpTracedPoint,
                    tracedList,
                    start,searchMaxLength,lspr,atoms);
      }else if(vconf.ringCalType==1){
        //for SiO2
        if(atoms.tag[next]==2){
          //ignore O
          trackSPRing(path,
                      next,
                      nowLength,
                      ntmpTracedPoint,
                      tmpTracedPoint,
                      tracedList,
                      start,searchMaxLength,lspr,atoms);
        }else{
          trackSPRing(path,
                      next,
                      nowLength+1,
                      ntmpTracedPoint,
                      tmpTracedPoint,
                      tracedList,
                      start,searchMaxLength,lspr,atoms);
        }
        /*
         * trackSPRing(path,
         *             next,
         *             nowLength,
         *             ntmpTracedPoint,
         *             tmpTracedPoint,
         *             tracedList,
         *             start,searchMaxLength,lspr,atoms);
         */
      }
    }
  }

  /**
   * search n-ring in serial
   */
  private void ringStatistic(){
    rings.clear();

    if(ctrl.getActiveRW()==null)return;
    viewer.renderer.Atoms atoms =ctrl.getActiveRW().getAtoms();

    int maxNeighbor=4;//because of Si
    ArrayList<ArrayList<Integer>> lspr
      = PairList.makeKthNearestPairList(atoms,ctrl.vconf.neighborAnalysisRcut,maxNeighbor,true,vconf.isPBC);



    int searchMaxLength=vconf.ringRangeMax;

    ArrayList<Integer> tracedList= new ArrayList<Integer>();

    for(int start=0;start<atoms.n;start++){
      if(atoms.tag[start]==Const.VOLUME_DATA_TAG)break;
      if(vconf.ringCalType==1 && atoms.tag[start]==2){
        //tracedList.add(start);
        continue;
      }
      String path=String.format("%d",start);
      int now=start;
      int nowLength=1;
      int ntmpTracedPoint=0;
      int[] tmpTracedPoint=new int[searchMaxLength*3];
      trackSPRing(path,
                  now,
                  nowLength,
                  ntmpTracedPoint,
                  tmpTracedPoint,
                  tracedList,
                  start,searchMaxLength,lspr,atoms);
      tracedList.add(start);

      //progress bar
      int digit=1;
      if(digit<atoms.n/30)digit=atoms.n/30;
      if(start%digit ==0 ){
        System.out.print("\r");
        System.out.print("calculating rings [");
        for(int jj=0;jj<start/digit;jj++)System.out.print("=");
        System.out.print(">");
        for(int jj=0;jj<30-start/digit;jj++)System.out.print(" ");
        System.out.print("] ");
        System.out.print(String.format("%3.0f %%",start/(float)atoms.n*100));
      }
    }
    //finish progress bar
    System.out.print("\r");
    for(int j=0;j<100;j++)System.out.print(" ");
    System.out.print("\r");

  }



  /**
   * delete duplication
   */
  private ArrayList<String> deleteDuplication(ArrayList<String> rings){

    int[] nRing=new int[vconf.ringRangeMax];

    ArrayList<String> sorted=new ArrayList<String>();
    //for(int ll=0;ll<rings.size();ll++){
    while(rings.size()>0){
      int i=0;//check term is alwayz zero
      String[] ipath=rings.get(i).split("[:-]");
      int iLength=Integer.valueOf(ipath[0]).intValue();
      iLength=ipath.length;

      boolean isDeletedIJ=false;
      for(int j=1;j<rings.size();j++){
        String[] jpath=rings.get(j).split("[:-]");
        int jLength=Integer.valueOf(jpath[0]).intValue();
        jLength=jpath.length;

        if(iLength==jLength){
          //check component
          boolean isMatch=true;
          for(int k=1;k<ipath.length-1;k++){
            if(Integer.valueOf(ipath[k]).intValue()!=
               Integer.valueOf(jpath[ipath.length-k]).intValue()){
              isMatch=false;
              break;//exit k
            }
          }//k
          if(isMatch){
            nRing[Integer.valueOf(ipath[0]).intValue()-1]++;
            sorted.add(rings.get(i));
            rings.remove(j);
            rings.remove(i);
            isDeletedIJ=true;
            break;//exit j
          }
        }
      }//j
      if(!isDeletedIJ){
        sorted.add(rings.get(i));
        rings.remove(i);
      }
    }//while


    printInfo(String.format("** Ring Statistics"));
    System.out.println(" Path of rings are...");
    for(int i=0;i<sorted.size();i++)System.out.println(" "+sorted.get(i));

    for(int i=2;i<nRing.length;i++){
      printInfo(String.format(" %d-ring: %d",i+1,nRing[i]));
    }


    return sorted;
  }

  private void structureFactor(){
    if(ctrl.getActiveRW()==null)return;
    viewer.renderer.Atoms atoms =ctrl.getActiveRW().getAtoms();

    int nx=10;
    int ny=10;
    int nz=10;
    double pi2=Math.PI*2;
    double rhocos=0.;
    double rhosin=0.;
    for(int lx=0;lx<nx;lx++){
      for(int ly=0;ly<ny;ly++){
        for(int lz=0;lz<nz;lz++){
          double[] ak=new double[3];
          for(int j=0;j<3;j++)ak[j]=pi2*(lx*atoms.hinv[0][j]+ly*atoms.hinv[1][j]+lz*atoms.hinv[2][j]);
          for(int i=0;i<atoms.n;i++){
            double arg=ak[0]*atoms.r[i][0]+ak[1]*atoms.r[i][1]+ak[2]*atoms.r[i][2];
            rhocos+=Math.cos(arg);
            rhosin+=Math.sin(arg);
          }
        }
      }//ly
    }//lx
    printInfo(String.format("%f %f",rhocos,rhosin));
  }



}
