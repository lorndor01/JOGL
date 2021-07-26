package simple_paint_lab;

import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import lib342.Colors;
import lib342.GraphicsWindowFP;
import lib342.opengl.Constants.PrimitiveType;
import lib342.opengl.Utilities;
import mygraphicslib.GLUtilities;
import mygraphicslib.PolylineCollection;
import mygraphicslib.PolylineMouseHandler;


public class PaintWindow extends GraphicsWindowFP {
	private HashMap<String, float[]> colors;
	private PolylineCollection lines;
	private PolylineMouseHandler mouseHandler;
	private JTextField textField;
	private JPanel panel;
	private final class KeyHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyChar()=='q' || e.getKeyChar()=='Q') {
				System.exit(0);
			}
		}
	}

	public PaintWindow() {
		super("A Simple Paint Program");
		lines = new PolylineCollection();
		mouseHandler = new PolylineMouseHandler(lines, true);
		mouseHandler.activate(canvas);
		add(createColorTextField(), BorderLayout.SOUTH);
		//Add key listener so that is listens to both the GLCanvas and the PaintWindow;
		KeyHandler handler = new KeyHandler();
		this.addKeyListener(handler);
		canvas.addKeyListener(handler);
		colors = new HashMap<String, float[]>();
		colors.put("white", Colors.WHITE);
		colors.put("red", Colors.RED);
		colors.put("blue", Colors.BLUE);
		colors.put("green", Colors.GREEN);
		colors.put("orange", Colors.HOPE_ORANGE);
	}
	
	public static void main(String[] args) {
		PaintWindow window = new PaintWindow();
		window.setVisible(true);
	}
	
	@Override
	public void display(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		GL2 gl = (GL2) canvas.getGL();
		GLUtilities.clearColorBuffer(gl, Colors.BLACK);
		GLUtilities.setWorldWindow(gl, 0, Utilities.getCanvasWidth(canvas), 0, Utilities.getCanvasHeight(canvas));
		gl.glViewport(0,0, Utilities.getCanvasWidth(canvas), Utilities.getCanvasHeight(canvas));
		float[] color;
		String colorRequest = textField.getText().toLowerCase();
		if(colors.keySet().contains(colorRequest)) {
			color = colors.get(colorRequest);
		}
		else {
			color = Colors.RED;
		}
		mouseHandler.getLines().draw(gl, PrimitiveType.LINE_STRIP, color);
	}
	
	public JPanel createColorTextField() {
		textField = new JTextField("White");
		panel = new JPanel();
		panel.setBorder(new TitledBorder("Available Colors: White, Red, Blue, Green, Orange"));
		panel.add(textField);
		return panel;
	}
}
