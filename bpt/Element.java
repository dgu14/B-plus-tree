package bpt;

public class Element {
	public int eln;
	public int pos;
	public Data k;
	public Node c;
	public BptNode host;
	
	public int cndn;
	public int hndn;
	
	public String convertFileFormat()
	{
		return "ELEMENT " + eln + " " + (host!=null?host.ndn:-1) + " " + pos + " " + (k!=null?k.d:0) + " " + (k!=null?k.rd:0) + " " + (c!=null?c.ndn:-1);   
	}
	
	Element() { this.c=null; this.host=null; this.k=null; this.pos=0; this.cndn=-1; this.hndn=-1; }
	Element(Data k, Node c) { this.k=k; this.c=c; this.host=null; this.pos=0; this.cndn=-1; this.hndn=-1; }
}