#!/bin/sh

export LIB=./lib


export CLASSPATH=./solrimporthandler.jar

for i in `ls ./lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done

echo $CLASSPATH


java -classpath $CLASSPATH com.jsclosures.solr.SolrImportHandler command=status secret=jsclosuresrules host=localhost port=8777