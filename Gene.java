public class Gene{
	
	public static final int GENE_SIZE =  30;//bits (30)
	private final int TRUE_RATE = 15; //1 out of every TRUE_RATE bits will be true
	public static final long GENE_MAX_VALUE = (long)(Math.pow(2, GENE_SIZE) - 1);
	
	boolean[] genotype;
	private long value;
	
	public Gene(){
		
		genotype = new boolean[GENE_SIZE];
		for(int i = 0; i < genotype.length; i++){
			if((int) (TRUE_RATE*Math.random()) == 0){
				genotype[i] = true;
			}
			else{
				genotype[i] = false;
			}
		}
		setValue();
	}
	
	public Gene(Gene g){
		for(int i = 0; i < genotype.length; i++){
			genotype[i] = g.genotype[i];
		}
		setValue();
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
	
	public long getValue(){
		
		return value;
	}
	
	public static void main(String[] args){
		
		Gene g = new Gene();
		System.out.println(g);
	}
	
}