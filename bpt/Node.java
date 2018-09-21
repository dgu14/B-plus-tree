package bpt;
 public class Node {
	public int ndn;				// 파일에 저장할 때 노드의 번호를 매깁니다. 프리-오더 순으로 번호가 매겨져서 루트는 0의 값을 가집니다.
	public Element pElem;		// 현 노드를 child로 가지는 부모노드의 Element를 저장합니다.
	
	public int peln;			// 파일에서 읽고 저장할 때 쓰는 보조 변수입니다. 
	
	public Node()
	{
		pElem=null;
		ndn=-1;
	}
	
	public Node findNode(Data cKey)
	{
		return this;			// 오버라이딩 된 함수로, cKey값이 있을 수도 있는 리프노드를 반환합니다. null을 반환하는 경우는 없습니다.
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
		if(pElem==null) return this;	// 루트를 찾으면 루트를 반환.
		return pElem.host.findRoot();
	}
}