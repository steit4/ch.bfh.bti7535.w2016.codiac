package exercise1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Tfd
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/Hamlet.txt"));
		
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		int[] occurences = new int[26];
		
		while(reader.ready())
		{
			char currentChar = (char)Character.toLowerCase(reader.read());
			
			if(alphabet.indexOf(currentChar) != -1)
			{
				occurences[alphabet.indexOf(currentChar)]++;
			}
		}
		
		for(int i = 0; i < alphabet.length(); i++)
		{
			System.out.println(i + " " + alphabet.charAt(i) + " " + occurences[i]);
		}
	        
	}
}
