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
		
		String s = "Num Ind:\n";
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
	
	public Gene getGene(int index) {
		
		return numIndChromosome[index];
	}
	
	public void setGene(int index, NumInd parent, int crossover, NumInd parentTwo) {
		
		numIndChromosome[index] = new Gene(NUMIND_CHROMOSOME_SIZE, 0);
		if (crossover < 0) {
			crossover = 0;
		}
		for(int i = 0; i < crossover; i++) {//also, 
			numIndChromosome[index].setBit(i, parent.getGene(index).getBit(i));
		}
		for (int j = crossover; j < NUMIND_GENE_SIZE; j++) {
			numIndChromosome[index].setBit(j, parent.getGene(index).getBit(j));
		}
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

	public static void main(String[] args) {
		
		NumInd n = new NumInd();
		System.out.println(n);
	}
	
}