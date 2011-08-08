      program kvs2cna
c-----------------------------------------------------------------------
c Reduce atoms of kvs-file using Common Neighbor Analysis.
c   - available only for FCC crystal?
c   - change cutoff radius, rc, to apply this to your system
c-----------------------------------------------------------------------
c  USAGE:
c    $ ./kvs2cna < kvs000 > cna000
c-----------------------------------------------------------------------
      implicit none
c-----max. num. of atoms
      integer,parameter:: namax = 1000000
c-----max. num. of neighbors
      integer,parameter:: nnmax = 30
c-----cutoff radius
      real(8),parameter:: rc    = 3.41d-10 /0.5291772d-10 *1.2d0

      integer:: i,j,k,l,m,n,ia,ib
      integer:: nstp,nouterg,noutpmd,iout_count,istp,nerg,ifpmd,npmd
      integer:: natm,nb,ntot,itmp,ipmd
      real(8):: dt
      real(8):: tcpu,tcpu1,tcpu2,tcom
      real(8):: tmp,dmp,treq,ttol,epot,ekin,epot0
c-----simulation box
      real(8):: h(3,3,0:1)
c-----positions, velocities, and accelerations
      integer:: is(namax)
      real(8):: ra(3,namax),va(3,namax),op(2,namax)
c-----cna data
      integer:: ncna
      integer:: icna(namax)
      real(8):: dcna(namax)
c-----species, index of FMV
      integer:: lspr(0:nnmax,namax)
c-----output file names
      character:: cnode*3,cnum*3


      call read_kvs(5,namax,natm,h,is,ra,va,2,2,op)

c-----make pair list, LSPR
      call mk_lspr(natm,ra,h,rc,nnmax,lspr)
c-----common neighbor analysis
      call cna(natm,nnmax,lspr,icna,ncna)
      n= 0
      do i=1,natm
        if( icna(i).ge.0 ) then
          n=n+1
          is(n)= is(i)
          ra(1:3,n)= ra(1:3,i)
          va(1:3,n)= va(1:3,i)
          dcna(n)= icna(i)
        endif
      enddo
      natm= n
      call write_kvs(6,natm,h,is,ra,va,1,1,dcna)

      end program kvs2cna
c=======================================================================
      subroutine mk_lspr(natm,ra,h,rc,nnmax,lspr)
      implicit none
      integer,intent(in):: natm,nnmax
      real(8),intent(in):: ra(3,natm),h(3,3),rc
      integer,intent(out):: lspr(0:nnmax,natm)

      integer:: i,j,n,ic,ici,icj,ir(3),iri(3),irj(3),nc(3),nxyz
     &     ,kux,kuy,kuz,nlsmax,nlsmin
      real(8):: al(3),dc(3),ri(3),rj(3),rij(3),r2,rc2
      integer,allocatable:: lscl(:)

      al(1)= h(1,1)
      al(2)= h(2,2)
      al(3)= h(3,3)
      rc2= rc*rc

      nc(1:3)= int( al(1:3)/rc ) +1
      dc(1:3)= 1d0/nc(1:3)
      nxyz= nc(1)*nc(2)*nc(3)
c      write(6,'(a,4i8)') "nc(1:3),nxyz=",nc(1:3),nxyz
c      write(6,'(a,3es15.7)') "dc(1:3)=",dc(1:3)
c      stop
c-----make cell-list 'LSCL'
      allocate(lscl(natm+nxyz))
      do i=1,natm
        ir(1:3)= int( ra(1:3,i)/dc(1:3) ) +1
        if( ir(1).le.0 ) ir(1)=1
        if( ir(2).le.0 ) ir(2)=1
        if( ir(3).le.0 ) ir(3)=1
        if( ir(1).gt.nc(1) ) ir(1)= nc(1)
        if( ir(2).gt.nc(2) ) ir(2)= nc(2)
        if( ir(3).gt.nc(3) ) ir(3)= nc(3)
