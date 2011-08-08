      subroutine read_kvs(nfile,namax,natm,h,is,ra,va,nopmax,nop,op)
      implicit none
      integer,intent(in):: nfile,namax,nopmax,nop
      integer,intent(out):: natm,is(namax)
      real(8),intent(out):: h(3,3,0:1),ra(3,namax),va(3,namax)
     &     ,op(nopmax,namax)

      integer:: i,ia,ib,l

c-----natm: num. of atoms
      read(nfile,*) natm
      read(nfile,*) (((h(ia,ib,l),ia=1,3),ib=1,3),l=0,1)
      do i=1,natm
c-------species, positions, velocities, index of motion
        read(nfile,*) is(i),ra(1:3,i),va(1:3,i),(op(l,i),l=1,nop)
      enddo

      return
      end subroutine read_kvs
c=======================================================================
      subroutine write_kvs(nfile,natm,h,is,ra,va,nopmax,nop,op)
      implicit none
      integer,intent(in):: nfile,natm,is(natm),nopmax,nop
      real(8),intent(in):: h(3,3,0:1),ra(3,natm),va(3,natm)
     &     ,op(nopmax,natm)

      integer:: i,ia,ib,l

      write(nfile,'(i10)') natm
      write(nfile,'(3es12.4)') (((h(ia,ib,l),ia=1,3),ib=1,3),l=0,1)
      do i=1,natm
        write(nfile,'(i4,20es11.3)') is(i),ra(1:3,i),va(1:3,i)
     &       ,(op(l,i),l=1,nop)
      enddo

      return
      end subroutine write_kvs
c=======================================================================
