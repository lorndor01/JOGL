package mygraphicslib;

import java.awt.Rectangle;

import org.joml.Vector2d;

import java.util.Stack;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import lib342.ViewportLocation;
import lib342.opengl.Constants;
import lib342.opengl.Constants.MatrixMode;
import lib342.opengl.Constants.PrimitiveType;

public class GLUtilities {
	/**
	 * Clears the color buffer and changes the canvas to the color values provided in the float array.
	 * @param gl
	 * @param rgb
	 */
	public static void clearColorBuffer(GL gl, float[] rgb) {
		gl.glClearColor(rgb[0], rgb[1], rgb[2], 1.0f);
		gl.glClear(Constants.GL_COLOR_BUFFER_BIT);
	}
	
	/**
	 * Sets up the world window based on the the sizes you specify.
	 * @param gl
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 */
	public static void setWorldWindow(GL2 gl, double left, double right, double bottom, double top) {
		GLU glu = GLU.createGLU(gl);
		
		gl.glMatrixMode(MatrixMode.PROJECTION.getValue());
		gl.glLoadIdentity();
		glu.gluOrtho2D(left, right, bottom, top);
		
	}
	/**
	 * Returns the maximum viewport allowed based on properties of the canvas.
	 * @param canvasWidth
	 * @param canvasHeight
	 * @param worldAspectRation
	 * @param location
	 * @return
	 */
	public static Rectangle getMaximumViewport(int canvasWidth, int canvasHeight,
			double worldAspectRation, ViewportLocation location) {		
		
		Rectangle viewport = McFallGLUtilities.getMaximumViewport(canvasWidth, canvasHeight, 1, ViewportLocation.Center);
		return viewport;
	}
	/**
	 * This method can be called instead of writing gl.glVertex2d(point.x, point.y)
	 * @param gl
	 * @param pt
	 */
	public static void glVertex2dv(GL2 gl, Vector2d pt) {
		gl.glVertex2d(pt.x, pt.y);
	}
	
	public static void drawArc(
			GL2 gl,
			double cx, double cy, double r,
			double startAngleInDegrees, double sweepInDegrees)
	{
		double startAngleInRadians = startAngleInDegrees*(Math.PI/180);
		double sweepInRadians = sweepInDegrees*(Math.PI/180);
		int n = 100;
		double  increment = (sweepInRadians/n);
		double currentAngle = startAngleInRadians;
		
		for(int i=1; i<=n; i++) {
			double currentPointx = (r*Math.cos(currentAngle))+cx;
			double currentPointy = (r*Math.sin(currentAngle))+cy;
			
			 gl.glVertex2d(currentPointx, currentPointy);
			
			currentAngle = startAngleInRadians+(i*increment);
		}
		double x = (r*Math.cos(startAngleInRadians+sweepInRadians))+cx;
		double y = (r*Math.sin(startAngleInRadians+sweepInRadians))+cy;
		gl.glVertex2d(x, y);
	}
	
	public static void drawElevatedArc(
			GL2 gl,
			double cx, double cy, double cz, double r,
			double startAngleInDegrees, double sweepInDegrees)
	{
		double startAngleInRadians = startAngleInDegrees*(Math.PI/180);
		double sweepInRadians = sweepInDegrees*(Math.PI/180);
		int n = 100;
		double  increment = (sweepInRadians/n);
		double currentAngle = startAngleInRadians;
		
		for(int i=1; i<=n; i++) {
			double currentPointx = (r*Math.cos(currentAngle))+cx;
			double currentPointy = (r*Math.sin(currentAngle))+cy;
			
			 gl.glVertex3d(currentPointx, currentPointy, cz);
			
			currentAngle = startAngleInRadians+(i*increment);
		}
		double x = (r*Math.cos(startAngleInRadians+sweepInRadians))+cx;
		double y = (r*Math.sin(startAngleInRadians+sweepInRadians))+cy;
		gl.glVertex3d(x, y, cz);
	}
	
	public static void drawRoundedRectangle(GL2 gl, double cx, double cy, double w, double h, double r) {
		//GLUtilities.drawRoundedRectangle(gl, x, y, 1, 1.5, .1);
		
		gl.glVertex2d(cx +(w/2), cy);
		gl.glVertex2d(cx +(w/2), cy+(h/2)-r);
		drawArc(gl, cx+(w/2)-r, cy+(h/2)-r, r, 0.0, 90.0);
		gl.glVertex2d(cx-(w/2)+r, cy+h/2);
		drawArc(gl, cx-(w/2)+r, cy+(h/2)-r, r, 90, 90);
		gl.glVertex2d(cx-(w/2), cy-(h/2)+r);
		drawArc(gl, cx-(w/2)+r, cy-(h/2)+r, r, 180, 90);
		gl.glVertex2d(cx+(w/2)-r, cy-(h/2));
		drawArc(gl, cx+(w/2)-r, cy-(h/2)+r, r, 270, 90);
		gl.glVertex2d(cx +(w/2), cy);
		
	}
	
	public static Vector2d pointOnCircle(
			double cx, double cy, double r, double angleInDegrees
	) {
		double angleInRadians = angleInDegrees*(Math.PI/180);
		double x = cx + (Math.cos(angleInRadians)*r);
		double y = cy + (Math.sin(angleInRadians)*r);
		return new Vector2d(x,y);
	}
	
	/**
	 * Allows cosx, ncosx, sinx, nsinx, tanx, ntanx where n an integer.
	 * @param gl
	 * @param trigFunction
	 * @param startingX
	 * @param endingX
	 */
	public static void drawBasicTrigFunction(GL2 gl, String trigFunction, double startingX, double endingX) {
		String function = trigFunction;
		gl.glBegin(PrimitiveType.LINE_STRIP.getValue());
		double step = .001;
		double currentX = startingX;
		if(function.contains("cosx")) {
			if(function.length()==4) {
				while(currentX<= endingX) {
					double x = currentX;
					double y = Math.cos(x);
					gl.glVertex2d(x,y);
					currentX += step;
				}
			}
			else {
				int length = function.length();
				int cosIndex = length-4;
				int n = Integer.parseInt(function.substring(0, cosIndex));
				System.out.println(n);
				while(currentX <= endingX) {
					double x = currentX;
					double y = n * Math.cos(x);
					gl.glVertex2d(x, y);
					currentX += step;
				}
			}
		}
		if(function.contains("sinx")) {
			if(function.length()==4) {
				while(currentX<= endingX) {
					double x = currentX;
					double y = Math.sin(x);
					gl.glVertex2d(x,y);
					currentX += step;
				}
			}
			else {
				int length = function.length();
				int cosIndex = length-4;
				int n = Integer.parseInt(function.substring(cosIndex));
				while(currentX <= endingX) {
					double x = currentX;
					double y = n * Math.sin(x);
					gl.glVertex2d(x, y);
					x += step;
				}
			}
		}
		if(function.contains("tanx")) {
			if(function.length()==4) {
				while(currentX<= endingX) {
					double x = currentX;
					double y = Math.tan(x);
					gl.glVertex2d(x,y);
					currentX += step;
				}
			}
			else {
				int length = function.length();
				int cosIndex = length-4;
				int n = Integer.parseInt(function.substring(cosIndex));
				while(currentX <= endingX) {
					double x = currentX;
					double y = n * Math.tan(x);
					gl.glVertex2d(x, y);
					x += step;
				}
			}
		}
		
		gl.glEnd();
	}
}
