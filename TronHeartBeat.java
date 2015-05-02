public class TronHeartBeat extends Thread{
    private int timing;//intervalle entre chaque battement, en millisecondes
    private TronServer server;
    
    public TronHeartBeat(int timing,TronServer server){
    	this.timing = timing;
    	this.server = server;
    }
    
    public void run(){
    	while(true){
	    	//On attend qu'au moins deux joeurs soient connectés
	    	while(this.server.getConnections().size() < 2){
		    	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.err.println("Sleep interrompu: "+ e);
				}
		    }
		    
		    try {
		    	Thread.sleep(10000);
		    } catch(InterruptedException e){
		    	System.err.println("Sleep interrompu: "+ e);
		    }
		    
		    //On "réveille" les connections pour le début de la partie
		    for(PlayerConnection connection: this.server.getConnections()){
		    	if(connection.isInitialized()){
		    		synchronized(connection){
		    			connection.notify();
		    		}
		    	}
		    }
		    
		    //On commence la partie
		    this.server.startPartie();
		    System.out.println("La partie commence...");
		    
		    int survivants;
		    
		    //Boucle principale
			do{
		    	//On attend un click d'horloge
		    	try {
					Thread.sleep(this.timing);
				} catch (InterruptedException e) {
					System.err.println("Erreur: battement interrompu: " + e);
				}
	
		    	survivants = this.server.getConnections().size();//Nombre de survivants restant
	
		    	//Déplacement des joueurs et envoie des directions des adversaires à chacun des joueurs
		    	for(PlayerConnection connection : server.getConnections()){
					if(!connection.isDead()){//Si le joueur est vivant
						connection.getPlayer().move();
					}
					//Envoie à chacun des clients les directions des autres joueurs, dans l'ordre de la liste des connections du serveur
					String directions = "";//Directions des autres joueurs
					for(PlayerConnection otherConnection: server.getConnections()){
						if (otherConnection.isDead()){
							survivants--;
						}
						//On accumule la direction des adversaires dans un string
					    if(!otherConnection.equals(connection)){
					    	//Si le joueur est mort, on envoie un 'X' au lieu de sa direction
					    	directions += (otherConnection.isDead()?"X":(otherConnection.getPlayer().getCurrentDirection()));
					    }
					}
					//Lorsqu'il reste un joueur ou moins, on sort de la boucle -- la partie est terminée
					if (survivants <= 1){
						break;
					} else {
						//On envoie les directions des adversaires au joueur
						connection.send("s"+directions);
					}
					
		    	}
		    } while(survivants > 1);
			//Quand il ne reste qu'un joueur en vie
			this.server.endPartie();
			
			//On attend 10 secondes avant d'envoyer le signal de réinitialisation
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				System.err.println("Sleep interrompu: "+ e1);
			}
			
			for(PlayerConnection connection : server.getConnections()){
				connection.send("R");
			}
	    	System.out.println("Fin de la partie.");
	    	
			this.restart();
			//appel la methode "restart" qui s'occupe de réinitialiser les paramètres des joeurs et de recommencer la partie avec les nouveaux joueurs
    	}
    }	
    
    public void restart(){
    	//On réinitialize la position de départ de chacun des joueurs.
    	//On supprime les joueurs déconnectés.
    	//On initialize les nouveaux joueurs.
    	//On envoie à chacun des joueur les informations sur chacun des joueurs.

    	for(int i = 0;i<this.server.getConnections().size();i++){
    		PlayerConnection connection = this.server.getConnections().get(i);
    		if(!connection.isConnected()){
    			this.server.getConnections().remove(i);
    		} else if (connection.isInitialized()){
    			connection.reset();
    			for(PlayerConnection otherConnection: this.server.getConnections()){
    				if(otherConnection.isInitialized()){
	    			    connection.send("+"+ otherConnection.getPlayer().getUsername());
	    			    connection.send(otherConnection.getPlayer().getMachineID());
	    			    connection.send(""+ otherConnection.getPlayer().getColor().getRGB());
	    			    connection.send(""+ otherConnection.getPlayer().getStartPoint().getX());
	    			    connection.send(""+ otherConnection.getPlayer().getStartPoint().getY());
	    			    otherConnection.send("+"+ connection.getPlayer().getUsername());
	    			    otherConnection.send(connection.getPlayer().getMachineID());
	    			    otherConnection.send(""+ connection.getPlayer().getColor().getRGB());
	    			    otherConnection.send(""+ connection.getPlayer().getStartPoint().getX());
	    			    otherConnection.send(""+ connection.getPlayer().getStartPoint().getY());
    				}
    			}
    		} else {
    			synchronized(connection){
    				connection.notify();
    			}
    		}
    	}
    }
}
