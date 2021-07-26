package tetris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class MainMethod {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean testMode = false;
		String filePath ="";
		if(args.length>0) {
			testMode = Boolean.valueOf(args[0]);
			if(args.length>1) {
				filePath = args[1];
				
			}
		}
		TetrisCanvas tetrisCanvas = new TetrisCanvas(testMode);
		if(!filePath.isEmpty()) {
			tetrisCanvas.parseFile(filePath);
		}
		tetrisCanvas.setVisible(true);
	}
	
	
}
