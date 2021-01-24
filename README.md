# MetNet
Comparison of Metabolic Networks


DESCRIPTION

Metabolic pathway comparison and interaction between different species can detect important information for drug engineering and medical science. MetNet is a Java tool that makes it possible to automatically reconstruct the metabolic network of two organisms selected in KEGG and to compare their two networks both quantitatively and visually.
The first step consists of selecting two organisms to analyse. Subsequently, their metabolic data are retrieved from KEGG and the corresponding networks of metabolic functions are built. We propose a metabolism comparison method based on a two-level representation of metabolic networks: a structural level representing the metabolic network topology and a functional level representing the metabolic functions of each pathway. The approach is supported by similarity indexes for the comparisons at both levels. The comparison results are composed and presented to offer a comprehensive view of the similarities/differences of the two organisms.


CONTENTS

This repository contains:

1. README.md : this file
2. lib : folder of software libraries used by MetNet 
3. src : source files folder
4. manifest.txt: configuration file to create the executable jar file
5. organismList.txt : configuration file containing the list of organisms available in KEGG
6. pathwayList.txt : configuration file containing the list of patways to be considered for the comparison


EXECUTABLE FILES AVAILABILITY

The executable version of MetNet is available at www.dsi.unive.it/~biolab
(follow the "Software" link)


HOW TO COMPILE THE SOURCE FILES

MetNet can be compiled using the Java compiler from a terminal window. The actions to be performed are the following:

1. Open a terminal window and set the current directory to the MetNet's files folder;
2. To compile the source code, copy-paste the following command and press return:

javac -cp ./lib/gs-algo-1.3.jar:./lib/gs-core-1.3.jar:./lib/gs-ui-1.3.jar:./lib/guava-19.0.jar:./lib/poi-3.9.jar:./lib/sax2r2.jar -d ./ -sourcepath ./src ./src/MetNet/MetNet.java

ATTENTION: the jar files listed in the -cp option must be separated using a semi-colon(;) in a windows machine or a colon(:) on a linux machine. 

3. to generate the jar file, copy-paste the following command and press return:

jar cfm MetNet.jar manifest.txt MetNet/

 
HOW TO EXECUTE

To execute MetNet perform the following actions:

1. Open a terminal window and set the current directory on the tool's directory;
2. Copy-paste the following command and press return:

java -jar MetNet.jar

Please ensure that the configuration files organismList.txt and pathwayList.txt 
are in the tool's directory.

MetNet will start the execution and the user will be guided through the various steps of the comparison by the visual interface. 

To run MetNet as a command line tool, the following three parameters must be specified when starting the execution: 
1. KEGG code of the first organism to compare
2. KEGG code of the second organism to compare
3. comparison method: "set" or "multiset"

For instance, the command

java -jar MetNet.jar hsa ptr set

will start the execution for the organisms "hsa" (Homo Sapiens) and "prt" (Pan troglodytes) with the comparison method "set".
