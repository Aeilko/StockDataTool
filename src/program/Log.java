package program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Log {
	private String file;
	
	public Log(String file){
		this.file = file;
	}
	
	public void write(String data){
		try{
			data += System.lineSeparator();
			Files.write(Paths.get(this.file), data.getBytes(), StandardOpenOption.APPEND);
		}
		catch(IOException e){ System.err.println("Can't write to log file"); }
	}
}