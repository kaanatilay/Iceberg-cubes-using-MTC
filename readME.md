MTC Algorithm
-

The Multiple-Tree Cubing (MTC) algorithm integrates shared computation and a pruning technique similar to Apriori algorithm to avoid unnecessary processing(Li et al., 2009). The algorithm relies on Attribute-Partition Trees (AP-Trees) that organizes data according to the established relations between dimensions. The AP-Tree is constructed by alternating between 2 different types of nodes at each level: Attribute Node and Partition Node. 
-	The attribute node represents dimensions and stores the dimension name along with at least one child partition node.
-	The Partition nodes represent distinct values of a dimension and store the corresponding value, its frequency count and potentially child attribute nodes. 

This intertwined structure combined with a minimum support threshold, allows the system to efficiently mine relevant relations and patterns across dimensions. Furthermore, attribute values are stored in lexicographical order to support efficient tree search.

The algorithm starts by performing one full scan of the dataset and storing the dataset in object-tuples to prioritize in-memory processing over multiple scans. Then, it processes each dimension in the reverse order, constructing an AP-Tree for each dimension. During this process, the algorithm maintains a list of previously processed trees which are reused to avoid repeated computations. Specifically, the MTC algorithm uses the previously computed trees as prefixes (matching paths) to extend or update the current tree for each dimension. After that, the algorithm  traverses the tree to prune branches extending from a partition nodes that does not satisfy the minimum support threshold. The generated tree is then added to the list of processed trees to be used  remaining iterations.

Algorithm Implementation
-
	
Authors Li et al. (2009) provides a pseudocode alongside the description of the MTC algorithm. I started by constructing my own version of the pseudocode, identifying each major step while closely following the original description. 

The AP-Tree implementation consists of three classes: “AttributeNode”, “PartitionNode”, and a connector class “APTree”. The AttributeNode class stores the dimension name and list of partition nodes, while PartitionNode class stores partition value, its frequency, and potentially list of attribute nodes as children. The APTree class includes a constructor along with helper functions that perform operations such as extending, updating or searching the tree or displaying the complete tree.

The implementation begins by scanning the dataset once to extract the dimension values and store corresponding data as in-memory data-tuples using custom “dataEntry” objects, where each attribute represents a partition value of a dimension. Program stores the constructed dataEntry objects in a global list, along with a list of dimensions values. It then, runs the MTC algorithm through a method called “run_MTC”. This method takes in the minimum support threshold which is used to invoke necessary operations of the algorithm. The run_MTC method first uses a nested HashMap structure to compute the distinct partitions of each dimension with their frequency counts. After that, it constructs the first AP-Tree based on the last dimension using only the valid partitions, partitions that satisfy the minimum support threshold. The constructed 1-level tree is then sorted lexicographically before being added to the globally maintained processed tree list. After sorting the first tree, the algorithm iteratively constructs AP-Trees for the remaining dimensions, in reverse order. This process starts by constructing first level of each tree using the current dimension as root-attribute node and valid corresponding partitions as partition nodes. After that, MTC performs a binary search on previously sorted nodes of the tree to find matching nodes with each data-tuple. For each matched node algorithm performs the following 3 operations:

    1)	It searches the previously processed trees to determine the list of matched prefixes extending from the current tree’s nodes. The implementation invokes the “findPrefixMatches” method from APTree class to perform a depth-first search(DFS) that tracks the prefixes used to traverse to a node and stores them in a list. The list of prefixes is returned after method traverses the tree depth-first and alternating between nodes. 

    2)	The algorithm then invokes the “updateorExtendTree” method that performs another binary search on sorted list of children nodes to find or insert nodes according to the matched prefixes. The binary search method provided by java.util libraries efficiently searches the already sorted tree to determine the index of a target node or the location it should be inserted in to, if the target does not exist in the list (GeeksforGeeks, 2025). Using this logic, the implementation updates or extends the tree based on identified prefix paths in the previous step. 

    3)	Finally, the implementation traverses the constructed tree and each branch to prune the branches extending from partition nodes that do not support minimum threshold. The implementation avoids unnecessary computation by checking whether the partition frequency satisfies minimum support threshold before going in the deeper nodes.
    The implementation keeps running until all dimensions are processed iteratively and the corresponding AP-Trees are constructed.

Experimental Results
-
The constructed AP-Trees reveal hidden patterns across the dataset that are not easily recognizable using traditional database systems. The AP-Tree representation of the data, combined with minimum support threshold, allows the MTC algorithm to reflect meaningful relationships that support decision-making. 

To analyze the efficiency of the algorithm, two counters respectively tracks the total number of nodes created and pruned along with a nanosecond timer. Then, I evaluated the implemented MTC using datasets with different sizes, executing the program three times for each dataset to determine average run-time.


Dataset Size (# of rows written while cleaning dataset)	| # of nodes created | # of nodes pruned | Avg. run-time (ms)
|:-----------------------------------------------------:|:------------------:|:-----------------:|:----------------:|
|1000 |	44,794 | 27 | 252.437
10,000 | 387,703 | 69 | 830.513
25,000 | 896,441 | 2045 | 1536.034
Full dataset | 990,584 | 3328 | 1744.843

The increasing number of nodes created as the dataset size grows indicates algorithm identifies significantly more relations and patterns in larger datasets. Additionally, the relatively slower growth of average run-time compared to the increase in number of created nodes reflects that the algorithm efficiently processes large-scale multi-dimensional datasets.



To test the complete program including Clean_DataSet class:
-
1- The original dataset named "dfTransjakarta.csv", Clean_Dataset.java and MTC.java should be placed in the same directory
for example project-folder> src folder > dfTransjakarta.csv, Clean_Dataset.java and MTC.java

2- Then run the following command to build & run data cleaning program commands:
    javac src/Clean_Dataset.java
    java src/Clean_Dataset
3- Then run the following commands to run MTC program from inside the project-folder directory:
    javac src/MTC.java
    java src/MTC

To test MTC program:
-
1- The clean dataset should be placed in the same directory as MTC.java program. For example, project-folder > src >
MTC.java, cleanDataset.csv

2- Then run the following commands to run MTC program from inside the project-folder directory:
       javac src/MTC.java
       java src/MTC

--> To test the program on datasets with different nuber of dimensions the global "DIM_NO" variable and target columns should be updated accordingly.

*References*
- 
- Li, X., Hamilton, H. J., Karimi, K., & Geng, L. (2009). The Multi-Tree Cubing    algorithm for computing iceberg cubes. Journal of Intelligent Information Systems, 33(2), 179–208. https://doi.org/10.1007/s10844-008-0074-3
- Renanda, D. (2023). Transjakarta - Public Transportation Transaction. Kaggle/datasets. https://www.kaggle.com/datasets/dikisahkan/transjakarta-transportation-transaction
- GeeksforGeeks. (2023, Feb. 1). Data Cube or OLAP Approach in Data Mining. https://www.geeksforgeeks.org/data-analysis/data-cube-or-olap-approach-in-data-mining/
- GeeksforGeeks. (2025, Jul. 23). Collections.binarySearch() in java with examples. https://www.geeksforgeeks.org/java/collections-binarysearch-java-examples/
