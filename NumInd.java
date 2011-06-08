public class NumInd{
	final int CHROMOSOME_SIZE = 100;
	Gene[] chromosome;
	
	public NumInd(){
		chromosome = new Gene[CHROMOSOME_SIZE];
		for(int i = 0; i < chromosome.length; i++){
			chromosome[i] = new Gene();
		}
	}
}