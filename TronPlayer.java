import java.awt.Color;

public class TronPlayer {
    private String username;//nom d'utilisateur du joueur
    private String machineID;//nom de la machine du client
    private Color playerColor;//couleur de la trace du joueur
    private Trace currentTrace;//trace actuelle
    private String currentDirection;//direction actuelle
    private Point startpoint;//position de départ
    private boolean dead = false;//le joueur est-il mort?

    public TronPlayer(String username,String machineID,Color playerColor,Point startpoint){
		this.username = username;
		this.playerColor = playerColor;
		this.machineID = machineID;
		this.startpoint = startpoint;
		this.currentTrace = new Trace(startpoint);
    }
    public Point getStartPoint(){
    	return this.startpoint;
    }
    public void setDirection(String direction){
    	this.currentDirection = direction;
    }
    public boolean hasCollided(TronPlayer player){
    	return player.getTrace().contains(this.currentTrace.getHead());
    }
    public Trace getTrace(){
    	return this.currentTrace;
    }
    public String getCurrentDirection(){
    	return this.currentDirection;
    }
    public boolean getDead(){
    	return this.dead;
    }
    public void kill(){
    	this.dead = true;
    }
    public void revive(){
    	this.dead = false;
    }
    
    public String getMachineID(){
    	return this.machineID;
    }

    public String getUsername(){
    	return this.username;
    }
    public Color getColor(){
    	return this.playerColor;
    }
    public void setColor(Color newColor){
    	this.playerColor = newColor;
    }
    public void setStartPoint(Point newStart){
    	this.startpoint = newStart;
    }
    //Effectue un déplacement en mettant à jour la trace avec un nouveau point
    public void move(){
    	Point lastPosition = this.currentTrace.getHead();
    	//la direction "N" décrémente la position verticale
    	//la direction "S" incrémente la position verticale
    	//la direction "E" incrémente la position horizontale
    	//la direction "W" décrémente la position horizontale
		if(this.currentDirection.equals("N")){
		    this.currentTrace.add(new Point(lastPosition.getX(),lastPosition.getY()-1));
		} else if (this.currentDirection.equals("S")){
		    this.currentTrace.add(new Point(lastPosition.getX(),lastPosition.getY()+1));
		} else if (this.currentDirection.equals("E")){
		    this.currentTrace.add(new Point(lastPosition.getX()+1,lastPosition.getY()));
		} else if (this.currentDirection.equals("W")){
		    this.currentTrace.add(new Point(lastPosition.getX()-1,lastPosition.getY()));
		} 
    }

    public boolean equals(TronPlayer other){
		//l'égalité de deux joueur est défini par l'égalité entre leur username
		return this.username.equals(other.getUsername());
    }
}
