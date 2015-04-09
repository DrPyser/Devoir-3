import java.util.Lists.*;
import java.net.Socket.*;

public class TronServer {
    private int port;
    private int clocktick;
    private int gridwidth;
    private int gridheight;
    private String serverhostname;
    private SocketServer serversocket = null;
    private List<PlayerConnection> connections;
    private Trace enceinte;
    private TronHeartBeat heart;
    
    //Constructeur de la classe
    public TronServer(int gridWidth,int gridHeight,int port,int clocktick){
	this.gridwidth = gridwidth;
	this.gridheight = gridheight;
	this.port = port;
	this.clocktick = clocktick;
	this.serverhostname = InetAddress.getLocalHost().getHostName();
	//Tentative de création du ServerSocket
	try {
	    this.serversocket = new SocketServer(this.port);
	    System.out.println("Serveur" + this.serverhostname + "en ligne sur le port " + this.port);
	} catch(IOException e){
	    System.err.println("Erreur: impossible de créer le ServerSocket: " + e);
	    System.exit(1);
	}
	this.connections = new ArrayList<PlayerConnection>();//On initialise la liste des connections à un arraylist vide

	this.heart = new TronHeartBeat(this);

	//On construit l'enceinte
	this.enceinte = new Trace(new Point(0,0));
	
	for(int i = 1;i < gridWidth;i++){
	    enceinte.add(new Point(i,0));
	}
	for(int j = 1;j < gridHeight;j++){
	    enceinte.add(new Point(gridWidth-1,j));
	}   
	for(int i = gridWidth-2;i > 0;i--){
	    enceinte.add(new Point(gridHeight-1,i));
	    }
	for(int j = gridHeight-1;j > 0;j--){
	    enceinte.add(new Point(0,j));
	}   
	
    }
    
    public Trace getEnceinte(){
	return this.enceinte;
    }
    
    public void addConnection(PlayerConnection connection){
	this.connections.add(connection);
    }

    public List<PlayerConnection> getConnections(){
	return this.connections;
    }
    public void startHeartBeat(){
	this.heart.start();
    }
    //Point d'entré du programme, s'occupe d'attendre et d'ajouter les connections au serveur
    public static void main(String[] args){
	if(args.length<4){
	    System.out.println("Nombre d'arguments insuffisant. \n"
			       +"Utilisation: TronServer <Numéro de port>" 
			       +"<Temps entre tick d'horloge> <Largeur grille de jeu> <Hauteur grille de jeu>");
	    System.exit(1);
	}
	
	port = Integer.parseInt(args[0]);
	clocktick = Integer.parseInt(args[1]);
	gridwidth = Integer.parseInt(args[2]);
	gridheight = Integer.parseInt(args[3]);
	
	TronServer server = new TronServer(gridwidth,gridheight,port,clocktick);

	//Good old while(true) pour écouter et attendre une connection cliente.
	while(true){
	    if(server.connections.size() >= 2){
		server.startHearBeat();
	    }
	    //Tentative de création du ClientSocket ET de la connection au client
	    try {
		System.out.println("En attente d'une connection cliente sur le port " + this.server.getLocalPort() + "...");
		System.out.flush();
		Socket clientSocket = serverSocket.accept();//On accept la prochaine connection cliente
		System.out.println("Connection établie!");
		System.out.flush();
		PlayerConnection connection = new PlayerConnection(clientSocket,server);//On crée un nouveau thread pour gérer la connection
		connection.start();//On commence l'exécution du thread
		server.addConnection(connection);//On ajoute la nouvelle connection à la liste des connections gérées par le serveur
	    } catch (IOException e) {
		System.err.println("Erreur: connection refusée: " + e);
		System.exit(1);
	    } catch (ConnectionException e) {
		System.err.println("Erreur: Échec de la tentative de connection: " + e);
		System.exit(1);
	    }
	}
    }
}
