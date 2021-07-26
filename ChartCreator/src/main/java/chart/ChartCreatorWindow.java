package chart;

import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.TextRenderer;

import lib342.Colors;
import lib342.GraphicsWindowPP;
import lib342.ViewportLocation;
import lib342.TwoD.Arc;
import lib342.glsl.CompileFailedException;
import lib342.glsl.Shader;
import lib342.glsl.Shader.CompiledProgram;
import lib342.opengl.Utilities;
import mygraphicslib.McFallGLUtilities;

public class ChartCreatorWindow extends GraphicsWindowPP {

	private int colorLoc, mvLoc, projLoc;
	
	private int vbo[] = new int[2];
	
	Matrix4f projMatrix = new Matrix4f().identity();
	FloatBuffer projMatrixBuffer = Buffers.newDirectFloatBuffer(16);
		
	Matrix4fStack modelViewMatrix = new Matrix4fStack(16);	
	FloatBuffer modelViewMatrixBuffer = Buffers.newDirectFloatBuffer(16);
		
	private JMenuBar menuBar;
	
	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem quitMenuItem;
	
	private JMenu formatMenu;
	private JMenuItem formatColorMenuItem;
	
	private JFileChooser fileChooser = new JFileChooser();
	private JColorChooser colorChooser;
	
	private InputFileReader fileReader;
	private String type;
	
	private Rectangle viewport;
	private Rectangle titleBox;
	
