package chart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.joml.Vector2f;

public class InputFileReader {
	private BufferedReader br;
	private ArrayList<String> lines;
	private ArrayList<float[]> colors;
	private ArrayList<String> DSNames;
	private ArrayList<ArrayList<Vector2f>> dataSets;
	public InputFileReader(String filePath) {
		lines = new ArrayList<String>();
		colors = new ArrayList<float[]>();
		DSNames = new ArrayList<String>();
		dataSets = new ArrayList<ArrayList<Vector2f>>();
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			String line;
			int currentDataSetIndex =0;
			while((line = br.readLine())!=null) {
				lines.add(line);
				if(line.startsWith("DS")) {
					String[] lineSplit = line.split("\t");
					DSNames.add(lineSplit[1]);
					dataSets.add(new ArrayList<Vector2f>());
				}
				else{
					if(line.equals("-1")) {
						currentDataSetIndex++;
					}
					else {
						try {
							String[] numbers = line.split(",");
							float x = Float.valueOf(numbers[0]);
							float y = Float.valueOf(numbers[1]);
							dataSets.get(currentDataSetIndex).add(new Vector2f(x,y));
						}
						catch(NumberFormatException e) {
							
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Default line colors
		if(!lines.get(2).equals("Colors")) {
			colors.add(new float[] {69f,114f,167f});
			colors.add(new float[] {170f,70f,67f});
			colors.add(new float[] {137f,165f,78f});
			colors.add(new float[] {113f,88f,143f});
			colors.add(new float[] {65f,152f,175f});
			colors.add(new float[] {219f,132f,61f});
			colors.add(new float[] {147f,169f,207f});
			colors.add(new float[] {209f,147f,146f});
		}
		else {
			for(int i =3; i<11; i++) {
				String line = lines.get(i);
				Scanner scanner = new Scanner(line);
				colors.add(new float[] {scanner.nextInt(), scanner.nextInt(), scanner.nextInt()});
			}
		}
	}
	public InputFileReader(File file) {
		this(file.getAbsolutePath());
		
	}
	
	public String getTypeOfChart() {
		String type = lines.get(0);
		if(type.length()>1) {
			System.err.println("Invalid type on line 1 of input file. Line one should be 1 character.");
		}
		else if(!type.equals("l") && !type.equals("p")) {
			System.err.println("Invalid type on line 1 of input file.");
			System.err.println("Type must be 'p' for pie-chart or 'l' for line-chart.");
		}
		else {
			return type;
		}
		return null;
	}
	
	public String getNameOfChart() {
		return lines.get(1);
	}

	public int getNumberOfDataSets() {
		int count = 0;
		for(String line : lines) {
			if(line.startsWith("DS")) {
				count++;
			}
		}
		return count;
	}
	
	public ArrayList<String> getDataSeriesNames(){
		return DSNames;
	}
	
	public ArrayList<float[]> getColors(){
		ArrayList<float[]> colorsCopy = colors;
		return colorsCopy;
	}
	
	public ArrayList<Vector2f> getDataSet(int index){
		return dataSets.get(index);
	}

	public ArrayList<ArrayList<Vector2f>> getDataSets(){
		return dataSets;
	}

	public int getYMax() {
		int yMax = 0;
		for(ArrayList<Vector2f> points : dataSets) {
			for(Vector2f point : points) {
				if(point.y>yMax) {
					yMax = (int) point.y;
				}
			}
		}
		return yMax;
	}
	
	public int getXMax() {
		int xMax = 0;
		for(ArrayList<Vector2f> points : dataSets) {
			for(Vector2f point : points) {
				if(point.x>xMax) {
					xMax = (int) point.x;
				}
			}
		}
		return xMax;
	}
}
