public class Gene{
	
	private long maxValue;
	private boolean[] genotype;
	private long value;
	
	public Gene(int size, int trueRate) {
		
		maxValue = (long)Math.pow(2, size)-1;
		genotype = new boolean[size];
		for(int i = 0; i < genotype.length; i++){
			if((int) (trueRate*Math.random()) == 0){
				genotype[i] = true;
			}
			else{
				genotype[i] = false;
			}
		}
		setValue();
	}
	
	public Gene(Gene g) {
		
		genotype = new boolean[g.getGenotype().length];
		System.arraycopy(g.getGenotype(), 0, genotype, 0, g.getGenotype().length); 
		setValue();
	}
	
	public boolean[] getGenotype(){
		return genotype;
	}
	
	public long getMaxValue(){
		return maxValue;
	}
	
	public String toString() {
		
		setValue();
		String s = "" + value + " ";
		for(boolean b: genotype){
			if(b)
				s+="1";
			else
				s+="0";
			//s += " ";
		}
		return s;
	}
	
	public void setValue() {
		
		value = 0;
		for(int i = 0; i < genotype.length; i++){
			if(genotype[i]){
				value += Math.pow(2, (genotype.length - i - 1));
			}
		}
	}
	
	public void geneMutate(double geneRate) {
		
		for(int i = 0; i < genotype.length; i++){
			if(Math.random() < geneRate){
				genotype[i] = !genotype[i];
			}
		}
	}
	
	//unnecessary stuff
	/*public boolean getBit(int index) {
	
		return genotype[index];
	}
	
	public void setBit(int index, boolean value){
		genotype[index] = value;
	}*/
	
	public long getValue(){
		
		return value;
	}
	
	public static void main(String[] args){
		
		//Gene g = new Gene();
		//System.out.println(g);
	}
	
}