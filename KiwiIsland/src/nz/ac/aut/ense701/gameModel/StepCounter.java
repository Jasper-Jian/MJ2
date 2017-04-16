
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nz.ac.aut.ense701.gameModel;

/**
 *
 * @author Administrator
 */
public class StepCounter {
    private int step;
    private static final StepCounter SC = new StepCounter();
    private StepCounter(){
    }
    public static StepCounter getSingleTon(){
        return SC;
    }
    public void setStep(int step){
        this.step = step;
    }
    public int getStep(){
    	return this.step;
    }
    public void addStep(){
        this.step++;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + step;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StepCounter other = (StepCounter) obj;
		if (step != other.step)
			return false;
		return true;
	}
    
    
}
