package bpt;
public class BptNode extends Node {
	public int csz;					// 내부노드에서 key수를 나타냅니다. 최대로 , 차수 m-1개 만큼을 가집니다.
	public Element[] els;			// (key, child) 페어인 Element배열입니다. 0번째부터 csz번째까지 있으며, 0번째는 key는 없고 left most child만 저장합니다.
	
	public int[] elseln;			// 파일입출력을 위한 보조 변수입니다.
	
	// BptNode에 대한 정보를 파일로 저장할 때, 다음과 같은 포맷으로 string을 만들어서 저장합니다.
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
		this.csz=0; els=new Element[Bpt.m+1]; // 편리한 연산을 위해 배열의 크기를 하나 더 크게 잡습니다
		elseln=new int[Bpt.m+1];
	}
	
	// 왼쪽 형제자매 노드와 오른쪽 형제자매노드를 얻는 함수입니다.
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
	
	// underflow 조건이 일어나는 조건입니다. 
	public boolean underflow(int sz)
	{
		return (Bpt.m-1)/2 > sz;
	}
	
	// 내부노드에서의 데이터 삭제. po번 째 data를 삭제하는 연산입니다.
	// 리프에서 재귀로만 실행됩니다. 층의 삭제가 루트에서만 일어난다는 점이 핵심입니다.
	public Node deleteData(int po)
	{
		// 리프노드와 마찬가지로 삭제할 원소 다음부터 끝까지의 원소를 한칸씩 당겨줍니다. 
		// 당기면서 Element의 위치 pos도 하나씩 빼줍니다. 어차피 O(b)의 시간이 걸리므로, 바이너리 서치는 하지 않습니다.
		for(int i=po;i<=csz;i++) { els[i]=els[i+1]; if(i!=csz) els[i].pos--; }
		csz--;
		
		if(underflow(csz))
		{
			// root 인경우
			if(pElem==null) { 
				if(csz==0)
				{
					// 원소가 없을 경우, 제일 왼쪽 자식을 루트로 삼습니다.
					this.els[0].c.pElem=null;	// 제일 왼쪽 자식의 부모를 null로 설정합니다.
					return this.els[0].c;
				}
				
				// 루트의 underflow조건은 없으므로 그냥 반환합니다.
				return this;
			}
			
			BptNode lcNode=getLeftCNode(), rcNode=getRightCNode();
			if(lcNode!=null && !underflow(lcNode.csz-1))
			{
				// borrow from left, 왼쪽 형제자매 노드에서 하나를 줘도 언더플로우가 일어나지 않을 때
				// 왼쪽에서 borrrow연산을 진행합니다 . 기본적으로 리프노드와 다르지 않으나, 원소를 옮겨줄 때 자식 트리들의
				// 연결관계를 재설정해야하기 때문에 추가 연산이 진행됩니다.
				Node tmp=els[0].c;									// 현재 노드의 제일 왼쪽 child를 임시배열 tmp에 넣습니다.
				Bpt.setChild(els[0], lcNode.els[lcNode.csz].c);		// 그리고 현재 노드의 제일 왼쪽 child에는 왼쪽노드의 제일 큰 child를 달아줍니다.
				Bpt.setChild(lcNode.els[lcNode.csz], tmp);			// 왼쪽노드의 제일 오른쪽 노드에는 tmp를 넣어줍니다.
				lcNode.els[lcNode.csz].pos=1;						// 이 왼쪽노드의 제일 오른쪽 원소를 부모노드의 데이터와 교체한  뒤에 현재노드의 첫번째로 넣어줄 겁니다.
				
				Data tmpk=lcNode.els[lcNode.csz].k;					// 왼쪽노드의 제일 오른쪽 원소와 부모노드의 데이터와의 교체.
				lcNode.els[lcNode.csz].k=pElem.k;
				pElem.k=tmpk;
				
				this.insertData(lcNode.els[lcNode.csz]); 			// 현재노드에 왼쪽노드의 제일 오른쪽 값을 삽입하고
				lcNode.deleteData(lcNode.csz);						// 왼쪽노드에서는 그 값을 삭제합니다.
			}
			else if(rcNode!=null && !underflow(rcNode.csz-1))
			{
				// borrow from right, 위의 왼쪽에서 borrow하는 연산과 주체만 다를 뿐이지 코드는 같습니다.
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
					
					Bpt.setChild(pElem, els[0].c);								// 현재노드의 제일 왼쪽원소를 부모노드의 자식으로 달아버리고
					pElem.pos=lcNode.csz+1; lcNode.insertData(pElem);			// 부모노드를 왼쪽노드에 추가합니다
					for(int i=1;i<=csz;i++) { els[i].pos=lcNode.csz+1; lcNode.insertData(els[i]); }	// 현재노드에 남아있는 원소들을 왼쪽노드로 다 넣습니다.
				
					parent.deleteData(dpo); // 부모노드의 dpo위치를 삭제합니다.
					
					// lcNode의 부모를 반환합니다.
					return lcNode.findRoot();
				}
				else if(rcNode!=null)
				{
					// merge with right, 위 코드와 연산의 주체만 다를 뿐 코드는 같습니다.
					Element pnElem=rcNode.pElem;
					BptNode parent=pnElem.host;
					int dpo=pnElem.pos;
					
					Bpt.setChild(pnElem, rcNode.els[0].c);
					pnElem.pos=this.csz+1; this.insertData(pnElem);
					for(int i=1;i<=rcNode.csz;i++) { rcNode.els[i].pos=csz+1; this.insertData(rcNode.els[i]); }
					
					parent.deleteData(dpo);
					
					// 현재노드의부모를 반환합니다.
					return this.findRoot();
				}
				else System.out.println("ERROR - BptNode");	// 이럴 경우는 존재하지 않습니다.
			}
		}
		
		return this.findRoot(); 
	}
	
 	public Node insertData(Element nElem)
	{
 		// 삽입할 원소자리부터 노드의 끝까지 한 칸씩 뒤로 밀어줍니다.
		for(int po=csz;po>=nElem.pos;po--) { els[po].pos++; els[po+1]=els[po]; }
		els[nElem.pos]=nElem; nElem.host=this; csz++;
		
		if(csz==Bpt.m) return split();	// 오버플로우가 일어날 경우 split연산을 수행합니다.
		return this.findRoot();
	}
 	
	
	public Node split()
	{
		// 리프노드의 split 과정과 똑같습니다.
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
		// 오버라이딩 된 함수로, cKey값이 있을 수도 있는 리프노드를 반환합니다. null을 반환하는 경우는 없습니다.
		// 바이너리 서치로 구현합니다.
		
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
		// 디버그용으로 만든 제일 작은 원소를 찾는 함수입니다.
		return els[0].c.getLinkedList();
	}
	
	public void print() {
		// 디버그용으로 만든 함수라서 신경 쓸 필요는 없습니다. 그냥 인-오더 순으로 트리를 출력하는 함수입니다.
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
		// -s 명령어가 들어왔을 때, 지나가는 노드들의 키값들을 모두 출력하는 함수.
		// 출력하는 과정에서 인덱스를 찾으면 되므로 바이너리 서치는 쓰지 않습니다.
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