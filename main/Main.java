package main;
import bpt.*;
public class Main {
 	public static void main(String[] args) 
	{
 		try
 		{
 			if(args[0].equals("-c"))
 			{
 				// create java -jar bptree -c index.dat 8
 				FileController.create(Integer.parseInt(args[2]), args[1]);
 			}
 			else if(args[0].equals("-i"))
 			{
 				// insert 
 				Bpt bpt=FileController.read(args[1]);
 				FileController.transferDataToBpt(bpt, args[2]);
 				FileController.write(bpt, args[1]);
 			}
 			else if(args[0].equals("-d"))
 			{
 				// delete
 				Bpt bpt=FileController.read(args[1]);
 				FileController.removeDataToBpt(bpt, args[2]);
 				FileController.write(bpt, args[1]);
 			}
 			else if(args[0].equals("-s"))
 			{
 				// search
 				Bpt bpt=FileController.read(args[1]);
 				FileController.printDataForKey(bpt, Integer.parseInt(args[2]));
 			}
 			else if(args[0].equals("-r"))
 			{
 				// range search
 				Bpt bpt=FileController.read(args[1]);
 				FileController.rangeSearch(bpt, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
 			}
 			else
 			{
 				System.out.println("command doesn't exist... ");
 			}
 			
 		} catch(Exception e)
 		{
 			System.out.println("file doesn't exist.. plz check the folder.. or command fault, plz check type");
 		}
	}
}