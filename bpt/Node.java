package bpt;
 public class Node {
	public int ndn;
	public Element pElem;
	
	public int peln;
	
	public Node()
	{
		pElem=null;
		ndn=-1;
	}
	
	public Node findNode(Data cKey)
	{
		return this;
	}
	
	public Node getLinkedList()
	{
		return this;
	}
	
	public String convertFileFormat()
	{
		return null;
	}
	
	public void print() {}
	public void printSearchPath(int key) {}
	public Node findRoot()
	{
		if(pElem==null) return this;
		return pElem.host.findRoot();
	}
}