package bpt;
 public class Data {
	public int d;		// key
	public int rd;		// value
	
	public Data() {}
	public Data(int d) { this.d=d; }
	public Data(int d, int rd) { this.d=d; this.rd=rd; }
	public Data(Data data){ this.d=data.d; this.rd=data.rd; }
	
	public boolean isSmallerThan(Data data)
	{
		return this.d<data.d;
	}
	
	public boolean equal(Data data)
	{
		return this.d==data.d;
	}
}