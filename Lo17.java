package lo17;
import java.util.Arrays;
import java.util.StringTokenizer;

import lo17.Lexique;
import lo17.Saisie;

public class Lo17 {
		public static void main(String[] args){
		String chaine, lemme, mot;
		String[] tabLemme;
		//cr�ation du lexique avec le fichier
		Lexique lex = new Lexique("lexique");
		
		//capture de la saisie utilisateur
		chaine = Saisie.newSaisie().toLowerCase();
		
		//r�cup�ration des mots de la saisie
		StringTokenizer st = new StringTokenizer(chaine);
		
		//traitement des mots
	    while (st.hasMoreTokens()) {
	    	mot = st.nextToken();
	        System.out.println("Mot e lematiser : " + mot );
			if((lemme=lex.getLemme(mot))!=null){//si le mot est directement dans la liste
				System.out.println("Le lemme correspondant � la recherche : " + lemme);
			}
			else if((tabLemme=lex.getPrefixe(mot))!=null){//si le mot est dans la liste � un pr�fixe pr�s
				System.out.println("Le lemme correspondant � la recherche (prefixe) : " + Arrays.asList(tabLemme));
			}
			else if((tabLemme=lex.getLeven(mot))!=null){//si le mot est pr�sent au sens de la distance de levenshtein
				System.out.println("Le lemme correspondant � la recherche (lenvenshtein) : " + Arrays.asList(tabLemme));
			}//sinon le mot n'existe pas en base
			else System.out.println("Le mot " + mot + " n'existe pas dans la base de donnees");
	    }    
		}

}
