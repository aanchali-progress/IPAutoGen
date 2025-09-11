/* mdb.java
 *
 * Copyright (c) 1995-2013 Progress Software Corporation. All Rights Reserved.
 *
 * Memory database used by Example IP
 *
 */

package oajava.example3;
import oajava.sql.*;

public class mdb
{
    final int   iSampleSize = 12;
    database[]  _DataBase;      /* database */
    long        _TotalItems;    /* total number of items in the database */
    database[]  sampleDataBase;

    /* Initialize database to hold nRecords records */
    public mdb()
    {
               int i =0;
               sampleDataBase = new database[iSampleSize];
               for(i =0; i < iSampleSize;i++)
                   sampleDataBase[i] = new database();
        Init();
    }

    // Init for new memory database
    public int Init()
    {
     int    i;
         sampleDataBase[0].Init(true, "Joe",   0,  0,  0.0f);
         sampleDataBase[1].Init(true, "Tom",   100,1,  1.0f);
         sampleDataBase[2].Init(true, "Yoh",   0,  200,2.0f);
         sampleDataBase[3].Init(true, "Joe1",  0,  0,  0.0f);
         sampleDataBase[4].Init(true, "Tom1",  100,1,  1.0f);
         sampleDataBase[5].Init(true, "Yoh1",  0,  200,2.0f);
         sampleDataBase[6].Init(true, "Joe2",  0,  0,  0.0f);
         sampleDataBase[7].Init(true, "Tom2",  100,1,  1.0f);
         sampleDataBase[8].Init(true, "Yoh2",  0,  200,2.0f);
         sampleDataBase[9].Init(true, "Joe3",  0,  0,  0.0f);
         sampleDataBase[10].Init(true, "Tom3",  100,1,  1.0f);
         sampleDataBase[11].Init(true, "Yoh3",  0,  200,2.0f);

         /* allocate the database to be of size nRecords */
         _DataBase = new database[iSampleSize];
         for( i = 0; i < iSampleSize; i++)
         {
             _DataBase[i] = new database();
             _DataBase[i].bUsed = false;
         }

     /* initialize some of the database records from the
     sample database */
         for( i = 0; i < iSampleSize; i++)
         {
             _DataBase[i].bUsed = sampleDataBase[i].bUsed;
             _DataBase[i].sName = new String(sampleDataBase[i].sName);
             _DataBase[i].lTime = sampleDataBase[i].lTime;
             _DataBase[i].iIntVal = sampleDataBase[i].iIntVal;
             _DataBase[i].dDoubleVal = sampleDataBase[i].dDoubleVal;
         }
         _TotalItems = iSampleSize;
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
                int i;

                for (i=index + 1; i< _TotalItems; i++)
                {
                    if(_DataBase[i].bUsed)
                    {
                        pIndex.setVal(i);
                        return true;
                    }
                }
                return false;
            }
            else
                return false;
        }

    public synchronized String getName(int index)
        {
            return _DataBase[index].sName;
        }

    public synchronized int getIntVal(int index)
    {
            return _DataBase[index].iIntVal;
        }

    public synchronized double getDVal(int index)
    {
            return _DataBase[index].dDoubleVal;
        }

    public synchronized long getTime(int index)
    {
            return _DataBase[index].lTime;
        }

    public synchronized boolean FindItemByName(String pName,xo_int pIndex)
    {
            int i;
            for (i=0; i< _TotalItems; i++)
            {
                if( _DataBase[i].bUsed)
                {
                    if(pName.compareTo(_DataBase[i].sName) == 0)
                    {
				        pIndex.setVal(i);
                        return true;
                    }
                }
            }/* failed to find a match */
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
            _DataBase[index].bUsed = false;
            return true;
        }

    public synchronized boolean SetUpdateRow(int index)
        {
            /* error checking */
            if( index < 0 || index >= _TotalItems) {
                return false;
                }

            /*
            if (sName != null) _DataBase[index].sName = new String(sName);
            if (iIntVal != null) _DataBase[index].iIntVal = iIntVal;
            if (dDoubleVal != null ) _DataBase[index].dDoubleVal = dDoubleVal;
            if (lTime != null) _DataBase[index].lTime = lTime;
            */

            return true;
        }

    public synchronized boolean UpdateRow(int index)
        {

            return true;
        }

    public synchronized boolean InsertRow(xo_int pIndex)
        {
        int     index;

        /* find the index where the row can be inserted */
        index = 0;
        while (index < _TotalItems) {
            if (!_DataBase[index].bUsed) {
                /* insert the new record */
	            _DataBase[index].bUsed = true;
                _DataBase[index].sName = new String();
                _DataBase[index].iIntVal = 0;
                _DataBase[index].dDoubleVal = 0.0f;
                _DataBase[index].lTime = 0;

                pIndex.setVal(index);
                return true;
                }
            index++;
            }
        return false;
        }

    public synchronized boolean setName(String sName,int index)
        {
        _DataBase[index].sName = new String(sName);
        return true;
        }

    public synchronized boolean setIntVal(int iIntVal,int index)
        {
        _DataBase[index].iIntVal = iIntVal;
        return true;
        }

    public synchronized boolean setDVal(double dDoubleVal,int index)
        {
        _DataBase[index].dDoubleVal = dDoubleVal;
        return true;
        }

    public synchronized boolean setTime(long lTime,int index)
        {
        _DataBase[index].lTime = lTime;
        return true;
        }


}
