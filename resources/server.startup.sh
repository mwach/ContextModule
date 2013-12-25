LD_LIBRARY_PATH=/usr/local/lib
export LD_LIBRARY_PATH=/usr/local/lib

java -cp libs/arena.jar:libs/ServicePlatformInterface.jar:/usr/local/share/java/zmq.jar  com.safran.arena.impl.Server
