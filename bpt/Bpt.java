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
			root=new BptDNode();
			((BptDNode)root).insertData(data);
		}
		else
		{
			BptDNode cNode=(BptDNode)(root.findNode(data));
			root=cNode.insertData(data);
		}
	}
	
	public void delete(Data data)
	{
		if(root==null) return;
		
		BptDNode dNode=search(data);
		if(dNode==null) return;
		
		int po = 0;
		for(int i=0;i<dNode.dsz;i++) if(dNode.ds[i].equal(data)) { po=i; break; }
		
		root=dNode.deleteData(po);
	}
	
	public BptDNode search(Data data)
	{
		BptDNode cNode=(BptDNode) (root.findNode(data));
		
		for(int i=0;i<cNode.dsz;i++) if(data.equal(cNode.ds[i])) return cNode;
		return null;
	}
	
	public void printSearchPath(int key)
	{
		if(root!=null) root.printSearchPath(key);
	}
	
	public Node getLinkedList()
	{
		if(root!=null) return root.getLinkedList();
		return null;
	}
	
	public void print()
	{
		if(root!=null) root.print();
		System.out.println();
	}
	
	public void printLinkedList()
	{
		BptDNode list=(BptDNode)getLinkedList();
		
		while(list!=null)
		{
			list.print(); list=list.r;
		}
		System.out.println();
	}
}