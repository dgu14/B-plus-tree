package bptTest;
import java.util.Arrays;
import java.util.Random;
public class Bpt {
	
	public static void main(String[] args)
	{
		int tt=1000;
		Random random=new Random();
		while(tt-->0)
		{
			int[] ori=new int[200000];
			int[] idx=new int[200000];
			
			for(int i=0;i<200000;i++)
			{
				ori[i]=random.nextInt(2000000)+1;
				idx[i]=i;
			}
			
			for(int i=0;i<200000;i++)
			{
				int a=random.nextInt(200000);
				int b=random.nextInt(200000);
 				int t=idx[a];
				idx[a]=idx[b];
				idx[b]=t;
			}
			
			Bpt bpt=new Bpt();
			Data isrt=bpt.new Data();
			for(int i=0;i<200000;i++)
			{
				isrt.d=ori[idx[i]];
				bpt.insert(isrt);
			}
			
			
			for(int i=0;i<100000;i++)
			{
				isrt.d=ori[i];
				bpt.delete(isrt);
			}
			
			for(int i=100000;i<150000;i++)
			{
				isrt.d=ori[i];
				bpt.delete(isrt);
			}
			
			for(int i=100000;i<150000;i++)
			{
				isrt.d=ori[i];
				bpt.insert(isrt);
			}
			
			for(int i=0;i<100000;i++)
			{
				isrt.d=ori[i];
				bpt.insert(isrt);
			}
			
			Arrays.sort(ori);
			int[] ret=new int[200000];
			int c=0;
			BptDNode rt=(BptDNode) bpt.getLinkedList();
			
			// 반대로 한번 테스트
			
			while(rt.r!=null)
			{
				rt=rt.r;
			}
			
			while(rt.l!=null)
			{
				rt=rt.l;
			}
			
			while(rt!=null)
			{
				for(int i=0;i<rt.dsz;i++)
				{
					if(c==200000) { System.out.println("실패 2" ); return; }
					ret[c++]=rt.ds[i].d;
				}
 				rt=rt.r;
			}
			
			for(int i=0;i<200000;i++)
			{
				if(ret[i]!=ori[i]) { System.out.println("실패" ); return; }
			}
			
			System.out.println(tt + " 번째 성공 , 테스트 데이터 " + ori[0] );
			
		}
	}
	
	public class Data {
		public int d;
		
		public Data() {}
		public Data(int d) { this.d=d; }
		public Data(Data data){ this.d=data.d; }
		
		public boolean isSmallerThan(Data data)
		{
			return this.d<data.d;
		}
		
		public boolean equal(Data data)
		{
			return this.d==data.d;
		}
	}
 	public class Element {
		public int pos;
		public Data k;
		public Node c;
		public BptNode host;
		
		Element() { this.c=null; this.host=null; this.k=null; this.pos=0; }
		Element(Data k, Node c) { this.k=k; this.c=c; this.host=null; this.pos=0; }
	}
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
	
	public class BptDNode extends Node{
		public int dsz;
		public Data[] ds;
		public BptDNode l, r;
		
		public BptDNode()
		{
			l=null; r=null; ds=new Data[Bpt.m+1]; dsz=0; 
		}
		
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
		
		public boolean underflow(int sz)
		{
			return (Bpt.m+1)/2 > sz;
		}
		
		public Node deleteData(int po)
		{
			for(int i=po;i<dsz;i++) { ds[i]=ds[i+1]; }
			dsz--;
			
			if(po==0)
			{
				Element cElem=pElem;
				while(cElem!=null && cElem.pos==0) cElem=cElem.host.pElem;
				
				if(cElem!=null) cElem.k=new Data(ds[0]);
			}
			
			if(underflow(dsz))
			{
				// root
				if(pElem==null)
				{
					if(dsz==0)
					{
						// if no element left
						return null;
					}
					
					return this;
				}
				
				BptDNode lcNode=getLeftCNode(), rcNode=getRightCNode();
				if(lcNode!=null && !underflow(lcNode.dsz-1))
				{
					// borrow from left 
					pElem.k=lcNode.ds[lcNode.dsz-1];
					this.insertData(lcNode.ds[lcNode.dsz-1]);
					lcNode.deleteData(lcNode.dsz-1);
				}
				else if(rcNode!=null && !underflow(rcNode.dsz-1))
				{
					// borrow from right
					Element pnElem=rcNode.pElem;
					pnElem.k=new Data(rcNode.ds[1]);
					this.insertData(rcNode.ds[0]);
					rcNode.deleteData(0);
				}
				else
				{
					// merge
					if(lcNode!=null)
					{
						// merge with left
						if(this.r!=null) this.r.l=lcNode;
						lcNode.r=this.r;
					
						for(int i=0;i<dsz;i++) lcNode.insertData(ds[i]);
						pElem.host.deleteData(pElem.pos); 
					}
					else if(rcNode!=null)
					{
						// merge with right
						if(rcNode.r!=null) rcNode.r.l=this;
						this.r=rcNode.r;
						
						Element pnElem=rcNode.pElem;
						for(int i=0;i<rcNode.dsz;i++) this.insertData(rcNode.ds[i]);
						
						pnElem.host.deleteData(pnElem.pos);
					}
					else System.out.println("ERROR - BptDNode");
				}
			}
			
			return this.findRoot(); 
		}
		
