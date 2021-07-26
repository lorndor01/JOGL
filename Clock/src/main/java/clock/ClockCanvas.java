package clock;

import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.util.Random;

import javax.swing.Timer;

import org.joml.Vector2d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import lib342.Colors;
import lib342.GraphicsWindowFP;
import lib342.ViewportLocation;
import lib342.opengl.Constants.PrimitiveType;
import lib342.opengl.Utilities;
import mygraphicslib.GLUtilities;
import mygraphicslib.McFallGLUtilities;
public class ClockCanvas extends GraphicsWindowFP {
	public static int second;
	public static int minute;
	public static int hour;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClockCanvas canvas = new ClockCanvas(1000, 500);
		canvas.setVisible(true);
	}
	
	public ClockCanvas(int width, int height) {
		super("Clock", width, height);
		Timer timer = new Timer(1000, e->{
			canvas.repaint();
		});
		timer.start();
	}
	@Override
	public void display(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		GL2 gl = (GL2) canvas.getGL();
		drawClock(gl);
		gl.glColor3fv(Colors.BLACK,0);
		LocalDateTime time = LocalDateTime.now();
		second = time.getSecond();
		minute = time.getMinute();
		hour = time.getHour();
		gl.glBegin(PrimitiveType.LINES.getValue());
		{
			gl.glVertex2d(0, -.1);
			double angle = -30*(hour-3);
			Vector2d point = GLUtilities.pointOnCircle(0, -.1, .2, angle);
			gl.glVertex2d(point.x, point.y);

			gl.glVertex2d(0, -.1);
			angle = -6*(minute-15);
			point = GLUtilities.pointOnCircle(0, -.1, .3, angle);
			gl.glVertex2d(point.x, point.y);

			gl.glVertex2d(0, -.1);
			angle = -6 * (second-15);
			point = GLUtilities.pointOnCircle(0, -.1, .4, angle);
			gl.glVertex2d(point.x, point.y);
		}
		gl.glEnd();
	}

	public void drawClock(GL2 gl) {
		GLUtilities.clearColorBuffer(gl, Colors.WHITE);
		McFallGLUtilities.setWorldWindow(gl, -2,2, -1, 1);
		Rectangle viewport = McFallGLUtilities.getMaximumViewport(Utilities.getCanvasWidth(canvas),Utilities.getCanvasHeight(canvas), 2.0,ViewportLocation.Center);
		gl.glViewport(50, 50, (int) (950*Utilities.getDPIScalingFactors(canvas).x), (int) (450*Utilities.getDPIScalingFactors(canvas).y));
		
		gl.glColor3fv(Colors.BLACK,0);
		gl.glLineWidth(5f);
		gl.glBegin(PrimitiveType.LINE_LOOP.getValue());
		{
			gl.glVertex2d(-2, -1);
			gl.glVertex2d(2, -1);
			gl.glVertex2d(2, 1);
			gl.glVertex2d(-2, 1);
		}
		gl.glEnd();
		gl.glLineWidth(1);
		float[] brown = new float[3];
		brown[0] = 149/255f; brown[1] = 85/255f; brown[2] =0;
		gl.glColor3fv(brown, 0);
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			gl.glVertex2d(-1.8, -.9);
			gl.glVertex2d(-1.8, 0);
			double x = -(.25+.091)/Math.tan(Math.PI/9);
			double y = .25;
			GLUtilities.drawArc(gl, x, y, .25, 270, 70);
			GLUtilities.drawArc(gl, 0, -.1, .75, 160, -140);
			x = (.25+.091)/Math.tan(Math.PI/9);
			y = .25;
			GLUtilities.drawArc(gl, x, y, .25, 200, 70);
			gl.glVertex2d(1.8, 0);
			gl.glVertex2d(1.8, -.9);
		}
		gl.glEnd();
		gl.glColor3fv(Colors.WHITE,0);
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			double x = -(.25+.091)/Math.tan(Math.PI/9);
			double y = .25;
			GLUtilities.drawArc(gl, x, y, .25, 5, -100);
		}
		gl.glEnd();
		gl.glBegin(PrimitiveType.POLYGON.getValue());
		{
			GLUtilities.drawArc(gl, 0, -.1, .65, 0, 360);
		}
		gl.glEnd();
		
		gl.glColor3fv(Colors.BLACK,0);
		double angle = 0;
		while(angle<=360) {
			if(angle%30==0) {
			gl.glBegin(PrimitiveType.LINES.getValue());
			{
				Vector2d point = GLUtilities.pointOnCircle(0, -.1, .575, angle);
				gl.glVertex2d(point.x, point.y);
				point =GLUtilities.pointOnCircle(0, -.1, .65, angle);
				gl.glVertex2d(point.x, point.y);
			}
			gl.glEnd();
			}
			gl.glBegin(PrimitiveType.LINES.getValue());
			{
				Vector2d point = GLUtilities.pointOnCircle(0, -.1, .625, angle);
				gl.glVertex2d(point.x, point.y);
				point =GLUtilities.pointOnCircle(0, -.1, .65, angle);
				gl.glVertex2d(point.x, point.y);
			}
			gl.glEnd();
			angle+=6;
		}

	}
}
