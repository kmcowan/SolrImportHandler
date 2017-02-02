package com.jsclosures.solr;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;


/**
A generalized bean to create a set of attributes or collections.

 */

public class Helper extends DataBean
{
     
     public Helper()
     {
          super();
     }
     public static String encode(String str){
         String result = str;
         
         try {
             result = URLEncoder.encode(str,"UTF-8");
         }
         catch(Exception e){
             writeLog(1,e.toString());
         }
         
         return( result );
     }
     
     public static void concatLists(ArrayList<DataBean> dest,ArrayList<DataBean> src)
     {
          //for(int i = 0,size = src.size();i < size;i++)
          //     dest.add(src.get(i));
               
          dest.addAll(src);
     }
     
     
     public static boolean DEBUG = true;
     
    private static final LogManager logManager = LogManager.getLogManager();
    public static final Logger log = Logger.getLogger( Helper.class.getName() );
    /*static{
            try {
                logManager.readConfiguration(new FileInputStream("C:\\Apps\\apache-tomcat-7.0.42\\conf\\log\\logging.properties"));
                log.fine("loaded log conf file");
                System.out.println("loaded log conf file");
            } catch (IOException exception) {
                System.out.println("Error in loading configuration"+exception);
            }
        }*/

    public static void writeLog(int level, String message) {

        /*if( level == 1 ){
            log.log( Level.FINE, message );
        }
        else if( level == 2 ){
            log.log( Level.FINER, message );
        }
        else if( level == 3 ){
            log.log( Level.FINEST, message );
        }
        else {
            log.log( Level.INFO, message );
        }*/
        
          if( DEBUG ) {
               if(message == null)
               {
                    message = "null message";
               }
			
                System.out.println(message);
               
                if( true ){ 
                    try
                   {
                        //java.io.FileOutputStream errorLog = new java.io.FileOutputStream("C:\\temp\\rest.txt",true);
                        //java.io.FileOutputStream errorLog = new java.io.FileOutputStream("/tmp/rest.txt",true);
                        java.io.FileOutputStream errorLog = new java.io.FileOutputStream(File.pathSeparator.equalsIgnoreCase(";") ? "c:\\temp\\gwrest.txt" : "/tmp/rest.txt",true);
    
                        java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(errorLog));
         
                        bw.write(message,0,message.length());
                        bw.write("\n");
                        bw.close();
         
                        errorLog.close();
                   }
                   catch(java.io.IOException e)
                   {
                        System.out.println(e.toString());
                   }
                }
          }
     }
     
     
     public static int parseInt(String v)
     {
         int result = -1;
         
         try{
             result = Integer.parseInt(v);
         }
         catch(Exception e){
             
         }
          return( result );
     }
     
    public static long parseLong(String v)
    {
        long result = -1;
        
        try{
            result = Long.parseLong(v);
        }
        catch(Exception e){
            
        }
         return( result );
    }
     
     public static ArrayList<String> splitFields(String sSrc,String sDelim)
     {
          ArrayList<String> retval = new ArrayList<String>();
          int nd = sDelim.length();
          int ns = sSrc.length();
          int i = 0;
          int j = 0;
          boolean bf = false;
          char q = '\"';

          if(sDelim.length() != 0 && ns != 0 )
          {
               while(j + nd <= ns)
               {
                    if(sSrc.charAt(j) == q)
                    {
                         bf = !bf;

                    }
                    if(sSrc.substring(j,j + nd).equals(sDelim) && !bf)
                    {
                         retval.add(sSrc.substring(i,j));
                         j += nd;
                         i = j;
                    }
                    else
                    {
                         j++;
                    }
               }
               retval.add(sSrc.substring(i));
          }

          return retval;
     }

     public static ArrayList<String> simpleSplitFields(String sSrc,String sDelim)
     {
          ArrayList<String> retval = new ArrayList<String>();
          int nd = sDelim.length();
          int ns = sSrc.length();
          int i = 0;
          int j = 0;
          boolean bf = false;
          char q = '\"';

          if(sDelim.length() != 0)
          {
               while(j + nd <= ns)
               {
                    if(sSrc.substring(j,j + nd).equals(sDelim))
                    {
                         retval.add(sSrc.substring(i,j));
                         j += nd;
                         i = j;
                    }
                    else
                    {
                         j++;
                    }
               }
               retval.add(sSrc.substring(i));
          }

          return retval;
     }
     
     public static DataBean readFromRESTURL(String url)
     {
    	 String json = readFromURL(url);
    	 
    	 
    	 return( parseJSON(json) );
     }
     public static String readFromURL(String url)
     {
          StringBuffer result = new StringBuffer();
          try
          {
               URL u = new URL(url);
               InputStream in = u.openStream();
               InputStreamReader uin = new InputStreamReader(in);
               BufferedReader fin = new BufferedReader(uin);
     
               String buffer;
               while((buffer = fin.readLine()) != null)
               {
                    result.append(buffer);
                    
               }
     
               uin.close();
               in.close();
          }
          catch(IOException e)
          {
               result.append("Error: " + e.toString() + " " + e.getMessage());
          }
          return(result.toString());
     }
     
     
     public static ArrayList<DataBean> cloneList(ArrayList<DataBean> src)
     {
          ArrayList<DataBean> result = new ArrayList<DataBean>(src.size());
          
          for(int i = 0,size = src.size();i < size;i++)
               result.add(src.get(i));
               
          return( result );
     }
	
	/**
	 *use to include the default
	 * version=1.0
	 * @param context
	 * @return
	 */
	public static DataBean getDefaultArguments(Object context)
	{
		DataBean args = new DataBean();
		args.setValue("version","1.0");
		
		return( args );
	}
	
	
	
	public static String readPut(InputStream in)
	{
		StringBuffer result = new StringBuffer();

		try
		{
			DataInputStream din = new DataInputStream(in);
			int dSize = in.available();
			int size;
			int blockSize = 1024;

			byte buffer[] = new byte[blockSize];


			int i;
			for (; ; )
			{
				size = din.read(buffer, 0, blockSize);

				for (i = 0; i < size; i++)
				{
					result.append((char) buffer[i]);

				}
				if (size < 0)
				{
					break;
				}
			}
		}
		catch (Exception e)
		{
			writeLog(1, "Read Error: " + e.toString());

		}

		return (result.toString());
	}

    public static DataBean parseJSON(String json){
        //writeLog(1,"parse: " + json);
       DataBean result = new DataBean();
       
       try {
           JSONObject obj = new JSONObject(json);
           result = parseJSON(obj);
       }
       catch(Exception e){
           result.setValue("error","parse error: " + e.toString());
           writeLog(1,result.getString("error"));
       }
       
        //setStructure("current",result);
        
       return( result );
    }
    
    
    public static DataBean parseJSON(JSONObject json) throws Exception {

        DataBean result = new DataBean();
        //writeLog(1,"internal parse:");

        String fNames[] = JSONObject.getNames(json);
        if( fNames != null ){
            for (int i = 0; i < fNames.length; i++) {
                Object currentObject = ((JSONObject) json).get(fNames[i]);
                //writeLog(1,"fcolumn: " + fNames[i] + " instance of: " + currentObject.getClass().getName());
    
                if (currentObject instanceof String || currentObject instanceof JSONString ||
                    currentObject instanceof Integer || currentObject instanceof Float || currentObject instanceof Double ||
                    currentObject instanceof Boolean) {
                    result.setValue(fNames[i], currentObject.toString());
                    //writeLog(1,"column: " + fNames[i] + " value: " + result.getString(fNames[i]));
                } else if (currentObject instanceof JSONArray) {
    
                    JSONArray currentArray = (JSONArray) currentObject;
                    for (int j = 0, jsize = currentArray.length(); j < jsize; j++) {
                        if (currentArray.get(j) instanceof String || currentArray.get(j) instanceof JSONString) {
                            result.addToCollection(fNames[i], currentArray.get(j).toString());
                            //writeLog(1,"column: " + fNames[i] + " value: " + result.getString(fNames[i]));
                        } else
                            result.addToCollection(fNames[i], parseJSON((JSONObject) currentArray.get(j)));
                    }
                } else if (currentObject instanceof JSONObject) {
                    result.setStructure(fNames[i], parseJSON((JSONObject) currentObject));
                }
    
            }
        }
        
        return (result);
    }
        
    public static DataBean readJSONFromString(String jsonData){
        DataBean result = new DataBean();
    

        try
        {
            writeLog(1, "parsing json string");
            //JSONObject obj = new JSONObject("{id:0,value:'test',column: 'title'}");
            JSONObject obj = new JSONObject(jsonData);
            String fNames[] = obj.getNames(obj);
            
            for(int i =0;i < fNames.length;i++) {
                result.setValue(fNames[i],(String)obj.get(fNames[i]));
            }
        }
        catch(Exception e) {
            writeLog(2,"json string parse failed: " + e.toString());
        }
        writeLog(1, "after parsing json: " + result.toString());
            return( result );
    }
	
    public static int indexOf(String list[],String value){
        int result = -1;
        
        for(int i = list.length -1;i >= 0;i--){
            if( list[i].equalsIgnoreCase(value) ){
                    result = i;
                    break;
            }
        }
        
        return( result );
    }
	public static int indexOf(ArrayList<DataBean> list,String column,String value){
		int result = -1;
		
		for(int i = list.size() -1;i >= 0;i--){
			if( list.get(i).getString(column).equalsIgnoreCase(value) ){
				result = i;
				
				break;
			}
				
		}
		
		return( result );
	}
        
        
    public static int indexOf(ArrayList<DataBean> list,String column1,String value1,String column2,String value2){
            int result = -1;
            
            for(int i = list.size() -1;i >= 0;i--){
                    if( list.get(i).getString(column1).equalsIgnoreCase(value1) && list.get(i).getString(column2).equalsIgnoreCase(value2) ){
                            result = i;
                            
                            break;
                    }
                            
            }
            
            return( result );
    }
    
        
    public static String generateKeyWithPrefix(String prefix){
        long hash = 0;
        String value = String.valueOf(Calendar.getInstance().getTime().getTime());
        
        for (int i = 0; i < value.length(); i++) {
          hash = 32 * hash + value.charAt(i);
        }
       
        return( prefix + String.valueOf(hash) );
    }
	
    public static ArrayList getColumnsFromEnumeration(Enumeration columns) {
        ArrayList result = new ArrayList();

        String tKey;

        while (columns.hasMoreElements()) {

            tKey = columns.nextElement().toString();
            result.add(tKey);

        }


        return (result);
    }

    public static String hashUserKey(String original){
        String result = original;
        
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(original.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                    sb.append(String.format("%02x", b & 0xff));
            }
            
            result = sb.toString();
        }
        catch(Exception e){
            
        }
        
        return( result );
    }
    
    
    /**
     * default encoding for cookie
     */

    /**
     *  Encode the string into a hash of specified format.
     * @param md
     * @param s
     * @return
     */
    public static String MessageDigestToHex(String s) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                    sb.append(String.format("%02x", b & 0xff));
            }
            
            result = sb.toString();
        } catch (NoSuchAlgorithmException exception) {
            writeLog(1, "unable to get message digest: " + exception.toString());
        }
        return result;
    }
    
    public static String toJson(DataBean target) {
        StringBuilder result = new StringBuilder("{");
        Enumeration columNameList = target.getColumnNames();

        String tKey;
        boolean hasColumns = false;
        if (columNameList != null) {
            int k = 0;
            while (columNameList.hasMoreElements()) {
                tKey = columNameList.nextElement().toString();

                if (k++ > 0)
                    result.append(",");
                result.append("\"");
                result.append(tKey);
                result.append("\":\"");
                result.append(org.json.simple.JSONObject.escape(target.getString(tKey)));
                result.append("\"");
                if (!hasColumns)
                    hasColumns = true;
            }

        }

        if (target.getCollections() != null) {
            Iterator cKeys = target.getCollections().keySet().iterator();
            String cName;

            if (hasColumns) {
                result.append(",");
            }

            if (!hasColumns)
                hasColumns = true;
            

            ArrayList subList;
            DataBean subEntry;
            int counter = 0;
            while (cKeys.hasNext()) {
                cName = cKeys.next().toString();

                subList = target.getCollection(cName);
                if( counter++ > 0 ){
                    result.append(",");
                }
                result.append("\"");
                result.append(cName);
                result.append("\":[");

                for (int j = 0; j < subList.size(); j++) {
                    if (j > 0) {
                        result.append(",");
                    }

                    if (subList.get(j) instanceof DataBean) {
                        subEntry = (DataBean) subList.get(j);

                        result.append(toJson(subEntry));
                    } else {
                        result.append("\"");
                        result.append(subList.get(j).toString());
                        result.append("\"");
                    }
                }

                result.append("]");

            }
        }


        if (target.getStructures() != null) {
            Iterator cKeys =target.getStructures().keySet().iterator();
            String cName;


            DataBean subEntry;

            while (cKeys.hasNext()) {
                cName = cKeys.next().toString();

                subEntry = target.getStructure(cName);
                
                if (hasColumns) {
                    result.append(",");
                }
                else {
                    hasColumns = true;
                }
                result.append("\"");
                result.append(cName);
                result.append("\":");
                result.append(toJson(subEntry));
            }
        }

        result.append("}");

        return (result.toString());
    }
    
    public static String toJsonArray(ArrayList list) {
        StringBuilder result = new StringBuilder("[");

        if (list != null) {
            
            for(int i = 0,size = list.size();i < size;i++){
                if (i > 0) {
                    result.append(",");
                }

                if (list.get(i) instanceof DataBean) {
                    DataBean subEntry = (DataBean) list.get(i);

                    result.append(toJson(subEntry));
                } else {
                    result.append("\"");
                    //result.append(StringEscapeUtils.escapeJson(list.get(i).toString()));
                    result.append("\"");
                }
            }
            
            
        }


        

        result.append("]");

        return (result.toString());
    }
    
    public static void main(String[] args) {
        
        System.exit(0);
    }
    
    /**/
   
 
        public static Date parseDate(String dateStr){
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            Date result =  new Date();
            try{
                result = df.parse(dateStr);
            }
            catch(Exception e){
                System.out.println("date parse error: " + e.toString() + " " + dateStr);
            }
            
            return( result );
        }

}
/*end DataStructure*/
