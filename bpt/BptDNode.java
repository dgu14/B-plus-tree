package bpt;
public class BptDNode extends Node{
	public int dsz;				// ������忡���� ���γ��� �ٸ��� �ִ� M(child subtree�� ����)��ŭ�� ���Ҹ� �����ϴ�.
	public Data[] ds;			// ��������� ������ �迭
	public BptDNode l, r;		// ������忡�� ����, ������ ������带 ����Ű�� ��ũ�� ����Ʈ ����.
	
	public int lndn, rndn;		// ���� ����� �� ���� ���̴� ���� ����.
	
	// BptDNode�� ���� ������ ���Ϸ� ������ ��, ������ ���� �������� string�� ���� �����մϴ�.
	public String convertFileFormat()
	{
		String ret="EXTERNAL " + ndn + " " + (pElem!=null?pElem.eln:-1) + " " + dsz + " " + (l!=null?l.ndn:-1) + " " + (r!=null?r.ndn:-1) + " ";
		for(int i=0;i<dsz;i++)
		{
			ret+=ds[i].d + " " + ds[i].rd + " ";
		}
		
		return ret;
	}
	
	public BptDNode()
	{
		l=null; r=null; ds=new Data[Bpt.m+1]; dsz=0;  // ���� ������ ���� �迭�� ũ�⸦ �ϳ� �� ũ�� ����ϴ�
	}
	
	// ���� �����ڸ� ���� ������ �����ڸų�带 ��� �Լ��Դϴ�.
	public BptDNode getLeftCNode()
	{
		if(pElem.pos==0) return null;
		return (BptDNode)(pElem.host.els[pElem.pos-1].c);
	}
	
	public BptDNode getRightCNode()
	{
		if(pElem.pos==pElem.host.csz) return null;
		return (BptDNode)(pElem.host.els[pElem.pos+1].c);
	}
	
	// underflow ������ �Ͼ�� �����Դϴ�. ���γ��ʹ� �ٸ��� child subtree�� ��ŭ
	// ���� �� �ְ� �����Ǿ����ϴ�. 
	public boolean underflow(int sz)
	{
		return (Bpt.m+1)/2 > sz;
	}
	
	// ������忡���� ������ ����. po�� ° data�� �����ϴ� �����Դϴ�.
	public Node deleteData(int po)
	{
		// �����Ǿ��� ������ ��ġ�� ���� ���ҵ��� ���� �����˴ϴ�.
		for(int i=po;i<dsz;i++) { ds[i]=ds[i+1]; }
		dsz--;
		
		// �ߺ� Ű�� ����ϴ� �߰� �ڵ��, �� ���� �����Ͱ� �����Ǿ��� �� �ٲ� ���� �����ͷ� 
		// �ε��� ���� ã�Ƽ� �ٲ��ִ� �ڵ��Դϴ�. �ߺ����� ������� �ʱ� ������ �ڼ��� ������ �����ʰڽ��ϴ�.
		if(po==0)
		{
			Element cElem=pElem;
			while(cElem!=null && cElem.pos==0) cElem=cElem.host.pElem;
			
			if(cElem!=null) cElem.k=new Data(ds[0]);
		}
		
		// underflow ���� Ȯ��.
		if(underflow(dsz))
		{
			// root�ΰ�� �Դϴ�.
			if(pElem==null)
			{
				if(dsz==0)
				{	
					// �ڽ��� ������ Ʈ���� �����Ͱ� �ƹ��͵� ���� ���̹Ƿ� null�� ��ȯ�մϴ�.
					return null;
				}
				
				// Ʈ���� ��Ʈ������ underflow ������ �����ϴ�. �׷��� �׳� root�� ��ȯ�մϴ�.
				return this;
			}
			
			BptDNode lcNode=getLeftCNode(), rcNode=getRightCNode();
			if(lcNode!=null && !underflow(lcNode.dsz-1))
			{
				// borrow from left, ���� �����ڸ� ��忡�� �ϳ��� �൵ ����÷ο찡 �Ͼ�� ���� ��
				// ���ʿ��� borrrow������ �����մϴ� 
				pElem.k=lcNode.ds[lcNode.dsz-1];			// �θ��� ������ ���� ���ʳ���� ���� ū������ ��ü
				this.insertData(lcNode.ds[lcNode.dsz-1]);	// ���� ��忡 �θ��忡 �ø� ������ ���� ����
				lcNode.deleteData(lcNode.dsz-1);			// ���ʳ���� ���� ū �� ����
			}
			else if(rcNode!=null && !underflow(rcNode.dsz-1))
			{
				// borrow from right
				Element pnElem=rcNode.pElem;				// ������ ����� �θ� �����͸� �������� ��´�.
				pnElem.k=new Data(rcNode.ds[1]);			// �� �θ� �����͸� ������ ����� ���� ���� ������ �ø���.
				this.insertData(rcNode.ds[0]);				// ���� ��忡 �ø� ������ ���� �����Ѵ�.
				rcNode.deleteData(0);						// ������ ��忡���� ���� ���� �� ����
			}
			else
			{
				// merge
				if(lcNode!=null)
				{
					// merge with left
					if(this.r!=null) this.r.l=lcNode;
					lcNode.r=this.r;						// ������忡�� ��ũ�� ����Ʈ ������ �����ϱ� ���� �ڵ��Դϴ�.
				
					for(int i=0;i<dsz;i++) lcNode.insertData(ds[i]); 
					pElem.host.deleteData(pElem.pos); 		// ���ʳ�忡 ���� ����� ������ ���� ��� �����ϰ� �θ��忡 �ִ� �����ϳ��� �����մϴ�.
					
					// lcNode�� �θ� ��ȯ�ؾ��մϴ�.
					return lcNode.findRoot();
				}
				else if(rcNode!=null)
				{
					// merge with right
					if(rcNode.r!=null) rcNode.r.l=this;
					this.r=rcNode.r;						// ������忡�� ��ũ�� ����Ʈ ������ �����ϱ� ���� �ڵ��Դϴ�.
							
					Element pnElem=rcNode.pElem;
					for(int i=0;i<rcNode.dsz;i++) this.insertData(rcNode.ds[i]);
					
					pnElem.host.deleteData(pnElem.pos);		// ���� ��忡 ������ ����� ������ ���� ��� �����ϰ� �θ��忡 �ִ� �����ϳ��� �����մϴ�.
					
					// �������� �θ� ��ȯ�ؾ��մϴ�.
					return this.findRoot();
				}
				else System.out.println("ERROR - BptDNode");	// �� ���� ���� �Ͼ�� �ʽ��ϴ�.
			}
		}
		
		return this.findRoot(); 		// ��Ʈ�� ã�Ƽ� ��ȯ�մϴ�. (��Ʈ�� �ٲ���� ���, bpt Ŭ�������� root�� ���� ���� ������ ����)
	}
	
	public Node insertData(Data data)
	{
		// ���԰� �ݴ�� ������ ��ġ�� ã�� �� ��ġ���� ���� ���ҵ��� �ϳ��� �̷�� �ִ� �κ��Դϴ�.
		// ������ ���Ҹ� �ϳ��� �̷�µ� O(b)��ŭ�� �ð��� �ɸ��Ƿ� ���� ���̳ʸ� ��ġ�� �����ʰ�
		// �������� ���� �� ���� �ڷ� �̷��, ���� ���� ������ �� ��ġ�� �߰��� �����͸� �����մϴ�.
		int po=dsz-1;
		for(;po>-1;po--)
		{
			if(data.isSmallerThan(ds[po])) ds[po+1]=ds[po];
			else break;
		}
		
		ds[po+1]=new Data(data); dsz++;
		
		if(dsz==Bpt.m+1) // overflow ����. ���������� child ������ŭ �����͸� �����ϴ�. (���κ��� +1)
		{
			int mid=dsz/2; dsz=0; Data md=new Data(ds[mid]);				// �߰����� ����.
			BptDNode lSibNode=new BptDNode();								// ���� ���� �ڸ� ������� ����
			for(int i=0;i<mid;i++) lSibNode.insertData(ds[i]);				// �߰� �� ���ʿ� �ִ� ���� ����
			for(int i=mid;i<=Bpt.m;i++) { ds[dsz++]=ds[i]; ds[i]=null; }	// ���� ��带 ������ �ڽ����� ���� ���̱� ������ �߰���ġ���� �������� ���Ҹ� ����ش�.
			
			if(this.l!=null) this.l.r=lSibNode;
			lSibNode.l=this.l; lSibNode.r=this; this.l=lSibNode;			// ��ũ�� ����Ʈ ������ �����ϱ� ���� �ڵ�
			
			BptNode parent=null;
			if(pElem==null)		// �θ� ��尡 �������� �ʴ� ��� ���� ���� �������ݴϴ�.
			{
				// els[0]�� key���� ������ ���� ���� ���� ���� child�� �����մϴ�. 
				parent=new BptNode(); parent.els[0]=new Element(null, lSibNode); 	// lSibNode�� parent�� ���� ���� child�� �����մϴ�.
				pElem=parent.els[0]; pElem.host=parent;								// els[0]�� �ִ� Element(key, child ���)�� host(�� �� ������ �ִ� ���)�� ���� ���� parent ���� �����մϴ�
			}
			else parent=pElem.host; 	// �θ� ��尡 �̹� �����ϴ� ���
	
			
			Element nElem=new Element(md, null); nElem.pos=pElem.pos+1;	// �߰��� Element�� �����ؼ�, ���� �θ� Element�� �ٷ� ���� ��ġ�� ������ �غ� �մϴ�
			Bpt.setChild(pElem, lSibNode); Bpt.setChild(nElem, this);	// �����ϱ� ���� ���� �������� ���ϴ�.
		
			return parent.insertData(nElem); // �θ��忡 �߰��� Element nElem�� ��ͷ� �������ݴϴ�.
		}
		
		return this.findRoot(); // ������ ���������� root ������ �Ͼ ���, ��Ʈ�� ã�Ƽ� ��ȯ���ݴϴ�. 
	}
	
	public void print()
	{
		// ����׿����� ���� �Լ��� �Ű� �� �ʿ�� �����ϴ�. �׳� ��-���� ������ Ʈ���� ����ϴ� �Լ��Դϴ�.
		System.out.print("(");
		for(int i=0;i<dsz;i++)
		{
			System.out.print(ds[i].d + ",");
		}
		
		System.out.print(")");
	}
	
	public void printSearchPath(int key) 
	{
		// -s �Է��� ������ ��, search �н��� ã���鼭 ���������� �������� �� ����Ǵ� �Լ��Դϴ�.
		// ���̳ʸ� ��ġ�� ã���ϴ�.
		
		int fpo=Bpt.binary_search(this, key);
		if(fpo==-1) System.out.println("NOT FOUND");
		else System.out.println(ds[fpo].rd); 
	}
}