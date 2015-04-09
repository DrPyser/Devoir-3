import java.awt.Color.*;

public class TronPlayer {
    private String username;
    private String machineID;
    private Color playerColor;
    private Trace currentTrace;
    private String currentDirection;
    private Point startpoint;

    public TronPlayer(String username,String machineID,Color playerColor,Point startpoint){
	this.username = username;
	this.playerColor = playerColor;
	this.machineID = machineID;
	this.startpoint = startpoint;
	this.currentTrace.add(startpoint);
    }
    public Point getStartpoint(){
	return this.startpoint;
    }
    public void setDirection(String direction){
	this.currentDirection = direction;
    }
    public boolean hasCollided(TronPlayer player){
	return player.getTrace().contains(this.getTrace.getHead());
    }
    public Trace getTrace(){
	return this.currentTrace;
    }
    public String getCurrentDirection(){
	return this.currentDirection;
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
    //Effectue un déplacement en mettant à jour la trace avec un nouveau point
    public void move(){
	Point lastPosition = this.currentTrace.getHead();
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
	return this.machineID.equals(other.getUsername());
    }
}
