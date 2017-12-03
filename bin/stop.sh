# Stop Vert.x Instance by kill pid
kill -s 9 `ps -ef | grep boruto | grep -v grep | awk '{print $2}'`

# Stop again
sleep 6
kill -s 9 `ps -ef | grep boruto | grep -v grep | awk '{print $2}'`
