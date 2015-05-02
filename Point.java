public class Point{
    private int x;
    private int y;
    
    public Point(int x,int y){
    	this.x = x;
    	this.y = y;
    }
    public int getX(){
    	return this.x;
    }
    public int getY(){
    	return this.y;
    }
    
    public double distance(Point point){
    	return (Math.sqrt((this.getX() - point.getX())^2 + (this.getY() - point.getY())^2));
    }
    public boolean equals(Point other){
    	return (this.x == other.getX() && this.y == other.getY());
    }
}
