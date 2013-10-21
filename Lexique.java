package lo17;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Classe qui gére toute la corespondance des mots et des lemmes. Elle permet de trouver
 * le lemme correspondant à un mot selon plusieurs critéres de distances.
 * Elle utilise une hashtable pour faire associer un mot à un lemme.
 * 
 * @author Hugo
 *
 */
public class Lexique {
	
	/**
	 * Hastable où est stockée le tableau d'association mot -> lemmes
	 * @see Lexique#Lexique(String)
	 * @see Lexique#getLemme(String)
	 * @see Lexique#getPrefixe(String)
	 */
	private Hashtable<String, String> table;
	
	/**
	 * Seuil Minimal pour l'algorithme du préfixe
	 * @see Lexique#getPrefixe(String)
	 */
    private static final int SEUILMIN = 2;
    
	/**
	 * Seuil Maximal pour l'algorithme du préfixe
	 * @see Lexique#getPrefixe(String)
	 */
    private static final int SEUILMAX = 4;
    
    /**
     * Distance de lenvenshtein maximale entre deux mots
     * @see Lexique#getLeven(String)
     */
    private static final int DISTMAXLEVEN = 2;
    
    /**
     * Proximité minimale entre deux mot pour l'algorithme du préfixe
     * @see Lexique#getPrefixe(String)
     */
    private static final int PROXIPREFI = 80;
    
    /**
     * Constructeur de la classe Lexique
     * 
     * @param file 	
     * 			Correspond au fichier de mot/lemme à utiliser
     */
	public Lexique(String file) {
		table = new Hashtable<String, String>();
		BufferedReader br= null;
		
		try{
			try{
				String chaine;
			    String[] result;
				File fichier = new File(file);
		        br = new BufferedReader(new FileReader(fichier));
		        while ((chaine=br.readLine())!=null){
		        	result=chaine.split("\\s\\s");
		        	table.put(result[0].toLowerCase(), result[1].toLowerCase());
		   
		        }
			}
	        catch(EOFException e) {
	            br.close();
	            }
		}
		catch(FileNotFoundException e) {
	          System.out.println("fichier inconnu : " + file);
	          } 
	    catch(IOException e) {
	          System.out.println("IO Exception");
	          }    	

	}
	
	/**
	 * Récupérer le lemme associé à un mot donné en argument
	 * 
	 * @param mot Correspond au mot à lématiser
	 * @return Le lemme correspondnant sous forme d'une chaine de caractére
	 */
	String getLemme(String mot){
		return (String) table.get(mot);
	}
	
	/**
	 * Récupérer le lemme associé à un mot au sens de l'algorithme du préfixe
	 * @see Lexique#getProximite(String, String)
	 * @param mot Correspond au mot à lématiser
	 * @return Le lemme le plus proche du mot donné au sens du préfixe ou null  si inexistant
	 */
	String[] getPrefixe(String mot){
		int i, j, nbMots;
		//création du tableau de promiscuité de la taille du hashtable
		float[] promiscuity=new float [table.size()];
		//récupération des clé(mots) du hashtable dans un array
		String[] wordDictionary=table.keySet().toArray(new String[0]);
		//calcul de la promiscuité pour chaque élément
		for(i = 0; i < promiscuity.length ; i++){
			promiscuity[i]= this.getProximite(mot, wordDictionary[i]);
		}
		
		//tri a bulle sur le tableau de promiscuité avec répercussion sur le tableau des mots
		for (i = 0 ; i < (promiscuity.length-1) ; i++)
            for (j = (promiscuity.length-1) ;i < j ; j-- )
                    if (promiscuity[j] > promiscuity[j-1])
                    {
                            float tmp = promiscuity[j-1];
                            promiscuity[j-1]=promiscuity[j];
                            promiscuity[j]=tmp;
                            
                            String var = wordDictionary[j-1];
                            wordDictionary[j-1]=wordDictionary[j];
                            wordDictionary[j]=var;
                            
                    }
		//retour du lemme correspondant au mot avec prox > 80
		//if(promiscuity[0] > 80)	return table.get(wordDictionary[0]).toString();
		
		//regroupe les mots avec une proximité supérieure à PROXIPREFI
		//Mots satisfaisant la proximité
		
		nbMots=0;
		while(promiscuity[nbMots] >= PROXIPREFI){
			nbMots++;
		}
		if(nbMots==0) return null;
		else{
			//tableau de mots satisfaisant PROXIPREFI
			String[] MotsResult=new String[nbMots];
			System.arraycopy(wordDictionary, 0, MotsResult, 0, nbMots);
			//tableau de lemmes uniques
			String[] result = this.findUniqLemme(MotsResult);
			return result;
		}	
	}
	
