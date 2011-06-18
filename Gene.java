public class Gene{
	
	private boolean[] genotype;
	private long value;
	
	public Gene(int size, int trueRate){
		
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
	
	public Gene(Gene g){
		System.arraycopy(g.getGenotype(), 0, genotype, 0, g.getGenotype().length); 
		setValue();
	}
	
	public boolean[] getGenotype(){
		return genotype;
	}
	
	public String toString(){
		
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
	
	private void setValue(){
		
		value = 0;
		for(int i = 0; i < genotype.length; i++){
			if(genotype[i]){     
				value += Math.pow(2, (genotype.length - i - 1));
			}
		}
	}
	
	public boolean getBit(int index) {
		
		return genotype[index];
	}
	
	public void setBit(int index, boolean value){
		
		genotype[index] = value; 
	}
	
	public long getValue(){
		
		return value;
	}
	
	public static void main(String[] args){
		
		//Gene g = new Gene();
		//System.out.println(g);
	}
	
}