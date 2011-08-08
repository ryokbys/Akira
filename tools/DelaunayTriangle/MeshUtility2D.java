package tools.DelaunayTriangle;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MeshUtility2D
 * <pre>
 * MeshData2D用ユーティリティクラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class MeshUtility2D {

  private MeshUtility2D(){}

  /**
   * メッシュ節点の法線ベクトルを取得
   * (節点の3列目のデータをz座標とみなす)
   *
   * @param mesh メッシュデータ
   * @return 法線ベクトルの配列
   */
  public static double[][] getNormal3DAtNode(MeshData2D mesh){
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    double[][] ret=new double[elem.length][];
    for(int i=0;i<elem.length;i++){
      double[] p0=node[elem[i][0]];
      double[] p1=node[elem[i][1]];
      double[] p2=node[elem[i][2]];
      double[] vec0=new double[]{p1[0]-p0[0],p1[1]-p0[1],p1[2]-p0[2]};
      double[] vec1=new double[]{p2[0]-p0[0],p2[1]-p0[1],p2[2]-p0[2]};
      ret[i]=cross(vec0,vec1);
    }
    double[][] rm=new double[node.length][3];
    double[] count=new double[node.length];
    for(int i=0;i<elem.length;i++){
      for(int j=0;j<elem[i].length;j++){
        rm[elem[i][j]][0] +=ret[i][0];
        rm[elem[i][j]][1] +=ret[i][1];
        rm[elem[i][j]][2] +=ret[i][2];
        count[elem[i][j]]++;
      }
    }
    for(int i=0;i<rm.length;i++){
      if(count[i]!=0){
        rm[i][0]=rm[i][0]/count[i];
        rm[i][1]=rm[i][1]/count[i];
        rm[i][2]=rm[i][2]/count[i];
      }
    }
    return rm;
  }

  /**
   * メッシュ要素の法線ベクトルを取得
   * (節点の3列目のデータをz座標とみなす)
   *
   * @param mesh メッシュデータ
   * @return 法線ベクトルの配列
   */
  public static double[][] getNormal3DAtElem(MeshData2D mesh){
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    double[][] ret=new double[elem.length][];
    for(int i=0;i<elem.length;i++){
      double[] p0=node[elem[i][0]];
      double[] p1=node[elem[i][1]];
      double[] p2=node[elem[i][2]];
      double[] vec0=new double[]{p1[0]-p0[0],p1[1]-p0[1],p1[2]-p0[2]};
      double[] vec1=new double[]{p2[0]-p0[0],p2[1]-p0[1],p2[2]-p0[2]};
      ret[i]=normalize3d(cross(vec0,vec1));
    }
    return ret;
  }

  /**
   * メッシュ要素の法線ベクトルを取得
   * (節点の3列目のデータをz座標とみなす)
   *
   * @param mesh メッシュデータ
   * @param eid 要素のID
   * @return
   */
  public static double[] getNormal3DAtElem(MeshData2D mesh,int eid){
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    double[] p0=node[elem[eid][0]];
    double[] p1=node[elem[eid][1]];
    double[] p2=node[elem[eid][2]];
    double[] vec0=new double[]{p1[0]-p0[0],p1[1]-p0[1],p1[2]-p0[2]};
    double[] vec1=new double[]{p2[0]-p0[0],p2[1]-p0[1],p2[2]-p0[2]};
    return normalize3d(cross(vec0,vec1));
  }

  /**
   * ベクトル(double[]{x,y,z})を正規化
   *
   * @param p　ベクトル（double[]）
   * @return
   */
  public static double[] normalize3d(double[] p){
    double lg=Math.sqrt(p[0]*p[0]+p[1]*p[1]+p[2]*p[2]);
    double x=p[0]/lg;
    double y=p[1]/lg;
    double z=p[2]/lg;
    return new double[]{x,y,z};
  }

  /**
   * ベクトル(double[]{x,y})を正規化
   *
   * @param p　ベクトル（double[]）
   * @return
   */
  public static double[] normalizeed(double[] p){
    double lg=Math.sqrt(p[0]*p[0]+p[1]*p[1]);
    double x=p[0]/lg;
    double y=p[1]/lg;
    return new double[]{x,y};
  }

  /**
   * ベクトル(double[]{x,y,z})の外積を計算
   *
   * @param p　ベクトル（double[]）
   * @return 計算結果
   */
  public static double[] cross(double[] vec1, double[] vec2){
    return new double[]{
        vec1[1] * vec2[2] - vec1[2] * vec2[1],
        vec1[2] * vec2[0] - vec1[0] * vec2[2],
        vec1[0] * vec2[1] - vec1[1] * vec2[0]
    };
  }

  /**
   * 要素のGeneralPathを取得
   *
   * @param mesh メッシュデータ
   * @param eid 要素ID
   * @return GeneralPath
   */
  public static GeneralPath createTriangle(MeshData2D mesh,int eid){
    double[][] n=mesh.getNode();
    int[][] e=mesh.getElem();
    GeneralPath gp=new GeneralPath();
    gp.moveTo((float)n[e[eid][0]][0], (float)n[e[eid][0]][1]);
    gp.lineTo((float)n[e[eid][1]][0], (float)n[e[eid][1]][1]);
    gp.lineTo((float)n[e[eid][2]][0], (float)n[e[eid][2]][1]);
    gp.closePath();
    return gp;
  }

  /**
   * 要素のGeneralPathを取得
   *
   * @param mesh メッシュデータ
   * @return GeneralPath
   */
  public static GeneralPath createGeneralPath(MeshData2D mesh){
    double[][] n=mesh.getNode();
    int[][] e=mesh.getElem();
    GeneralPath gp=new GeneralPath();
    for(int i=0;i<e.length;i++){
      gp.moveTo((float)n[e[i][0]][0], (float)n[e[i][0]][1]);
      gp.lineTo((float)n[e[i][1]][0], (float)n[e[i][1]][1]);
      gp.lineTo((float)n[e[i][2]][0], (float)n[e[i][2]][1]);
      gp.lineTo((float)n[e[i][0]][0], (float)n[e[i][0]][1]);
    }
    return gp;
  }

  /**
   * BufferedReaderからMeshData2Dを構築
   *
   * @param br BufferedReader
   * @return MeshData2D
   * @throws IOException
   */
  public static MeshData2D loadMeshData2D(BufferedReader br) throws IOException{
    String line=br.readLine();
    String[] array=line.split(",");
    int n=Integer.valueOf(array[0]);
    double[][] node=new double[n][];
    for(int i=0;i<n;i++){
      line=br.readLine();
      array=line.split(",");
      node[i]=transStrToDouble(array,1);
    }
    line=br.readLine();
    array=line.split(",");
    n=Integer.valueOf(array[0]);
    int[][] elem=new int[n][3];
    int[][] map=new int[n][3];
    int[][] boundary=new int[n][3];
    for(int i=0;i<n;i++){
      line=br.readLine();
      array=line.split(",");
      int[] tmp=transStrToInteger(array,1);
      int id=1;
      for(int j=0;j<elem[i].length;j++)elem[i][j]=tmp[id++];
      for(int j=0;j<elem[i].length;j++)map[i][j]=tmp[id++];
      for(int j=0;j<elem[i].length;j++)boundary[i][j]=tmp[id++];
    }
    MeshData2D mesh=new MeshData2D(node,elem,map,boundary);
    line=br.readLine();
    if(line!=null){
      array=line.split(",");
      double[] minmax=transStrToDouble(array,0);
      mesh.setMinMax(minmax);
    }else{
      double minX=Double.MAX_VALUE;
      double maxX=-Double.MAX_VALUE;
      double minY=Double.MAX_VALUE;
      double maxY=-Double.MAX_VALUE;
      for(int i=0;i<node.length;i++){
        minX=Math.min(minX, node[i][0]);
        maxX=Math.max(maxX, node[i][0]);
        minY=Math.min(minY, node[i][1]);
        maxY=Math.max(maxY, node[i][1]);
      }
      mesh.setMinMax(new double[]{minX,maxX,minY,maxY});
    }
    br.close();
    return mesh;
  }


  /**
   * メッシュデータをBufferedWriterへ出力
   *
   * @param mesh メッシュデータ
   * @param bw BufferedWriter
   * @param colName 節点の列名（null可）
   * @throws IOException
   */
  public static void saveMeshData(MeshData2D mesh,BufferedWriter bw,String[] colName) throws IOException{
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    int[][] map=mesh.getMap();
    int[][] boundary=mesh.getBoundary();
    bw.write(Integer.toString(node.length));
    if(colName!=null){
      for(int i=0;i<colName.length;i++){
        bw.write(","+colName[i]);
      }
    }
    bw.write("\n");
    for(int i=0;i<node.length;i++){
      bw.write(Integer.toString(i));
      for(int j=0;j<node[i].length;j++){
        bw.write(","+Double.toString(node[i][j]));
      }
      bw.write("\n");
    }
    bw.write(Integer.toString(elem.length)+"\n");
    for(int i=0;i<elem.length;i++){
      bw.write(Integer.toString(i));
      for(int j=0;j<elem[i].length;j++){
        bw.write(","+Integer.toString(elem[i][j]));
      }
      for(int j=0;j<map[i].length;j++){
        bw.write(","+Integer.toString(map[i][j]));
      }
      for(int j=0;j<boundary[i].length;j++){
        bw.write(","+Integer.toString(boundary[i][j]));
      }
      bw.write("\n");
    }
    double[] minmax=mesh.getMinMax();
    if(minmax!=null){
      for(int i=0;i<minmax.length;i++){
        if(i==0){
          bw.write(Double.toString(minmax[i]));
        }else{
          bw.write(","+Double.toString(minmax[i]));
        }
      }
      bw.write("\n");
    }
    bw.close();
  }

  /**
   * 構造格子データから非構造格子データを生成
   *
   * @param br BufferedReader
   * @return MeshData2D
   * @throws IOException
   */
  public static MeshData2D createMeshData2DFromGridData(BufferedReader br) throws IOException{
    String[][] str=parseCsv(br);
    int mx=Integer.parseInt(str[0][0]);
    int my=Integer.parseInt(str[0][1]);
    double[][] nd=new double[mx*my][str[1].length];
    List<int[]> ml=new ArrayList<int[]>();
    List<int[]> jl=new ArrayList<int[]>();
    int id=0,im=0;
    for(int i=0;i<mx;i++){
      for(int j=0;j<my;j++){
        for(int k=0;k<nd[im].length;k++){
          nd[im][k]=Double.parseDouble(str[im+1][k]);
        }
        im++;
        if(i<mx-1&&j<my-1){
          int[] i0=new int[]{i*my+j,(i+1)*my+j+1,(i+1)*my+j};
          int[] i1=new int[]{i*my+j,i*my+j+1,(i+1)*my+j+1};
          ml.add(i0);
          ml.add(i1);
        }else{
          continue;
        }
        if(i==mx-1||j==my-1)continue;
        if(i==0){
          if(j==0){
            int[] j0=new int[]{id+1,id+my*2-1,-1};
            id++;
            jl.add(j0);
            int[] j1=new int[]{-1,id+1,id-1};
            jl.add(j1);
            id++;
          }else if(j==my-2){
            int[] j0=new int[]{id+1,id+my*2-1,id-1};
            id++;
            jl.add(j0);
            int[] j1=new int[]{-1,-1,id-1};
            jl.add(j1);
            id++;
          }else{
            int[] j0=new int[]{id+1,id+my*2-1,id-1};
            id++;
            jl.add(j0);
            int[] j1=new int[]{-1,id+1,id-1};
            jl.add(j1);
            id++;
          }
        }else if(i==mx-2){
          if(j==0){
            int[] j0=new int[]{id+1,-1,-1};
            id++;
            jl.add(j0);
            int[] j1=new int[]{id-(my*2-1),id+1,id-1};
            jl.add(j1);
            id++;
          }else if(j==my-2){
            int[] j0=new int[]{id+1,-1,id-1};
            id++;
            jl.add(j0);
            int[] j1=new int[]{id-(my*2-1),-1,id-1};
            jl.add(j1);
            id++;
          }else{
            int[] j0=new int[]{id+1,-1,id-1};
            id++;
            jl.add(j0);
            int[] j1=new int[]{id-(my*2-1),id+1,id-1};
            jl.add(j1);
            id++;
          }
        }else{
          if(j==0){
            int[] j0=new int[]{id+1,id+my*2-1,-1};
            id++;
            jl.add(j0);
            int[] j1=new int[]{id-(my*2-1),id+1,id-1};
            jl.add(j1);
            id++;
          }else if(j==my-2){
            int[] j0=new int[]{id+1,id+my*2-1,id-1};
            id++;
            jl.add(j0);
            int[] j1=new int[]{id-(my*2-1),-1,id-1};
            jl.add(j1);
            id++;
          }else{
            int[] j0=new int[]{id+1,id+my*2-1,id-1};
            id++;
            jl.add(j0);
            int[] j1=new int[]{id-(my*2-1),id+1,id-1};
            jl.add(j1);
            id++;
          }
        }
      }
    }
    double[][] node=nd;
    int[][] elem=ml.toArray(new int[ml.size()][]);
    int[][] map=jl.toArray(new int[ml.size()][]);
    int[][] boundary=new int[elem.length][3];
    if(isTurn(node,elem)){
      int[][] ee=new int[elem.length][3];
      int[][] mm=new int[map.length][3];
      for(int i=0;i<elem.length;i++){
        ee[i][0]=elem[i][0];
        ee[i][1]=elem[i][2];
        ee[i][2]=elem[i][1];
        mm[i][0]=map[i][0];
        mm[i][1]=map[i][2];
        mm[i][2]=map[i][1];
      }
      elem=ee;
      map=mm;
    }
    MeshData2D mesh=new MeshData2D(node,elem,map,boundary);
    double minX=Double.MAX_VALUE;
    double maxX=-Double.MAX_VALUE;
    double minY=Double.MAX_VALUE;
    double maxY=-Double.MAX_VALUE;
    for(int i=0;i<node.length;i++){
      minX=Math.min(minX, node[i][0]);
      maxX=Math.max(maxX, node[i][0]);
      minY=Math.min(minY, node[i][1]);
      maxY=Math.max(maxY, node[i][1]);
    }
    mesh.setMinMax(new double[]{minX,maxX,minY,maxY});
    return mesh;
  }

  private static double[] transStrToDouble(String[] s,int st){
    int n=0;
    for(int i=0;i<s.length;i++){
      if(s[i]!=null || s[i].length()>0)n++;
    }
    double[] ret=new double[n];
    for(int i=st;i<n;i++){
      ret[i]=Double.valueOf(s[i]);
    }
    return ret;
  }

  private static int[] transStrToInteger(String[] s,int st){
    int n=0;
    for(int i=0;i<s.length;i++){
      if(s[i]!=null || s[i].length()>0)n++;
    }
    int[] ret=new int[n];
    for(int i=st;i<n;i++){
      ret[i]=Integer.valueOf(s[i]);
    }
    return ret;
  }

  /**
   * 節点x,y座標の最小・最大値を取得
   *
   * @param arg 節点配列
   * @return 最小・最大(xmin,xmax,ymin,ymax)
   */
  public static double[] getMinMax2D(double[][] arg){
    double minX=Double.MAX_VALUE;
    double maxX=-Double.MAX_VALUE;
    double minY=Double.MAX_VALUE;
    double maxY=-Double.MAX_VALUE;
    for(int i=0;i<arg.length;i++){
      if(!Double.isNaN(arg[i][0])){
        minX=Math.min(minX, arg[i][0]);
        maxX=Math.max(maxX, arg[i][0]);
      }
      if(!Double.isNaN(arg[i][1])){
        minY=Math.min(minY, arg[i][1]);
        maxY=Math.max(maxY, arg[i][1]);
      }
    }
    return new double[]{minX,maxX,minY,maxY};
  }

  /**
   * 節点の重複をチェックし、重複メッシュ番号をリストで取得
   *
   * @param arg 節点配列
   * @return 節点の重複チェック結果
   */
  public static List<int[]> isNodeOverlaps(List<double[][]> arg){
    List<int[]> ret=new ArrayList<int[]>();
    Set<Point2D> ss=new HashSet<Point2D>();
    int id=0;
    for(double[][] dd : arg){
      for(int i=0;i<dd.length;i++){
        Point2D px=new Point2D.Double(dd[i][0],dd[i][1]);
        if(ss.contains(px)){
          ret.add(new int[]{id,i});
        }else{
          ss.add(px);
        }
      }
      id++;
    }
    return ret;
  }

  /**
   * メッシュの境界データ更新する（最外郭境界に-1を代入）
   *
   * @param mesh メッシュデータ
   * @return 境界区分情報の配列
   */
  public static int[][] getBoundary(MeshData2D mesh){
    int[][] elem=mesh.getElem();
    int[][] map=mesh.getMap();
    List<int[]> mark=new LinkedList<int[]>();
    for(int i=0;i<elem.length;i++){
      for(int j=0;j<map[i].length;j++){
        if(map[i][j]==-1){
          mark.add(new int[]{elem[i][j],elem[i][(j+1)%3]});
        }
      }
    }
    List<int[]> ret=new ArrayList<int[]>();
    List<Integer> tmp=new ArrayList<Integer>();
    int sz=mark.size();
    int check=-1;
    int start=-1;
    int id=0;
    while(!mark.isEmpty()){
      if(check==-1){
        int[] val=mark.remove(0);
        tmp.add(val[0]);
        tmp.add(val[1]);
        check=val[1];
        start=val[0];
        sz--;
        id=0;
      }else{
        if(mark.get(id)[0]==check){
          int[] val=mark.remove(id);
          sz--;
          if(start==val[1]){
            id=0;
            check=-1;
            tmp.add(val[1]);
            Integer[] it=tmp.toArray(new Integer[tmp.size()]);
            int[] rr=new int[it.length];
            for(int i=0;i<rr.length;i++)rr[i]=it[i];
            ret.add(rr);
            tmp.clear();
          }else{
            tmp.add(val[1]);
            id=0;
            check=val[1];
          }
        }else{
          id++;
        }
      }
      if(id>=sz){
        id=0;
        check=-1;
        Integer[] it=tmp.toArray(new Integer[tmp.size()]);
        int[] rr=new int[it.length];
        for(int i=0;i<rr.length;i++)rr[i]=it[i];
        ret.add(rr);
        tmp.clear();
      }
    }
    return ret.toArray(new int[ret.size()][]);
  }

  /**
   * 二次元等高線データを取得
   *
   * @param mesh メッシュデータ
   * @param val　等高線を取得する値
   * @param col 等高線を取得するデータ列
   * @return 等高線データ(double[][]：等高線の始点と終点)のリスト
   */
  public static List<double[][]> getContour2D(MeshData2D mesh,double val,int col){
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    double[] vals=new double[node.length];
    for(int i=0;i<vals.length;i++)vals[i]=node[i][col];
    return getContourList2D(elem,node,vals,val);
  }

  /**
   * 二次元等高線データを取得
   *
   * @param mesh メッシュデータ
   * @param val　等高線を取得する値
   * @param col　等高線を取得するデータ列
   * @param mark　要素のマーカー(mark[i]!=0の場合、その要素は等高線を描画しない)
   * @return 等高線データ(double[][]：等高線の始点と終点)のリスト
   */
  public static List<double[][]> getContour2D(MeshData2D mesh,double val,int col,int[] mark){
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    double[] vals=new double[node.length];
    for(int i=0;i<vals.length;i++)vals[i]=node[i][col];
    return getContourList2D(elem,node,vals,val,mark);
  }

  private static List<double[][]> getContourList2D(int[][] elem,double[][] node,double[] vals,double val){
    List<double[][]> ret=new ArrayList<double[][]>();
    for(int i=0;i<elem.length;i++){
      double[][] nd=new double[][]{
          node[elem[i][0]],node[elem[i][1]],node[elem[i][2]]};
      double[] vv=new double[]{
        vals[elem[i][0]],vals[elem[i][1]],vals[elem[i][2]]};
      double[][] var=getContourArray2D(nd,vv,val);
      if(var!=null)ret.add(var);
    }
    return ret;
  }

  private static List<double[][]> getContourList2D(int[][] elem,double[][] node,double[] vals,double val,int[] mark){
    List<double[][]> ret=new ArrayList<double[][]>();
    for(int i=0;i<elem.length;i++){
      if(mark[i]!=0)continue;
      double[][] nd=new double[][]{
          node[elem[i][0]],node[elem[i][1]],node[elem[i][2]]};
      double[] vv=new double[]{
        vals[elem[i][0]],vals[elem[i][1]],vals[elem[i][2]]};
      double[][] var=getContourArray2D(nd,vv,val);
      if(var!=null)ret.add(var);
    }
    return ret;
  }

  private static double[][] getContourArray2D(double[][] d,double[] vals,double val){
    int[] it=new int[]{0,1,2};
    double[] tmpVal=new double[]{vals[0],vals[1],vals[2]};
    double[][] p=sort(it,d,tmpVal);
    if(val>=vals[it[0]]){
      if(val>vals[it[2]]){
        return null;
      }else{
        if(val>=vals[it[1]]){
          double[] a=getPoint2D(p[0],vals[it[0]],p[2],vals[it[2]],val);
          double[] b=getPoint2D(p[1],vals[it[1]],p[2],vals[it[2]],val);
          if(a==null||b==null)return null;
          return new double[][]{a,b};
        }else{
          double[] a=getPoint2D(p[0],vals[it[0]],p[2],vals[it[2]],val);
          double[] b=getPoint2D(p[0],vals[it[0]],p[1],vals[it[1]],val);
          if(a==null||b==null)return null;
          return new double[][]{a,b};
        }
      }
    }else{
      return null;
    }
  }

  private static double[] getPoint2D(double[] small,double smallVal,double[] large,double largeVal,double val){
    if(largeVal==smallVal)return null;
    double rr=(val-smallVal)/(largeVal-smallVal);
    double x=small[0];
    double y=small[1];
    double xx=(large[0]-x)*rr+x;
    double yy=(large[1]-y)*rr+y;
    double l10=Math.sqrt(Math.pow(xx-x, 2)+Math.pow(yy-y, 2));
    double l11=Math.sqrt(Math.pow(xx-large[0], 2)+Math.pow(yy-large[1], 2));
    double[] ret=new double[small.length];
    if(l10==0){
      for(int i=0;i<ret.length;i++)ret[i]=small[i];
    }else if(l10==1){
      for(int i=0;i<ret.length;i++)ret[i]=large[i];
    }else{
      double r0=1/l10;
      double r1=1/l11;
      ret[0]=xx;  ret[1]=yy;
      for(int i=2;i<ret.length;i++){
        ret[i]=(small[i]*r0+large[i]*r1)+(r0+r1);
      }
    }
    return ret;
  }

  private static double[][] sort(int[] it,double[][] d,double[] val){
    for(int i=1;i<it.length;i++){
      if(val[it[i]]<val[it[i-1]]){
        int t=it[i-1];
        it[i-1]=it[i];
        it[i]=t;
        return sort(it,d,val);
      }
    }
    return new double[][]{d[it[0]],d[it[1]],d[it[2]]};
  }

  /**
   * 三次元等高線データを取得
   * (節点の3列目のデータをz座標とみなす)
   *
   * @param mesh メッシュデータ
   * @param val　等高線を取得する値
   * @param col 等高線を取得するデータ列
   * @return 等高線データ(double[][]：等高線の始点と終点)のリスト
   */
  public static List<double[][]> getContour3D(MeshData2D mesh,double val,int col){
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    double[] vals=new double[node.length];
    for(int i=0;i<vals.length;i++)vals[i]=node[i][col];
    return getContourList3D(elem,node,vals,val);
  }

  /**
   * 三次元等高線データを取得
   * (節点の3列目のデータをz座標とみなす)
   *
   * @param mesh メッシュデータ
   * @param val　等高線を取得する値
   * @param col 等高線を取得するデータ列
   * @param mark　要素のマーカー(mark[i]!=0の場合、その要素は等高線を描画しない)
   * @return 等高線データ(double[][]：等高線の始点と終点)のリスト
   */
  public static List<double[][]> getContour3D(MeshData2D mesh,double val,int col,int[] mark){
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    double[] vals=new double[node.length];
    for(int i=0;i<vals.length;i++)vals[i]=node[i][col];
    return getContourList3D(elem,node,vals,val,mark);
  }

  private static List<double[][]> getContourList3D(int[][] elem,double[][] node,double[] vals,double val){
    List<double[][]> ret=new ArrayList<double[][]>();
    for(int i=0;i<elem.length;i++){
      double[][] nd=new double[][]{
          node[elem[i][0]],node[elem[i][1]],node[elem[i][2]]};
      double[] vv=new double[]{
        vals[elem[i][0]],vals[elem[i][1]],vals[elem[i][2]]};
      double[][] var=getContourList3D(nd,vv,val);
      if(var!=null)ret.add(var);
    }
    return ret;
  }

  private static List<double[][]> getContourList3D(int[][] elem,double[][] node,double[] vals,double val,int[] mark){
    List<double[][]> ret=new ArrayList<double[][]>();
    for(int i=0;i<elem.length;i++){
      if(mark[i]!=0)continue;
      double[][] nd=new double[][]{
          node[elem[i][0]],node[elem[i][1]],node[elem[i][2]]};
      double[] vv=new double[]{
        vals[elem[i][0]],vals[elem[i][1]],vals[elem[i][2]]};
      double[][] var=getContourList3D(nd,vv,val);
      if(var!=null)ret.add(var);
    }
    return ret;
  }

  private static double[][] getContourList3D(double[][] d,double[] vals,double val){
    int[] it=new int[]{0,1,2};
    double[] tmpVal=new double[]{vals[0],vals[1],vals[2]};
    double[][] p=sort(it,d,tmpVal);
    if(val>=vals[it[0]]){
      if(val>vals[it[2]]){
        return null;
      }else{
        if(val>=vals[it[1]]){
          double[] a=getPoint3D(p[0],vals[it[0]],p[2],vals[it[2]],val);
          double[] b=getPoint3D(p[1],vals[it[1]],p[2],vals[it[2]],val);
          if(a==null||b==null)return null;
          return new double[][]{a,b};
        }else{
          double[] a=getPoint3D(p[0],vals[it[0]],p[2],vals[it[2]],val);
          double[] b=getPoint3D(p[0],vals[it[0]],p[1],vals[it[1]],val);
          if(a==null||b==null)return null;
          return new double[][]{a,b};
        }
      }
    }else{
      return null;
    }
  }

  private static double[] getPoint3D(double[] small,double smallVal,double[] large,double largeVal,double val){
    if(largeVal==smallVal)return null;
    double rr=(val-smallVal)/(largeVal-smallVal);
    if(rr==1.0){
      return large;
    }else if(rr==0.0){
      return small;
    }else{
      double x=small[0];
      double y=small[1];
      double z=small[2];
      double xx=(large[0]-x)*rr+x;
      double yy=(large[1]-y)*rr+y;
      double zz=(large[2]-z)*rr+z;
      double[] ret=new double[small.length];
      ret[0]=xx;
      ret[1]=yy;
      ret[2]=zz;
      for(int i=3;i<ret.length;i++){
        ret[i]=(large[i]-small[i])*rr+small[i];
      }
      return ret;
    }
  }

  private static boolean isTurn(double[][] n,int[][] e){
    for(int i=0;i<e.length;i +=e.length/10){
      double r=getTriArea(n[e[i][0]],n[e[i][1]],n[e[i][2]]);
      if(r<0){
        return true;
      }
    }
    return false;
  }

  private static double getTriArea(double[] p0,double[] p1,double[] p2){
    double a=p0[0]*p1[1];
    double b=p1[0]*p2[1];
    double c=p2[0]*p0[1];
    double d=p0[0]*p2[1];
    double e=p1[0]*p0[1];
    double f=p2[0]*p1[1];
    return 0.5*(a+b+c-d-e-f);
  }

  private static String[][] parseCsv(BufferedReader reader) throws IOException{
    String[][] ret=parse(reader);
    reader.close();
    return ret;
  }

  private static String[][] parse(BufferedReader reader) throws IOException{
    final String REG="\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|([^,]+)|,|";
    ArrayList<String[]> list=new ArrayList<String[]>();
    Pattern pattern = Pattern.compile(REG);
    String line;
    while((line=reader.readLine())!=null){
      String[] sp=split(pattern,line);
      list.add(sp);
    }
    if(list.size()==0)return new String[0][0];
    return list.toArray(new String[list.size()][]);
  }

  private static String[] split(Pattern pattern,String line){
    Matcher matcher=pattern.matcher(line);
    List<String> list=new ArrayList<String>();
    int index=0;
    int com=0;
    while(index<line.length()){
      if(matcher.find(index+com)){
        String s=matcher.group();
        index=matcher.end();
        list.add(s);
        com=1;
      }
    }
    return list.toArray(new String[list.size()]);
  }

  /**
   * MeshData2Dから頂点の配列を生成
   *
   * @param m　メッシュデータ
   * @param zCol z軸の列インデックス
   * @return
   */
  public static final double[][] createTrianglePoint3dArray(MeshData2D m,int zCol){
    double[][] node=m.getNode();
    double[][] ver=new double[node.length][3];
    if(zCol<0||zCol>=node[0].length){
      for(int i=0;i<ver.length;i++){
        ver[i]=new double[]{node[i][0],node[i][1],0};
      }
    }else{
      for(int i=0;i<ver.length;i++){
        ver[i]=new double[]{node[i][0],node[i][1],node[i][zCol]};
      }
    }
    int[][] elem=m.getElem();
    double[][] ret=new double[elem.length*3][];
    int id=0;
    for(int i=0;i<elem.length;i++){
      ret[id++]=ver[elem[i][0]];
      ret[id++]=ver[elem[i][1]];
      ret[id++]=ver[elem[i][2]];
    }
    return ret;
  }

  /**
   * Voronoi図を取得
   *
   * @param mesh
   * @return
   */
  public static double[][] getVoronoi(MeshData2D mesh){
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    int[][] map=mesh.getElem();
    int num=elem.length;
    List<double[]> ret=new ArrayList<double[]>();
    for(int i=0;i<num;i++){
      double[] p0=getVoronoiCenter(
          node[elem[i][0]],node[elem[i][1]],node[elem[i][2]]);
      for(int j=0;j<map[i].length;j++){
        if(map[i][j]!=-1){
          double[] p1=getVoronoiCenter(
              node[elem[map[i][j]][0]],
              node[elem[map[i][j]][1]],
              node[elem[map[i][j]][2]]);
          ret.add(p0);
          ret.add(p1);
        }
      }
    }
    return ret.toArray(new double[ret.size()][]);
  }

  /**
   * Voronoi分岐点の配列を取得
   *
   * @param mesh
   * @return
   */
  public static double[][] getVoronoiPoint(MeshData2D mesh){
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
    double[][] ret=new double[elem.length][];
    for(int i=0;i<elem.length;i++){
      double[] p=node[elem[i][0]];
      double[] q=node[elem[i][1]];
      double[] r=node[elem[i][2]];
      ret[i]=getVoronoiCenter(p,q,r);
    }
    return ret;
  }

  private static double[] getVoronoiCenter(double[] p,double[] q,double[] r){
    double px=p[0];
    double py=p[1];
    double qx=q[0];
    double qy=q[1];
    double rx=r[0];
    double ry=r[1];
    double a1=-0.5*(px*px-qx*qx+py*py-qy*qy);
    double b1=px-qx;
    double c1=py-qy;
    double a2=-0.5*(qx*qx-rx*rx+qy*qy-ry*ry);
    double b2=qx-rx;
    double c2=qy-ry;
    double w=(b1*c2)-(b2*c1);
    double x=-((a1*c2)-(a2*c1));
    double y=(a1*b2)-(a2*b1);
    return new double[]{x/w,y/w};
  }
}
