package mygraphicslib;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.joml.Vector2d;

import com.jogamp.opengl.awt.GLCanvas;

import lib342.opengl.Utilities;

public class PolylineMouseHandler extends MouseAdapter {
	private PolylineCollection lines;
	private Polyline currentLine;
	private Vector2d previousPoint;
	private int time;
	private boolean trackMouse;
	private double scalingFactorX;
	private double scalingFactorY;
	public PolylineMouseHandler(PolylineCollection lines, boolean trackMouse) {
		// TODO Auto-generated constructor stub
		this.lines = lines;
		time =1;
		this.trackMouse = trackMouse;
	}
	public PolylineMouseHandler(PolylineCollection lines) {
		this(lines, false);
	}
	public PolylineCollection getLines() {
		return lines;
	}
	public void activate(GLCanvas canvas) {
		Vector2d scalingValues = Utilities.getDPIScalingFactors(canvas);
		scalingFactorX = scalingValues.x;
		scalingFactorY = scalingValues.y;
		canvas.addMouseListener(this);
		if(trackMouse ==true) {
			canvas.addMouseMotionListener(this);
		}	
	}
	public void deactivate(GLCanvas canvas) {
		canvas.removeMouseListener(this);
		if(trackMouse==true) {
			canvas.removeMouseMotionListener(this);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		super.mouseMoved(e);
		if(time!=1)
		{
			lines.remove();
			Vector2d nextPoint = new Vector2d(e.getX()*scalingFactorX, scalingFactorY*(e.getComponent().getHeight()-e.getY()));
			Polyline line = new Polyline(previousPoint, nextPoint);
			lines.add(line);
			e.getComponent().repaint();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
			//If the user left clicks
			if(e.getButton()==1) {
				//If this is the first right click then a new polyline is created.
				if(time ==1) {
					previousPoint = new Vector2d(e.getX()*scalingFactorX, (e.getComponent().getHeight()-e.getY())*scalingFactorY);
					currentLine = new Polyline(previousPoint);
					lines.add(currentLine);
					time++;	
				}
				//If this is not the first right click then we continue adding points to the polyline created when t==1
				else {
					previousPoint = new Vector2d(e.getX()*scalingFactorX, (e.getComponent().getHeight()-e.getY())*scalingFactorY);
					currentLine.addPoint(previousPoint);
					lines.add(currentLine);
					time++;
				}
			}
			//If the user right clicks.
			if(e.getButton()==3) {
				//If current line is not null then we add one last point and set it to null.
				if(currentLine != null) {
					previousPoint = new Vector2d(e.getX()*scalingFactorX, (e.getComponent().getHeight()-e.getY())*scalingFactorY);
					currentLine.addPoint(previousPoint);
					lines.add(currentLine);
					time =1;
					currentLine = null;
				}
			}
		
		e.getComponent().repaint();
	}
	public boolean isTrackMouse() {
		return trackMouse;
	}
	public void setTrackMouse(boolean trackMouse) {
		this.trackMouse = trackMouse;
	}
	
		
}
