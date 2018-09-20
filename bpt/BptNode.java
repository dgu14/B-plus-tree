package bpt;
public class BptNode extends Node {
	public int csz;
	public Element[] els;
	
	public int[] elseln;
	
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
		this.csz=0; els=new Element[Bpt.m+1]; 
		elseln=new int[Bpt.m+1];
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
 					BptNode parent=pElem.host;
					int dpo=pElem.pos;
					
					Bpt.setChild(pElem, els[0].c);
					pElem.pos=lcNode.csz+1; lcNode.insertData(pElem);
					for(int i=1;i<=csz;i++) { els[i].pos=lcNode.csz+1; lcNode.insertData(els[i]); }
				
					parent.deleteData(dpo); 
				}
				else if(rcNode!=null)
				{
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
	
	public void printSearchPath(int key)
	{
		for(int i=1;i<=csz;i++)
		{
			if(i!=1) System.out.print(",");
			System.out.print(els[i].k.d);
		}
		System.out.println();
		
		for(int i=1;i<=csz;i++)
		{
			if(key<els[i].k.d)
			{
				els[i-1].c.printSearchPath(key);
				return;
			}
		}
		
		els[csz].c.printSearchPath(key);
	}
}