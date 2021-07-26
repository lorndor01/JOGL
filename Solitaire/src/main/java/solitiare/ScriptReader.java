package solitiare;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;


public class ScriptReader {
	private InputStream inputStream;
	private KeyStrokeMapper mapper;
	private SolitaireWindow window;
	public ScriptReader(InputStream inputStream) throws MalformedURLException {
		this.inputStream = inputStream;
		mapper = new KeyStrokeMapper();
	}
	public void play(SolitaireWindow window) {
		this.window = window;
		window.setVisible(true);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		try {
			while((line = br.readLine())!=null) {
				if(!line.startsWith("#")) {
					String[] commands = line.split(" ");
					for(String command : commands) {
						if(command.length()==2) {
							try {
								int numberOfTimes = Integer.parseInt(String.valueOf(command.charAt(0)));
								mapConsecutiveCommands(numberOfTimes, command.charAt(1));
							}
							catch(NumberFormatException e) {
								if(command.charAt(0)=='t') {
									mapLengthTwoCommmand(command.charAt(0), command.charAt(1));
								}
								if(command.charAt(0)=='a') {
									mapLengthTwoCommmand(command.charAt(0), Integer.parseInt(String.valueOf(command.charAt(1))));
								}
							}
						}
						else if(command.length()==4) {
							char key = command.charAt(0);
							char source = command.charAt(1);
							char deno = command.charAt(2);
							char destination = command.charAt(3);
							mapLengthFourCommand(key, source, deno, destination);
						}
						else {
							mapSingleCommand(command.charAt(0));
						}
					}
				}
			}
		} 
		//is thrown in the form loop if first character in command isn't an integer.
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Allows the same command to be executed multiple times by writing in the script file
	 * ic where i = the number of times you want the command to be executed and c = the command.
	 * @param numberOfTimes number of times the command should be executed.
	 * @param command the command.
	 */
	public void mapConsecutiveCommands(int numberOfTimes, char command) {
		int i = 0;
		while(i<numberOfTimes) {
			mapSingleCommand(command);
			i++;
		}
	}
	
	/**
	 * Maps a single command.
	 * @param command the command.
	 */
	public void mapSingleCommand(char command) {
		if(command == 'd' || command == 'D') {
			mapper.mapFirstKeyStroke(command, window);
		}
		
		if(command == '*') {
			mapper.mapFirstKeyStroke(command, window);
		}
		if(command == 'A') {
			mapper.mapFirstKeyStroke(command, window);
		}
	}
	
	/**
	 * Maps a command that is represented by multiple characters.
	 * @param key the second character of the command.
	 * @param pileNumber the number of the pile on switch the action should be performed.
	 */
	public void mapLengthTwoCommmand(char key, int pileNumber) {
		if(key == 't') {
			mapper.mapFirstKeyStroke((char) pileNumber, window);
		}
		if(key == 'a') {
			mapper.mapSecondKeyStroke((char)pileNumber, window);
		}
	}
	
	public void mapLengthFourCommand(char key, char source, char startingCard, char destinationPile) {
		if(key == 'm') {
			mapper.mapFirstKeyStroke(key, window);
			mapper.mapSecondKeyStroke(source, window);
			mapper.mapThirdKeyStroke(startingCard, window);
			mapper.mapFourthKeyStroke(destinationPile, window);
		}
	}
}
