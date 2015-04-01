public class TronServer{
    private int port;
    private SockerServer server;

    public TronServer(int port){
	this.port = port;
	this.server = new SocketServer(port);
	
	    
    }

    
}
