
set title 'Length Distribution'
set xl 'Length'
set yl ''
plot "< awk -f distribution-length.awk aveLength.d" w boxes fs pattern 1
pause -1


set multiplot
set size 1,0.5
set origin 0,0
set title 'Si Angle Distribution'
set xlabel 'Angle [deg]'
set ylabel ''
set xtics 10
set ytics 0.1
plot "< awk -f 001distribution-angle.awk aveAngle.d" us 1:2 w boxes fs pattern 4 title 'Si'

set size 1,0.5
set origin 0,0.5
set xtics 10
set ytics 0.1
set title 'O Angle Distribution'
set xlabel ''
set ylabel ''
plot "< awk -f 001distribution-angle.awk aveAngle.d" us 1:3 w boxes fs pattern 5 lt 2 title 'O'

set nomultiplot
pause -1
exit

set title 'Angle Distribution'
plot "< awk -f 001distribution-angle.awk aveAngle.d" us 1:2 w boxes fs pattern 4 title 'Si' ,\
     "< awk -f 001distribution-angle.awk aveAngle.d" us 1:3 w boxes fs pattern 5 title 'O'
pause -1
