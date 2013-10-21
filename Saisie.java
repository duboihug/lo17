package lo17;

import java.io.*;

class Saisie{
     public static String newSaisie() {
     BufferedReader br=null;
     String chaine=null;
     try {
          try {
              br = new BufferedReader(new InputStreamReader(System.in));
              System.out.print("saisie : ");
              chaine=br.readLine();
               } 
          catch(EOFException e) {
               br.close();
               }
          } 
     catch(IOException e) {
          System.out.println("IO Exception ");
          }
	return chaine;
     }
}