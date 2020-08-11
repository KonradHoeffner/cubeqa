# CubeQA—Question Answering on Statistical Linked Data

## Abstract
As an increasing amount of statistical data is published as RDF, intuitive ways of satisfying information needs and getting new insights out of this type of data becomes increasingly important.
Question answering systems provide intuitive access to data by translating natural language queries into SPARQL, which is the native query language of RDF knowledge bases.
Statistical data, however, is structurally very different from other data and cannot be queried using existing approaches.
Building upon a question corpus established in previous work, we created a benchmark for evaluating questions on statistical Linked Data in order to evaluate statistical question answering algorithms and to stimulate further research.
Furthermore, we designed a question answering algorithm for statistical data, which covers a wide range of question types.
To our knowledge, this is the first question answering approach for statistical RDF data and could open up a new research area.
Apart from providing evaluation results, we discuss future challenges in this field.

## Requirements
You need to have **Java 8**, Git and Maven 3 installed.
Clone the project via "`git clone https://github.com/AKSW/cubeqa.git`".

### IDE Setup
If you use an IDE, you also need to download and execute lombok.jar (doubleclick it, or run java -jar lombok.jar). Follow instructions.
That is because CubeQA uses [Project Lombok](http://projectlombok.org/), which removes much boilerplate from Java.

## Benchmark
CubeQA contains a benchmark ([View Benchmark](https://github.com/AKSW/cubeqa/tree/master/benchmark/qbench2.xml) | [View Results](https://github.com/AKSW/cubeqa/tree/master/benchmark/qbench2-results.csv)) that runs on 50 datasets of LinkedSpending ([Download](http://linkedspending.aksw.org/extensions/page/page/export/qbench2datasets.zip) | [Browse LinkedSpending](http://linkedspending.aksw.org)).
The benchmark source package is [`org.aksw.cubeqa.benchmark`](https://github.com/AKSW/cubeqa/tree/master/src/main/java/org/aksw/cubeqa/benchmark).

### Run the Evaluation yourself
We believe that good science should be open and reproducible. Feel free to verify our claims by running our evaluation yourself. Please [contact us](mailto:konrad.hoeffner@uni-leipzig.de?subject=CubeQA Evaluation&body=Dear%20Konrad,) if you encounter issues.

* install [OpenLink Virtuoso](http://virtuoso.openlinksw.com/) (a different triple store may work as well) on your machine and load the [datasets](http://linkedspending.aksw.org/extensions/page/page/export/qbench2datasets.zip) in the graphs `http://linkedspending.aksw.org/<dataseturi>` as well as `http://linkedspending.aksw.org/` (each dataset graph is a subgraph of `http://linkedspending.aksw.org/`.
Virtuoso needs to be accessible at localhost:8890 (default), but you can change this in org.aksw.cubeqa.CubeSparql.java.
* start Virtuoso
* run the main class org.aksw.cubeqa.scripts.EvaluateQBench2 via `mvn compile exec:java -Dexec.mainClass="org.aksw.cubeqa.scripts.EvaluateQBench2"`.

You will see the results on the console and also in the file `benchmark/qbench<timestamp>.csv`.

## Graphical User Interface
CubeQA can be used as a plugin for [openQA](https://bitbucket.org/emarx/openqa/wiki/FAQ), which offers a graphical user interface. 

## Warning: Research Prototype
While CubeQA is implemented in Java using Maven so it theoretically should run everywhere, it is under development, using snapshots and generally
of the status of a research prototype so I don't give any guarantee of it successfully running on your machine but I'm happy to help with your questions (best to open a new issue).

## License
The source code of CubeQA is freely available under the GPLv3 license (see the LICENSE file), which requires you to publish derivative works under the same license. If this creates a licensing conflict or for commercial usage, please [contact us](mailto:konrad.hoeffner@uni-leipzig.de?subject=CubeQA License&body=Dear%20Konrad,).
