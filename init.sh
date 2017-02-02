#!/bin/sh

export LIB=./lib


export CLASSPATH=./solrimporthandler.jar

for i in `ls ./lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done

echo $CLASSPATH


java -classpath $CLASSPATH com.jsclosures.solr.SolrImportHandler command=init secret=jsclosuresrules host=localhost port=8777