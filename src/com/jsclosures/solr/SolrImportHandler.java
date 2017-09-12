package com.jsclosures.solr;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CursorMarkParams;

/**
 *


 */
public class SolrImportHandler implements DoThreadListener {
    public static String SECRETCODE = "jsclosuresrules";
    public static String START = "start";
    public static String SHUTDOWN = "shutdown";
    public static String RESTART = "restart";
    public static String STATUS = "status";
    public static String INIT = "init";
    public static String TEST = "test";
    public static String HELP = "help";
    private Properties props = null;
    
    private DataBean configuration = new DataBean();
    public DataBean getConfiguration(){
        return( configuration );
    }
    
    public String getConfiguration(String which){
        return( getConfiguration().getString(which) );
    }
    
    public void writeLog(int level, String message) {
        if (level <= getConfiguration().getInt("maximumloglevel") ) {
            Helper.writeLog(1,message);
        }
    }
    
    public void setConfiguration(String name,String value){
        configuration.setValue(name,value);
    }
    
    public void setConfiguration(String name,int value){
        configuration.setValue(name,value);
    }
    
    public void setConfiguration(DataBean conf){
        this.configuration = conf;
    }
    
    private DataBean parseMessage(String json){
        DataBean result = Helper.parseJSON(json);

        return( result );
    }

    private String readFromInput(BufferedReader input){
        StringBuilder result = new StringBuilder();
        
        try {
            int buffer;
            
            while( (buffer = input.read()) != -1 ){
                result.append((char)buffer);
            }
        }
        catch(Exception e){
            result.append(e.toString());
        }
        
        return( result.toString() );
    }
    

    public void server(int port) throws BindException, IOException
    {

      ServerSocket serv = new ServerSocket( port );
      boolean isExit = false;

      while (true) {
              
            Socket sock = serv.accept();
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
          
            String jsonInput = readFromInput(r);
            
            PrintWriter w = new PrintWriter(sock.getOutputStream(), false); // no autoFlush
            Helper.writeLog(1,"Server reading input");
            Helper.writeLog(1,"Server input: " + jsonInput);
          
            DataBean tCmd = parseMessage(jsonInput);
            String istr = "";
          
            if( tCmd.isValid("command") && tCmd.getString("secret").equals(SECRETCODE) )
                istr = tCmd.getString("command");
            
            if (istr.equals(SHUTDOWN)) {
                Helper.writeLog(1,"received: " + SHUTDOWN);
                w.println("server is shutting down.  Please wait.  This may take several minutes.  See the log for details ...\n");
                w.flush();
                doProcessing = false;
                processShutdown();
                isExit = true;
                w.println("application shut down...\n");
            } else if (istr.equals(RESTART)) {
                Helper.writeLog(1,"received: " + RESTART);
                w.println("Application is restarting...\n");
                processRestart();
            } else if (istr.equals(INIT)) {
                Helper.writeLog(1,"received: " + RESTART);
                w.println("Application is initializing...\n");
                processStart();
            } else if (istr.equals(TEST)) {
                Helper.writeLog(1,"received: " + TEST);
                w.println("Application is testing...\n");
                processTest();
            } else if(istr.equals(STATUS)) {
                Helper.writeLog(1,"received: " + STATUS);
                w.println( "OK");
            }  else {
                Helper.writeLog(1,"received unknown command: " + istr);
                w.println("received unknown command: " + istr + "\n");
            }

            w.flush();
            r.close();
            w.close();
            sock.close();
            
            if(isExit)
                break;
        }

      serv.close();
      System.exit(0);
    }
    
    public void client(String host,int port,String cmd) throws IOException
    {
       Helper.writeLog(1,"openning client socket host: " + host + " port: " + port + " cmd: " + cmd);
       
       Socket sock = new Socket(host, port);
       PrintWriter w = new PrintWriter(sock.getOutputStream(), true); // autoFlush
       BufferedReader r = new BufferedReader(new InputStreamReader(sock.getInputStream()));
       DataBean cRec = new DataBean();
       cRec.copyFrom(getConfiguration());
       
       cRec.setValue("command",cmd);
       cRec.setValue("secret",SECRETCODE);
       String json = Helper.toJson(cRec);
       Helper.writeLog(1,"json: " + json);
       w.println(json);
       
       if( r.ready() ){
            while (true)
            {
                String clientCommand = r.readLine();
    
                if (clientCommand != null)
                    Helper.writeLog(1,clientCommand);
                else
                    break;
            }
       }

        r.close();
        //sock.close();
        w.close();
         
    }
    public void processTest(){
       

    }
    
