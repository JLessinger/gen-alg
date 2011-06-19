/**
Dummy individuals. Simply a number whose value is 
the sum of 10 20-bit numbers (genes)
**/

public class NumInd{
	
	public static final int NUMIND_CHROMOSOME_SIZE = 10;
	public static final int NUMIND_GENE_SIZE = 20;//bits
	public static final int NUMIND_TRUE_RATE = 10; //1 out of every NUMIND_TRUE_RATE bits will be true
	public static final long GENE_MAX_VALUE = (long)(Math.pow(2, NUMIND_GENE_SIZE) - 1);
	
	public static final long NUMIND_MAX_FITNESS = NUMIND_CHROMOSOME_SIZE * GENE_MAX_VALUE;
 
	
	private long numIndFitness;
	private Gene[] numIndChromosome;
	
	public String toString() {
		setNumIndFitness();
		String s = "Num Ind:\n";
		s += "Fitness = " + numIndFitness + "\n";
		for(int i = 0; i < numIndChromosome.length; i++){
			Gene g = numIndChromosome[i];
			s += "Gene " + i + ": " + g;
			s += "\n";
		}
		return s;
	}
	
	public NumInd() {
		
		numIndChromosome = new Gene[NUMIND_CHROMOSOME_SIZE];
		for(int i = 0; i < numIndChromosome.length; i++){
			numIndChromosome[i] = new Gene(NUMIND_GENE_SIZE, NUMIND_TRUE_RATE);
		}
	}
	
	public NumInd(NumInd other){
		numIndChromosome = new Gene[NUMIND_CHROMOSOME_SIZE];
		for(int i = 0; i < NUMIND_CHROMOSOME_SIZE; i++){
			numIndChromosome[i] = new Gene(other.getNumIndGene(i));
		}
	}
	
	public Gene getNumIndGene(int index) {
		
		return numIndChromosome[index];
	}
	

	public void setNumIndGene(int index, NumInd parent) {
		
		numIndChromosome[index] = new Gene(parent.numIndChromosome[index]);
	}
	
	public void setNumIndFitness() {
		
		numIndFitness = 0;
		for(int i = 0; i < numIndChromosome.length; i++){
			numIndFitness += numIndChromosome[i].getValue();//add the gene values
		}
	}
	
	
	public long getNumIndFitness() {
		
		setNumIndFitness();
		return numIndFitness;
	}

	//Mutates the calling NumInd
	public void numIndMutate(double chromosomeRate, double geneRate){
		for(int i = 0; i < NUMIND_CHROMOSOME_SIZE; i++){
			if(Math.random() < chromosomeRate){
				numIndChromosome[i].geneMutate(geneRate);
			}
		}
	}
	
	public static void main(String[] args) {
		
		NumInd n = new NumInd();
		System.out.println(n);
	}
	
}