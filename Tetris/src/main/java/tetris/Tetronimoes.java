package tetris;

import java.util.ArrayList;

import org.joml.Rectangled;

import com.jogamp.opengl.GL2;

import lib342.Colors;
import lib342.opengl.Constants.PrimitiveType;

public class Tetronimoes{
	private boolean firstGeneration;
	private boolean[][] array;
	private boolean isSquare;
	private double leftBound;
	private double rightBound;
	private double currentX;
	private double currentY;
	private float[] color;
	private double topBound;
	private double bottomBound;
	private ArrayList<Rectangled> rectangles;
	private Rectangled middleSquare;
	public Tetronimoes(int randomInt, double leftBound, double rightBound, double topBound, double bottomBound, float[] color){
		this.color = color;
		this.topBound = topBound;
		this.bottomBound = bottomBound;
		firstGeneration = true;
		isSquare = false;
		array = new boolean[3][3];
		this.leftBound = leftBound;
		this.rightBound = rightBound;
		rectangles = new ArrayList<Rectangled>();
		switch(randomInt) {
		case 1: generateLine(); break;
		case 2: generateHat(); break;
		case 3: generateZ(); break;
		case 4:generateL(); break;
		case 5: generateInvertedL(); break;
		case 6: generateZInverted(); break;
		case 7: generateBlock(); break;
		}
	}
	
	public void generateRectangles(double x, double y) {
		 currentX = x;
		 currentY = y;
		 boolean shiftUp = false;
		 int iFirst;

		 if(firstGeneration) {
			 firstGeneration=false;
			 if(array[0][1]==false&&array[0][1]==false&&array[0][2]==false) {
				 iFirst = 1;
			 }
			 else {
				 iFirst = 0;
			 }
			 if(array[0][2]==false&&array[1][2]==false&&array[2][2]==false) {
				 shiftUp=true;
			 }

		 }
		 else {
			 iFirst=0;
		 }
		for(int i=iFirst; i<3; i++) {
			if(shiftUp) {
				currentY =y+1;
			}
			else {
				currentY =y;
			}
			for(int j =0; j<3; j++) {

				if(array[i][j]) {
					Rectangled rectangle = new Rectangled(currentX, currentY, currentX+1, currentY +1);
					rectangles.add(rectangle);
					if(i==1&&j==1) {
						middleSquare = rectangle;
					}
				}
				
				currentY++;
			}
			currentX++;
		}
	}
	
	public void draw(GL2 gl) {

		for(Rectangled rectangle : rectangles) {
			gl.glLineWidth(1f);
			
			gl.glColor3fv(color, 0);
			gl.glBegin(PrimitiveType.TRIANGLE_STRIP.getValue());
			{
				gl.glVertex2d(rectangle.minX, rectangle.minY);
				gl.glVertex2d(rectangle.maxX, rectangle.minY);
				gl.glVertex2d(rectangle.minX, rectangle.maxY);
				gl.glVertex2d(rectangle.minX, rectangle.maxY);
				gl.glVertex2d(rectangle.maxX, rectangle.maxY);
				gl.glVertex2d(rectangle.maxX, rectangle.minY);
				
			}
			gl.glEnd();
			gl.glColor3fv(Colors.BLACK, 0);
			gl.glBegin(PrimitiveType.LINE_LOOP.getValue());
			{
				gl.glVertex2d(rectangle.minX, rectangle.minY);
				gl.glVertex2d(rectangle.minX, rectangle.minY+1);
				gl.glVertex2d(rectangle.maxX, rectangle.maxY);
				gl.glVertex2d(rectangle.maxX, rectangle.maxY-1);
			}
			gl.glEnd();
			
		}
	}
	
	public void shiftRight() {
		for(Rectangled rectangle : rectangles){
			rectangle.translate(1,0);
		}
	}
	
	public void shiftLeft() {
			for(Rectangled rectangle : rectangles){
				rectangle.translate(-1,0);
			}
	}
	
