public class Alg{
	
	final int NUMIND_POP_SIZE = 30;
	
	private long numIndTotalFitness;
	private long algFitness; 	//name generic things  like "fitness" according 
	private long numIndAvgFitness;	//to the class
	private NumInd[] numIndPop;		//to avoid confusion
	
	//Alg genes
	/****/
	
	
	
	/****/
	public Alg() {
		
		numIndPop = new NumInd[NUMIND_POP_SIZE];
		for(int i = 0; i < numIndPop.length; i++){
			numIndPop[i] = new NumInd();
		}
	}
	
	public Alg(Alg a) {
		
		numIndPop = a.copyNumIndPop();
	}
	
	public String toString() {
		
		String s = "Alg:\n";
		for(int i = 0; i < numIndPop.length; i++){
			NumInd n = numIndPop[i];
			s += "NumInd " + (i) + ": fitness = " + n.getNumIndFitness() + "\n";
		}
		return s;
	}
	
	public NumInd getNumInd(int index){
		return numIndPop[index];
	}
	
	public void setNumIndTotalFitness() {
		
		numIndTotalFitness = 0;
		for(int i = 0; i < numIndPop.length; i++){
			numIndTotalFitness += numIndPop[i].getNumIndFitness();
		}
	}
		
	public void setNumIndAvgFitness() {
		
		numIndAvgFitness = 0;
		setNumIndTotalFitness();
		numIndAvgFitness = numIndTotalFitness / NUMIND_POP_SIZE;
	}
	
	public NumInd[] copyNumIndPop() {
		
		NumInd[] copy = new NumInd[NUMIND_POP_SIZE];
		for(int i = 0; i < NUMIND_POP_SIZE; i++){
			copy[i] = numIndPop[i];
		}
		return copy;
	}
	
	public NumInd mate(int motInd, int fatInd) {
		NumInd mother = numIndPop[motInd];
		NumInd father = numIndPop[fatInd];
		NumInd child = new NumInd();
		for(int i = 0; i < child.CHROMOSOME_SIZE; i++){
			if((int) (2 * Math.random())==0){
				child.setNumIndGene(i, mother);//make the gene in child's chromosome 
							//at i the same as mother's at i 
			}				
			else{
				child.setNumIndGene(i, father);
			}
		}
		return child;
	}
	
	public void matingSeason() {
		
	}
	
	public static void main(String[] args) {
	/*	
		Alg a = new Alg();
		System.out.println(a);
		a.setNumIndAvgFitness();
		System.out.println(a.numIndAvgFitness + " " + NumInd.NUMIND_MAX_FITNESS);
		System.out.println((double)NumInd.NUMIND_MAX_FITNESS / a.numIndAvgFitness);
		System.out.println(a.numIndPop[0]); */
		
		Alg a = new Alg();
		System.out.println(a);
		NumInd kid = a.mate(0, 1);
		System.out.println(a.getNumInd(0));
		System.out.println(a.getNumInd(1));
		System.out.println(kid);
	}
	
}