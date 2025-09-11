/* damip.java
 *
 * Copyright (c) 1995-2012 Progress Software Corporation. All Rights Reserved.
 *
 *
 * Description:     Template DAM IP
 *                  - is implemented in "JAVA"
 *                  - supports SELECT operations
 *					- Support Dynamic Schema
 */

package oajava.template;

import oajava.sql.*;
/* define the class template to implement the sample IP */
public class damip implements oajava.sql.ip
{
    private long m_tmHandle = 0;
    final static String OA_CATALOG_NAME   = "SCHEMA";        /* SCHEMA */
    final static String OA_USER_NAME      = "OAUSER";        /* OAUSER */

    /* Support array */
    private final int[]   ip_support_array =
                    {
                        0,
                        1, /* IP_SUPPORT_SELECT */
                        0, /* IP_SUPPORT_INSERT */
                        0, /* IP_SUPPORT_UPDATE */
                        0, /* IP_SUPPORT_DELETE */
                        1, /* IP_SUPPORT_SCHEMA - IP supports Schema Functions */
                        0, /* IP_SUPPORT_PRIVILEGES  */
                        0, /* IP_SUPPORT_OP_EQUAL */
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
                        0,  /* IP_SUPPORT_QUERY_MODE_SELECTION */
                        0,  /* IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE */
                        1,  /* IP_SUPPORT_UNICODE_INFO */
                        0,  /* Reserved for future use */
                        0,  /* Reserved for future use */
                        0,  /* Reserved for future use */
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
	}

    public String ipGetInfo(int iInfoType)
    {
		String str = null;

        jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipGetInfo called\n");

        return str;
   }

    public int ipSetInfo(int iInfoType,String InfoVal)
    {
        jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipSetInfo called\n");
        return IP_SUCCESS;
    }

    public int ipGetSupport(int iSupportType)
    {
        jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipGetSupport called\n");
        return(ip_support_array[iSupportType]);
    }

   /*ipConnect is called immediately after an instance of this object is created. You should
    *perform any tasks related to connecting to your data source */
    public int ipConnect(long tmHandle,long dam_hdbc,String sDataSourceName, String sUserName, String sPassword,
						String sCurrentCatalog, String sIPProperties, String sIPCustomProperties)
    {
            /* Save the trace handle */
            m_tmHandle = tmHandle;
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipConnect called\n");

            /* Code to connect to your data source source. */
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

    public int ipExecute(long dam_hstmt, int iStmtType, long hSearchCol,xo_long piNumResRows)
    {
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipExecute called\n");
            return IP_SUCCESS;
    }

    /* this example uses static schema and only SELECT command supported, so following functions are not called */
    public int ipSchema(long dam_hdbc,long pMemTree,int iType, long pList, Object pSearchObj)
    {
			jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipSchema called\n");
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
				}
				break;
			case DAMOBJ_TYPE_COLUMN:
				{
					schemaobj_column pColSearchObj = (schemaobj_column) pSearchObj;

					if (pColSearchObj != null)
					{
						jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for column <"+pColSearchObj.getColumnName()+"> of table:<"+pColSearchObj.getTableQualifier()+"."+pColSearchObj.getTableOwner()+"."+pColSearchObj.getTableName()+"> is being requested\n");
					}
					else
					{
						jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all columns of all tables is being requested\n");
					}
				}
				break;

			case DAMOBJ_TYPE_STAT:
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

    public int        ipDDL(long dam_hstmt, int iStmtType, xo_long piNumResRows)
    {
			jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipDDL called\n");
            return IP_FAILURE;
    }

    public int        ipProcedure(long dam_hstmt, int iType, xo_long piNumResRows)
    {
			jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipProcedure called\n");
            return IP_FAILURE;
    }

    public int        ipDCL(long dam_hstmt, int iStmtType, xo_long piNumResRows)
    {
			jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipProcedure called\n");
            return IP_FAILURE;
    }

    public int        ipPrivilege(int iStmtType,String pcUserName,String pcCatalog,String pcSchema,String pcObjName)
    {
			jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipPrivilege called\n");
			return IP_FAILURE;
    }

    public int        ipNative(long dam_hstmt, int iCommandOption, String sCommand, xo_long piNumResRows)
    {
			jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipNative called\n");
            return IP_FAILURE;
    }

    public int        ipSchemaEx(long dam_hstmt, long pMemTree, int iType, long pList,Object pSearchObj)
	{
			jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipSchemaEx called\n");
            return IP_FAILURE;
    }

    public int        ipProcedureDynamic(long dam_hstmt, int iType, xo_long piNumResRows)
    {
			jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipProcedureDynamic called\n");
            return IP_FAILURE;
    }
}