	public void moveDown() {
		if(canMoveDown()) {
			for(Rectangled rectangle : rectangles){
				rectangle.translate(0,-1);
			}
		}
	}
	
	void generateL() {
		array[1][0] = true;
		array[1][1] = true;
		array[1][2] = true;
		array[2][2] = true;
	}
	
	public void generateInvertedL() {
		array[0][2] = true;
		array[1][0] = true;
		array[1][1] = true;
		array[1][2] = true;
	}
	
	public void generateBlock() {
		array[0][2] = true;
		array[0][1] = true;
		array[1][2] = true;
		array[1][1] = true;
		isSquare = true;
	}
	
	public void generateLine() {
		array[0][1] = true;
		array[1][1] = true;
		array[2][1] = true;
	}
	
	public void generateHat() {
		array[0][1] = true;
		array[1][1] = true;
		array[2][1] = true;
		array[1][2] = true;
	}
	
	public void generateZ() {
		array[0][2] = true;
		array[1][2] = true;
		array[1][1] = true;
		array[2][1] = true;
	}
	
	public void generateZInverted() {
		array[0][1] = true;
		array[1][1] = true;
		array[1][2] = true;
		array[2][2] = true;
	}
	
	public boolean[][] getTetronimoesArray() {
		return array;
	}
	
	public void printShapeTest() {
		System.out.println(array[0][2] + " " + array[1][2] + " " + array[2][2]);
		System.out.println(array[0][1] + " " + array[1][1] + " " + array[2][1]);
		System.out.println(array[0][0] + " " + array[1][0] + " " + array[2][0]);
	}
	
	public ArrayList<Rectangled> getRectangles(){
		return rectangles;
	}
	
	public boolean canMoveRight(boolean[][] gameBoard) {
		boolean canMove = true;
		for(Rectangled rectangle : rectangles) {
			if(rectangle.maxX==rightBound) {
				canMove = false;
			}
			else {
				boolean spotIsFilled = gameBoard[(int)rectangle.minX+1][(int)rectangle.minY];
				if(spotIsFilled) {
					canMove = false;
				}
			}
		}
		return canMove;
	}

	public boolean canMoveLeft(boolean[][] gameBoard) {
		boolean canMove = true;
		for(Rectangled rectangle : rectangles) {
			if(rectangle.minX==leftBound) {
				canMove = false;
			}
			else {
				boolean boardSpotFilled = gameBoard[(int) rectangle.minX-1][(int) rectangle.minY];
				if(boardSpotFilled) {
					canMove = false;
				}
			}
		}
		return canMove;
	}

	public boolean canMoveDown() {
		boolean canMove = true;
		for(Rectangled rectangle : rectangles) {
			if(rectangle.minY==0) {
				canMove = false;
			}
		}
		return canMove;
	}

	public void rotateClockWise(boolean[][] gameBoard) {
		
		if(!isSquare) {
			if(canRotateClockwise(gameBoard)) {
				array = generateRotatedClockwiseArray();
				double minX = (middleSquare.minX-1);
				double minY = (middleSquare.minY -1); 
				rectangles.clear();
				generateRectangles(minX, minY);
			}
		}
	}

	public void rotateCounterClockwise(boolean[][] gameBoard) {
		if(canRotateClockwise(gameBoard)) {
			array = generateRotatedCounterClockwiseArray();
			double minX = middleSquare.minX-1;
			double minY = middleSquare.minY-1;
			rectangles.clear();
			generateRectangles(minX, minY);
		}
	}
	
	public void reflect(boolean[][] gameBoard) {
		if(canReflect(gameBoard)) {
			array = generateReflectedArray();
			double minX = middleSquare.minX-1;
			double minY = middleSquare.minY-1;
			rectangles.clear();
			generateRectangles(minX, minY);
		}
	}
	
	public boolean[][] generateRotatedClockwiseArray(){
		int dimensions = array.length;
		boolean[][] arrayRotated = new boolean[dimensions][dimensions];
		for(int i = 0; i<dimensions; i++) {
			for(int j = 0; j<dimensions; j++) {
				int x = i-1;
				int y = j -1;
				int newX = y;
				int newY = -(x);
				newX++;
				newY++;
				arrayRotated[newX][newY] = array[i][j];
			}
		}
		return arrayRotated;
		
	}
	
