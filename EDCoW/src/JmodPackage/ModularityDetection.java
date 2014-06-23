package JmodPackage;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import Dwt.Event;
import Dwt.KeyWord;
import Dwt.Main;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;
import ch.epfl.lis.jmod.Jmod;
import ch.epfl.lis.jmod.JmodNetwork;
import ch.epfl.lis.jmod.JmodSettings;
import ch.epfl.lis.jmod.modularity.community.Community;
import ch.epfl.lis.jmod.modularity.community.RootCommunity;
import ch.epfl.lis.networks.Edge;
import ch.epfl.lis.networks.EdgeFactory;
import ch.epfl.lis.networks.Node;
import ch.epfl.lis.networks.NodeFactory;
import ch.epfl.lis.networks.Structure;

/**
 * 
 * This class is used for clustering the remaining significant and more correlated keywords.
 * @author Yue HE & Falitokiniaina RABEARISON
 *
 */

public class ModularityDetection {
	List<Node> nodeList;	
	ArrayList<Community> arrayCommunities;
	Structure<Node, Edge<Node>> structure;		
    LinkedList<Event> events;
    float startDay;
    float endDay;
    int window;
    String nwtFolder;

    /**
     * Running the modularity detection of each window :
     * - instantiate the structure by adding the nodes then the edges (just we add to the network the nodes that have non zero values as correlation)
     * - 
     * @param partKeywords the remaining significant keywords, in a List of 'KeyWord', after filtering with theta1
     * @param correlations correlation matrix of the keywords 
     * @param startDay index of the first signal of the treated window
     * @param endDay indes of the end signal of the treated window
     * @param window_ index of the window to be run
     * @param nwtFolder_ folder containing nwt files
     * @throws Exception
     */
	// Add two arguments : start and end index of the frequency array
	public ModularityDetection(LinkedList<KeyWord> partKeywords, double[][] correlations, float startDay, float endDay, int window_, String nwtFolder_) throws Exception{
		this.startDay = startDay;
		this.endDay = endDay;
		window = window_;
		nwtFolder = nwtFolder_;
				
		// instantiate structure
		NodeFactory<Node> nodeFactory = new NodeFactory<>(new Node());
		EdgeFactory<Edge<Node>> edgeFactory = new EdgeFactory<>(new Edge<Node>());
		structure = new Structure<>(nodeFactory, edgeFactory);	 
		 System.out.println(" partKeywords.size()" +  partKeywords.size());
                for(int i = 0; i < partKeywords.size()-1; i++){                	                	                	                	                 	 
                    for(int j = i+1; j < partKeywords.size(); j++){                    	 
                        if(correlations[i][j] > 0.0){                        	 
                             //Adding keywords
                             structure.addNode(partKeywords.get(i).getKeyWord());
                             structure.addNode(partKeywords.get(j).getKeyWord());
                             
                             //Adding edges
                             structure.addEdge(new Edge<Node>(structure.getNode(partKeywords.get(i).getKeyWord()),structure.getNode(partKeywords.get(j).getKeyWord()),correlations[i][j]));
                        }
                    }
                }                               
        nodeList = structure.getNodesOrderedByNames();
		System.out.println("[window "+window+"] ["+startDay+","+endDay+"] : Number of nodes: " + structure.getSize());
		System.out.println("[window "+window+"] ["+startDay+","+endDay+"] : Number of edges: " + structure.getNumEdges());

		if(structure.getNumEdges()>0){//IF THERE IS NO EDGE, NO NEED TO PARTITION	
				// instantiate JmodNetwork
				JmodNetwork network = new JmodNetwork(structure);
				JmodSettings settings = JmodSettings.getInstance();
				settings.setUseMovingVertex(false);
				settings.setUseGlobalMovingVertex(false);	
				
				if(Boolean.valueOf(Main.mapParam.get("debugMode"))){			
					// select the datasets to export
					// modularity Q and number of indivisible communities
					settings.setExportBasicDataset(true);
					// export the modules detected to files
					settings.setExportCommunityNetworks(true);
					// specify the file format of the modules detected
					settings.setCommunityNetworkFormat(Structure.TSV);
					// export the original network in GML format with node
					// colors depending on which module a given node belongs to
					settings.setExportColoredCommunities(true);
					// export the community tree (dendrogram)
					settings.setExportCommunityTree(true);
				}
				
				// run modularity detection
				Jmod jmod = new Jmod();		 
				try {
					jmod.runModularityDetection(network);			                
			        events = new LinkedList<>();
					RootCommunity rc = jmod.getRootCommunity();
					System.out.println("[window "+window+"] ["+startDay+","+endDay+"] : There are "+rc.getNumIndivisibleCommunities()+" indivisible communities.");
					arrayCommunities = rc.getIndivisibleCommunities();
			
					//put events in events
					explore(rc,nodeList);
					
			//		for(Community c:arrayCommunities){			
			//		exploreCommunity(c,nodeList);
			//	}		
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}	
				
				if(Boolean.valueOf(Main.mapParam.get("debugMode"))){	
					// set output directory			//create the folder "results"
					String pathRes = nwtFolder+"/results/Jmod/window"+window;
					File fb = new File(pathRes); 
					fb.mkdirs();		
					URI outputDirURI = new URI(pathRes);
					// run modularity detection		
					jmod.setOutputDirectory(outputDirURI);		
					jmod.printResult();
					jmod.exportDataset();
				}		
		
		}			
	}

