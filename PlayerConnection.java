import java.util.ArrayList.*;
import java.awt.Color.*;
public class PlayerConnection extends Thread{

    private TronPlayer player;
    private String nextStep;
    private Socket clientsocket = null;
    private InputStream in;
    private OutputStream out;
    private TronServer host;
    
    public PlayerConnection(Socket clientsocket,TronServer host){
	this.host = host;
	this.clientsocket = clientsocket;
	this.in = new BufferedReader(this.clientsocket.getInputStream());
	this.out = new BufferedWriter(this.clientsocket.getOutputStream());
    }

    public TronPlayer getPlayer(){
	return this.player;
    }

    public boolean isDead(){
	boolean collision = this.host.getEnceinte().contains(this.player.getTrace().getHead());
	if(!collision){
	    for(PlayerConnection connection: this.host.getConnections()){
		collision = collision || connection.getPlayer().hasCollided(this.player);
		if(collision){ 
		    break; 
		}
	    }
	}
	return collision;
    }
    
    public boolean equals(PlayerConnection other){
	//L'égalité entre deux connection est défini par l'égalité entre deux profil de joueur
	return this.player.equals(other.getPlayer());
    }

    private void run(){
	String username;
	String clientLine;
	Color randomColor;
	//Implémentation du protocole de communication ici
	if(!partieEnCours){
	    clientLine = this.in.readLine();
	    if(clientLine.equals("Bonjours, je suis un client.")){
		this.out.println("Enchanté, je suis un serveur. Quel est votre nom?");
		username = this.in.readLine();//Le joueur donne son nom/username
		this.out.println(this.gridwidth);
		this.out.println(this.gridheight);
		this.out.flush();
		//TODO: Assigner au joueur une couleur aléatoire
		this.player = new TronPlayer(username,clientSocket.getInetAddress().getHostName(),randomColor);
		
		//On affiche les informations sur chacun des joueurs connectés
		for(PlayerConnection otherConnection: this.host.getConnections()){
		    this.out.println(">+"+ otherConnection.player.getUsername());
		    this.out.println(">"+ otherConnection.player.getMachineID());
		    this.out.println(">"+ otherConnection.player.getColor().getRGB());
		    this.out.println(">"+ otherConnection.player.getStartpoint().getX());
		    this.out.println(">"+ otherConnection.player.getStartpoint().getY());
		    this.out.flush();
		}
		
	    } else {
		this.out.println("Erreur:Bris du protocole.");
		this.out.flush();
	    }
	} else {
	    this.out.println("Veuillez attendre la prochaine partie");
	    this.out.flush();
	}
    }
    
}
