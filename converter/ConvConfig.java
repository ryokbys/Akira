package converter;

import java.io.*;
import java.util.*;

import data.*;

public class ConvConfig {
  /**
   * Configure of KVSConv.
   * Reading function is also implemented
   */
  public static String fileExtension=".Akira";
  /* members*/
  public String systemName =null;
  //public String readFileFormat =null;

  public ArrayList<String>  readFileFormat = new ArrayList<String>();
  public ArrayList<String>  readFilePath   = new ArrayList<String>();
  public ArrayList<String>  readFileName   = new ArrayList<String>();
  public ArrayList<String>  readFileEndian = new ArrayList<String>();
  public ArrayList<Integer> startFrame     = new ArrayList<Integer>();
  public ArrayList<Integer> endFrame       = new ArrayList<Integer>();
  public ArrayList<Integer> frameInc       = new ArrayList<Integer>();


  public float startTime = 0.0f;
  public float timeInterval = 0.0f;

  public boolean isCutX,isCutY,isCutZ;
  public float xMin,xMax;
  public float yMin,yMax;
  public float zMin,zMax;

  public boolean isCutSphere;
  public float cutRadius;
  public float[] cutCenter=new float[3];

  public boolean createBondsWithLength=false;
  public boolean createBondsWithFile=false;
  public String bondFile;

  //bond info
  public ArrayList<Integer> atom1List = new ArrayList<Integer>();
  public ArrayList<Integer> atom2List = new ArrayList<Integer>();
  public ArrayList<Float> lengthList = new ArrayList<Float>();

  //for coarseGraining
  public boolean isReducing=false;
  public int reducedX,reducedY,reducedZ;


  public ConvConfig(){
  }
  public int getTotalFrame(){
    int n=0;
    for(int ii=0;ii<readFilePath.size();ii++){
      for(int i=startFrame.get(ii);
          i<=endFrame.get(ii);
          i+=frameInc.get(ii)){
        n++;
      }
    }
    return n;
  }
  public float getStartTime(){
    return startTime;
  }
  /**
   * 時間間隔を返す
   * 異なるintervalの時には対応できない
   */
  public float getTimeInterval(){
    //really OK?
    return timeInterval*frameInc.get(0);
  }


  /**
   * reading Conf.AKIRAConv
   */
  public void read( String filename ){
    try {
      FileReader fr = new FileReader( filename );
      BufferedReader br = new BufferedReader( fr );
      String line;
      Scanner sc;

      System.out.println("Config of AKIRA Converter");

      //skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));

      //system name
      systemName = line;
      System.out.println(String.format(" |- system name: %s",systemName));

      //skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));

      //read filename
      while(!line.startsWith("#") && !line.equals("")){
        sc = new Scanner(line);
        sc.useDelimiter("\\s*,\\s*");

        Scanner ssc = new Scanner(sc.next());
        ssc.useDelimiter("\\s*:\\s*");
        //path: eliminate last slash
        String format=ssc.next();
        String path=(ssc.next()).replaceFirst("/$", "");
        readFileFormat.add(format);
        readFilePath.add(path);
        readFileName.add(ssc.next());

        startFrame.add(sc.nextInt());
        endFrame.add(sc.nextInt());
        frameInc.add(sc.nextInt());
        if(sc.hasNext())readFileEndian.add(sc.next().toLowerCase());
        else readFileEndian.add("little");
        line = br.readLine();
      }

      String str;
      for(int ii=0;ii<readFilePath.size();ii++){
        System.out.println(" |- To read file...");
        str=String.format("  \\== %s/%s",
                          readFilePath.get(ii),
                          readFileName.get(ii));
        System.out.println(str);
        str=String.format("    \\== Frame: %d to %d at interval=%d",
                          startFrame.get(ii),
                          endFrame.get(ii),
                          frameInc.get(ii));
        System.out.println(str);
      }


      // skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));
      //time
      sc = new Scanner( line );
      startTime = sc.nextFloat();
      timeInterval = sc.nextFloat();
      System.out.println(" |- Start time, interval: "+line);

      // skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));
      //write bonds
      sc = new Scanner( line );
      createBondsWithLength= sc.nextBoolean();

      //skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));

      while(!line.startsWith("#") && !line.equals("")){
        sc = new Scanner(line);
        //sc.useDelimiter("\\s*,\\s*");
        int tag1=sc.nextInt();
        int tag2=sc.nextInt();
        float length=sc.nextFloat();
        atom1List.add(tag1);
        atom2List.add(tag2);
        lengthList.add(length);

        line = br.readLine();

        if(createBondsWithLength){
          System.out.println(" |- Create bonds with length");
          System.out.println(String.format
                             ("  \\== between %d-%d with length= %.2f Ang."
                              ,tag1,tag2,length));
        }
      }


      // skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));
      sc = new Scanner( line );
      createBondsWithFile= sc.nextBoolean();

      // skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));
      bondFile=line;

      if(createBondsWithFile)System.out.println(" |- Create bond with "+bondFile);

      if(createBondsWithLength && createBondsWithFile){
        System.out.println("only one flag. ");
        System.exit(1);
      }


      // skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));
      //cutx
      if( line != null ){
        sc = new Scanner( line );
        isCutX= sc.nextBoolean();
        xMin = sc.nextFloat();
        xMax = sc.nextFloat();
      }
      if(isCutX)System.out.println(String.format(" |= Cut x: %.3f~%.3f",xMin,xMax));

      // skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));
      //cuty
      if( line != null ){
        sc = new Scanner( line );
        isCutY= sc.nextBoolean();
        yMin = sc.nextFloat();
        yMax = sc.nextFloat();
      }
      if(isCutY)System.out.println(String.format(" |= Cut y: %.3f~%.3f",yMin,yMax));

      // skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));
      //cutz
      if( line!= null ){
        sc = new Scanner( line );
        isCutZ= sc.nextBoolean();
        zMin = sc.nextFloat();
        zMax = sc.nextFloat();
      }
      if(isCutZ)System.out.println(String.format(" |= Cut z: %.3f~%.3f",zMin,zMax));

      // skip comment line
      do{
        line = br.readLine();
      }while(line.startsWith("#") || line.equals(""));
      //cut sphere
      if( line != null ){
        sc = new Scanner( line );
        isCutSphere= sc.nextBoolean();
        cutRadius = sc.nextFloat();
        cutCenter[0] = sc.nextFloat();
        cutCenter[1] = sc.nextFloat();
        cutCenter[2] = sc.nextFloat();
      }
      if(isCutSphere)System.out.println(String.format
                                 (" |= Cut shpere: %.3f (%.3f,%.3f,%.3f)"
                                  ,cutRadius
                                  ,cutCenter[0]
                                  ,cutCenter[1]
                                  ,cutCenter[2]));


      System.out.println("");
      br.close();
      fr.close();
    }catch ( IOException e ){
      System.out.println("CHECK akiraconv.conf");
      System.out.println("IS IT REALLY LATEST \"akiraconv.conf\" ?");
      System.exit(1);
    }catch (Exception e){
      System.out.println("================================================");
      System.out.println("|| CHECK akiraconv.conf                       ||");
      System.out.println("|| Is it LATEST/ CORRECT \"akiraconv.conf\" ?   ||");
      System.out.println("================================================");
      System.exit(1);
    }
  }


}