		public Node insertData(Data data)
		{
			int po=dsz-1;
			for(;po>-1;po--)
			{
				if(data.isSmallerThan(ds[po])) ds[po+1]=ds[po];
				else break;
			}
			
			ds[po+1]=new Data(data); dsz++;
			
			if(dsz==Bpt.m+1)
			{
				int mid=dsz/2; dsz=0; Data md=new Data(ds[mid]);
				BptDNode lSibNode=new BptDNode();
				for(int i=0;i<mid;i++) lSibNode.insertData(ds[i]);
				for(int i=mid;i<=Bpt.m;i++) { ds[dsz++]=ds[i]; ds[i]=null; }
				
				if(this.l!=null) this.l.r=lSibNode;
				lSibNode.l=this.l; lSibNode.r=this; this.l=lSibNode;
				
				BptNode parent=null;
				if(pElem==null)
				{
					parent=new BptNode(); parent.els[0]=new Element(null, lSibNode);
					pElem=parent.els[0]; pElem.host=parent;
				}
				else parent=pElem.host; 
		
				
				Element nElem=new Element(md, null); nElem.pos=pElem.pos+1;
				Bpt.setChild(pElem, lSibNode); Bpt.setChild(nElem, this);
			
				return parent.insertData(nElem);
			}
			
			return this.findRoot();
		}
		
		public void print()
		{
			System.out.print("(");
			for(int i=0;i<dsz;i++)
			{
				System.out.print(ds[i].d + ",");
			}
			
			System.out.print(")");
		}
	}
	
	public class BptNode extends Node {
		public int csz;
		public Element[] els;
		
		public BptNode()
		{
			this.csz=0; els=new Element[Bpt.m+1]; 
		}
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
		
		public boolean underflow(int sz)
		{
			return (Bpt.m-1)/2 > sz;
		}
		
		public Node deleteData(int po)
		{
			for(int i=po;i<=csz;i++) { els[i]=els[i+1]; if(i!=csz) els[i].pos--; }
			csz--;
			
			if(underflow(csz))
			{
				// root
				if(pElem==null) { 
					if(csz==0)
					{
						// if no element left
						this.els[0].c.pElem=null;
						return this.els[0].c;
					}
					
					return this;
				}
				
				BptNode lcNode=getLeftCNode(), rcNode=getRightCNode();
				if(lcNode!=null && !underflow(lcNode.csz-1))
				{
					// borrow from left 
					Node tmp=els[0].c;
					Bpt.setChild(els[0], lcNode.els[lcNode.csz].c);
					Bpt.setChild(lcNode.els[lcNode.csz], tmp);
					lcNode.els[lcNode.csz].pos=1;
					
					Data tmpk=lcNode.els[lcNode.csz].k;
					lcNode.els[lcNode.csz].k=pElem.k;
					pElem.k=tmpk;
					
					this.insertData(lcNode.els[lcNode.csz]); 
					lcNode.deleteData(lcNode.csz);		
				}
				else if(rcNode!=null && !underflow(rcNode.csz-1))
				{
					// borrow from right
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
					// merge
					if(lcNode!=null)
					{
						// merge with left
						BptNode parent=pElem.host;
						int dpo=pElem.pos;
						
						Bpt.setChild(pElem, els[0].c);
						pElem.pos=lcNode.csz+1; lcNode.insertData(pElem);
						for(int i=1;i<=csz;i++) { els[i].pos=lcNode.csz+1; lcNode.insertData(els[i]); }
					
						parent.deleteData(dpo); 
					}
					else if(rcNode!=null)
					{
						// merge with right
						Element pnElem=rcNode.pElem;
						BptNode parent=pnElem.host;
						int dpo=pnElem.pos;
						
						Bpt.setChild(pnElem, rcNode.els[0].c);
						pnElem.pos=this.csz+1; this.insertData(pnElem);
						for(int i=1;i<=rcNode.csz;i++) { rcNode.els[i].pos=csz+1; this.insertData(rcNode.els[i]); }
						
						parent.deleteData(dpo);
					}
					else System.out.println("ERROR - BptNode");
				}
			}
			
			return this.findRoot(); 
		}
		
		
		public Node insertData(Element nElem)
		{
			for(int po=csz;po>=nElem.pos;po--) { els[po].pos++; els[po+1]=els[po]; }
			els[nElem.pos]=nElem; nElem.host=this; csz++;
			
			if(csz==Bpt.m) return split();
			return this.findRoot();
		}
		
		public Node split()
		{
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
			for(int i=1;i<=csz;i++) if(cKey.isSmallerThan(els[i].k)) return els[i-1].c.findNode(cKey);
			return els[csz].c.findNode(cKey);
		}
		
		public Node getLinkedList()
		{
			return els[0].c.getLinkedList();
		}
		
		public void print() {
			System.out.print("(");
			for(int i=1;i<=csz;i++)
			{
				els[i-1].c.print();
				System.out.print(els[i].k.d+",");
			}
			els[csz].c.print();
			
			System.out.print(")");
		}
	}
	
	public static final int m=5;
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