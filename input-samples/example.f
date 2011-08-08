       program ljmd
c     Last modified <2011-05-20 19:43:57 by NAKAMURA Takahide>
c
c     Molecular dynamics (MD) simulation
c      with the Lennard-Jones potential
c       in Free Boundary Condition.

      implicit none
      integer,parameter::nvolume=1
      integer,parameter::nvx=2
      integer,parameter::nvy=nvx
      integer,parameter::nvz=nvx

      integer,parameter::nscale=1
c-----# of copy for each direction
      integer,parameter::nx=3
      integer,parameter::ny=nx
      integer,parameter::nz=nx
c-----total # of atoms
      integer,parameter::natm=4*nx*ny*nz
      integer,parameter::ndata=5
c-----dt
      real*8,parameter::dt=40d0
      real*8,parameter::dt2=0.5d0*dt
c-----iteration limit
      integer,parameter::nloop=10000
c-----output interval
      integer,parameter::mts_out=max(nloop/11,1)
      integer,parameter::nout=mts_out
c-----initial temperature
      real*8,parameter::temp_init=50d0




c-----LJ parameter for Ar in a.u.
      real*8,parameter::eps=1.032326d-2/2.72116d1
      real*8,parameter::sgm=3.41d0/5.29177d-1
      real*8,parameter::mass=1840d0*40d0
c-----cut-off radius
      real*8,parameter::rcut=2.5d0*sgm
c-----lattice adjustment parameter for 3D LJ potential.
      real*8,parameter::fac=0.9815d0

      real*8,parameter::zero=0d0
      real*8,parameter::tempau=3.15785d5

c-----for timer
      real*8 ts,te
c-----simulation box size
      real*8 al(3),h(3,3)
c-----basic fcc lattice vector
      real*8 rfcc(3,4)
c-----atom vector
      real*8 r(3,natm),r0(3,natm)
c-----atom velocity
      real*8 rv(3,natm)
c-----atom accel
      real*8 ra(3,natm)

c-----declaration
      integer iloop,i,j,k,l,inc,imts
      real*8 dseed,cunit,vsqr,scale,epot,epot0,ekin,adata(9),v
      character*6 outfile
      character*7 outfile2


c-----timer starts
      call cpu_time(ts)

      imts=-1

c=====set initial configurarion
c-----set up fcc lattice
      cunit=2d0**(1d0/6)*sgm/(0.5d0*sqrt(2d0))*fac
c-----simulation box size
      al(1)=cunit*nx*nscale
      al(2)=cunit*ny*nscale
      al(3)=cunit*nz*nscale
      h=0d0
      h(1,1)=al(1)
      h(2,2)=al(2)
      h(3,3)=al(3)
c-----basic fcc unit in reduced unit
      rfcc(1,1)=0.0d0
      rfcc(2,1)=0.0d0
      rfcc(3,1)=0.0d0
       rfcc(1,2)=0.5d0
       rfcc(2,2)=0.5d0
       rfcc(3,2)=0.0d0
      rfcc(1,3)=0.5d0
      rfcc(2,3)=0.0d0
      rfcc(3,3)=0.5d0
       rfcc(1,4)=0.0d0
       rfcc(2,4)=0.5d0
       rfcc(3,4)=0.5d0

      inc=0
      do i=0,nx-1
        do j=0,ny-1
          do k=0,nz-1
            do l=1,4
              inc=inc+1
              r(1,inc)=(rfcc(1,l)+dble(i))*cunit+1d-1
              r(2,inc)=(rfcc(2,l)+dble(j))*cunit+1d-1
              r(3,inc)=(rfcc(3,l)+dble(k))*cunit+1d-1
            enddo
          enddo
        enddo
      enddo
      do i=1,natm
        do j=1,3
          r0(j,i)=r(j,i)
        enddo
      enddo


c-----random velocity
      dseed=1234d0
      vsqr=0d0
      do i=1,natm
        do j=1,3
          call myrnd(rv(j,i),dseed)
          rv(j,i)=rv(j,i)-0.5d0
          vsqr=vsqr+rv(j,i)*rv(j,i)*mass*0.5d0
        enddo
      enddo
c-----scale for init temp
      scale=dsqrt(3d0*natm*temp_init/(vsqr*3.158d5))
      do i=1,natm
        do j=1,3
          rv(j,i)=rv(j,i)*scale
        enddo
      enddo
      call EvalProps(epot0,r,natm,sgm,eps,rcut,al,ekin,rv,mass)

c=====drag
c      r(1,1)=r(1,1)-10d0
c      r(2,1)=r(2,1)+10d0
c      r(3,1)=r(3,1)+10d0
c      print*,'input velocity(recomend less than 1d-2)'
c      read(*,*)rv(1,1)
c      print*,''

      rv(1,1)=9d-3
      rv(2,1)=9d-3
      rv(3,1)=9d-3

c-----compute initial accel
      call CompAccel(ra,r,natm,sgm,eps,rcut,al,mass)
      open(333,file='ene.dat')

