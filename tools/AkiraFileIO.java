package tools;
import java.io.*;
import java.util.*;
import java.text.*;

import tools.*;
import data.*;
import converter.*;
import viewer.RenderingWindow;

/**
 * File reader/writer specific for Akira program.
 */

public class AkiraFileIO{

  String filename;
  public boolean existBonds=false;

  public AkiraFileIO(String file){
    this.filename=file;
  }

  /* for footer */
  int numAtomsMax=0;
  float[][] hMax = new float[3][3];
  float[][] range = new float[Atom.MAX_NUM_DATA][2];
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

  public void writeHeader( int totalFrame, float startTime,
                           float timeInterval, boolean existBonds){
    Date date = new Date();  //
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
    try {
      dos.writeUTF( sdf.format(date) );//conv. date
      dos.writeInt( totalFrame );
      dos.writeFloat( startTime );
      dos.writeFloat( timeInterval );
      dos.writeBoolean( existBonds );
    }catch ( IOException e ){
      System.out.println("error at write header");
    }
  }

  public void writeFooter(){
    try {
      byte mdata= Atom.MAX_NUM_DATA;
      dos.writeInt(numAtomsMax);
      //hmax
      for(int k=0; k<3; k++)
        for(int l=0; l<3; l++)
          dos.writeFloat( hMax[k][l] );
      //dataRange
      for(int k=0; k<mdata; k++){
        for(int l=0; l<2; l++) 
          dos.writeFloat( range[k][l] );
      }

      //involved tags num.
      dos.writeByte( (byte)involvedTags.size() );
      System.out.println(String.format(" involvedTags.size()= %d",involvedTags.size()));

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
      for(int i=0;i<inc;i++)dos.writeByte((byte)tags[i]);


    }catch ( IOException e ){
      System.out.println("error at write footer");
    }
  }