	/**
	 * Récupérer les lemmes de maniére unique d'un tableau de mots
	 * @param mots Tableau de mots à lématiser
	 * @return Un tableau de lemmes uniques correspondant au tableau de mots
	 */
	String[] findUniqLemme(String[] mots){
		if(mots==null) //si pointeur null, retoune null
			return null;
		else{//sinon on récupére les lemmes associès aux mots
			String[] tabTmp = new String[mots.length];
			for(int i = 0; i < mots.length; i++){
				tabTmp[i] = this.table.get(mots[i].toString());
			}//end for
			//on recupére les lemmes uniques
			Set<String> hashTemp = new HashSet<String>(Arrays.asList(tabTmp));
			String[] result = hashTemp.toArray(new String[hashTemp.size()]);
			return result;//tableau de lemmes uniques
			
		}//end else
		
	}//end findUniqLemme
	
	/**
	 * Retourne la proximité entre deux mots
	 * @see Lexique#getPrefixe(String)
	 * @param mot1 Mot à comparer
	 * @param mot2 Mot à comparer
	 * @return La proximité entre les deux mots, sous la forme d'un float 
	 */
	private float getProximite(String mot1, String mot2) {
		float prox = 0;
		if (mot1.length() < SEUILMIN || mot2.length() < SEUILMAX) {
			return 0;
		} 
		else if (Math.abs(mot1.length() - mot2.length()) > SEUILMAX) {
			return 0;
		}
		else{
			int i = 0;
			while((i < Math.min(mot1.length(), mot2.length()) && (mot1.charAt(i) == mot2.charAt(i)))){
				i++;
				prox = (((float) i)/((float) Math.max(mot1.length(), mot2.length())))*100;
			}
			return prox;
		}
		
	}
	
	/**
	 * Cherche le meilleur lemme entre le mot donné en argument et le fichier de mot-lemme
	 * @param mot Mot à lématiser
	 * @return Le lemme correspondnat au mot ou null
	 */
	String[] getLeven(String mot){
		int i, j, nbMots;
		//création du tableau de promiscuité de la taille du hashtable
		int[] promiscuity=new int [table.size()];
		//récupération des clé(mots) du hashtable dans un array
		String[] wordDictionary=table.keySet().toArray(new String[0]);
		//calcul de la distance de lenvenshtein pour chaque élément
		for(i = 0; i < promiscuity.length ; i++){
			promiscuity[i]= this.getLevenDist(mot, wordDictionary[i]);
		}
		
		//tri a bulle sur le tableau de promiscuité avec répercussion sur le tableau des mots
		for (i = 0 ; i < (promiscuity.length-1) ; i++)
            for (j = (promiscuity.length-1) ;i < j ; j-- )
                    if (promiscuity[j] < promiscuity[j-1])
                    {
                            int tmp = promiscuity[j-1];
                            promiscuity[j-1]=promiscuity[j];
                            promiscuity[j]=tmp;
                            
                            String var = wordDictionary[j-1];
                            wordDictionary[j-1]=wordDictionary[j];
                            wordDictionary[j]=var;
                            
                    }
		//retour du lemme correspondant à une distance de lenvenshtein 
		//if(promiscuity[0] < DISTMAXLEVEN)	return table.get(wordDictionary[0]).toString();
		//else return null;
		
		//Mots satisfaisant la distance de lenvshtein
		nbMots=0;
		while(promiscuity[nbMots] <= DISTMAXLEVEN){
			nbMots++;
		}
		if(nbMots==0) return null;
		else{
			//tableau de mots satisfaisant PROXIPREFI
			String[] MotsResult=new String[nbMots];
			System.arraycopy(wordDictionary, 0, MotsResult, 0, nbMots);
			//tableau de lemmes uniques
			String[] result = this.findUniqLemme(MotsResult);
			return result;
		}	
			
	}
	
	/**
	 * Calcule la distance de Levenshtein entre deux mots
	 * @param mot1 Mot à comparer
	 * @param mot2 Mot à comparer
	 * @return La distance de levenshtein entre les deux mots, sous forme d'entier
	 */
	int getLevenDist(String mot1, String mot2){
		int[] [] dist = new int [mot1.length() +1 ] [mot2.length() + 1];
		int dist1, dist2, dist3;
		for(int i = 0; i <= mot1.length(); i++){
			dist[i][0] = i;
		}
		for(int j = 0; j <= mot2.length(); j++){
			dist[0][j] = j;
		}
		for(int i = 1; i <= mot1.length(); i++){
			for(int j = 1; j <= mot2.length(); j++){
					if(mot1.charAt(i - 1) == mot2.charAt(j - 1))  dist1 = dist[i-1][j-1] + 0;
					else dist1 = dist[i-1][j-1] + 1;
				 dist2 = dist[i-1][j] + 1;
				 dist3 = dist[i][j-1] + 1;
				 dist[i][j] = minimum(dist1, dist2, dist3);
			}
		}
		return dist[mot1.length()][mot2.length()];
	}
	
	/**
	 * Retourne le minimum entre trois entier
	 * @param a
	 * @param b
	 * @param c
	 * @return Le minimum entre a, b et c
	 */
	private int minimum(int a, int b, int c){
		return Math.min(Math.min(a,b), c);
	}
	
}//end class
	
