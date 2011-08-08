BEGIN{
  n=0;
  max=-10000;
  min= 10000;
}

{
  if($1 ~/^#/){
  }else{
    a[n]=$1;
    if(a[n]>max)max=a[n];
    if(a[n]<min)min=a[n];
    n++;
  }
}

END{
  ndiv=20;
  dx=0.1;

  for(i=0;i<ndiv;i++){
    cnt[i]=0;
  }


  for(i=0;i<n;i++){
    j=int(a[i]/dx);
    cnt[j]++;
  }

  for(i=1;i<ndiv;i++){
    print i*dx,cnt[i]/n;
  }
}
