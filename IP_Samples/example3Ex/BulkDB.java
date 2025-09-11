/* mdb.java
 *
 * Copyright (c) 1995-2012 Progress Software Corporation. All Rights Reserved.
 *
 * Memory database used by Example IP
 *
 */

package oajava.example3Ex;
import oajava.sql.*;
import java.util.ArrayList;

public class BulkDB
{	
	//For Bulk Insert
	ArrayList<BulkDBRow> _DataBase2;
	final int   dbSize = 50;
	long        _TotalItems; 

    /* Initialize database to hold nRecords records */
    public BulkDB()
    {			   
        Init();
    }

    // Init for new memory database
    public int Init()
    {
		int    i;
		BulkDBRow bulkRow;

         /* allocate the database to be of size nRecords */
         _DataBase2 = new ArrayList<BulkDBRow>(dbSize);
         for( i = 0; i < dbSize; i++)
         {
        	 bulkRow =  new BulkDBRow();
        	 
        	 //Add to array list
        	 _DataBase2.add(i,bulkRow);             
         }
         
    	 _TotalItems = dbSize ;
        
         /* initialize the first hundred rows */
         for(i = 0; i < 50; i++)
         {
     
        	 bulkRow = _DataBase2.get(i);

        	 bulkRow.bBitVal = false;

        	 bulkRow.sCharVal = new String("CharVal"+i);        

        	 bulkRow.sVarcharVal = new String("VarcharVal"+i);        

        	 bulkRow.sWcharVal = new String("WcharVal"+i);  

        	 bulkRow.sWvarcharVal = new String("WvarcharVal"+i);   

        	 bulkRow.iBigIntVal = 1000+i;

        	 bulkRow.iIntVal = 100+i;

        	 bulkRow.ishortVal = (short)(10+i);

        	 bulkRow.iTinyIntVal = (byte)(1+i);

        	 bulkRow.dDoubleVal = 22.5+i;      

        	 bulkRow.fRealVal = (float)(45.67+i);

        	 bulkRow.fFloatVal = (float)(45.67+i);        

        	 bulkRow.numericVal = new String("12"+i+".51");

        	 bulkRow.decimalVal = new String("56"+i+".95");
			 
			 String u1=new String("AB"+i*10+"C");
			 bulkRow.binaryVal = new byte[256];
			 bulkRow.binaryVal= u1.getBytes();
			 
			 String u2=new String("DE"+i*10+"F");
			 bulkRow.varbinaryVal = new byte[256];
			 bulkRow.varbinaryVal= u2.getBytes();
		
        	 bulkRow.dateVal = (1000000+(i*100));        

        	 bulkRow.timeVal = (1000000+(i*100));

        	 bulkRow.timestampVal = (1000000+(i*100)); 
         }
						
		return 0;
    }

    public synchronized boolean FirstRow(xo_int pIndex)
        {
            pIndex.setVal(-1);

            return NextRow(pIndex);
        }

    public synchronized boolean NextRow(xo_int pIndex)
    {
    	int index = pIndex.getVal();

    	if((index + 1) < _TotalItems)
    	{
    		int i = index+1;
    		
    		pIndex.setVal(i);
    		return true;

    	}
    	else
    		return false;
    }

    synchronized boolean   ReadRow(int index)
    {
        /* error checking */
        return (!( index < 0 || index >= _TotalItems));
    }

    public synchronized boolean DeleteRow(int index)
        {
            /* error checking */
            if( index < 0 || index >= _TotalItems) {
                return false;
                }
            _DataBase2.remove(index);
            return true;
        }

    public synchronized boolean SetUpdateRow(int index)
        {
            /* error checking */
            if( index < 0 || index >= _TotalItems) {
                return false;
                }
            return true;
        }

    public synchronized boolean UpdateRow(int index)
        {
            return true;
        }

    public synchronized int InsertRow()
    {
    	try{
    		BulkDBRow bulkRow = new BulkDBRow();

    		/* insert the new record */    		
    		bulkRow.sCharVal = new String();        

    		bulkRow.sVarcharVal = new String();        

    		bulkRow.sWcharVal = new String();      

    		bulkRow.sWvarcharVal = new String(); 

    		bulkRow.iBigIntVal = 0;

    		bulkRow.iIntVal = 0;

    		bulkRow.ishortVal = 0;

    		bulkRow.iTinyIntVal = 0;

    		bulkRow.dDoubleVal = 0;        

    		bulkRow.fRealVal = 0;

    		bulkRow.fFloatVal = 0;        

    		bulkRow.numericVal = new String();

    		bulkRow.decimalVal = new String();

    		bulkRow.binaryVal = new byte[10];

    		bulkRow.varbinaryVal = new byte[10];

    		bulkRow.dateVal = 0;        

    		bulkRow.timeVal = 0;

    		bulkRow.timestampVal = 0;        

    		_DataBase2.add(bulkRow);
    	}
    	catch(Exception e ){
    		return -1;
    	}
    		
    	return (_DataBase2.size()-1);
    }

