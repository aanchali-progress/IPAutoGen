/* damip.java
 *
 * Copyright (c) 2013-2016 Progress Software Corporation. All Rights Reserved.
 *
 *
 *
 * Description:     This example IP
 *                  - is implemented in "JAVA"
 *                  - supports dynamic schema
 *                  - supports single column index on column NAME
 *                  - supports SELECT, INSERT, UPDATE & DELETE operations
 *                  - supports customization of data source information and data type information
 */

package oajava.example3Ex;

import java.util.Date;
import java.util.Calendar;
import oajava.sql.*;

/* define the damip class to implement the sample IP */
public class damip implements oajava.sql.ip
{
	private long m_tmHandle = 0;
	private long m_iNumResRows;
	private int isTableFunction = DAM_FALSE;
	private xo_int iCurrentRow;
	private StringBuffer    sTableName; /* Name of the table being queried (char [DAM_MAX_ID_LEN+1] )*/
	private static mdb m_pMdb = null;
	private static BulkDB m_bulkDB = null;
	private static boolean  bAllowSchemaSearchPattern = false;

	/* connection information */
	private StringBuffer    m_sQualifier;         /* char [DAM_MAX_ID_LEN+1] */
	private StringBuffer    m_sUserName;          /* char [DAM_MAX_ID_LEN+1] */
	private long iFetchSize;  /* records to return in cursor mode */

	/* index information */
	private StringBuffer    sIndexName;         /* char [DAM_MAX_ID_LEN+1] */
	private xo_long         hindex;             /* DAM_HINDEX */
	private xo_long         hset_of_condlist;   /* DAM_HSET_OF_CONDLIST */

	/* column handles of curvalue table*/
	private long hcolName, hcolIntVal, hcolDoubleVal, hcolTime; /* DAM_HCOL */

	/* column handles for bulk table*/
	private long hcolBitVal;
	private long hcolBigIntVal, hcolTinyIntVal, hcolSmallIntVal; /* DAM_HCOL */
	private long hcolRealVal, hcolFloatVal; /* DAM_HCOL */
	private long hcolCharVal, hcolVarcharVal, hcolWcharVal, hcolWvarcharVal; /* DAM_HCOL */
	private long hcolNumericVal, hcolDecimalVal; /* DAM_HCOL */
	private long hcolBinaryVal, hcolVarbinaryVal; /* DAM_HCOL */
	private long hcolDateVal, hcolTimeVal, hcolTimeStampVal; /* DAM_HCOL */    

	final static String OA_CATALOG_NAME   = "SCHEMA";        /* SCHEMA */
	final static String OA_USER_NAME      = "OAUSER";        /* OAUSER */

	final static short MAX_ARGS_FOR_CURVALUE_F = 5;          /* MAXIMUM number of columns of CURVALUE_F*/
	final static short NO_OF_COLS_FOR_TYPES_F = 4;           /* Number of columns of TYPES_F*/
	final static short MAX_ARGS_FOR_TYPES_F = 12;            /* MAXIMUM number of arguments for TYPES_F */
	final static short MAX_ROWS_FOR_CURVALUE_F = 5;          /*	MAXIMUM number of arguments for curvalue tablefunction */
	final static short NO_OF_ARGS_FOR_SAMPLE_F = 1;          /*	Number of arguments for SAMPLE_F tablefunction */

	/* Support array */
	private final int[]   ip_support_array =
	{
			0,
			1, /* IP_SUPPORT_SELECT */
			1, /* IP_SUPPORT_INSERT */
			1, /* IP_SUPPORT_UPDATE */
			1, /* IP_SUPPORT_DELETE */
			1, /* IP_SUPPORT_SCHEMA - IP supports Schema Functions */
			0, /* IP_SUPPORT_PRIVILEGES  */
			1, /* IP_SUPPORT_OP_EQUAL */
			0, /* IP_SUPPORT_OP_NOT   */
			0, /* IP_SUPPORT_OP_GREATER */
			0, /* IP_SUPPORT_OP_SMALLER */
			0, /* IP_SUPPORT_OP_BETWEEN */
			0, /* IP_SUPPORT_OP_LIKE    */
			0, /* IP_SUPPORT_OP_NULL    */
			0, /* IP_SUPPORT_SELECT_FOR_UPDATE */
			0, /* IP_SUPPORT_START_QUERY */
			0, /* IP_SUPPORT_END_QUERY */
			0, /* IP_SUPPORT_UNION_CONDLIST */
			0, /* IP_SUPPORT_CREATE_TABLE */
			0, /* IP_SUPPORT_DROP_TABLE */
			0, /* IP_SUPPORT_CREATE_INDEX */
			0, /* IP_SUPPORT_DROP_INDEX */
			0, /* IP_SUPPORT_PROCEDURE */
			0, /* IP_SUPPORT_CREATE_VIEW */
			0, /* IP_SUPPORT_DROP_VIEW */
			0, /* IP_SUPPORT_QUERY_VIEW */
			0, /* IP_SUPPORT_CREATE_USER */
			0, /* IP_SUPPORT_DROP_USER */
			0, /* IP_SUPPORT_CREATE_ROLE */
			0, /* IP_SUPPORT_DROP_ROLE */
			0, /* IP_SUPPORT_GRANT */
			0, /* IP_SUPPORT_REVOKE */
			0,  /* IP_SUPPORT_PASSTHROUGH_QUERY */
			0,  /* IP_SUPPORT_NATIVE_COMMAND */
			0,  /* IP_SUPPORT_ALTER_TABLE */
			0,  /* IP_SUPPORT_BLOCK_JOIN */
			0,  /* IP_SUPPORT_XA */
			1,  /* IP_SUPPORT_QUERY_MODE_SELECTION */
			0,  /* IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE */
			1,  /* IP_SUPPORT_UNICODE_INFO */
			0,  /* IP_SUPPORT_JOIN_ORDER_SELECTION */
			1,  /* IP_SUPPORT_TABLE_FUNCTIONS */
			1,  /* IP_SUPPORT_BULK_INSERT */
			0,  /* Reserved for future use */
			0,  /* Reserved for future use */
			0,  /* Reserved for future use */
			0,  /* Reserved for future use */
			0,  /* Reserved for future use */
			0,  /* Reserved for future use */
			0   /* Reserved for future use */
	};


	public damip()
	{
		m_tmHandle = 0;
		m_iNumResRows = 0;
		iCurrentRow = new xo_int(0);

		m_sQualifier = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
		m_sUserName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
		sTableName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
		sIndexName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
		hcolName = 0;
		hcolIntVal = 0;
		hcolDoubleVal = 0;
		hcolTime = 0;
		hindex= new xo_long(0);
		hset_of_condlist = new xo_long(0);

		synchronized(getClass())
		{
			/*create one instance of the mdb object */
			if(m_pMdb == null)
				m_pMdb = new mdb();

			/*create one instance of the mdb object */
			if(m_bulkDB == null)
				m_bulkDB = new BulkDB();
		}
	}

	public String ipGetInfo(int iInfoType)
	{
		String str = null;
		switch (iInfoType)
		{
		case IP_INFO_QUALIFIER_TERMW:
			/* return the Qualifier term used in Schema information */
			str = "database";
			break;

		case IP_INFO_OWNER_TERMW:
			/* return the Owner term used in Schema information */
			str = "owner";
			break;

		case IP_INFO_QUALIFIER_NAMEW:
			str = m_sQualifier.toString();
			break;

		case IP_INFO_OWNER_NAMEW:
			/* we need to return information that matches schema */
			str = m_sUserName.toString();
			break;

		case IP_INFO_SUPPORT_SCHEMA_SEARCH_PATTERN:
			str = (!bAllowSchemaSearchPattern) ? "0" : "1"; /* false/true */
			break;

		case IP_INFO_SUPPORT_VALUE_FOR_RESULT_ALIAS:
		case IP_INFO_VALIDATE_TABLE_WITH_OWNER:
			str = "0"; /* false */
			break;

		case IP_INFO_FILTER_VIEWS_WITH_QUALIFIER_NAME:
		case IP_INFO_CONVERT_NUMERIC_VAL:
		case IP_INFO_TABLE_ROWSET_REPORT_MEMSIZE_LIMIT:
			str = "1"; /* true */
			break;
		case IP_INFO_TYPE_INFO:
			str = "1";
			break;
		case IP_INFO_DS_INFO:
			str = "1";
			break;
		default:
			jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipGetInfo(): Information type:"+ iInfoType +" is out of range\n");
		break;
		}
		return str;
	}

	public int ipSetInfo(int iInfoType,String InfoVal)
	{
		switch(iInfoType)
		{
		case IP_INFO_QUALIFIER_NAMEW:
			m_sQualifier.delete(0, m_sQualifier.length());
			m_sQualifier.append(InfoVal);
			break;
		default:
			jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipSetInfo(): Information type:"+ iInfoType +" is out of range\n");
		return DAM_NOT_AVAILABLE;
		}

		return IP_SUCCESS;
	}

	public int ipGetSupport(int iSupportType)
	{
		return(ip_support_array[iSupportType]);
	}

	/*ipConnect is called immediately after an instance of this object is created. You should
	 *perform any tasks related to connecting to your data source */
	public int ipConnect(long tmHandle,long dam_hdbc,String sDataSourceName, String sUserName, String sPassword,
			String sCurrentCatalog, String sIPProperties, String sIPCustomProperties)
	{
		int  iRetCode;
		
	    /* Save the trace handle */
		m_tmHandle = tmHandle;
		jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipConnect called\n");

		m_sQualifier.delete(0, m_sQualifier.length());
		m_sQualifier.append(OA_CATALOG_NAME);
		m_sUserName.delete(0, m_sUserName.length());
		m_sUserName.append(OA_USER_NAME);
		/* m_sUserName.append(sUserName); */

		/* Code to connect to your data source source.
               for our sample MDB database no need to connect */
			   
		/* Skip post processing. Post processing will be skipped only if query runs in PassThrough mode */	   
		iRetCode = jdam.dam_setOption(DAM_CONN_OPTION, dam_hdbc, DAM_CONN_OPTION_POST_PROCESSING, DAM_PROCESSING_OFF);
		if (iRetCode != DAM_SUCCESS) return iRetCode;
		
		return IP_SUCCESS;
	}

	public int ipDisconnect(long dam_hdbc)
	{   /* disconnect from the data source */
		jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipDisonnect called\n");
		return IP_SUCCESS;
	}

	public int ipStartTransaction(long dam_hdbc)
	{
		/* start a new transaction */
		jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipStartTransaction called\n");
		return IP_SUCCESS;
	}

	public int ipEndTransaction(long dam_hdbc,int iType)
	{
		/* end the transaction */
		jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipEndTransaction called\n");
		if (iType == DAM_COMMIT)
		{
		}
		else if (iType == DAM_ROLLBACK)
		{
		}
		return IP_SUCCESS;
	}

	/********************************************************************************************
    Method:          ipGetTypesInfo()
    Description:     returns types information
    Return:          Object[]
	 *********************************************************************************************/
	public oa_types_info[] ipGetTypesInfo()
	{
		/* types information */
		int i = 0;
		jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipGetTypesInfo called\n");
		oa_types_info[] typesInfo = new oa_types_info[22];
		typesInfo[i++] = new oa_types_info("CHAR", 1, 4096, "'", "'", "length", 1, 1, 3, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "CHAR");
		typesInfo[i++] = new oa_types_info("NUMERIC", 2, 40, null, null, "precision,scale", 1, 0, 2, 0, 0, 0, 0, 32, "NUMERIC");
		typesInfo[i++] = new oa_types_info("DECIMAL", 3, 40, null, null, "precision,scale", 1, 0, 2, 0, 0, 0, 0, 32, "DECIMAL");
		typesInfo[i++] = new oa_types_info("INTEGER", 4, 10, null, null, null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "INTEGER");
		typesInfo[i++] = new oa_types_info("BIGINT", XO_TYPE_BIGINT, 19, null, null, null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "BIGINT");
		typesInfo[i++] = new oa_types_info("SMALLINT", 5, 5, null, null, null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "SMALLINT");
		typesInfo[i++] = new oa_types_info("REAL", 7, 7, null, null, null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "REAL");
		typesInfo[i++] = new oa_types_info("DOUBLE", 8, 15, null, null, null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "DOUBLE");
		typesInfo[i++] = new oa_types_info("BINARY", -2, 4096, "0x", null, "length", 1, 0, 0, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "BINARY");
		typesInfo[i++] = new oa_types_info("VARBINARY", -3, 4096, "0x", null, "max length", 1, 0, 0, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "VARBINARY");
		typesInfo[i++] = new oa_types_info("LONGVARBINARY", -4, 2147483647, "0x", null, "max length", 1, 0, 0, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "LONGVARBINARY");
		typesInfo[i++] = new oa_types_info("VARCHAR", 12, 4096, "'", "'", "max length", 1, 1, 3, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "VARCHAR");
		typesInfo[i++] = new oa_types_info("LONGVARCHAR", -1, 2147483647, "'", "'", "max length", 1, 1, 3, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "LONGVARCHAR");
		typesInfo[i++] = new oa_types_info("DATE", 91, 10, "'", "'", null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "DATE");
		typesInfo[i++] = new oa_types_info("TIME", 92, 8, "'", "'", null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "TIME");
		typesInfo[i++] = new oa_types_info("TIMESTAMP", 93, 19, "'", "'", null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "TIMESTAMP");
		typesInfo[i++] = new oa_types_info("BIT", XO_TYPE_BIT, 1, null, null, null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "BIT");
		typesInfo[i++] = new oa_types_info("TINYINT", XO_TYPE_TINYINT, 3, null, null, null, 1, 0, 2, 1, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "TINYINT");
		typesInfo[i++] = new oa_types_info("NULL", XO_TYPE_NULL, 1, null, null, null, 1, 0, 2, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "NULL");
		typesInfo[i++] = new oa_types_info("WCHAR", -8, 4096, "N'", "'", "length", 1, 1, 3, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "WCHAR");
		typesInfo[i++] = new oa_types_info("WVARCHAR", -9, 4096, "N'", "'", "max length", 1, 1, 3, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "WVARCHAR");
		typesInfo[i] = new oa_types_info("WLONGVARCHAR", -10, 2147483647, "N'", "'", "max length", 1, 1, 3, 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "WLONGVARCHAR");

		jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipGetTypesInfo return\n");

		return typesInfo;
	}

