package src;

/**
 * References
 * - MTC
 *       Li, X., Hamilton, H. J., Karimi, K., & Geng, L. (2009). The Multi-Tree Cubing algorithm for computing iceberg
 *      cubes. Journal of Intelligent Information Systems, 33(2), 179–208. https://doi.org/10.1007/s10844-008-0074-3
 * - https://www.javaspring.net/blog/tree-implementation-in-java-root-parents-and-children/
 * - Binary Search
 *      https://www.softpost.org/java/binary-search-in-arraylist-in-java#google_vignette
 * - CompareTo
 *      https://www.geeksforgeeks.org/java/comparable-interface-in-java-with-examples/
 *      https://www.geeksforgeeks.org/java/collections-binarysearch-java-examples/
 * - Timer: https://www.javaspring.net/blog/measure-execution-time-for-a-java-method/
 */

/**
 * PseudoCode
 * 1- Extract dimensions for each Tree_d
 * 2- Scan the dataset ONCE to determine single-partitions counts for all dimensions
 * 3- Construct the tree for the LAST dimension m
 *      for (each partition of the Tree_m)
 *          if (count >= min_sup)
 *              add partition to Tree_m.children
 * 4- Traverse (iterate) Tree_dm to:
 *      - output results
 *      - add Tree_dm to processedTreeList (arraylist)
 * 5- Starting from the LAST remaining dimension, for( each dimension_d)
 *       Tree_d
 *       for (each partition of dimension_d
 *           if (count >= min_sup)
 *               add partition to Tree_d.children
 *
 *       for ( each entry in dataSet )
 *
 *           {--- 5.1_Finding the corresponding node to entry value}
 *           SEARCH Tree_d.children to find if (partition == entry[d] )
 *
 *           {--- 5.2_Extending from exiting trees ---}
 *
 *           if found
 *              for ( each Tree T in processedTreeList )
 *                 find ALL paths in T that (DFS method the returns list of matched paths)== dimension values of entry
 *                 for (each path) either
 *                     if (path exists in Tree_d){
 *                        increment count
 *                     }else{
 *                        add new partitionNode with count 1
 *                     }
 *
 *       {--- 5.3_PRUNING ---}
 *
 *       traverse Tree_d - for each node n
 *              if n.count >= min_sup
 *                  output result
 *              else
 *                  prune branch after n
 *
 *       add Tree_d to END of processedTreeList
 */
import java.util.*;
import java.io.*;
public class MTC {