	/**
	 * Compute the total edges of a given community
	 * @param c community to compute the total edges
	 * @param nodeList list of the nodes of the network
	 * @return total weight, total of the edges
	 */
	public double computeEdgesWeight(Community c, List<Node> nodeList){		
		double totalWeight =0;
		if(c.getChild1() == null){
			DenseVector nodesC = c.getVertexIndexes();			
			for(VectorEntry ve : nodesC){
				int id = (int)ve.get();												
				Set<Edge<Node>> edges = structure.getEdges(nodeList.get(id));
				//System.out.println("un noeud");
				for(Edge<?> e:edges){
					if(e.getSource().equals(nodeList.get(id)))
						//System.out.println(e.getSource().getName()+" to "+e.getTarget().getName());
						totalWeight += e.getWeight(); 
				}
			}
		}else{
			computeEdgesWeight(c.getChild1(),nodeList);
			computeEdgesWeight(c.getChild2(),nodeList);
		}
		return totalWeight;
	}
	
	/**
	 * add event to the list of events detected in the treated window.
	 * @param c community to evaluate
	 * @param nodeList list of the nodes of the network
	 */
	public void explore(Community c, List<Node> nodeList){
		if(c.getChild1() == null){
			DenseVector nodesC = c.getVertexIndexes();
			//System.out.println("community: ");
			Event event = new Event();
			for(VectorEntry ve : nodesC){
				int id = (int)ve.get();				
				event.keywords.add(nodeList.get(id).getName());
				event.setEpsylon(computeE(c));
				event.setStartDay(startDay);
				event.setEndDay(endDay);
				event.setWindow(window);
			}
			if(!events.contains(event))	{events.add(event);			
			}
		}else{
			explore(c.getChild1(),nodeList);
			explore(c.getChild2(),nodeList);
		}
	}			
	
	/**
	 * Filter the detected events with the epsylon value which is a parameter from the user, 'thresholdE' in 'param' file, that is done with related to the event significant of each event. 
	 * @param thresholdE From the 'param' file
	 * @return ArrayList of the filtered events
	 */
	public ArrayList<Community> getCommunitiesFiltered(double thresholdE) {
		// TODO Auto-generated method stub
		ArrayList<Community> ComFiltered = new ArrayList<Community>();
		for(Community c:arrayCommunities){				
			double tempComE= computeE(c);
			if(tempComE>thresholdE) ComFiltered.add(c);
		}				
		return ComFiltered;
	}

	/**
	 * Compute the significance event of each community passed as argument. equation 18 in the paper.
	 * @param c Community to be treated
	 * @return Value of the event significance
	 */
	private double computeE(Community c) {
		// TODO Auto-generated method stub
		int n = c.getCommunitySize();
		double totalWeight=0;
		
		totalWeight = computeEdgesWeight(c,nodeList);
		double fact = 1 ;
		for(int i=1 ; i<(2*n)+1 ; i++){
			fact = fact * i;
		}
		double e = totalWeight * ((Math.exp(1.5*n))/fact);
		return e;
	}

	public ArrayList<Community> getArrayCommunities(){
		return arrayCommunities;
	}

	public List<Node> getNodeList() {
		return nodeList;
	}
    
	public LinkedList<Event> getEvents(){
        return events;
    }	
		
}
