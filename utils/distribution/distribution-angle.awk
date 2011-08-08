BEGIN{
  nsi=0;
  no=0;
}

{
  if($1==1){
    si[nsi]=$2;
    nsi++;
  }else if($1==2){
    o[no]=$2;
    no++;
  }
}

END{
  dx=5;
  ndiv=180/dx;

  for(i=0;i<ndiv;i++){
    csi[i]=0;
    co[i]=0;
  }

  for(i=0;i<nsi;i++){
    j=int(si[i]/dx);
    csi[j]++;
  }
  for(i=0;i<no;i++){
    j=int(o[i]/dx);
    co[j]++;
  }

  for(i=1;i<ndiv;i++){
    print i*dx,csi[i]/nsi,co[i]/no;
  }

}
