package converter;

public class Reducer{

  /*
   * Reducing atoms(atoms.nAtoms) to reducedAtoms(nx*ny*nz)
   */

  /*
   * void reduce(){
   *   int nx=cconf.reducedX;
   *   int ny=cconf.reducedY;
   *   int nz=cconf.reducedZ;
   *
   *   int ncg=nx*ny*nz;
   *   int[] nCount=new int[ncg];
   *   for(int i=0;i<ncg;i++)nCount[i]=0;
   *
   *   float lx=atoms.h[0][0]/nx;
   *   float ly=atoms.h[1][1]/ny;
   *   float lz=atoms.h[2][2]/nz;
   *
   *   reducedAtoms.allocate4Conv(ncg);
   *   reducedAtoms.nAtoms=ncg;
   *   reducedAtoms.readNAtoms=ncg;
   *   reducedAtoms.ntag=atoms.ntag;
   *   reducedAtoms.ndata=atoms.ndata;
   *   reducedAtoms.h=atoms.h;
   *
   *   int inc=0;
   *   for(int i=0;i<nx;i++){
   *     for(int j=0;j<ny;j++){
   *       for(int k=0;k<nz;k++){
   *         reducedAtoms.tag[inc]=9;
   *         reducedAtoms.ra[inc][0]=lx*(i+0.5f);
   *         reducedAtoms.ra[inc][1]=ly*(j+0.5f);
   *         reducedAtoms.ra[inc][2]=lz*(k+0.5f);
   *         inc++;
   *       }
   *     }
   *   }
   *   int ix,iy,iz,ii;
   *   for(int i=0;i<atoms.readNAtoms;i++){
   *     ix=(int)(atoms.ra[i][0]/lx);
   *     if(ix>=nx)ix=nx-1;
   *     iy=(int)(atoms.ra[i][1]/ly);
   *     if(iy>=ny)iy=ny-1;
   *     iz=(int)(atoms.ra[i][2]/lz);
   *     if(iz>=nz)iz=nz-1;
   *     ii=iz+iy*nz+ix*nz*ny;
   *     if(ii>ncg)
   *       System.out.println(String.format("cg i: %d %d %d %d",ix,iy,iz,ii));
   *
   *     nCount[ii]++;
   *     for(int j=0;j<Const.DATA;j++)reducedAtoms.data[ii][j]+=atoms.data[i][j];
   *   }
   *
   *   //average
   *   for(int i=0;i<ncg;i++)
   *     for(int j=0;j<Const.DATA;j++)reducedAtoms.data[i][j]/=nCount[i];
   *
   *   reducedAtoms.skipByte=
   *     4//nAtoms
   *     +4//ntag
   *     +4//ndata
   *     +4*3*3//h[3][3]
   *     +(1//tag
   *       +4*3//ra
   *       //+4*3//va
   *       +4*reducedAtoms.ndata//data
   *       )*reducedAtoms.nAtoms;
   * }//end of reducing
   */

}
