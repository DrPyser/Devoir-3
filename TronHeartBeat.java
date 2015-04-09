public class TronHeartBeat extends Thread{
    private Timer clockwork;
    private int timing;
    private TronServer server;
    public TronHeartBeat(int timing,TronServer server){
	this.timing = timing;
	this.server = server;
	this.clockwork = new Timer(timing,new ActionListener(){
		@Override
		public void actionPerformed(Event e){
		    //Tous les actions exécutés périodiquement(e.g. envoyer des informations aux joueurs, vérifier si un joueur est mort, etc
		    
		    for(PlayerConnection connection : this.server.getConnections()){
			if(!connection.isDead()){
			    String direction = connection.in.readLine();
			    connection.getPlayer().setDirection(direction);
			    connection.getPlayer().move();
			}
			//Envoyer à chacun des clients les directions des autres joueurs, dans un ordre prédéfini
			String directions = "";//Directions des autres joueurs
			for(PlayerConnection otherConnection: this.server.getConnections()){
			    if(!otherConnection.username.equals(connection.username)){
				directions += (otherConnection.isDead())?"X":(otherConnection.getPlayer.getCurrentDirection());
			    }
			}
			connection.out.println("s"+directions);
		    }
		}
	    }
	    );
	this.clockwork.setInitialDelay(10000);//Attend 10 secondes avant le début de la partie
    }
    
    public void run(){
	this.clockwork.start();
    }
}