	public boolean[][] generateRotatedCounterClockwiseArray(){
		int dimension = array.length;
		boolean[][] arrayRotated = new boolean[dimension][dimension];
		for(int i = 0; i<dimension; i++) {
			for(int j = 0; j< dimension; j++) {
				int xPosition = i-1;
				int yPosition = j-1;
				int newXPosition = -(yPosition);
				int newYPosition = (xPosition);
				newXPosition +=1;
				newYPosition +=1;
				arrayRotated[newXPosition][newYPosition]= array[i][j];
			}
		}
		return arrayRotated;
	}
	
	public boolean[][] generateReflectedArray(){
		int dimension = array.length;
		boolean[][] flippedArray = new boolean[dimension][dimension];
		for(int i = 0; i<dimension; i++) {
			for(int j = 0; j<dimension; j++) {
				int x = i-1;
				int newX = -(x);
				
				newX++;
				flippedArray[newX][j] = array[i][j];			
			}
		}
		return flippedArray;
	}
	
	public boolean canRotateClockwise(boolean[][] gameBoard){
		boolean canRotate =true;
		ArrayList<Rectangled> rectanglesCopy = rectangles;
		boolean[][] arrayCopy = array;
		array = generateRotatedClockwiseArray();
		rectangles.clear();
		generateRectangles(middleSquare.minX-1, middleSquare.minY-1);
		for(Rectangled rectangle : rectangles) {
			if((rectangle.maxX>rightBound)|| (rectangle.minX<leftBound)||(rectangle.maxY>=topBound) || (rectangle.minY<=bottomBound)) {
				canRotate = false;
			}
			
			else {
				boolean gameBoardSquare = gameBoard[(int) rectangle.minX][(int) rectangle.minY];
				if(gameBoardSquare ==true) {
					canRotate = false;
				}
			}
		}
		rectangles = rectanglesCopy;
		array = arrayCopy;
		return canRotate;
	}
	
	public boolean canRotateCounterClockwise(boolean[][] gameBoard){
		boolean canRotate =true;
		ArrayList<Rectangled> rectanglesCopy = rectangles;
		boolean[][] arrayCopy = array;
		array = generateRotatedCounterClockwiseArray();
		rectangles.clear();
		generateRectangles(middleSquare.minX-1, middleSquare.minY-1);
		for(Rectangled rectangle : rectangles) {
			if(rectangle.maxX>rightBound|| rectangle.minX<leftBound||rectangle.maxY>=topBound || rectangle.minY<=bottomBound) {
				canRotate = false;
			}
			else {
				boolean gameBoardSquare = gameBoard[(int) rectangle.minX][(int) rectangle.minY];
				if(gameBoardSquare ==true) {
					canRotate = false;
				}
			}
		}
		rectangles = rectanglesCopy;
		array = arrayCopy;
		return canRotate;
	}
	
	public boolean canReflect(boolean[][] gameBoard) {
		boolean canReflect =true;
		ArrayList<Rectangled> rectanglesCopy = rectangles;
		boolean[][] arrayCopy = array;
		array = generateReflectedArray();
		double minX = middleSquare.minX-1;
		double minY = middleSquare.minY-1;
		rectangles.clear();
		generateRectangles(minX, minY);
		for(Rectangled rectangle : rectangles) {
			if(rectangle.maxX>=rightBound|| rectangle.minX<=leftBound||rectangle.maxY>=topBound || rectangle.minY<=bottomBound) {
				canReflect = false;
			}
			else {
				boolean gameBoardSquare = gameBoard[(int) rectangle.minX][(int) rectangle.minY];
				if(gameBoardSquare ==true) {
					canReflect = false;
				}
			}
		}
		rectangles = rectanglesCopy;
		array = arrayCopy;
		return canReflect;
	}
}