c=====iteration starts (velocity verlet scheme)
      do iloop=1,nloop
c-------KVS
        if(mod(iloop,mts_out).eq.1)then
          call EvalProps(epot,r,natm,sgm,eps,rcut,al,ekin,rv,mass)
          write(333,'(i10,20e12.4)')iloop,ekin,epot-epot0,ekin+epot
     &         -epot0
          write(*,'(i10,20e12.4)')iloop,ekin,epot-epot0,ekin+epot-epot0
          imts=imts+1
          outfile='akr000'
          outfile2='bakr000'
          write(outfile(4:6),'(i3.3)') imts
          write(outfile2(5:7),'(i3.3)') imts
          open(111,file=outfile)
          open(222,file=outfile2,form="unformatted")
          write(111,'(4i10)')natm,ndata,nvolume,nvolume*nvx*nvy*nvz
          write(111,'(3es16.8)')(h(i,1),i=1,3)
          write(111,'(3es16.8)')(h(i,2),i=1,3)
          write(111,'(3es16.8)')(h(i,3),i=1,3)

          write(222)natm,ndata,nvolume,nvolume*nvx*nvy*nvz
          write(222)real(h(1,1)),real(h(1,2)),real(h(1,3))
          write(222)real(h(2,1)),real(h(2,2)),real(h(2,3))
          write(222)real(h(3,1)),real(h(3,2)),real(h(3,3))

          do i=1,natm
            adata(1:3)=rv(1:3,i)
            adata(4)=(rv(1,i)*rv(1,i)+rv(2,i)*rv(2,i)+rv(3,i)*rv(3,i))
     &           *mass/3d0*tempau
            adata(5)=ra(1,i)
            adata(6)=ra(2,i)
            adata(7)=ra(3,i)
            adata(8)=dble(i)
            adata(9)=dble(i*i)
            write(111,'(i5,20e17.7)')
     &           mod(i,2)+1,r(1:3,i)/al(1:3),adata(1:ndata)
            write(222)int(mod(i,100)+1),real(r(1:3,i)/al(1:3))
     &           ,real(adata(1:ndata))
          enddo
c         volume1
          write(111,'(3i10)')nvx,nvy,nvz
          write(111,'(3es16.8)')(h(i,i)*0d0,i=1,3) !shift
          write(111,'(3es16.8)')(h(i,1)/4d0,i=1,3)
          write(111,'(3es16.8)')(h(i,2)/4d0,i=1,3)
          write(111,'(3es16.8)')(h(i,3)/4d0,i=1,3)
          write(222)nvx,nvy,nvz
          write(222)real(h(1,1)*0d0),real(h(2,2)*0d0),real(h(3,3)*0d0)!shift
          write(222)real(h(1,1)/4d0),real(h(1,2)/4d0),real(h(1,3)/4d0)
          write(222)real(h(2,1)/4d0),real(h(2,2)/4d0),real(h(2,3)/4d0)
          write(222)real(h(3,1)/4d0),real(h(3,2)/4d0),real(h(3,3)/4d0)
          do i=1,nvz
            do j=1,nvy
              do k=1,nvx
                v=(i/real(nvz))**2+(j/real(nvy))**2+(k/real(nvx))**2
                write(111,'(3es16.8)')v
                write(222)real(v)
              enddo
            enddo
          enddo
c         volume2
          write(111,'(3i10)')nvx,nvy,nvz
          write(111,'(3es16.8)')(h(i,i)*0.5d0,i=1,3) !shift
          write(111,'(3es16.8)')(h(i,1)/4d0,i=1,3)
          write(111,'(3es16.8)')(h(i,2)/4d0,i=1,3)
          write(111,'(3es16.8)')(h(i,3)/4d0,i=1,3)
          write(222)nvx,nvy,nvz
          write(222)real(h(1,1)*0.5),real(h(2,2)*0.5),real(h(3,3)*0.5)
          write(222)real(h(1,1)/4d0),real(h(1,2)/4d0),real(h(1,3)/4d0)
          write(222)real(h(2,1)/4d0),real(h(2,2)/4d0),real(h(2,3)/4d0)
          write(222)real(h(3,1)/4d0),real(h(3,2)/4d0),real(h(3,3)/4d0)
          do i=1,nvz
            do j=1,nvy
              do k=1,nvx
                v=(i/real(nvz))**2+(j/real(nvy))**2+(k/real(nvx))**2
                write(111,'(3es16.8)')v
                write(222)real(v)
              enddo
            enddo
          enddo

          close(111)
          close(222)
        endif



c-----first kick
        do i=1,natm
          do j=1,3
            rv(j,i)=rv(j,i)+dt2*ra(j,i)
          enddo
        enddo

c-----update coordinate
        do i=1,natm
           do j=1,3
             r(j,i)=r(j,i)+dt*rv(j,i)
           enddo
         enddo
         do i=1,natm
           do j=1,3
             r(j,i)=r(j,i)-anint(r(j,i)/al(j)-0.5d0)*al(j)
           enddo
         enddo

