gfortran-mp-4.4 example.f
./a.out
tar czvf ascii.tgz akr0*
tar cjvf ascii.tbz2 akr0*
tar cjvf binary.tbz2 bakr0*
tar czvf binary.tgz bakr0*
mv bakr0* binary/
mv akr0* ascii/
rm a.out
