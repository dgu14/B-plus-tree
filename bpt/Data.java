package bpt;
 public class Data {
	public int d;
	
	public Data() {}
	public Data(int d) { this.d=d; }
	public Data(Data data){ this.d=data.d; }
	
	public boolean isSmallerThan(Data data)
	{
		return this.d<data.d;
	}
	
	public boolean equal(Data data)
	{
		return this.d==data.d;
	}
}