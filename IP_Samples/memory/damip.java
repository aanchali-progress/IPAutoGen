/* damip.java
 *
 * Copyright (c) 1995-2019 Progress Software Corporation. All Rights Reserved.
 *
 * Description:     Memory IP
 */
package oajava.memory;
import java.io.*;
import java.nio.*;
import java.util.Calendar;

import oajava.sql.*;

/********************************************************************************************
 Define memory class to implement the MEMORY IP interface

*********************************************************************************************/
class mem_sample_entry {
    String  sName;
    int     iAge;
    int     iRowVer;
    public void Init(String _sName, int _iAge)
        {
        sName = _sName;
        iAge = _iAge;
        iRowVer = 0;
        }
    };

public class damip implements oajava.sql.ip
{
    private long m_tmHandle = 0;
    private long m_iNumResRows;

    /* Table Type */
    final static int MAX_QUERY_LEN = 1024;
    final static int  EMPTY_TABLE          = 0;   /* No Table */
    final static int  EMP_TABLE            = 1;   /* EMP */
    final static int  DEPT_TABLE           = 2;   /* DEPT */
    final static int  PICTURE_TABLE        = 3;   /* EMP_TABLE */
    final static int  SAMPLE_TABLE         = 4;   /* SAMPLE_TABLE */
    final static int  VARVALUE_TABLE       = 5;   /* VARVALUE to test Variants */
    final static int  RECOVER_TABLE        = 6;   /* Not yet Implemented */
    final static int  TYPES_TABLE          = 7;   /* TYPES */
    final static int  UEMP_TABLE           = 8;   /* UEMP */
    final static int  ARRAYEMP_TABLE       = 9;   /* Not yet Implemented */
    final static int  ALIAS_TABLE          = 10;   /* ALIAS table to test ALIAS support */
    final static int  SPECIAL_TABLE        = 11;   /* Special characters in Identifiers */
    final static int  BINARY_TABLE         = 12;   /* Not Yet Implemented */
    final static int  STRING_TABLE         = 13;   /* STRING_TABLE */
    final static int  WSTRING_TABLE        = 14;   /* WSTRING_TABLE */
    final static int  CSV_TABLE            = 15;   /* Not Yet Implemented */
    final static int  LONG_IDENTIFIERS_TABLE =16;
    final static String OA_CATALOG_NAME   = "SCHEMA";        /* SCHEMA */
    final static String OA_CATALOG_NAME_MAX = "SCHEMA_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_1";
    final static String OA_USER_NAME = "OAUSER";       /* OAUSER */
    final static String OA_USER_NAME_MAX = "OAUSER_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_1";
    final static String OA_TEST_ATTR = "OA_TEST";
    final static String LONG_IDENTIFIERS_TABLE_NAME = "LONG_IDENTIFIERS_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_1";
    final static String LONG_IDENTIFIERS_INT_COL = "INT_VAL_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_";
    final static String LONG_IDENTIFIERS_VARCHAR_COL = "VARCHAR_VAL456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_";
    final static String LONG_IDENTIFIERS_TIMESTAMP_COL = "TIMESTAMP_VAL456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_12345678";
    final static String LONG_IDENTIFIERS_INT_COL_INDEX = "INT_VAL_INDEX6789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_123456789_";
    final static int JDAM_MAX_TRACE_MSG_LEN = 1024;

    final static int FILEIO_MAX_BUFFER_SIZE = 10240; /* 65534*/        /* bytes */

    final static int RESULT_BUFFER_LOB_MAX_SIZE =  65534;        /* bytes */

    final static int IP_ADDR_LEN = 45; /* Maximum Length of the IPV6 address in textual representation */
    /* connection information */
    private long            m_dam_hdbc;
    private StringBuffer    m_sIniFile;
    private StringBuffer    m_sQualifier;         /* char [DAM_MAX_ID_LEN+1] */
    private StringBuffer    m_sUserName;          /* char [DAM_MAX_ID_LEN+1] */

    /* client info attributes */
    private StringBuffer    m_sClientInfoAppName;         /* char [DAM_MAX_ID_LEN+1] */
    private StringBuffer    m_sClientInfoAccInfo;         /* char [DAM_MAX_ID_LEN+1] */
    private StringBuffer    m_sClientInfoHostName;        /* char [DAM_MAX_ID_LEN+1] */
    private StringBuffer    m_sClientInfoUser;            /* char [DAM_MAX_ID_LEN+1] */
    private StringBuffer    m_sClientInfoProgID;          /* char [DAM_MAX_ID_LEN+1] */

    /* index information */
    private StringBuffer    sIndexName;         /* char [DAM_MAX_ID_LEN+1] */

    private String sMemoryWorkingDir;


    /* Array of StmtDA structures to save state information of each active query */
    MEM_STMT_DA[]           stmtDA;
    MEM_PROC_DA[]           procDA;
    MEM_NC_DA[]             nativeDA;

    /* for formatting queries */
    FORMAT                  fmt;

    /* Allow IP to handle schema search pattern if true */
    boolean bAllowSchemaSearchPattern = false;
    boolean bSchemaTestMode = false;
    boolean gbMemMultipleSchema = false;

    boolean bPushDownJoinSupport = false;
	boolean bNullAsInteger = false;

    boolean bVarValueIndexSupport = false;
    boolean bVarValueConditionSupport = false;
    boolean bPassThroughMode = false;

    /* test modes */
   final static int  MEM_TEST_CLIENT_LICENSE_KEY            = 1;
   final static int  MEM_TEST_SERVER_LICENSE_KEY            = 2;
   final static int  MEM_TEST_LOAD_BALANCING                = 3;
   final static int  MEM_TEST_CONNECT_STRING_LIMITS         = 4;
   final static int  MEM_TEST_PRODUCT_CODE_NOTUSED          = 5;
   final static int  MEM_TEST_WINDOWS_SECURITY              = 6;
   final static int  MEM_TEST_VIEW_INFO                     = 7;
   final static int  MEM_TEST_PARENT_HWND                   = 8;
   final static int  MEM_TEST_NESTED_TRANSACTION            = 9;
   final static int  MEM_TEST_CUSTOM_STRING_FUNC            = 10;
   final static int  MEM_TEST_LARGE_SCHEMA                  = 11;
   final static int  MEM_TEST_RESULT_ALIAS                  = 12;
   final static int  MEM_TEST_RESULT_ALIAS_NORMAL_JOIN      = 13;
   final static int  MEM_TEST_JVM_OPTIONS                   = 14;
   final static int  MEM_TEST_NORMAL_JOIN					= 18;
   final static int  MEM_TEST_MAP_SCALAR_AS_COLUMNS         = 21;
   final static int  MEM_TEST_ADD_CHAR_AS_WCHAR		    = 22;
   final static int  MEM_TEST_ADD_WCHAR_AS_CHAR             = 23;
   final static int  MEM_TEST_USE_BULK_FETCH	            = 24;
   final static int MEM_TEST_LOB_LOCATOR					= 25;
   final static int  MEM_TEST_LONG_IDENTIFIERS              = 26;
   final static int  MEM_TEST_GENERATE_COL_NAME_FOR_EXP     = 27;
   final static int  MEM_TEST_IGNORE_AMBIGUOUS_COLUMN_ERROR = 29;
   final static int  MEM_TEST_VALIDATE_SCALAR_FUNC          = 30;
   final static int  MEM_TEST_64BIT_ROWCOUNT			    = 31;
   final static int  MEM_TEST_DISTINCT_LONGDATA 			= 32; /*Test mode for Distinct LONGDATATYPE Testing */
   final static int  MEM_TEST_QUALIFIER_FUNCTION_SUPPORT    = 33; /*Test mode for Qualifier.Function support */

    int giTestMode = 0;
    int giLongDataBufferSize = 0;
	boolean gbMemValidateSchemaObjectInUse = false;

    long RowCount = 0;

    static int giConnections = 0;
    static int giTranslateNum =0;

    /* SAMPLE_TABLE data */
    mem_sample_entry[] pSampleDb;
    int giLastInsertedRowId;
    final static int MAX_SAMPLE_ENTRY = 5;


    private final int[]   ip_support_array =
                {
                    0,
                    1, /* IP_SUPPORT_SELECT */
                    0, /* IP_SUPPORT_INSERT */
                    0, /* IP_SUPPORT_UPDATE */
                    0, /* IP_SUPPORT_DELETE */
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
                    1, /* IP_SUPPORT_CREATE_TABLE */
                    1, /* IP_SUPPORT_DROP_TABLE */
                    1, /* IP_SUPPORT_CREATE_INDEX */
                    1, /* IP_SUPPORT_DROP_INDEX */
                    1, /* IP_SUPPORT_PROCEDURE */
                    0, /* IP_SUPPORT_CREATE_VIEW */
                    0, /* IP_SUPPORT_DROP_VIEW */
                    0, /* IP_SUPPORT_QUERY_VIEW */
                    1, /* IP_SUPPORT_CREATE_USER */
                    1, /* IP_SUPPORT_DROP_USER */
                    1, /* IP_SUPPORT_CREATE_ROLE */
                    1, /* IP_SUPPORT_DROP_ROLE */
                    0, /* IP_SUPPORT_GRANT */
                    0, /* IP_SUPPORT_REVOKE */
                    0,  /* IP_SUPPORT_PASSTHROUGH_QUERY */
                    1,  /* IP_SUPPORT_NATIVE_COMMAND */
                    1,  /* IP_SUPPORT_ALTER_TABLE */
                    0,  /* IP_SUPPORT_BLOCK_JOIN */
                    0,  /* IP_SUPPORT_XA */
                    0,  /* IP_SUPPORT_QUERY_MODE_SELECTION */
                    0,  /* IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE */
                    1,  /* IP_SUPPORT_UNICODE_INFO */
                    0,  /* IP_SUPPORT_JOIN_ORDER_SELECTION */
                    0,  /* Reserved for future use */
                    1,  /* IP_SUPPORT_BULK_INSERT */
                    0,  /* Reserved for future use */
                    0,  /* Reserved for future use */
                    0,  /* Reserved for future use */
                    0,  /* Reserved for future use */
                    0,  /* Reserved for future use */
                    0,  /* Reserved for future use */
                    0   /* Reserved for future use */
                };

/********************************************************************************************
    Method:          memory()
    Description:     Constructor for the IP class
*********************************************************************************************/
    public damip()
    {
        int     i;

        m_tmHandle = 0;
        m_iNumResRows = 0;
        stmtDA = new MEM_STMT_DA[50];   /* Array elements will be created when required */
        procDA = new MEM_PROC_DA[50];   /* Array elements will be created when required */
        nativeDA = new MEM_NC_DA[50];   /* Array elements will be created when required */
        fmt = new FORMAT();

        m_dam_hdbc = 0;
        m_sIniFile = new StringBuffer(256 + 1);
        m_sQualifier = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
        m_sUserName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
        sIndexName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);

        /* client info attributes */
        m_sClientInfoAppName  = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
        m_sClientInfoAccInfo  = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
        m_sClientInfoHostName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
        m_sClientInfoUser     = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
        m_sClientInfoProgID   = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);

        pSampleDb = new mem_sample_entry[MAX_SAMPLE_ENTRY];
        for(i =0; i < MAX_SAMPLE_ENTRY;i++) {
            pSampleDb[i] = new mem_sample_entry();
            pSampleDb[i].Init("", 0);
            }
    }

/********************************************************************************************
    Method:          ipGetInfo()
    Description:     Returns Connection and Statement level information from JDAM
    Return:          String value of the Info requested.
*********************************************************************************************/
    public String ipGetInfo(int iInfoType)
    {
            String str = null;
            String sBuf = "";

            jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipGetInfo() Called. iInfoType=" + iInfoType + "\n");
            switch (iInfoType)
            {
                case IP_INFO_QUALIFIER_TERMW:
                    /* return the Qualifier term used in Schema information */
                    str = "database";
                    break;

                case IP_INFO_OWNER_TERMW:
                    /* return the Owner term used in Schema information */
                    str = "owner";
                    /*
                    Use for testing return of NULL
                    str = "";
                    */
                    break;

                case IP_INFO_QUALIFIER_NAMEW:
                    str = m_sQualifier.toString();
                    sBuf = "IP_INFO_QUALIFIER_NAMEW=" + str + "\n";
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, sBuf);
					break;

                case IP_INFO_OWNER_NAMEW:
                    /* we need to return information that matches schema */
                    str = m_sUserName.toString();
                    break;

                case IP_INFO_FILTER_VIEWS_WITH_QUALIFIER_NAME:
                    str = "0"; /* false */
                    break;
                case IP_INFO_VALIDATE_TABLE_WITH_OWNER:
                    /* return support for multiple schemas */
                    if (gbMemMultipleSchema)
                        str = "1"; /* true */
                    else
                        str = "0"; /* false */
                    break;

                case IP_INFO_VALIDATE_TABLE_WITH_QUALIFIER:
                    /* return support for multiple schemas */
                    if (gbMemMultipleSchema)
                        str = "1"; /* true */
                    else
                        str = "0"; /* false */
                    break;

                case IP_INFO_USE_CURRENT_QUALIFIER_FOR_SYSTEM_SCHEMA:
                    /* return support for multiple schemas */
                    if (gbMemMultipleSchema)
                        str = "1"; /* true */
                    else
                        str = "0"; /* false */
                    break;
                case IP_INFO_SUPPORT_SCHEMA_SEARCH_PATTERN:
                    if(bAllowSchemaSearchPattern)
                        str = "1";  /* true */
                    else
                        str = "0";
                    sBuf = "IP_INFO_SUPPORT_SCHEMA_SEARCH_PATTERN=" + str + "\n";
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, sBuf);
                    break;
                case IP_INFO_SUPPORT_VALUE_FOR_RESULT_ALIAS:
                    {
                    str = "0"; /* false */
                    if (giTestMode == MEM_TEST_RESULT_ALIAS || giTestMode == MEM_TEST_RESULT_ALIAS_NORMAL_JOIN)
                        str = "1"; /* true */

                    sBuf = "IP_INFO_SUPPORT_VALUE_FOR_RESULT_ALIAS=" + str + "\n";
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, sBuf);
                    }
                    break;

                case IP_INFO_CONVERT_NUMERIC_VAL:
                    str = "1";
                    break;

                case IP_INFO_TABLE_ROWSET_REPORT_MEMSIZE_LIMIT:
                    /* report error from dam_addRowToTable when TableRowset size exceeds memory usage */
                    str = "1";
                    break;

                case IP_INFO_TXN_ISOLATION:
                    str = "0x01";
                    sBuf = "IP_INFO_TXN_ISOLATION=" + str + "\n";
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, sBuf);
                    break;
                case IP_INFO_TYPE_INFO:
                    str = "1";
                    break;
                case IP_INFO_DS_INFO:
                    str = "1";
                    break;
                case IP_INFO_DDL_RESULT_ROWS:
					if(giTestMode == MEM_TEST_64BIT_ROWCOUNT) {
						str = String.valueOf(1 + RowCount);
					  }
					  else {
						str = "1";
					  }
                    break;
                case IP_INFO_GENERATE_COL_NAME_FOR_EXP:
                     if (giTestMode == MEM_TEST_GENERATE_COL_NAME_FOR_EXP)
                        str="0";
                     else
                        str = "1";
                    break;
                case IP_INFO_IGNORE_AMBIGUOUS_COLUMN_ERROR:
                     if (giTestMode == MEM_TEST_IGNORE_AMBIGUOUS_COLUMN_ERROR)
                        str="1";
                     else
                        str="0";
                    break;
				case IP_INFO_VALIDATE_SCALAR_FUNC:
                     if (giTestMode == MEM_TEST_VALIDATE_SCALAR_FUNC)
                        str="0";
                     else
                        str="1";
                    break;
				case IP_INFO_NULL_AS_INTEGER:
					if(bNullAsInteger == true)
						  str="1";
					else
						  str="0";
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
            case IP_INFO_CLIENTINFO_APPNAMEW:
                m_sClientInfoAppName.delete(0, m_sClientInfoAppName.length());
                m_sClientInfoAppName.append(InfoVal);
                break;
            case IP_INFO_CLIENTINFO_ACCOUNTINFOW:
                m_sClientInfoAccInfo.delete(0, m_sClientInfoAccInfo.length());
                m_sClientInfoAccInfo.append(InfoVal);
                break;
            case IP_INFO_CLIENTINFO_HOSTNAMEW:
                m_sClientInfoHostName.delete(0, m_sClientInfoHostName.length());
                m_sClientInfoHostName.append(InfoVal);
                break;
            case IP_INFO_CLIENTINFO_USERW:
                m_sClientInfoUser.delete(0, m_sClientInfoUser.length());
                m_sClientInfoUser.append(InfoVal);
                break;
            case IP_INFO_CLIENTINFO_PROGRAMIDW:
                m_sClientInfoProgID.delete(0, m_sClientInfoProgID.length());
                m_sClientInfoProgID.append(InfoVal);
                break;
            default:
            	jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipSetInfo(): Information type:"+ iInfoType +" is out of range\n");
                return DAM_NOT_AVAILABLE;
        }

        return IP_SUCCESS;
    }


/********************************************************************************************
    Method:          ipGetSupport()
    Description:     Initialize IP variables
    Return:          1 if Support exists and 0 if support does not exist.
*********************************************************************************************/
    public int ipGetSupport(int iSupportType)
    {
    if(iSupportType == IP_SUPPORT_BLOCK_JOIN && giTestMode != MEM_TEST_NORMAL_JOIN && giTestMode != MEM_TEST_RESULT_ALIAS_NORMAL_JOIN )
        return 1;
	else if(iSupportType == IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE && gbMemValidateSchemaObjectInUse )
        return 1;
    else
        return(ip_support_array[iSupportType]);
    }

/********************************************************************************************
    Method:          ipConnect
    Description:     Called immediately after an instance of this object is created.
                     You should perform any tasks related to connecting to your data source
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
        /**/
    public int ipConnect(long tmHandle,long dam_hdbc,String sDataSourceName, String sUserName, String sPassword,
						String sCurrentCatalog, String sIPProperties, String sIPCustomProperties)
        {
            String          sExpectedIPProperties="memory";
            String          sBuf = "";
            String          sUsrConnStr;
            int             iRetCode = IP_SUCCESS;
            StringBuffer sDamInfo = new StringBuffer();

            /* Save the trace handle */
            m_tmHandle = tmHandle;
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipConnect called\n");

            /* get DAM_INFO_IP_CLASS info */
            iRetCode = jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_IP_CLASS, sDamInfo, null);
            if (iRetCode != DAM_SUCCESS ) return DAM_FAILURE;
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"[MEM IP]DAM_INFO_IP_CLASS=<" + sDamInfo.toString() + ">\n");

            /* get DAM_INFO_LOGFILE info */
            iRetCode = jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_LOGFILE, sDamInfo, null);
            if (iRetCode != DAM_SUCCESS ) return DAM_FAILURE;
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"[MEM IP]DAM_INFO_LOGFILE=<" + sDamInfo.toString() + ">\n");

            /* get Client Local IP address */
            iRetCode = jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_CLIENT_ADDRESS, sDamInfo, null);
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"[MEM IP]DAM_INFO_CLIENT_ADDRESS=<" + sDamInfo.toString() + ">\n");

            /* get Client Public IP address */
            iRetCode = jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_CLIENT_PUBLIC_ADDRESS, sDamInfo, null);
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"[MEM IP]DAM_INFO_CLIENT_PUBLIC_ADDRESS=<" + sDamInfo.toString() + ">\n");

            /* get openrda.ini and read configuration information */
            iRetCode = jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_OPENRDA_INI,
                                            m_sIniFile, null);
            jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "[MEM IP]OPENRDA_INI=<" + m_sIniFile.toString() + ">\n");
            mem_read_configuration();

            /* Test load balancing controlled by IP */
	        if(giTestMode == MEM_TEST_LOAD_BALANCING)
	        {
		        iRetCode = mem_ip_load_balance_test(dam_hdbc);
		        if(iRetCode != DAM_SUCCESS) return DAM_FAILURE;
	        }

            /* Test JVM OPTIONS */
	        if(giTestMode == MEM_TEST_JVM_OPTIONS)
	        {
		        iRetCode = mem_ip_jvm_options_test(dam_hdbc);
		        if(iRetCode != DAM_SUCCESS) return DAM_FAILURE;
	        }



            /* Test custom connect properties */
            sUsrConnStr = sIPCustomProperties;
            sBuf = "User defined connect string:<" + sUsrConnStr +">\n";
            if (sBuf.length() > JDAM_MAX_TRACE_MSG_LEN)
                jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, sBuf.substring(0,JDAM_MAX_TRACE_MSG_LEN-1));
            else
                jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, sBuf);

	        if (giTestMode == MEM_TEST_CONNECT_STRING_LIMITS && sUsrConnStr.indexOf(OA_TEST_ATTR) >=0)
	        {
		        iRetCode = mem_ip_connect_str_test(dam_hdbc, sUsrConnStr);
		        return IP_FAILURE;
	        }

            /* Test code - Get the application name, Session Token from DAM IP*/
            mem_get_ip_connection_info(dam_hdbc);

            /* Code to connect to your data source */
            /* check if database name is valid */
            if (!gbMemMultipleSchema  && !sIPProperties.equals(sExpectedIPProperties)) {
                sBuf = "Invalid DataSourceIPProperties:" + sIPProperties + ". The supported DataSourceIPProperties is:" + sExpectedIPProperties+"\n";
                jdam.dam_addError(dam_hdbc, 0, DAM_IP_ERROR, 0, sBuf);
                jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                return DAM_FAILURE;
                }

            if (!gbMemMultipleSchema  && !sUserName.equals("pooh")) {
                String sMaxErrorUnixMsg = "Invalid username:MaxErrorUnix. The expected user name is:Pooh. 123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901";
                String sMaxErrorExactMsg = "Invalid username:MaxErrorExact. The expected user name is:Pooh. 123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123.";
                String sMaxErrorMoreMsg = "Invalid username:MaxErrorMore. The expected user name is:Pooh. 1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234.more than 2048 characters";
                String sMaxErrorLessMsg = "Invalid username:MaxErrorLess. The expected user name is:Pooh. 123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567.less than 2048 characters";

                if (sUserName.equals("MaxErrorUnix"))
                {
                    jdam.dam_addError(dam_hdbc, 0, DAM_IP_ERROR, 100, sMaxErrorUnixMsg);
                    jdam.trace(m_tmHandle, UL_TM_ERRORS, sMaxErrorUnixMsg);
                    return DAM_FAILURE;
                }
                if (sUserName.equals("MaxErrorExact"))
                {
                    jdam.dam_addError(dam_hdbc, 0, DAM_IP_ERROR, 100, sMaxErrorExactMsg);
                    jdam.trace(m_tmHandle, UL_TM_ERRORS, sMaxErrorExactMsg);
                    return DAM_FAILURE;
                }
                if (sUserName.equals("MaxErrorMore"))
                {
                    jdam.dam_addError(dam_hdbc, 0, DAM_IP_ERROR, 100, sMaxErrorMoreMsg);
                    jdam.trace(m_tmHandle, UL_TM_ERRORS, sMaxErrorMoreMsg);
                    return DAM_FAILURE;
                }
                if (sUserName.equals("MaxErrorLess"))
                {
                    jdam.dam_addError(dam_hdbc, 0, DAM_IP_ERROR, 100, sMaxErrorLessMsg);
                    jdam.trace(m_tmHandle, UL_TM_ERRORS, sMaxErrorLessMsg);
                    return DAM_FAILURE;
                }

                sBuf = "Invalid User Name:" + sUserName + ". The supported User Name is Pooh\n";
                jdam.dam_addErrorEx(dam_hdbc, 0, "28000", -99, sBuf);
                jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                return DAM_FAILURE;
                }

            if (!gbMemMultipleSchema  && !sPassword.equals("bear")) {
                sBuf = "Invalid Password:" + sPassword + ". The supported Password is bear\n";
                jdam.dam_addError(dam_hdbc, 0, DAM_IP_ERROR, 100, sBuf);
                jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                return DAM_FAILURE;
                }

            m_sQualifier.delete(0, m_sQualifier.length());
            if (gbMemMultipleSchema)
                m_sQualifier.append(sIPProperties);
            else
                m_sQualifier.append(OA_CATALOG_NAME);
            m_sUserName.delete(0, m_sUserName.length());

            /* reset client info */
            m_sClientInfoAppName.delete(0, m_sClientInfoAppName.length());
            m_sClientInfoAccInfo.delete(0, m_sClientInfoAccInfo.length());
            m_sClientInfoHostName.delete(0, m_sClientInfoHostName.length());
            m_sClientInfoUser.delete(0, m_sClientInfoUser.length());
            m_sClientInfoProgID.delete(0, m_sClientInfoProgID.length());

            /* m_sUserName.append(OA_USER_NAME); */
            m_sUserName.append(sUserName);

	        if(giTestMode == MEM_TEST_VIEW_INFO)
            {
		        mem_ip_view_info_test(dam_hdbc);
            }


            jdam.dam_setOption(ip.DAM_CONN_OPTION, dam_hdbc, ip.DAM_CONN_OPTION_CASE_IN_STRINGS, ip.DAM_CIS_IGNORE_NONE);
            m_dam_hdbc = dam_hdbc;
            return IP_SUCCESS;
        }

    private int mem_read_configuration()
    {
            String          sTemp = "";
            int             iTemp;

            /* get the MemoryWorkingDir from OPENRDA_INI */
            sMemoryWorkingDir = jdam.getProfileString("Memory", "WorkingDir", "", m_sIniFile.toString());

            /* VARVALUE configuration */
            sTemp = jdam.getProfileString("Memory", "VarValueIndex", "0", m_sIniFile.toString());
			iTemp = Integer.valueOf(sTemp);
            if (iTemp == 1)
                bVarValueIndexSupport = true;
            else
                bVarValueIndexSupport = false;

            sTemp = jdam.getProfileString("Memory", "VarValueCondition", "0", m_sIniFile.toString());
            iTemp = Integer.valueOf(sTemp);
            if (iTemp == 1)
                bVarValueConditionSupport = true;
            else
                bVarValueConditionSupport = false;

            /* Schema Configuration */
            sTemp = jdam.getProfileString("Memory", "SchemaTestMode", "0", m_sIniFile.toString());
            iTemp = Integer.valueOf(sTemp);
            if (iTemp == 1)
                bSchemaTestMode = true;
            else
                bSchemaTestMode = false;

            /* Multiple Schema */
            sTemp = jdam.getProfileString("Memory", "MultipleSchema", "0", m_sIniFile.toString());
            iTemp = Integer.valueOf(sTemp);
            if (iTemp == 1)
                gbMemMultipleSchema = true;
            else
                gbMemMultipleSchema = false;
            jdam.trace(m_tmHandle, UL_TM_INFO,"Multiple Schema :"+ gbMemMultipleSchema + "\n");

            /* Pushdown Join Configuration */
            sTemp = jdam.getProfileString("Memory", "PushdownJoin", "0", m_sIniFile.toString());
            iTemp = Integer.valueOf(sTemp);
            if (iTemp == 1)
                bPushDownJoinSupport = true;
            else
                bPushDownJoinSupport = false;

            sTemp = jdam.getProfileString("Memory", "SchemaSearchPattern", "0", m_sIniFile.toString());
            iTemp = Integer.valueOf(sTemp);
            if (iTemp == 1)
                bAllowSchemaSearchPattern = true;
            else
                bAllowSchemaSearchPattern = false;

            if(bSchemaTestMode) {
                if(bAllowSchemaSearchPattern)
                    System.out.println("[MEM IP] SchemaSearchPattern is Supported");
                else
                    System.out.println("[MEM IP] SchemaSearchPattern is Not Supported");
            }

			sTemp = jdam.getProfileString("Memory", "NULLASINTEGER", "0", m_sIniFile.toString());
            iTemp = Integer.valueOf(sTemp);
            if (iTemp == 1)
                bNullAsInteger = true;
            else
                bNullAsInteger = false;
			
            /* TestMode Configuration */
            sTemp = jdam.getProfileString("Memory", "TestMode", "0", m_sIniFile.toString());
            giTestMode = Integer.valueOf(sTemp);
			
			/* Get Buffer size for Long Datatypes block size from INI*/
			sTemp = jdam.getProfileString("Memory", "LongDataBufferSize", "10240", m_sIniFile.toString());
			giLongDataBufferSize = Integer.valueOf(sTemp);
			
			 /* Get ValidateSchemaObjectInUse*/
			sTemp = jdam.getProfileString("Memory", "ValidateSchemaObjectInUse", "0", m_sIniFile.toString());
			if ((Integer.valueOf(sTemp))==1)
                gbMemValidateSchemaObjectInUse = true;

			/* Get INT64 Value for 64Bit RowCount Test Cases*/
            sTemp = jdam.getProfileString("Memory", "RowCount", "0", m_sIniFile.toString());
            RowCount = Long.valueOf(sTemp);

            return IP_SUCCESS;
    }

/********************************************************************************************
    Method:          ipDisconnect()
    Description:     Initialize IP variables
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int ipDisconnect(long dam_hdbc)
    {   /* disconnect from the data source */
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipDisonnect called\n");

	        if(giTestMode == MEM_TEST_LOAD_BALANCING)
	        {
		        if(giConnections > 0)
			        giConnections--;
	        }

            return IP_SUCCESS;
    }

/********************************************************************************************
    Method:          ipStartTransaction()
    Description:     Transaction starts
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int ipStartTransaction(long dam_hdbc)
    {
            /* start a new transaction */
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipStartTransaction called\n");
            return IP_SUCCESS;
    }

/********************************************************************************************
    Method:          ipEndTransaction()
    Description:     Transaction Ends
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
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
        typesInfo[i++] = new oa_types_info("DECIMAL", 2, 40, null, null, "precision,scale", 1, 0, 2, 0, 0, 0, 0, 32, "DECIMAL");
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

        //piNumRows.setVal(24);
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

        dsInfo[j++] = new oa_ds_info("SQL_ACTIVE_STATEMENTS", 1, 0, DAMOBJ_NOTSET, "", "The maximum number of statements supported");
        dsInfo[j++] = new oa_ds_info("SQL_ROW_UPDATES", 11, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "N", "Y if driver can detect row changes between multiple fetches of");
        dsInfo[j++] = new oa_ds_info("SQL_ODBC_SQL_CONFORMANCE", 15, 0, DAMOBJ_NOTSET, "", "SQL Grammar supported by the driver");
        dsInfo[j++] = new oa_ds_info("SQL_SEARCH_PATTERN_ESCAPE", 14, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "\\", "");
        dsInfo[j++] = new oa_ds_info("SQL_DBMS_NAME", 17, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "OpenAccess", "");
        dsInfo[j++] = new oa_ds_info("SQL_DBMS_VER", 18, DAMOBJ_NOTSET, DAMOBJ_NOTSET, "06.00", "Version of current DBMS product of the form ##.## ex: 01.00");
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

/********************************************************************************************
    Method:          java_mem_init_stmt
    Description:     Initialize IP variables
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int ipExecute(long dam_hstmt, int iStmtType, long hSearchCol,xo_long piNumResRows)
        {
            MEM_STMT_DA pStmtDA;
            int iRetCode = DAM_SUCCESS;
            int idx = -1;

	    jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipExecute called. Statement Type " + iStmtType + "\n");


			if (iStmtType == DAM_SET_QUERY_MODE)
			{
				if (bPassThroughMode)
				{
					jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"Setting query execution to be Passthrough mode.\n");
					iRetCode = jdam.dam_setOption(DAM_STMT_OPTION, dam_hstmt, DAM_STMT_OPTION_PASSTHROUGH_QUERY, DAM_PROCESSING_ON);
				}
				bPassThroughMode = !bPassThroughMode;
				return DAM_SUCCESS;
			}

            if (iStmtType == DAM_SELECT || iStmtType == DAM_INSERT || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE) {
                // print information about bound parameter
                mem_print_param_info(dam_hstmt, iStmtType);

                xo_int  piValue;

                piValue = new xo_int();

				/* check query execution mode */
				iRetCode = jdam.dam_getInfo(0, dam_hstmt, DAM_INFO_PASSTHROUGH_QUERY,
					null, piValue);
				if (piValue.getVal() > 0)
				{
					jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"Returning empty results for Passthrough query\n");
					return DAM_SUCCESS;
				}

				pStmtDA = new MEM_STMT_DA();

                pStmtDA.dam_hstmt = dam_hstmt;
                pStmtDA.iType = iStmtType;
                pStmtDA.hSearchCol = hSearchCol;


                /* get the table information */
                jdam.dam_describeTable(dam_hstmt, null, null, pStmtDA.sTableName,null, null);

                iRetCode = java_mem_init_stmt(pStmtDA);
                if (iRetCode != IP_SUCCESS) return iRetCode;

                /* get fetch block size */
                iRetCode = jdam.dam_getInfo(0, pStmtDA.dam_hstmt, DAM_INFO_FETCH_BLOCK_SIZE,
                                            null, piValue);
                if (iRetCode != DAM_SUCCESS)
                    pStmtDA.iFetchSize = 2;
                else
                    pStmtDA.iFetchSize = piValue.getVal();

                jdam.trace(m_tmHandle, UL_TM_INFO,"ipExecute() Fetch size = "+ pStmtDA.iFetchSize + "\n");

                /* get the statement handle */
                idx = getStmtIndex();
                if(idx >= 0) {
                    stmtDA[idx] = pStmtDA;
                    }

		if ( MEM_TEST_USE_BULK_FETCH == giTestMode ) {
		    pStmtDA.m_resultBuffer = jdam.dam_allocResultBuffer(dam_hstmt);
		}
                jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the StmtDA index*/

            }
            else if (iStmtType == DAM_FETCH) { /* return more results from previously executed query */
                 idx = -1;

                 idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
                 pStmtDA = stmtDA[idx];
                }
            else if (iStmtType == DAM_CLOSE) { /* close results of executed query. Free any results */
                idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
		pStmtDA = stmtDA[idx];
		if ( MEM_TEST_USE_BULK_FETCH == giTestMode ) {
		    jdam.dam_freeResultBuffer(dam_hstmt,pStmtDA.m_resultBuffer);
		}
                pStmtDA = null;
                stmtDA[idx] = null;
                return IP_SUCCESS;
                }
            else if (iStmtType == DAM_SET_JOIN_ORDER)
            { /* Join order via IP */
                long   htable;
                long   hquery;

                /* get the query handle */
                hquery = jdam.dam_getJoinQuery(dam_hstmt);
                StringBuffer sSqlString = new StringBuffer(MAX_QUERY_LEN);
                fmt.ip_format_query(hquery, sSqlString);
                String str = "ipExecute() : The format of the hquery : < " + sSqlString + ">\n";
		        jdam.trace(m_tmHandle, UL_TM_INFO, str);
                htable = jdam.damex_getFirstTable(hquery);

                while (htable != 0) {
                    jdam.dam_setJoinOrder(htable);
                    htable = jdam.damex_getNextTable(hquery);
                }
                return IP_SUCCESS;
            }
            else if (iStmtType == DAM_INSERT_BULK)
            {
            	/* Bulk Insert */
            	pStmtDA = new MEM_STMT_DA();

				pStmtDA.dam_hstmt = dam_hstmt;
				pStmtDA.iType = iStmtType;

				/* get the table information */
				jdam.dam_describeTable(dam_hstmt, null, null, pStmtDA.sTableName,null, null);
				jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"Bulk Insert called for the table : "+ pStmtDA.sTableName + "\n");

				iRetCode = mem_exec_bulk_insert(pStmtDA, piNumResRows);
				return iRetCode;
            }
            else
                return IP_FAILURE;

            /* process the query based on the type */
           if (iStmtType == DAM_SELECT || iStmtType == DAM_FETCH) {

                /* initialize the result */
                piNumResRows.setVal(0);

                /* handle all tables except EMP and DEPT seperately */
                if (pStmtDA.iTable == PICTURE_TABLE) {
                   m_iNumResRows = 0;
                   if ( MEM_TEST_USE_BULK_FETCH == giTestMode )
                   {
                       iRetCode = java_build_buffer_rows_picture_table(pStmtDA, (iStmtType == DAM_SELECT) ? true : false);
                       if (iRetCode != IP_SUCCESS) return iRetCode;
                       piNumResRows.setVal(m_iNumResRows);
                   }
                   else
                    iRetCode = java_mem_exec_picture_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == SAMPLE_TABLE) {
                    iRetCode = java_mem_exec_sample_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == VARVALUE_TABLE) {
                    iRetCode = java_mem_exec_varvalue_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == RECOVER_TABLE) {
                    iRetCode = java_mem_exec_recover_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == TYPES_TABLE)
                {
                    m_iNumResRows = 0;
                    if ( MEM_TEST_USE_BULK_FETCH == giTestMode )
                    {
                        iRetCode = java_build_buffer_rows_types_table(pStmtDA, (iStmtType == DAM_SELECT) ? true : false);
                        if (iRetCode != IP_SUCCESS) return iRetCode;
                        piNumResRows.setVal(m_iNumResRows);
                    }
                    else
                        iRetCode = java_mem_exec_types_table(pStmtDA, piNumResRows);
                }
                else if (pStmtDA.iTable == ALIAS_TABLE) {
                    iRetCode = java_mem_exec_alias_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == SPECIAL_TABLE) {
                    iRetCode = java_mem_exec_special_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == UEMP_TABLE) {
                    iRetCode = java_mem_exec_uemp_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == ARRAYEMP_TABLE) {
                    iRetCode = java_mem_exec_arrayemp_table(pStmtDA, piNumResRows);
                    }
	       else if (pStmtDA.iTable == LONG_IDENTIFIERS_TABLE) {
                    iRetCode = java_mem_exec_long_identifiers_table(pStmtDA, piNumResRows);
                    }
		else { /* EMP, DEPT, STRING_TABLE tables */

                    /* use index information for optimized query processing - cases where NAME='Joe' type of
                       conditions appear in the query*/
                    if(pStmtDA.hindex.getVal() != 0)
                        {
                        m_iNumResRows = 0;
                        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"Using optimized processing\n");
                        jdam.dam_describeIndex(pStmtDA.hindex.getVal(), null, sIndexName, null, null, null);

                        /* use the index to access and build rows */
                        iRetCode = java_optimize_exec(pStmtDA, iStmtType, piNumResRows);
                        if (iRetCode == IP_SUCCESS)
                        {
                            jdam.dam_freeSetOfConditionList(pStmtDA.hset_of_condlist.getVal()); /* free the set of condition list */
                        }
                        if (iRetCode != IP_SUCCESS) return iRetCode;
                        }
                    else
                        {
                        /* non-optimized query processing -- cases where no WHERE clause or conditions on columns
                           other than NAME. Do a full table scan*/

                        m_iNumResRows = 0;
                        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"Doing a full table scan\n");
                        iRetCode = java_exec(pStmtDA, iStmtType, piNumResRows);
                        if (iRetCode != IP_SUCCESS) return iRetCode;
                        piNumResRows.setVal(m_iNumResRows);
                        }
                    iRetCode = DAM_SUCCESS;
                    }
                }
            else if (iStmtType == DAM_INSERT || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE) {
                if (pStmtDA.iTable == EMP_TABLE || pStmtDA.iTable == DEPT_TABLE) {
                    long i = pStmtDA.lItems;
                    /* return success. We don't process insert, update, delete */
                    piNumResRows.setVal(i);
                    mem_insert_row(pStmtDA);
                    iRetCode = IP_SUCCESS;
                    }
                else if (pStmtDA.iTable == VARVALUE_TABLE) {
                    iRetCode = java_mem_exec_varvalue_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == SAMPLE_TABLE) {
                    iRetCode = java_mem_exec_sample_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == PICTURE_TABLE) {
                    iRetCode = java_mem_exec_picture_table(pStmtDA, piNumResRows);
                    }
		else if (pStmtDA.iTable == STRING_TABLE) {
                    iRetCode = java_mem_exec_string_table(pStmtDA, piNumResRows);
                    }
		else if (pStmtDA.iTable == WSTRING_TABLE) {
                    iRetCode = java_mem_exec_wstring_table(pStmtDA, piNumResRows);
                    }
                else if (pStmtDA.iTable == BINARY_TABLE) {
                    iRetCode = java_mem_exec_binary_table(pStmtDA, piNumResRows);
                    }
                else
                    iRetCode = DAM_FAILURE;
            }

        /* when processing is fully done, release from Stmt Array */
        if(idx >= 0) {
	   pStmtDA = stmtDA[idx];
	   if ( MEM_TEST_USE_BULK_FETCH == giTestMode ) {
	       jdam.dam_freeResultBuffer(dam_hstmt,pStmtDA.m_resultBuffer);
	   }
           stmtDA[idx] = null;
           }


        return iRetCode;
    }


   /* Utility methods */
/********************************************************************************************
    Method:          java_mem_init_stmt
    Description:     Initialize IP variables
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/

    int java_mem_init_stmt(MEM_STMT_DA pStmtDA)
        {
            long hcur_condlist;  /* DAM_HCONDLIST */
            long hcond;          /* DAM_HCOND */
            long hcol;           /* DAM_HCOL */
            int iRetCode;
            long hcolItems, hcolEmpId;

            xo_int iLeftOp, iLeftXoType, iLeftValLen, iStatus;
            Long pLeftData = null;
	    jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_init_stmt called\n");

            /* check if MAXROWS is specified */
            xo_int piValue = new xo_int();
            iRetCode = jdam.dam_getInfo(0, pStmtDA.dam_hstmt, DAM_INFO_QUERY_MAX_ROWS,
                                            null, piValue);
            pStmtDA.iMaxRows = piValue.getVal();
            pStmtDA.iTotalRowCount = 0;
            jdam.trace(m_tmHandle, UL_TM_MINOR_EV,"java_mem_init_stmt(). Max Rows=" + pStmtDA.iMaxRows + "\n");

            /* check if TOP N clause is set */
            piValue.setVal(0);
            iRetCode = jdam.dam_getInfo(0, pStmtDA.dam_hstmt, DAM_INFO_QUERY_TOP_ROWS,
                                            null, piValue);
            pStmtDA.iTopRows = piValue.getVal();
            pStmtDA.iTotalRowCount = 0;
            jdam.trace(m_tmHandle, UL_TM_MINOR_EV,"java_mem_init_stmt(). Top Rows=" + pStmtDA.iTopRows + "\n");

            iLeftOp = new xo_int(0);
            iLeftXoType = new xo_int(0);
            iLeftValLen = new xo_int(0);
            iStatus = new xo_int(0);

            if (pStmtDA.sTableName.toString().equalsIgnoreCase("EMP") || pStmtDA.sTableName.toString().equalsIgnoreCase("EMP_ALIAS")) {
                pStmtDA.iTable = EMP_TABLE;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("DEPT")) {
                pStmtDA.iTable = DEPT_TABLE;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("EMP_TABLE")) {
                pStmtDA.iTable = PICTURE_TABLE;
                return IP_SUCCESS;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("SAMPLE_TABLE")) {
                pStmtDA.iTable = SAMPLE_TABLE;
                return IP_SUCCESS;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("VARVALUE")) {
                pStmtDA.iTable = VARVALUE_TABLE;
                return IP_SUCCESS;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("OA_TXN_LOG")) {
                pStmtDA.iTable = RECOVER_TABLE;
                return IP_SUCCESS;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("TYPES_TABLE")) {
                pStmtDA.iTable = TYPES_TABLE;
                return IP_SUCCESS;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("ALIAS_TABLE")) {
                pStmtDA.iTable = ALIAS_TABLE;
                return IP_SUCCESS;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("UEMP")) {
                pStmtDA.iTable = UEMP_TABLE;
                return IP_SUCCESS;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("EMPARRAY")) {
                pStmtDA.iTable = ARRAYEMP_TABLE;
                return IP_SUCCESS;
            }
            else if (pStmtDA.sTableName.toString().indexOf("SPECIAL") >= 0) {
                pStmtDA.iTable = SPECIAL_TABLE;
                return IP_SUCCESS;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("BINARY_TABLE")) {
                pStmtDA.iTable = BINARY_TABLE;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("STRING_TABLE")) {
                pStmtDA.iTable = STRING_TABLE;
            }
            else if (pStmtDA.sTableName.toString().equalsIgnoreCase("WSTRING_TABLE")) {
                pStmtDA.iTable = WSTRING_TABLE;
            }
	else if (pStmtDA.sTableName.toString().equalsIgnoreCase(LONG_IDENTIFIERS_TABLE_NAME)) {
                pStmtDA.iTable = LONG_IDENTIFIERS_TABLE;
                return IP_SUCCESS;
            }
            else {
                return IP_FAILURE;
                }

            /* get the restrictions on NumItems */
            hcolItems = jdam.dam_getCol(pStmtDA.dam_hstmt, "ITEMS");

			/* If option IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE is enabled, handles are available only
			   for the columns used in the query.And if the query does not contain the "items" column,
			   handle for "items" column will be NULL. In such cases set the rowcount to 1 */

			if (hcolItems!=0)
	            hcur_condlist = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, hcolItems);
			else
				hcur_condlist=0;

			if (hcur_condlist == 0)
                pStmtDA.lItems = 1;
            else {
                /* get the condition details */
                hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hcur_condlist);
                pLeftData = (Long)jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
                iRetCode = iStatus.getVal();
                if (iRetCode != IP_SUCCESS) return iRetCode;
                if (iLeftOp.getVal() != SQL_OP_EQUAL || iLeftXoType.getVal() != XO_TYPE_BIGINT)
                    return DAM_FAILURE;

                /* check if condition value is NULL */
                if (pLeftData.intValue() == 0)
                    pStmtDA.lItems =0;
                else
                    pStmtDA.lItems = pLeftData.longValue();

                if (jdam.dam_getNextCond(pStmtDA.dam_hstmt, hcur_condlist) != 0) {
                    jdam.trace(m_tmHandle, UL_TM_ERRORS,"Driver does not support more than one condition on the ITEMS column\n");
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 100, "Driver does not support more than one condition on the ITEMS column");
                    return IP_FAILURE;
                    }
                iRetCode = jdam.dam_setOption(ip.DAM_CONDLIST_OPTION, hcur_condlist, ip.DAM_CONDLIST_OPTION_EVALUATION, ip.DAM_PROCESSING_OFF);

                }

            /* get the restrictions on EmpId */
            if (pStmtDA.iTable == EMP_TABLE) {
                hcolEmpId = jdam.dam_getCol(pStmtDA.dam_hstmt, "EMPID");

				/* If option IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE is enabled, handles are available only
				   for the columns used in the query.And if the query does not contain the "items" column,
				   handle for "items" column will be NULL. In such cases set the rowcount to 1 */

				if (hcolEmpId!=0)
    	       		hcur_condlist = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, hcolEmpId);
				else
					hcur_condlist=0;

    	        if (hcur_condlist == 0)
        	        pStmtDA.lEmpId = 1;
            	else {

                    /* get the condition details */
                    hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hcur_condlist);
                    pLeftData = (Long)jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
                    iRetCode = iStatus.getVal();
                    if (iRetCode != DAM_SUCCESS) return iRetCode;
                    if (iLeftOp.getVal() != SQL_OP_EQUAL || iLeftXoType.getVal() != XO_TYPE_BIGINT)
                        return DAM_FAILURE;
                    /* check if condition value is NULL */
                    if (pLeftData.longValue() == 0)
                        pStmtDA.lEmpId =0;
                    else
                        pStmtDA.lEmpId = pLeftData.intValue();

                if (jdam.dam_getNextCond(pStmtDA.dam_hstmt, hcur_condlist) != 0) {
                    jdam.trace(m_tmHandle, UL_TM_ERRORS,"Driver does not support more than one condition on the EMPID column\n");
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, -99, "Driver does not support more than one condition on the EMPID column");
                    return IP_FAILURE;
                    }
                }
            }

            /* get restriction on Ename */
        if (pStmtDA.iTable == STRING_TABLE || pStmtDA.iTable == WSTRING_TABLE || pStmtDA.iTable == BINARY_TABLE)
        {

            hcol = jdam.dam_getCol(pStmtDA.dam_hstmt, "ENAME");
            hcur_condlist = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, hcol);
            if (hcur_condlist == 0)
                pStmtDA.sName =  "pooh";
            else
            {
                /* process each of the conditions */
                hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hcur_condlist);
                pStmtDA.sName = (String)jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
                iRetCode = iStatus.getVal();
                if (iRetCode != DAM_SUCCESS) return iRetCode;
                if (iLeftOp.getVal() != SQL_OP_EQUAL || !(iLeftOp.getVal() == XO_TYPE_CHAR || iLeftOp.getVal() == XO_TYPE_VARCHAR))
                    return DAM_FAILURE;


                if (jdam.dam_getNextCond(pStmtDA.dam_hstmt, hcur_condlist) != 0)
                {
                    jdam.trace(m_tmHandle, UL_TM_ERRORS,"Driver does not support more than one condition on the ENAME column\n");
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, -99, "Driver does not support more than one condition on the ENAME column");
                    return IP_FAILURE;
                }
                /*
                 lets mark the condition list as evaluated, since we return the name-item instead of name
                 for each record
                 */
                jdam.dam_setOption(DAM_CONDLIST_OPTION, hcur_condlist, DAM_CONDLIST_OPTION_EVALUATION, DAM_PROCESSING_OFF);
            }
        } /* STRING_TABLE WSTRING_TABLE */

            /* get all the column handles in use */
            pStmtDA.m_iNoOfResColumns = 0;
            hcol = jdam.dam_getFirstCol(pStmtDA.dam_hstmt, ip.DAM_COL_IN_USE);
            while (hcol != 0) {
                StringBuffer    sColName;
                sColName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
                xo_int          iValue;
                boolean         bSkipScalar;

                pStmtDA.m_iNoOfResColumns++;
                bSkipScalar = false;
                jdam.dam_describeCol(hcol, null, sColName, null, null);
                if( giTestMode == MEM_TEST_MAP_SCALAR_AS_COLUMNS )
                {
                    if (jdam.dam_describeColScalar(hcol, null)!= 0) bSkipScalar = true;
                }
                /* skip scalar columns */
                if (bSkipScalar) /* skip */
                    ;
                else if (sColName.toString().equals("DEPTID"))pStmtDA.hcolDeptId = hcol;
                else if (sColName.toString().equals("DNAME")) pStmtDA.hcolDname = hcol;
                else if (sColName.toString().equals("ITEMS")) pStmtDA.hcolItems = hcol;
                else if (sColName.toString().equals("EMPID")) pStmtDA.hcolEmpId = hcol;
                else if (sColName.toString().equals("ENAME")) pStmtDA.hcolEname = hcol;
                else if (sColName.toString().equals("DATE_VAL")) pStmtDA.hcolDate = hcol;
                else if (sColName.toString().equals("DOUBLE_VAL")) pStmtDA.hcolDouble = hcol;
                else if (sColName.toString().equals("NUMERIC_VAL")) pStmtDA.hcolNumeric = hcol;
                else if (sColName.toString().equals("NOTE")) pStmtDA.hcolNote = hcol;
		else if (sColName.toString().equals("COMMENT")) pStmtDA.hcolComment = hcol;
		else if (sColName.toString().equals("COMMENTXL")) pStmtDA.hcolCommentXL = hcol;
                else if (sColName.toString().equals("STAMP")) pStmtDA.hcolStamp = hcol;
                else if (sColName.toString().equals("PICTURE")) pStmtDA.hcolPicture = hcol;
                else if (sColName.toString().equals("PICTUREXL")) pStmtDA.hcolPictureXL = hcol;
                else return DAM_FAILURE;

                iValue = new xo_int(0);
                jdam.dam_getColOption(m_dam_hdbc, hcol, ip.DAM_COL_OPTION_IGNORE_CASE_IN_STRINGS, iValue);
                jdam.trace(m_tmHandle, UL_TM_MINOR_EV, "Column:" + sColName.toString() + ". IgnoreCase=" + iValue.getVal() + "\n");

                hcol =  jdam.dam_getNextCol(pStmtDA.dam_hstmt);
                }

            /* initialize the search column conditions */
            iRetCode = jdam.dam_getOptimalIndexAndConditions(pStmtDA.dam_hstmt, pStmtDA.hindex, pStmtDA.hset_of_condlist);
            if (iRetCode != DAM_SUCCESS) return IP_FAILURE; /* return on error */
            if (pStmtDA.hindex.getVal() != 0) {
                pStmtDA.hSearchCondList = jdam.dam_getFirstCondList(pStmtDA.hset_of_condlist.getVal());
                }

            /* check if group by can be optimized */
            {
            int     iColCount;
            xo_int  iValue;
            iValue =  new xo_int(0);

            jdam.dam_getInfo(0, pStmtDA.dam_hstmt,DAM_INFO_GROUP_BY_OPTIMIZABLE, null, iValue);
            if (iValue.getVal() > 0) { /* lets get the columns in group by */
                jdam.trace(m_tmHandle, UL_TM_ERRORS, "java_optimize_exec(): Invalid operator type "+ iLeftOp + "\n");

                jdam.trace(m_tmHandle, UL_TM_MINOR_EV, "java_mem_init_stmt(). Group By Processing can be Optmized by IP. Number of GroupBy Columns="+
                                iValue.getVal() + "\n");

                for (iColCount = 0; iColCount < iValue.getVal(); iColCount++) {
                    xo_int             iSchemaNum;
                    StringBuffer    sColName;

                    sColName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
                    iSchemaNum = new xo_int(0);

                    jdam.dam_getGroupByCol(pStmtDA.dam_hstmt, iColCount, iSchemaNum, sColName);
                    jdam.trace(m_tmHandle, UL_TM_MINOR_EV, "java_mem_init_stmt(). Group By ColNum="+iColCount+", SchemaNum="+iSchemaNum+", ColName="+sColName + "\n");

                    if (iValue.getVal() == 1 && pStmtDA.iTable == EMP_TABLE &&
                            (sColName.toString().equalsIgnoreCase("EMPID") || sColName.toString().equalsIgnoreCase("DEPTID"))) {
                        jdam.trace(m_tmHandle, UL_TM_MINOR_EV, "java_mem_init_stmt(). Group By will be processed by IP\n");
                        jdam.dam_setOption(DAM_STMT_OPTION, pStmtDA.dam_hstmt, DAM_STMT_OPTION_GROUP_BY, DAM_PROCESSING_OFF);
                        }
                    }
                }
            } /* GROUP BY check */

            return IP_SUCCESS;
        }


/********************************************************************************************
    Method:          java_exec
    Description:     This function is called to do a full table scan
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int java_exec(MEM_STMT_DA pStmtDA, int iStmtType, xo_long piNumResRows)
        {
            long    hrow; /* DAM_HROW */
            int     iRetCode;

            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_exec called\n");

            piNumResRows.setVal(0);
//            int i = 0;

        if ( MEM_TEST_USE_BULK_FETCH == giTestMode )
        {

            if (pStmtDA.iTable != EMP_TABLE && pStmtDA.iTable != STRING_TABLE && pStmtDA.iTable != WSTRING_TABLE && pStmtDA.iTable != DEPT_TABLE && pStmtDA.iTable != BINARY_TABLE)
            {
                jdam.trace(m_tmHandle, UL_TM_ERRORS,"Driver does not support queries on the table\n");
                jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, "Driver does not support queries on the table");
                return DAM_FAILURE;
            }

            pStmtDA.m_resultBuffer.clear();
            pStmtDA.m_resultBuffer.setColumnType(DAM_COL_IN_USE);
            iRetCode = java_build_buffer_rows(pStmtDA, (iStmtType == DAM_SELECT) ? true : false);
            if (iRetCode != IP_SUCCESS) return iRetCode;
        }
	    else{
		/* build the DAM row with the values read from memory database */
		hrow = java_build_row(pStmtDA, (iStmtType == DAM_SELECT) ? true : false);
		if (hrow == 0 && pStmtDA.lCurItem <= pStmtDA.lItems) return DAM_FAILURE;
		while(hrow != 0) {
		    /* process the row */
		    iRetCode = java_process_row(pStmtDA, iStmtType, hrow);

		    /* check if MAX ROWS are processed */
		    if (pStmtDA.iMaxRows > 0 && pStmtDA.iTotalRowCount >= pStmtDA.iMaxRows) {
			piNumResRows.setVal(m_iNumResRows);
			return IP_SUCCESS;
			}

		    /* check if TOP rows are processed */
		    if (pStmtDA.iTopRows > 0 && pStmtDA.iTotalRowCount >= pStmtDA.iTopRows) {
			piNumResRows.setVal(m_iNumResRows);
			return IP_SUCCESS;
			}

		    if (iRetCode != IP_SUCCESS) return iRetCode; /* return on error */

		    /* read next row from the memory database */
		    hrow = java_build_row(pStmtDA, false);
		    if (hrow == 0 && pStmtDA.lCurItem <= pStmtDA.lItems) return DAM_FAILURE;
		    }
	    }

            piNumResRows.setVal(m_iNumResRows);
            return IP_SUCCESS;
    }

    public int java_build_buffer_rows(MEM_STMT_DA pStmtDA, boolean bFirst)
    {
        /* Code to handle 0 items */
    	if(pStmtDA.lItems == 0)
    		return DAM_SUCCESS;

        if (pStmtDA.iTable == EMP_TABLE)
        {
            return java_build_buffer_rows_emp(pStmtDA,bFirst);
        }
        else if (pStmtDA.iTable == STRING_TABLE)
        {
            return java_build_buffer_rows_string_table(pStmtDA,bFirst);
        }
        else if (pStmtDA.iTable == WSTRING_TABLE)
        {
            return java_build_buffer_rows_wstring_table(pStmtDA,bFirst);
        }
        else if (pStmtDA.iTable == DEPT_TABLE)
        {
            return java_build_buffer_rows_dept(pStmtDA,bFirst);
        }
        else if (pStmtDA.iTable == BINARY_TABLE)
        {
            return java_build_buffer_rows_binary_table(pStmtDA,bFirst);
        }

        return DAM_FAILURE;
    }
    public int java_build_buffer_rows_binary_table(MEM_STMT_DA pStmtDA, boolean bFirst)
    {
    	int     iRetCode;
        String  szName;
        String  sFileName = "";
        StringBuffer pData = new StringBuffer();
        boolean bIsFirst = bFirst;
        byte[] stampBuffer = null;
        byte[] pictureBuffer = null;
        byte[] picturexlBuffer = null;
        while (true)
        {
            if (bIsFirst)
            {
                pStmtDA.m_resultBuffer.setNoOfResColumns(pStmtDA.m_iNoOfResColumns);
                pStmtDA.lCurItem = 1;
            }
            else
            {
                pStmtDA.lCurItem++;
            }
            if (pStmtDA.lCurItem > pStmtDA.lItems)
            {
                pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
                jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
                pStmtDA.m_resultBuffer.clear();
                iRetCode =  IP_SUCCESS; /* End Of Data */
                break;
            }

            try
            {
                szName = pStmtDA.sName + "-" + pStmtDA.lCurItem;
                if (pStmtDA.hcolEname != 0)
                    pStmtDA.m_resultBuffer.putString(szName);
                if (pStmtDA.hcolStamp != 0)
                {

                    /* get the file name */
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat(pStmtDA.sName);
                    sFileName = sFileName.concat("S.bmp");
                    stampBuffer = mem_get_long_binary_data(sFileName);
                    if (stampBuffer != null)
                    {
                        pStmtDA.m_resultBuffer.putBinary(stampBuffer);
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (pStmtDA.hcolPicture != 0)
                {

                    /* get the file name */
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat(pStmtDA.sName);
                    sFileName = sFileName.concat("L.bmp");

                    pictureBuffer = mem_get_long_binary_data(sFileName);
                    if (pictureBuffer != null)
                    {
                          pStmtDA.m_resultBuffer.putBinary(pictureBuffer);
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (pStmtDA.hcolPictureXL != 0)
                {

                    /* get the file name */
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat(pStmtDA.sName);
                    sFileName = sFileName.concat("XL.bmp");
                    picturexlBuffer = mem_get_long_binary_data(sFileName);
                    if (picturexlBuffer != null)
                    {
                            pStmtDA.m_resultBuffer.putLongBinary(picturexlBuffer);
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (pStmtDA.hcolItems != 0)
                   	pStmtDA.m_resultBuffer.putBigInt(pStmtDA.lCurItem);
               m_iNumResRows++;
            }
            catch(BufferOverflowException e)
            {
                pStmtDA.lCurItem--;
                pStmtDA.lItems--;
                pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
                jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
                iRetCode = DAM_SUCCESS_WITH_RESULT_PENDING;
                pStmtDA.m_resultBuffer.clear();
                break;
            }

            bIsFirst = false;
        }

        return iRetCode;

    }
    public int java_build_buffer_rows_dept(MEM_STMT_DA pStmtDA, boolean bFirst)
    {
    	 int iRetCode;
    	 String szName;
    	 boolean bIsFirst = bFirst;

    	while(true)
    	{
    		if (bIsFirst)
            {
                pStmtDA.m_resultBuffer.setNoOfResColumns(pStmtDA.m_iNoOfResColumns);
                pStmtDA.lCurItem = 1;
                pStmtDA.lDeptId = 1;
            }
            else
            {
                pStmtDA.lCurItem++;
                pStmtDA.lDeptId++;
            }
    	  if (pStmtDA.lCurItem > pStmtDA.lItems)
          {
              pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
              iRetCode = jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
              if(DAM_FAILURE == iRetCode)
              {
                  System.out.println("Error Occurred. Error Index " + pStmtDA.m_resultBuffer.getErrorIndex());
                  iRetCode =  IP_FAILURE;
              }
              pStmtDA.m_resultBuffer.clear();
              break;
          }

        szName = "Dept" + pStmtDA.lDeptId;
        try
		{
	        if (pStmtDA.hcolDeptId != 0)
	            pStmtDA.m_resultBuffer.putBigInt(pStmtDA.lDeptId);
	        if (pStmtDA.hcolDname != 0)
	        	pStmtDA.m_resultBuffer.putString(szName);
	        if (pStmtDA.hcolItems != 0)
	        	pStmtDA.m_resultBuffer.putBigInt(pStmtDA.lCurItem);
	        m_iNumResRows++;
		}
        catch(BufferOverflowException e)
        {
            pStmtDA.lCurItem--;
            pStmtDA.lEmpId--;
            pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
            iRetCode = jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
            if(DAM_FAILURE == iRetCode)
            {
                iRetCode =  IP_FAILURE;
            }
            else
                iRetCode = DAM_SUCCESS_WITH_RESULT_PENDING;
            pStmtDA.m_resultBuffer.clear();
            break;
        }
        bIsFirst = false;
        }
    	return iRetCode;
    }

    public int java_build_buffer_rows_emp(MEM_STMT_DA pStmtDA, boolean bFirst)
    {
        int     iRetCode;
        String  szName;
        double  dDoubleVal;
        String    sNumeric = "123456789012345.12345";
        xo_tm   xoTime;
        boolean bIsFirst = bFirst;

        while (true)
        {

            if (bIsFirst)
            {
                pStmtDA.m_resultBuffer.setNoOfResColumns(pStmtDA.m_iNoOfResColumns);
                pStmtDA.lCurItem = 1;
            }
            else
            {
                pStmtDA.lCurItem++;
            }
            if (pStmtDA.lCurItem > pStmtDA.lItems)
            {
                pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
                iRetCode = jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
                if(DAM_FAILURE == iRetCode)
                {
                    System.out.println("Error Occurred. Error Index " + pStmtDA.m_resultBuffer.getErrorIndex());
                    iRetCode =  IP_FAILURE;
                }
                pStmtDA.m_resultBuffer.clear();
                break;
            }

            if(bIsFirst)
            {
                pStmtDA.lDeptId = 1;
            }
            else
            {
                pStmtDA.lEmpId++;
            }

            szName = "Emp" + pStmtDA.lDeptId + "-" + pStmtDA.lEmpId;
            dDoubleVal = 125.25;

            xoTime = new xo_tm();
            if (pStmtDA.hcolNumeric != 0)
            {
                int       iMod;

                iMod = (int)(pStmtDA.lCurItem % 5);
                switch(iMod)
                {
                    case 0:
                        sNumeric = "123456789012345.123"; break;
                    case 1:
                        sNumeric = "1234"; break;
                    case 2:
                        sNumeric = "999888777666555"; break;
                    case 3:
                        sNumeric = "123456789012345"; break;
                    default:
                        sNumeric = "123456789012345.12345"; break;
                }
            }
            if (pStmtDA.hcolDate != 0)
            {
                /*
                 set date to current date
                 xoTime.setVal(xo_tm.DAY_OF_MONTH,pTm.get(Calendar.DAY_OF_MONTH));
                 xoTime.setVal(xo_tm.MONTH,pTm.get(Calendar.MONTH));
                 xoTime.setVal(xo_tm.YEAR,pTm.get(Calendar.YEAR));
                 */

                /* set date to 1999-01-01 */
                xoTime.setVal(xo_tm.DAY_OF_MONTH, 1);
                xoTime.setVal(xo_tm.MONTH, 0);
                xoTime.setVal(xo_tm.YEAR, 1999);
            }

            try
            {
                if (pStmtDA.hcolEmpId != 0)
                    pStmtDA.m_resultBuffer.putBigInt(pStmtDA.lEmpId);
                if (pStmtDA.hcolEname != 0)
                    pStmtDA.m_resultBuffer.putString(szName);
                if (pStmtDA.hcolDeptId != 0)
                    pStmtDA.m_resultBuffer.putBigInt(pStmtDA.lDeptId);
                if (pStmtDA.hcolDate != 0)
                    pStmtDA.m_resultBuffer.putDate(xoTime);
                if (pStmtDA.hcolDouble != 0)
                    pStmtDA.m_resultBuffer.putDouble(dDoubleVal);
                if (pStmtDA.hcolNumeric != 0)
                    pStmtDA.m_resultBuffer.putString(sNumeric);
                if (pStmtDA.hcolItems != 0)
                    pStmtDA.m_resultBuffer.putBigInt(pStmtDA.lCurItem);

                m_iNumResRows++;
            }
            catch(BufferOverflowException e)
            {
                pStmtDA.lCurItem--;
                pStmtDA.lEmpId--;
                pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
                iRetCode = jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
                if(DAM_FAILURE == iRetCode)
                {
                    iRetCode =  IP_FAILURE;
                }
                else
                    iRetCode = DAM_SUCCESS_WITH_RESULT_PENDING;
                pStmtDA.m_resultBuffer.clear();
                break;
            }
            bIsFirst = false;
        }
        return iRetCode;
    }

    public int java_build_buffer_rows_string_table(MEM_STMT_DA pStmtDA, boolean bFirst)
    {
        int     iRetCode;
        String  szName;
        String  sFileName = "";
        StringBuffer pData = new StringBuffer();
        boolean bIsFirst = bFirst;

        while (true)
        {
            if (bIsFirst)
            {
                pStmtDA.m_resultBuffer.setNoOfResColumns(pStmtDA.m_iNoOfResColumns);
                pStmtDA.lCurItem = 1;
            }
            else
            {
                pStmtDA.lCurItem++;
            }
            if (pStmtDA.lCurItem > pStmtDA.lItems)
            {
                pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
                jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
                pStmtDA.m_resultBuffer.clear();
                iRetCode =  IP_SUCCESS; /* End Of Data */
                break;
            }

            try
            {
                szName = pStmtDA.sName + "-" + pStmtDA.lCurItem;
                if (pStmtDA.hcolEname != 0)
                    pStmtDA.m_resultBuffer.putString(szName);
                if (pStmtDA.hcolNote != 0)
                {

                    /* String    sFileName;*/
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat(pStmtDA.sName);
                    sFileName = sFileName.concat("S.txt");

                    if ( mem_get_longchar_data(sFileName,pData) )
                    {
                        pStmtDA.m_resultBuffer.putString(pData.toString());
                        pData.delete(0,pData.length());
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (pStmtDA.hcolComment != 0)
                {

                    /* get the file name */
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat(pStmtDA.sName);
                    sFileName = sFileName.concat("L.txt");

                    if ( mem_get_longchar_data(sFileName,pData) )
                    {
                        pStmtDA.m_resultBuffer.putString(pData.toString());
                        pData.delete(0,pData.length());
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (pStmtDA.hcolCommentXL != 0)
                {

                    /* get the file name */
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat(pStmtDA.sName);
                    sFileName = sFileName.concat("XL.txt");

                    if ( mem_get_longchar_data(sFileName,pData) )
                    {
                    		pStmtDA.m_resultBuffer.putLongString(pData.toString());
                            pData.delete(0,pData.length());
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (pStmtDA.hcolItems != 0)
                    pStmtDA.m_resultBuffer.putBigInt(pStmtDA.lCurItem);
                m_iNumResRows++;
            }
            catch(BufferOverflowException e)
            {
                pStmtDA.lCurItem--;
                pStmtDA.lEmpId--;
                pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
                jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
                iRetCode = DAM_SUCCESS_WITH_RESULT_PENDING;
                pStmtDA.m_resultBuffer.clear();
                break;
            }

            bIsFirst = false;
        }

        return iRetCode;
    }
   public int java_build_buffer_rows_picture_table(MEM_STMT_DA pStmtDA, boolean bFirst)
   {
       long	hcol , hcolName, hcolPicture, hcolComments, hcolWComments;
       long    hCondList;
       long    hcond;
	    long	hrow;
       String  pName;
       String sFileName;
       StringBuffer pData = new StringBuffer();
       byte[]  picturexlBuffer;
	    int		iRetCode = IP_FAILURE;
	    xo_int  ilType =  new xo_int(0);
       xo_int  ilValType = new xo_int(0);
       xo_int  ilValLen = new xo_int(0);
       xo_int  iStatus = new xo_int(0);
       Object  plData;

       jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_build_buffer_rows_picture_table\n");
       hcol = hcolName = hcolPicture = hcolComments = hcolWComments=0;
       /* initialize row count */
       m_iNumResRows = 0;
       /* get the column handles */
       hcol = jdam.dam_getFirstCol(pStmtDA.dam_hstmt, ip.DAM_COL_IN_USE);
       while (hcol != 0)
       {
           StringBuffer    sColName;
           sColName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);

           jdam.dam_describeCol(hcol, null, sColName, null, null);
           pStmtDA.m_iNoOfResColumns++;
           if (sColName.toString().equals("NAME")) hcolName = hcol;
           else if (sColName.toString().equals("PICTURE")) hcolPicture = hcol;
           else if (sColName.toString().equals("COMMENTS")) hcolComments = hcol;
           else if (sColName.toString().equals("WCOMMENTS")) hcolWComments = hcol;
           else return DAM_FAILURE;
           hcol =  jdam.dam_getNextCol(pStmtDA.dam_hstmt);
       }
   	   try
       {
        /* get restrictions on Name field */
        hCondList = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, hcolName);

        /* if no conditions are specified on name, return default "pooh" record */

        if (hCondList == 0)
        {

            pName="pooh";
            if (hcolName != 0)
            {
                pStmtDA.m_resultBuffer.putString(pName);
            }
            if(hcolPicture != 0)
            {
                /* get the file name */
                sFileName = sMemoryWorkingDir;
   	            sFileName = sFileName.concat(pName);
   	            sFileName = sFileName.concat(".bmp");
   	            picturexlBuffer = mem_get_long_binary_data(sFileName);
   	            if (picturexlBuffer != null)
                {
                   pStmtDA.m_resultBuffer.putLongBinary(picturexlBuffer);
                }
                else
               	   pStmtDA.m_resultBuffer.putNull();
	        }
			if (hcolComments != 0)
		    {
                sFileName = sMemoryWorkingDir;
                sFileName = sFileName.concat(pName);
                sFileName = sFileName.concat(".txt");

                if ( mem_get_longchar_data(sFileName,pData) )
                {
                    pStmtDA.m_resultBuffer.putLongString(pData.toString());
                    pData.delete(0,pData.length());
                }
            }
            if (hcolWComments != 0)
            {
                sFileName = sMemoryWorkingDir;
                sFileName = sFileName.concat(pName);
                sFileName = sFileName.concat("W.txt");
                if ( mem_get_longwchar_data(sFileName,pData) )
                {
                    pStmtDA.m_resultBuffer.putLongString(pData.toString());
                    pData.delete(0,pData.length());
                }
            }
        }
        else
        {
            hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hCondList);
            while(hcond != 0 )
            {
                plData = jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, ilType, ilValType, ilValLen, iStatus);
                iRetCode = iStatus.getVal();
                if (iRetCode != DAM_SUCCESS) return iRetCode;
                if (ilType.getVal() != SQL_OP_EQUAL || !(ilValType.getVal() == XO_TYPE_CHAR || ilValType.getVal() == XO_TYPE_VARCHAR))
                return DAM_FAILURE;

                pName = (String)plData;
                if (hcolName != 0)
                {
                    pStmtDA.m_resultBuffer.putString(pName);
                }
                if(hcolPicture != 0)
                {
                    /* get the file name */
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat((String)plData);
                    sFileName = sFileName.concat(".bmp");
                    picturexlBuffer = mem_get_long_binary_data(sFileName);
                    if (picturexlBuffer != null)
                    {
                       pStmtDA.m_resultBuffer.putLongBinary(picturexlBuffer);
                    }
                    else
                       pStmtDA.m_resultBuffer.putNull();
                }
                if (hcolComments != 0)
                {
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat((String)plData);
                    sFileName = sFileName.concat(".txt");

                    if ( mem_get_longchar_data(sFileName,pData) )
                    {
                        pStmtDA.m_resultBuffer.putLongString(pData.toString());
                        pData.delete(0,pData.length());
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (hcolWComments != 0)
                {
                        sFileName = sMemoryWorkingDir;
                        sFileName = sFileName.concat((String)plData);
                        sFileName = sFileName.concat("W.txt");
                        if ( mem_get_longwchar_data(sFileName,pData) )
                        {
                            pStmtDA.m_resultBuffer.putLongString(pData.toString());
                            pData.delete(0,pData.length());
                        }
                        else
                            pStmtDA.m_resultBuffer.putNull();
                }
                    hcond = jdam.dam_getNextCond(pStmtDA.dam_hstmt, hCondList);
            }

            m_iNumResRows++;
            pStmtDA.m_resultBuffer.setNoOfResColumns(pStmtDA.m_iNoOfResColumns);
            pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
            jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
            iRetCode =  IP_SUCCESS; /* End Of Data */
        }
        }
        catch(BufferOverflowException e)
        {
            pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
            jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
            iRetCode = DAM_SUCCESS_WITH_RESULT_PENDING;
        }
        catch(Exception e){
	    }
        return iRetCode;
    }

    public int java_build_buffer_rows_wstring_table(MEM_STMT_DA pStmtDA, boolean bFirst)
    {
        int     iRetCode;
        String  szName;
        String  sFileName = "";
        StringBuffer pData = new StringBuffer();
        boolean bIsFirst = bFirst;

        while (true)
        {
            if (bIsFirst)
            {
                pStmtDA.m_resultBuffer.setNoOfResColumns(pStmtDA.m_iNoOfResColumns);
                pStmtDA.lCurItem = 1;
            }
            else
            {
                pStmtDA.lCurItem++;
            }
            if (pStmtDA.lCurItem > pStmtDA.lItems)
            {
                pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
                jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
                iRetCode =  IP_SUCCESS; /* End Of Data */
                break;
            }

            try
            {
                szName = pStmtDA.sName + "-" + pStmtDA.lCurItem;
                if (pStmtDA.hcolEname != 0)
                    pStmtDA.m_resultBuffer.putString(szName);
                if (pStmtDA.hcolNote != 0)
                {

                    /* String    sFileName;*/
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat(pStmtDA.sName);
                    sFileName = sFileName.concat("SW.txt");

                    if ( mem_get_longwchar_data(sFileName,pData) )
                    {
                        pStmtDA.m_resultBuffer.putString(pData.toString());
                        pData.delete(0,pData.length());
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (pStmtDA.hcolComment != 0)
                {

                    /* get the file name */
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat(pStmtDA.sName);
                    sFileName = sFileName.concat("LW.txt");

                    if ( mem_get_longwchar_data(sFileName,pData) )
                    {
                        pStmtDA.m_resultBuffer.putString(pData.toString());
                        pData.delete(0,pData.length());
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (pStmtDA.hcolCommentXL != 0)
                {

                    /* get the file name */
                    sFileName = sMemoryWorkingDir;
                    sFileName = sFileName.concat(pStmtDA.sName);
                    sFileName = sFileName.concat("XLW.txt");

                    if ( mem_get_longwchar_data(sFileName,pData) )
                    {
                    		pStmtDA.m_resultBuffer.putLongString(pData.toString());
                    		pData.delete(0,pData.length());
                    }
                    else
                        pStmtDA.m_resultBuffer.putNull();
                }
                if (pStmtDA.hcolItems != 0)
                    pStmtDA.m_resultBuffer.putBigInt(pStmtDA.lCurItem);
                m_iNumResRows++;
            }
            catch(BufferOverflowException e)
            {
                pStmtDA.lCurItem--;
                pStmtDA.lEmpId--;
                pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
                jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
                iRetCode = DAM_SUCCESS_WITH_RESULT_PENDING;
                break;
            }

            bIsFirst = false;
        }

        return iRetCode;
    }

    public int java_build_buffer_rows_types_table(MEM_STMT_DA pStmtDA, boolean bFirst)
    {
        long		hcol;
        long            hcolId, hcolChar, hcolNumeric, hcolDecimal, hcolReal, hcolDouble;
        long            hcolDate, hcolTime, hcolTimestamp, hcolVarchar;
        long		hcolBit, hcolTinyint;
        long 		hCondList;
        long 		hcolBigint;
        int		iRetCode = IP_SUCCESS;

        if(bFirst)
            pStmtDA.m_iTypesTableCurItem = 1;
        else
            pStmtDA.m_iTypesTableCurItem++;

        hcolId =  hcolChar = hcolNumeric = hcolDecimal = hcolReal = hcolDouble = hcolDate = hcolTime = hcolTimestamp = hcolVarchar = 0;
        hcolBit = hcolTinyint = hCondList = hcolBigint = 0;
        /* get all the column handles in use */
        pStmtDA.m_iNoOfResColumns = 0;
        hcol = jdam.dam_getFirstCol(pStmtDA.dam_hstmt, ip.DAM_COL_IN_USE);
        while (hcol != 0)
        {
            StringBuffer    sColName;
            sColName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);

            jdam.dam_describeCol(hcol, null, sColName, null, null);
            pStmtDA.m_iNoOfResColumns++;
            if (sColName.toString().equals("ID")) hcolId = hcol;
            else if (sColName.toString().equals("CHAR_VAL")) hcolChar = hcol;
            else if (sColName.toString().equals("NUMERIC_VAL")) hcolNumeric = hcol;
            else if (sColName.toString().equals("DECIMAL_VAL")) hcolDecimal = hcol;
            else if (sColName.toString().equals("REAL_VAL")) hcolReal = hcol;
            else if (sColName.toString().equals("DOUBLE_VAL")) hcolDouble = hcol;
            else if (sColName.toString().equals("DATE_VAL")) hcolDate = hcol;
            else if (sColName.toString().equals("TIME_VAL")) hcolTime = hcol;
            else if (sColName.toString().equals("TIMESTAMP_VAL")) hcolTimestamp = hcol;
            else if (sColName.toString().equals("VARCHAR_VAL")) hcolVarchar = hcol;
            else if (sColName.toString().equals("BIT_VAL")) hcolBit = hcol;
            else if (sColName.toString().equals("TINYINT_VAL")) hcolTinyint = hcol;
            else if (sColName.toString().equals("BIGINT_VAL")) hcolBigint = hcol;
            else return DAM_FAILURE;

            hcol =  jdam.dam_getNextCol(pStmtDA.dam_hstmt);
        }

        try
        {
            int longVal;
            String charValue,numericValue,decimalValue;
            float floatVal;
            double doubleVal;
            xo_tm   tmVal1;
            xo_tm   tmVal2;
            xo_tm   tmVal3;
            String varcharValue;
            boolean bitValue;
            byte tinyintValue;
            long bigIntValue;
            int	i;

            while(true)
            {
                switch (pStmtDA.m_iTypesTableCurItem)
                {
                    case 1:
                        {
                        longVal = 1;
                        charValue = "NTS String.";
                        numericValue = "123456789012";
                        decimalValue = "123456789012";
                        floatVal = (float)123.45;
                        doubleVal = 123.45;
                        tmVal1 = new xo_tm(1999, 0, 1);
                        tmVal2 = new xo_tm(10, 5, 0, 0);
                        tmVal3 = new xo_tm(1999, 0, 1, 10, 5, 0, 0);
                        varcharValue= "OpenAccess Test Data.";
                        bitValue = true;
                        tinyintValue= 16;
                        bigIntValue= 100;
                        break;
                    }
                    case 2:
                        {
                        longVal = 2;
                        charValue = "non NTS.";
                        numericValue = "123456789.50";
                        decimalValue = "123456789.50";
                        floatVal = (float)123.45;
                        doubleVal = 123;
                        tmVal1 = new xo_tm(1999, 1, 20);
                        tmVal2 = new xo_tm(20, 20, 5, 0);
                        tmVal3 = new xo_tm(1999, 1, 20, 10, 10, 9, 0);
                        varcharValue= "New test String.";
                        bitValue = false;
                        tinyintValue= 127;
                        bigIntValue= 1000000000000000000L;
                        break;
                    }
                    case 3:
                        {
                        longVal = 2147483647;
                        charValue = "";
                        for (i=0;i<=2048;i++)
                        {
                            charValue = charValue + 'A';
                        }
                        numericValue = "123456789.50";
                        decimalValue = "123456789.50";
                        floatVal = (float)2147483647.3322;
                        doubleVal = 2147483647.3322;
                        tmVal1 = new xo_tm(1999, 1, 20);
                        tmVal2 = new xo_tm(20, 20, 5, 0);
                        tmVal3 = new xo_tm(1999, 1, 20, 10, 10, 9, 0);
                        varcharValue = "";
                        for (i=0;i<=2048;i++)
                        {
                            varcharValue = varcharValue + 'C';
                        }
                        bitValue = false;
                        tinyintValue= 127;
                        bigIntValue= 1000000000000000000L;
                        break;
                    }
                    default:
                        {
                        if (hcolId != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolChar != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolNumeric != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolDecimal != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolReal != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolDouble != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolDate != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolTime != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolTimestamp != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolVarchar != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolBit != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolTinyint != 0)
                            pStmtDA.m_resultBuffer.putNull();
                        if (hcolBigint != 0)
                            pStmtDA.m_resultBuffer.putNull();

                        m_iNumResRows++;
                        pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
                        pStmtDA.m_resultBuffer.setNoOfResColumns(pStmtDA.m_iNoOfResColumns);
                        jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
                        pStmtDA.m_resultBuffer.clear();
                        return IP_SUCCESS;
                    }
                }
                if (hcolId != 0)
                    pStmtDA.m_resultBuffer.putInt(longVal);
                if (hcolChar != 0)
                    pStmtDA.m_resultBuffer.putString(charValue);
                if (hcolNumeric != 0)
                    pStmtDA.m_resultBuffer.putString(numericValue);
                if (hcolDecimal != 0)
                    pStmtDA.m_resultBuffer.putString(decimalValue);
                if (hcolReal != 0)
                    pStmtDA.m_resultBuffer.putReal(floatVal);
                if (hcolDouble != 0)
                    pStmtDA.m_resultBuffer.putDouble(doubleVal);
                if (hcolDate != 0)
                    pStmtDA.m_resultBuffer.putDate(tmVal1);
                if (hcolTime != 0)
                    pStmtDA.m_resultBuffer.putTime(tmVal2);
                if (hcolTimestamp != 0)
                    pStmtDA.m_resultBuffer.putTimeStamp(tmVal3);
                if (hcolVarchar != 0)
                    pStmtDA.m_resultBuffer.putString(varcharValue);
                if (hcolBit != 0)
                    pStmtDA.m_resultBuffer.putShort((short)(bitValue?1:0));
                if (hcolTinyint != 0)
                    pStmtDA.m_resultBuffer.putShort(tinyintValue);
                if (hcolBigint != 0)
                    pStmtDA.m_resultBuffer.putBigInt(bigIntValue);

                pStmtDA.m_iTypesTableCurItem++;
                m_iNumResRows++;
                continue;
            }
        }
        catch(BufferOverflowException e)
        {
            pStmtDA.m_iTypesTableCurItem--;
            pStmtDA.m_resultBuffer.setNoRowsInBuffer((int)m_iNumResRows);
            pStmtDA.m_resultBuffer.setNoOfResColumns(pStmtDA.m_iNoOfResColumns);
            jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
            iRetCode = DAM_SUCCESS_WITH_RESULT_PENDING;
            pStmtDA.m_resultBuffer.clear();
        }

        return iRetCode;
    }

/********************************************************************************************
    Method:          java_build_row
    Description:     Build a DAM ROW structure from the raw column values
    Return:          handle to the row on success and 0 on failure
*********************************************************************************************/
    public long java_build_row(MEM_STMT_DA pStmtDA, boolean bFirst)
        {
            long    hrow = 0;
            int     iRetCode;
            double  dDoubleVal;
            String  szName;

            if (bFirst) {
                pStmtDA.lCurItem = 1;
                }
            else {
                pStmtDA.lCurItem++;
                }
            if (pStmtDA.lCurItem > pStmtDA.lItems) return 0; /* End Of Data */

            if (pStmtDA.iTable == DEPT_TABLE) {
                if (bFirst) {
                    pStmtDA.lDeptId = 1;
                    }
                else {
                    pStmtDA.lDeptId++;
                    }

                /* allocate a new row */
                hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
				if(hrow==0) return 0;

                szName = "Dept" + pStmtDA.lDeptId;
                if (pStmtDA.hcolDeptId != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolDeptId , pStmtDA.lDeptId, 0);
                if (pStmtDA.hcolDname != 0)
                    iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow,pStmtDA.hcolDname, szName, XO_NTS);
                if (pStmtDA.hcolItems != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolItems, pStmtDA.lCurItem, 0);
                if (bPushDownJoinSupport) mem_build_join_rowset(pStmtDA, hrow);
                }

            if (pStmtDA.iTable == EMP_TABLE) {
                if(bFirst) {
                    pStmtDA.lDeptId = 1;
                    }
                else {
                    pStmtDA.lEmpId++;
                    }

                szName = "Emp" + pStmtDA.lDeptId + "-" + pStmtDA.lEmpId;
                dDoubleVal = 125.25;

                xo_tm   xoTime = new xo_tm();
                /* allocate a new row */
                hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
				if(hrow==0) return 0;
                if (pStmtDA.hcolEmpId != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolEmpId, pStmtDA.lEmpId, 0);
                if (pStmtDA.hcolEname != 0)
                    iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow,pStmtDA.hcolEname, szName, XO_NTS);
                if (pStmtDA.hcolDeptId != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolDeptId , pStmtDA.lDeptId, 0);
                if (pStmtDA.hcolDouble != 0)
                    iRetCode = jdam.dam_addDoubleValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolDouble, dDoubleVal, 0);
                if (pStmtDA.hcolNumeric != 0) {
                    String    sNumeric;
                    int       iMod;

                    iMod = (int)(pStmtDA.lCurItem % 5);
                    switch(iMod) {
                    case 0:
                        sNumeric = "123456789012345.123"; break;
                    case 1:
                        sNumeric = "1234"; break;
                    case 2:
                        sNumeric = "999888777666555"; break;
                    case 3:
                        sNumeric = "123456789012345"; break;
                    default:
                        sNumeric = "123456789012345.12345"; break;
                    }
                    iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolNumeric, sNumeric, XO_NTS);
                }
                if (pStmtDA.hcolItems != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolItems, pStmtDA.lCurItem, 0);
                if (pStmtDA.hcolDate != 0) {
                    /*
                    set date to current date
                    xoTime.setVal(xo_tm.DAY_OF_MONTH,pTm.get(Calendar.DAY_OF_MONTH));
                    xoTime.setVal(xo_tm.MONTH,pTm.get(Calendar.MONTH));
                    xoTime.setVal(xo_tm.YEAR,pTm.get(Calendar.YEAR));
                    */

                    /* set date to 1999-01-01 */
                    xoTime.setVal(xo_tm.DAY_OF_MONTH, 1);
                    xoTime.setVal(xo_tm.MONTH, 0);
                    xoTime.setVal(xo_tm.YEAR, 1999);

                    iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolDate, xoTime, 0);
                }

                /* add results for any scalar columns */
		if(giTestMode == MEM_TEST_MAP_SCALAR_AS_COLUMNS)
                {
                    long    hcol;

                    hcol = jdam.dam_getFirstCol(pStmtDA.dam_hstmt, ip.DAM_COL_IN_USE);
                    while (hcol != 0) {
                        StringBuffer    sColName;
                        StringBuffer    sFuncName;
                        sColName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
                        sFuncName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);

                        jdam.dam_describeCol(hcol, null, sColName, null, null);
                        if (jdam.dam_describeColScalar(hcol, sFuncName) != 0) {
                            sColName.append(sFuncName.toString());
                            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, sColName.toString(), XO_NTS );
                            }

                        hcol =  jdam.dam_getNextCol(pStmtDA.dam_hstmt);
                        }
                }

                if (bPushDownJoinSupport) mem_build_join_rowset(pStmtDA, hrow);
            }
            else if (pStmtDA.iTable == STRING_TABLE) {

		/* allocate a new row */
		hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
		if(hrow==0) return 0;

		szName = pStmtDA.sName + "-" + pStmtDA.lCurItem;
		if (pStmtDA.hcolEname != 0)
		    iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow,pStmtDA.hcolEname, szName, XO_NTS);
		if (pStmtDA.hcolItems != 0)
		    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolItems, pStmtDA.lCurItem, 0);
		if (pStmtDA.hcolNote != 0) {

		    String    sFileName;

		    /* get the file name */
		    sFileName = sMemoryWorkingDir;
		    sFileName = sFileName.concat(pStmtDA.sName);
		    sFileName = sFileName.concat("S.txt");

		    iRetCode = java_mem_add_shortchar_data(pStmtDA, hrow, pStmtDA.hcolNote, sFileName);
		    if (iRetCode != DAM_SUCCESS) return 0;
		    }
		if (pStmtDA.hcolComment != 0) {
		    String    sFileName;

		    /* get the file name */
		    sFileName = sMemoryWorkingDir;
		    sFileName = sFileName.concat(pStmtDA.sName);
		    sFileName = sFileName.concat("L.txt");

		    iRetCode = java_mem_add_shortchar_data(pStmtDA, hrow, pStmtDA.hcolComment, sFileName);
		    if (iRetCode != DAM_SUCCESS) return 0;
		    }
		if (pStmtDA.hcolCommentXL != 0) {
		    String    sFileName;

		    /* get the file name */
		    sFileName = sMemoryWorkingDir;
		    sFileName = sFileName.concat(pStmtDA.sName);
		    sFileName = sFileName.concat("XL.txt");

		    iRetCode = java_mem_add_longchar_data(pStmtDA, hrow, pStmtDA.hcolCommentXL, sFileName);
		    if (iRetCode != DAM_SUCCESS) return 0;
		    }
		}
            else if (pStmtDA.iTable == WSTRING_TABLE) {

		/* allocate a new row */
		hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
		if(hrow==0) return 0;

		szName = pStmtDA.sName + "-" + pStmtDA.lCurItem;
		if (pStmtDA.hcolEname != 0)
		    iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow,pStmtDA.hcolEname, szName, XO_NTS);
		if (pStmtDA.hcolItems != 0)
		    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolItems, pStmtDA.lCurItem, 0);
		if (pStmtDA.hcolNote != 0) {
		    String    sFileName;
		    /* get the file name */
		    /* get the file name */
		    sFileName = sMemoryWorkingDir;
		    sFileName = sFileName.concat(pStmtDA.sName);
		    sFileName = sFileName.concat("SW.txt");

		    iRetCode = java_mem_add_shortwchar_data(pStmtDA, hrow, pStmtDA.hcolNote, sFileName);
		    if (iRetCode != DAM_SUCCESS) return 0;
		    }
		if (pStmtDA.hcolComment != 0) {
		    String    sFileName;

		    /* get the file name */
		    sFileName = sMemoryWorkingDir;
		    sFileName = sFileName.concat(pStmtDA.sName);
		    sFileName = sFileName.concat("LW.txt");

		    iRetCode = java_mem_add_shortwchar_data(pStmtDA, hrow, pStmtDA.hcolComment, sFileName);
		    if (iRetCode != DAM_SUCCESS) return 0;
		    }
		if (pStmtDA.hcolCommentXL != 0) {
		    String    sFileName;

		    /* get the file name */
		    sFileName = sMemoryWorkingDir;
		    sFileName = sFileName.concat(pStmtDA.sName);
		    sFileName = sFileName.concat("XLW.txt");

		    iRetCode = java_mem_add_longwchar_data(pStmtDA, hrow, pStmtDA.hcolCommentXL, sFileName);
		    if (iRetCode != DAM_SUCCESS) return 0;
		    }
	    }
            else if ((pStmtDA.iTable == BINARY_TABLE))
            {
                hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
				if(hrow==0) return 0;
                szName = pStmtDA.sName + "-" + pStmtDA.lCurItem;

                if (pStmtDA.hcolEname != 0)
                    iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolEname, szName, XO_NTS);
                if (pStmtDA.hcolItems != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolItems, pStmtDA.lCurItem, 0);

                String sFileName = sMemoryWorkingDir + pStmtDA.sName + "S.bmp";
                if (pStmtDA.hcolStamp != 0)
                {
                    iRetCode = java_mem_add_short_binary_data(pStmtDA, hrow, pStmtDA.hcolStamp, sFileName);
                    if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE)
                    {
                        return iRetCode;
                    }
                }
                if (pStmtDA.hcolPicture != 0)
                {
                    sFileName = sMemoryWorkingDir + pStmtDA.sName + "L.bmp";
                    iRetCode = java_mem_add_short_binary_data(pStmtDA, hrow, pStmtDA.hcolPicture, sFileName);
                    if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE)
                    {
                        return iRetCode;
                    }
                }
                if (pStmtDA.hcolPictureXL != 0)
                {
                    sFileName = sMemoryWorkingDir + pStmtDA.sName + "XL.bmp";
                    iRetCode = java_mem_add_long_binary_data(pStmtDA, hrow, pStmtDA.hcolPictureXL, sFileName);
                    if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE)
                    {
                        return iRetCode;
                    }
                }
            }
            return hrow;
        }

/********************************************************************************************
    Method:          java_process_row
    Description:     Process the row according to Statement type
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/

    int java_process_row(MEM_STMT_DA pStmtDA, int iStmtType, long hrow)
        {
            switch (iStmtType)
            {
                case DAM_SELECT:
                case DAM_FETCH:
                   int iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
                   if (iRetCode == DAM_ERROR) {
			       jdam.dam_freeRow(hrow);
			       return DAM_FAILURE;
			       }
                    if (iRetCode == DAM_TRUE) {
                    	iRetCode = jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                    	if (iRetCode != DAM_SUCCESS)
                    		return iRetCode;
                        m_iNumResRows++;

                        pStmtDA.iTotalRowCount++;

                        if (m_iNumResRows >= pStmtDA.iFetchSize) {
                            return DAM_SUCCESS_WITH_RESULT_PENDING;
                            }

                        }
                    else {
                        jdam.dam_freeRow(hrow);
                        }
                    break;
                default:
                	jdam.trace(m_tmHandle, UL_TM_F_TRACE, "java_process_row(): Statement type"+ iStmtType +" is out of range\n");
                    return IP_FAILURE;
            }
            return IP_SUCCESS;
        }

/********************************************************************************************
    Method:          java_mem_build_indexed_row
    Description:     Build a DAM ROW structure from the raw column values
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public long java_mem_build_indexed_row(MEM_STMT_DA pStmtDA, long lDeptId, boolean bFirst)
        {
            long    hrow = 0;
            int     iRetCode;
            double  dDoubleVal;
            String  szName;

            if (bFirst) {
                pStmtDA.lCurItem = 1;
                pStmtDA.lDeptId = lDeptId;
                }
            else {
                pStmtDA.lCurItem++;
                }

            if (pStmtDA.lCurItem > pStmtDA.lItems) {
                return 0; /* End Of Data */
                }
            if (pStmtDA.iTable == DEPT_TABLE) {
                /* allocate a new row */
                hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

                szName = "Dept" + pStmtDA.lDeptId;
                if (pStmtDA.hcolDeptId != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolDeptId , pStmtDA.lDeptId, 0);
                if (pStmtDA.hcolDname != 0)
                    iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow,pStmtDA.hcolDname, szName, XO_NTS);
                if (pStmtDA.hcolItems != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolItems, pStmtDA.lCurItem, 0);
                if (bPushDownJoinSupport) mem_build_join_rowset(pStmtDA, hrow);
                }

            else if (pStmtDA.iTable == EMP_TABLE) {
                if(bFirst) {
                    }
                else {
                    pStmtDA.lEmpId++;
                    }

                szName = "Emp" + pStmtDA.lDeptId + "-" + pStmtDA.lEmpId;
                dDoubleVal = 125.25;

                xo_tm   xoTime = new xo_tm();
                /* allocate a new row */
                hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
                if (pStmtDA.hcolEmpId != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolEmpId, pStmtDA.lEmpId, 0);
                if (pStmtDA.hcolEname != 0)
                    iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow,pStmtDA.hcolEname, szName, XO_NTS);
                if (pStmtDA.hcolDeptId != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolDeptId , pStmtDA.lDeptId, 0);
                if (pStmtDA.hcolDouble != 0)
                    iRetCode = jdam.dam_addDoubleValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolDouble, dDoubleVal, 0);
                if (pStmtDA.hcolNumeric != 0) {
                    String    sNumeric;
                    int       iMod;

                    iMod = (int)(pStmtDA.lCurItem % 5);
                    switch(iMod) {
                    case 0:
                        sNumeric = "123456789012345.123"; break;
                    case 1:
                        sNumeric = "1234"; break;
                    case 2:
                        sNumeric = "999888777666555"; break;
                    case 3:
                        sNumeric = "123456789012345"; break;
                    default:
                        sNumeric = "123456789012345.12345"; break;
                        }
                    iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolNumeric, sNumeric, XO_NTS);
                    }
                if (pStmtDA.hcolItems != 0)
                    iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolItems, pStmtDA.lCurItem, 0);
                if (pStmtDA.hcolDate != 0) {
                    /*
                    set date to current date
                    xoTime.setVal(xo_tm.DAY_OF_MONTH,pTm.get(Calendar.DAY_OF_MONTH));
                    xoTime.setVal(xo_tm.MONTH,pTm.get(Calendar.MONTH));
                    xoTime.setVal(xo_tm.YEAR,pTm.get(Calendar.YEAR));
                    */

                    /* set date to 1999-01-01 */
                    xoTime.setVal(xo_tm.DAY_OF_MONTH, 1);
                    xoTime.setVal(xo_tm.MONTH, 0);
                    xoTime.setVal(xo_tm.YEAR, 1999);

                    iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolDate, xoTime, 0);
                    }
                if (bPushDownJoinSupport) mem_build_join_rowset(pStmtDA, hrow);
                }
            else
                return 0;

            return hrow;
        }

/********************************************************************************************
    Method:          java_optimize_exec
    Description:     This function is called to do optimized execution
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/

    int java_optimize_exec(MEM_STMT_DA pStmtDA, int iStmtType, xo_long piNumResRows)
        {
            long hrow;           /* DAM_HROW */
            int iRetCode;
            boolean bFirstItem = false;

            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_optimize_exec called\n");

            m_iNumResRows = 0;
            piNumResRows.setVal(0);
//            xo_int iLeftXoType = new xo_int(0);
            xo_int iLeftOp = new xo_int(0);
            xo_int iLeftValType = new xo_int(0);
            xo_int iLeftValLen = new xo_int(0);
            xo_int iStatus = new xo_int(0);
            Object pLeftData;

            bFirstItem = (iStmtType == DAM_SELECT) ? true : false;
            while(pStmtDA.hSearchCondList != 0) {

                    /* get the conditions on index columns */
                    pStmtDA.hSearchCond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, pStmtDA.hSearchCondList);

                    /* get details of the condition.  */
                    pLeftData = jdam.dam_describeCondEx(pStmtDA.dam_hstmt, pStmtDA.hSearchCond, DAM_COND_PART_LEFT, iLeftOp,iLeftValType, iLeftValLen, iStatus);
                    iRetCode = iStatus.getVal();
                    if (iRetCode != DAM_SUCCESS) return IP_FAILURE; /* return on error */

                    if (iLeftValType.getVal() != XO_TYPE_BIGINT)
                    {
                    	jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, "Table " + pStmtDA.sTableName + " Invalid column data type.");
                        jdam.trace(m_tmHandle, UL_TM_ERRORS, "java_optimize_exec(): Table " + pStmtDA.sTableName + " invalid column data type.\n");
                        return IP_FAILURE;
                    }

                    Long lData = (Long)pLeftData;


                    /* build result rows that match current index condition */
                        switch(iLeftOp.getVal()){
                            case SQL_OP_EQUAL:
                                /* allocate and build the row */
                                hrow = java_mem_build_indexed_row(pStmtDA, lData.longValue(), bFirstItem);
				     			if(hrow==0 && pStmtDA.lCurItem <= pStmtDA.lItems) return 0;

                                while(hrow != 0) {
                                    /* process the row */
                                    iRetCode = java_process_row(pStmtDA, iStmtType, hrow);

                                    /* check if MAX ROWS are processed */
                                    if (pStmtDA.iMaxRows > 0 && pStmtDA.iTotalRowCount >= pStmtDA.iMaxRows) {
			                        piNumResRows.setVal(m_iNumResRows);
			                        return IP_SUCCESS;
			                        }

				    /* check if TOP rows are processed */
                                    if (pStmtDA.iTopRows > 0 && pStmtDA.iTotalRowCount >= pStmtDA.iTopRows) {
                                        piNumResRows.setVal(m_iNumResRows);
                                        return IP_SUCCESS;
                                        }
                                    if (iRetCode != IP_SUCCESS) return iRetCode; /* return on error */

                                    /* read next row from the memory database */
                                    hrow = java_mem_build_indexed_row(pStmtDA, lData.longValue(), false);
                                    }
                                    break;

                            case SQL_OP_NOT:
                            case SQL_OP_SMALLER:
                            case SQL_OP_GREATER:
                            case SQL_OP_LIKE:
                            case SQL_OP_ISNULL:
                            default:
                                jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, "Invalid operator type "+ iLeftOp.getVal());
                                jdam.trace(m_tmHandle, UL_TM_ERRORS, "java_optimize_exec(): Invalid operator type "+ iLeftOp.getVal() + "\n");
                                break;

                            }/* end switch */

                /* Try next condition. */
                pStmtDA.hSearchCond = jdam.dam_getNextCond(pStmtDA.dam_hstmt, pStmtDA.hSearchCondList);
                if(pStmtDA.hSearchCond != 0) {
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, "Table " + pStmtDA.sTableName + " does not support multicolumn index.");
                    jdam.trace(m_tmHandle, UL_TM_ERRORS, "java_optimize_exec(): Table " + pStmtDA.sTableName + " does not support multicolumn index.\n");
                    return IP_FAILURE;
                }

                pStmtDA.hSearchCondList = jdam.dam_getNextCondList(pStmtDA.hset_of_condlist.getVal());
                bFirstItem = true;
                iRetCode = java_mem_reinit_stmt(pStmtDA);
                if (iRetCode != IP_SUCCESS) return iRetCode;

                }
            piNumResRows.setVal(m_iNumResRows);
            return IP_SUCCESS;
    }

/********************************************************************************************
    Method:          java_mem_reinit_stmt
    Description:     Initialize IP variables
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/

    int java_mem_reinit_stmt(MEM_STMT_DA pStmtDA)
        {
            long hcur_condlist;  /* DAM_HCONDLIST */
            long hcond;          /* DAM_HCOND */
            int iRetCode;
            long hcolEmpId;

            xo_int iLeftOp, iLeftXoType, iLeftValLen, iStatus;
            Long pLeftData = null;

            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_reinit_stmt called\n");

            iLeftOp = new xo_int(0);
            iLeftXoType = new xo_int(0);
            iLeftValLen = new xo_int(0);
            iStatus = new xo_int(0);

            /* get the restrictions on EmpId */
            if (pStmtDA.iTable == EMP_TABLE) {
                hcolEmpId = jdam.dam_getCol(pStmtDA.dam_hstmt, "EMPID");
                hcur_condlist = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, hcolEmpId);
                if (hcur_condlist == 0)
                    pStmtDA.lEmpId = 1;
                else {

                    /* get the condition details */
                    hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hcur_condlist);
                    pLeftData = (Long)jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
                    iRetCode = iStatus.getVal();
                    if (iRetCode != DAM_SUCCESS) return iRetCode;
                    if (iLeftOp.getVal() != SQL_OP_EQUAL || iLeftXoType.getVal() != XO_TYPE_BIGINT)
                        return DAM_FAILURE;
                    /* check if condition value is NULL */
                    if (pLeftData.longValue()== 0)
                        pStmtDA.lEmpId =0;
                    else
                        pStmtDA.lEmpId = pLeftData;

                if (jdam.dam_getNextCond(pStmtDA.dam_hstmt, hcur_condlist) != 0) {
                    jdam.trace(m_tmHandle, UL_TM_ERRORS,"Driver does not support more than one condition on the ITEMS column\n");
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, "Driver does not support more than one condition on the ITEMS column");
                    return IP_FAILURE;
                    }
                }
            }
            return IP_SUCCESS;
    }

/************************************************************************
Function:       mem_insert_row()
Description:    Insert the given row
Return:         IP_SUCCESS on Success
                IP_FAILURE   on Failure
************************************************************************/
int             mem_insert_row(MEM_STMT_DA pStmtDA)
{
    long        hrow;
    long        hRowElem;
    long        hcol;
    int         iColNum;
    Object      pVal;
    xo_int      piStatus, piXOType;
//    String      sData;
//    int         iRetCode;


    piStatus = new xo_int();
        piXOType = new xo_int();

    if (pStmtDA.iType != DAM_INSERT)
        return IP_FAILURE;

    hrow = jdam.dam_getFirstInsertRow(pStmtDA.dam_hstmt);

    /* get the values to be set */
    /* get all the columns that need to be updated */
    hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
    iColNum = 0;
        while (hRowElem != 0) {
            piStatus = new xo_int(1);
            hcol = jdam.dam_getColToSet(hRowElem);
            jdam. dam_describeCol(hcol, null, null, piXOType, null);
            iColNum++; /* Codebase Columns start at 1. DAM columns satrt at 0 */

            pVal = jdam.dam_getValueToSet(hRowElem, piXOType.getVal(), piStatus);
            if (piStatus.getVal() == DAM_FAILURE) return IP_FAILURE;

            hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
            }

    return IP_SUCCESS;
}

/********************************************************************************************
    Method:          java_mem_exec_picture_table
    Description:     This function is called to execute query on EMP_TABLE (Picture table)

    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
public int java_mem_exec_picture_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
{
	    long	hcol, hcolName, hcolPicture, hcolComments, hcolWComments; // , hcolKids;
        long    hCondList;
        long    hcond;
	    long	hrow;
        xo_int  ilType =  new xo_int(0);
        xo_int  ilValType = new xo_int(0);
        xo_int  ilValLen = new xo_int(0);
        xo_int  iStatus = new xo_int(0);
        Object  plData;
	    int		iRetCode;

        jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_picture_table called\n");

        /* initialize row count */
        m_iNumResRows = 0;

        /* get the column handles */
        hcolName        = jdam.dam_getCol(pStmtDA.dam_hstmt, "NAME");
/*        hcolKids      = jdam.dam_getCol(pStmtDA.dam_hstmt, "KIDS"); */
        hcolPicture     = jdam.dam_getCol(pStmtDA.dam_hstmt, "PICTURE");
        hcolComments    = jdam.dam_getCol(pStmtDA.dam_hstmt, "COMMENTS");
        hcolWComments   = jdam.dam_getCol(pStmtDA.dam_hstmt, "WCOMMENTS");
	    if (hcolName == 0 || hcolPicture == 0 || hcolComments == 0 || hcolWComments == 0)
		    return IP_FAILURE;

	if (pStmtDA.iType == DAM_SELECT || pStmtDA.iType == DAM_UPDATE) {
		/* get restrictions on Name field */
		hCondList = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, hcolName);

        /* if no conditions are specified on name, return default "pooh" record */
		if (hCondList == 0)
        {
            String pName="pooh";
			/* prepare a row for the given employee */
			hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
            ilValType.setVal(XO_TYPE_CHAR);
            plData=pName;
			jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolName,pName, XO_NTS );

            iRetCode = DAM_SUCCESS;

/*			iRetCode = mem_add_array_data(pStmtDA, hrow, hcolKids, plData);
			if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE) {
				return iRetCode;
				}
*/
            if(hcolPicture != 0)
            {
                String    sFileName;

                /* get the file name */
                sFileName = sMemoryWorkingDir;
                sFileName = sFileName.concat((String)plData);
                sFileName = sFileName.concat(".bmp");

                iRetCode = java_mem_add_long_binary_data(pStmtDA, hrow, hcolPicture, sFileName);
                if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE) {
                    return iRetCode;
                    }
            }

            if(hcolComments != 0)
            {
                String    sFileName;

                /* get the file name */
                sFileName = sMemoryWorkingDir;
                sFileName = sFileName.concat((String)plData);
                sFileName = sFileName.concat(".txt");

                iRetCode = java_mem_add_longchar_data(pStmtDA, hrow, hcolComments, sFileName);
                if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE) {
                    return iRetCode;
                    }
            }

            if(hcolWComments != 0)
            {
                String    sFileName;

                /* get the file name */
                sFileName = sMemoryWorkingDir;
                sFileName = sFileName.concat((String)plData);
                sFileName = sFileName.concat("W.txt");

                iRetCode = java_mem_add_longwchar_data(pStmtDA, hrow, hcolWComments, sFileName);
                if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE) {
                    return iRetCode;
                    }
            }

            if (iRetCode == DAM_SUCCESS) {
                /* check and add row to result */
                iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
                if (iRetCode == DAM_ERROR) {
                jdam.dam_freeRow(hrow);
		        return DAM_FAILURE;
		        }
                if (iRetCode == DAM_TRUE) {
                    if (pStmtDA.iType == DAM_UPDATE)
                        return mem_update_picture_table(pStmtDA, piNumResRows, hrow, (String) plData);
                    iRetCode = jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                    if (iRetCode != DAM_SUCCESS) return iRetCode;
                    }
                else
                    jdam.dam_freeRow(hrow);
                }
            return IP_SUCCESS;

		}

		/* process each of the conditions */
		hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hCondList);
		while (hcond != 0)
        {
			plData = jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, ilType, ilValType, ilValLen, iStatus);
			iRetCode = iStatus.getVal();
			if (iRetCode != DAM_SUCCESS) return iRetCode;
			if (ilType.getVal() != SQL_OP_EQUAL || !(ilValType.getVal() == XO_TYPE_CHAR || ilValType.getVal() == XO_TYPE_VARCHAR))
				return DAM_FAILURE;


			/* prepare a row for the given employee */
            iRetCode = DAM_SUCCESS;

			hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
			jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolName, (String)plData, XO_NTS );

/*			iRetCode = mem_add_array_data(pStmtDA, hrow, hcolKids, plData);
			if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE) {
				return iRetCode;
				}
*/

            if(hcolPicture != 0)
            {
                String    sFileName;

    		    /* get the file name */
    		    sFileName = sMemoryWorkingDir;
    		    sFileName = sFileName.concat((String)plData);
    		    sFileName = sFileName.concat(".bmp");

			    iRetCode = java_mem_add_long_binary_data(pStmtDA, hrow, hcolPicture, sFileName);
			    if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE) {
				    return iRetCode;
				    }
            }

            if(hcolComments != 0)
            {
                String    sFileName;

    		    /* get the file name */
    		    sFileName = sMemoryWorkingDir;
    		    sFileName = sFileName.concat((String)plData);
    		    sFileName = sFileName.concat(".txt");

			    iRetCode = java_mem_add_longchar_data(pStmtDA, hrow, hcolComments, sFileName);
			    if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE) {
				    return iRetCode;
				    }
            }

            if(hcolWComments != 0)
            {
                String    sFileName;

    		    /* get the file name */
    		    sFileName = sMemoryWorkingDir;
    		    sFileName = sFileName.concat((String)plData);
    		    sFileName = sFileName.concat("W.txt");

			    iRetCode = java_mem_add_longwchar_data(pStmtDA, hrow, hcolWComments, sFileName);
			    if (iRetCode != DAM_SUCCESS && iRetCode != DAM_NOT_AVAILABLE) {
				    return iRetCode;
				    }
            }

			if (iRetCode == DAM_SUCCESS) {
				/* check and add row to result */
                int iRc = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
			        if (iRc == DAM_ERROR) {
				    jdam.dam_freeRow(hrow);
				    return DAM_FAILURE;
				}
				if (iRc == DAM_TRUE) {
				    if (pStmtDA.iType == DAM_UPDATE)
					return mem_update_picture_table(pStmtDA, piNumResRows, hrow, (String) plData);
					jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
				}
				else{
				    jdam.dam_freeRow(hrow);
				}
			}

			hcond = jdam.dam_getNextCond(pStmtDA.dam_hstmt, hCondList);
			}
		} /* SELECT or UPDATE */

	else if (pStmtDA.iType == DAM_INSERT) {
	    long   hRowElem, hRowElemName, hRowElemPicture, hRowElemComments, hRowElemWComments;
		xo_int	 iColNum, iColNumName, iColNumPicture, iColNumComments, iColNumWComments;
		Object		pName;
        xo_int      piStatus = new xo_int(0);

         iColNum            = new xo_int(0);
         iColNumName        = new xo_int(0);
         iColNumPicture     = new xo_int(0);
         iColNumComments    = new xo_int(0);
         iColNumWComments   = new xo_int(0);

		/* process each of the insert rows
          for each row to be inserted, get the column values and insert
		*/
        jdam.dam_describeCol(hcolName, iColNumName, null, null, null);
        jdam.dam_describeCol(hcolPicture, iColNumPicture, null, null, null);
        jdam.dam_describeCol(hcolComments, iColNumComments, null, null, null);
        jdam.dam_describeCol(hcolWComments, iColNumWComments, null, null, null);
        hrow = jdam.dam_getFirstInsertRow(pStmtDA.dam_hstmt);
        while (hrow != 0)
        {
            /* insert the row */

			hRowElemName = hRowElemPicture = hRowElemComments = hRowElemWComments = 0;
		    hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
			while (hRowElem != 0)
            {
				hcol = jdam.dam_getColToSet(hRowElem);
		        jdam.dam_describeCol(hcol, iColNum, null, null, null);
				if (iColNum.getVal() == iColNumName.getVal())
					hRowElemName = hRowElem;
				if (iColNum.getVal() == iColNumPicture.getVal())
					hRowElemPicture = hRowElem;
				if (iColNum.getVal() == iColNumComments.getVal())
					hRowElemComments = hRowElem;
				if (iColNum.getVal() == iColNumWComments.getVal())
					hRowElemWComments = hRowElem;

		        hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
		    }

			if (hRowElemName == 0)
				return IP_FAILURE;

            if (hRowElemPicture != 0)
            {
                String      sFileName;

                /* get the name */
                pName = jdam.dam_getValueToSet(hRowElemName, XO_TYPE_CHAR,piStatus);
                iRetCode = piStatus.getVal();
                if (iRetCode != DAM_SUCCESS) {
                    return iRetCode;
                    }

                /* save the picture */
                sFileName = sMemoryWorkingDir;
                sFileName = sFileName.concat((String)pName);
                sFileName = sFileName.concat(".bmp");

                iRetCode = mem_get_binary_data(pStmtDA, hRowElemPicture, sFileName, XO_TYPE_VARBINARY);
            }

            if (hRowElemComments != 0)
            {
                String      sFileName;

                /* get the name */
                pName = jdam.dam_getValueToSet(hRowElemName, XO_TYPE_CHAR,piStatus);
                iRetCode = piStatus.getVal();
                if (iRetCode != DAM_SUCCESS) {
                    return iRetCode;
                    }

                /* save the picture */
                sFileName = sMemoryWorkingDir;
                sFileName = sFileName.concat((String)pName);
                sFileName = sFileName.concat(".txt");

                /* save the comments */
                iRetCode = mem_get_longchar_data(pStmtDA, hRowElemComments, sFileName);
                }

            if (hRowElemWComments != 0) {

                String      sFileName;

                /* get the name */
                pName = jdam.dam_getValueToSet(hRowElemName, XO_TYPE_CHAR, piStatus);
                iRetCode = piStatus.getVal();
                if (iRetCode != DAM_SUCCESS) {
                    return iRetCode;
                    }

                 /* save the picture */
                sFileName = sMemoryWorkingDir;
                sFileName = sFileName.concat((String)pName);
                sFileName = sFileName.concat("W.txt");

                /* save the comments */
                iRetCode = mem_get_longwchar_data(pStmtDA, hRowElemWComments, sFileName);
                }

            m_iNumResRows++;
            hrow = jdam.dam_getNextInsertRow(pStmtDA.dam_hstmt);
          } /* Loop */
		} /* INSERT */
	else if (pStmtDA.iType == DAM_DELETE) {
        /* nothing to delete */
        }
	else
		return IP_FAILURE;

    piNumResRows.setVal(m_iNumResRows);
    return IP_SUCCESS;
}

/********************************************************************************************
Method:          java_mem_exec_binary_table
Description:     This function is called to execute query on BINARY_TABLE

Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
public int java_mem_exec_binary_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
{
	    long hcol, hcolName, hcolStamp, hcolPicture, hcolPictureXL, hcolItems;
		long		hrow;
		int				iRetCode;
	    xo_int piStatus = new xo_int();

        /* initialize row count */
        m_iNumResRows = 0;

	    /* get the column handles */
	    hcolName = jdam.dam_getCol(pStmtDA.dam_hstmt, "ENAME");
	    hcolStamp = jdam.dam_getCol(pStmtDA.dam_hstmt, "STAMP");
	    hcolPicture = jdam.dam_getCol(pStmtDA.dam_hstmt, "PICTURE");
	    hcolPictureXL = jdam.dam_getCol(pStmtDA.dam_hstmt, "PICTUREXL");
	    hcolItems = jdam.dam_getCol(pStmtDA.dam_hstmt, "ITEMS");

		/* If option IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE is enabled, handles are available only
		   for the columns used in the query.And if the query does not contain the "items" column,
		   handle for "items" column will be NULL. In such cases set the rowcount to 1 */

		if(!gbMemValidateSchemaObjectInUse) {
		   if ( (hcolName == 0) || (hcolStamp == 0) || (hcolPicture == 0) || (hcolPicture == 0) || (hcolPictureXL == 0) || hcolItems == 0) {
			   return DAM_FAILURE;
		   }
		}

	   if (pStmtDA.iType == DAM_INSERT)
	    {
		    long    hRowElem, hRowElemName, hRowElemStamp, hRowElemPicture, hRowElemPictureXL;
			xo_int				iColNum, iColNumName, iColNumStamp, iColNumPicture, iColNumPictureXL;

			iColNum = new xo_int(0);
			iColNumName = new xo_int(0);
			iColNumStamp = new xo_int(0);
			iColNumPicture = new xo_int(0);
			iColNumPictureXL = new xo_int(0);

			/* process each of the insert rows
	          for each row to be inserted, get the column values and insert
			*/
	        jdam.dam_describeCol(hcolName, iColNumName, null, null, null);
	        jdam.dam_describeCol(hcolStamp, iColNumStamp, null, null, null);
	        jdam.dam_describeCol(hcolPicture, iColNumPicture, null, null, null);
	        jdam.dam_describeCol(hcolPictureXL, iColNumPictureXL, null, null, null);
	        hrow = jdam.dam_getFirstInsertRow(pStmtDA.dam_hstmt);
			hRowElemName = 0;
			hRowElemStamp = 0;
			hRowElemPicture = 0;
			hRowElemPictureXL = 0;
	        while (hrow != 0) {
	            /* insert the row */

				hRowElemName = hRowElemStamp = hRowElemPicture = 0;
			    hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
				while (hRowElem != 0) {
					hcol = jdam.dam_getColToSet(hRowElem);
			        jdam.dam_describeCol(hcol, iColNum, null, null, null);
					if (iColNum.getVal() == iColNumName.getVal())
						hRowElemName = hRowElem;
					if (iColNum.getVal() == iColNumStamp.getVal())
						hRowElemStamp = hRowElem;
					if (iColNum.getVal() == iColNumPicture.getVal())
						hRowElemPicture = hRowElem;
					if (iColNum.getVal() == iColNumPictureXL.getVal())
						hRowElemPictureXL = hRowElem;

			        hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
					}

				if (hRowElemName == 0)
					return DAM_FAILURE;

				if (hRowElemStamp != 0) {
					Object		pName;
	                String sFileName;

					/* get the name */
			        pName = jdam.dam_getValueToSet(hRowElemName, XO_TYPE_VARCHAR, piStatus);
	                iRetCode = piStatus.getVal();
					if (iRetCode != DAM_SUCCESS) {
						return iRetCode;
						}

					/* save the picture */
	                sFileName = sMemoryWorkingDir;
	                sFileName = sFileName.concat((String)pName);
	                sFileName = sFileName.concat("S.bmp");
	                iRetCode = mem_get_binary_data(pStmtDA,hRowElemStamp,sFileName, XO_TYPE_BINARY);
	   				if (iRetCode != DAM_SUCCESS) return iRetCode;
					}

				if (hRowElemPicture != 0) {
					Object		pName;
	                String sFileName;

					/* get the name */
			        pName = jdam.dam_getValueToSet(hRowElemName, XO_TYPE_VARCHAR, piStatus);
	                iRetCode = piStatus.getVal();
					if (iRetCode != DAM_SUCCESS) {
						return iRetCode;
						}

					/* save the picture */
	                sFileName = sMemoryWorkingDir;
	                sFileName = sFileName.concat((String)pName);
	                sFileName = sFileName.concat("L.bmp");
	                iRetCode = mem_get_binary_data(pStmtDA, hRowElemPicture, sFileName, XO_TYPE_VARBINARY);
	                if (iRetCode != DAM_SUCCESS) return iRetCode;
					}

				if (hRowElemPictureXL != 0) {
					Object		pName;
	                String sFileName;

					/* get the name */
			        pName = jdam.dam_getValueToSet(hRowElemName, XO_TYPE_VARCHAR, piStatus);
	                iRetCode = piStatus.getVal();
					if (iRetCode != DAM_SUCCESS) {
						return iRetCode;
						}

					/* save the picture */
	                sFileName = sMemoryWorkingDir;
	                sFileName = sFileName.concat((String)pName);
	                sFileName = sFileName.concat("XL.bmp");
	                iRetCode = mem_get_binary_data(pStmtDA, hRowElemPictureXL, sFileName, XO_TYPE_LONGVARBINARY);
	                if (iRetCode != DAM_SUCCESS) return iRetCode;
					}

				m_iNumResRows++;
	            hrow = jdam.dam_getNextInsertRow(pStmtDA.dam_hstmt);
	            }
			}
			else if (pStmtDA.iType == DAM_DELETE) {
		        /* nothing to delete */
			return DAM_FAILURE;
			}

	piNumResRows.setVal(m_iNumResRows);
	return DAM_SUCCESS;
}

/********************************************************************************************
    Method:          java_mem_exec_sample_table
    Description:    Process queries on Sample Table
                Supports PUSEDO ROWID and ROWVER fields.
                select rowid, name, emp_age, rowver from sample_table;

                Support reading last inserted record:
                "select * from sample_table where rowid is null"

    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int java_mem_exec_sample_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
        {
        long            hcol, hcolRowid, hcolName, hcolAge, hcolRowver;
        long            hCondList;
        long            hcond;
        long            hrow;
        int             iRowid;
        int             iRetCode;
        String          sBuf = "";

        jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_sample_table called\n");

        /* initialize row count */
        m_iNumResRows = 0;

        /* get the column handles */
        hcolRowid = jdam.dam_getCol(pStmtDA.dam_hstmt, "ROWID");
        hcolName = jdam.dam_getCol(pStmtDA.dam_hstmt, "NAME");
        hcolAge = jdam.dam_getCol(pStmtDA.dam_hstmt, "EMP_AGE");
        hcolRowver = jdam.dam_getCol(pStmtDA.dam_hstmt, "Rowver");

		/* If option IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE is enabled, handles are available only
   		   for the columns used in the query.And if the query does not contain the "items" column,
		   handle for "items" column will be NULL. In such cases set the rowcount to 1 */

		if(!gbMemValidateSchemaObjectInUse) {
	        if (hcolRowid == 0 || hcolName == 0 || hcolAge == 0 || hcolRowver == 0) {
    	        return DAM_FAILURE;
        	    }
		}
        if (pStmtDA.iType == DAM_SELECT || pStmtDA.iType == DAM_UPDATE || pStmtDA.iType == DAM_DELETE) {
            /* get restrictions on Rowid field */
            hCondList = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, hcolRowid);
            if (hCondList == 0) { /* return all records */

                for (iRowid = 0; iRowid < MAX_SAMPLE_ENTRY; iRowid++) {
                    if (pSampleDb[iRowid].iRowVer > 0) {
                        /* prepare the row */
                        hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
                        jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolRowid, iRowid, 0);
                        jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolName, pSampleDb[iRowid].sName, XO_NTS );
                        jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolAge, pSampleDb[iRowid].iAge, 0);
                        jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolRowver, pSampleDb[iRowid].iRowVer, 0 );

                        /* check and process the row */
                        iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
                        if (iRetCode == DAM_ERROR) {
			            jdam.dam_freeRow(hrow);
                        return DAM_FAILURE;
			            }
			            if (iRetCode == DAM_TRUE){
                            mem_process_sample_row(pStmtDA, hrow, iRowid);
                        }else{
			                jdam.dam_freeRow(hrow);
                        }
                        }
                    }
                }
            else {
                xo_int iLeftOp, iLeftXoType, iLeftValLen, iStatus;
                Integer pLeftData = null;

                iLeftOp = new xo_int(0);
                iLeftXoType = new xo_int(0);
                iLeftValLen = new xo_int(0);
                iStatus = new xo_int(0);

                /* process each of the conditions */
                hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hCondList);
                while (hcond != 0) {
                    pLeftData = (Integer)jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
                    iRetCode = iStatus.getVal();
                    if (iRetCode != IP_SUCCESS) return iRetCode;
                    if (iLeftOp.getVal() == SQL_OP_ISNULL) {

                        /* return last inserted record */
                        iRowid = giLastInsertedRowId;

                        /* prepare the row */
                        hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
                        jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolRowid, iRowid, 0);
                        jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolName, pSampleDb[iRowid].sName, XO_NTS );
                        jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolAge, pSampleDb[iRowid].iAge, 0);
                        jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolRowver, pSampleDb[iRowid].iRowVer, 0 );

                        /* check and add the row */
                        mem_process_sample_row(pStmtDA, hrow, iRowid);
                        piNumResRows.setVal(m_iNumResRows);
                        return DAM_SUCCESS;
                        }
                    if (iLeftOp.getVal() != SQL_OP_EQUAL || iLeftXoType.getVal() != XO_TYPE_INTEGER)
                        return DAM_FAILURE;

                    /* check if rowid is valid */
                    iRowid = pLeftData.intValue();

                    if (iRowid >= 0 && iRowid < MAX_SAMPLE_ENTRY &&
                            pSampleDb[iRowid].iRowVer > 0) {
                        /* prepare the row */
                        hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
                        jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolRowid, iRowid, 0);
                        jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolName, pSampleDb[iRowid].sName, XO_NTS );
                        jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolAge, pSampleDb[iRowid].iAge, 0);
                        jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolRowver, pSampleDb[iRowid].iRowVer, 0 );

                        /* check and add the row */
			            iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
                        if (iRetCode == DAM_ERROR) {
			                jdam.dam_freeRow(hrow);
                            return DAM_FAILURE;
			            }
                        if (iRetCode == DAM_TRUE)
                            mem_process_sample_row(pStmtDA, hrow, iRowid);
            			else
                           jdam.dam_freeRow(hrow);

                    hcond = jdam.dam_getNextCond(pStmtDA.dam_hstmt, hCondList);
                    }
                }
            } /* SELECT */
	    }
        else if (pStmtDA.iType == DAM_INSERT) {
            long        hRowElem, hRowElemName, hRowElemAge;
            xo_int      piStatus;

            /* process each of the insert rows
              for each row to be inserted, get the column values and insert
            */
            piStatus = new xo_int(1);
            hrow = jdam.dam_getFirstInsertRow(pStmtDA.dam_hstmt);
            while (hrow != 0) {
                /* insert the row */

                hRowElemName = hRowElemAge = 0;
                hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
                while (hRowElem != 0) {
                    hcol = jdam.dam_getColToSet(hRowElem);
                    if (jdam.dam_compareCol(hcol, hcolName) == 1)
                        hRowElemName = hRowElem;
                    else if (jdam.dam_compareCol(hcol, hcolAge) == 1)
                        hRowElemAge = hRowElem;
                    else {
                        /* For psuedo columns, if user specifies NULL values, IP should ignore and not report error */
                        StringBuffer    sColName;
                        String          pVal;

                        /* get the column name */
                        sColName = new StringBuffer();
                        sColName.delete(0, sColName.length());
                        jdam.dam_describeCol(hcol, null, sColName, null, null);

                        pVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, piStatus);
                        if (piStatus.getVal() == DAM_FAILURE) return IP_FAILURE;
                        if (pVal != null) {
                            sBuf = "Non-NULL values are being inserted for Psuedo column:" + sColName.toString();
                            jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, sBuf);
                            jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                            return DAM_FAILURE;
                            }
                        }

                    hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
                    }

                if (hRowElemName == 0 || hRowElemAge == 0)
                    return DAM_FAILURE;

                /* get the next available rowid */
                iRowid = 0;
                while (iRowid < MAX_SAMPLE_ENTRY) {
                    if (pSampleDb[iRowid].iRowVer <= 0)
                        break;
                    iRowid++;
                    }
                if (iRowid >= MAX_SAMPLE_ENTRY) {
                    sBuf = "The max allowed records in SAMPLE_TABLE of " + MAX_SAMPLE_ENTRY + " has exceeded";
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, sBuf);
                    jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                    return DAM_FAILURE;
                    }

                /* add the record */
                {
                    String      pName;
                    Integer     piAge;

                    /* get the name & age */
                    pName = (String) jdam.dam_getValueToSet(hRowElemName, XO_TYPE_CHAR, piStatus);
                    if (piStatus.getVal() == DAM_FAILURE) return IP_FAILURE;

                    piAge = (Integer) jdam.dam_getValueToSet(hRowElemAge, XO_TYPE_INTEGER, piStatus);
                    if (piStatus.getVal() == DAM_FAILURE) return IP_FAILURE;

                    pSampleDb[iRowid].sName = pName;
                    pSampleDb[iRowid].iAge = piAge.intValue();
                    pSampleDb[iRowid].iRowVer = 1; /* initialize the rowver */

                    giLastInsertedRowId = iRowid;
                }

                m_iNumResRows++;
                hrow = jdam.dam_getNextInsertRow(pStmtDA.dam_hstmt);
                }
            }
        else
            return DAM_FAILURE;

        piNumResRows.setVal(m_iNumResRows);
        return IP_SUCCESS;
        }

    int mem_process_sample_row(MEM_STMT_DA pStmtDA, long hTargetRow, int iRowid)
    {
        String          sBuf = "";

        if (pStmtDA.iType == DAM_SELECT) {
            jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hTargetRow);
            }
        else if (pStmtDA.iType == DAM_UPDATE) {
            long            hcol, hcolName, hcolAge;
            long            hrow;
            long            hRowElem, hRowElemName, hRowElemAge;
            String          pName;
            Integer         piAge;
            xo_int          piStatus;

            piStatus = new xo_int(1);

            hcolName = jdam.dam_getCol(pStmtDA.dam_hstmt, "NAME");
            hcolAge = jdam.dam_getCol(pStmtDA.dam_hstmt, "EMP_AGE");
            hrow = jdam.dam_getUpdateRow(pStmtDA.dam_hstmt, hTargetRow);

            /* update the row by getting the column values
            */
            hRowElemName = hRowElemAge = 0;
            hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
            while (hRowElem != 0) {
                hcol = jdam.dam_getColToSet(hRowElem);
                if (jdam.dam_compareCol(hcol, hcolName) == 1)
                    hRowElemName = hRowElem;
                else if (jdam.dam_compareCol(hcol, hcolAge) == 1)
                    hRowElemAge = hRowElem;
                else {
                    sBuf = "Values are being updated for invalid columns";
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, sBuf);
                    jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                    return DAM_FAILURE;
                    }

                hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
                }
            /* get the name & age */
            if (hRowElemName != 0) {
                pName = (String) jdam.dam_getValueToSet(hRowElemName, XO_TYPE_CHAR, piStatus);
                if (piStatus.getVal() == DAM_FAILURE) return IP_FAILURE;
                pSampleDb[iRowid].sName = pName;
                }
            if (hRowElemAge != 0) {
                piAge = (Integer) jdam.dam_getValueToSet(hRowElemAge, XO_TYPE_INTEGER, piStatus);
                if (piStatus.getVal() == DAM_FAILURE) return IP_FAILURE;
                pSampleDb[iRowid].iAge = piAge.intValue();
                }
            pSampleDb[iRowid].iRowVer++; /* update the rowver */
            m_iNumResRows++;
            }
        else if (pStmtDA.iType == DAM_DELETE) {
            pSampleDb[iRowid].iRowVer = 0;  /* mark it deleted */
            m_iNumResRows++;
            }

        return IP_SUCCESS;
    }

/********************************************************************************************
    Method:          java_mem_exec_varvalue_table
    Description:     This function is called to execute query on VARVALUE_TABLE

    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int java_mem_exec_varvalue_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
        {


    long            hCondList;
    long            hcond;
    long            iItems = 1;
    int             iRetCode;
    xo_int          iLeftOp, iLeftXoType, iLeftValLen, iStatus;
    xo_int          iRightOp, iRightXoType, iRightValLen;
    Integer         pLeftData = null;
//    Integer         pRightData = null;
    String          sBuf = "";

    jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_varvalue_table called\n");

    /* initialize row count */
    m_iNumResRows = 0;

    /* get the column handles */
    pStmtDA.hcolTag = jdam.dam_getCol(pStmtDA.dam_hstmt, "TAG");
    pStmtDA.hcolValue = jdam.dam_getCol(pStmtDA.dam_hstmt, "VALUE");
    pStmtDA.hcolItems = jdam.dam_getCol(pStmtDA.dam_hstmt, "ITEMS");
    if (pStmtDA.hcolTag ==0 || pStmtDA.hcolValue == 0 || pStmtDA.hcolItems == 0) {
        return DAM_FAILURE;
        }

    if (pStmtDA.iType == DAM_SELECT ) {
//        int              hindex;
//        int              hset_of_condlist;

        iLeftOp = new xo_int(0);
        iLeftXoType = new xo_int(0);
        iRightOp = new xo_int(0);
        iRightXoType = new xo_int(0);
        iLeftValLen = new xo_int(0);
        iStatus = new xo_int(0);
        iRightValLen = new xo_int(0);

        /* get restrictions on Items field */
        hCondList = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, pStmtDA.hcolItems);

        /* if no conditions are specified on Items, return default number record */
        if (hCondList > 0) {
            /* process each of the conditions */
            hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hCondList);
            pLeftData = (Integer)jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
            iRetCode = iStatus.getVal();
            if (iRetCode != IP_SUCCESS) return iRetCode;

            if (iLeftOp.getVal() != SQL_OP_EQUAL || iLeftXoType.getVal() != XO_TYPE_INTEGER)
                return DAM_FAILURE;

            /* check if condition value is NULL */
            if (pLeftData == null)
                iItems =0;
            else
                iItems = pLeftData.intValue();
            jdam.dam_setOption(ip.DAM_CONDLIST_OPTION, hCondList, ip.DAM_CONDLIST_OPTION_EVALUATION, ip.DAM_PROCESSING_OFF);
            }

        /* check if index condition is specified */
        iRetCode = jdam.dam_getOptimalIndexAndConditions(pStmtDA.dam_hstmt, pStmtDA.hindex, pStmtDA.hset_of_condlist);
        if (iRetCode != DAM_SUCCESS) return IP_FAILURE; /* return on error */
        if (pStmtDA.hindex.getVal() != 0) {
            hCondList = jdam.dam_getFirstCondList(pStmtDA.hset_of_condlist.getVal());
            while (hCondList != 0 ) {
                hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hCondList);
                jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
                iRetCode = iStatus.getVal();
                if (iRetCode != DAM_SUCCESS) return iRetCode;
                jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_RIGHT, iRightOp, iRightXoType, iRightValLen, iStatus);
                iRetCode = iStatus.getVal();
                if (iRetCode != DAM_SUCCESS) return iRetCode;
                sBuf = "Index condition on Value: LeftOp=" + iLeftOp.getVal() + " LeftXoType=" + iLeftXoType.getVal() +
                                               "RightOp=" + iRightOp.getVal() + " RightXoType=" + iRightXoType.getVal();
                jdam.trace(m_tmHandle, UL_TM_INFO, sBuf);
                iRetCode = java_mem_build_varvalue_rows(pStmtDA, iItems, iLeftXoType.getVal(), hcond);
                if (iRetCode != DAM_SUCCESS) return iRetCode;

                hCondList = jdam.dam_getNextCondList(pStmtDA.hset_of_condlist.getVal());
                }
            jdam.dam_freeSetOfConditionList(pStmtDA.hset_of_condlist.getVal()); /* free the conditions */
            }
        else {

            /* get restrictions on VARVALUE */

            hCondList = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, pStmtDA.hcolValue);

            /* if no conditions are specified on Value, return default values */
            if (bVarValueConditionSupport && hCondList > 0) {
                /* process each of the conditions */
                hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hCondList);
                jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
                iRetCode = iStatus.getVal();
                if (iRetCode != DAM_SUCCESS) return iRetCode;
                jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_RIGHT, iRightOp, iRightXoType, iRightValLen, iStatus);
                iRetCode = iStatus.getVal();
                if (iRetCode != DAM_SUCCESS) return iRetCode;
                sBuf = "Index condition on Value: LeftOp=" + iLeftOp.getVal() + " LeftXoType=" + iLeftXoType.getVal() +
                                               "RightOp=" + iRightOp.getVal() + " RightXoType=" + iRightXoType.getVal();
                jdam.trace(m_tmHandle, UL_TM_INFO, sBuf);
                iRetCode = java_mem_build_varvalue_rows(pStmtDA, iItems, iLeftXoType.getVal(), hcond);
                if (iRetCode != DAM_SUCCESS) return iRetCode;

                }
            else
                iRetCode = java_mem_build_varvalue_rows(pStmtDA, iItems, 0, 0);
                if (iRetCode != DAM_SUCCESS) return iRetCode;

            }

        } /* SELECT */
    else if (pStmtDA.iType == DAM_UPDATE ) {
        long            hcol;
        long            hrow, hTargetRow;
        long            hRowElem;
        StringBuffer    sName;
        xo_int          piStatus;

        /* skip identifying the update rows */
        hTargetRow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
        hrow = jdam.dam_getUpdateRow(pStmtDA.dam_hstmt, hTargetRow);

        sName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        piStatus = new xo_int();

        /* update the row by getting the column values
        */
        System.out.println("[MEM IP] Update record with the following values:\n");
        hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
        while (hRowElem > 0) {
            String   pVal;
            hcol = jdam.dam_getColToSet(hRowElem);
            sName.delete(0, sName.length());
            jdam.dam_describeCol(hcol, null, sName, null, null);

            pVal = (String)jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, piStatus);
            if (piStatus.getVal() == DAM_FAILURE) return DAM_FAILURE;
            System.out.println("[MEM IP] " + sName.toString() + "=" + pVal);

            hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
            }
        } /* UPDATE */
    else if (pStmtDA.iType == DAM_INSERT ) {
        long       hcol;
        long       hrow;
        long       hRowElem;
        StringBuffer    sName;
        xo_int          piStatus;
//        xo_int          piXOType;

        sName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        piStatus = new xo_int();

        hrow = jdam.dam_getFirstInsertRow(pStmtDA.dam_hstmt);

        /* insert the row by getting the column values
        */
        while (hrow > 0) {
            System.out.println("[MEM IP] Insert record with the following values:\n");

            hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
            while (hRowElem != 0) {

                String   pVal;
//                int     iValLen;

                hcol = jdam.dam_getColToSet(hRowElem);
                sName.delete(0, sName.length());
                jdam.dam_describeCol(hcol, null, sName, null, null);

                pVal = (String)jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, piStatus);
                if (piStatus.getVal() == DAM_FAILURE) return DAM_FAILURE;
                System.out.println("[MEM IP] " + sName.toString() + "=" + pVal);

                hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
                }

            hrow = jdam.dam_getNextInsertRow(pStmtDA.dam_hstmt);
            }
        } /* INSERT */
    else
        return DAM_FAILURE;

        piNumResRows.setVal(m_iNumResRows);
        return IP_SUCCESS;
        }

    /************************************************************************
    Function:       mem_exec_string_table()
    Description:    Process queries on String Table. Supports reading Comment, Notes
		    of a given employee from the WorkingDir location. The Comment/Note
		    file is looked up using the same name of the employee.

		    By default returns employee name as "pooh"
    Return:         DAM_SUCESS on success
				    DAM_FAILURE on error
    ************************************************************************/
    public int java_mem_exec_string_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
    {
	    String      sBuf;
	    long	hcol, hcolName, hcolNote, hcolComment, hcolCommentXL, hcolItems;
	    long	hrow;
	    int		iRetCode;

	    /* initialize row count */
		if(giTestMode == MEM_TEST_64BIT_ROWCOUNT) {
			m_iNumResRows = 0 + RowCount;
		}
		else {
			m_iNumResRows = 0;
		}

	/* get the column handles */
	hcolName = jdam.dam_getCol(pStmtDA.dam_hstmt, "ENAME");
	hcolNote = jdam.dam_getCol(pStmtDA.dam_hstmt, "NOTE");
	hcolComment = jdam.dam_getCol(pStmtDA.dam_hstmt, "COMMENT");
	hcolCommentXL = jdam.dam_getCol(pStmtDA.dam_hstmt, "COMMENTXL");
	hcolItems = jdam.dam_getCol(pStmtDA.dam_hstmt, "ITEMS");
	    if ( hcolName == 0 || hcolNote == 0 || hcolComment == 0 || hcolCommentXL == 0 || hcolItems == 0) {
		    return DAM_FAILURE;
	    }

	    if (pStmtDA.iType == DAM_INSERT) {
		long    hRowElem, hRowElemName, hRowElemNote, hRowElemComment, hRowElemCommentXL;
		xo_int	 iColNum, iColNumName, iColNumNote, iColNumComment, iColNumCommentXL;
		xo_int          piStatus;

		iColNum = new xo_int(0);
		iColNumName  = new xo_int(0);
		iColNumNote  = new xo_int(0);
		iColNumComment  = new xo_int(0);
		iColNumCommentXL  = new xo_int(0);
		    /* process each of the insert rows
	      for each row to be inserted, get the column values and insert
		    */
	    jdam.dam_describeCol(hcolName, iColNumName, null, null, null);
	    jdam.dam_describeCol(hcolNote, iColNumNote, null, null, null);
	    jdam.dam_describeCol(hcolComment, iColNumComment, null, null, null);
	    jdam.dam_describeCol(hcolCommentXL, iColNumCommentXL, null, null, null);
	    hrow = jdam.dam_getFirstInsertRow(pStmtDA.dam_hstmt);
		    hRowElemName = 0;
		    hRowElemNote = 0;
		    hRowElemComment = 0;
		    hRowElemCommentXL = 0;

		    piStatus = new xo_int();
	    while (hrow != 0) {
		/* insert the row */

			    hRowElemName = hRowElemComment = hRowElemNote = 0;
			hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
			    while (hRowElem != 0) {
				    hcol = jdam.dam_getColToSet(hRowElem);
			    jdam.dam_describeCol(hcol, iColNum, null, null, null);
				    if (iColNum.getVal() == iColNumName.getVal())
					    hRowElemName = hRowElem;
				    if (iColNum.getVal() == iColNumNote.getVal())
					    hRowElemNote = hRowElem;
				    if (iColNum.getVal() == iColNumComment.getVal())
					    hRowElemComment = hRowElem;
				    if (iColNum.getVal() == iColNumCommentXL.getVal())
					    hRowElemCommentXL = hRowElem;

			    hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
				    }

			    if (hRowElemName == 0)
				    return DAM_FAILURE;

			    if (hRowElemNote != 0) {
				    String	pName;
				    String      sFileName;

				    /* get the name */
				    pName =(String)jdam.dam_getValueToSet(hRowElem, XO_TYPE_VARCHAR, piStatus);
				    if (piStatus.getVal() == DAM_FAILURE) return DAM_FAILURE;


				    /* get the file name */
				    sFileName = sMemoryWorkingDir;
				    sFileName = sFileName.concat(pName);
				    sFileName = sFileName.concat("S.txt");
				    iRetCode = mem_get_longchar_data(pStmtDA, hRowElemNote, sFileName);
				    if (iRetCode != DAM_SUCCESS) return iRetCode;
				    }

			    if (hRowElemComment != 0) {
				    String	pName;
				    String      sFileName;

				    /* get the name */
				    pName =(String)jdam.dam_getValueToSet(hRowElemName, XO_TYPE_VARCHAR, piStatus);
				    if (piStatus.getVal() == DAM_FAILURE) return DAM_FAILURE;

				    /* save the picture */
				    sFileName = sMemoryWorkingDir;
				    sFileName = sFileName.concat(pName);
				    sFileName = sFileName.concat("L.txt");
				    iRetCode = mem_get_longchar_data(pStmtDA, hRowElemComment, sFileName);
				    if (iRetCode != DAM_SUCCESS) return iRetCode;
				    }

			    if (hRowElemCommentXL != 0) {
				    String	pName;
				    String      sFileName;

				    /* get the name */
				    pName =(String)jdam.dam_getValueToSet(hRowElem, XO_TYPE_VARCHAR, piStatus);
				    if (piStatus.getVal() == DAM_FAILURE) return DAM_FAILURE;

				    /* save the picture */
				    sFileName = sMemoryWorkingDir;
				    sFileName = sFileName.concat(pName);
				    sFileName = sFileName.concat("XL.txt");
				    iRetCode = mem_get_longchar_data(pStmtDA, hRowElemCommentXL, sFileName);
				    if (iRetCode != DAM_SUCCESS) return iRetCode;
				    }

		m_iNumResRows++;
		hrow = jdam.dam_getNextInsertRow(pStmtDA.dam_hstmt);
	    }
		}
	else {
		    sBuf = "Operation is not supported.";
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, sBuf);
                    jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                    return DAM_FAILURE;

	    }
		piNumResRows.setVal(m_iNumResRows);

	    return DAM_SUCCESS;
    }


/************************************************************************
Function:       java_mem_exec_wstring_table()
Description:    Process queries on WString Table. Supports reading Comment, Notes
                of a given employee from the WorkingDir location. The Comment/Note
                file is looked up using the same name of the employee.

                By default returns employee name as "pooh"
Return:         DAM_SUCESS on success
				DAM_FAILURE on error
************************************************************************/
	public int java_mem_exec_wstring_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
	{
	    String      sBuf;
	    long	hcol, hcolName, hcolNote, hcolComment, hcolCommentXL, hcolItems;
	    long	hrow;
	    int		iRetCode;

	    /* initialize row count */
	    m_iNumResRows = 0;

	    /* get the column handles */
	    hcolName = jdam.dam_getCol(pStmtDA.dam_hstmt, "ENAME");
	    hcolNote = jdam.dam_getCol(pStmtDA.dam_hstmt, "NOTE");
	    hcolComment = jdam.dam_getCol(pStmtDA.dam_hstmt, "COMMENT");
	    hcolCommentXL = jdam.dam_getCol(pStmtDA.dam_hstmt, "COMMENTXL");
	    hcolItems = jdam.dam_getCol(pStmtDA.dam_hstmt, "ITEMS");
	    if ( hcolName == 0 || hcolNote == 0 || hcolComment == 0 || hcolCommentXL == 0 || hcolItems == 0) {
			return DAM_FAILURE;
	    }

	    if (pStmtDA.iType == DAM_INSERT) {
		long    hRowElem, hRowElemName, hRowElemNote, hRowElemComment, hRowElemCommentXL;
		xo_int	 iColNum, iColNumName, iColNumNote, iColNumComment, iColNumCommentXL;
		xo_int          piStatus;

		iColNum = new xo_int();
		iColNumName  = new xo_int();
		iColNumNote  = new xo_int();
		iColNumComment  = new xo_int();
		iColNumCommentXL  = new xo_int();
		    /* process each of the insert rows
	      for each row to be inserted, get the column values and insert
		    */
	    jdam.dam_describeCol(hcolName, iColNumName, null, null, null);
	    jdam.dam_describeCol(hcolNote, iColNumNote, null, null, null);
	    jdam.dam_describeCol(hcolComment, iColNumComment, null, null, null);
	    jdam.dam_describeCol(hcolCommentXL, iColNumCommentXL, null, null, null);

	    hrow = jdam.dam_getFirstInsertRow(pStmtDA.dam_hstmt);
		    hRowElemName = 0;
		    hRowElemNote = 0;
		    hRowElemComment = 0;
		    hRowElemCommentXL = 0;

		    piStatus = new xo_int();
	    while (hrow > 0) {
		    /* insert the row */

		    hRowElemName = hRowElemComment = hRowElemNote = 0;
		    hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
		    while (hRowElem != 0) {
			    hcol = jdam.dam_getColToSet(hRowElem);
		    jdam.dam_describeCol(hcol, iColNum, null, null, null);
			    if (iColNum == iColNumName)
				    hRowElemName = hRowElem;
			    if (iColNum == iColNumNote)
				    hRowElemNote = hRowElem;
			    if (iColNum == iColNumComment)
				    hRowElemComment = hRowElem;
			    if (iColNum == iColNumCommentXL)
				    hRowElemCommentXL = hRowElem;

		    hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
			    }

		    if (hRowElemName == 0)
			    return DAM_FAILURE;

		    if (hRowElemNote != 0) {
			String	pName;
			String      sFileName;

			/* get the name */
			pName =(String)jdam.dam_getValueToSet(hRowElem, XO_TYPE_VARCHAR, piStatus);
			if (piStatus.getVal() == DAM_FAILURE) return DAM_FAILURE;


			/* get the file name */
			sFileName = sMemoryWorkingDir;
			sFileName = sFileName.concat(pName);
			sFileName = sFileName.concat("SW.txt");
			iRetCode = mem_get_longwchar_data(pStmtDA, hRowElemNote, sFileName);
			if (iRetCode != DAM_SUCCESS) return iRetCode;
		    }

		    if (hRowElemComment != 0) {
			String	pName;
			String      sFileName;

			/* get the name */
			pName =(String)jdam.dam_getValueToSet(hRowElem, XO_TYPE_VARCHAR, piStatus);
			if (piStatus.getVal() == DAM_FAILURE) return DAM_FAILURE;

			/* save the picture */
			sFileName = sMemoryWorkingDir;
			sFileName = sFileName.concat(pName);
			sFileName = sFileName.concat("LW.txt");
			iRetCode = mem_get_longwchar_data(pStmtDA, hRowElemComment, sFileName);
			if (iRetCode != DAM_SUCCESS) return iRetCode;
		    }

		    if (hRowElemCommentXL != 0) {
			String	pName;
			String      sFileName;

			/* get the name */
			pName =(String)jdam.dam_getValueToSet(hRowElem, XO_TYPE_VARCHAR, piStatus);
			if (piStatus.getVal() == DAM_FAILURE) return DAM_FAILURE;

			/* save the picture */
			sFileName = sMemoryWorkingDir;
			sFileName = sFileName.concat(pName);
			sFileName = sFileName.concat("XLW.txt");
			iRetCode = mem_get_longwchar_data(pStmtDA, hRowElemCommentXL, sFileName);
			if (iRetCode != DAM_SUCCESS) return iRetCode;
		    }

		    m_iNumResRows++;
		    hrow = jdam.dam_getNextInsertRow(pStmtDA.dam_hstmt);
		    }
		}
	    else {
		    sBuf = "Operation is not supported.";
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, sBuf);
                    jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                    return DAM_FAILURE;

	    }

	    return DAM_SUCCESS;
	}
/************************************************************************
Function:       java_mem_build_varvalue_rows()
Description:
Return:         DAM_SUCESS on success
                DAM_FAILURE on error
************************************************************************/
private int     java_mem_build_varvalue_rows(MEM_STMT_DA pStmtDA, long iItems, int iValXoType, long hcond)
{
    long            hrow;
    long            iCount;
//    int             iValLen;
    int             iValid;
    String          sBuf = "";


    if (hcond != 0) {
        for (iCount = 1; iCount <= iItems; iCount++) {
            String  sTag;
            long    lItemNum;

            /* create a new row */
            hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

            sTag = "tag" + iCount;
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolTag, sTag, XO_NTS );

            lItemNum = iCount;
            jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolItems, lItemNum, 0 );

            if (iValXoType == XO_TYPE_CHAR || iValXoType == XO_TYPE_VARCHAR) {
                String  pData;
                pData = (String)jdam.dam_getCondVal(hcond);
                jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, pData, XO_NTS);
                }
            else if (iValXoType == XO_TYPE_SMALLINT) {
                Short   pData;
                pData = (Short)jdam.dam_getCondVal(hcond);
                jdam.dam_addShortValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, pData.shortValue(), 0 );
                System.out.println("Smallint");
                }
            else if (iValXoType == XO_TYPE_INTEGER) {
                Integer  pData;
                pData = (Integer)jdam.dam_getCondVal(hcond);
                jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, pData.intValue(), 0);
                }
            else if (iValXoType == XO_TYPE_DOUBLE) {
                Double  pData;
                pData = (Double)jdam.dam_getCondVal(hcond);
                jdam.dam_addDoubleValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, pData.doubleValue(), 0 );
                }
            else if (iValXoType == XO_TYPE_VARIANT) {
                jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, null, XO_NULL_DATA);
                }
            else {
                sBuf = "VarValue table. Condition type:" + iValXoType + " not supported";
                jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, sBuf);
                jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                return DAM_FAILURE;
                }


            /* check and add row to result */
            iValid = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
            if (iValid == DAM_ERROR) {/* error */
		       jdam.dam_freeRow(hrow);
		       return DAM_FAILURE;
            }
            else if (iValid == DAM_TRUE) { /* target row */
                jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                }
            else
                jdam.dam_freeRow(hrow);
            }
        }
    else {
        for (iCount = 1; iCount <= iItems; iCount++) {
            String  sTag;
            long    lItemNum;
            long    iItemType;

            /* create a new row */
            hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

            sTag = "tag" + iCount;
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolTag, sTag, XO_NTS );

            lItemNum = iCount;
            jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolItems, lItemNum, 0 );

            iItemType = iCount % 10 ;
            if (iItemType == 0) { /* NULL */
                jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, null, XO_NULL_DATA );
                }
            else if (iItemType == 1) { /* CHAR */
                String    sValue;

                sValue = "Value " + iCount;
                jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, sValue, XO_NTS);
                }
            else if (iItemType == 2) { /* SMALLINT */
                short   val;

                val = (short)iCount;
                jdam.dam_addShortValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, val, 0 );
                }
            else if (iItemType == 3) { /* INTEGER */
                long val;

                val = iCount;
                jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, val, 0 );
                }
            else if (iItemType == 4) { /* DOUBLE */
                double  val;

                val = iCount + 0.5;
                jdam.dam_addDoubleValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, val, 0);
                }
            else { /* CHAR */
                String    sValue;

                sValue = "Value" + iCount;
                jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, pStmtDA.hcolValue, sValue, XO_NTS);
                }


            /* check and add row to result */
            iValid = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
            if (iValid == DAM_ERROR) {/* error */
                jdam.dam_freeRow(hrow);
		        return DAM_FAILURE;
	        }
            else if (iValid == DAM_TRUE) { /* target row */
                jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                }
            else
                jdam.dam_freeRow(hrow);
            }
        }
    return DAM_SUCCESS;
}

/********************************************************************************************
    Method:          java_mem_exec_recover_table
    Description:     This function is called to execute query on RECOVER_TABLE

    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int java_mem_exec_recover_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
        {


        jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_recover_table called\n");

        /* initialize row count */
        m_iNumResRows = 0;

        piNumResRows.setVal(m_iNumResRows);
        return IP_SUCCESS;
        }

/********************************************************************************************
    Method:          java_mem_exec_types_table
    Description:     This function is called to execute query on TYPES_TABLE

    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int java_mem_exec_types_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
        {
            int             iRetCode;
            long            hcolId, hcolChar, hcolNumeric, hcolDecimal, hcolReal, hcolDouble;
            long            hcolDate, hcolTime, hcolTimestamp, hcolVarchar;
			long			hcolBit, hcolTinyint;
			long 			hCondList;
			long 			hcolBigint;
			Object          pLeftVal;

            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_types_table called\n");

            /* get the column handles */
            hcolId = jdam.dam_getCol(pStmtDA.dam_hstmt, "ID");
            hcolChar = jdam.dam_getCol(pStmtDA.dam_hstmt, "CHAR_VAL");
            hcolNumeric = jdam.dam_getCol(pStmtDA.dam_hstmt, "NUMERIC_VAL");
            hcolDecimal = jdam.dam_getCol(pStmtDA.dam_hstmt, "DECIMAL_VAL");
            hcolReal = jdam.dam_getCol(pStmtDA.dam_hstmt, "REAL_VAL");
            hcolDouble = jdam.dam_getCol(pStmtDA.dam_hstmt, "DOUBLE_VAL");
            hcolDate = jdam.dam_getCol(pStmtDA.dam_hstmt, "DATE_VAL");
            hcolTime = jdam.dam_getCol(pStmtDA.dam_hstmt, "TIME_VAL");
            hcolTimestamp = jdam.dam_getCol(pStmtDA.dam_hstmt, "TIMESTAMP_VAL");
            hcolVarchar = jdam.dam_getCol(pStmtDA.dam_hstmt, "VARCHAR_VAL");
            hcolBit = jdam.dam_getCol(pStmtDA.dam_hstmt, "BIT_VAL");
            hcolTinyint = jdam.dam_getCol(pStmtDA.dam_hstmt, "TINYINT_VAL");
	    hcolBigint = jdam.dam_getCol(pStmtDA.dam_hstmt, "BIGINT_VAL");

			/* If option IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE is enabled, handles are available only
			   for the columns used in the query.And if the query does not contain the "items" column,
			   handle for "items" column will be NULL. In such cases set the rowcount to 1 */

			if(!gbMemValidateSchemaObjectInUse)
			{
            	if (hcolId == 0 || hcolChar == 0 || hcolNumeric == 0 || hcolDecimal == 0 || hcolReal == 0 || hcolDouble == 0 ||
                    hcolDate  == 0|| hcolTime == 0 || hcolTimestamp == 0 || hcolVarchar == 0 ||
					hcolBit == 0 || hcolTinyint == 0 || hcolBigint == 0) {
                return IP_FAILURE;
                }
			}
            if (pStmtDA.iType == DAM_SELECT) {
                long     hrow;

            iRetCode = jdam.dam_getOptimalIndexAndConditions(pStmtDA.dam_hstmt,pStmtDA.hindex,pStmtDA.hset_of_condlist);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* intialize indexed columns */
            int iLeftIntVal = 1;
            boolean iLeftBoolVal = true;
            byte bTinyintValue= 16;
	    long lLeftLongVal = 100;

            /*
             * Indexes are supported only in Integer,bit and tiny int type fields. If where clause is specified in the DML statement
             * use the that value for first row in the table.
             */
            if(pStmtDA.hindex.getVal() != 0){
            	hCondList = jdam.dam_getFirstCondList(pStmtDA.hset_of_condlist.getVal());

            	long hCond;
            	xo_int iLeftOp,iLeftXoType,iLeftValLen,iStatus;

            	iLeftOp = new xo_int(0);
                iLeftXoType = new xo_int(0);
                iLeftValLen = new xo_int(0);
                iStatus = new xo_int(0);

            	if(hCondList != 0){
            		hCond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt,hCondList);
            		pLeftVal = jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hCond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
            		iRetCode = iStatus.getVal();
            		 if (iRetCode != DAM_SUCCESS) return iRetCode;
                     if (iLeftOp.getVal() != SQL_OP_EQUAL)// || iLeftXoType.getVal() != XO_TYPE_BIT)
                         return DAM_FAILURE;
                     if(pLeftVal == null) return DAM_FAILURE;
                     if(iLeftXoType.getVal() == XO_TYPE_INTEGER)
                    	 iLeftIntVal = ((Integer)pLeftVal).intValue();
                     if(iLeftXoType.getVal() == XO_TYPE_BIT)
                    	 iLeftBoolVal = ((Boolean)pLeftVal).booleanValue();
                     if(iLeftXoType.getVal() == XO_TYPE_TINYINT)
                    	 bTinyintValue = ((Byte)pLeftVal).byteValue();
            	}
            }

            /* Build row by adding column values as NTS values */
            {

            int longVal = iLeftIntVal;
            hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);


            iRetCode = jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolId, longVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* char_value */
            String charValue= new String ("NTS String.");
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolChar, charValue, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* numeric_value */
            String numericValue = new String("123456789012");
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolNumeric,numericValue, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* decimal_value */
            String decimalValue = new String("123456789012");
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolDecimal,decimalValue, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* float_value */
            float floatVal = (float)123.45;
            iRetCode = jdam.dam_addFloatValToRow(pStmtDA.dam_hstmt, hrow, hcolReal, floatVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* double_value */
            double doubleVal = 123.45;
            iRetCode = jdam.dam_addDoubleValToRow(pStmtDA.dam_hstmt, hrow, hcolDouble, doubleVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* date_value */
            xo_tm   tmVal1 = new xo_tm(1999, 0, 1);
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolDate, tmVal1, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* time_value */
            xo_tm   tmVal2 = new xo_tm(10, 5, 0, 0);
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTime, tmVal2, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* timestamp_value */
            xo_tm   tmVal3 = new xo_tm(1999, 0, 1, 10, 5, 0, 0);
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTimestamp, tmVal3, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* varchar_value */
            String varcharValue= new String ("OpenAccess Test Data.");
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolVarchar, varcharValue, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* bit_value */
            boolean bitValue = iLeftBoolVal;
            iRetCode = jdam.dam_addBitValToRow(pStmtDA.dam_hstmt, hrow, hcolBit, bitValue, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* Tinyint_value */
            byte tinyintValue= bTinyintValue;
            iRetCode = jdam.dam_addTinyintValToRow(pStmtDA.dam_hstmt, hrow, hcolTinyint, tinyintValue, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

	    /* Bigint_value */
            long bigIntValue= lLeftLongVal;
            iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, hcolBigint, bigIntValue, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
            if(iRetCode == DAM_ERROR) {
		    jdam.dam_freeRow(hrow);
		    return DAM_FAILURE;
		    }
	    if (iRetCode == DAM_TRUE) {
                jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                m_iNumResRows++;
                }
            else
                jdam.dam_freeRow(hrow);
            } /* First row */

            /* Build row by adding column values as non-NTS values */
            {
            hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

            /* id */
            int longVal = 2;
            iRetCode = jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolId, longVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* char_value */
            String charValue= new String ("non NTS.");  /* keep it non-NTS even though its null terminated. Match with C Mem IP */
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolChar, charValue, charValue.length());
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* numeric_value */
            String numericValue = new String("123456789.50");
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolNumeric,numericValue, numericValue.length());
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* decimal_value */
            String decimalValue = new String("123456789.50");
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolDecimal,decimalValue, decimalValue.length());
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* float_value */
            float floatVal = (float)123.45;
            iRetCode = jdam.dam_addFloatValToRow(pStmtDA.dam_hstmt, hrow, hcolReal, floatVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* double_value */
            double doubleVal = 123;
            iRetCode = jdam.dam_addDoubleValToRow(pStmtDA.dam_hstmt, hrow, hcolDouble, doubleVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* date_value */
            xo_tm   tmVal1 = new xo_tm(1999, 1, 20);
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolDate, tmVal1, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* time_value */
            xo_tm   tmVal2 = new xo_tm(20, 20, 5, 0);
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTime, tmVal2, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* timestamp_value */
            xo_tm   tmVal3 = new xo_tm(1999, 1, 20, 10, 10, 9, 0);
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTimestamp, tmVal3, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* varchar_value */
            String varcharValue= new String ("New test String.");
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolVarchar, varcharValue, varcharValue.length());
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* bit_value */
            boolean bitValue = false;
            iRetCode = jdam.dam_addBitValToRow(pStmtDA.dam_hstmt, hrow, hcolBit, bitValue, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* Tinyint_value */
            byte tinyintValue= 127;
            iRetCode = jdam.dam_addTinyintValToRow(pStmtDA.dam_hstmt, hrow, hcolTinyint, tinyintValue, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

	    /* Bigint_value */
            long bigIntValue= 1000000000000000000L;
            iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, hcolBigint, bigIntValue, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
            if(iRetCode == DAM_ERROR){
		    jdam.dam_freeRow(hrow);
		    return DAM_FAILURE;
            }
            if (iRetCode == DAM_TRUE) {
                jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                m_iNumResRows++;
            }
            else
                jdam.dam_freeRow(hrow);
            } /* Second row */

            /* Build row by adding column values as NULL values */
            {
            hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

            /* id */
            iRetCode = jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolId, 0, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* char_value */
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolChar, null, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* numeric_value */
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolNumeric, null, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* decimal_value */
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolDecimal, null, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* float_value */
            iRetCode = jdam.dam_addFloatValToRow(pStmtDA.dam_hstmt, hrow, hcolReal, 0, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* double_value */
            iRetCode = jdam.dam_addDoubleValToRow(pStmtDA.dam_hstmt, hrow, hcolDouble, 0, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* date_value */
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolDate, null, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* time_value */
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTime, null, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* timestamp_value */
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTimestamp, null, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* varchar_value */
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolVarchar, null, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* bit_value */
            iRetCode = jdam.dam_addBitValToRow(pStmtDA.dam_hstmt, hrow, hcolBit, true, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* Tinyint_value */
            iRetCode = jdam.dam_addTinyintValToRow(pStmtDA.dam_hstmt, hrow, hcolTinyint, (byte)0, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

	    /* bigint_value */
            iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, hcolBigint, 0, XO_NULL_DATA);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
            if(iRetCode == DAM_ERROR){
		       jdam.dam_freeRow(hrow);
		       return DAM_FAILURE;
            }
            if (iRetCode == DAM_TRUE) {
                jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                m_iNumResRows++;
                }
            else
                jdam.dam_freeRow(hrow);
            } /* Third row */
            }

            piNumResRows.setVal(m_iNumResRows);
            return IP_SUCCESS;
        }

        /********************************************************************************************
            Method:          java_mem_exec_long_identifiers_table
            Description:     This function is called to execute query on LONG_IDENTIFIERS table

            Return:          IP_SUCCESS on success and IP_FAILURE on failure
        *********************************************************************************************/
        public int java_mem_exec_long_identifiers_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
        {
            int             iRetCode,longVal;
            long            hcolInt;
            long            hcolTimestamp, hcolVarchar;
			long 			hCondList;
			StringBuffer    sIndexName = new StringBuffer(130);
            long            hrow;
            Object          pLeftVal;

            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_types_table called\n");

            /* get the column handles */
            hcolInt = jdam.dam_getCol(pStmtDA.dam_hstmt, LONG_IDENTIFIERS_INT_COL);
            hcolTimestamp = jdam.dam_getCol(pStmtDA.dam_hstmt, LONG_IDENTIFIERS_TIMESTAMP_COL);
            hcolVarchar = jdam.dam_getCol(pStmtDA.dam_hstmt, LONG_IDENTIFIERS_VARCHAR_COL);


            if (hcolInt == 0 || hcolTimestamp == 0 || hcolVarchar == 0 )
                {
                return IP_FAILURE;
                }

            iRetCode = jdam.dam_getOptimalIndexAndConditions(pStmtDA.dam_hstmt,pStmtDA.hindex,pStmtDA.hset_of_condlist);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* intialize indexed columns */
            int iLeftIntVal = 1;
            /*
             * Index is supported only on Integer
             */
            if(pStmtDA.hindex.getVal() != 0){
            	jdam.dam_describeIndex(pStmtDA.hindex.getVal(), null, sIndexName, null, null, null);
                if (!sIndexName.toString().equals(LONG_IDENTIFIERS_INT_COL_INDEX))
					return DAM_ERROR;

				hCondList = jdam.dam_getFirstCondList(pStmtDA.hset_of_condlist.getVal());

            	long hCond;
            	xo_int iLeftOp,iLeftXoType,iLeftValLen,iStatus;

            	iLeftOp = new xo_int(0);
                iLeftXoType = new xo_int(0);
                iLeftValLen = new xo_int(0);
                iStatus = new xo_int(0);

            	if(hCondList != 0){
            		hCond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt,hCondList);
            		pLeftVal = jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hCond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
            		iRetCode = iStatus.getVal();
            		 if (iRetCode != DAM_SUCCESS) return iRetCode;
                     if (iLeftOp.getVal() != SQL_OP_EQUAL)
                         return DAM_FAILURE;
                     if(pLeftVal == null) return DAM_FAILURE;
                     if(iLeftXoType.getVal() == XO_TYPE_INTEGER)
                    	 iLeftIntVal = ((Integer)pLeftVal).intValue();
						 longVal = iLeftIntVal;
				if(longVal == 1 )
				{
					longVal = iLeftIntVal;
					hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

					/* integer value */
					iRetCode = jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolInt, longVal, 0);
					if (iRetCode != DAM_SUCCESS) return iRetCode;

					/* timestamp_value */
					xo_tm   tmVal3 = new xo_tm(9999, 11, 31, 23, 59, 59, 9);
					iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTimestamp, tmVal3, 0);
					if (iRetCode != DAM_SUCCESS) return iRetCode;

					/* varchar_value */
					String varcharValue= new String ("James");
					iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolVarchar, varcharValue, XO_NTS);
					if (iRetCode != DAM_SUCCESS) return iRetCode;

					iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
					if(iRetCode == DAM_ERROR){
					jdam.dam_freeRow(hrow);
					return DAM_FAILURE;
					}
					if (iRetCode == DAM_TRUE) {
					jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
					m_iNumResRows++;
					}
					else
						jdam.dam_freeRow(hrow);
					return IP_SUCCESS;
				}
				else if ( longVal == 2 )
				{
					hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

					/* integer values */
					longVal = 2;
					iRetCode = jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolInt, longVal, 0);
					if (iRetCode != DAM_SUCCESS) return iRetCode;

					/* timestamp_value */
					xo_tm   tmVal3 = new xo_tm(9999, 11, 31, 23, 59, 59, 9);
					iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTimestamp, tmVal3, 0);
					if (iRetCode != DAM_SUCCESS) return iRetCode;

					/* varchar_value */
					String varcharValue= new String ("Adams");
					iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolVarchar, varcharValue, varcharValue.length());
					if (iRetCode != DAM_SUCCESS) return iRetCode;

					iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
					if(iRetCode == DAM_ERROR){
					jdam.dam_freeRow(hrow);
					return DAM_FAILURE;
					}
					if (iRetCode == DAM_TRUE) {
						jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
						m_iNumResRows++;
					}
					else
						jdam.dam_freeRow(hrow);
					return IP_SUCCESS;
					}

            	}
            }

            /* Build first row by adding column values as NTS values */
            {

            longVal = iLeftIntVal;
            hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

			/* integer value */
            iRetCode = jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolInt, longVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* timestamp_value */
            xo_tm   tmVal3 = new xo_tm(2009, 10, 10, 10, 10, 10, 10);
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTimestamp, tmVal3, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* varchar_value */
            String varcharValue= new String ("James");
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolVarchar, varcharValue, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
	        if(iRetCode == DAM_ERROR){
              jdam.dam_freeRow(hrow);
              return DAM_FAILURE;
            }
            if (iRetCode == DAM_TRUE) {
                jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                m_iNumResRows++;
                }
            else
                jdam.dam_freeRow(hrow);
            } /* First row */

            /* Build second row */
            {
            hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

            /* integer values */
            longVal = 2;
            iRetCode = jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolInt, longVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* timestamp_value */
            xo_tm   tmVal3 = new xo_tm(9999, 11, 31, 23, 59, 59, 9);
            iRetCode = jdam.dam_addTimeStampValToRow(pStmtDA.dam_hstmt, hrow, hcolTimestamp, tmVal3, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* varchar_value */
            String varcharValue= new String ("Adams");
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolVarchar, varcharValue, varcharValue.length());
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
      	    if(iRetCode == DAM_ERROR){
		    jdam.dam_freeRow(hrow);
		    return DAM_FAILURE;
	        }
            if (iRetCode == DAM_TRUE) {
                jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                m_iNumResRows++;
                }
            else
                jdam.dam_freeRow(hrow);
            } /* Second row */

            piNumResRows.setVal(m_iNumResRows);
            return IP_SUCCESS;
        }
/********************************************************************************************
    Method:          java_mem_exec_alias_table
    Description:     This function is called to execute query on ALIAS_TABLE

    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int java_mem_exec_alias_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
        {


    int             iRetCode;
    long            hcolName;
//    int             hcolAlias;
    long            hcolItems;
    StringBuffer    sAliasName;
    String          sNameVal;
    long            hrow, hrow_copy;
    int             iAliasFound;
    long            hCondList;
    long            iItems = 1;
    long            iCurItem;
    long            iProcessOrder;
    boolean         bTableRowset;
    xo_long         pihcolAlias = new xo_long(0);

    jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_alias_table called\n");

    /* initialize row count */
    m_iNumResRows = 0;

    sAliasName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

    /* get the column handles */
    hcolName = jdam.dam_getCol(pStmtDA.dam_hstmt, "NAME");
    hcolItems = jdam.dam_getCol(pStmtDA.dam_hstmt, "ITEMS");

    if (pStmtDA.iType != DAM_SELECT )
        return DAM_FAILURE;

    /* check if query is on inner table of Join and use TableRowset */
    {

        xo_int  piValue;

        bTableRowset = false;
        piValue = new xo_int();
        iRetCode = jdam.dam_getInfo(0, pStmtDA.dam_hstmt, DAM_INFO_QUERY_PROCESS_ORDER,
                                    null, piValue);
        if (iRetCode != DAM_SUCCESS)
            iProcessOrder = 0;
        else
            iProcessOrder = piValue.getVal();
        if (iProcessOrder > 0) {
            iRetCode = jdam.dam_setOption(ip.DAM_STMT_OPTION, pStmtDA.dam_hstmt, ip.DAM_STMT_OPTION_TABLE_ROWSET, ip.DAM_PROCESSING_ON);
            if (iRetCode == DAM_SUCCESS) {
                bTableRowset = true;
                jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_alias_table() Processing query as TableRowset\n");
                }
        }
    }

        /* get restrictions on Items field */
        hCondList = jdam.dam_getRestrictionList(pStmtDA.dam_hstmt, hcolItems);

        /* if no conditions are specified on Items, return default number record */
        if (hCondList != 0) {
            long            hcond;
            xo_int          iLeftOp, iLeftXoType, iLeftValLen, iStatus;
            Long            pLeftData;

            iLeftOp = new xo_int(0);
            iLeftXoType = new xo_int(0);
            iLeftValLen = new xo_int(0);
            iStatus = new xo_int(0);

            /* process each of the conditions */
            hcond = jdam.dam_getFirstCond(pStmtDA.dam_hstmt, hCondList);
            pLeftData = (Long)jdam.dam_describeCondEx(pStmtDA.dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftXoType, iLeftValLen, iStatus);
            iRetCode = iStatus.getVal();
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            if (iLeftOp.getVal() != SQL_OP_EQUAL || iLeftXoType.getVal() != XO_TYPE_BIGINT)
                return DAM_FAILURE;

            /* check if condition value is NULL */
            if (pLeftData == null)
                iItems =0;
            else
                iItems = pLeftData.longValue();
            jdam.dam_setOption(ip.DAM_CONDLIST_OPTION, hCondList, ip.DAM_CONDLIST_OPTION_EVALUATION, ip.DAM_PROCESSING_OFF);
            }

    iCurItem = 1;
    while (iCurItem <= iItems) {

        hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

        sNameVal = "xxx-" + iCurItem;
        iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolName, sNameVal, XO_NTS);
        if (iRetCode != DAM_SUCCESS) return iRetCode;

        iRetCode = jdam.dam_addBigIntValToRow(pStmtDA.dam_hstmt, hrow, hcolItems, iCurItem, 0);
        if (iRetCode != DAM_SUCCESS) return iRetCode;

        /* check the alias columns and add the same name as
        result value
        */
        iAliasFound = jdam.dam_describeColResAlias(pStmtDA.dam_hstmt, hcolName, 1, sAliasName, pihcolAlias);
        while (iAliasFound == DAM_SUCCESS) {

            iRetCode = jdam.dam_addColAliasCharValToRow(pStmtDA.dam_hstmt, hrow, pihcolAlias.getVal(), sAliasName.toString(), XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            sAliasName.delete(0, sAliasName.length());
            iAliasFound = jdam.dam_describeColResAlias(pStmtDA.dam_hstmt, hcolName, 0, sAliasName, pihcolAlias);
            }

        /* copy the row */
        hrow_copy = jdam.dam_copyRow(pStmtDA.dam_hstmt, hrow);

        /* add row to result */
        iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
	    if(iRetCode == DAM_ERROR){
	    jdam.dam_freeRow(hrow);
	    return DAM_FAILURE;
	    }
        if (iRetCode == DAM_TRUE) {
            iRetCode = jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
            if (iRetCode != DAM_SUCCESS) {
                if (bTableRowset) {
                    jdam.dam_clearError(0, pStmtDA.dam_hstmt);
                    jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 0, "Memory - TableRowset exceeds limit");
                    }
                return iRetCode;
                }
            }
        else
            jdam.dam_freeRow(hrow);

        /* modify the cloned row and add to result */
        sNameVal = "xxx-copy";
        iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow_copy, hcolName, sNameVal, XO_NTS);
        if (iRetCode != DAM_SUCCESS) return iRetCode;
        iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow_copy);
	    if(iRetCode == DAM_ERROR){
	    jdam.dam_freeRow(hrow);
	    return DAM_FAILURE;
	    }
        if (iRetCode == DAM_TRUE) {
            jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow_copy);
            m_iNumResRows++;
            }
        else
            jdam.dam_freeRow(hrow_copy);


        iCurItem++;
        }

        piNumResRows.setVal(m_iNumResRows);
        return IP_SUCCESS;
        }

/********************************************************************************************
    Method:          java_mem_exec_special_table
    Description:     This function is called to execute query on SPECIAL_TABLE

    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int java_mem_exec_special_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
        {

        int             iRetCode;
        long            hcol;
        StringBuffer    sColumnName;
        long            hrow;

        jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_special_table called\n");

        /* initialize row count */
        m_iNumResRows = 0;

        sColumnName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

        if (pStmtDA.iType != DAM_SELECT )
            return DAM_FAILURE;

        /* get the column handles */
        hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);
        hcol = jdam.dam_getFirstCol(pStmtDA.dam_hstmt, DAM_COL_IN_USE);
        while (hcol != 0) {
            sColumnName.delete(0, sColumnName.length());
            jdam.dam_describeCol(hcol, null, sColumnName, null, null);
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, sColumnName.toString(), XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            hcol = jdam.dam_getNextCol(pStmtDA.dam_hstmt);
            }

        /* add row to result */
        iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
	    if(iRetCode == DAM_ERROR){
	    jdam.dam_freeRow(hrow);
	    return DAM_FAILURE;
	    }
        if (iRetCode == DAM_TRUE) {
            iRetCode = jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
            if (iRetCode != DAM_SUCCESS) {
                return iRetCode;
                }
            }
        else
            jdam.dam_freeRow(hrow);

        piNumResRows.setVal(m_iNumResRows);
        return DAM_SUCCESS;
}



/********************************************************************************************
    Method:          java_mem_exec_uemp_table
    Description:     This function is called to execute query on UEMP table

    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int java_mem_exec_uemp_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
        {
            int             iRetCode;
            long            hcolId, hcolName, hcolUname, hcolUWCname;

            /* get the column handles */
            hcolId = jdam.dam_getCol(pStmtDA.dam_hstmt, "ID");
            hcolName = jdam.dam_getCol(pStmtDA.dam_hstmt, "NAME");
            hcolUname = jdam.dam_getCol(pStmtDA.dam_hstmt, "UNAME");
			hcolUWCname = jdam.dam_getCol(pStmtDA.dam_hstmt, "UWCNAME");

            if (hcolId == 0 || hcolName == 0 || hcolUname == 0 || hcolUWCname == 0 ) {
                return IP_FAILURE;
                }

            if (pStmtDA.iType == DAM_SELECT) {
                long     hrow;

            /* Build row by adding column values as NTS values */
            {
            hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

            /* id */
            int longVal = 1;
            iRetCode = jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolId, longVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* char_value */
            String name= new String ("Joe");
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolName, name, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

			String Uname = new String("\u03b1" + "\u03b2" + "\u03b3");
            iRetCode = jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcolUname, Uname, XO_NTS);
			if (iRetCode != DAM_SUCCESS) return iRetCode;

			iRetCode = jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcolUWCname, Uname, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
            if(iRetCode == DAM_ERROR){
		    jdam.dam_freeRow(hrow);
		    return DAM_FAILURE;
		    }
            if (iRetCode == DAM_TRUE) {
                jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                m_iNumResRows++;
                }
            else
                jdam.dam_freeRow(hrow);
            }

			 /* Build row by adding column values as NTS values */
            {
            hrow = jdam.dam_allocRow(pStmtDA.dam_hstmt);

            /* id */
            int longVal = 5;
            iRetCode = jdam.dam_addIntValToRow(pStmtDA.dam_hstmt, hrow, hcolId, longVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* char_value */
            String name= new String ("empty-string");
            iRetCode = jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcolName, name, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

			/* insert empty string in wchar and wvarchar columns */
			String Uname = new String("");
            iRetCode = jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcolUname, Uname, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

			iRetCode = jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcolUWCname, Uname, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_isTargetRow(pStmtDA.dam_hstmt, hrow);
            if(iRetCode == DAM_ERROR){
		    jdam.dam_freeRow(hrow);
		    return DAM_FAILURE;
		    }
            if (iRetCode == DAM_TRUE) {
                jdam.dam_addRowToTable(pStmtDA.dam_hstmt, hrow);
                m_iNumResRows++;
                }
            else
                jdam.dam_freeRow(hrow);
            }/* Second row */
            }

        return IP_SUCCESS;
        }

/********************************************************************************************
    Method:          java_mem_exec_arrayemp_table
    Description:     This function is called to execute query on EMPARRAY

    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int java_mem_exec_arrayemp_table(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
        {


        jdam.trace(m_tmHandle, UL_TM_F_TRACE,"java_mem_exec_arrayemp_table called\n");

        /* initialize row count */
        m_iNumResRows = 0;

        piNumResRows.setVal(m_iNumResRows);
        return IP_SUCCESS;
        }


/********************************************************************************************
    Method:         ipSchema
    Description:    Dynamic schema implementation
    Return:         IP_SUCCESS on success
                    IP_FAILURE on error
*********************************************************************************************/
    public int ipSchema(long dam_hdbc, long pMemTree,int iType, long pList, Object pSearchObj)
    {
        if (bSchemaTestMode) mem_ip_print_schema_object(iType, pSearchObj);

        switch(iType)
        {
            case DAMOBJ_TYPE_CATALOG:
                {
                    schemaobj_table TableObj = new schemaobj_table();

                    TableObj.SetObjInfo(OA_CATALOG_NAME,null,null,null,null,null,null,null);
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                    if(giTestMode == MEM_TEST_LONG_IDENTIFIERS)
                    {
                        TableObj.SetObjInfo(OA_CATALOG_NAME_MAX, null, null, null, null, null, null, null);
                        jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, TableObj);
                    }
                }
                break;
            case DAMOBJ_TYPE_SCHEMA:
                {
                    schemaobj_table TableObj = new schemaobj_table();

                    schemaobj_table pSearchTableObj = null;
                    pSearchTableObj = (schemaobj_table)pSearchObj;

                    if (pSearchTableObj != null)
                    {
                        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for schema:<"+pSearchTableObj.getTableQualifier()+"."+pSearchTableObj.getTableOwner()+"> is being requested\n");
                    }
                    else
                    {
                        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all schemas is being requested\n");
                    }

                    if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, "SYSTEM", null))
                    {
                        TableObj.SetObjInfo(OA_CATALOG_NAME,"SYSTEM",null,null,null,null,null,null);
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                    }
                    
                    if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, null))
                    {
                        TableObj.SetObjInfo(OA_CATALOG_NAME,OA_USER_NAME,null,null,null,null,null,null);
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                    }
                    
                    if(giTestMode == MEM_TEST_LONG_IDENTIFIERS)
                    {
                        if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME_MAX, OA_USER_NAME_MAX, null))
                        {
                            TableObj.SetObjInfo(OA_CATALOG_NAME_MAX, OA_USER_NAME_MAX, null, null, null, null, null, null);
                            jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, TableObj);
                        }
                    }
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
                schemaobj_table TableObj = new schemaobj_table();

                schemaobj_table pSearchTableObj = null;
                pSearchTableObj = (schemaobj_table)pSearchObj;

                if (pSearchTableObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for table:<"+pSearchTableObj.getTableQualifier()+"."+pSearchTableObj.getTableOwner()+"."+pSearchTableObj.getTableName()+"> of Type:<"+pSearchTableObj.getTableType()+"> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all tables is being requested\n");
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "DEPT"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","DEPT","TABLE",null,"Sample UserData for Dept:TablePath:ColumnCount","0x0E","Department Values");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "EMP"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","EMP","TABLE",null,null,"0x0E","Employee Values");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "EMP_ALIAS"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","EMP_ALIAS","TABLE",null,null,"0x0E","Employee Values");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "EMP_VIEW"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","EMP_VIEW","VIEW",null,null,"0x0F","Employee Values");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "EMP_TABLE"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","EMP_TABLE","TABLE",null,null,"0x0E","Employee Picture Table");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "BINARY_TABLE"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","BINARY_TABLE","TABLE",null,null,"0x06","Binary Data Test Table");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "STRING_TABLE"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","STRING_TABLE","TABLE",null,null,"0x06","String Data Test Table");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "WSTRING_TABLE"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","WSTRING_TABLE","TABLE",null,null,"0x06","WString Data Test Table");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_TABLE"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","SAMPLE_TABLE","TABLE",null,null,"0x0E","Sample Table");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "VARVALUE"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","VARVALUE","TABLE",null,null,"0x0E","Table with variant values");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "OA_TXN_LOG"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","OA_TXN_LOG","TABLE",null,null,"0x0E","Table to recover TXN");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "TYPES_TABLE"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","TABLE",null,null,null,"Types Table");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "UEMP"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","UEMP","TABLE",null,null,"0x0E","");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "EMPARRAY"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","TABLE",null,null,"0x06","Employee Array Table");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "ALIAS_TABLE"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","ALIAS_TABLE","TABLE",null,null,null,"Alias Table");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "SPECIAL01_#@^$&%"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","SPECIAL01_#@^$&%","TABLE",null,null,null,"Table name with all special characters");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "SPECIAL-TABLE"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","SPECIAL-TABLE","TABLE",null,null,null,"Table name with dash character -");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "\\SPECIAL-02\\Name"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","\\SPECIAL-02\\Name","TABLE",null,null,null,"Table Name with backslash");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "//SPECIAL-03/Name"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","//SPECIAL-03/Name","TABLE",null,null,null,"Table Name with forward slash");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }

                if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME, OA_USER_NAME, "1SPECIAL"))
                {
                TableObj.SetObjInfo("SCHEMA","OAUSER","1SPECIAL","TABLE",null,null,null,"Table name with Number at the start");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                }
                if(giTestMode == MEM_TEST_LONG_IDENTIFIERS)
                {
                    if (mem_is_matching_table(pSearchTableObj, OA_CATALOG_NAME_MAX, OA_USER_NAME_MAX, LONG_IDENTIFIERS_TABLE_NAME))
                    {
                        TableObj.SetObjInfo(OA_CATALOG_NAME_MAX, OA_USER_NAME_MAX, LONG_IDENTIFIERS_TABLE_NAME, "TABLE", null, null, null, "Table name with the length of its name and its column names 128 characters each");
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
                    }
                }
            }
            break;
            case DAMOBJ_TYPE_COLUMN:
            {
                schemaobj_column pSearchColumnObj = null;
                pSearchColumnObj = (schemaobj_column)pSearchObj;

                if (pSearchColumnObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for column <"+pSearchColumnObj.getColumnName()+"> of table:<"+pSearchColumnObj.getTableQualifier()+"."+pSearchColumnObj.getTableOwner()+"."+pSearchColumnObj.getTableName()+"> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all columns of all tables is being requested\n");
                }

                /* DEPT TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "DEPT"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","DEPT","DEPTID",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,"Sample UserData for DeptID:ColumnOffset=0:DataType=4",null,(short)DAMOBJ_NOTSET,(short)0,"ID of the department");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","DEPT","DNAME",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Department");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","DEPT","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* EMP TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "EMP"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP","EMPID",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"ID of the employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP","ENAME",(short)12,"VARCHAR",240,240,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP","DEPTID",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"DeptID of the employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP","DATE_VAL",(short)9,"DATE",6,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Date Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP","DOUBLE_VAL",(short)8,"DOUBLE",8,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Double Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo( "SCHEMA","OAUSER","EMP","NUMERIC_VAL",(short)2,"NUMERIC",34,32,(short)DAMOBJ_NOTSET,(short)5,
                        (short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null,(short)DAMOBJ_NOTSET, (short)0, "Numeric value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* EMP_ALIAS TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "EMP_ALIAS"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_ALIAS","EMPID",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"ID of the employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_ALIAS","ENAME",(short)12,"VARCHAR",240,240,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_ALIAS","DEPTID",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"DeptID of the employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_ALIAS","DATE_VAL",(short)9,"DATE",6,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Date Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_ALIAS","DOUBLE_VAL",(short)8,"DOUBLE",8,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Double Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo( "SCHEMA","OAUSER","EMP_ALIAS","NUMERIC_VAL",(short)2,"NUMERIC",34,32,(short)DAMOBJ_NOTSET,(short)5,
                        (short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null,(short)DAMOBJ_NOTSET, (short)0, "Numeric value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_ALIAS","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* EMP_VIEW COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "EMP_VIEW"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_VIEW","EMPID",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"ID of the employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_VIEW","ENAME",(short)12,"VARCHAR",240,240,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_VIEW","DEPTID",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"DeptID of the employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_VIEW","DATE_VAL",(short)9,"DATE",6,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Date Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_VIEW","DOUBLE_VAL",(short)8,"DOUBLE",8,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Double Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo( "SCHEMA","OAUSER","EMP_VIEW","NUMERIC_VAL",(short)2,"NUMERIC",34,32,(short)DAMOBJ_NOTSET,(short)5,
                        (short)XO_NULLABLE, (short)DAMOBJ_NOTSET, null, null,(short)DAMOBJ_NOTSET, (short)0, "Numeric value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_VIEW","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* EMP_TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "EMP_TABLE"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_TABLE","NAME",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

/*                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_TABLE","KIDS",(short)jdam.xo_make_array_type(12),"VARCHAR",255,255,(short)5,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Comments about the Employee");
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
*/

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_TABLE","PICTURE",(short)XO_TYPE_LONGVARBINARY,"LONGVARBINARY",16000000,16000000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Picture of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_TABLE","COMMENTS",(short)XO_TYPE_LONGVARCHAR,"LONGVARCHAR",16000000,16000000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Comments about the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);


                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMP_TABLE","WCOMMENTS",(short)XO_TYPE_WLONGVARCHAR,"WLONGVARCHAR",16000000,16000000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"WComments about the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                }

                /* BINARY_TABLE TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "BINARY_TABLE"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","BINARY_TABLE","ENAME",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","BINARY_TABLE","STAMP",(short)XO_TYPE_BINARY,"BINARY",255,255,(short)5,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0," Stamp Size picture of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","BINARY_TABLE","PICTURE",(short)XO_TYPE_VARBINARY,"VARBINARY",32000,32000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Picture of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","BINARY_TABLE","PICTUREXL",(short)XO_TYPE_LONGVARBINARY,"LONGVARBINARY",16000000,16000000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Extra Large Picture of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","BINARY_TABLE","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* STRING_TABLE TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "STRING_TABLE"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","STRING_TABLE","ENAME",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","STRING_TABLE","NOTE",(short)XO_TYPE_CHAR,"CHAR",255,255,(short)5,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0," Short Comment on the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","STRING_TABLE","COMMENT",(short)XO_TYPE_VARCHAR,"VARCHAR",32000,32000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Notes of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","STRING_TABLE","COMMENTXL",(short)XO_TYPE_LONGVARCHAR,"LONGVARCHAR",16000000,16000000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Extra Large Notes of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","STRING_TABLE","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* WSTRING_TABLE TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "WSTRING_TABLE"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","WSTRING_TABLE","ENAME",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","WSTRING_TABLE","NOTE",(short)XO_TYPE_WCHAR,"WCHAR",255,255,(short)5,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0," Short Comment on the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","WSTRING_TABLE","COMMENT",(short)XO_TYPE_WVARCHAR,"WVARCHAR",32000,32000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Notes of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","WSTRING_TABLE","COMMENTXL",(short)XO_TYPE_WLONGVARCHAR,"WLONGVARCHAR",16000000,16000000,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Extra Large Notes of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","WSTRING_TABLE","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* SAMPLE_TABLE TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "SAMPLE_TABLE"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","SAMPLE_TABLE","ROWID",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)SQL_SCOPE_SESSION,null,"0x10",(short)SQL_PC_PSEUDO,(short)SQL_BEST_ROWID,"RowId for the employee record");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","SAMPLE_TABLE","NAME",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","SAMPLE_TABLE","EMP_AGE",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)SQL_SCOPE_SESSION,null,null,(short)SQL_PC_NOT_PSEUDO,(short)0,"Age of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","SAMPLE_TABLE","ROWVER",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)SQL_SCOPE_SESSION,null,"0x10",(short)SQL_PC_PSEUDO,(short)SQL_ROWVER,"Row Version for the employee record");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                }

                /* VARVALUE TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "VARVALUE"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","VARVALUE","TAG",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","VARVALUE","VALUE",(short)XO_TYPE_VARIANT,"VARIANT",32,32,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,"0x0f",(short)DAMOBJ_NOTSET,(short)0,"Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","VARVALUE","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                }

                /* OA_TXN_LOG TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "OA_TXN_LOG"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","OA_TXN_LOG","TXN_ID",(short)1,"CHAR",36,36,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Transaction ID");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","OA_TXN_LOG","TXN_INFO",(short)1,"CHAR",240,240,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,"0x0f",(short)DAMOBJ_NOTSET,(short)0,"Transaction Information");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","OA_TXN_LOG","TXN_STATUS",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","OA_TXN_LOG","RECOVERY_STATUS",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","OA_TXN_LOG","RM_GUID",(short)1,"CHAR",36,36,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Transaction ID");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","OA_TXN_LOG","DTC_GUID",(short)1,"CHAR",36,36,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Transaction ID");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","OA_TXN_LOG","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                }

                /* TYPES TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "TYPES_TABLE"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","ID",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"ID/Integer Field");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

		    if(MEM_TEST_USE_BULK_FETCH == giTestMode)
			ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","CHAR_VAL",(short)1,"CHAR",2050,2050,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Character Value");
		    else
			ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","CHAR_VAL",(short)1,"CHAR",16,16,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Character Value");

                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","NUMERIC_VAL",(short)2,"NUMERIC",34,32,(short)DAMOBJ_NOTSET,(short)5,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Numeric Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","DECIMAL_VAL",(short)3,"DECIMAL",34,32,(short)DAMOBJ_NOTSET,(short)5,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Decimal Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","REAL_VAL",(short)7,"REAL",4,7,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Real Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","DOUBLE_VAL",(short)8,"DOUBLE",8,15,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Double Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","DATE_VAL",(short)9,"DATE",6,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Date Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","TIME_VAL",(short)10,"TIME",6,8,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Date Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","TIMESTAMP_VAL",(short)11,"TIMESTAMP",16,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Date Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

		    if(MEM_TEST_USE_BULK_FETCH == giTestMode)
			ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","VARCHAR_VAL",(short)12,"CHAR",2050,2050,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Varchar Value");
		    else
			ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","VARCHAR_VAL",(short)12,"CHAR",32,32,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Varchar Value");
		    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","BIT_VAL",(short)XO_TYPE_BIT,"BIT",1,1,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Bit Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","TINYINT_VAL",(short)XO_TYPE_TINYINT,"TINYINT",1,3,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Tinyint Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

		    ColumnObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE","BIGINT_VAL",(short)XO_TYPE_BIGINT,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Bigint Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* UEMP TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "UEMP"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","UEMP","ID",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"ID of the employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","UEMP","NAME",(short)12,"VARCHAR",255, 255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","UEMP","UNAME",(short)-9,"WVARCHAR",255, 255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
					ColumnObj.SetObjInfo("SCHEMA","OAUSER","UEMP","UWCNAME",(short)-8,"WCHAR",255, 255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                }

                /* EMPARRAY TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "EMPARRAY"))
                {

                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","NAME",(short)12,"VARCHAR",255, 255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","KIDS",(short)jdam.xo_make_array_type(12),"VARCHAR",255, 255,(short)100,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Kids of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","KIDAGES",(short)jdam.xo_make_array_type(4),"INTEGER",4,10,(short)100,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Ages of Kids of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","SALARY",(short)jdam.xo_make_array_type(8),"DOUBLE",15,10,(short)100,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Salary of the Employee-Basic+inc+da+perks");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","NUMFIELD",(short)jdam.xo_make_array_type(2),"NUMERIC",15,13,(short)100,(short)2,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Numeric field");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","VARFIELD",(short)jdam.xo_make_array_type(XO_TYPE_VARIANT),"VARIANT",15,10,(short)100,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Variant of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","KID_DOB",(short)jdam.xo_make_array_type(9),"DATE",6,10,(short)100,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"DOBs of the kids of Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","DATES",(short)jdam.xo_make_array_type(11),"TIMESTAMP",16,19,(short)100,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Important dates of employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","EMPARRAY","ELEMENTS",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of elements of array");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);


                }

                /* ALIAS_TABLE TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "ALIAS_TABLE"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","ALIAS_TABLE","NAME",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","ALIAS_TABLE","ITEMS",(short)-5,"BIGINT",8,19,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Number of Items to Return");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* SPECIAL01_#@^$&% TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "SPECIAL01_#@^$&%"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","SPECIAL01_#@^$&%","NAME01_#@^$&%",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* "SPECIAL-TABLE" TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "SPECIAL-TABLE"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","SPECIAL-TABLE","COL-NAME",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

               /* "\\SPECIAL-02\\Name" TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "\\SPECIAL-02\\Name"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","\\SPECIAL-02\\Name","\\\\NAME01\\Col1",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","\\SPECIAL-02\\Name","\\\\NAME01\\Col2",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* "//SPECIAL-03/Name" TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "//SPECIAL-03/Name"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","//SPECIAL-03/Name","/Col1",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","//SPECIAL-03/Name","//Col2/Name",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }

                /* "1SPECIAL" TABLE COLUMNS */
                if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME, OA_USER_NAME, "1SPECIAL"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","1SPECIAL","1Col",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"Name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo("SCHEMA","OAUSER","1SPECIAL","2Col",(short)12,"VARCHAR",255,255,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET
                        ,(short)0,"Name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }
		/* LONG IDENTIFIERS table columns */
                if(giTestMode == MEM_TEST_LONG_IDENTIFIERS)
                {
                    if (mem_is_matching_column(pSearchColumnObj, OA_CATALOG_NAME_MAX, OA_USER_NAME_MAX, LONG_IDENTIFIERS_TABLE_NAME))
                    {
                        schemaobj_column ColumnObj = new schemaobj_column();

                        ColumnObj.SetObjInfo(OA_CATALOG_NAME_MAX, OA_USER_NAME_MAX, LONG_IDENTIFIERS_TABLE_NAME, LONG_IDENTIFIERS_INT_COL, (short)4, "INTEGER", 4, 10, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
                            (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"INTEGER field of the LONG_IDENTIFIERS table");
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                        ColumnObj.SetObjInfo(OA_CATALOG_NAME_MAX, OA_USER_NAME_MAX, LONG_IDENTIFIERS_TABLE_NAME, LONG_IDENTIFIERS_VARCHAR_COL, (short)12, "VARCHAR", 255, 255, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
                            (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"VARCHAR field of the LONG_IDENTIFIERS table");
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                        ColumnObj.SetObjInfo(OA_CATALOG_NAME_MAX, OA_USER_NAME_MAX, LONG_IDENTIFIERS_TABLE_NAME, LONG_IDENTIFIERS_TIMESTAMP_COL, (short)11, "TIMESTAMP", 16, 19, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,
                            (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)DAMOBJ_NOTSET,(short)0,"TIMESTAMP field of the LONG_IDENTIFIERS table");
                        jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    }
                }
            }
            break;

            case DAMOBJ_TYPE_STAT:
            {
				long cardinality = 100;
                schemaobj_stat pSearchStatObj = null;
                pSearchStatObj = (schemaobj_stat)pSearchObj;

                if (pSearchStatObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for Statistics of table:<"+pSearchStatObj.getTableQualifier()+"."+pSearchStatObj.getTableOwner()+"."+pSearchStatObj.getTableName()+"> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all statistics of all tables is being requested\n");
                }

                schemaobj_stat StatObj = new schemaobj_stat();

                if(giTestMode == MEM_TEST_64BIT_ROWCOUNT) {
                	cardinality = 100 + RowCount;
        		}

                StatObj.SetObjInfo("SCHEMA","OAUSER","DEPT",(short)DAMOBJ_NOTSET,null,null,(short)0,(short)DAMOBJ_NOTSET,null,
                    null,cardinality,DAMOBJ_NOTSET,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,StatObj);

                StatObj.SetObjInfo("SCHEMA","OAUSER","DEPT",(short)0,null,"DEPTID",(short)1,(short)1,"DEPTID",
                    "A",cardinality,DAMOBJ_NOTSET,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,StatObj);

                StatObj.SetObjInfo("SCHEMA","OAUSER","EMP",(short)1,null,"DEPTID",(short)1,(short)1,"DEPTID",
                    "A",DAMOBJ_NOTSET,DAMOBJ_NOTSET,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,StatObj);

                if (bVarValueIndexSupport) {
                StatObj.SetObjInfo("SCHEMA","OAUSER","VARVALUE",(short)1,null,"DEPTID",(short)3,(short)1,"VALUE",
                    "A",DAMOBJ_NOTSET,DAMOBJ_NOTSET,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,StatObj);
                }

                /*
                 * Add index for types_table - Id,bit_val and tiny_int fields.
                 */
                StatObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE",(short)1,null,"ID_INDEX",(short)1,(short)1,"ID",
                        "A",DAMOBJ_NOTSET,DAMOBJ_NOTSET,null);
                 jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,StatObj);

                StatObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE",(short)1,null,"BIT_VAL_INDEX",(short)1,(short)1,"BIT_VAL",
                         "A",DAMOBJ_NOTSET,DAMOBJ_NOTSET,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,StatObj);

                StatObj.SetObjInfo("SCHEMA","OAUSER","TYPES_TABLE",(short)1,null,"TINYINT_VAL_INDEX",(short)1,(short)1,"TINYINT_VAL",
                        "A",DAMOBJ_NOTSET,DAMOBJ_NOTSET,null);
               jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,StatObj);
               if(giTestMode == MEM_TEST_LONG_IDENTIFIERS)
               {
                    StatObj.SetObjInfo(OA_CATALOG_NAME_MAX, OA_USER_NAME_MAX, LONG_IDENTIFIERS_TABLE_NAME, (short)0, null, LONG_IDENTIFIERS_INT_COL_INDEX, (short)1, (short)1, LONG_IDENTIFIERS_INT_COL,
                          "A",DAMOBJ_NOTSET,DAMOBJ_NOTSET,null);
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,StatObj);
               }
            }
            break;

            case DAMOBJ_TYPE_FKEY:
            {
                schemaobj_fkey pSearchFkeyObj = null;
                pSearchFkeyObj = (schemaobj_fkey)pSearchObj;

                if (pSearchFkeyObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for ForeignKeys for PKtable:<"+pSearchFkeyObj.getPKTableQualifier()+"."+pSearchFkeyObj.getPKTableOwner()+"."+pSearchFkeyObj.getPKTableName()+
                    "> and FKtable:<"+pSearchFkeyObj.getFKTableQualifier()+"."+pSearchFkeyObj.getFKTableOwner()+"."+pSearchFkeyObj.getFKTableName()+ "> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all FKEYS is being requested\n");
                }

                /* add FKY definition */
                schemaobj_fkey FkeyObj = new schemaobj_fkey();
                FkeyObj.SetObjInfo("SCHEMA","OAUSER", "DEPT", "DEPTID",
                                    "SCHEMA","OAUSER", "EMP", "DEPTID",
                                    (short)1, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET, "DEPT_DEPTID", "EMP_DEPTID");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, FkeyObj);

            }
            break;

            case DAMOBJ_TYPE_PKEY:
            {
                schemaobj_pkey pSearchPkeyObj = null;
                pSearchPkeyObj = (schemaobj_pkey)pSearchObj;

                if (pSearchPkeyObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for PrimaryKeys for PKtable:<"+pSearchPkeyObj.getPKTableQualifier()+"."+pSearchPkeyObj.getPKTableOwner()+"."+pSearchPkeyObj.getPKTableName()+
                    "> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all PRIMARY KEYS is being requested\n");
                }

                /* Add Primary Key definitions */
                schemaobj_pkey PkeyObj = new schemaobj_pkey();

                /* EMP table indexed optimization only expects non-unique index on DEPTID. So we will not expose
                PkeyObj.SetObjInfo("SCHEMA","OAUSER", "EMP", "EMPID",
                                    (short)1,null);
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, PkeyObj);
                */
                PkeyObj.SetObjInfo("SCHEMA","OAUSER", "DEPT", "DEPTID",
                                    (short)1, "pkey_deptid");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, PkeyObj);
            }
            break;

            case DAMOBJ_TYPE_PROC:
            {
                schemaobj_proc ProcObj = new schemaobj_proc();
                schemaobj_proc pSearchProcObj = null;
                pSearchProcObj = (schemaobj_proc)pSearchObj;

                if (pSearchProcObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for Procedure:<"+pSearchProcObj.getQualifier()+"."+pSearchProcObj.getOwner()+"."+pSearchProcObj.getProcName()+"> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all Procedures is being requested\n");
                }

                /* Add Proc definitions */
                ProcObj.SetObjInfo("SCHEMA","OAUSER", "QUERY_EMP", 1, DAMOBJ_NOTSET, 1,
                                    (short)DAMOBJ_NOTSET, null, "Query employee");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "FORMAT_QUERY", 1, DAMOBJ_NOTSET, 1,
                                    (short)DAMOBJ_NOTSET, null, "Return format of the input Query");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "UPDATE_EMP", 4, DAMOBJ_NOTSET, 1,
                                    (short)DAMOBJ_NOTSET, null, "Update employee");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "UPDATE_DEPT", 3, DAMOBJ_NOTSET, DAMOBJ_NOTSET,
                                    (short)DAMOBJ_NOTSET, null, "Update department");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "REFRESH_DB", 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET,
                                    (short)DAMOBJ_NOTSET, null, "Refresh Database");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "TEST_ARRAY", 3, DAMOBJ_NOTSET, DAMOBJ_NOTSET,
                                    (short)DAMOBJ_NOTSET, null, "TEST_ARRAY");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "LOOP", 1, DAMOBJ_NOTSET, DAMOBJ_NOTSET,
                                    (short)DAMOBJ_NOTSET, null, "Loop specified number of million times");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "TEST_STRING_PARAM", 5, DAMOBJ_NOTSET, 1,
                                    (short)DAMOBJ_NOTSET, null, "TEST String Params");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

		ProcObj.SetObjInfo("SCHEMA","OAUSER", "MULTIRESULT_REGULAR", 1, DAMOBJ_NOTSET, 1,
                                    (short)DAMOBJ_NOTSET, null, "Returns multiple results");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "MULTIRESULT", 1, DAMOBJ_NOTSET, 2,
                                    (short)DAMOBJ_NOTSET, null, "Returns multiple results");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "MULTIRESULT_OUT", 1, DAMOBJ_NOTSET, 2,
                                    (short)DAMOBJ_NOTSET, null, "Returns multiple results");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "CHECK_STATUS_INT", 1, DAMOBJ_NOTSET, 1,
                                    (short)DAMOBJ_NOTSET, null, "Returns Status of SP");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "CHECK_STATUS_CHAR", 1, DAMOBJ_NOTSET, 1,
                                    (short)DAMOBJ_NOTSET, null, "Returns Status of SP as Char");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "PROC_OUT_RET", 1, 1, DAMOBJ_NOTSET,
                                    (short)DAMOBJ_NOTSET, null, "Returns Status of SP as Char");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "DYN_PROC", 1, DAMOBJ_NOTSET, 2,
                                    (short)DAMOBJ_NOTSET, null, "Returns Status of SP as Char");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "PROC_MULTI_OUT", 1, 3, 2,
                                    (short)DAMOBJ_NOTSET, null, "Multiple output paramters");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "PROC_GTECH", 2, 3, 2,
                                    (short)DAMOBJ_NOTSET, null, "SP for GTech example");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "PROC_SKIP_RESULTCOLS", 1, DAMOBJ_NOTSET, 2,
                                    (short)DAMOBJ_NOTSET, null, "Returns multiple results");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "PROC_EMPTY_RESULTSETS", 1, DAMOBJ_NOTSET, 2,
                                    (short)DAMOBJ_NOTSET, null, "Returns multiple results");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);

                ProcObj.SetObjInfo("SCHEMA","OAUSER", "DYN_PROC_NO_ARGS", 0, 0, 2,
                                    (short)DAMOBJ_NOTSET, null, "SP for Repeated query execution test. No Input/Output/Return Arguments");
                jdam.dam_add_schemaobj(pMemTree,iType, pList, pSearchObj, ProcObj);


            }
            break;
            case DAMOBJ_TYPE_PROC_COLUMN:
            {
                schemaobj_proccolumn ProcColumnObj = new schemaobj_proccolumn();

                schemaobj_proccolumn pSearchProcColumnObj = null;
                pSearchProcColumnObj = (schemaobj_proccolumn)pSearchObj;

                if (pSearchProcColumnObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for column <"+pSearchProcColumnObj.getColumnName()+"> of Procedure:<"+pSearchProcColumnObj.getQualifier()+"."+pSearchProcColumnObj.getOwner()+"."+pSearchProcColumnObj.getProcName()+"> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all columns of all Procedures is being requested\n");
                }
                /* Add Proc column definitions */

                { /* QUERY_EMP */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","QUERY_EMP","QUERY",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Query to execute");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","QUERY_EMP","EMPID",
                    (short)SQL_RESULT_COL, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"ID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","QUERY_EMP","ENAME",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* QUERY_EMP */

                { /* FORMAT_QUERY */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","FORMAT_QUERY","QUERY",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Query to execute");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","FORMAT_QUERY","FORMAT_QUERY",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 1024, 1024, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Formatted Query");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* FORMAT_QUERY */

                { /* UPDATE_DEPT */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_DEPT","DEPTID",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"ID of the department");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_DEPT","DNAME",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Department");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_DEPT","ITEMS",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of Items to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);


                } /* UPDATE_DEPT */

                { /* UPDATE_EMP */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_EMP","EMPID",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"ID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_EMP","ENAME",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_EMP","DEPTID",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"DeptID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_EMP","ITEMS",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of Items to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_EMP","NEW_ENAME",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_EMP","NEW_DEPTID",
                    (short)SQL_RESULT_COL, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"DeptID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","UPDATE_EMP","NEW_ITEMS",
                    (short)SQL_RESULT_COL, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of Items to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);
                } /* UPDATE_EMP */

                { /* TEST_ARRAY */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_ARRAY","ITEMS",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT", (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of reseult rows to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_ARRAY","ELEMENTS",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of Items in each array to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_ARRAY","WCHAR_FIELD",
                    (short)SQL_RESULT_COL, (short)XO_TYPE_WVARCHAR,"WVARCHAR", 32, 32, (short)DAMOBJ_NOTSET,(short)0,
                    (short)XO_NULLABLE,null,"CHAR ARRAY");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_ARRAY","CHAR_ARRAY",
                    (short)SQL_RESULT_COL, (short)jdam.xo_make_array_type(XO_TYPE_VARCHAR),"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)10,
                    (short)XO_NULLABLE,null,"CHAR ARRAY");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_ARRAY","INTEGER_ARRAY",
                    (short)SQL_RESULT_COL, (short)jdam.xo_make_array_type(XO_TYPE_INTEGER),"INTEGER", 4, 10, (short)DAMOBJ_NOTSET,(short)10,
                    (short)XO_NULLABLE,null,"CHAR ARRAY");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_ARRAY","DOUBLE_ARRAY",
                    (short)SQL_RESULT_COL, (short)jdam.xo_make_array_type(XO_TYPE_DOUBLE),"DOUBLE", 8, 15, (short)DAMOBJ_NOTSET,(short)10,
                    (short)XO_NULLABLE,null,"CHAR ARRAY");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_ARRAY","VARIANT_ARRAY",
                    (short)SQL_RESULT_COL, (short)jdam.xo_make_array_type(XO_TYPE_VARIANT),"VARIANT", 32, 32, (short)DAMOBJ_NOTSET,(short)10,
                    (short)XO_NULLABLE,null,"CHAR ARRAY");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);



                } /* TEST_ARRAY */

                /* LOOP */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","LOOP","COUNT",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"loop count*million times");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                { /* TEST_STRING_PARAM */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_STRING_PARAM","ARG1",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Arg1");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_STRING_PARAM","ARG2",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Arg2");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_STRING_PARAM","ARG3",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Arg3");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_STRING_PARAM","ARG4",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Arg4");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_STRING_PARAM","ARG5",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Arg5");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","TEST_STRING_PARAM","RES",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Result");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* TEST_STRING_PARAM */

		{ /* MULTIRESULT_REGULAR */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_REGULAR","ITEMS",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT",19,8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result rows to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

		ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_REGULAR","NAME",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

		ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_REGULAR","ID",
                    (short)SQL_RESULT_COL, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"ID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_REGULAR","RETVAL",
                    (short)SQL_RETURN_VALUE, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result rows to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);
                } /* MULTIRESULT_REGULAR */

                { /* MULTIRESULT */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT","RESULTS",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT","ITEMS",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT",8,19, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT","TEMP",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"TEMP field");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT","RETVAL",
                    (short)SQL_RETURN_VALUE, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);
                } /* MULTIRESULT */

                { /* MULTIRESULT_OUT */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_OUT","RESULTS",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_OUT","ITEMS",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT",8,19, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_OUT","TEMP",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"TEMP field");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_OUT","RETVAL",
                    (short)SQL_RETURN_VALUE, (short)4,"INTEGER", 4,10, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_OUT","OUTVAL",
                    (short)SQL_PARAM_OUTPUT, (short)4,"INTEGER",4,10, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);
                } /* MULTIRESULT_OUT */

                { /* CHECK_STATUS_INT */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","CHECK_STATUS_INT","INPUT_VAL",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number to check status");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","CHECK_STATUS_INT","RET_VAL",
                    (short)SQL_RETURN_VALUE, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* CHECK_STATUS_INT */

                { /* CHECK_STATUS_CHAR */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","CHECK_STATUS_CHAR","INPUT_VAL",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number to check status");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","CHECK_STATUS_CHAR","RET_VAL",
                    (short)SQL_RETURN_VALUE, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"String to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","CHECK_STATUS_CHAR","RES_COL",
                    (short)SQL_RESULT_COL, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"TEMP field");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* CHECK_STATUS_CHAR */

                { /* PROC_OUT_RET */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_OUT_RET","INPUT_VAL",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Input Number");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_OUT_RET","RET_VAL",
                    (short)SQL_RETURN_VALUE, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"String to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_OUT_RET","OUT_VAL",
                    (short)SQL_PARAM_OUTPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Output Number");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* PROC_OUT_RET */

                { /* DYN_PROC */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","DYN_PROC","RESULTSETS",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","DYN_PROC","DETAIL",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Return with Detail flag");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","DYN_PROC", "TEMP",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"TEMP field");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* DYN_PROC */

                { /* PROC_MULTI_OUT */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_MULTI_OUT","RESULTSETS",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_MULTI_OUT","ROWS",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of rows to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_MULTI_OUT","TEMP",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"TEMP field");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_MULTI_OUT","OUTVAL1",
                    (short)SQL_PARAM_OUTPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_MULTI_OUT","OUTVAL2",
                    (short)SQL_PARAM_OUTPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_MULTI_OUT","OUTVAL3",
                    (short)SQL_PARAM_OUTPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* PROC_MULTI_OUT */

                { /* PROC_GTECH */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_GTECH","RESULTSETS",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_GTECH","INPUT1",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Input1");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_GTECH","INPUT2",
                    (short)SQL_PARAM_INPUT, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Input2");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_GTECH","TEMP",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"TEMP field");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_GTECH","RETVAL",
                    (short)SQL_RETURN_VALUE, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Return Value");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* PROC_GTECH */

                { /* PROC_SKIP_RESULTCOLS */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_SKIP_RESULTCOLS","RESULTSETS",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_SKIP_RESULTCOLS","ROWS",
                    (short)SQL_PARAM_INPUT, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of rows to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_SKIP_RESULTCOLS","TEMP",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"TEMP field");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_SKIP_RESULTCOLS","RETVAL",
                    (short)SQL_RETURN_VALUE, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* PROC_SKIP_RESULTCOLS */

                { /* PROC_EMPTY_RESULTSETS */
                /* Add Proc column definitions */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_EMPTY_RESULTSETS","RESULTSETS",
                    (short)SQL_PARAM_INPUT, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number of result sets to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_EMPTY_RESULTSETS","TEMP",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"TEMP field");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_EMPTY_RESULTSETS","RETVAL",
                    (short)SQL_RETURN_VALUE, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Number to Return");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                } /* PROC_EMPTY_RESULTSETS */

                /* DYN_PROC_NO_ARGS */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","DYN_PROC_NO_ARGS", "TEMP",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"TEMP field");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);


            }
            break;
            default:
            break;
        }

        return IP_SUCCESS;
    }

private int      mem_ip_print_schema_object(int iType, Object pSearchObj)
{

    switch (iType) {
    case DAMOBJ_TYPE_CATALOG:
        System.out.println("[MEM IP] Catalog list requested\n");
        break;
    case DAMOBJ_TYPE_SCHEMA:
        System.out.println("[MEM IP] Schema list requested\n");
        break;
    case DAMOBJ_TYPE_TABLETYPE:
        System.out.println("[MEM IP] Table Types list requested\n");
        break;
    case DAMOBJ_TYPE_TABLE:
        {
        schemaobj_table pSearchTableObj = null;
        String str = "";

        pSearchTableObj = (schemaobj_table)pSearchObj;

        if (pSearchTableObj != null) {
            str = jdam.dam_isSearchPatternObject(pSearchObj) == 1 ? "Pattern SearchObject" : "Non-Pattern SearchObject";
            System.out.println("[MEM IP] Table Schema requested with " + str);
            System.out.println("[MEM IP] Table SearchObject:("
                    +pSearchTableObj.getTableQualifier()+"."+pSearchTableObj.getTableOwner()+"."+pSearchTableObj.getTableName()
                    +")");
            }
        else {
            System.out.println("[MEM IP] Full Table Schema requested\n");
            }
        }
        break;

    case DAMOBJ_TYPE_COLUMN:
        {
        schemaobj_column pSearchColumnObj = null;
        String str = "";

        if(pSearchObj != null)
            pSearchColumnObj = (schemaobj_column)pSearchObj;

        if (pSearchColumnObj != null) {
            str = jdam.dam_isSearchPatternObject(pSearchObj) == 1 ? "Pattern SearchObject" : "Non-Pattern SearchObject";
            System.out.println("[MEM IP] Column Schema requested with " + str);
            System.out.println("[MEM IP] Column SearchObject:("
                      +pSearchColumnObj.getTableQualifier()+"."+pSearchColumnObj.getTableOwner()+"."+pSearchColumnObj.getTableName()+"."
                      +pSearchColumnObj.getColumnName()+")");
            }
        else {
            System.out.println("[MEM IP] Full Column Schema requested\n");
            }
        }
        break;

    case DAMOBJ_TYPE_STAT:
        {
        schemaobj_stat pSearchStatObj = null;

        pSearchStatObj = (schemaobj_stat)pSearchObj;

        if (pSearchStatObj != null) {
            System.out.println("[MEM IP] Statistics SearchObject:("
                        + pSearchStatObj.getTableQualifier()+"."+ pSearchStatObj.getTableOwner()+"."+pSearchStatObj.getTableName()
                        + ")");
            }
        else {
            System.out.println("[MEM IP] Full Statistics Schema requested\n");
            }
        }
        break;

    case DAMOBJ_TYPE_FKEY:
        {
        schemaobj_fkey pSearchFkeyObj = null;

        pSearchFkeyObj = (schemaobj_fkey)pSearchObj;

        if (pSearchFkeyObj != null) {
            System.out.println("[MEM IP] OA_FKEYS ForeignKeys for PKtable:("
                    +pSearchFkeyObj.getPKTableQualifier()+"."+pSearchFkeyObj.getPKTableOwner()+"."+pSearchFkeyObj.getPKTableName()
                    +") and FKtable:("
                    +pSearchFkeyObj.getFKTableQualifier()+"."+pSearchFkeyObj.getFKTableOwner()+"."+pSearchFkeyObj.getFKTableName()
                    +")");
            }
        else {
            System.out.println("[MEM IP] Full OA_FKEYS Schema requested\n");
            }
        }
        break;

    case DAMOBJ_TYPE_PKEY:
        {
        schemaobj_pkey pSearchPkeyObj = null;

        pSearchPkeyObj = (schemaobj_pkey)pSearchObj;

        if (pSearchPkeyObj != null) {
            System.out.println("[MEM IP] OA_FKEYS PrimaryKeys for PKtable:("
                    +pSearchPkeyObj.getPKTableQualifier()+"."+pSearchPkeyObj.getPKTableOwner()+"."+pSearchPkeyObj.getPKTableName()
                    +")");
            }
        else {
            System.out.println("[MEM IP] Full OA_FKEYS PrimaerKeys Schema requested\n");
            }
        }
        break;

    case DAMOBJ_TYPE_PROC:
        {
        schemaobj_proc pSearchProcObj = null;
        String str = "";

        pSearchProcObj = (schemaobj_proc)pSearchObj;

        if (pSearchProcObj != null) {
            str = jdam.dam_isSearchPatternObject(pSearchObj) == 1 ? "Pattern SearchObject" : "Non-Pattern SearchObject";
            System.out.println("[MEM IP] Procedure Schema requested with " + str);
            System.out.println("[MEM IP] Procedure SearchObject:("
                    +pSearchProcObj.getQualifier()+"."+pSearchProcObj.getOwner()+"."+pSearchProcObj.getProcName()
                    +")");
            }
        else {
            System.out.println("[MEM IP] Full Procedure Schema requested\n");
            }
        }
        break;

    case DAMOBJ_TYPE_PROC_COLUMN:
        {
        schemaobj_proccolumn pSearchProcColumnObj = null;
        String str = "";

        pSearchProcColumnObj = (schemaobj_proccolumn)pSearchObj;

        if (pSearchProcColumnObj != null) {
            str = jdam.dam_isSearchPatternObject(pSearchObj) == 1 ? "Pattern SearchObject" : "Non-Pattern SearchObject";
            System.out.println("[MEM IP] Procedure Column Schema requested with " + str);
            System.out.println("[MEM IP] Procedure Column SearchObject:("
                    +pSearchProcColumnObj.getQualifier()+"."+pSearchProcColumnObj.getOwner()+"."+pSearchProcColumnObj.getProcName()+"."+pSearchProcColumnObj.getColumnName()
                    +")");
            }
        else {
            System.out.println("[MEM IP] Full Procedure Column Schema requested\n");
            }
        }
        break;

    default:
        break;
    }

    System.out.println("\n");
    return 0;
}


/********************************************************************************************
    Method:          ipDDL()
    Description:     DDL implementation.
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int        ipDDL(long dam_hstmt, int iStmtType,xo_long piNumResRows)
    {
    long          pList;
//    int          iCount=1;

    /* chekc the DDL command type */
    switch (iStmtType) {
        case DAM_CREATE_TABLE:  System.out.println("[MEM IP] Handling CREATE TABLE command\n");
                                System.out.println("CREATE TABLE\n");
                                break;
        case DAM_ALTER_TABLE:   System.out.println("[MEM IP] Handling ALTER TABLE command\n");
                                System.out.println("ALTER TABLE\n");
                                break;
        case DAM_DROP_TABLE:    System.out.println("[MEM IP] Handling DROP TABLE command\n");
                                System.out.println("DROP TABLE\n");
                                break;
        case DAM_CREATE_INDEX:  System.out.println("[MEM IP] Handling CREATE INDEX command\n");
                                System.out.println("CREATE INDEX\n");
                                break;
        case DAM_DROP_INDEX:    System.out.println("[MEM IP] Handling DROP INDEX command\n");
                                System.out.println("DROP INDEX\n");
                                break;
        case DAM_CREATE_VIEW:   System.out.println("[MEM IP] Handling CREATE VIEW command\n");
                                return ip_ddl_create_view(dam_hstmt);

        case DAM_DROP_VIEW:     System.out.println("[MEM IP] Handling DROP VIEW command\n");
                                return DAM_SUCCESS;
        default:
            System.out.println("Invalid DDL command:" + iStmtType);
            return DAM_FAILURE;
        }


    /* walk through the schema object lists ...*/
    {
    schemaobj_table pObj;
    System.out.println("\nTable info...\n");
    pList = jdam.dam_getSchemaObjectList(dam_hstmt, DAMOBJ_TYPE_TABLE);
    pObj = (schemaobj_table)jdam.dam_getFirstSchemaObject(pList);
    while (pObj != null) {
        dam_print_schemaobj_table(pObj);
        pObj = (schemaobj_table)jdam.dam_getNextSchemaObject(pList);
        System.out.println("\n");
    }
    }

    {
    schemaobj_column pObj;

    System.out.println("\nColumn info...\n");
    pList = jdam.dam_getSchemaObjectList(dam_hstmt, DAMOBJ_TYPE_COLUMN);
    pObj = (schemaobj_column) jdam.dam_getFirstSchemaObject(pList);
    while (pObj != null) {
        dam_print_schemaobj_column(pObj);
        pObj = (schemaobj_column) jdam.dam_getNextSchemaObject(pList);
    }
    }

    {
    schemaobj_stat pObj;
    System.out.println("Stat info...\n");
    pList = jdam.dam_getSchemaObjectList(dam_hstmt, DAMOBJ_TYPE_STAT);
    pObj = (schemaobj_stat)jdam.dam_getFirstSchemaObject(pList);
    while (pObj != null) {
        dam_print_schemaobj_stat(pObj);
        pObj = (schemaobj_stat)jdam.dam_getNextSchemaObject(pList);
    }
    }

    {
    schemaobj_fkey pObj;
    System.out.println("Fkey info...\n");
    pList = jdam.dam_getSchemaObjectList(dam_hstmt, DAMOBJ_TYPE_FKEY);
    pObj = (schemaobj_fkey)jdam.dam_getFirstSchemaObject(pList);
    while (pObj != null) {
        dam_print_schemaobj_fkey(pObj);
        pObj = (schemaobj_fkey)jdam.dam_getNextSchemaObject(pList);
    }
    }
		if(giTestMode == MEM_TEST_64BIT_ROWCOUNT) {
			piNumResRows.setVal(1 + RowCount);
		}
		else {
			piNumResRows.setVal(1);
		}

    return DAM_SUCCESS;
    }

/********************************************************************************************
    Method:          ipProcedure()
    Description:     Regular Stored procedures
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int        ipProcedure(long dam_hstmt, int iType, xo_long piNumResRows)
    {
        StringBuffer    sProcName;
        long            hrow, hRowElem;
        int             iParamNum;
        long            hcol;
        xo_int          piXOType, piColumnType;
        xo_int          iValueStatus;

        long            iItems;
        long            iElements;
        int             iRetVal = 0;
        int             iRetCode;
        boolean         bEmpProcedure = false;
        boolean         bCheckStatusInt = false;
        boolean         bCheckStatusChar = false;
        boolean         bProcOutRet = false;
        boolean         bArrayProcedure = false;


        sProcName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        piXOType = new xo_int(0);
        piColumnType = new xo_int(0);
        iValueStatus = new xo_int(0);
        iItems = 0;


        /* get the procedure information */
        jdam.dam_describeProcedure(dam_hstmt, null, null, sProcName,null);
        System.out.println("[MEM IP] Procedure=" + sProcName);
        if (sProcName.toString().equalsIgnoreCase("QUERY_EMP") ||
            sProcName.toString().equalsIgnoreCase("FORMAT_QUERY") ||
            sProcName.toString().equalsIgnoreCase("LOOP") ||
            sProcName.toString().equalsIgnoreCase("TEST_STRING_PARAM"))
        {
        jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, "Queries on Stored Procedure are not supported.");
        return DAM_FAILURE;
        }

	if (sProcName.toString().equalsIgnoreCase("MULTIRESULT_REGULAR")) {
            return mem_procedure_multiresult_regular(dam_hstmt, iType, piNumResRows);
	}
        if (sProcName.toString().equalsIgnoreCase("UPDATE_EMP")) bEmpProcedure = true;
        if (sProcName.toString().equalsIgnoreCase("TEST_ARRAY")) bArrayProcedure = true;
        if (sProcName.toString().equalsIgnoreCase("CHECK_STATUS_INT")) bCheckStatusInt = true;
        if (sProcName.toString().equalsIgnoreCase("CHECK_STATUS_CHAR")) bCheckStatusChar = true;
        if (sProcName.toString().equalsIgnoreCase("PROC_OUT_RET")) bProcOutRet = true;

        hrow = jdam.dam_getInputRow(dam_hstmt);

       /* Get and print the value of the input parameters */
        if (hrow != 0) {
            iParamNum=1;
            for (hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow); hRowElem != 0;
                    hRowElem = jdam.dam_getNextValueSet(dam_hstmt)) {

                String  sVal;

                hcol = jdam.dam_getColToSet(hRowElem);
                jdam.dam_describeCol(hcol, null, null, piXOType, null);
                jdam.dam_describeColDetail(hcol, null, piColumnType, null);
                if (piColumnType.getVal() != SQL_PARAM_INPUT) continue;

                sVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
                System.out.println("[MEM IP] Param #" + iParamNum +  "=" + sVal);

                if (bEmpProcedure) {
		            if ( iParamNum == 4) {
                       	iItems = Integer.valueOf(sVal);
                        }
                    }
                if (bArrayProcedure) {
		            if ( iParamNum == 1) iItems = Integer.valueOf(sVal);
		            if ( iParamNum == 2) iElements = Integer.valueOf(sVal);
                    }
        		if (iParamNum == 1 && (bCheckStatusInt || bCheckStatusChar || bProcOutRet)) iRetVal = Integer.valueOf(sVal);

                iParamNum++;
                }
            }

        /* return results for UPDATE_EMP procedure */
         if (bEmpProcedure) {
            int         lCount;
            long        hRow;
            long        hColEname, hColDeptId, hColItems;
            String      sName;
            int         iColCount;

            hColEname = jdam.dam_getCol(dam_hstmt, "NEW_ENAME");
            hColDeptId = jdam.dam_getCol(dam_hstmt, "NEW_DEPTID");
            hColItems = jdam.dam_getCol(dam_hstmt, "NEW_ITEMS");

            iColCount = (int)(iItems%4);
            for (lCount = 1; lCount <= iItems; lCount++) {

                hRow = jdam.dam_allocRow(dam_hstmt);

                sName = "Emp" + lCount;
                if (iColCount != 0)
                    iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColEname, sName, XO_NTS);
                if (iColCount != 1)
                    iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hRow, hColDeptId , lCount, 0);
                if (iColCount > 2)
                    iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColItems, lCount, 0);
                jdam.dam_addRowToTable(dam_hstmt, hRow);
                }

            }

        /* return results for CHECK_STATUS_INT procedure */
         if (bCheckStatusInt) {
            long        hOutputRow;
            long        hColRetVal;

            hColRetVal = jdam.dam_getCol(dam_hstmt, "RET_VAL");

            /* build output row */
            hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hOutputRow, hColRetVal, iRetVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            }

        /* return results for CHECK_STATUS_CHAR procedure */
         if (bCheckStatusChar) {
            long        hOutputRow, hRow;
            long        hColRetVal,hColResCol;
            String      sRetVal=  "FALSE";

            hColRetVal = jdam.dam_getCol(dam_hstmt, "RET_VAL");
            hColResCol = jdam.dam_getCol(dam_hstmt, "RES_COL");

            hRow = jdam.dam_allocRow(dam_hstmt);

            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hRow, hColResCol, iRetVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addRowToTable(dam_hstmt, hRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            if(iRetVal%2 != 0)
                sRetVal = "TRUE";

            /* build output row */
            hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hOutputRow, hColRetVal, sRetVal, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            }

        /* return results for PROC_OUT_RET procedure */
        if (bProcOutRet) {

            long        hOutputRow;
            long        hColRetVal,hColOutVal;
            String      sRetVal = "FAILURE";

            hColRetVal = jdam.dam_getCol(dam_hstmt, "RET_VAL");
            hColOutVal = jdam.dam_getCol(dam_hstmt, "OUT_VAL");

            if(iRetVal%2 != 0)
                sRetVal = "SUCCESS";

            /* build output row */
            hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hOutputRow, hColOutVal, iRetVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hOutputRow, hColRetVal, sRetVal, XO_NTS);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

        }

            return IP_SUCCESS;
    }

/********************************************************************************************
    Method:          ipDCL
    Description:     DCL
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int        ipDCL(long dam_hstmt, int iStmtType,xo_long piNumResRows)
    {
    StringBuffer    sCatalog; /* Schema name */
    StringBuffer    sUserName;    /* User name */
    StringBuffer    sPassword;    /* Password */
    StringBuffer 	sUserData;       /* User's data */
    StringBuffer 	sRoleName;       /* Role Name */

    sCatalog =  new StringBuffer();
    sUserName = new StringBuffer();
    sPassword = new StringBuffer();
    sUserData = new StringBuffer();
    sRoleName = new StringBuffer();

    /* check the DCL command type */
    switch (iStmtType) {

        case DAM_CREATE_USER:
        	System.out.println("[MEM IP] Handling CREATE USER command\n");
        	jdam.dam_getUser(dam_hstmt, sCatalog, sUserName, sPassword, sUserData);
        	System.out.println("[MEM IP]");
        	mem_print_user(sCatalog, sUserName, sPassword, sUserData);
        	System.out.println("\n");
        	break;

        case DAM_DROP_USER:
        	System.out.println("[MEM IP] Handling DROP USER command\n");
        	jdam.dam_getUser(dam_hstmt, sCatalog, sUserName, sPassword, sUserData);
        	System.out.println("[MEM IP]");
        	mem_print_user(sCatalog, sUserName, sPassword, sUserData);
        	System.out.println("\n");
        	break;

        case DAM_CREATE_ROLE:
        	System.out.println("[MEM IP] Handling CREATE ROLE command\n");
        	jdam.dam_getRole(dam_hstmt, sCatalog, sRoleName);
        	System.out.println("[MEM IP]");
        	mem_print_role(sCatalog, sRoleName);
        	System.out.println("\n");
        	break;

        case DAM_DROP_ROLE:
        	System.out.println("[MEM IP] Handling DROP ROLE command\n");
        	jdam.dam_getRole(dam_hstmt, sCatalog, sRoleName);
        	System.out.println("[MEM IP]");
        	mem_print_role(sCatalog, sRoleName);
        	System.out.println("\n");
        	break;


       default:
            System.out.println("Invalid DCL command:" + iStmtType);
            return DAM_FAILURE;
        }

	if(giTestMode == MEM_TEST_64BIT_ROWCOUNT)
		{
			piNumResRows.setVal(1 + RowCount);
		}
	else
		{
			piNumResRows.setVal(1);
		}

    return DAM_SUCCESS;

}

/********************************************************************************************
    Method:          ipPrivilege
    Description:     Privilege
    Return:          IP_SUCCESS on success and IP_FAILURE on failure

*********************************************************************************************/
    public int        ipPrivilege(int iStmtType,String pcUserName,String pcCatalog,String pcSchema,String pcObjName)
    {
                return IP_FAILURE;
    }

/********************************************************************************************
    Method:          ipNative()
    Description:     Native SQL
    Return:          IP_SUCCESS on success and IP_FAILURE on failure

*********************************************************************************************/
    public int        ipNative(long dam_hstmt, int iCommandOption, String sCommand, xo_long piNumResRows)
    {
    MEM_NC_DA   	pNC;
    int             idx;
    String          sBuf = "";

    jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipNative called\n");
	if (iCommandOption == DAM_NC_VALIDATE) {
        pNC = new MEM_NC_DA();

        idx = getNativeIndex();
        if(idx >= 0) {
            nativeDA[idx] = pNC;
            }

		/* initialize the StmtDA */
        pNC.sCmd.delete(0, pNC.sCmd.length());
        pNC.sCmd.append(sCommand);

		/* register the stmt handle */
        jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the index*/

		if (pNC.sCmd.toString().indexOf("SET") == 0) {
			if (pNC.sCmd.toString().indexOf("SET M") == 0) {
				return DAM_SUCCESS;
				}
			else {
                sBuf = "Mem IP only supports native commands:SET M";
                jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, sBuf);
                jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);
                return DAM_FAILURE;
				}
			}
		else
			return DAM_NOT_AVAILABLE;
		}
	else if (iCommandOption == DAM_NC_EXECUTE) {
        idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
		pNC = nativeDA[idx]; /* get the StmtDA */

		/* check if the command string matches */
        if (!pNC.sCmd.toString().equalsIgnoreCase(sCommand))
			return DAM_FAILURE;
	if(giTestMode == MEM_TEST_64BIT_ROWCOUNT) {
			piNumResRows.setVal(1 + RowCount);
		}
	else {
			piNumResRows.setVal(1);
		}
		return DAM_SUCCESS;
		}

	return DAM_FAILURE;
    }

/********************************************************************************************
    Method:          ipSchemaEx()
    Description:     Dynamic Stored Procedure implementation
    Return:          IP_SUCCESS on success and IP_FAILURE on failure

*********************************************************************************************/
    public int        ipSchemaEx(long dam_hstmt, long pMemTree, int iType, long pList,Object pSearchObj)
    {
        switch(iType)
        {
           case DAMOBJ_TYPE_PROC_COLUMN:
            {
                schemaobj_proccolumn ProcColumnObj = new schemaobj_proccolumn();
                int         iColCount;
                int         idx = -1;
                MEM_PROC_DA pProcDA;

                idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
                if (idx < 0) return DAM_SUCCESS;
                pProcDA = procDA[idx];

                schemaobj_proccolumn pSearchProcColumnObj = null;
                pSearchProcColumnObj = (schemaobj_proccolumn)pSearchObj;

                if (pSearchProcColumnObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for column <"+pSearchProcColumnObj.getColumnName()+"> of Procedure:<"+pSearchProcColumnObj.getQualifier()+"."+pSearchProcColumnObj.getOwner()+"."+pSearchProcColumnObj.getProcName()+"> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all columns of all Procedures is being requested\n");
                }

                /* Columns of MULTIRESULT */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT","NAME",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT","ID",
                    (short)SQL_RESULT_COL, (short)-5,"INTEGER", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"ID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                /* add dynamic columns MULTIRESULT */
                for (iColCount = 0; iColCount <= pProcDA.iCurResultSetNum; iColCount++) {
                    String  sColName;

                    sColName = "D" + iColCount;
                    ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT", sColName,
                        (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,null,"Name of the Employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);
                    }

                /* Columns of MULTIRESULT_OUT */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_OUT","NAME",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_OUT","ID",
                    (short)SQL_RESULT_COL, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"ID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                /* add dynamic columns MULTIRESULT_OUT */
                for (iColCount = 0; iColCount <= pProcDA.iCurResultSetNum; iColCount++) {
                    String  sColName;

                    sColName = "D" + iColCount;
                    ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","MULTIRESULT_OUT", sColName,
                        (short)SQL_RESULT_COL, (short)-5,"BIGINT", (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,null,"Dynamic Column");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);
                    }

                /* Columns of DYN_PROC */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","DYN_PROC","ID",
                    (short)SQL_RESULT_COL, (short)4,"INTEGER", 10, 4, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"ID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","DYN_PROC","NAME",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                /* add dynamic columns DYN_PROC */
                if ( pProcDA.iItems > 0) {
                    ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","DYN_PROC","Age",
                        (short)SQL_RESULT_COL, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,null,"Age of the employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                    ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","DYN_PROC","Salary",
                        (short)SQL_RESULT_COL, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,null,"Salary of the employee");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                    ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","DYN_PROC", "DateOfJoining",
                        (short)SQL_RESULT_COL, (short)9,"DATE", (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,null,"Date Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);
                    }


                /* Columns of PROC_MULTI_OUT */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_MULTI_OUT","NAME",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_MULTI_OUT","ID",
                    (short)SQL_RESULT_COL, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"ID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_MULTI_OUT","AGE",
                    (short)SQL_RESULT_COL, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Age of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                /* add dynamic columns PROC_GTECH */
                if (pProcDA.iTotalResultSets > 0) {
                for (iColCount = 0; iColCount <= pProcDA.iCurResultSetNum; iColCount++) {
                    String  sColName;

                    sColName = "Column " + iColCount+1;
                    ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_GTECH", sColName,
                        (short)SQL_RESULT_COL, (short)4,"INTEGER", (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,null,"Column values");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);
                    }
                }

                /* Columns of PROC_SKIP_RESULTCOLS */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_SKIP_RESULTCOLS","NAME",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_SKIP_RESULTCOLS","ID",
                    (short)SQL_RESULT_COL, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"ID of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_SKIP_RESULTCOLS","AGE",
                    (short)SQL_RESULT_COL, (short)-5,"BIGINT", 19, 8, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"Age of the employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_SKIP_RESULTCOLS","DATE",
                    (short)SQL_RESULT_COL, (short)9,"DATE", (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NULLABLE,null,"date Value");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_SKIP_RESULTCOLS","REMARKS",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);

                /* Columns of PROC_EMPTY_RESULTSETS */
                ProcColumnObj.SetObjInfo("SCHEMA","OAUSER","PROC_EMPTY_RESULTSETS","NAME",
                    (short)SQL_RESULT_COL, (short)12,"VARCHAR", 255, 255, (short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                    (short)XO_NO_NULLS,null,"Name of the Employee");
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ProcColumnObj);


                break;
                }
            default:
                break;
            }

        return IP_SUCCESS;
    }

/********************************************************************************************
    Method:          ipProcedureDynamic()
    Description:     Dynamic Stored Procedure implementation
                     for each Dynamic stored procedure
    Return:          IP_SUCCESS on success and IP_FAILURE on failure
*********************************************************************************************/
    public int        ipProcedureDynamic(long dam_hstmt, int iType, xo_long piNumResRows)
    {
        StringBuffer        sProcName;
        int                 iRetCode;
        int                 idx = -1;

        if (iType == DAM_DSP_INIT) {
            jdam.dam_setIP_hstmt(dam_hstmt, idx); /* reset the ProcDA index*/
            }
        sProcName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

        jdam.dam_describeProcedure(dam_hstmt, null, null, sProcName,null);
        System.out.println("[MEM IP] Procedure=" + sProcName);
        if (sProcName.toString().equalsIgnoreCase("MULTIRESULT")) {
            iRetCode = mem_procedure_multiresult(dam_hstmt, iType, piNumResRows);
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            }
        else if (sProcName.toString().equalsIgnoreCase("MULTIRESULT_OUT")) {
            iRetCode = mem_procedure_multiresult_out(dam_hstmt, iType, piNumResRows);
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            }
        else if (sProcName.toString().equalsIgnoreCase("DYN_PROC")) {
            iRetCode = mem_procedure_dyn_proc(dam_hstmt, iType, piNumResRows);
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            }
        else if (sProcName.toString().equalsIgnoreCase("PROC_MULTI_OUT")) {
            iRetCode = mem_procedure_proc_multi_out(dam_hstmt, iType, piNumResRows);
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            }
        else if (sProcName.toString().equalsIgnoreCase("PROC_GTECH")) {
            iRetCode = mem_procedure_proc_gtech(dam_hstmt, iType, piNumResRows);
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            }
        else if (sProcName.toString().equalsIgnoreCase("PROC_SKIP_RESULTCOLS")) {
            iRetCode = mem_procedure_proc_skip_resultcols(dam_hstmt, iType, piNumResRows);
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            }
        else if (sProcName.toString().equalsIgnoreCase("PROC_EMPTY_RESULTSETS")) {
            iRetCode = mem_procedure_proc_empty_resultsets(dam_hstmt, iType, piNumResRows);
            if (iRetCode != DAM_SUCCESS) return iRetCode;
            }
        else if (sProcName.toString().equalsIgnoreCase("DYN_PROC_NO_ARGS")) {
            jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, "Queries on Stored Procedure are not supported.");
            return DAM_FAILURE;
            }

        return DAM_SUCCESS;
    }


    /********************************************************************************************
	Method:          mem_procedure_multiresult_regular()
    *********************************************************************************************/
	public int        mem_procedure_multiresult_regular(long dam_hstmt, int iType, xo_long piNumResRows)
	{
	MEM_PROC_DA     pProcDA;
	int             iRetCode;
	int             idx;


	if (iType == DAM_PROCEDURE) {
	    long     pMemTree;

	    pMemTree = jdam.dam_getMemTree(dam_hstmt);
	    pProcDA = new MEM_PROC_DA();
	    pProcDA.dam_hstmt = dam_hstmt;
	    pProcDA.pMemTree = pMemTree;
	    jdam.dam_describeProcedure(dam_hstmt, pProcDA.sQualifier, pProcDA.sOwner, pProcDA.sProcName, pProcDA.sUserData);

	    /* get the statement handle */
	    idx = getProcIndex();
	    if(idx >= 0) {
		procDA[idx] = pProcDA;
		}

	    jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the StmtDA index*/
	    pProcDA.iItems = 0;

	     /* get the fetch block size for cursor mode */
	    {
		 xo_int  piValue;

		piValue = new xo_int();
		/* get fetch block size */
		iRetCode = jdam.dam_getInfo(0, pProcDA.dam_hstmt, DAM_INFO_FETCH_BLOCK_SIZE,
						null, piValue);
		if (iRetCode != DAM_SUCCESS)
		    pProcDA.iFetchSize = 2;
		else
		    pProcDA.iFetchSize = piValue.getVal();

		jdam.trace(m_tmHandle, UL_TM_INFO,"mem_procedure_multiresult() Fetch size = "+ pProcDA.iFetchSize + "\n");
	    }

	    /* get the input parameter info */
	    {
	    long            hrow, hRowElem;
	    long            hcol;
	    xo_int          piXOType, piColumnType;
	    xo_int          iValueStatus;

	    piXOType = new xo_int(0);
	    piColumnType = new xo_int(0);
	    iValueStatus = new xo_int(0);

	    hrow = jdam.dam_getInputRow(dam_hstmt);

	   /* Get and print the value of the input parameters */
	    for (hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow); hRowElem != 0;
		    hRowElem = jdam.dam_getNextValueSet(dam_hstmt)) {

		String  sVal;

		hcol = jdam.dam_getColToSet(hRowElem);
		jdam.dam_describeCol(hcol, null, null, piXOType, null);
		jdam.dam_describeColDetail(hcol, null, piColumnType, null);
		if (piColumnType.getVal() != SQL_PARAM_INPUT) continue;

		sVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
		pProcDA.iItems = Long.valueOf(sVal);
		}
	    }
	    }
	if (iType == DAM_PROCEDURE || iType == DAM_FETCH) {
	    long        hColName, hColId;
	    String      sName;
	    long        lVal;
	    long        iRowCount;
	    long        hRow;

	    idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
	    pProcDA = procDA[idx];

	    if (iType == DAM_PROCEDURE)
		 pProcDA.iCurItems = 0;

	    hColName = jdam.dam_getCol(dam_hstmt, "NAME");
	    hColId = jdam.dam_getCol(dam_hstmt, "ID");

	    /* build result set */
	    iRowCount = 0;
	    while (pProcDA.iCurItems < pProcDA.iItems) {
		hRow = jdam.dam_allocRow(dam_hstmt);

		sName = "Name" + "-" + pProcDA.iCurItems;
		lVal = pProcDA.iCurItems;
		iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColName, sName, XO_NTS);
		iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColId , lVal, 0);
		jdam.dam_addRowToTable(dam_hstmt, hRow);
		pProcDA.iCurItems++;
		iRowCount++;
		if (iRowCount >= pProcDA.iFetchSize)
		{
		    return DAM_SUCCESS_WITH_RESULT_PENDING;
		}
		}
	    {
		long        hcolRetVal;
		long        hOutputRow;

		hcolRetVal = jdam.dam_getCol(dam_hstmt, "RETVAL");

		/* build output row */
		hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
		lVal = pProcDA.iItems;
		iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hOutputRow, hcolRetVal , lVal, 0);
		if (iRetCode != DAM_SUCCESS) return iRetCode;

		iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
		if (iRetCode != DAM_SUCCESS) return iRetCode;


		}
	    }
	else if ( iType == DAM_CLOSE )
	{
	    idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
	    pProcDA = procDA[idx];
	}
	else {
	    return IP_FAILURE;
	    }

	/* when processing is fully done, release from Proc Array */
	if(idx >= 0) {
	   procDA[idx] = null;
	   }
	jdam.dam_setIP_hstmt(dam_hstmt, 0);
	return IP_SUCCESS;
	}
/********************************************************************************************
    Method:          mem_procedure_multiresult()
*********************************************************************************************/
    public int        mem_procedure_multiresult(long dam_hstmt, int iType, xo_long piNumResRows)
    {
    MEM_PROC_DA     pProcDA;
    int             iRetCode;
    int             idx;


    if (iType == DAM_DSP_INIT) {
        long     pMemTree;

        pMemTree = jdam.dam_getMemTree(dam_hstmt);
        pProcDA = new MEM_PROC_DA();
        pProcDA.dam_hstmt = dam_hstmt;
        pProcDA.pMemTree = pMemTree;
        jdam.dam_describeProcedure(dam_hstmt, pProcDA.sQualifier, pProcDA.sOwner, pProcDA.sProcName, pProcDA.sUserData);

        /* get the statement handle */
        idx = getProcIndex();
        if(idx >= 0) {
            procDA[idx] = pProcDA;
            }

        jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the StmtDA index*/
        pProcDA.iCurResultSetNum = 0;
        pProcDA.iItems = 0;

	 /* get the fetch block size for cursor mode */
	{
	     xo_int  piValue;

            piValue = new xo_int();
	    /* get fetch block size */
	    iRetCode = jdam.dam_getInfo(0, pProcDA.dam_hstmt, DAM_INFO_FETCH_BLOCK_SIZE,
                                            null, piValue);
	    if (iRetCode != DAM_SUCCESS)
		pProcDA.iFetchSize = 2;
	    else
		pProcDA.iFetchSize = piValue.getVal();

	    jdam.trace(m_tmHandle, UL_TM_INFO,"mem_procedure_multiresult() Fetch size = "+ pProcDA.iFetchSize + "\n");
	}

        /* get the input parameter info */
        {
        long            hrow, hRowElem;
        int             iParamNum;
        long            hcol;
        xo_int          piXOType, piColumnType;
        xo_int          iValueStatus;

        piXOType = new xo_int(0);
        piColumnType = new xo_int(0);
        iValueStatus = new xo_int(0);

        hrow = jdam.dam_getInputRow(dam_hstmt);

       /* Get and print the value of the input parameters */
        iParamNum=1;
        for (hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow); hRowElem != 0;
                hRowElem = jdam.dam_getNextValueSet(dam_hstmt)) {

            String  sVal;

            hcol = jdam.dam_getColToSet(hRowElem);
            jdam.dam_describeCol(hcol, null, null, piXOType, null);
            jdam.dam_describeColDetail(hcol, null, piColumnType, null);
            if (piColumnType.getVal() != SQL_PARAM_INPUT) continue;

            sVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
            System.out.println("[MEM IP] Param #" + iParamNum +  "=" + sVal);

            if (iParamNum == 1) pProcDA.iTotalResultSets = Integer.valueOf(sVal);
            if (iParamNum == 2) pProcDA.iItems = Integer.valueOf(sVal);
            iParamNum++;

            }
        }
        return IP_SUCCESS;
        }
    else if (iType == DAM_DSP_EXECUTE || iType == DAM_FETCH) {
        long        hColName, hColId, hColD;
        String      sName, sColName;
        long        lVal;
        long        iRowCount;
	int         iColCount;
        long        hRow;

        idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
        pProcDA = procDA[idx];

        if (iType == DAM_DSP_EXECUTE)
             pProcDA.iCurItems = 0;

        hColName = jdam.dam_getCol(dam_hstmt, "NAME");
        hColId = jdam.dam_getCol(dam_hstmt, "ID");

        /* build result set */
        iRowCount = 0;
        while (pProcDA.iCurItems < pProcDA.iItems) {
            hRow = jdam.dam_allocRow(dam_hstmt);

            sName = "Name" + pProcDA.iCurResultSetNum + "-" + pProcDA.iCurItems;
            lVal = pProcDA.iCurItems;
            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColName, sName, XO_NTS);
            iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColId , lVal, 0);
            for (iColCount = 0; iColCount <= pProcDA.iCurResultSetNum; iColCount++) {
                sColName = "D" +  iColCount;
                hColD = jdam.dam_getCol(dam_hstmt, sColName);
                lVal = iColCount;
                iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColD , lVal, 0);
                }
            jdam.dam_addRowToTable(dam_hstmt, hRow);
            pProcDA.iCurItems++;
	    iRowCount++;
	    if (iRowCount >= pProcDA.iFetchSize)
	    {
		return DAM_SUCCESS_WITH_RESULT_PENDING;
	    }
            }

        /* check if we have processed all result sets */
        pProcDA.iCurResultSetNum++;
        if (pProcDA.iCurResultSetNum < pProcDA.iTotalResultSets) {
            return DAM_SUCCESS_WITH_MORE_RESULT_SETS;
            }
        else {
            long        hcolRetVal;
            long        hOutputRow;

            hcolRetVal = jdam.dam_getCol(dam_hstmt, "RETVAL");

            /* build output row */
            hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
            lVal = pProcDA.iTotalResultSets;
            iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hOutputRow, hcolRetVal , lVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;


            }
        }
    else if ( iType == DAM_CLOSE )
    {
	idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
	pProcDA = procDA[idx];
    }
    else {
        return IP_FAILURE;
        }

    /* when processing is fully done, release from Proc Array */
    if(idx >= 0) {
       procDA[idx] = null;
       }
    jdam.dam_setIP_hstmt(dam_hstmt, 0);
    return IP_SUCCESS;
    }

/********************************************************************************************
    Method:          mem_procedure_multiresult_out()
*********************************************************************************************/
    public int        mem_procedure_multiresult_out(long dam_hstmt, int iType, xo_long piNumResRows)
    {
    MEM_PROC_DA     pProcDA;
    int             iRetCode;
    int             idx;


    if (iType == DAM_DSP_INIT) {
        long     pMemTree;

        pMemTree = jdam.dam_getMemTree(dam_hstmt);
        pProcDA = new MEM_PROC_DA();
        pProcDA.dam_hstmt = dam_hstmt;
        pProcDA.pMemTree = pMemTree;
        jdam.dam_describeProcedure(dam_hstmt, pProcDA.sQualifier, pProcDA.sOwner, pProcDA.sProcName, pProcDA.sUserData);

        /* get the statement handle */
        idx = getProcIndex();
        if(idx >= 0) {
            procDA[idx] = pProcDA;
            }

        jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the StmtDA index*/
        pProcDA.iCurResultSetNum = 0;

        /* get the input parameter info */
        {
        long            hrow, hRowElem;
        int             iParamNum;
        long            hcol;
        xo_int          piXOType, piColumnType;
        xo_int          iValueStatus;

        piXOType = new xo_int(0);
        piColumnType = new xo_int(0);
        iValueStatus = new xo_int(0);

        hrow = jdam.dam_getInputRow(dam_hstmt);

       /* Get and print the value of the input parameters */
        iParamNum=1;
        for (hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow); hRowElem != 0;
                hRowElem = jdam.dam_getNextValueSet(dam_hstmt)) {

            String  sVal;

            hcol = jdam.dam_getColToSet(hRowElem);
            jdam.dam_describeCol(hcol, null, null, piXOType, null);
            jdam.dam_describeColDetail(hcol, null, piColumnType, null);
            if (piColumnType.getVal() != SQL_PARAM_INPUT) continue;

            sVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
            System.out.println("[MEM IP] Param #" + iParamNum +  "=" + sVal);

            if (iParamNum == 1) pProcDA.iTotalResultSets = Integer.valueOf(sVal);
            if (iParamNum == 2) pProcDA.iItems = Integer.valueOf(sVal);
            iParamNum++;

            }
        }
        return IP_SUCCESS;
        }
    else if (iType == DAM_DSP_EXECUTE) {
        long        hColName, hColId, hColD;
        String      sName, sColName;
        long        lVal;
        long        iRowCount;
	int         iColCount;
        long        hRow;

        idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
        pProcDA = procDA[idx];

        hColName = jdam.dam_getCol(dam_hstmt, "NAME");
        hColId = jdam.dam_getCol(dam_hstmt, "ID");

        /* build result set */
        iRowCount = 0;
        while (iRowCount < pProcDA.iItems) {
            hRow = jdam.dam_allocRow(dam_hstmt);

            sName = "Name" + pProcDA.iCurResultSetNum + "-" + iRowCount;
            lVal = iRowCount;
            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColName, sName, XO_NTS);
            iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColId , lVal, 0);
            for (iColCount = 0; iColCount <= pProcDA.iCurResultSetNum; iColCount++) {
                sColName = "D" +  iColCount;
                hColD = jdam.dam_getCol(dam_hstmt, sColName);
                lVal = iColCount;
                iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hRow, hColD , (int)lVal, 0);
                }
            jdam.dam_addRowToTable(dam_hstmt, hRow);
            iRowCount++;
            }

        /* check if we have processed all result sets */
        pProcDA.iCurResultSetNum++;
        if (pProcDA.iCurResultSetNum < pProcDA.iTotalResultSets) {
            return DAM_SUCCESS_WITH_MORE_RESULT_SETS;
            }
        else {
            long        hcolRetVal, hcolOutVal;
            long        hOutputRow;

            hcolRetVal = jdam.dam_getCol(dam_hstmt, "RETVAL");
            hcolOutVal = jdam.dam_getCol(dam_hstmt, "OUTVAL");

            /* build output row */
            hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
            lVal = pProcDA.iTotalResultSets;
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hOutputRow, hcolRetVal , (int)lVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            lVal = pProcDA.iItems;
            iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hOutputRow, hcolOutVal , lVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* when processing is fully done, release from Proc Array */
            if(idx >= 0) {
               procDA[idx] = null;
               }
            return IP_SUCCESS;
            }
        }
    else {
        return IP_FAILURE;
        }

    }

/********************************************************************************************
    Method:          mem_procedure_dyn_proc()
*********************************************************************************************/
    public int        mem_procedure_dyn_proc(long dam_hstmt, int iType, xo_long piNumResRows)
    {
    MEM_PROC_DA     pProcDA;
    int             iRetCode;
    int             idx;


    if (iType == DAM_DSP_INIT) {
        long     pMemTree;

        pMemTree = jdam.dam_getMemTree(dam_hstmt);
        pProcDA = new MEM_PROC_DA();
        pProcDA.dam_hstmt = dam_hstmt;
        pProcDA.pMemTree = pMemTree;
        jdam.dam_describeProcedure(dam_hstmt, pProcDA.sQualifier, pProcDA.sOwner, pProcDA.sProcName, pProcDA.sUserData);

        /* get the statement handle */
        idx = getProcIndex();
        if(idx >= 0) {
            procDA[idx] = pProcDA;
            }

        jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the StmtDA index*/
        pProcDA.iCurResultSetNum = 0;

        /* get the input parameter info */
        {
        long            hrow, hRowElem;
        int             iParamNum;
        long            hcol;
        xo_int          piXOType, piColumnType;
        xo_int          iValueStatus;

        new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
        piXOType = new xo_int(0);
        piColumnType = new xo_int(0);
        iValueStatus = new xo_int(0);

        hrow = jdam.dam_getInputRow(dam_hstmt);

       /* Get and print the value of the input parameters */
        iParamNum=1;
        for (hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow); hRowElem != 0;
                hRowElem = jdam.dam_getNextValueSet(dam_hstmt)) {

            String  sVal;

            hcol = jdam.dam_getColToSet(hRowElem);
            jdam.dam_describeCol(hcol, null, null, piXOType, null);
            jdam.dam_describeColDetail(hcol, null, piColumnType, null);
            if (piColumnType.getVal() != SQL_PARAM_INPUT) continue;

            sVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
            System.out.println("[MEM IP] Param #" + iParamNum +  "=" + sVal);

            if (iParamNum == 1) pProcDA.iTotalResultSets = Integer.valueOf(sVal);
            if (iParamNum == 2) pProcDA.iItems = Integer.valueOf(sVal);
            iParamNum++;

            }
        }
        return IP_SUCCESS;
        }
    else if (iType == DAM_DSP_EXECUTE) {
        long        hColName, hColId;
        String      sName;
        long        lVal;
        long        iRowCount;
        long        hRow;

        idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
        pProcDA = procDA[idx];

        hColName = jdam.dam_getCol(dam_hstmt, "NAME");
        hColId = jdam.dam_getCol(dam_hstmt, "ID");

        /* build result set */
        iRowCount = 0;
        while (iRowCount < pProcDA.iTotalResultSets) {
            hRow = jdam.dam_allocRow(dam_hstmt);

            sName = "Name" + pProcDA.iCurResultSetNum + "-" + iRowCount;
            lVal = iRowCount;
            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColName, sName, XO_NTS);
            iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColId , lVal, 0);
            if (pProcDA.iItems > 0) {
                    long   hColAge, hColSalary, hColDOJ;
                    xo_tm dateVal = new xo_tm();;

                    hColAge = jdam.dam_getCol(dam_hstmt, "Age");
                    hColSalary = jdam.dam_getCol(dam_hstmt, "Salary");
                    hColDOJ = jdam.dam_getCol(dam_hstmt, "DateOfJoining");

                    lVal = iRowCount + 30;
                    jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColAge, lVal, 0);
                    lVal = (iRowCount+3) * 1000;
                    jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColSalary, lVal, 0);

                    dateVal.setVal(xo_tm.DAY_OF_MONTH, (int)(1 + iRowCount));
                    dateVal.setVal(xo_tm.MONTH, 0);
                    dateVal.setVal(xo_tm.YEAR, (int)(1990 + iRowCount));
                    jdam.dam_addTimeStampValToRow(dam_hstmt, hRow, hColDOJ, dateVal, 0);
                }
            jdam.dam_addRowToTable(dam_hstmt, hRow);
            iRowCount++;
            }

        /* check if we have processed all result sets */
        pProcDA.iCurResultSetNum++;
        if (pProcDA.iCurResultSetNum < pProcDA.iTotalResultSets) {
            return DAM_SUCCESS_WITH_MORE_RESULT_SETS;
            }
        else {
            /* when processing is fully done, release from Proc Array */
            if(idx >= 0) {
               procDA[idx] = null;
               }
            return IP_SUCCESS;
            }
        }
    else {
        return IP_FAILURE;
        }

    }

/********************************************************************************************
    Method:          mem_print_param_info()
*********************************************************************************************/
    public int        mem_print_param_info(long dam_hstmt, int iStmtType)
    {
        int iRetCode;

        xo_int  piProcessOrder;
        piProcessOrder = new xo_int();
        iRetCode = jdam.dam_getInfo(0, dam_hstmt, DAM_INFO_QUERY_PROCESS_ORDER, null, piProcessOrder);
        long iProcessOrder = piProcessOrder.getVal();
        if (iProcessOrder == 0) {
            String  strObject;
            Integer iObject;
            xo_tm   xoTime;
            Double  dObject;
            Float   fObject;
            Short   sObject;
            Boolean bObject;
            Byte    byObject;
            Long    lObject;

            xo_int          iValueStatus;
            xo_int          iXoType;
            xo_long         lValLen;
            xo_int          piParamCount;
            Object          pData;
            iValueStatus = new xo_int();
            iXoType = new xo_int();
            lValLen = new xo_long();
            piParamCount = new xo_int();

            iRetCode = jdam.dam_getOriginalQueryParamCount(dam_hstmt, piParamCount);
            jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Param count "+ piParamCount.getVal() + "\n");

            for (int iParamNum = 0; iParamNum < piParamCount.getVal(); ++iParamNum) {
              int cnt=0;
              pData = jdam.dam_getOriginalQueryParamValue(dam_hstmt, iParamNum, iXoType, lValLen, iValueStatus);
              StringBuffer outBuffer = new StringBuffer();
              switch (iXoType.getVal())
              {
                case ip.XO_TYPE_CHAR: /* pVal is a char literal */
                case ip.XO_TYPE_VARCHAR:
                case ip.XO_TYPE_NUMERIC:
                case ip.XO_TYPE_DECIMAL:
                  strObject = (String) pData;
                  outBuffer.append("'");
                  String resultStr = strObject.replaceAll("'","''");
                  outBuffer.append(resultStr);
                  outBuffer.append("'");
                  break;
                case ip.XO_TYPE_WCHAR: /* pVal is a wchar literal */
                case ip.XO_TYPE_WVARCHAR:
                  strObject = (String) pData;
                  outBuffer.append("N'").append(strObject).append("'");
                  break;
                case ip.XO_TYPE_INTEGER:  /* pVal is a integer literal */
                  iObject = (Integer) pData;
                  outBuffer.append(iObject.intValue());
                  break;
                case ip.XO_TYPE_SMALLINT: /* pVal is small integer literal */
                  sObject = (Short) pData;
                  outBuffer.append(sObject.shortValue());
                  break;
                case ip.XO_TYPE_FLOAT: /* pVal is a double literal */
                case ip.XO_TYPE_DOUBLE:
                  dObject = (Double) pData;
                  outBuffer.append(dObject.doubleValue());
                  break;
                case ip.XO_TYPE_REAL: /* pVal is a float literal */
                  fObject = (Float) pData;
                  outBuffer.append(fObject.floatValue());
                  break;
                case ip.XO_TYPE_DATE:
                  xoTime = (xo_tm)pData;
                  outBuffer.append("{d '").append(xoTime.getVal(xo_tm.YEAR)).append("-").append(xoTime.getVal(xo_tm.MONTH)+1).append("-").append(xoTime.getVal(xo_tm.DAY_OF_MONTH)).append("'}");
                  break;
                case ip.XO_TYPE_TIME:
                  xoTime = (xo_tm)pData;
                  outBuffer.append("{t '").append(" ").append(xoTime.getVal(xo_tm.HOUR)).append(":").append(xoTime.getVal(xo_tm.MINUTE)).append(":").append(xoTime.getVal(xo_tm.SECOND)).append("'}");
                  break;
                case ip.XO_TYPE_TIMESTAMP:
                  xoTime = (xo_tm)pData;
                  if (xoTime.getVal(xo_tm.FRACTION) > 0)
                  {
                    int     frac;

                    frac = (int) (xoTime.FRACTION * 0.000001);
                    outBuffer.append("{ts '").append(xoTime.getVal(xo_tm.YEAR)).append("-").append(xoTime.getVal(xo_tm.MONTH)+1).append("-").append(xoTime.getVal(xo_tm.DAY_OF_MONTH))
                      .append(" ").append(xoTime.getVal(xo_tm.HOUR)).append(":").append(xoTime.getVal(xo_tm.MINUTE)).append(":").append(xoTime.getVal(xo_tm.SECOND))
                      .append(".").append(xoTime.getVal(xo_tm.FRACTION)).append("'}");
                  }
                  else
                  {
                    outBuffer.append("{ts '").append(xoTime.getVal(xo_tm.YEAR)).append("-").append(xoTime.getVal(xo_tm.MONTH)+1).append("-").append(xoTime.getVal(xo_tm.DAY_OF_MONTH))
                      .append(" ").append(xoTime.getVal(xo_tm.HOUR)).append(":").append(xoTime.getVal(xo_tm.MINUTE)).append(":").append(xoTime.getVal(xo_tm.SECOND)).append("'}");
                  }

                  break;

                case ip.XO_TYPE_BIT:
                  bObject = (Boolean)pData;
                  outBuffer.append(bObject.booleanValue()?1:0);
                  break;

                case ip.XO_TYPE_TINYINT:

                  byObject = (Byte)pData;
                  outBuffer.append(byObject.byteValue());
                  break;

                case ip.XO_TYPE_BIGINT:
                  lObject = (Long)pData;
                  outBuffer.append(lObject.longValue());
                  break;

                case XO_TYPE_BINARY:
                case XO_TYPE_VARBINARY:
                case XO_TYPE_LONGVARBINARY:
                  outBuffer.append("chunk#").append("[").append(cnt).append("]");
                  pData = jdam.dam_getOriginalQueryParamValue(dam_hstmt, iParamNum, iXoType, lValLen, iValueStatus);
                  while(iValueStatus.getVal() == DAM_SUCCESS_WITH_RESULT_PENDING) {
                    outBuffer.append("chunk#").append("[").append(++cnt).append("] ");
                    pData = jdam.dam_getOriginalQueryParamValue(dam_hstmt, iParamNum, iXoType, lValLen, iValueStatus);
                  }
                  if (iValueStatus.getVal() == DAM_FAILURE) return iValueStatus.getVal();

                default:
                  outBuffer.append("Invalid Xo Value Type:").append(iXoType.getVal());
                  break;
              }
              jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Param #" + iParamNum + "= <" + outBuffer.toString() + ">\n");
            }
        }

        return DAM_SUCCESS;
    }

/********************************************************************************************
    Method:          mem_procedure_proc_multi_out()
*********************************************************************************************/
    public int        mem_procedure_proc_multi_out(long dam_hstmt, int iType, xo_long piNumResRows)
    {
    MEM_PROC_DA     pProcDA;
    int             iRetCode;
    int             idx;


    if (iType == DAM_DSP_INIT) {
        long     pMemTree;

        pMemTree = jdam.dam_getMemTree(dam_hstmt);
        pProcDA = new MEM_PROC_DA();
        pProcDA.dam_hstmt = dam_hstmt;
        pProcDA.pMemTree = pMemTree;
        jdam.dam_describeProcedure(dam_hstmt, pProcDA.sQualifier, pProcDA.sOwner, pProcDA.sProcName, pProcDA.sUserData);

        /* get the statement handle */
        idx = getProcIndex();
        if(idx >= 0) {
            procDA[idx] = pProcDA;
            }

        jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the StmtDA index*/
        pProcDA.iCurResultSetNum = 0;

        /* get the input parameter info */
        {
        long            hrow, hRowElem;
        int             iParamNum;
        long            hcol;
        xo_int          piXOType, piColumnType;
        xo_int          iValueStatus;

        piXOType = new xo_int(0);
        piColumnType = new xo_int(0);
        iValueStatus = new xo_int(0);

        hrow = jdam.dam_getInputRow(dam_hstmt);

       /* Get and print the value of the input parameters */
        iParamNum=1;
        for (hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow); hRowElem != 0;
                hRowElem = jdam.dam_getNextValueSet(dam_hstmt)) {

            String  sVal;

            hcol = jdam.dam_getColToSet(hRowElem);
            jdam.dam_describeCol(hcol, null, null, piXOType, null);
            jdam.dam_describeColDetail(hcol, null, piColumnType, null);
            if (piColumnType.getVal() != SQL_PARAM_INPUT) continue;

            sVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
            System.out.println("[MEM IP] Param #" + iParamNum +  "=" + sVal);

            if (iParamNum == 1) pProcDA.iTotalResultSets = Integer.valueOf(sVal);
            if (iParamNum == 2) pProcDA.iItems = Integer.valueOf(sVal);
            iParamNum++;

            }
        }
        return IP_SUCCESS;
        }
    else if (iType == DAM_DSP_EXECUTE) {
        long        hColName, hColId, hColAge;
        String      sName;
        long         lVal;
        long        iRowCount;
        long        hRow;

        idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
        pProcDA = procDA[idx];

        hColName = jdam.dam_getCol(dam_hstmt, "NAME");
        hColId = jdam.dam_getCol(dam_hstmt, "ID");
        hColAge = jdam.dam_getCol(dam_hstmt, "AGE");

        /* build result set */
        iRowCount = 0;
        while (iRowCount < pProcDA.iItems) {
            hRow = jdam.dam_allocRow(dam_hstmt);

            sName = "Name" + pProcDA.iCurResultSetNum + "-" + iRowCount;
            lVal = iRowCount;
            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColName, sName, XO_NTS);
            iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColId , lVal, 0);
            lVal = iRowCount+1;
            iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColAge , lVal, 0);

            jdam.dam_addRowToTable(dam_hstmt, hRow);
            iRowCount++;
            }

        /* check if we have processed all result sets */
        pProcDA.iCurResultSetNum++;
        if (pProcDA.iCurResultSetNum < pProcDA.iTotalResultSets) {
            return DAM_SUCCESS_WITH_MORE_RESULT_SETS;
            }
        else {
            long        hcolOutVal1, hcolOutVal2, hcolOutVal3;
            long        hOutputRow;

            hcolOutVal1 = jdam.dam_getCol(dam_hstmt, "OUTVAL1");
            hcolOutVal2 = jdam.dam_getCol(dam_hstmt, "OUTVAL2");
            hcolOutVal3 = jdam.dam_getCol(dam_hstmt, "OUTVAL3");

            /* build output row */
            hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
            lVal = pProcDA.iTotalResultSets;
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hOutputRow, hcolOutVal1 , (int)lVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            lVal = pProcDA.iItems;
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hOutputRow, hcolOutVal2 , (int)lVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            lVal = pProcDA.iItems + pProcDA.iTotalResultSets;
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hOutputRow, hcolOutVal3 , (int)lVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* when processing is fully done, release from Proc Array */
            if(idx >= 0) {
               procDA[idx] = null;
               }
            return IP_SUCCESS;
            }
        }
    else {
        return IP_FAILURE;
        }
    }

/********************************************************************************************
    Method:          mem_procedure_proc_gtech()
*********************************************************************************************/
    public int        mem_procedure_proc_gtech(long dam_hstmt, int iType, xo_long piNumResRows)
    {
    MEM_PROC_DA     pProcDA;
    int             iRetCode;
    int             idx;


    if (iType == DAM_DSP_INIT) {
        long     pMemTree;

        pMemTree = jdam.dam_getMemTree(dam_hstmt);
        pProcDA = new MEM_PROC_DA();
        pProcDA.dam_hstmt = dam_hstmt;
        pProcDA.pMemTree = pMemTree;
        jdam.dam_describeProcedure(dam_hstmt, pProcDA.sQualifier, pProcDA.sOwner, pProcDA.sProcName, pProcDA.sUserData);

        /* get the statement handle */
        idx = getProcIndex();
        if(idx >= 0) {
            procDA[idx] = pProcDA;
            }

        jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the StmtDA index*/
        pProcDA.iCurResultSetNum = 0;

        /* get the input parameter info */
        {
        long            hrow, hRowElem;
        int             iParamNum;
        long            hcol;
        xo_int          piXOType, piColumnType;
        xo_int          iValueStatus;

        piXOType = new xo_int(0);
        piColumnType = new xo_int(0);
        iValueStatus = new xo_int(0);

        hrow = jdam.dam_getInputRow(dam_hstmt);

       /* Get and print the value of the input parameters */
        iParamNum=1;
        for (hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow); hRowElem != 0;
                hRowElem = jdam.dam_getNextValueSet(dam_hstmt)) {

            String  sVal;

            hcol = jdam.dam_getColToSet(hRowElem);
            jdam.dam_describeCol(hcol, null, null, piXOType, null);
            jdam.dam_describeColDetail(hcol, null, piColumnType, null);
            if (piColumnType.getVal() != SQL_PARAM_INPUT) continue;

            sVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
            System.out.println("[MEM IP] Param #" + iParamNum +  "=" + sVal);

            if (iParamNum == 1) pProcDA.iTotalResultSets = Integer.valueOf(sVal);
            if(pProcDA.iTotalResultSets > 3) pProcDA.iTotalResultSets = 3;
            iParamNum++;

            }
        }
        return IP_SUCCESS;
        }
    else if (iType == DAM_DSP_EXECUTE) {
        long        hColD;
        String      sColName;
        int         lVal;
        int         iColCount, iCurRes;
        long        hRow;

        idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
        pProcDA = procDA[idx];
        iCurRes = pProcDA.iCurResultSetNum + 1;

        /* build result set */
        if(pProcDA.iTotalResultSets > 0){
            hRow = jdam.dam_allocRow(dam_hstmt);

            for (iColCount = 0; iColCount <= pProcDA.iCurResultSetNum; iColCount++) {
                sColName = "Column " +  iColCount;
                hColD = jdam.dam_getCol(dam_hstmt, sColName);
                lVal = iCurRes*10 + iColCount+1;
                iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hRow, hColD , lVal, 0);
                }
            jdam.dam_addRowToTable(dam_hstmt, hRow);
            }

        /* check if we have processed all result sets */
        pProcDA.iCurResultSetNum++;
        if (pProcDA.iCurResultSetNum < pProcDA.iTotalResultSets) {
            return DAM_SUCCESS_WITH_MORE_RESULT_SETS;
            }
        else {
            long        hcolRetVal;
            long        hOutputRow;

            hcolRetVal = jdam.dam_getCol(dam_hstmt, "RETVAL");

            /* build output row */
            hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
            switch(pProcDA.iTotalResultSets)
            {

            case 0:
                lVal = -999;
                break;

            case 1:
                lVal = -100;
                break;
            case 2:
                lVal = -200;
                break;
            case 3:
                lVal = -300;
                break;
            default:
                lVal = -300;
                break;

            }
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hOutputRow, hcolRetVal , lVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* when processing is fully done, release from Proc Array */
            if(idx >= 0) {
               procDA[idx] = null;
               }
            return IP_SUCCESS;
            }
        }
    else {
        return IP_FAILURE;
        }

    }

/********************************************************************************************
    Method:          mem_procedure_proc_skip_resultcols()
*********************************************************************************************/
    public int        mem_procedure_proc_skip_resultcols(long dam_hstmt, int iType, xo_long piNumResRows)
    {
    MEM_PROC_DA     pProcDA;
    int             iRetCode;
    int             idx;


    if (iType == DAM_DSP_INIT) {
        long     pMemTree;

        pMemTree = jdam.dam_getMemTree(dam_hstmt);
        pProcDA = new MEM_PROC_DA();
        pProcDA.dam_hstmt = dam_hstmt;
        pProcDA.pMemTree = pMemTree;
        jdam.dam_describeProcedure(dam_hstmt, pProcDA.sQualifier, pProcDA.sOwner, pProcDA.sProcName, pProcDA.sUserData);

        /* get the statement handle */
        idx = getProcIndex();
        if(idx >= 0) {
            procDA[idx] = pProcDA;
            }

        jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the StmtDA index*/
        pProcDA.iCurResultSetNum = 0;
        pProcDA.iItems = 0;

	 /* get the fetch block size for cursor mode */
	{
	     xo_int  piValue;

            piValue = new xo_int();
	    /* get fetch block size */
	    iRetCode = jdam.dam_getInfo(0, pProcDA.dam_hstmt, DAM_INFO_FETCH_BLOCK_SIZE,
                                            null, piValue);
	    if (iRetCode != DAM_SUCCESS)
		pProcDA.iFetchSize = 2;
	    else
		pProcDA.iFetchSize = piValue.getVal();

	    jdam.trace(m_tmHandle, UL_TM_INFO,"mem_procedure_multiresult() Fetch size = "+ pProcDA.iFetchSize + "\n");
	}

        /* get the input parameter info */
        {
        long            hrow, hRowElem;
        int             iParamNum;
        long            hcol;
        xo_int          piXOType, piColumnType;
        xo_int          iValueStatus;

        piXOType = new xo_int(0);
        piColumnType = new xo_int(0);
        iValueStatus = new xo_int(0);

        hrow = jdam.dam_getInputRow(dam_hstmt);

       /* Get and print the value of the input parameters */
        iParamNum=1;
        for (hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow); hRowElem != 0;
                hRowElem = jdam.dam_getNextValueSet(dam_hstmt)) {

            String  sVal;

            hcol = jdam.dam_getColToSet(hRowElem);
            jdam.dam_describeCol(hcol, null, null, piXOType, null);
            jdam.dam_describeColDetail(hcol, null, piColumnType, null);
            if (piColumnType.getVal() != SQL_PARAM_INPUT) continue;

            sVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
            System.out.println("[MEM IP] Param #" + iParamNum +  "=" + sVal);

            if (iParamNum == 1) pProcDA.iTotalResultSets = Integer.valueOf(sVal);
            if (iParamNum == 2) pProcDA.iItems = Long.valueOf(sVal);
            iParamNum++;

            }
        }
        return IP_SUCCESS;
        }
    else if (iType == DAM_DSP_EXECUTE || iType == DAM_FETCH) {
        long        hColName, hColId, hColAge,hColDate,hColRem;
        String      sName, sRemarks;
        long        lVal;
        long        iRowCount;
        long        hRow;
        xo_tm dateVal = new xo_tm();;


        dateVal.setVal(xo_tm.DAY_OF_MONTH, 1);
        dateVal.setVal(xo_tm.MONTH, 0);
        dateVal.setVal(xo_tm.YEAR, 1990);

        idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
        pProcDA = procDA[idx];

        if (iType == DAM_DSP_EXECUTE)
            pProcDA.iCurItems = 0;
        hColName = jdam.dam_getCol(dam_hstmt, "NAME");
        hColId = jdam.dam_getCol(dam_hstmt, "ID");
        hColAge = jdam.dam_getCol(dam_hstmt, "AGE");
        hColDate = jdam.dam_getCol(dam_hstmt, "DATE");
        hColRem = jdam.dam_getCol(dam_hstmt, "REMARKS");

        /* build result set */
        iRowCount = 0;
        if(pProcDA.iCurResultSetNum == 0) {
        while (pProcDA.iCurItems < pProcDA.iItems) {
            hRow = jdam.dam_allocRow(dam_hstmt);
            lVal = pProcDA.iCurItems;

            sRemarks = "Good";
            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColRem, sRemarks, XO_NTS);
            jdam.dam_addTimeStampValToRow(dam_hstmt, hRow, hColDate, dateVal, 0);

            jdam.dam_addRowToTable(dam_hstmt, hRow);
            pProcDA.iCurItems++;
            iRowCount++;
            if (iRowCount >= pProcDA.iFetchSize) {
    		return DAM_SUCCESS_WITH_RESULT_PENDING;
    	    }
           }
        }
        else
        {
        while (iRowCount < pProcDA.iItems) {
            hRow = jdam.dam_allocRow(dam_hstmt);

            sName = "Name" + pProcDA.iCurResultSetNum;
            lVal = iRowCount;
                /* Add this field 3rd resultset onwards */
            if(pProcDA.iCurResultSetNum != 1)
                iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColName, sName, XO_NTS);
            iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColId , lVal, 0);

            lVal = iRowCount+15;
            iRetCode = jdam.dam_addBigIntValToRow(dam_hstmt, hRow, hColAge , lVal, 0);

            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColRem, "Good", XO_NTS);
            jdam.dam_addTimeStampValToRow(dam_hstmt, hRow, hColDate, dateVal, 0);

            jdam.dam_addRowToTable(dam_hstmt, hRow);
            iRowCount++;
            }
        }


        /* check if we have processed all result sets */
        pProcDA.iCurResultSetNum++;
        if (pProcDA.iCurResultSetNum < pProcDA.iTotalResultSets) {
            return DAM_SUCCESS_WITH_MORE_RESULT_SETS;
            }
        else {
            long        hcolRetVal;
            long        hOutputRow;

            hcolRetVal = jdam.dam_getCol(dam_hstmt, "RETVAL");

            /* build output row */
            hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
            lVal = pProcDA.iTotalResultSets;
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hOutputRow, hcolRetVal , (int)lVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* when processing is fully done, release from Proc Array */
            if(idx >= 0) {
               procDA[idx] = null;
               }
            return IP_SUCCESS;
            }
        }
    else {
        return IP_FAILURE;
        }
    }
/********************************************************************************************
    Method:          mem_procedure_proc_empty_resultsets()
*********************************************************************************************/
    public int        mem_procedure_proc_empty_resultsets(long dam_hstmt, int iType, xo_long piNumResRows)
    {
    MEM_PROC_DA     pProcDA;
    int             iRetCode;
    int             idx;


    if (iType == DAM_DSP_INIT) {
        long     pMemTree;

        pMemTree = jdam.dam_getMemTree(dam_hstmt);
        pProcDA = new MEM_PROC_DA();
        pProcDA.dam_hstmt = dam_hstmt;
        pProcDA.pMemTree = pMemTree;
        jdam.dam_describeProcedure(dam_hstmt, pProcDA.sQualifier, pProcDA.sOwner, pProcDA.sProcName, pProcDA.sUserData);

        /* get the statement handle */
        idx = getProcIndex();
        if(idx >= 0) {
            procDA[idx] = pProcDA;
            }

        jdam.dam_setIP_hstmt(dam_hstmt, idx); /* save the StmtDA index*/
        pProcDA.iCurResultSetNum = 0;

        /* get the input parameter info */
        {
        long            hrow, hRowElem;
        int             iParamNum;
        long            hcol;
        xo_int          piXOType, piColumnType;
        xo_int          iValueStatus;

        piXOType = new xo_int(0);
        piColumnType = new xo_int(0);
        iValueStatus = new xo_int(0);

        hrow = jdam.dam_getInputRow(dam_hstmt);

       /* Get and print the value of the input parameters */
        iParamNum=1;
        for (hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow); hRowElem != 0;
                hRowElem = jdam.dam_getNextValueSet(dam_hstmt)) {

            String  sVal;

            hcol = jdam.dam_getColToSet(hRowElem);
            jdam.dam_describeCol(hcol, null, null, piXOType, null);
            jdam.dam_describeColDetail(hcol, null, piColumnType, null);
            if (piColumnType.getVal() != SQL_PARAM_INPUT) continue;

            sVal = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
            System.out.println("[MEM IP] Param #" + iParamNum +  "=" + sVal);

            if (iParamNum == 1) pProcDA.iTotalResultSets = Integer.valueOf(sVal);
            if (iParamNum == 2) pProcDA.iItems = Integer.valueOf(sVal);
            iParamNum++;

            }
        }
        return IP_SUCCESS;
        }
    else if (iType == DAM_DSP_EXECUTE) {
        long        hColName;
        String      sName;
        int         lVal;
//        int         iRowCount, iColCount;
        long        hRow;

        idx = (int)jdam.dam_getIP_hstmt(dam_hstmt);
        pProcDA = procDA[idx];

        hColName = jdam.dam_getCol(dam_hstmt, "NAME");

        /* build result set . Add row only for first result set */

        if(pProcDA.iCurResultSetNum == 0) {
            hRow = jdam.dam_allocRow(dam_hstmt);

            sName = "Name" + pProcDA.iCurResultSetNum;
            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hRow, hColName, sName, XO_NTS);
            jdam.dam_addRowToTable(dam_hstmt, hRow);
            }


        /* check if we have processed all result sets */
        pProcDA.iCurResultSetNum++;
        if (pProcDA.iCurResultSetNum < pProcDA.iTotalResultSets) {
            return DAM_SUCCESS_WITH_MORE_RESULT_SETS;
            }
        else {
            long        hcolRetVal;
            long        hOutputRow;

            hcolRetVal = jdam.dam_getCol(dam_hstmt, "RETVAL");

            /* build output row */
            hOutputRow = jdam.dam_allocOutputRow(dam_hstmt);
            lVal = pProcDA.iTotalResultSets;
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hOutputRow, hcolRetVal , lVal, 0);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            iRetCode = jdam.dam_addOutputRow(dam_hstmt, hOutputRow);
            if (iRetCode != DAM_SUCCESS) return iRetCode;

            /* when processing is fully done, release from Proc Array */
            if(idx >= 0) {
               procDA[idx] = null;
               }
            return IP_SUCCESS;
            }
        }
    else {
        return IP_FAILURE;
        }
    }

/********************************************************************************************
    Method:          getStmtIndex()
    Description:     Return the index of the StmtDA to use

*********************************************************************************************/
    public int getStmtIndex()
    {
        for (int i=0; i < stmtDA.length; i++) {
            if(stmtDA[i] == null)
                return i;
        }
        return -1;
    }

/********************************************************************************************
    Method:          getProcIndex()
    Description:     Return the index of the ProcDA to use

*********************************************************************************************/
    public int getProcIndex()
    {
        for (int i=0; i < procDA.length; i++) {
            if(procDA[i] == null)
                return i;
        }
        return -1;
    }

/********************************************************************************************
    Method:          getNativeIndex()
    Description:     Return the index of the NativeDA to use

*********************************************************************************************/
    public int getNativeIndex()
    {
        for (int i=0; i < nativeDA.length; i++) {
            if(nativeDA[i] == null)
                return i;
        }
        return -1;
    }


/********************************************************************************************
    Method:       mem_is_matching_table()
    Description:    Return the requested schema objects
    Return:         true if search object will match the given search pattern
                    false otherwise
*********************************************************************************************/
    private boolean     mem_is_matching_table(schemaobj_table pSearchObj,
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

/********************************************************************************************
    Method:       mem_is_matching_column()
    Description:    Return the requested schema objects
    Return:         true if search object will match the given search pattern
                    false otherwise
*********************************************************************************************/

    private boolean     mem_is_matching_column(schemaobj_column pSearchObj,
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


/************************************************************************
Function:       mem_build_join_rowset()
Description:
Return:         DAM_SUCESS on success
                DAM_FAILURE on error
************************************************************************/
int      mem_build_join_rowset(MEM_STMT_DA pParentStmtDA, long hrow_parent)
{
    long            hstmt_next;
    MEM_STMT_DA     pStmtDA;
    StringBuffer    sTableName;   /* Name of the table being queried */
    long            pMemTree;
    int             iRetCode;
    long            hrow;
    long            hrowset;
    int             iProcessOrder;

    /* get the next join table */
    hstmt_next = jdam.dam_getNextJoinStmt(pParentStmtDA.dam_hstmt);
    if (hstmt_next==0) return DAM_SUCCESS;

    /* check the query process order and the size of the join */
    {
    xo_int  piValue;
    piValue = new xo_int();
    iRetCode = jdam.dam_getInfo(0, hstmt_next, DAM_INFO_QUERY_PROCESS_ORDER,
                                null, piValue);
    if (iRetCode != DAM_SUCCESS) return IP_FAILURE;
    iProcessOrder = piValue.getVal();;

    jdam.trace(m_tmHandle, UL_TM_INFO, "IP PushdownJoin information. ProcessOrder:" + iProcessOrder);
    }

    /* get the join condition */
    {
    xo_int     piColNum, piOpType, piOuterTableProcessOrder, piOuterTableColNum;

    piColNum = new xo_int();
    piOpType = new xo_int();
    piOuterTableProcessOrder = new xo_int();
    piOuterTableColNum = new xo_int();
    iRetCode = jdam.dam_describeJoinCond(hstmt_next, piColNum, piOpType, piOuterTableProcessOrder,
                        piOuterTableColNum);
    if (iRetCode == DAM_SUCCESS) {
        jdam.trace(m_tmHandle, UL_TM_INFO, "IP PushdownJoin condition. ColumnNum:" +  piColNum.getVal() + " OpType:" + piOpType.getVal() + " iTableProcessOrder:" + piOuterTableProcessOrder.getVal() + "iColNum:" + piOuterTableColNum);
        }

    }

    /* disable pushdown joins on tables with high process order */
    if (iProcessOrder >= 7) return DAM_SUCCESS;

    /* get the table name and check that the next table is not the same as the current
    table. This will allow us to do pushdown when the table sequence is DEPT, EMP
    or EMP, DEPT
    If the JOIN is on D1, E1, D2, E2 we will do complete pushdown.
    If the JOIN is on D1, E1, E2, D2 we do pushdown of E1 and D2 tables.
    */
    sTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    jdam.dam_describeTable(hstmt_next, null, null, sTableName, null, null);
    if (sTableName.toString().equalsIgnoreCase(pParentStmtDA.sTableName.toString())) return DAM_SUCCESS;

        /* get the memory tree to be used */
        pMemTree = jdam.dam_getMemTree(hstmt_next);

        /* allocate a new stmt */
        pStmtDA = new MEM_STMT_DA();

        /* initialize the StmtDA */
        pStmtDA.pMemTree = pMemTree;
        pStmtDA.dam_hstmt = hstmt_next;
        pStmtDA.iType = pParentStmtDA.iType;
        pStmtDA.iTable = EMPTY_TABLE;
        pStmtDA.hSearchCol = 0;
        pStmtDA.hSearchCondList = 0;
        pStmtDA.hSearchCond = 0;
        pStmtDA.sTableName = sTableName;

        iRetCode = java_mem_init_stmt(pStmtDA);

        if (iRetCode != DAM_SUCCESS) return iRetCode;

        /* check for any index conditions */
        {
        /* check if we can use a multi column index */
        iRetCode = jdam.dam_getOptimalIndexAndConditions(pStmtDA.dam_hstmt, pStmtDA.hindex, pStmtDA.hset_of_condlist);
        if (iRetCode != DAM_SUCCESS) return IP_FAILURE; /* return on error */
        if (pStmtDA.hindex.getVal() == 0) {
            jdam.trace(m_tmHandle, UL_TM_INFO, "No Index conditions for pushdown join");
            }
        }

        /* build the rows */
        hrowset = jdam.dam_allocRowSet(pStmtDA.dam_hstmt);
        hrow = java_mem_build_indexed_row(pStmtDA, pParentStmtDA.lDeptId, true);
        while (hrow != 0) {

            iRetCode = jdam.dam_addRowToRowSet(hrowset, hrow);
            if (iRetCode != DAM_SUCCESS) return iRetCode; /* error */

            /* go to next row */
            hrow = java_mem_build_indexed_row(pStmtDA, pParentStmtDA.lDeptId, false);
            } /* while */

        jdam.dam_addJoinRowSet(hrowset, hrow_parent);

        /* free the child StmtDA */
        /* free(pStmtDA); */

    return DAM_SUCCESS;

}

/******************************************************************************
 * Function:    mem_ip_load_balance_test
 * Description: Test load balancing. Report load balancing error if
				number of connections exceed 2.
 * Return:      DAM_SUCCESS on Success
                DAM_FAILURE   on Failure
 *****************************************************************************/

    private int		mem_ip_load_balance_test(long dam_hdbc)
    {
        String  sBuf = "";

	    if(giConnections >= 2)
	    {
		    sBuf = "No more connection please";
            jdam.dam_addError(dam_hdbc, 0, DAM_IP_ERROR, OA_IPCONNECT_REFUSE_CONNECTION, sBuf);
            jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);

		    return DAM_FAILURE;
	    }
	    else
		    giConnections++;

        return DAM_SUCCESS;
    }

/******************************************************************************
 * Function:    mem_ip_jvm_options_test
 * Description:
 * Return:      DAM_SUCCESS on Success
                DAM_FAILURE   on Failure
 *****************************************************************************/

    private int		mem_ip_jvm_options_test(long dam_hdbc)
    {
        String  sBuf = "";

        sBuf = System.getProperty("java.class.path");
        System.out.println("[MEM IP] java.class.path=" + sBuf);

        sBuf = System.getProperty("OA_DEF1");
        System.out.println("[MEM IP] OA_DEF1=" + sBuf);

        sBuf = System.getProperty("OA_DEF2");
        System.out.println("[MEM IP] OA_DEF2=" + sBuf);

        return DAM_SUCCESS;
    }

/******************************************************************************
 * Function:    mem_ip_connect_str_test
 * Description: Test limits of User connect string -
                Total length of connect string,
                Length of keywords UID, PWD, DATABASE.
 * Return:      DAM_SUCCESS on Success
                DAM_FAILURE   on Failure
 *****************************************************************************/

private int		mem_ip_connect_str_test(long dam_hdbc, String sUsrConnStr)
{
    int         iIndex, iIndexEnd;
    String      sTestType;
    int         iStrLength;
    String      sBuf = "";
    String      sValue;

        jdam.trace(m_tmHandle, UL_TM_F_TRACE,"mem_ip_connect_str_test called\n");

        iIndex = sUsrConnStr.indexOf(OA_TEST_ATTR);
        iIndex += OA_TEST_ATTR.length();
        iIndex += 1; /* Skip = */

        /* copy the Test Type */
        iIndexEnd = sUsrConnStr.indexOf(';', iIndex);
        if(iIndexEnd >= 0)
        {
            sTestType = sUsrConnStr.substring(iIndex, iIndexEnd);
        }
        else
            sTestType = sUsrConnStr.substring(iIndex);

        if (sTestType.equalsIgnoreCase("MAX_LENGTH"))
        {
            iStrLength = sUsrConnStr.length();
            sBuf = "Connection String Test:<" + sTestType + ">." + " Length of the Connection String is " +  iStrLength + ".";
        }
        else if (sTestType.equalsIgnoreCase("MAX_UID_LENGTH"))
        {
            iIndex = sUsrConnStr.indexOf("UID");
            iIndex += "UID".length();
            iIndex += 1; /* skip = */
            iIndexEnd = sUsrConnStr.indexOf(';', iIndex);

            sValue = sUsrConnStr.substring(iIndex, iIndexEnd);
            sBuf = "Connection String Test:<" + sTestType + ">." + " Length of the UID String is " +  sValue.length() + ".";
        }
        else if (sTestType.equalsIgnoreCase("MAX_PWD_LENGTH"))
        {
            iIndex = sUsrConnStr.indexOf("PWD");
            iIndex += "PWD".length();
            iIndex += 1; /* skip = */
            iIndexEnd = sUsrConnStr.indexOf(';', iIndex);

            sValue = sUsrConnStr.substring(iIndex, iIndexEnd);
            sBuf = "Connection String Test:<" + sTestType + ">." + " Length of the PWD String " +  sValue.length() + ".";
        }
        else if (sTestType.equalsIgnoreCase("MAX_DB_LENGTH"))
        {
            iIndex = sUsrConnStr.indexOf("Database");
            iIndex += "Database".length();
            iIndex += 1; /* skip = */
            iIndexEnd = sUsrConnStr.indexOf(';', iIndex);

            sValue = sUsrConnStr.substring(iIndex, iIndexEnd);
            sBuf = "Connection String Test:<" + sTestType + ">." + " Length of the Database String is " +  sValue.length() + ".";
        }
        else
        {
            sBuf = "Connection String Test:<" + sTestType + ">." + "Invalid Test Type.";
        }
        jdam.dam_addError(dam_hdbc, 0, DAM_IP_ERROR, 0, sBuf);
        jdam.trace(m_tmHandle, UL_TM_ERRORS, sBuf);

    return DAM_SUCCESS;
}


/******************************************************************************
 * Function:    mem_ip_view_info_test
 * Description: Get and print View information
 * Return:
 *****************************************************************************/

private void		mem_ip_view_info_test(long dam_hdbc)
{
    /* read view info */
    StringBuffer szTableQualifier;  /* Catalog */
    StringBuffer szTableOwner;      /* Owner */
    StringBuffer szTableName;       /* View name */
    int  iRetCode;

    szTableQualifier = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    szTableOwner = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    szTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

    iRetCode = jdam.dam_getFirstView(dam_hdbc, szTableQualifier, szTableOwner, szTableName);
    while (iRetCode != DAM_NO_DATA_FOUND) {
        System.out.println("[MEM IP] View:<" + szTableQualifier.toString() + "." + szTableOwner.toString() + "." +  szTableName.toString() + ">");

        szTableQualifier.delete(0, szTableQualifier.length());
        szTableOwner.delete(0, szTableOwner.length());
        szTableName.delete(0, szTableName.length());
        iRetCode = jdam.dam_getNextView(dam_hdbc, szTableQualifier, szTableOwner, szTableName);
        }
}

/*****************************************************************
Function:       mem_get_printval
Description:
Output:         None
*****************************************************************/
String          mem_get_printval(String str)
{
    return (str != null) ? str : "";
}

/*****************************************************************
Function:       mem_get_printval
Description:
Output:         None
*****************************************************************/
short          mem_get_printval(short val)
{
    return (val != ip.DAMOBJ_NOTSET) ? val : 0;
}

/*****************************************************************
Function:       mem_get_printval
Description:
Output:         None
*****************************************************************/
int           mem_get_printval(int val)
{
    return (val != ip.DAMOBJ_NOTSET) ? val : 0;
}

/*****************************************************************
Function:       mem_print_user
Description:    print info of a user
Output:         None
*****************************************************************/
void     mem_print_user(StringBuffer Catalog,StringBuffer UserName,StringBuffer Password,StringBuffer UserData)
{
	 if(!(Catalog.length()==0))
		System.out.println(Catalog);

		System.out.println(UserName);

	if(!(Password.length()==0))
		System.out.println(" IDENTIFIED BY " + Password);

	if(!(UserData.length()==0))
		System.out.println(" USERDATA " + UserData);
}

/*****************************************************************
Function:       mem_print_role
Description:    print info of a role
Output:         None
*****************************************************************/
void     mem_print_role(StringBuffer Catalog, StringBuffer RoleName)
{
	if(!(Catalog.length()==0))
		System.out.println(Catalog);

	System.out.println(RoleName);
}
/*****************************************************************
Function:       dam_print_schemaobj_table
Description:    print info of table in the schema
Output:         None
*****************************************************************/
void     dam_print_schemaobj_table(schemaobj_table TableObj)
{
    System.out.println("========= Table ===========\n");
    System.out.println("Table qualifier: <" + mem_get_printval(TableObj.getTableQualifier()) + ">");
    System.out.println("Table owner:     <" + mem_get_printval(TableObj.getTableOwner()) + ">");
    System.out.println("Table name:      <" + mem_get_printval(TableObj.getTableName()) + ">");
    System.out.println("Table type:      <" + mem_get_printval(TableObj.getTableType()) + ">");
}

/*****************************************************************
Function:       dam_print_schemaobj_column
Description:    print info of column in the schema
Output:         None
*****************************************************************/
void     dam_print_schemaobj_column(schemaobj_column ColumnObj)
{
	xo_short	  uDataType;
	StringBuffer	sTypeName;
	xo_int			lCharMaxLength;
	xo_int			lNumericPrecision;
	xo_short	    uNumericPrecisionRadix;
	xo_short		uNumericScale;
	xo_short		uNullable;
	xo_short		uScope;
	StringBuffer	sUserData;
	StringBuffer	sOperatorSupport;
	xo_short		uPsuedoColumn;
	xo_short		uColumnType;
	StringBuffer	sRemarks;

    uDataType = new xo_short((short)0);
    sTypeName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    lCharMaxLength = new xo_int(0);
    lNumericPrecision = new xo_int(0);
    uNumericPrecisionRadix = new xo_short((short)0);
    uNumericScale = new xo_short((short)0);
    uNullable = new xo_short((short)0);
    uScope = new xo_short((short)0);
    sUserData = new StringBuffer(ip.DAMOBJ_MAX_EXT_LEN+1);
    sOperatorSupport = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    uPsuedoColumn = new xo_short((short)0);
    uColumnType = new xo_short((short)0);
    sRemarks = new StringBuffer(ip.DAMOBJ_MAX_EXT_LEN+1);

    ColumnObj.GetObjInfo(null,
			null,
			null,
			null,
			uDataType,
			sTypeName,
			lCharMaxLength,
			lNumericPrecision,
			uNumericPrecisionRadix,
			uNumericScale,
			uNullable,
			uScope,
			sUserData,
			sOperatorSupport,
			uPsuedoColumn,
			uColumnType,
			sRemarks
		);
    System.out.println("========= Column ===========\n");
    System.out.println("Table qualifier: <" + mem_get_printval(ColumnObj.getTableQualifier()) + ">");
    System.out.println("Table owner:     <" + mem_get_printval(ColumnObj.getTableOwner()) + ">");
    System.out.println("Table name:      <" + mem_get_printval(ColumnObj.getTableName()) + ">");
    System.out.println("Column name:     <" + mem_get_printval(ColumnObj.getColumnName()) + ">");
    /* we will print directly the data type as the value can be -1 (XO_TYPE_LONGVARCHAR) */
    System.out.println("Data type:       <" + uDataType.getVal() + ">");
    System.out.println("Type name:       <" + sTypeName + ">");
    System.out.println("Max length:      <" + mem_get_printval(lCharMaxLength.getVal()) + ">");
    System.out.println("precision:       <" + mem_get_printval(lNumericPrecision.getVal()) + ">");
    System.out.println("scale:           <" + mem_get_printval(uNumericScale.getVal()) + ">");
    System.out.println("nullable:        <" + mem_get_printval(uNullable.getVal()) + ">");
    System.out.println("scope:           <" + mem_get_printval(uScope.getVal()) + ">");
    System.out.println("user data:       <" + sUserData + ">");
    System.out.println("support:         <" + sOperatorSupport + ">");
    System.out.println("psuedo column:   <" + mem_get_printval(uPsuedoColumn.getVal()) + ">");
    System.out.println("column type:     <" + mem_get_printval(uColumnType.getVal()) + ">");
}

/*****************************************************************
Function:       dam_print_schemaobj_stat
Description:    print info of stat in the schema
Output:         None
*****************************************************************/
void     dam_print_schemaobj_stat(schemaobj_stat StatObj)
{
	xo_short		uNonUnique;
	StringBuffer	sIndexQualifier;
	StringBuffer	sIndexName;
	xo_short		uType;
	xo_short		uSeqInIndex;
	StringBuffer	sColumnName;
	StringBuffer	sCollation;

    uNonUnique = new xo_short((short)0);
    sIndexQualifier = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    sIndexName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    uType = new xo_short((short)0);
    uSeqInIndex = new xo_short((short)0);
    sColumnName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    sCollation = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

	StatObj.GetObjInfo(null,
                    null,
                    null,
					uNonUnique,
					sIndexQualifier,
					sIndexName,
					uType,
					uSeqInIndex,
					sColumnName,
					sCollation,
					null,
					null,
					null);

    System.out.println("========= Stat ============\n");
    System.out.println("Table qualifier: <" + mem_get_printval(StatObj.getTableQualifier()) + ">");
    System.out.println("Table owner:     <" + mem_get_printval(StatObj.getTableOwner()) + ">");
    System.out.println("Table name:      <" + mem_get_printval(StatObj.getTableName()) + ">");
    System.out.println("Column name:     <" + sColumnName + ">");
    System.out.println("Non-unique:      <" + mem_get_printval(uNonUnique.getVal()) + ">");
    System.out.println("Index qualifier: <" + sIndexQualifier + ">");
    System.out.println("Index name:      <" + sIndexName + ">");
    System.out.println("Type:            <" + mem_get_printval(uType.getVal()) + ">");
    System.out.println("Seq in index:    <" + mem_get_printval(uSeqInIndex.getVal()) + ">");
    if (sCollation.length() > 0)
    System.out.println("collation:     <" + sCollation + ">");
    else
    System.out.println("collation:     < >");
}

/*****************************************************************
Function:       dam_print_fkey
Description:    print info of fkey in the schema
Output:         None
*****************************************************************/
void     dam_print_schemaobj_fkey(schemaobj_fkey FkeyObj)
{
    xo_short        uKeySeq;
    xo_short        uUpdateRule;
    xo_short        uDeleteRule;
    StringBuffer    sFKName;
    StringBuffer    sPKName;

    uKeySeq = new xo_short((short)0);
    uUpdateRule = new xo_short((short)0);
    uDeleteRule = new xo_short((short)0);
    sFKName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    sPKName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

    FkeyObj.GetObjInfo(null, null, null, null,
                    null, null, null, null,
                    uKeySeq,uUpdateRule,uDeleteRule,
                    sFKName,sPKName);

    System.out.println("========= Fkey ===========\n");
    System.out.println("PK Table qualifier: <" + mem_get_printval(FkeyObj.getPKTableQualifier()) + ">");
    System.out.println("PK Table owner:     <" + mem_get_printval(FkeyObj.getPKTableOwner()) + ">");
    System.out.println("PK Table name:      <" + mem_get_printval(FkeyObj.getPKTableName()) + ">");
    System.out.println("PK Column name:     <" + mem_get_printval(FkeyObj.getPKColumnName()) + ">");
    System.out.println("FK Table qualifier: <" + mem_get_printval(FkeyObj.getFKTableQualifier()) + ">");
    System.out.println("FK Table owner:     <" + mem_get_printval(FkeyObj.getFKTableOwner()) + ">");
    System.out.println("FK Table name:      <" + mem_get_printval(FkeyObj.getFKTableName()) + ">");
    System.out.println("FK Column name:     <" + mem_get_printval(FkeyObj.getFKColumnName()) + ">");
    System.out.println("Key seq:            <" + mem_get_printval(uKeySeq.getVal()) + ">");
    System.out.println("Update rule:        <" + mem_get_printval(uUpdateRule.getVal()) + ">");
    System.out.println("Delete rule:        <" + mem_get_printval(uDeleteRule.getVal()) + ">");
    System.out.println("FK Name:            <" + sFKName + ">");
    System.out.println("PK Name:            <" + sPKName + ">");
}
/************************************************************************
Function:       java_mem_add_short_binary_data()
Description:    Add the picture data to the result row
Return:         DAM_SUCESS on success
                DAM_FAILURE on error
************************************************************************/
int             java_mem_add_short_binary_data(MEM_STMT_DA pStmtDA,
        long    hrow,
        long    hcol,
        String sFileName)
{
	DataInputStream fpSource = null;
    byte[] pBuffer;
    long nAmtRead = 0;
    int nBufferSize = 0;
   try
    {
	    /* get file length */
	   {
		   File file = new File(sFileName);
		   nBufferSize = (int)file.length();
	   }
        /* Open the source file */
    	fpSource = new DataInputStream(new FileInputStream(sFileName));

         /* allocate a buffer */
        pBuffer = new byte[nBufferSize];
        nAmtRead = fpSource.read(pBuffer, 0, fpSource.available());
        if (nAmtRead > 0)
        {
            jdam.dam_addBinaryValToRow(pStmtDA.dam_hstmt, hrow, hcol, pBuffer, (int)nAmtRead);
        }
        else
        {
            jdam.dam_addBinaryValToRow(pStmtDA.dam_hstmt, hrow, hcol, pBuffer, 0);
        }

        /* Close the files */
        fpSource.close();
    }
    catch (Exception e)
    {   /*
System.Console.Writeln("Exception from mem_add_long_binary_data");
System.Console.Writeln(e.Message);
return ipc.DAM_FAILURE;
*/
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Exception from mem_add_short_binary_data. Message=" + e.getMessage());
        jdam.dam_addBinaryValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);
        return DAM_SUCCESS;

    }

    return DAM_SUCCESS;
}
/************************************************************************
Function:       mem_add_long_binary_data()
Description:    Add the picture data to the result row
Return:         DAM_SUCESS on success
                DAM_FAILURE on error
************************************************************************/
int             java_mem_add_long_binary_data(MEM_STMT_DA pStmtDA,
                                       long    hrow,
                                       long    hcol,
                                       String sFileName)
{
    DataInputStream fpSource = null;
    byte[]  pBuffer;
    long    nBufferSize;
    long    nAmtRead;
    boolean bEmptyFile = true;

    try
    {
    /* Open the source file */
    fpSource = new DataInputStream(new FileInputStream(sFileName));

    if(giTestMode ==  MEM_TEST_LOB_LOCATOR)
    {
    	jdam.dam_addLOBLocatorValToRow(pStmtDA.dam_hstmt, hrow, hcol, XO_TYPE_BINARY, fpSource, 0);
        return DAM_SUCCESS;
    }
    /* allocate a buffer */
	if(giTestMode == MEM_TEST_DISTINCT_LONGDATA) { 
    byte[] append = Long.toString(pStmtDA.lCurItem%5).getBytes();
	pBuffer = new byte[giLongDataBufferSize + append.length];
	nBufferSize = giLongDataBufferSize;

	nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);
   while (nAmtRead > 0) {
      bEmptyFile = false;
      System.arraycopy(append, 0, pBuffer, (int)nAmtRead, append.length);
      jdam.dam_addBinaryValToRow(pStmtDA.dam_hstmt, hrow, hcol, pBuffer, (int) nAmtRead+append.length);
      nAmtRead = fpSource.read(pBuffer, 0,(int) nBufferSize);
      }
	}
	else {
		pBuffer = new byte[giLongDataBufferSize];
		nBufferSize = giLongDataBufferSize;
		nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);
		while (nAmtRead > 0) {
			bEmptyFile = false;
			jdam.dam_addBinaryValToRow(pStmtDA.dam_hstmt, hrow, hcol, pBuffer, (int) nAmtRead);
			nAmtRead = fpSource.read(pBuffer, 0,(int) nBufferSize);
      }
      }
    if(bEmptyFile == true)
    {
       jdam.dam_addBinaryValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, 0);
    }
    /* Close the files */
    fpSource.close();
    }
    catch(Exception e)
    {   /*
        System.out.println("Exception from mem_add_long_binary_data");
        System.out.println(e.getMessage());
        return DAM_FAILURE;
        */
    	if(giTestMode ==  MEM_TEST_LOB_LOCATOR)
        	jdam.dam_addLOBLocatorValToRow(pStmtDA.dam_hstmt, hrow, hcol, XO_TYPE_BINARY, null, XO_NULL_DATA);
        else
        jdam.dam_addBinaryValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);
        return DAM_SUCCESS;
    }

    return DAM_SUCCESS;
}

    /************************************************************************
    Function:       java_mem_add_shortchar_data()
    Description:    Add the comments data to the result row
    Return:         DAM_SUCESS on success
		    DAM_FAILURE on error
    ************************************************************************/
    int             java_mem_add_shortchar_data(MEM_STMT_DA pStmtDA,
        long	  hrow,
        long   hcol,
        String sFileName)
    {
        BufferedReader fpSource = null;
        char[]  pBuffer;
        long    nBufferSize;
        long    nAmtRead;
        String  pData;

        try
        {
            /* Open the source file */
            fpSource = new BufferedReader(new FileReader(sFileName));

            /* allocate a buffer */
			pBuffer = new char[giLongDataBufferSize];
			nBufferSize = giLongDataBufferSize;

			nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);
            if(nAmtRead <= 0)
            {
                if(giTestMode ==  MEM_TEST_ADD_CHAR_AS_WCHAR)
                    jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, 0);
                else
                    jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, 0);
            }
            else
            {
                pData = new String(pBuffer,0,(int)nAmtRead);
                while (nAmtRead > 0)
                {
                    nAmtRead = fpSource.read(pBuffer, 0,(int) nBufferSize);
                    if(nAmtRead > 0)
                       pData = pData.concat(String.copyValueOf(pBuffer,0,(int)nAmtRead));
                }
                if(giTestMode ==  MEM_TEST_ADD_CHAR_AS_WCHAR)
                     jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, pData, pData.length());
                else
                     jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, pData, pData.length());
            }
            /* Close the files */
            fpSource.close();
        }
        catch(Exception e)
        {
            /*
             System.out.println("Exception from mem_add_longwchar_data");
             System.out.println(e.getMessage());
             return DAM_FAILURE;
             */
	    if(giTestMode ==  MEM_TEST_ADD_CHAR_AS_WCHAR)
		jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);
	    else
		jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);

            return DAM_SUCCESS;

        }
        return DAM_SUCCESS;
    }

    /************************************************************************
    Function:       mem_add_shortwchar_data()
    Description:    Add the comments data to the result row
    Return:         DAM_SUCESS on success
		    DAM_FAILURE on error
    ************************************************************************/
	int             java_mem_add_shortwchar_data(MEM_STMT_DA pStmtDA,
						                            long	  hrow,
						                            long   hcol,
						                            String sFileName)
	{
		BufferedReader fpSource = null;
		FileInputStream fis;
		char[]  pBuffer;
		long    nBufferSize;
		long    nAmtRead;
		String  pData;

		try
		{
		/* Open the source file */
	         fis = new FileInputStream(new File(sFileName));
	         fpSource = new BufferedReader(new InputStreamReader(fis, "UTF-16LE"));

		/* allocate a buffer */
		pBuffer = new char[giLongDataBufferSize];
		nBufferSize = giLongDataBufferSize;

		nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);
        if(nAmtRead <= 0)
        {
            if(giTestMode ==  MEM_TEST_ADD_WCHAR_AS_CHAR)
                jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, 0);
            else
                jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, 0);
        }
        else
        {
           pData = new String(pBuffer,0,(int)nAmtRead);

	       while (nAmtRead > 0)
	       {
                nAmtRead = fpSource.read(pBuffer, 0,(int) nBufferSize);
                if(nAmtRead > 0)
                    pData = pData.concat(String.copyValueOf(pBuffer,0,(int)nAmtRead));
           }
            if(giTestMode ==  MEM_TEST_ADD_WCHAR_AS_CHAR)
                jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, pData, pData.length());
            else
                jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, pData, pData.length());
        }
		/* Close the files */
		fpSource.close();
		}
		catch(Exception e)
		{
		    /*
		    System.out.println("Exception from mem_add_longwchar_data");
		    System.out.println(e.getMessage());
		    return DAM_FAILURE;
		    */
		    if(giTestMode ==  MEM_TEST_ADD_WCHAR_AS_CHAR)
			jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);
		    else
			jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);

		    return DAM_SUCCESS;

		}

	    return DAM_SUCCESS;
	    }
/************************************************************************
Function:       java_mem_add_longchar_data()
Description:    Add the comments data to the result row
Return:         DAM_SUCESS on success
                DAM_FAILURE on error
************************************************************************/
int             java_mem_add_longchar_data(MEM_STMT_DA pStmtDA,
					       long	  hrow,
					       long   hcol,
					       String sFileName)
{
	BufferedReader fpSource = null;
	char[]  pBuffer;
	long    nBufferSize;
	long    nAmtRead;
	String  pData;
    boolean bEmptyFile = true;

	try
	{
	/* Open the source file */
	fpSource = new BufferedReader(new FileReader(sFileName));

	if( MEM_TEST_LOB_LOCATOR == giTestMode ) {
        jdam.dam_addLOBLocatorValToRow(pStmtDA.dam_hstmt, hrow, hcol, XO_TYPE_CHAR, fpSource, 0);
        return DAM_SUCCESS;
    }
	/* allocate a buffer */
	pBuffer = new char[giLongDataBufferSize];
	nBufferSize = giLongDataBufferSize;

    nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);
    while (nAmtRead > 0)
    {
        bEmptyFile = false;
        pData = new String(pBuffer,0,(int)nAmtRead);
		if(giTestMode == MEM_TEST_DISTINCT_LONGDATA) {
	        pData = pData + Long.toString(pStmtDA.lCurItem%5);
		}
        if(giTestMode ==  MEM_TEST_ADD_CHAR_AS_WCHAR)
            jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, pData, pData.length());
        else
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, pData, pData.length());
        nAmtRead = fpSource.read(pBuffer, 0,(int) nBufferSize);
    }
    if(bEmptyFile == true)
    {
        if(giTestMode ==  MEM_TEST_ADD_CHAR_AS_WCHAR)
            jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, 0);
        else
            jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, 0);
    }
	/* Close the files */
	fpSource.close();
	}
	catch(Exception e)
	{
	    /*
	    System.out.println("Exception from mem_add_longwchar_data");
	    System.out.println(e.getMessage());
	    return DAM_FAILURE;
	    */
	    if(giTestMode ==  MEM_TEST_ADD_CHAR_AS_WCHAR)
		jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);
	    else
		jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);

	    return DAM_SUCCESS;

	}
	return DAM_SUCCESS;
    }

    /************************************************************************
     Function:       java_mem_add_longwchar_data()
     Description:    Add the comments data to the result row
     Return:         DAM_SUCESS on success
     DAM_FAILURE on error
     ************************************************************************/
    int             java_mem_add_longwchar_data(MEM_STMT_DA pStmtDA,
                                                    long	  hrow,
                                                    long   hcol,
                                                    String sFileName)
    {
        BufferedReader fpSource = null;
        char[]  pBuffer;
        long    nBufferSize;
        long    nAmtRead;
        String  pData;
        FileInputStream fis;
        boolean bEmptyFile=true;

        try
        {
            fis = new FileInputStream(new File(sFileName));
            fpSource = new BufferedReader(new InputStreamReader(fis, "UTF-16LE"));

            if( MEM_TEST_LOB_LOCATOR == giTestMode ) {
        		jdam.dam_addLOBLocatorValToRow(pStmtDA.dam_hstmt, hrow, hcol, XO_TYPE_WCHAR, fpSource, 0);
                return DAM_SUCCESS;
            }
            /* allocate a buffer */
           	pBuffer = new char[giLongDataBufferSize];
			nBufferSize = giLongDataBufferSize;

			nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);
            while (nAmtRead > 0)
            {
                bEmptyFile = false;
                pData = new String(pBuffer,0,(int)nAmtRead);
				if(giTestMode == MEM_TEST_DISTINCT_LONGDATA) {
                	pData = pData + Long.toString(pStmtDA.lCurItem%5);
				}
                if(giTestMode ==  MEM_TEST_ADD_WCHAR_AS_CHAR)
                    jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, pData, pData.length());
                else
                    jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, pData, pData.length());
                nAmtRead = fpSource.read(pBuffer, 0,(int) nBufferSize);
            }
            if(bEmptyFile == true)
            {
                if(giTestMode ==  MEM_TEST_ADD_WCHAR_AS_CHAR)
                    jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, 0);
                else
                    jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, 0);
            }

            /* Close the files */
            fpSource.close();
        }
        catch(Exception e)
        {
            /*
             System.out.println("Exception from mem_add_longwchar_data");
             System.out.println(e.getMessage());
             return DAM_FAILURE;
             */
	    if(giTestMode ==  MEM_TEST_ADD_WCHAR_AS_CHAR)
		jdam.dam_addCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);
	    else
		jdam.dam_addWCharValToRow(pStmtDA.dam_hstmt, hrow, hcol, null, XO_NULL_DATA);

            return DAM_SUCCESS;

        }
        return DAM_SUCCESS;
    }
/************************************************************************
Function:       mem_get_binary_data()
Description:    Get the picture data from input row
Return:         DAM_SUCESS on success
                DAM_FAILURE on error
************************************************************************/
int             mem_get_binary_data(MEM_STMT_DA pStmtDA,
                                       long hRowElem,
                                       String sFileName,
                                       int      iXoType)
{
    DataOutputStream    fpDest;
    byte[]  pData;
    int     lDataLen;
    int     iRetCode;
    xo_int  piStatus = new xo_int(0);

    try
    {
    /* open the file for writing */
    fpDest = new DataOutputStream(new FileOutputStream(sFileName));

    /* write  data to file */
    while (true) {
        pData = (byte[])jdam.dam_getValueToSet(hRowElem, iXoType,piStatus);
        iRetCode = piStatus.getVal();

        if (iRetCode != DAM_SUCCESS && iRetCode != DAM_SUCCESS_WITH_RESULT_PENDING) {
            return iRetCode;
            }
        lDataLen = pData.length;
        fpDest.write(pData, 0,lDataLen );

        if (iRetCode == DAM_SUCCESS)
            break;
        else if (iRetCode != DAM_SUCCESS_WITH_RESULT_PENDING)
            return iRetCode;
        }

    fpDest.close();
    }
    catch(Exception e)
    {
        System.out.println("Exception from mem_get_binary_data");
        System.out.println(e.getMessage());
        return DAM_FAILURE;
    }

    return iRetCode;

}

/************************************************************************
Function:       mem_get_longchar_data()
Description:    Get the comments data from input row
Return:         DAM_SUCESS on success
                DAM_FAILURE on error
************************************************************************/
int             mem_get_longchar_data(MEM_STMT_DA pStmtDA,
                                       long hRowElem,
                                       String sFileName)
{
    int     iValType;
    BufferedWriter    fpDest;
    String  pData;
    int     iRetCode;
    xo_int  piStatus = new xo_int(0);


    try
    {
    /* open the file for writing */
    fpDest = new BufferedWriter(new FileWriter(sFileName));

    iValType = XO_TYPE_LONGVARCHAR;

    /* write  data to file */
    while (true) {
        pData = (String)jdam.dam_getValueToSet(hRowElem, iValType,piStatus);
        iRetCode = piStatus.getVal();

        if (iRetCode != DAM_SUCCESS && iRetCode != DAM_SUCCESS_WITH_RESULT_PENDING) {
            return iRetCode;
            }
        fpDest.write(pData);

        if (iRetCode == DAM_SUCCESS)
            break;
        else if (iRetCode != DAM_SUCCESS_WITH_RESULT_PENDING)
            return iRetCode;
        }

    fpDest.close();
    }
    catch(Exception e)
    {
        System.out.println("Exception from mem_get_longchar_data");
        System.out.println(e.getMessage());
        return DAM_FAILURE;
    }

    return iRetCode;

}

/************************************************************************
Function:       mem_get_longwchar_data()
Description:    Get the comments data from input row
Return:         DAM_SUCESS on success
                DAM_FAILURE on error
************************************************************************/
int             mem_get_longwchar_data(MEM_STMT_DA pStmtDA,
                                       long hRowElem,
                                       String sFileName)
{
    int     iValType;
    BufferedWriter    fpDest;
    String  pData;
    int     iRetCode;
    xo_int  piStatus = new xo_int(0);
    FileOutputStream fos;


    try
    {
    /* open the file for writing */
    fos = new FileOutputStream(new File(sFileName));
    fpDest = new BufferedWriter(new OutputStreamWriter(fos, "UTF-16LE"));
    iValType = XO_TYPE_WLONGVARCHAR;

    /* write  data to file */
    while (true) {
        pData = (String)jdam.dam_getValueToSet(hRowElem, iValType,piStatus);
        iRetCode = piStatus.getVal();

        if (iRetCode != DAM_SUCCESS && iRetCode != DAM_SUCCESS_WITH_RESULT_PENDING) {
            return iRetCode;
            }
        fpDest.write(pData);

        if (iRetCode == DAM_SUCCESS)
            break;
        else if (iRetCode != DAM_SUCCESS_WITH_RESULT_PENDING)
            return iRetCode;
        }

    fpDest.close();
    }
    catch(Exception e)
    {
        System.out.println("Exception from mem_get_longwchar_data");
        System.out.println(e.getMessage());
        return DAM_FAILURE;
    }

    return iRetCode;

}

/************************************************************************
Function:       mem_update_picture_table()
Description:    Update the picture data of the target row
Return:         DAM_SUCESS on success
                DAM_FAILURE on error
************************************************************************/
int             mem_update_picture_table(MEM_STMT_DA pStmtDA, xo_long piRowCount, long hTargetRow, String pName)
{
        long      hrow;
        long      hRowElem, hRowElemName, hRowElemPicture, hRowElemComments;
        xo_int    iColNum, iColNumName, iColNumPicture, iColNumComments;
        long      hcol, hcolName, hcolPicture, hcolComments;
        int       iRetCode;
        iColNum            = new xo_int(0);
         iColNumName        = new xo_int(0);
         iColNumPicture     = new xo_int(0);
         iColNumComments    = new xo_int(0);

        /* get the column handles */
        hcolName = jdam.dam_getCol(pStmtDA.dam_hstmt, "NAME");
        hcolPicture = jdam.dam_getCol(pStmtDA.dam_hstmt, "PICTURE");
        hcolComments = jdam.dam_getCol(pStmtDA.dam_hstmt, "COMMENTS");

        /* process the update
          for each row to be updated, get the column values and insert
        */
        jdam.dam_describeCol(hcolName, iColNumName, null, null, null);
        jdam.dam_describeCol(hcolPicture, iColNumPicture, null, null, null);
        jdam.dam_describeCol(hcolComments, iColNumComments, null, null, null);
        hrow = jdam.dam_getUpdateRow(pStmtDA.dam_hstmt, hTargetRow);

            /* update the row */

            hRowElemName = hRowElemPicture = hRowElemComments = 0;
            hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
            while (hRowElem != 0) {
                hcol = jdam.dam_getColToSet(hRowElem);
                jdam.dam_describeCol(hcol, iColNum, null, null, null);
                if (iColNum.getVal() == iColNumName.getVal())
                    hRowElemName = hRowElem;
                if (iColNum.getVal() == iColNumPicture.getVal())
                    hRowElemPicture = hRowElem;
                else
                    hRowElemComments = hRowElem;

                hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
                }

            if (hRowElemName != 0)
                return DAM_FAILURE;

            if (hRowElemPicture != 0) {
                String    sFileName;

                /* get the file name */
                sFileName = sMemoryWorkingDir;
                sFileName = sFileName.concat(pName);
                sFileName = sFileName.concat(".bmp");
                /* save the picture */
                iRetCode = mem_get_binary_data(pStmtDA, hRowElemPicture, sFileName, XO_TYPE_LONGVARBINARY);
                }

            if (hRowElemComments != 0) {
            	/* save the comments */
                iRetCode = mem_get_longchar_data(pStmtDA, hRowElemComments,pName);
                }

            piRowCount.setVal(piRowCount.getVal() + 1);

    return DAM_SUCCESS;
}

    public int        ip_ddl_create_view(long dam_hstmt)
    {
    StringBuffer    sQualifier = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    StringBuffer    sOwner = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    StringBuffer    sName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
    StringBuffer    sQueryStr;
    int             iRetCode;
    long            hquery;

    jdam.dam_describeView(dam_hstmt, sQualifier, sOwner, sName);
    System.out.println("View:" + sQualifier + "." + sOwner + "." + sName );
    sQueryStr = new StringBuffer(4096 + 1);

    /* Test custom connect properties */
    iRetCode = jdam.dam_getInfo(0, dam_hstmt, DAM_INFO_ORIGINAL_QUERY_STRINGW,
                                sQueryStr, null);
    System.out.println("Original Query:" + sQueryStr);

    /* get View query information using PassThrough API */
    hquery = jdam.dam_getViewQuery(dam_hstmt);

    /*
    sSqlString = new StringBuffer(MAX_QUERY_LEN);
    ip_format_query(hquery, sSqlString);
    String str = "Query:< " + sSqlString + ">\n";
    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, str);
    System.out.println(str);
    */

    return DAM_SUCCESS;

    }

    public scalar_function[] ipGetScalarFunctions()
    {
        scalar_function[] MyFuncs = null;
		if (giTestMode == MEM_TEST_QUALIFIER_FUNCTION_SUPPORT)
			MyFuncs = new scalar_function[16];
		else
			MyFuncs = new scalar_function[9];
		
        int  iClientInfoLength = 5 * (DAM_MAX_ID_LEN + 1) + DAM_MAX_ID_LEN;

        MyFuncs[0] = new scalar_function("INTVAL",1,"ip_func_intval",XO_TYPE_INTEGER,1);
        MyFuncs[1] = new scalar_function("DOUBLEVAL",1,"ip_func_doubleval",XO_TYPE_DOUBLE,1);
        MyFuncs[2] = new scalar_function("CHARVAL",1,"ip_func_charval",XO_TYPE_CHAR,1);
        MyFuncs[3] = new scalar_function("TRANSLATE",1,1,"ip_func_translate",XO_TYPE_VARCHAR,32,32,0,1);
        MyFuncs[4] = new scalar_function("BIGINTVAL",1,"ip_func_bigintval",XO_TYPE_BIGINT,1);
        MyFuncs[5] = new scalar_function("MAXROWS",1,"ip_func_maxrows",XO_TYPE_BIGINT,0);
        MyFuncs[6] = new scalar_function("PUBLIC_IP",1,"ip_func_public_ip",XO_TYPE_VARCHAR,(IP_ADDR_LEN + 1),(IP_ADDR_LEN + 1),0,0);
        MyFuncs[7] = new scalar_function("PRIVATE_IP",1,"ip_func_private_ip",XO_TYPE_VARCHAR,(IP_ADDR_LEN + 1),(IP_ADDR_LEN + 1),0,0);
        MyFuncs[8] = new scalar_function("CLIENTINFO", 1, "ip_func_clientinfo", XO_TYPE_WVARCHAR,iClientInfoLength*2,iClientInfoLength,0,0);
		
		if (giTestMode == MEM_TEST_QUALIFIER_FUNCTION_SUPPORT)
		{
			MyFuncs[9] 	= new scalar_function("ADD",1,"ip_func_add",XO_TYPE_INTEGER,2);
			MyFuncs[10] = new scalar_function("INTEGER","ADD",1,"ip_func_int_add",XO_TYPE_INTEGER,2);
			MyFuncs[11] = new scalar_function("INTEGER","SUBSTRACT",1,"ip_func_int_substract",XO_TYPE_INTEGER,2);
			MyFuncs[12] = new scalar_function("STRING","ADD",1,"ip_func_string_add",XO_TYPE_VARCHAR,2);
			MyFuncs[13] = new scalar_function("DOUBLE","ADD",1,"ip_func_double_add",XO_TYPE_DOUBLE,2);
			MyFuncs[14] = new scalar_function("DOUBLE","SUBSTRACT",1,"ip_func_double_substract",XO_TYPE_DOUBLE,2);
			MyFuncs[15] = new scalar_function("DATE","DATEVAL",1,"ip_func_date_dateval",XO_TYPE_DATE,1);
		}
		
        return MyFuncs;
    }
	
	public long ip_func_add(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
        long hValExp1,hValExp2;
        xo_int piRetCode = new xo_int(0);
        Integer iFirstArgValue,iSecondArgVal;
		Integer iResultVal = 0;

        /* get the first argument value */
        hValExp1 = jdam.dam_getFirstValExp(hValExpList);
        iFirstArgValue = (Integer) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp1, XO_TYPE_INTEGER, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(iFirstArgValue == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, null, XO_NULL_DATA);
            return hVal;
        }
		
    	/* get the second argument value */
        hValExp2 = jdam.dam_getNextValExp(hValExpList);
        iSecondArgVal = (Integer) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp2, XO_TYPE_INTEGER, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(iSecondArgVal == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, null, XO_NULL_DATA);
            return hVal;
        }
		
		iResultVal = iFirstArgValue + iSecondArgVal;
		
		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, iResultVal, 0);

        return hVal;
	}
	
	public long ip_func_int_add(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
        long hValExp1,hValExp2;
        xo_int piRetCode = new xo_int(0);
        Integer iFirstArgValue,iSecondArgVal;
		Integer iResultVal = 0;

        /* get the first argument value */
        hValExp1 = jdam.dam_getFirstValExp(hValExpList);
        iFirstArgValue = (Integer) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp1, XO_TYPE_INTEGER, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(iFirstArgValue == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, null, XO_NULL_DATA);
            return hVal;
        }
		
    	/* get the second argument value */
        hValExp2 = jdam.dam_getNextValExp(hValExpList);
        iSecondArgVal = (Integer) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp2, XO_TYPE_INTEGER, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(iSecondArgVal == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, null, XO_NULL_DATA);
            return hVal;
        }
		
		iResultVal = iFirstArgValue + iSecondArgVal;
		
		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, iResultVal, 0);

        return hVal;	
	}

	public long ip_func_int_substract(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
        long hValExp1,hValExp2;
        xo_int piRetCode = new xo_int(0);
        Integer iFirstArgValue,iSecondArgVal;
		Integer iResultVal = 0;

        /* get the first argument value */
        hValExp1 = jdam.dam_getFirstValExp(hValExpList);
        iFirstArgValue = (Integer) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp1, XO_TYPE_INTEGER, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(iFirstArgValue == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, null, XO_NULL_DATA);
            return hVal;
        }
		
    	/* get the second argument value */
        hValExp2 = jdam.dam_getNextValExp(hValExpList);
        iSecondArgVal = (Integer) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp2, XO_TYPE_INTEGER, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(iSecondArgVal == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, null, XO_NULL_DATA);
            return hVal;
        }
		
		iResultVal = iFirstArgValue - iSecondArgVal;
		
		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, iResultVal, 0);

        return hVal;	
	}
	
	public long ip_func_string_add(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
        long hValExp1,hValExp2;
        xo_int piRetCode = new xo_int(0);
        String sFirstArgValue,sSecondArgVal;
		String sResultVal;

        /* get the first argument value */
        hValExp1 = jdam.dam_getFirstValExp(hValExpList);
        sFirstArgValue = (String) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp1, XO_TYPE_VARCHAR, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(sFirstArgValue == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_VARCHAR, null, XO_NULL_DATA);
            return hVal;
        }
		
    	/* get the second argument value */
        hValExp2 = jdam.dam_getNextValExp(hValExpList);
        sSecondArgVal = (String) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp2, XO_TYPE_VARCHAR, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(sSecondArgVal == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_VARCHAR, null, XO_NULL_DATA);
            return hVal;
        }
		
		sResultVal = sFirstArgValue + sSecondArgVal;
		
		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_VARCHAR, sResultVal, XO_NTS);

        return hVal;	
	}
	
	public long ip_func_int_count(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
        long hValExp;
		long iResultVal = 0;

        /* get the input */
        hValExp = jdam.dam_getFirstValExp(hValExpList);
        while (hValExp != 0) {
		    iResultVal++;
		    hValExp = jdam.dam_getNextValExp(hValExpList);
	    }
		
		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, iResultVal, 0);

        return hVal;	
	}
	
	
	public long ip_func_double_add(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
        long hValExp1,hValExp2;
        xo_int piRetCode = new xo_int(0);
        Double dFirstArgValue,dSecondArgVal;
		Double dResultVal;

        /* get the first argument value */
        hValExp1 = jdam.dam_getFirstValExp(hValExpList);
        dFirstArgValue = (Double) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp1, XO_TYPE_DOUBLE, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(dFirstArgValue == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, null, XO_NULL_DATA);
            return hVal;
        }
		
    	/* get the second argument value */
        hValExp2 = jdam.dam_getNextValExp(hValExpList);
        dSecondArgVal = (Double) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp2, XO_TYPE_DOUBLE, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(dSecondArgVal == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, null, XO_NULL_DATA);
            return hVal;
        }
		
		dResultVal = dFirstArgValue + dSecondArgVal;
		
		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, dResultVal, 0);

        return hVal;	
	}
	
	
	public long ip_func_double_substract(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
        long hValExp1,hValExp2;
        xo_int piRetCode = new xo_int(0);
        Double dFirstArgValue,dSecondArgVal;
		Double dResultVal;

        /* get the first argument value */
        hValExp1 = jdam.dam_getFirstValExp(hValExpList);
        dFirstArgValue = (Double) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp1, XO_TYPE_DOUBLE, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(dFirstArgValue == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, null, XO_NULL_DATA);
            return hVal;
        }
		
    	/* get the second argument value */
        hValExp2 = jdam.dam_getNextValExp(hValExpList);
        dSecondArgVal = (Double) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp2, XO_TYPE_DOUBLE, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(dSecondArgVal == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, null, XO_NULL_DATA);
            return hVal;
        }
		
		dResultVal = dFirstArgValue - dSecondArgVal;
		
		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, dResultVal, 0);

        return hVal;	
	}
	
	public long ip_func_date_dateval(long hstmt,long pMemTree,long hValExpList)
	{
		long hVal;
        long hValExp;
        xo_int piRetCode = new xo_int(0);
        xo_tm pInputXoTime,ResultXoTime;

        /* get the input */
        hValExp = jdam.dam_getFirstValExp(hValExpList);
        pInputXoTime = (xo_tm) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_DATE, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(pInputXoTime == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DATE, null, XO_NULL_DATA);
            return hVal;
        }
		
		ResultXoTime = pInputXoTime;
		
		hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DATE, ResultXoTime, 0);

        return hVal;	
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

    public long ip_func_bigintval(long hstmt,long pMemTree,long hValExpList)
    {
        long hVal;
        long hValExp;
        xo_int piRetCode = new xo_int(0);
        Long lObj;

        /* get the input */
        hValExp = jdam.dam_getFirstValExp(hValExpList);
        lObj = (Long) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_BIGINT, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(lObj == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_BIGINT, null, XO_NULL_DATA);
            return hVal;
        }

        hVal = jdam.dam_createVal(pMemTree, XO_TYPE_BIGINT, lObj, 0);

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

    public long ip_func_translate(long hstmt,long pMemTree,long hValExpList)
    {
        long     hVal;

        /* create the value to return */
        String strObj = "POST-EXEC-" + giTranslateNum++;
        hVal = jdam.dam_createVal(pMemTree, XO_TYPE_VARCHAR, strObj, XO_NTS);

        return hVal;
    }

    public long ip_func_public_ip(long hstmt,long pMemTree,long hValExpList)
    {
        long     hVal;
		int  iRetCode = 0;
        StringBuffer sDamInfo = new StringBuffer();

        iRetCode = jdam.dam_getInfo(0, hstmt, DAM_INFO_CLIENT_PUBLIC_ADDRESS, sDamInfo, null);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"[MEM IP]DAM_INFO_CLIENT_PUBLIC_ADDRESS=<" + sDamInfo.toString() + ">\n");

        /* create the value to return */
        hVal = jdam.dam_createVal(pMemTree, XO_TYPE_VARCHAR, sDamInfo.toString(), XO_NTS);

        return hVal;
    }

    public long ip_func_private_ip(long hstmt,long pMemTree,long hValExpList)
    {
        long     hVal;
		int  iRetCode = 0;
        StringBuffer sDamInfo = new StringBuffer();

        iRetCode = jdam.dam_getInfo(0, hstmt, DAM_INFO_CLIENT_ADDRESS, sDamInfo, null);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"[MEM IP]DAM_INFO_CLIENT_ADDRESS=<" + sDamInfo.toString() + ">\n");

        /* create the value to return */
        hVal = jdam.dam_createVal(pMemTree, XO_TYPE_VARCHAR, sDamInfo.toString(), XO_NTS);

        return hVal;
    }

    public long ip_func_maxrows(long hstmt,long pMemTree,long hValExpList)
    {
        long hVal;
        int  iRetCode = 0;
        xo_long piValue = new xo_long(0);

        iRetCode = jdam.dam_getInfo(0, hstmt, DAM_INFO_QUERY_MAX_ROWS,
                                            null, piValue);

        hVal = jdam.dam_createVal(pMemTree, XO_TYPE_BIGINT, piValue, 0);

        return hVal;
    }

    /* client info */
    public long ip_func_clientinfo(long hstmt, long pMemTree, long hValExpList) {
        long hVal;
        int iRetCode = 0;
        StringBuffer sClientInfo = new StringBuffer();

        /* Append the client info values */
        sClientInfo.append("ApplicationName:")
                .append(m_sClientInfoAppName.toString()).append(",");
        sClientInfo.append("AccountingInfo:")
                .append(m_sClientInfoAccInfo.toString()).append(",");
        sClientInfo.append("ClientHostname:")
                .append(m_sClientInfoHostName.toString()).append(",");
        sClientInfo.append("ClientUser:").append(m_sClientInfoUser.toString())
                .append(",");
        sClientInfo.append("ProgramID:")
                .append(m_sClientInfoProgID.toString());

        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "[MEM IP]ClientInfo =<"
                + sClientInfo.toString() + ">\n");

        /* create the value to return */
        hVal = jdam.dam_createVal(pMemTree, XO_TYPE_WVARCHAR,
                sClientInfo.toString(), XO_NTS);

        return hVal;
    }

    public int ipGetLongData(long dam_hstmt, Object locator, int iXOType, int iOpType, char[] Buffer, long lBufferLen, xo_long piLenOrInd)
    {
        try
        {
            BufferedReader fpSource = (BufferedReader)locator;
            long nAmtRead;

            jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipGetLongData called.\n");

            if (DAM_LOB_CLOSE == iOpType)
            {
                /* Close the files */
                fpSource.close();
                return DAM_SUCCESS;
            }

            if (null == Buffer)
            {
                if (null != piLenOrInd)
                {
                    piLenOrInd.setVal(XO_NO_TOTAL);
                }
                return DAM_SUCCESS;
            }

            switch (iXOType)
            {
                case XO_TYPE_CHAR:
                case XO_TYPE_VARCHAR:
                case XO_TYPE_LONGVARCHAR:
                    {
                        nAmtRead = fpSource.read(Buffer, 0, (int)lBufferLen);
                    }
                    break;
                case XO_TYPE_WCHAR:
                case XO_TYPE_WVARCHAR:
                case XO_TYPE_WLONGVARCHAR:
                    {
                        nAmtRead = fpSource.read(Buffer, 0, (int)lBufferLen);
                    }
                    break;
                default:
                    return DAM_FAILURE;
            }

            if (-1 == nAmtRead) return DAM_NO_DATA_FOUND;
            if(nAmtRead < lBufferLen) {
                piLenOrInd.setVal(nAmtRead);
                fpSource.close();
                return DAM_SUCCESS;
            }
            else {
                piLenOrInd.setVal(XO_NO_TOTAL);
                return DAM_SUCCESS_WITH_INFO;
            }
        }
        catch (Exception e)
        {
            jdam.trace(m_tmHandle, UL_TM_F_TRACE, "Exception in ipGetLongData" + e.toString() + "\n");
            return DAM_FAILURE;
        }
    }

    public int ipGetLongData(long dam_hstmt, Object locator, int iXOType, int iOpType, byte[] Buffer, long lBufferLen, xo_long piLenOrInd)
    {
        try
        {
            DataInputStream  fpSource = (DataInputStream)locator;
            long            nAmtRead;

            jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipGetLongData called.\n");

            if (DAM_LOB_CLOSE == iOpType)
            {
                /* Close the files */
                fpSource.close();
                return DAM_SUCCESS;
            }

            if (null == Buffer)
            {
                if (null != piLenOrInd)
                {
                    piLenOrInd.setVal(XO_NO_TOTAL);
                }
                return DAM_SUCCESS;
            }

            switch (iXOType)
            {
                case XO_TYPE_BINARY:
                case XO_TYPE_VARBINARY:
                case XO_TYPE_LONGVARBINARY:
                    {
                        nAmtRead = fpSource.read(Buffer, 0, (int)lBufferLen);
                    }
                    break;
                default:
                    return DAM_FAILURE;
            }

            if (-1 == nAmtRead) return DAM_NO_DATA_FOUND;

            System.out.println("nAmtRead " + nAmtRead);
            if(nAmtRead < lBufferLen) {
                piLenOrInd.setVal(nAmtRead);
                fpSource.close();
                return DAM_SUCCESS;
            }
            else {
                piLenOrInd.setVal(XO_NO_TOTAL);
                return DAM_SUCCESS_WITH_INFO;
            }
        }
        catch (Exception e)
        {
            jdam.trace(m_tmHandle, UL_TM_F_TRACE, "Exception in ipGetLongData" + e.toString() + "\n");
            return DAM_FAILURE;
        }
    }

    void mem_get_ip_connection_info(long dam_hdbc)
    {
        StringBuffer    sApplicationName;
        StringBuffer    sSessionToken;
        StringBuffer    sServiceIPPath;
        StringBuffer    sLanguageID;
        StringBuffer    sClientProdVersion;
        StringBuffer    sSessionCipherSuite;
        xo_int		    iClientType;
        xo_int		    iSessionCryptoProtocolVersion;
        xo_int          iConnectionModel;

        sApplicationName = new StringBuffer(128 + 1);
        sClientProdVersion = new StringBuffer(128 + 1);
        sSessionCipherSuite = new StringBuffer(128 + 1);
        sSessionToken    = new StringBuffer(128 + 1);
        sLanguageID      = new StringBuffer(128 + 1);
        sServiceIPPath	 = new StringBuffer(512 + 1);
        iClientType		 = new xo_int();
        iSessionCryptoProtocolVersion = new xo_int();
        iConnectionModel = new xo_int();

        jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_CLIENT_APPLICATION_NAME, sApplicationName,null);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipConnect() Application Name:<"+ sApplicationName.toString() +">\n");

        jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_SESSION_TOKEN, sSessionToken,null);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipConnect() Session Token:<"+sSessionToken.toString()+">\n");

        jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_CLIENT_TYPE,null,iClientType);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipConnect() Client Type:<"+mem_get_client_type(iClientType.getVal())+">\n");

        jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_CLIENT_PRODUCT_VERSION, sClientProdVersion,null);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipConnect() Client Product Version:<"+ sClientProdVersion.toString() +">\n");

        jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_SESSION_CRYPTO_PROTOCOL_VERSION,null,iSessionCryptoProtocolVersion);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipConnect() Session Crypto Protocol Version:<"+mem_get_session_protocol_description(iSessionCryptoProtocolVersion.getVal())+">\n");

        jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_SESSION_CIPHER_SUITE, sSessionCipherSuite,null);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipConnect() Session Cipher Suite:<"+ sSessionCipherSuite.toString() +">\n");

        jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_LANGUAGE_ID,sLanguageID,null);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipConnect() Client Language ID:<"+sLanguageID.toString()+">\n");

        jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_CONNECTION_MODEL,null,iConnectionModel);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipConnect() Connection Model:<"+mem_get_connection_model(iConnectionModel.getVal())+">\n");

        jdam.dam_getInfo(dam_hdbc, 0, DAM_INFO_SERVICEIPPATH, sServiceIPPath, null);
        jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "ipConnect() ServiceIPPath:<"+sServiceIPPath.toString()+">\n");
    }

    String mem_get_client_type(int iClientType)
    {
        switch(iClientType)
        {
            case DAM_CLIENTTYPE_ODBC:
                return "ODBC Client";
            case DAM_CLIENTTYPE_JDBC:
                return "JDBC Client";
            case DAM_CLIENTTYPE_OLEDB:
                return "OLEDB Client";
            case DAM_CLIENTTYPE_NET:
                return "ADO .NET Client";
            default:
                break;
        }
        return "Unknown Client";
    }

    String mem_get_session_protocol_description(int iSessionCryptoProtocolVersion)
    {
        switch(iSessionCryptoProtocolVersion)
        {
            case DAM_VERSION_TLS1:
                return "TLSV1";
            case DAM_VERSION_TLS1_1:
                return "TLSV1.1";
            case DAM_VERSION_TLS1_2:
                return "TLSV1.2";
		default:
			break;
        }
          return "UNDEFINED";
    }

    String mem_get_connection_model(int iConnectionModel)
    {
        switch(iConnectionModel)
        {
            case DAM_CONNECTMODEL_THREADPOOL:
                return "Thread Pool";
            case DAM_CONNECTMODEL_PROCESS:
                return "Process";
            case DAM_CONNECTMODEL_THREAD:
                return "Thread";
		default:
			break;
        }
        return "Unknown Connection Model";
    }

    boolean      mem_get_longchar_data(String sFileName,StringBuffer pData)
    {
        BufferedReader fpSource = null;
        char[]  pBuffer;
        long    nBufferSize;
        long    nAmtRead;

        try
        {
            /* Open the source file */
            fpSource = new BufferedReader(new FileReader(sFileName));
            /* allocate a buffer */
            pBuffer = new char[RESULT_BUFFER_LOB_MAX_SIZE];
            nBufferSize = RESULT_BUFFER_LOB_MAX_SIZE;

            nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);
            while (nAmtRead > 0)
            {
                pData.append(pBuffer,0,(int)nAmtRead);
                nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);
            }
            /* Close the files */
            fpSource.close();
        }
        catch(Exception e)
        {
            return false;
        }

        return true;
    }

    boolean         mem_get_longwchar_data(String sFileName,StringBuffer pData)
    {
        BufferedReader fpSource = null;
        char[]  pBuffer;
        long    nBufferSize;
        long    nAmtRead;
        FileInputStream fis;

        try
        {
            /* Open the source file */
            fis = new FileInputStream(new File(sFileName));
           fpSource = new BufferedReader(new InputStreamReader(fis, "UTF-16LE"));

            /* allocate a buffer */
            pBuffer = new char[RESULT_BUFFER_LOB_MAX_SIZE];
            nBufferSize = RESULT_BUFFER_LOB_MAX_SIZE;

            nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);

            while(nAmtRead > 0)
            {
                pData.append(pBuffer,0,(int)nAmtRead);
                nAmtRead = fpSource.read(pBuffer, 0, (int)nBufferSize);
            }

            /* Close the files */
            fpSource.close();
        }
        catch(Exception e)
        {
            return false;
        }

        return true;
    }

    byte[]         mem_get_long_binary_data(String sFileName)
    {
        DataInputStream fpSource = null;
        byte[] pData;
        int    nBufferSize;
        long    nAmtRead;

        try
        {
            /* Open the source file */
            fpSource = new DataInputStream(new FileInputStream(sFileName));
            /* allocate a buffer */

               nBufferSize = fpSource.available();
            /* allocate a buffer */
            pData = new byte[nBufferSize];
            nAmtRead = fpSource.read(pData, 0, (int)nBufferSize);
            if (nAmtRead < 0)
            {
                  return null;
            }

            /* Close the files */
            fpSource.close();
        }
        catch(Exception e)
        {
            return null;
        }

        return pData;
    }

    /************************************************************************
    Function:       mem_exec_bulk_insert()
    Description:    Implementation of Bulk Insert
    Return:         IP_SUCESS on success
                    IP_FAILURE on error
    ************************************************************************/
    int         mem_exec_bulk_insert(MEM_STMT_DA pStmtDA, xo_long piNumResRows)
    {

    	long         	hrow;
		long            hRowElem;
		long            hcol;
		StringBuffer    sColName = new StringBuffer();
		xo_long         iRowCount =  new xo_long(0);
		xo_int         iXoType =  new xo_int(0);
		Object[]        objects;
		int             iRow = 0, index = 0;

    	jdam.trace(m_tmHandle, UL_TM_F_TRACE,"mem_exec_bulk_insert called\n");

    	/* for each row to be inserted, get the bulk values */
		hrow = jdam.dam_getFirstInsertRow(pStmtDA.dam_hstmt);
		if (hrow == 0) {
			jdam.dam_addError(0, pStmtDA.dam_hstmt, DAM_IP_ERROR, 1, "jdam.dam_getFirstInsertRow() returned null");
			return IP_SUCCESS;
		}

		hRowElem = jdam.dam_getFirstValueSet(pStmtDA.dam_hstmt, hrow);
		if(hRowElem == 0) return IP_SUCCESS;

		while(hRowElem != 0)
		{
			/* get all the columns that need to be updated */
			hcol = jdam.dam_getColToSet(hRowElem);
			jdam.dam_describeCol(hcol, null, sColName, iXoType, null);
			objects = jdam.dam_getBulkValueToSet(pStmtDA.dam_hstmt, hRowElem, iRowCount);
			jdam.dam_freeBulkValue(objects);
			hRowElem = jdam.dam_getNextValueSet(pStmtDA.dam_hstmt);
		}

		/* get the param status array */
		byte[] rowStatusArray = jdam.dam_getBulkRowStatusArray(pStmtDA.dam_hstmt);
		if(rowStatusArray == null) return IP_FAILURE;

		while(iRow < iRowCount.getVal())
		{
			rowStatusArray[iRow] = DAM_ROW_SUCCESS;
			iRow++;

		}

		piNumResRows.setVal(iRow);
		jdam.trace(m_tmHandle, UL_TM_F_TRACE,"No of rows processed successfully in Bulk Insert : "+iRow+" \n");
		return IP_SUCCESS;
    }

}

/********************************************************************************************
    Class:          MEM_STMT_DA
    Description:    Store Statement level information

*********************************************************************************************/

class MEM_STMT_DA {
    long                pMemTree;
    long                dam_hstmt;      /* DAM handle to the statement */

    StringBuffer        sTableName;   /* Name of the table being queried */
    int                 iTable;     /* indicates table being queried: employee or dept table */
    int                 iType;          /* indicates SELECT,INSERT etc */

    xo_long             hindex;             /* DAM_HINDEX */
    xo_long             hset_of_condlist;   /* DAM_HSET_OF_CONDLIST */

    long                hSearchCol;     /* indicates if there is a search column for the query */
    long                hSearchCondList;
    long                hSearchCond;    /* indicates the current condition being processed */

    long                lItems;
    long                lCurItem;
    long                lDeptId;
    long                lEmpId;

    long                iFetchSize;     /* records to return in cursor mode */
    long                iTopRows;       /* any setting indicating TOP N clause */
    long                iMaxRows;       /* Maximum number of rows to be returned */
    long                iTotalRowCount; /* total number of rows returned */
    long                hcolEmpId;
    long                hcolEname;
    long                hcolDeptId;
    long                hcolItems;
    long                hcolDname;
    long                hcolDate;
    long                hcolDouble;
    long                hcolNumeric;
    long		hcolNote;
    long		hcolComment;
    long		hcolCommentXL;
    long		hcolStamp;
    long		hcolPicture;
    long		hcolPictureXL;

    long                hcolTag, hcolValue;
    String              sName;
    /* Bulk Fetch */
    int             m_iNoOfResColumns;
    ResultBuffer	m_resultBuffer;

    /* TypesTable - Bulk Fetch */
    int			m_iTypesTableCurItem;
    MEM_STMT_DA()
    {
        hindex= new xo_long(0);
        hset_of_condlist = new xo_long(0);
        sTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        m_resultBuffer = null;
        m_iNoOfResColumns = 0;
    }
};

/* Procedure Descriptor Area */
class MEM_PROC_DA {
    long                pMemTree;
    long                dam_hstmt;      /* DAM handle to the statement */

    StringBuffer        sQualifier;
    StringBuffer        sOwner;
    StringBuffer        sProcName;      /* Name of the Procedure being queried */
    StringBuffer        sUserData;

	int			iTotalResultSets;	/* Number of result sets that should be returned */
	int			iCurResultSetNum;	/* current result set being processed */
	long		iItems;				/* number of rows in each result set */
 	long		iCurItems;			/* number of rows in each result set */
    	long        iFetchSize;     /* records to return in cursor mode */

    MEM_PROC_DA()
    {
        sQualifier = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        sOwner = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        sProcName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        sUserData = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

        iTotalResultSets = 0;
        iCurResultSetNum = 0;
        iItems = 0;
	iCurItems = 0;
	iFetchSize = 0;
    }
};

class MEM_NC_DA {
    StringBuffer    sCmd;

    MEM_NC_DA()
    {
        sCmd = new StringBuffer(ip.XO_MAX_STMT_LENGTH+1);
    }

    };

class FORMAT
{
    private long m_tmHandle = 0;

    private StringBuffer    m_sWorkingDir; /* [DAM_MAXPATH]; */
    private boolean         m_bPushPostProcessing; /* Indicates if Post-processing (GROUP BY, ORDER BY etc)
                                                    should be sent to back-end or allow DAM to process */
    private boolean         m_bUseOriginalSelectList; /* Indicates if query sent to backend should use original select list
                                                        expressions or just columns in use. When original query
                                                        has GROUP BY, SET functions and  gbPushPostProcessing is FALSE,
                                                        IP should return values for base columns so that DAM can do the post-processing
                                                      */
    public FORMAT()
    {
        m_tmHandle = 0;
        m_sWorkingDir = new StringBuffer(ip.DAM_MAXPATH + 1);

        m_bPushPostProcessing    = false;
        m_bUseOriginalSelectList = true;
    }


/************************************************************************
Function:       ip_format_query()
Description:
Return:
************************************************************************/
    public  int ip_format_query(long hquery, StringBuffer pSqlBuffer)
    {
        int         iQueryType;

        iQueryType = jdam.damex_getQueryType(hquery);

        switch (iQueryType)
        {
        case ip.DAM_SELECT:
            ip_format_select_query(hquery, pSqlBuffer);
            break;
        case ip.DAM_INSERT:
            ip_format_insert_query(hquery, pSqlBuffer);
            break;
        case ip.DAM_UPDATE:
            ip_format_update_query(hquery, pSqlBuffer);
            break;
        case ip.DAM_DELETE:
            ip_format_delete_query(hquery, pSqlBuffer);
            break;
	case ip.DAM_TABLE:
            ip_format_table_query(hquery,pSqlBuffer);
        default:
            break;
        }

        return ip.IP_SUCCESS;
    }

/********************************************************************************************
    Method:         ip_format_select_query
    Description:    Format the given query
    Return:         IP_SUCCESS on success
                    IP_FAILURE on error
*********************************************************************************************/

    public  int ip_format_select_query(long hquery, StringBuffer pSqlBuffer)
    {
    xo_int            piSetQuantifier;
    xo_long           phSelectValExpList, phGroupValExpList, phOrderValExpList;
    xo_long           phSearchExp;
    xo_long           phHavingExp;
    xo_long           piTopResRows;
    xo_int            pbTopPercent;
    xo_int            piUnionType = new xo_int(0);
    xo_long           phUnionQuery = new xo_long(0);

    piSetQuantifier     = new xo_int(0);
    phSelectValExpList  = new xo_long(0);
    phGroupValExpList   = new xo_long(0);
    phOrderValExpList   = new xo_long(0);
    phSearchExp         = new xo_long(0);
    phHavingExp         = new xo_long(0);
    piTopResRows        = new xo_long(0);
    pbTopPercent        = new xo_int(0);

    try
    {
    jdam.damex_describeSelectQuery(hquery, piSetQuantifier,
                                        phSelectValExpList,
                                        phSearchExp,
                                        phGroupValExpList,
                                        phHavingExp,
                                        phOrderValExpList);

    jdam.damex_describeSelectTopClause(hquery, piTopResRows, pbTopPercent);


    /* check if query cannot use orginal select expression */
            if (!m_bPushPostProcessing)
            {
        m_bUseOriginalSelectList = ip_isOriginalSelectListCompatible(phSelectValExpList.getVal(),
                                        phGroupValExpList.getVal(), phOrderValExpList.getVal());
        }

    /* get the table list */
    {
    long        htable;

    htable = jdam.damex_getFirstTable(hquery);
                while (htable != 0)
                {
                    xo_int      piTableNum = new xo_int(0);
                    StringBuffer wsTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
                    long    hCol;
                    StringBuffer wsColName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

                    jdam.damex_describeTable(htable, piTableNum, null, null, wsTableName, null, null);
                    /*
                     System.out.print("Table:" + sTableName +". Columns Used: ", sTableName);
                     */
                    hCol = jdam.damex_getFirstCol(htable, ip.DAM_COL_IN_USE);
                    while (hCol != 0)
                    {
                        jdam.damex_describeCol(hCol, null, null, wsColName, null, null, null, null);
                        /*
                         System.out.print(sColName + ",");
                         */
                        hCol = jdam.damex_getNextCol(htable);
                    }
                    /*
                     System.out.println("");
                     */
                    htable = jdam.damex_getNextTable(hquery);
                }
            }

            pSqlBuffer.append("SELECT ");
            if(!m_bUseOriginalSelectList)
            {
                ip_format_col_in_use(hquery, pSqlBuffer);
            }
            else
            {
                if ((piSetQuantifier.getVal() == ip.SQL_SELECT_DISTINCT) && (m_bPushPostProcessing))
                    pSqlBuffer.append("DISTINCT ");
                if (piTopResRows.getVal() != ip.DAM_NOT_SET)
                {
                    pSqlBuffer.append("TOP ").append(piTopResRows.getVal()).append(" ");
                    if (pbTopPercent.getVal() != 0)
                        pSqlBuffer.append("PERCENT ");
                }
                ip_format_valexp_list(hquery, phSelectValExpList.getVal(), pSqlBuffer);
            }

            pSqlBuffer.append(" FROM ");
            ip_format_table_list(hquery, pSqlBuffer);
            if (phSearchExp.getVal() != 0)
            {
                pSqlBuffer.append("WHERE ");
                ip_format_logexp(hquery, phSearchExp.getVal(), pSqlBuffer);
            }
            if ((phGroupValExpList.getVal() != 0) && (m_bPushPostProcessing))
            {
                pSqlBuffer.append(" GROUP BY ");
                ip_format_group_list(hquery, phGroupValExpList.getVal(), pSqlBuffer);
            }
            if ((phHavingExp.getVal() != 0) && (m_bPushPostProcessing))
            {
                pSqlBuffer.append(" HAVING ");
                ip_format_logexp(hquery, phHavingExp.getVal(), pSqlBuffer);
            }

            /* check if query has a UNION clause */
            jdam.damex_describeUnionQuery(hquery, piUnionType, phUnionQuery);

            if (phUnionQuery.getVal() != 0)
            {
                pSqlBuffer.append(" UNION ");
                if (piUnionType.getVal() != 0)
                    pSqlBuffer.append(" ALL ");
                ip_format_select_query(phUnionQuery.getVal(), pSqlBuffer);
            }

            if ((phOrderValExpList.getVal() != 0) && (m_bPushPostProcessing))
            {
                pSqlBuffer.append(" ORDER BY ");
                ip_format_order_list(hquery, phOrderValExpList.getVal(), pSqlBuffer);
            }

        }
        catch(Exception e)
        {
            jdam.trace(m_tmHandle, ip.UL_TM_ERRORS, "Error: " + e + "\n");
        }

        return ip.IP_SUCCESS;

    }

/********************************************************************************************
    Method:         ip_format_insert_query
    Description:    Format the given query
    Return:         IP_SUCCESS on success
                    IP_FAILURE on error
*********************************************************************************************/

    public  int 	ip_format_insert_query(long hquery, StringBuffer pSqlBuffer)
    {
	xo_long 	phTable, phColList,phInputRowList, phInsertQuery;
        StringBuffer    wsCatalog   = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        StringBuffer    wsSchema    = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
	StringBuffer    wsTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

	phTable		= new xo_long(0);
	phColList	= new xo_long(0);
	phInputRowList	= new xo_long(0);
	phInsertQuery	= new xo_long(0);

        try
        {
            jdam.damex_describeInsertQuery(hquery, phTable, phColList,phInputRowList, phInsertQuery);
	    pSqlBuffer.append("INSERT INTO ");
            jdam.damex_describeTable(phTable.getVal(), null, wsCatalog, wsSchema, wsTableName, null, null);

	    if(wsCatalog != null && wsCatalog.length() > 0)
                pSqlBuffer.append(wsCatalog).append(".");
            if(wsSchema != null && wsSchema.length() > 0)
                pSqlBuffer.append(wsSchema).append(".");
	    pSqlBuffer.append(wsTableName).append(" ");
            /* columns */
	    {
		long    	hCol;
		int        	iFirst;

		pSqlBuffer.append("( ");

		iFirst = ip.TRUE;
		hCol = jdam.damex_getFirstColInList(phColList.getVal());

                while (hCol != 0)
                {
                    StringBuffer wsColName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

                    if(iFirst == ip.FALSE)
                        pSqlBuffer.append(", ");
                    else
                        iFirst = ip.FALSE;

                    jdam.damex_describeCol(hCol,null,null,wsColName, null, null, null, null);

                    pSqlBuffer.append(wsColName);
                    hCol = jdam.damex_getNextColInList(phColList.getVal());
                }
                pSqlBuffer.append(" ) ");
            }
            if(phInputRowList.getVal() != 0)
            {
                pSqlBuffer.append("VALUES ");
                ip_format_insertrow_list(hquery, phInputRowList.getVal(), pSqlBuffer);
            }
            else
            {
                ip_format_query(phInsertQuery.getVal(), pSqlBuffer);
            }

        }
        catch(Exception e)
        {
            jdam.trace(m_tmHandle, ip.UL_TM_ERRORS, "Error: " + e + "\n");
        }
        return ip.IP_SUCCESS;
    }

    public int     ip_format_insertrow_list(long hquery, long hRowList, StringBuffer pSqlBuffer)
    {
        try
        {
            long    hRow;
            int     iFirst = ip.TRUE;
            int     iMultiRowInsert = ip.FALSE;

            hRow = jdam.damex_getFirstInsertRow(hRowList);
            if (jdam.damex_getNextInsertRow(hRowList) != 0) iMultiRowInsert = ip.TRUE;

            if (iMultiRowInsert == ip.TRUE)
                pSqlBuffer.append("( ");

            hRow = jdam.damex_getFirstInsertRow(hRowList);
            while (hRow != 0)
            {
                if (iFirst == ip.FALSE)
                    pSqlBuffer.append(", ");
                else
                    iFirst = ip.FALSE;
                ip_format_insertrow(hquery, hRow, pSqlBuffer);

                hRow = jdam.damex_getNextInsertRow(hRowList);
            }

            if (iMultiRowInsert == ip.TRUE)
                pSqlBuffer.append("  )");
        }
        catch(Exception e)
        {
            jdam.trace(m_tmHandle, ip.UL_TM_ERRORS, "Error: " + e + "\n");
        }
        return ip.IP_SUCCESS;
    }

    public int     ip_format_insertrow(long hquery,long hRow,StringBuffer pSqlBuffer)
    {
        try
        {
            long    hValExp;
            int     iFirst = ip.TRUE;

            pSqlBuffer.append("( ");

            hValExp = jdam.damex_getFirstInsertValueExp(hquery, hRow);
            while (hValExp != 0)
            {
                if (iFirst == ip.FALSE)
                    pSqlBuffer.append(", ");
                else
                    iFirst = ip.FALSE;

                ip_format_valexp(hquery, hValExp, pSqlBuffer);

                hValExp = jdam.damex_getNextInsertValueExp(hquery);
            }
            pSqlBuffer.append(" )");
        }
        catch(Exception e)
        {
            jdam.trace(m_tmHandle, ip.UL_TM_ERRORS, "Error: " + e + "\n");
        }
        return ip.IP_SUCCESS;
    }

/********************************************************************************************
    Method:         ip_format_update_query
    Description:    Format the given query
    Return:         IP_SUCCESS on success
                    IP_FAILURE on error
*********************************************************************************************/

    public int ip_format_update_query(long hquery, StringBuffer pSqlBuffer)
    {
        try
        {
            xo_long     phTable;
            xo_long     phRow;
            xo_long     phSearchExp;
            xo_int              piTableNum;
            StringBuffer    wsTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
            StringBuffer    wsCatalog   = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
            StringBuffer    wsSchema    = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

            phTable         = new xo_long(0);
            phRow       = new xo_long(0);
            phSearchExp     = new xo_long(0);
            piTableNum      = new xo_int(0);

            jdam.damex_describeUpdateQuery(hquery, phTable, phRow, phSearchExp);

            pSqlBuffer.append("UPDATE ");

            jdam.damex_describeTable(phTable.getVal(), piTableNum, wsCatalog, wsSchema, wsTableName, null, null);

	    if(wsCatalog != null && wsCatalog.length() > 0)
                pSqlBuffer.append(wsCatalog).append(".");
            if(wsSchema != null && wsSchema.length() > 0)
                pSqlBuffer.append(wsSchema).append(".");
            pSqlBuffer.append(wsTableName).append(" T").append(piTableNum.getVal()).append("_Q").append(hquery).append(" ");

            ip_format_update_list(hquery, phRow.getVal(), pSqlBuffer);

            if (phSearchExp.getVal() != 0)
            {
                pSqlBuffer.append(" WHERE ");
                ip_format_logexp(hquery, phSearchExp.getVal(), pSqlBuffer);
            }
        }
        catch(Exception e)
        {
            jdam.trace(m_tmHandle, ip.UL_TM_ERRORS, "Error: " + e + "\n");
        }
        return ip.IP_SUCCESS;
    }

    public int     ip_format_update_list(long hquery, long hRow, StringBuffer pSqlBuffer)
    {
        try
        {
            xo_long     phCol;
            long        hValExp;
            int         iFirst;

            phCol       = new xo_long(0);
            iFirst = ip.TRUE;

            pSqlBuffer.append("SET ");
            hValExp = jdam.damex_getFirstUpdateSet(hquery, hRow, phCol);

            while (hValExp != 0)
            {
                if (iFirst == ip.FALSE)
                    pSqlBuffer.append(", ");
                else
                    iFirst = ip.FALSE;

                ip_format_col(hquery, phCol.getVal(), pSqlBuffer);
                pSqlBuffer.append(" = ");
                ip_format_valexp(hquery, hValExp, pSqlBuffer);
                hValExp = jdam.damex_getNextUpdateSet(hquery, phCol);
            }
        }
        catch(Exception e)
        {
            jdam.trace(m_tmHandle, ip.UL_TM_ERRORS, "Error: " + e + "\n");
        }
        return ip.IP_SUCCESS;
    }

/********************************************************************************************
    Method:         ip_format_delete_query
    Description:    Format the given query
    Return:         IP_SUCCESS on success
                    IP_FAILURE on error
*********************************************************************************************/

    public int ip_format_delete_query(long hquery, StringBuffer pSqlBuffer)
    {
        try
        {
            xo_long     phTable;
            xo_long     phSearchExp;
            StringBuffer    wsCatalog   = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
            StringBuffer    wsSchema    = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
            StringBuffer    wsTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

            phTable     = new xo_long(0);
            phSearchExp     = new xo_long(0);

            jdam.damex_describeDeleteQuery(hquery, phTable, phSearchExp);

            pSqlBuffer.append("DELETE FROM ");

            jdam.damex_describeTable(phTable.getVal(), null, wsCatalog, wsSchema, wsTableName, null, null);

	    if(wsCatalog != null && wsCatalog.length() > 0)
                pSqlBuffer.append(wsCatalog).append(".");
            if(wsSchema != null && wsSchema.length() > 0)
                pSqlBuffer.append(wsSchema).append(".");

            pSqlBuffer.append(wsTableName).append(" ");

            if (phSearchExp.getVal() != 0)
            {
                pSqlBuffer.append("WHERE ");
                ip_format_logexp(hquery, phSearchExp.getVal(), pSqlBuffer);
            }
        }
        catch(Exception e)
        {
            jdam.trace(m_tmHandle, ip.UL_TM_ERRORS, "Error: " + e + "\n");
        }
        return ip.IP_SUCCESS;
    }

    public int     ip_format_col_in_use(long hquery, StringBuffer pSqlBuffer)
    {
        long       htable;
        int        iFirst = ip.TRUE;

        htable = jdam.damex_getFirstTable(hquery);
        while (htable != 0)
        {
            xo_int      piTableNum = new xo_int(0);
            StringBuffer wsTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
            long    hCol;

            jdam.damex_describeTable(htable, piTableNum, null, null, wsTableName, null, null);
            hCol = jdam.damex_getFirstCol(htable, ip.DAM_COL_IN_USE);

            while (hCol != 0)
            {
                if(iFirst == ip.FALSE)
                    pSqlBuffer.append(", ");
                ip_format_col(hquery, hCol, pSqlBuffer);
                hCol = jdam.damex_getNextCol(htable);
                iFirst = ip.FALSE;
            }
            htable = jdam.damex_getNextTable(hquery);
        }
        return ip.IP_SUCCESS;
    }

    public int     ip_format_col(long hquery, long hCol, StringBuffer pSqlBuffer)
    {
        xo_int         piTableNum, piColNum;
        StringBuffer wsColName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        long         hParentQuery;

        piTableNum  = new xo_int(0);
        piColNum    = new xo_int(0);

        jdam.damex_describeCol(hCol,
            piTableNum,
            piColNum,
            wsColName, null, null, null, null);

        /* check if TableNum is valid. For COUNT(*) the column has iTableNum not set */
        if (piTableNum.getVal() == ip.DAM_NOT_SET)
            return ip.IP_SUCCESS;

        if(jdam.damex_getQueryType(hquery) == ip.DAM_SELECT)
        {
            if (jdam.damex_isCorrelatedCol(hCol) != 0)
            {
                hParentQuery = jdam.damex_getCorrelatedQuery(hCol);
                pSqlBuffer.append("T").append(piTableNum.getVal()).append("_Q").append(hParentQuery).append(".\"").append(wsColName).append("\"");
            }
            else
                pSqlBuffer.append("T").append(piTableNum.getVal()).append("_Q").append(hquery).append(".\"").append(wsColName).append("\"");;
        }
        else
        {
            pSqlBuffer.append("\"").append(wsColName).append("\"");
        }

        return ip.IP_SUCCESS;
    }


    public int     ip_format_valexp_list(long hquery, long hValExpList, StringBuffer pSqlBuffer)
    {
        long    hValExp;
        int     iFirst = ip.TRUE;
        StringBuffer      wsAsColName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);

        hValExp = jdam.damex_getFirstValExp(hValExpList);
        while (hValExp != 0)
        {
            if (iFirst == ip.FALSE)
                pSqlBuffer.append(", ");
            else
                iFirst = ip.FALSE;

            ip_format_valexp(hquery, hValExp, pSqlBuffer);

            jdam.damex_describeValExpEx(hValExp, wsAsColName, null);
            if (wsAsColName.length() > 0)
                pSqlBuffer.append(" AS \"").append(wsAsColName).append("\" ");

            hValExp = jdam.damex_getNextValExp(hValExpList);
        }

        pSqlBuffer.append(" ");
        return ip.IP_SUCCESS;
    }

    public int     ip_format_valexp(long hquery, long hValExp, StringBuffer pSqlBuffer)
    {
        xo_int      piType = new xo_int(0); /* literal value, column, +, -, *, / etc   */
        xo_int      piFuncType = new xo_int(0);
        xo_long     hLeftValExp = new xo_long(0);
        xo_long     hRightValExp = new xo_long(0);
        xo_long     hVal = new xo_long(0);
        xo_long     hScalarValExp = new xo_long(0);
        xo_long     hCaseValExp = new xo_long(0);
		xo_long     hRankValExp = new xo_long(0);
        int         iFuncType;
        xo_int      piSign = new xo_int(0);

        jdam.damex_describeValExp(hValExp, piType, /* literal value, column, +, -, *, / etc   */
            piFuncType,
            hLeftValExp,
            hRightValExp,
            hVal,
            hScalarValExp,
            hCaseValExp,
			hRankValExp
            );

        iFuncType = piFuncType.getVal();

        jdam.damex_describeValExpEx(hValExp, null, piSign);

        if(piSign.getVal() != 0)
        {
            pSqlBuffer.append("-(");
        }

        /* function type */
        if ((iFuncType & ip.SQL_F_COUNT_ALL) != 0) pSqlBuffer.append("COUNT(*) ");
        if ((iFuncType & ip.SQL_F_COUNT) != 0) pSqlBuffer.append("COUNT ");
        if ((iFuncType & ip.SQL_F_AVG) != 0) pSqlBuffer.append("AVG ");
        if ((iFuncType & ip.SQL_F_MAX) != 0) pSqlBuffer.append("MAX ");
        if ((iFuncType & ip.SQL_F_MIN) != 0) pSqlBuffer.append("MIN ");
        if ((iFuncType & ip.SQL_F_SUM) != 0) pSqlBuffer.append("SUM ");
        if ((iFuncType & ip.SQL_F_VAR) != 0) pSqlBuffer.append("VAR_SAMP ");
        if ((iFuncType & ip.SQL_F_VARP) != 0) pSqlBuffer.append("VAR_POP ");
        if ((iFuncType & ip.SQL_F_STDDEV) != 0) pSqlBuffer.append("STDDEV_SAMP ");
        if ((iFuncType & ip.SQL_F_STDDEVP) != 0) pSqlBuffer.append("STDDEV_POP ");

        if ((iFuncType != 0) && (iFuncType != ip.SQL_F_COUNT_ALL))
            pSqlBuffer.append("( ");
        if ((iFuncType & ip.SQL_F_DISTINCT) != 0) pSqlBuffer.append("DISTINCT ");
        switch (piType.getVal())
        {
            case ip.SQL_VAL_EXP_VAL:
                ip_format_val(hquery, hVal.getVal(), pSqlBuffer);
                break;
            case ip.SQL_VAL_EXP_ADD:
                pSqlBuffer.append("( ");
                ip_format_valexp(hquery, hLeftValExp.getVal(), pSqlBuffer);
                pSqlBuffer.append(" + ");
                ip_format_valexp(hquery, hRightValExp.getVal(), pSqlBuffer);
                pSqlBuffer.append(" )");

                break;
            case ip.SQL_VAL_EXP_SUBTRACT:
                pSqlBuffer.append("( ");
                ip_format_valexp(hquery, hLeftValExp.getVal(), pSqlBuffer);
                pSqlBuffer.append(" - ");
                ip_format_valexp(hquery, hRightValExp.getVal(), pSqlBuffer);
                pSqlBuffer.append(" )");

                break;

            case ip.SQL_VAL_EXP_MULTIPLY:

                pSqlBuffer.append("( ");
                ip_format_valexp(hquery, hLeftValExp.getVal(), pSqlBuffer);
                pSqlBuffer.append(" * ");
                ip_format_valexp(hquery, hRightValExp.getVal(), pSqlBuffer);
                pSqlBuffer.append(" )");

                break;

            case ip.SQL_VAL_EXP_DIVIDE:

                pSqlBuffer.append("( ");
                ip_format_valexp(hquery, hLeftValExp.getVal(), pSqlBuffer);
                pSqlBuffer.append(" / ");
                ip_format_valexp(hquery, hRightValExp.getVal(), pSqlBuffer);
                pSqlBuffer.append(" )");

                break;

            case ip.SQL_VAL_EXP_SCALAR:
                ip_format_scalar_valexp(hquery, hScalarValExp.getVal(), pSqlBuffer);
                break;

            case ip.SQL_VAL_EXP_CASE:
                ip_format_case_valexp(hquery, hCaseValExp.getVal(), pSqlBuffer);
                break;

			case ip.SQL_VAL_EXP_RANK:
            	ip_format_rank_valexp(hquery, hRankValExp.getVal(), pSqlBuffer);
            	break;

            default:            pSqlBuffer.append("Invalid Value Expression Type:").append(piType.getVal());
                break;
        }

        if ((iFuncType != 0) && (iFuncType != ip.SQL_F_COUNT_ALL))
            pSqlBuffer.append(")");


        if (piSign.getVal() != 0)
        {
            pSqlBuffer.append(")");
        }
        return ip.IP_SUCCESS;
    }

    public int     ip_format_scalar_cast(long hquery, long hValExpList, StringBuffer pSqlBuffer)
    {
        StringBuffer    sName;
        long            hValExp;
        xo_int         iResXoType = new xo_int(0);
        xo_int         iLength = new xo_int(0);
        xo_int         iPrecision = new xo_int(0);
        xo_int         iScale = new xo_int(0);
        String         sTypeName;

        sName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
        hValExp = jdam.damex_getFirstValExp(hValExpList);
        ip_format_valexp(hquery, hValExp, pSqlBuffer);

        jdam.dam_describeScalarEx(hValExpList, sName, null, iResXoType, iLength, iPrecision, iScale);
        sTypeName = map_type_to_name(iResXoType.getVal());
        pSqlBuffer.append(" AS " + sTypeName);
        if (iResXoType.getVal() == ip.XO_TYPE_NUMERIC || iResXoType.getVal() == ip.XO_TYPE_DECIMAL)
        {
            pSqlBuffer.append("(" + iPrecision.getVal() + "," + iScale.getVal() + ")");
        }

        return ip.IP_SUCCESS;
    }

    public int     ip_format_scalar_valexp(long hquery, long   hScalarValExp, StringBuffer pSqlBuffer)
    {
        StringBuffer    sName;
        xo_long      phValExpList;

        phValExpList = new xo_long();
        sName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);

        jdam.damex_describeScalarValExp(hScalarValExp, sName, phValExpList);

        /* check if scalar function refers to a special @@ identifier */
        if((sName.substring(0,2)) .equals ("@@"))
        {
            pSqlBuffer.append(sName);
            return ip.IP_SUCCESS;
        }

        /* handle CONVERT function */
        if (sName.toString().equals("CONVERT"))
        {
            pSqlBuffer.append("{fn ");
            pSqlBuffer.append(sName);
            pSqlBuffer.append("( ");
            //if (phValExpList.getVal() != 0) ip_format_scalar_convert(hquery, phValExpList.getVal(), pSqlBuffer);
            pSqlBuffer.append(") }" );
        }
        else if (sName.toString().equals("CAST"))
        {
            pSqlBuffer.append(sName);
            pSqlBuffer.append("( ");
            if (phValExpList.getVal() != 0) ip_format_scalar_cast(hquery, phValExpList.getVal(), pSqlBuffer);
            pSqlBuffer.append(")" );
        }
        else
        {
            pSqlBuffer.append(sName);
            pSqlBuffer.append("( ");
            if (phValExpList.getVal() != 0)
                ip_format_valexp_list(hquery, phValExpList.getVal(), pSqlBuffer);
            pSqlBuffer.append(") ");
        }
        return ip.IP_SUCCESS;
    }

    public int     ip_format_case_valexp(long hquery, long   hCaseValExp, StringBuffer pSqlBuffer)
    {
        xo_long             hInputValExp, hCaseElemList, hElseValExp;

        hInputValExp = new xo_long(0);
        hCaseElemList = new xo_long(0);
        hElseValExp = new xo_long(0);
        jdam.damex_describeCaseValExp(hCaseValExp, hInputValExp, hCaseElemList, hElseValExp);
        pSqlBuffer.append("CASE ");
        if (hInputValExp.getVal() != 0)
            ip_format_valexp(hquery, hInputValExp.getVal(), pSqlBuffer);
        pSqlBuffer.append(" ");
        ip_format_case_elem_list(hquery, hCaseElemList.getVal(), pSqlBuffer);
        if (hElseValExp.getVal() != 0)
        {
            pSqlBuffer.append(" ELSE ");
            ip_format_valexp(hquery, hElseValExp.getVal(), pSqlBuffer);
        }
        pSqlBuffer.append(" END ");

        return ip.IP_SUCCESS;
    }

	int     ip_format_rank_valexp(long hquery, long   hRankValExp, StringBuffer pSqlBuffer)
    {
    	xo_int iRankType = new xo_int();
    	xo_int iOrderType = new xo_int();
    	xo_long phOrderByValExpList = new xo_long();
    	xo_long phPartitionByValExpList = new xo_long();
    	
    	jdam.damex_describeRankValExp(hRankValExp, iRankType, phOrderByValExpList, phPartitionByValExpList);
    	
    	if(iRankType.getVal() == ip.SQL_RANK_VAL_RANK)
    	{
    		pSqlBuffer.append("RANK() OVER (");
    	}
    	else if(iRankType.getVal() == ip.SQL_RANK_VAL_DENSE_RANK)
    	{
    		pSqlBuffer.append("DENSE_RANK() OVER (");
    	}
    	else if(iRankType.getVal() == ip.SQL_RANK_VAL_NTILE)
    	{
    		pSqlBuffer.append("NTILE() OVER (");
    	}
    	else if(iRankType.getVal() == ip.SQL_RANK_VAL_ROW_NUMBER)
    	{
    		pSqlBuffer.append("ROW_NUMBER() OVER (");
    	}
    	
    	if (phPartitionByValExpList.getVal() != 0)
    	{
    		pSqlBuffer.append("PARTITION BY ");
            ip_format_valexp_list(hquery, phPartitionByValExpList.getVal(), pSqlBuffer);
    	}
    	
    	if (phOrderByValExpList.getVal() != 0)
    	{
    		pSqlBuffer.append("ORDER BY ");
    		ip_format_order_list(hquery, phOrderByValExpList.getVal(), pSqlBuffer);
    	}
    	
    	pSqlBuffer.append(")");
    	
    	return ip.IP_SUCCESS;
    }

    public int     ip_format_case_elem_list(long hquery, long   hCaseElemList, StringBuffer pSqlBuffer)
    {
        long             hCaseElem;
        xo_long          hWhenValExp;
        xo_long          hWhenBoolExp;
        xo_long          hResValExp;

        hWhenValExp = new xo_long(0);
        hWhenBoolExp = new xo_long(0);
        hResValExp = new xo_long(0);
        hCaseElem = jdam.damex_getFirstCaseElem(hCaseElemList);
        while (hCaseElem != 0)
        {
            pSqlBuffer.append(" WHEN ");

            jdam.damex_describeCaseElem(hCaseElem, hWhenValExp, hWhenBoolExp, hResValExp);
            if (hWhenValExp.getVal() != 0) ip_format_valexp(hquery, hWhenValExp.getVal(), pSqlBuffer);
            if (hWhenBoolExp.getVal() != 0) ip_format_logexp(hquery, hWhenBoolExp.getVal(), pSqlBuffer);
            pSqlBuffer.append(" THEN ");
            ip_format_valexp(hquery, hResValExp.getVal(), pSqlBuffer);

            hCaseElem = jdam.damex_getNextCaseElem(hCaseElemList);
        }

        return ip.IP_SUCCESS;
    }

    public int     ip_format_val(long   hquery, long hVal, StringBuffer pSqlBuffer)
    {
        int         iType; /* literal value, column */
        int         iXoType; /* type of literal value - INTEGER, CHAR etc */
        xo_int      piType, piXoType, piValLen;
        xo_long     hCol;
        xo_long     hSubQuery;
        xo_int      piValStatus;
        Object      pData;

        piType = new xo_int();
        piXoType = new xo_int();
        piValLen =  new xo_int(0);
        hCol = new xo_long();
        hSubQuery = new xo_long();
        piValStatus = new xo_int();


        pData = jdam.damex_describeVal(hVal, piType,
            piXoType,
            piValLen,
            hCol,
            hSubQuery, piValStatus);

        iType = piType.getVal();
        iXoType = piXoType.getVal();

        switch (iType)
        {

            case ip.SQL_VAL_DATA_CHAIN:
                pSqlBuffer.append("?");
                /*                ghValBlob = hVal; */
                break;
            case ip.SQL_VAL_NULL:
                pSqlBuffer.append("NULL"); break;
            case ip.SQL_VAL_QUERY: /* query */
                pSqlBuffer.append("( ");
                ip_format_query(hSubQuery.getVal(),pSqlBuffer);
                pSqlBuffer.append(" )");
                break;
            case ip.SQL_VAL_COL: /* value is the column value */
                ip_format_col(hquery, hCol.getVal(), pSqlBuffer); break;
            case ip.SQL_VAL_INTERVAL:
                break;
            case ip.SQL_VAL_LITERAL: /* value is a Xo Type literal */
                {
                String  strObject;
                Integer iObject;
                xo_tm   xoTime;
                Double  dObject;
                Float   fObject;
                Short   sObject;
                Boolean bObject;
                Byte    byObject;
                Long    lObject;

                switch (iXoType)
                {
                    case ip.XO_TYPE_CHAR: /* pVal is a char literal */
                    case ip.XO_TYPE_VARCHAR:
                    case ip.XO_TYPE_NUMERIC:
                    case ip.XO_TYPE_DECIMAL:
                        strObject = (String) pData;
                        ip_format_string_literal(strObject,pSqlBuffer);
                        break;
                    case ip.XO_TYPE_WCHAR: /* pVal is a wchar literal */
                    case ip.XO_TYPE_WVARCHAR:
                        strObject = (String) pData;
                        pSqlBuffer.append("N'").append(strObject).append("'");
                        break;
                    case ip.XO_TYPE_INTEGER:  /* pVal is a integer literal */
                        iObject = (Integer) pData;
                        pSqlBuffer.append(iObject.intValue());
                        break;
                    case ip.XO_TYPE_SMALLINT: /* pVal is small integer literal */
                        sObject = (Short) pData;
                        pSqlBuffer.append(sObject.shortValue());
                        break;
                    case ip.XO_TYPE_FLOAT: /* pVal is a double literal */
                    case ip.XO_TYPE_DOUBLE:
                        dObject = (Double) pData;
                        pSqlBuffer.append(dObject.doubleValue());
                        break;
                    case ip.XO_TYPE_REAL: /* pVal is a float literal */
                        fObject = (Float) pData;
                        pSqlBuffer.append(fObject.floatValue());
                        break;
                    case ip.XO_TYPE_DATE:
                        xoTime = (xo_tm)pData;
                        pSqlBuffer.append("{d '").append(xoTime.getVal(xo_tm.YEAR)).append("-").append(xoTime.getVal(xo_tm.MONTH)+1).append("-").append(xoTime.getVal(xo_tm.DAY_OF_MONTH)).append("'}");
                        break;
                    case ip.XO_TYPE_TIME:
                        xoTime = (xo_tm)pData;
                        pSqlBuffer.append("{t '").append(" ").append(xoTime.getVal(xo_tm.HOUR)).append(":").append(xoTime.getVal(xo_tm.MINUTE)).append(":").append(xoTime.getVal(xo_tm.SECOND)).append("'}");
                        break;
                    case ip.XO_TYPE_TIMESTAMP:
                        xoTime = (xo_tm)pData;
                        if (xoTime.getVal(xo_tm.FRACTION) > 0)
                        {
                            int     frac;

                            frac = (int) (xoTime.FRACTION * 0.000001);
                            pSqlBuffer.append("{ts '").append(xoTime.getVal(xo_tm.YEAR)).append("-").append(xoTime.getVal(xo_tm.MONTH)+1).append("-").append(xoTime.getVal(xo_tm.DAY_OF_MONTH))
                                .append(" ").append(xoTime.getVal(xo_tm.HOUR)).append(":").append(xoTime.getVal(xo_tm.MINUTE)).append(":").append(xoTime.getVal(xo_tm.SECOND))
                                .append(".").append(xoTime.getVal(xo_tm.FRACTION)).append("'}");
                        }
                        else
                        {
                            pSqlBuffer.append("{ts '").append(xoTime.getVal(xo_tm.YEAR)).append("-").append(xoTime.getVal(xo_tm.MONTH)+1).append("-").append(xoTime.getVal(xo_tm.DAY_OF_MONTH))
                                .append(" ").append(xoTime.getVal(xo_tm.HOUR)).append(":").append(xoTime.getVal(xo_tm.MINUTE)).append(":").append(xoTime.getVal(xo_tm.SECOND)).append("'}");
                        }

                        break;

                    case ip.XO_TYPE_BIT:
                        bObject = (Boolean)pData;
                        pSqlBuffer.append(bObject.booleanValue()?1:0);
                        break;

                    case ip.XO_TYPE_TINYINT:

                        byObject = (Byte)pData;
                        pSqlBuffer.append(byObject.byteValue());
                        break;

                    case ip.XO_TYPE_BIGINT:
                        lObject = (Long)pData;
                        pSqlBuffer.append(lObject.longValue());
                        break;

                    default:
                        pSqlBuffer.append("Invalid Xo Value Type:").append(iXoType);
                        break;
                }
            }
                break;
            default:
                pSqlBuffer.append("Invalid Value Type:").append(iType); break;
        }
        return ip.IP_SUCCESS;

    }

    public int     ip_format_table_list(long hquery, StringBuffer pSqlBuffer)
    {
        long              htable;
        int               iFirst = ip.TRUE;
        xo_int            piTableNum = new xo_int(0);
        StringBuffer      wsCatalog   = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        StringBuffer      wsSchema    = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        StringBuffer      wsTableName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
        int               iJoinType;
        xo_int            piJoinType = new xo_int(0);
        xo_long           phJoinExp = new xo_long(0);
        long        hTableSubQuery;

        htable = jdam.damex_getFirstTable(hquery);
        while (htable != 0)
        {
            jdam.damex_describeTable(htable, piTableNum, wsCatalog, wsSchema, wsTableName, null, null);

            /* check if table subquery */
            hTableSubQuery = jdam.damex_isTableSubQuery(htable);

            if(hTableSubQuery != 0)
            {
                if (iFirst == ip.FALSE)
                {
                    pSqlBuffer.append(", ");
                }
                pSqlBuffer.append("( ");
                ip_format_query(hTableSubQuery,pSqlBuffer);
                pSqlBuffer.append(" ) ");
                pSqlBuffer.append("T").append(piTableNum.getVal()).append("_Q").append(hquery);
                iFirst = ip.FALSE;
                htable = jdam.damex_getNextTable(hquery);
                continue;
            }
            phJoinExp.setVal(0);

            jdam.damex_describeTableJoinInfo(htable, piJoinType, phJoinExp);

            iJoinType = piJoinType.getVal();

            switch (iJoinType)
            {
                case ip.SQL_JOIN_LEFT_OUTER:
                    pSqlBuffer.append(" LEFT OUTER JOIN ");
                    break;
                case ip.SQL_JOIN_RIGHT_OUTER:
                    pSqlBuffer.append(" RIGHT OUTER JOIN ");
                    break;
                case ip.SQL_JOIN_FULL_OUTER:
                    pSqlBuffer.append(" FULL OUTER JOIN ");
                    break;
                case ip.SQL_JOIN_INNER:
                    pSqlBuffer.append(" INNER JOIN ");
                    break;
                case ip.SQL_JOIN_OLD_STYLE:
                    if (iFirst == ip.FALSE)
                        pSqlBuffer.append(", ");
                    break;
            }
            if(wsCatalog != null && wsCatalog.length() > 0)
                pSqlBuffer.append(wsCatalog).append(".");
            if(wsSchema != null && wsSchema.length() > 0)
                pSqlBuffer.append(wsSchema).append(".");

            pSqlBuffer.append(wsTableName).append(" T").append(piTableNum.getVal()).append("_Q").append(hquery);
            if (phJoinExp.getVal() != 0)
            {
                pSqlBuffer.append(" ON ");
                ip_format_logexp(hquery, phJoinExp.getVal(), pSqlBuffer);
            }

            iFirst = ip.FALSE;
            htable = jdam.damex_getNextTable(hquery);
        }

        pSqlBuffer.append(" ");
        return ip.IP_SUCCESS;
    }

/********************************************************************************************
    Method:         ip_format_logexp
    Description:    Logical expression handling
    Return:         IP_SUCCESS on success
                    IP_FAILURE on error
*********************************************************************************************/
    public int     ip_format_logexp(long hquery, long hLogExp, StringBuffer pSqlBuffer)
    {
        xo_int         iType; /* AND, OR , NOT or CONDITION */
        xo_long        hLeft, hRight;
        xo_long        hCond;

        iType = new xo_int(0);
        hLeft = new xo_long(0);
        hRight = new xo_long(0);
        hCond = new xo_long(0);

        jdam.damex_describeLogicExp(hLogExp,
            iType, /* AND, OR , NOT or CONDITION */
            hLeft,
            hRight,
            hCond);

        switch (iType.getVal())
        {
            case ip.SQL_EXP_COND:
                pSqlBuffer.append("( ");
                ip_format_cond(hquery, hCond.getVal(), pSqlBuffer);
                pSqlBuffer.append(" )");
                break;
            case ip.SQL_EXP_AND:
                pSqlBuffer.append("( ");
                ip_format_logexp(hquery, hLeft.getVal(), pSqlBuffer);
                pSqlBuffer.append(" AND ");
                ip_format_logexp(hquery, hRight.getVal(), pSqlBuffer);
                pSqlBuffer.append(" )");

                break;
            case ip.SQL_EXP_OR:
                pSqlBuffer.append("( ");
                ip_format_logexp(hquery, hLeft.getVal(), pSqlBuffer);
                pSqlBuffer.append(" OR ");
                ip_format_logexp(hquery, hRight.getVal(), pSqlBuffer);
                pSqlBuffer.append(" )");

                break;
            case ip.SQL_EXP_NOT:
                pSqlBuffer.append("( ");
                pSqlBuffer.append(" NOT ");
                ip_format_logexp(hquery, hLeft.getVal(), pSqlBuffer);
                pSqlBuffer.append(" )");
                break;
            default:            pSqlBuffer.append("Invalid Expression Type:").append(iType);
                break;

        }

        return ip.IP_SUCCESS;
    }

/********************************************************************************************
    Method:         ip_format_cond
    Description:    Condition, Operator handling
    Return:         IP_SUCCESS on success
                    IP_FAILURE on error
*********************************************************************************************/
    public int ip_format_cond(long hquery, long hCond, StringBuffer pSqlBuffer)
    {
        xo_int     piType;
        xo_long    hLeft, hRight, hExtra;
        int        iType;

        piType = new xo_int(0);
        hLeft = new xo_long(0);
        hRight = new xo_long(0);
        hExtra = new xo_long(0);

        jdam.damex_describeCond(hCond,
            piType, /* >, <, =, BETWEEN etc.*/
            hLeft,
            hRight,
            hExtra); /* used for BETWEEN */

        iType = piType.getVal();

        /* EXISTS and UNIQUE predicates */
        if ((iType & (ip.SQL_OP_EXISTS | ip.SQL_OP_UNIQUE)) != 0)
        {

            if ((iType & ip.SQL_OP_NOT) != 0) pSqlBuffer.append(" NOT ");
            if ((iType & ip.SQL_OP_EXISTS) != 0) pSqlBuffer.append(" EXISTS (");
            if ((iType & ip.SQL_OP_UNIQUE) != 0) pSqlBuffer.append(" UNIQUE (");

            ip_format_valexp(hquery, hLeft.getVal(), pSqlBuffer);
            pSqlBuffer.append(" )");
        }


        /* conditional predicates */
        if ((iType & ( ip.SQL_OP_SMALLER | ip.SQL_OP_GREATER |  ip.SQL_OP_EQUAL)) != 0)
        {
            ip_format_valexp(hquery, hLeft.getVal(), pSqlBuffer);

            if ((iType & ip.SQL_OP_NOT) != 0)
            {
                if ((iType & ip.SQL_OP_EQUAL) != 0)
                    pSqlBuffer.append(" <> ");
            }
            else
            {
                pSqlBuffer.append(" ");
                if ((iType & ip.SQL_OP_SMALLER) != 0) pSqlBuffer.append("<");
                if ((iType & ip.SQL_OP_GREATER) != 0) pSqlBuffer.append(">");
                if ((iType & ip.SQL_OP_EQUAL) != 0) pSqlBuffer.append("=");
                pSqlBuffer.append(" ");
            }

            if ((iType & (ip.SQL_OP_QUANTIFIER_ALL | ip.SQL_OP_QUANTIFIER_SOME | ip.SQL_OP_QUANTIFIER_ANY)) != 0)
            {
                if ((iType & ip.SQL_OP_QUANTIFIER_ALL) != 0)
                    pSqlBuffer.append(" ALL ( ");
                if ((iType & ip.SQL_OP_QUANTIFIER_SOME) != 0)
                    pSqlBuffer.append(" SOME ( ");
                if ((iType & ip.SQL_OP_QUANTIFIER_ANY) != 0)
                    pSqlBuffer.append(" ANY ( ");
            }

            ip_format_valexp(hquery, hRight.getVal(), pSqlBuffer);
        }
        /* like predicate */
        if ((iType & ip.SQL_OP_LIKE) != 0)
        {
            ip_format_valexp(hquery, hLeft.getVal(), pSqlBuffer);
            if ((iType & ip.SQL_OP_NOT) != 0)
                pSqlBuffer.append(" NOT ");
            pSqlBuffer.append(" LIKE ");
            ip_format_valexp(hquery, hRight.getVal(), pSqlBuffer);

            if (hExtra.getVal() != 0)
            {
                pSqlBuffer.append(" ESCAPE ");
                ip_format_valexp(hquery, hExtra.getVal(), pSqlBuffer);
            }

        }

        /* Is NULL predicate */
        if ((iType & ip.SQL_OP_ISNULL) != 0)
        {
            ip_format_valexp(hquery, hLeft.getVal(), pSqlBuffer);
            if ((iType & ip.SQL_OP_NOT) != 0)
                pSqlBuffer.append(" IS NOT NULL ");
            else
                pSqlBuffer.append(" IS NULL ");
        }

        /* IN predicate */
        if ((iType & ip.SQL_OP_IN) != 0)
        {
            ip_format_valexp(hquery, hLeft.getVal(), pSqlBuffer);
            if ((iType & ip.SQL_OP_NOT) != 0)
                pSqlBuffer.append(" NOT ");
            pSqlBuffer.append(" IN ");
            ip_format_valexp(hquery, hRight.getVal(), pSqlBuffer);
        }

        /* BETWEEN predicate */
        if ((iType & ip.SQL_OP_BETWEEN) != 0)
        {

            /* check if the between is a form of ( >= and < ) OR (> and <)
             OR (> and <=)
             */
            if (((iType & ip.SQL_OP_BETWEEN_OPEN_LEFT) != 0) || ((iType & ip.SQL_OP_BETWEEN_OPEN_RIGHT) != 0))
            {
                /* format it as two conditions */
                ip_format_valexp(hquery, hLeft.getVal(), pSqlBuffer);
                if ((iType & ip.SQL_OP_BETWEEN_OPEN_LEFT) != 0)
                    pSqlBuffer.append(" > ");
                else
                    pSqlBuffer.append(" >= ");
                ip_format_valexp(hquery, hRight.getVal(), pSqlBuffer);

                pSqlBuffer.append(" AND ");

                ip_format_valexp(hquery, hLeft.getVal(), pSqlBuffer);
                if ((iType & ip.SQL_OP_BETWEEN_OPEN_RIGHT) != 0)
                    pSqlBuffer.append(" < ");
                else
                    pSqlBuffer.append(" <= ");
                ip_format_valexp(hquery, hExtra.getVal(), pSqlBuffer);
            }
            else
            {
                /* standard BETWEEN pattern */
                ip_format_valexp(hquery, hLeft.getVal(), pSqlBuffer);

                if ((iType & ip.SQL_OP_NOT) != 0)
                    pSqlBuffer.append(" NOT ");
                pSqlBuffer.append(" BETWEEN ");

                ip_format_valexp(hquery, hRight.getVal(), pSqlBuffer);
                pSqlBuffer.append(" AND ");
                ip_format_valexp(hquery, hExtra.getVal(), pSqlBuffer);
            }

        }

        return ip.IP_SUCCESS;
    }

        public int     ip_format_group_list(long hquery, long hValExpList, StringBuffer pSqlBuffer)
        {
            ip_format_valexp_list(hquery, hValExpList, pSqlBuffer);
            return ip.IP_SUCCESS;
        }

        public int     ip_format_order_list(long   hquery, long hValExpList, StringBuffer pSqlBuffer)
        {
            long            hValExp;
            xo_int          piResultColNum = new xo_int(0);
            xo_int          piSortOrder = new xo_int(0);
            int             iFirst = ip.TRUE;

            hValExp = jdam.damex_getFirstValExp(hValExpList);
            while (hValExp != 0)
            {

                if (iFirst == ip.FALSE)
                    pSqlBuffer.append(", ");
                else
                    iFirst = ip.FALSE;

                jdam.damex_describeOrderByExp(hValExp, piResultColNum, piSortOrder);

                if (piResultColNum.getVal() != ip.DAM_NOT_SET) /* use the result column number */
                    pSqlBuffer.append(piResultColNum.getVal()+1);
                else
                    ip_format_valexp(hquery, hValExp, pSqlBuffer);

                if (piSortOrder.getVal() == ip.SQL_ORDER_ASC)
                    pSqlBuffer.append(" ASC");
                else if (piSortOrder.getVal() == ip.SQL_ORDER_DESC)
                    pSqlBuffer.append(" DESC");

                hValExp = jdam.damex_getNextValExp(hValExpList);
            }

            pSqlBuffer.append(" ");
            return ip.IP_SUCCESS;
        }

        public int ip_format_string_literal(String pString,StringBuffer pSqlBuffer)
        {
            pSqlBuffer.append("'");
            String resultStr = pString.replaceAll("'","''");
            pSqlBuffer.append(resultStr);
            pSqlBuffer.append("'");
            return ip.IP_SUCCESS;
        }

        public int     ip_format_table_query(long hquery, StringBuffer pSqlBuffer)
        {
            long         hVal;
            int          iFirst = ip.TRUE;

            hVal = jdam.damex_getFirstTableQueryVal(hquery);
            while (hVal != 0)
            {
                if (iFirst == ip.FALSE)
                    pSqlBuffer.append(", ");
                else
                    iFirst = ip.FALSE;

                ip_format_val(hquery, hVal, pSqlBuffer);
                hVal = jdam.damex_getNextTableQueryVal(hquery);
            }
            return ip.IP_SUCCESS;
        }
        /************************************************************************
        Function:       ip_isOriginalSelectListCompatible()
        Description:
        Return:
        ************************************************************************/
        public boolean             ip_isOriginalSelectListCompatible(long hSelectValExpList,
                                                long hGroupValExpList,
                                                long hOrderValExpList)
        {

            /* Check if there are Set functions in the Query */
            {
                long    hValExp;
                xo_int  piType = new xo_int(0);
                xo_int  piFuncType = new xo_int(0);
                xo_long phLeftValExp = new xo_long(0);
                xo_long phRightValExp = new xo_long(0);
                xo_long phVal = new xo_long(0);
                xo_long phScalarValExp = new xo_long(0);
                xo_long phCaseValExp = new xo_long(0);
				xo_long phRankValExp = new xo_long(0);

                hValExp = jdam.damex_getFirstValExp(hSelectValExpList);
                while (hValExp != 0)
                {
                    jdam.damex_describeValExp(hValExp, piType, piFuncType, phLeftValExp, phRightValExp,phVal,phScalarValExp, phCaseValExp, phRankValExp);

                    if (piFuncType.getVal() != 0) return false;
                    hValExp = jdam.damex_getNextValExp(hSelectValExpList);
                }
            }

            /* Check for GROUP BY */
            if (hGroupValExpList != 0) return false;

            /* check if ORDER BY does not refer to result columns */
            if (hOrderValExpList != 0)
            {
                long        hValExp;
                xo_int      piResultColNum = new xo_int(0);
                xo_int      piSortOrder = new xo_int(0);

                hValExp = jdam.damex_getFirstValExp(hOrderValExpList);
                while (hValExp != 0)
                {

                    jdam.damex_describeOrderByExp(hValExp, piResultColNum, piSortOrder);
                    if (piResultColNum.getVal() == ip.DAM_NOT_SET) return false;

                    hValExp = jdam.damex_getNextValExp(hOrderValExpList);
                }
            }

            return true;
        }

    public String  map_type_to_name(int iType)
    {
        switch (iType)
        {
            case ip.XO_TYPE_CHAR:return "CHAR";
            case ip.XO_TYPE_VARCHAR:return "VARCHAR";
            case ip.XO_TYPE_LONGVARCHAR: return "LONGVARCHAR";
            case ip.XO_TYPE_NUMERIC:return "NUMERIC";
            case ip.XO_TYPE_DECIMAL:return "DECIMAL";
            case ip.XO_TYPE_INTEGER:return "INTEGER";
            case ip.XO_TYPE_SMALLINT:return "SMALLINT";
            case ip.XO_TYPE_DOUBLE:return "DOUBLE";
            case ip.XO_TYPE_BIGINT: return "BIGINT";
            case ip.XO_TYPE_FLOAT: return "FLOAT";
            case ip.XO_TYPE_REAL: return "REAL";
            case ip.XO_TYPE_WCHAR: return "WCAHR";
            case ip.XO_TYPE_WVARCHAR: return "WVARCHAR";
            case ip.XO_TYPE_WLONGVARCHAR: return "WLONGVARCHAR";
            case ip.XO_TYPE_DATE:  return "DATE";
            case 91:  return "DATE";  /* XO_TYPE_DATE_TYPE */
            case ip.XO_TYPE_TIME: return "TIME";
            case 92: return "TIME";  /* XO_TYPE_TIME_TYPE */
            case ip.XO_TYPE_TIMESTAMP: return "TIMESTAMP";
            case 93: return "TIMESTAMP";    /* XO_TYPE_TIMESTAMP_TYPE */
            case ip.XO_TYPE_BINARY: return "BINARY";
            case ip.XO_TYPE_VARBINARY: return "VARBINARY";
            case ip.XO_TYPE_LONGVARBINARY: return "LONGVARBINARY";
            case ip.XO_TYPE_TINYINT: return "TINYINT";
            case ip.XO_TYPE_BIT: return "BIT";
        }
        return "ERROR";
    }

} /* Class format */
