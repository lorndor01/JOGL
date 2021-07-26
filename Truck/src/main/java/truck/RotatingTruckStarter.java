package truck;

import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.joml.Matrix4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

import lib342.Colors;
import lib342.GraphicsWindowPP;
import lib342.models.Truck;
import lib342.glsl.CompileFailedException;
import lib342.glsl.Shader;
import lib342.glsl.Shader.CompiledProgram;
import lib342.opengl.Constants.PrimitiveType;
import mygraphicslib.McFallGLUtilities;


@SuppressWarnings("serial")
public class RotatingTruckStarter extends GraphicsWindowPP {

	private Truck truck;
	
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f transformationMatrix = new Matrix4f();
	private FloatBuffer projMatrixBuffer = Buffers.newDirectFloatBuffer(16);
	private FloatBuffer transformationMatrixBuffer = Buffers.newDirectFloatBuffer(16);

	private int projMatLoc;
	private int transformationMatLoc;
	private int colorLoc;
				
	private int rotateAngle = 0;
	
	private int increment;
	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		GL4 gl = (GL4) drawable.getGL();
		truck = new Truck(gl);
		
		CompiledProgram program;
		try {
			program = Shader.compileShaders(gl, GL_VERTEX_SHADER, "/RotatingTruckStarter.vertex.shader", GL_FRAGMENT_SHADER, "/RotatingTruckStarter.fragment.shader");
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
				
		projectionMatrix.setOrtho2D(-3, 3, -3, 3);
				
		projMatLoc = gl.glGetUniformLocation(rendering_program, "proj_matrix");	
		transformationMatLoc = gl.glGetUniformLocation(rendering_program, "transformation_matrix");
		colorLoc = gl.glGetUniformLocation(rendering_program, "input_color");	
		
		Timer timer = new Timer(10, (e) -> {
			rotateAngle = (rotateAngle + 2) % 360; 
			canvas.repaint();
		});
		timer.start();
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) canvas.getGL();
		McFallGLUtilities.clearColorBuffer(gl, Colors.BLACK);

		Rectangle viewport = McFallGLUtilities.getMaximumViewport(canvas, 1.0);
		gl.glViewport(viewport.x, viewport.y, viewport.width, viewport.height);
		
		gl.glUseProgram(rendering_program);										
		gl.glUniformMatrix4fv(projMatLoc, 1, false, projectionMatrix.get(projMatrixBuffer));				
		
		transformationMatrix.identity();
		transformationMatrix.scaleLocal(.3f);
		transformationMatrix.rotateLocal((float)(-Math.PI/2), 0, 0, 1);
		
		transformationMatrix.translate(0, 3.9f, 0);
		transformationMatrix.rotateLocal((float)-Math.toRadians(rotateAngle), 0, 0, 1);
		gl.glUniformMatrix4fv(transformationMatLoc, 1, false, transformationMatrix.get(transformationMatrixBuffer));
		
		gl.glUniform4f(colorLoc, 1.0f, 0.0f, 0.0f, 1.0f);
		truck.draw(gl);	
										
		gl.glUseProgram(0);
		GL2 gl2 = (GL2) gl;
		McFallGLUtilities.setWorldWindow(gl2, -3, 3, -3, 3);
				
		PrimitiveType.LINE_LOOP.begin(gl2);
		McFallGLUtilities.drawArc(gl2, 0, 0, 1, 0, 360);
		PrimitiveType.LINE_LOOP.end(gl2);
	}
	
	public RotatingTruckStarter() {
	}
	public static void main(String[] args) {
		new RotatingTruckStarter().setVisible(true);
	}
}

