package com.jsclosures.solr;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.commons.collections.iterators.IteratorEnumeration;

public class DataBean extends Object
{
     
     public DataBean()
     {
          super();
     }
     
    public DataBean(String name,String value)
    {
         super();
         setValue("name",name);
         setValue("value",value);
    }
    /**
     *Copy the attributes from one the passed record into this record.
     * @param rec
     */
    public void copyFrom(DataBean rec){
            Enumeration innerColumNameList = rec.getColumnNames();
                    
            if( innerColumNameList != null )
            {
                String tKey;
                while( innerColumNameList.hasMoreElements() )
                {
                        tKey = innerColumNameList.nextElement().toString();
                        setValue(tKey,rec.getString(tKey));
                }
            }
    }
     private HashMap structures;

     public void setStructure(String name,DataBean value)
     {
        if( structures == null )
               structures = new HashMap();

        structures.put(name.toLowerCase(),value);
     }
     public boolean hasStructure(String name){
         return( structures != null && structures.get(name.toLowerCase()) != null );
     }
     public DataBean getStructure(String name)
     {
         return( structures != null && structures.get(name.toLowerCase()) != null ? (DataBean)structures.get(name.toLowerCase()) : new DataBean() );
     }
     
    private HashMap objects;

    public void setObject(String name,Object value)
    {
       if( objects == null )
              objects = new HashMap();

       objects.put(name.toLowerCase(),value);
    }

    public Object getObject(String name)
    {
        return( objects != null && objects.get(name.toLowerCase()) != null ? (Object)objects.get(name.toLowerCase()) : null );
    }

     private HashMap collections;

     public void setCollection(String name,ArrayList value)
     {
        if( collections == null )
               collections = new HashMap();

        collections.put(name.toLowerCase(),value);
     }

     public ArrayList getCollection(String name)
     {
         return( collections != null && collections.get(name.toLowerCase()) != null ? (ArrayList)collections.get(name.toLowerCase()) : null );
     }
     
     public void addToCollection(String name,Object value)
     {
          ArrayList entryList = getCollection(name);
          
          if( entryList == null )
          {
               setCollection(name,new ArrayList());
               entryList = getCollection(name);
          }

          entryList.add(value);
     }
     
     public void removeValue(String which){
         if( internalValues != null ){
             internalValues.remove(which);
         }
     }
     public HashMap getCollections()
     {
          return( collections );
     }
     
     public HashMap getStructures()
     {
          return( structures );
     }

     public Enumeration getColumnNames()
     {
          Enumeration result = null;
          
          if( internalValues != null )
               result = new IteratorEnumeration(internalValues.keySet().iterator());
          
          return( result );
     }
     
     private HashMap internalValues;

     public void setValue(String name,Object value)
     {
        if( internalValues == null )
               internalValues = new HashMap();

		if( value != null )
			internalValues.put(name.toLowerCase(),value);
     }
    public void setValue(String name,int value)
     {
        setValue(name,String.valueOf(value));
     }
        
     public String getString(String name)
     {
         return( internalValues != null && internalValues.get(name.toLowerCase()) != null ? internalValues.get(name.toLowerCase()).toString() : "" );
     }
	 
	public String getString(String name,String defaultValue )
	{
		return( internalValues != null && internalValues.get(name.toLowerCase()) != null ? internalValues.get(name.toLowerCase()).toString() : defaultValue );
	}

     public float getFloat(String name)
     {
         return( internalValues != null && internalValues.get(name.toLowerCase()) != null ? Float.valueOf(internalValues.get(name.toLowerCase()).toString()).floatValue() : 0 );
     }

     public int getInt(String name)
     {
         return( internalValues != null && internalValues.get(name.toLowerCase()) != null ? Helper.parseInt(internalValues.get(name.toLowerCase()).toString()) : 0 );
     }
     
    public long getLong(String name)
    {
        return( internalValues != null && internalValues.get(name.toLowerCase()) != null ? Helper.parseLong(internalValues.get(name.toLowerCase()).toString()) : 0 );
    }

     public Object getValue(String name)
     {
         return( internalValues != null && internalValues.get(name.toLowerCase()) != null ? internalValues.get(name.toLowerCase()) : null );
     }

     public boolean isValid(String name)
     {
         return( internalValues != null && internalValues.get(name.toLowerCase()) != null ? internalValues.get(name.toLowerCase()).toString().length() > 0 : false );
     }
     
    public boolean hasSlot(String name)
    {
        return( internalValues != null && internalValues.get(name.toLowerCase()) != null );
    }
    
    public void removeSlot(String name){
        if( internalValues != null ){
            internalValues.remove(name);
        }
    }

     public boolean isValid()
     {
         return( true );
     }
     public void reset()
     {
          internalValues = new HashMap();
          collections = new HashMap();
     }

	public HashMap getValues()
	{
		return( internalValues );
	}
    public String toString()
    {
        return( toString(";") );
    }
    
     public String toString(String delimiter)
     {
          StringBuffer result = new StringBuffer();
          
          Enumeration cols = getColumnNames();
          
          String tStr;
          
		if( cols != null )
		{
			while( cols.hasMoreElements() )
			{
				tStr = cols.nextElement().toString();
				
				result.append(tStr);
				result.append(":");
				result.append(getString(tStr));
				result.append( delimiter);
			}
		}
          
          HashMap cHash = getCollections();
          
          if( cHash != null )
          {
               cols = new IteratorEnumeration(cHash.keySet().iterator());;
               ArrayList subList;

               while( cols.hasMoreElements() )
               {
                   tStr = cols.nextElement().toString();
                   if( getCollection(tStr) != null ){
                        result.append(tStr);
                        result.append(" collection:");
                        
                        subList = getCollection(tStr);
                        
                        for(int j = 0;j < subList.size();j++)
                        {
                        
                             result.append(subList.get(j).toString());
                             
                        }
                   }
               }
          }
          
          return( result.toString() );
     }
     
     
     public DataBean cloneDataBean(){
         DataBean result = new DataBean();
         
         Enumeration cols = getColumnNames();
         
         
         
       if( cols != null )
       {
           String tStr;
               while( cols.hasMoreElements() )
               {
                       tStr = cols.nextElement().toString();
                   result.setValue(tStr,getValue(tStr));
               }
       }
         
         
         return( result );
     }
	
	private String dataItemsKey = "items";
	
	/**
	* @return the dataItemsKey
	*/
	public String getDataItemsKey() {
	   return dataItemsKey;
	}

	/**
	* @param dataItemsKey the dataItemsKey to set
	*/
	public void setDataItemsKey(String dataItemsKey) {
	   this.dataItemsKey = dataItemsKey;
	}
     
}
/*end DataStructure*/
