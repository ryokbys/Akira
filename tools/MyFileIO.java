package tools;
import java.io.*;
import java.util.*;
import java.text.*;

import tools.*;
import data.*;
import converter.*;

/**
 * my file reader/writer
 */

public class MyFileIO{

  String filename;
  public boolean existBonds=false;

  public MyFileIO(String file){
    this.filename=file;
  }


  /* for footer */
  int nAtomsMax=0;
  //int nDataMax=0;
  float[][] hMax = new float[3][3];
  float[][] range = new float[Const.DATA][2];
  HashMap<Integer,Integer> involvedTags = new HashMap<Integer,Integer>();


  FileOutputStream fos;
  BufferedOutputStream bos;
  DataOutputStream dos;
  public void wopen( ){
    try {
      fos = new FileOutputStream( filename );
      bos = new BufferedOutputStream( fos );
      dos = new DataOutputStream( bos );
    }
    catch ( IOException e ){
    }
  }
  public void wclose(){
    try {
      dos.flush();
      bos.flush();
      dos.close();
      bos.close();
      fos.close();
    }
    catch ( IOException e ){
    }
  }

  public void writeHeader(int totalFrame,float startTime, float timeInterval, boolean bond){
    Date date = new Date();  //
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
    try {
      dos.writeUTF(sdf.format(date));//conv. date
      dos.writeInt(totalFrame);
      dos.writeFloat((float)startTime);
      dos.writeFloat((float)timeInterval);
      dos.writeBoolean(bond);
    }catch ( IOException e ){
      System.out.println("error at write header");
    }
  }

  public void writeFooter(){
    try {
      dos.writeInt(nAtomsMax);
      //hmax
      for(int k=0; k<3; k++)for(int l=0; l<3; l++)dos.writeFloat( hMax[k][l] );
      //dataRange
      for(int k=0; k<Const.DATA; k++){
        for(int l=0; l<2; l++)dos.writeFloat( range[k][l] );
      }

      //involved tags num.
      dos.writeInt(involvedTags.size());
      //sort involved tags
      Set set = involvedTags.keySet();
      Iterator iterator = set.iterator();
      Integer object;
      int[] tags=new int[involvedTags.size()];
      int inc=0;
      while(iterator.hasNext()){
        object = (Integer)iterator.next();
        tags[inc]=object.intValue();
        inc++;
      }
      java.util.Arrays.sort(tags);//modified-quick sort!
      //write
      for(int i=0;i<inc;i++)dos.writeInt(tags[i]);


    }catch ( IOException e ){
      System.out.println("error at write footer");
    }
  }

  public void write(Atoms atoms, Bonds bonds){
    //output atom
    try {
      //skipByte
      int allSkipByte=atoms.getSkipByte();
      if(existBonds)allSkipByte+=bonds.getSkipByte();

      dos.writeInt(allSkipByte);
      ///atoms
      dos.writeInt( atoms.n );
      dos.writeInt( atoms.nData );
      for(int k=0; k<3; k++)for(int l=0; l<3; l++)dos.writeFloat( atoms.h[k][l] );
      for(int k=0; k<3; k++)for(int l=0; l<3; l++)dos.writeFloat( atoms.hinv[k][l] );



      for( int i=0; i<atoms.n; i++ ){
        dos.writeByte(atoms.tag[i]);
        involvedTags.put((int)atoms.tag[i],1);
        for(int k=0; k<3; k++) dos.writeFloat( (float)atoms.r[i][k] );
        for(int k=0; k<atoms.nData; k++){
          dos.writeFloat( (float)atoms.data[i][k] );
          //search range
          if(this.range[k][0]>atoms.data[i][k])this.range[k][0]=atoms.data[i][k];
          if(this.range[k][1]<atoms.data[i][k])this.range[k][1]=atoms.data[i][k];
        }
      }

      //bonds
      if(existBonds){
        dos.writeInt( bonds.getN());
        dos.writeInt( bonds.getCNN());
        dos.writeFloat( bonds.maxBondLength);
        dos.writeInt( bonds.maxCN);
        for( int i=0; i<bonds.getN(); i++ ){
          Bond b=bonds.get(i);
          dos.writeInt( b.i );
          dos.writeInt( b.j );
          for( int k=0; k<3; k++ ) dos.writeFloat( b.origin[k] );
          dos.writeFloat( b.length );
          dos.writeFloat( b.theta );
          dos.writeFloat( b.phi );
        }

        Set set = bonds.CN.keySet();
        Iterator iterator = set.iterator();
        Integer object;
        while(iterator.hasNext()){
          object = (Integer)iterator.next();
          dos.writeInt( object.intValue() );
          dos.writeInt( bonds.CN.get(object) );
        }
      }

      dos.flush();
      bos.flush();
    }
    catch ( IOException e ){
    }


    //store
    if(nAtomsMax<atoms.n)nAtomsMax=atoms.n;
    //if(nDataMax<atoms.nData)nDataMax=atoms.nData;
    //search max h-matrix
    vol=atoms.h[0][0]*(atoms.h[1][1]*atoms.h[2][2]-atoms.h[1][2]*atoms.h[2][1])
      +atoms.h[0][1]*(atoms.h[1][2]*atoms.h[2][0]-atoms.h[1][0]*atoms.h[2][2])
      +atoms.h[0][2]*(atoms.h[1][0]*atoms.h[2][1]-atoms.h[1][1]*atoms.h[2][0]);
    if(vol>vol0){
      for(int i=0;i<3;i++)for(int j=0;j<3;j++)hMax[i][j]=atoms.h[i][j];
      vol0=vol;
    }
  }
  float vol0=0.f,vol=0.f;





