package tools.DelaunayTriangle.voxel;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import tools.DelaunayTriangle.Delaunay2D;
import tools.DelaunayTriangle.MeshData2D;

/**
 * TetrahedronVoxel
 * <pre>
 * 非構造格子から内挿補完でボクセル格子を構築するクラス
 * </pre>
 * @author t.matsuoka
 * @version 0.1
 */
public class TetrahedronVoxel implements Voxel {
  private double[] origin;
  private double[] vec;
  private double[][][] voxel;

  /**
   * コンストラクタ
   *
   * @param del　処理済みDelaunay2D
   * @param height　高さの配列
   * @param scalar　スカラー値
   * @param dx　ボクセル格子のx間隔
   * @param dy　ボクセル格子のy間隔
   * @param dz　ボクセル格子のz間隔
   */
  public TetrahedronVoxel(Delaunay2D del,double[] height,double[][] scalar,
      double dx,double dy,double dz){
    MeshData2D data=del.getDelaunayData();
    int[][] elem=data.getElem();
    GeneralPath gp=new GeneralPath();
//    double[][] node=data.getNode();
    double[][] node=del.getMeshData().getNode();

    gp.moveTo(node[0][0], node[0][1]);
    double zmin=Double.MAX_VALUE;
    for(int i=1;i<node.length;i++){
      gp.lineTo(node[i][0], node[i][1]);
      if(node[i].length<3)continue;
      if(node[i][2]<zmin)zmin=node[i][2];
    }
    gp.closePath();
    Rectangle2D rect=gp.getBounds2D();
    gp=null;
    int xw=Math.round((int)(rect.getWidth()/dx));
    int yw=Math.round((int)(rect.getHeight()/dy));
    int zw=(int)((height[height.length-1]-height[0])/dz);
    origin=new double[]{rect.getX(),rect.getY(),zmin+height[0]};
    vec=new double[]{dx,dy,dz};
    voxel=new double[xw][yw][zw];
    int elemId=0;
    for(int x=0;x<xw;x++){
      double px=origin[0]+dx*(double)x;
      for(int y=0;y<yw;y++){
        double py=origin[1]+dy*(double)y;
        elemId=del.getLocation(elemId, new double[]{px,py});
        if(elemId<0||elemId>elem.length){
          for(int z=0;z<zw;z++){
            voxel[x][y][z]=Double.NaN;
          }
          elemId=0;
          continue;
        }else{
          for(int z=0;z<zw;z++){
            double pz=origin[2]+dz*(double)z;
            voxel[x][y][z]=Double.NaN;
            if(elem[elemId][0]<3||elem[elemId][1]<3||elem[elemId][2]<3)continue;
            double tmp=interporlateZ(
              px,py,node[elem[elemId][0]-3],
              node[elem[elemId][1]-3],node[elem[elemId][2]-3]
            );
            for(int i=1;i<height.length;i++){
              if(pz>=tmp+height[i-1]&&pz<=tmp+height[i]){
                voxel[x][y][z]=interporlateValue(
                  px,py,pz,
                  node[elem[elemId][0]-3],
                  node[elem[elemId][1]-3],
                  node[elem[elemId][2]-3],
                  height[i-1],height[1],
                  scalar[elem[elemId][0]-3][i-1],
                  scalar[elem[elemId][1]-3][i-1],
                  scalar[elem[elemId][2]-3][i-1],
                  scalar[elem[elemId][0]-3][i],
                  scalar[elem[elemId][1]-3][i],
                  scalar[elem[elemId][2]-3][i]);
                break;
              }
            }
          }
        }
      }
    }
  }

  private double distance(double x0,double y0,double z0,
        double x1,double y1,double z1){
    double xx=(x1-x0)*(x1-x0);
    double yy=(y1-y0)*(y1-y0);
    double zz=(z1-z0)*(z1-z0);
    return Math.sqrt(xx+yy+zz);
  }

  private double interporlateValue(double x,double y,double z,double[] v0,
      double[] v1,double[] v2,double h0,double h1,
      double s0,double s1,double s2,double s3,double s4,double s5){

    double r0=distance(x,y,z,v0[0],v0[1],v0[2]+h0);
    if(Math.abs(r0)<1e-16)return s0;
    double r1=distance(x,y,z,v1[0],v1[1],v1[2]+h0);
    if(Math.abs(r1)<1e-16)return s1;
    double r2=distance(x,y,z,v2[0],v2[1],v2[2]+h0);
    if(Math.abs(r2)<1e-16)return s2;
    double r3=distance(x,y,z,v0[0],v0[1],v0[2]+h1);
    if(Math.abs(r3)<1e-16)return s3;
    double r4=distance(x,y,z,v1[0],v1[1],v1[2]+h1);
    if(Math.abs(r4)<1e-16)return s4;
    double r5=distance(x,y,z,v2[0],v2[1],v2[2]+h1);
    if(Math.abs(r5)<1e-16)return s5;
    double vv=s0/r0+s1/r1+s2/r2+s3/r3+s4/r4+s5/r5;
    double ll=1/r0+1/r1+1/r2+1/r3+1/r4+1/r5;
    return vv/ll;
  }

  private double interporlateZ(double x,double y,double[] v0,
      double[] v1,double[] v2){
    double r0=Math.sqrt(Math.pow(v0[0]-x,2)+Math.pow(v0[1]-y,2));
    if(Math.abs(r0)<1e-16)return v0[2];
    double r1=Math.sqrt(Math.pow(v1[0]-x,2)+Math.pow(v1[1]-y,2));
    if(Math.abs(r1)<1e-16)return v1[2];
    double r2=Math.sqrt(Math.pow(v2[0]-x,2)+Math.pow(v2[1]-y,2));
    if(Math.abs(r2)<1e-16)return v2[2];
    double zz=v0[2]/r0+v1[2]/r1+v2[2]/r2;
    double ll=1/r0+1/r1+1/r2;
    return zz/ll;
  }

  @Override
  public double[] getVoxelOrigin() {
    return origin;
  }

  @Override
  public double[] getVoxelVector() {
    return vec;
  }

  @Override
  public double[][][] getVoxel() {
    return voxel;
  }

}