	private Color titleColor;
	private boolean titleClicked;
	public ChartCreatorWindow() {
		// TODO Auto-generated constructor stub
		super("Drawing a line chart");	
		canvas.addMouseListener(new MouseHandler());
		fileReader = null;
		titleClicked = false;
		
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		openMenuItem = new JMenuItem("Open");
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		openMenuItem.addActionListener(e -> this.fileOpenClicked());
		
		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		quitMenuItem.addActionListener(e -> System.exit(0));
		
		fileMenu.add(openMenuItem);
		fileMenu.add(quitMenuItem);
		
		formatMenu = new JMenu("Format");
		
		formatColorMenuItem = new JMenuItem("Color");
		formatColorMenuItem.addActionListener(e -> this.formatColorClicked());
		formatColorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		//  Disable this menu item by default, since something must be selected for it to be 
		//  enabled
		formatColorMenuItem.setEnabled(false);
		colorChooser = new JColorChooser(Color.BLACK);
		
		formatMenu.add(formatColorMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(formatMenu);
		setJMenuBar(menuBar);
		titleColor = Color.BLACK;
	}
	
	@Override
	public void display(GLAutoDrawable arg0) {

		GL4 gl = (GL4) canvas.getGL();
		McFallGLUtilities.clearColorBuffer(gl, Colors.WHITE);
		gl.glUniform4fv(colorLoc,  1, Colors.BLACK,0);
		
		gl.glViewport(0, 0, Utilities.getCanvasWidth(canvas), Utilities.getCanvasHeight(canvas));
		//Render Chart Title
		{
			gl.glUseProgram(0);
			gl.glBindVertexArray(0);
			TextRenderer renderer = new TextRenderer(new Font(Font.MONOSPACED, Font.BOLD, 30));
			renderer.setColor(colorChooser.getColor());
			renderer.beginRendering(canvas.getWidth(), canvas.getHeight());
			double width = renderer.getBounds(fileReader.getNameOfChart()).getWidth();
			double height = renderer.getBounds(fileReader.getNameOfChart()).getHeight();
			renderer.draw(fileReader.getNameOfChart(), canvas.getWidth()/2-(int)(width/2), (int)(canvas.getHeight()*.95));
			titleBox = new Rectangle(canvas.getWidth()/2-(int)(width/2), (int)(canvas.getHeight()*.95), (int)width, (int)height);
			renderer.endRendering();
			if(titleClicked) {
				formatColorMenuItem.setEnabled(true);
				drawTitleBox(gl);
			}
			else {
				formatColorMenuItem.setEnabled(false);
			}
		}
		
		//chart viewport
		{
		viewport = McFallGLUtilities.getMaximumViewport(canvas, 1.0/1.0, ViewportLocation.BottomLeft);
		gl.glViewport(10, 10, (int)(viewport.width*.9), (int)(viewport.height*.9));
		drawBorder(gl);
		}
		
		//Legend viewport
		{
			drawLegend(gl);
		}
		//chart viewport for drawing data
		{
			viewport = McFallGLUtilities.getMaximumViewport(canvas, 1.0/1.0, ViewportLocation.BottomLeft);
			gl.glViewport(10, 10, (int)(viewport.width*.9), (int)(viewport.height*.9));
			drawData(gl);
		}
	}
	
	public void drawTitleBox(GL4 gl) {
		projMatrix.setOrtho2D(0, Utilities.getCanvasWidth(canvas), 0, Utilities.getCanvasHeight(canvas));
		
		gl.glUseProgram(rendering_program);
		//Set up matrices
		gl.glUniformMatrix4fv(projLoc, 1, false, projMatrix.get(projMatrixBuffer));
		modelViewMatrix.identity();
		gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
		double dpiScalingFactorX = Utilities.getDPIScalingFactors(canvas).x;
		double dpiScalingFactorY = Utilities.getDPIScalingFactors(canvas).y;
		float x = (float) (titleBox.x*dpiScalingFactorX);
		float y = (float) (titleBox.y*dpiScalingFactorY);
		float width = (float) (titleBox.width*dpiScalingFactorX);
		float height = (float) (titleBox.height*dpiScalingFactorY);
		float[] boxData = new float[] {
				x,y,
				x+width,y,
				x+width,y+height,
				x, y+height
		};
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(boxData);
		
		gl.glGenBuffers(1, vbo, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
		gl.glBufferData(gl.GL_ARRAY_BUFFER, vBuf.limit()*4, vBuf, gl.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0,2,gl.GL_FLOAT, false, 0 ,0);
		gl.glLineWidth(2);
		gl.glDrawArrays(gl.GL_LINE_LOOP, 0, 4);
		gl.glBindVertexArray(0);
	}
	
	public void drawBorder(GL4 gl) {
		projMatrix.setOrtho2D(0,10,0,10);
		gl.glUseProgram(rendering_program);
		
		//Set up matrices
		gl.glUniformMatrix4fv(projLoc, 1, false, projMatrix.get(projMatrixBuffer));
		modelViewMatrix.identity();
		gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
		
		//Generate buffers with points.
		float[] borderData = {0,0, 10.0f,0f, 10.0f,10.0f, 0f,10.0f};
		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(borderData);
		gl.glGenBuffers(1, vbo, 0);
		
		gl.glBindVertexArray(vao[0]);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
		gl.glBufferData(gl.GL_ARRAY_BUFFER, vBuf.limit()*4, vBuf, gl.GL_STATIC_DRAW);
		
		gl.glVertexAttribPointer(0,2,gl.GL_FLOAT, false, 0 ,0);
		gl.glLineWidth(10);
		gl.glDrawArrays(gl.GL_LINE_LOOP, 0, 4);
		gl.glBindVertexArray(0);
		
		float[] tickData = {
				1f,0f, 1f,.3f,
				2f,0f, 2f,.3f,
				3f,0f, 3f,.3f,
				4f,0f, 4f,.3f,
				5f,0f, 5f,.3f,
				6f,0f, 6f,.3f,
				7f,0f, 7f,.3f,
				8f,0f, 8f,.3f,
				9f,0f, 9f,.3f,
				
				0f,1f, .3f,1f,
				0f,2f, .3f,2f,
				0f,3f, .3f,3f,
				0f,4f, .3f,4f,
				0f,5f, .3f,5f,
				0f,6f, .3f,6f,
				0f,7f, .3f,7f,
				0f,8f, .3f,8f,
				0f,9f, .3f,9f,
		};

		
		FloatBuffer tickBuf = Buffers.newDirectFloatBuffer(tickData);
		gl.glGenBuffers(1, vbo, 0);
		
		gl.glBindVertexArray(vao[0]);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
		gl.glBufferData(gl.GL_ARRAY_BUFFER, tickBuf.limit()*4, tickBuf, gl.GL_STATIC_DRAW);
		
		gl.glVertexAttribPointer(0,2,gl.GL_FLOAT, false, 0 ,0);
		gl.glLineWidth(5);
		gl.glDrawArrays(gl.GL_LINES, 0, 36);
		gl.glBindVertexArray(0);
		
		
	}

	public void drawLegend(GL4 gl) {
		Rectangle viewport = McFallGLUtilities.getMaximumViewport(canvas, 1.0/1.0, ViewportLocation.BottomLeft);
		double legendWidth = (Utilities.getCanvasWidth(canvas)- (viewport.width*.9))*.8;
		double legendHeight = 100*fileReader.getNumberOfDataSets();
		gl.glViewport(Utilities.getCanvasWidth(canvas)-(int)(legendWidth), (int)(Utilities.getCanvasHeight(canvas)/2-(legendHeight/2)), (int)(legendWidth), (int)(legendHeight));
		
		//Set up matrices
		int numDataEntry = fileReader.getNumberOfDataSets();
		projMatrix.setOrtho2D(0, 1, 0, numDataEntry);
		gl.glUniformMatrix4fv(projLoc, 1, false, projMatrix.get(projMatrixBuffer));
		modelViewMatrix.identity();
		gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
		
		//Generate, bind, and use vertex buffers
		float[] border = new float[] {0,0, 1f,0f, 1f,(float)numDataEntry,0f,(float)numDataEntry};
		FloatBuffer borderBuf = Buffers.newDirectFloatBuffer(border);
		gl.glGenBuffers(1, vbo, 0);
		gl.glBindVertexArray(vao[0]);
		
		//Box
		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
		gl.glBufferData(gl.GL_ARRAY_BUFFER, borderBuf.limit()*4, borderBuf, gl.GL_STATIC_DRAW);

		
		//Drawing Box
		gl.glBindVertexArray(vao[0]);
		gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 2, GL.GL_FLOAT, false, 0, 0);
		gl.glDrawArrays(GL.GL_LINE_LOOP, 0, 4);
		gl.glBindVertexArray(0);
		
		
		gl.glUseProgram(0);
		double legendWidthConverted = legendWidth/Utilities.getDPIScalingFactors(canvas).x;
		double legendHeightConverted = legendHeight/Utilities.getDPIScalingFactors(canvas).y;
		int height = 0;
		for(int i =0; i<fileReader.getNumberOfDataSets(); i++) {
			TextRenderer tr = new TextRenderer(new Font(Font.MONOSPACED, Font.PLAIN, (int)(legendHeightConverted/fileReader.getNumberOfDataSets())-20));
			float[] color = fileReader.getColors().get(i);
			tr.setColor(color[0]/255, color[1]/255, color[2]/255, 1);
			height += legendHeightConverted/fileReader.getNumberOfDataSets();
			tr.beginRendering((int)(legendWidthConverted), (int)(legendHeightConverted));
			tr.draw(fileReader.getDataSeriesNames().get(i), (int)(legendWidthConverted*.25), (int)(height-((legendHeightConverted/fileReader.getNumberOfDataSets())/2)-tr.getBounds(fileReader.getDataSeriesNames().get(i)).getHeight()/2));
			tr.endRendering();
		}
		gl.glUseProgram(rendering_program);
		float height2 = 0;
		for(int i = 0; i<fileReader.getNumberOfDataSets(); i++) {
			float[] color = fileReader.getColors().get(i);
			gl.glUniform4fv(colorLoc, 1, new float[] {color[0]/255, color[1]/255, color[2]/255}, 0);
			height2++;
			float[] data = new float[] {};
			switch(i) {
			case 0:
				data = new float[] {
						.05f,(float) (height2-.9f),
						.15f,(float) (height2-.9f),
						.15f,(float) (height2-.4f),
						.05f,(float) (height2-.4f),
				};
				break;
			case 1:
				Arc arc = new Arc(.10f, (float)height2-.65f, .05f, 0, 360);
				arc.setCenterPointIncluded(true);
				data = arc.getVertices(100);
				int j = 0;
				
				break;
			case 2:
				data = new float[] {
						.1f,height2-.4f,
						.05f, height2-.9f,
						.15f, height2-.9f
				};
				break;
			case 3:
				data = new float[] {
						.1f,height2-.3f,
						.05f, height2-.65f,
						.1f, height2-1f,
						.15f, height2-.65f
				};
				break;
			}
			FloatBuffer vBuf = Buffers.newDirectFloatBuffer(data);
			gl.glGenBuffers(1, vbo, 0);
			gl.glBindVertexArray(vao[0]);
			gl.glEnableVertexAttribArray(0);
			gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
			gl.glBufferData(gl.GL_ARRAY_BUFFER, vBuf.limit()*4, vBuf, gl.GL_STATIC_DRAW);
			
			gl.glVertexAttribPointer(0,2,gl.GL_FLOAT, false, 0 ,0);
			gl.glLineWidth(5);
			gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, data.length/2);
			gl.glBindVertexArray(0);
		}
	}
	
