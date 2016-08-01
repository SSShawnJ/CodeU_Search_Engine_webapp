package resources;

public class TFIDF {
	private static final double k1=1.5;
	private static final double b=0.75;
	
	
	/**
	 * Get the Okapi BM25 implementation of TF term frequency double value
	 * 
	 * @param termCount: the number of times that term t occurs in document
	 * @param avg: the average document length in the text collection 
	 * @param D: the length of the document D in words
	 * @return
	 */
	public static double TF(int termCount,double avg,int D){
		return (termCount*(k1+1))/(termCount+k1*(1-b+b*D/avg));
	}
		
	
	/**
	 * Get the Okapi BM25 implementation of IDF inverse document frequency double value
	 * 
	 * @param N: total number of documents in the corpus
	 * @param d: number of documents where the term appears
	 * @return
	 */
	public static double IDF(int N,int d){
		return Math.log((N-d+0.5)/(d+0.5));
	}
}