  public void write( Atoms atoms ){
    //output atom
    try {
      //skipByte
      //int allSkipByte= atoms.getSkipByte();
      //if( existBonds ) allSkipByte+=bonds.getSkipByte();
      int allSkipByte= atoms.getSizeByByte();
      int natm= atoms.getNumAtoms();
      byte mdata= Atom.MAX_NUM_DATA;

      dos.writeInt( allSkipByte );
      //System.out.println(String.format(" allskipbyte= %d",allSkipByte)); 
      ///atoms
      dos.writeInt( natm );
      //System.out.println(String.format(" natm,ndata,mdata= %d %d %d",
      //natm,ndata,mdata ));
      for(int k=0; k<3; k++)for(int l=0; l<3; l++)dos.writeFloat( atoms.hmat[k][l] );
      for(int k=0; k<3; k++)for(int l=0; l<3; l++)dos.writeFloat( atoms.hmati[k][l] );

      for( int i=0; i<natm; i++ ){
        Atom ai= atoms.getAtom(i);
        int nbnd= ai.getNumBonds();
        // tag
        dos.writeByte( ai.tag );
        involvedTags.put( (int)ai.tag, 1 );
        // pos
        for(int k=0; k<3; k++) dos.writeFloat( ai.pos[k] );
        // auxData
        for(int k=0; k<mdata; k++){
          float data= ai.auxData[k];
          dos.writeFloat( data );
          this.range[k][0]= Math.min( this.range[k][0], data );
          this.range[k][1]= Math.max( this.range[k][1], data );
        }
        // bonds
        dos.writeInt( nbnd );
        for( int k=0; k<nbnd; k++ ){
          Bond b= ai.getBond(k);
          dos.writeFloat( b.length );
          dos.writeFloat( b.theta );
          dos.writeFloat( b.phi );
        }
      }
      
      /**
       * Bonds class no longer exists
       *

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
      */

      dos.flush();
      bos.flush();
    }
    catch ( IOException e ){
    }

    //store
    numAtomsMax= Math.max( numAtomsMax, atoms.getNumAtoms() );
    //if(nAtomsMax<atoms.n)nAtomsMax=atoms.n;
    //if(nDataMax<atoms.nData)nDataMax=atoms.nData;
    // search max h-matrix
    vol= atoms.getVolume();
    if(vol>vol0){
      for(int i=0;i<3;i++)for(int j=0;j<3;j++)hMax[i][j]=atoms.hmat[i][j];
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
  public void set( int now, int next, RenderingWindow rw ){
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
      readHeader(rw);
      for(int i=0;i<next;i++){
        try{
          dis.skipBytes(readSkipBytes());
        }
        catch ( IOException e ){}
      }
    }
    readSkipBytes();
    read( rw.atoms );
  }

  private int readSkipBytes(){
    int sb=-1;
    try {
      sb=dis.readInt();
      //System.out.println(String.format(" sb= %d",sb)); 
    }catch(IOException e ){
    }
    return sb;
  }

  public void readHeader( RenderingWindow rw ){
    try {
      rw.convDate=dis.readUTF();
      rw.totalFrame=dis.readInt();
      rw.startTime=dis.readFloat();
      rw.timeInterval=dis.readFloat();
      this.existBonds=dis.readBoolean();
    }catch(IOException e ){
      System.out.println("error at read header");
    }
  }
  public void readFooter( RenderingWindow rw ){
    rclose();
    ropen();
    readHeader(rw);
    for(int i=0;i<rw.totalFrame;i++){
      try{
        dis.skipBytes( readSkipBytes() );
      }
      catch ( IOException e ){
        System.out.println("read footer 1");
        e.printStackTrace();
      }
    }

    try{
      //natom
      //numAtomsMax=dis.readInt();
      rw.maxNumAtoms= dis.readInt();
      //System.out.println(String.format(" numAtomsMax= %d",numAtomsMax));
      //hmax
      //atoms.hMinLength=Float.MAX_VALUE;
      rw.minHmatLength= Float.MAX_VALUE;
      for(int k=0; k<3; k++){
        float tmp=0.f;
        for(int l=0; l<3; l++){
          rw.maxHmat[k][l]=dis.readFloat();
          tmp+= rw.maxHmat[k][l]*rw.maxHmat[k][l];
          //System.out.println(String.format(" k,l,hMax= %d %d %f",k,l,hMax[k][l]));
        }
        rw.minHmatLength= Math.min( rw.minHmatLength, tmp );
      }
      rw.minHmatLength= (float)Math.sqrt( rw.minHmatLength );

      // dataRange
      byte mdata= Atom.MAX_NUM_DATA;
      for(int k=0; k<mdata; k++){
        for(int l=0; l<2; l++){
          rw.orgDataRange[k][l]= dis.readFloat();
          //atoms.originalDataRange[k][l]=dis.readFloat();
        }
        //System.out.println(String.format(" dataRange= %f %f",
        //                                 atoms.originalDataRange[k][0],
        //                                 atoms.originalDataRange[k][1]));
      }
      //involved tags
      rw.maxNumTag= dis.readByte();
      //atoms.nTagMax=dis.readInt();
      //System.out.println(String.format(" atoms.nTagMax= %d",atoms.nTagMax));
      rw.tags=new byte[rw.maxNumTag];
      for(int k=0; k<rw.maxNumTag; k++)
        rw.tags[k]= dis.readByte();

    }catch(IOException e ){
      System.out.println("read footer 2");
      e.printStackTrace();
    }
    //atoms.allocate(numAtomsMax);
    rclose();
    ropen();
    readHeader(rw);
  }

  void read( Atoms atoms ){
    try {
      int n= dis.readInt();
      byte mdata= Atom.MAX_NUM_DATA;
      // h matrix
      for(int k=0; k<3; k++)for(int l=0; l<3; l++)atoms.hmat[k][l]=dis.readFloat();
      for(int k=0; k<3; k++)for(int l=0; l<3; l++)atoms.hmati[k][l]=dis.readFloat();

      // refresh atoms arrayList
      atoms.listAtom= new ArrayList<Atom>();

      for(int i=0; i<n; i++){
        // tag
        Atom a= new Atom( dis.readByte() );
        // pos
        a.pos[0]= dis.readFloat();
        a.pos[1]= dis.readFloat();
        a.pos[2]= dis.readFloat();
        // data
        for(int k=0;k<mdata;k++)
          a.auxData[k]= dis.readFloat();
        // bonds
        int nb= dis.readInt();
        for( int k=0; k<nb; k++ ){
          float length= dis.readFloat();
          float theta  = dis.readFloat();
          float phi    = dis.readFloat();
          Bond b= new Bond( length, theta, phi );
          a.listBond.add( b );
        }
        
        // finally add atom to arrayList
        atoms.listAtom.add( a );
      }

      /**
       * Bonds class no longer exists
       *
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
      */
    }catch(IOException e ){
      System.out.println("error at read body");
      e.printStackTrace();
    }
  }
}