  /* reader */
  FileInputStream fis;
  BufferedInputStream bis;
  DataInputStream dis;
  public void ropen(){
    try {
      fis = new FileInputStream( filename );
      bis = new BufferedInputStream( fis );
      dis = new DataInputStream( bis );
    }
    catch ( IOException e ){
    }
  }
  public void rclose(){
    try {
      fis.close();
      bis.close();
      dis.close();
    }
    catch ( IOException e ){
    }
  }

  /**
   * nextフレーム目のデータをセットする．
   * ファイルポインタはnowフレーム目にあるので，nextフレーム目に移動してから読み始める．
   * もしもnow<nextのときは，ファイルを開き直して始めから読み直す．
   */
  public void set(int now, int next,Atoms atoms,Bonds bonds){
    if(next>now){
      for(int i=now+1;i<next;i++){
        try{
          dis.skipBytes(readSkipBytes());
        }
        catch ( IOException e ){}
      }
    }else if(next<=now){
      rclose();
      ropen();
      readHeader(atoms);
      for(int i=0;i<next;i++){
        try{
          dis.skipBytes(readSkipBytes());
        }
        catch ( IOException e ){}
      }
    }
    readSkipBytes();
    read(atoms,bonds);
  }
  int readSkipBytes(){
    int sb=-1;
    try {
      sb=dis.readInt();
    }catch(IOException e ){
    }
    return sb;
  }

  public void readHeader(Atoms atoms){
    try {
      atoms.convDate=dis.readUTF();
      atoms.totalFrame=dis.readInt();
      atoms.startTime=dis.readFloat();
      atoms.timeInterval=dis.readFloat();
      existBonds=dis.readBoolean();
    }catch(IOException e ){
      System.out.println("error at read header");
    }
  }
  public void readFooter(Atoms atoms){
    rclose();
    ropen();
    readHeader(atoms);
    for(int i=0;i<atoms.totalFrame;i++){
      try{
        dis.skipBytes( readSkipBytes() );
      }
      catch ( IOException e ){
        System.out.println("read footer 1");
      }
    }

    try{
      //natom
      nAtomsMax=dis.readInt();
      //hmax
      atoms.hMinLength=Float.MAX_VALUE;
      for(int k=0; k<3; k++){
        float tmp=0.f;
        for(int l=0; l<3; l++){
          atoms.hMax[k][l]=dis.readFloat();
          tmp+=atoms.hMax[k][l]*atoms.hMax[k][l];
        }
        if(atoms.hMinLength>tmp)atoms.hMinLength=tmp;
      }
      atoms.hMinLength=(float)Math.sqrt(atoms.hMinLength);

      //dataRange
      for(int k=0; k<Const.DATA; k++)
        for(int l=0; l<2; l++)
          atoms.originalDataRange[k][l]=dis.readFloat();
      //involved tags
      atoms.nTagMax=dis.readInt();
      atoms.involvedTags=new int[atoms.nTagMax];
      for(int k=0; k<atoms.nTagMax; k++)atoms.involvedTags[k]=dis.readInt();

    }catch(IOException e ){
        System.out.println("read footer 2");
        e.printStackTrace();
    }
    atoms.allocate(nAtomsMax);
    rclose();
    ropen();
    readHeader(atoms);
  }

  void read(Atoms atoms, Bonds bonds){
    try {
      atoms.n=dis.readInt();
      atoms.nData=dis.readInt();
      //h matrix
      for(int k=0; k<3; k++)for(int l=0; l<3; l++)atoms.h[k][l]=dis.readFloat();
      for(int k=0; k<3; k++)for(int l=0; l<3; l++)atoms.hinv[k][l]=dis.readFloat();


      //core info.
      for(int i=0; i<atoms.n; i++){
        //store for trj
        atoms.tag[i]=dis.readByte();
        for(int k=0; k<3; k++) {
          atoms.prev[i][k] = atoms.r[i][k];
          atoms.r[i][k]=dis.readFloat();
        }
        for(int k=0;k<atoms.nData;k++)atoms.data[i][k]=dis.readFloat();
      }

      if(existBonds){
        bonds.clear();
        int bondN = dis.readInt();
        int CNN= dis.readInt();
        bonds.maxBondLength = dis.readFloat();
        bonds.maxCN = dis.readInt();
        for( int i=0; i<bondN; i++ ){
          int ii= dis.readInt();
          int jj= dis.readInt();
          float[] org=new float[3];
          for( int k=0; k<3; k++) org[k]= dis.readFloat();
          float l = dis.readFloat();
          float t = dis.readFloat();
          float p = dis.readFloat();
          bonds.add(ii,jj,org,l,t,p);
        }

        for( int i=0; i<CNN; i++ ){
          int ii= dis.readInt();//key
          int jj= dis.readInt();//counter
          bonds.CN.put(ii,jj);
        }
      }
    }catch(IOException e ){
      System.out.println("error at read body");
      e.printStackTrace();
    }
  }
}
