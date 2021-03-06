package bpt;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

public class FileController {

	static int ndcnt;			// 총 노드의 수 (리프노드 + 내부노드)
	static int elcnt;			// 총 Element의 수 (내부노드의 Elements)
	static Node[] nds;			// 노드의 프리-오더 순으로 저장한 배열
	static Element[] els;		// Element의 프리-오더순으로 저장한 배열
	static String tokens[];		// make_token함수를 썼을 때, tokenized 된 데이터들이 자동으로 들어가는 배열.
	
	public FileController()
	{
		
	}
	
	// dfs는 트리 순회를 통해서 노드 카운터와 Element 카운터를 세는 함수이다.
	public static void dfs(Node rt)
	{
		if(rt==null) return ;
		rt.ndn=ndcnt++;
		
		if(rt instanceof BptNode) {	
			BptNode rta=(BptNode) rt;
			
			for(int i=0;i<=rta.csz;i++)
			{
				dfs(rta.els[i].c); rta.els[i].eln=elcnt++;
			}
		}
	}
	
	// dfs2는 트리 순회를 통해서 노드배열과 Element배열에 저장하는 함수이다.
	// dfs와 나눈 이유는 배열의 크기를 할당할 때, 그 크기를 모르기 때문이다.
	public static void dfs2(Node rt)
	{
		if(rt==null) return;
		nds[ndcnt++]=rt;
		
		if(rt instanceof BptNode)
		{
			BptNode rta=(BptNode) rt;
			
			for(int i=0;i<=rta.csz;i++)
			{
				dfs2(rta.els[i].c); els[elcnt++]=rta.els[i];
			}
		}
	}

	
	public static void create(int m, String fname) throws Exception
	{
		// 최대 차수 m인 b+tree를 fname을 가지는 파일을 만들어서 저장한다. (Overwrite)
		Bpt.m=m;
		PrintWriter out = new PrintWriter(new FileOutputStream(fname, false));
		out.println("META " + Bpt.m + " 0 0");
		out.close();
	}
	
	public static void write(Bpt bpt, String fname) throws Exception
	{
		PrintWriter out = new PrintWriter(new FileOutputStream(fname, false));
		
		Node rt=bpt.root; ndcnt=0; elcnt=0; dfs(rt);
		nds=new Node[ndcnt]; els=new Element[elcnt];
		ndcnt=0; elcnt=0; dfs2(rt);
		
		// 메타정보 출력
		out.println("META " + Bpt.m + " " + ndcnt + " " + elcnt);
		
		// ELEMENT 정보 출력
		for(int i=0;i<elcnt;i++) out.print(els[i].convertFileFormat() + "\n");
		// NODE 정보 출력
		for(int i=0;i<ndcnt;i++) out.print(nds[i].convertFileFormat() + "\n");
		
		out.close();
	}
	
	public static int makeToken(String str, char delim)
	{
		// delim을 기준으로 string을 나눈다. tokens 전역배열에 저장.
		if(str==null) return 0;
		int c=0; String cstr="";
		
		for(int i=0;i<str.length();i++)
		{
			if(str.charAt(i)==delim)
			{
				tokens[c++]=cstr;
				cstr="";
			}
			else cstr+=str.charAt(i);
		}
		
		if(cstr!="") tokens[c++]=cstr;
		
		return c;
	}
	
	
	public static void ASSERT(boolean test)
	{
		if(!test) System.exit(-1);
	}
	
