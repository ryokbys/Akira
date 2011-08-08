package tools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.imageio.ImageIO;

/**
 * my file chooser
 */
public class MyOpen{

  //this is only JAVA1.6
  //FileNameExtensionFilter filter = new FileNameExtensionFilter( "*.akira", "akira" );
  public String getOpenFilename(){
    String currentDir=System.getProperty("user.dir");
    JFileChooser jfc = new JFileChooser( (new File(currentDir)).getAbsolutePath() );

    jfc.addChoosableFileFilter(new MyFilter(".Akira"));


    //add preview
    ImagePreview preview = new ImagePreview(jfc);
    jfc.addPropertyChangeListener(preview);
    jfc.setAccessory(preview);


    jfc.setDialogTitle("Choose \"***.Akira\"");

    String str = null;
    //jfc.setFileFilter( filter );//this is only JAVA1.6
    int s = jfc.showOpenDialog( null );
    if( s == JFileChooser.APPROVE_OPTION ){
      File file = jfc.getSelectedFile();
      str = new String( file.getAbsolutePath() );
    }
    return str;
  }

}

//////////////
class ImagePreview extends JPanel implements PropertyChangeListener {
  private JFileChooser jfc;

  public ImagePreview(JFileChooser jfc){
    this.jfc = jfc;
    Dimension sz = new Dimension(200,300);
    setPreferredSize(sz);
  }

  public void propertyChange(PropertyChangeEvent evt){
    try {
      //System.out.println("updating");
      File file = jfc.getSelectedFile();
      updateAKIRA(file);
    } catch (IOException ex){
      //System.out.println(ex.getMessage());
      //ex.printStackTrace();
    }
  }

  public void updateAKIRA(File file) throws IOException {
    info.clear();
    info.add("Akira file is not selected");

    if(file==null)return;
    if(file.getName().toLowerCase().endsWith(".akira")){
      readHeader(file.getName());
      repaint();
    }else{
      //reset param
      info.clear();
      info.add("It is not Akira file");
      return;
    }
  }

  ArrayList<String> info= new ArrayList<String>();
  void readHeader(String file){
    info.clear();

    FileInputStream fis;
    BufferedInputStream bis;
    DataInputStream dis;
    try {
      fis = new FileInputStream( file);
      bis = new BufferedInputStream( fis );
      dis = new DataInputStream( bis );

      int itmp;
      float[] tmp=new float[3];
      boolean btmp;

      //header
      String str=dis.readUTF();//convDate
      info.add(String.format("conv. date:"));
      info.add(str);
      info.add(" ");

      itmp=dis.readInt();//totalFrame
      info.add(String.format("total frame: %d",itmp));
      dis.readFloat();//startTime
      dis.readFloat();//timeInterval
      btmp=dis.readBoolean();//existBond
      if(btmp)info.add("bonds exist");

      //frame=1
      info.add(" ");
      info.add("frame 1 information");
      dis.readInt();//skipbyte
      itmp=dis.readInt();//natom
      info.add(String.format("atoms: %d",itmp));
      itmp=dis.readInt();//ndata
      info.add(String.format("data: %d",itmp));

      info.add("h matrix");
      for(int k=0; k<3; k++){
        for(int l=0; l<3; l++) tmp[l]=dis.readFloat();
        info.add(String.format("%.3f %.3f %.3f",tmp[0],tmp[1],tmp[2]));
      }

      fis.close();
      bis.close();
      dis.close();

    }catch ( IOException e ){
    }

  }
  public void paintComponent(Graphics g){
    // fill the background
    g.setColor(Color.white);
    g.fillRect(0,0,getWidth(),getHeight());

    // print a message
    g.setColor(Color.black);
    for(int i=0;i<info.size();i++){
      g.drawString(info.get(i),10,10+18*i);
    }
  }
}