c-------cell index
        ic= (ir(1)-1)*nc(2)*nc(3) +(ir(2)-1)*nc(3) +ir(3)
        if( lscl(natm+ic).ne.0 ) then
          lscl(i)= lscl(natm+ic)
        endif
        lscl(natm+ic)= i
      enddo

c      write(6,'(a)') "make lspr"
      lspr(0:nnmax,1:natm)= 0
      do i=1,natm
        n= 0
c        if(mod(i,10).eq.0) write(6,'(a,i8)') "i=",i
        ri(1:3)= ra(1:3,i)
        iri(1:3)= int( ri(1:3)/dc(1:3) ) +1
c-------cell index of atom-i
        ici= (iri(1)-1)*nc(2)*nc(3) +(iri(2)-1)*nc(3) +iri(3)
        do 10 kux=-1,1
        do 10 kuy=-1,1
        do 10 kuz=-1,1
          irj(1)= iri(1) +kux
          irj(2)= iri(2) +kuy
          irj(3)= iri(3) +kuz
c---------periodic boundary condition
          if( irj(1).le.0 ) irj(1)= irj(1) +nc(1)
          if( irj(2).le.0 ) irj(2)= irj(2) +nc(2)
          if( irj(3).le.0 ) irj(3)= irj(3) +nc(3)
          if( irj(1).gt.nc(1) ) irj(1)= irj(1) -nc(1)
          if( irj(2).gt.nc(2) ) irj(2)= irj(2) -nc(2)
          if( irj(3).gt.nc(3) ) irj(3)= irj(3) -nc(3)
          icj= (irj(1)-1)*nc(2)*nc(3) +(irj(2)-1)*nc(3) +irj(3)
          j= lscl(natm+icj)
 11       continue
          if( j.eq.0 ) goto 10
          if( j.eq.i ) goto 12
          n=n+1
          rj(1:3)= ra(1:3,j)
          rij(1:3)= (rj(1:3)-ri(1:3)-anint(rj(1:3)-ri(1:3)))*al(1:3)
          r2= rij(1)*rij(1) +rij(2)*rij(2) +rij(3)*rij(3)
          if( r2.lt.rc2 ) then
            lspr(0,i)=lspr(0,i) +1
            if( lspr(0,i).gt.nnmax ) stop " !!! lspr(0,i).gt.nnmax"
            lspr(lspr(0,i),i)= j
          endif
 12       continue
          j= lscl(j)
          goto 11
 10     continue
      enddo

c-----check
      nlsmax= 0
      nlsmin= 100000
      do i=1,natm
        nlsmax= max(nlsmax,lspr(0,i))
        nlsmin= min(nlsmin,lspr(0,i))
      enddo
c      write(6,'(a,2i8)') "nlsmax,min=",nlsmax,nlsmin

      deallocate(lscl)
      end subroutine mk_lspr
c=======================================================================
      subroutine cna(natm,nnmax,lspr,icna,ncna)
c-----------------------------------------------------------------------
c Reduce natm by using Common Neighbor Analysis
c-----------------------------------------------------------------------
      implicit none
      integer,intent(in):: natm,nnmax,lspr(0:nnmax,natm)
      integer,intent(out):: ncna,icna(natm)

      integer:: i,j,l,m,n,ii,iii,ni,jj,nj,il,jl,n1,n2,iil,nn1,im,iim
     &     ,ib1,ib2,iib1,iib2,n421,n422
      integer,allocatable:: icommon(:),ibond(:,:),nb(:),idc(:,:,:)
      integer,parameter:: lmax= 12
      integer,parameter:: mmax= 12

      allocate(icommon(lmax),ibond(2,mmax),nb(mmax)
     &     ,idc(3,nnmax,natm))

c-----init three indices
      idc(1:3,1:nnmax,1:natm)= 0

c-----for each atom-i, store three indices (LMN)
      do i=1,natm

c-------for each 1st n.n.
        do ii=1,lspr(0,i)
          j=lspr(ii,i)
