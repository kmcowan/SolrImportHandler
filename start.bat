 


set CLASSPATH=./SolrImportHandler.jar:lib/*.jar

 


java -jar -classpath $CLASSPATH SolrImportHandler.jar command=start secret=jsclosuresrules host=localhost port=8777