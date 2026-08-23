To test the complete program including Clean_DataSet class:
1- The original dataset named "dfTransjakarta.csv", Clean_Dataset.java and MTC.java should be placed in the same directory
for example project-folder> src folder > dfTransjakarta.csv, Clean_Dataset.java and MTC.java
2- Then run the following command to build & run data cleaning program commands:
    javac src/Clean_Dataset.java
    java src/Clean_Dataset
3- Then run the following commands to run MTC program from inside the project-folder directory:
    javac src/MTC.java
    java src/MTC

To test MTC program:
1- The clean dataset should be placed in the same directory as MTC.java program. For example, project-folder > src >
MTC.java, cleanDataset.csv
2- Then run the following commands to run MTC program from inside the project-folder directory:
       javac src/MTC.java
       java src/MTC

--> To test the program on datasets with different nuber of dimensions the global "DIM_NO" variable should be updated accordingly.