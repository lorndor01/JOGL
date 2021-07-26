package mygraphicslib;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import lib342.opengl.Constants.PrimitiveType;

public class PolylineCollection {
	private ArrayList<Polyline> polylines;
	public PolylineCollection(){
		polylines = new ArrayList<Polyline>();
	}
	
	
	public PolylineCollection(Polyline... polyline) {
		this();
		for(Polyline line : polyline) {
			polylines.add(line);
		}
	}
	
	public void draw(GL2 gl, PrimitiveType type, float[] color) {
		for(Polyline line : polylines) {
			line.draw(gl, type, color);
		}
	}
	public void add(Polyline line) {
		polylines.add(line);
	}
	public void remove() {
		polylines.remove(polylines.size()-1);
	}
	public int getSize() {
		return polylines.size();
	}
	
	public Polyline get(int index) {
		return polylines.get(index);
	}
	
	public void replace(int index, Polyline line) {
		polylines.remove(index);
		polylines.add(index, line);
	}
}
