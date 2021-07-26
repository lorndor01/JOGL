package mygraphicslib;

import java.io.Closeable;
import java.io.IOException;

import com.jogamp.opengl.GL2;

import lib342.opengl.Constants;

public class Drawer implements Closeable {

	private GL2 gl;
	
	public Drawer (Constants.PrimitiveType type, GL2 gl) {
		this.gl = gl;
		gl.glBegin(type.getValue());
	}
	
	public void vertex(double x, double y) {
		gl.glVertex2d(x, y);
	}
	
	public void close() {
		gl.glEnd();
	}
}
