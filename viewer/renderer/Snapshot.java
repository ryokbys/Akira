package viewer.renderer;

import java.io.*;
import java.util.*;

import java.awt.event.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;

import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.*;

import com.jogamp.opengl.util.awt.*;


import viewer.*;
import viewer.renderer.*;

public class Snapshot {
  Container pane;

  static final String seq_name = "seq%05d.%s";
  static final String one_name = "snap%05d.%s";
  String filename_seq;
  String filename_one;
  ViewConfig vconf;

  public Snapshot( Container pane, String rootPath,  ViewConfig vconf){
    this.pane = pane;
    this.vconf=vconf;

    filename_seq = new String(seq_name);
    filename_one = new String(one_name);

    File fseq = new File( rootPath, seq_name );
    File fone = new File( rootPath, one_name );
    filename_seq = fseq.getAbsolutePath();
    filename_one = fone.getAbsolutePath();

  }

  public int stepOne=0;
  public void writeImage(){
    String imageformat=vconf.imageFormat;
    //frame to buf
    BufferedImage buf =
      Screenshot.readToBufferedImage( 0, 0,pane.getWidth(),pane.getHeight(),false );

    //file name
    //String filename = String.format(filename_one, stepOne, imageformat );
    //stepOne++;
    String filename=getFileName(filename_one, stepOne, imageformat);

    //save
    try {
      ImageIO.write( buf, imageformat, new File(filename) );
    }
    catch ( IOException e ){
    }

  }

  int stepSeq=0;
  public void writeImageSequential(){
    String imageformat=vconf.imageFormat;
    //frame to buf
    BufferedImage buf =
      Screenshot.readToBufferedImage( 0, 0,pane.getWidth(),pane.getHeight(),false );

    //file name
    //String filename = String.format(filename_seq, stepSeq, imageformat );
    //stepSeq++;
    String filename=getFileName(filename_seq, stepSeq, imageformat);

    //save
    try {
      ImageIO.write( buf, imageformat, new File(filename) );
    }
    catch ( IOException e ){
    }
  }


  String getFileName(String filename, int num, String format){
    num++;
    File file=new File( String.format(filename, num, format) );

    while(file.exists()){
      //confirm
      if( isOverrideConfirm(file.getAbsolutePath()) ) break;

      //try next step
      num++;
      file=new File( String.format(filename, num, format) );
    }

    //update global steps
    if(filename.matches(".*snap.*"))
      stepOne=num;
    else
      stepSeq=num;

    return file.getAbsolutePath();
  }

  boolean isOverrideConfirm(String file){
    JFrame frame = new JFrame();
    int ans = JOptionPane.showConfirmDialog(null,
                                           "override "+file+" ?",
                                           file+"already exists",
                                           JOptionPane.YES_NO_OPTION,
                                           JOptionPane.QUESTION_MESSAGE);

    if(ans==JOptionPane.OK_OPTION)
      return true;
    else
      return false;
  }

}
