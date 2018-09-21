package bpt;

public class Element {
	public int eln;				// ���� ������� �� �� Element�� ��ȣ ��.
	public int pos;				// �ش� Element�� host��忡�� �� ��°�� ��ġ�ϴ� ��
	public Data k;				
	public Node c;
	public BptNode host;		// �ش� Element�� ������ ����� ���� host.
	
	public int cndn;			
	public int hndn;
	
	// BptNode�� ���� ������ ���Ϸ� ������ ��, ������ ���� �������� string�� ���� �����մϴ�.
	public String convertFileFormat()
	{
		return "ELEMENT " + eln + " " + (host!=null?host.ndn:-1) + " " + pos + " " + (k!=null?k.d:0) + " " + (k!=null?k.rd:0) + " " + (c!=null?c.ndn:-1);   
	}
	
	Element() { this.c=null; this.host=null; this.k=null; this.pos=0; this.cndn=-1; this.hndn=-1; }
	Element(Data k, Node c) { this.k=k; this.c=c; this.host=null; this.pos=0; this.cndn=-1; this.hndn=-1; }
}