package oajava.example3Ex;

public class BulkDBRow
{
	final int SP1_NAMEMAX = 32;
	final int SP1_DBMAX  = 100;

	//Bit type
	boolean  bBitVal;

	//String Types
	String   sCharVal;     // SP1_NAMEMAX 
	String   sVarcharVal;
	String   sWcharVal;
	String   sWvarcharVal;

	//Integer Types
	long     iBigIntVal;
	int      iIntVal;
	short    ishortVal;   
	byte     iTinyIntVal;

	//Float Types
	float    fRealVal;
	double    fFloatVal;
	double   dDoubleVal;

	//Numeric and Decimal Types
	String numericVal;
	String decimalVal;

	//Binary Type
	byte     binaryVal[];
	byte     varbinaryVal[];

	//Date and Time types   
	long    		dateVal;
	long    		timeVal;
	long	timestampVal;

	public void Init(boolean _bBitVal, String _sCharVal, String _sVarcharVal, String _sWcharVal, String _sWvarcharVal,
			long _iBigIntVal, int _iIntVal, short _ishortVal, byte _iTinyIntVal,
			float _fRealVal, float _fFloatVal, double _dDoubleVal,
			String _numericVal, String _decimalVal,
			byte _binaryVal[], byte _varbinaryVal[],
			long _dateVal, long _timeVal, long _timestampVal)
	{
		//Bit type
		bBitVal = _bBitVal;

		//String Types
		sCharVal = _sCharVal;      
		sVarcharVal = _sVarcharVal;
		sWcharVal = _sWcharVal;
		sWvarcharVal = _sWvarcharVal;

		//Integer Types
		iBigIntVal = _iBigIntVal;
		iIntVal  = _iIntVal;
		ishortVal = _ishortVal;   
		iTinyIntVal  = _iTinyIntVal;

		//Float Types
		fRealVal  = _fRealVal;
		fFloatVal = _fFloatVal;
		dDoubleVal = _dDoubleVal;

		//Numeric and Decimal Types
		numericVal = _numericVal;
		decimalVal = _decimalVal;

		//Binary Type
		binaryVal = _binaryVal;
		varbinaryVal = _varbinaryVal;

		//Date and Time types   
		dateVal = _dateVal;
		timeVal = _timeVal;
		timestampVal = _timestampVal;
	}
}
