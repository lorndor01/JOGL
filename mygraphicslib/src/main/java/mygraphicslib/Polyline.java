package mygraphicslib;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.joml.Vector2d;

import com.jogamp.opengl.GL2;

import lib342.opengl.Constants.PrimitiveType;

public class Polyline implements Iterable<Vector2d>{
	private List<Vector2d> points;
	
	public Polyline() {
		// TODO Auto-generated constructor stub
		points = new LinkedList<Vector2d>();
		
	}
	
	public Polyline(Vector2d point) {
		this();
		points.add(point);
	}
	public Polyline(Vector2d... newPoints) {
		this();
		for(Vector2d point : newPoints) {
			this.points.add(point);
		}
	}
	public Polyline(double... values) {
		this();
		addPoints(values);
	}
	public void addPoint(Vector2d pt) {
		points.add(pt);
	}
	
	public void addPoint(double x, double y) {
		Vector2d point = new Vector2d(x,y);
		points.add(point);
	}
	
	public void addPoints(double... values) throws IllegalArgumentException{
		if((values.length%2)!=0) {
			throw new IllegalArgumentException();
		}
		else {
			for(int i =0; i<values.length; i+=2) {
				Vector2d point = new Vector2d(values[i], values[i+1]);
				addPoint(point);
			}
		}
	}

	public Iterator<Vector2d> iterator() {
		// TODO Auto-generated method stub
		return points.iterator();
	}
	
	public void draw(GL2 gl) {
		for(Vector2d point : points) {
			gl.glVertex2d(point.x,point.y);
		}
	}
	
	public void draw(GL2 gl, PrimitiveType type, float[] color) {
		gl.glColor3fv(color,0);
		gl.glBegin(type.getValue());
		{
			draw(gl);
		}
		gl.glEnd();		
	}
	
	public int getSize() {
		return points.size();
	}
	public void remove() {
		points.remove(points.size()-1);
	}
}
