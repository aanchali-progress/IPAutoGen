package oajava.example3;

public class database
{
   final int SP1_NAMEMAX = 32;
   final int SP1_DBMAX  = 100;

   boolean  bUsed;
   String   sName;     // SP1_NAMEMAX 
   long     lTime;
   int      iIntVal;
   double   dDoubleVal;
   public void Init(boolean _bUsed, String _sName, long _lTime, int _iIntVal,double _dDoubleVal)
   {
       bUsed = _bUsed;
       sName = _sName;
       lTime = _lTime;
       iIntVal = _iIntVal;
       dDoubleVal = _dDoubleVal;
   }
}
