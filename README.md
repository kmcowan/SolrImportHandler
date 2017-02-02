# SolrImportHandler
A small command-line, properties-driven application that allows you to move collections from one Solr instance to another. 
To use:
    You can either run SolrImportHandler directly using java SolrImportHandler.jar
    Alternatively, you can use one of the shell scripts provided.  
    The SolrImportHandler supports the following commands:
    init/start/shutdown/status/restart/help
    
    You can set a default action in the properties file.  NOTE:  Using the shell scripts will overwrite the default values
    in the properties file. 
    
    Default Values:
maximumloglevel=1
command=help
host=localhost
port=8777
solrin=http://localhost:8983/solr/name-of-your-collection
solrout=http://localhost:8983/solr/name-of-your-collection
csolrout=localhost:2181
mode=http
uniquekey=id
batchsize=10000
maxworkqueuesize=20

You can override any of these values by passing in a key=value arugment when you launch the .jar file.  E.g. java -jar SolrImportHandler.jar port=0000

You MUST change the values of 'solrin' and 'solrout' to point to your respective instances and/or collections.  If you're using 
localhost, you'll only need to change the final piece of the url to the name of your collection. 
