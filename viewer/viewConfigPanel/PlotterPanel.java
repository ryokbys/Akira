package viewer.viewConfigPanel;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import com.sun.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import data.*;
import tools.*;
import viewer.*;
import viewer.renderer.*;

public class PlotterPanel extends JPanel implements ActionListener{

  int drawType=0;
  public void actionPerformed( ActionEvent ae){
    drawType=drawTypeCmb.getSelectedIndex();
    if(drawType==0){
      if(rdCanv!=null){
        String currentDir=System.getProperty("user.dir");
        JFileChooser jfc = new JFileChooser( (new File(currentDir)).getAbsolutePath() );
        String str = null;
        int s = jfc.showOpenDialog( null );
        if( s == JFileChooser.APPROVE_OPTION ){
          File file = jfc.getSelectedFile();
          str = new String( file.getAbsolutePath() );
        }
        readFile(str);
      }
    }
    update();
  }

  public void update(){
    if(rdCanv!=null)rdCanv.repaint();
  }

  private RDCanvas rdCanv;
  private Controller ctrl;
  public PlotterPanel(Controller ctrl){
    this.ctrl=ctrl;
    createPanel();
  }



  private JComboBox drawTypeCmb;

  private void createPanel(){

    String[] type = {"Energy", "x", "y", "z"};
    drawTypeCmb=new JComboBox(type);
    drawTypeCmb.addActionListener(this);
    drawTypeCmb.setSelectedIndex(0);
    drawTypeCmb.setFocusable(false);
    //canvas
    rdCanv= new RDCanvas();
    rdCanv.setPreferredSize(new Dimension(500, 150));
    rdCanv.setBackground(Color.white);
    rdCanv.setFocusable(false);
    SpringLayout layout = new SpringLayout();
    this.setLayout( layout );

    layout.putConstraint( SpringLayout.NORTH, drawTypeCmb, 10, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.EAST, drawTypeCmb, -10, SpringLayout.EAST, this );
    //canvas
    layout.putConstraint( SpringLayout.SOUTH, rdCanv, -5, SpringLayout.SOUTH, this );
    layout.putConstraint( SpringLayout.NORTH, rdCanv, 5, SpringLayout.NORTH, this );
    layout.putConstraint( SpringLayout.EAST, rdCanv, -5,SpringLayout.WEST, drawTypeCmb);
    layout.putConstraint( SpringLayout.WEST, rdCanv, 10,SpringLayout.WEST, this );

    this.add(drawTypeCmb);
    this.add(rdCanv);

    requestFocusInWindow();
  }


  ArrayList<Float> energy = new ArrayList<Float>();
  float maxEne=-10000.f;
  float minEne=10000.f;
  public void readFile(String file){
    if(file==null)return;
    try {
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader( fr );
      String line;
      Scanner sc;

      //read
      line = br.readLine();
      while(line!=null){
        //parse
        sc = new Scanner( line );
        sc.nextInt();//skip integer
        float kin = sc.nextFloat();
        float pot = sc.nextFloat();
        float tot = sc.nextFloat();
        if(minEne>kin)minEne=kin;
        if(minEne>pot)minEne=pot;
        if(minEne>tot)minEne=tot;
        if(maxEne<kin)maxEne=kin;
        if(maxEne<pot)maxEne=pot;
        if(maxEne<tot)maxEne=tot;

        energy.add(kin);
        energy.add(pot);
        energy.add(tot);
        //read next
        line = br.readLine();
      }

      br.close();
      fr.close();
    }
    catch ( IOException e ){
      System.out.println("no read: "+file);
    }

    rdCanv.repaint();

  }


  ///////////////////////////////////////////////////////////////////
  class RDCanvas extends JPanel{

    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      int x1,x2,y1,y2;
      int width=getWidth();
      int height=getHeight();

      int offset=4;
      int r=5;

      int h1=15;
      int h2=height-h1;
      int h12=h2-h1;

      int w1=74;
      int w2=width-10;
      int w12=w2-w1;

      int half=getHeight()/2;
      //int top=under-dis;

      //refresh
      g.setColor(Color.white);
      g.fillRect(0,0,width,height);

      //x-axis
      g.setColor(Color.black);
      g.drawLine(w1,half,w2,half);
      //y-axis
      y2=h2-h12;
      g.drawLine(w1,h1,w1,h2);
      //y-tics
      g.drawLine(w1-r,h1,w1+r,h1);
      g.drawLine(w1-r,h2,w1+r,h2);