    //Number of dimensions
    static final int DIM_NO = 6;
    public static ArrayList<dataEntry> entryList = new ArrayList<>();
    public static HashMap<String, HashMap<String, Integer>> singlePartitions = new HashMap<>();
    public static String[] dimensions, tempLineValues;
    public static ArrayList<APTree> processedTrees = new ArrayList<>();
    public static int totalNodes, totalPruned = 0;
    public static void main(String[] args) {

        Scanner minSupReader = new Scanner(System.in);


        File inputFile = new File("src/cleanTransportationData.csv");
        File outputFile = new File("src/icebergOutput.txt");
        String tempLine;

        System.out.println("Please enter the minimum support threshold: ");
        int minSup = minSupReader.nextInt();

        //Starting the timer for the algorithm
        long startTime = System.nanoTime();

        try {
            BufferedReader inputReader = new BufferedReader(new FileReader(inputFile));


            tempLine = inputReader.readLine();

            /* Step-1 */
            dimensions = tempLine.split(",");
            //System.out.println(inputReader.readLine().split(",")[5]);

            tempLine = inputReader.readLine();


            // Read in the data tuples
            while (tempLine != null){

                tempLineValues = tempLine.split(",");
                entryList.add(new dataEntry(tempLineValues[0], tempLineValues[1], tempLineValues[2], tempLineValues[3], tempLineValues[4], tempLineValues[5]) );

                tempLine = inputReader.readLine();
            }
            inputReader.close();
        }catch (IOException e){
            System.out.println("Can NOT read the CLEAN dataset");
        }

        run_MTC(minSup);

        //Stopping the timer
        long endTime = System.nanoTime();
        double execTime = (endTime - startTime) / 1000000.0;



        System.out.println("\nTotal number of nodes created: " + totalNodes);
        System.out.println("Total nuber of nodes pruned: " + totalPruned);
        System.out.printf("Execution lasted for: %.3f ms", execTime);

    }
    public static void run_MTC(int min_sup){
        HashMap <String, Integer> tempHashMap = new HashMap<>();
        String curDimension;
        /* Step-2 */
        // Count each partition

        for (dataEntry entry : entryList){

            for (int i=0; i < DIM_NO; i++){
                curDimension = dimensions[i];
                if (!singlePartitions.containsKey(curDimension)){
                    singlePartitions.put(curDimension, new HashMap<String, Integer>());
                }
                tempHashMap = singlePartitions.get(curDimension);


                String attrValue = entry.getDimension(i);


                //Add or increment the count of partition
                if (tempHashMap.containsKey(attrValue)){
                    tempHashMap.put(attrValue, (tempHashMap.get(attrValue)+1));
                    singlePartitions.put(dimensions[i], tempHashMap);
                }else {
                    tempHashMap.put(attrValue, 1);
                    singlePartitions.put(dimensions[i], tempHashMap );
                }

            }
        }

        /* Step-3 */
        //Constructing the tree for last dimension
        tempHashMap = singlePartitions.get(dimensions[(DIM_NO-1)]);

        AttributeNode initialRootNode = new AttributeNode(dimensions[(DIM_NO-1)]);
        APTree initialTree = new APTree(initialRootNode);

        for (String str : tempHashMap.keySet()){
            if ( tempHashMap.get(str) >= min_sup){

                PartitionNode childNode = new PartitionNode(str, tempHashMap.get(str));

                //System.out.println(childNode.attrName + " - " + childNode.count + "at the index: " + idx);
                initialTree.rootNode.addChildren(childNode);
            }
        }
        //Sorting the list of children
        Collections.sort(initialRootNode.childrenNodes);

        /* Step-4 */
        initialTree.displayTree();
        processedTrees.add(initialTree);

        /* Step-5 */
        // Loop to process all remaining dimensions in reverse order
        for (int i = (DIM_NO-2); i >=0 ; i--){

            curDimension = dimensions[i];
            AttributeNode rootNode = new AttributeNode(curDimension);
            APTree tree = new APTree(rootNode);

            HashMap<String, Integer> tempPartitionMap = singlePartitions.get(curDimension);

            for (String str: tempPartitionMap.keySet()){
                if (tempPartitionMap.get(str) >= min_sup){

                    PartitionNode childNode = new PartitionNode(str, tempPartitionMap.get(str));
                    tree.rootNode.addChildren(childNode);
                }
            }
            //Sorting the list of children
            Collections.sort(tree.rootNode.childrenNodes);

            //System.out.println("TREE: "+i); tree.displayTree();
            //Step-5.1
            for (dataEntry entry: entryList){

                //Finding the node corresponding to data entry - binary search on sorted list of children
                PartitionNode targetNode = new PartitionNode(entry.getDimension(i), 0);
                int idx = Collections.binarySearch(tree.rootNode.childrenNodes, targetNode);

                    // If location of the corresponding node found
                    if (idx >= 0){
                        PartitionNode partitNode = tree.rootNode.childrenNodes.get(idx);

                        //Step-5.2
                        // Searching previously processed trees for matching prefixes
                        for (APTree processedTree: processedTrees ){

                            ArrayList<PartitionNode> curPath = new ArrayList<>();
                            ArrayList<ArrayList<PartitionNode>> pathList = processedTree.findPrefixMatches(entry, (i+1), processedTree.rootNode, curPath);


                            // Extending the tree according to matched prefixes
                            for (ArrayList<PartitionNode> path : pathList){


                                System.out.print("PATH: ");
                                for (PartitionNode node : path){
                                    System.out.println(node.attrName+" - ");
                                }
                                System.out.println();

                                tree.updateOrExtendTree(partitNode, path, i);
                            }

                        }

                    }

            }
            // Step-5.3
            //Pruning
            tree.traversalMinSupCheck(tree.rootNode, min_sup);
            processedTrees.add(tree);



            System.out.println("TREE: "+i); tree.displayTree();

        }
        /* ------- Partition test ----------------
        int size=0;
        for (String str: singlePartitions.keySet()){
            HashMap <String, Integer> hashMap = new HashMap<>();
            hashMap = singlePartitions.get(str);

            for (String s : hashMap.keySet()){
                System.out.println( str + "\n\t" + s + " - " + hashMap.get(s));
            }
            size += singlePartitions.get(str).size();

        }
        System.out.println(size);*/

    }

    public static class PartitionNode implements Comparable<PartitionNode>{
        private String attrName;
        private int count;
        private ArrayList<AttributeNode> childrenNodes;

        public PartitionNode(String attrName, int count){
            this.attrName = attrName;
            this.count = count;
            this.childrenNodes = new ArrayList<>();

            //Increment counter at each node created
            totalNodes++;
        }
        public void addChildren(AttributeNode newNode){
            this.childrenNodes.add(newNode);
        }

        @Override
        // Making partition comparable based on the attribute value stored to sort to list
        public int compareTo(PartitionNode partitNode){
            return this.attrName.compareTo(partitNode.attrName);
        }

    }

    public static class AttributeNode{

        private String name;
        private ArrayList<PartitionNode> childrenNodes;

        public AttributeNode(String name){
            this.name = name;
            this.childrenNodes = new ArrayList<PartitionNode>();

            //Increment counter at each node created
            totalNodes++;
        }

        public void addChildren(PartitionNode newNode){
            this.childrenNodes.add(newNode);
        }




    }

    public static class APTree{
        private PartitionNode partitionNode;
        private AttributeNode attributeNode;
        private AttributeNode rootNode;

        public APTree(AttributeNode rootNode){
            this.rootNode = rootNode;
        }

