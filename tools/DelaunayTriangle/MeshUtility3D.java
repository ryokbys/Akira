package tools.DelaunayTriangle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MeshUtility3D
 * <pre>
 * MeshData3D用ユーティリティクラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class MeshUtility3D {
  private static double EPS=1e-12;

  private MeshUtility3D(){}

  /**
   * 要素（面）の法線ベクトルを計算
   *
   * @param a　節点1
   * @param b　節点2
   * @param c　節点3
   * @return 法線ベクトル（）
   */
  public static double[] getNormal3DAtElem(double[] a,double[] b,double[] c){
    double[] vec0=new double[]{b[0]-a[0],b[1]-a[1],b[2]-a[2]};
    double[] vec1=new double[]{c[0]-a[0],c[1]-a[1],c[2]-a[2]};
    return MeshUtility2D.normalize3d(MeshUtility2D.cross(vec0,vec1));
  }

  /**
   * 節点x,y,z座標の最小・最大値を取得
   *
   * @param arg 節点配列
   * @return 最小・最大(xmin,xmax,ymin,ymax,zmin,zmax)
   */
  public static double[] getMinMax3D(double[][] arg){
    double minX=Double.MAX_VALUE;
    double maxX=-Double.MAX_VALUE;
    double minY=Double.MAX_VALUE;
    double maxY=-Double.MAX_VALUE;
    double minZ=Double.MAX_VALUE;
    double maxZ=-Double.MAX_VALUE;
    for(int i=0;i<arg.length;i++){
      if(!Double.isNaN(arg[i][0])){
        minX=Math.min(minX, arg[i][0]);
        maxX=Math.max(maxX, arg[i][0]);
      }
      if(!Double.isNaN(arg[i][1])){
        minY=Math.min(minY, arg[i][1]);
        maxY=Math.max(maxY, arg[i][1]);
      }
      if(!Double.isNaN(arg[i][2])){
        minZ=Math.min(minZ, arg[i][2]);
        maxZ=Math.max(maxZ, arg[i][2]);
      }
    }
    return new double[]{minX,maxX,minY,maxY,minZ,maxZ};
  }

  /**
   * MeshData2DデータからMeshData3Dデータを生成
   *
   * @param mesh MeshData2D
   * @param node 節点の配列（MeshData2Dの節点配列を基盤とし、高さ方向を拡張した節点配列）
   * @return MeshData3D
   */
  public static MeshData3D createMeshData3DFromMeshData2D(MeshData2D mesh,double[][] node){
    double[][] nd=mesh.getNode();
    if(node.length%nd.length!=0)throw new IllegalArgumentException();
    int[][] e=mesh.getElem();
    List<int[]> tlist=new ArrayList<int[]>();
    int nn=node.length/nd.length;
    int nz=nd.length;
    for(int i=0;i<e.length;i++){
      for(int j=1;j<nn;j++){
        int v00=e[i][0]+(j-1)*nz;
        int v01=e[i][1]+(j-1)*nz;
        int v02=e[i][2]+(j-1)*nz;
        int v10=e[i][0]+j*nz;
        int v11=e[i][1]+j*nz;
        int v12=e[i][2]+j*nz;
        int[] t0=new int[]{v10,v00,v02,v01};
        int[] t1=new int[]{v11,v01,v10,v02};
        int[] t2=new int[]{v12,v02,v11,v10};
        tlist.add(t0);tlist.add(t1);tlist.add(t2);
      }
    }
    int[][] tetra=tlist.toArray(new int[tlist.size()][]);
    return new MeshData3D(node,tetra);
  }

  /**
   * BufferedReaderからMeshData3Dを構築
   *
   * @param br BufferedReader
   * @return MeshData3D
   * @throws IOException
   */
  public static MeshData3D loadMeshData3D(BufferedReader br) throws IOException{
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
    int[][] elem=new int[n][];
    for(int i=0;i<n;i++){
      line=br.readLine();
      array=line.split(",");
      int[] tmp=transStrToInteger(array,1);
      for(int j=0;j<elem.length;j++){
        elem[i][j]=tmp[j+1];
      }
    }
    MeshData3D mesh=new MeshData3D(node,elem);
    line=br.readLine();
    if(line!=null){
      array=line.split(",");
      double[] minmax=transStrToDouble(array,0);
      mesh.setMinMax(minmax);
    }
    br.close();
    return mesh;
  }

  private static double[] transStrToDouble(String[] s,int st){
    double[] ret=new double[s.length];
    for(int i=st;i<ret.length;i++){
      ret[i]=Double.valueOf(s[i]);
    }
    return ret;
  }

  private static int[] transStrToInteger(String[] s,int st){
    int[] ret=new int[s.length];
    for(int i=st;i<ret.length;i++){
      ret[i]=Integer.valueOf(s[i]);
    }
    return ret;
  }

  /**
   * メッシュデータをBufferedWriterへ出力
   *
   * @param mesh メッシュデータ
   * @param bw BufferedWriter
   * @param colName 節点の列名（null可）
   * @throws IOException
   */
  public static void saveMeshData(MeshData3D mesh,BufferedWriter bw,String[] colName) throws IOException{
    double[][] node=mesh.getNode();
    int[][] elem=mesh.getElem();
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
   * MeshData3Dから頂点の配列を生成
   *
   * @param m　メッシュデータ
   * @return
   */
  public static final double[][] createTrianglePoint3dArray(MeshData3D m){
    double[][] node=m.getNode();
    double[][] pos=new double[node.length][];
    for(int i=0;i<pos.length;i++){
      pos[i]=new double[]{node[i][0],node[i][1],node[i][2]};
    }
    int[][] tetra=m.getElem();
    double[][] ret=new double[tetra.length*12][];
    int id=0;
    for(int i=0;i<tetra.length;i++){
      for(int j=0;j<4;j++){
        int[] fs=m.getFace(i,j);
        ret[id++]=pos[fs[0]];
        ret[id++]=pos[fs[1]];
        ret[id++]=pos[fs[2]];
      }
    }
    return ret;
  }

  /**
   * MeshData3D領域の体積を計算
   *
   * @param mesh メッシュデータ
   * @return 領域の体積
   */
  public static double getTetraVolume(MeshData3D mesh){
    double vol=0;
    int n=mesh.getElem().length;
    for(int i=0;i<n;i++){
      double vv=getTetraVolume(mesh,i);
      if(vv<0)System.out.println("111");
      vol +=getTetraVolume(mesh,i);
    }
    return vol;
  }

  /**
   * 四面体の体積を計算
   *
   * @param mesh メッシュデータ
   * @param tid 四面体のID
   * @return 四面体の体積
   */
  public static double getTetraVolume(MeshData3D mesh,int tid){
    double[][] node=mesh.getNode();
    int[] t=mesh.getElem()[tid];
    return getTetraVolume(
      node[t[0]],node[t[1]],node[t[2]],node[t[3]]);
  }

  /**
   * 四面体の体積を計算
   *
   * @param n1　頂点1
   * @param n2　頂点2
   * @param n3　頂点3
   * @param n4　頂点4
   * @return　体積
   */
  public static double getTetraVolume(double[] n1,double[] n2,double[] n3,double[] n4){
    double[][] node=new double[][]{n1,n2,n3,n4};
    return computeTetraVolume(node);
  }

  private static double computeTetraVolume(double[][] node){
    double va=node[1][0]*node[2][1]*node[3][2]+node[0][0]*node[0][1]*node[3][2]
              +node[1][0]*node[0][1]*node[0][2]+node[0][0]*node[2][1]*node[0][2]
              -(node[1][0]*node[2][1]*node[0][2]+node[0][0]*node[0][1]*node[0][2]
                +node[1][0]*node[0][1]*node[3][2]+node[0][0]*node[2][1]*node[3][2]);
    double vb=node[1][1]*node[2][2]*node[3][0]+node[0][1]*node[0][2]*node[3][0]
              +node[1][1]*node[0][2]*node[0][0]+node[0][1]*node[2][2]*node[0][0]
              -(node[1][1]*node[2][2]*node[0][0]+node[0][1]*node[0][2]*node[0][0]
                +node[1][1]*node[0][2]*node[3][0]+node[0][1]*node[2][2]*node[3][0]);
    double vc=node[1][2]*node[2][0]*node[3][1]+node[0][2]*node[0][0]*node[3][1]
              +node[1][2]*node[0][0]*node[0][1]+node[0][2]*node[2][0]*node[0][1]
              -(node[1][2]*node[2][0]*node[0][1]+node[0][2]*node[0][0]*node[0][1]
               +node[1][2]*node[0][0]*node[3][1]+node[0][2]*node[2][0]*node[3][1]);
    double wa=node[1][0]*node[2][2]*node[0][1]+node[0][0]*node[0][2]*node[0][1]
              +node[1][0]*node[0][2]*node[3][1]+node[0][0]*node[2][2]*node[3][1]
              -(node[1][0]*node[2][2]*node[3][1]+node[0][0]*node[0][2]*node[3][1]
                +node[1][0]*node[0][2]*node[0][1]+node[0][0]*node[2][2]*node[0][1]);
    double wb=node[1][1]*node[2][0]*node[0][2]+node[0][1]*node[0][0]*node[0][2]
              +node[1][1]*node[0][0]*node[3][2]+node[0][1]*node[2][0]*node[3][2]
              -(node[1][1]*node[2][0]*node[3][2]+node[0][1]*node[0][0]*node[3][2]
                +node[1][1]*node[0][0]*node[0][2]+node[0][1]*node[2][0]*node[0][2]);
    double wc=node[1][2]*node[2][1]*node[0][0]+node[0][2]*node[0][1]*node[0][0]
              +node[1][2]*node[0][1]*node[3][0]+node[0][2]*node[2][1]*node[3][0]
              -(node[1][2]*node[2][1]*node[3][0]+node[0][2]*node[0][1]*node[3][0]
                +node[1][2]*node[0][1]*node[0][0]+node[0][2]*node[2][1]*node[0][0]);
    return va+vb+vc+wa+wb+wc;
  }

  /**
   * 四面体を包括する球データを計算
   *
   * @param n1　頂点1
   * @param n2　頂点2
   * @param n3　頂点3
   * @param n4　頂点4
   * @return　球データ(x,y,z,radius)
   */
  public static double[] getTetraBoundingSphere(double[] n1,double[] n2,double[] n3,double[] n4){
    double[][] node=new double[][]{n1,n2,n3,n4};
    return computeTetraSphere(node,computeTetraVolume(node));
  }

  /**
   * 四面体を包括する球データを計算
   *
   * @param n1　頂点1
   * @param n2　頂点2
   * @param n3　頂点3
   * @param n4　頂点4
   * @param vol 四面体の体積
   * @return　球データ(x,y,z,radius)
   */
  public static double[] getTetraBoundingSphere(double[] n1,double[] n2,double[] n3,double[] n4,double vol){
    double[][] node=new double[][]{n1,n2,n3,n4};
    return computeTetraSphere(node,vol);
  }

  private static double[] computeTetraSphere(double[][] node,double volume){
    double p11=node[2][1]*node[3][2]+node[0][1]*node[0][2]
                +node[3][1]*node[0][2]+node[0][1]*node[2][2]
                -(node[2][1]*node[0][2]+node[0][1]*node[3][2]
                  +node[3][1]*node[2][2]+node[0][1]*node[0][2]);
    double p12=node[3][0]*node[2][2]+node[0][0]*node[0][2]
                +node[2][0]*node[0][2]+node[0][0]*node[3][2]
                -(node[3][0]*node[0][2]+node[0][0]*node[2][2]
                  +node[2][0]*node[3][2]+node[0][0]*node[0][2]);
    double p13=node[2][0]*node[3][1]+node[0][0]*node[0][1]
                +node[3][0]*node[0][1]+node[0][0]*node[2][1]
                -(node[2][0]*node[0][1]+node[0][0]*node[3][1]
                  +node[3][0]*node[2][1]+node[0][0]*node[0][1]);
    double p21=node[3][1]*node[1][2]+node[0][1]*node[0][2]
                +node[1][1]*node[0][2]+node[0][1]*node[3][2]
                -(node[3][1]*node[0][2]+node[0][1]*node[1][2]
                   +node[1][1]*node[3][2]+node[0][1]*node[0][2]);
    double p22=node[1][0]*node[3][2]+node[0][0]*node[0][2]
                +node[3][0]*node[0][2]+node[0][0]*node[1][2]
                -(node[1][0]*node[0][2]+node[0][0]*node[3][2]
                  +node[3][0]*node[1][2]+node[0][0]*node[0][2]);
    double p23=node[3][0]*node[1][1]+node[0][0]*node[0][1]
                +node[1][0]*node[0][1]+node[0][0]*node[3][1]
                -(node[3][0]*node[0][1]+node[0][0]*node[1][1]
                  +node[1][0]*node[3][1]+node[0][0]*node[0][1]);
    double p31=node[1][1]*node[2][2]+node[0][1]*node[0][2]
                +node[2][1]*node[0][2]+node[0][1]*node[1][2]
                -(node[1][1]*node[0][2]+node[0][1]*node[2][2]
                  +node[2][1]*node[1][2]+node[0][1]*node[0][2]);
    double p32=node[2][0]*node[1][2]+node[0][0]*node[0][2]
                +node[1][0]*node[0][2]+node[0][0]*node[2][2]
                -(node[2][0]*node[0][2]+node[0][0]*node[1][2]
                  +node[1][0]*node[2][2]+node[0][0]*node[0][2]);
    double p33=node[1][0]*node[2][1]+node[0][0]*node[0][1]
                +node[2][0]*node[0][1]+node[0][0]*node[1][1]
                -(node[1][0]*node[0][1]+node[0][0]*node[2][1]
                  +node[2][0]*node[1][1]+node[0][0]*node[0][1]);
    double xyza=node[0][0]*node[0][0]+node[0][1]*node[0][1]+node[0][2]*node[0][2];
    double aa=0.5*(node[1][0]*node[1][0]+node[1][1]*node[1][1]+node[1][2]*node[1][2]-xyza);
    double bb=0.5*(node[2][0]*node[2][0]+node[2][1]*node[2][1]+node[2][2]*node[2][2]-xyza);
    double cc=0.5*(node[3][0]*node[3][0]+node[3][1]*node[3][1]+node[3][2]*node[3][2]-xyza);
    double xx=p11*aa+p21*bb+p31*cc;
    double yy=p12*aa+p22*bb+p32*cc;
    double zz=p13*aa+p23*bb+p33*cc;
    double xv=xx/(volume);
    double yv=yy/(volume);
    double zv=zz/(volume);
    double radius2=node[0][0]*node[0][0]+xv*xv+node[0][1]*node[0][1]+yv*yv
        +node[0][2]*node[0][2]+zv*zv-2*(node[0][0]*xx+node[0][1]*yy
        +node[0][2]*zz)/(volume);
    return new double[]{xv,yv,zv,Math.sqrt(radius2)};
  }

  /**
   * 三次元等高線を取得
   *
   * @param mesh メッシュデータ
   * @param col 等高線を生成するデータの列インデックス
   * @param iso　等高線を生成する値
   * @return 等高線データ(double[][]：等高線の始点と終点)のリスト
   */
  public static List<double[][]> getContour3D(MeshData3D mesh,int col,double iso){
    double[][] node=mesh.getNode();
    double[] val=new double[node.length];
    for(int i=0;i<val.length;i++){
      val[i]=node[i][col];
    }
    int[][] tetra=mesh.getElem();
    List<int[]> tmp=new ArrayList<int[]>();
    for(int i=0;i<tetra.length;i++){
      int[] e0=new int[]{tetra[i][0],tetra[i][1],tetra[i][2]};
      int[] e1=new int[]{tetra[i][0],tetra[i][2],tetra[i][3]};
      int[] e2=new int[]{tetra[i][0],tetra[i][3],tetra[i][1]};
      int[] e3=new int[]{tetra[i][1],tetra[i][2],tetra[i][2]};
      tmp.add(e0);tmp.add(e1);tmp.add(e2);tmp.add(e3);
    }
    int[][] elem=tmp.toArray(new int[tmp.size()][]);
    return getContourList3D(elem,node,val,iso);
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
   * 等値面を取得
   *
   * @param mesh メッシュデータ
   * @param col 等値面を生成するデータの列インデックス
   * @param iso　等値面を生成する値
   * @return 等値面データ(double[][]：等値面の頂点(3点)の座標)のリスト
   */
  public static double[][][] getIsoSurfacePoint3dyArray(MeshData3D mesh,int col,double iso){
    double[][] node=mesh.getNode();
    double[][] vert=new double[node.length][];
    double[] val=new double[node.length];
    for(int i=0;i<vert.length;i++){
      vert[i]=node[i];
      val[i]=node[i][col];
    }
    int[][] tetra=mesh.getElem();
    List<double[][]> tmp=new ArrayList<double[][]>();
    for(int i=0;i<tetra.length;i++){
      double[][] vv=new double[][]{vert[tetra[i][0]],vert[tetra[i][1]],vert[tetra[i][2]],vert[tetra[i][3]]};
      double[] va=new double[]{val[tetra[i][0]],val[tetra[i][1]],val[tetra[i][2]],val[tetra[i][3]]};
      double[][] ans=getIsoSurface(vv,va,iso,EPS);
      if(ans!=null)tmp.add(ans);
    }
    return tmp.toArray(new double[tmp.size()][][]);
  }

  private static double[][] getIsoSurface(double[][] vert,double[] val,double iso,double eps){
    int[] id=new int[]{0,1,2,3};
    id=sort(id,new double[]{val[0],val[1],val[2],val[3]});
    if(iso>=val[id[0]]){
      if(iso>val[id[3]]){
        return null;
      }else{
        if(iso>=val[id[1]]){
          if(iso>=val[id[2]]){
            double[] a=getInterporatePoint3d(vert[id[0]],val[id[0]],vert[id[3]],val[id[3]],iso,eps);
            double[] b=getInterporatePoint3d(vert[id[1]],val[id[1]],vert[id[3]],val[id[3]],iso,eps);
            double[] c=getInterporatePoint3d(vert[id[2]],val[id[2]],vert[id[3]],val[id[3]],iso,eps);
            if(a==null||b==null||c==null)return null;
            if(isAntiCounterclockwise(a,b,c)){
              return new double[][]{a,b,c};
            }else{
              return new double[][]{a,c,b};
            }
          }else{
            double[] a=getInterporatePoint3d(vert[id[0]],val[id[0]],vert[id[2]],val[id[2]],iso,eps);
            double[] b=getInterporatePoint3d(vert[id[0]],val[id[0]],vert[id[3]],val[id[3]],iso,eps);
            double[] c=getInterporatePoint3d(vert[id[1]],val[id[1]],vert[id[2]],val[id[2]],iso,eps);
            double[] e=getInterporatePoint3d(vert[id[1]],val[id[1]],vert[id[3]],val[id[3]],iso,eps);
            if(a==null||b==null||c==null||e==null)return null;
            return getTriFace(a,b,c,e);
          }
        }else{
          double[] a=getInterporatePoint3d(vert[id[0]],val[id[0]],vert[id[2]],val[id[2]],iso,eps);
          double[] b=getInterporatePoint3d(vert[id[0]],val[id[0]],vert[id[3]],val[id[3]],iso,eps);
          double[] c=getInterporatePoint3d(vert[id[0]],val[id[0]],vert[id[1]],val[id[1]],iso,eps);
          if(a==null||b==null||c==null)return null;
          if(isAntiCounterclockwise(a,b,c)){
            return new double[][]{a,b,c};
          }else{
            return new double[][]{a,c,b};
          }
        }
      }
    }else{
      return null;
    }
  }

  private static double[] getInterporatePoint3d(double[] p0,double v0,double[] p1,double v1,double iso,double eps){
    if(v0==v1){
      return null;
    }else if(v1>v0){
      double r=(iso-v0)/(v1-v0);
      if(Math.abs(r-1.0)<eps){
        return p1;
      }else if(Math.abs(r)<eps){
        return p0;
      }else{
        double xx=(p1[0]-p0[0])*r+p0[0];
        double yy=(p1[1]-p0[1])*r+p0[1];
        double zz=(p1[2]-p0[2])*r+p0[2];
        return new double[]{xx,yy,zz};
      }
    }else{
      double r=(iso-v1)/(v0-v1);
      if(Math.abs(r-1.0)<eps){
        return p0;
      }else if(Math.abs(r)<eps){
        return p1;
      }else{
        double xx=(p0[0]-p1[0])*r+p1[0];
        double yy=(p0[1]-p1[1])*r+p1[1];
        double zz=(p0[2]-p1[2])*r+p1[2];
        return new double[]{xx,yy,zz};
      }
    }
  }

  private static int[] sort(int[] id,double[] val){
    for(int i=1;i<id.length;i++){
      if(val[id[i]]<val[id[i-1]]){
        int t=id[i-1];
        id[i-1]=id[i];
        id[i]=t;
        return sort(id,val);
      }
    }
    return id;
  }

  private static boolean isAntiCounterclockwise(double[] a,double[] b,double[] c){
    double val=computeTriangleArea(a,b,c);
    return (val>0);
  }

  private static double computeTriangleArea(double[] p1,double[] p2,double[] p3){
    double a=p1[0]*p2[1];
    double b=p2[0]*p3[1];
    double c=p3[0]*p1[1];
    double d=p1[0]*p3[1];
    double e=p2[0]*p1[1];
    double f=p3[0]*p2[1];
    return 0.5*(a+b+c-d-e-f);
  }

  private static double dist3d(double[] a,double[] b){
    double x=a[0]-b[0];
    double y=a[1]-b[1];
    double z=a[2]-b[2];
    return Math.sqrt(x*x+y*y+z*z);
  }

  private static double[][] getTriFace(double[] a,double[] b,double[] c,double[] d){
    double[] ll=new double[6];
    ll[0]=dist3d(a,b);
    ll[1]=dist3d(a,c);
    ll[2]=dist3d(a,d);
    ll[3]=dist3d(b,c);
    ll[4]=dist3d(b,d);
    ll[5]=dist3d(c,d);
    int idx=0;
    for(int k=1;k<ll.length;k++){
      if(ll[idx]<ll[k])idx=k;
    }
    double[][] ret=new double[6][];
    switch(idx){
    case 0:
      if(isAntiCounterclockwise(a,b,c)){
        ret[0]=a; ret[1]=b; ret[2]=c;
      }else{
        ret[0]=a; ret[1]=c; ret[2]=b;
      }
      if(isAntiCounterclockwise(a,b,d)){
        ret[3]=a; ret[4]=b; ret[5]=d;
      }else{
        ret[3]=a; ret[4]=d; ret[5]=b;
      }
      break;
    case 1:
      if(isAntiCounterclockwise(a,c,b)){
        ret[0]=a; ret[1]=c; ret[2]=b;
      }else{
        ret[0]=a; ret[1]=b; ret[2]=c;
      }
      if(isAntiCounterclockwise(a,c,d)){
        ret[3]=a; ret[4]=c; ret[5]=d;
      }else{
        ret[3]=a; ret[4]=d; ret[5]=c;
      }
      break;
    case 2:
      if(isAntiCounterclockwise(a,d,b)){
        ret[0]=a; ret[1]=d; ret[2]=b;
      }else{
        ret[0]=a; ret[1]=b; ret[2]=d;
      }
      if(isAntiCounterclockwise(a,d,c)){
        ret[3]=a; ret[4]=d; ret[5]=c;
      }else{
        ret[3]=a; ret[4]=c; ret[5]=d;
      }
      break;
    case 3:
      if(isAntiCounterclockwise(b,c,a)){
        ret[0]=b; ret[1]=c; ret[2]=a;
      }else{
        ret[0]=b; ret[1]=a; ret[2]=c;
      }
      if(isAntiCounterclockwise(b,c,d)){
        ret[3]=b; ret[4]=c; ret[5]=d;
      }else{
        ret[3]=b; ret[4]=d; ret[5]=c;
      }
      break;
    case 4:
      if(isAntiCounterclockwise(b,d,a)){
        ret[0]=b; ret[1]=d; ret[2]=a;
      }else{
        ret[0]=b; ret[1]=a; ret[2]=d;
      }
      if(isAntiCounterclockwise(b,d,c)){
        ret[3]=b; ret[4]=d; ret[5]=c;
      }else{
        ret[3]=b; ret[4]=c; ret[5]=d;
      }
      break;
    default:
      if(isAntiCounterclockwise(c,d,a)){
        ret[0]=c; ret[1]=d; ret[2]=a;
      }else{
        ret[0]=c; ret[1]=a; ret[2]=d;
      }
      if(isAntiCounterclockwise(c,d,b)){
        ret[3]=c; ret[4]=d; ret[5]=b;
      }else{
        ret[3]=c; ret[4]=b; ret[5]=d;
      }
      break;
    }
    return ret;
  }
}
