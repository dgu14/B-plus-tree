package bpt;
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
		
		// underflow 조건 확인.
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