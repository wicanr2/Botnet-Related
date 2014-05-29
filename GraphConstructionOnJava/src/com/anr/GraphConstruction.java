package com.anr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphConstruction {
	private List<NodeContainer> mLogList = new ArrayList<NodeContainer>();
	private List<AdjacencyList> mAdjacencyMatrix = new LinkedList<AdjacencyList>();
	public GraphConstruction() {
		
	}
	public void constructGraph(String filePath, String outFilePath) throws Exception {
		File transactionFile = new File(filePath);
		System.out.println("analyse file : "
				+ transactionFile.getAbsolutePath());
		if (transactionFile.isFile() && transactionFile.exists()) {
			FileInputStream fstream = new FileInputStream(filePath);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = br.readLine()) != null) {
				mLogList.add(new NodeContainer(line));
				//System.out.println("line = " + line);
			}
			for ( int i = 0 ; i < 20 ; i++ ) {
				NodeContainer nc = mLogList.get(i);
				System.out.println("["+i+"] = " + nc );
			}
			float tmpAvgIntervalDiff = 0.0f;
			float tmpIORatioDiff = 0.0f;
			float tmpPacketPerSecDiff = 0.0f;
			boolean bAvgIntervalEdge = false; // 1
			boolean bIORatioEdge = false; // 5 
			boolean bPacketPerSecEdge = false; // 0.002
			PrintStream pOut = new PrintStream(  new FileOutputStream(outFilePath) );
			AdjacencyList alist = new AdjacencyList();
			double comp_time = (double)(mLogList.size())/1000.0f;
			for ( int i = 0 ; i < mLogList.size() ; i ++ ) {
				alist.clear();
				NodeContainer ncI = mLogList.get(i);
				alist.setNode(ncI);
				//mAdjacencyMatrix.add(alit);
				if ( (i+1) % 1000 == 0 ) {
					double x1 = (double)(i+1) / 1000.0f;
					
					System.out.println("process Node " + i + " total comp_time " +  x1 * comp_time + " M \tncI = " + ncI);
				}
				for ( int j = 0 ; j < mLogList.size(); j++ ) {
					NodeContainer ncJ = mLogList.get(j);
					tmpAvgIntervalDiff = 0.0f;
					tmpIORatioDiff = 0.0f;
					tmpPacketPerSecDiff = 0.0f;
					bAvgIntervalEdge = false;
					bIORatioEdge = false;
					bPacketPerSecEdge = false;
					if ( ncI.getNodeID().equals(ncJ.getNodeID()) == false ) {
						if ( ncI.getAvgInterval() > 0.0f && ncJ.getAvgInterval() > 0.0f) {
							tmpAvgIntervalDiff = Math.abs(ncI.getAvgInterval() - ncJ.getAvgInterval());
						} else {
							tmpAvgIntervalDiff = -1.0f;
						}
						if ( ncI.getIORatio() > 0.0f && ncJ.getIORatio() > 0.0f ) {
							tmpIORatioDiff = Math.abs(ncI.getIORatio() - ncJ.getIORatio());
						} else {
							tmpIORatioDiff = -1.0f;
						}
						if ( ncI.getPacketPerSec() > 0.0f && ncJ.getPacketPerSec() > 0.0f ) {
							tmpPacketPerSecDiff = Math.abs(ncI.getPacketPerSec()- ncJ.getPacketPerSec());
						} else {
							tmpPacketPerSecDiff = -1.0f;
						}
									
						if ( tmpAvgIntervalDiff <= 1.0f ) {
							bAvgIntervalEdge = true;
							if ( tmpAvgIntervalDiff <= 0.0f ) 
								tmpAvgIntervalDiff = 1.0f; // give minimal value if diff equals zero
						} else {
							tmpAvgIntervalDiff = -1.0f;
						}
						if ( tmpIORatioDiff <= 5.0f ) {
							bIORatioEdge = true;
							if ( tmpIORatioDiff <= 0.0f ) 
								tmpIORatioDiff = 1.0f; // give minimal value if diff equals zero
						} else {
							tmpIORatioDiff = -1.0f;
						}
						if ( tmpPacketPerSecDiff <= 0.002 ) {
							bPacketPerSecEdge = true;
							if ( tmpPacketPerSecDiff <= 0.0f ) 
								tmpPacketPerSecDiff = 0.001f; // give minimal value if diff equals zero
						} else {
							tmpPacketPerSecDiff = -1.0f;
						}
						if ( (bAvgIntervalEdge && bIORatioEdge) || (bIORatioEdge && bPacketPerSecEdge) || (bAvgIntervalEdge && bPacketPerSecEdge)  ) {
							alist.addAdjacencyNode(ncJ);
						}
					}
					
				}
				/*if ( i % 1000 == 0 ) {
					System.out.println("write adjacency list  " + i + " al = " + alist);
				} */
				if ( alist.getAdjacencyNodeSize() > 0 ) {
					pOut.println(alist.toString());
					pOut.flush();
				}
			}
		}
	}
	public static void main(String args[]) {
		System.out.println("Graph Construction ");
		File currentDir = new File(".");
		System.out.println("currently dir = " + currentDir.getAbsolutePath());
		GraphConstruction myGC = new GraphConstruction();
		try {
			myGC.constructGraph("test.txt","adjaceny.txt");
		} catch ( Exception e ) {
			
		}
	}
}
