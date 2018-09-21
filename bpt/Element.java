package bpt;

public class Element {
	public int eln;				// 파일 입출력을 할 때 Element의 번호 수.
	public int pos;				// 해당 Element가 host노드에서 몇 번째로 위치하는 지
	public Data k;				
	public Node c;
	public BptNode host;		// 해당 Element를 가지는 노드의 정보 host.
	
	public int cndn;			
	public int hndn;
	
	// BptNode에 대한 정보를 파일로 저장할 때, 다음과 같은 포맷으로 string을 만들어서 저장합니다.
	public String convertFileFormat()
	{
		return "ELEMENT " + eln + " " + (host!=null?host.ndn:-1) + " " + pos + " " + (k!=null?k.d:0) + " " + (k!=null?k.rd:0) + " " + (c!=null?c.ndn:-1);   
	}
	
	Element() { this.c=null; this.host=null; this.k=null; this.pos=0; this.cndn=-1; this.hndn=-1; }
	Element(Data k, Node c) { this.k=k; this.c=c; this.host=null; this.pos=0; this.cndn=-1; this.hndn=-1; }
}