        private void displayAttrNode(AttributeNode node, int level){
            if (node.childrenNodes.size() > 0){

                for (PartitionNode partitNode :node.childrenNodes){
                    for (int i=0; i < level; i++){
                        System.out.print("\t");
                    }
                    System.out.println("\t|_ " + partitNode.attrName + " - " + partitNode.count);

                    displayPartitNode(partitNode, (level+1));
                }
            }
        }
        private void displayPartitNode(PartitionNode node, int level){

            if (node.childrenNodes != null){

                for (AttributeNode attrNode : node.childrenNodes){
                    for (int i=0; i < level; i++){
                        System.out.print("\t");
                    }
                    System.out.println("\t|_ " + attrNode.name);

                    displayAttrNode(attrNode, (level+1));
                }
            }
        }

        public void displayTree(){

            System.out.println(this.rootNode.name);

            displayAttrNode(this.rootNode, 1);


        }

        public ArrayList<ArrayList<PartitionNode>> findPrefixMatches(dataEntry entry, int dimNo, AttributeNode attrNode, ArrayList<PartitionNode> curPath) {

            ArrayList<ArrayList<PartitionNode>> pathList = new ArrayList<>();

            if (dimNo >= DIM_NO){
                return pathList;
            }

            for (PartitionNode partitNode : attrNode.childrenNodes){

                if (partitNode.attrName.equals(entry.getDimension(dimNo))){
                    //Adding curPath if match found
                    curPath.add(partitNode);

                    //Adding curPath to the list of found paths
                    ArrayList<PartitionNode> tempCurPath = new ArrayList<>(curPath);
                    pathList.add(tempCurPath);

                    //Traversing tree(while alternating nodes)
                    if (partitNode.childrenNodes != null){
                        for (AttributeNode child : partitNode.childrenNodes){

                            ArrayList<ArrayList<PartitionNode>> innerPathList = findPrefixMatches(entry, (dimNo+1), child, curPath);
                            for (ArrayList<PartitionNode> innerPath : innerPathList){
                                pathList.add(innerPath);
                            }
                        }
                    }
                }
            }

            return pathList;
        }

        public void updateOrExtendTree(PartitionNode partitNode, ArrayList<PartitionNode> path, int dimNo){


            PartitionNode curNode = partitNode;

            for (int i=0; i < path.size(); i++){

                AttributeNode attrNode = null;
                PartitionNode pathNode = path.get(i);
                int curDimIdx = (dimNo + i +1);

                //Get or add(if not exist yet) attribute node
                if (curNode.childrenNodes != null){
                    for (AttributeNode n : curNode.childrenNodes){
                        if (n.name.equals(dimensions[curDimIdx])){
                            attrNode = n;
                            break;
                        }
                    }
                }

                if(attrNode == null){
                    attrNode = new AttributeNode(dimensions[curDimIdx]);
                    curNode.addChildren(attrNode);
                }

                //Update or add partition node
                PartitionNode newNode;
                if (attrNode.childrenNodes != null){

                    PartitionNode targetNode = new PartitionNode(pathNode.attrName, 0);
                    int idx = Collections.binarySearch(attrNode.childrenNodes, targetNode);

                    if (idx >=0){
                        attrNode.childrenNodes.get(idx).count++;
                        curNode = attrNode.childrenNodes.get(idx);
                    }else{
                        int insertPos = -(idx+1);
                        newNode = new PartitionNode(pathNode.attrName, 1);

                        attrNode.childrenNodes.add(insertPos, newNode);
                        curNode = newNode;
                    }

                }


            }
        }
        public void traversalMinSupCheck(AttributeNode attrNode, int minSup){

            ArrayList<PartitionNode> pruneList = new ArrayList<>();

            for (PartitionNode partitNode : attrNode.childrenNodes){
                //Traversing tree(while alternating nodes) and pruning branches starting from partition nodes that DON`T satisfy min_sup
                if (partitNode.count >= minSup){
                    if (partitNode.childrenNodes != null){
                        for (AttributeNode child : partitNode.childrenNodes){


                            traversalMinSupCheck(child, minSup);
                        }
                    }
                }else{
                    pruneList.add(partitNode);
                }
            }
            //Pruning the branch by reoving node (and thereby its children)
            attrNode.childrenNodes.removeAll(pruneList);

            totalPruned += pruneList.size();
        }
    }

    public static class dataEntry{
        String corridorName, direction, inStopName, outStopName, payCardBank, payCardGender;

        public dataEntry(String corridorName, String direction, String inStopName, String outStopName, String payCardBank,String payCardGender){
            this.corridorName = corridorName;
            this.direction = direction;
            this.inStopName = inStopName;
            this.outStopName = outStopName;
            this.payCardBank = payCardBank;
            this.payCardGender = payCardGender;

        }

        public String getDimension(int dimensionNo){
            switch (dimensionNo){
                case (0):
                    return this.corridorName;
                case (1):
                    return this.direction;
                case (2):
                    return this.inStopName;
                case (3):
                    return this.outStopName;
                case (4):
                    return this.payCardBank;
                case (5):
                    return this.payCardGender;
                default:
                    return null;

            }

        }
    }

}
