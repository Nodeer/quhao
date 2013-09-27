package vo;

public class PaiduiVO implements Comparable{
	public int numberOfSeat = 0;;
	
	public int currentNumber = 0;
	public int maxNumber = 0;
	public int canceled = 0;
	public int expired = 0;
	public int finished = 0;
	public boolean enable = false;
	
	@Override
	public int compareTo(Object o) {
		return this.numberOfSeat - ((PaiduiVO) o).numberOfSeat;
	}
}