public class Gene{
	final int GENE_SIZE = 5;
	boolean[] genotype;
	int value;
	
	public Gene(){
		genotype = new boolean[GENE_SIZE];
		for(int i = 0; i < genotype.length; i++){
			if((int) (2*Math.random()) == 0){
				genotype[i] = false;
			}
			else{
				genotype[i] = true;
			}
		}
		setValue();
	}
	
	public String toString(){
		String s = "";
		for(boolean b: genotype){
			s += b;
			s += " ";
		}
		setValue();
		s += value;
		return s;
	}
	
	private void setValue(){
		for(int i = 0; i < genotype.length; i++){
			if(genotype[i]){
				value += Math.pow(2, genotype.length - i - 1);
			}
		}
	}
	
	public static void main(String[] args){
		Gene g = new Gene();
		System.out.println(g);
	}
}