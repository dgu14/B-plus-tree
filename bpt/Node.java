package bpt;
 public class Node {
	public int ndn;				// ���Ͽ� ������ �� ����� ��ȣ�� �ű�ϴ�. ����-���� ������ ��ȣ�� �Ű����� ��Ʈ�� 0�� ���� �����ϴ�.
	public Element pElem;		// �� ��带 child�� ������ �θ����� Element�� �����մϴ�.
	
	public int peln;			// ���Ͽ��� �а� ������ �� ���� ���� �����Դϴ�. 
	
	public Node()
	{
		pElem=null;
		ndn=-1;
	}
	
	public Node findNode(Data cKey)
	{
		return this;			// �������̵� �� �Լ���, cKey���� ���� ���� �ִ� ������带 ��ȯ�մϴ�. null�� ��ȯ�ϴ� ���� �����ϴ�.
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
		if(pElem==null) return this;	// ��Ʈ�� ã���� ��Ʈ�� ��ȯ.
		return pElem.host.findRoot();
	}
}