	public void drawData(GL4 gl) {
		gl.glUseProgram(rendering_program);

		for(int i = 0; i<fileReader.getNumberOfDataSets(); i++) {
			projMatrix.setOrtho2D(0,fileReader.getXMax(),0,fileReader.getYMax());
			//Set up matrices
			gl.glUniformMatrix4fv(projLoc, 1, false, projMatrix.get(projMatrixBuffer));
			modelViewMatrix.identity();
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			float[] color = fileReader.getColors().get(i);
			gl.glUniform4fv(colorLoc, 1, new float[] {color[0]/255, color[1]/255, color[2]/255}, 0);
			ArrayList<Vector2f> vertexArray = fileReader.getDataSet(i);
			float[] vertexData = new float[vertexArray.size()*2];
			int index = 0;
			for(Vector2f vector : vertexArray) {
				vertexData[index] = vector.x;
				index++;
				vertexData[index] = vector.y;
				index++;
			}
			
			FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vertexData);
			
			gl.glGenBuffers(1, vbo, 0);
			gl.glBindVertexArray(vao[0]);
			gl.glEnableVertexAttribArray(0);
			
			gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
			gl.glBufferData(gl.GL_ARRAY_BUFFER, vBuf.limit()*4, vBuf, gl.GL_STATIC_DRAW);
			
			gl.glVertexAttribPointer(0,2,gl.GL_FLOAT, false, 0 ,0);
			gl.glLineWidth(5);
			gl.glDrawArrays(gl.GL_LINE_STRIP, 0, fileReader.getDataSets().get(i).size());
			gl.glBindVertexArray(0);
			
			switch(i) {
			case 0:
				drawSquares(gl, vertexData);
				break;
			case 1:
				drawCircles(gl, vertexData);
				break;
			case 2:
				drawTriangles(gl, vertexData);
				break;
			case 3:
				drawDiamonds(gl, vertexData);
				break;
			}
		}
	}
	
	public void drawSquares(GL4 gl, float[] vertexData) {
		projMatrix.setOrtho2D(0, viewport.width, 0, viewport.height);
		gl.glUniformMatrix4fv(projLoc, 1, false, projMatrix.get(projMatrixBuffer));
		modelViewMatrix.identity();
		gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
		int i =0;
		while(i<vertexData.length) {
			Vector2f vertex = new Vector2f(vertexData[i], vertexData[i+1]);
			float viewX = ((vertex.x)/fileReader.getXMax())*(viewport.width);
			float viewY = ((vertex.y)/fileReader.getYMax())*(viewport.height);
			float[] squareData = new float[] {
					(float) (viewX-(.01*viewport.width)),viewY-(.01f*viewport.height),
					(float) (viewX+(.01*viewport.width)),viewY-(.01f*viewport.height),
					(float) (viewX+(.01*viewport.width)),viewY+(.01f*viewport.height),
					(float) (viewX-(.01*viewport.width)),viewY+(.01f*viewport.height)
			};
			FloatBuffer vBuf = Buffers.newDirectFloatBuffer(squareData);
			gl.glGenBuffers(1, vbo, 0);
			gl.glBindVertexArray(vao[0]);
			gl.glEnableVertexAttribArray(0);
			gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
			gl.glBufferData(gl.GL_ARRAY_BUFFER, vBuf.limit()*4, vBuf, gl.GL_STATIC_DRAW);
			gl.glVertexAttribPointer(0, 2, gl.GL_FLOAT, false, 0, 0);
			gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, 4);
			gl.glBindVertexArray(0);
			i+=2;
		}
	}
	public void drawCircles(GL4 gl, float[] vertexData) {
		projMatrix.setOrtho2D(0, viewport.width, 0, viewport.height);
		gl.glUniformMatrix4fv(projLoc, 1, false, projMatrix.get(projMatrixBuffer));
		modelViewMatrix.identity();
		gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
		int i =0;
		while(i<vertexData.length) {
			Vector2f vertex = new Vector2f(vertexData[i], vertexData[i+1]);
			float viewX = ((vertex.x)/fileReader.getXMax())*(viewport.width);
			float viewY = ((vertex.y)/fileReader.getYMax())*(viewport.height);
			Arc arc = new Arc(viewX, viewY, (float) (viewport.height*.01), 0, 360);
			arc.setCenterPointIncluded(true);
			float[] arcData = arc.getVertices(1000);
			FloatBuffer vBuf = Buffers.newDirectFloatBuffer(arcData);
			
			gl.glGenBuffers(1, vbo, 0);
			gl.glBindVertexArray(vao[0]);
			gl.glEnableVertexAttribArray(0);
			gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
			gl.glBufferData(gl.GL_ARRAY_BUFFER, vBuf.limit()*4, vBuf, gl.GL_STATIC_DRAW);
			gl.glVertexAttribPointer(0, 2, gl.GL_FLOAT, false, 0, 0);
			gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, arcData.length/2);
			gl.glBindVertexArray(0);
			i+=2;
		}
	}
	public void drawTriangles(GL4 gl, float[] vertexData) {
		projMatrix.setOrtho2D(0, viewport.width, 0, viewport.height);
		gl.glUniformMatrix4fv(projLoc, 1, false, projMatrix.get(projMatrixBuffer));
		modelViewMatrix.identity();
		gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
		int i =0;
		while(i<vertexData.length) {
			Vector2f vertex = new Vector2f(vertexData[i], vertexData[i+1]);
			float viewX = ((vertex.x)/fileReader.getXMax())*(viewport.width);
			float viewY = ((vertex.y)/fileReader.getYMax())*(viewport.height);
			float[] triangleData = new float[] {
					(float) (viewX-(.01*viewport.width)),viewY-(.01f*viewport.height),
					(float) (viewX+(.01*viewport.width)),viewY-(.01f*viewport.height),
					(float) (viewX),viewY+(.01f*viewport.height),
			};
			FloatBuffer vBuf = Buffers.newDirectFloatBuffer(triangleData);
			gl.glGenBuffers(1, vbo, 0);
			gl.glBindVertexArray(vao[0]);
			gl.glEnableVertexAttribArray(0);
			gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
			gl.glBufferData(gl.GL_ARRAY_BUFFER, vBuf.limit()*4, vBuf, gl.GL_STATIC_DRAW);
			gl.glVertexAttribPointer(0, 2, gl.GL_FLOAT, false, 0, 0);
			gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, 3);
			gl.glBindVertexArray(0);
			i+=2;
		}
	}
	public void drawDiamonds(GL4 gl, float[] vertexData) {
		projMatrix.setOrtho2D(0, viewport.width, 0, viewport.height);
		gl.glUniformMatrix4fv(projLoc, 1, false, projMatrix.get(projMatrixBuffer));
		modelViewMatrix.identity();
		gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
		int i =0;
		while(i<vertexData.length) {
			Vector2f vertex = new Vector2f(vertexData[i], vertexData[i+1]);
			float viewX = ((vertex.x)/fileReader.getXMax())*(viewport.width);
			float viewY = ((vertex.y)/fileReader.getYMax())*(viewport.height);
			float[] diamondData = new float[] {
					viewX-(.01f*viewport.width), viewY,
					viewX, viewY-(.015f*viewport.height),
					viewX+(.01f*viewport.width), viewY,
					viewX, viewY+(.015f*viewport.height)
			};
			FloatBuffer vBuf = Buffers.newDirectFloatBuffer(diamondData);
			gl.glGenBuffers(1, vbo, 0);
			gl.glBindVertexArray(vao[0]);
			gl.glEnableVertexAttribArray(0);
			gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0]);
			gl.glBufferData(gl.GL_ARRAY_BUFFER, vBuf.limit()*4, vBuf, gl.GL_STATIC_DRAW);
			gl.glVertexAttribPointer(0, 2, gl.GL_FLOAT, false, 0, 0);
			gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, 4);
			gl.glBindVertexArray(0);
			i+=2;
		}
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);	
		
		GL4 gl = (GL4) drawable.getGL();
		
		CompiledProgram program;
		try {
			program = Shader.compileShaders(gl, 
				GL_VERTEX_SHADER, "/vertex.projection-and-viewing-3d.shader", 
				GL_FRAGMENT_SHADER, "/fragment.uniform-color.shader"
			);
			rendering_program = program.getProgram();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CompileFailedException e) {
			for (Map.Entry<Integer, String> failure : e.getFailures().entrySet()) {
				System.err.println("Failure in %d".formatted(failure.getKey()));
				System.err.println(failure.getValue());
			}
		}
		

		colorLoc = gl.glGetUniformLocation(rendering_program, "input_color");				
		mvLoc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		projLoc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
		System.err.println(projMatrix);
		
	}
	
	private void fileOpenClicked () {
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			setInputFile(selectedFile);
			System.err.println("Opened file %s".formatted(selectedFile.getAbsolutePath()));
		}
	}
	
	private void formatColorClicked() {
		System.err.println("Format color clicked");
		JFrame frame = new JFrame("Choose a color");
		frame.add(colorChooser);
		frame.setSize(500, 500);
		frame.setVisible(true);
		colorChooser.setVisible(true);
		WindowListener exitListener = new WindowAdapter() {

		    @Override
		    public void windowClosing(WindowEvent e) {
		        titleColor = colorChooser.getColor();
		        canvas.repaint();
		    }
		};
		frame.addWindowListener(exitListener);
	}
	
	private void setInputFile(String filePath) {
		fileReader = new InputFileReader(filePath);
	}
	private void setInputFile(File file) {
		fileReader = new InputFileReader(file);
	}
	
	public static void main(String[] args) {
		ChartCreatorWindow window = new ChartCreatorWindow();
		if(args.length>0) {
			window.setInputFile(args[0]);
		}
		else {
			window.fileOpenClicked();
		}
		window.setVisible(true);
		
	}

	public class MouseHandler extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			int xCor = e.getX();
			int yCor = canvas.getHeight()-e.getY();
			if(withinBounds(titleBox.x, (int) titleBox.getMaxX(), titleBox.y, (int) titleBox.getMaxY(), xCor, yCor)) {
				if(titleClicked) {
					titleClicked = false;
					formatColorClicked();
					canvas.repaint();
				}
				else {
					titleClicked = true;
					canvas.repaint();
				}
			}
			else {
				titleClicked = false;
				canvas.repaint();
			}
		}
		public boolean withinBounds(int left, int right, int bottom, int top, int eventX, int eventY) {
			if(eventX>left && eventY>bottom) {
				if(eventX<right&&eventY<top) {
					return true;
				}
			}
			return false;
		}
	}
}
