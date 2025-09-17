## :warning: **CubeQA is not maintained and the benchmark SPARQL endpoint is not available anymore.**
This repository has been archived on 2025-09-17.

# CubeQA—Question Answering on Statistical Linked Data
[![test](https://github.com/AskNowQA/cubeqa/actions/workflows/test.yml/badge.svg)](https://github.com/AskNowQA/cubeqa/actions/workflows/test.yml)
[![License: GNU GPLv3](https://img.shields.io/badge/license-GPL-blue)](LICENSE)
[![JavaDoc](https://img.shields.io/badge/javadoc-here-green)](https://konradhoeffner.github.io/cubeqa)
[![SWH](https://archive.softwareheritage.org/badge/origin/https://github.com/KonradHoeffner/cubeqa/)](https://archive.softwareheritage.org/browse/origin/?origin_url=https://github.com/KonradHoeffner/cubeqa)

## Abstract
As an increasing amount of statistical data is published as RDF, intuitive ways of satisfying information needs and getting new insights out of this type of data becomes increasingly important.
Question answering systems provide intuitive access to data by translating natural language queries into SPARQL, which is the native query language of RDF knowledge bases.
Statistical data, however, is structurally very different from other data and cannot be queried using existing approaches.
Building upon a question corpus established in previous work, we created a benchmark for evaluating questions on statistical Linked Data in order to evaluate statistical question answering algorithms and to stimulate further research.
Furthermore, we designed a question answering algorithm for statistical data, which covers a wide range of question types.
To our knowledge, this is the first question answering approach for statistical RDF data and could open up a new research area.
Apart from providing evaluation results, we discuss future challenges in this field.

## Requirements
* CubeQA 1.0 requires **Java 11**, Git and Maven 3 installed.
* further versions may requirer higher Java versions.
* Clone the project via "`git clone https://github.com/AKSW/cubeqa.git`" to get the current state.
* You may checkout release 1.0 for a stable version that runs on Java 11.

### IDE Setup
If you use an IDE, you also need to download and execute lombok.jar (doubleclick it, or run java -jar lombok.jar). Follow instructions.
That is because CubeQA uses [Project Lombok](http://projectlombok.org/), which removes much boilerplate from Java.

## Benchmark
CubeQA contains a benchmark ([View Benchmark](https://github.com/AKSW/cubeqa/tree/master/benchmark/)) that runs on 50 datasets of LinkedSpending ([Download](https://github.com/KonradHoeffner/linkedspending/releases/download/data-qbench2datasets/qbench2datasets.zip) | [Browse LinkedSpending](https://linkedspending.aksw.org/)).
The benchmark source package is [`org.aksw.cubeqa.benchmark`](https://github.com/AKSW/cubeqa/tree/master/src/main/java/org/aksw/cubeqa/benchmark).

### Run the Evaluation yourself
We believe that good science should be open and reproducible. Feel free to verify our claims by running our evaluation yourself. Please [contact us](mailto:konrad.hoeffner@uni-leipzig.de?subject=CubeQA%20Evaluation&body=Dear%20Konrad,) if you encounter issues. 

* run the evaluation main classes e.g. for QALD6 Task 3 training set via `mvn compile exec:java -Dexec.mainClass="org.aksw.cubeqa.scripts.EvaluateQald6T3Train"`.
* You will see the results on the console and also in the file `benchmark/qbench<timestamp>.csv`.

The evaluation code and the JUnit tests are preconfigured to use the SPARQL endpoint <http://cubeqa.aksw.org/sparql> but but that is not active anymore.
You can install and load your own SPARQL endpoint and change the configuration to use your own endpoint as described below.

#### Load the Datasets into your own Virtuoso Endpoint
* install [OpenLink Virtuoso](http://virtuoso.openlinksw.com/) (a different triple store may work as well) on your machine and load the datasets (see below)
* download the [datasets](https://github.com/KonradHoeffner/linkedspending/releases/download/data-qbench2datasets/qbench2datasets.zip)
* upload the [LinkedSpending ontology](https://raw.githubusercontent.com/KonradHoeffner/linkedspending/master/schema/ontology.ttl) into graph <http://linkedspending.aksw.org/ontology/> and add that graph to the graph group <http://linkedspending.aksw.org/>  
* upload each <x>.nt file into graph `http://linkedspending.aksw.org/<x>` and add them to graph group <http://linkedspending.aksw.org/>
* you can automate this with the `virtloadbench` script adapted to your use case 
* then go to the folder containing the dataset ntriples files and execute the shell command `ls | sed "s|\\.nt||" | xargs -I @ virtloadbench @.nt http://linkedspending.aksw.org/@`
* alternative virtload scripts are at <https://github.com/SmartDataAnalytics/aksw-commons/tree/master/aksw-commons-scripts/virtuoso>
* in http://<yourendpoint>/conductor add prefixes qb: <http://purl.org/linked-data/cube#>, ls: <http://linkedspending.aksw.org/instance/> and lso: <http://linkedspending.aksw.org/ontology/>
* set the URI, such as "localhost:8890" (default) in org.aksw.cubeqa.CubeSparql.java.
* start Virtuoso
 

## Graphical User Interface
CubeQA can be used as a plugin for [openQA](https://bitbucket.org/emarx/openqa/wiki/FAQ), which offers a graphical user interface. 

## Warning: Research Prototype
While CubeQA is implemented in Java using Maven so it theoretically should run everywhere, it is under development, using snapshots and generally
of the status of a research prototype so I don't give any guarantee of it successfully running on your machine but I'm happy to help with your questions (best to open a new issue).
CubeQA was part of my PhD thesis and is not my current research topic, so I can perform maintenance only very rarely.
Due to the large amount of progress, especially using LLMs, this approach is probably not competitive anymore so I just keep it available for historical purposes or as a reference point for future work.
Successful compilation (of the main code, the tests did not compile) was last checked with `mvn compile` on 2025-09-17 using Java 24, but the benchmark was not run as the SPARQL endpoint is not online anymore.
If you want to know more about current research, I recommend reading "R. Cocco, M. Atzori, and C. Zaniolo. Machine learning of SPARQL templates for Question Answering over LinkedSpending. In 2019 IEEE 28th International Conference on Enabling Technologies:
Infrastructure for Collaborative Enterprises (WETICE), pages 156–161, 06 2019." ([IEEE page](https://ieeexplore.ieee.org/document/8795383), [PDF](http://ceur-ws.org/Vol-2400/paper-22.pdf)). 

## License
The source code of CubeQA is freely available under the GPLv3 license (see the LICENSE file), which requires you to publish derivative works under the same license. If this creates a licensing conflict or for commercial usage, please [contact us](mailto:konrad.hoeffner@uni-leipzig.de?subject=CubeQA%20License&body=Dear%20Konrad,).