	public static Element extractElement()
	{
		ASSERT(tokens[0].equals("ELEMENT"));
		
		Element ret=new Element();
		ret.eln=Integer.parseInt(tokens[1]);
		ret.hndn=Integer.parseInt(tokens[2]);
		ret.pos=Integer.parseInt(tokens[3]);
		ret.k=new Data(Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
		ret.cndn=Integer.parseInt(tokens[6]);
		
		return ret;
	}
	
	public static BptNode extractBptNode()
	{
		BptNode ret=new BptNode();
		ret.ndn=Integer.parseInt(tokens[1]);
		ret.peln=Integer.parseInt(tokens[2]);
		ret.csz=Integer.parseInt(tokens[3]);
		
		for(int i=0;i<=ret.csz;i++)
		{
			ret.elseln[i]=Integer.parseInt(tokens[4+i]);
		}
		
		return ret;
	}
	
	public static BptDNode extractBptDNode()
	{
		BptDNode ret=new BptDNode();
		ret.ndn=Integer.parseInt(tokens[1]);
		ret.peln=Integer.parseInt(tokens[2]);
		ret.dsz=Integer.parseInt(tokens[3]);
		ret.lndn=Integer.parseInt(tokens[4]);
		ret.rndn=Integer.parseInt(tokens[5]);
		
		for(int i=0;i<ret.dsz;i++)
		{
			ret.ds[i]=new Data(Integer.parseInt(tokens[6+2*i]), Integer.parseInt(tokens[6+2*i+1]));
		}
		
		return ret;
	}
	
	public static void transferDataToBpt(Bpt bpt, String fname) throws Exception
	{
		// -i 명령어 처리.
		BufferedReader in = new BufferedReader(new FileReader(fname));
		tokens=new String[3];

		while(makeToken(in.readLine(), ',')!=0) bpt.insert(new Data(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])));
		in.close();
	}
	
	public static void removeDataToBpt(Bpt bpt, String fname) throws Exception
	{
		// -d 명령어 처리
		BufferedReader in = new BufferedReader(new FileReader(fname));
		tokens=new String[3];

		String str="";
		while((str=in.readLine())!=null) bpt.delete(new Data(Integer.parseInt(str)));
		in.close();
	}
	
	
	public static void printDataForKey(Bpt bpt, int key) throws Exception
	{
		// -s 명령어 처리
		bpt.printSearchPath(key);
	}
	
	public static void rangeSearch(Bpt bpt, int key, int ekey) throws Exception
	{
		// -r 명령어 처리
		if(bpt.root==null) return;
		BptDNode here=(BptDNode) bpt.root.findNode(new Data(key,0));
		
		if(here==null || key>ekey) return;
		
		while(here!=null)
		{
			for(int i=0;i<here.dsz;i++)
			{
				if(here.ds[i].d>ekey)
				{
					return;
				}
				
				if(here.ds[i].d>=key)
				{
					System.out.println(here.ds[i].d+","+here.ds[i].rd);
				}
			}
			
			here=here.r;
		}
	}
	
	
	public static Bpt read(String fname) throws Exception
	{
		// bpt를 파일로 부터 읽는 함수.
		BufferedReader in = new BufferedReader(new FileReader(fname));
		tokens=new String[10000];
		
		makeToken(in.readLine(), ' ');
		ASSERT(tokens[0].equals("META"));
		Bpt bpt=new Bpt(); Bpt.m=Integer.parseInt(tokens[1]);
		ndcnt=Integer.parseInt(tokens[2]);
		elcnt=Integer.parseInt(tokens[3]);
		
		nds=new Node[ndcnt]; els=new Element[elcnt];
		
		for(int i=0;i<elcnt;i++)
		{
			makeToken(in.readLine(), ' ');
			els[i]=extractElement();
		}
		
		for(int i=0;i<ndcnt;i++)
		{
			makeToken(in.readLine(), ' ');
			
			if(tokens[0].equals("INTERNAL"))
			{
				nds[i]=extractBptNode();
			}
			else if(tokens[0].equals("EXTERNAL"))
			{
				nds[i]=extractBptDNode();
			}
		}
	
		// 노드배열의 연결 상태들을 재정의 한다.
		for(int i=0;i<ndcnt;i++)
		{
			if(nds[i] instanceof BptNode)
			{
				BptNode rt=(BptNode) nds[i];
				if(rt.peln!=-1) rt.pElem=els[rt.peln]; else rt.pElem=null;
				for(int j=0;j<=rt.csz;j++) rt.els[j]=els[rt.elseln[j]];
			}
			else
			{
				BptDNode rt=(BptDNode) nds[i];
				
				if(rt.peln!=-1)
				{
					rt.pElem=els[rt.peln]; 
				}
				else rt.pElem=null;
				
				if(rt.lndn!=-1) rt.l=(BptDNode) nds[rt.lndn]; 
				else rt.l=null; 
				
				if(rt.rndn!=-1) rt.r=(BptDNode) nds[rt.rndn]; 
				else rt.r=null;
			}
		}
		
		// Element배열의 연결 상태들을 재정의 한다.
		for(int i=0;i<elcnt;i++)
		{
			if(els[i].cndn!=-1) els[i].c=nds[els[i].cndn]; else els[i].c=null;
			if(els[i].hndn!=-1) els[i].host=(BptNode) nds[els[i].hndn]; else els[i].host=null;
		}
		
		bpt.root=(ndcnt==0?null:nds[0]);
		
		in.close();
		return bpt;
	}
}
