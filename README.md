#CubeQA—Question Answering on Statistical Linked Data#
##Abstract##
As an increasing amount of statistical data is published as RDF, intuitive ways of satisfying information needs and getting new insights out of this type of data becomes increasingly important.
Question answering systems provide intuitive access to data by translating natural language queries into SPARQL, which is the native query language of RDF knowledge bases.
Statistical data, however, is structurally very different from other data and cannot be queried using existing approaches.
Building upon a question corpus established in previous work, we created a benchmark for evaluating questions on statistical Linked Data in order to evaluate statistical question answering algorithms and to stimulate further research.
Furthermore, we designed a question answering algorithm for statistical data, which covers a wide range of question types.
To our knowledge, this is the first question answering approach for statistical RDF data and could open up a new research area.
Apart from providing evaluation results, we discuss future challenges in this field.
##Benchmark##
CubeQA contains a benchmark which runs on the LinkedSpending Finland-Aid Dataset ([Download](http://linkedspending.aksw.org/extensions/page/page/export/finland-aid.nt.zip) | [Browse on LinkedSpending](http://linkedspending.aksw.org/view/r/ls%3Afinland-aid)).
The benchmark source package is [`org.aksw.cubeqa.benchmark`](https://github.com/AKSW/cubeqa/tree/master/src/main/java/org/aksw/autosparql/cube/benchmark).

##Warning: Research Prototype##
While CubeQA is implemented in Java using Maven so it theoretically should run everywhere, it is under development, using snapshots and generally
of the status of a research prototype so I don't give any guarantee of it successfully running on your machine but I'm happy to help with your questions (best to open a new issue).
At the moment, our Maven repository is acting up so you may get errors "unable to resolve artifact" or "error opening zip file".
