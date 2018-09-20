package bpt;
 public class Node {
	public Element pElem;
	
	public Node()
	{
		pElem=null;
	}
	
	public Node findNode(Data cKey)
	{
		return this;
	}
	
	public Node getLinkedList()
	{
		return this;
	}
	
	public void print() {}
	public Node findRoot()
	{
		if(pElem==null) return this;
		return pElem.host.findRoot();
	}
}