import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Test {
	private static String user;
	private static String pass;
	private static String name;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		user = "";
		pass = "";
		name = "";
		
		try {
			Socket socket = new Socket("irc.twitch.tv",6667);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			
			writer.write("PASS " + pass + "\r\n");
			writer.write("NICK " + user + "\r\n");
			writer.write("JOIN\r\n");
			writer.flush();
			
			writer.write("CAP REQ :twitch.tv/tags twitch.tv/commands twitch.tv/membership\r\n");
			writer.flush();
			
			String line;
			while ((line = reader.readLine()) != null){
				System.out.println(line);
				String[] parts = line.split(" ");
				if(parts[0].equals("PING")){
					System.out.println("Replying to pong");
					writer.write("PONG irc.twitch.tv" + "\r\n");
					writer.flush();
				} else if(parts[2].equals("PRIVMSG")){
					if(parts[3].equals("#")){
						if (parts[3].substring(1).equals("!song")){
							writer.write("PRIVMSG # :Song: " + readFile(new File(name))+"\r\n");
							writer.flush();
						}else if (parts[4].substring(1).equals("!bot")){
							writer.write("PRIVMSG # :Hello, I am your friendly neighbourhood Bot\r\n");
							writer.flush();
						}
					}
				}
			}
			socket.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static String readFile(File file) {
        try {
                FileInputStream reader = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                reader.read(data);
                reader.close();
                System.out.println("Read from file: " + new String(data, "UTF-8"));
                return new String(data, "UTF-8");
        } catch (IOException e) {
            System.err.println("An IOException was caught!");
                e.printStackTrace();
        }
        return "Cannot read the song file!";
}

}