c-------compute accel
        call CompAccel(ra,r,natm,sgm,eps,rcut,al,mass)

c-----second kick
        do i=1,natm
          do j=1,3
            rv(j,i)=rv(j,i)+dt2*ra(j,i)
          enddo
        enddo



c-----end of iteration
      enddo

c-----timer stops
      call cpu_time(te)
      print*,''
      print'(a,i5,a,i2,a,f6.2,a)','executing time',
     &     int((te-ts)/3600),'h',mod(int((te-ts)/60),60),'m',
     &     dmod(te-ts,60d0),'s'
      print*,''

c-----end of main
      end program ljmd
cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
      subroutine CompAccel(ra,r,n,sgm,eps,rcut,al,mass)

      implicit none

      integer n,i,j
      real*8 r(3,n),ra(3,n),al(3),sgm,eps,rcut,dx,dy,dz,r1,ff,dvlj,mass
     &     ,mi,ri

c-----initialize
      do i=1,n
        do j=1,3
          ra(j,i)=0d0
        enddo
      enddo

c-----inverse mass
      mi=1d0/mass

c-----compute accel
      do i=1,n-1
        do j=i+1,n
          dx=r(1,j)-r(1,i)-anint((r(1,j)-r(1,i))/al(1))*al(1)
          dy=r(2,j)-r(2,i)-anint((r(2,j)-r(2,i))/al(2))*al(2)
          dz=r(3,j)-r(3,i)-anint((r(3,j)-r(3,i))/al(3))*al(3)
          r1=sqrt(dx*dx+dy*dy+dz*dz)
          ri=1d0/r1
          ff=dvlj(r1,rcut,eps,sgm)
          ra(1,i)=ra(1,i)+ff*dx*ri*mi
          ra(2,i)=ra(2,i)+ff*dy*ri*mi
          ra(3,i)=ra(3,i)+ff*dz*ri*mi
          ra(1,j)=ra(1,j)-ff*dx*ri*mi
          ra(2,j)=ra(2,j)-ff*dy*ri*mi
          ra(3,j)=ra(3,j)-ff*dz*ri*mi
        enddo
      enddo


      end subroutine CompAccel
cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
      subroutine EvalProps(epot,r,natm,sgm,eps,rcut,al,ekin,rv,mass)
      implicit none
      integer natm,i,j
      real*8 r(3,natm),rv(3,natm),sgm,eps,rcut,ekin,epot,mass,vlj
      real*8 al(3),dx,dy,dz,r1

      ekin=0d0
      epot=0d0
      do i=1,natm-1
        do j=i+1,natm
          dx=r(1,j)-r(1,i)-anint((r(1,j)-r(1,i))/al(1))*al(1)
          dy=r(2,j)-r(2,i)-anint((r(2,j)-r(2,i))/al(2))*al(2)
          dz=r(3,j)-r(3,i)-anint((r(3,j)-r(3,i))/al(3))*al(3)
          r1=sqrt(dx*dx+dy*dy+dz*dz)
          epot=epot+vlj(r1,rcut,eps,sgm)
        enddo
        ekin=ekin+rv(1,i)*rv(1,i)+rv(2,i)*rv(2,i)+rv(3,i)*rv(3,i)
      enddo
      i=natm
      ekin=ekin+rv(1,i)*rv(1,i)+rv(2,i)*rv(2,i)+rv(3,i)*rv(3,i)
      ekin=ekin*0.5d0*mass


      return
      end subroutine EvalProps
cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
      function vlj(r,rcut,eps,sgm)
c
c     return LJ pot.
c
      implicit real*8 (a-h,o-z)
      if(r.le.rcut)then
        rrcut=rcut/sgm
        vcut=4d0*eps*((1d0/rrcut)**12-(1d0/rrcut)**6)
        dvcut=-24d0*eps/sgm*(2d0*(1d0/rrcut)**13-(1d0/rrcut)**7)
        rr=r/sgm
        vlj=4d0*eps*((1d0/rr)**12-(1d0/rr)**6)-vcut-(r-rcut)*dvcut
      else
        vlj=0d0
      endif

      return
      end
ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
      function dvlj(r,rcut,eps,sgm)
c
c     dphi/dr
c
      implicit real*8 (a-h,o-z)

      if(r.le.rcut)then
        rrcut=rcut/sgm
        dvcut=-24d0*eps/sgm*(2d0*(1d0/rrcut)**13-(1d0/rrcut)**7)
        rr=r/sgm
        dvlj=-24d0*eps/sgm*(2d0*(1d0/rr)**13-(1d0/rr)**7)-dvcut
      else
        dvlj=0d0
      endif

      return
      end
cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
      subroutine myrnd(rnd,dseed)

c     Random-number generator in [0:1]

      implicit real*8(a-h,o-z)
      real*8 rnd,dseed
      real*8 d2p31m,d2p31
      data d2p31m/2147483647d0/
      data d2p31 /2147483648d0/
      dseed=dmod(16807d0*dseed,d2p31m)
      rnd=dseed/d2p31
      return
      end subroutine myrnd
cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