    public void processArgs(String[] args) {
      /*  setConfiguration("maximumloglevel",1);
        setConfiguration("command",START);
        setConfiguration("host","localhost");
        setConfiguration("port","8777");
        setConfiguration("solrin","http://solr.jsclosures.com:8983/solr/zen");
        setConfiguration("solrout","http://solr.jsclosures.com:8983/solr/zenshard");
        setConfiguration("csolrout","solr.jsclosures.com:2181");
        setConfiguration("mode","http");
        setConfiguration("uniquekey","id");
        
        setConfiguration("batchsize",10000);
        setConfiguration("maxworkqueuesize",20);
        */
        // load defaults from external properties file
        props = getProperties(); //new Properties();
        try{
       // props.load(new FileReader(new File("solrimporthandler.properties")));
        Iterator iter = props.keySet().iterator();
        String key, value;
        while(iter.hasNext()){
            key = (String)iter.next();
            value = props.getProperty(key);
            System.out.println("Set Property: "+key+" "+value);
            setConfiguration(key,value);
        }
        
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
        for (int i = 0; i < args.length; i++) {
            int idx = args[i].indexOf("=");
            
            if( idx > -1 ){
                String name = args[i].substring(0,idx);
                String value = args[i].substring(idx+1);
                setConfiguration(name,value);
            }
        }

        MAXWORKERQUEUESIZE = getConfiguration().getInt("maxworkqueuesize");
        
        writeLog(1,getConfiguration().toString());
    }
    
    private Properties getProperties(){
        if(props == null){
            try{
                props = new Properties();
                 props.load(new FileReader(new File("solrimporthandler.properties")));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return props;
    }
    
    private void printProperties(){
         try{
             props = getProperties();
       // props.load(new FileReader(new File("solrimporthandler.properties")));
        Iterator iter = props.keySet().iterator();
        String key, value;
        while(iter.hasNext()){
            key = (String)iter.next();
            value = props.getProperty(key);
            System.out.println("Property Option: Key: "+key+"  Default Value: "+value);
            
        }
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     *   BatchProcess start localhost 8777
     * @param args
     */
    public static void main(String[] args)
    {
        SolrImportHandler runner = new SolrImportHandler();
        runner.processArgs(args);
        
        String command = runner.getConfiguration("command");
        String host = runner.getConfiguration("host");
        int port = runner.getConfiguration().getInt("port");
        
        boolean isStart = false;
        boolean isRestart = false;
        boolean isStatus = false;
        boolean isValid = true;
        
        if ( true ) {
            if (command.equalsIgnoreCase("shutdown")){
                command = SHUTDOWN;
            }
            else if(command.equalsIgnoreCase("status")) {
                command = STATUS;
                isStatus = true;
            } else if(command.equalsIgnoreCase("restart")) {
                command = RESTART;
            }
            else if( command.equalsIgnoreCase("start") ){
                command = START;
                isStart = true;
            } else if(command.equalsIgnoreCase("help")){
                  System.out.println("Solr Import/Exporter Available Options: ");
                  runner.printProperties();
                  System.exit(0);
                
                        
            } else {
                System.out.println("Invalid Command.  Please use one of: command=init|start|shutdown|status|restart|help");
                System.exit(0);
            }
        }
        
        if( port > -1 ) {
            if( !isStart ){
                boolean needsStarted =  false;
                
                try {
                    runner.client(host, port,command);
                } catch(IOException ex) {
                  String showCommand = command;
                  if ( showCommand != null)
                    showCommand = command;
                    Helper.writeLog(1,"unable to send command: '" + showCommand + "' stack: " + ex.toString());
                    if( isStatus ){
                        needsStarted = true;
                    }
                }
                
                if( isRestart ){
                    try {
                        command = START + "@" + SECRETCODE;
                        
                        runner.client(host, port,command);
                    } catch(IOException ex) {
                      String showCommand = command;
                      if ( showCommand != null)
                        showCommand = command;
                        Helper.writeLog(1,"unable to send command: '" + showCommand + "' stack: " + ex.toString());
                    }
                }

                if( needsStarted ){
                    runner.startServer(port);
                }
                else {
                    System.exit(0);
                }
            }
            else {
                runner.startServer(port);
            }
        }

    }
    
    public void startServer(int port){
        try {
            Helper.writeLog(1,"starting server");
            this.server(port);
        }
        catch(Exception e){
            Helper.writeLog(1,"Server start exception");
            System.exit(0);
        }
    }
    private boolean  doProcessing = true;
    
    public void processShutdown(){
        //when shutdown is called
        destroyWorkerQueue();
    }
    
    public void processRestart(){
        //when restart is called
        destroyWorkerQueue();
        initWorkerQueue();
    }
    
    public void processStart(){
        //when start is called
        initWorkerQueue();
        
        launchTimer(new DataBean());
    }
    
    public void startImport(){
        SolrServer server = new HttpSolrServer(getConfiguration("solrin"));
        //SolrServer outServer = new HttpSolrServer("http://solrtest.aws.cmgdigital.com:8983/solr/medleyshard");
        
            SolrQuery q = new SolrQuery("*:*");
            q.setFields("*");
            q.setRows(getConfiguration().getInt("batchsize"));
            q.setSort(SolrQuery.SortClause.desc(getConfiguration("uniquekey")));

            // You can't use "TimeAllowed" with "CursorMark"
            // The documentation says "Values <= 0 mean 
            // no time restriction", so setting to 0.
            q.setTimeAllowed(0);

            String cursorMark = CursorMarkParams.CURSOR_MARK_START;
            boolean done = false;
            QueryResponse rsp = null;
               
            while (!done) {
              q.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
              try {
                rsp = server.query(q);
              } catch (SolrServerException e) {
                e.printStackTrace();
                return;
              }
                
              //writeOutUniqueIds(rsp, zos);
                DataBean payload = new DataBean();
                //payload.setObject("server",outServer);
                payload.setObject("items",rsp.getResults());
                
              queueWork(payload);
                
              String nextCursorMark = rsp.getNextCursorMark();
              if (cursorMark.equals(nextCursorMark) && doProcessing ) {
                done = true;
              } else {
                cursorMark = nextCursorMark;
              }
            }
    }
    
    public void doThreadComplete(DoThread t) {
        try {
            if( t.getType().equalsIgnoreCase("worker") ){
                workerQueue.offer(t);
            }
           
            //writeLog(1,"adding back worker: " + t);
        } catch (Exception e) {
            writeLog(-1, "thread reloading error: " + e.toString());
        }
    }
    
    private BlockingQueue workerQueue;
    private int MAXWORKERQUEUESIZE = 1;
    private void initWorkerQueue() {
        workerQueue = new ArrayBlockingQueue(MAXWORKERQUEUESIZE);

        try {
            for (int i = 0; i < MAXWORKERQUEUESIZE; i++) {
                DoThread newWorker = new WorkerThread();
                newWorker.setContext(this);
                newWorker.setType("worker");
                newWorker.addDoThreadListener(this);
                newWorker.setSleepInterval(500);
                newWorker.start();
                writeLog(1, "loading worker: " + i);
                workerQueue.offer(newWorker);
            }
        } catch (Exception e) {
            writeLog(-1, "thread loading error: " + e.toString());
        }
    }
    
    private void destroyWorkerQueue() {
        writeLog(1,"start destroy worker queue");
        
        try {
            for (;;) {
                if (workerQueue.size() == MAXWORKERQUEUESIZE) {
                    break;
                } else {
                    Thread.currentThread().sleep(100);
                }
            }
        } catch (Exception e) {
            writeLog(-1, "thread destroy error: " + e.toString());
        }
        
        writeLog(1,"after flush destroy worker queue");
    }
    
    public DoThread getNextWorker() {
        DoThread result = null;
        try {

            result = (DoThread) workerQueue.take();
            //System.out.println("getting worker: " + result);
        } catch (Exception e) {
            writeLog(-1, "thread getting next error: " + e.toString());
        }
        return (result);
    }
    
    public void queueWork(DataBean args){
        DoThread newWorker = getNextWorker();
        newWorker.setTarget(args);
        newWorker.setRunning(true);
    }
    
    public void launchTimer(DataBean args){
        java.util.Timer tTimer = new java.util.Timer();
        tTimer.schedule(new DoTimerTask(this,args), 1000);
    }
    
    private class DoTimerTask extends java.util.TimerTask {
        private SolrImportHandler context;
        private DataBean queryArgs;
        
        public DoTimerTask(SolrImportHandler context, DataBean queryArgs){
            super();
            
            this.context = context;
            this.queryArgs = queryArgs;
            
        }
        
        public void run() {
            Helper.writeLog(1,"Timer Fired: " + queryArgs.toString());
            startImport();
        }
    }
    
    
    public class WorkerThread extends DoThread {
        public WorkerThread() {
            super();
        }
        public boolean doOperation() {
            boolean result = true;

            DataBean target = getTarget();
            
            SolrServer server;
            
            if( getConfiguration("mode").equalsIgnoreCase("http") ){
                server = new HttpSolrServer(getConfiguration("solrout")); 
            }
            else {
                server = new CloudSolrServer(getConfiguration("csolrout"));
            }
            SolrDocumentList docs = (SolrDocumentList)target.getObject("items");
            if( server != null && docs != null ){
                Helper.writeLog(1,"process  " + docs.size() + " docs");
                Collection<SolrInputDocument> docList = new ArrayList<SolrInputDocument>();
                SolrInputDocument doc;
                SolrDocument inDoc;
                
                for (int i = 0; i < docs.size(); i++)
                {
                    inDoc = docs.get(i);
                    inDoc.removeFields("_version_");
                    doc = org.apache.solr.client.solrj.util.ClientUtils.toSolrInputDocument(inDoc);    
                    docList.add(doc);
                }
                try{
                server.add(docList);
                    server.commit();
                    server.shutdown();
                }
                catch(Exception e){
                    writeLog(1,"solr error: " + e.toString());
                }
            }
            
            return (result);
        }
        
        public void cleanUp() {
            //noop default
            setRunning(false);
            
            notifyThreadListeners();
        }
    }
    
    
}