c---------j>i only
          if(j.le.i) cycle

c---------count num of common neighbors: L
          l= 0
          icommon(1:lmax)= 0
          do iii=1,lspr(0,i)
            ni=lspr(iii,i)
            if(ni.eq.j) cycle
            do jj=1,lspr(0,j)
              nj=lspr(jj,j)
              if(nj.eq.ni) then
                l=l+1
                if(l.gt.lmax) stop " l.gt.lmax!!!"
                icommon(l)= ni
                exit
              endif
            enddo
c---------end of counting L
          enddo

c---------count num of bonds between common neighbors: M
          m= 0
          ibond(1:2,1:mmax)= 0
c---------for each common neighbor-n1
          do il=1,l
            n1=icommon(il)
c-----------for common neighbor-n2 which must be larger than n1
            do jl=1,l
              n2=icommon(jl)
              if(n2.le.n1) cycle
c-------------scan 1st n.n. of n1
              do iil=1,lspr(0,n1)
                nn1=lspr(iil,n1)
                if(nn1.eq.n2) then
                  m=m+1
                  if(m.gt.mmax) stop " m.gt.mmax!!"
                  ibond(1:2,m)= (/ n1,n2 /)
                  exit
                endif
              enddo
            enddo
          enddo

c---------count max num of continuous bonds: N
          nb(1:mmax)= 1
c---------this does not distinguish star and chain connections
          do im=1,m-1
            ib1= ibond(1,im)
            ib2= ibond(2,im)
            do iim=im+1,m
              iib1= ibond(1,iim)
              iib2= ibond(2,iim)
c-------------if two bonds are connected, up nb
              if(iib1.eq.ib1 .or. iib2.eq.ib1
     &             .or. iib1.eq.ib2 .or. iib2.eq.ib2) then
                nb(im)=nb(im) +1
                nb(iim)=nb(iim) +1
              endif
            enddo
          enddo
c---------maximum nb
          n= 0
          do im=1,m
            n= max(nb(im),n)
          enddo

c---------store (LMN) to i
          idc(1:3,ii,i)= (/ l,m,n /)
c---------store (LMN) to j, too
          do jj=1,lspr(0,j)
            if(lspr(jj,j).eq.i) then
              idc(1:3,jj,j)= (/ l,m,n /)
              exit
            endif
          enddo
c-------end of 1st n.n. of i: j
        enddo
c-----end of atom-i
      enddo

c-----reduce atoms
      ncna= 0
      do i=1,natm
c-------default icna=-1: perfect FCC structure
        icna(i)= -1
        n421= 0
        n422= 0
        do ii=1,lspr(0,i)
          l=idc(1,ii,i)
          m=idc(2,ii,i)
          n=idc(3,ii,i)
          if(l.eq.4 .and. m.eq.2 .and. n.eq.1 ) n421=n421 +1
          if(l.eq.4 .and. m.eq.2 .and. n.eq.2 ) n422=n422 +1
        enddo
cc-------check
c        if(mod(i,1000).eq.0) then
c          write(6,'(a,2i8)') "i,lspr(0,i)=",i,lspr(0,i)
c          write(6,'(a,2i5)') "n421,n422=",n421,n422
c        endif
c-------if perfect FCC structure, reduce the atom-i, so skip
        if(n421.eq.12 .and. n422.eq.0) cycle
c        if(mod(i,10).ne.0) cycle
c-------if not FCC
        ncna=ncna +1
c-------if HCP structure, itype(i)= 2
        if(n421.eq.6 .and. n422.eq.6) then
          icna(i)= 2
c-------otherwise, itype(i)= 1
        else
          icna(i)= 1
        endif
      enddo

      deallocate(icommon,ibond,nb,idc)
      end subroutine cna
c=======================================================================
c-----------------------------------------------------------------------
c     Local Variables:
c     compile-command: "ifort -o kvs2cna kvs2cna.f kvs-io.f"
c     End:
