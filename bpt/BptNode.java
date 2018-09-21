package bpt;
public class BptNode extends Node {
	public int csz;					// ���γ�忡�� key���� ��Ÿ���ϴ�. �ִ�� , ���� m-1�� ��ŭ�� �����ϴ�.
	public Element[] els;			// (key, child) ����� Element�迭�Դϴ�. 0��°���� csz��°���� ������, 0��°�� key�� ���� left most child�� �����մϴ�.
	
	public int[] elseln;			// ����������� ���� ���� �����Դϴ�.
	
	// BptNode�� ���� ������ ���Ϸ� ������ ��, ������ ���� �������� string�� ���� �����մϴ�.
	public String convertFileFormat()
	{
		String ret="INTERNAL " + ndn + " " + (pElem!=null?pElem.eln:-1) + " " + csz + " ";
		for(int i=0;i<=csz;i++)
		{
			ret+=els[i].eln + " ";
		}
		
		return ret;
	}
	
	public BptNode()
	{
		this.csz=0; els=new Element[Bpt.m+1]; // ���� ������ ���� �迭�� ũ�⸦ �ϳ� �� ũ�� ����ϴ�
		elseln=new int[Bpt.m+1];
	}
	
	// ���� �����ڸ� ���� ������ �����ڸų�带 ��� �Լ��Դϴ�.
	public BptNode getLeftCNode()
	{
		if(pElem.pos==0) return null;
		return (BptNode)(pElem.host.els[pElem.pos-1].c);
	}
	
	public BptNode getRightCNode()
	{
		if(pElem.pos==pElem.host.csz) return null;
		return (BptNode)(pElem.host.els[pElem.pos+1].c);
	}
	
	// underflow ������ �Ͼ�� �����Դϴ�. 
	public boolean underflow(int sz)
	{
		return (Bpt.m-1)/2 > sz;
	}
	
	// ���γ�忡���� ������ ����. po�� ° data�� �����ϴ� �����Դϴ�.
	// �������� ��ͷθ� ����˴ϴ�. ���� ������ ��Ʈ������ �Ͼ�ٴ� ���� �ٽ��Դϴ�.
	public Node deleteData(int po)
	{
		// �������� ���������� ������ ���� �������� �������� ���Ҹ� ��ĭ�� ����ݴϴ�. 
		// ���鼭 Element�� ��ġ pos�� �ϳ��� ���ݴϴ�. ������ O(b)�� �ð��� �ɸ��Ƿ�, ���̳ʸ� ��ġ�� ���� �ʽ��ϴ�.
		for(int i=po;i<=csz;i++) { els[i]=els[i+1]; if(i!=csz) els[i].pos--; }
		csz--;
		
		if(underflow(csz))
		{
			// root �ΰ��
			if(pElem==null) { 
				if(csz==0)
				{
					// ���Ұ� ���� ���, ���� ���� �ڽ��� ��Ʈ�� ����ϴ�.
					this.els[0].c.pElem=null;	// ���� ���� �ڽ��� �θ� null�� �����մϴ�.
					return this.els[0].c;
				}
				
				// ��Ʈ�� underflow������ �����Ƿ� �׳� ��ȯ�մϴ�.
				return this;
			}
			
			BptNode lcNode=getLeftCNode(), rcNode=getRightCNode();
			if(lcNode!=null && !underflow(lcNode.csz-1))
			{
				// borrow from left, ���� �����ڸ� ��忡�� �ϳ��� �൵ ����÷ο찡 �Ͼ�� ���� ��
				// ���ʿ��� borrrow������ �����մϴ� . �⺻������ �������� �ٸ��� ������, ���Ҹ� �Ű��� �� �ڽ� Ʈ������
				// ������踦 �缳���ؾ��ϱ� ������ �߰� ������ ����˴ϴ�.
				Node tmp=els[0].c;									// ���� ����� ���� ���� child�� �ӽù迭 tmp�� �ֽ��ϴ�.
				Bpt.setChild(els[0], lcNode.els[lcNode.csz].c);		// �׸��� ���� ����� ���� ���� child���� ���ʳ���� ���� ū child�� �޾��ݴϴ�.
				Bpt.setChild(lcNode.els[lcNode.csz], tmp);			// ���ʳ���� ���� ������ ��忡�� tmp�� �־��ݴϴ�.
				lcNode.els[lcNode.csz].pos=1;						// �� ���ʳ���� ���� ������ ���Ҹ� �θ����� �����Ϳ� ��ü��  �ڿ� �������� ù��°�� �־��� �̴ϴ�.
				
				Data tmpk=lcNode.els[lcNode.csz].k;					// ���ʳ���� ���� ������ ���ҿ� �θ����� �����Ϳ��� ��ü.
				lcNode.els[lcNode.csz].k=pElem.k;
				pElem.k=tmpk;
				
				this.insertData(lcNode.els[lcNode.csz]); 			// �����忡 ���ʳ���� ���� ������ ���� �����ϰ�
				lcNode.deleteData(lcNode.csz);						// ���ʳ�忡���� �� ���� �����մϴ�.
			}
			else if(rcNode!=null && !underflow(rcNode.csz-1))
			{
				// borrow from right, ���� ���ʿ��� borrow�ϴ� ����� ��ü�� �ٸ� ������ �ڵ�� �����ϴ�.
				Element pnElem=rcNode.pElem;
				
				Node tmp=rcNode.els[1].c;
				Bpt.setChild(rcNode.els[1], rcNode.els[0].c);
				Bpt.setChild(rcNode.els[0], tmp);
				rcNode.els[1].pos=csz+1;
				
				Data tmpk=rcNode.els[1].k;
				rcNode.els[1].k=pnElem.k;
				pnElem.k=tmpk;
				
				this.insertData(rcNode.els[1]);
				rcNode.deleteData(1);
			}
			else
			{
				if(lcNode!=null)
				{
					// merge with left
 					BptNode parent=pElem.host;
					int dpo=pElem.pos;
					
					Bpt.setChild(pElem, els[0].c);								// �������� ���� ���ʿ��Ҹ� �θ����� �ڽ����� �޾ƹ�����
					pElem.pos=lcNode.csz+1; lcNode.insertData(pElem);			// �θ��带 ���ʳ�忡 �߰��մϴ�
					for(int i=1;i<=csz;i++) { els[i].pos=lcNode.csz+1; lcNode.insertData(els[i]); }	// �����忡 �����ִ� ���ҵ��� ���ʳ��� �� �ֽ��ϴ�.
				
					parent.deleteData(dpo); // �θ����� dpo��ġ�� �����մϴ�.
					
					// lcNode�� �θ� ��ȯ�մϴ�.
					return lcNode.findRoot();
				}
				else if(rcNode!=null)
				{
					// merge with right, �� �ڵ�� ������ ��ü�� �ٸ� �� �ڵ�� �����ϴ�.
					Element pnElem=rcNode.pElem;
					BptNode parent=pnElem.host;
					int dpo=pnElem.pos;
					
					Bpt.setChild(pnElem, rcNode.els[0].c);
					pnElem.pos=this.csz+1; this.insertData(pnElem);
					for(int i=1;i<=rcNode.csz;i++) { rcNode.els[i].pos=csz+1; this.insertData(rcNode.els[i]); }
					
					parent.deleteData(dpo);
					
					// �������Ǻθ� ��ȯ�մϴ�.
					return this.findRoot();
				}
				else System.out.println("ERROR - BptNode");	// �̷� ���� �������� �ʽ��ϴ�.
			}
		}
		
		return this.findRoot(); 
	}
	
 	public Node insertData(Element nElem)
	{
 		// ������ �����ڸ����� ����� ������ �� ĭ�� �ڷ� �о��ݴϴ�.
		for(int po=csz;po>=nElem.pos;po--) { els[po].pos++; els[po+1]=els[po]; }
		els[nElem.pos]=nElem; nElem.host=this; csz++;
		
		if(csz==Bpt.m) return split();	// �����÷ο찡 �Ͼ ��� split������ �����մϴ�.
		return this.findRoot();
	}
 	
	
	public Node split()
	{
		// ��������� split ������ �Ȱ����ϴ�.
		int mid=(csz+1)/2, i=1; csz=0; Element md=els[mid];
		BptNode lSibNode=new BptNode(); lSibNode.els[0]=new Element(null, null);
		
		Bpt.setChild(lSibNode.els[0], this.els[0].c); lSibNode.els[0].host=lSibNode; 
		for(;i<mid;i++) { lSibNode.insertData(els[i]); } 
		for(i=mid+1;i<=Bpt.m;i++) {els[++csz]=els[i]; els[i]=null; els[csz].pos=csz; } Bpt.setChild(els[0], md.c); els[0].pos=0; els[0].k=null;
		
		BptNode parent=null;
		if(pElem==null)
		{
			parent=new BptNode(); parent.els[0]=new Element(null, lSibNode);
			pElem=parent.els[0]; pElem.host=parent;
		}
		else parent=pElem.host;
		
		
		md.pos=pElem.pos+1;
		Bpt.setChild(pElem, lSibNode); Bpt.setChild(md, this);
		
		return parent.insertData(md);
	}
	
	public Node findNode(Data cKey)
	{
		// �������̵� �� �Լ���, cKey���� ���� ���� �ִ� ������带 ��ȯ�մϴ�. null�� ��ȯ�ϴ� ���� �����ϴ�.
		// ���̳ʸ� ��ġ�� �����մϴ�.
		
		int lo=1, hi=csz, mid, ret=-1;
		while(lo<=hi)
		{
			mid=(lo+hi)>>1;
			if(els[mid].k.d>cKey.d) { ret=mid; hi=mid-1; }
			else lo=mid+1;
		}
		
		if(ret==-1) return els[csz].c.findNode(cKey);
		else return els[ret-1].c.findNode(cKey);
	}
	
	public Node getLinkedList()
	{
		// ����׿����� ���� ���� ���� ���Ҹ� ã�� �Լ��Դϴ�.
		return els[0].c.getLinkedList();
	}
	
	public void print() {
		// ����׿����� ���� �Լ��� �Ű� �� �ʿ�� �����ϴ�. �׳� ��-���� ������ Ʈ���� ����ϴ� �Լ��Դϴ�.
		System.out.print("(");
		for(int i=1;i<=csz;i++)
		{
			els[i-1].c.print();
			System.out.print(els[i].k.d+",");
		}
		els[csz].c.print();
		
		System.out.print(")");
	}
	
	public void printSearchPath(int key)
	{
		// -s ��ɾ ������ ��, �������� ������ Ű������ ��� ����ϴ� �Լ�.
		// ����ϴ� �������� �ε����� ã���� �ǹǷ� ���̳ʸ� ��ġ�� ���� �ʽ��ϴ�.
		int fpo=-1;
		for(int i=1;i<=csz;i++)
		{
			if(i!=1) System.out.print(",");
			System.out.print(els[i].k.d);
			
			if(fpo==-1 && key<els[i].k.d) fpo=i;
		}
		System.out.println();
		
		if(fpo==-1) els[csz].c.printSearchPath(key);
		else els[fpo-1].c.printSearchPath(key);
	}
}