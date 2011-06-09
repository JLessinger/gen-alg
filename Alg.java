public class Alg{
	final int NUMIND_POP_SIZE = 30;
	private long numIndTotalFitness;
	private long algFitness; 	//name generic things  like "fitness" according 
	private long numIndAvgFitness;	//to the class
	NumInd[] numIndPop;		//to avoid confusion
	
	public Alg(){
		numIndPop = new NumInd[NUMIND_POP_SIZE];
		for(int i = 0; i < numIndPop.length; i++){
			numIndPop[i] = new NumInd();
		}
	}
	
	public String toString(){
		String s = "Alg:\n";
		for(int i = 0; i < numIndPop.length; i++){
			NumInd n = numIndPop[i];
			s += "NumInd " + (i) + ": " + "fitness = " + n.getNumIndFitness();
			s += "\n";
		}
		return s;
	}
	
	public void setNumIndTotalFitness(){
		numIndTotalFitness = 0;
		for(int i = 0; i < numIndPop.length; i++){
			numIndTotalFitness += numIndPop[i].getNumIndFitness();
		}
	}
		
	public void setNumIndAvgFitness(){
		numIndAvgFitness = 0;
		setNumIndTotalFitness();
		numIndAvgFitness = numIndTotalFitness / NUMIND_POP_SIZE;
	}
	
	public static void main(String[] args){
		Alg a = new Alg();
		System.out.println(a);
		a.setNumIndAvgFitness();
		System.out.println(a.numIndAvgFitness + " " + NumInd.NUMIND_MAX_FITNESS);
		System.out.println((double)NumInd.NUMIND_MAX_FITNESS / a.numIndAvgFitness);
		System.out.println(a.numIndPop[0]);
	}
}