import java.awt.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class PlayerConnection extends Thread{
    private TronPlayer player;//Profil du joueur -- trace,username,couleur,point de départ
    private boolean connected;//le joueur est-il connecté?
    private boolean initialized = false;//Le joeur est-il initialisé(s'est fait assigné un profil joueur)?
    private Socket clientsocket;
    private BufferedReader in;
    private PrintWriter out;
    private TronServer host;
    
    public PlayerConnection(Socket clientsocket,TronServer host){
    	this.host = host;
    	this.clientsocket = clientsocket;
    	try {
			this.in = new BufferedReader(new InputStreamReader(this.clientsocket.getInputStream()));
			this.out = new PrintWriter(this.clientsocket.getOutputStream());
			this.connected = true;
			this.initialize();//On initialise le joueur
		} catch (IOException e) {
			System.err.println("Erreur: Impossible de créer le inputStream ou le outputStream: "+ e);
			this.disconnect();//Si le input ou le outputStream ne peuvent pas s'ouvrir correctement, le joueur est considéré mort
		}
    	
    	
    }
    public void initialize(){
    	//On initialise le joueur
    	String username;
		Color randomColor = new Color(randomRange(0,255),randomRange(0,255),randomRange(0,255));//Génère une couleur aléatoirement
		Point startingPoint = new Point(randomRange(0,this.host.getWidth()),randomRange(0,this.host.getHeight()));//Génère un point de départ aléatoire
		//Si une partie est en cours, on fait attendre le thread jusqu'à ce que l'horloge le réveil(notify())
		if(this.host.ispartieEnCours()){
			this.out.println("Veuillez attendre la prochaine partie.");
			this.out.flush();
			synchronized(this){
				while(this.host.ispartieEnCours()){
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		} 
		//On obtient le nom d'utilisateur, et on envoie au client la taille de l'arene
	    try {
	    	username = this.in.readLine();//Le joueur donne son username
	    	
			this.out.println(this.host.getWidth());
			this.out.println(this.host.getHeight());
			
			this.player = new TronPlayer(username,clientsocket.getInetAddress().getHostName(),randomColor,startingPoint);
			
			//On s'assure que le point de départ et la couleur est unique au joueur
			for(int i = 0;i<this.host.getConnections().size();i++){
				PlayerConnection otherConnection = this.host.getConnections().get(i);
				if(!otherConnection.equals(this)){
					if(otherConnection.getPlayer().getColor().equals(this.player.getColor())){
						this.player.setColor(new Color(randomRange(0,255),randomRange(0,255),randomRange(0,255)));//Génère une couleur aléatoirement
						i = 0;
					}
					if(otherConnection.getPlayer().getStartPoint().equals(this.player.getStartPoint())){
						this.player.setStartPoint(new Point(randomRange(0,this.host.getWidth()),randomRange(0,this.host.getHeight())));//nouveau point aléatoire
						i = 0;
					}
				}
			}	
			
		} catch (IOException e) {
			System.err.println("Impossible de lire la ligne: " + e);
			System.err.flush();
		} finally {
			this.out.flush();
		}	
		//On affiche les informations sur chacun des joueurs connectés
		for(PlayerConnection connection: this.host.getConnections()){
			System.out.println(connection.getPlayer().getUsername());
		    this.out.println("+"+ connection.getPlayer().getUsername());
		    this.out.println(connection.getPlayer().getMachineID());
		    this.out.println(connection.getPlayer().getColor().getRGB());
		    this.out.println(connection.getPlayer().getStartPoint().getX());
		    this.out.println(connection.getPlayer().getStartPoint().getY());
		    this.out.flush();
		    if (!this.equals(connection)){
			    connection.send("+"+this.player.getUsername());
			    connection.send(this.player.getMachineID());
			    connection.send(""+this.player.getColor().getRGB());
			    connection.send(""+this.player.getStartPoint().getX());
			    connection.send(""+this.player.getStartPoint().getY());
		    }
		}
		    
		this.initialized = true;//Le joueur a été initialisé
    }
    
    
    //Cette méthode lit le inputStream et met à jour la direction du joueur
    public void run(){
		synchronized(this){
			//Tant que la partie n'est pas commencé, on attend une notification(de l'horloge du serveur)
			while(!this.host.ispartieEnCours()){
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}//On pause l'exécution du thread jusqu'au début de la partie
		}
		while(this.host.ispartieEnCours()){
			String command;
			try {
				command = this.in.readLine();
				if(command.equals("quit")){
					this.disconnect();
					return;
				} else{
					this.player.setDirection(command);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
				
		}
		
    }
    
    public void reset(){
    	//On réinitialise le point de départ du joueur à une nouvelle position aléatoire unique
    	this.player.setStartPoint(new Point(randomRange(0,this.host.getWidth()),randomRange(0,this.host.getHeight())));
    	for(int i = 0;i<this.host.getConnections().size();i++){
			PlayerConnection otherConnection = this.host.getConnections().get(i);
			if(!otherConnection.equals(this)){
				if(otherConnection.getPlayer().getStartPoint().equals(this.player.getStartPoint())){
					this.player.setStartPoint(new Point(randomRange(0,this.host.getWidth()),randomRange(0,this.host.getHeight())));
					i = 0;
				}
			}
		}
    	//On réanime le joueur, s'il est mort
    	if(this.isDead()){
    		this.getPlayer().revive();
    	}
    }
    
    public void disconnect(){
    	this.connected = false;
    	try {
    		this.in.close();
    		this.out.close();
			this.clientsocket.close();
		} catch (IOException e) {
			System.err.println("Erreur: impossible de fermer la connection: "+ e);
		}
    	this.player.kill();
    }
    
    public boolean isConnected(){
    	return this.connected;
    }
    
    public boolean isInitialized(){
    	return this.initialized;
    }
    
    public String getCommand(){
    	//lit une ligne inputStream
    	try{
    		return this.in.readLine();
    	} catch (IOException e){
    		System.err.println("Erreur: impossible de lire le inputStream de cette connection: "+ e);
    		return "";
    	}
    	
    }
    
    //Envoie une ligne contenant un message au client
    public void send(String message){
    	this.out.println(message);
    	this.out.flush();
    	
    }

    public TronPlayer getPlayer(){
    	return this.player;
    }

    public boolean isDead(){
    	//On vérifi si le joueur est mort
    	if (!this.player.getDead()){
    		//On vérifie si le joueur est entré en collision avec l'enceinte
    		boolean collision = this.host.getEnceinte().contains(this.player.getTrace().getHead());
    		//On vérifie si le joueur est entré en collision avec la trace d'un joueur
	    	if(!collision){
	    		for(PlayerConnection connection: this.host.getConnections()){
	    			collision = collision || connection.getPlayer().hasCollided(this.player);
	    			if(collision){ 
	    				break; 
	    			}
	    		}
	    	}
	    	if (collision){
	    		this.player.kill();
	    	}
    	}
    	return this.player.getDead();
    }
    
    public boolean equals(PlayerConnection other){
    	//L'égalité entre deux connection est défini par l'égalité entre deux profil de joueur
    	return this.player.equals(other.getPlayer());
    }

    
    
    public static int randomRange(int from, int to){
    	//Génère un nombre aléatoire entre "from" et "to"
    	return ((int) (from + (Math.floor(Math.random()*to))));
    }
    
}
