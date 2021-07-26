package robot;

import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

import lib342.Colors;
import lib342.GraphicsWindowPP;
import lib342.glsl.CompileFailedException;
import lib342.glsl.Shader;
import lib342.glsl.Shader.CompiledProgram;
import lib342.models.Cube;
import lib342.opengl.Utilities;
import mygraphicslib.McFallGLUtilities;

@SuppressWarnings("serial")
public class RobotStarter extends GraphicsWindowPP {
	
	enum Direction {
		POSITIVE, NEGATIVE
	}
	
	enum Joint {
		HEAD, BODY, WAIST, LEFT_ARM, RIGHT_ARM, LEFT_ELBOW, RIGHT_ELBOW
	}
	
	enum Rotation {
		LEFT_ARM_POSITIVE(Joint.LEFT_ARM, Direction.POSITIVE), 
		LEFT_ARM_NEGATIVE(Joint.LEFT_ARM, Direction.NEGATIVE),
		RIGHT_ARM_POSITIVE(Joint.RIGHT_ARM, Direction.POSITIVE), 
		RIGHT_ARM_NEGATIVE(Joint.RIGHT_ARM, Direction.NEGATIVE),
		LEFT_ELBOW_POSITIVE(Joint.LEFT_ELBOW, Direction.POSITIVE), 
		LEFT_ELBOW_NEGATIVE(Joint.LEFT_ELBOW, Direction.NEGATIVE),
		RIGHT_ELBOW_POSITIVE(Joint.RIGHT_ELBOW, Direction.POSITIVE), 
		RIGHT_ELBOW_NEGATIVE(Joint.RIGHT_ELBOW, Direction.NEGATIVE),
		HEAD_POSITIVE(Joint.HEAD, Direction.POSITIVE), 
		HEAD_NEGATIVE(Joint.HEAD, Direction.NEGATIVE),
		WAIST_POSITIVE(Joint.WAIST, Direction.POSITIVE), 
		WAIST_NEGATIVE(Joint.WAIST, Direction.NEGATIVE),
		BODY_POSITIVE(Joint.BODY, Direction.POSITIVE), 
		BODY_NEGATIVE(Joint.BODY, Direction.NEGATIVE), 
		NONE(null, null);

		public Direction direction;
		public Joint joint;
		
		private Rotation(Joint joint, Direction direction) {
			this.joint = joint;
			this.direction = direction;
		}
		
		public static Rotation fromKey(char key) {
			switch(key) {
			case 'l':
				return LEFT_ARM_POSITIVE;
			case 'L':
				return LEFT_ARM_NEGATIVE;
			case 'r':
				return RIGHT_ARM_POSITIVE;
			case 'R':
				return RIGHT_ARM_NEGATIVE;
			case 'e':
				return LEFT_ELBOW_POSITIVE;
			case 'E':
				return LEFT_ELBOW_NEGATIVE;
			case 'x':
				return RIGHT_ELBOW_POSITIVE;
			case 'X':
				return RIGHT_ELBOW_NEGATIVE;
			case 'h':
				return HEAD_POSITIVE;
			case 'H':
				return HEAD_NEGATIVE;
			case 'w':
				return WAIST_POSITIVE;
			case 'W':
				return WAIST_NEGATIVE;
			case 'b':
				return BODY_POSITIVE;
			case 'B':
				return BODY_NEGATIVE;
			}			
			return NONE;
		}
	}
	