      if(drawType==0){
        //energy
        int n=energy.size()/3;
        if(n<1)return;

        //number
        String str=String.format("%.2e",maxEne);
        g2.drawString(str, offset, h1+offset);
        str=String.format("%.2e",(maxEne-minEne)*0.5f);
        g2.drawString(str, offset, half+offset);
        str=String.format("%.2e",minEne);
        g2.drawString(str, offset, h2+offset);

        for(int i=0;i<n-1;i++){
          float kin1 =energy.get(3*i);
          float pot1 =energy.get(3*i+1);
          float tot1 =energy.get(3*i+2);

          float kin2 =energy.get(3*i+3);
          float pot2 =energy.get(3*i+1+3);
          float tot2 =energy.get(3*i+2+3);

          x1=(int)(w1+w12*i/(float)(n-1));
          x2=(int)(w1+w12*(i+1)/(float)(n-1));

          //kin
          g.setColor(Color.red);
          y1=h2-(int)((kin1-minEne)*h12/(maxEne-minEne));
          y2=h2-(int)((kin2-minEne)*h12/(maxEne-minEne));
          g.drawLine(x1,y1,x2,y2);
          g.drawOval(x1-r/2,y1-r/2,r,r);
          g.drawOval(x2-r/2,y2-r/2,r,r);
          //pot
          g.setColor(Color.green);
          y1=h2-(int)((pot1-minEne)*h12/(maxEne-minEne));
          y2=h2-(int)((pot2-minEne)*h12/(maxEne-minEne));
          g.drawLine(x1,y1,x2,y2);
          g.drawOval(x1-r/2,y1-r/2,r,r);
          g.drawOval(x2-r/2,y2-r/2,r,r);
          //tot
          g.setColor(Color.blue);
          y1=h2-(int)((tot1-minEne)*h12/(maxEne-minEne));
          y2=h2-(int)((tot2-minEne)*h12/(maxEne-minEne));
          g.drawLine(x1,y1,x2,y2);
          g.drawOval(x1-r/2,y1-r/2,r,r);
          g.drawOval(x2-r/2,y2-r/2,r,r);

          //x-tics
          g.setColor(Color.black);
          g.drawLine(x1,half+r,x1,half-r);
          g.drawLine(x2,half+r,x2,half-r);

        }///i

        //current line
        g.setColor(Color.pink);
        x1=ctrl.getActiveRW().currentFrame;
        x2=(int)(w1+w12*x1/(float)(n-1));
        g.drawLine(x2,0,x2,height);



      }else{
        RenderingWindow rw=ctrl.getActiveRW();
        if(rw==null)return;
        viewer.renderer.Atoms atoms=rw.getAtoms();
        if(rw.renderingAtomDataIndex>0){
          for(int i=0;i<atoms.n;i++){
            float sx=atoms.hinv[drawType-1][0]*atoms.r[i][0]+atoms.hinv[drawType-1][1]*atoms.r[i][1]+atoms.hinv[drawType-1][2]*atoms.r[i][2];

            int x=w1+(int)(w12*sx);
            int y=h2-(int)((atoms.data[i][rw.renderingAtomDataIndex-1]-atoms.originalDataRange[rw.renderingAtomDataIndex-1][0])*h12
                           /(atoms.originalDataRange[rw.renderingAtomDataIndex-1][1]-atoms.originalDataRange[rw.renderingAtomDataIndex-1][0]));
            g.drawOval(x-r/2,y-r/2,r,r);
          }
          //y-axis
          g.drawLine(w1,0,w1,height);
          //y-tics
          g.drawLine(w1-r,h1,w1+r,h1);
          g.drawLine(w1-r,h2,w1+r,h2);
          //number
          String str=String.format("%.2e",atoms.originalDataRange[rw.renderingAtomDataIndex-1][1]);
          g2.drawString(str, offset, h1+offset);
          str=String.format("%.2e",(atoms.originalDataRange[rw.renderingAtomDataIndex-1][1]-atoms.originalDataRange[rw.renderingAtomDataIndex-1][0])*0.5f);
          g2.drawString(str, offset, half+offset);
          str=String.format("%.2e",atoms.originalDataRange[rw.renderingAtomDataIndex-1][0]);
          g2.drawString(str, offset, h2+offset);

        }
      }
    }

  }//end of RDCanvas

}
