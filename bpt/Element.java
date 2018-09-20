package bpt;
 public class Element {
	public int pos;
	public Data k;
	public Node c;
	public BptNode host;
	
	Element() { this.c=null; this.host=null; this.k=null; this.pos=0; }
	Element(Data k, Node c) { this.k=k; this.c=c; this.host=null; this.pos=0; }
}