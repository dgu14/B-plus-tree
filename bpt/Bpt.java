package bpt;
 public class Bpt {
	public static int m=5;
	public Node root;
	
	public Bpt()
	{
		root=null;
	}
	
	static void setChild(Element a, Node b)
	{
		a.c=b;
		b.pElem=a;
	}
	
	public void insert(Data data)
	{
		if(root==null)
		{
			root=new BptDNode(); 					// 데이터가 처음 들어가는 경우는 리프노드이다.
			((BptDNode)root).insertData(data);		
		}
		else
		{
			BptDNode cNode=(BptDNode)(root.findNode(data));	// 이미 트리가 존재하는 경우라면 data가 들어갈 위치를 찾고
			root=cNode.insertData(data);					// 데이터를 삽입한 다음, 루트가 변했을 수도 있으니 새로운 루트로 변경한다.
		}
	}
	
	public static int binary_search(BptDNode nd, int key)
	{
		int lo=0, hi=nd.dsz-1, mid, ret=-1;
		while(lo<=hi)
		{
			mid=(lo+hi)>>1;
			if(nd.ds[mid].d==key) { ret=mid; break; }
			else if(nd.ds[mid].d>key) hi=mid-1;
			else lo=mid+1;
		}

		return ret;
	}
	
	public void delete(Data data)
	{
		if(root==null) return;			// 트리가 존재하지 않는 경우
		
		BptDNode dNode=(BptDNode) (root.findNode(data));
		
		int po = binary_search(dNode, data.d);
		if(po==-1) return ;				// 삭제할 원소가 없는 경우
		
		root=dNode.deleteData(po);
	}

	public void printSearchPath(int key)
	{
		// -s 연산 지원
		if(root!=null) root.printSearchPath(key);
	}
	
	public Node getLinkedList()
	{
		// 디버그 용
		if(root!=null) return root.getLinkedList();
		return null;
	}
	
	public void print()
	{
		// 디버그용 
		if(root!=null) root.print();
		System.out.println();
	}
	
	public void printLinkedList()
	{
		// 디버그용
		BptDNode list=(BptDNode)getLinkedList();
		
		while(list!=null)
		{
			list.print(); list=list.r;
		}
		System.out.println();
	}
}