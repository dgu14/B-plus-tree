package main;
 import java.util.Random;
 import java.util.Scanner;
 import bpt.*;
 public class Main {
 	public static void main(String[] args)
	{
 		Scanner sc=new Scanner(System.in);
		Bpt bpt=new Bpt();
		Data inserted=new Data(); 	
		
		final int MAX_N=(int) 1e6;
		try {
			String cmd;
			while(true)
			{
				System.out.println("¸í·É: insert,delete,search (ex insert 7, delete 5, search 3), 'quit' for quit");
				cmd = sc.next(); if(cmd.equals("quit")) break;
				inserted.d=Integer.parseInt(sc.next()); 
				
				if(cmd.equals("insert")){
					bpt.insert(inserted);
					bpt.print();
					bpt.printLinkedList();
				}
				else if(cmd.equals("delete"))
				{
					bpt.delete(inserted);
					bpt.print();
					bpt.printLinkedList();
				}
				else if(cmd.equals("search"))
				{
					BptDNode t=bpt.search(inserted);
					if(t==null) System.out.println("No");
					else System.out.println("Yes");
				}
			}
		} catch(Exception e) {}
		
		System.out.println("done");	
	}
}