package oajava.example3Ex;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import oajava.sql.ip;

public class BulkData {

	boolean[] bitVals;
	
	//String Types;
	String[]   sCharVals;     
	String[]   sVarcharVals;
	String[]   sWcharVals;
	String[]   sWvarcharVals;

	//Integer Types
	Long[]     iBigIntVals;
	Integer[]      iIntVals;
	Short[]    iShortVals;   
	Byte[]     iTinyIntVals;

	//Float Types
	Float[]    fRealVals;
	Double[]   fFloatVals;
	Double[]   dDoubleVals;

	//Numeric and Decimal Types
	String[] numericVals;
	String[] decimalVals;

	//Binary Type
	byte[][]     binaryVals;
	byte[][]     varbinaryVals;

	//Date and Time types   
	long[]    		dateVals;
	long[]    		timeVals;
	long[]			timestampVals;

	public synchronized boolean setBitVal(Boolean[] _bitVals, long iNrOfRows)
	{
		bitVals = new boolean[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			bitVals[index] = _bitVals[index].booleanValue();
		return true;
	}
	
	//Set String Types
	public synchronized boolean setCharVal(String[] _sCharVals,long iNrOfRows)
	{
		sCharVals = new String[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			sCharVals[index] = new String(_sCharVals[index]);
		return true;
	}

	public synchronized boolean setVarcharVal(String[] _sVarcharVals,long iNrOfRows)
	{
		sVarcharVals = new String[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			sVarcharVals[index] = new String(_sVarcharVals[index]);
		return true;
	}

	public synchronized boolean setWcharVal(String[] _sWcharVals,long iNrOfRows)
	{
		sWcharVals = new String[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			sWcharVals[index] = new String(_sWcharVals[index]);
		return true;
	}

	public synchronized boolean setWvarcharVal(String[] _sWvarcharVals,long iNrOfRows)
	{
		sWvarcharVals = new String[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			sWvarcharVals[index] = new String(_sWvarcharVals[index]);
		return true;
	}

	//Set Integer Types
	public synchronized boolean setBigIntVal(String[] _iBigIntVals,long iNrOfRows)
	{
		iBigIntVals = new Long[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			iBigIntVals[index]=  Long.valueOf(_iBigIntVals[index]);
		return true;
	}

	public synchronized boolean setIntVal(Integer[] _iIntVals,long iNrOfRows)
	{
		iIntVals = new Integer[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			iIntVals[index] = Integer.valueOf(_iIntVals[index]);
		return true;
	}

	public synchronized boolean setShortVal(Short[] _iShortVals,long iNrOfRows)
	{
		iShortVals = new Short[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			iShortVals[index] = Short.valueOf(_iShortVals[index]);
		return true;
	}

	public synchronized boolean setTinyIntVal(Byte[] _iTinyIntVals,long iNrOfRows)
	{
		iTinyIntVals = new Byte[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)			
			iTinyIntVals[index] =  Byte.valueOf(_iTinyIntVals[index]);
		return true;
	}

	//Set Float Types
	public synchronized boolean setDoubleVal(Double[] _dDoubleVals,long iNrOfRows)
	{
		dDoubleVals = new Double[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			dDoubleVals[index] = Double.valueOf(_dDoubleVals[index]);
		return true;
	}

	public synchronized boolean setRealVal(Float[] _fRealVals,long iNrOfRows)
	{
		fRealVals = new Float[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			fRealVals[index] =  Float.valueOf(_fRealVals[index]);
		return true;
	}

	public synchronized boolean setFloatVal(Double[] _fFloatVals,long iNrOfRows)
	{
		fFloatVals = new Double[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			fFloatVals[index] =  Double.valueOf(_fFloatVals[index]);
		return true;
	}

	//Set Numeric and Decimal Types
	public synchronized boolean setNumericVal(String[] _numericVals,long iNrOfRows)
	{
		numericVals = new String[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			numericVals[index] =  new String(_numericVals[index]);
		return true;
	}

	public synchronized boolean setDecimalVal(String[] _decimalVals,long iNrOfRows)
	{
		decimalVals = new String[(int)iNrOfRows];
		for(int index = 0; index < iNrOfRows; index++)
			decimalVals[index] = new String(_decimalVals[index]);
		return true;
	}

	//Set Binary Types
	public synchronized boolean setBinaryVal(Object[] _binaryVals,long iNrOfRows)
	{
		binaryVals = new byte[(int)iNrOfRows][];
		for(int index = 0; index < iNrOfRows; index++)
		{
			byte[] binaryValTemp = (byte[])_binaryVals[index];
			binaryVals[index] = new byte[binaryValTemp.length];
			for(int j = 0; j < binaryValTemp.length; j++)
				binaryVals[index][j] = binaryValTemp[j];
		}
		return true;
	}

	public synchronized boolean setVarbinaryVal(Object[] _varbinaryVals,long iNrOfRows)
	{
		varbinaryVals = new byte[(int)iNrOfRows][];
		for(int index = 0; index < iNrOfRows; index++)
		{
			byte[] varbinaryValTemp = (byte[])_varbinaryVals[index];
			varbinaryVals[index] = new byte[varbinaryValTemp.length];
			for(int j = 0; j < varbinaryValTemp.length; j++)
				varbinaryVals[index][j] = varbinaryValTemp[j];
		}
		return true;
	}

	//set Date, Time and TimeStamp
	public synchronized boolean setDateVal(String[] _dateVals,long iNrOfRows)
	{
		dateVals = new long[(int)iNrOfRows];
		for(int index=0; index < iNrOfRows ; index++)
		{
			dateVals[index] = (Date.valueOf(_dateVals[index])).getTime()/1000;
		}
		return true;
	}

	public synchronized boolean setTimeVal(String[] _timeVals,long iNrOfRows)
	{
		timeVals = new long[(int)iNrOfRows];
		for(int index=0; index < iNrOfRows ; index++)
		{
			timeVals[index] = (Time.valueOf(_timeVals[index])).getTime()/1000;
		}
		return true;

	}

	public synchronized boolean setTimestampVal(String[] _timestampVals,long iNrOfRows)
	{
		timestampVals = new long[(int)iNrOfRows];
		for(int index=0; index < iNrOfRows ; index++)
		{
			timestampVals[index] = (Timestamp.valueOf(_timestampVals[index])).getTime()/1000;
		}
		return true;
	}		

	//get methods

	public synchronized boolean[] getBitVal()
	{
		return bitVals;
	}
	
	//get String Types
	public synchronized String[] getCharVal()
	{
		return sCharVals;
	}

	public synchronized String[] getVarcharVal()
	{
		return sVarcharVals;
	}

	public synchronized String[] getWcharVal()
	{
		return sWcharVals;
	}

	public synchronized String[] getWvarcharVal()
	{
		return sWvarcharVals;
	}

	//get Integer Types
	public synchronized Long[] getBigIntVal()
	{
		return iBigIntVals;
	}

	public synchronized Integer[] getIntVal()
	{
		return iIntVals;
	}

	public synchronized Short[] getShortVal()
	{
		return iShortVals;
	}

	public synchronized Byte[] getTinyIntVal()
	{
		return iTinyIntVals;
	}

	//get Float Types
	public synchronized Double[] getDoubleVal()
	{
		return dDoubleVals;
	}

	public synchronized Float[] getRealVal()
	{
		return fRealVals;
	}

	public synchronized Double[] getFloatVal()
	{
		return fFloatVals;
	}

	//get Numeric and Decimal Types
	public synchronized String[] getNumericVal()
	{
		return numericVals;
	}

	public synchronized String[] getDecimalVal()
	{
		return decimalVals;
	}

	//get Binary Types
	public synchronized byte[][] getBinaryVal()
	{
		return binaryVals;
	}

	public synchronized byte[][] getVarbinaryVal()
	{
		return varbinaryVals;
	}

	//get Date, Time and TimeStamp
	public synchronized long[] getDateVal()
	{
		return dateVals;
	}

	public synchronized long[] getTimeVal()
	{
		return timeVals;
	}

	public synchronized long[] getTimestampVal()
	{
		return timestampVals;
	}		
	
	public synchronized boolean setMembers(Object[] objects, int iXoType, long iNrOfRows)
	{
		switch(iXoType)
		{
		case ip.XO_TYPE_CHAR: setCharVal((String[])objects, iNrOfRows); break;
		case ip.XO_TYPE_VARCHAR: setVarcharVal((String[])objects, iNrOfRows); break;        
		case ip.XO_TYPE_NUMERIC: setNumericVal((String[])objects, iNrOfRows); break;
		case ip.XO_TYPE_DECIMAL: setDecimalVal((String[])objects, iNrOfRows); break;
		case ip.XO_TYPE_INTEGER: setIntVal((Integer[])objects, iNrOfRows); break;
		case ip.XO_TYPE_SMALLINT: setShortVal((Short[])objects, iNrOfRows); break;
		case ip.XO_TYPE_DOUBLE: setDoubleVal((Double[])objects, iNrOfRows); break;
		case ip.XO_TYPE_BIGINT: setBigIntVal((String[])objects, iNrOfRows); break;
		case ip.XO_TYPE_FLOAT: setFloatVal((Double[])objects, iNrOfRows); break;
		case ip.XO_TYPE_REAL: setRealVal((Float[])objects, iNrOfRows); break;
		case ip.XO_TYPE_WCHAR: setWcharVal((String[])objects, iNrOfRows); break;
		case ip.XO_TYPE_WVARCHAR: setWvarcharVal((String[])objects, iNrOfRows); break;        
		case ip.XO_TYPE_DATE: setDateVal((String[])objects, iNrOfRows); break;         
		case ip.XO_TYPE_TIME: setTimeVal((String[])objects, iNrOfRows); break;      
		case ip.XO_TYPE_TIMESTAMP:  setTimestampVal((String[])objects, iNrOfRows); break;        
		case ip.XO_TYPE_BINARY: setBinaryVal(objects, iNrOfRows); break;
		case ip.XO_TYPE_VARBINARY: setVarbinaryVal(objects, iNrOfRows); break;    
		case ip.XO_TYPE_TINYINT: setTinyIntVal((Byte[])objects, iNrOfRows); break;
		case ip.XO_TYPE_BIT: setBitVal((Boolean[])objects, iNrOfRows); break;	
		}
		return true;		
	}
}
