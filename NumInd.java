/**
Dummy individuals. Simply a number whose value is 
the sum of 10 30-bit numbers (genes)
**/

public class NumInd{
	
	public static final int CHROMOSOME_SIZE = 10;
	public static final long NUMIND_MAX_FITNESS = CHROMOSOME_SIZE * Gene.GENE_MAX_VALUE;
	
	private long numIndFitness;
	private Gene[] chromosome;
	
	public String toString() {
		
		String s = "Num Ind:\n";
		for(int i = 0; i < chromosome.length; i++){
			Gene g = chromosome[i];
			s += "Gene " + i + ": " + g;
			s += "\n";
		}
		return s;
	}
	
	public NumInd() {
		
		chromosome = new Gene[CHROMOSOME_SIZE];
		for(int i = 0; i < chromosome.length; i++){
			chromosome[i] = new Gene();
		}
	}
	
	public void setGene(int index, NumInd parent){
		Gene g = new Gene(parent.chromosome[index]);
		chromosome[index] = g;
	}
	public void setNumIndFitness() {
		
		numIndFitness = 0;
		for(int i = 0; i < chromosome.length; i++){
			numIndFitness += chromosome[i].getValue();//add the gene values
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