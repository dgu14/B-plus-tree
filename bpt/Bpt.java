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
			root=new BptDNode(); 					// �����Ͱ� ó�� ���� ���� ��������̴�.
			((BptDNode)root).insertData(data);		
		}
		else
		{
			BptDNode cNode=(BptDNode)(root.findNode(data));	// �̹� Ʈ���� �����ϴ� ����� data�� �� ��ġ�� ã��
			root=cNode.insertData(data);					// �����͸� ������ ����, ��Ʈ�� ������ ���� ������ ���ο� ��Ʈ�� �����Ѵ�.
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
		if(root==null) return;			// Ʈ���� �������� �ʴ� ���
		
		BptDNode dNode=(BptDNode) (root.findNode(data));
		
		int po = binary_search(dNode, data.d);
		if(po==-1) return ;				// ������ ���Ұ� ���� ���
		
		root=dNode.deleteData(po);
	}

	public void printSearchPath(int key)
	{
		// -s ���� ����
		if(root!=null) root.printSearchPath(key);
	}
	
	public Node getLinkedList()
	{
		// ����� ��
		if(root!=null) return root.getLinkedList();
		return null;
	}
	
	public void print()
	{
		// ����׿� 
		if(root!=null) root.print();
		System.out.println();
	}
	
	public void printLinkedList()
	{
		// ����׿�
		BptDNode list=(BptDNode)getLinkedList();
		
		while(list!=null)
		{
			list.print(); list=list.r;
		}
		System.out.println();
	}
}