	class KeyHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if ( ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) > 0) && (e.getKeyCode() == KeyEvent.VK_R)) {										
				resetRotations();					
				canvas.repaint();								
			}
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == 'q' || e.getKeyChar() == 'Q') {
				System.exit(0);
			}
			
			Rotation rotation = Rotation.fromKey(e.getKeyChar());
			if (rotation != Rotation.NONE) {
				int current = rotations.get(rotation.joint);
				if(rotation == Rotation.LEFT_ELBOW_NEGATIVE || rotation == Rotation.RIGHT_ELBOW_NEGATIVE) {
					if(current>-25) {
						current += rotation.direction == Direction.POSITIVE ? 5 : -5;
						rotations.put(rotation.joint, current);	
					}
				}
				else {
					current += rotation.direction == Direction.POSITIVE ? 5 : -5;
					rotations.put(rotation.joint, current);	
				}
				canvas.repaint();
			}			
		}
	}
	
	Map<Joint, Integer> rotations;
		
	Matrix4f projMatrix = new Matrix4f().identity();
	FloatBuffer projMatrixBuffer = Buffers.newDirectFloatBuffer(16);
		
	Matrix4fStack modelViewMatrix = new Matrix4fStack(16);
	
	FloatBuffer modelViewMatrixBuffer = Buffers.newDirectFloatBuffer(16);
	
	Cube cube;
	
	int colorLoc, mvLoc, projLoc;
	
	int bodyRotate = 10;
	
	public RobotStarter() {
		super("Pauly the Robot", 600, 600);
		rotations = new HashMap<Joint, Integer> ();
		resetRotations();
	}
	
	private void resetRotations () {		
		for (Joint joint : Joint.values()) {
			rotations.put(joint, 0);
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
		
		cube = Cube.getWireFrameCube(gl);		
		
		colorLoc = gl.glGetUniformLocation(rendering_program, "input_color");				
		mvLoc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		projLoc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		
		canvas.addKeyListener(new KeyHandler());
		SwingUtilities.invokeLater(() -> canvas.requestFocus());
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) canvas.getGL();
		gl.glViewport(0 , 0, Utilities.getCanvasWidth(canvas), Utilities.getCanvasHeight(canvas));
		gl.glUseProgram(rendering_program);
		
		McFallGLUtilities.clearColorBuffer(gl, Colors.WHITE);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
				
		gl.glUniform4f(colorLoc, 0f, 0f, 0f, 1f);
		
		//  Use the Utilities class methods here to compute the canvas' aspect ratio,
		//  just in case it's not a non-uniform scaling ratio for X and Y
		float ar = Utilities.getCanvasWidth(canvas) / Utilities.getCanvasHeight(canvas);		
		projMatrix.setPerspective((float) Math.PI/3, ar, 0.1f, 1000);
		
		gl.glUniformMatrix4fv(projLoc, 1, false, projMatrix.get(projMatrixBuffer));
		
		//  This puts the "viewing transformation" on the top of the stack
		//  Here we are positioning ourselves out on the positive Z axis, looking 
		//  towards the origin
		modelViewMatrix.setLookAt(0, 0f, 15f, 0, 0, 0, 0, 1f, 0);
					
		//  Scale things down a bit
		modelViewMatrix.scale(0.5f);
		
		// Rotate the entire body
		modelViewMatrix.rotateY((float) Math.toRadians(rotations.get(Joint.BODY)));
		
		modelViewMatrix.pushMatrix();
		{
			modelViewMatrix.rotateY((float) Math.toRadians(rotations.get(Joint.WAIST)));

			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
		}
		modelViewMatrix.popMatrix();
		modelViewMatrix.pushMatrix();
		{
			modelViewMatrix.rotateY((float) Math.toRadians(rotations.get(Joint.WAIST)));

			//  A medium green color
			gl.glUniform4f(colorLoc, 0f, 0.5f, 0f, 1f);
			modelViewMatrix.translate(0, 2, 0);
			//  Rotate this one around the x axis, based on the value of the "HEAD" rotation
			
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			
			cube.draw(gl);
			modelViewMatrix.rotateX((float) Math.toRadians(rotations.get(Joint.RIGHT_ARM)));
			//Right Arm
			modelViewMatrix.translate(2, 0, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(2, 0, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			
			//Right Elbow
			
			modelViewMatrix.translate(2, 0, 0);
			modelViewMatrix.rotateZ((float) Math.toRadians(rotations.get(Joint.RIGHT_ELBOW)));
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, 2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, 2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			
			
		}
		modelViewMatrix.popMatrix();
		
		modelViewMatrix.pushMatrix();
		{
			modelViewMatrix.rotateY((float) Math.toRadians(rotations.get(Joint.WAIST)));
			modelViewMatrix.translate(0,2,0);
			//Left Arm
			modelViewMatrix.rotateX((float) Math.toRadians(rotations.get(Joint.LEFT_ARM)));
			modelViewMatrix.translate(-2, 0, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(-2, 0, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			
			//Left Elbow
			
			modelViewMatrix.translate(-2, 0, 0);
			modelViewMatrix.rotateZ((float) Math.toRadians(rotations.get(Joint.LEFT_ELBOW)));
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, 2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, 2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
		}
		modelViewMatrix.popMatrix();
		modelViewMatrix.pushMatrix();
		
		{
			modelViewMatrix.rotateY((float) Math.toRadians(rotations.get(Joint.WAIST)));
			modelViewMatrix.translate(0,4,0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
		}
		modelViewMatrix.popMatrix();
		modelViewMatrix.pushMatrix();
		{
			modelViewMatrix.rotateX((float) Math.toRadians(rotations.get(Joint.HEAD)));
			modelViewMatrix.rotateY((float) Math.toRadians(rotations.get(Joint.WAIST)));
			modelViewMatrix.translate(0,4,0);
			
			modelViewMatrix.translate(0,2,0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(2,0,0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(-4,0,0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0,0,2);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0,0,-2);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0,0,-2);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			
			modelViewMatrix.translate(2,0,0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(2,0,0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			
			modelViewMatrix.translate(0,0,4);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(-2,0,0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
		}
		modelViewMatrix.popMatrix();
		//  After popping the matrix, origin is back at the center of the black cube
		//  Move down 2 and draw a blue cube
		//  We're not doing anything after this, so we don't really need to push / pop 
		//  the modelview matrix, but do it anyway for consistency's sake
		modelViewMatrix.pushMatrix();
		{
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));

			//  A medium blue color
			gl.glUniform4f(colorLoc, 0f, 0f, 0.5f, 1f);
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			//left leg
			modelViewMatrix.translate(-2, 2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(-2, 0, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, 0, 2);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(2, 0, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			
			
		}
		modelViewMatrix.popMatrix();
		modelViewMatrix.pushMatrix();
		{
			//right leg
			modelViewMatrix.translate(0, -4 , 0);
			modelViewMatrix.translate(2, 2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, -2, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(2, 0, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(0, 0, 2);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);
			modelViewMatrix.translate(-2, 0, 0);
			gl.glUniformMatrix4fv(mvLoc, 1, false, modelViewMatrix.get(modelViewMatrixBuffer));
			cube.draw(gl);

			//  A medium blue color
			
		}
		modelViewMatrix.popMatrix();
	}
	
	public static void main(String[] args) {
		new RobotStarter().setVisible(true);

	}

}