import java.util.*;
import java.net.*;
import java.io.*;

public class TronServer {
    private int port;
    private int clocktick;
    private int gridwidth;
    private int gridheight;
    private String serverhostname;
    private ServerSocket serversocket = null;
    private List<PlayerConnection> connections;
    private Trace enceinte;
    private TronHeartBeat heart;
    private boolean partieEnCours = false;
    
    //Constructeur de la classe
    public TronServer(int gridwidth,int gridheight,int port,int clocktick) throws IOException {
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
		this.port = port;
		this.clocktick = clocktick;
		this.serverhostname = InetAddress.getLocalHost().getHostName();
		//Tentative de création du ServerSocket
		try {
		    this.serversocket = new ServerSocket(this.port);
		    System.out.println("Serveur " + this.serverhostname + " en ligne sur le port " + this.port);
		} catch(IOException e){
		    System.err.println("Erreur: impossible de créer le ServerSocket: " + e);
		    System.exit(1);
		}
		this.connections = new ArrayList<PlayerConnection>();//On initialise la liste des connections à un arraylist vide
	
		this.heart = new TronHeartBeat(this.clocktick,this);
	
		//On construit l'enceinte
		this.enceinte = new Trace(new Point(0,0));
		
		for(int i = 1;i < gridwidth;i++){
		    enceinte.add(new Point(i,0));
		}
		for(int j = 1;j < gridheight;j++){
		    enceinte.add(new Point(gridwidth-1,j));
		}   
		for(int i = gridwidth-2;i > 0;i--){
		    enceinte.add(new Point(gridheight-1,i));
		    }
		for(int j = gridheight-1;j > 0;j--){
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
    
    public boolean ispartieEnCours(){
    	return this.partieEnCours;
    }
    public void startPartie(){
    	this.partieEnCours = true;
    }
    public void endPartie(){
    	this.partieEnCours = false;
    }
    
    public int getWidth(){
    	return this.gridwidth;
    }
    public int getHeight(){
    	return this.gridheight;
    }
    public void startClock(){
	this.heart.start();
    }
    //Point d'entré du programme, s'occupe d'attendre et d'ajouter les connections au serveur
    public static void main(String[] args){
		if(args.length<4){
		    System.out.println("Nombre d'arguments insuffisant. \n"
				       +"Utilisation: TronServer <Numéro de port>" 
				       +"<Temps entre tick d'horloge, en millisecondes> <Largeur grille de jeu> <Hauteur grille de jeu>");
		    System.exit(1);
		}
		
		int port = Integer.parseInt(args[0]);
		int clocktick = Integer.parseInt(args[1]);
		int gridwidth = Integer.parseInt(args[2]);
		int gridheight = Integer.parseInt(args[3]);
		
		TronServer server;
		try {
			server = new TronServer(gridwidth,gridheight,port,clocktick);
			//Good old while(true) pour écouter et attendre une connection cliente.
			server.startClock();
			while(true){
			    //Tentative de création du ClientSocket ET de la connection au client
			    System.out.println("En attente d'une connection cliente sur le port " + server.port + "...");
			    System.out.flush();
			    try {
					Socket clientsocket = server.serversocket.accept();//On accept la prochaine connection cliente
					System.out.println("Connection établie!");
					System.out.flush();
					PlayerConnection connection = new PlayerConnection(clientsocket,server);//On crée un nouveau thread pour gérer la connection
					server.addConnection(connection);//On ajoute la nouvelle connection à la liste des connections gérées par le serveur
					connection.start();//On commence l'exécution du thread
					System.out.println("Partie commencée?: "+ server.partieEnCours);
					
			    } catch (IOException e) {
					System.err.println("Erreur: connection refusée: " + e);
					System.exit(1);
			    } /*catch (ConnectException e) {
					System.err.println("Erreur: Échec de la tentative de connection: " + e);
					System.exit(1);
			    }*/
			}
		} catch (IOException e1) {
			System.err.println("Erreur: impossible de créer le serveur: " + e1);
		}
	
    }
}