    public synchronized boolean setBitVal(boolean _bitVal,int index)
    {
    	BulkDBRow bulkRow = _DataBase2.get(index);
    	bulkRow.bBitVal = _bitVal;
    	return true;
    }

    //Set String Types
	public synchronized boolean setCharVal(String _sCharVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.sCharVal = new String(_sCharVal);
        return true;
        }
		
	public synchronized boolean setVarcharVal(String _sVarcharVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.sVarcharVal = new String(_sVarcharVal);
        return true;
        }
		
	public synchronized boolean setWcharVal(String _sWcharVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.sWcharVal = new String(_sWcharVal);
        return true;
        }
		
	public synchronized boolean setWvarcharVal(String _sWvarcharVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.sWvarcharVal = new String(_sWvarcharVal);
        return true;
        }
	
	//Set Integer Types
	public synchronized boolean setBigIntVal(long _BigIntVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.iBigIntVal = _BigIntVal;
        return true;
        }
		
	public synchronized boolean setIntVal(int _IntVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.iIntVal =_IntVal;
        return true;
        }
		
	public synchronized boolean setShortVal(short _ShortVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.ishortVal = _ShortVal;
        return true;
        }
		
	public synchronized boolean setTinyIntVal(byte _TinyIntVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.iTinyIntVal = _TinyIntVal;
        return true;
        }
		
	//Set Float Types
	public synchronized boolean setDoubleVal(double _dDoubleVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.dDoubleVal = _dDoubleVal;
        return true;
        }
		
	public synchronized boolean setRealVal(float _fRealVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.fRealVal = _fRealVal;
        return true;
        }
		
	public synchronized boolean setFloatVal(double _fFloatVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.fFloatVal = _fFloatVal;
        return true;
        }
		
	//Set Numeric and Decimal Types
	public synchronized boolean setNumericVal(String _numericVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.numericVal = new String(_numericVal);
        return true;
        }
		
	public synchronized boolean setDecimalVal(String _decimalVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.decimalVal = new String(_decimalVal);
        return true;
        }
		
	//Set Binary Types
	public synchronized boolean setBinaryVal(byte[] _binaryVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		for(int i=0; i< _binaryVal.length ; i++)
		{
			bulkRow.binaryVal[i] = _binaryVal[i];
		}
        return true;
        }
		
	public synchronized boolean setVarbinaryVal(byte[] _varbinaryVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		for(int i=0; i< _varbinaryVal.length ; i++)
		{
			bulkRow.varbinaryVal[i] = _varbinaryVal[i];
		}
        return true;
        }
		
	//set Date, Time and TimeStamp
	public synchronized boolean setDateVal(long _dateVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.dateVal = _dateVal;
        return true;
        }
		
	public synchronized boolean setTimeVal(long _timeVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.timeVal = _timeVal;
        return true;
        }
		
	public synchronized boolean setTimestampVal(long _timestampVal,int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);
		bulkRow.timestampVal = _timestampVal;
        return true;
        }		
	
	//get methods
	
	//get String Types
	public synchronized String getCharVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.sCharVal;        
        }
		
	public synchronized String getVarcharVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.sVarcharVal;        
        }
		
	public synchronized String getWcharVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.sWcharVal;        
        }
		
	public synchronized String getWvarcharVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.sWvarcharVal; 
        }
	
	//get Integer Types
	public synchronized long getBigIntVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.iBigIntVal;
        }
		
	public synchronized int getIntVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.iIntVal;
        }
		
	public synchronized short getShortVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.ishortVal;
        }
		
	public synchronized byte getTinyIntVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.iTinyIntVal;
        }
		
	//get Float Types
	public synchronized double getDoubleVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.dDoubleVal;        
        }
		
	public synchronized float getRealVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.fRealVal;
        }
		
	public synchronized double getFloatVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.fFloatVal;        
        }
		
	//get Numeric and Decimal Types
	public synchronized String getNumericVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.numericVal;
        }
		
	public synchronized String getDecimalVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.decimalVal;
        }
		
	//get Binary Types
	public synchronized byte[] getBinaryVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.binaryVal;
        }
		
	public synchronized byte[] getVarbinaryVal(int index)
        {
        
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.varbinaryVal;
		}

	//get Date, Time and TimeStamp
	public synchronized long getDateVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.dateVal;        
        }
		
	public synchronized long getTimeVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.timeVal;
        }
		
	public synchronized long getTimestampVal(int index)
        {
		BulkDBRow bulkRow = _DataBase2.get(index);	
		return bulkRow.timestampVal;        
        }
	
	public synchronized boolean getBitVal(int index)
    {
		BulkDBRow bulkRow = _DataBase2.get(index);
		return bulkRow.bBitVal;    	
    }
	
	public void updateTotalRows()
	{
		_TotalItems = _DataBase2.size();
	}
}