	/********************************************************************************************
     Method:          ipGetDSInfo()
     Description:     returns DS information
     Return:          Object[]
	 *********************************************************************************************/
	public oa_ds_info[] ipGetDSInfo()
	{
		/* DS INFO */
		oa_ds_info[] dsInfo = new oa_ds_info[122];
		int j = 0;
		jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipGetDSInfo called\n");

		dsInfo[j++] = new oa_ds_info("SQL_ACTIVE_STATEMENTS", 1, 0, DAMOBJ_NOTSET, "", "The maximum number of statements supported.");
		dsInfo[j++] = new oa_ds_info("SQL_ROW_UPDATES", 11, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "N", "Y if driver can detect row changes between multiple fetches of");
		dsInfo[j++] = new oa_ds_info("SQL_ODBC_SQL_CONFORMANCE", 15, 0, DAMOBJ_NOTSET, "", "SQL Grammar supported by the driver");
		dsInfo[j++] = new oa_ds_info("SQL_SEARCH_PATTERN_ESCAPE", 14, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "\\", "");
		dsInfo[j++] = new oa_ds_info("SQL_DBMS_NAME", 17, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "OpenAccess", "");
		dsInfo[j++] = new oa_ds_info("SQL_DBMS_VER", 18, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "08.10", "Version of current DBMS product of the form ##.## ex: 01.00");
		dsInfo[j++] = new oa_ds_info("SQL_ACCESSIBLE_TABLES", 19, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "Y", "Y if user is guaranted access to all tables returned by SQL_TAB");
		dsInfo[j++] = new oa_ds_info("SQL_ACCESSIBLE_PROCEDURES", 20,DAMOBJ_NOTSET,DAMOBJ_NOTSET, "Y", "Y if dat source supports procedures");
		dsInfo[j++] = new oa_ds_info("SQL_PROCEDURES", 21, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "Y", "Y if user can execute returned by SQL_PROCEDURES");
		dsInfo[j++] = new oa_ds_info("SQL_CONCAT_NULL_BEHAVIOR", 22, 0, DAMOBJ_NOTSET, "", "0 if string+NULL=NULL / 1 if result is string");
		dsInfo[j++] = new oa_ds_info("SQL_DATA_SOURCE_READ_ONLY", 25, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "N", "Y if data source set to read only");
		dsInfo[j++] = new oa_ds_info("SQL_EXPRESSIONS_IN_ORDERBY", 27, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "Y", "Y if driver supports ORDER BY expression");
		dsInfo[j++] = new oa_ds_info("SQL_IDENTIFIER_CASE", 28, 4, DAMOBJ_NOTSET, "", "1= case insensitive(stored upper), 2 = lower case, 3 = case sensitive, stored mixed, 4 = case    insensitive, stored mixed");
		dsInfo[j++] = new oa_ds_info("SQL_IDENTIFIER_QUOTE_CHAR", 29, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "\"", "the character string used to surround a delimiter identifier. b");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_COLUMN_NAME_LEN", 30, 128, DAMOBJ_NOTSET, "", "Max length of a column name in the data source");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_OWNER_NAME_LEN", 32, 128, DAMOBJ_NOTSET, "", "Max length of an owner name in the data source");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_PROCEDURE_NAME_LEN", 33, 128, DAMOBJ_NOTSET, "", "Max length of a procedure name in the data source");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_QUALIFIER_NAME_LEN", 34, 128, DAMOBJ_NOTSET, "", "Max length of a qualifier name in the data source");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_TABLE_NAME_LEN", 35, 128, DAMOBJ_NOTSET, "", "Max length of a table name in the data source");
		dsInfo[j++] = new oa_ds_info("SQL_MULT_RESULT_SETS", 36, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "Y", "Y if databases support multiple result sets");
		dsInfo[j++] = new oa_ds_info("SQL_MULTIPLE_ACTIVE_TXN", 37, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "Y", "Y if transact. on multiple connection are allowed");
		dsInfo[j++] = new oa_ds_info("SQL_OUTER_JOINS", 38, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "Y", "Y if data source supports outer joins.");
		dsInfo[j++] = new oa_ds_info("SQL_PROCEDURE_TERM", 40, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "procedure", "the vendor's name for procedure");
		dsInfo[j++] = new oa_ds_info("SQL_QUALIFIER_NAME_SEPARATOR", 41, DAMOBJ_NOTSET, DAMOBJ_NOTSET, ".", "the character string defines as a separator between the qualifi");
		dsInfo[j++] = new oa_ds_info("SQL_TABLE_TERM", 45, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "table", "the vendor's name for table");
		dsInfo[j++] = new oa_ds_info("SQL_TXN_CAPABLE", 46, 1, DAMOBJ_NOTSET, "", "0= transact not supported. 1 = transact contains only DML state, 2=DML or DDL");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_FUNCTIONS", 48, DAMOBJ_NOTSET, 7, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_NUMERIC_FUNCTIONS", 49, DAMOBJ_NOTSET, 16777215, "", "bitmask enumerating scalar numeric functions supported by the d");
		dsInfo[j++] = new oa_ds_info("SQL_STRING_FUNCTIONS", 50, DAMOBJ_NOTSET, 16547839, "", "bitmask enumerating scalar string functions supported by the dr");
		dsInfo[j++] = new oa_ds_info("SQL_SYSTEM_FUNCTIONS", 51, DAMOBJ_NOTSET, 3, "", "bitmask enumerating scalar system functions supported by the dr");
		dsInfo[j++] = new oa_ds_info("SQL_TIMEDATE_FUNCTIONS", 52, DAMOBJ_NOTSET, 270335, "", "bitmask enumerating scalar time and date functions supported by");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_BIGINT", 53, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_BINARY", 54, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_BIT", 55, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_CHAR", 56, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_DATE", 57, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_DECIMAL", 58, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_DOUBLE", 59, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_FLOAT", 60, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_INTEGER", 61, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_LONGVARCHAR", 62, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_NUMERIC", 63, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_REAL", 64, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_SMALLINT", 65, DAMOBJ_NOTSET, 0, "","");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_TIME", 66, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_TIMESTAMP", 67, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_TINYINT", 68, DAMOBJ_NOTSET, 0, "",  "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_VARBINARY", 69, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_VARCHAR", 70, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CONVERT_LONGVARBINARY", 71, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_ODBC_SQL_OPT_IEF", 73, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "N", "Specifies whether the data source supports Integrity Enhancement");
		dsInfo[j++] = new oa_ds_info("SQL_TXN_ISOLATION_OPTION", 72, DAMOBJ_NOTSET, 7, "", "bitmask enumarating transaction isolation levels.");
		dsInfo[j++] = new oa_ds_info("SQL_CORRELATION_NAME", 74, 2, DAMOBJ_NOTSET, "", "16 bits integer indicating if correlation names are supported");
		dsInfo[j++] = new oa_ds_info("SQL_NON_NULLABLE_COLUMNS", 75, 1, DAMOBJ_NOTSET, "", "16 bit int specifying whether the data source supports non null");
		dsInfo[j++] = new oa_ds_info("SQL_GETDATA_EXTENSIONS", 81, DAMOBJ_NOTSET, 3, "", "32 bit bitamask enumarating extensions to SQLGetData");
		dsInfo[j++] = new oa_ds_info("SQL_NULL_COLLATION", 85, 1, DAMOBJ_NOTSET, "", "Specifies where null are sorted in a list.");
		dsInfo[j++] = new oa_ds_info("SQL_ALTER_TABLE", 86, DAMOBJ_NOTSET, 37867, "", "Bitmask enumerating supported ALTER TABLE clauses.");
		dsInfo[j++] = new oa_ds_info("SQL_COLUMN_ALIAS", 87, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "Y", "Y if driver supports column alias");
		dsInfo[j++] = new oa_ds_info("SQL_GROUP_BY", 88, 2, DAMOBJ_NOTSET, "", "16 bit int specifying the relationship between col in group by. 2=SQL_GB_GROUP_BY_CONTAINS_SELECT");
		dsInfo[j++] = new oa_ds_info("SQL_KEYWORDS", 89, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "HINT,IDENTIFIED", "List of source specific Keywords");
		dsInfo[j++] = new oa_ds_info("SQL_ORDER_BY_COLUMNS_IN_SELECT", 90, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "N", "Y if the columns in the ORDER BY stmt clause must be in the sel");
		dsInfo[j++] = new oa_ds_info("SQL_OWNER_USAGE", 91, DAMOBJ_NOTSET, 15, "", "Enumarates the statements in which owners can be used.");
		dsInfo[j++] = new oa_ds_info("SQL_QUALIFIER_USAGE", 92, DAMOBJ_NOTSET, 7, "", "bitmask enumerating the statements in which a qualifier can be");
		dsInfo[j++] = new oa_ds_info("SQL_QUOTED_IDENTIFIER_CASE", 93, 4, DAMOBJ_NOTSET, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SPECIAL_CHARACTERS", 94, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "*()(}|:;", "List of special characters that can be used in an object name");
		dsInfo[j++] = new oa_ds_info("SQL_SUBQUERIES", 95, DAMOBJ_NOTSET, 31, "", "bitmask enumerating predicates that support subqueries");
		dsInfo[j++] = new oa_ds_info("SQL_UNION", 96, DAMOBJ_NOTSET, 3, "", "Bitmask enumarating the support for the union clause");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_COLUMNS_IN_GROUP_BY", 97, 0, DAMOBJ_NOTSET, "", "Max number of columns in a GROUP BY stmt. 0 if unknown");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_COLUMNS_IN_INDEX", 98, 0, DAMOBJ_NOTSET, "", "Max number of columns in an index. 0 if unknown");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_COLUMNS_IN_ORDER_BY", 99, 0, DAMOBJ_NOTSET, "", "Max number of columns in an ORDER BY clause. 0 if unknown");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_COLUMNS_IN_SELECT", 100, 0, DAMOBJ_NOTSET, "", "Max column in SELECT stmt. 0 if unknown");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_COLUMNS_IN_TABLE", 101, 0, DAMOBJ_NOTSET, "", "Max number of tables in a table");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_INDEX_SIZE", 102, DAMOBJ_NOTSET, 0, "", "Max number of bytes allowed in the combined field of an index.");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_ROW_SIZE_INCLUDES_LONG", 103, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "N", "Y if MAX_ROW_SIZE includes the length of all long Data types.");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_ROW_SIZE", 104, DAMOBJ_NOTSET, 0, "", "Max size of a row in a datasource. This limitation comes from t");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_STATEMENT_LEN", 105, DAMOBJ_NOTSET, 32768, "", "Max length of an SQL stmt");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_TABLES_IN_SELECT", 106, 0, DAMOBJ_NOTSET, "", "Max table number in Select stmt. 0 if unknown");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_CHAR_LITERAL_LEN", 108, DAMOBJ_NOTSET, 8192, "", "32 bits int specifying max length of a character literal in a S");
		dsInfo[j++] = new oa_ds_info("SQL_TIMEDATE_ADD_INTERVALS", 109, DAMOBJ_NOTSET, 0, "", "bitmask enumerating the timestamp intervals supported in TIMEST");
		dsInfo[j++] = new oa_ds_info("SQL_TIMEDATE_DIFF_INTERVALS", 110, DAMOBJ_NOTSET, 0, "", "bitmask enumerating the timestamp intervals supported in TIMEST");
		dsInfo[j++] = new oa_ds_info("SQL_MAX_BINARY_LITERAL_LEN", 112, DAMOBJ_NOTSET, 8192, "", "32 bits int specifying max length of a binary literal");
		dsInfo[j++] = new oa_ds_info("SQL_LIKE_ESCAPE_CLAUSE", 113, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "Y", "Y if data source supports escape character in LIKE clause");
		dsInfo[j++] = new oa_ds_info("SQL_QUALIFIER_LOCATION", 114, 1, DAMOBJ_NOTSET, "", "indicates the position of the qualifier in a qualified table name");
		dsInfo[j++] = new oa_ds_info("SQL_OJ_CAPABILITIES", 115, DAMOBJ_NOTSET, 0x49, "", " bitmask enumerating the types of outer joins supported by the driver ");
		dsInfo[j++] = new oa_ds_info("SQL_ALTER_DOMAIN", 117, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL_CONFORMANCE", 118, DAMOBJ_NOTSET, 2, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DATETIME_LITERALS", 119, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_BATCH_ROW_COUNT", 120, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_BATCH_SUPPORT", 121, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CREATE_ASSERTION", 127, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CREATE_CHARACTER_SET", 128, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CREATE_COLLATION", 129, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CREATE_DOMAIN", 130, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CREATE_SCHEMA", 131, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CREATE_TABLE", 132, DAMOBJ_NOTSET, 1, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CREATE_TRANSLATION", 133, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_CREATE_VIEW", 134, DAMOBJ_NOTSET, 1, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DROP_ASSERTION", 136, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DROP_CHARACTER_SET", 137, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DROP_COLLATION", 138, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DROP_DOMAIN", 139, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DROP_SCHEMA", 140, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DROP_TABLE", 141, DAMOBJ_NOTSET, 1, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DROP_TRANSLATION", 142, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DROP_VIEW", 143, DAMOBJ_NOTSET, 1, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_INDEX_KEYWORDS", 148, DAMOBJ_NOTSET, 3, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_INFO_SCHEMA_VIEWS", 149, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_DATETIME_FUNCTIONS", 155, DAMOBJ_NOTSET, 7, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_FOREIGN_KEY_DELETE_RULE", 156, DAMOBJ_NOTSET, 2, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_FOREIGN_KEY_UPDATE_RULE", 157, DAMOBJ_NOTSET, 2, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_GRANT", 158, DAMOBJ_NOTSET, 3184, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_NUMERIC_VALUE_FUNCTIONS", 159, DAMOBJ_NOTSET, 37, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_PREDICATES", 160, DAMOBJ_NOTSET, 16135, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_RELATIONAL_JOIN_OPERATORS", 161, DAMOBJ_NOTSET, 848, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_REVOKE", 162, DAMOBJ_NOTSET, 3184, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_ROW_VALUE_CONSTRUCTOR", 163, DAMOBJ_NOTSET, 11, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_STRING_FUNCTIONS", 164, DAMOBJ_NOTSET, 145, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_SQL92_VALUE_EXPRESSIONS", 165, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_AGGREGATE_FUNCTIONS", 169, DAMOBJ_NOTSET, 127, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_DDL_INDEX", 170, DAMOBJ_NOTSET, 0, "", "");
		dsInfo[j++] = new oa_ds_info("SQL_INSERT_STATEMENT", 172, DAMOBJ_NOTSET, 3, "", "");
		dsInfo[j] = new oa_ds_info("SQL_COLLATION_SEQ", 10004, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "ISO 8859-1", "The name of the collation sequence for the default character set (for example, 'ISO 8859-1' or EBCDIC). ");

		jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipGetDSInfo return\n");

		return dsInfo;
	}


	public int ipExecute(long dam_hstmt, int iStmtType, long hSearchCol,xo_long piNumResRows)
	{
		int iRetCode = 0;

		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipExecute called\n");

		/* set pass through mode for simple query : select count(*) from curvalue */		
		if (iStmtType == DAM_SET_QUERY_MODE) {
									
			if ( java_checkIfQueryCanRunInPassThrough(dam_hstmt) ) {				
				iRetCode = jdam.dam_setOption(DAM_STMT_OPTION, dam_hstmt, DAM_STMT_OPTION_PASSTHROUGH_QUERY, DAM_PROCESSING_ON); 				
				if (iRetCode != DAM_SUCCESS) return iRetCode; 								
			}	
			else { 				
				iRetCode = jdam.dam_setOption(DAM_STMT_OPTION, dam_hstmt, DAM_STMT_OPTION_PASSTHROUGH_QUERY, DAM_PROCESSING_OFF);
				if (iRetCode != DAM_SUCCESS) return iRetCode;
			}
			return DAM_SUCCESS;
		}

		java_init_stmt(dam_hstmt, iStmtType);    	

		switch(iStmtType)
		{
		case DAM_SELECT:
		{
			xo_int  piValue;

			piValue = new xo_int();

			/* get fetch block size */
			iRetCode = jdam.dam_getInfo(0, dam_hstmt, DAM_INFO_FETCH_BLOCK_SIZE, null, piValue);
			if (iRetCode != DAM_SUCCESS) iFetchSize = 2;
			else iFetchSize = piValue.getVal();
			jdam.trace(m_tmHandle, UL_TM_INFO, "ipExecute(): Fetch Block Size is set to " + iFetchSize + "rows.\n");
			
			/* Check the query mode */
			piValue.setVal(0);
			jdam.dam_getInfo(0, dam_hstmt, DAM_INFO_PASSTHROUGH_QUERY, null, piValue);
			if(piValue.getVal() != 0) 
			{
				/* Query is in pass though mode, add the final result */
				return java_exec_passthrough_query(dam_hstmt, piNumResRows);
			}
										
			/* process tablefunctions */
			if (isTableFunction == DAM_TRUE)
			{
				String sTableFunction = sTableName.toString();
				if (sTableFunction.compareToIgnoreCase("TYPES_F") == 0)
				{
					return java_exec_table_function_types_f(dam_hstmt);
				}
				else if (sTableFunction.compareToIgnoreCase("CURVALUE") == 0)
				{
					return java_exec_table_function_curvalue(dam_hstmt);
				}
				else if (sTableFunction.compareToIgnoreCase("SAMPLE_F") == 0)
				{
					return java_exec_table_function_sample_f(dam_hstmt);
				}
			}

			iRetCode = java_exec_stmt(dam_hstmt,iStmtType, piNumResRows);
			if (iRetCode != IP_SUCCESS) return iRetCode;    		
		}
		break;
		case DAM_INSERT:
		{
			if(sTableName.toString().equalsIgnoreCase("CURVALUE"))
			{
				long         hrow;

				/* for each row to be inserted, get the column values and insert */
				hrow = jdam.dam_getFirstInsertRow(dam_hstmt);
				while (hrow != 0) {
					/* insert the row */
					iRetCode = java_insert_row(dam_hstmt, hrow);
					if (iRetCode != IP_SUCCESS) return iRetCode;

					hrow = jdam.dam_getNextInsertRow(dam_hstmt);
				}
				piNumResRows.setVal(m_iNumResRows);
			}
			else
			{
				jdam.trace(m_tmHandle, UL_TM_ERRORS, "ipExecute():INSERT is supported only on CURVALUE table.\n");
				jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, "ipExecute():INSERT is supported only on CURVALUE table.");
				return DAM_FAILURE;
			}
		}
		break;
		case DAM_INSERT_BULK:
		{
			if(sTableName.toString().equalsIgnoreCase("BULK_TABLE"))
			{
				iRetCode = java_insert_bulk_table_rows(dam_hstmt);
				if (iRetCode != IP_SUCCESS) return iRetCode;
				piNumResRows.setVal(m_iNumResRows);
			}
			else
			{
				jdam.trace(m_tmHandle, UL_TM_ERRORS, "BULK INSERT is supported only on BULK_TABLE.\n");
				jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, "BULK INSERT is supported only on BULK_TABLE.");
				return DAM_FAILURE;
			}
		}
		break;
		case DAM_UPDATE:
		{
			if(sTableName.toString().equalsIgnoreCase("CURVALUE"))
			{
				iRetCode = java_exec_stmt(dam_hstmt,iStmtType, piNumResRows);
				if (iRetCode != IP_SUCCESS) return iRetCode; 
			}
			else
			{
				jdam.trace(m_tmHandle, UL_TM_ERRORS, "UPDATE is supported only on CURVALUE table.\n");
				jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, "UPDATE is supported only on CURVALUE table.");
				return DAM_FAILURE;
			}
		}
		break;
		case DAM_DELETE:
		{
			iRetCode = java_exec_stmt(dam_hstmt,iStmtType, piNumResRows);
			if (iRetCode != IP_SUCCESS) return iRetCode;    	
		}
		break;
		case DAM_FETCH:
		{
			xo_int  piValue;

			piValue = new xo_int();

			/* check if query was cancelled */
			iRetCode = jdam.dam_getInfo(0, dam_hstmt, DAM_INFO_QUERY_CANCEL, null, piValue);

			if (piValue.getVal() > 0)
			{
				jdam.trace(m_tmHandle, UL_TM_ERRORS, "Query is Cancelled.\n");
				jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, "Query is Cancelled");
				return DAM_FAILURE;
			}

			/* query processing */
			iRetCode = java_exec(dam_hstmt, iStmtType, piNumResRows);

			/* check if there are any pending result rows*/
			if (iRetCode == DAM_SUCCESS_WITH_RESULT_PENDING)
			{
				jdam.trace(m_tmHandle, UL_TM_INFO, "ipExecute(): Returning with result rows pending.\n");
				return iRetCode;
			}

			/* check for errors*/
			if (iRetCode != IP_SUCCESS) return iRetCode;
		}
		case DAM_CLOSE:
			return IP_SUCCESS;
		default:
			return IP_FAILURE;
		}

		if((iStmtType == DAM_SELECT || iStmtType == DAM_FETCH) && (hindex.getVal() == 0))
			jdam.trace(m_tmHandle, UL_TM_INFO, "ipExecute(): Returning with no result rows pending.\n");

		return IP_SUCCESS;
	}

	public int ipSchema(long dam_hdbc,long pMemTree,int iType, long pList, Object pSearchObj)
	{
		switch(iType)
		{
		case DAMOBJ_TYPE_CATALOG:
		{
			schemaobj_table TableObj = new schemaobj_table(OA_CATALOG_NAME,null,null,null,null,null,null,null);

			jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
		}
		break;
		case DAMOBJ_TYPE_SCHEMA:
		{
			schemaobj_table TableObj = new schemaobj_table();

			TableObj.SetObjInfo(null,"SYSTEM",null,null,null,null,null,null);
			jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);

			TableObj.SetObjInfo(null,OA_USER_NAME,null,null,null,null,null,null);
			jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
		}
		break;
		case DAMOBJ_TYPE_TABLETYPE:
		{
			schemaobj_table TableObj = new schemaobj_table();

			TableObj.SetObjInfo(null,null,null,"SYSTEM TABLE",null,null,null,null);
			jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);

			TableObj.SetObjInfo(null,null,null,"TABLE",null,null,null,null);
			jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);

			TableObj.SetObjInfo(null,null,null,"VIEW",null,null,null,null);
			jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
		}

		break;
		case DAMOBJ_TYPE_TABLE:
		{
			schemaobj_table  pTableSearchObj = (schemaobj_table) pSearchObj;
			if (pTableSearchObj != null)
			{
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema  of table:<"+pTableSearchObj.getTableQualifier()+"."+pTableSearchObj.getTableOwner()+"."+pTableSearchObj.getTableName()+"> is being requested\n");
			}
			else
			{
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all tables is being requested\n");
			}

			if(jdam.dam_isSchemaTableFunction(pTableSearchObj) == DAM_TRUE)   /* Table Function */
			{
				if (IsMatchingTable(pTableSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_F"))
				{
					schemaobj_table TableObj = new schemaobj_table();

					TableObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_F", "TABLE FUNCTION", null, null, null, "Sample Table Function");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, TableObj);
				}
				if (IsMatchingTable(pTableSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE"))
				{
					schemaobj_table TableObj = new schemaobj_table();

					TableObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE", "TABLE FUNCTION", null, null, null, "CURVALUE Table Function");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, TableObj);
				}
				if (IsMatchingTable(pTableSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "TYPES_F"))
				{
					schemaobj_table TableObj = new schemaobj_table();

					TableObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "TYPES_F", "TABLE FUNCTION", null, null, null, "Types Table Function");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, TableObj);
				}
			}
			else     /* Table */
			{					
				if (IsMatchingTable(pTableSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE"))
				{
					schemaobj_table TableObj = new schemaobj_table();

					TableObj.SetObjInfo(OA_CATALOG_NAME,OA_USER_NAME,"CURVALUE","TABLE",null,null,null,"Current Values Table");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj, TableObj);
				}

				if (IsMatchingTable(pTableSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE"))
				{
					schemaobj_table TableObj = new schemaobj_table();

					TableObj.SetObjInfo(OA_CATALOG_NAME,OA_USER_NAME,"BULK_TABLE","TABLE",null,null,null,"Table for Implementing Bulk Insert");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj, TableObj);
				}
			}	

		}
		break;
		case DAMOBJ_TYPE_COLUMN:
		{
			schemaobj_column pColSearchObj = (schemaobj_column) pSearchObj;
			boolean bIsTableFunction = false;

			if (pColSearchObj != null)
			{
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for column <"+pColSearchObj.getColumnName()+"> of table:<"+pColSearchObj.getTableQualifier()+"."+pColSearchObj.getTableOwner()+"."+pColSearchObj.getTableName()+"> is being requested\n");
				bIsTableFunction = pColSearchObj.getTableObj() != 0 ? true : false;
			}
			else
			{
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all columns of all tables is being requested\n");
			}

			if(bIsTableFunction)
			{
				// SAMPLE_F TABLEFUNCTION COLUMNS
				if (IsMatchingColumn(pColSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_F"))
				{
					if (pColSearchObj != null)
					{
						long hValExpList;
						hValExpList = jdam.dam_getTableFunctionArgList(pColSearchObj.getTableObj());
						String strObj;
						xo_int piRetCode = new xo_int(0);
						int iArgCount = 0;

						/* get the input */
						long hValExp = jdam.dam_getFirstValExp(hValExpList);
						while (hValExp != 0)
						{
							iArgCount++;
							hValExp = jdam.dam_getNextValExp(hValExpList);
						}
						if (iArgCount != NO_OF_ARGS_FOR_SAMPLE_F)
						{
							String dam_msgBuf = "Invalid Number of Parameters to the table function:SAMPLE_F. SAMPLE_F takes only one argument.";
							jdam.dam_addError(0, 0, DAM_IP_ERROR, 0, dam_msgBuf);
							return DAM_FAILURE;
						}

						hValExp = jdam.dam_getFirstValExp(hValExpList);
						strObj = (String)jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_CHAR, piRetCode);
						if (strObj.equalsIgnoreCase("ONE"))
						{
							schemaobj_column ColumnObj = new schemaobj_column();

							ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_F", "COLUMN1", (short)12, "VARCHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
									(short)XO_NO_NULLS, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "COLUMN1");
							jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);
						}
						else if (strObj.equalsIgnoreCase("TWO"))
						{
							schemaobj_column ColumnObj = new schemaobj_column();

							ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_F", "COLUMN1", (short)12, "VARCHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
									(short)XO_NO_NULLS, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "COLUMN1");
							jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

							ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_F", "COLUMN2", (short)12, "VARCHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
									(short)XO_NO_NULLS, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "COLUMN2");
							jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);
						}
						else if (strObj.equalsIgnoreCase("THREE"))
						{
							schemaobj_column ColumnObj = new schemaobj_column();

							ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_F", "COLUMN1", (short)12, "VARCHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
									(short)XO_NO_NULLS, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "COLUMN1");
							jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

							ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_F", "COLUMN2", (short)4, "INTEGER", 4, 10, (short)DAMOBJ_NOTSET, (short)0,
									(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "COLUMN2");
							jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

							ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_F", "COLUMN3", (short)8, "DOUBLE", 8, 15, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
									(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "COLUMN3");
							jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);
						}
						else
						{
							String errorMsg = "Invalid Parameter to the table function:SAMPLE_F";
							jdam.dam_addError(0, 0, DAM_IP_ERROR, 0, errorMsg);
							return IP_FAILURE;
						}
					}
				}

				/* CURVALUE table functions columns */
				if (IsMatchingColumn(pColSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE"))
				{

					if (pColSearchObj != null)
					{
						long hValExpList = jdam.dam_getTableFunctionArgList(pColSearchObj.getTableObj());
						int iColCount = 0;
						String strObj;
						xo_int piRetCode = new xo_int(0);

						/* get the input */
						long hValExp = jdam.dam_getFirstValExp(hValExpList);

						while (hValExp != 0)
						{
							iColCount++;
							if (iColCount <= MAX_ARGS_FOR_CURVALUE_F)
							{

								jdam.dam_getValueTypeOfExp(pMemTree, hValExpList, hValExp);
								strObj = (String)jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_CHAR, piRetCode);
								if (piRetCode.getVal() == DAM_FAILURE) return DAM_FAILURE;

								schemaobj_column ColumnObj = new schemaobj_column();

								ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE", strObj, (short)12, "VARCHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
										(short)XO_NO_NULLS, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "Column of the CURVALUE table function");
								jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);
							}
							hValExp = jdam.dam_getNextValExp(hValExpList);
						}
						if (iColCount > MAX_ARGS_FOR_CURVALUE_F)
						{
							String errorMsg = "Invalid Number of Parameters to the Table Function:CURVALUE. Max. Number of Parameters is" + MAX_ARGS_FOR_CURVALUE_F;
							jdam.dam_addError(0, 0, DAM_IP_ERROR, 0, errorMsg);
							return IP_FAILURE;
						}
					}
				}

				if (IsMatchingColumn(pColSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "TYPES_F"))
				{

					if (pColSearchObj != null)
					{

						schemaobj_column ColumnObj = new schemaobj_column();

						ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "TYPES_F", "INTCOL", (short)4, "INTEGER", 4, 10, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
								(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)DAMOBJ_NOTSET, (short)0, "Integer Field of the TYPES_F table function");
						jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

						ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "TYPES_F", "FLOATCOL", (short)6, "DOUBLE", 8, 15, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
								(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)DAMOBJ_NOTSET, (short)0, "Float Field of the TYPES_F table function");
						jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

						ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "TYPES_F", "DATECOL", (short)9, "DATE", 6, 10, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
								(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)DAMOBJ_NOTSET, (short)0, "Date Field of the TYPES_F table function");
						jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

						ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "TYPES_F", "TIMESTAMPCOL", (short)11, "TIMESTAMP", 16, 19, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
								(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)DAMOBJ_NOTSET, (short)0, "Timestamp Field of the TYPES_F table function");
						jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);
					}
				}
			}
			else
			{					
				// CURVALUE TABLE COLUMNS
				if (IsMatchingColumn(pColSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE"))
				{
					schemaobj_column ColumnObj = new schemaobj_column();

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE", "NAME", (short)12, "VARCHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
							(short)XO_NO_NULLS, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "name");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE", "INTVAL", (short)4, "INTEGER", 4, 10, (short)DAMOBJ_NOTSET, (short)0,
							(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "Integer Value");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE", "FLOATVAL", (short)8, "DOUBLE", 8, 15, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "Float Value");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE", "TIME", (short)11, "TIMESTAMP", 16, 19, (short)DAMOBJ_NOTSET, (short)0,
							(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "Time Value");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);
				}

				//BULK TABLE COLUMNS
				if (IsMatchingColumn(pColSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE"))
				{
					schemaobj_column ColumnObj = new schemaobj_column();

					//String Columns
					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE", "CHAR_VAL", (short)XO_TYPE_CHAR, "CHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE", "VARCHAR_VAL", (short)XO_TYPE_VARCHAR, "VARCHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE", "WCHAR_VAL", (short)XO_TYPE_WCHAR, "WCHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE", "WVARCHAR_VAL", (short)XO_TYPE_WVARCHAR, "WVARCHAR", 32, 32, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);
					
					//Numeric/Decimal columns
					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","NUMERIC_VAL",(short)XO_TYPE_NUMERIC,"NUMERIC",34,32,(short)DAMOBJ_NOTSET,(short)5,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Numeric Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","DECIMAL_VAL",(short)XO_TYPE_DECIMAL,"DECIMAL",34,32,(short)DAMOBJ_NOTSET,(short)5,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Decimal Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
					
					//Integer Columns
					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","BIGINT_VAL",(short)XO_TYPE_BIGINT,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Bigint Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","INT_VAL",(short)XO_TYPE_INTEGER,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"ID/Integer Field");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","SMALLINT_VAL",(short)XO_TYPE_SMALL,"SHORTINT",2,5,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Bigint Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","TINYINT_VAL",(short)XO_TYPE_TINYINT,"TINYINT",1,3,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Tinyint Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
					
					//Bit column
					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME,"BULK_TABLE","BIT_VAL",(short)XO_TYPE_BIT,"BIT",1,1,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Bit Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
					
					//Float/Double columns
					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE", "FLOAT_VAL", (short)XO_TYPE_FLOAT, "FLOAT", 8, 15, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null, (short)SQL_PC_NOT_PSEUDO, (short)0, "Float Value");
					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, ColumnObj);
					
					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","REAL_VAL",(short)XO_TYPE_REAL,"REAL",4,7,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Real Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
					
					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","DOUBLE_VAL",(short)XO_TYPE_DOUBLE,"DOUBLE",8,15,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Double Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
					
					//Date, Time and Timestamp Columns
					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","DATE_VAL",(short)9,"DATE",6,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Date Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","TIME_VAL",(short)10,"TIME",6,8,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Time Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","TIMESTAMP_VAL",(short)11,"TIMESTAMP",16,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Date Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
					
					//Binary Columns
					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","BINARY_VAL",(short)XO_TYPE_BINARY,"BINARY",32,32,(short)5,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Binary Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

					ColumnObj.SetObjInfo(OA_CATALOG_NAME, OA_USER_NAME, "BULK_TABLE","VARBINARY_VAL",(short)XO_TYPE_VARBINARY,"VARBINARY",32000,32000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
							(short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Varbinary Value");
					jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
					
					
					
				}
			}
		}

		break;

		case DAMOBJ_TYPE_STAT:
		{
			schemaobj_stat pSearchStatObj = (schemaobj_stat)pSearchObj;
			boolean bIsTableFunction = false;

			if (pSearchStatObj != null)
			{
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for Statistics of table:<" + pSearchStatObj.getTableQualifier() + "." + pSearchStatObj.getTableOwner() + "." + pSearchStatObj.getTableName() + "> is being requested\n");
				bIsTableFunction = (pSearchStatObj.getTableObj() != 0) ? true : false;
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipSchema():bIsTableFunction=" + bIsTableFunction + "\n");
			}
			else
			{
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all statistics of all tables is being requested\n");
			}

			if (pSearchStatObj == null || pSearchStatObj.getTableName().equalsIgnoreCase("CURVALUE") && !bIsTableFunction)
			{
				schemaobj_stat StatObj = new schemaobj_stat(OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE", (short)0, OA_CATALOG_NAME, "OA_NAME", (short)3, (short)1, "NAME",
						"A", DAMOBJ_NOTSET, DAMOBJ_NOTSET, null);
				jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, StatObj);
			}
			jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipSchema():pSearchStatObj.getTableName()=" + pSearchStatObj.getTableName() + "\n");

			if (pSearchStatObj.getTableName().equalsIgnoreCase("CURVALUE") && bIsTableFunction)
			{
				long hValExpList;
				hValExpList = jdam.dam_getTableFunctionArgList(pSearchStatObj.getTableObj());
				String strObj;
				xo_int piRetCode = new xo_int(0);

				/* get the input */
				if (hValExpList != 0)
				{
					long hValExp = jdam.dam_getFirstValExp(hValExpList);
					strObj = (String)jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_CHAR, piRetCode);

					schemaobj_stat StatObj = new schemaobj_stat(OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE", (short)0, OA_CATALOG_NAME, "OA_NAME", (short)3, (short)1, strObj,
							"A", DAMOBJ_NOTSET, DAMOBJ_NOTSET, null);

					jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, StatObj);
				}
			}
		}
		break;

		case DAMOBJ_TYPE_FKEY:
			break;
		case DAMOBJ_TYPE_PKEY:
			break;
		case DAMOBJ_TYPE_PROC:
			break;
		case DAMOBJ_TYPE_PROC_COLUMN:
			break;
		default:
			break;
		}

		return IP_SUCCESS;

	}

	private boolean     IsMatchingTable(schemaobj_table pSearchObj,
			String table_qualifier,
			String table_owner,
			String table_name)
	{
		if (pSearchObj == null) return true;
		boolean  bSearchPattern = !(!bAllowSchemaSearchPattern || (jdam.dam_isSearchPatternObject(pSearchObj) == 0));

		if(!bSearchPattern)
		{
			/* match the search pattern */
			if ((pSearchObj.getTableQualifier() != null) && !pSearchObj.getTableQualifier().equalsIgnoreCase(table_qualifier))
				return false;
			if ((pSearchObj.getTableOwner() != null) && !pSearchObj.getTableOwner().equalsIgnoreCase(table_owner))
				return false;
			if ((pSearchObj.getTableName() != null) && !pSearchObj.getTableName().equalsIgnoreCase(table_name))
				return false;
		}
		else
		{
			/* match the search pattern */
			if ((pSearchObj.getTableQualifier() != null) && jdam.dam_strlikecmp(pSearchObj.getTableQualifier(),table_qualifier) != 0)
				return false;
			if ((pSearchObj.getTableOwner() != null) && jdam.dam_strlikecmp(pSearchObj.getTableOwner(),table_owner) != 0)
				return false;
			if ((pSearchObj.getTableName() != null) && jdam.dam_strlikecmp(pSearchObj.getTableName(),table_name) != 0)
				return false;
		}

		return true;
	}

	private boolean     IsMatchingColumn(schemaobj_column pSearchObj,
			String table_qualifier,
			String table_owner,
			String table_name)
	{
		if (pSearchObj == null) return true;
		boolean  bSearchPattern = !(!bAllowSchemaSearchPattern || (jdam.dam_isSearchPatternObject(pSearchObj) == 0));

		if(!bSearchPattern)
		{
			/* match the search pattern */
			if ((pSearchObj.getTableQualifier() != null) && !pSearchObj.getTableQualifier().equalsIgnoreCase(table_qualifier))
				return false;
			if ((pSearchObj.getTableOwner() != null) && !pSearchObj.getTableOwner().equalsIgnoreCase(table_owner))
				return false;
			if ((pSearchObj.getTableName() != null) && !pSearchObj.getTableName().equalsIgnoreCase(table_name))
				return false;
		}
		else
		{
			/* match the search pattern */
			if ((pSearchObj.getTableQualifier() != null) && jdam.dam_strlikecmp(pSearchObj.getTableQualifier(),table_qualifier) != 0)
				return false;
			if ((pSearchObj.getTableOwner() != null) && jdam.dam_strlikecmp(pSearchObj.getTableOwner(),table_owner) !=0 )
				return false;
			if ((pSearchObj.getTableName() != null) && jdam.dam_strlikecmp(pSearchObj.getTableName(),table_name) != 0)
				return false;
		}

		return true;
	}


	/* this example uses dynamic schema and only SELECT command supported, so following functions are not called */
	public int        ipDDL(long dam_hstmt, int iStmtType, xo_long piNumResRows)
	{
		return IP_FAILURE;
	}

	public int        ipProcedure(long dam_hstmt, int iType, xo_long piNumResRows)
	{
		return IP_FAILURE;
	}

	public int        ipDCL(long dam_hstmt, int iStmtType, xo_long piNumResRows)
	{
		return IP_FAILURE;
	}

	public int        ipPrivilege(int iStmtType,String pcUserName,String pcCatalog,String pcSchema,String pcObjName)
	{
		return IP_FAILURE;
	}

	public int        ipNative(long dam_hstmt, int iCommandOption, String sCommand, xo_long piNumResRows)
	{
		return IP_FAILURE;
	}

	public int        ipSchemaEx(long dam_hstmt, long pMemTree, int iType, long pList,Object pSearchObj)
	{
		return IP_FAILURE;
	}

	public int        ipProcedureDynamic(long dam_hstmt, int iType, xo_long piNumResRows)
	{
		return IP_FAILURE;
	}

	// Utility functions
	int java_optimize_exec(long dam_hstmt, xo_long hindex, xo_long hset_of_condlist,int iStmtType, xo_long piNumResRows)
	{
		long hcur_condlist;  /* DAM_HCONDLIST */
		long hcond;          /* DAM_HCOND */
		long hrow;           /* DAM_HROW */
		int iRetCode;
		m_iNumResRows = 0;
		piNumResRows.setVal(0);
		/* get the conditions on index columns */
		hcur_condlist = jdam.dam_getFirstCondList(hset_of_condlist.getVal());
		while (hcur_condlist != 0)
		{
			xo_int iLeftOp = new xo_int(0);
			xo_int iLeftValType = new xo_int(0);
			xo_int iLeftValLen = new xo_int(0);
			xo_int iStatus = new xo_int(0);
			String pLeftData;
			xo_int piMdbIndex =  new xo_int(0);
			/* Each condition list will have only one condition, since its a single column index */
			hcond = jdam.dam_getFirstCond(dam_hstmt, hcur_condlist);
			/* get details of the condition. since this example does not support BETWEEN, we do not
                   need to get the values for RIGHT part of condition */
			pLeftData = (String)jdam.dam_describeCondEx(dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftValType, iLeftValLen, iStatus);
			iRetCode = iStatus.getVal();
			if (iRetCode != DAM_SUCCESS) return IP_FAILURE; /* return on error */
			/* build result rows that match current index condition */
			if (m_pMdb.FindItemByName(pLeftData,piMdbIndex))
			{   /* found a match */
				String    sName;
				int       iIntVal;
				double    dDoubleVal;
				long      lTime;
				int		  iCurRow = piMdbIndex.getVal();

				/* read row from the memory database */
				m_pMdb.ReadRow(iCurRow);
				/* Get the field values from the Row */
				sName = m_pMdb.getName(iCurRow);
				iIntVal = m_pMdb.getIntVal(iCurRow);
				dDoubleVal = m_pMdb.getDVal(iCurRow);
				lTime = m_pMdb.getTime(iCurRow);
				/* build the DAM row with the values read from memory database */
				hrow = java_build_curvalue_row(dam_hstmt, sName, iIntVal, dDoubleVal, lTime);
				if (hrow == 0) return IP_FAILURE; /* return on error */
				/* process the row */
				iRetCode = java_process_row(dam_hstmt, iStmtType,hrow, iCurRow);
				if (iRetCode != IP_SUCCESS) return iRetCode; /* return on error */
			}
			hcur_condlist = jdam.dam_getNextCondList(hset_of_condlist.getVal());
		}
		piNumResRows.setVal(m_iNumResRows);
		return IP_SUCCESS;
	}
	/* this function is called to do a full table scan */
	public int java_exec(long dam_hstmt, int iStmtType, xo_long piNumResRows)
	{
		jdam.dam_describeTable(dam_hstmt, null, null, sTableName, null, null);

		if(sTableName.toString().equalsIgnoreCase("CURVALUE"))
		{
			return java_exec_curvalue(dam_hstmt, iStmtType, piNumResRows);
		}

		if(sTableName.toString().equalsIgnoreCase("BULK_TABLE"))
		{
			return java_exec_bulk_table(dam_hstmt, iStmtType, piNumResRows);
		}

		return IP_FAILURE;
	}

	public int java_exec_curvalue(long dam_hstmt, int iStmtType, xo_long piNumResRows)
	{
		long    hrow; /* DAM_HROW */
		int     iRetCode;
		int     iIntVal;
		double  dDoubleVal;
		long    lTime;
		boolean bFound = false;
		String  sName;

		m_iNumResRows = 0;
		piNumResRows.setVal(0);
		if(iStmtType == DAM_SELECT  || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE)
		{
			/* read row from the memory database */
			iCurrentRow.setVal(0);
			bFound = m_pMdb.FirstRow(iCurrentRow);
		}
		else
		{
			bFound = m_pMdb.NextRow(iCurrentRow);
		}
		while (bFound)
		{   /* Get the Row */
			int iCurRow = iCurrentRow.getVal();

			sName = m_pMdb.getName(iCurRow);
			iIntVal = m_pMdb.getIntVal(iCurRow);
			dDoubleVal = m_pMdb.getDVal(iCurRow);
			lTime = m_pMdb.getTime(iCurRow);
			/* build the DAM row with the values read from memory database */
			hrow = java_build_curvalue_row(dam_hstmt, sName, iIntVal, dDoubleVal, lTime);
			if (hrow == 0) return IP_FAILURE; /* return on error */
			/* process the row */
			iRetCode = java_process_row(dam_hstmt, iStmtType,hrow, iCurRow);
			if (iRetCode != IP_SUCCESS) return iRetCode; /* return on error */

			piNumResRows.setVal(m_iNumResRows);
			if(iStmtType == DAM_SELECT  || iStmtType == DAM_FETCH)
			{
				if(m_iNumResRows == iFetchSize)
					return DAM_SUCCESS_WITH_RESULT_PENDING;
			}
			/* read next row from the memory database */
			bFound = m_pMdb.NextRow(iCurrentRow);
		}

		return IP_SUCCESS;
	}

	public int java_exec_bulk_table(long dam_hstmt, int iStmtType, xo_long piNumResRows)
	{
		long    hrow; /* DAM_HROW */
		int     iRetCode;

		/*byte val*/
		boolean   bitVal;

		/*String types */
		String   charVal, varcharVal, wcharVal, wvarcharVal;

		/*integer values */
		long     bigintVal;
		int      intVal;
		short    smallVal;
		byte     tinyintVal;

		/* float values */
		float   realVal;
		double  floatVal, doubleVal;

		String numericVal, decimalVal;

		/* Date and Time Vals */
		long    	date;
		long    	time;
		long   timestamp;

		/* binary vals */
		byte[]     binaryVal;
		byte[]     varbinaryVal;

		boolean 	bFound = false;

		m_iNumResRows = 0;
		piNumResRows.setVal(0);
		if(iStmtType == DAM_SELECT  || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE)
		{
			/* read row from the memory database */
			iCurrentRow.setVal(0);
			bFound = m_bulkDB.FirstRow(iCurrentRow);
		}
		else
		{
			bFound = m_bulkDB.NextRow(iCurrentRow);
		}
		while (bFound)
		{   
			/* Get the Row */
			int iCurRow = iCurrentRow.getVal();

			bitVal = m_bulkDB.getBitVal(iCurRow);

			charVal = m_bulkDB.getCharVal(iCurRow);
			varcharVal = m_bulkDB.getVarcharVal(iCurRow);
			wcharVal = m_bulkDB.getWcharVal(iCurRow);
			wvarcharVal = m_bulkDB.getWvarcharVal(iCurRow);

			bigintVal = m_bulkDB.getBigIntVal(iCurRow); 
			intVal = m_bulkDB.getIntVal(iCurRow);
			smallVal = m_bulkDB.getShortVal(iCurRow);
			tinyintVal = m_bulkDB.getTinyIntVal(iCurRow);

			floatVal = m_bulkDB.getFloatVal(iCurRow);
			realVal = m_bulkDB.getRealVal(iCurRow);
			doubleVal = m_bulkDB.getDoubleVal(iCurRow);

			numericVal = m_bulkDB.getNumericVal(iCurRow);
			decimalVal = m_bulkDB.getDecimalVal(iCurRow);            

			date = m_bulkDB.getDateVal(iCurRow);
			time = m_bulkDB.getTimeVal(iCurRow);
			timestamp = m_bulkDB.getTimestampVal(iCurRow);

			binaryVal = m_bulkDB.getBinaryVal(iCurRow);
			varbinaryVal = m_bulkDB.getVarbinaryVal(iCurRow);           

			/* build the DAM row with the values read from memory database */
			hrow = java_build_bulk_table_row(dam_hstmt, bitVal, charVal, varcharVal, wcharVal, wvarcharVal,
					bigintVal, intVal, smallVal, tinyintVal, floatVal, realVal, doubleVal, 
					numericVal, decimalVal, date, time, timestamp, binaryVal, varbinaryVal);
			if (hrow == 0) return IP_FAILURE; /* return on error */

			/* process the row */
			iRetCode = java_process_row(dam_hstmt, iStmtType,hrow, iCurRow);
			if (iRetCode != IP_SUCCESS) return iRetCode; /* return on error */

			piNumResRows.setVal(m_iNumResRows);
			if(iStmtType == DAM_SELECT  || iStmtType == DAM_FETCH)
			{
				if(m_iNumResRows == iFetchSize)
					return DAM_SUCCESS_WITH_RESULT_PENDING;
			}

			/* read next row from the memory database */
			bFound = m_bulkDB.NextRow(iCurrentRow);
		}

		m_bulkDB.updateTotalRows();
		return IP_SUCCESS;
	}

	public int java_exec_table_function_sample_f(long dam_hstmt)
	{
		long hrow;
		long hcol;
		int iValid;
		int iRetCode = 0;
		xo_int iArgCount, piRetCode;
		xo_long hValExpList;
		long hValExp1;
		int i;
		double iVal;

		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_exec_table_function_sample_f()called");

		long pMemTree = jdam.dam_getMemTree(dam_hstmt);
		hrow = jdam.dam_allocRow(dam_hstmt);
		hcol = jdam.dam_getFirstCol(dam_hstmt, DAM_COL_IN_SCHEMA);

		hValExpList = new xo_long();
		iArgCount = new xo_int();
		piRetCode = new xo_int();

		jdam.dam_describeTableFunction(dam_hstmt, null, null, null, null, null, hValExpList, iArgCount);

		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_exec_table_function_sample_f(): hValExpList.getVal():" + hValExpList.getVal() + "\n");
		if (hValExpList.getVal() != 0)
		{
			hValExp1 = jdam.dam_getFirstValExp(hValExpList.getVal());
			String pArg1 = (String)jdam.dam_getValueOfExp(pMemTree, hValExpList.getVal(), hValExp1, XO_TYPE_CHAR, piRetCode);
			hrow = jdam.dam_allocRow(dam_hstmt);
			jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_exec_table_function_sample_f(): pArg1:" + pArg1 + "\n");
			if (pArg1.equalsIgnoreCase("one"))
			{
				iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, hcol, pArg1, XO_NTS);
				if (iRetCode != DAM_SUCCESS) return iRetCode;
			}
			else if (pArg1.equalsIgnoreCase("two"))
			{
				while (hcol != 0)
				{
					String sValBuf = "OpenAccess-" + pArg1;
					jdam.dam_addCharValToRow(dam_hstmt, hrow, hcol, sValBuf, XO_NTS);
					hcol = jdam.dam_getNextCol(dam_hstmt);
				}
			}
			else if (pArg1.equalsIgnoreCase("three"))
			{
				i = 0;
				while (hcol != 0)
				{
					if (i == 0)
					{
						String sValBuf = "OpenAccess-" + pArg1;
						iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, hcol, sValBuf, XO_NTS);
					}
					if (i == 1)
					{
						iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hrow, hcol, i, 0);
						if (iRetCode != DAM_SUCCESS) return iRetCode;
					}
					if (i == 2)
					{
						iVal = i + 10.998;
						iRetCode = jdam.dam_addDoubleValToRow(dam_hstmt, hrow, hcol, iVal, 0);
						if (iRetCode != DAM_SUCCESS) return iRetCode;
					}
					hcol = jdam.dam_getNextCol(dam_hstmt);
					i++;
				}
			}
		}

		iValid = jdam.dam_isTargetRow(dam_hstmt, hrow);
		if (iValid == DAM_ERROR) /* error */
			return DAM_FAILURE;
		else if (iValid == DAM_TRUE)
		{ /* target row */
			iRetCode = jdam.dam_addRowToTable(dam_hstmt, hrow);
			if (iRetCode != DAM_SUCCESS) return iRetCode; /* error */
		}
		else /* not a target row */
			jdam.dam_freeRow(hrow);

		return DAM_SUCCESS;
	}

	public int java_exec_table_function_types_f(long dam_hstmt)
	{
		long hrow;
		long hcol;
		int iValid;
		int iRetCode, iXoType;
		xo_int iArgCount, piRetCode;
		xo_long hValExpList;
		long hValExp;
		int iTempRowCount = 0;

		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_exec_table_function_types_f()called");

		long pMemTree = jdam.dam_getMemTree(dam_hstmt);
		hrow = jdam.dam_allocRow(dam_hstmt);
		hcol = jdam.dam_getFirstCol(dam_hstmt, DAM_COL_IN_USE);

		hValExpList = new xo_long();
		iArgCount = new xo_int();
		piRetCode = new xo_int();

		jdam.dam_describeTableFunction(dam_hstmt, null, null, null, null, null, hValExpList, iArgCount);

		if (iArgCount.getVal() % NO_OF_COLS_FOR_TYPES_F != 0 || iArgCount.getVal() > MAX_ARGS_FOR_TYPES_F)
		{
			jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, "Invalid Number of Parameters to the Table Function:TYPES_F. Number of Parameters should be a Multiple of" + NO_OF_COLS_FOR_TYPES_F + " and Maximum Number of Parameters is" + MAX_ARGS_FOR_TYPES_F);
			return DAM_FAILURE;
		}

		hValExp = jdam.dam_getFirstValExp(hValExpList.getVal());
		while (iTempRowCount <= (iArgCount.getVal() / NO_OF_COLS_FOR_TYPES_F))
		{
			iTempRowCount++;
			if (hValExp != 0)
			{
				Integer iArg;

				hrow = jdam.dam_allocRow(dam_hstmt);
				hcol = jdam.dam_getFirstCol(dam_hstmt, DAM_COL_IN_SCHEMA);

				/*column 1*/
				iXoType = jdam.dam_getValueTypeOfExp(pMemTree, hValExpList.getVal(), hValExp);
				if (iXoType != XO_TYPE_INTEGER)
				{
					String dam_msgBuf = "Invalid Parameters passed to the Table Function:TYPES_F. Expecting INTEGER Argument at arg#" + (iTempRowCount * 4 - 3) + "\n";
					jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, dam_msgBuf);
					jdam.trace(m_tmHandle, UL_TM_ERRORS, dam_msgBuf);
					return DAM_FAILURE;
				}
				iArg = (Integer)jdam.dam_getValueOfExp(pMemTree, hValExpList.getVal(), hValExp, XO_TYPE_INTEGER, piRetCode);
				if (piRetCode.getVal() != DAM_SUCCESS) return piRetCode.getVal();
				iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hrow, hcol, iArg.intValue(), 0);
				if (iRetCode != DAM_SUCCESS) return iRetCode;

				hValExp = jdam.dam_getNextValExp(hValExpList.getVal());
				hcol = jdam.dam_getNextCol(dam_hstmt);

				/*column 2*/
				iXoType = jdam.dam_getValueTypeOfExp(pMemTree, hValExpList.getVal(), hValExp);
				if (iXoType != XO_TYPE_DOUBLE)
				{
					String dam_msgBuf = "Invalid Parameters passed to the Table Function:TYPES_F. Expecting FLOAT Argument at arg#" + (iTempRowCount * 4 - 2) + "\n";
					jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, dam_msgBuf);
					jdam.trace(m_tmHandle, UL_TM_ERRORS, dam_msgBuf);
					return DAM_FAILURE;
				}
				Double dObj;

				dObj = (Double)jdam.dam_getValueOfExp(pMemTree, hValExpList.getVal(), hValExp, XO_TYPE_DOUBLE, piRetCode);
				if (piRetCode.getVal() != DAM_SUCCESS) return iRetCode;
				iRetCode = jdam.dam_addDoubleValToRow(dam_hstmt, hrow, hcol, dObj.doubleValue(), 0);
				if (iRetCode != DAM_SUCCESS) return iRetCode;

				hValExp = jdam.dam_getNextValExp(hValExpList.getVal());
				hcol = jdam.dam_getNextCol(dam_hstmt);

				/*column 3*/
				iXoType = jdam.dam_getValueTypeOfExp(pMemTree, hValExpList.getVal(), hValExp);
				if (iXoType != XO_TYPE_VARCHAR)
				{
					String dam_msgBuf = "Invalid Parameters passed to the Table Function:TYPES_F. Expecting DATE Argument at arg#%d." + (iTempRowCount * 4 - 1) + "\n";
					jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, dam_msgBuf);
					jdam.trace(m_tmHandle, UL_TM_ERRORS, dam_msgBuf);
					return DAM_FAILURE;
				}
				xo_tm dtArg = new xo_tm();

				dtArg = (xo_tm)jdam.dam_getValueOfExp(pMemTree, hValExpList.getVal(), hValExp, XO_TYPE_DATE, piRetCode);
				if (piRetCode.getVal() != DAM_SUCCESS) return iRetCode;
				iRetCode = jdam.dam_addTimeStampValToRow(dam_hstmt, hrow, hcol, dtArg, 0);
				if (iRetCode != DAM_SUCCESS) return iRetCode;

				hValExp = jdam.dam_getNextValExp(hValExpList.getVal());
				hcol = jdam.dam_getNextCol(dam_hstmt);

				/*column 4*/
				iXoType = jdam.dam_getValueTypeOfExp(pMemTree, hValExpList.getVal(), hValExp);
				if (iXoType != XO_TYPE_VARCHAR)
				{
					String dam_msgBuf = "Invalid Parameters passed to the Table Function:TYPES_F. Expecting TIMESTAMP Argument at arg#" + iTempRowCount * 4 + "\n";
					jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, dam_msgBuf);
					jdam.trace(m_tmHandle, UL_TM_ERRORS, dam_msgBuf);
					return DAM_FAILURE;
				}
				xo_tm tsArg = new xo_tm();

				tsArg = (xo_tm)jdam.dam_getValueOfExp(pMemTree, hValExpList.getVal(), hValExp, XO_TYPE_TIMESTAMP, piRetCode);
				if (piRetCode.getVal() != DAM_SUCCESS) return iRetCode;
				iRetCode = jdam.dam_addTimeStampValToRow(dam_hstmt, hrow, hcol, tsArg, 0);
				if (iRetCode != DAM_SUCCESS) return iRetCode;

				iValid = jdam.dam_isTargetRow(dam_hstmt, hrow);
				if (iValid == DAM_ERROR) /* error */
				return DAM_FAILURE;
				else if (iValid == DAM_TRUE)
				{ /* target row */
					iRetCode = jdam.dam_addRowToTable(dam_hstmt, hrow);
					if (iRetCode != DAM_SUCCESS) return iRetCode; /* error */
				}
				else /* not a target row */
					jdam.dam_freeRow(hrow);
				
				hValExp = jdam.dam_getNextValExp(hValExpList.getVal());
			}			
		}
		return DAM_SUCCESS;
	}

	public int java_exec_table_function_curvalue(long dam_hstmt)
	{
		int iValid = 0, iRetCode = 0;
		int iTotalRows = 1;
		xo_long hValExpList = new xo_long();
		xo_int iArgCount = new xo_int();
		long hrow;

		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_exec_table_function_curvalue() has been called\n");

		jdam.dam_describeTableFunction(dam_hstmt, null, null, null, null, null, hValExpList, iArgCount);
		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "Total Number of arguments passed to the table funtion is:" + iArgCount + "\n");

		/* Sample showing the usage of API's that can be used for optimizing.*/
		iRetCode = jdam.dam_getOptimalIndexAndConditions(dam_hstmt, hindex, hset_of_condlist);
		if (iRetCode != DAM_SUCCESS) return iRetCode; /* return on error */

		if (hindex.getVal() != 0)
		{
			jdam.dam_describeIndex(hindex.getVal(), null, sIndexName, null, null, null);
			jdam.trace(m_tmHandle, UL_TM_MINOR_EV, "java_exec_table_function_curvalue(): Query can be optimized using index:" + sIndexName + "\n");
			iRetCode = java_optimize_exec_curvalue_table_function(dam_hstmt);
			jdam.dam_freeSetOfConditionList(hset_of_condlist.getVal()); /* free the set of condition list */
			if (iRetCode != DAM_SUCCESS)
			{  /* check for errors */
				return iRetCode;
			}
		}
		else
		{
			while (iTotalRows <= MAX_ROWS_FOR_CURVALUE_F)
			{
				hrow = 0;
				hrow = java_build_table_function_curvalue_row(dam_hstmt, iTotalRows);
				iValid = jdam.dam_isTargetRow(dam_hstmt, hrow);
				if (iValid == DAM_ERROR) /* error */
				return DAM_FAILURE;
				else if (iValid == DAM_TRUE)
				{ /* target row */
					iRetCode = jdam.dam_addRowToTable(dam_hstmt, hrow);
					if (iRetCode != DAM_SUCCESS) return iRetCode; /* error */
				}
				else /* not a target row */
					jdam.dam_freeRow(hrow);
				iTotalRows++;
			}
		}

		return DAM_SUCCESS;
	}

	public int java_optimize_exec_curvalue_table_function(long dam_hstmt)
	{
		long hcur_condlist;
		long hcond;
		long hrow;
		int iRetCode;

		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_optimize_exec_curvalue_table_function() has been called\n");

		xo_int iLeftOp = new xo_int(0);
		xo_int iLeftXoType = new xo_int(0);
		xo_int iLeftValLen = new xo_int(0);
		xo_int iStatus = new xo_int(0);

		/* get the conditions on index columns */
		hcur_condlist = jdam.dam_getFirstCondList(hset_of_condlist.getVal());
		while (hcur_condlist != 0)
		{
			String sLeftData;

			hrow = 0;

			/* Each condition list will have only one
			condition, since its a single column index */
			hcond = jdam.dam_getFirstCond(dam_hstmt, hcur_condlist);

			/* get details of the condition */
			sLeftData = (String)jdam.dam_describeCondEx(dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
			iRetCode = iStatus.getVal();
			if (iRetCode != IP_SUCCESS) return iRetCode;

			/* build result rows that match current index condition */
			hrow = java_build_table_function_curvalue_index_row(dam_hstmt, sLeftData);

			if (hrow != 0)
			{
				iRetCode = jdam.dam_isTargetRow(dam_hstmt, hrow);
				if (iRetCode == DAM_ERROR || iRetCode == DAM_FALSE)
				{
					jdam.dam_freeRow(hrow);
					if (iRetCode == DAM_ERROR) return DAM_FALSE;
				}
				else if (iRetCode == DAM_TRUE)
				{ /* target row */
					iRetCode = jdam.dam_addRowToTable(dam_hstmt, hrow);
				if (iRetCode != DAM_SUCCESS) return iRetCode;
				}
			}

			hcur_condlist = jdam.dam_getNextCondList(hset_of_condlist.getVal());
		}

		return DAM_SUCCESS;
	}

	public long java_build_table_function_curvalue_row(long dam_hstmt, int iRowCount)
	{
		long hcol;
		long hrow;
		int iColNo = 0;

		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_build_table_function_curvalue_row() has been called\n");

		hcol = jdam.dam_getColByNum(dam_hstmt, iColNo);
		hrow = jdam.dam_allocRow(dam_hstmt);
		while (hcol != 0)
		{
			String sValBuf = "OpenAccess-" + iRowCount + (iColNo + 1);
			jdam.dam_addCharValToRow(dam_hstmt, hrow, hcol, sValBuf, XO_NTS);
			iColNo++;
			hcol = jdam.dam_getColByNum(dam_hstmt, iColNo);
		}
		return hrow;
	}

	public long java_build_table_function_curvalue_index_row(long dam_hstmt, String sLeftData)
	{
		long hcol;
		long hrow;
		int iColNum = 0, iRowNum;

		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_build_table_function_curvalue_index_row() has been called\n");

		/* Get the row number */
		String pRowNum = sLeftData.substring("OpenAccess-".length(), "OpenAccess-".length() + 1);
		iRowNum = Integer.parseInt(pRowNum);

		/* add values to the columns */
		if (iRowNum > 0 && iRowNum <= MAX_ROWS_FOR_CURVALUE_F)
		{
			hcol = jdam.dam_getColByNum(dam_hstmt, 0);
			hrow = jdam.dam_allocRow(dam_hstmt);

			while (hcol != 0)
			{
				String sValBuf = "OpenAccess-" + iRowNum + (iColNum + 1);
				jdam.dam_addCharValToRow(dam_hstmt, hrow, hcol, sValBuf, XO_NTS);
				iColNum++;
				hcol = jdam.dam_getColByNum(dam_hstmt, iColNum);
			}
			return hrow;
		}

		return 0;
	}

	/* given the raw values, build a DAM ROW structure */
	public long java_build_curvalue_row(long dam_hstmt, String sName, int iIntVal, double dDoubleVal, long lTime)
	{
		long    hrow = 0;
		int     iRetCode;
		xo_tm   xoTime;
		/* allocate a new row */
		hrow = jdam.dam_allocRow(dam_hstmt);
		/* change the column values to data types accepted by DAM*/
		xoTime = java_cvrt_time_to_xotm(lTime);
		if(xoTime == null)
			return 0;
		/* set values for columns of the row */
		iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, hcolName,sName,XO_NTS);
		if (iRetCode != DAM_SUCCESS) return 0;
		iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hrow, hcolIntVal, iIntVal,0);
		if (iRetCode != DAM_SUCCESS) return 0;
		iRetCode = jdam.dam_addDoubleValToRow(dam_hstmt, hrow, hcolDoubleVal,dDoubleVal,0);
		if (iRetCode != DAM_SUCCESS) return 0;
		iRetCode = jdam.dam_addTimeStampValToRow(dam_hstmt, hrow, hcolTime,xoTime,0);
		if (iRetCode != DAM_SUCCESS) return 0;
		return hrow;
	}

	public long java_build_bulk_table_row(long dam_hstmt, boolean   bitVal, String charVal,String  varcharVal,String  wcharVal,String  wvarcharVal,long     bigintVal,
			int      intVal, short    smallVal, byte     tinyintVal, double   floatVal, float realVal, double  doubleVal,
			String numericVal, String decimalVal,
			long   date, long   time, long   timestamp, byte[]     binaryVal, byte[]     varbinaryVal)
	{
		long    hrow = 0;
		int     iRetCode;
		xo_tm   xoTime;
		xo_tm   xoDate;
		xo_tm   xoTimeStamp;

		/* allocate a new row */
		hrow = jdam.dam_allocRow(dam_hstmt);

		/* change the column values to data types accepted by DAM*/
		xoTime = java_cvrt_time_to_xotm(time);
		if(xoTime == null) return 0;

		xoDate = java_cvrt_time_to_xotm(date);
		if(xoDate == null) return 0;

		xoTimeStamp = java_cvrt_time_to_xotm(timestamp);
		if(xoTimeStamp == null) return 0;

		/* change the column values to data types accepted by DAM*/
		/*xoTime = java_cvrt_time_to_xotm(lTime);*/
		iRetCode = jdam.dam_addBitValToRow(dam_hstmt, hrow, this.hcolBitVal, bitVal, 0);
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, hcolCharVal, (charVal != null ? charVal.toString(): null) , (charVal != null ? XO_NTS : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, this.hcolVarcharVal, (varcharVal != null ? varcharVal.toString(): null) , (varcharVal != null ? XO_NTS : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, this.hcolWcharVal, (wcharVal != null ? wcharVal.toString(): null) , (wcharVal != null ? XO_NTS : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, this.hcolWvarcharVal, (wvarcharVal != null ? wvarcharVal.toString(): null) , (wvarcharVal != null ? XO_NTS : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hrow, this.hcolIntVal, intVal, 0);
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hrow, this.hcolBigIntVal, bigintVal, 0);
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hrow, this.hcolTinyIntVal, tinyintVal, 0);
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hrow, this.hcolSmallIntVal, smallVal, 0);
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addFloatValToRow(dam_hstmt, hrow, this.hcolRealVal , realVal, 0);
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addFloatValToRow(dam_hstmt, hrow, this.hcolFloatVal, (float)floatVal, 0);
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addDoubleValToRow(dam_hstmt, hrow, this.hcolDoubleVal, doubleVal, 0);
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, this.hcolNumericVal, (numericVal != null ? numericVal : null) , (numericVal != null ? XO_NTS : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, this.hcolDecimalVal, (decimalVal != null ? decimalVal : null) , (decimalVal != null ? XO_NTS : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addTimeStampValToRow(dam_hstmt, hrow, this.hcolTimeVal,xoTime,(xoTime != null ? 0 : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addTimeStampValToRow(dam_hstmt, hrow, this.hcolDateVal, xoDate,(xoDate != null ? 0 : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addTimeStampValToRow(dam_hstmt, hrow, this.hcolTimeStampVal, xoTimeStamp,(xoTimeStamp != null ? 0 : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addBinaryValToRow(dam_hstmt, hrow, this.hcolBinaryVal, binaryVal, (binaryVal != null ? binaryVal.length : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;

		iRetCode = jdam.dam_addBinaryValToRow(dam_hstmt, hrow, this.hcolVarbinaryVal, varbinaryVal, (varbinaryVal != null ? varbinaryVal.length : XO_NULL_DATA));
		if (iRetCode != DAM_SUCCESS) return 0;       

		return hrow;
	}

	int java_process_row(long dam_hstmt, int iStmtType,long hrow, int rowIndex)
	{
		switch (iStmtType)
		{
		case DAM_SELECT:
		case DAM_FETCH:
			if (jdam.dam_isTargetRow(dam_hstmt, hrow) == DAM_TRUE){
				jdam.dam_addRowToTable(dam_hstmt, hrow);
				m_iNumResRows++;
			}
			else
				jdam.dam_freeRow(hrow);
			break;
		case DAM_UPDATE:
			java_update_row(dam_hstmt, hrow, rowIndex);
			break;

		case DAM_DELETE:
			if (jdam.dam_isTargetRow(dam_hstmt, hrow) == DAM_TRUE) {
				jdam.trace(m_tmHandle, UL_TM_INFO,"Delete Row:" + rowIndex + "\n");
				if(sTableName.toString().equalsIgnoreCase("CURVALUE"))
				{
					m_pMdb.DeleteRow(rowIndex);                			
					m_iNumResRows++;
				}
				else{
					if(sTableName.toString().equalsIgnoreCase("BULK_TABLE"))
					{
						m_bulkDB.DeleteRow(rowIndex);
						iCurrentRow.setVal(rowIndex-1);
						m_iNumResRows++;
					}                        	
				}
			}
			jdam.dam_freeRow(hrow);
			break;
		default:
			break;
		}
		return IP_SUCCESS;
	}

	int java_update_row(long dam_hstmt, long hrow, int rowIndex)
	{
		long     hInputRow;
		long     hRowElem;
		long            hcol;
		StringBuffer    sColName;
		xo_int          iValueStatus;
		String          sName;
		Integer         intVal;
		Double          doubleVal;
		xo_tm           timeVal;


		hInputRow = jdam.dam_getUpdateRow(dam_hstmt, hrow);

		m_pMdb.SetUpdateRow(rowIndex);

		hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hInputRow);
		sColName = new StringBuffer();
		iValueStatus = new xo_int(1);

		while (hRowElem != 0) {

			sColName.delete(0, sColName.length());
			hcol = jdam.dam_getColToSet(hRowElem);
			jdam.dam_describeCol(hcol, null, sColName, null, null);
			if (sColName.toString().compareToIgnoreCase("NAME") == 0) {
				sName = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
				m_pMdb.setName(sName,rowIndex);
			}
			else if (sColName.toString().compareToIgnoreCase("INTVAL") == 0) {
				intVal = (Integer) jdam.dam_getValueToSet(hRowElem, XO_TYPE_INTEGER, iValueStatus);
				if (intVal != null)
					m_pMdb.setIntVal(intVal.intValue(),rowIndex);
				else
					m_pMdb.setIntVal(0,rowIndex);
			}
			else if (sColName.toString().compareToIgnoreCase("FLOATVAL") == 0) {
				doubleVal = (Double) jdam.dam_getValueToSet(hRowElem, XO_TYPE_DOUBLE, iValueStatus);
				if (doubleVal != null)
					m_pMdb.setDVal(doubleVal.doubleValue(),rowIndex);
				else
					m_pMdb.setDVal(0,rowIndex);
			}
			else if (sColName.toString().compareToIgnoreCase("TIME") == 0) {
				timeVal = (xo_tm) jdam.dam_getValueToSet(hRowElem, XO_TYPE_TIMESTAMP, iValueStatus);
				if (timeVal != null)
					m_pMdb.setTime(java_cvrt_time_to_xotm(timeVal),rowIndex);
				else
					m_pMdb.setTime(0,rowIndex);
			}
			hRowElem = jdam.dam_getNextValueSet(dam_hstmt);
		}

		m_pMdb.UpdateRow(rowIndex);
		m_iNumResRows++;

		return IP_SUCCESS;
	}

	int java_insert_bulk_table_rows(long dam_hstmt)
	{
		long         	hrow;
		long            hRowElem;
		long            hcol;
		StringBuffer    sColName = new StringBuffer();
		xo_long         iRowCount =  new xo_long(0); 
		xo_int         iXoType =  new xo_int(0); 
		Object[]        objects;
		int             iRow = 0, index = 0;

		/* for each row to be inserted, get the bulk values */
		hrow = jdam.dam_getFirstInsertRow(dam_hstmt);
		if (hrow == 0) {
			jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 1, "jdam.dam_getFirstInsertRow() returned null");
			return IP_SUCCESS;
		}

		hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow);
		if(hRowElem == 0) return IP_SUCCESS;

		BulkData bulkData = new BulkData();

		while(hRowElem != 0)
		{
			hcol = jdam.dam_getColToSet(hRowElem);
			jdam.dam_describeCol(hcol, null, sColName, iXoType, null);
			objects = jdam.dam_getBulkValueToSet(dam_hstmt, hRowElem, iRowCount);
			bulkData.setMembers(objects, iXoType.getVal(), iRowCount.getVal());
			jdam.dam_freeBulkValue(objects);
			hRowElem = jdam.dam_getNextValueSet(dam_hstmt);        	
		}		

		byte[] rowStatusArray = jdam.dam_getBulkRowStatusArray(dam_hstmt);
		if(rowStatusArray == null) return IP_FAILURE;

		do
		{
			try{

				index = m_bulkDB.InsertRow(); 
				if(index == -1) break;

				boolean[] bitVals = bulkData.getBitVal();			
				m_bulkDB.setBitVal(bitVals[iRow], index);

				String[] charVals = bulkData.getCharVal();
				m_bulkDB.setCharVal(charVals[iRow], index);

				String[] varcharVals = bulkData.getVarcharVal();
				m_bulkDB.setVarcharVal(varcharVals[iRow], index);

				String[] wcharVals = bulkData.getWcharVal();
				m_bulkDB.setWcharVal(wcharVals[iRow], index);

				String[] wvarcharVals = bulkData.getWvarcharVal();
				m_bulkDB.setWvarcharVal(wvarcharVals[iRow],index);

				Integer[] intVals = bulkData.getIntVal(); 
				m_bulkDB.setIntVal(intVals[iRow], index);

				Long[] longVals = bulkData.getBigIntVal();
				m_bulkDB.setBigIntVal(longVals[iRow], index);

				Short[]  shortVals = bulkData.getShortVal();
				m_bulkDB.setShortVal(shortVals[iRow], index);

				Byte[] tinyintVals = bulkData.getTinyIntVal();
				m_bulkDB.setTinyIntVal(tinyintVals[iRow], index);

				Float[] realVals = bulkData.getRealVal();
				m_bulkDB.setRealVal(realVals[iRow], index);

				Double[] floatVals = bulkData.getFloatVal();
				m_bulkDB.setFloatVal(floatVals[iRow], index);

				Double[] doubleVals = bulkData.getDoubleVal();
				m_bulkDB.setDoubleVal(doubleVals[iRow], index);

				String[] numericVals = bulkData.getNumericVal();
				m_bulkDB.setNumericVal(numericVals[iRow], index);

				String[] decimalVals = bulkData.getDecimalVal();
				m_bulkDB.setDecimalVal(decimalVals[iRow], index);

				byte[][] binaryVals = bulkData.getBinaryVal();
				m_bulkDB.setBinaryVal(binaryVals[iRow], index);

				byte[][] varbinaryVals = bulkData.getVarbinaryVal();
				m_bulkDB.setVarbinaryVal(varbinaryVals[iRow], index);

				long[] timeVals = bulkData.getTimeVal();
				m_bulkDB.setTimeVal(timeVals[iRow], index);

				long[] dateVals = bulkData.getDateVal();
				m_bulkDB.setDateVal(dateVals[iRow], index);

				long[] timestampVals = bulkData.getTimestampVal();
				m_bulkDB.setTimestampVal(timestampVals[iRow], index);	

				rowStatusArray[iRow] = DAM_ROW_SUCCESS;

				iRow++;
				m_iNumResRows++;
			}
			catch(Exception e)			
			{
				jdam.trace(m_tmHandle, UL_TM_INFO,"Error Occurred while inserting row:" + iRow + "\n");
				rowStatusArray[iRow] = DAM_ROW_ERROR;
				iRow++;
				m_iNumResRows++;
				continue;
			}

		}while(iRow < iRowCount.getVal());

		m_bulkDB.updateTotalRows();
		return IP_SUCCESS;
	}

	int java_insert_row(long dam_hstmt, long hrow)
	{
		long            hRowElem;
		long            hcol;
		StringBuffer    sColName;
		xo_int          iValueStatus;
		String          sName;
		Integer         intVal;
		Double          doubleVal;
		xo_tm           timeVal;
		xo_int	        piIndex = new xo_int(0);


		if (!m_pMdb.InsertRow(piIndex)) {
			jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 1, "New records cannot be inserted");
			return IP_FAILURE;
		}

		int iCurRow = piIndex.getVal();

		hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow);
		sColName = new StringBuffer();
		iValueStatus = new xo_int(1);

		while (hRowElem != 0) {

			sColName.delete(0, sColName.length());
			hcol = jdam.dam_getColToSet(hRowElem);
			jdam.dam_describeCol(hcol, null, sColName, null, null);
			if (sColName.toString().compareToIgnoreCase("NAME") == 0) {
				sName = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
				m_pMdb.setName(sName,iCurRow);
			}
			else if (sColName.toString().compareToIgnoreCase("INTVAL") == 0) {
				intVal = (Integer) jdam.dam_getValueToSet(hRowElem, XO_TYPE_INTEGER, iValueStatus);
				if (intVal != null)
					m_pMdb.setIntVal(intVal.intValue(),iCurRow);
				else
					m_pMdb.setIntVal(0,iCurRow);
			}
			else if (sColName.toString().compareToIgnoreCase("FLOATVAL") == 0) {
				doubleVal = (Double) jdam.dam_getValueToSet(hRowElem, XO_TYPE_DOUBLE, iValueStatus);
				if (doubleVal != null)
					m_pMdb.setDVal(doubleVal.doubleValue(),iCurRow);
				else
					m_pMdb.setDVal(0,iCurRow);
			}
			else if (sColName.toString().compareToIgnoreCase("TIME") == 0) {
				timeVal = (xo_tm) jdam.dam_getValueToSet(hRowElem, XO_TYPE_TIMESTAMP, iValueStatus);
				if (timeVal != null)
					m_pMdb.setTime(java_cvrt_time_to_xotm(timeVal),iCurRow);
				else
					m_pMdb.setTime(0,iCurRow);
			}
			hRowElem = jdam.dam_getNextValueSet(dam_hstmt);
		}

		m_iNumResRows++;

		return IP_SUCCESS;
	}

	xo_tm java_cvrt_time_to_xotm(long lTime)
	{
		Date       dt  =  new Date(lTime * 1000);
		Calendar   pTm = Calendar.getInstance();
		xo_tm  pXoTm = new xo_tm();

		pTm.setTime(dt);

		/* copy the local time to xo_tm variable */
		pXoTm.setVal(xo_tm.SECOND,pTm.get(Calendar.SECOND));
		pXoTm.setVal(xo_tm.MINUTE,pTm.get(Calendar.MINUTE));
		pXoTm.setVal(xo_tm.HOUR,pTm.get(Calendar.HOUR_OF_DAY));
		pXoTm.setVal(xo_tm.DAY_OF_MONTH,pTm.get(Calendar.DAY_OF_MONTH));
		pXoTm.setVal(xo_tm.MONTH,pTm.get(Calendar.MONTH));
		pXoTm.setVal(xo_tm.YEAR,pTm.get(Calendar.YEAR));
		pXoTm.setVal(xo_tm.DAY_OF_WEEK,pTm.get(Calendar.DAY_OF_WEEK));
		pXoTm.setVal(xo_tm.DAY_OF_YEAR,pTm.get(Calendar.DAY_OF_YEAR));

		if(pTm.isSet(Calendar.DST_OFFSET))
			pXoTm.setVal(xo_tm.IS_DST,1);
		else
			pXoTm.setVal(xo_tm.IS_DST,0);

		pXoTm.setVal(xo_tm.FRACTION,pTm.get(Calendar.MILLISECOND) * 1000000);
		return pXoTm;
	}

	public long java_cvrt_time_to_xotm(xo_tm pXoTm)
	{
		Date        dt;
		Calendar   pTm = Calendar.getInstance();

		pTm.set(Calendar.SECOND, pXoTm.getVal(xo_tm.SECOND));
		pTm.set(Calendar.MINUTE, pXoTm.getVal(xo_tm.MINUTE));
		pTm.set(Calendar.HOUR_OF_DAY, pXoTm.getVal(xo_tm.HOUR));
		pTm.set(Calendar.DAY_OF_MONTH, pXoTm.getVal(xo_tm.DAY_OF_MONTH));
		pTm.set(Calendar.MONTH, pXoTm.getVal(xo_tm.MONTH));
		pTm.set(Calendar.YEAR, pXoTm.getVal(xo_tm.YEAR));
		/*
        pTm.set(Calendar.DAY_OF_WEEK, pXoTm.getVal(xo_tm.DAY_OF_WEEK));
        pTm.set(Calendar.DAY_OF_YEAR, pXoTm.getVal(xo_tm.DAY_OF_YEAR));
		 */

		dt = pTm.getTime();
		return dt.getTime()/1000;
	}

	public scalar_function[] ipGetScalarFunctions()
	{
		scalar_function[] MyFuncs = new scalar_function[4];

		MyFuncs[0] = new scalar_function("INTVAL",1,"ip_func_intval",XO_TYPE_INTEGER,1);
		MyFuncs[1] = new scalar_function("DOUBLEVAL",1,"ip_func_doubleval",XO_TYPE_DOUBLE,1);
		MyFuncs[2] = new scalar_function("CHARVAL",1,"ip_func_charval",XO_TYPE_CHAR,200,200,0,1);
		MyFuncs[3] = new scalar_function("IPNAME",1,"ip_func_ipname",XO_TYPE_CHAR,25,25,0,-3);

		return MyFuncs;
	}

	public void java_init_stmt(long dam_hstmt, int iStmtType)
	{

		if (iStmtType == DAM_SELECT || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE || iStmtType == DAM_INSERT || iStmtType == DAM_INSERT_BULK)
		{
			/* get the table/table function information */
			isTableFunction = jdam.dam_isTableFunction(dam_hstmt);

			/* get the table/tablefunction information */
			if (isTableFunction == DAM_TRUE)
			{
				jdam.dam_describeTableFunction(dam_hstmt, null, null, sTableName, null, null, null, null);
			}
			else
			{
				jdam.dam_describeTable(dam_hstmt, null, null, sTableName, null, null);
			}

			if (isTableFunction == DAM_FALSE)
			{
				if(sTableName.toString().equalsIgnoreCase("CURVALUE"))
				{
					/* get the column handles */
					hcolName = jdam.dam_getCol(dam_hstmt, "NAME");
					hcolIntVal = jdam.dam_getCol(dam_hstmt, "INTVAL");
					hcolDoubleVal = jdam.dam_getCol(dam_hstmt, "FLOATVAL");
					hcolTime = jdam.dam_getCol(dam_hstmt, "TIME");

					hindex.setVal(0);
				}

				if(sTableName.toString().equalsIgnoreCase("BULK_TABLE"))
				{
					/* get the column handles */
					hcolBitVal = jdam.dam_getCol(dam_hstmt, "BIT_VAL");
					hcolIntVal = jdam.dam_getCol(dam_hstmt,"INT_VAL");
					hcolBigIntVal = jdam.dam_getCol(dam_hstmt, "BIGINT_VAL");
					hcolTinyIntVal = jdam.dam_getCol(dam_hstmt, "TINYINT_VAL");
					hcolSmallIntVal = jdam.dam_getCol(dam_hstmt, "SMALLINT_VAL");
					hcolRealVal = jdam.dam_getCol(dam_hstmt, "REAL_VAL");
					hcolFloatVal = jdam.dam_getCol(dam_hstmt, "FlOAT_VAL");
					hcolDoubleVal = jdam.dam_getCol(dam_hstmt, "DOUBLE_VAL");
					hcolCharVal = jdam.dam_getCol(dam_hstmt, "CHAR_VAL");
					hcolVarcharVal = jdam.dam_getCol(dam_hstmt, "VARCHAR_VAL");
					hcolWcharVal = jdam.dam_getCol(dam_hstmt, "wCHAR_VAL");
					hcolWvarcharVal = jdam.dam_getCol(dam_hstmt, "WVARCHAR_VAL");
					hcolNumericVal = jdam.dam_getCol(dam_hstmt, "NUMERIC_VAL");
					hcolDecimalVal = jdam.dam_getCol(dam_hstmt, "DECIMAL_VAL");
					hcolBinaryVal = jdam.dam_getCol(dam_hstmt, "BINARY_VAL");
					hcolVarbinaryVal = jdam.dam_getCol(dam_hstmt, "VARBINARY_VAL");
					hcolDateVal = jdam.dam_getCol(dam_hstmt, "DATE_VAL");
					hcolTimeVal = jdam.dam_getCol(dam_hstmt, "TIME_VAL");
					hcolTimeStampVal = jdam.dam_getCol(dam_hstmt, "TIMESTAMP_VAL");            		
				}
			}    		
		}

		/* initialize the result */
		m_iNumResRows = 0;
	}

	public int java_exec_stmt(long dam_hstmt, int iStmtType, xo_long piNumResRows)
	{
		int iRetCode = 0;

		/* process the query based on the type */
		if (iStmtType == DAM_SELECT || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE)
		{
			/* get the index information and process the query*/
			iRetCode = jdam.dam_getOptimalIndexAndConditions(dam_hstmt, hindex, hset_of_condlist);
			if (iRetCode != DAM_SUCCESS) return IP_FAILURE; /* return on error */

			/* use index information for optimized query processing - cases where NAME='Joe' type of
               conditions appear in the query*/
			if(hindex.getVal() != 0)
			{
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"Using optimized processing\n");
				jdam.dam_describeIndex(hindex.getVal(), null, sIndexName, null, null, null);
				iRetCode = java_optimize_exec(dam_hstmt, hindex, hset_of_condlist,iStmtType,piNumResRows);
				jdam.dam_freeSetOfConditionList(hset_of_condlist.getVal()); /* free the set of condition list */
				if (iRetCode != IP_SUCCESS)
				{   /* check for errors */
					return IP_FAILURE;
				}
			}
			else
			{
				/* non-optimized query processing -- cases where no WHERE clause or conditions on columns
                   otther than NAME. */
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"Doing a full table scan\n");
				iRetCode = java_exec(dam_hstmt,iStmtType,piNumResRows);

				/* check if there are any pending result rows*/
				if(iRetCode == DAM_SUCCESS_WITH_RESULT_PENDING){
					jdam.trace(m_tmHandle, UL_TM_INFO, "ipExecute(): Returning with result rows pending.\n");
					return iRetCode;
				}

				/* check for errors */
				if (iRetCode != IP_SUCCESS) return iRetCode;
			}
		}
		return iRetCode;
	}

	public long ip_func_intval(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
		long hValExp;
		xo_int piRetCode = new xo_int(0);
		Integer iObj;

		/* get the input */
		hValExp = jdam.dam_getFirstValExp(hValExpList);
		iObj = (Integer) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_INTEGER, piRetCode);
		if(piRetCode.getVal() != DAM_SUCCESS)
			return 0;
		if(iObj == null)
		{
			hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, null, XO_NULL_DATA);
			return hVal;
		}

		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, iObj, 0);

		return hVal;
	}

	public long ip_func_doubleval(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
		long hValExp;
		xo_int piRetCode = new xo_int(0);
		Double dObj;

		/* get the input */
		hValExp = jdam.dam_getFirstValExp(hValExpList);
		dObj = (Double) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_DOUBLE, piRetCode);
		if(piRetCode.getVal() != DAM_SUCCESS)
			return 0;
		if(dObj == null)
		{
			hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, null, XO_NULL_DATA);
			return hVal;
		}

		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, dObj, 0);

		return hVal;
	}

	public long ip_func_charval(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
		long hValExp;
		xo_int piRetCode = new xo_int(0);
		String strObj;

		/* get the input */
		hValExp = jdam.dam_getFirstValExp(hValExpList);
		strObj = (String) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_CHAR, piRetCode);
		if(piRetCode.getVal() != DAM_SUCCESS)
			return 0;
		if(strObj == null)
		{
			hVal = jdam.dam_createVal(pMemTree, XO_TYPE_CHAR, null, XO_NULL_DATA);
			return hVal;
		}

		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_CHAR, strObj, strObj.length());

		return hVal;
	}

	public long ip_func_ipname(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
		hVal=jdam.dam_createVal(pMemTree, XO_TYPE_CHAR,"Java Module, example3 IP", XO_NTS);
		return hVal;
	}
	
	/************************************************************************
     Function:       java_checkIfQueryCanRunInPassThrough

     Description:    Checks whether the query executing is "select count(*) from curvalue;" or not. 
     Return:         True if the query is "select count(*) from curvalue;" 
                     else False.
     ************************************************************************/
	public boolean java_checkIfQueryCanRunInPassThrough(long hstmt)
	{
	    long         hquery = 0, 
					 htable = 0,
					 htbl = 0,
					 hValExp = 0;						
		xo_int       piUnionType,
					 pbTopPercent,
					 piSetQuantifier,
					 piFuncType;
		xo_long      piTopResRows,
					 phSelectValExpList, 
					 phGroupValExpList, 
					 phOrderValExpList,
					 phUnionQuery,
					 phSearchExp,
					 phHavingExp;
					 
		StringBuffer sTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);					
		piFuncType          = new xo_int(0);			 
		piUnionType 		= new xo_int(0);
		piSetQuantifier     = new xo_int(0);		
		phUnionQuery 		= new xo_long(0);
        phSelectValExpList  = new xo_long(0);
        phGroupValExpList   = new xo_long(0);
        phOrderValExpList   = new xo_long(0);
        phSearchExp         = new xo_long(0);
        phHavingExp         = new xo_long(0);
        piTopResRows        = new xo_long(0);
        pbTopPercent        = new xo_int(0);
				
		/* get the query */
	    hquery = jdam.dam_getQuery(hstmt);
		if (hquery == 0) return false;
				
		/* get the table */
		htable = jdam.damex_getFirstTable(hquery);
	    if (htable == 0) return false;		
		
		/* Check the Table Name */
		jdam.damex_describeTable(htable, null, null, null, sTableName, null, null);
		if ( ! sTableName.toString().equalsIgnoreCase("CURVALUE") ) return false; 
		
		/* return if it is more than 1 table */
		htbl = jdam.damex_getNextTable(hquery);	
		if (htbl != 0)  return false;
		
		/* return if it is a table function */
		isTableFunction = jdam.dam_isTableFunction(hstmt);
		if (isTableFunction == DAM_TRUE) return false; 
				
		/* return if it is a union query */
		jdam.damex_describeUnionQuery(hquery, piUnionType, phUnionQuery);
		if (phUnionQuery.getVal() != 0) return false;	
			
		/* Get the top clause value */
		jdam.damex_describeSelectTopClause(hquery, piTopResRows, pbTopPercent);
		if (piTopResRows.getVal() != DAM_NOT_SET) return false;	
		
		
		/* Get the select expression,search exp, group by, having and the order by */
		jdam.damex_describeSelectQuery(hquery, 
									   piSetQuantifier,
									   phSelectValExpList,
									   phSearchExp,
									   phGroupValExpList,
									   phHavingExp,
									   phOrderValExpList); 	
								  
		if (piSetQuantifier.getVal() == SQL_SELECT_DISTINCT) return false;
		
		if ( (phSearchExp.getVal() != 0)  || ( phHavingExp.getVal() != 0)  ||  (phGroupValExpList.getVal() != 0) || (phOrderValExpList.getVal() != 0))  
			return false;
					
		if (phSelectValExpList.getVal() == 0) return false; 

		/* Now get the column list */
		hValExp = jdam.damex_getFirstValExp(phSelectValExpList.getVal());
		if (hValExp == 0) return false;

		jdam.damex_describeValExp(hValExp, null, piFuncType, null, null,null,null, null, null);
		if (piFuncType.getVal() != SQL_F_COUNT_ALL) return false;

		/* Check if there are any more columns/expressions other than count(*) */
		hValExp = jdam.damex_getNextValExp(phSelectValExpList.getVal());
		if (hValExp != 0) return false; 
				
		return true;										
	}
		
    /************************************************************************
     Function:       java_exec_passthrough_query
     Description:    Add the value for count(*) 
     Return:         IP_SUCCESS on success
                     IP_FAILURE on failure.
    ************************************************************************/	
	public int java_exec_passthrough_query( long hstmt, xo_long piNumResRows)
	{
		long 			pVal,hrow;
		int  			iRetCode;
		StringBuffer    sTblName;
		
		jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_exec_passthrough_qry() is called \n");		
		
		sTblName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
		
		/* Check the table name */
		jdam.dam_describeTable(hstmt, null, null, sTblName, null, null);
		
		if (!sTblName.toString().equalsIgnoreCase("CURVALUE") ) 
			return IP_FAILURE; 
		
		/* get the row count */
		pVal = m_pMdb.getRowCount();
		
		/* allocate the result row */
		hrow = jdam.dam_allocRow(hstmt);
		
		iRetCode = jdam.damex_addBigIntResValToRow(hstmt, hrow, 0, pVal, 0);
		if (iRetCode != DAM_SUCCESS) return iRetCode;
		
		iRetCode = jdam.damex_addRowToTable(hstmt, hrow);
		if (iRetCode != DAM_SUCCESS) return iRetCode;
		
		piNumResRows.setVal(1);	
		
		return IP_SUCCESS;
	}
}
