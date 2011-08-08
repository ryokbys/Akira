#!/bin/sh
#PBS -N test
#PBS -j oe
#PBS -l nodes=12:ppn=8
#cd /home/test/vasp/test

VASP=/home/vasp/vasp/vasp.4.6/vasp

cd $PBS_O_WORKDIR
mpirun -n 2 $VASP

