package bpt;
public class BptDNode extends Node{
	public int dsz;				// 리프노드에서는 내부노드와 다르게 최대 M(child subtree의 개수)만큼의 원소를 가집니다.
	public Data[] ds;			// 리프노드의 데이터 배열
	public BptDNode l, r;		// 리프노드에서 왼쪽, 오른쪽 리프노드를 가리키는 링크드 리스트 구조.
	
	public int lndn, rndn;		// 파일 입출력 할 때만 쓰이는 보조 변수.
	
	// BptDNode에 대한 정보를 파일로 저장할 때, 다음과 같은 포맷으로 string을 만들어서 저장합니다.
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
		l=null; r=null; ds=new Data[Bpt.m+1]; dsz=0;  // 편리한 연산을 위해 배열의 크기를 하나 더 크게 잡습니다
	}
	
	// 왼쪽 형제자매 노드와 오른쪽 형제자매노드를 얻는 함수입니다.
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
	
	// underflow 조건이 일어나는 조건입니다. 내부노드와는 다르게 child subtree수 만큼
	// 가질 수 있게 설정되었습니다. 
	public boolean underflow(int sz)
	{
		return (Bpt.m+1)/2 > sz;
	}
	
	// 리프노드에서의 데이터 삭제. po번 째 data를 삭제하는 연산입니다.
	public Node deleteData(int po)
	{
		// 삭제되야할 원소의 위치로 뒤의 원소들을 당기면 삭제됩니다.
		for(int i=po;i<dsz;i++) { ds[i]=ds[i+1]; }
		dsz--;
		
		// 중복 키를 허용하는 추가 코드로, 맨 왼쪽 데이터가 삭제되었을 때 바뀐 왼쪽 데이터로 
		// 인덱스 값을 찾아서 바꿔주는 코드입니다. 중복값을 허용하지 않기 때문에 자세한 설명은 하지않겠습니다.
		if(po==0)
		{
			Element cElem=pElem;
			while(cElem!=null && cElem.pos==0) cElem=cElem.host.pElem;
			
			if(cElem!=null) cElem.k=new Data(ds[0]);
		}
		
		// underflow 조건 확인.
		if(underflow(dsz))
		{
			// root인경우 입니다.
			if(pElem==null)
			{
				if(dsz==0)
				{	
					// 자식이 없으면 트리에 데이터가 아무것도 없는 것이므로 null을 반환합니다.
					return null;
				}
				
				// 트리의 루트에서는 underflow 조건은 없습니다. 그래서 그냥 root를 반환합니다.
				return this;
			}
			
			BptDNode lcNode=getLeftCNode(), rcNode=getRightCNode();
			if(lcNode!=null && !underflow(lcNode.dsz-1))
			{
				// borrow from left, 왼쪽 형제자매 노드에서 하나를 줘도 언더플로우가 일어나지 않을 때
				// 왼쪽에서 borrrow연산을 진행합니다 
				pElem.k=lcNode.ds[lcNode.dsz-1];			// 부모의 데이터 값을 왼쪽노드의 제일 큰값으로 교체
				this.insertData(lcNode.ds[lcNode.dsz-1]);	// 현재 노드에 부모노드에 올린 데이터 값을 삽입
				lcNode.deleteData(lcNode.dsz-1);			// 왼쪽노드의 제일 큰 값 삭제
			}
			else if(rcNode!=null && !underflow(rcNode.dsz-1))
			{
				// borrow from right
				Element pnElem=rcNode.pElem;				// 오른쪽 노드의 부모 데이터를 기준으로 삼는다.
				pnElem.k=new Data(rcNode.ds[1]);			// 그 부모 데이터를 오른쪽 노드의 제일 작은 값으로 올린다.
				this.insertData(rcNode.ds[0]);				// 현재 노드에 올린 데이터 값을 삽입한다.
				rcNode.deleteData(0);						// 오른쪽 노드에서는 제일 작은 값 삭제
			}
			else
			{
				// merge
				if(lcNode!=null)
				{
					// merge with left
					if(this.r!=null) this.r.l=lcNode;
					lcNode.r=this.r;						// 리프노드에서 링크드 리스트 구조를 유지하기 위한 코드입니다.
				
					for(int i=0;i<dsz;i++) lcNode.insertData(ds[i]); 
					pElem.host.deleteData(pElem.pos); 		// 왼쪽노드에 현재 노드의 데이터 값을 모두 삽입하고 부모노드에 있는 원소하나를 제거합니다.
					
					// lcNode의 부모를 반환해야합니다.
					return lcNode.findRoot();
				}
				else if(rcNode!=null)
				{
					// merge with right
					if(rcNode.r!=null) rcNode.r.l=this;
					this.r=rcNode.r;						// 리프노드에서 링크드 리스트 구조를 유지하기 위한 코드입니다.
							
					Element pnElem=rcNode.pElem;
					for(int i=0;i<rcNode.dsz;i++) this.insertData(rcNode.ds[i]);
					
					pnElem.host.deleteData(pnElem.pos);		// 현재 노드에 오른쪽 노드의 데이터 값을 모두 삽입하고 부모노드에 있는 원소하나를 제거합니다.
					
					// 현재노드의 부모를 반환해야합니다.
					return this.findRoot();
				}
				else System.out.println("ERROR - BptDNode");	// 이 외의 경우는 일어나지 않습니다.
			}
		}
		
		return this.findRoot(); 		// 루트를 찾아서 반환합니다. (루트가 바뀌었을 경우, bpt 클래스에서 root에 대한 정보 유지를 위해)
	}
	
	public Node insertData(Data data)
	{
		// 삽입과 반대로 삽입할 위치를 찾고 그 위치부터 뒤의 원소들을 하나씩 미루어 주는 부분입니다.
		// 어차피 원소를 하나씩 미루는데 O(b)만큼의 시간이 걸리므로 굳이 바이너리 서치를 하지않고
		// 작은값을 만날 때 까지 뒤로 미루고, 작은 값을 만나면 그 위치에 추가할 데이터를 삽입합니다.
		int po=dsz-1;
		for(;po>-1;po--)
		{
			if(data.isSmallerThan(ds[po])) ds[po+1]=ds[po];
			else break;
		}
		
		ds[po+1]=new Data(data); dsz++;
		
		if(dsz==Bpt.m+1) // overflow 조건. 리프에서는 child 갯수만큼 데이터를 가집니다. (내부보다 +1)
		{
			int mid=dsz/2; dsz=0; Data md=new Data(ds[mid]);				// 중간값을 저장.
			BptDNode lSibNode=new BptDNode();								// 왼쪽 형제 자매 리프노드 생성
			for(int i=0;i<mid;i++) lSibNode.insertData(ds[i]);				// 중간 값 왼쪽에 있는 값들 삽입
			for(int i=mid;i<=Bpt.m;i++) { ds[dsz++]=ds[i]; ds[i]=null; }	// 현재 노드를 오른쪽 자식으로 삼을 것이기 때문에 중간위치부터 끝까지의 원소를 당겨준다.
			
			if(this.l!=null) this.l.r=lSibNode;
			lSibNode.l=this.l; lSibNode.r=this; this.l=lSibNode;			// 링크드 리스트 구조를 유지하기 위한 코드
			
			BptNode parent=null;
			if(pElem==null)		// 부모 노드가 존재하지 않는 경우 새로 만들어서 연결해줍니다.
			{
				// els[0]은 key값을 가지고 있지 않은 제일 왼쪽 child를 저장합니다. 
				parent=new BptNode(); parent.els[0]=new Element(null, lSibNode); 	// lSibNode를 parent의 제일 왼쪽 child로 지정합니다.
				pElem=parent.els[0]; pElem.host=parent;								// els[0]에 있는 Element(key, child 페어)의 host(그 페어를 가지고 있는 노드)를 새로 만든 parent 노드로 설정합니다
			}
			else parent=pElem.host; 	// 부모 노드가 이미 존재하는 경우
	
			
			Element nElem=new Element(md, null); nElem.pos=pElem.pos+1;	// 중간값 Element를 생성해서, 현재 부모 Element의 바로 다음 위치에 삽입할 준비를 합니다
			Bpt.setChild(pElem, lSibNode); Bpt.setChild(nElem, this);	// 삽입하기 전에 연결 정보들을 씁니다.
		
			return parent.insertData(nElem); // 부모노드에 중간값 Element nElem을 재귀로 삽입해줍니다.
		}
		
		return this.findRoot(); // 삭제와 마찬가지로 root 변경이 일어날 경우, 루트를 찾아서 반환해줍니다. 
	}
	
	public void print()
	{
		// 디버그용으로 만든 함수라서 신경 쓸 필요는 없습니다. 그냥 인-오더 순으로 트리를 출력하는 함수입니다.
		System.out.print("(");
		for(int i=0;i<dsz;i++)
		{
			System.out.print(ds[i].d + ",");
		}
		
		System.out.print(")");
	}
	
	public void printSearchPath(int key) 
	{
		// -s 입력이 들어왔을 때, search 패스를 찾으면서 리프노드까지 도달했을 때 실행되는 함수입니다.
		// 바이너리 서치로 찾습니다.
		
		int fpo=Bpt.binary_search(this, key);
		if(fpo==-1) System.out.println("NOT FOUND");
		else System.out.println(ds[fpo].rd); 
	}
}