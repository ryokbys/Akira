package converter;

import java.io.*;
import java.nio.*;
import java.util.*;

import tools.*;
import data.*;
import converter.*;
import converter.reader.*;

//for reading tar, gz, bz2
import org.apache.tools.tar.*;
import java.util.zip.*;
import org.apache.tools.bzip2.*;


/**
 * This class converts akira format files to binary file
 */

public class AkiraConverter{

  public static void main( String[] args ){
    AkiraConverter akiraconv =new AkiraConverter(false);
  }

  //config file name
  static final String convConfFile = "AkiraConverter.conf";


  ConvConfig cconf=new ConvConfig();
  MyFileIO atomFileIO;

  //frame counter
  int ithFrame=0;

  /**
   * This is constructor.
   * and this measures elapsed time of myMain()
   */
  //constructor
  public AkiraConverter(boolean calledGUI){
    File configFilename = new File( convConfFile );

    if(calledGUI){
      ConfCreater confCreater=new ConfCreater(this);
    }else{
      if( configFilename.exists() ){
        startConv();
      }else{
        ConfCreater confCreater=new ConfCreater(this);
      }
    }
  }


  /**
   * is practical main function. <br>
   * This is switching reading-functions according to read-file format, and
   * creating bonds and vectors if necessary
   */
  public void startConv(){
    //core methods
    long start = System.currentTimeMillis();

    cconf.read(convConfFile);
    atomFileIO= new MyFileIO(cconf.systemName+cconf.fileExtension);

    System.out.println(String.format("Total Number of Frames: %d",cconf.getTotalFrame()));
    System.out.println();

    //write header
    atomFileIO.wopen();
    atomFileIO.writeHeader(cconf.getTotalFrame(),
                           cconf.getStartTime(),
                           cconf.getTimeInterval(),
                           (cconf.createBondsWithLength || cconf.createBondsWithFile) );

    //read and write body
    for(int itarget=0;itarget<cconf.readFilePath.size();itarget++){

      //if file does not exsist, AKIRAConv exit.
      if(!(new File( convConfFile )).exists()){
        (new File(cconf.systemName+cconf.fileExtension)).delete();
        System.exit(1);
      }


      //read files according to extension
      String format=cconf.readFileFormat.get(itarget);
      if(format.equalsIgnoreCase("chgcar")){
        CHGCAR.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("cube")){
        Cube.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("coord")){
        Coord.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("xyz")){
        XYZ.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("chem3d")){
        Chem3D.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("xcrysden")){
        XCrysDen.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("akira")){
        AkiraAscii.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("akiratgz")){
        AkiraAsciiTgz.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("akiratbz2")){
        AkiraAsciiTbz2.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("akirabintgz")){
        AkiraBinaryTgz.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("akirabintbz2")){
        AkiraBinaryTbz2.conv(atomFileIO,cconf,itarget,ithFrame);
      }else if(format.equalsIgnoreCase("akirabin")){
        AkiraBinary.conv(atomFileIO,cconf,itarget,ithFrame);
      }else{
        System.out.println("unknown file format!!");
        System.out.println("check akiraconv.conf");
      }


    }//end of itarget

    //write footer
    atomFileIO.writeFooter();
    atomFileIO.wclose();

    long end = System.currentTimeMillis();
    System.out.println();
    System.out.println(String.format(" Elapsed time: %10.2f sec",0.001f*(end-start)));
  }//end of myMain


}
