package solitiare;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainMethod {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SolitaireWindow window;
		boolean testMode =false;
		if(args.length>0) {
			testMode = Boolean.valueOf(args[0]);
		}
		
		window = new SolitaireWindow(testMode);
		if(args.length>1) {
			String script = args[1];
			File file = new File(script);
			try {
				URL url = file.toURI().toURL();
				ScriptReader scriptReader = new ScriptReader(url.openStream());
				scriptReader.play(window);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		window.setVisible(true);
	}

}
