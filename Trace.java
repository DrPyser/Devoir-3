import java.util.List;
import java.util.ArrayList;
public class Trace{
    private List<Point> trace = new ArrayList<Point>();
    
    public Trace(Point startingPoint){
    	this.trace.add(startingPoint);
    }
    public void add(Point point){
    	this.trace.add(point);
    }
    public Point get(int index){
    	return this.trace.get(index);
    }
    public Point getHead(){
		int length = this.trace.size();
		return this.trace.get(length-1);
    }
    public boolean contains(Point point){
    	return this.trace.contains(point);
    }
}
