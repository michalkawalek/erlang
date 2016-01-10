import java.io.*;

public class Zapis {
	public static void main(String args[]) {
		
		PrintWriter zapis;
	
		try {
			zapis = new PrintWriter("/home/galaisius/erlang/fetjaba-post-1/atomy.txt");
			zapis.println("nic");
			zapis.close();
			System.out.println("dzialaj");
		}
		catch(Exception e) {

		}
		

	}
}
