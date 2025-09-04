![](media/image1.jpeg){width="8.5in" height="4.867227690288714in"}

> **DataDirect OpenAccess SDK Help**
>
> **Generated on: 2 September 2025**

1

> **Programmer\'s Reference for Java**
>
> The *Programmer\'s Reference for Java* provides information for
> implementing a custom data source interface provider using Java. It
> covers topics such as how to set up your development environment for
> implementing an IP, required and optional API functions, and the core
> functionality exposed by the JDAM class of the OpenAccess SDK SQL
> engine. It also provides instructions for using IP samples included in
> the product package and for embedding the OpenAccess SDK Server in
> Java applications.

# Setting up your development environment for an IP

> This section explains how to set up the development environment to
> implement your IP in Java. Follow this procedure to set up the source
> code, schema, and the OpenAccess SDK service for your IP.
>
> To implement a new IP, you must set up the source files and build
> project for your IP to compile and create a Java class file. The IP
> module is loaded by the OpenAccess SDK SQL engine, which is
> dynamically loaded by the OpenAccess SDK Server (client/server
> version) or the OpenAccess SDK Local Server (local version). The
> OpenAccess SDK includes the jar files and templates needed to build
> your IP.
>
> The architecture of an OpenAccess SDK client/server configuration is
> shown in the following figure. The OpenAccess clients implement the
> ODBC, JDBC, ADO, or .NET API and contain the client/server
> communications layer for interacting with the OpenAccess SDK Server.
>
> **Note**: The ADO and ADO.NET clients are currently available only in
> OpenAccess SDK 8.1. However, they are compatible with the OpenAccess
> SDK 9.0 server.

#### Client/server configuration

#### 

> ![](media/image2.png){width="4.708333333333333in"
> height="2.9791666666666665in"}
>
> Setting up to develop a new IP is a three-step process:

1.  Set up the source and schema files for your IP to allow the IP
    module to be built. See [Setting up the
    IP](#setting-up-the-ip-source-and-schema) [source and
    schema](#setting-up-the-ip-source-and-schema) for more information.

2.  Configure an OpenAccess SDK service for your data source using the
    OpenAccess SDK Manager. See [Setting up an OpenAccess SDK Service
    and data
    source](#setting-up-an-openaccess-sdk-service-and-data-source) for
    more information.

3.  Perform a test connection to your data source from an OpenAccess SDK
    client. See [Connecting to your](#connecting-to-your-data-source)
    [data source](#connecting-to-your-data-source) for more information.

## Requirements for compiling an IP

> The IP must be built using a Java compiler version compatible with the
> JVM that will be deployed at run-time. Refer to the *OpenAccess SDK
> Installation Guide* for the supported compilers and JVM.

## Setting up the IP source and schema

> You can develop an IP for your data source by starting with the
> template IP or an example installed with OpenAccess SDK.
>
> The steps in this section assume you are starting from the template IP
> installed at *install_dir*/ip/ oajava/template. The template IP
> contains stubs for each IP function that you must implement.
>
> If you start from an example installed with OpenAccess SDK, the steps
> are the same as if you start from the template IP, except that you
> replace references to *template* with *example*. For code samples
> downloaded from the Progress DataDirect code library, follow the
> instructions provided with the sample.
>
> The template IP can be found in the *install_dir*/ip/oajava/template
> directory and contains the following file:

- damip.java - implementation of the IP API

> OpenAccess SDK provides the following schema directories and files:

- The *install_dir*/ip/schema/template_static directory contains the
  minimal OA_TABLES, OA_COLUMNS, OA_STATISTICS, and OA_FKEYS
  configuration required to use the OpenAccess SDK SQL engine schema
  manager.

- The *install_dir*/ip/schema/template_dynamic directory contains the
  minimal OA_TABLES and OA_COLUMNS required to support views using a
  dynamic schema and are used when the IP is implementing the schema
  management.

#### To set up the source and schema for a new IP:

1.  Copy the contents of the *install_dir*/ip/oajava/template directory
    and all of its subdirectories to the directory
    *install_dir*/ip/oajava/yourip and rename the template files. For
    example, on UNIX, perform the following commands, replacing *yourip*
    with the name you have chosen for your IP:

    a.  cd *install_dir*/ip/oajava

    b.  mkdir yourip

    c.  cp -r template/\* yourip

    d.  cd yourip

    e.  mv damip.java yourip.java

2.  Edit the IP files you placed in the *install_dir*/ip/oajava/yourip
    directory to modify them for your IP-specific file names.

    a.  Edit yourip.java and replace package oajava.template with your
        > IP\'s filename, for example:

> package oajava.yourip

b.  Change the name of the class from damip to yourip.

c.  Change the name of the class constructor from damip() to \<yourip().

<!-- -->

3.  Compile the IP files:

    a.  Change to directory *install_dir*/ip/oajava/yourip.

    b.  Set the CLASSPATH to include *install_dir*/ip/oajava/oasql.jar
        > and any additional files your IP needs to reference.

    c.  javac yourip.java

> The name of the IP class that implements the oajava.sql.ip interface
> is specified in the DataSourceIPClass data source attribute. The
> OpenAccess SDK SQL engine creates an instance of this class for each
> connection. You must configure the ServiceJVMClassPath service
> attribute with all classes your IP requires, the path
> *install_dir*/ip, and oasql.jar.

4.  For schema support, perform one of the following actions:

- Dynamic schema: To implement a dynamic schema, where the IP implements
  the SCHEMA function, and to take advantage of SQL views, copy the
  contents of the *install_dir*/ip/schema/ template_dynamic directory to
  the *install_dir*/ip/schema/yourip directory.

- Static schema: To use the static schema feature, where the OpenAccess
  SDK SQL engine manages the schema, set up the required schema storage
  files by copying the contents of the *install_dir*/ip/
  schema/template_static directory to the *install_dir*/ip/schema/yourip
  directory.

> The schema directory you create is used as the DataSourceIPSchemaPath
> when configuring the data source for the OpenAccess SDK service using
> this IP.
>
> On UNIX, perform the following commands to set up a static schema:

- cd ip/schema

- mkdir yourip

- cp -r template_static/\* yourip

> The development environment and schema folder for your IP are now set
> up. Next, set up the OpenAccess SDK service to access your data
> source. Refer to [Debugging an OpenAccess SDK Interface
> Provider](https://documentation.progress.com/output/DataDirect/collateral/debuggingip.pdf)
> for details on how to debug an IP on Linux, UNIX, and Windows.

## Setting up an OpenAccess SDK Service and data source

> A client connects to your data source by connecting to an OpenAccess
> SDK Service that has been configured to access your data source
> through the IP code you are developing. The OpenAccess SDK
> installation sets up a service OpenAccessSDK900_Java and installs a
> template OpenAccess SDK 9.0 Service for Java. It is recommended that
> you use the existing service OpenAccessSDK900_Java and create a data
> source entry under it instead of trying to create a new service.
>
> You must configure the following Service and Data Source settings to
> load your IP code and to make it available when a connection is
> received by the OpenAccess SDK Server or the OpenAccess Local Server.
> Refer to the *OpenAccess SDK Administrator\'s Guide* for a detailed
> explanation of all the service and data source attributes that are
> used to configure an IP for the OpenAccess SDK SQL engine.

- DataSourceIPType - DAMIP.

- DataSourceIPClass - name of the IP class that is implementing the
  oajava.sql.ip interface (for example,
  *install_dr*\ip\oajava\example1\damip). The IP class must reside in
  the *install_dr*\ip\\ oajava\\sampleIP\> folder.

- DataSourceIPSchemaPath - full path of the directory containing the
  schema for your IP (for example C:\Program
  Files\DataDirect\oaodbclocal90\ip\schema\yourip). The OpenAccess SDK
  SQL engine stores the schema for the IP in the files contained in this
  directory.

- DataSourceLogonMethod - if you want your IP to authenticate based on
  user name and password passed in from the client, then you set this
  data source attribute to DBMSLogon(*UID*,*PWD*); otherwise, leave it
  set to Anonymous.

- ServiceJVMOptions - include options that need to be passed to the JVM.
  For Local configuration, you must include -Xms4M -Xmx8M.

> The OpenAccess SDK launches a Java Virtual Machine when it is started
> by loading the JVM located at ServiceIPJVMLocation. The options passed
> into the JVM are controlled by setting the ServiceJVMOptions.
>
> The JVM will use the value of ServiceJVMClassPath as its CLASSPATH.
> After a successful installation, this setting is configured to include
> Java classes of the OpenAccess SDK and the directory where you have
> installed the OpenAccess SDK. If your Java classes require additional
> paths then you must add them in this ServiceJVMClassPath setting. If
> this setting is not set, the JVM uses the CLASSPATH environment
> variable set in ServiceEnvironmentVariable. Add any additional JAR
> files and paths that are required by your IP.
>
> Refer to the *OpenAccess SDK Administrator\'s Guide* for information
> about how to configure a new data source or to set up a new OpenAccess
> SDK service for your IP based on the provided Java template.
> Initially, you should use the existing OpenAccessSDK900_Java service
> and add a data source for configuring your IP. Assuming you call this
> data source *yourip*, the OpenAccess SDK Client references the
> *yourip* data source entry within the OpenAccessSDK900_Java service.
>
> **Note:** You must stop and restart the service for configuration
> changes to take effect and each time the IP class is recompiled.

## Connecting to your data source

> Once you have configured an OpenAccess SDK service with a data source
> entry for your IP, OpenAccess SDK clients can connect to it, select
> the data source, and issue SQL commands. To connect:

- In a client/server configuration, specify the host and port number
  that the OpenAccess SDK service is running on and the data source
  name.

- In a local configuration, specify the service name and the data source
  name.

> The Interactive SQL tools can be used to execute SQL commands for
> testing and for setting up the schema. For detailed information about
> testing using the OpenAccess SDK Clients, refer to the *OpenAccess SDK
> Installation Guide*.

# Interface Provider class

> This section describes the required and optional functions in the API
> of the OpenAccess SDK Interface Provider (IP) Software Development
> Kit, that you, as a developer, are responsible for implementing to
> develop an IP.

## Overview: Interface Provider class

> The following table lists all of the functions and associated methods
> that the OpenAccess SDK SQL engine can call to process queries. In
> addition to these functions, the IP must implement any functions it
> has registered for handling user-defined scalar functions and stored
> procedures. The oajava.sql.ip class shipped with the product defines
> each of these methods.

#### Functions Supported by an IP

> **Function Method Name Description**
>
> CONNECT
>
> Required
>
> ipConnect Called when a client needs to establish a connection with a
> data source serviced by the IP. Authentication information such as the
> user name and password are passed in.
>
> The default value is DAM_SUCCESS. If no return value is specified, the
> OpenAccess SDK SQL engine assumes that the return value is
> DAM_SUCCESS.
>
> The IP does not generate a DAM_FAILURE unless specified.
>
> DCL
>
> Optional
>
> ipDCL Called with GRANT and other DCL commands to configure
> privileges.
>
> DDL
>
> Optional
>
> ipDDL Called with CREATE TABLE, DROP TABLE, CREATE INDEX, or DROP
>
> INDEX code to perform the requested operation (required to support DDL
> operations).
>
> DISCONNECT
>
> Required
>
> ipDisconnect Closes the connection. The IP should close files or other
> connections established on behalf of this connection.
>
> DYNAMIC_RESULTS
>
> Optional
>
> ipProcedureDynamic Called to invoke a stored procedure that returns
> one or more result sets that can be defined at runtime.
>
> END TRANSACTION
>
> Optional
>
> ipEndTransaction Called with COMMIT or ROLLBACK or PREPARE_TO_COMMIT
> code.
>
> EXECUTE
>
> Required
>
> ipExecute Called to select, insert, update, and delete rows.
>
> GETDSINFO
>
> Optional
>
> ipGetDSInfo Called to obtain information about the data source such as
> the SQL capabilities, limits on object names, and other information
> that is needed.
>
> GETINFO
>
> Required
>
> ipGetInfo Called to get the IP version number and other information.
>
> GETLONGDATA
>
> Optional
>
> ipGetLongData Called to stream data from a particular column.
>
> GETSUPPORT
>
> Required
>
> ipGetSupport Called to query the IP for the types of operators it
> supports and the mode of operation.
>
> GETTYPESINFO
>
> Optional
>
> ipGetTypesInfo Called to get information about data types.
>
> NATIVE
>
> Optional
>
> ipNative Called to execute a command that the OpenAccess SDK SQL
> engine did not recognize as a valid SQL command.
>
> PRIVILEGE
>
> Optional
>
> ipPrivilege Called to verify privileges for the specified user,
> object, or operation.
>
> PROCEDURE
>
> Optional
>
> ipProcedure Called to invoke a stored procedure (only required to
> support stored procedures with pre-defined result sets).
>
> SCHEMA
>
> Optional
>
> ipSchema Called to retrieve the schema information of your database
> data source (only required to handle schema management).
>
> SCHEMAEX
>
> Optional
>
> ipSchemaEx Called to retrieve schema information for stored procedures
> that define a result set at runtime.
>
> SETINFO
>
> Required
>
> ipSetInfo Called to pass connection and statement level settings
> modified by the client to the IP.
>
> START TRANSACTION
>
> Optional
>
> ipStartTransaction Called to initiate a new transaction.
>
> The IP uses this entry point to perform transaction management for
> each connection.
>
> The Java class implementing the IP for the OpenAccess SDK SQL engine
> must implement the interface oajava.sql.ip. An instance of this class
> is created for each connection and is used to support requests on that
> connection.
>
> public interface ip
>
> {
>
> public String ipGetInfo(int iInfoType);
>
> public int ipSetInfo(int iInfoType, String InfoVal); public int
> ipGetSupport(int iSupportType);
>
> public int ipConnect(long tmHandle, long dam_hdbc,
>
> String sDataSourceName, String sUserName, String
>
> sPassword,
>
> String sCurrentCatalog, String sIPProperties, String
> sIPCustomProperties);
>
> public int ipDisconnect(long dam_hdbc); public int
> ipStartTransaction(long dam_hdbc);
>
> public int ipEndTransaction(long dam_hdbc, int iType);
>
> public int ipExecute(long hstmt, int iStmtType,long hSearchCol,
> xo_long piNumResRows);
>
> public int ipSchema(long dam_hdbc, long pMemTree, int iType, long
> pList,
>
> Object pSearchObj);
>
> public int ipDDL(long hstmt, int iType,xo_long piNumResRows);
>
> public int ipProcedure(long hstmt, int iType, xo_long piNumResRows);
> public int ipDCL(long hstmt, int iType,xo_long piNumResRows);
>
> public int ipPrivilege(int iStmtType, String pcUserName,
>
> String pcCatalog,String pcSchema,String pcObjName); public int
> ipNative(long hstmt, int iCmdType, String szCmd,
>
> xo_long piNumResRows);
>
> public int ipSchemaEx(long dam_hstmt, long pMemTree, int iType, long
> pList,Object pSearchObj);
>
> public int ipProcedureDynamic(long hstmt, int iType, xo_long
> piNumResRows);
>
> }

## IP methods reference

> To execute in multithreading mode, the IP must be implemented to
> support concurrent access to the IP class. The OpenAccess SDK SQL
> engine creates an instance of the Java IP class and uses that instance
> for all operations on that connection. If the IP class uses global
> classes or class level variables, it must provide sufficient
> safeguards.

## ipGetDsInfo

> This method is called to obtain information about the data source such
> as the SQL capabilities, limits on object names, and other
> information.
>
> oa_ds_info\[\] ipGetDsInfo()

#### Parameters for sqlipGetDsInfo

> **Parameter Type Description RETURN**
>
> oa_ds_info\[ \] OADS_SUCCESSOADS_ERROR - error allocating a connection
> handle

## Creating an oa_ds_info Object

> An object of oa_ds_info\[\] can be created using the *new* keyword.
> For example, oa_ds_info\[\] *xxxx* = new oa_ds_info\[n\].
>
> where:
>
> \'*xxxx*\' is the variable name and \'*n*\' is an integer value.
>
> The following code-snippet illustrates how to create an oa_ds_info
> object.
>
> /\* Overloaded Constructor
>
> oa_ds_info(String infoName, int infoNum, int infoInt, long
> infoBitmask, String infoText, String remarks) \*/
>
> oa_ds_info\[\] dsInfo = new oa_ds_info\[121\];
>
> dsInfo\[0\] = new oa_ds_info(\"SQL_ACTIVE_STATEMENTS\", 1, 0,
> DAMOBJ_NOTSET, \"\", \"The maximum number of statements supported.\");
>
> ...
>
> dsInfo\[121\] = new oa_ds_info(\"SQL_COLLATION_SEQ\", 10004,
> DAMOBJ_NOTSET, DAMOBJ_NOTSET, \"ISO 8859-1\", \"The name of the
> collation sequence for the default character set (for example, \'ISO
> 8859-1\' or EBCDIC). \");
>
> The following table contains different parameter values of the
> overloaded oa_ds_info constructor that can used to customize the data
> source information.

#### Parameter values for the overloaded constructor of oa_ds_info

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 15%" />
<col style="width: 19%" />
<col style="width: 22%" />
<col style="width: 22%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>infoName1</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>infoNum2</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>infoInt3</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>infoBitmask4</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>infoText5</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>SQL_ACTIVE_STAT EMENTS</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_ROW_UPDATE S</p>
</blockquote></td>
<td><blockquote>
<p>11</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>DAMOBJ_ NOTSET</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_SEARCH_PATT ERN_ ESCAPE</p>
</blockquote></td>
<td><blockquote>
<p>14</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"\\"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_ODBC_SQL_ CONFORMANCE</p>
</blockquote></td>
<td><blockquote>
<p>15</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_DBMS_NAME</p>
</blockquote></td>
<td><blockquote>
<p>17</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"OpenAccess"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_DBMS_VER</p>
</blockquote></td>
<td><blockquote>
<p>18</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"08.10"</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_ACCESSIBLE_ TABLES</p>
</blockquote></td>
<td><blockquote>
<p>19</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"Y"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_ACCESSIBLE_ PROCEDURES</p>
</blockquote></td>
<td><blockquote>
<p>20</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"Y"</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_PROCEDURES</p>
</blockquote></td>
<td><blockquote>
<p>21</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"Y"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONCAT_NUL L_ BEHAVIOR</p>
</blockquote></td>
<td><blockquote>
<p>22</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>""</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_DATA_SOURC E_READ_ONLY</p>
</blockquote></td>
<td><blockquote>
<p>25</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"N"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_EXPRESSION S_IN_ORDERBY</p>
</blockquote></td>
<td><blockquote>
<p>27</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"Y"</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_IDENTIFIER_C ASE</p>
</blockquote></td>
<td><blockquote>
<p>28</p>
</blockquote></td>
<td><blockquote>
<p>4</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_IDENTIFIER_Q UOTE_ CHAR</p>
</blockquote></td>
<td><blockquote>
<p>29</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"\""</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_MAX_COLUMN</p>
<p>_NAME_LEN</p>
</blockquote></td>
<td><blockquote>
<p>30</p>
</blockquote></td>
<td><blockquote>
<p>128</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 13%" />
<col style="width: 22%" />
<col style="width: 24%" />
<col style="width: 17%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>SQL_MAX_OWNER_ NAME_ LEN</p>
</blockquote></th>
<th><blockquote>
<p>32</p>
</blockquote></th>
<th><blockquote>
<p>128</p>
</blockquote></th>
<th><blockquote>
<p>NA</p>
</blockquote></th>
<th><blockquote>
<p>NA</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>SQL_MAX_PROCED URE_ NAME_LEN</p>
</blockquote></td>
<td><blockquote>
<p>33</p>
</blockquote></td>
<td><blockquote>
<p>128</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_MAX_QUALIFI ER_ NAME_LEN</p>
</blockquote></td>
<td><blockquote>
<p>34</p>
</blockquote></td>
<td><blockquote>
<p>128</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_MAX_TABLE_N AME_ LEN</p>
</blockquote></td>
<td><blockquote>
<p>35</p>
</blockquote></td>
<td><blockquote>
<p>128</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_MULT_RESULT</p>
<p>_SETS</p>
</blockquote></td>
<td><blockquote>
<p>36</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"Y"</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_MULTIPLE_AC TIVE_ TXN</p>
</blockquote></td>
<td><blockquote>
<p>37</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"Y"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_OUTER_JOINS</p>
</blockquote></td>
<td><blockquote>
<p>38</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"Y"</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_PROCEDURE_ TERM</p>
</blockquote></td>
<td><blockquote>
<p>40</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"procedure"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_QUALIFIER_N AME_ SEPARATOR</p>
</blockquote></td>
<td><blockquote>
<p>41</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"."</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_TABLE_TERM</p>
</blockquote></td>
<td><blockquote>
<p>45</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"table"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_TXN_CAPABLE</p>
</blockquote></td>
<td><blockquote>
<p>46</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_FU NCTIONS</p>
</blockquote></td>
<td><blockquote>
<p>48</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_NUMERIC_FU NCTIONS</p>
</blockquote></td>
<td><blockquote>
<p>49</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>16777215</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_STRING_FUNC TIONS</p>
</blockquote></td>
<td><blockquote>
<p>50</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>16547839</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_SYSTEM_FUN CTIONS</p>
</blockquote></td>
<td><blockquote>
<p>51</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_TIMEDATE_FU NCTIONS</p>
</blockquote></td>
<td><blockquote>
<p>52</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>630783</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONVERT_BIG INT</p>
</blockquote></td>
<td><blockquote>
<p>53</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_BIN ARY</p>
</blockquote></td>
<td><blockquote>
<p>54</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONVERT_BIT</p>
</blockquote></td>
<td><blockquote>
<p>55</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_CH AR</p>
</blockquote></td>
<td><blockquote>
<p>56</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONVERT_DA TE</p>
</blockquote></td>
<td><blockquote>
<p>57</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 14%" />
<col style="width: 24%" />
<col style="width: 23%" />
<col style="width: 14%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>SQL_CONVERT_DE CIMAL</p>
</blockquote></th>
<th><blockquote>
<p>58</p>
</blockquote></th>
<th><blockquote>
<p>NA</p>
</blockquote></th>
<th><blockquote>
<p>0</p>
</blockquote></th>
<th>NA</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_DO UBLE</p>
</blockquote></td>
<td><blockquote>
<p>59</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONVERT_FL OAT</p>
</blockquote></td>
<td><blockquote>
<p>60</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_INT EGER</p>
</blockquote></td>
<td><blockquote>
<p>61</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONVERT_ LONGVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>62</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_NU MERIC</p>
</blockquote></td>
<td><blockquote>
<p>63</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONVERT_RE AL</p>
</blockquote></td>
<td><blockquote>
<p>64</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_SM ALLINT</p>
</blockquote></td>
<td><blockquote>
<p>65</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONVERT_TIM E</p>
</blockquote></td>
<td><blockquote>
<p>66</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_TIM ESTAMP</p>
</blockquote></td>
<td><blockquote>
<p>67</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONVERT_TIN YINT</p>
</blockquote></td>
<td><blockquote>
<p>68</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_VA RBINARY</p>
</blockquote></td>
<td><blockquote>
<p>69</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CONVERT_VA RCHAR</p>
</blockquote></td>
<td><blockquote>
<p>70</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CONVERT_ LONGVARBINARY</p>
</blockquote></td>
<td><blockquote>
<p>71</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_ODBC_SQL_O PT_IEF</p>
</blockquote></td>
<td><blockquote>
<p>73</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td>"N"</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_TXN_ISOLATIO N_ OPTION</p>
</blockquote></td>
<td><blockquote>
<p>72</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>7</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CORRELATION</p>
<p>_NAME</p>
</blockquote></td>
<td><blockquote>
<p>74</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_NON_NULLAB LE_ COLUMNS</p>
</blockquote></td>
<td><blockquote>
<p>75</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_GETDATA_ EXTENSIONS</p>
</blockquote></td>
<td><blockquote>
<p>81</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_NULL_COLLAT</p>
</blockquote></td>
<td><blockquote>
<p>85</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td>NA</td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 12%" />
<col style="width: 20%" />
<col style="width: 21%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>ION</p>
<p>SQL_ALTER_TABLE</p>
</blockquote></th>
<th><blockquote>
<p>86</p>
</blockquote></th>
<th><blockquote>
<p>NA</p>
</blockquote></th>
<th><blockquote>
<p>37867</p>
</blockquote></th>
<th><blockquote>
<p>NA</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>SQL_COLUMN_ALIA S</p>
</blockquote></td>
<td><blockquote>
<p>87</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"Y"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_GROUP_BY</p>
</blockquote></td>
<td><blockquote>
<p>88</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_KEYWORDS</p>
</blockquote></td>
<td><blockquote>
<p>89</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"HINT, IDENTIFIED"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_ORDER_BY_C OLUMNS_IN_ SELECT</p>
</blockquote></td>
<td><blockquote>
<p>90</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"N"</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_OWNER_USA GE</p>
</blockquote></td>
<td><blockquote>
<p>91</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>15</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_QUALIFIER_U SAGE</p>
</blockquote></td>
<td><blockquote>
<p>92</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>7</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_QUOTED_IDE NTIFIER_CASE</p>
</blockquote></td>
<td><blockquote>
<p>93</p>
</blockquote></td>
<td><blockquote>
<p>4</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_SPECIAL_ CHARACTERS</p>
</blockquote></td>
<td><blockquote>
<p>94</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"*()(}|:;"</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_SUBQUERIES</p>
</blockquote></td>
<td><blockquote>
<p>95</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>31</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_UNION</p>
</blockquote></td>
<td><blockquote>
<p>96</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_MAX_COLUMN S_IN_ GROUP_BY</p>
</blockquote></td>
<td><blockquote>
<p>97</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_MAX_COLUMN S_IN_ INDEX</p>
</blockquote></td>
<td><blockquote>
<p>98</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_MAX_COLUMN S_IN_ ORDER_BY</p>
</blockquote></td>
<td><blockquote>
<p>99</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_MAX_COLUMN S_IN_ SELECT</p>
</blockquote></td>
<td><blockquote>
<p>100</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_MAX_COLUMN S_IN_ TABLE</p>
</blockquote></td>
<td><blockquote>
<p>101</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_MAX_INDEX_S IZE</p>
</blockquote></td>
<td><blockquote>
<p>102</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_MAX_ROW_SI ZE_INCLUDES</p>
<p>_LONG</p>
</blockquote></td>
<td><blockquote>
<p>103</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"N"</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_MAX_ROW_SI ZE</p>
</blockquote></td>
<td><blockquote>
<p>104</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_MAX_STATEM ENT_LEN</p>
</blockquote></td>
<td><blockquote>
<p>105</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>32768</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 14%" />
<col style="width: 23%" />
<col style="width: 24%" />
<col style="width: 13%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>SQL_MAX_TABLES_ IN_ SELECT</p>
</blockquote></th>
<th><blockquote>
<p>106</p>
</blockquote></th>
<th><blockquote>
<p>0</p>
</blockquote></th>
<th><blockquote>
<p>NA</p>
</blockquote></th>
<th>NA</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>SQL_MAX_CHAR_LI TERAL_ LEN</p>
</blockquote></td>
<td><blockquote>
<p>108</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>8192</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_TIMEDATE_AD D_ INTERVALS</p>
</blockquote></td>
<td><blockquote>
<p>109</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_TIMEDATE_DIF F_ INTERVALS</p>
</blockquote></td>
<td><blockquote>
<p>110</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_MAX_BINARY_ LITERAL_LEN</p>
</blockquote></td>
<td><blockquote>
<p>112</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>8192</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_LIKE_ESCAPE</p>
<p>_CLAUSE</p>
</blockquote></td>
<td><blockquote>
<p>113</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td>"Y"</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_QUALIFIER_L OCATION</p>
</blockquote></td>
<td><blockquote>
<p>114</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_OJ_CAPABILITI ES</p>
</blockquote></td>
<td><blockquote>
<p>115</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0x49</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_ALTER_DOMAI N</p>
</blockquote></td>
<td><blockquote>
<p>117</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_SQL_CONFOR MANCE</p>
</blockquote></td>
<td><blockquote>
<p>118</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_DATETIME_LIT ERALS</p>
</blockquote></td>
<td><blockquote>
<p>119</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_BATCH_ROW_ COUNT</p>
</blockquote></td>
<td><blockquote>
<p>120</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_BATCH_SUPP ORT</p>
</blockquote></td>
<td><blockquote>
<p>121</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CREATE_ASS ERTION</p>
</blockquote></td>
<td><blockquote>
<p>127</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CREATE_CHA RACTER_SET</p>
</blockquote></td>
<td><blockquote>
<p>128</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CREATE_COLL ATION</p>
</blockquote></td>
<td><blockquote>
<p>129</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CREATE_DOM AIN</p>
</blockquote></td>
<td><blockquote>
<p>130</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CREATE_SCH EMA</p>
</blockquote></td>
<td><blockquote>
<p>131</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_CREATE_TABL E</p>
</blockquote></td>
<td><blockquote>
<p>132</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_CREATE_</p>
</blockquote></td>
<td><blockquote>
<p>133</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 14%" />
<col style="width: 23%" />
<col style="width: 25%" />
<col style="width: 12%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>TRANSLATION</p>
<p>SQL_CREATE_VIEW</p>
</blockquote></th>
<th><blockquote>
<p>134</p>
</blockquote></th>
<th>NA</th>
<th><blockquote>
<p>1</p>
</blockquote></th>
<th>NA</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>SQL_DROP_ASSER TION</p>
</blockquote></td>
<td><blockquote>
<p>136</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_DROP_CHARA CTER_ SET</p>
</blockquote></td>
<td><blockquote>
<p>137</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_DROP_COLLA TION</p>
</blockquote></td>
<td><blockquote>
<p>138</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_DROP_DOMAI N</p>
</blockquote></td>
<td><blockquote>
<p>139</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_DROP_SCHEM A</p>
</blockquote></td>
<td><blockquote>
<p>140</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_DROP_TABLE</p>
</blockquote></td>
<td><blockquote>
<p>141</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_DROP_TRANS LATION</p>
</blockquote></td>
<td><blockquote>
<p>142</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_DROP_VIEW</p>
</blockquote></td>
<td><blockquote>
<p>143</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_INDEX_KEYW ORDS</p>
</blockquote></td>
<td><blockquote>
<p>148</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_INFO_SCHEM A_VIEWS</p>
</blockquote></td>
<td><blockquote>
<p>149</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_SQL92_DATETI ME_ FUNCTIONS</p>
</blockquote></td>
<td><blockquote>
<p>155</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>7</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_SQL92_FOREI GN_KEY_DELETE_ RULE</p>
</blockquote></td>
<td><blockquote>
<p>156</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_SQL92_FOREI GN_KEY_UPDATE_ RULE</p>
</blockquote></td>
<td><blockquote>
<p>157</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_SQL92_GRAN T</p>
</blockquote></td>
<td><blockquote>
<p>158</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>3184</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_SQL92_NUME RIC_ VALUE_FUNCTIONS</p>
</blockquote></td>
<td><blockquote>
<p>159</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>63</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_SQL92_PREDI CATES</p>
</blockquote></td>
<td><blockquote>
<p>160</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>16135</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_SQL92_RELATI ONAL_ JOIN_OPERATORS</p>
</blockquote></td>
<td><blockquote>
<p>161</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>592</p>
</blockquote></td>
<td>NA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_SQL92_REVO</p>
</blockquote></td>
<td><blockquote>
<p>162</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>3184</p>
</blockquote></td>
<td>NA</td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 13%" />
<col style="width: 18%" />
<col style="width: 20%" />
<col style="width: 27%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>KE</p>
</blockquote></th>
<th colspan="4"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>SQL_SQL92_ROW_ VALUE_ CONSTRUCTOR</p>
</blockquote></td>
<td><blockquote>
<p>163</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>11</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_SQL92_STRIN G_ FUNCTIONS</p>
</blockquote></td>
<td><blockquote>
<p>164</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>238</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_SQL92_VALUE</p>
<p>_ EXPRESSIONS</p>
</blockquote></td>
<td><blockquote>
<p>165</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_AGGREGATE_ FUNCTIONS</p>
</blockquote></td>
<td><blockquote>
<p>169</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>127</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_DDL_INDEX</p>
</blockquote></td>
<td><blockquote>
<p>170</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_INSERT_STAT EMENT</p>
</blockquote></td>
<td><blockquote>
<p>172</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SQL_COLLATION_S EQ</p>
</blockquote></td>
<td><blockquote>
<p>10004</p>
</blockquote></td>
<td>NA</td>
<td><blockquote>
<p>NA</p>
</blockquote></td>
<td><blockquote>
<p>"ISO 8859-1"</p>
</blockquote></td>
</tr>
</tbody>
</table>

1.  Information Type Name

2.  Information Type Number

3.  Information Value Integer

4.  Information Value Bitmask

5.  Information Value Text

## ipGetInfo

> This method is used by the OpenAccess SDK SQL engine to query the IP
> about its version and its behavior in various areas. It is called
> after the ipConnect method call and may be called before processing
> some other functions. Most IPs can ignore all the keys except the
> IP_VERSION key and use the implementation shown in the IP template.
> The sample IPs contain implementations with settings that are
> generally applicable.
>
> String ipGetInfo(int iInfoType)

#### Parameters for ipGetInfo

> **Parameter Type Description INPUT**
>
> iInfoType int The type of information requested. See [Table
> 5](#ipgetinfo) for more information.
>
> **RETURN**
>
> String Value of the option as a string. For options that require a
> TRUE or FALSE value, use \"1\" to return TRUE and \"0\" to return
> FALSE. Return NULL to accept the default.

#### Information Type for ipGetInfo

> **Information Type Description**
>
> IP_INFO_ALLOW_BLOCK_JOIN_ON_NON_EQUAL\_ COND
>
> Return TRUE if block joins should be supported on join conditions that
> do not use the EQUAL operator. The value is returned as a short.
>
> The default value is FALSE.
>
> IP_INFO_ALLOW_SCHEMA_UPDATE Return TRUE if static schema can be
> modified. The value
>
> is returned as a short.
>
> The default value is the value specified for the service attribute
> ServiceSQLSchemaUpdateAllowed.
>
> IP_INFO_COND_LIST_NORMALIZATION\_ LIMIT Return the limit on the number
> of condition lists that can
>
> be generated to normalize the search condition. If the search
> condition cannot be normalized within the limit, the query will be
> processed without exposing the condition lists to the IP. The value is
> returned as a short.
>
> The default value is 1024.
>
> IP_INFO_CONVERT_NUMERIC_VAL Return TRUE if the OpenAccess SDK SQL
> engine should
>
> convert numeric strings to have correct number of scale digits based
> on Scale/Precision. Return FALSE if no conversion should be applied.
>
> The default value is FALSE.
>
> IP_INFO_DDL_RESULT_ROWS Connection level information. Returns the
> value for the result row count.
>
> The IP can specify the required result row count for DDL operations.
> If the IP does not specify any value, a value of
>
> -1 is reported.
>
> The default value is -1.
>
> IP_INFO_DS_INFO Connection level information. Return 1 if the IP is
> written to customize the driver information.
>
> The default value is 0.
>
> IP_INFO_ERROR_COUNT_LIMIT Return the limit on number of errors that
> should be reported per query. Any errors that exceed the limit will be
> ignored. The value is returned as a short.
>
> The default value is 10.
>
> IP_INFO_FILTER_VIEWS_WITH_QUALIFIER_NAME Return TRUE if you want the
> Catalog functions to only
>
> return views that belong to the current qualifier. Return FALSE if you
> want all views to be exposed to client applications.
>
> The default value is FALSE.
>
> IP_INFO_GENERATE_COL_NAME_FOR_EXP Connection level information.
> Specifies whether to
>
> generate column name for the constant expression in the query. Return
> FALSE if no column name is generated for a constant expression in the
> query.
>
> The default value is TRUE.
>
> IP_INFO_IN_COND_LIST_NORMALIZATION_LIMIT Return the limit on condition
> lists that are expanded for IN
>
> (subquery). dam_getSetOfConditionListsEx should skip expansion of IN
> condition list when it exceeds the limit. The value is returned as a
> short.
>
> The default value is 1024.
>
> IP_INFO_IGNORE_DATETIME_PARSE_ERROR Return TRUE if you want to ignore
> DATE, TIME, and
>
> DATETIME literal errors in pass-through and selective pass-through
> modes.
>
> The default value is FALSE.
>
> IP_INFO_JOINORDER_UNORDERED\_ PERCENT_LIMIT
>
> Return the percentage of tables that can remain unordered when star
> join relation is used for deciding the join order. When join ordering
> is decided using the star join relation of a single fact table related
> to multiple Dimension tables, this limit is used to check whether
> ordering can be decided even if all tables cannot be ordered. Any
> tables that cannot be ordered will be processed at the end of the
> ordered list in the order they appear in the query. The value is
> returned as a short.
>
> The default value is 40.
>
> IP_INFO_MINIMUM_NUMERIC_SCALE Return the min scale value for
> NUMERIC/DECIMAL data
>
> types to the OpenAccess SDK SQL engine to perform numeric
> calculations.
>
> If this value is not specified in the IP, a default value of 3 is
> returned. An error is reported if the value is greater than 127 or is
> less than 0.
>
> IP_INFO_MULTI_COLUMN_INDEX_FILTER Return a flag indicating what
> conditions should be passed
>
> through to the IP in case of multi- column indexes. Should return a
> bitwise OR with one of the following options:

- IP_MCI_FILTER_NONE - pass in all conditions.

- IP_MCI_FILTER_LIKE - filter out like.

- IP_MCI_FILTER_ISNULL - filter out IS NULL.

- IP_MCI_FILTER_NOT - filter out NOT.

- IP_MCI_FILTER_COLLATION_ORDER - filter out COLLATION ORDER.

- IP_MCI_FILTER_DEFAULT - filter out LIKE, IS NULL, NOT and COLLATION
  > ORDER.

> If the IP returns IP_MCI_FILTER_NONE as the value, then the OpenAccess
> SDK SQL engine only checks operator support for the condition and does
> not apply any additional filtering. If the IP returns
> IP_MCI_FILTER_DEFAULT as the value, then the OpenAccess SDK SQL engine
> filters out columns with LIKE, IS NULL, NOT and COLLATION ORDER
>
> conditions. The value is returned as a short. The default value is
> IP_MCI_FILTER_DEFAULT
>
> IP_INFO_OWNER_NAMEW The IP should return the current login name or a
> fixed name like \"OAUSER\". The owner name is used by the OpenAccess
> SDK SQL engine as the default owner name during query validation.
>
> The default value is OAUSER.
>
> IP_INFO_OWNER_TERMW The term used to refer to the second part of the
> three part object name. The value is returned as a string.
>
> The default value is Owner.
>
> IP_INFO_QUALIFIER_NAMEW Return the default qualifier value for this
> connection. If CREATE VIEW/DROP VIEW commands do not specify the
> qualifier, this value is used as the qualifier. The value returned
> here and the value returned as the TABLE_QUALIFIER for the
> table/column objects within this database must match.
>
> The default value is SCHEMA.
>
> IP_INFO_QUALIFIER_TERMW The term used to describe what the first part
> of the three- part object name refers to.
>
> The default value is Database.
>
> IP_INFO_SUPPORT_SCHEMA_SEARCH\_ PATTERN Return TRUE if you want the
> schema search objects to
>
> include search pattern. Return FALSE if you cannot support search
> patterns.
>
> The default value is FALSE.
>
> IP_INFO_SUPPORT_VALUE_FOR_RESULT\_ ALIAS Return TRUE if you want to be
> able to return result value
>
> based on the result column alias name. Return FALSE if you want the
> base column value to be returned for the corresponding result columns.
>
> The default behavior is FALSE.
>
> IP_INFO_TABLE_ROWSET_REPORT\_ MEMSIZE_LIMIT Return TRUE to indicate
> that the OpenAccess SDK SQL
>
> engine should report an error when TableRowset limit is exceeded.
> Default behavior is for the OpenAccess SDK SQL engine to convert the
> table rowset into data required for current outer block rows being
> processed and not raise any errors.
>
> The default behavior is FALSE.
>
> IP_INFO_TYPE_INFO Connection level information. Return 1 if the IP is
> written to customize the types information.
>
> The default value is 0.
>
> IP_INFO_TXN_ISOLATION The transaction isolation level supported by the
> IP. Should return one of the following: SQL_TXN_READ_UNCOMMITTED,
> SQL_TXN_READ_COMMITTED, SQL_TXN_REPEATABLE_READ, SQL_TXN_SERIALIZABLE.
>
> The value is returned as an integer.
>
> The default value is SQL_TXN_READ_COMMITTED.
>
> IP_INFO_USE_PKEY_FOR_INDEX\_ OPTIMIZATION Return TRUE if the
> OpenAccess SQL Engine should use
>
> the Primary Key as the Unique index for query optimization. The value
> is returned as a short. The default value is FALSE.
>
> IP_INFO_USE_ROWID_FOR_INDEX\_ OPTIMIZATION Return TRUE if the
> OpenAccess SQL Engine should use
>
> the ROWID column as the Unique index for query optimization. The value
> is returned as a short.
>
> The default value is FALSE.
>
> IP_INFO_VALIDATE_NULL_CONSTRAINT Indicates to the OpenAccess SDK SQL
> Engine whether or
>
> not it should enforce a check constraint when null values are
> specified in non-nullable result columns.
>
> Valid values:
>
> TRUE \| FALSE
>
> If set to TRUE, the default value, the OpenAccess SDK SQL Engine
> enforces the check constraint and reports the integrity constraint
> violation.
>
> If set to FALSE, the OpenAccess SDK SQL Engine ignores the violation
> and allows null values.
>
> **Note:** This option is only applicable when working in SQL
> pass-through mode.
>
> IP_INFO_VALIDATE_QUERY_SEMANTICS Return FALSE if you do not want the
> OpenAccess SDK
>
> SQL engine to validate query semantics in pass-through query mode.
> This is useful when the data source query engine supports non-standard
> SQL syntax. The value is returned as a short.
>
> The default value is TRUE.
>
> All columns of the SELECT list and ORDER BY should have set functions.
>
> Example:
>
> SELECT COUNT(empno) FROM emp ORDER BY empno;
>
> IP_INFO_VALIDATE_SCALAR_FUNC Return FALSE if scalar function
> validation should be
>
> optional. If the query uses a scalar function that is not registered,
> the SQL Engine will not report an error during query planning.
>
> The value is returned as a short. The default value is TRUE.
>
> IP_INFO_VALIDATE_TABLE_WITH_OWNER Return TRUE if the OpenAccess SDK
> SQL engine should
>
> validate the table using the value returned for IP_INFO_OWNER_NAME.
> This option is used when the query does not qualify the table
> reference with the owner. Note that if the query is fully qualified
> these options are not applicable. The table owner validation is
> applied to User Tables and Views and not to System Tables.Applications
> that have fixed schema can return FALSE.The default value is FALSE.
>
> IP_VERSION Return the IP version string in the format \##.##.
>
> IP_INFO_VALIDATE_TABLE_WITH_QUALIFIER Return TRUE for the OpenAccess
> SDK SQL engine to
>
> validate the table reference in the query using the value returned for
> IP_INFO_QUALIFIER_NAME. This option is used when the query does not
> qualify the table reference with the qualifier. If the query is fully
> qualified, this option is not applicable. The table qualifier
> validation is applied to User Tables and Views and not to System
> Tables.
>
> Applications that have a fixed schema can return FALSE. The default
> value is FALSE.
>
> IP_INFO_USE_CURRENT_QUALIFIER_FOR_SYSTEM\_ SCHEMA
>
> Return TRUE for the OpenAccess SDK SQL engine to use the current
> qualifier for the System table schema instead of the default
> \"SCHEMA\".
>
> The default value is FALSE.

## ipGetSupport

> This method is used by the OpenAccess SDK SQL engine to query the IP
> about its support for the types of SQL operations allowed, the mode in
> which the IP is working, whether DCL is supported, and other features.
> The information reported by this method defines the mode in which the
> OpenAccess SDK SQL engine operates and how it interacts with the IP.
> ipGetSupport is called after ipConnect and may be called multiple
> times between other IP method calls.
>
> int ipGetSupport(int iSupportType)

#### Parameters for ipGetSupport

> **Parameter Type Description INPUT**
>
> iSupportType int The type of support queried. See
>
> [Table 7](#ipgetsupport) for more information.
>
> **RETURN**
>
> int 1 - the requested option is enabled.
>
> 0 - the requested option is not supported.
>
> The value for the IP_SUPPORT_OP_XX is the logical OR of the setting
> returned by this method and the setting in the OA_SUPPORT column of
> the OA_COLUMNS table for the specified column.
>
> The value for the IP_SUPPORT_SELECT, IP_SUPPORT_INSERT,
> IP_SUPPORT_UPDATE,
>
> IP_SUPPORT_DELETE and IP_SUPPORT_SELECT_FOR_UPDATE is the logical OR
> of the setting returned by this method and the setting in the
> OA_SUPPORT column of the OA_TABLES table for the specified table name.

#### IP support options

#### 

> **Support Option Description**
>
> IP_SUPPORT_SELECT The IP supports select.
>
> IP_SUPPORT_INSERT The IP supports insert.
>
> IP_SUPPORT_UPDATE The IP supports update.
>
> IP_SUPPORT_DELETE The IP supports delete.
>
> IP_SUPPORT_SELECT_FOR_UPDATE The IP supports select for update.
>
> IP_SUPPORT_START_QUERY The IP is to be notified of starting of query
> execution on the same table through multiple queries.
>
> IP_SUPPORT_END_QUERY The IP is to be notified of ending of query
> execution on the same table through multiple queries.
>
> IP_SUPPORT_SCHEMA The IP implements dynamic schema.
>
> IP_SUPPORT_UNION_CONDLIST The IP is to receive the condition lists as
> a union condition list.
>
> IP_SUPPORT_PRIVILEGES The IP implements a privilege function.
>
> IP_SUPPORT_UNICODE_INFO The IP is to use Unicode strings for owner
> name, owner term, qualifier, an qualifier term.
>
> IP_SUPPORT_PASSTHROUGH_QUERY The IP will work in pass through mode
> where it is
>
> responsible for executing the query.
>
> IP_SUPPORT_TABLE_FUNCTIONS The IP supports table functions.
>
> IP_SUPPORT_NATIVE_COMMAND The IP implements an ipNative function
> method that should be called when the parser is unable to recognize
> the query.
>
> IP_SUPPORT_BLOCK_JOIN The IP has implemented block join optimization.
> IP_SUPPORT_JOIN_ORDER\_ SELECTION The IP supports the join order
> selection.
>
> IP_SUPPORT_QUERY_MODE\_ SELECTION The IP determines at EXECUTE call
> whether it needs to
>
> work in pass-through query mode or in row-based mode.
>
> IP_SUPPORT_VALIDATE\_ SCHEMAOBJECTS_IN_USE This option applies only
> when working in dynamic schema
>
> mode. Call the IP to return column information only for the columns
> referenced in the query. The SCHEMA function must be implemented to
> check for the filter condition on the column name.
>
> **Operator Support**
>
> IP_SUPPORT_OP_EQUAL The IP can handle = conditions.
>
> IP_SUPPORT_OP_NOT The IP can handle NOT conditions.
>
> IP_SUPPORT_OP_GREATER The IP can handle \> conditions.
>
> IP_SUPPORT_OP_SMALLER The IP can handle \< conditions.
>
> IP_SUPPORT_OP_BETWEEN The IP can handle BETWEEN conditions.
>
> IP_SUPPORT_OP_LIKE The IP can handle LIKE conditions.
>
> IP_SUPPORT_OP_NULL The IP can handle IS NULL conditions.
>
> **DDL SQL Data Definition Language support**
>
> IP_SUPPORT_CREATE_TABLE The IP supports table creation.
>
> IP_SUPPORT_DROP_TABLE The IP supports table deletion.
>
> IP_SUPPORT_CREATE_INDEX The IP supports index creation.
>
> IP_SUPPORT_DROP_INDEX The IP supports index deletion.
>
> IP_SUPPORT_ALTER_TABLE The IP supports altering existing tables.
>
> **Stored procedure**
>
> IP_SUPPORT_PROCEDURE The IP supports procedure execution.
>
> **Views**
>
> IP_SUPPORT_CREATE_VIEW The IP supports view creation.
>
> IP_SUPPORT_DROP_VIEW The IP supports view deletion.
>
> IP_SUPPORT_QUERY_VIEW The IP supports querying views. SQL Data Control
> Language support (DCL)
>
> IP_SUPPORT_CREATE_USER The IP supports the CREATE USER DCL command.
>
> IP_SUPPORT_DROP_USER The IP supports the DROP USER DCL command.
>
> IP_SUPPORT_CREATE_ROLE The IP supports the CREATE ROLE DCL command.
>
> IP_SUPPORT_DROP_ROLE The IP supports the DROP ROLE DCL command.
>
> IP_SUPPORT_GRANT The IP supports the GRANT DCL command.
>
> IP_SUPPORT_REVOKE The IP supports the REVOKE DCL command.
>
> The value for the IP_SUPPORT_OP_XX is the logical OR of the setting
> returned by this function and the setting in the OA_SUPPORT column of
> the OA_COLUMNS table for the specified column.
>
> The value for the IP_SUPPORT_SELECT, IP_SUPPORT_INSERT,
> IP_SUPPORT_UPDATE,
>
> IP_SUPPORT_DELETE and IP_SUPPORT_SELECT_FOR_UPDATE is the logical OR
> of the setting returned by this function and the setting in the
> OA_SUPPORT column of the OA_TABLES table for the specified table name.
>
> One way to implement this feature, and the template uses this
> approach, is to set up an array as shown in this example, and to use
> the iSupportType as an index into it.

### Example

> IP_SUPPORT_ARRAY mem_support_array =

<table>
<colgroup>
<col style="width: 6%" />
<col style="width: 4%" />
<col style="width: 89%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>{0,</p>
</blockquote></th>
<th colspan="2"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>1,</td>
<td>/*</td>
<td><blockquote>
<p>IP_SUPPORT_SELECT */</p>
</blockquote></td>
</tr>
<tr class="even">
<td>0,</td>
<td>/*</td>
<td><blockquote>
<p>IP_SUPPORT_INSERT */</p>
</blockquote></td>
</tr>
<tr class="odd">
<td>0,</td>
<td>/*</td>
<td><blockquote>
<p>IP_SUPPORT_UPDATE */</p>
</blockquote></td>
</tr>
<tr class="even">
<td>0,</td>
<td>/*</td>
<td><blockquote>
<p>IP_SUPPORT_DELETE */</p>
</blockquote></td>
</tr>
<tr class="odd">
<td>0,</td>
<td>/*</td>
<td><blockquote>
<p>IP_SUPPORT_SCHEMA - IP supports Schema methods */</p>
</blockquote></td>
</tr>
</tbody>
</table>

> 0, /\* IP_SUPPORT_PRIVILEGES - IP can validate user privileges \*/ 1,
> /\* IP_SUPPORT_OP_EQUAL \*/
>
> 0, /\* IP_SUPPORT_OP_NOT \*/
>
> 0, /\* IP_SUPPORT_OP_GREATER \*/
>
> 0, /\* IP_SUPPORT_OP_SMALLER \*/
>
> 0, /\* IP_SUPPORT_OP_BETWEEN \*/
>
> 0, /\* IP_SUPPORT_OP_LIKE \*/
>
> 0, /\* IP_SUPPORT_OP_NULL \*/
>
> 0, /\* IP_SUPPORT_SELECT_FOR_UPDATE \*/
>
> \...
>
> };

## ipGetTypesInfo

> This method is called to get information about data types.
>
> oa_types_info\[\] sqlipTypeInfo()
>
> **Parameters for sqlipTypeInfo**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 36%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>oa_types_info[ ]</p>
</blockquote></td>
<td><blockquote>
<p>OADS_SUCCESSOADS_ERROR</p>
</blockquote></td>
</tr>
</tbody>
</table>

## Creating an oa_types_info object

> An object of oa_types_info\[\] can be created using the *new* keyword.
> For example, oa_types_info\[\] *xxxx*
>
> = new oa_types_info\[n\]. where:
>
> \'*xxxx*\' is the variable name and \'*n*\' is an integer value.
>
> The following code snippet illustrates how to create an oa_types_info
> object.
>
> /\* Overloaded Constructor\*/
>
> oa_types_info(String Typename, int Datatype, long OaPrecision,
>
> String LiteralPrefix, String LiteralSuffix, String CreateParams, int
> OaNullable, int OaCaseSensitive, int OaSearchable,
>
> int UnsignedAttrib, int OaMoney, int AutoIncrement,
>
> int MinimumScale, int MaximumScale, String LocaltypeName) \*/
>
> oa_types_info\[\] typesInfo = new oa_types_info\[22\];
>
> typesInfo\[0\] = new oa_types_info(\"CHAR\", 1, 4096, \"\'\", \"\'\",
> \"length\", 1, 1, 3,
>
> 0, 0, 0, DAMOBJ_NOTSET, DAMOBJ_NOTSET, \"CHAR\");
>
> ...
>
> typesInfo\[21\] = new oa_types_info(\"WLONGVARCHAR\", -10, 2147483647,
> \"N\'\", \"\'\", \"max length\", 1, 1, 3, 0, 0, 0, DAMOBJ_NOTSET,
>
> DAMOBJ_NOTSET, \"WLONGVARCHAR\");
>
> The following table contains different parameter values of the
> overloaded oa_types_info constructor that can be used to customize the
> information type.
>
> **Parameter values for the overloaded constructor of oa_types_info**

<table>
<colgroup>
<col style="width: 20%" />
<col style="width: 24%" />
<col style="width: 20%" />
<col style="width: 21%" />
<col style="width: 14%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameters</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Values</strong></p>
</blockquote></th>
<th colspan="3"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>Typename</p>
</blockquote></td>
<td><blockquote>
<p>WLONGVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>WVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>WCHAR</p>
</blockquote></td>
<td><blockquote>
<p>BIT</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>Datatype</p>
</blockquote></td>
<td><blockquote>
<p>-10</p>
</blockquote></td>
<td><blockquote>
<p>-9</p>
</blockquote></td>
<td><blockquote>
<p>-8</p>
</blockquote></td>
<td><blockquote>
<p>-7</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaPrecision</p>
</blockquote></td>
<td><blockquote>
<p>2147483647</p>
</blockquote></td>
<td><blockquote>
<p>4096</p>
</blockquote></td>
<td><blockquote>
<p>4096</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>LiteralPrefix</p>
</blockquote></td>
<td><blockquote>
<p>N'</p>
</blockquote></td>
<td><blockquote>
<p>N'</p>
</blockquote></td>
<td><blockquote>
<p>N'</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LiteralSuffix</p>
</blockquote></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>CreateParams</p>
</blockquote></td>
<td><blockquote>
<p>max length</p>
</blockquote></td>
<td><blockquote>
<p>max length</p>
</blockquote></td>
<td><blockquote>
<p>length</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaNullable</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaCaseSensitive</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaSearchable</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>UnsignedSttrib</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaMoney</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>AutoIncrement</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>MinimumScale</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>maximumScale</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LocaltypeName</p>
</blockquote></td>
<td><blockquote>
<p>WLONGVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>WVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>WCHAR</p>
</blockquote></td>
<td><blockquote>
<p>BIT</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **Parameter values for the overloaded constructor of oa_types_info**

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 20%" />
<col style="width: 21%" />
<col style="width: 27%" />
<col style="width: 14%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameters</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Values</strong></p>
</blockquote></th>
<th colspan="3"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>Typename</p>
</blockquote></td>
<td><blockquote>
<p>TINYINT</p>
</blockquote></td>
<td><blockquote>
<p>BIGINT</p>
</blockquote></td>
<td><blockquote>
<p>LONGVARBINARY</p>
</blockquote></td>
<td><blockquote>
<p>VARBINARY</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>Datatype</p>
</blockquote></td>
<td><blockquote>
<p>-6</p>
</blockquote></td>
<td><blockquote>
<p>-5</p>
</blockquote></td>
<td><blockquote>
<p>-4</p>
</blockquote></td>
<td><blockquote>
<p>-3</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaPrecision</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td><blockquote>
<p>19</p>
</blockquote></td>
<td><blockquote>
<p>2147483647</p>
</blockquote></td>
<td><blockquote>
<p>4096</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>LiteralPrefix</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>0x</p>
</blockquote></td>
<td><blockquote>
<p>0x</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LiteralSuffix</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 17%" />
<col style="width: 21%" />
<col style="width: 27%" />
<col style="width: 14%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>CreateParams</p>
</blockquote></th>
<th><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></th>
<th><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></th>
<th><blockquote>
<p>max length</p>
</blockquote></th>
<th><blockquote>
<p>max length</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>OaNullable</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaCaseSensitive</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaSearchable</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>UnsignedSttrib</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaMoney</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>AutoIncrement</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>MinimumScale</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>maximumScale</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LocaltypeName</p>
</blockquote></td>
<td><blockquote>
<p>TINYINT</p>
</blockquote></td>
<td><blockquote>
<p>BIGINT</p>
</blockquote></td>
<td><blockquote>
<p>LONGVARBINARY</p>
</blockquote></td>
<td><blockquote>
<p>VARBINARY</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **Parameter values for the overloaded constructor of oa_types_info**

<table>
<colgroup>
<col style="width: 20%" />
<col style="width: 18%" />
<col style="width: 27%" />
<col style="width: 17%" />
<col style="width: 15%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameters</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Values</strong></p>
</blockquote></th>
<th colspan="3"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>Typename</p>
</blockquote></td>
<td><blockquote>
<p>BINARY</p>
</blockquote></td>
<td><blockquote>
<p>LONGVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>NULL</p>
</blockquote></td>
<td><blockquote>
<p>CHAR</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>Datatype</p>
</blockquote></td>
<td><blockquote>
<p>-2</p>
</blockquote></td>
<td><blockquote>
<p>-1</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaPrecision</p>
</blockquote></td>
<td><blockquote>
<p>4096</p>
</blockquote></td>
<td><blockquote>
<p>2147483647</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>4096</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>LiteralPrefix</p>
</blockquote></td>
<td><blockquote>
<p>0x</p>
</blockquote></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LiteralSuffix</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>CreateParams</p>
</blockquote></td>
<td><blockquote>
<p>length</p>
</blockquote></td>
<td><blockquote>
<p>max length</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>length</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaNullable</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaCaseSensitive</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaSearchable</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>UnsignedSttrib</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaMoney</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>AutoIncrement</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>MinimumScale</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>maximumScale</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LocaltypeName</p>
</blockquote></td>
<td><blockquote>
<p>BINARY</p>
</blockquote></td>
<td><blockquote>
<p>LONGVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>NULL</p>
</blockquote></td>
<td><blockquote>
<p>CHAR</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **Parameter values for the overloaded constructor of oa_types_info**
>
> **Parameters Values**

<table style="width:100%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 21%" />
<col style="width: 21%" />
<col style="width: 23%" />
<col style="width: 13%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>Typename</p>
</blockquote></th>
<th><blockquote>
<p>NUMERIC</p>
</blockquote></th>
<th><blockquote>
<p>INTEGER</p>
</blockquote></th>
<th><blockquote>
<p>SMALLINT</p>
</blockquote></th>
<th><blockquote>
<p>REAL</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>Datatype</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>4</p>
</blockquote></td>
<td><blockquote>
<p>5</p>
</blockquote></td>
<td><blockquote>
<p>7</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaPrecision</p>
</blockquote></td>
<td><blockquote>
<p>40</p>
</blockquote></td>
<td><blockquote>
<p>10</p>
</blockquote></td>
<td><blockquote>
<p>5</p>
</blockquote></td>
<td><blockquote>
<p>24</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LiteralPrefix</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>LiteralSuffix</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>CreateParams</p>
</blockquote></td>
<td><blockquote>
<p>precisionscale</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaNullable</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaCaseSensitive</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaSearchable</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>UnsignedSttrib</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaMoney</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>AutoIncrement</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>MinimumScale</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>maximumScale</p>
</blockquote></td>
<td><blockquote>
<p>32</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>LocaltypeName</p>
</blockquote></td>
<td><blockquote>
<p>NUMERIC</p>
</blockquote></td>
<td><blockquote>
<p>INTEGER</p>
</blockquote></td>
<td><blockquote>
<p>SMALLINT</p>
</blockquote></td>
<td><blockquote>
<p>REAL</p>
</blockquote></td>
</tr>
</tbody>
</table>

#### Parameter values for the overloaded constructor of oa_types_info

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 13%" />
<col style="width: 18%" />
<col style="width: 15%" />
<col style="width: 17%" />
<col style="width: 17%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameters</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Values</strong></p>
</blockquote></th>
<th colspan="4"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>Typename</p>
</blockquote></td>
<td><blockquote>
<p>DOUBLE</p>
</blockquote></td>
<td><blockquote>
<p>VARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>DATE</p>
</blockquote></td>
<td><blockquote>
<p>TIME</p>
</blockquote></td>
<td><blockquote>
<p>TIMESTAMP</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>Datatype</p>
</blockquote></td>
<td><blockquote>
<p>8</p>
</blockquote></td>
<td><blockquote>
<p>12</p>
</blockquote></td>
<td><blockquote>
<p>91</p>
</blockquote></td>
<td><blockquote>
<p>92</p>
</blockquote></td>
<td><blockquote>
<p>93</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaPrecision</p>
</blockquote></td>
<td><blockquote>
<p>53</p>
</blockquote></td>
<td><blockquote>
<p>4096</p>
</blockquote></td>
<td><blockquote>
<p>10</p>
</blockquote></td>
<td><blockquote>
<p>8</p>
</blockquote></td>
<td><blockquote>
<p>19</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>LiteralPrefix</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LiteralSuffix</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>'</p>
</blockquote></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>CreateParams</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>max length</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaNullable</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaCaseSensitive</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>1</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaSearchable</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>3</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
<td><blockquote>
<p>2</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>UnsignedSttrib</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaMoney</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>AutoIncrement</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>MinimumScale</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>&lt;Null&gt;</p>
</blockquote></td>
<td><blockquote>
<p>0</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 14%" />
<col style="width: 17%" />
<col style="width: 14%" />
<col style="width: 16%" />
<col style="width: 17%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>maximumScale</p>
<p>LocaltypeName</p>
</blockquote></th>
<th><blockquote>
<p>&lt;Null&gt;</p>
<p>DOUBLE</p>
</blockquote></th>
<th><blockquote>
<p>&lt;Null&gt;</p>
<p>VARCHAR</p>
</blockquote></th>
<th><blockquote>
<p>&lt;Null&gt;</p>
<p>DATE</p>
</blockquote></th>
<th><blockquote>
<p>&lt;Null&gt;</p>
<p>TIME</p>
</blockquote></th>
<th><blockquote>
<p>0</p>
<p>TIMESTAMP</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>ipConnect</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method connects to the given logical database data source using
> the supplied user name and password. The IP can validate the user and
> password and generate a DAM_FAILURE if the user is not authorized to
> access the database. The IP can also set up data structures to enforce
> user rights to select, delete, insert and update tables. This method
> is called immediately after OpenAccess SDK has created the IP object.
>
> int ipConnect(
>
> long tmHandle, long dam_hdbc,
>
> String sDataSourceName, String sUserName, String sPassword, String
> sCurrentCatalog, String sIPProperties,
>
> String sIPCustomProperties)

#### Parameters for ipConnect

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 27%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>tmHandle</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>A handle for use with the tm_trace() method.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>dam_hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The OpenAccess SDK SQL engine connection handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>sDataSourceName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the data source to which the connection is being
made.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>sUserName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>User name passed in by the client.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>sPassword</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Password typed in by the client.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>sCurrentCatalog</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Name of the catalog to use. The value can be specified by the client
or in the DataSourceCurrentCatalog attribute</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>sIPProperties</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Data source specific information that is set in the
DataSourceIPProperties attribute.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>sIPCustomProperties</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Custom properties passed in by the client. The keywords that can
appear in this string are based on the setting</p>
</blockquote></td>
</tr>
</tbody>
</table>

> of the DataSourceIPCustomProperties attribute.
>
> **RETURN**
>
> int DAM_SUCCESS -- on
>
> successDAM_FAILURE -- on failure**Note:** The default value is
> DAM_SUCCESS. If no return value is specified, the OpenAccess SDK SQL
> engine assumes that the return value is DAM_SUCCESS. The IP does not
> generate a DAM_FAILURE unless specified.
>
> The sUserName and sPassword parameters are valid only if the data
> sourceproperty DataSourceLogonMethod is set toDBMSLogon(UID,PWD) or
> DBMSLogon(DBUID,DBPWD). Please refer to the *OpenAccess SDK
> Administrator\'s Guide* for information about what this method needs
> to do to support Microsoft Windows authentication (NTLM or Kerberos).
>
> The sIPCustomProperties string contains all the information that was
> supplied by the user, including any custom options your IP will
> support. The string contains all the options as semicolon separated
> values. Each value consists of the option name and its setting. This
> allows your IP to supports its own keywords for specifying connection
> level information.

### Supporting failover and load balancing

> Refer to the *OpenAccess SDK Administrator\'s Guide* to learn how
> OpenAccess SDK supports failover and load balancing.

## ipDCL

> You must implement this method in your IP if it is to handle the SQL
> database control language (DCL) syntax for managing user privileges.
> This means that the OpenAccess SDK SQL engine calls this method
> anytime it needs to manipulate a system or object privilege (for
> example, GRANT CREATE TABLE TO JOHN) or it needs to delete a privilege
> (for example, REVOKE DROP ANY INDEX FROM JOHN). The GETSUPPORT method
> should return true for the supported DCL operations.
>
> Refer to [Implementing DCL
> Support](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/implementing-dcl-support.html)
> in the *OpenAccess SDK Programmer\'s Guide* for more information.
>
> int ipDCL(
>
> long hstmt,
>
> int iType)
>
> xo_long piNumResRows)

#### Parameters for ipDCL

> **Parameter Type Description INPUT**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>hstmt</p>
</blockquote></th>
<th><blockquote>
<p>long</p>
</blockquote></th>
<th><blockquote>
<p>Statement handle of the currently active statement.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Type of command requested.</p>
</blockquote>
<ul>
<li><blockquote>
<p>DAM_CREATE_USER - create an user.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_DROP_USER - drop an user.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_CREATE_ROLE - create a role.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_DROP_ROLE - drop a role.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_GRANT - grant a privilege.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_REVOKE - revoke a privilege.</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piNumResRows</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The number of rows affected by this operation.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on</p>
<p>successDAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
</tbody>
</table>

## ipDDL

> You must implement this method in your IP if it is to handle the DDL
> SQL syntax for creating tables, indexes, and other supported objects.
> The OpenAccess SDK SQL engine calls this method when it needs to
> create or delete an object (for example, a table). ipGetSupport should
> return true for the supported DDL operations.
>
> int ipDDL(
>
> long hstmt,
>
> int iType,
>
> xo_long piNumResRows)

#### Parameters for ipDDL

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle of the currently active statement.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Type of object management requested:</p>
</blockquote>
<ul>
<li><blockquote>
<p>DAM_CREATE_TABLE - create a table.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_DROP_TABLE - drop a table.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_CREATE_INDEX - create an index.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_DROP_INDEX - drop an index.</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piNumResRows</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>Number of rows affected by this operation.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **RETURN**
>
> int DAM_SUCCESS - on
>
> successDAM_FAILURE - on failure

### See also

> **1.** [ipGetSupport](#ipgetsupport)

## ipDisconnect

> This method closes the specified connection. All associated data
> should be freed. This call is generated when the user disconnects from
> the server and OpenAccess SDK client issues a disconnect call.
>
> Immediately after this, the Java object that was created for this
> connection will be destroyed.
>
> int ipDisconnect(long dam_hdbc)
>
> **Parameters for ipDisconnect**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>dam_hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The connection handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on</p>
<p>successDAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
</tbody>
</table>

## ipEndTransaction

> This method is called by the OpenAccess SDK SQL engine to end a
> transaction with a COMMIT, a ROLLBACK, or a PREPARE_TO\_ COMMIT. This
> method will be called when a transaction is terminated by the user or
> by the server in response to errors. The necessary transaction and
> lock management code for your data source goes here.
>
> This method must return DAM_SUCCESS even if the IP will not support
> transactions.
>
> int ipEndTransaction( long dam_hdbc, int iType)

#### Parameters for ipEndTransaction

> Parameter Type Description
>
> **INPUT**

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 31%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>dam_hdbc</p>
</blockquote></th>
<th><blockquote>
<p>long</p>
</blockquote></th>
<th><blockquote>
<p>The connection handle.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_ROLLBACK -</p>
<p>rollbackDAM_COMMIT - commitDAM_PREPARE_TO_COMMI T - commit for first
phase of a two- phase operation.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on</p>
<p>successDAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
</tbody>
</table>

> You can return an IP-specific error code for a failed COMMIT operation
> by calling dam_addError to add an error with your application-specific
> error message and native code and then return DAM_FAILURE.

dam_addError(

dam_hdbc,

> NULL,DAM_ERR_01000, 100, \"My error message\")
>
> The previous line returns a native error code 100 and message \"My
> error message\" to the client application.

## ipExecute

> This method is called with iStmtType set to the operation to perform.
> The connection handle, statement handle, and search column handle are
> passed in.
>
> When working in row-based mode, the handle is used to determine if a
> search condition for a column is passed to the IP. The IP should then
> use this condition to read only the rows that match the specified
> index value(s). The search column handle that is passed can be part of
> a single column index or a multi-column index. If the IP supports
> multi-column indexes, it should call dam_getOptimalIndexAndConditions.
>
> For UPDATE, DELETE, and INSERT statements, the IP should maintain a
> counter for the number of rows effected and return it through the
> iNumResRows object that is passed in.
>
> int ipExecute( long hstmt,
>
> int iStmtType, long hSearchCol, xo_long piNumResRows)
>
> **Parameters for ipExecute**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 32%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Handle to the statement being executed.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iStmtType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The type of the statement:</p>
</blockquote>
<ul>
<li><blockquote>
<p>DAM_SELECT - select</p>
</blockquote></li>
</ul></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2" rowspan="2"></th>
<th><ul>
<li><blockquote>
<p>DAM_UPDATE - update</p>
</blockquote></li>
<li><blockquote>
<p>DAM_INSERT - insert</p>
</blockquote></li>
<li><blockquote>
<p>DAM_DELETE - delete</p>
</blockquote></li>
<li><blockquote>
<p>DAM_SELECT_FOR_UPDATE - lock</p>
</blockquote></li>
</ul></th>
</tr>
<tr class="odd">
<th><blockquote>
<p>selected rows.</p>
</blockquote>
<ul>
<li><blockquote>
<p>DAM_FETCH - called to process additional rows in the case of a
select.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_CLOSE - called to close the current select processing.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_START_QUERY - called to mark the beginning of a multiple sub-
query execution sequence on the same table.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_END_QUERY - called to mark the end of a multiple sub-query
execution sequence on the same table.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_SET_QUERY_MODE - called to allow the IP to determine whether to
work in row-based or SQL pass- through mode.</p>
</blockquote></li>
</ul></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hSearchCol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the column that has the search list to optimize the IP.
If this is non-zero, then it must be used. It will be non-zero if the
column has an index on it and the IP has reported that it supports
equality and other comparison operators used in the search condition.
Use the dam_getOptimalIndexAndConditions method to get the associated
search list.</p>
<p>This parameter is not applicable when working in SQL pass-through
mode.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piNumResRows</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>Number of rows effected by a INSERT, UPDATE, or DELETE</p>
<p>statement execution. Use piNumResRows.setVal() to return this
information.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure
DAM_SUCCESS_WITH_RESULT_P</p>
<p>ENDING - when the IP is processing</p>
<p>partial results sets in cursor mode</p>
</blockquote></td>
</tr>
</tbody>
</table>

## ipfuncxxx

> The IP must implement any function it has registered for handling
> custom scalar functions. When a scalar function is identified in a
> query, the OpenAccess SDK SQL engine first tries to find a match from
> its built-in functions. If it doesn\'t find a match, then it searches
> in the list of function registered by the IP as part of the
> INIT_SCALAR operation. If the OpenAccess SDK SQL engine finds a match
> in the IP registered list of scalar functions, it calls that function.
> Otherwise, an error is reported.
>
> Refer to the *DataDirect OpenAccess SDK Programmer\'s Guide* for more
> information on OpenAccess SDK built-in and IP-defined scalar function
> processing.
>
> long ip_func_xxx( long hstmt, long pMemTree
>
> long hValExpList)
>
> **Parameters for ipFuncxxx**

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 22%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle of the currently active statement.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pMemTree</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The tree to be used for any memory allocation.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hValExpList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>List of input arguments to be used by the function. This list is
traversed and accessed using the OpenAccess SDK SQL engine functions
dam_getFirstValExp, dam_getValueTypeOfExp, and dam_getNextValExp.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>valid DAM_HVAL - create an output value using the dam_createVal
function.</p>
<p>0 - on failure</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td colspan="2" rowspan="4"></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_getfirstvalexp">dam_getFirstValExp</a></p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#dam_getnextvalexp">dam_getNextValExp</a></p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_getvaluetypeofexp">dam_getValueTypeOfExp</a></p>
</blockquote></li>
</ul></td>
</tr>
</tbody>
</table>

## ipGetLongData

> This function support streaming for a particular column. The IP calls
> dam_addLOBLocatorValToRow to add the locator value. When the client
> requests data, the OpenAccess SDK SQL Engine invokes
>
> OAIP_GetLongData, which is exported from the IP.
>
> If the IP specifies that the type of stream is CHAR, the OpenAccess
> SDK SQL Engine uses following syntax in the IP:
>
> int ip_GetLongData( long dam_hstmt, Object Locator, int iXOType,
>
> int iOpType, char() Buffer, long IBufferLen, xo_long piLenOrInd)
>
> If the IP specifies that the type of stream is binary, the OpenAccess
> SDK SQL Engine uses following syntax in the IP:
>
> int ip_GetLongData( long dam_hstmt, Object Locator, int iXOType,
>
> int iOpType, byte() Buffer, long IBufferLen, xo_long piLenOrInd)

#### Parameters for ipGetLongData

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 34%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>dam_hstmt</p>
</blockquote></td>
<td><blockquote>
<p>Statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>Locator</p>
</blockquote></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>LOB locator value, which is added in dam_addLOBLocatorValToRow.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iXOType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>XOtype specified in dam_addLOBLocatorValToRow.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iOpType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Type of LOB Locator options. Valid values are LOB_FIRST, LOB_NEXT,
and LOB_CLOSE.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>Buffer</p>
</blockquote></td>
<td><blockquote>
<p>char()byte()</p>
</blockquote></td>
<td><blockquote>
<p>The Buffer location, into which the IP can populate data.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>IBufferLen</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The ioTargetValPtr buffer length.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piLenOrInd</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The Length or Indicator pointer.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_SUCCESS_WITH_INFO - on</p>
<p>success, but more data still must be</p>
</blockquote></td>
</tr>
</tbody>
</table>

> fetched for the LOB DAM_FAILURE - on failure

### See also

> [dam_addLOBLocatorValToRow](#dam_addloblocatorvaltorow)

## ipGetScalarFunctions

> This is an optional method you can implement in your IP to register
> user-defined scalar functions. [Setting Up](#setting-up-the-samples)
> [the samples](#setting-up-the-samples) describes using example3 for a
> sample implementation.
>
> Refer to [User Defined Scalar
> Functions](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/User_defined_scalar_functions_2.html)
> in the *OpenAccess SDK Programmer\'s Guide* for information on
> implementing user defined scalar functions.
>
> scalar_functions\[\] ipGetScalarFunctions()
>
> **Parameters for ipGetScalarFunctions**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 36%" />
<col style="width: 41%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>RETURN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td></td>
<td><blockquote>
<p>scalar_functions[ ]</p>
</blockquote></td>
<td><blockquote>
<p>An array of scalar_functions used to</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>register user defined scalar functions.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>The IP class must implement a</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>method for each user defined scalar</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>function and register the method's</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>name.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### Example

> scalar_functions\[\] ipGetScalarFunctions()
>
> {
>
> scalar_functions\[\] MyFuncs = new scalar_function\[5\];
> MyFuncs\[0\]=new scalar_function(\"INTVAL\", 1, \"ip_func_intval\",
>
> XO_TYPE_INTEGER, 1);
>
> MyFuncs\[1\]=new scalar_function(\"DOUBLEVAL\", 1,
> \"ip_func_doubleval\", XO_TYPE_DOUBLE, 1);
>
> MyFuncs\[2\] = new scalar_function(\"CHARVAL\", 1,
> \"ip_func_charval\", XO_TYPE_CHAR, 1);
>
> MyFuncs\[3\] = new scalar_function(\"INTEGER\", \"ADD\", 1,
> \"ip_func_Integer_add\", XO_TYPE_INTEGER, 1);
>
> MyFuncs\[4\] = new scalar_function(\"STRING\", \"ADD\", 1,
> \"ip_func_String_add\", XO_TYPE_VARCHAR, 1);
>
> return MyFuncs;
>
> }

## ipNative

> You must implement this method in your IP if it is to handle the
> execution of commands that the OpenAccess SDK SQL engine is unable to
> parse. This method is called if during the parsing of a SQL command
> there is a syntax error.
>
> In this case, the OpenAccess SDK SQL engine calls ipNative in the IP
> to check whether it can handle the parsing. If ipNative returns true,
> then it is called again to execute the command.
>
> Currently, only commands that do not return rows are supported.
> ipGetSupport should return true for IP_SUPPORT_NATIVE_COMMAND.
>
> int ipNative(
>
> long hstmt,
>
> int iCmdType, String szCmd,
>
> xo_long piNumResRows)

#### Parameters for ipNative

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 30%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Statement handle of the currently active statement.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iCmdType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_NC_VALIDATE - validate the passed in command.</p>
<p>DAM_NC_EXECUTE - execute the native command and return the number of
rows affected.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szCmd</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The command to execute.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piNumResRows</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The number of rows affected by this operation.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - in</p>
<p>DAM_NC_VALIDATE mode, this indicates that the command is something
that is supported. In DAM_NC_EXECUTE mode, this indicates that the
command was successfully executed.</p>
<p>DAM_FAILURE - indicates there is a syntax error in the native
command. The specific error message should be added using the
dam_addError method.</p>
<p>DAM_NOT_AVAILABLE - in</p>
</blockquote></td>
</tr>
</tbody>
</table>

> DAM_NC_VALIDATE mode, the command issued is not supported.
>
> The ipNative method can call dam_setIP_hstmt to save any context it
> creates during validation and use dam_getIP_hstmt to retrieve the
> context when called for DAM_NC_EXECUTE.

### See also

- [dam_addError](#dam_adderror)

- [dam_getIP_hstmt](#dam_getip_hstmt)

- [dam_setIP_hstmt](#dam_setip_hstmt)

- [ipGetSupport](#ipgetsupport)

## ipProcedure

> If you want the IP to support stored procedure calls that return a
> single result set, implement this method in your IP. The OpenAccess
> SDK SQL engine passes stored procedure calls to this method for
> procedures that return, at most, a single pre-defined result set.
> Stored procedure calls are invoked using the ODBC stored procedure
> invocation syntax.
>
> ipGetSupport should return true for IP_SUPPORT_PROCEDURE.
>
> Refer to [Stored Procedure
> Processing](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/stored-procedure-processing.html)
> in the *DataDirect OpenAccess SDK Programmer\'s Guide* for more
> information.
>
> int ipProcedure( long hstmt,
>
> int iType,
>
> xo_long piNumResRows)

#### Parameters for ipProcedure

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 30%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle of the currently active statement.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Type of operation requested:</p>
</blockquote>
<ul>
<li><blockquote>
<p>DAM_PROCEDURE - execute the stored procedure and return the first set
of rows or all the rows. If the IP is written to work in cursor mode,
return DAM_SUCCESS_WITH_RESULT_P</p>
</blockquote></li>
</ul>
<blockquote>
<p>ENDING after returning a block of rows.</p>
</blockquote>
<ul>
<li><blockquote>
<p>DAM_FETCH - get next set of rows. Return</p>
</blockquote></li>
</ul>
<blockquote>
<p>DAM_SUCCESS_WITH_RESULT_P</p>
</blockquote></td>
</tr>
</tbody>
</table>

> ENDING when more rows are pending. Return DAM_SUCCESS when completed.

- DAM_CLOSE - cleanup processing of the stored procedure.

> piNumResRows xo_long The number of rows affected by this operation.
>
> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure

### See also

- [ipGetSupport](#ipgetsupport)

## ipProcedureDynamic

> If your IP is to support stored procedure calls that return one or
> more results sets that are defined at runtime, you must implement this
> method in your IP. The OpenAccess SDK SQL engine passes stored
> procedure calls to this method for all stored procedures marked in the
> schema as supporting results defined at run-time.
>
> Stored procedure calls are invoked using the ODBC stored procedure
> invocation syntax. ipGetSupport should return true for
> IP_SUPPORT_PROCEDURE.
>
> int ipProcdureDynamic( long hstmt,
>
> int iType,
>
> xo_long piNumResRows)

#### Parameters for ipProcedureDynamic

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 30%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle of the currently active statement.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Type of operation requested:</p>
</blockquote>
<ul>
<li><blockquote>
<p>DAM_DSP_INIT - initialize</p>
</blockquote></li>
<li><blockquote>
<p>DAM_DSP_EXECUTE - execute the stored procedure and return either all
rows or the first block of rows from the current result set. If the IP
is written to work in cursor mode, then return
DAM_SUCCESS_WITH_RESULT_P</p>
</blockquote></li>
</ul>
<blockquote>
<p>ENDING after returning a block of</p>
</blockquote></td>
</tr>
</tbody>
</table>

> rows.

- DAM_FETCH - get the next set of rows from the current result set.
  > Return DAM_SUCCESS_WITH_RESULT_P

> ENDING when more rows are pending. Return DAM_SUCCESS when completed.

- DAM_CLOSE - cleanup processing of the current result set.

> piNumResRows xo_long The number of rows affected by this operation.
>
> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure DAM_SUCCESS_WITH_MORE_RE
>
> SULT_SETS - more results available.

### See also

- [ipGetSupport](#ipgetsupport)

## ipPrivilege

> You must implement this method in your IP if it is to support
> privileges. This method is called before the OpenAccess SDK SQL engine
> processes any commands. It passes in the user name, the operation
> code, and the fully qualified object name. Your method should return
> TRUE if the specified user is allowed to perform the requested
> operation on the object. ipGetSupport should return true for
> IP_SUPPORT_PRIVILEGE.
>
> Refer to [Implementing Privilege
> Support](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/implementing-privilege-support.html)
> in the *DataDirect OpenAccess SDK Programmer\'s Guide* for more
> information.
>
> int ipPrivilege(
>
> int iCmdType, String pcUserName, String pcQualifier, String pcOwner,
> String pcName)

#### Parameters for ipPrivilege

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 35%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>Parameter</p>
</blockquote></th>
<th><blockquote>
<p>Type</p>
</blockquote></th>
<th><blockquote>
<p>Description</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>INPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iCmdType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Type of DCL operation.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 31%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>pcUserName</p>
</blockquote></th>
<th><blockquote>
<p>String</p>
</blockquote></th>
<th><blockquote>
<p>Name of the connected user.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pcQualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Name of the database in which the table falls. Can be used to
distribute tables into physically different databases.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pcOwner</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The owner of this object.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pcName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of this object.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_TRUE - the SQL engine can perform the requested operation.
DAM_FALSE - the operation is not authorized.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> When calling ipPrivilege, the OpenAccess SDK SQL engine passes the
> full object information (qualifier.owner.name) for the following
> commands:

- CREATE TABLE

- ALTER TABLE

- DROP TABLE

- CREATE VIEW

- DROP VIEW

### See also

- [ipGetSupport](#ipgetsupport)

## ipSchema

> You must implement this method in your IP if it is to handle schema
> management. The OpenAccess SDK SQL engine calls this method when it
> needs to get any of the following information about the schema:

- List of tables

- Columns for a table

- Details about each columns

- Foreign keys

- Indexes

- Other schema objects

> If this method is implemented, the IP takes over the responsibility of
> exposing the OA_TABLES, OA_COLUMNS, OA_STATISTICS, OA_FKEYS, OA_PROC
> and OA_PROCCOLUMNS tables. The
>
> GETSUPPORT method should return true for IP_SUPPORT_SCHEMA. The
> implementation should use the search conditions that are passed in,
> and should cache any schema information that is time-consuming to
> retrieve.
>
> int ipSchema(
>
> long dam_hdbc, long pMemTree, int iType,
>
> long pList, Object pSearchObj)

#### Parameters for ipSchema

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 30%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>dam_hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The OpenAccess SDK SQL engine connection handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pMemTree</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The memory tree to be used for all memory allocation.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The type of schema information requested:</p>
</blockquote>
<ul>
<li><blockquote>
<p>DAMOBJ_TYPE_TABLE - tables information as defined by OA_TABLES.</p>
</blockquote></li>
<li><blockquote>
<p>DAMOBJ_TYPE_COLUMN - columns information as defined by
OA_COLUMNS.</p>
</blockquote></li>
<li><blockquote>
<p>DAMOBJ_TYPE_STAT - index information as defined by OA_STATISTICS.</p>
</blockquote></li>
<li><blockquote>
<p>DAMOBJ_TYPE_FKEY - foreign key information as defined by
OA_FKEYS.</p>
</blockquote></li>
<li><blockquote>
<p>DAMOBJ_TYPE_PROC - procedures information as defined by OA_PROC.</p>
</blockquote></li>
<li><blockquote>
<p>DAMOBJ_TYPE_PROC_COLUMN -</p>
</blockquote></li>
</ul>
<blockquote>
<p>procedure columns information as defined by OA_PROCCOLUMNS.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Requested object(s) are returned by placing them in this list using
the dam_add_schemaobj calls. The IP can pass the pSearchObj filter to
the dam_add_schemaobj method to have the OpenAccess SDK SQL engine
filter the objects before adding to the list.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pSearchObj</p>
</blockquote></td>
<td><blockquote>
<p>object</p>
</blockquote></td>
<td><blockquote>
<p>Contains the information about search conditions for the requested
object type. The IP and the OpenAccess SDK SQL engine use this
information to filter out the information returned by the IP.</p>
<p>For example, if the OpenAccess SDK</p>
</blockquote></td>
</tr>
</tbody>
</table>

> SQL engine needs only the columns for a specific table, it sets the
> table_name in the pSearchObj. The IP must use this information or pass
> the pSearchObj to the OpenAccess SDK SQL engine when calling the
> dam_add_schemaobj method. It is NULL if no search criteria is
> specified. For instance, if the user issues the query:
>
> SELECT \* FROM oa_tables
>
> then pSearchObj is null and the IP returns all the table objects.
> Access this data by typecasting this pointer based on the requested
> object type (iType) and then accessing the members of the object to
> retrieve the required information:

- DAMOBJ_TYPE_CATALOG -

> schemaobj_table

- DAMOBJ_TYPE_SCHEMA -

> schemaobj_table

- DAMOBJ_TYPE_TABLETYPE -

> schemaobj_table

- DAMOBJ_TYPE_TABLE -

> schemaobj_table

- DAMOBJ_TYPE_COLUMN -

> schemaobj_column

- DAMOBJ_TYPE_STAT -

> schemaobj_stat

- DAMOBJ_TYPE_FKEY -

> schemaobj_fkey

- DAMOBJ_TYPE_PKEY -

> schemaobj_pkey

- DAMOBJ_TYPE_PROC -

> schemaobj_proc

- DAMOBJ_TYPE_PROC_COLUMN -

> schemaobj_proccolumn Similarly, if the OpenAccess SDK
>
> SQL engine only needs the schema for a specific table, it sets the
> schema name in the pSearchObj. The IP must use this information or
> pass the pSearchObj to the OpenAccess SDK SQL engine when calling
> dam_add_damobjW(). It is NULL if no search criteria is specified.
>
> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure

## ipSchemaEx

> You must implement this optional method in your IP to support stored
> procedures with one or more runtime defined results. The OpenAccess
> SDK SQL engine calls this method to get a list of procedure result
> columns. The GETSUPPORT method should return true for
> IP_SUPPORT_SCHEMA.
>
> **Note:** This method is not required if the output result set of the
> stored procedure is fixed in terms of what columns are returned and
> you are only returning one result set.
>
> int ipSchemaEx( long hstmt,
>
> long pMemTree, int iType,
>
> long pList, Object pSearchObj)
>
> **Parameters for ipSchemaEx**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The OpenAccess SDK SQL engine statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pMemTree</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The memory tree to be used for all memory allocation.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Type of schema information requested: DAMOBJ_TYPE_PROC_COLUMN -</p>
<p>procedure columns information as defined by OA_PROCCOLUMNS.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Requested object(s) are returned by placing them in this list using
the dam_add_schemaobj calls. The IP can pass the pSearchObj filter to
the dam_add_schemaobj methods to have the OpenAccess SDK SQL engine
filter the objects before adding to the list.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pSearchObj</p>
</blockquote></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>Contains the information about search conditions for the requested
object type. This information is used by the IP and by the OpenAccess
SDK SQL engine to filter out information returned by the IP. For
example, if the OpenAccess SDK</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 23%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2"></th>
<th><blockquote>
<p>SQL engine only needs columns for a specific table, it sets
table_name in the pSearchObj. The IP uses this information or passes the
pSearchObj to the OpenAccess SDK SQL engine when calling the
dam_add_schemaobj method. It is NULL if no search criteria is specified.
For example, if the user issues SELECT * FROM oa_tables, pSearchObj is
NULL and the IP returns all table objects. Access this data by
typecasting this object based on the requested object type (iType) and
then accessing the members methods to retrieve the required information:
DAMOBJ_TYPE_PROC_COLUMN</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#dam_add_schemaobj">dam_add_schemaobj</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

## ipSetInfo

> This method modifies the IP settings. ipSetInfo is used by the
> OpenAccess SDK SQL engine to pass configuration changes to the IP.
>
> See Table **Information Type for ipSetInfo** for the types of
> information that the IP can provide.
>
> int ipSetInfo(
>
> int iInfoType, String pInfoValue)

#### Parameters for ipSetInfo

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 34%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>iInfoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The type of information.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pInfoValue</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>A string buffer containing the information value.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> int DAM_SUCCESS - on success
>
> DAM_NOT_AVAILABLE - value not supported by IP

#### Table 30. Information Type for ipSetInfo {#table-30.-information-type-for-ipsetinfo}

> **Information Type Description**
>
> IP_INFO_QUALIFIER_NAMEW The value of the current qualifier for the
> connection. The IP should return schema information based on the
> current qualifier.
>
> IP_INFO_TXN_ISOLATION Transaction isolation level that is being
> requested by the client. The IP can use this information to control
> how it locks the data it accesses. The value is an integer.

- SQL_TXN_READ_UNCOMMITTED

- SQL_TXN_READ_COMMITTED

- SQL_TXN_REPEATABLE_READ

- SQL_TXN_SERIALIZABLE

## ipStartTransaction

> This method is called by the OpenAccess SDK SQL engine to start a new
> transaction and after any COMMIT or ROLLBACK request to initiate a new
> transaction. The necessary transaction and lock management code for
> you data source goes here. This method must return DAM_SUCCESS even if
> the IP will not support transactions.
>
> int ipStartTransaction(long dam_hdbc)
>
> **Parameters for ipStartTransaction**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 33%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>dam_hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Connection handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
</tbody>
</table>

# OpenAccess SDK SQL Engine core methods for Java

> This section describes the OpenAccess SDK SQL engine core
> functionality exposed by the JDAM Class of
>
> OpenAccess SDK SQL engine.
>
> For additional methods that allow more detailed inspection of the
> query, see [SQL Engine parse tree
> methods](#sql-engine-parse-tree-methods-for-java) [for
> Java](#sql-engine-parse-tree-methods-for-java). Those methods are
> required for working in SQL pass-through mode, in table expression
> pushdown mode, and are otherwise optional, for when you need to
> retrieve additional data from the parse tree.
>
> If your IP is to be implemented for SQL pass-through mode, use only
> methods that are documented in
> [SQL](#sql-engine-parse-tree-methods-for-java) [Engine parse tree
> methods for Java](#sql-engine-parse-tree-methods-for-java).

## Passing data to and from the OpenAccess SDK SQLEngine

> The OpenAccess SDK SQL engine interface for Java uses objects that are
> created by the IP and passed into the methods for Java when more than
> one item or complex data type needs to be returned. For example, if a
> method has a return value and another integer output value, then the
> method would look like:
>
> int getData(xo_int iData)
>
> An object of type xo_int would first be instantiated so that the
> called function, getData in this case, can update its value.
>
> xo_int iData = new xo_int() int output = getData(iData)
>
> The xo_int class defines the methods setVal and getVal. In cases of
> strings, the IP code must declare objects of type StringBuffer and
> pass these into the method so that it can update the value. Dates are
> handled using the class xo_tm that has members to handle year, month,
> day and time.

## Exchanging data

> This section contains information about working with rows, getting
> column handles, and setting variant data types.

### Working with rows

> The OpenAccess SDK SQL engine handles all the data management for
> creating results sets for sending to the client and for creating row
> structures for the IP to fill in.
>
> Calling the row allocation function dam_allocRow with the statement
> handle starts a new row. After a row is allocated, columns can be
> added one at a time using dam_addxxxValToRow. The order in which the
> columns are added to a row does not matter. When building a row, map
> the data from the physical format to the row that matches the
> definition in the schema. A row does not have to contain all the
> columns defined for it in the schema.
>
> For example, you can:

1.  Build a row with just the columns that appear in the WHERE clause.

2.  Then, evaluate the row using dam_isTargetRow.

3.  If it is a target row, add the columns that appear in the SELECT
    clause.

4.  Free any row that fails the dam_isTargetRow evaluation, using
    dam_freeRow.

> Internally the OpenAccess SDK SQL engine uses row and column caches to
> optimize the processing in which rows and columns may be created and
> destroyed.
>
> The format in which the data is represented by the OpenAccess SDK SQL
> engine for the SQL data types is shown in [Reference
> tables](#reference-tables). The mapping specified in this table
> applies to data supplied to the OpenAccess SDK SQL engine in calls
> such as dam_addxxxValToRow and data provided by the OpenAccess SDK SQL
> engine in calls such as dam_getValueToSet.

### See also

- [dam_allocRow](#dam_allocrow)

- [dam_addxxxValToRow](#dam_addxxxvaltorow)

- [dam_freeRow](#dam_freerow)

- [dam_getValueToSet](#_bookmark70)

- [dam_isTargetRow](#dam_istargetrow)

### Getting column handles

> Many OpenAccess SDK SQL engine API functions use column handles
> instead of column names. A column handle is found by calling the
> following functions:

- dam_getCol with the column name.

- dam_getColByNum with the column number in the schema.

- dam_getFirstCol and dam_getNextCol with the column type filter.

> For writing the fastest IP, the function dam_getCol should be
> performed once and the column handles saved and reused as more rows of
> data are formed.

### See also

- [dam_getCol](#_bookmark38)

- [dam_getColByNum](#dam_getcolbynum)

- [dam_getFirstCol](#dam_getfirstcol)

- [dam_getNextCol](#_bookmark53)

### Bulk fetching

> The Java IP supports bulk fetching of rows using a Select query. The
> Java IP populates multiple rows in the result buffer using the
> ResultBuffer class. The OpenAccess SDK SQL engine then parses the
> result buffer and populates rows in the result set.
>
> The ResultBuffer class provides various methods, such as putNull, to
> put the data in the result buffer.
>
> Bulk fetching is performed when IP EXECUTE is called with the
> DAM_SELECT statement type. If the IP is unable to fetch the entire
> data during the first iteration, in further iterations, IP EXECUTE is
> called with the DAM_FETCH statement type to fetch the remaining data.
>
> **Note:** Bulk Fetching is supported only in Java.

#### To perform bulk fetching:

1.  Allocate the result buffer using dam_allocResultbuffer.

> ResultBuffer resultBuffer;
>
> resultBuffer = jdam.dam_allocResultbuffer()

2.  Enter the column values into the result buffer.

    a.  Obtain the column handles for the columns specified in the query
        > using dam_getFirstCol(hstmt, DAM_COL_IN_USE) and
        > dam_getNextCol.

    b.  Add the column values, for which column handles exist, to the
        > buffer using putXXX() or each row of a table.

> while ( hCol != 0)
>
> {
>
> //Add Column details and values to buffer. hCol =
> dam_getNextCol(stmt);
>
> }

c.  After all the rows or the fetched block size of rows are processed,
    > set the number of columns using ResultBuffer.setNoOfResColumns.
    > And set the number of rows for the values filled in the buffer
    > using ResultBuffer.setNoRowsInBuffer.

d.  Add the buffer rows to the result table using
    > dam_addResultBufferToTable and return a return code for any of the
    > following conditions:

    - If BufferOverflowException occurs, return code must be
      > DAM_SUCCESS_WITH_RESULT_PENDING. This return code notifies
      > OpenAccess that the IP still needs to add some more rows from
      > the result buffer to the table.

    - If all rows are added to the result table and
      > BufferOverflowException does not occur, return code must be
      > DAM_SUCCESS.

e.  Clear the buffer using clear.

f.  Return the return code.

g.  Repeat steps 2 until all the data is fetched from the result buffer
    > to the result table.

> catch(BufferOverflowException e)
>
> {
>
> resultBuffer.setNoRowsInBuffer(iNumResRows);
> resultBuffer.setNoOfResColumns(iNoOfResColumns);
> jdam.dam_addResultBufferToTable(dam_hstmt, resultBuffer); iRetCode =
> DAM_SUCCESS_WITH_RESULT_PENDING;
>
> resultBuffer.clear();
>
> }
>
> return iRetCode;

3.  Release the allocated buffer using dam_freeResultBuffer.

#### Notes

- When IP EXECUTE is called with the DAM_CLOSE statement type, free the
  allocated buffer using

> dam_freeResultBuffer.

- Use a try block for the code from steps from 2(b) to 2 (e).

- See the bulk fetch example at:
  \<install-Dir\>\oaserver900\ip\oajava\memory\damip.java.

#### Example : Bulk fetching of rows from PICTURE_TABLE {#example-bulk-fetching-of-rows-from-picture_table}

> Suppose you have PICTURE_TABLE and you want to perform bulk fetch on
> its rows. To perform bulk fetch, use the ResultBuffer class to fill
> PICTURE_TABLE rows in the result buffer. OpenAccess then parses the
> result buffer and fills rows in the result table.

#### PICTURE_TABLE Columns

> **Column Name Type**
>
> NAME STRING
>
> PICTURE LONGBINARY
>
> COMMENTS LONGSTRING
>
> WCOMEMNTS LONGSTRING
>
> Consider the following bulk fetch query example: SELECT NAME, PICTURE,
> COMMENTS, WCOMMENTS FROM PICTURE_TABLE.
>
> To bulk fetch rows from PICTURE_TABLE:

1.  Allocate the buffer using dam_allocResultBuffer.
    dam_allocResultBuffer(stmt, Byte Buffer, String Buffer, LOB buffer)

2.  Enter the column values into the result buffer.

    a.  Obtain the column handles for the columns specified in the query
        > using

> dam_getFirstCol(dam_hstmt, DAM_COL_IN_USE) and dam_getNextCol.

b.  Add the column values, for which column handles exist, to the buffer
    > using putXXX() for each row of PICTURE_TABLE.

c.  After all the rows or fetch block size are processed, set the number
    > of columns using ResultBuffer setNoOfResColumns, and set the
    > number of rows that are filled in the buffer using ResultBuffer
    > setNoRowsInBuffer.

d.  Add the buffer rows to the result table using
    > dam_addResultBufferToTable and return a return code for any of the
    > following conditions:

- If BufferOverflowException occurs, return code must be
  > DAM_SUCCESS_WITH_RESULT_PENDING. This return code notifies
  > OpenAccess that the IP still needs to add some more rows from the
  > result buffer to the table.

- If all rows are added to the result table and BufferOverflowException
  > does not occur, return code must be DAM_SUCCESS.

e.  Clear the buffer using clear.

f.  Return the return code.

g.  Repeat step 2 untill all data is fetched from the result buffer to
    > the result table.

> The following code snippet illustrates how to implement the steps
> mentioned above for bulk fetching.
>
> hcol = hcolName = hcolPicture = hcolComments = hcolWComments=0;
>
> /\* initialize row count \*/ m_iNumResRows = 0;
>
> /\* get the column handles \*/
>
> hcol = jdam.dam_getFirstCol(pStmtDA.dam_hstmt, ip.DAM_COL_IN_USE);
> while (hcol != 0)
>
> { StringBuffer sColName;
>
> sColName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
>
> jdam.dam_describeCol(hcol, null, sColName, null, null);
> pStmtDA.m_iNoOfResColumns++;
>
> /\* get next column handle \*/
>
> hcol = jdam.dam_getNextCol(pStmtDA.dam_hstmt);
>
> }
>
> ...
>
> /\* entering column values in buffer using putXXX() \*/
> pStmtDA.m_resultBuffer.putString(pName);
> pStmtDA.m_resultBuffer.putLongBinary(picturexlBuffer);
> pStmtDA.m_resultBuffer.putLongString(pData.toString());
> pStmtDA.m_resultBuffer.putLongString(pData.toString());
>
> ...
>
> /\*Handling BufferOverflowException\*/ catch(BufferOverflowException
> e)
>
> {
>
> pStmtDA.m_resultBuffer.setNoRowsInBuffer(m_iNumResRows);
>
> jdam.dam_addResultBufferToTable(pStmtDA.dam_hstmt,pStmtDA.m_resultBuffer);
> iRetCode = DAM_SUCCESS_WITH_RESULT_PENDING;
>
> resultBuffer.clear()
>
> }

3.  Release the allocated buffer.

> jdam.dam_freeResultBuffer(dam_hstmt,resultBuffer);

### Reference tables

> This section contains tables referenced by the APIs listed in this
> section. The following table represents the Java data types that the
> Java code must use when calling the OpenAccess SDK SQL engine methods
> for Java.

#### OpenAccess SDK Data Types and Java Type When Adding Value

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 38%" />
<col style="width: 35%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>SQL Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Data Type - use with API calls</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Java Data Type</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>BIGINT</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_BIGINT</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>BINARY</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_BINARY</p>
</blockquote></td>
<td><blockquote>
<p>byte [ ]</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>BIT</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_BIT</p>
</blockquote></td>
<td><blockquote>
<p>boolean</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>CHAR</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_CHAR</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer - string of characters</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>DATE</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_DATE</p>
</blockquote></td>
<td><blockquote>
<p>xo_tm - create this object using xo_tm(year, month, day).</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>DOUBLE</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_DOUBLE</p>
</blockquote></td>
<td><blockquote>
<p>double</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>FLOAT</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_FLOAT</p>
</blockquote></td>
<td><blockquote>
<p>double</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>INTEGER</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_INTEGER</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LONGVARBINARY</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_ LONGVARBINARY</p>
</blockquote></td>
<td><blockquote>
<p>byte [ ]</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>LONGVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_ LONGVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer - string of characters</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>NUMERIC</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_NUMERIC</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer - a string with a decimal point. Does not have to be
padded for exact precision and scale specifications. Use "." as the
decimal separator regardless of the locale.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>REAL</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_REAL</p>
</blockquote></td>
<td><blockquote>
<p>float</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>SMALLINT</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_SMALLINT</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>TIME</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_TIME</p>
</blockquote></td>
<td><blockquote>
<p>xo_tm - create this object using xo_tm(hour, minute, second,
fraction).</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>TIMESTAMP</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_TIMESTAMP</p>
</blockquote></td>
<td><blockquote>
<p>xo_tm - create this object using xo_tm (year, month, day, hour,
minute, second, fraction).</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>TINYINT</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_TINYINT</p>
</blockquote></td>
<td><blockquote>
<p>byte [ ]</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>VARBINARY</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_VARBINARY</p>
</blockquote></td>
<td><blockquote>
<p>byte [ ]</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>VARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_VARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer - string of characters</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>WCHAR</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_WCHAR</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer - string of Unicode characters</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 27%" />
<col style="width: 41%" />
<col style="width: 31%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>WLONGVARCHAR</p>
</blockquote></th>
<th><blockquote>
<p>XO_TYPE_ WLONGVARCHAR</p>
</blockquote></th>
<th><blockquote>
<p>StringBuffer - string of Unicode characters</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>WVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>XO_TYPE_WVARCHAR</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer - string of Unicode characters</p>
</blockquote></td>
</tr>
</tbody>
</table>

> Following table details how an object is interpreted when returned
> from a method. The class of the object returned by the methods depends
> on the type of the column or the condition. For example, if the left
> condition value type is XO_TYPE_CHAR, then calling dam_getCondVal on
> that condition will return an object of type String.

#### How the methods for Java return a value as an object

> **Data type of the condition/column Class type of the returned
> object**
>
> XO_TYPE_BIGINT Long
>
> XO_TYPE_BINARY Byte \[ \]
>
> XO_TYPE_BIT Boolean
>
> XO_TYPE_CHAR String
>
> XO_TYPE_DATE xo_tm - access the fields xo_tm.YEAR, xo_tm.MONTH, and
> xo_tm.DAY_OF_MONTH using the getVal method.
>
> XO_TYPE_DOUBLE Double
>
> XO_TYPE_FLOAT Double
>
> XO_TYPE_INTEGER Integer
>
> XO_TYPE_LONGVARBINARY Byte \[ \]
>
> XO_TYPE_LONGVARCHAR String
>
> XO_TYPE_NUMERIC String
>
> XO_TYPE_REAL Float
>
> XO_TYPE_SMALLINT Short
>
> XO_TYPE_TIME xo_tm - access the fields xo_tm.HOUR, xo_tm.MINUTE,
> xo_tm.SECOND, and xo_tm.FRACTION using the getVal method.
>
> XO_TYPE_TIMESTAMP xo_tm - access the fields xo_tm.YEAR, xo_tm.MONTH,
> xo_tm.DAY_OF_MONTH, xo_tm.HOUR, xo_tm.MINUTE, xo_tm.SECOND, and
> xo_tm.FRACTION using the getVal method.
>
> XO_TYPE_TINYINT Byte \[ \]
>
> XO_TYPE_VARBINARY Byte \[ \]
>
> XO_TYPE_VARCHAR String
>
> XO_TYPE_WCHAR String
>
> XO_TYPE_WLONGVARCHAR String
>
> XO_TYPE_WVARCHAR String

### Helper classes

> The following table provides the Java classes that an IP uses to pass
> data to and from the OpenAccess SDK SQL engine methods for Java.

#### Helper classes

> **Class name Description**
>
> oa_ds_info Defines the name, number, value, and remarks field of a
> customized driver info property.
>
> oa_types_info Defines the information describing a data type supported
> by the data source.
>
> ResultBuffer Stores the bulk fetch data temporarily.
>
> scalar_functions Registers user-defined scalar functions.
>
> schemaobj_column Defines column objects and is used to access the
> search conditions when ipSchema is called for ip.DAMOBJ_TYPE_COLUMN.
>
> schemaobj_fkeys Defines foreign key column objects and is used to
> access the search conditions when ipSchema is called for
> ip.DAMOBJ_TYPE_FKEY.
>
> schemaobj_pkeys Defines primary key column objects and is used to
> access the search conditions when ipSchema is called for
> ip.DAMOBJ_TYPE_PKEY.
>
> schemaobj_proc Defines stored procedure objects and is used to access
> the search conditions when ipSchema is called for ip.DAMOBJ_TYPE_PROC.
>
> schemaobj_proccolumns Defines stored procedure column objects and is
> used to access the search conditions when ipSchema is called for
> ip.DAMOBJ_TYPE_PROCCOLUMNS.
>
> schemaobj_stat Defines index objects and is used to access the search
> conditions when ipSchema is called for ip.DAMOBJ_TYPE_STAT.
>
> schemaobj_table Defines table objects and is used to access the search
> conditions when ipSchema is called for ip.DAMOBJ_TYPE_TABLE.
>
> xo_int Allows the IP to return an integer value.
>
> xo_long Allows the IP to return an integer value.
>
> xo_short Allows the IP to return an integer value.
>
> xo_tm Allows the IP to exchange a timestamp value with the OpenAccess
> SDK SQL engine.
>
> xo_type Represents data type details about a column. An object of this
> type is returned by a call to dam_describeColDetail.

### See also

- [dam_describeColDetail](#_bookmark25)

### oa_ds_info class

> This class helps you return information about the data source to which
> the application is currently connected.

### setDSInfo method

> This method is used to set general information about a data source.
>
> public void setDSInfo(String infoName, int infoNum,
>
> int infoInt,
>
> long infoBitmask, String infoText, String remarks)
>
> **Parameters for setDSInfo when using oa_ds_info**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 32%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>infoName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The information type name</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>infoNum</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number associated with the information type</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>infoInt</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>An integer value of the information</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>infoBitmask</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>A bit value of the information</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>infoText</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>A string value of the information</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>remarks</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Any remarks that you would like to add</p>
</blockquote></td>
</tr>
</tbody>
</table>

### oa_ds_info Default constructor

> This is the default constructor for oa_ds_info that initializes all
> the class members to null or DAMOBJ_NOTSET.
>
> oa_ds_info()
>
> {
>
> this((String)null, ip.DAMOBJ_NOTSET, ip.DAMOBJ_NOTSET,
> (long)ip.DAMOBJ_NOTSET, (String)null, (String)null);
>
> }

### oa_ds_info overloaded constructor

> This overloaded constructor is used to create an oa_ds_info object
> that is used to customize the data source information.
>
> public oa_ds_info(String infoName, int infoNum, int infoInt, long
> infoBitmask, String infoText, String remarks)

#### Parameters for the overloaded constructor of oa_ds_info

> **Parameter Type Description**
>
> infoName String The information type name
>
> infoNum int The number associated with the information type
>
> infoInt int An integer value of the information
>
> infoBitmask long A bit value of the information
>
> infoText String A string value of the information
>
> remarks String Any remarks that you would like to add

### oa_types_info class

> This class is used to return information about data types supported by
> a data source. The information is in the form of an SQL result set.

### setTypesInfo method

> This method is used to set data types supported by a data source.
>
> public void setTypesInfo(String Typename, int Datatype,
>
> long OaPrecision, String LiteralPrefix, String LiteralSuffix, String
> CreateParams, int OaNullable,
>
> int OaCaseSensitive,
>
> int OaSearchable, int UnsignedAttrib, int OaMoney,
>
> int AutoIncrement, int MinimumScale, int MaximumScale,
>
> String LocaltypeName)
>
> **Parameters for setTypesInfo when using oa_types_info**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 28%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>Typename</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Data source-dependent data type name</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>Datatype</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The ODBC SQL data type</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaPrecision</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The maximum precision of the data type on the data source. NULL is
returned for data types where precision is not applicable.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>LiteralPrefix</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Characters used to prefix a literal. For data types where a literal
is not applicable, use NULL.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LiteralSuffix</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Characters used to terminate a literal. For data types where a
literal is not applicable, use NULL.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>CreateParams</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Parameters for data type definition.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaNullable</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Whether the data type can be null: XO_NO_NULLS (0) - No nulls
allowed</p>
<p>XO_NULLABLE (1) - Can be null XO_NULLABLE_UNKNOWN (2) -</p>
<p>Nullable unknown</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaCaseSensitive</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Whether the data type is case sensitive in collation and comparison.
TRUE if the data type is a character data type and it is case
sensitive.</p>
<p>FALSE if the data type is not a character data type or is not case
sensitive.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaSearchable</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>How the data type is used in a WHERE clause.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>UnsignedSttrib</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Whether the data type is unsigned. OpenAccess SDK does not support
unsigned data types.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaMoney</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Whether the data type is a money data type.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 30%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>AutoIncrement</p>
</blockquote></th>
<th><blockquote>
<p>int</p>
</blockquote></th>
<th><blockquote>
<p>Whether the data type is autoincrementing.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>MinimumScale</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td>Minimum scale of the data type.</td>
</tr>
<tr class="even">
<td><blockquote>
<p>maximumScale</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td>Maximum scale of the data type.</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LocaltypeName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Localized version of the database dependent data type name.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### oa_types_info default constructor

> This is the default constructor for oa_types_info that initializes all
> the class members to null or DAMOBJ_NOTSET.
>
> oa_types_info()
>
> {
>
> this((String)null, ip.DAMOBJ_NOTSET, (long)ip.DAMOBJ_NOTSET, null,
> null, null, ip.DAMOBJ_NOTSET, ip.DAMOBJ_NOTSET, ip.DAMOBJ_NOTSET,
> ip.DAMOBJ_NOTSET, ip.DAMOBJ_NOTSET, ip.DAMOBJ_NOTSET,
>
> ip.DAMOBJ_NOTSET, ip.DAMOBJ_NOTSET, (String)null);
>
> }

### oa_types_info overloaded constructor

> This overloaded constructor is used to create oa_types_info objects
> that are used to customize the information type.
>
> public oa_types_info(String Typename, int Datatype,
>
> long OaPrecision, String LiteralPrefix, String LiteralSuffix, String
> CreateParams, int OaNullable,
>
> int OaCaseSensitive, int OaSearchable, int UnsignedAttrib, int
> OaMoney,
>
> int AutoIncrement, int MinimumScale, int MaximumScale,
>
> String LocaltypeName)

#### Parameters for the overloaded constructor of oa_types_info

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 32%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><strong>Description</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>Typename</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Data source-dependent data type name</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 23%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>Datatype</p>
</blockquote></th>
<th><blockquote>
<p>int</p>
</blockquote></th>
<th><blockquote>
<p>The ODBC SQL data type</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>OaPrecision</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The maximum precision of the data type on the data source. NULL is
returned for data types where precision is not applicable.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>LiteralPrefix</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Characters used to prefix a literal. For data types where a literal
is not applicable, use NULL.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LiteralSuffix</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Characters used to terminate a literal. For data types where a
literal is not applicable, use NULL.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>CreateParams</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Parameters for data type definition.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaNullable</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Whether the data type can be null: XO_NO_NULLS (0) - No nulls
allowed</p>
<p>XO_NULLABLE (1) - Can be null XO_NULLABLE_UNKNOWN (2) -</p>
<p>Nullable unknown</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OaCaseSensitive</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Whether the data type is case sensitive in collation and comparison.
TRUE if the data type is a character data type and it is case
sensitive.</p>
<p>FALSE if the data type is not a character data type or is not case
sensitive.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaSearchable</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>How the data type is used in a WHERE clause.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>UnsignedSttrib</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Whether the data type is unsigned. OpenAccess SDK does not support
unsigned data types.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OaMoney</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Whether the data type is a money data type.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>AutoIncrement</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Whether the data type is autoincrementing.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>MinimumScale</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Minimum scale of the data type.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>maximumScale</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Maximum scale of the data type.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>LocaltypeName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Localized version of the database dependent data type name.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>ResultBuffer class</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This class manages the data present in the result buffer.

### putNull method

> This method is used to set a NULL value for a column in the buffer.
>
> putNull()

### putShort method

> This method is used to put a short value for a XO_TYPE_SHORT column in
> the buffer.
>
> putShort(sShortVal)

#### Parameters for putShort

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 26%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>sShortVal</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>putInt method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to put an integer value for a XO_TYPE_INTEGER
> column in the buffer.
>
> putInt(iIntVal)

#### Parameters for putInt

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 24%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>iIntVal</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>putBigInt method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to put an integer value for a XO_TYPE_BIGINT
> column in the buffer.
>
> putBigInt(lLongVal)
>
> **Parameters for putBigInt**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 32%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>lLongVal</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### putReal method

> This method is used to put a float value for a XO_TYPE_REAL column in
> the buffer.
>
> putReal(ffloatVal)

#### Parameters for putReal

<table>
<colgroup>
<col style="width: 31%" />
<col style="width: 23%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>ffloatVal</p>
</blockquote></td>
<td><blockquote>
<p>float</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>putDouble method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to put a double value for a XO_TYPE_DOUBLE column
> in the buffer.
>
> putDouble(dDoubleVal)

#### Parameters for putDouble

<table style="width:100%;">
<colgroup>
<col style="width: 30%" />
<col style="width: 25%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>dDoubleVal</p>
</blockquote></td>
<td><blockquote>
<p>double</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>putString method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to put a String value for a XO_TYPE\_(W)CHAR or
> XO_TYPE\_(W)VARCHAR column in the buffer.
>
> putString(strVal)

#### Parameters for putString

<table>
<colgroup>
<col style="width: 34%" />
<col style="width: 21%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>strVal</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>putLongString method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to put a long String value for a
> XO_TYPE\_(W)LONGVARCHAR column in the buffer.
>
> putLongString(strVal)

#### Parameters for putLongString

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 25%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>strVal</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>putDate method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to put a date value for a XO_TYPE_DATE column in
> the buffer.
>
> putDate(dateVal)

#### Parameters for putDate

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 25%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>dateVal</p>
</blockquote></td>
<td><blockquote>
<p>xo_tm</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>putTime method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to put a time value for a XO_TYPE_TIME column in
> the buffer.
>
> putTime(timeVal)

#### Parameters for putTime

<table>
<colgroup>
<col style="width: 34%" />
<col style="width: 21%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>timeVal</p>
</blockquote></td>
<td><blockquote>
<p>xo_tm</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>putTimeStamp method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to put a time stamp value for a XO_TYPE_TIMESTAMP
> column in the buffer.
>
> putTimeStamp(timeStampVal)
>
> **Parameters for putTimeStamp**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 31%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>timeStampVal</p>
</blockquote></td>
<td><blockquote>
<p>xo_tm</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### putBinary method

> This method is used to put a binary value for a XO_TYPE_BINARY and
> XO_TYPE_VARBINARY columns in the buffer.
>
> putBinary(binaryVal)

#### Parameters for putBinary

<table>
<colgroup>
<col style="width: 34%" />
<col style="width: 20%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>binaryVal</p>
</blockquote></td>
<td><blockquote>
<p>byte []</p>
</blockquote></td>
<td><blockquote>
<p>The value of the table column for a row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>putLongBinary method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to put a binary value for a XO_TYPE_LONGVARBINARY
> column in the buffer.
>
> putLongBinary(binaryVal)

#### Parameters for putLongBinary

> **Parameter Type Description**
>
> binaryVal byte \[\] The value of the table column for a row.

### setColumnType method

> This method is used to set the column type in the result buffer, that
> is, DAM_COL_IN_USE, DAM_COL_IN_RESULT, and so on.
>
> setColumnType(iColumnType)
>
> **Parameters for setColumnType**

<table>
<colgroup>
<col style="width: 27%" />
<col style="width: 36%" />
<col style="width: 35%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>iColumnType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The column type</p>
</blockquote></td>
</tr>
</tbody>
</table>

### setCheckIsTargetRow method

> This method is used to check whether to call dam_IsTargetRow.
>
> setCheckIsTargetRow(bCheckIsTargetRow)
>
> **Parameters for setCheckIsTargetRow**

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 29%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>bCheckIsTargetRow</p>
</blockquote></td>
<td><blockquote>
<p>boolean</p>
</blockquote></td>
<td><blockquote>
<p>TRUE- to call dam_IsTargetRow. Otherwise, FALSE.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### setNoRowsInBuffer method

> This method is used to set the number of rows in ResultBuffer.
>
> setNoRowsInBuffer(iNoRowsInBuffer)
>
> **Parameters for setNoRowsInBuffer**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 29%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>iNoRowsInBuffer</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number of rows in ResultBuffer.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### getErrorIndex method

> This method is used to get the row index at which the error occurred
> in ResultBuffer.
>
> public int getErrorIndex()

#### Parameters for getErrorIndex

> **Parameter Type Description Return**
>
> int The row index at which the error
>
> occurred.

### Pass-Through Mode methods

> You can use the following methods in the Pass-Through mode.

### setResultColumnType method

> This method sets the column type in the result buffer.
> setResultColumnType(bResColumnType) **Parameters for
> setResultColumnType**

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 34%" />
<col style="width: 35%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameters</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>bResColumnType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The column type.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### setNoOfResColumns method

> This method sets the number of result columns in the result buffer.
>
> setNoOfResColumns(iNoOfResColumns)
>
> **Parameters for setNoOfResColumns**

<table>
<colgroup>
<col style="width: 27%" />
<col style="width: 31%" />
<col style="width: 41%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameters</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>iNoOfResColumns</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number of columns in ResultBuffer.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### setColumnXoTypes method

> This method sets the column XO type in the result buffer.
>
> setColumnXoTypes(colXOTypes)
>
> **parameters for setColumnXoTypes**

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 35%" />
<col style="width: 38%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameters</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>colXOTypes</p>
</blockquote></td>
<td><blockquote>
<p>int[]</p>
</blockquote></td>
<td><blockquote>
<p>The column XO type.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### schemaobj_column class

> This class is used to define column information. Use the method
> SetObjInfo to set values for this object.

### SetObjInfo method

> This method is used to set values for an object to return schema
> information about columns. All values should be set. Set Null values
> as follows:

- For a string, set the value to null.

- For an integer, set the value to DAMOBJ_NOTSET.

> Refer to [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> in the *OpenAccess SDK Programmer\'s Guide* for information about each
> of the values to set.
>
> void SetObjInfo(
>
> String table_qualifier, String table_owner, String table_name, String
> column_name, short data_type, String type_name,
>
> int char_max_length, int numeric_precision,
>
> short numeric_precision_radix, short numeric_scale,
>
> short nullable, short scope, String userdata,
>
> String operator_support, short pseudo_column, short column_type,
> String remarks)
>
> **Parameters for SetObjInfo When Using schemaobj_column**

<table>
<colgroup>
<col style="width: 27%" />
<col style="width: 26%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>table_qualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the database in which the table is created.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>table_owner</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>table_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>column_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the column of the specified table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>data_type</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The data type of this column.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>type_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The X/Open column type.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>char_max_length</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The length in bytes of data transferred to the client in its default
format.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>numeric_precision</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The maximum number of digits used by the data in the column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>numeric_precision_radix</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Reserved for future use.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>numeric_scale</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The total number of digits to the right of the decimal point.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>nullable</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Specifies whether the column can be null.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>scope</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Scope of the SQL_BEST_ROWID column.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 24%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>userdata</p>
</blockquote></th>
<th><blockquote>
<p>String</p>
</blockquote></th>
<th><blockquote>
<p>Any proprietary data about the column that you want the IP to access.
The IP calls dam_describeColDetail to get this information.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>operator_support</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>A place for the IP to maintain which operators it supports for
conditions on this column (for example, =, &gt;, LIKE), when this column
is considered for a restriction or a search condition.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pseudo_column</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Indicates whether the column is a pseudo column such as Oracle
ROWID.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>column_type</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Defines the column type.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>remarks</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The description of the column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#_bookmark25">dam_describeColDetail</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### getColumnName method

> Obtains the column name value from the schemaobj_column object. Use
> this method to retrieve the column name value from a search object.
>
> public String getColumnName()

#### Parameters for getColumnName

> **Parameter Type Description RETURN**
>
> String Column name. A null value if a value is not specified.

### getTableName method

> Obtains the table name value from the schemaobj_column object. Use
> this method to retrieve the table name value from a search object.
>
> public String getTableName()

#### Parameters for getTableName

> **Parameter Type Description RETURN**
>
> String The table name. A null value if a value is not specified.

### getTableObj method

> Obtains the handle to the table object from the schemaobj_column
> object. Use this method to retrieve the table object handle from a
> search object.
>
> public long getTableObj()

#### Parameters for getTableObj with the schemaobj_column Object

> **Parameter Type Description RETURN**
>
> long The handle to the table object. A null value if called for a
> table function.

### getTableOwner method

> Obtains the table owner value from the schemaobj_column object. Use
> this method to retrieve the table owner value from a search object.
>
> public String getTableOwner()

#### Parameters for getTableOwner

> **Parameter Type Description RETURN**
>
> String Table owner. A null value if a value is not specified.

### getTableQualifier method

> Obtains the table qualifier value from the schemaobj_column object.
> Use this method to retrieve the table qualifier value from a search
> object.
>
> public String getTableQualifier()

#### Parameters for getTableQualifier

> **Parameter Type Description RETURN**
>
> String Table qualifier. This is a null value if a value is not
> specified.

### schemaobj_fkey class

> This class is used to define primary and foreign key schema
> information. Use the SetObjInfo method to set values for this object.

### SetObjInfo method

> This method is used to return schema information about primary and
> foreign keys.
>
> Refer to [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> in the *OpenAccess SDK Programmer\'s Guide* for information about each
> of the values to set.
>
> void SetObjInfo(
>
> String pktable_qualifier, String pktable_owner, String pktable_name,
> String pkcolumn_name, String fktable_qualifier, String fktable_owner,
> String fktable_name, String fkcolumn_name, short key_seq,
>
> short update_rule, short delete_rule, String fk_name, String pk_name)
>
> **Parameters for SetObjInfo When Using schemaobj_fkey**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 29%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pktable_qualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the database that</p>
</blockquote></td>
</tr>
<tr class="even">
<td colspan="2" rowspan="7"></td>
<td><blockquote>
<p>contains the primary key table. This</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>parameter can be used to distribute</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>tables into physically different</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>databases. Typically, specify</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SCHEMA. To expose this field as an</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>empty value and return a 0 when</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>OAIP_getInfo(SQL_MAX_QUALIFIE</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th></th>
<th></th>
<th><blockquote>
<p>R_NAME) is called, set this field value to null.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pktable_owner</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the primary key table. Set the value to SYSTEM or USER.
SYSTEM - the table is managed by the OpenAccess SDK SQL engine. OAUSER -
the table is managed by the IP.</p>
<p>The value for this parameter and OA_COLUMNS must match.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pktable_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the primary key table (how the end user refers to
it).</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pkcolumn_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the primary key column.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>fktable_qualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the database that contains the foreign key table. This
parameter can be used to distribute tables into physically different
databases. Typically, specify SCHEMA.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>fktable_owner</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the foreign key table. Set the value to SYSTEM or USER.
SYSTEM - the table is managed by the OpenAccess SDK SQL engine. OAUSER -
the table is managed by the IP.</p>
<p>The value for this parameter and OA_COLUMNS must match.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>fktable_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the foreign key table (how the end user refers to
it).</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>fkcolumn_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the foreign key column.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>key_seq</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The column sequence number in key, starting with 1.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>delete_rule</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Not used. Set to DAMOBJ_NOTSET.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>fk_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The foreign key identifier.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pk_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The primary key identifier.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
</tbody>
</table>

### getFKColumnName method

> Obtains the column name value from the schemaobj_fkey object. Use this
> method to retrieve the column name value from a search object.
>
> public String getColumnName()

#### Parameters for getFKColumnName with the schemaobj_fkey Object

> **Parameter Type Description RETURN**
>
> String The column name. A null value if a value is not specified.

### getFKTableName method

> Obtains the table name value from the schemaobj_fkey object. Use this
> method to retrieve the table name value from a search object.
>
> public String getFKTableName()

#### Parameters for getFKTableName with the schemaobj_fkey Object

> **Parameter Type Description RETURN**
>
> String The table name. A null value if a value is not specified.

### getFKTableOwner method

> Obtains the table owner value from the schemaobj_fkey object. Use this
> method to retrieve the table owner value from a search object.
>
> public String getFKTableOwner()

#### Parameters for getFKTableOwner with the schemaobj_fkey Object

> **Parameter Type Description RETURN**
>
> String The table owner. A null value if a value is not specified.

### getFKTableQualifier method

> Obtains the table qualifier value from the schemaobj_fkey object. Use
> this method to retrieve the table qualifier value from a search
> object.
>
> public String getFKTableQualifier()

#### Parameters for getFKTableQualifier with the schemaobj_fkey Object

> **Parameter Type Description RETURN**
>
> String Table qualifier. A null value if a value is not specified.

### getPKColumnName method

> Obtains the column name value from the schemaobj_fkey object. Use this
> method to retrieve the column name value from a search object.
>
> public String getColumnName()

#### Parameters for getPKColumnName with the schemaobj_fkey Object

> **Parameter Type Description RETURN**
>
> String Column name. A null value if a value is not specified.

### getPKTableName method

> Obtains the table name value from the schemaobj_fkey object. Use this
> method to retrieve the table name value from a search object.
>
> public String getPKTableName()

#### Parameters for getPKTableName with the schemaobj_fkey Object

> **Parameter Type Description RETURN**
>
> String The table name. A null value if a value is not specified.

### getPKTableOwner method

> Obtains the table owner value from the schemaobj_fkey object. Use this
> method to retrieve the table owner value from a search object.
>
> public String getPKTableOwner()

#### Parameters for getPKTableOwner with the schemaobj_fkey Object

> **Parameter Type Description RETURN**
>
> String The table owner. A null value if a value is not specified.

### getPKTableQualifier method

> Obtains the table qualifier value from the schemaobj_fkey object. Use
> this method to retrieve the table qualifier value from a search
> object.
>
> public String getPKTableQualifier()

#### Parameters for getPKTableQualifier with the schemaobj_fkey Object

> **Parameter Type Description RETURN**
>
> String The table qualifier. A null value if a value is not specified.

### schemaobj_pkey class

> This class is used to define primary key schema information. Use the
> SetObjInfo method to set values for this object.
>
> All values should be set. Set Null values as follows:

- For a string, set the value to null.

- For an integer, set the value to DAMOBJ_NOTSET.

> Refer to [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> in the *OpenAccess SDK Programmer\'s Guide* for information about each
> of the values to set.
>
> void SetObjInfo(
>
> String pktable_qualifier, String pktable_owner, String pktable_name,
> String pkcolumn_name, short key_seq,
>
> String pk_name)

#### Parameters for SetObjInfo When Using schemaobj_pkey

#### 

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 21%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pktable_qualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the database that contains the primary key table. Can be
used to distribute tables into physically different databases. In most
cases, specify SCHEMA. If you want to expose this field as an empty
value, then return a 0 when OAIP_getInfo(SQL_MAX_QUALIFIE R_NAME) is
called and set this field value to null.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pktable_owner</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the primary key table. Set the value to SYSTEM or USER.
SYSTEM - the table is managed by the OpenAccess SDK SQL engine. OAUSER -
the table is managed by the IP.</p>
<p>The value for this parameter and OA_COLUMNS must match.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pktable_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the primary key table. This is how the end user refers to
it.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pkcolumn_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the primary key column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>key_seq</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The column sequence number in key, starting with 1.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pk_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The primary key identifier.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>schemaobj_proc class</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This class is used to define stored procedure schema information. Use
> the SetObjInfo method to set values for this object.

### SetObjInfo method

> This method is used to return schema information about stored
> procedures. All values should be set. Set Null values as follows:

- For a string, set the value to null.

- For an integer, set the value to DAMOBJ_NOTSET.

> Refer to [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> in the *OpenAccess SDK Programmer\'s Guide* for information about each
> of the values to set.
>
> void SetObjInfo(
>
> String proc_qualifier, String proc_owner, String proc_name,
>
> int num_input_params, int num_output_params, int num_result_sets,
> short proc_type,
>
> String userdata, String remarks)

#### Parameters for SetObjInfo when using schemaobj_proc

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 23%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>proc_qualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the database in which the stored procedure exists.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>proc_owner</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the stored procedure.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>proc_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the stored procedure.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>num_input_params</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number of input parameters. Not used at this time.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>num_output_params</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number of output parameters. Not used at this time.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>num_result_sets</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number of result sets.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>proc_type</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The type of the stored procedure.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>userdata</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>A place for the IP to maintain IP- specific data. The IP can read it
by using dam_describeProcedure.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>remarks</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The description of the stored procedure.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_describeprocedure">dam_describeProcedure</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>getOwner method</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> Obtains the procedure owner value from the schemaobj_proc object. Use
> this method to retrieve the procedure owner value from a search
> object.
>
> public String getOwner()

#### Parameters for getOwner with the schemaobj_proc Object

> **Parameter Type Description RETURN**
>
> String The procedure owner. A null value if a value is not specified.

### getProcName method

> Obtains the procedure name value from the schemaobj_proc object. Use
> this method to retrieve the procedure name value from a search object.
>
> public String getProcName()

#### Parameters for getProcName with the schemaobj_proc Object

> **Parameter Type Description RETURN**
>
> String The procedure name. A null value if a value is not specified.

### getQualifier method

> Obtains the procedure qualifier value from the schemaobj_proc object.
> Use this method to retrieve the procedure qualifier value from a
> search object.
>
> public String getQualifier()

#### Parameters for getQualifier with the schemaobj_proc Object

> **Parameter Type Description RETURN**
>
> String The procedure qualifier. A null value if a value is not
> specified.

### schemaobj_proccolumn class

> This class is used to define schema information for stored procedure
> columns. Use the SetObjInfo method to set values for this object.

### SetObjInfo method

> This method is used to return schema information about procedure
> columns. All values should be set. Set Null values as follows:

- For a string, set the value to null to null.

- For an integer, set the value to DAMOBJ_NOTSET.

> Refer to [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> in the *OpenAccess SDK Programmer\'s Guide* for information about each
> of the values to set.
>
> void SetObjInfo( String qualifier, String owner, String name,
>
> String column_name, short column_type, short data_type, String
> type_name, int precision,
>
> int length, short scale, short radix, short nullable, String userdata,
> String remarks)
>
> **Parameters for SetObjInfo When Using schemaobj_proccolumn**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 30%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>qualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the database in which the table is created.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>owner</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>column_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the column of the specified table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>column_type</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Whether the procedure column is a parameter or a result set
column.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>data_type</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The data type of the column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>type_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The X/Open data type name.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 24%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>precision</p>
</blockquote></th>
<th><blockquote>
<p>int</p>
</blockquote></th>
<th><blockquote>
<p>The number of digits of mantissa precision.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>length</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The maximum length for character data types.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>scale</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The number of significant digits to the right of the decimal
point.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>radix</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Reserved for future use.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>nullable</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Whether the column can be null.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>userdata</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Any proprietary data about the column that the IP wants to access.
The IP uses dam_describeColDetail to get this information.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>remarks</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The description of this procedure column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#_bookmark25">dam_describeColDetail</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### getColumnName method

> Obtains the column name value from the schemaobj_proccolumn object.
> Use this method to retrieve the column name value from a search
> object.
>
> public String getColumnName()

#### Parameters for getColumnName

> **Parameter Type Description RETURN**
>
> String Procedure column name. A null value if a value is not
> specified.

### getOwner method

> Obtains the procedure owner value from the schemaobj_proccolumn
> object. Use this method to retrieve the procedure owner value from a
> search object.
>
> public String getOwner()

#### Parameters for getOwner

> **Parameter Type Description RETURN**
>
> String The procedure owner. A null value if a value is not specified.

### getProcName method

> Obtains the procedure name value from the schemaobj_proccolumn object.
> Use this method to retrieve the procedure name value from a search
> object.
>
> public String getProcName()

#### Parameters for getProcName

> **Parameter Type Description RETURN**
>
> String The procedure name. A null value if a value is not specified.

### getQualifier method

> Obtains the procedure qualifier value from the schemaobj_proccolumn
> object. Use this method to retrieve the procedure qualifier value from
> a search object.
>
> public String getQualifier()

#### Parameters for getQualifier

> **Parameter Type Description RETURN**
>
> String The procedure qualifier. A null value if a value is not
> specified.

### schemaobj_stat class

> This class is used to define table indexes and table statistics schema
> information. Use the SetObjInfo method to set values for this object.

### SetObjInfo method

> This method is used to return schema information about table indexes
> and table statistics. All values should be set. Set Null values as
> follows:

- For a string, set the value to null.

- For an integer, set the value to DAMOBJ_NOTSET.

> Refer to [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> in the *OpenAccess SDK Programmer\'s Guide* for information about each
> of the values to set.
>
> void SetObjInfo(
>
> String table_qualifier, String table_owner, String table_name, short
> non_unique, String index_qualifier, String index_name, short type,
>
> short seq_in_index, String column_name, String collation, long
> cardinality, int pages,
>
> String filter_conditions)

#### Parameters for SetObjInfo When Using schemaobj_stat

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 30%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>table_qualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the database in which the table resides. It can be used
to distribute tables into physically different databases. Specify SCHEMA
for most scenarios. The entry here is the same as you use in OA_COLUMNS
and OA_TABLES.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>table_owner</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the table. You would normally set it to system or
user.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>table_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the table. This is how the end user refers to it.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>non_unique</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>Indicates whether the index prohibits duplicate values:</p>
<p>TRUE (1) - the index values can be non-unique.</p>
<p>FALSE (0) - the index values must be</p>
<p>unique.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 30%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th></th>
<th></th>
<th><blockquote>
<p>NULL (empty) - if type is SQL_TABLE_STAT.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>index_qualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the database to which the table belongs.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>index_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the index. This value must be NULL if type is
SQL_TABLE_STAT.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>type</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The type of information returned. Specify 1, 2, or 3 as the value for
Microsoft Access to use the indexes. Valid values are:</p>
<p>SQL_TABLE_STAT (0) - a statistic for the table.</p>
<p>SQL_INDEX_CLUSTERED (1) - a</p>
<p>clustered index. SQL_INDEX_HASHED (2) - a hashed index.</p>
<p>SQL_INDEX_OTHER (3) - another type of index.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>seq_in_index</p>
</blockquote></td>
<td><blockquote>
<p>short</p>
</blockquote></td>
<td><blockquote>
<p>The column sequence number in the index, starting with 1. Set to NULL
if type is SQL_TABLE_STAT. This value is 1 if only one column defines
the index.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>column_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The column identifier. Set to NULL if type is SQL_TABLE_STAT.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>collation</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The collation sequence. Valid values are:</p>
<p>A - ascending D - descending</p>
<p>NULL - if type is SQL_TABLE_STAT or if no collation occurs</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>cardinality</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The cardinality of the table or index. Used by the OpenAccess SDKSQL
engine to use an optimal index if this is not a unique index. Valid
values are:</p>
<p>The number of rows in the table if type is SQL_TABLE_STAT.</p>
<p>The number of unique values in the index if type is not
SQL_TABLE_STAT. High cardinality indexes are preferred for query
optimization.</p>
<p>NULL if the value is not available from the data source.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pages</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Reserved for future use. Set to</p>
</blockquote></td>
</tr>
</tbody>
</table>

> DAMOBJ_NOTSET.
>
> filter_conditions String Reserved for future use. Set to null.
>
> **RETURN**

NONE

### getTableName method

> Obtains the table name value from the schemaobj_stat object. Use this
> method to retrieve the table name value from a search object.
>
> public String getTableName()

#### Parameters for getTableName with the schemaobj_stat Object

> **Parameter Type Description RETURN**
>
> String The table name. A null value if a value is not specified.

### getTableObj method

> Obtains the handle to the table object from the schemaobj_stat object.
> Use this method to retrieve the table object handle from a search
> object.
>
> public long getTableObj()

#### Parameters for getTableObj with the schemaobj_stat Object

> **Parameter Type Description RETURN**
>
> Long The handle to the table object. A null value if called for a
> table function.

### getTableOwner method

> Obtains the table owner value from the schemaobj_stat object. Use this
> method to retrieve the table owner value from a search object.
>
> public String getTableOwner()

#### Parameters for getTableOwner with the schemaobj_stat Object

#### 

> **Parameter Type Description RETURN**
>
> String Table owner. A null value if a value is not specified.

### getTableQualifier method

> Obtains the table qualifier value from the schemaobj_stat object. Use
> this method to retrieve the table qualifier value from a search
> object.
>
> public String getTableQualifier()

#### Parameters for getTableQualifier with the schemaobj_stat Object

> **Parameter Type Description RETURN**
>
> String The table qualifier. A null value if a value is not specified.

### schemaobj_table class

> This class is used to define tables schema information. Use the method
> SetObjInfo to set values for this object.

### SetObjInfo method

> This method is used to return schema information about tables. All
> values should be set. Set Null values as follows:

- For a string, set the value to null.

- For an integer, set the value to DAMOBJ_NOTSET.

> Refer to [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> in *OpenAccess SDK Programmer\'s Guide* for details about each of the
> values to set.
>
> void SetObjInfo(
>
> String table_qualifier, String table_owner, String table_name, String
> table_type, String table_path, String table_userdata, String
> function_support,
>
> String remarks)
>
> **Parameters for SetObjInfo When Using schemaobj_table**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 30%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>table_qualifier</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the database in which the table falls. Can be used to
distribute tables into physically different databases. You would
normally specify SCHEMA. If you want to expose this field as an empty
value, then return 0 when ipGetInfo(SQL_MAX_QUALIFIER_N AME) is called
and set this field value to NULL.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>table_owner</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the table. You would normally set it to SYSTEM or USER.
SYSTEM - the table is managed by the OpenAccess SDK SQL engine. OAUSER -
the table is managed by the IP.</p>
<p>The value for this parameter and OA_COLUMNS must match.</p>
<p>If you want to expose this field as an empty value, then return 0
when ip_GetInfo(SQL_MAX_OWNER_NAM E) is called and set this field value
to NULL.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>table_name</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the table. This is how the end user refers to it.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>table_type</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>Affects how the table is handled. Set to:</p>
<p>SYSTEM TABLE - This table is managed by the CSV IP.</p>
<p>TABLE - The table is managed by the IP.</p>
<p>TABLE FUNCTION - The table function is managed by the IP.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>table_path</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The full pathname of the file holding this table (if any). The file
name by itself defaults to the current directory. You can use the
relative path specifier to define a file name relative to the schema
path as defined in the DataSourceIPSchemaPath data source attribute.</p>
<p>Place a single period (.) to indicate</p>
<p>the schema directory.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 26%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2" rowspan="2"></th>
<th><blockquote>
<p>Place a forward or backward slash and follow with the remainder of
the name. For example:./mem_db/</p>
<p>test.dbf expands to</p>
</blockquote></th>
</tr>
<tr class="odd">
<th><blockquote>
<p>{schema_path}/mem_db/ test.dbf where schema_path is as defined in the
DataSourceIPPath data source attribute.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>table_userdata</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>A place for the IP to maintain IP- specific data. The IP can read it
by using the dam_describeTable function.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>function_support</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>A place for the IP to maintain the functionality it supports for the
table (SELECT, INSERT and so on). The OpenAccess SDK SQL engine allows a
user to perform only these operations for the table.</p>
<p>This value is used if the GETSUPPORT function returns false for that
capability.</p>
<p>The value in this field is specified as a hex number with the 0x
convention. It is bitwise OR of the following bit masks:
IP_TABLE_SUPPORT_SELECT - 0x01</p>
<p>IP_TABLE_SUPPORT_INSERT - 0x02 IP_TABLE_SUPPORT_UPDATE - 0x04
IP_TABLE_SUPPORT_DELETE - 0x08 IP_TABLE_SUPPORT_SELECT_FO R_UPDATE -
0x10</p>
<p>For example, set this field to '0x1F' to indicate full support. This
number is the result of 0x01 | 0x02 | 0x04 | 0x08</p>
<p>| 0x10 where the '|' indicates bitwise OR.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>remarks</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The description of the table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#ipgetinfo">ipGetInfo</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#_bookmark34">dam_describeTable</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### getTableName method

> Obtains the table name value from the schemaobj_table object. Use this
> method to retrieve the table name value from a search object.
>
> public String getTableName()

#### Parameters for getTableName with the schemaobj_table Object

> **Parameter Type Description RETURN**
>
> String The table name. A null value if a value is not specified.

### getTableOwner method

> Obtains the table owner value from the schemaobj_table object. Use
> this method to retrieve the table owner value from a search object.
>
> public String getTableOwner()

#### Parameters for getTableOwner with the schemaobj_table Object

> **Parameter Type Description RETURN**
>
> String The table owner. A null value if a value is not specified.

### getTableQualifier method

> Obtains the table qualifier value from the schemaobj_table object. Use
> this method to retrieve the table qualifier value from a search
> object.
>
> public String getTableQualifier()

#### Parameters for getTableQualifier with the schemaobj_table Object

> **Parameter Type Description RETURN**
>
> String The table qualifier. A null value if a value is not specified.

### xo_tm class

> This class is used to pass time, date, and timestamp values between
> the IP and the OpenAccess SDK. The xo_tm class consists of integer
> fields for storing the different parts of a timestamp value. Different
> setVal methods are provided to allow you to set the values one field
> at a time, or with complete data, time, or timestamp values.

### getVal method

> Get the value of the specified field in the xo_tm object.
>
> int getVal(int field)

#### Parameters for getVal

> **Parameter Type Description INPUT**
>
> field int The field to get. See [Using
> the](#using-the-setval-method-with-a-selected-field)
>
> [setVal method with a
> selected](#using-the-setval-method-with-a-selected-field)
> [field](#using-the-setval-method-with-a-selected-field) for the field
> identifier constants.
>
> **RETURN**
>
> int The value of the specified field.

### Using the setVal method with a selected field

> Sets the selected field\'s value.
>
> void setVal( int field, int value)
>
> **Parameters for setVal**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>field</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The field to set. See Table <a
href="#using-the-setval-method-with-a-selected-field">xo_tm</a> <a
href="#using-the-setval-method-with-a-selected-field">Field Identifier
Constants</a> for the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>field identifier constants.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>value</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The value to set. Must be in range as</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>specified in Table <a
href="#using-the-setval-method-with-a-selected-field">xo_tm
Field</a></p>
<p><a href="#using-the-setval-method-with-a-selected-field">Identifier
Constants</a>.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 35%" />
<col style="width: 12%" />
<col style="width: 51%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></th>
<th></th>
<th rowspan="3"></th>
</tr>
<tr class="odd">
<th></th>
<th><blockquote>
<p>NONE</p>
</blockquote></th>
</tr>
<tr class="header">
<th><blockquote>
<p><strong>xo_tm Field Identifier Constants</strong></p>
</blockquote></th>
<th></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>Field Identifier Constant</strong></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><strong>Description</strong></p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SECOND</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Seconds after the minute - [0,59].</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>MINUTE</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Minutes after the hour - [0,59].</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>HOUR</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Hours since midnight - [0,23].</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>DAY_OF_MONTH</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Day of the month - [1,31].</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>MONTH</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Months since January - [0,11].</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>YEAR</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Years after 0 A.D.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>DAY_OF_WEEK</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Days since Sunday - [0,6].</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>DAY_OF_YEAR</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Days since January 1 - [0,365].</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>IS_DST</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Daylight savings time flag.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>FRACTION</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Number of billionths of a second and ranges from 0 - 999999999).</p>
</blockquote></td>
</tr>
</tbody>
</table>

### Using the setVal method to set a date

> Use this method to set a value for XO_TYPE_DATE column.
>
> void setVal( int year, int month,
>
> int day_of_month)
>
> **Parameters for setVal When Setting a Date**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 32%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>IN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>year</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Years after 0 A.D.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>month</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Months since January - [0,11].</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>day_of_month</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Day of the month - [1,31].</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
</tbody>
</table>

### Using the setVal method to set a time

> Use this method to set a value for XO_TYPE_TIME column.
>
> void setVal( int hour, int minute, int second,
>
> int fraction)
>
> **Parameters for setVal When Setting Time**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 33%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hour</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Hours since midnight - [0,23].</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>minute</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Minutes after the hour - [0,59].</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>second</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Seconds after the minute - [0,59].</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>fraction</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Not used. Set to 0.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
</tbody>
</table>

### Using setVal to Set a Timestamp

> Use this method to set a value for XO_TYPE_TIMESTAMP column.
>
> void setVal( int year, int month,
>
> int day_of_month, int hour,
>
> int minute, int second, int fraction)

#### Parameters for setVal When Setting a Timestamp

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 31%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>year</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Years after 0 A.D.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>month</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Months since January - [0,11].</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>day_of_month</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Day of the month - [1,31].</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 28%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>hour minute</p>
<p>second</p>
</blockquote></th>
<th><blockquote>
<p>int int</p>
<p>int</p>
</blockquote></th>
<th><blockquote>
<p>Hours since midnight - [0,23]. Minutes after the hour - [0,59].</p>
<p>Seconds after the minute - [0,59].</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>fraction</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Number of billionths of a second and ranges from 0 - 999999999).</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>xo_int class</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This class is used to pass integer values between the IP and the
> OpenAccess SDK.

### xo_int

> Construct an object of type xo_int with specified value.
>
> xo_int(int Val)

#### Parameters for xo_int

> **Parameter Type Description INPUT**
>
> Val int The value to set.
>
> **RETURN**

NONE

### getVal method

> Get the integer value from the xo_int object.
>
> int getVal()
>
> **Parameters for get_val**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 33%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>RETURN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Value held in the xo_int object.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### xo_long class

> The xo_long class is used to pass long values between the IP and the
> OpenAccess SDK.

### xo_long

> Construct an object of type xo_long with a specified value.
>
> xo_long(long Val)

#### Parameters for xo_long

> **Parameter Type Description INPUT**
>
> Val long Value to set.
>
> **RETURN**

None

### getVal method

> Gets the long value from the xo_long object.
>
> long getVal()

#### Parameters for getVal

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 26%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>RETURN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>xo_short class</strong></p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Value held in the xo_long object.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> This class is used to pass short integer values between the IP and the
> OpenAccess SDK.

### xo_short

> Construct an object of type xo_short with a specified value.
>
> xo_short(short Val)

#### Parameters for xo_short

> **Parameter Type Description INPUT**
>
> Val short The value to set.
>
> **RETURN**

NONE

### getVal method

> Get the short integer value from the xo_short object.
>
> short getVal()

#### Parameters for getVal

> **Parameter Type Description RETURN**
>
> short Value held in the xo_short object.

### xo_type class

> This class is used to manage detailed information about a column data
> type.

### xo_type

> This constructor method is used to create a detailed column data type.
> Refer to [Schema Definition
> and](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> [Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> in the *OpenAccess SDK Programmer\'s Guide* for information about each
> of the values to set.
>
> xo_type( int type,
>
> int length, int prec, int radix, int scale, int inull)

#### Parameters for xo_type

> **Parameter Type Description**

<table>
<colgroup>
<col style="width: 31%" />
<col style="width: 22%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>INPUT</strong></p>
<p>type</p>
</blockquote></th>
<th><blockquote>
<p>int</p>
</blockquote></th>
<th><blockquote>
<p>The data type as XO_TYPE_<em>xxx</em>.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>length</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The length of column data.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>prec</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The precision of column data.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>radix</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The radix for numeric data types.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>scale</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The scale of column data.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>inull</p>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td><blockquote>
<p>int</p>
<p>NONE</p>
</blockquote></td>
<td><blockquote>
<p>If column can have nulls.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><strong>getLength method</strong></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td>Get the length information.</td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td>int getLength()</td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><strong>Parameters for getLength</strong></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>Parameter RETURN</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Type</strong></p>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p><strong>Description</strong></p>
<p>Length</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><strong>getNull method</strong></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td>Get the null information.</td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td>int getNull()</td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><strong>Parameters for getNull</strong></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>Parameter RETURN</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Type</strong></p>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p><strong>Description</strong></p>
<p>Null information</p>
</blockquote></td>
</tr>
<tr class="even">
<td><strong>getPrecision method</strong></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td>Get the precision information.</td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> int getPrecision()

#### TParameters for getPrecision

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 23%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>RETURN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Precision information.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><strong>getRadix method</strong></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td>Get the radix information.</td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td>int getRadix()</td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><strong>Parameters for getRadix</strong></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>Parameter RETURN</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Type</strong></p>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p><strong>Description</strong></p>
<p>The radix information.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><strong>getScale method</strong></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td>Get the scale information.</td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td>int getScale()</td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><strong>Parameters for getScale</strong></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>Parameter RETURN</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Type</strong></p>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p><strong>Description</strong></p>
<p>Scale information.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><strong>getType method</strong></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td>Get the type information.</td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td>int getType()</td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><strong>Parameters for getType</strong></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>Parameter</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Type</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Description</strong></p>
</blockquote></td>
</tr>
</tbody>
</table>

> **RETURN**
>
> int Type information.

### scalar_function class

> This class is used to register user-defined scalar functions. This
> class has three constructors to support the different types of scalar
> functions that are supported. See the IP method
> [ipfuncxxx](#ipfuncxxx) for details on how to implement methods for
> scalar functions. See the IP method
> [ipGetScalarFunctions](#ipgetscalarfunctions) for details on
> registering scalar functions.

### Defining a Basic Scalar Function

> Use this method to define a scalar function with default values for
> precision, scale, and length for the result of the scalar function.
>
> scalar_function( String sName,
>
> int iConstantFunc, String pfScalarFunc, int iResXoType,
>
> int iNumInputParams)
>
> If you want to define a scalar function that has a qualifier, use the
> following syntax:
>
> scalar_function(
>
> String sQualifierName, String sName,
>
> int iConstantFunc, String pfScalarFunc, int iResXoType,
>
> int iNumInputParams)

#### Parameters for Basic Scalar Function

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>IN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>sQualifierName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the function qualifier.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>sName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the scalar function.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iConstantFunc</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Indicates whether the function has</p>
</blockquote></td>
</tr>
<tr class="even">
<td colspan="2" rowspan="6"></td>
<td><blockquote>
<p>constant output.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>1 - indicates that the function is a</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>constant scalar function and is</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>evaluated only once per query if all its</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>arguments are literal values.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>0 - indicates that the function is a</p>
</blockquote></td>
</tr>
</tbody>
</table>

> variable scalar function and therefore should be evaluated once per
> record during the dam_isTargetRow call, regardless of the input.
>
> pfScalarFunc String The method in the IP that is to be called to
> execute this scalar function.
>
> iResXoType int The data type of the result value of the scalar
> function.
>
> iNumInputParams int The number of arguments expected by the function.
>
> \>=0 to allow fixed argument list.
>
> \< 0 to allow variable argument list with a maximum of \|args\|.
>
> **RETURN**

NONE

### See also

- [dam_isTargetRow](#dam_istargetrow)

### Using scalar_function method to specify result details

> Use this method to define a scalar function with specific values for
> the precision, scale, and length of the result of the scalar function.
> The length, precision, and scale of the result of the scalar functions
> can be explicitly defined or set to DAM_NOT_SET to use the defaults.
> Default values are indicated in the description of the arguments in
> Table: [Operator Types for Conditions](#dam_describecond).
>
> For details on setting the length, precision, and scale, refer to the
> corresponding fields in the OA_COLUMNS table in [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> of the *OpenAccess SDK Programmer\'s Guide*.
>
> scalar_function( String sName,
>
> int iConstantFunc, String pfScalarFunc, int iResXoType,
>
> int iLength,
>
> int iPrecision,
>
> int iScale,
>
> int iNumInputParams)

#### Parameters for scalar_function When Specifying Result Details

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 33%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>sName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the scalar function.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 28%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>iConstantFunc</p>
</blockquote></th>
<th><blockquote>
<p>int</p>
</blockquote></th>
<th><blockquote>
<p>Indicates whether the function has constant output.</p>
<p>1 - indicates that the function is a constant scalar function and is
evaluated only once per query if all its arguments are literal
values.</p>
<p>0 - indicates that the function is a variable scalar function and
therefore should be evaluated once per record during the dam_isTargetRow
call, regardless of the input.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pfScalarFunc</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The method in the IP that is to be called to execute this scalar
function.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iResXoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The data type of the result value of the scalar function.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iLength</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The length of the result value of the scalar function. Set to
DAM_NOT_SET to accept the following default values:</p>
<p>CHAR, BINARY: 255</p>
<p>WVARCHAR: 16000</p>
<p>VARCHAR, VARBINARY: 8000 LONGVARCHAR, LONGVARBINARY: 1000000
WLONGVARCHAR: 2000000NUMERIC: 34</p>
<p>Other types: Default values as specified in OA_COLUMNS.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iPrecision</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The precision of the result value of the scalar function. Set to
DAM_NOT_SET to accept the following default values:</p>
<p>CHAR, WCHAR, BINARY: 255 VARCHAR, WVARCHAR, VARBINARY: 8000
LONGVARCHAR, WLONGVARCHAR, LONGVARBINARY: 1000000</p>
<p>NUMERIC: 32</p>
<p>Other types: Default values as specified in OA_COLUMNS.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iScale</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Scale of the result value of the scalar function.Set to DAM_NOT_SET
to accept the following default value: NUMERIC: 5</p>
<p>Other types: Default values as specified in OA_COLUMNS.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iNumInputParams</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number of arguments expected by the function.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> \>=0 to allow fixed argument list.
>
> \< 0 to allow variable argument list with a maximum of \|args\|.
>
> **RETURN**

NONE

### See also

- [dam_isTargetRow](#dam_istargetrow)

### Using the scalar_function method to specify result details and map to a column

> Use this method to define a scalar function with specific values for
> the precision, scale, and length of the result of the scalar function
> and to control whether the scalar function should be mapped to a
> column. The length, precision, and scale of the result of the scalar
> functions can be explicitly defined or set to DAM_NOT_SET to use the
> defaults. Default values are indicated in the description of the
> arguments in Table: [Operator Types for
> Conditions](#dam_describecond). For details on setting the length,
> precision, and scale, refer to the corresponding fields in the
> OA_COLUMNS table in [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> of the *OpenAccess SDK Programmer\'s Guide*.
>
> scalar_function( String sName,
>
> int iMapAsColumn, int iConstantFunc, String pfScalarFunc, int
> iResXoType,
>
> int iLength,
>
> int iPrecision,
>
> int iScale,
>
> int iNumInputParams)
>
> If you want to define a scalar function that has a qualifier, use the
> following syntax:
>
> scalar_function(
>
> String sQualifierName, String sName,
>
> int iMapAsColumn, int iConstantFunc, String pfScalarFunc, int
> iResXoType,
>
> int iLength,
>
> int iPrecision,
>
> int iScale,
>
> int iNumInputParams)

#### Parameters for scalar_function

> **Parameter Type Description**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th colspan="2"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>sQualifierName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the function qualifier.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>sName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the scalar function.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iMapAsColumn</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Map function as column. Set it to 0 for normal scalar functions.</p>
<p>1 - map the function as a column. 0 - map the function as a normal
scalar function.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iConstantFunc</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Indicates whether the function has constant output.</p>
<p>1 - the function is a constant scalar function and is evaluated only
once per query if all its arguments are literal values.</p>
<p>0 - the function is a variable scalar function and therefore should
be evaluated once per record during the dam_isTargetRow call, regardless
of the input.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pfScalarFunc</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The method in the IP that is to be called to execute this scalar
function.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iResXoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The data type of the result value of the scalar function.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iLength</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The length of the result value of the scalar function. Set to
DAM_NOT_SET to accept the following default values:</p>
<p>CHAR, BINARY: 255</p>
<p>WVARCHAR: 16000</p>
<p>VARCHAR, VARBINARY: 8000 LONGVARCHAR, LONGVARBINARY: 1000000</p>
<p>WLONGVARCHAR: 2000000</p>
<p>NUMERIC: 34</p>
<p>Other types: Default values as specified in OA_COLUMNS.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iPrecision</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The precision of the result value of the scalar function. Set to
DAM_NOT_Set to accept the following default values:</p>
<p>CHAR, WCHAR, BINARY: 255 VARCHAR, WVARCHAR, VARBINARY: 8000
LONGVARCHAR, WLONGVARCHAR, LONGVARBINARY: 1000000</p>
<p>NUMERIC: 32</p>
</blockquote></td>
</tr>
</tbody>
</table>

> Other types: Default values as specified in OA_COLUMNS.
>
> iScale int The scale of the result value of the scalar function.
>
> Set to DAM_NOT_SET to accept the following default value:
>
> NUMERIC: 5
>
> Other types: Default values as specified in OA_COLUMNS.
>
> iNumInputParams int The number of arguments expected by the function.
>
> \>=0 to allow a fixed argument list.
>
> \< 0 to allow variable argument list with a maximum of \|args\|.
>
> **RETURN**

NONE

### See also

- [dam_isTargetRow](#dam_istargetrow)

### Custom scalar functions

> Custom scalar functions are an extension of scalar functions that are
> used only in a search condition (WHERE clause). OpenAccess SDK enables
> the IP to obtain details of the custom scalar conditions and to handle
> their evaluation. OpenAccess SDK then transforms the custom conditions
> into a set of AND conditions and returns each set as a list. Each list
> can contain one or more conditions as OR predicates.
>
> Your IP must perform a sequence of operations to handle the evaluation
> of custom scalar functions in WHERE clause. See [Using Custom Scalar
> Functions in a WHERE
> Clause](#using-custom-scalar-functions-in-a-where-clause) for more
> information.

### SQL Engine core methods API

> **scalar_function**
>
> This method can be used to define a custom scalar function.
>
> scalar_function(String pName)

#### Parameters for scalar_function

> **Parameter Type Description IN**
>
> pName String The name of the custom scalar function.
>
> **RETURN**

NONE

### dam_getSetOfCustomConditionLists

> This method is used to retrieve custom conditions lists from the Where
> clause.
>
> long dam_getSetOfCustomConditionLists( long hstmt,
>
> xo_int pbPartialLists)
>
> **Parameters for dam_getSetOfCustomConditionLists**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 29%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>IN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td>long</td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pbPartialLists</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>TRUE - the custom condition list provided to the IP is a partial
list. This happens in cases where:</p>
</blockquote>
<ul>
<li><blockquote>
<p>A custom scalar function is an operand in a condition and another
operand is not a column of the table.</p>
</blockquote></li>
<li><blockquote>
<p>A custom scalar function is used with non-literal parameters.</p>
</blockquote></li>
<li><blockquote>
<p>A custom scalar function is in an expression.</p>
</blockquote></li>
<li><blockquote>
<p>A custom scalar function in a JOIN is bound by the OR operator.</p>
</blockquote></li>
</ul>
<blockquote>
<p>The IP cannot mark all custom condition lists as evaluated, so the IP
should return an error.</p>
<p>FALSE - the condition list provided to the IP contains the full
expression.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td>long</td>
<td><blockquote>
<p>The search custom condition list. Navigate it using
dam_getFirstCondList and dam_getNextCondList. A 0 is returned if no
search list is available. The IP must call dam_freeSetOfConditionList to
this handle when done.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_describeCustomCond

> This method is used to retrieve the description of a custom condition,
> including the column, operator, name of the scalar function, and any
> parameters passed to the function.
>
> int dam_describeCustomCond( long hstmt,
>
> long hcond,
>
> xo_long phCol,
>
> xo_int piOpType, StringBuffer pScalarName, xo_long phValExpList)
>
> **Parameters for dam_describeCustomCond**

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 24%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>Parameter</p>
</blockquote></th>
<th><blockquote>
<p>Type</p>
</blockquote></th>
<th><blockquote>
<p>Description</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>IN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>DAM_HSTMT</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hcond</p>
</blockquote></td>
<td><blockquote>
<p>DAM_HCOND</p>
</blockquote></td>
<td><blockquote>
<p>The custom condition handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phCol</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the column associated</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>with the condition.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piOpType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The operator type. See Table:</p>
<p><a href="#dam_describecond">Operator Types for Conditions</a> for the
description of supported operators</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>and their associated values.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pScalarName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The scalar function name.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phValExpList</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the value expression</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>list that contains the arguments to the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>scalar function.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - valid condition is</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>returned.</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - the operation failed.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also:</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a
href="#dam_describecustomcondex2">dam_describeCustomCondEx2</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### dam_describeCustomCondEx2

> This method is used to retrieve the description of a custom condition,
> including the name of the function
>
> qualifier, column, operator, name of the scalar function, and any
> parameters passed to the function.
>
> int dam_describeCustomCond( long hstmt,
>
> long hcond,
>
> xo_long phCol,
>
> xo_int piOpType, StringBuffer pQualifierName, StringBuffer
> pScalarName, xo_long phValExpList)
>
> **Parameters for dam_describeCustomCondEx2**

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 28%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>Parameter</p>
</blockquote></th>
<th><blockquote>
<p>Type</p>
</blockquote></th>
<th><blockquote>
<p>Description</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>IN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>DAM_HSTMT</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hcond</p>
</blockquote></td>
<td><blockquote>
<p>DAM_HCOND</p>
</blockquote></td>
<td><blockquote>
<p>The custom condition handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phCol</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the column associated</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>with the condition.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piOpType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The operator type. See Table</p>
<p><a href="#dam_describecond">Operator Types for Conditions</a> for the
description of supported operators</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>and their associated values.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pQualifierName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the function qualifier.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pScalarName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The scalar function name.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phValExpList</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the value expression</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>list that contains the arguments to the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>scalar function.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - valid condition is</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>returned.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - the operation failed.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also:</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_getcondval">dam_getCondVal</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#dam_getcondrightval">dam_getCondRightVal</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### Using Custom Scalar Functions in a WHERE Clause

> Your IP can use custom scalar functions that are specified in a WHERE
> clause. For example, the following
>
> query specifies a scalar function named NEAR:
>
> SELECT \* FROM curvalue WHERE name = NEAR(\'tom\', \'jones\');
>
> The OpenAccess SDK SQL engine allows the IP to obtain information
> about the custom scalar conditions and evaluate them, returning each
> set of custom conditions as a list of OR predicates. Your IP must
> complete the following procedure to evaluate and use custom scalar
> functions.
>
> **Note:** This feature is not supported for .NET.

### Using Custom Scalar Functions With a Java IP

#### To use custom scalar functions with a Java IP:

1.  Register the custom scalar function with the OpenAccess SDK SQL
    engine. Call the ipGetScalarFunctions() method to return a
    scalar_function array with an entry for each method you want to
    expose. This example registers a custom scalar function named NEAR:

> IpFuncs\[x\] = new scalar_function(\"NEAR\");
>
> **Note:** You only need to specify the name of the scalar function;
> you do not need to specify the implementation or handle.
>
> **Note:** You can specify multiple literal parameters (for example,
> 0\*).

2.  Obtain the custom conditions in the query. When IP EXECUTE is
    called, call dam_getSetOfCustomConditionLists to validate the custom
    scalar function and get the custom condition lists. For example:

> pSetOfCustCondList = jdam.dam_getSetOfCustomConditionLists(dam_hstmt,
> pbPartialLists);

- If pbPartialLists is returned as TRUE, a partial list is returned. A
  > partial list is returned in the following cases:

- If a custom scalar function is an operand in a condition and another
  > operand is not a COLUMN of the table.

- If a custom scalar function is used with non-literal parameters.

- If a custom scalar function is used in an expression.

- If a custom scalar function is used in a JOIN that is bound by the OR
  > operator.

> When a partial list is returned, your IP cannot mark all the custom
> conditions in the list as having been evaluated. In this case,
> generate an exception and exit from the query execution; otherwise,
> the SQL engine reports an error on each target row validation.

3.  Get details about each custom condition. Iterate through each set of
    custom condition lists (using dam_getFirstCondList and
    dam_getNextCondList, for example) and fetch the custom conditions
    (using dam_getFirstCond and dam_getNextCond, for example).

> For each condition, call dam_describeCustomCond to get the following
> information:

- Column specified in the condition.

- Operator (for example =, \>, or LIKE).

- Name of the scalar function.

- Parameters passed to the method. Parameters are returned as a
  > ValExpList. You can navigate this list by calling dam_getFirstValExp
  > and dam_getNextValExp. You can get details of each expression by
  > calling dam_getValueofExp.

> This example requests this information from the scalar function named
> NEAR:
>
> jdam.dam_describeCustomCond(hstmt, hcond, phCol, piOpType, \"NEAR\",
> phValExpList);

4.  Mark each custom condition list as having been processed. Call
    dam_setOption for each condition list, marking each as having been
    processed. This example marks the DAM_CONDLIST_OPTION_EVALUATION
    option to a value of DAM_PROCESSING_OFF:

> jdam.dam_setOption(DAM_CONDLIST_OPTION, pCustCondList,
> DAM_CONDLIST_OPTION_EVALUATION, DAM_PROCESSING_OFF)
>
> See [dam_setOption](#dam_setoption) for more information about this
> method.
>
> The SQL engine marks these condition lists as PROCESSED by the IP and
> skips their evaluation when validating target rows; otherwise, the SQL
> engine reports an error for each target row validation.

5.  Build results that match the custom conditions.

> **Note:** Custom scalar functions are only supported on INTERSECT type
> condition lists.
>
> **Note:** The dam_getOptimalIndexAndConditions(),
> dam_getRestrictionList(), dam_getSetOfConditionLists() and
> dam_getSetOfConditionListsEx() methods only return non-custom scalar
> condition lists.
>
> **Note:** If a standard method and a custom scalar function are
> registered with same name, the first method that is registered has
> precedence.
>
> For information about error messages that can occur while using custom
> scalar functions, see
> [Error](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/troubleguide/codes.html)
> [Message
> Codes](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/troubleguide/codes.html)
> in the *OpenAccess SDK Troubleshooting Guide*.

### Methods for Java reference

> This section is your reference to the OpenAccess SDK SQL Engine
> methods for Java.

### dam_addColAliasxxxValToRow

> These methods are used to build up a row by adding values for result
> columns based on Alias names. The result column handle refers to
> column handles that are returned by dam_describeColResAlias.
>
> These methods require all data to be passed in using the same format
> as the column definition in the schema database or in any format for
> which a conversion is supported. Please refer to the following data
> conversion table:
>
> <https://documentation.progress.com/output/DataDirect/collateral/dataconversiontable.html>
>
> Character data can be added by supplying the length or by marking the
> data as null terminated. NULL data is added by specifying the
> XO_NULL_DATA value flag for the column value length. These methods
> copy data from the user supplied buffer to its internal buffers and
> therefore the IP can free the memory associated with the input buffer
> (pColVal).
>
> For a Java IP, there are data type specific methods.
>
> int dam_addColAliasBigIntValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, long colVal, int lColValLen)
>
> int dam_addColAliasBinaryValToRow( int dam_hstmt,
>
> long hRow,
>
> long hColAlias, byte\[\] pColVal, int lColValLen)
>
> int dam_addColAliasBitValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, boolean colVal,
>
> int lColValLen)
>
> int dam_addColAliasCharValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, String colVal,
>
> int lColValLen)
>
> int dam_addColAliasWCharValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, String colVal,
>
> int lColValLen)
>
> int dam_addColAliasDoubleValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, double colVal,
>
> int lColValLen)
>
> int dam_addColAliasFloatValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, float colVal,
>
> int lColValLen)
>
> int dam_addColAliasIntValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, int colVal,
>
> int lColValLen)
>
> int dam_addColAliasShortValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, short colVal,
>
> int lColValLen)
>
> int dam_addColAliasTimeStampValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, xo_tm colVal,
>
> int lColValLen)
>
> int dam_addColAliasTinyintValToRow( long hstmt,
>
> long hRow,
>
> long hColAlias, byte colVal,
>
> int lColValLen)

#### Parameters for dam_addColAliasxxxValToRow

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 41%" />
<col style="width: 36%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hColAlias</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle returned from</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>dam_describeColResAlias.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>ColVal</p>
</blockquote></td>
<td><blockquote>
<p>Depends on the method used</p>
</blockquote></td>
<td><blockquote>
<p>The Java type of the data should</p>
</blockquote></td>
</tr>
<tr class="even">
<td colspan="2" rowspan="7"></td>
<td><blockquote>
<p>correspond to the iXoType value. See</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>Table <a href="#reference-tables">How the methods for Java</a></p>
<p><a href="#reference-tables">return a value as an object</a>. The</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>data can be supplied in a format that</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>corresponds to the column's definition</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>in the schema or in any other format</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>and the OpenAccess SDK SQL</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>engine will perform the required</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 30%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2"></th>
<th><blockquote>
<p>conversion.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>lColValLen</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The length of the data: XO_NULL_DATA - indicates a null value.</p>
<p>For VARCHAR, CHAR, WVARCHAR,</p>
<p>WCHAR, and NUMERIC, either the number of characters or XO_NTS to add
the entire string.</p>
<p>For all other data types, the length of the data.</p>
<p><strong>Note:</strong> The length value is required to allow
OpenAcccess SDK to validate that the correct size of data is passed in
for the iXoType.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - added the value to the row.</p>
<p>DAM_FAILURE - error adding the</p>
<p>value.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> The LONGVARBINARY type can be provided to the OpenAccess SDK SQL
> engine in segments by calling this method multiple times. In each
> call, include the data and its length. Also, the data must be provided
> as binary data and not as character data.

### See also

- [dam_describeColResAlias](#dam_describecolresalias)

### dam_addError

> This method adds an error to the error list that is maintained by the
> OpenAccess SDK SQL engine at the environment, connection, or statement
> level. The error string is returned to the client. This is the only
> way to pass the IP-specific error information to the client. An IP
> method uses dam_addError to return an error by adding an error and
> returning DAM_FAILURE. Error messages that are added using this
> function are reported with SQL state HY000. Use dam_addErrorEx to
> specify the SQL state. Using dam_addError is the recommended approach
> to report back errors to the client.
>
> void dam_addError( long hdbc,
>
> long hstmt,
>
> int iErrorIndex, int iNativeError, String szErrorText)

#### Parameters for dam_addError

> **Parameter Type Description**

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 24%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th colspan="2"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The connection handle. Set to 0 if the error is related to a
statement and not to the connection.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle. Set to 0 if the error is related to a statement
and not to the connection.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iErrorIndex</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Set to DAM_IP_ERROR to indicate an error from the IP.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iNativeError</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Native error as defined by the IP.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szErrorText</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The text to use in place of the standard text. Data from this buffer
is copied into the error queue. Set to NULL to use the default error
string.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_adderror">dam_addError</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#_bookmark15">dam_addErrorEx</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><span id="_bookmark15"
class="anchor"></span><strong>dam_addErrorEx</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method adds an error to the error list that is maintained by the
> OpenAccess SDK SQL engine at the environment, connection, or statement
> level. The error string is returned to the client. This is the only
> way to pass the IP-specific error information to the client. An IP
> method uses dam_addErrorEx to return an error by adding an error and
> returning DAM_FAILURE.
>
> Use the dam_addError function if you want to use the default SQL state
> instead of specifying a SQL state.
>
> void dam_addErrorEx( long hdbc,
>
> long hstmt,
>
> int iErrorIndex, int iNativeError, String szErrorText)
>
> **Parameters for dam_addErrorEx**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The connection handle. Set to 0 if the</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 28%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th></th>
<th></th>
<th><blockquote>
<p>error is related to a statement and not to the connection.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle. Set to 0 if the error is related to a
connection.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iErrorIndex</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Set to DAM_IP_ERROR to indicate an error from the IP.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iNativeError</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Native error as defined by the IP.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szErrorText</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The text to use in place of the standard text. Data from this buffer
is copied into the error queue. Set to NULL to use the default error
string.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#dam_adderror">dam_addError</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### dam_addLOBLocatorValToRow

> This method is used to getting LOB data from the IP, based on a
> request from the client. The IP calls this method to add the locator
> value. When a client requests data, OpenAccess SQL Engine invokes
> dam_addLOBLocatorValToRow, which is then exported from the IP.
>
> int dam_addLOBLocatorValToRow( long dam_hstmt,
>
> long hRow,
>
> long hCol,
>
> int iXOType, Object pColVal, long lColValLen)

#### Parameters for dam_addLOBLocatorValToRow

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 32%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>dam_hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hCol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iXOType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>XO data type of the stream.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pColVal</p>
</blockquote></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>LOB locator value, for example, File</p>
</blockquote></td>
</tr>
</tbody>
</table>

> Handle.
>
> IColValLen long The LOB locator value length.
>
> **RETURN**
>
> int DAM_SUCCESS - added the value to the row.
>
> DAM_FAILURE - error adding the value.

### dam_addOutputRow

> This method provides the row to the OpenAccess SDK SQL engine for
> returning return values and output parameters from stored procedures.
>
> int dam_addOutputRow( long hstmt,
>
> long hRow)
>
> **Parameters for dam_addOutputRow**

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 25%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle obtained from dam_allocOutputRow.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - Added the row. DAM_FAILURE - Failed to add the row.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#_bookmark21">dam_allocOutputRow</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### dam_addResultBufferToTable

> This method adds multiple rows to the OpenAccess SDK SQL Engine and
> sets the status in result buffer Status\[\]. If an error occurs, it
> sets the error index and returns DAM_FAILURE.
>
> long dam_addResultBufferToTable( long hstmt, ResultBuffer pResBuffer)
>
> **Parameters for dam_addResultBufferToTable**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 35%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>IN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pResBuffer</p>
</blockquote></td>
<td><blockquote>
<p>ResultBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The result buffer.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - Added the row. DAM_FAILURE - Failed to add the row.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_addRowBufferToRowTable

> This method adds the row to the join rowset. Each row that is built
> for a specific row of the outer table should be added using this
> method. This function performs the dam_allocRowSet,
> dam_addxxxValToRow, and dam_isTargetRow operations. On success, the
> method performs dam_addRowToTable.
>
> Return codes are returns as part of the RowBuffer.status\[index\].
>
> int dam_addRowToRowSet( long hrowset,
>
> long hRow)

#### Parameters for dam_addRowBufferToRowTable

> **Parameter Type Description INPUT**
>
> hrowset long The handle to the rowset.
>
> hRow long The handle to the row.
>
> **RETURN**
>
> int DAM_SUCCESS - added the row to the rowset.
>
> DAM_FAILURE - error adding the row.

### See also

- [dam_allocRowSet](#dam_allocrowset)

- [dam_addxxxValToRow](#dam_addxxxvaltorow)

- [dam_addRowToTable](#_bookmark18)

- [dam_isTargetRow](#dam_istargetrow)

### dam_addRowToRowSet

> This method adds the row to the join rowset. Each row that is built
> for a specific row of the outer table should be added using this
> method.
>
> int dam_addRowToRowSet( long hrowset,
>
> long hRow)

#### Parameters for dam_addRowToRowSet

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 21%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hrowset</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the rowset.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the row.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - added the row to the rowset.</p>
<p>DAM_FAILURE - error adding the row.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><span id="_bookmark18"
class="anchor"></span><strong>dam_addRowToTable</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method adds the row to the result set. The IP should call this
> method if dam_isTargetRow returns DAM_TRUE. All rows that are added to
> the result set will be sent back to the client. The IP should check
> the return code from this method and, on failure, stop processing the
> query and return DAM_FAILURE.
>
> int dam_addRowToTable( long hstmt,
>
> long hRow)

#### Parameters for dam_addRowToTable

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - Added the row. DAM_FAILURE - Failed to add the row.
This could occur when values for all columns are not specified or
there</p>
</blockquote></td>
</tr>
</tbody>
</table>

> is an error in computing any expressions in the SELECT list. If return
> code is DAM_FAILURE, the IP should return from ipExecute with
> DAM_FAILURE. DAM_ABORT_OPERATION - Failed
>
> to add the row when any of the specified resource quotas are exceeded.
> For example, if the Query Timeout occurs, this error is returned. The
> IP should check and return from ipExecute with DAM_FAILURE.

### See also

- [dam_isTargetRow](#dam_istargetrow)

### dam_addxxxValToRow

> These methods are used to build up a row by adding values for columns,
> one at a time. These methods require all data to be passed in using
> the same format as the column definition in the schema database or in
> any format for which a conversion is supported. Please refer to the
> following data conversion table:
>
> <https://documentation.progress.com/output/DataDirect/collateral/dataconversiontable.html>
> NULL data is added by specifying the XO_NULL_DATA value flag for the
> column value length.
>
> These methods copy data from the user-supplied buffer to its internal
> buffers; therefore, the IP can free the memory associated with the
> input buffer (ColVal).
>
> For a Java IP, there are data type-specific methods.
>
> int dam_addBigIntValToRow( long hstmt,
>
> long hRow, long hCol, long colVal,
>
> int lColValLen)
>
> int dam_addBinaryValToRow( long hstmt,
>
> long hRow,
>
> long hCol, byte\[\] ColVal,
>
> int lColValLen)
>
> int dam_addBitValToRow( long hstmt,
>
> long hRow,
>
> long hCol, boolean colVal,
>
> int lColValLen) int dam_addCharValToRow(
>
> long hstmt,
>
> long hRow,
>
> long hCol, String colVal,
>
> int lColValLen)
>
> int dam_addWCharValToRow( long hstmt,
>
> long hRow,
>
> long hCol, String colVal,
>
> int lColValLen)
>
> int dam_addDoubleValToRow( long hstmt,
>
> long hRow,
>
> long hCol, double colVal,
>
> int lColValLen)
>
> int dam_addFloatValToRow( long hstmt,
>
> long hRow, long hCol, float colVal,
>
> int lColValLen)
>
> int dam_addIntValToRow( long hstmt,
>
> long hRow, long hCol, int colVal,
>
> int lColValLen)
>
> int dam_addShortValToRow( long hstmt,
>
> long hRow, int hCol, short colVal,
>
> int lColValLen)
>
> int dam_addTimeStampValToRow( long hstmt,
>
> long hRow, long hCol, xo_tm colVal,
>
> int lColValLen)
>
> int dam_addTinyIntValToRow( long hstmt,
>
> long hRow,
>
> long hCol, byte colVal,
>
> int lColValLen)

#### Parameters for dam_addxxxValToRow

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 41%" />
<col style="width: 36%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hCol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>ColVal</p>
</blockquote></td>
<td><blockquote>
<p>depends on the method used</p>
</blockquote></td>
<td><blockquote>
<p>The Java type of the data should</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>correspond to the iXoType value. See</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>Table <a href="#reference-tables">How the methods for Java</a> <a
href="#reference-tables">return a value as an object</a></p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>lColValLen</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The length of the data.</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>XO_NULL_DATA - indicates a null</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>value</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>For VARCHAR, CHAR, WVARCHAR,</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>WCHAR, and NUMERIC either the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>length of the number of characters or</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>XO_NTS to add the entire string.</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>For all other data types, 0 or any</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>value other than XO_NULL_DATA.</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p><strong>Note:</strong> The length value is required to</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>allow OpenAccess SDK to validate</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>that the correct size of data is passed</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>in for the iXoType.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - added the value to</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>the row.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - error adding the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>value.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> The LONGVARBINARY, LONGVARCHAR, and WLONGVARCHAR type of data
> (character data or binary data) can be provided to the OpenAccess SDK
> SQL engine in segments by calling dam_addxxxValToRow multiple times.
> In each call, include the data and its length. However, for
> LONGVARBINARY, the data must be provided as binary data.

### See also

- [dam_addxxxValToRow](#dam_addxxxvaltorow)

### dam_add_schemaobj

> This method adds a Java schema object to the list of objects that are
> returned to the OpenAccess SDK SQL engine. This method should be
> called by the IP to add a table, column, index, foreign key,
> procedure, or procedure column objects.
>
> void dam_add_schemaobj( long pMemTree, long iType,
>
> long pList, Object pSearchObj, Object pSchemaObj)

#### Parameters for dam_add_schemaobj

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pMemTree</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The memory tree handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Type of schema information being returned: DAMOBJ_TYPE_CATALOG -</p>
<p>catalogs DAMOBJ_TYPE_SCHEMA -</p>
<p>schemas DAMOBJ_TYPE_TABLETYPE - table</p>
<p>types</p>
<p>DAMOBJ_TYPE_TABLE - tables information as defined by OA_TABLES</p>
<p>DAMOBJ_TYPE_COLUMN - column information as defined by OA_COLUMNS</p>
<p>DAMOBJ_TYPE_STAT - index information as defined by OA_STATISTICS</p>
<p>DAMOBJ_TYPE_FKEY - foreign key information as defined by OA_FKEYS
DAMOBJ_TYPE_PROC - procedures information DAMOBJ_TYPE_PROC_COLUMN -</p>
<p>procedure columns information</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The list on which the objects are to be added. It is passed in the
call to the IP. The IP can pass the pSearchObj filter to
dam_add_schemaobj () to have the OpenAccess SDK SQL engine filter the
objects before adding to the list.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pSearchObj</p>
</blockquote></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>Java schema object to use for filtering the objects added to the
list. If you have taken care of only adding the objects that are
requested, then set</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table style="width:100%;">
<colgroup>
<col style="width: 32%" />
<col style="width: 21%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2"></th>
<th><blockquote>
<p>this to NULL. If you want the OpenAccess SDK SQL engine to do the
filtering, then pass in the same value as passed into your IP.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pSchemaObj</p>
</blockquote></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>Java schema object. Depending upon iType, you will pass the
schemaobj_xxx Java object. Type of object to pass in based on iType:
DAMOBJ_TYPE_CATALOG -</p>
<p>schemaobj_table DAMOBJ_TYPE_SCHEMA -</p>
<p>schemaobj_table DAMOBJ_TYPE_TABLETYPE -</p>
<p>schemaobj_table DAMOBJ_TYPE_TABLE -</p>
<p>schemaobj_table DAMOBJ_TYPE_COLUMN -</p>
<p>schemaobj_column DAMOBJ_TYPE_STAT -</p>
<p>schemaobj_stat DAMOBJ_TYPE_FKEY -</p>
<p>schemaobj_fkey DAMOBJ_TYPE_PKEY -</p>
<p>schemaobj_pkey DAMOBJ_TYPE_PROC -</p>
<p>schemaobj_proc DAMOBJ_TYPE_PROC_COLUMN -</p>
<p>schemaobj_proccolumn</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><span id="_bookmark21"
class="anchor"></span><strong>dam_allocOutputRow</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method allocates an output row and returns its handle. The
> OpenAccess SDK SQL engine allocates the memory that is required by the
> row. Use this row to provide return values and output values from
> stored procedure calls.
>
> long dam_allocRow(long hstmt)

#### Parameters for dam_allocOutputRow

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 35%" />
<col style="width: 38%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> long The handle to a new row. A 0 is
>
> returned if a row could not be allocated.

### dam_allocResultBuffer

> This method creates a result buffer object from the ResultBuffer class
> and returns it to the IP.
>
> When the buffer is full, it throws a BufferOverflowException
> exception. When all result rows are added to the buffer or when an
> exception occurs, the IP should call dam_addResultBufferToTable by
> passing the ResultBuffer object. If the result buffer is no longer
> required, the IP can call dam_freeResultBuffer to free the
> ResultBuffer.
>
> long dam_allocResultBuffer(long hstmt)

#### Parameters for dam_allocResultBuffer

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The handle to a new rowset. A 0 is returned if a rowset could not
> be allocated.

### See also

- [dam_addResultBufferToTable](#dam_addresultbuffertotable)

- [dam_freeResultBuffer](#_bookmark35)

### dam_allocRow

> This method allocates a new row and returns its handle. The OpenAccess
> SDK SQL engine allocates the memory that is required by the row. A row
> should be freed if the dam_isTargetRow call fails. A new row must be
> allocated for each data set that you want to evaluate using
> dam_isTargetRow.
>
> long dam_allocRow(long hstmt)

#### Parameters for dam_allocRow

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 35%" />
<col style="width: 38%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **RETURN**
>
> long The handle to a new row. If a row could not be allocated, 0 is
> returned.

### See also

- [dam_isTargetRow](#dam_istargetrow)

### dam_allocRowSet

> This method allocates a new rowset (result table) and returns its
> handle. The OpenAccess SDK SQL engine allocates the memory that is
> required by the rowset. A new rowset must be allocated for each rowset
> that you want to associate with a outer table row. For example, if
> tables dept and emp are being joined, then for each row of dept, you
> must allocate a new rowset and add the emp rows to it.
>
> long dam_allocRowSet(long hstmt)

#### Parameters for dam_allocRowSet

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The handle to a new rowset. A 0 is returned if a rowset could not
> be allocated.

### dam_clearError

> This method allows the IP to clear any existing errors. Use this
> method in special cases when you want the IP to remove errors that
> were added by the OpenAccess SDK SQL engine. For example, the
> OpenAccess SDK SQL engine adds an error message when the TableRowset
> limit is exceeded before it returns the DAM_ABORT_OPERATION error from
> dam_addRowToTable.
>
> You can implement the IP to remove existing errors using
> dam_clearError, and add its own custom message using dam_addError.
>
> void dam_clearError( int hdbc, long hstmt)

#### Parameters for dam_clearError

> **Parameter Type Description**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 37%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th colspan="2"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hdbc</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Connection handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also

- [dam_addError](#dam_adderror)

- [dam_addRowToTable](#_bookmark18)

### dam_compareCol

> This method checks if the column handles refer to the same column.
> Even if the columns handles refers to the same column, they may not be
> equal.
>
> int dam_compareCol( long hcol1, long hcol2)

#### Parameters for dam_compareCol

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 26%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hcol1</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to a column.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hcol2</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to a column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>TRUE - the column handles refer to the same column.</p>
<p>FALSE - the column handles do not refer to the same column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>dam_copyRow</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method makes a copy of the input row and returns its handle. The
> OpenAccess SDK SQL engine allocates the memory that is required by the
> row and copies all values from the input row. The IP can replace
> values for some of the columns in the new row by calling
> [dam_addxxxValToRow](#dam_addxxxvaltorow). The row should be freed if
> the dam_isTargetRow call fails.
>
> long dam_copyRow( long hstmt, long hRow)

#### Parameters for dam_copyRow

#### 

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> hRow long The row handle for which the values should be copied into
> the new row.
>
> **RETURN**
>
> long The handle to a new row. A 0 is
>
> returned if a row could not be allocated.

### See also

- [dam_addxxxValToRow](#dam_addxxxvaltorow)

- [dam_isTargetRow](#dam_istargetrow)

### dam_createVal

> This method is used to build the output value of a scalar function.
> NULL data is added by specifying the XO_NULL_DATA value flag for the
> column value length. The data must be provided in the same format as
> the output of the scalar function or in any format for which a
> conversion is supported. This method copies data from the
> user-supplied buffer to its internal buffers, so that the IP can free
> the memory associated with the input buffer (pColVal).
>
> long dam_createVal( long pMemTree, int iXoType, Object pColVal, int
> iColValLen)

#### Parameters for dam_createVal

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pMemTree</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The tree to use for allocating space.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iXoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The type of the data. Must be the type</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>defined as the output for this scalar</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>function.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pColVal</p>
</blockquote></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>The data object. The Java type of the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>data should correspond to the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>iXoType value. See Table</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p><a href="#reference-tables">OpenAccess SDK Data Types</a></p>
<p><a href="#reference-tables">and Java Type When Adding</a> <a
href="#reference-tables">Value</a>.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 23%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>iColValLen</p>
</blockquote></th>
<th><blockquote>
<p>int</p>
</blockquote></th>
<th><blockquote>
<p>The length of the data. XO_NULL_DATA - indicates a null value.</p>
<p>For VARCHAR, CHAR and</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>NUMERIC either the length of the number of characters or XO_NTS to
add the entire string.</p>
<p>For all other data types, pass in 0 or any value other than
XO_NULL_DATA.</p>
<p><strong>Note:</strong> The length value is required to allow
OpenAccess SDK to validate that the correct size of data is passed in
for the iXoType.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>non-null - valid handle to the return value.</p>
<p>0 - error adding the value.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><span id="_bookmark24"
class="anchor"></span><strong>dam_describeCol</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to retrieve the description of the specified
> column. You must set up string buffers of at least DAM_MAX_ID_LEN+1
> for the identifier names returned by this method. Pass in NULL for any
> attributes that you do not want.
>
> The attributes for controlling how the column is used in the query are
> returned as a bitwise OR (\|) of the column attributes. You must
> implement the IP to perform a bitwise AND (&) of the output with the
> column type (DAM_COL_IN_COND) to check for all the ways the column is
> used.
>
> void dam_describeCol( long hcol,
>
> xo_int piColNum, StringBuffer pColName, xo_int piXOType,
>
> xo_int piColType)

#### Parameters for dam_describeCol

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 33%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piColNum</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The column number as defined in the schema database. Create and pass
in an xo_int object.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pColName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The column name. Create and pass</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 20%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2"></th>
<th><blockquote>
<p>in a StringBuffer of DAM_MAX_ID_LEN+1.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>piXOType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The data type (see Table</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p><a href="#reference-tables">OpenAccess SDK Data Types</a> <a
href="#reference-tables">and Java Type When Adding</a></p>
<p><a href="#reference-tables">Value</a>). Create and pass in an
xo_int</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>object.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piColType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Create and pass in an xo_int object.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>How the column appears in the query</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>is specified by the setting of one or</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>more of the following flags:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_COL_IN_SCHEMA - column is</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>defined in the schema database. This</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>flag applies to all columns.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_COL_IN_RESULT - column is</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>part of the select list.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_COL_IN_CONDITION - column</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>is part of the where clause.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_COL_IN_UPDATE_VAL_EXP -</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>column is part of an update value</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>expression.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><span id="_bookmark25"
class="anchor"></span><strong>dam_describeColDetail</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to retrieve the details of the specified column.
> Pass in NULL for any attributes that you do not want. The IP uses this
> method to:

- Retrieve data type details - get the exact scale, precision, radix,
  length and other attributes associated with a column.

- Retrieve user data - get the user data that is optionally stored for
  each column in the schema.

> xo_type dam_describeColDetail( long hcol,
>
> xo_int piPseudoColumn,
>
> xo_int piColumnType,
>
> xo_int piSortOrder,
>
> xo_int piFunctionType,
>
> xo_int piIndexType, StringBuffer pUserData)
>
> **Parameters for dam_describeColDetail**

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 31%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>piPseudoColumn</p>
</blockquote></th>
<th><blockquote>
<p>xo_int</p>
</blockquote></th>
<th><blockquote>
<p>Indicates if the column is marked as a PSEUDO column in which case
the user is not allowed to insert or update its value. The output will
be one of the following:</p>
<p>SQL_PC_UNKNOWN - not set. SQL_PC_NOT_PSEUDO - the</p>
<p>column is not a PSEUDO column and should be treated like a normal
user column.</p>
<p>SQL_PC_PSEUDO - if the column is a PSEUDO column.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>piColumnType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>If the column has been marked as a SQL_PC_PSEUDO to indicate it is a
pseudo column, then this field indicates whether it is a ROWID or
ROWVER.</p>
<p>DAM_NOT_SET - if column is not a pseudo column.</p>
<p>SQL_BEST_ROWID - the column is used to uniquely identify the row.</p>
<p>SQL_ROWVER - the column is used to track the version of the row.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pUserData</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>User data as specified in the schema. Create and pass in a
StringBuffer of DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>xo_type</p>
</blockquote></td>
<td><blockquote>
<p>Object of type xo_type detailing the precision, scale, length, radix,
null attributes of the column as defined in the schema. See below for
the xo_type definition.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### xo_type

> xo_type is an object with the following attributes:

- int type - Identifies the type of the column.

- int length - Contains the maximum length for a character data type.

- int prec - Contains the number of digits of mantissa precision.

- int scale - Total number of significant digits to the right of the
  decimal point.

- int radix - Radix of PREC if TYPE is one of the approximate numeric
  types.

- int null - Contains 0 (XO_NO_NULLS) if not nullable or 1 (XO_NULLABLE)
  if column is nullable.

### dam_describeColResAlias

> This method is used to get the result column alias description for the
> specified column. You must set up buffers of at least DAM_MAX_ID_LEN+1
> for the identifier names that are returned by this method. Pass in
> NULL for any attributes that you do not want. To get the first result
> alias, pass 1 for bFirst. The IP can continue calling the method by
> passing 0 for bFirst to get all result alias descriptions.
>
> int dam_describeColResAlias( long hstmt,
>
> long hcol,
>
> int bFirst,
>
> StringBuffer pAliasName, xo_long phcolAlias)

#### Parameters for dam_describeColResAlias

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> hcol long The column handle.
>
> bFirst int Pass in 1 to get the first alias and 0 to get next.
>
> **OUTPUT**
>
> pAliasName StringBuffer The result alias name.
>
> phColAlias xo_long The result alias column handle. Use this column
> handle when the IP returns a value for the result column using
> dam_addColAliasxxxValToRow.
>
> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure DAM_NOT_AVAILABLE - when no result columns
> match the input column handle.

### See also

- [dam_addColAliasxxxValToRow](#dam_addcolaliasxxxvaltorow)

### dam_describeColScalar

> This method is used to check if the column is a scalar function that
> is being mapped to a column.
>
> int dam_describeColScalar( long hcol,
>
> StringBuffer pScalarFuncName)
>
> **Parameters for dam_describeColScalar**

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 25%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle of the column.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pScalarFuncName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the scalar function.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>TRUE - column is mapped from scalar function.</p>
<p>FALSE - base column.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also:</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_describecolscalarex2">dam_describeColScalarEx2</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### dam_describeColScalarEx2

> This method is used to check if the column is a scalar function that
> is being mapped to a column. In addition, this method retrieves the
> description of the scalar function, including the qualifier name.
>
> int dam_describeColScalarEx2( long hcol,
>
> StringBuffer pQualifierName, StringBuffer pScalarFuncName)
>
> **Parameters for dam_describeColScalarEx2**

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 31%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle of the column.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pQualifierName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the function qualifier.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pScalarFuncName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the scalar function.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>TRUE - column is mapped from scalar function.</p>
<p>FALSE - base column.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also:

- [dam_describeColScalar](#dam_describecolscalar)

### dam_describeCond

> This method is used to retrieve the description of the condition. Use
> this method to retrieve the condition (=,
>
> \>, \<, etc.) and the value of the operand is (\'TAG\', 100, etc.).
> The condition is returned as a left and right pair. The right part
> only applies in the case of BETWEEN or LIKE type of conditions. For
> other conditions, when the right part is requested, a value of 0 is
> returned for the operator type. Each condition is described by the
> operator type, the value of the operand, and the data type of the
> operand value. You must use the method dam_getCondVal and/or
> dam_getCondRightVal to get the data for the condition.
>
> int dam_describeCond( long hcond,
>
> xo_int piLeftOpType, xo_int piLeftXOType, xo_int piRightOpType, xo_int
> piRightXOTpe)

#### Parameters for dam_describeCond

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hcond</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The condition handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piLeftOpType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The left operator type. See Table <strong>Operator Types for
Conditions</strong> for the operators and the associated value. All
conditions always have a left value. You must create and pass in an
object of type xo_int for this argument.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piLeftXOType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The data type. You must create and pass in an object of type xo_int
for this argument.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piRightOpType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The right operator type. See Table <strong>Operator Types for
Conditions</strong> for the operators and the associated value. This
value is only present in BETWEEN and LIKE type of statements. A value of
0 is returned if there is no right value. You must create and pass in an
object of type xo_int for this argument.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piRightXOType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The data type. You must create and</p>
</blockquote></td>
</tr>
</tbody>
</table>

> pass in an object of type xo_int for this argument.
>
> **RETURN**
>
> int DAM_SUCCESS - valid condition
>
> returned.
>
> DAM_FAILURE - error occurred while getting a condition because the
> arguments to the call are invalid or the format of the data in the
> condition cannot be converted to the column type.
>
> Table [Operator Types for Conditions](#dam_describecond) shows the
> operators that are allowed in a condition. Refer to
> [Default](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/default-optimization.html)
> [Optimization](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/default-optimization.html)
> of the *OpenAccess SDK Programmer\'s Guide* for examples on the
> operator values returned by this method to describe conditions like
> VALUE\>=*xxx* and VALUE BETWEEN *xxx* AND *yyy*.

#### Operator Types for Conditions

> **Operator Description**

SQL_OP_SMALLER The column value should be smaller than. Set this bit if
\<,

\<=, or BETWEEN predicates are used in the SQL query.

SQL_OP_GREATER The column value should be greater than. Set this bit if
\>,

\>=, or BETWEEN predicates are used in the SQL query.

> SQL_OP_LIKE The column value should be LIKE. The ppRightData will
> contain a valid pointer to XO_TYPE_CHAR data if an ESCAPE clause was
> used with LIKE.
>
> SQL_OP_ISNULL The column value should be NULL.
>
> SQL_OP_EQUAL The column value should be equal. This bit is set if \<=,
>
> \>=, or BETWEEN predicates are used in the SQL query.
>
> SQL_OP_NOT This operator occurs always in combination with either
> SQL_OP_NULL or SQL_OP_EQUAL and implies that the column value should
> not be NULL and column value should not be EQUAL respectively.

### See also

- [dam_getCondVal](#dam_getcondval)

- [dam_getCondRightVal](#dam_getcondrightval)

### dam_describeCondEx

> This method is used to retrieve the description of the condition. Use
> this method to retrieve the condition is (=, \>, \<, etc.) and the
> value of the operand is (\'TAG\', 100, etc.). It returns details of
> either the left or right part of the condition based on the input
> iCondPart. The right part only applies in a BETWEEN condition. For
> other
>
> conditions, when right part is requested, a value of 0 is returned for
> the Operator type. Each condition is described by the operator type,
> the value of the operand, and the data type of the operand value.
>
> Object dam_describeCondEx( long hstmt,
>
> long hcond,
>
> int iCondPart, xo_int iOpType, xo_int iXOType, xo_int iDataLen, xo_int
> iStatus)

#### Parameters for dam_describeCondEx

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Condition handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iCondPart</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Condition part (left or right) for which</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>details are being requested. Pass one</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>of the following:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_COND_PART_LEFT</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_COND_PART_RIGHT</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iOpType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Operator type. See Table <a href="#dam_describecond">Operator</a> <a
href="#dam_describecond">Types for Conditions</a> for the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>operators and the associated value.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>When details of a right part are</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>requested for condition that does not</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>have a right part, iOpTyp is set to 0.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iXOType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Data type of the condition.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iDataLen</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Length of the data. XO_NULL_DATA</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>indicates a NULL value.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iStatus</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - valid condition</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>returned.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - error getting a</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>condition because the arguments to</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>the call are invalid or the format of the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>data in the condition cannot be</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>converted to the column type.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>The object that represents the data</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>portion of the condition. The format of</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>the data depends on the data type of</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>the condition as indicated by</p>
</blockquote></td>
</tr>
</tbody>
</table>

> iXOType. Use this object only if iValLen is not set to XO_NULL_DATA.
> See Table [How the methods for](#reference-tables) [Java return a
> value as an object](#reference-tables).

### dam_describeIndex

> This method is used to retrieve the description of the optimal index
> returned by dam_getOptimalIndexAndConditions. The information for the
> index is retrieved from the OA_STATISTICS table. Pass in NULL for any
> descriptor in which you have no interest. The output parameters to
> this method are objects that must be created before this method is
> called.
>
> int dam_describeIndex( long hIndex,
>
> StringBuffer pszIndexQualifier, StringBuffer pszIndexName, xo_int
> piType,
>
> xo_int piNonUnique,
>
> xo_int piIndexLen)
>
> **Parameters for dam_describeIndex**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 30%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hIndex</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Index handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pszIndexQualifier</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>Qualifier of the index. Must create an object of type StringBuffer of
size DAMOBJ_MAX_ID_LEN.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pszIndexName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>Name of the index. Must create an object of type StringBuffer of size
DAMOBJ_MAX_ID_LEN.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>ptType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Type of information being returned:</p>
</blockquote>
<ul>
<li><blockquote>
<p>SQL_TABLE_STAT (0) - indicates a statistic for the table.</p>
</blockquote></li>
<li><blockquote>
<p>SQL_INDEX_CLUSTERED(1) -</p>
</blockquote></li>
</ul>
<blockquote>
<p>indicates a clustered index.</p>
</blockquote>
<ul>
<li><blockquote>
<p>SQL_INDEX_HASHED(2) - indicates a hashed index.</p>
</blockquote></li>
<li><blockquote>
<p>SQL_INDEX_OTHER(3) - indicates another type of index.</p>
</blockquote></li>
</ul>
<blockquote>
<p>Must create an object of type xo_int.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piNonUnique</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Indicates whether the index prohibits duplicate values:</p>
</blockquote>
<ul>
<li><blockquote>
<p>TRUE (1) - if the index values can be</p>
</blockquote></li>
</ul></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 30%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th></th>
<th></th>
<th><blockquote>
<p>non-unique.</p>
</blockquote>
<ul>
<li><blockquote>
<p>FALSE (0) - if the index values must be unique.</p>
</blockquote></li>
</ul></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>piIndexLen</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The number of columns in this index. This number is as indicated in
the OA_STATISTICS and does not reflect how many of the columns belonging
to the index are part of the query.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - valid information returned.</p>
<p>DAM_FAILURE - failure.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also

- [dam_getOptimalIndexAndConditions](#dam_getoptimalindexandconditions)

### dam_describeIndexCol

> This method is used to retrieve the description of a column associated
> with the optimal index returned by dam_getOptimalIndexAndConditions.
> The information for the index column is retrieved from the
> OA_STATISTICS table. Pass in NULL for any descriptor in which you have
> no interest.
>
> int dam_describeIndexCol( long hIndexCol, xo_int piSeqInIndex,
> StringBuffer pszColName, xo_int piCollation)

#### Parameters for dam_describeIndexCol

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 33%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hIndexCol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Index column handle as returned by the dam_getFirstIndexCol and
dam_getNextIndexCol methods.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piSeqInIndex</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The sequence of this column in the index starting at 1.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pszColName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piCollation</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The collation sequence: SQL_ORDER_ASC - for ascending SQL_ORDER_DESC
- for</p>
<p>descending</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> int DAM_SUCCESS - valid information returned.
>
> DAM_FAILURE - failure.

### See also

- [dam_getFirstIndexCol](#_bookmark46)

- [dam_getNextIndexCol](#dam_getnextindexcol)

- [dam_getOptimalIndexAndConditions](#dam_getoptimalindexandconditions)

### dam_describeJoinCond

> This method is used by the IP when building a pushdown join result
> set. This method returns the description of the join condition. Use
> this method to find out what the join condition is (innertable.col =
> outertable.col).
>
> The join condition is returned as a condition on a column of the inner
> table, the condition type (=, NOT =, \> etc,) and the outer table
> column. The outer table column is identified by the outer table
> process order number and the schema column number.
>
> int dam_describeJoinCond( long hstmt,
>
> xo_int piInnerTableColNum, xo_int piOpType,
>
> xo_int piOuterTableProcessOrder, xo_int piOuterTableColNum)

#### Parameters for dam_describeJoinCond

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 25%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle of the inner table that is being processed for
building the pushdown join results.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piInnerTableColNum</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The schema column number of a column of the inner table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piOpType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The operator type. The types are same as returned in
dam_describeCond. See Table <strong>Operator Types for
Conditions</strong> for the operators and the associated value. BETWEEN
type conditions are not considered as valid join conditions.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piOuterTableProcessOrder</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The table process order of the outer</p>
</blockquote></td>
</tr>
</tbody>
</table>

> table whose column is used in join condition.
>
> piOuterTableColNum xo_int The schema column number of the outer table
> column.
>
> **RETURN**
>
> int DAM_SUCCESS - valid join condition returned.
>
> DAM_NOT_AVAILABLE - no clear join condition can be returned.
>
> DAM_FAILURE - error.

### dam_describeJoinTable

> This method returns the attributes of the table with the given
> TableProcessOrder in the join query being processed. This method is
> called from ipExecute to get the details about each of the tables in
> the join query. Pass in NULL for any attribute in which you have no
> interest.
>
> int dam_describeJoinTable( long hstmt,
>
> int iTableProcessOrder, StringBuffer pCatalog, StringBuffer pSchema,
> StringBuffer pTableName, StringBuffer pTablePath, StringBuffer
> pUserData)

#### Parameters for dam_describeJoinTable

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 30%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iTableProcessOrder</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The process order of the table. Numbers start at 0.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The table qualifier as entered in the OA_TABLES table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pSchema</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The table owner as entered in the OA_TABLES table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pTableName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pTablePath</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The path of the table that is specified in the path column of the
OA_TABLES table. This is an IP</p>
</blockquote></td>
</tr>
</tbody>
</table>

> specific field.
>
> pUserData StringBuffer The Table_Userdata as entered in the OA_TABLES
> table. This can be any string that the IP wants to associate with a
> table.
>
> **RETURN**
>
> int DAM_SUCCESS - found table with
>
> matching iTableProcessOrder. DAM_NOT_AVAILABLE - invalid
> iTableProcessOrder. Matching table not found.
>
> DAM_FAILURE - The query that is being processed is not a join query.

### dam_describeProcedure

> This method is called from ipProcedure to get information about the
> stored procedure for which the IP has been called. Pass in NULL for
> any descriptor in which you have no interest.
>
> void dam_describeProcedure( long hstmt, StringBuffer pszCatalog,
> StringBuffer pszOwner, StringBuffer pszProcName, StringBuffer
> pszUserData)
>
> **Parameters for dam_describeProcedure**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 32%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle of the currently active statement.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pszCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The qualifier as entered in the OA_PROC table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pszOwner</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The owner as entered in the OA_PROC table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pszProcName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the procedure.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pszUserData</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The OA_USERDATA as entered in the OA_PROC table. This can be any
string that the IP wants to know about the procedure.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_describeScalarEx

> This method is used to retrieve the description of the scalar
> function. When the IP-registered scalar function is called, it can use
> this API call to get scalar function details. This is useful when the
> IP registers the same scalar function to evaluate multiple scalar
> function names or when the scalar function supports a variable number
> of arguments.
>
> int dam_describeScalarEx( long hValExpList StringBuffer pName,
>
> xo_int piNoOfActualParams,
>
> xo_int piResXoType,
>
> xo_int piLength,
>
> xo_int piPrecision,
>
> xo_int piScale)

#### Parameters for dam_describeScalarEx

> **Parameter Type Description INPUT**
>
> hValExpList long The handle of the scalar function argument list.
>
> **OUTPUT**
>
> pName StringBuffer Scalar function name
>
> piNoOfActualParams xo_int The number of arguments passed to the scalar
> function.
>
> piResXoType xo_int Scalar function result value type.
>
> piLength xo_int Scalar function result value length.
>
> piPrecision xo_int Scalar function result value precision.
>
> piScale xo_int Scalar function result value scale.
>
> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure

### See also:

- [dam_describeScalarEx2](#dam_describescalarex2)

### dam_describeScalarEx2

> This method is used to retrieve the description of the scalar
> function, including the qualifier name. When the
>
> scalar function that is registered with a qualifier in the IP is
> called, it can use this API call to get scalar function details. This
> is useful when the IP registers the same scalar function to evaluate
> multiple scalar function names or when the scalar function supports a
> variable number of arguments.
>
> int dam_describeScalarEx2( long hValExpList,
>
> StringBuffer pQualifierName, StringBuffer pName,
>
> xo_int piNoOfActualParams,
>
> xo_int piResXoType,
>
> xo_int piLength,
>
> xo_int piPrecision,
>
> xo_int piScale)

#### Parameters for dam_describeScalarEx2

<table>
<colgroup>
<col style="width: 31%" />
<col style="width: 25%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hValExpList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle of the scalar function argument list.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pQualifierName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the function qualifier</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>Scalar function name</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piNoOfActualParams</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The number of arguments passed to the scalar function.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piResXoType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Scalar function result value type.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piLength</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Scalar function result value length.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piPrecision</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Scalar function result value precision.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piScale</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Scalar function result value scale.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also:</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_describescalarex">dam_describeScalarEx</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><span id="_bookmark34"
class="anchor"></span><strong>dam_describeTable</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method returns the attributes of the current table being
> processed. This method is called from ipExecute to find out about the
> table for which the IP has been called. Pass in NULL for any
> descriptor in which you have no interest.
>
> void dam_describeTable( long hstmt, StringBuffer pCatalog,
> StringBuffer pSchema,
>
> StringBuffer pTableName, StringBuffer pTablePath, StringBuffer
> pUserData)
>
> **Parameters for dam_describeTable**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 33%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The table qualifier as entered in the OA_TABLES table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pSchema</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The table owner as entered in the OA_TABLES table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pTableName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pTablePath</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>Path of the table that is specified in the path column of the
OA_TABLES table. This is an IP-specific field.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pUserData</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The Table_Userdata as entered in the OA_TABLES table. This can be any
string that the IP wants to know about the table.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_describeTableByProcessOrder

> This method returns the attributes of the table with the given
> TableProcessOrder in the join query being processed. This method is
> called from the ipExecute method by the IP to get details for each of
> the tables in the join query. Pass in NULL for any attribute in which
> you have no interest.
>
> int dam_describeTableByProcessOrder( long hstmt,
>
> int iTableProcessOrder,
>
> xo_int piTableNum, StringBuffer pCatalog, StringBuffer pSchema,
> StringBuffer pTableName, StringBuffer pTablePath, StringBuffer
> pUserData)
>
> **Parameters for dam_describeTableByProcessOrder**

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 30%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iTableProcessOrder</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The process order of the table. Numbers start at 0.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piTableNum</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The table number for the table. Tables are numbered in the order that
they appear in the from-clause of the select query starting at 0.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The table qualifier as entered in the OA_TABLES table. Create and
pass in a StringBuffer of DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pSchema</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The table owner as entered in the OA_TABLES table. Create and pass in
a StringBuffer of DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pTableName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the table. Create and pass in a StringBuffer of
DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pTablePath</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>Path of the table specified in the path column of the OA_TABLES
table.</p>
<p>This is IP specific field. Create and pass in a StringBuffer of
DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pUserData</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The Table_Userdata as entered in the OA_TABLES table. This can be any
string that the IP wants to associate with a table. Create and pass in a
StringBuffer of DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - found table with matching iTableProcessOrder.</p>
<p>DAM_NOT_AVAILABLE - invalid iTableProcessOrder. Matching table not
found.</p>
<p>DAM_FAILURE - The query that is</p>
<p>being processed is not a join query.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_describeTableCorrelationName

> This method returns the correlation name (Alias Name) of the current
> table being processed.
>
> void dam_describeTableCorrelationName( long hstmt,
>
> StringBuffer pCorrelationName)
>
> **Parameters for dam_describeTableCorrelationName**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 30%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pCorrelationName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The table alias name in the query. If alias name is not explicitly
specified in the query, an empty string is returned.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_describeTableFunction and dam_describeTableFunctionW

> These methods return the attributes of the table function being
> processed. The dam_describeTableFunction/ dam_describeTableFunctionW
> method is called from the ipExecute method of IP to find out the
> information about the table function for which IP is called. Pass in
> NULL for any attributes that you do not want.
>
> int dam_describeTableFunction (DAM_STMT hstmt, char \* pCatalog,
>
> char \* pSchema, char \* pTableName, char \* pTablePath, char \*
> pUserData,
>
> DAM_HVALEXP_LIST phValExpList, int \* piArgCount)
>
> int dam_describeTableFunctionW (DAM_STMT hstmt, OAWCHAR \* pCatalog,
>
> OAWCHAR \* pSchema, OAWCHAR \* pTableName, OAWCHAR \* pTablePath,
> OAWCHAR \* pUserData,
>
> DAM_HVALEXP_LIST phValExpList, int \* piArgCount)

#### Parameters for dam_describeTableFunction and dam_describeTableFunctionW

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 35%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>IN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>DAM_STMT</p>
</blockquote></td>
<td><blockquote>
<p>The handle of the table object for which the attributes are
requested.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 27%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>OUT</strong></p>
</blockquote></th>
<th colspan="2"></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pCatalog</p>
</blockquote></td>
<td><blockquote>
<p>char *OAWCHAR *</p>
</blockquote></td>
<td><blockquote>
<p>The table qualifier as entered in the OA_TABLES table.</p>
<p>Pass in a buffer of 128 characters.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pSchema</p>
</blockquote></td>
<td><blockquote>
<p>char *OAWCHAR *</p>
</blockquote></td>
<td><blockquote>
<p>The table owner, as entered in the OA_TABLES table.</p>
<p>Pass in a buffer of 128 characters.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pTableName</p>
</blockquote></td>
<td><blockquote>
<p>char *OAWCHAR *</p>
</blockquote></td>
<td><blockquote>
<p>The name of the table.</p>
<p>Pass in a buffer of 128 characters.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pTablePath</p>
</blockquote></td>
<td><blockquote>
<p>char *OAWCHAR *</p>
</blockquote></td>
<td><blockquote>
<p>The path of the table specified in the path column of the OA_TABLES
table. This is an IP-specific field.</p>
<p>Pass in a buffer of 256 characters.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pUserData</p>
</blockquote></td>
<td><blockquote>
<p>char *OAWCHAR *</p>
</blockquote></td>
<td><blockquote>
<p>The table user data, as entered in the OA_TABLES table. This can be
any string that the IP wants to know about the table.</p>
<p>Pass in a buffer of 256 characters.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phValExpList</p>
</blockquote></td>
<td><blockquote>
<p>DAM_HVALEXP_LIST</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the parameter list of the table function.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piArgCount</p>
</blockquote></td>
<td><blockquote>
<p>int*</p>
</blockquote></td>
<td><blockquote>
<p>The number of parameters of the table function.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURNS</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>DAM_ERROR/ DAM_SUCCESS</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_ERROR - if the handle passed is not of the table function.</p>
<p>DAM_SUCCESS - ON SUCCESS</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>dam_evaluateColCond</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method evaluates a search or restriction condition for the IP.
> Use it in cases when you have specified support for a condition type
> but cannot support certain columns.
>
> int dam_evaluateColCond( long hstmt,
>
> long hcond,
>
> int iXoType, Object pColVal, long lColValLen)

#### Parameters for dam_evaluateColCond

> **Parameter Type Description INPUT**

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 21%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>hstmt</p>
<p>hcond</p>
</blockquote></th>
<th><blockquote>
<p>long</p>
<p>long</p>
</blockquote></th>
<th><blockquote>
<p>The statement handle.</p>
<p>The condition handle.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>iXoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The data type. The data can be</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>supplied in a format that corresponds</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>to the column's definition in the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>schema or in any other format for</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>which conversion is supported.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pColVal</p>
</blockquote></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>The Java object containing the value</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>of type iXoType. See Table <a href="#reference-tables">How the</a> <a
href="#reference-tables">methods for Java return a value</a> <a
href="#reference-tables">as an object</a>.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>lColValLen</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The length of the data.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>XO_NULL_DATA - indicates a null</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>value.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>For a character data type, either the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>number of characters or XO_NTS to</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>pass the entire string.</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>For binary, the length of the data.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>1 - condition evaluates to true.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>0 - condition evaluates to false.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><strong>dam_freeBulkValue</strong></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td>Frees the bulk value.</td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td>void dam_freeBulkValue</td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><span id="_bookmark35"
class="anchor"></span><strong>dam_freeResultBuffer</strong></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method frees the result buffer object created by
> dam_allocResultBuffer.
>
> void dam_freeResultBuffer( long hstmt
>
> ResultBuffer pResBuffer)
>
> **Parameters for dam_freeResultBuffer**

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 39%" />
<col style="width: 35%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pResBuffer</p>
</blockquote></td>
<td><blockquote>
<p>ResultBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The result buffer.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_freeRow

> This method frees a row of data. All memory that is associated with
> the row is freed by the OpenAccess SDK SQL engine. Call this method
> after a call to dam_isTargetRow fails.
>
> void dam_freeRow(long hRow)
>
> **Parameters for dam_freeRow**

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 38%" />
<col style="width: 35%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also

- [dam_isTargetRow](#dam_istargetrow)

### dam_freeSetOfConditionList

> This method frees the condition lists that are allocated by
> dam_getOptimalIndexAndConditions and dam_getSetOfConditionListsEx. The
> memory used by this list must be freed once the IP is finished with
> the execution of the query.
>
> int dam_freeSetOfConditionList(long hSetOfCondList)

#### Parameters for dam_freeSetOfConditionList

> **Parameter Type Description INPUT**
>
> hSetOfCondList long The handle to the set of condition lists returned
> by dam_getOptimalIndexAndConditions or dam_getSetOfConditionListsEx.
>
> **RETURN**
>
> int DAM_SUCCESS - memory freed
>
> DAM_FAILURE - error

### See also

- [dam_getOptimalIndexAndConditions](#dam_getoptimalindexandconditions)

- [dam_getSetOfConditionListsEx](#dam_getsetofconditionlistsex)

### dam_getBulkRowStatusArray

> This method returns a byte array to indicate the insert status of each
> row during bulk insert.
>
> byte\[\] dam_getBulkRowStatusArray(long hstmt)

#### Parameters for dam_getBulkRowStatusArray

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> byte\[\] A byte array
>
> DAM_SUCCESS - on success. DAM_FAILURE - on failure.

### dam_getBulkValueToSet

> This method returns an object array for the specified statement handle
> and row element.
>
> Object\[\] dam_getBulkValueToSet(long hstmt, long hRowElem,
>
> xo_long iRowCount)

#### Parameters for dam_getBulkValueToSet

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 33%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRowElem</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row element.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iRowCount</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>Object[]</p>
</blockquote></td>
<td><blockquote>
<p>An array of objects. Object[] - on success. NULL - on failure.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><span id="_bookmark38"
class="anchor"></span><strong>dam_getCol</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method returns the column handle for the given column name. The
> column name is as defined in the OA_COLUMNS table of the schema
> database (see [Schema Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> of the *OpenAccess*
>
> *SDK Programmer\'s Guide*).
>
> long dam_getCol(
>
> long hstmt, String ColumnName)
>
> **Parameters for dam_getCol**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 30%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>ColumnName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the specified column. 0 is returned if the column does
not exist.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_getColByNameAndType

> This method returns the column handle for the type of the current
> store procedure and the specified column number. Use this method to
> get a column of a specific type as defined in the OA_COLUMNTYPE field
> of OA_PROCCOLUMNS table of the schema database (see [Schema Definition
> and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> of the *OpenAccess SDK Programmer\'s Guide*).
>
> long dam_getColByNameAndType( long hstmt,
>
> String wsColName, int iColType)

#### Parameters for dam_getColByNameAndType

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 31%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>wsColName</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The name of the column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iColType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td>Type of column as defined in the</td>
</tr>
<tr class="even">
<td colspan="2" rowspan="7"></td>
<td>schema.</td>
</tr>
<tr class="odd">
<td>For stored procedures, iColType</td>
</tr>
<tr class="even">
<td><blockquote>
<p>parameters can be:</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p>SQL_PARAM_INPUT</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p>SQL_RESULT_COL</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p>SQL_PARAM_OUTPUT</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p>SQL_RETURN_VALUE</p>
</blockquote></li>
</ul></td>
</tr>
</tbody>
</table>

> For tables, iColType parameters can be:

- DAM_COL_IN_SCHEMA

- DAM_COL_IN_RESULT

- DAM_COL_IN_CONDITION

- DAM_COL_IN_USE

- DAM_COL_IN_OUTPUT

> **RETURN**
>
> long The handle to the specified column. 0 is returned if the column
> does not exist.

### dam_getColByNum

> This method returns the column handle for the given column number in
> schema. The column number is based on the order in which columns are
> defined in the OA_COLUMNS table of the schema database (see [Schema
> Definition and
> Management](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/schema-definition-and-management.html)
> of the *OpenAccess SDK Programmer\'s Guide*). The column numbers start
> from 0.
>
> long dam_getColByNum( long hstmt,
>
> int iColumn)

#### Parameters for dam_getColByNum

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 23%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iColumn</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The column number in the schema, starting with 0.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the specified column. A 0 is returned if the column
does not exist.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>dam_getColCount</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to retrieve the number of columns in the various
> categories. Call this method to retrieve the number of columns in any
> one category or in multiple categories. Use this method to quickly
> check if columns of a specific type exist, and if so, how many.
>
> int dam_getColCount( long hstmt,
>
> int iColType,
>
> xo_int piColCount)

#### Parameters for dam_getColCount

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 23%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iColType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Identifies the column type. Use a bitwise OR ( | ) of the following
flags to scan through columns that are in multiple categories:
DAM_COL_IN_SCHEMA - list of all columns as defined in the schema
database.</p>
<p>DAM_COL_IN_RESULT - list of columns that are part of the result set.
DAM_COL_IN_CONDITION - list of columns that are in the where clause.
DAM_COL_IN_USE - list of columns that are either part of the result or
are in the where clause.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piColCount</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The number of columns of the specified iColType. Create and pass in
an object of type xo_int.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the method execution: DAM_SUCCESS - valid column count
returned.</p>
<p>DAM_FAILURE - Error due to invalid iColType.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>dam_getColInCond</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method gets the handle to the column on which a given condition
> applies. Use this method if you are using the
> dam_getSetOfConditionListsEx method to get restrictions on multiple
> columns and then stepping through these conditions. For each condition
> obtained using dam_getFirstCond or dam_getNextCond, call this method
> to determine the column involved in the condition.
>
> long dam_getColInCond(long hcond)

#### Parameters for dam_getColInCond

> **Parameter Type Description INPUT**
>
> hcond long The condition obtained from a DAM_HCONDLIST using
> dam_getFirstCond or dam_getNextCond.
>
> **RETURN**
>
> long The handle to the column associated with the condition.

### See also

- [dam_getFirstCond](#dam_getfirstcond)

- [dam_getNextCond](#dam_getnextcond)

- [dam_getSetOfConditionListsEx](#dam_getsetofconditionlistsex)

### dam_getColOption

> This method is used to retrieve the column options based on Connection
> properties and Column\'s Operator Support setting. The IP can use this
> method to check the aggregate setting of the column, which takes into
> account the global connection setting as well as the column-specific
> setting. Connection properties are set up using dam_setOption and
> column\'s operator support is specified in the column schema.
>
> int dam_getColOption( long hdbc,
>
> long hcol,
>
> int iType, xo_int piValue)

#### Parameters for dam_getColOption

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The DAM connection handle.</p>
<p>Pass 0 if connection properties is to be ignored and only the column-
specific value is to be returned.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Type of information requested: DAM_COL_OPTION_IGNORE_CAS</p>
<p>E_IN_STRINGS - column values are to be considered case sensitive.</p>
<p>DAM_COL_OPTION_IGNORE_BLA</p>
<p>NKS - trailing blanks in the column value are to be ignored.</p>
<p>DAM_COL_OPTION_IGNORE_CAS</p>
<p>E_IN_LIKE - column values are to be</p>
</blockquote></td>
</tr>
</tbody>
</table>

> considered case insensitive in LIKE operation.
>
> **OUTPUT**
>
> piValue xo_int The value of the column information.
>
> **RETURN**
>
> int Status of the call:
>
> DAM_SUCCESS - retrieved the option value.
>
> DAM_FAILURE - error.

### See also

- [dam_setOption](#dam_setoption)

### dam_getColToSet

> This method gets the column handle portion of a value set for an
> update, insert, or input row. A value set consists of the column
> handle and the value for that column. Use the method dam_getValueToSet
> to retrieve the associated value.
>
> long dam_getColToSet(long hRowElem)

#### Parameters for dam_getColToSet

> **Parameter Type Description INPUT**
>
> hRowElem long The value set handle.
>
> **RETURN**
>
> long The handle to the column to be
>
> updated and inserted.

### See also

- [dam_getValueToSet](#_bookmark70)

### dam_getCondRightVal

> This method is used to retrieve the right value of a condition. If the
> condition is for a BETWEEN or a LIKE operator, then the condition may
> contain a right value. A right value is available if dam_describeCond
> returned a non-zero value for the piRightOpType.
>
> Object dam_getCondRightVal(long hcond)

#### Parameters for dam_getCondRightVal INPUT

> **Parameter Type Description**
>
> hcond long The condition handle.
>
> **RETURN**
>
> Object The object that represents the data portion of the condition.
> The format of the data depends on the data type of the condition. See
> Table [How the](#reference-tables) [methods for Java return a
> value](#reference-tables) [as an object](#reference-tables). This
> value is NULL for col=NULL condition.

### See also

- [dam_describeCond](#dam_describecond)

### dam_getCondVal

> This method is used to retrieve the value of a condition.
>
> All conditions except BETWEEN return only one value. If the query
> contains a BETWEEN or LIKE operator, then the condition may contain a
> right value, which you can obtain by calling dam_getCondRightVal.
>
> Object dam_getCondVal(long hcond)

#### Parameters for dam_getCondVal

> **Parameter Type Description INPUT**
>
> hcond long The condition handle.
>
> **RETURN**
>
> Object The object that represents the data portion of the condition.
> The format of the data depends on the data type of the condition. See
> Table [How the](#reference-tables) [methods for Java return a
> value](#reference-tables) [as an object](#reference-tables). This
> value is NULL for col=NULL conditions.

### dam_getFirstCol

> This method is used to navigate through the columns that appear in the
> SELECT, UPDATE and WHERE clause of the SQL query or through the
> columns in the schema definition of the table. Call this method, with
> the column types to be navigated, and then use dam_getNextCol to step
> through the list.
>
> The dam_getFirstCol method is used to:

- Write an IP that is dynamic in that it does not hard-code any column
  names.

- Optimize an IP where it first builds a row with only the columns in
  the WHERE clause and then adds the columns in the SELECT or UPDATE
  part only if dam_isTargetRow returns TRUE.

> long dam_getFirstCol( long hstmt,
>
> int iColType)

#### Parameters for dam_getFirstCol

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 24%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iColType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Identifies the column list to navigate. Use a bitwise OR ( | ) one of
the following flags to scan through columns that are in multiple
categories: DAM_COL_IN_SCHEMA - list of all columns as defined in the
schema database.</p>
<p>DAM_COL_IN_RESULT - list of columns that are part of the result set.
DAM_COL_IN_CONDITION - list of columns that are in the WHERE clause.</p>
<p>DAM_COL_IN_UPDATE_VAL_EXP -</p>
<p>list of all columns that are part of update value expressions.</p>
<p>DAM_COL_IN_USE - list of columns that are either part of the result
or are in the WHERE clause. The row passed to dam_addRowToTable must
include at minimum all the columns in this list.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the first column that has the attributes of iColType;
otherwise, 0.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#_bookmark18">dam_addRowToTable</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

- [dam_getNextCol](#_bookmark53)

- [dam_isTargetRow](#dam_istargetrow)

### dam_getFirstCond

> This method is used to navigate through the conditions in the
> condition list. It gets the first condition in the search or
> restriction list. Use the method dam_getNextCond to go through the
> list.
>
> long dam_getFirstCond( long hstmt,
>
> long hlist)

#### Parameters for dam_getFirstCond

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> hlist long Either the search or the restriction condition list.
>
> **RETURN**
>
> long The first condition. 0 is returned if the list is empty.

### See also

- [dam_getNextCond](#dam_getnextcond)

### dam_getFirstCondList

> This method gets the first condition list from the set of condition
> lists. This method is used to navigate through the set of condition
> lists that was obtained by calling dam_getSetOfConditionListsEx or
> dam_getOptimalIndexAndConditions. This list contains either an
> intersection or a union style set of expressions.
>
> Use the method dam_getNextCondList to go through the list. long
> dam_getFirstCondList(long hset_of_condlist) **Parameters for
> dam_getFirstCondList**

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 30%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hset_of_condlist</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the set of condition</p>
</blockquote></td>
</tr>
</tbody>
</table>

> lists.
>
> **RETURN**
>
> long The first condition list. 0 is returned if the list is empty.

### See also

- [dam_getNextCondList](#dam_getnextcondlist)

- [dam_getOptimalIndexAndConditions](#dam_getoptimalindexandconditions)

- [dam_getSetOfConditionListsEx](#dam_getsetofconditionlistsex)

### dam_getFirstGrantedObject

> This method is used to navigate the granted object list in the GRANT
> and REVOKE commands of the ipDCL method. The object can be a table,
> view, or procedure.
>
> int dam_getFirstGrantedObject( long hstmt, StringBuffer szQualifier,
> StringBuffer szOwner, StringBuffer szName)

#### Parameters for dam_getFirstGrantedObject

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 23%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szQualifier</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The qualifier of the object.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szOwner</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the object.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the object.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><ul>
<li><blockquote>
<p>DAM_SUCCESS - on success</p>
</blockquote></li>
<li><blockquote>
<p>DAM_FAILURE - on failure</p>
</blockquote></li>
<li><blockquote>
<p>DAM_NO_DATA_FOUND - on end of list</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>dam_getFirstGrantee</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to navigate the granted user list in the GRANT and
> REVOKE commands of the ipDCL
>
> method.
>
> int dam_getFirstGrantee( long hstmt, StringBuffer szCatalog,
>
> StringBuffer szGranteeName)

#### Parameters for dam_getFirstGrantee

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 22%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the catalog.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szGranteeName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name will be one of the following:</p>
</blockquote>
<ul>
<li><blockquote>
<p>user name</p>
</blockquote></li>
<li><blockquote>
<p>role name</p>
</blockquote></li>
<li><blockquote>
<p>DAM_PUBLIC_USER_NAME - the</p>
</blockquote></li>
</ul>
<blockquote>
<p>grantee is PUBLIC.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><ul>
<li><blockquote>
<p>DAM_SUCCESS - on success</p>
</blockquote></li>
<li><blockquote>
<p>DAM_FAILURE - on failure</p>
</blockquote></li>
<li><blockquote>
<p>DAM_NO_DATA_FOUND - on end of list</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><span id="_bookmark46"
class="anchor"></span><strong>dam_getFirstIndexCol</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method gets the first column associated with the optimal index
> returned by the dam_getOptimalIndexAndConditions method. Use
> dam_getNextIndexCol to navigate to the next one and
> dam_describeIndexCol to retrieve data about each of the index columns.
>
> long dam_getFirstIndexCol(long hIndex)

#### Parameters for dam_getFirstIndexCol

> **Parameter Type Description INPUT**
>
> hIndex long The index handle as returned by the
> dam_getOptimalIndexAndConditions method.
>
> **RETURN**
>
> long The first column making up the index.
>
> Call dam_describeIndexCol to get
>
> column details.

### See also

- [dam_describeIndexCol](#dam_describeindexcol)

- [dam_getNextIndexCol](#dam_getnextindexcol)

- [dam_getOptimalIndexAndConditions](#dam_getoptimalindexandconditions)

### dam_getFirstInsertRow

> This method gets the first row of data to be used for inserting into
> your data source. Related methods:

- Use the methods dam_getFirstValueSet and dam_getNextValueSet to step
  through the columns of the insert row.

- Use the methods dam_getColToSet and dam_getValueToSet to retrieve the
  corresponding column handle and associated value.

- Use the method dam_describeCol with the column handle to get the name
  and number of the target column.

> long dam_getFirstInsertRow(long hstmt)

#### Parameters for dam_getFirstInsertRow

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The handle to the insert row. 0 is returned if an insert row is
> not available.
>
> Do not use dam_freeRow with this handle to free the row. The
> OpenAccess SDK SQL engine automatically frees it.

### See also

- [dam_describeCol](#_bookmark24)

- [dam_getColToSet](#dam_getcoltoset)

- [dam_getFirstValueSet](#dam_getfirstvalueset)

- [dam_getNextValueSet](#dam_getnextvalueset)

- [dam_getValueToSet](#_bookmark70)

### dam_getFirstPrivilege

> This method is used to navigate the privilege list in the GRANT and
> REVOKE commands of the ipDCL method.
>
> Refer to the
> [Privileges](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/privileges.html)
> section in the *OpenAccess SDK Programmer\'s Guide* for details on
> implementing this method.
>
> int dam_getFirstPrivilege( long hstmt,
>
> xo_int piPrivilegeId, StringBuffer szCatalog, StringBuffer szRoleName)
>
> **Parameters for dam_getFirstPrivilege**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 29%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td>long</td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piPrivilegeId</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The type of the Privilege:</p>
</blockquote></td>
</tr>
<tr class="odd">
<td colspan="2" rowspan="30"></td>
<td><ul>
<li><blockquote>
<p>0 - indicates that the privilege is a</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><blockquote>
<p>Role.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p>System Privilege.</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p>Object Privilege.</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>System Privileges</strong>:</p>
</blockquote></td>
</tr>
<tr class="even">
<td>SQL_SYS_PRIV_CREATE_ANY_IN</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>DEX</p>
</blockquote></td>
</tr>
<tr class="even">
<td>SQL_SYS_PRIV_ALTER_ANY_INDE</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>X</p>
</blockquote></td>
</tr>
<tr class="even">
<td>SQL_SYS_PRIV_DROP_ANY_INDE</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>X</p>
</blockquote></td>
</tr>
<tr class="even">
<td>SQL_SYS_PRIV_GRANT_ANY_PRI</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>VILEGE</p>
</blockquote></td>
</tr>
<tr class="even">
<td>SQL_SYS_PRIV_CREATE_PROCED</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>URE</p>
</blockquote></td>
</tr>
<tr class="even">
<td>SQL_SYS_PRIV_CREATE_ANY_PR</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>OCEDURE</p>
</blockquote></td>
</tr>
<tr class="even">
<td>SQL_SYS_PRIV_DROP_ANY_PRO</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>CEDURE</p>
</blockquote></td>
</tr>
<tr class="even">
<td>SQL_SYS_PRIV_EXECUTE_ANY_P</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>ROCEDURE</p>
</blockquote></td>
</tr>
<tr class="even">
<td>SQL_SYS_PRIV_GRANT_ANY_ROL</td>
</tr>
<tr class="odd">
<td><blockquote>
<p>E</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>SQL_SYS_PRIV_CREATE_TABLE</p>
</blockquote></td>
</tr>
<tr class="odd">
<td>SQL_SYS_PRIV_CREATE_ANY_TA</td>
</tr>
<tr class="even">
<td><blockquote>
<p>BLE</p>
</blockquote></td>
</tr>
<tr class="odd">
<td>SQL_SYS_PRIV_ALTER_ANY_TABL</td>
</tr>
<tr class="even">
<td><blockquote>
<p>E</p>
</blockquote></td>
</tr>
<tr class="odd">
<td>SQL_SYS_PRIV_DROP_ANY_TABL</td>
</tr>
<tr class="even">
<td><blockquote>
<p>E</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 33%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th></th>
<th></th>
<th><blockquote>
<p>SQL_SYS_PRIV_SELECT_ANY_TA BLE SQL_SYS_PRIV_INSERT_ANY_TAB LE
SQL_SYS_PRIV_UPDATE_ANY_TA BLE SQL_SYS_PRIV_DELETE_ANY_TA BLE
SQL_SYS_PRIV_CREATE_VIEW SQL_SYS_PRIV_CREATE_ANY_VIE W
SQL_SYS_PRIV_DROP_ANY_VIEW SQL_SYS_PRIV_CREATE_USER
SQL_SYS_PRIV_CREATE_ANY_US ER SQL_SYS_PRIV_DROP_ANY_USER
SQL_SYS_PRIV_CREATE_ROLE SQL_SYS_PRIV_CREATE_ANY_RO LE
SQL_SYS_PRIV_DROP_ANY_ROLE</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p><strong>Object Privileges:</strong> SQL_OBJ_PRIV_ALTER
SQL_OBJ_PRIV_DELETE SQL_OBJ_PRIV_EXECUTE SQL_OBJ_PRIV_INDEX
SQL_OBJ_PRIV_INSERT SQL_OBJ_PRIV_SELECT SQL_OBJ_PRIV_UPDATE</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the Catalog. Applicable if Privilege Id is returned as
0.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szRoleName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the role.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure DAM_NO_DATA_FOUND -
on end of list</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_getFirstSchemaObject

> This method is used to navigate through the items in the schema object
> list returned by dam_getSchemaObjectList. This method returns a schema
> object. Type cast to the type of objects contained in the list
> (schemaobj_table, schemaobj_column, schemaobj_stat or schemaobj_fkey).
>
> Object dam_getFirstSchemaObject(long obj_list)

#### Parameters for dam_getFirstSchemaObject

#### 

> **Parameter Type Description INPUT**
>
> obj_list long The handle to the list of schema objects.
>
> **RETURN**
>
> Object The Java schema object in the list. 0 is returned at the end of
> the list.
>
> For example, to retrieve an object from a list of DAMOBJ_TYPE_TABLE
> objects:
>
> schemaobj_table pTable =
>
> (schemaobj_table)jOpenAccess SDK SQL
> jdam.dam_getFirstSchemaObject(obj_list)

### See also

- [dam_getSchemaObjectList](#dam_getschemaobjectlist)

### dam_getFirstValExp

> This method gets the first argument passed into a scalar function
> implemented by the IP. Use the method dam_getValueOfExp to get the
> value of the argument.
>
> long dam_getFirstValExp(long hValExpList)

#### Parameters for dam_getFirstValExp

> **Parameter Type Description INPUT**
>
> hValExpList long Argument list handle as passed into the scalar
> function.
>
> **RETURN**
>
> long The handle to the first argument, or 0 if at the end of the list.

### See also

- [dam_getValueOfExp](#_bookmark69)

### dam_getFirstValueSet

> This method gets the first value set of a row that has been retrieved
> from the OpenAccess SDK SQL engine for update, insert, or stored
> procedure processing. A value set consists of the column handle and
> the value
>
> for that column.
>
> Use dam_getColToSet and dam_getValueToSet to get the corresponding
> column handle and the associated value. The method dam_describeCol can
> be used with the column handle to obtain the name and number of the
> target column.
>
> long dam_getFirstValueSet( long hstmt,
>
> long hRow)

#### Parameters for dam_getFirstValueSet

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> hRow long The row handle.
>
> **RETURN**
>
> long The handle to the update/insert value set. 0 is returned if the
> value set is not available.

### See also

- [dam_getColToSet](#dam_getcoltoset)

- [dam_describeCol](#_bookmark24)

- [dam_getValueToSet](#_bookmark70)

### dam_getFirstView

> This method gets the first view from all currently active views.
>
> int dam_getFirstView( long hdbc,
>
> StringBuffer pTableQualifier, StringBuffer pTableOwner, StringBuffer
> pTableName)

#### Parameters for dam_getFirstView

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 35%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The connection handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 24%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>pTableQualifier pTableOwner</p>
<p>pTableName</p>
</blockquote></th>
<th><blockquote>
<p>StringBuffer StringBuffer</p>
<p>StringBuffer</p>
</blockquote></th>
<th><blockquote>
<p>The qualifier name. The owner name.</p>
<p>The view name.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure DAM_NO_DATA_FOUND -
no views exist</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><span id="_bookmark49"
class="anchor"></span><strong>dam_getGroupByCol</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> Use this method to obtain column information about the columns
> specified as part of the GROUP BY clause.
>
> int dam_getGroupByCol( long hstmt,
>
> int iGroupByColNum,
>
> xo_int piColNum, StringBuffer pColName)
>
> **Parameters for dam_getGroupByCol**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 31%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iGroupByColNum</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number of the Group By column to return information about.Valid
numbers:</p>
<p>0 - (Value returned from dam_getInfo (DAM_INFO_GROUP_BY_OPTIMIZ ABLE)
-1)</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piColNum</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The column number as defined in the schema database.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pColName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The column name as defined in the schema database.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the call: DAM_SUCCESS - on success. DAM_FAILURE - wrong
value for iGroupByColNum.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also

- [dam_getInfo](#dam_getinfo)

### dam_getInfo

> This method returns the requested connection and statement level
> information from the OpenAccess SDK SQL engine. See Table [dam_getInfo
> Infotype Options](#dam_getinfo) for the types of information that can
> be returned.
>
> int dam_getInfo(
>
> long hdbc,
>
> long hstmt,
>
> int iInfoType, StringBuffer pStrInfoValue, xo_int pIntInfoValue)

#### Parameters for dam_getInfo

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 24%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The OpenAccess SDK SQL engine</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>connection handle to be used for</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>connection level options. Set to 0 for</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>statement level options.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle to be used for</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>statement level options. Set to 0 for</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>connection level options.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iInfoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The type of information requested.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>See Table <a href="#dam_getinfo">dam_getInfo Infotype</a> <a
href="#dam_getinfo">Options</a> for a list of options.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pStrInfoValue</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>String information type is returned in</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>this object.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pIntInfoValue</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Integer and short integer information</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>types are returned in this object.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Status of the call:</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_SUCCESS - retrieved the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>iInfoType value.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - wrong value for</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>iInfoType or the output buffer is not</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>large enough.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>dam_getInfo Infotype Options</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> **Information Type Description**
>
> DAM_INFO_BLOCKJOIN_IS\_ ENABLED Statement level information. The
> return value is a short
>
> integer with Value 1 if Block Join is enabled for the query. The value
> is 0 if Block Join is disabled.
>
> DAM_INFO_CLIENT_ADDRESS Connection level information. Returns the
> local IP address of the client as a string of the form
> \'*ddd*.*ddd*.*ddd*.*ddd*\' (for example \"100.23.24.25\". The value
> is a string of maximum 128 characters. This option can be used while
> running in DAM_MODE to implement a client IP-based authentication
> scheme.
>
> DAM_INFO_CLIENT_APPLICATION_NAME Connection level information
> indicating the name of the
>
> client application on this connection. The value is an ASCII string of
> maximum length 128.
>
> DAM_INFO_CLIENT_PRODUCT_VERSION Connection level information
> indicating the version of the
>
> client product that the OpenAccess SDK client is accessing for this
> connection. The value is a string of maximum 128 characters.
>
> DAM_INFO_CLIENT_PUBLIC_ADDRESS Connection level information. Returns
> the public IP
>
> address of the client as a string of the form
> \'*ddd*.*ddd*.*ddd*.*ddd*\' (for example \"100.23.24.25\". The value
> is a string of maximum 128 characters. This option can be used while
> running in DAM_MODE to implement a client IP-based authentication
> scheme.
>
> DAM_INFO_CLIENT_TYPE Connection level information indicating the
> OpenAccess SDK client type for this connection. The value is a short
> integer. The possible values are:

- DAM_CLIENTTYPE_ODBC

- DAM_CLIENTTYPE_JDBC

- DAM_CLIENTTYPE_OLEDB

- DAM_CLIENTTYPE_NET

> DAM_INFO_CONNECTION_ALIVE Connection level information. This value is
> returned as a short integer. A value of 0 is returned if connection is
> not active. The IP can cancel its operations and return DAM_FAILURE.
> The SQL Engine then closes the connection.
>
> The ServiceSQLCheckConnectionAlive service attribute must be enabled
> to support this feature. The default is Enable. Refer to the
> *OpenAccess SDK Administrator\'s Guide* for more information about the
> ServiceSQLCheckConnectionAlive attribute.
>
> DAM_INFO_CONNECTION_MODEL Global information. Returns a short integer
> value indicating the threading mode of the service that is associated
> with this data source. The threading modes are:

- DAM_CONNECTMODEL_THREADPOOL

- DAM_CONNECTMODEL_PROCESS

- DAM_CONNECTMODEL_THREAD

> DAM_INFO_DAM_HDBC Statement level information. Returns the connection
> handle for the given statement. The value is returned as void \*. This
> must be casted to DAM_CONN_DA pointer.
>
> DAM_INFO_DISTINCT_OPTIMIZABLE Statement level information. The return
> value is greater
>
> than 0 if the current query has a SELECT DISTINCT clause that can be
> processed by the IP. The value is returned as a short integer. If the
> IP decides to handle the DISTINCT processing then it should call the
> dam_setOption to set the DAM_STMT_OPTION_DISTINCT to DAM_PROCESS_OFF.
>
> DAM_INFO_ESTIMATED_NUM_SUBQUERIES Statement level information. Returns
> the expected
>
> number of subqueries to process the given query. The value returned is
> an int data type.
>
> DAM_INFO_FETCH_BLOCK_SIZE Connection level information for the number
> of rows to be fetched each time EXECUTE is called. This information is
> used if the IP is implementing cursor based select processing. The
> value is returned as a short integer.
>
> DAM_INFO_GROUP_BY_OPTIMIZABLE Statement level option. The returned
> value is greater than
>
> 0 if the current query has a GROUP BY clause that can be processed by
> the IP. This value is a short integer and it indicates the number of
> columns in the GROUP BY clause. Use dam_getGroupByCol to get
> information about the columns in the GROUP BY statement.
>
> DAM_INFO_IP_CLASS Connection level information. Returns the
> DataSourceIPClass attribute value. The value is an ASCII string of
> maximum length 128.
>
> DAM_INFO_IP_SUPPORT_UNICODE_INFO Connection level information. Returns
> a value that
>
> indicates that the IP supports Unicode options in ipGetInfo and
> ipSetInfo. The value returned is of a short data type.
>
> DAM_INFO_JOIN_QUERY_SIZE Statement level option. Returns the size (in
> the number of tables) of the join query being processed. This value is
> returned as a short integer. A value of 1 is returned if the query is
> a simple single table select.
>
> An IP can call this method from EXECUTE to check if the current query
> being processed is part of a join and call dam_getInfo
> (DAM_INO_QUERY_PROCESS_ORDER) to get the process order of the current
> table.
>
> DAM_INFO_LANGUAGE_ID Connection level information. Returns the
> LANGUAGE ID. The value returned is an ASCII string having a maximum
> length of 128 characters.
>
> DAM_INFO_LOGFILE Connection level information. Returns the LOG file
> name. The value returned is an ASCII string having a maximum length of
> 128 characters.
>
> DAM_INFO_MODE Global information (set hdbc and hstmt to 0). Returns a
> short integer with the value DAM_MODE_SERVER or
>
> DAM_MODE_LOCAL to indicate if the IP module has been loaded by an
> OpenAccess SDK Server or an OpenAccess SDK Local Server. Use this
> information to control the behavior of your IP for standalone
> operation and for client/server operation without having to have
> separate builds.
>
> DAM_INFO_OASQL_INI Global information (set hdbc and hstmt to 0).
> Returns the path and the file name that is being used for the
> OpenAccess SDK configuration information. The information is returned
> as a character string.
>
> DAM_INFO_ORDER_BY_OPTIMIZABLE Statement level option. The returned
> value is greater than
>
> 0 if the current query has an ORDER BY clause that can be processed by
> the IP. This value is a short integer and it indicates the number of
> columns in the ORDER BY clause. Use dam_getOrderByCol( ) to get
> information about the columns in the ORDER BY statement.
>
> DAM_INFO_ORIGINAL_QUERY_STRINGW Statement level information. Retrieves
> the SQL string as
>
> specified by the application. The information is returned as a wide
> character string. Can be used to implement your own logging of
> queries.
>
> DAM_INFO_OUTER_TABLE\_ CUR_ROWCOUNT Statement level information.
> Returns the outer table
>
> current rowcount. If query is not a join, 0 is returned.When the IP is
> processing inner query, it can get row count of outer table. The IP
> can use this information to decide if inner table can be processed as
> TableRowset. If outer table row count is a small number like 1, the IP
> can process the inner query directly and not use TableRowset. The
> return value is a 64-bit integer.
>
> DAM_INFO_PASSTHROUGH_QUERY Statement level information. The return
> value is greater
>
> than 0 if the current query is being executed in pass- through mode.
> The value is a short integer.
>
> DAM_INFO_QUERY_CANCEL Statement level information. The return value is
> greater than 0 if the current query execution was requested to be
> cancelled. The value is a short integer. When the IP detects a CANCEL
> request, it must clean up its resources that were allocated to handle
> this statement and return a DAM_FAILURE.
>
> DAM_INFO_QUERY_MAX_ROWS Statement level information. Returns the least
> of MaxRows or TOP N value provided in the query. The value returned is
> an int64 data type.
>
> DAM_INFO_QUERY_HAS_NOWAIT Statement level information. Indicates
> whether the NOWAIT clause was specified in the SELECT FOR UPDATE
> statement. Used to determine whether or not to wait when a row needs
> to be locked but it is already locked. The return value is a short
> integer 1 if there is a NOWAIT clause and 0 otherwise.
>
> DAM_INFO_QUERY_HAS_SEARCH\_ CONDITION Statement level information.
> Indicates whether or not a
>
> statement has a WHERE clause. This can be used to detect operations
> that are on the entire table. The return value is a short integer 1 if
> there is a where clause and 0 if there is no WHERE clause.
>
> DAM_INFO_QUERY_IS_NESTED Statement level option. This method will
> return a short integer, 1 if the current statement represents a inner
> query and 0 if it represents an outer query. Note that if the outer
> query is a join the queries on the tables in the join list are
> considered to be outer queries. For example, the table DEPT is
> considered an inner query in the following query: SELECT \* FROM emp
> WHERE dept_id in (SELECT dept_id FROM dept WHERE location=\'SJ\')
>
> DAM_INFO_QUERY_PROCESS_ORDER Statement level option. When executing a
> JOIN on *M*
>
> tables, the returned value indicates which of the *M* tables is being
> processed. A value of 0 is returned for the first table and a value of
> *M*-1 for the last table. This value is returned as a short integer.
>
> DAM_INFO_QUERY_TIMEOUT Statement level option. Returns the query
> timeout value in seconds. This value is returned as an integer. A
> value of 0 is returned if query does not have a timeout set.
>
> DAM_INFO_QUERY_TOP_ROWS Statement level information. For a query
> containing a TOP N clause, the return value is N if the TOP N can be
> pushed down to the IP. The value is 0 (DAM_MAX_ROWS_UNLIMITED) if
> query does not have TOP N option or option cannot be exposed to the
> IP. The value is a 64-bit integer.
>
> DAM_INFO_QUERY_TYPE Statement level information. Returns the type of
> query that is being processed. The query type value is returned as a
> short integer that is one of the constants defined for the statement
> type in the EXECUTE method.
>
> DAM_INFO_RM_GUID Connection level information. Retrieves the resource
> manager ID of the current MTS transaction. The information is returned
> as a character string of 36 characters. This information is not
> available during a ipStartTransaction IP call.
>
> DAM_INFO_SCHEMA_PATH Connection level information. Retrieves the
> schema path for the connection. The information is returned as a
> character string.
>
> DAM_INFO_SERVICEIPPATH Connect level information. Returns the path for
> the IP files as set in the ServiceIPPath attribute. The value is an
> ASCII string of maximum length 256.
>
> DAM_INFO_SESSION_CIPHER_SUITE Connection level information. Returns
> the cipher suite
>
> used for communication between the OpenAccess SDK client and server.
> The value returned is a string data type.
>
> DAM_INFO_SESSION_CRYPTO_PROTOCOL_VERSIO N
>
> Connection level information. Returns the cryptographic protocol
> version that can be used to create an SSL connection between the
> OpenAccess SDK client and server. The value returned is an integer
> data type.
>
> Valid or expected values are the following SSL/TLS versions:

- DAM_VERSION_UNDEFINED -- 0

- DAM_VERSION_TLS1_2 -- 3

- DAM_VERSION_TLS1_3 -- 4

- Default when SSL Enabled: DAM_VERSION_TLS1_3 -- 4

> DAM_INFO_SESSION_TOKEN Connect level information. Returns session
> token as defined by the DataSourceSessionToken attribute. The value is
> an ASCII string of maximum length 1024.
>
> For example:
>
> Session from \$ClientInfo connected to \$DataSourceName
>
> DAM_INFO_SET_FUNCTIONS_IN_QUERY Statement level information. Returns
> the number of set
>
> functions in the given query. The value returned is an int data type.
>
> DAM_INFO_SETLOCALE Connection level information. Returns the LOCALE
> information of the client. The value returned is an ASCII string
> having a maximum length of 128 characters.
>
> DAM_INFO_STMT_IP_CONTEXT Statement level information. Retrieves the
> context information stored for the entire query. The returned value is
> a void \* (pass in an address of a void \* variable for pInfoValue and
> a size of void \* for integer).
>
> DAM_INFO_TXN_ID Connection level information. Retrieves the
> transaction ID of the current MTS transaction. The information is
> returned as a character string of 36 characters. This information is
> not available during startTransaction IP call.
>
> DAM_INFO_TXN_INFO Connection level information. Retrieves the
> transaction information that is associated with the current MTS
> transaction. The information is returned as a character string of up
> to 240 characters. This information is not available during a
> startTransaction IP call.
>
> DAM_INFO_TXN_ISO_LEVEL Connection level information. Returns the
> isolation level of the connection. The value returned is an int data
> type.
>
> DAM_INFO_TXN_NESTING_LEVEL Connection level information. Returns the
> nesting level of

the transaction. The value returned is of a short data type.

DAM_INFO_TXN_TYPE Connection level information. Provides information
about

> the current mode of transaction processing. The options are:

- DAM_TXN_IMPLICIT - the currently active transaction is a local
  > transaction with AUTO COMMIT ON option set on the connection. In
  > this mode the ODBC driver or ADO data provider generates a COMMIT
  > after each non- SELECT statement. This will result in the
  > EndTransaction() being called with the specified option. This will
  > then be followed by an ip_xxx_StartTransaction() call to start a new
  > transaction.

- DAM_TXN_EXPLICIT - the currently active transaction is a local
  > transaction with AUTO COMMIT OFF. In this mode the application must
  > call COMMIT or ROLLBACK. This will result in the
  > ip_xxx_EndTransaction() being called with the specified option,
  > which will be followed by an ip_xxx_StartTransaction() to start a
  > new transaction.

> DAM_INFO_USER Connection level information. Returns the user name of
> the connection. The value returned is an ASCII string having a maximum
> length of 128 characters.

### See also

- [dam_getGroupByCol](#_bookmark49)

- [dam_getOrderByCol](#dam_getorderbycol)

### dam_getInputRow

> This method retrieves input arguments for a stored procedure call.
>
> Use the methods dam_getFirstValueSet and dam_getNextValueSet to
> navigate through the arguments contained in the returned row.
>
> long dam_getInputRow(long hstmt)

#### Parameters for dam_getInputRow

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The handle to a row containing the input arguments for the stored
> procedure call. 0 if no arguments.

### See also

- [dam_getFirstValueSet](#dam_getfirstvalueset)

- [dam_getNextValueSet](#dam_getnextvalueset)

### dam_getIP_hcol

> This method is used to retrieve the column information that was
> previously set using dam_setIP_hcol.
>
> The IP uses the dam_setIP_hcol method to associate column specific
> information in the form of an integer key.
>
> long dam_getIP_hcol( long hstmt, long hcol)

#### Parameters for dam_getIP_hcol

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> hcol long The column handle.
>
> **RETURN**
>
> long The index into a vector; the IP is using the vector to store the
> object that is created by the IP for storing information at statement
> level for the column.

### See also

- [dam_setIP_hcol](#dam_setip_hcol)

### dam_getIP_hstmt

> This method is used to retrieve state information for a statement
> being processed. Use this method when performing cursor-based
> execution of a statement in which the IP returns partial results in
> each call. The IP must save the state to know where to begin next
> time.
>
> long dam_getIP_hstmt(long hstmt)
>
> Table 189. Parameters for dam_getIP_hstmt
>
> Parameter Type Description
>
> **INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The index into a vector; the IP uses this vector to store the
> object created by the IP for storing information at statement level.

### dam_getJoinColValue

> This method returns the column value of the table already processed.
> This method is used by the IP to get values for the join conditions
> for use in processing the inner table.
>
> Object dam_getJoinColValue( long hstmt,
>
> long hcol,
>
> int iColNum,
>
> int iXoType, xo_int piValLen, xo_int piValStatus)

#### Parameters for dam_getJoinColValue

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iColNum</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The column number of the column for which the value is being
requested.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iXoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The data type in which to return the column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piValLen</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The length of the data in the returned object.</p>
<p>XO_NULL_DATA - null data</p>
<p>XO_NTS - null terminated string&gt;= 0 - length of the data</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piValStatus</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the method call. DAM_SUCCESS - all data retrieved.
DAM_SUCCESS_WITH_RESULT_P</p>
<p>ENDING - more data is available and</p>
<p>can be retrieved by calling this</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 23%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2" rowspan="5"></th>
<th><blockquote>
<p>method again. This will occur for large</p>
<p>LONGVARBINARY data.</p>
</blockquote></th>
</tr>
<tr class="odd">
<th><blockquote>
<p>DAM_FAILURE - error in getting the</p>
</blockquote></th>
</tr>
<tr class="header">
<th><blockquote>
<p>data. It is possible that the data</p>
</blockquote></th>
</tr>
<tr class="odd">
<th><blockquote>
<p>cannot be converted to the XO_Type</p>
</blockquote></th>
</tr>
<tr class="header">
<th><blockquote>
<p>asked for.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>The object that represents the data</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>portion of the column value; the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>format of the data depends on the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>type of the iXoType specified. Use</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>this object only if piValLen is not set</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>to XO_NULL_DATA. See Table <a href="#reference-tables">How</a> <a
href="#reference-tables">the methods for Java return a</a> <a
href="#reference-tables">value as an object</a>.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>dam_getJoinQuery</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method takes the statement handle, hstmt, as the parameter and
> returns a handle to the query. This method returns the current select
> query which is being processed by the Server to the IP. This is
> required as the user\'s query can contain many subqueries.
>
> long dam_getJoinQuery(long hstmt)

#### Parameters for dam_getJoinQuery

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 26%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>dam_getMemTree</strong></p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the query.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> This method returns the tree to use for allocating memory associated
> with the specified statement. Memory trees to use at initialization
> and connection are passed in as arguments to those method calls.
>
> long dam_getMemTree(long hstmt)

#### Parameters for dam_getMemTree

> **Parameter Type Description INPUT**

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 25%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>hstmt</p>
</blockquote></th>
<th><blockquote>
<p>long</p>
</blockquote></th>
<th><blockquote>
<p>The statement handle.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><span id="_bookmark53"
class="anchor"></span><strong>dam_getNextCol</strong></p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the memory tree.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> This method is used to navigate through the columns that appear in the
> SELECT and WHERE clauses of the SQL query or through the columns in
> the schema definition of the table. Call this method after calling
> dam_getFirstCol.
>
> long dam_getNextCol(long hstmt)

#### Parameters for dam_getNextCol

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The handle to the next column in the list. A 0 is returned at the
> end of the list.

### See also

- [dam_getFirstCol](#dam_getfirstcol)

### dam_getNextCond

> This method is used to navigate through the conditions in the search
> and restriction condition list. Call this method after calling the
> dam_getFirstCond method.
>
> long dam_getNextCond( long hstmt, long hlist)

#### Parameters for dam_getNextCond

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hlist</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The condition list that was returned by</p>
</blockquote></td>
</tr>
</tbody>
</table>

> dam_getOptimalIndexAndConditions or by dam_getRestrictionList.
>
> **RETURN**
>
> long The handle to the next condition in the list. A 0 is returned at
> the end of the list.

### See also

- [dam_getFirstCond](#dam_getfirstcond)

- [dam_getOptimalIndexAndConditions](#dam_getoptimalindexandconditions)

- [dam_getRestrictionList](#dam_getrestrictionlist)

### dam_getNextCondList

> This method is used to navigate through the set of condition lists
> that was obtained by calling dam_getSetOfConditionListsEx or
> dam_getOptimalIndexAndConditions. This method gets the next condition
> list from the set of conditions.
>
> long dam_getNextCondList(long hset_of_condlist)

#### Parameters for dam_getNextCondList

> **Parameter Type Description INPUT**
>
> hset_of_condlist long The handle to the set of condition lists.
>
> **RETURN**
>
> long The next condition list handle. 0 is returned at the end of the
> list.

### See also

- [dam_getOptimalIndexAndConditions](#dam_getoptimalindexandconditions)

- [dam_getSetOfConditionListsEx](#dam_getsetofconditionlistsex)

### dam_getNextGrantedObject

> This method is used to navigate the granted object list in the GRANT
> and REVOKE commands. This method is used in the ipDCL method. The
> object can be a table, view, or procedure.
>
> int dam_getFirstGrantedObject( long hstmt,
>
> StringBuffer szQualifier, StringBuffer szOwner, StringBuffer szName)

#### Parameters for dam_getNextGrantedObject

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 24%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szQualifier</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The qualifier of the object.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szOwner</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The owner of the object.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the object.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure DAM_NO_DATA_FOUND -
on end of list</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>dam_getNextGrantee</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method is used to navigate the granted user list in the GRANT and
> REVOKE commands. This method is used in the ipDCL method.
>
> int dam_getNextGrantee( long hstmt, StringBuffer szCatalog,
>
> StringBuffer szGranteeName)

#### Parameters for dam_getNextGrantee

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 30%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the catalog.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szGranteeName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name will be one of the following:</p>
</blockquote>
<ul>
<li><blockquote>
<p>user name.</p>
</blockquote></li>
<li><blockquote>
<p>role name.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_PUBLIC_USER_NAME - to</p>
</blockquote></li>
</ul>
<blockquote>
<p>indicate that the grantee is PUBLIC.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure DAM_NO_DATA_FOUND - on end of list

### dam_getNextIndexCol

> This method gets the next column that is associated with the optimal
> index that is returned by the dam_getOptimalIndexAndConditions method.
>
> Related methods:

- dam_getFirstIndexCol navigates to the first index column.

- dam_describeIndexCol retrieves information about each of the index
  columns.

> long dam_getNextIndexCol(long hIndex)

#### Parameters for dam_getNextIndexCol

> **Parameter Type Description INPUT**
>
> hIndex long The index handle as returned by the
> dam_getOptimalIndexAndConditions method.
>
> **RETURN**
>
> long The next column making up the
>
> index. Call dam_describeIndexCol to obtain information about this
> column. A 0 is returned at the end of the list.

### See also

- [dam_describeIndexCol](#dam_describeindexcol)

- [dam_getFirstIndexCol](#_bookmark46)

- [dam_getOptimalIndexAndConditions](#dam_getoptimalindexandconditions)

### dam_getNextInsertRow

> This method gets the next row of data to be used for inserting into
> your data source. Related functions:

- dam_getFirstValueSet and dam_getNextValueSet step through the columns
  of the insert row.

- dam_getColToSet and dam_getValueToSet obtain the corresponding column
  handle and the associated value.

- dam_describeCol with the column handle retrieves the name and number
  of the target column.

> long dam_getNextInsertRow(long hstmt)

#### Parameters for dam_getNextInsertRow

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The handle to the insert row. 0 is returned if an insert row is
> not available.

### See also

- [dam_describeCol](#_bookmark24)

- [dam_getColToSet](#dam_getcoltoset)

- [dam_getFirstValueSet](#dam_getfirstvalueset)

- [dam_getFirstValueSet](#dam_getfirstvalueset)

- [dam_getValueToSet](#_bookmark70)

### dam_getNextJoinStmt

> This method gets the OpenAccess SDK SQL engine statement handle for
> the next table in the join. The IP uses this method to get the next
> statement handle for implementing the join pushdown. For example, if
> tables A and B are joined in the listed order, then when processing
> table A, you can call this method to get the handle for table B. In
> this case, table B is referred to as the inner table.
>
> long dam_getNextJoinStmt(long hstmt_outer)
>
> **Parameters for dam_getNextJoinStmt**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt_outer</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle of the current statement.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle of the next table in the join.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_getNextPrivilege

> This method is used to navigate the privilege list in the GRANT and
> REVOKE commands. It is used in the ipDCL method.
>
> int dam_getNextPrivilege( long hstmt,
>
> xo_int piPrivilegeId, StringBuffer szCatalog, StringBuffer szRoleName)
>
> **Parameters for dam_getNextPrivilege**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 29%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td>long</td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piPrivilegeId</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Type of the Privilege:</p>
</blockquote>
<ul>
<li><blockquote>
<p>0 - indicates that the privilege is a Role.</p>
</blockquote></li>
<li><blockquote>
<p>System Privilege</p>
</blockquote></li>
<li><blockquote>
<p>Object Privilege</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>System Privileges: SQL_SYS_PRIV_CREATE_ANY_IN DEX
SQL_SYS_PRIV_ALTER_ANY_INDE X SQL_SYS_PRIV_DROP_ANY_INDE X
SQL_SYS_PRIV_GRANT_ANY_PRI VILEGE SQL_SYS_PRIV_CREATE_PROCED URE
SQL_SYS_PRIV_CREATE_ANY_PR OCEDURE SQL_SYS_PRIV_DROP_ANY_PRO CEDURE
SQL_SYS_PRIV_EXECUTE_ANY_P ROCEDURE SQL_SYS_PRIV_GRANT_ANY_ROL E</p>
<p>SQL_SYS_PRIV_CREATE_TABLE SQL_SYS_PRIV_CREATE_ANY_TA BLE
SQL_SYS_PRIV_ALTER_ANY_TABL E SQL_SYS_PRIV_DROP_ANY_TABL</p>
<p>E</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 33%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th></th>
<th></th>
<th><blockquote>
<p>SQL_SYS_PRIV_SELECT_ANY_TA BLE SQL_SYS_PRIV_INSERT_ANY_TAB LE
SQL_SYS_PRIV_UPDATE_ANY_TA BLE SQL_SYS_PRIV_DELETE_ANY_TA BLE
SQL_SYS_PRIV_CREATE_VIEW SQL_SYS_PRIV_CREATE_ANY_VIE W
SQL_SYS_PRIV_DROP_ANY_VIEW SQL_SYS_PRIV_CREATE_USER
SQL_SYS_PRIV_CREATE_ANY_US ER SQL_SYS_PRIV_DROP_ANY_USER
SQL_SYS_PRIV_CREATE_ROLE SQL_SYS_PRIV_CREATE_ANY_RO LE
SQL_SYS_PRIV_DROP_ANY_ROLE</p>
<p>Object Privileges: SQL_OBJ_PRIV_ALTER SQL_OBJ_PRIV_DELETE
SQL_OBJ_PRIV_EXECUTE SQL_OBJ_PRIV_INDEX SQL_OBJ_PRIV_INSERT
SQL_OBJ_PRIV_SELECT SQL_OBJ_PRIV_UPDATE</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>szCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the Catalog. Applicable if Privilege Id is returned as
0.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szRoleName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the role.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure DAM_NO_DATA_FOUND -
on end of list</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_getNextSchemaObject

> This method is used to navigate through the items in the schema object
> list returned by the dam_getSchemaObjectList method. This method
> returns the Java schema object. It should be type cast to the type of
> objects contained in the list (schemaobj_table, schemaobj_column,
> schemaobj_stat, or schemaobj_fkey).
>
> Object dam_getNextSchemaObject(long obj_list)

#### Parameters for dam_getNextSchemaObject

#### 

> **Parameter Type Description INPUT**
>
> obj_list long The handle to the list of schema objects.
>
> **RETURN**
>
> Object The Java schema object in the list. A NULL is returned at the
> end of the list.
>
> For example, to retrieve an object from a list of DAMOBJ_TYPE_TABLE
> objects:
>
> schemaobj_table pTable = (schemaobj_table)jOpenAccess SDK SQL
>
> jdam.dam_getNextSchemaObject(obj_list)

### dam_getNextValExp

> This method gets the next argument that is passed into a scalar
> function implemented by the IP. Use the method
> [dam_getValueTypeOfExp](#dam_getvaluetypeofexp) to get the value of
> the argument.
>
> long dam_getNextValExp(long hValExpList)

#### Parameters for dam_getNextValExp

> **Parameter Type Description INPUT**
>
> hValExpList long The argument list handle as passed into the scalar
> function.
>
> **RETURN**
>
> long The handle to the next argument. A 0 is returned if at the end of
> the list.

### dam_getNextValueSet

> This method gets the next value set of an update or an insert row. A
> value set consists of the column handle and the value for that column.

#### Related functions:

- dam_getColToSet and dam_getValueToSet retrieve the corresponding
  column handle and the associated value.

- dam_describeCol can be used with the column handle to get the name and
  number of the target column.

> long dam_getNextValueSet(long hstmt)

#### Parameters for dam_getNextValueSet

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The handle to the update/insert value set. A 0 is returned if
> value set is not available.

### See also

- [dam_describeCol](#_bookmark24)

- [dam_getColToSet](#dam_getcoltoset)

- [dam_getValueToSet](#_bookmark70)

### dam_getNextView

> This method gets the next view from all currently active views.
>
> int dam_getNextView( long hdbc,
>
> StringBuffer pTableQualifier, StringBuffer pTableOwner, StringBuffer
> pTableName)
>
> **Parameters for dam_getNextView**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 33%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Connection handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pTableQualifier</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>Qualifier name.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pTableOwner</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>Owner name.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pTableName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>View name.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure DAM_NO_DATA_FOUND -
end of views list</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_getOptimalIndexAndConditions

> This method is used to support single or multiple column indexes. Use
> this function to get the optimal index and the set of search
> conditions associated with the columns of that index. This information
> is used by the IP to restrict the rows that are read.
>
> Each condition list contains conditions on all or some of the columns
> in the index. The relationship between the individual condition
> elements in the list is that of AND. This means that each row that the
> OpenAccess SDK SQL engine builds for a condition list must satisfy all
> conditions in that list and must be validated using the
> dam_isTargetRow method. The order of the conditions corresponds to the
> order of the columns in the index. The relationship between each
> condition list is that of OR. This means that the IP builds a set of
> rows for each condition list.
>
> The following requirements must be met to generate an optimal index:

- One or more column names must be marked as having an index in the
  OA_STATISTICS table.

- Support for one or more operators must be enabled.

> int dam_getOptimalIndexAndConditions( long hstmt,
>
> long phIndex,
>
> long phSetOfCondList)

#### Parameters for dam_getOptimalIndexAndConditions

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 28%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phIndex</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle of the index identified as optimal for the statement. This
value is 0 if no optimal index was identified. Use the method
dam_describeIndex to get details about the index. Use
dam_getFirstIndexCol, dam_getNextIndexCol, and dam_describeIndexCol to
get details about the columns in the index.</p>
<p>Your IP code must create this object before calling this method.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phSetOfCondList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>If an optimal index is found, this is a handle to the set of
conditions that are present in the query. (Otherwise, this is 0.)
Navigate it by using the dam_getFirstCondList and dam_getNextCondList
methods.</p>
<p>Your code must create this object</p>
<p>before calling this method.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> The IP must call dam_freeSetOfConditionList to free this handle when
> finished with the query.
>
> **RETURN**
>
> int Status of the call:
>
> DAM_SUCCESS - on success DAM_FAILURE - error

### See also

- [dam_describeIndexCol](#dam_describeindexcol)

- [dam_freeSetOfConditionList](#dam_freesetofconditionlist)

- [dam_getFirstCondList](#dam_getfirstcondlist)

- [dam_getFirstIndexCol](#_bookmark46)

- [dam_getNextCondList](#dam_getnextcondlist)

- [dam_getNextIndexCol](#dam_getnextindexcol)

- [dam_isTargetRow](#dam_istargetrow)

### dam_getOrderByCol

> This method returns the column name and position in the schema for the
> specified ORDER BY column.
>
> int dam_getOrderByCol( long hstmt,
>
> int iOrderByColNum,
>
> xo_int piColNum, StringBuffer pColName, xo_int piSortOrder)
>
> **Parameters for dam_getOrderByCol**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 31%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iOrderByColNum</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The column number of the ORDER BY column (the column for which
information is to be returned).</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piColNum</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The column number as defined in the schema database.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pColName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The column name as defined in the</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 30%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th></th>
<th></th>
<th><blockquote>
<p>schema database.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>piSortOrder</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The sort order for the column:</p>
</blockquote>
<ul>
<li><blockquote>
<p>SQL_ORDER_ASC - Ascending</p>
</blockquote></li>
<li><blockquote>
<p>SQL_ORDER_DESC - Descending</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Status of the call: DAM_SUCCESS - on success DAM_FAILURE - wrong
value for iOrderByColNum</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_getOriginalQueryParamCount

> This method returns the number of parameters specified in the original
> query.
>
> int dam_getOriginalQueryParamCount( long hstmt,
>
> xo_int piParamCount)

#### Properties of dam_getOriginalQueryParamCount

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 33%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piParamCount</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The number of parameters</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the call:</p>
</blockquote></td>
</tr>
</tbody>
</table>

- DAM_SUCCESS - all data retrieved

- DAM_FAILURE - error in getting the data

### See also

- [dam_getOriginalQueryParamValue](#dam_getoriginalqueryparamvalue)

### dam_getOriginalQueryParamValue

> This method returns the type, value, and length of the parameter
> specified in the original query.
>
> object dam_getOriginalQueryParamValue( long hstmt,
>
> int iParam,
>
> xo_int piXoType, xo_long plValLen, xo_int piValStatus)
>
> **Properties of dam_getOriginalQueryParamValue**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 32%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iParam</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The parameter index. Its value starts</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>from zero.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piXoType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The data type of the result value.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>plValLen</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The length of the data returned in the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>object:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>XO_NULL_DATA - null data</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>XO_NTS - null terminated string</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>&gt;= 0 - length of the data</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piValStatus</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the method call:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_SUCCESS - all data retrieved.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_SUCCESS_WITH_RESULT_P</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>ENDING - data is partially retrieved.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>For the result sets of</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>LONGVARBINARY,</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>WLONGVARCHAR, and</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>LONGVARCHAR types, the IP needs</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>to call on the same parameter index</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>repeatedly to get the complete result</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>set. Once the complete result set is</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>retrieved, the function returns</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_SUCCESS.</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - error in getting the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>data.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>object</p>
</blockquote></td>
<td><blockquote>
<p>The object that represents the data</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>portion of the result value. The format</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>of the data depends on the type of the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>data type returned in piXoType. Use</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>this object only if the pIValLen is not</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>set to XO_NULL_DATA. See Table</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p><a href="#reference-tables">How the methods for Java return</a></p>
<p><a href="#reference-tables">a value as an object</a>.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also

- [dam_getOriginalQueryParamCount](#dam_getoriginalqueryparamcount)

### dam_getQuery

> This method returns the query handle of the SQL statement for the
> current table that is being processed. The query handle can be used to
> get complete information about the query using the SQL Engine Parse
> Tree API. For more information, see [SQL Engine parse tree methods for
> Java](#sql-engine-parse-tree-methods-for-java).
>
> long dam_getQuery(long hstmt)
>
> **Parameters for dam_getQuery**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 35%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the query.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_getQueryFirstResultValue

> This method returns the first result value of the subquery. It is used
> by the IP to get the result values of the subquery.
>
> Object dam_getQueryFirstResultValue( long hquery,
>
> xo_int piXoType, xo_int piValLen, xo_int piValStatus)

#### Parameters for dam_getQueryFirstResultValue

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The subquery handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piXoType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The data type of the result value.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piValLen</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The length of the data pointed to by</p>
<p>*ppVal:</p>
<p>XO_NULL_DATA - null data. XO_NTS - null terminated string.</p>
<p>&gt;= 0 - length of the data.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piValStatus</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the method call: DAM_SUCCESS - all data retrieved.
DAM_NO_DATA_FOUND - no results</p>
</blockquote></td>
</tr>
</tbody>
</table>

> are available. Result set is empty. DAM_FAILURE - error in getting the
> data. Most likely the data cannot be converted to the XO_Type asked
> for.
>
> **RETURN**
>
> The object that represents the data portion of the result value. The
> format of the data depends on the type of the data type returned in
> piXoType. Use this object only if the piValLen is not set to
> XO_NULL_DATA. See Table [How the methods for Java
> return](#reference-tables) [a value as an object](#reference-tables).

### dam_getQueryNextResultValue

> This method returns the next result value of the subquery. It is used
> by the IP to get the result values of the subquery.
>
> Object dam_getQueryNextResultValue( long hquery,
>
> xo_int piXoType, xo_int piValLen, xo_int piValStatus)

#### Parameters for dam_getQueryNextResultValue

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 30%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td>long</td>
<td><blockquote>
<p>The subquery handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piXoType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The data type of the result value.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piValLen</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The length of the data returned in the object:</p>
<p>XO_NULL_DATA - null data XO_NTS - null terminated string</p>
<p>&gt;= 0 - length of the data</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piValStatus</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Status of the method call:</p>
</blockquote>
<ul>
<li><blockquote>
<p>DAM_SUCCESS - all data retrieved.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_NO_DATA_FOUND - reached end of the list.</p>
</blockquote></li>
<li><blockquote>
<p>DAM_FAILURE - error in getting the data. Most likely the data cannot
be</p>
</blockquote></li>
</ul>
<blockquote>
<p>converted to the requested XO_Type.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **RETURN**
>
> The object that represents the data portion of the result value. The
> format of the data depends on the type of the data type returned in
> piXoType. Use this object only if the piValLen is not set to
> XO_NULL_DATA. See [How](#reference-tables) [the methods for Java
> return a](#reference-tables) [value as an object](#reference-tables).

### dam_getRestrictionList

> This method is used to get the handle to the list of restriction
> conditions on a column. This method is used in conjunction with the
> dam_getOptimalIndexAndConditions method to process as few rows as
> possible.
>
> When using restriction lists, a row is built for evaluation if, and
> only if, it matches a search condition (returned by
> dam_getOptimalIndexAndConditions), and it matches at least one
> restriction condition in each of the restriction lists. The IP
> navigates through the search condition list, if there is one, returned
> by the dam_getOptimalIndexAndConditions. For each search condition,
> the IP builds rows that satisfy at least one condition in each of the
> restriction lists.
>
> Related functions:

- Use this function with dam_getFirstCond, dam_getNextCond, and
  dam_describeCond.

- Use dam_getRestrictionList on other columns to further restrict the
  number of rows processed.

- Use dam_setOption to mark a restriction expression as evaluated by the
  IP to prevent the OpenAccess SDK SQL engine from attempting to
  evaluate it.

> long dam_getRestrictionList( long hstmt,
>
> long hcol)

#### Parameters for dam_getRestrictionList

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> hcol long The handle of the column for which the restriction is
> requested.
>
> **RETURN**
>
> long The restriction condition list; navigate it by using the methods
> dam_getFirstCond and dam_getNextCond. 0 is returned if no restrictions
> apply on the specified column.

### Examples

> In a query of the form:
>
> SELECT \* FROM emp WHERE dept_id = 1 or dept_id = 2;
>
> calling dam_getRestrictionList on column dept_id returns a restriction
> list with conditions C1 and C2, and column dept_id.
>
> The first condition, C1, has the operator set to \'=\' and the value
> set to 1. The second condition, C2, has the operator set to \'=\' and
> the value set to 2.
>
> The IP should build rows where the dept_id is 1 and rows where the
> dept_id is 2.
>
> /\* Get restriction on column dept_id assuming hDeptId is the \*/
>
> /\* column handle \*/
>
> hCondList = jdam_getRestrictionList(hstmt, hDeptId); hCond1 =
> jdam_getFirstCond(hstmt, hCondList);
>
> /\* call dam_describeCond() to get the operator type, operand and the
> \*/
>
> /\* data type \*/
>
> hCond2 = jdam_getNextCond(hstmt, hCondList);

### See also

- [dam_describeCond](#dam_describecond)

- [dam_getFirstCond](#dam_getfirstcond)

- [dam_getNextCond](#dam_getnextcond)

- [dam_getOptimalIndexAndConditions](#dam_getoptimalindexandconditions)

- [dam_getRestrictionList](#dam_getrestrictionlist)

- [dam_setOption](#dam_setoption)

### dam_getRole

> This method returns the user information in the CREATE ROLE and DROP
> ROLE commands. It is used in the ipDCL method.
>
> void dam_getRole(
>
> long hstmt, StringBuffer szCatalog, StringBuffer szRoleName)

### for Commands

> CREATE ROLE RoleName DROP USER RoleName
>
> **Parameters for dam_getRole**

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 38%" />
<col style="width: 35%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The database catalog.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szRoleName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the role.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_getRowBuffer

> This method returns the RowBuffer object that the IP uses to add
> values to.
>
> The call to dam_getRowBuffer should not cause a new block of memory to
> be allocated and then freed when dam_isTargetRow is called.
>
> void dam_getRowBuffer( RowBuffer szCatalog, RowBuffer szRoleName)

#### Parameters for dam_getRowBuffer

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 37%" />
<col style="width: 36%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szCatalog</p>
</blockquote></td>
<td><blockquote>
<p>RowBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The database catalog.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szRoleName</p>
<p><strong>See also</strong></p>
</blockquote></td>
<td><blockquote>
<p>RowBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the role.</p>
</blockquote></td>
</tr>
</tbody>
</table>

- [dam_isTargetRow](#dam_istargetrow)

### dam_getSchemaObjectList

> This method returns the list of schema objects of the specified type.
> Use this method in the ipDDL method to find information about tables,
> columns, indexes, and foreign key references used in CREATE TABLE and
> CREATE INDEX statements. Also use this method for DROP TABLE and DROP
> INDEX statements.
>
> long dam_getSchemaObjectList( long hstmt,
>
> int iSchemaType)
>
> **Parameters for dam_getSchemaObjectList**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 34%" />
<col style="width: 41%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iSchemaType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Type of schema information requested. All types are returned as a
DAM_OBJ type, which should be cast to the requested type.</p>
<p>DAMOBJ_TYPE_TABLE - return a list with a single entry for a table to
be created or dropped. Each entry is of type schemaobj_table.</p>
<p>DAMOBJ_TYPE_COLUMN - return a list with an entry for each column to
be created. Each entry is of type schemaobj_column.</p>
<p>DAMOBJ_TYPE_STAT - return a list with an entry for each index column
to be created by a CREATE INDEX statement or one entry for the index to
be dropped. Each entry is of type schemaobj_stat.</p>
<p>DAMOBJ_TYPE_FKEY - return a list with an entry for each foreign key
relationship. Each entry is of type schemaobj_fkey.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>DAM_OBJ_LIST</p>
</blockquote></td>
<td><blockquote>
<p>List of requested schema types. NULL if no objects of that type
exist.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#ipddl">ipDDL</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### dam_getSetOfConditionListsEx

> This method is used to retrieve expressions from the WHERE clause on
> one or more columns in the form of AND or OR expressions. It can be
> used to retrieve all the expressions in the WHERE clause by passing in
> 0 for the column handle.
>
> As in the case of the condition list returned by
> dam_getRestrictionList, each of the condition lists contained in this
> set can be marked as evaluated by using the dam_setOption method.
>
> The OpenAccess SDK SQL engine handles all condition lists not marked
> as evaluated. This feature can be used to take a look at all the
> available expressions but only handle the ones it can. If the
> condition list returned is a partial list, as indicated by the
> pbPartialLists output, then the OpenAccess SDK SQL engine
>
> evaluates all the conditions. When finished with the list, the IP must
> call dam_freeSetOfConditionList to free the set of conditions list.
>
> See [Advanced
> Optimization](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/advanced-optimization.html)
> in the *DataDirect OpenAccess SDK Programmer\'s Guide* for more
> information.
>
> long dam_getSetOfConditionListsEx( long hstmt,
>
> long iType,
>
> long hcol,
>
> xo_int pbPartialLists)

#### Parameters for dam_getSetOfConditionListsEx

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 30%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iType</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The type of condition list requested: SQL_SET_CONDLIST_INTERSECT</p>
<p>- transform the where clause into a set of AND conditions and return
these as a list. This is valid only if IP_SUPPORT_UNION_CONDLIST is</p>
<p>set to 0. SQL_SET_CONDLIST_UNION -</p>
<p>transform the where clause into a set of OR conditions and return
these as a list. This is valid only if IP_SUPPORT_UNION_CONDLIST is</p>
<p>set to 1.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column to retrieve expressions on.</p>
<p>Set to 0 to specify all columns.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pbPartialLists</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>TRUE - the condition list provided to the IP is a partial list. This
happens in cases where the query contains expressions like (A=B). In
this case, the OpenAccess SDK SQL engine builds temporary condition
lists which contain all the conditions that can be exposed to IP. The IP
cannot mark these condition lists as evaluated.</p>
<p>The OpenAccess SDK SQL engine will be forced to evaluate all the
original condition lists of the search expression.</p>
<p>FALSE - the condition list provided to</p>
<p>the IP contains the full expression.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **RETURN**
>
> long The search condition list. Navigate it by using the
> dam_getFirstCondList and dam_getNextCondList methods. A 0 is returned
> if no search list is available. The IP must call
> dam_freeSetOfConditionList to this handle when finished with the
> query.

### See also

- [dam_getFirstCondList](#dam_getfirstcondlist)

- [dam_getNextCondList](#dam_getnextcondlist)

- [dam_freeSetOfConditionList](#dam_freesetofconditionlist)

- [dam_getRestrictionList](#dam_getrestrictionlist)

- [dam_setOption](#dam_setoption)

### dam_getTableSearchExp

> This method returns the search condition (filter condition) and join
> conditions for the given table. This method is called from ipExecute
> by the IP to get the filter conditions and join conditions for each
> table in the join query.
>
> int dam_getTableSearchExp( long hstmt,
>
> int iTableNum, long phSearchExp, xo_int piJoinType, xo_int phJoinExp
> xo_int pbPartial)

#### Parameters for dam_getTableSearchExp

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 30%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iTableNum</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number of the table about which to get the conditions. Numbers
start at 0.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phSearchExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the search expression that contains all the filter
conditions applicable for the table. Use damex_describeLogicExp to
get</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 23%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2"></th>
<th><blockquote>
<p>details of the logical search expression.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>piJoinType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Type of the Join: SQL_JOIN_LEFT_OUTER - left outer join</p>
<p>SQL_JOIN_RIGHT_OUTER - right outer join</p>
<p>SQL_JOIN_FULL_OUTER - full outer join</p>
<p>SQL_JOIN_INNER - inner join SQL_JOIN_OLD_STYLE - join condition
specified in the WHERE clause. Implies inner join.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phJoinExp</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the search expression that contains all the join
conditions for the table. Use damex_describeLogicExp to get details of
the logical search expression.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pbPartial</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>TRUE - Indicates that the complete expressions in the WHERE clause
and JOIN condition are returned FALSE - Indicates that expressions
containing correlated sub-queries were not included in the returned
search expression.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - found table with matching iTableNum DAM_NOT_AVAILABLE -
invalid iTableNum. Matching table not found. DAM_FAILURE - Requested
iTableNum &gt; 0 and query is not a join.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>dam_getTableStmt</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method gets the OpenAccess SDK SQL engine statement handle for
> the tableNum in the join. This is used by the IP to get the statement
> handle for implementing the join pushdown.
>
> long dam_getTableStmt( long hstmt_outer, int iTableNum)

#### Parameters for dam_getTableStmt

> **Parameter Type Description**
>
> **INPUT**
>
> hstmt_outer long The statement handle of the current statement.
>
> iTableNum int The table number for the table for which to get the
> conditions. Numbers start at 0.
>
> **RETURN**
>
> hstmt long The statement handle of the TableNum in the join.
>
> If a matching TableNum is not found, 0 is returned.

### dam_getTableFunctionArgList

> This method gets, during the ipSchema method call, the parameter list
> of the table function. The obtained value expression list is used to
> get each individual arguments passed to the table function using
> [dam_getFirstValExp](#dam_getfirstvalexp) and
> [dam_getNextValExp](#dam_getnextvalexp).
>
> DAM_HVALEXP_LIST dam_getTableFunctionArgList (DAM_OBJ pObj)

#### Parameters for dam_getTableFunctionArgList

> **Parameter Type Description INPUT**
>
> pObj DAM_OBJ The handle of the table object for which the parameters
> list is requested.
>
> **RETURNS**
>
> DAM_HVALEXP_LIST The handle to the parameters list.

### dam_getUpdateRow

> This method gets the row of data to be used for updating the selected
> rows in the database. The processing for UPDATE is as for SELECT, up
> to the point of finding the row in the data source that matches the
> conditions in the query. At this point, update processing requires the
> update values specified in the query to be used to modify the selected
> row in the data source.
>
> Related functions:

- Use dam_getFirstValueSet and dam_getNextValueSet to step through the
  columns of the UPDATE row.

- Use dam_getColToSet and dam_getValueToSet to obtain the corresponding
  column handle and the associated value.

- Use dam_describeCol method with the column handle to get the name and
  number of the target column.

> This method, dam_getUpdateRow, should be called after dam_isTargetRow
> returns true to get update values based on the selected row in case
> the update values are derived from expressions.
>
> long dam_getUpdateRow( long hstmt,
>
> long hTargetRow)

#### Parameters for dam_getUpdateRow

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle of the current statement.
>
> hTargetRow long The handle of the row for which the update operation
> will be applied. This would be a row that passes the dam_isTargetRow
> call.
>
> **RETURN**
>
> long The handle to the update row. 0 is returned if an update row is
> not available. This handle is required by dam_getFirstValueSet. Do not
> call dam_freeRow on this handle to free this row. It is automatically
> freed by the OpenAccess SDK SQL engine.

### See also

- [dam_describeCol](#_bookmark24)

- [dam_getColToSet](#dam_getcoltoset)

- [dam_getFirstValueSet](#dam_getfirstvalueset)

- [dam_isTargetRow](#dam_istargetrow)

### dam_getUpdateRowAsExp

> This method gets the update row that contains the values of columns
> and the corresponding update expressions. Using this method, the IP
> can directly send the UPDATE expression and SEARCH expression to the
> backend.
>
> In row-based mode, dam_getUpdateRow is used when the IP first searches
> for target records and then updates each selected row.

#### Related functions:

#### 

- Use damex_getFirstUpdateSet and damex_getNextUpdateSet to get the
  column and the UPDATE expression.

- Use damex_describeCol with the column handle to obtain the column
  details.

- Use damex_describeValExp and related methods to get the UPDATE
  expression for the column.

> long dam_getUpdateRowAsExp(long hstmt)

#### Parameters for dam_getUpdateRowAsExp

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle of the current statement.
>
> **RETURN**
>
> long The handle to the update row. 0 is returned if an update row is
> not available. This handle can be passed to damex_getFirstUpdateSet
> and damex_getNextUpdateSet.
>
> Do not call dam_freeRow on this handle to free this row. It is
> automatically freed by the OpenAccess SDK SQL engine.

### See also

- [dam_getUpdateRow](#dam_getupdaterow)

- [damex_describeCol](#_bookmark85)

- [damex_describeValExp](#damex_describevalexp)

- [damex_getFirstUpdateSet](#damex_getfirstupdateset)

- [damex_getNextUpdateSet](#damex_getnextupdateset)

### dam_getUser

> This method returns the user information in the CREATE USER and DROP
> USER commands of the ipDCL method.
>
> void dam_getUser(
>
> long hstmt, StringBuffer szCatalog, StringBuffer szUserName,
> StringBuffer szPassword, StringBuffer szUserData)

### Command syntax

> CREATE USER UserName \[IDENTIFIED\]
>
> {BY Password \| EXTERNALLY}\] \[USERDATA \'UserData\'\]
>
> DROP USER UserName

#### Parameters for dam_getUser

<table>
<colgroup>
<col style="width: 31%" />
<col style="width: 24%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The database catalog. Not supported in this version.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szUserName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the user.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>szPassword</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The password of the user. The password string will be
DAM_OPS_PASSWORD, to indicate that the password was specified as
EXTERNALLY.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>szUserData</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The user data string.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>NONE</p>
</blockquote></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><span id="_bookmark69"
class="anchor"></span><strong>dam_getValueOfExp</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method gets the value of the argument passed into a scalar
> function implemented by the IP. Use the methods dam_getFirstValExp and
> dam_getNextValExp to step through the argument list.
>
> Object dam_getValueOfExp( long pMemTree, long hValExpList, long
> hValExp,
>
> int iXoType, xo_int piStatus)

#### Parameters for dam_getValueOfExp

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pMemTree</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The memory tree to use for temporary</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 31%" />
<col style="width: 22%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2"></th>
<th><blockquote>
<p>storage.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hValExpList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The list of arguments as passed into</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>the scalar function.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The argument for which you want to</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>retrieve the data.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iXoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The data type in which to get the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>value. It can be any data type that</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>can be a conversion target of the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>argument's data type as defined in</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>the scalar function definition.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piStatus</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the method call:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_SUCCESS - all data retrieved.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_SUCCESS_WITH_RESULT_P</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>ENDING - more data is available and</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>should be retrieved by calling this</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>method again. This will occur for a</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>large LONGVARBINARY,</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>LONGVARCHAR , and</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>WLONGVARCHAR data.</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - error in getting the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>data; most likely the data cannot be</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>converted to the specified XO_Type.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>The value of the argument. The Java</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>type of the data will correspond to the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>iXoType value. See Table <a href="#reference-tables">How the</a></p>
<p><a href="#reference-tables">methods for Java return a value</a> <a
href="#reference-tables">as an object</a>. A NULL Object is</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>returned if the underlying value is</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>NULL data.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#dam_getfirstvalueset">dam_getFirstValueSet</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_getnextvalueset">dam_getNextValueSet</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><span id="_bookmark70"
class="anchor"></span><strong>dam_getValueToSet</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method gets the value portion of a value object. A value object
> consists of the column handle and the value for that column. It is
> retrieved from a row by using the methods dam_getFirstValueSet and
> dam_getNextValueSet.
>
> The XoType specifies how you want the value to be returned.
>
> You can obtain the type of the column by using dam_describeCol on the
> column handle returned by dam_getColToSet.
>
> Object dam_getValueToSet( long hRowElem, int iXoType, xo_int piStatus)
>
> **Parameters for dam_getValueToSet**

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 24%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hRowElem</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Value set handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iXoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The data type in which to get the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>value.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piStatus</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the method call:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_SUCCESS - all data retrieved.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_SUCCESS_WITH_RESULT_P</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>ENDING - more data is available and</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>should be retrieved by calling this</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>method again. This will occur for a</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>large LONGVARBINARY data.</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - error in getting the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>data. Most likely the data cannot be</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>converted to the XO_Type asked for.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>The data object. The java type of the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>data will correspond to the iXoType</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>value. See Table <a href="#reference-tables">How the methods</a></p>
<p><a href="#reference-tables">for Java return a value as an</a> <a
href="#reference-tables">object</a>. A NULL Object is returned if</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>the underlying value is NULL data.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#_bookmark24">dam_describeCol</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#dam_getcoltoset">dam_getColToSet</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_getfirstvalueset">dam_getFirstValueSet</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#dam_getnextvalueset">dam_getNextValueSet</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### dam_getValueTypeOfExp

> This method gets the value type of the argument passed into a scalar
> function implemented by the IP. This method is used when the output
> type of the scalar function is determined based on an argument type.
>
> Use the methods dam_getFirstValExp and dam_getNextValExp to step
> through the argument list.
>
> int dam_getValueTypeOfExp( long pMemTree,
>
> long hValExpList, long hValExp)

#### Parameters for dam_ValueTypeOfExp

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 25%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>Parameter</p>
</blockquote></th>
<th><blockquote>
<p>Type</p>
</blockquote></th>
<th><blockquote>
<p>Description</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p><strong>INPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pMemTree</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The memory tree to use for temporary</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>storage.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hValExpList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The list of arguments as passed into</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>the scalar function.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The argument for which you want to</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>retrieve the data.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The data type of the value</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>expression. See Table <a href="#reference-tables">OpenAccess</a> <a
href="#reference-tables">SDK Data Types and Java Type</a> <a
href="#reference-tables">When Adding Value</a>.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#dam_getfirstvalexp">dam_getFirstValExp</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#dam_getnextvalexp">dam_getNextValExp</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>dam_ip_ddl</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method processes IP DDL commands.
>
> int dam_ip_ddl( long hdbc, long hstmt,
>
> int iStmtType)
>
> **Parameters for dam_ip_ddl**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 35%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The connection handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 30%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>iStmtType</p>
</blockquote></th>
<th><blockquote>
<p>int</p>
</blockquote></th>
<th><blockquote>
<p>The type of DDL statement:</p>
<p>DAM_CREATE_TABLE - create a</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>table.</p>
<p>DAM_ALTER_TABLE - alter a table. DAM_DROP_TABLE - drop a table.
DAM_CREATE_INDEX - create an index.</p>
<p>DAM_DROP_INDEX - drop an index. DAM_CREATE_VIEW - create a view.</p>
<p>DAM_DROP_VIEW - drop a view.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - On Success DAM_NOT_AVAILABLE - On failure</p>
</blockquote></td>
</tr>
</tbody>
</table>

### dam_isSearchPatternObject

> This method checks if any of the arguments of the schema object have
> pattern values.
>
> int dam_isSearchPatternObject(Object pSearchObj)

#### Parameters for dam_isSearchPatternObject

> **Parameter Type Description IN**
>
> pSearchObj Object The schema search Java object.
>
> **RETURN**
>
> int 1 - pattern values are in at least one argument.
>
> 0 - pattern values are not in any argument.

### dam_isSchemaTableFunction() and dam_isSchemaTableFunctionW

> This method checks, during the ipSchema method call, whether the table
> object passed is a table function or a table. The method returns TRUE
> if the table object is a table function. Otherwise, it returns FALSE.
>
> short int dam\_ isSchemaTableFunction(DAMOBJ_TABLEOBJ\* pObj) Short
> int dam\_ isSchemaTableFunctionW(DAMOBJ_TABLEOBJ\* pObj)

#### Parameters for dam\_ isSchemaTableFunction() and dam\_ isSchemaTableFunctionW

> **Parameter Type Description**
>
> **INPUT**
>
> pObj DAMOBJ_TABLEOBJ\* The handle of the table schema object.
>
> **RETURNS**
>
> TRUE/FALSE short int TRUE- if the argument is a table function.
>
> FALSE- if the argument is a table.

### dam_isTableFunction

> This method checks, during the ipExecute method call, whether the
> table handle passed is a table function or a table. It returns TRUE if
> the table handle is a table function. Otherwise, it returns FALSE.
>
> short dam_isTableFunction (DAM_STMT hstmt)

#### Parameters for dam_isTableFunction

> **Parameter Type Description INPUT**
>
> hstmt DAM_HSTMT The handle of the table schema object.
>
> **RETURNS**
>
> TRUE/FALSE short DAM_TRUE- if the argument is a table function.
>
> DAM_FALSE-if the argument is a regular table.

### dam_isTargetRow

> This method evaluates the row against the WHERE clause of the active
> SQL statement. It returns DAM_TRUE if the row matches the WHERE clause
> and DAM_FALSE if not. If DAM_TRUE, call dam_addRowToTable. If
> DAM_FALSE, call dam_freeRow to free the row.
>
> DAM_ERROR is returned if the row handle is invalid or if all the
> required column values have not been set. If the table has index(s)
> defined and the query contains restrictions on index columns, this
> method assumes that all rows provided by the IP satisfy the index
> conditions and does not perform further validation.
>
> int dam_isTargetRow( long hstmt, long hRow)

#### Parameters for dam_isTargetRow

#### 

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> hRow long The row handle.
>
> **RETURN**
>
> Int DAM_TRUE - The row matches the restrictions.
>
> DAM_FALSE - The row does not match the restrictions.
>
> DAM_ERROR - The row handle is invalid or not all required column
> values have been set.

### See also

- [dam_addRowToTable](#_bookmark18)

- [dam_freeRow](#dam_freerow)

### dam_isTargetRowForConditionList

> This method is used to verify if the target row matches the condition
> list.
>
> This method is useful when the IP uses UNION condition lists and
> builds target rows for each of the condition lists returned from
> dam_getSetOfConditionListsEx. The IP can call this method to verify if
> the target row was already processed for any of the previous condition
> lists.
>
> int dam_isTargetRowForConditionList( long stmt,
>
> long hRow,
>
> long hCondList)

#### Restriction on method usage:

> The IP should not call dam_setOption(DAM_CONDLIST_OPTION, hcondList,
> DAM_CONDLIST_OPTION_EVALUATION, DAM_PROCESSING_OFF) on any of the
> condition lists.

#### Parameters for dam_isTargetRowForConditionList

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 36%" />
<col style="width: 38%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Row handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hCondList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Condition list handle.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **RETURN**
>
> int DAM_TRUE - if row is a target row for the condition list.
>
> DAM_FALSE - if row is not a target row for the condition list.
>
> DAM_ERROR - on error.

### See also

- [dam_getSetOfConditionListsEx](#dam_getsetofconditionlistsex)

- [dam_setOption](#dam_setoption)

### dam_setInfo

> This method sets the specified global, connection or statement level
> information to be used by the OpenAccess SDK SQL engine. See Table
> [Information Type for dam_setInfo](#dam_setinfo) for the types of
> information that can be set.
>
> int dam_setInfo( long hdbc,
>
> long hstmt,
>
> int iInfoType, String pStrInfoValue, int intVal)

#### Parameters for dam_setInfo

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hdbc</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The OpenAccess SDK SQL engine</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>connection handle to be used for</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>connection level options. Set to 0 for</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>statement level options.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle to be used for</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>statement level options. Set to 0 for</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>connection level options.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iInfoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The information to set. See Table</p>
<p><a href="#dam_setinfo">Information Type for dam_setInfo</a>.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pInfoValue</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>String type options are passed in</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>through this argument.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>intVal</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Integer type options are passed in</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>through this argument.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> int The status of the call:
>
> DAM_SUCCESS - set the iInfoType value.
>
> DAM_FAILURE - wrong value for iInfoType or the input buffer is not
> large enough.

#### Information Type for dam_setInfo

> **Information Type Description**
>
> DAM_INFO_STMT_IP_CONTEXT Statement level option; used to store a key
> to IP defined data structure that needs to persist across all queries
> executed to handle the requested query.
>
> This value can be retrieved using the dam_getInfo. Joins and
> sub-queries are examples where the IP is called multiple times to
> execute a given query.
>
> The key input value pInfoValue is stored as an integer. The input
> value iInfoValueLen is set to size of void \*.
>
> The methods dam_setIP_hstmt and dam_getIP_hstmt are used to reference
> IP-defined data structures for a sub- query on a specific table.
>
> Use dam_setInfo and dam_getInfo to store IP-specific information that
> is accessible during the execution of any of the involved tables.

#### Parameters for dam_getValueToSet

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hRowElem</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Value set handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iXoType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The data type in which to get the value.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piStatus</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the method call: DAM_SUCCESS - all data retrieved.
DAM_SUCCESS_WITH_RESULT_P</p>
<p>ENDING - more data is available and should be retrieved by calling
this method again. This will occur for a large LONGVARBINARY data.</p>
<p>DAM_FAILURE - error in getting the data. Most likely the data cannot
be converted to the XO_Type asked for.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td><blockquote>
<p>The data object. The java type of the data will correspond to the
iXoType</p>
</blockquote></td>
</tr>
</tbody>
</table>

> value. See Table [How the methods](#reference-tables) [for Java return
> a value as an](#reference-tables) [object](#reference-tables). A NULL
> Object is returned if the underlying value is NULL data.

### See also

- [dam_getInfo](#dam_getinfo)

- [dam_getIP_hstmt](#dam_getip_hstmt)

- [dam_getNextValueSet](#dam_getnextvalueset)

- [dam_setInfo](#dam_setinfo)

- [dam_setIP_hstmt](#dam_setip_hstmt)

### dam_setIP_hcol

> This method is used to associate column-specific information with the
> corresponding OpenAccess SDK SQL engine column handle. Use this method
> to associate a column with some data that can be used later when
> processing values for that column. The saved column-specific
> information can be retrieved later by calling dam_getIP_hcol.
>
> The column-specific information is an object that can reference any
> information. Implement your IP to create a vector to store these
> objects in and then save the index into this vector using this method.
>
> int dam_setIP_hcol( long hstmt, long hcol, long ip_hcol)
>
> **Parameters for dam_getsetIP_hcol**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>ip_hcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>An index into a vector. The vector stores objects allocated by the IP
for storing column-specific information.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Status of the method call: DAM_SUCCESS - Value is associated.</p>
<p>DAM_FAILURE - Error.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also

- [dam_getIP_hcol](#dam_getip_hcol)

### dam_setIP_hstmt

> This method is used to save state information for a statement that is
> being processed. Use this method when performing cursor-based
> execution of the statement in which the IP returns partial results in
> each call. It must save the state to know where to begin next time.
> The saved state is in the form of an object that can be retrieved
> later by calling dam_getIP_hstmt. Implement your IP to create a vector
> to store these objects in, and then save the index into this vector
> using this method.
>
> void dam_setIP_hstmt( long hstmt,
>
> long ip_hstmt_index)

#### Parameters for dam_setIP_hstmt

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> ip_hstmt_index long An index into a vector, into which is saved the
> object allocated by the IP for storing information at statement level.

### See also

- [dam_getIP_hstmt](#dam_getip_hstmt)

### dam_setJoinOrder

> This method is used to set the join order for the tables in the query.
> This method is called for each table present in the query. The
> sequence in which it is called dictates the join order for those
> tables. The tables of the Select query can be accessed using
> dam_getJoinQuery first for getting Query handle, and
> damex_getFirstTable and damex_getNextTable for getting tables in the
> Select Query.
>
> int dam_setJoinOrder(long htable)

#### Parameters for dam_setJoinOrder

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 37%" />
<col style="width: 35%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>IN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>htable</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The table handle</p>
</blockquote></td>
</tr>
</tbody>
</table>

> **RETURN**
>
> int Status of the method call:
>
> DAM_SUCCESS - Value is associated DAM_FAILURE - Error

### dam_setOption

> This method sets the options related to the operations of the
> OpenAccess SDK SQL engine. Options can be set at the connection, the
> statement, or the conditions list level. See [Connection Options
> Values for](#connection-options-values-for-dam_setoption)
> [dam_setOption](#connection-options-values-for-dam_setoption),
> [Statement Options for
> dam_setOption](#statement-options-for-dam_setoption), and [Condition
> List Options for
> dam_setOption](#condition-list-options-for-dam_setoption) for
> information about the options that you can set.
>
> int dam_setOption( int iOptionType,
>
> long pObjectHandle, int iOption,
>
> int lOptionValue)

#### Parameters for dam_SetOption

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 28%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>iOptionType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The type of option:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>DAM_CONN_OPTION</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>DAM_STMT_OPTION</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>DAM_CONDLIST_OPTION</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pObjectHandle</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td>The object handle to which the option</td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td>applies. It can be one of the following</td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>object handles:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>Connection handle.</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>Statement handle.</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>Condition list handle.</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iOption</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The option to set. See <a
href="#connection-options-values-for-dam_setoption">Connection</a> <a
href="#connection-options-values-for-dam_setoption">Options Values
for</a> <a
href="#connection-options-values-for-dam_setoption">dam_setOption</a>
for more information</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>about the option.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iOptionValue</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The value to set for the option</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>iOption. See <a
href="#connection-options-values-for-dam_setoption">Connection
Options</a></p>
<p><a href="#connection-options-values-for-dam_setoption">Values for
dam_setOption</a> for the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>option.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The status of the method call</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>DAM_SUCCESS - option set.</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>DAM_FAILURE - error in setting the</p>
</blockquote></li>
</ul></td>
</tr>
</tbody>
</table>

> specified option for the specified object type.

### Connection Options Values for dam_setOption

> Set the option type to DAM_CONN_OPTION and the object handle to the
> current connection handle.

#### Connection Options Values for dam_setOption

> **Option (iOption) Option Value**
>
> DAM_CONN_OPTION_BLANKS\_ IN_STRINGS Determines whether the OpenAccess
> SDK SQL engine
>
> ignores trailing blanks in strings when performing string comparisons
> and LIKE processing. The actual string values in and out of the IP are
> not stripped. This is useful when the database stores information with
> space padding but the user in the query does not provide the padded
> blanks. Valid values are:

- DAM_BIS_IGNORE_NONE (default) - compare as is unless the
  > SQL_OP_IGNORE_BLANKS bit is set in the OA_SUPPORT field of the
  > column.

- DAM_BIS_IGNORE_TRAILING - ignore trailing blanks and compare; applies
  > to all columns.

> If this option is set, the strings \'Joe\' and \'Joe \' are considered
> equal.
>
> DAM_CONN_OPTION_CASE_IN_LIKE Determines whether LIKE processing is
> case sensitive.
>
> Valid values are:

- DAM_CIL_IGNORE_NONE - case sensitive unless the
  > SQL_OP_IGNORE_CASE_IN_LIKE bit is set in the OA_SUPPORT field of the
  > column.

- DAM_CIL_IGNORE_ALL (default) - case insensitive. Applies to all
  > columns.

> DAM_CONN_OPTION_CASE\_ IN \_STRINGS Determines whether the OpenAccess
> SDK SQL engine
>
> ignores case when performing string compares. This option is useful
> when the database has case insensitive data. Valid values are:

- DAM_CIS_IGNORE_NONE (default) - compare as is unless the
  > SQL_OP_IGNORE_CASE_IN_STRINGS bit is set in the OA_SUPPORT field of
  > the column.

- DAM_CIS_IGNORE_ALL - perform case insensitive string comparisons.

> This is a global setting. To apply this to specific columns, leave
> this option as DAM_CIS_IGNORE_NONE and set the
> SQL_OP_IGNORE_CASE_IN_STRINGS flag in the OA_SUPPORT field of the
> schema definition for those columns.
>
> DAM_CONN_OPTION_INDEX_OPTIMIZATION Determines whether the OpenAccess
> SDK SQL engine
>
> performs optimization when identifying a search column. This option
> should not be changed unless your IP handles most of the optimization.
> Valid values are:

- DAM_INDEX_IGNORE_NONE (default) - perform default optimization.

- DAM_INDEX_IGNORE_ALL - disable default optimization.

> DAM_CONN_OPTION_JOINORDER_USING\_ FKEY Determines whether OA_FKEYS
> information is used
>
> when determining the join order. Valid values are:

- DAM_PROCESSING_OFF - ignore foreign fkeys in determining the join
  > order.

- DAM_IPROCESSING_ON (default) - make use of foreign keys in determining
  > the join order as detailed in the Join Processing section in the
  > *OpenAccess SDK Programmer\'s Guide*.

> DAM_CONN_OPTION_JOINORDER_USING_SEARCHC Determines whether search
> conditions are used when ONDITION determining the join order. Valid
> values are:

- DAM_PROCESSING_OFF - ignore search conditions in determining the join
  > order.

- DAM_IPROCESSING_ON (default) - make use of search conditions in
  > determining the join order as detailed in the Join Processing
  > section in the *OpenAccess SDK Programmer\'s Guide*.

> DAM_CONN_OPTION_JOINORDER_USING_STARJOIN Enables star Join detection
> and optimization. Valid values
>
> are:

- DAM_PROCESSING_ON -enable star join detection and optimization.

- DAM_PROCESSING_OFF (default) - disable star join detection and
  > optimization.

> DAM_CONN_OPTION_JOINORDER_USING_STATISTIC S
>
> Determines whether the OpenAccess SDK SQL engine considers the
> cost-based join order algorithm when determining the order to join
> tables when executing a query.
>
> Valid values are:

- DAM_PROCESSING_OFF (default) - do not use the cost- based join order
  > algorithm to determine the table join order.

- DAM_PROCESSING_ON - use the cost-based join order algorithm to
  > determine the table join order. Whether the cost-based join order
  > algorithm is actually used depends on whether conditions are met in
  > the priority hierarchy for join algorithms. For more information
  > about join processing and the join algorithm hierarchy, refer to the
  > *OpenAccess SDK Programmer\'s Guide*.

> DAM_CONN_OPTION_JOINORDER_VERIFY_INDEX_O N_JOIN_CONDITION
>
> Determines whether the OpenAccess SDK SQL engine checks if inner
> tables have an index on the join conditions. If inner tables do not
> have an index on join conditions, the join ordering should not be
> used. This is used to check if join ordering based on search condition
> or foreign keys
>
> should be used. Valid values are:

- DAM_PROCESSING_OFF (default) - do not check indexes on join
  > conditions.

- DAM_IPROCESSING_ON - check indexes on join conditions.

> DAM_CONN_OPTION_NEGATIVE\_ ZERO_RESULT Determines whether the
> OpenAccess SDK SQL engine
>
> converts -0 to 0. Valid values are:

- DAM_NZR_CONVERT_NONE (default) - Return -0 result as-is.

- DAM_NZR_CONVERT_ALL - convert -0 to 0.

> DAM_CONN_OPTION_NUMERIC\_ RESULT Determines whether the result from an
> aggregate function
>
> that operates on SQL_NUMERIC data is rounded to the scale of the
> input. The default is DAM_NR_ROUND_NONE.

- DAM_NR_ROUND_NONE - the output of an aggregate function is returned
  > with a higher scale than the input. In the following example, this
  > option returns a value of 518.8766667 for the avg(numerictype)
  > expression.

- DAM_NR_ROUND_ALL - the output of an aggregate function is rounded to
  > the scale of the numeric argument. In the following example, this
  > option returns a value of

> 518.88 for the avg(numerictype) expression. For example,
>
> create table atypes(id integer, numerictype numeric(8,2) insert into
> atypes(id, numerictype) values (1, 349.61); insert into atypes(id,
> numerictype) values (2, 572.21); insert into atypes(id, numerictype)
> values (3, 634.81);
>
> DAM_CONN_OPTION_POST_PROCESSING Determines whether the OpenAccess SDK
> SQL engine
>
> performs post-processing (GROUP BY, ORDER BY). This option is used
> when using SQL pass-through mode. Valid values are:

- DAM_PROCESSING_ON (default) - the OpenAccess SDK SQL engine should
  > handle post-processing.

- DAM_PROCESSING_OFF - the OpenAccess SDK SQL engine should skip
  > post-processing operations.

### Statement Options for dam_setOption

> Set the option type to DAM_STMT_OPTION and the object handle to the
> current statement handle.

#### Statement Options for dam_setOption

> **Option (iOption) Option Value**
>
> DAM_STMT_OPTION_DISTINCT Indicate to the OpenAccess SDK SQL engine
> whether the IP has taken care of DISTINCT processing. The IP can use
> the dam_getInfo method to find out if a query has an
>
> optimizable SELECT DISTINCT clause.

- DAM_PROCESS_ON (default) - the OpenAccess SDK SQL engine handles the
  > processing.

- DAM_PROCESS_OFF - the IP has performed the processing.

> DAM_STMT_OPTION_GROUP_BY Indicate to the OpenAccess SDK SQL engine
> whether the IP has taken care of GROUP BY processing.

- DAM_PROCESS_ON (default) - the OpenAccess SDK SQL engine handles the
  > processing.

- DAM_PROCESS_OFF - the IP has performed the processing

> DAM_STMT_OPTION_ORDER_BY Indicate to the OpenAccess SDK SQL engine
> whether the IP has taken care of ORDER BY processing.

- DAM_PROCESS_ON (default) - the OpenAccess SDK SQL engine handles the
  > processing.

- DAM_PROCESS_OFF - the IP has performed the processing.

> DAM_STMT_OPTION_PASSTHROUGH_QUERY When working in Query Selection
> Mode, the IP sets this
>
> option to tell the OpenAccess SDK SQL engine which mode the IP wants
> to handle the query in. Set this option when ipExecute is called with
> DAM_SET_QUERY_MODE. Valid values are:

- DAM_PROCESS_ON - the IP wants to handle the query in SQL pass-through
  > mode.

- DAM_PROCESS_OFF -the IP continues working in normal row-based mode.

> DAM_STMT_OPTION_SUBQUERY\_ CONDLISTS Support for marking entire list
> of condition lists of
>
> subquery as \"Evaluated\" by the IP so that the OpenAccess SDK SQL
> engine skips evaluation during dam_isTargetRow. The IP would use this
> option when dam_getSetOfConditionListsEx returns complete condition
> lists. Valid values are:

- DAM_PROCESS_ON (default) - the OpenAccess SDK SQL engine handles the
  > evaluation.

- DAM_PROCESS_OFF - the IP performs the processing.

> DAM_STMT_OPTION_TABLE_ROWSET When operating in BLOCK JOIN mode, this
> option
>
> determines whether the OpenAccess SDK SQL engine or the IP returns
> Table Rowset results. Valid values are:

- DAM_PROCESS_ON (default) - the OpenAccess SDK SQL engine handles the
  > processing.

- DAM_PROCESS_OFF - the IP has performed the processing.

> If Table Rowset is being processed, the IP should not operate in
> Cursor Mode. The IP should process and add all rows before returning
> from IP execute. dam_setOption returns DAM_NOT_AVAILABLE when query
> cannot be processed as Table Rowset. The IP should check the return
> status and modify its behavior
>
> based on if Table Rowset can be processed.

### See also

- [dam_getInfo](#dam_getinfo)

- [dam_getSetOfConditionListsEx](#dam_getsetofconditionlistsex)

- [dam_isTargetRow](#dam_istargetrow)

- [dam_setOption](#dam_setoption)

### Condition List Options for dam_setOption

> For the condition list options, set the option type to
> DAM_CONDLIST_OPTION and the object handle to the current condition
> list handle.

#### Condition List Options for dam_setOption

> **Option Value Option Value**
>
> DAM_CONDLIST_OPTION_EVALUATION Support for marking the condition list
> as \"Evaluated\" by
>
> the IP so that the OpenAccess SDK SQL engine does not reevaluate the
> conditions during the dam_isTargetRow evaluation. This feature is
> useful when the IP wants to handle the condition because it may want
> to do it in a special way. For example, if you want to support your
> own time format, you can handle conditions on that column and then
> mark them evaluated.

- DAM_PROCESSING_ON (default) - the OpenAccess SDK SQL engine will
  > evaluate the condition as part of dam_isTargetRow.

- DAM_PROCESSING_OFF - the IP has handled the evaluation.

### See also

- [dam_isTargetRow](#dam_istargetrow)

### dam_strlikecmp

> This method compares the given string to the LIKE pattern. This method
> performs a case-insensitive comparison and ignores trailing blanks.
> This method can be used by the IP to match schema objects.
>
> int dam_strlikecmp( String pLikeString, String pMatchString)

#### Parameters for dam_strlikecmp

#### 

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 30%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>pLikeString</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The pattern string against which to compare the input string.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pMatchString</p>
</blockquote></td>
<td><blockquote>
<p>String</p>
</blockquote></td>
<td><blockquote>
<p>The input string to be checked.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>TRUE - MatchString matches the LikeString pattern.</p>
<p>FALSE - MatchString does not match the LikeString pattern.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><span id="_bookmark80"
class="anchor"></span><strong>tm_trace</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> The OpenAccess SDK SQL engine supports the logging method tm_trace to
> allow method calls, arguments, and other messages to be traced based
> on a flag provided at runtime. The tm_trace method provides a flexible
> way to trace events because you can set different levels of tracing
> with total control over enabling or disabling at runtime.
>
> Insert as many traces as possible, specifically for each error or
> important event occurring, as well as for each assumption or minor
> event occurring. Function calls should be traced by placing a trace as
> the first instruction of each function (using the flag UL_TM_F_TRACE
> to trace the program flow). Additionally, IP identification and
> version information should be displayed by the IP initialization
> routines (using the flag UL_TM_INFO) to facilitate future maintenance.
> For information about how to control tracing, refer to the *OpenAccess
> SDK Troubleshooting Guide*. The ServiceIPLogOption service attribute
> controls the tracing level. Log files for each connection are stored
> in the install_dir/logging directory.
>
> void tm_trace( long handle,
>
> int flag, String msg)

#### Parameters for tm_trace

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>handle</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the trace module that</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>was passed in when ipConnect is</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>called.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>flag</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Flag indicating which trace level must</p>
</blockquote></td>
</tr>
<tr class="odd">
<td colspan="2" rowspan="5"></td>
<td><blockquote>
<p>be enabled for this trace message to</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>be written to the trace file. See Table</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><a href="#_bookmark80">Trace Flag Masks for use in</a></p>
<p><a href="#_bookmark80">tm_trace()</a>. The flag and the</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>ServiceIPLogOptions setting</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>determine if the message specified in</p>
</blockquote></td>
</tr>
</tbody>
</table>

format is logged at runtime.

msg String Message string to be traced.

> The flag is a mask defined by the combination of using one or more of
> the masks listed in [Table Trace Flag](#_bookmark80) [Masks for use in
> tm_trace()](#_bookmark80).

#### Trace Flag Masks for use in tm_trace()

> []{#_bookmark81 .anchor}**Flag Mask ServiceIPLogOption**1
> **Description**

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p>UL_TM_FATAL</p>
</blockquote></th>
<th><blockquote>
<p>Disable All Tracing</p>
</blockquote></th>
<th><blockquote>
<p>Indicates fatal errors, such as cannot get memory or invalid address.
Use to enable trace messages that need to be written, no matter what the
ServiceIPLogOption attribute is set to.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>UL_TM_ERRORS</p>
</blockquote></td>
<td><blockquote>
<p>Enable Error Tracing</p>
</blockquote></td>
<td><blockquote>
<p>Indicates general errors such as invalid user name or password.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>UL_TM_MAJOR_EV</p>
</blockquote></td>
<td><blockquote>
<p>Enable Major Events Tracing</p>
</blockquote></td>
<td><blockquote>
<p>Indicates a major application event such as processing schema
request.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>UL_TM_INFO</p>
</blockquote></td>
<td><blockquote>
<p>Enable Full Tracing</p>
</blockquote></td>
<td><blockquote>
<p>Indicates detailed logging of the IP's activities.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>UL_TM_TRIVIA</p>
</blockquote></td>
<td><blockquote>
<p>Enable Verbose Tracing</p>
</blockquote></td>
<td><blockquote>
<p>Indicates very detailed messages. This setting could generate lots of
output.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> [1](#_bookmark81) The selected logging level and all levels above it
> are enabled. For example, selecting Enable Major Events will turn on
> all trace messages that use the flag UL_TM_FATAL, UM_TM_ERRORS, or
> UL_TM_MAJOR_EV.

# SQL Engine parse tree methods for Java

> This section describes a special set of OpenAccess SDK API methods
> that your IP can call for the following purposes:

- Working in SQL pass-through mode.

> **Note:** An IP that works in SQL pass-through mode must use only the
> methods in this section.

- Performing table pushdown.

> Refer to [About Table Expression
> Pushdown](https://docs.progress.com/bundle/datadirect-openaccess/page/topics/programguide/about-table-expression-pushdown.html)
> in the *OpenAccess SDK Programmer\'s Guide* for additional information
> on implementing table pushdown.

- Retrieving more information through the parse tree than is provided
  through the standard
  [OpenAccess](#openaccess-sdk-sql-engine-core-methods-for-java) [SDK
  SQL Engine core methods for
  Java](#openaccess-sdk-sql-engine-core-methods-for-java).

## Pass-through query processing

> When OpenAccess SDK is run in SQL pass-through mode, query processing
> is performed as follows:

- The OpenAccess SDK SQL engine performs the parsing of the query.

- The IP handles the execution of the query, including all joins,
  unions, etc.

> An IP working in SQL pass-through mode must exclusively use the API
> calls that are documented in this section when called to execute a
> statement through the EXECUTE entry point (ipExecute method in your
> IP).

## SQL pass-through and advanced query methods reference

> This section describes the pass-through and advanced query methods.

### damex_addxxxColValToRow

> This method is used to build up a row by adding values for columns of
> the tables involved in the query. NULL data is added by specifying the
> XO_NULL_DATA value flag for the value length.
>
> This method copies data from the user-supplied buffer to its internal
> buffers. Therefore, the IP can free the memory associated with the
> input buffer (colVal).
>
> These are the data type specific methods for Java:
>
> int damex_addBigIntColValToRow( long hstmt,
>
> long hRow, long hCol, long colVal,
>
> int lColValLen)
>
> int damex_addBinaryColValToRow( long hstmt,
>
> long hRow,
>
> long hCol, byte\[\] colVal,
>
> int lColValLen)
>
> int damex_addBitColValToRow( long hstmt,
>
> long hRow,
>
> long hCol, boolean colVal,
>
> int lColValLen)
>
> int damex_addCharColValToRow( long hstmt,
>
> long Row,
>
> long hCol,
>
> String colVal,
>
> int lColValLen)
>
> int damex_addDoubleColValToRow( long hstmt,
>
> long hRow,
>
> long hCol, double colVal,
>
> int lColValLen)
>
> int damex_addFloatColValToRow( long hstmt,
>
> long hRow, long hCol, float colVal,
>
> int lColValLen)
>
> int damex_addIntColValToRow( long hstmt,
>
> long hRow, long hCol, int colVal,
>
> int lColValLen)
>
> int damex_addShortColValToRow( long hstmt,
>
> long hRow, long hCol, short colVal,
>
> int lColValLen)
>
> int damex_addTimeStampColValToRow( long hstmt,
>
> long hRow, long hCol, xo_tm colVal,
>
> int lColValLen)
>
> int damex_addTinyIntColValToRow( long hstmt,
>
> long hRow, long hCol, byte colVal,
>
> int lColValLen)
>
> int damex_addWCharColValToRow( long hstmt,
>
> long hRow,
>
> long hCol, String colVal,
>
> int lColValLen)
>
> **Parameters for damex_addxxxColValToRow**

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 41%" />
<col style="width: 36%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>hCol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>colVal</p>
</blockquote></td>
<td><blockquote>
<p>depends on the method used</p>
</blockquote></td>
<td><blockquote>
<p>The Java type of the data should</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>correspond to the iXoType value. See</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>Table <a href="#reference-tables">How the methods for Java</a> <a
href="#reference-tables">return a value as an object</a>.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>lColValLen</p>
</blockquote></td>
<td><blockquote>
<p>Int</p>
</blockquote></td>
<td><blockquote>
<p>The length of the data:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>XO_NULL_DATA - indicates a null</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>value,</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>For VARCHAR, CHAR and</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>NUMERIC either the number of</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>characters or XO_NTS to add the</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>entire string.</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>For all other data types, 0 or any</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>value other than XO_NULL_DATA.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - added the value to</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>the row,</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - error adding the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>value.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### damex_addResultBufferToTable

> This method adds rows to the OpenAccess SDK SQL Engine and sets the
> status in result buffer Status\[\]. If an error occurs, it sets the
> error index and returns DAM_FAILURE.
>
> long damex_addResultBufferToTable( long hstmt, ResultBuffer
> pResBuffer)

#### Parameters for dam_addResultBufferToTable

<table style="width:100%;">
<colgroup>
<col style="width: 22%" />
<col style="width: 35%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pResBuffer</p>
</blockquote></td>
<td><blockquote>
<p>ResultBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The result buffer.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - Added the row.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> DAM_FAILURE - Failed to add the row.

### damex_addxxxResValToRow

> This method is used to build up a row by adding values for the result
> column. NULL data is added by specifying the XO_NULL_DATA value flag
> for the value length.
>
> This method copies data from the user supplied input buffer to its
> internal buffers and therefore the IP can free the memory that is
> associated with the input buffer (colVal).
>
> These are the data type specific methods for Java:
>
> int damex_addBigIntResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, long colVal,
>
> int lColValLen)
>
> int damex_addBinaryResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, byte\[\] colVal,
>
> int lColValLen)
>
> int damex_addBitResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, boolean colVal,
>
> int lColValLen)
>
> int damex_addCharResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, String colVal,
>
> int lColValLen)
>
> int damex_addDoubleResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, double colVal,
>
> int lColValLen)
>
> int damex_addFloatResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, float colVal,
>
> int lColValLen)
>
> int damex_addIntResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, int colVal,
>
> int lColValLen)
>
> int damex_addShortResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, short colVal,
>
> int lColValLen)
>
> int damex_addTimeStampResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, xo_tm colVal,
>
> int lColValLen)
>
> int damex_addTinyIntResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, byte colVal,
>
> int lColValLen)
>
> int damex_addWCharResValToRow( long hstmt,
>
> long hRow,
>
> long iResColNum, String colVal,
>
> int lColValLen)

#### Parameters for damex_addxxxResValToRow

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 40%" />
<col style="width: 36%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>iResColNum</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The result column number.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>colVal</p>
</blockquote></td>
<td><blockquote>
<p>depends on the method used</p>
</blockquote></td>
<td><blockquote>
<p>The Java type of the data should</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>correspond to the iXoType value. See</p>
<p>Table <a href="#reference-tables">How the methods for Java</a></p>
</blockquote></td>
</tr>
</tbody>
</table>

> [return a value as an object](#reference-tables).
>
> lColValLen int The length of the data: XO_NULL_DATA - indicates a null
> value.
>
> For VARCHAR, CHAR and
>
> NUMERIC either the number of characters or XO_NTS to add the entire
> string.
>
> For all other data types, 0 or any value other than XO_NULL_DATA.
>
> **RETURN**
>
> int DAM_SUCCESS - added the value to the row.
>
> DAM_FAILURE - error adding the value.

### damex_addRowToTable

> This method adds the row to the result set. All rows that are added to
> the result set will be sent back to the client.
>
> int damex_addRowToTable( long hstmt,
>
> long hRow)
>
> **Parameters for damex_addRowToTable**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 32%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - Added the row. DAM_FAILURE - Failed to add the row.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### damex_allocResultBuffer

> This method creates a result buffer object and returns it to the IP.
> long damex_allocResultBuffer(long hstmt) **Parameters for
> damex_allocResultBuffer**
>
> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The handle to a new result buffer. A 0 is returned if a result
> buffer could not be allocated.

### damex_allocRow

> This method allocates a new row and returns its handle.
>
> long damex_allocRow(long hstmt)

#### Parameters for damex_allocRow

> **Parameter Type Description INPUT**
>
> hstmt long The statement handle.
>
> **RETURN**
>
> long The handle to a new row. A 0 is
>
> returned if a row could not be allocated.

### damex_describeCaseElem

> This method retrieves the details of the WHEN-THEN Case element of the
> CASE expression. The WHEN- THEN expression has one of the following
> formats:

- Simple Case

> WHEN when_expression THEN result_expression

- Searched Case

> WHEN boolean_expression THEN result_expression

### Syntax

> int damex_describeCaseElem( long hCaseElem,
>
> long phWhenValExp, long phWhenBoolExp,
>
> long phResValExp)
>
> **Parameters for damex_describeCaseElem**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hCaseElem</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Element handle of the WHEN-THEN expression.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phWhenValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the WHEN value expression. This value is set only when
the Case is a simple Case expression.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phWhenBoolExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the WHEN boolean expression. This value is set only
when the Case is a searched Case expression.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phResValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the result expression.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
</tbody>
</table>

### damex_describeCaseValExp

> This method retrieves the description of a conditional CASE
> expression. It returns a description of two formats for CASE
> expression, as follows:

#### Simple CASE expression:

> CASE input_expression
>
> WHEN when_expression THEN result_expression \[\...\]
>
> \[ELSE else_result_expression
>
> \]END

#### Searched CASE expression:

> CASE
>
> WHEN Boolean_expression THEN result_expression \[\...\]
>
> \[
>
> ELSE else_result_expression
>
> \] END

### Syntax

> int damex_describeCaseValExp( long hCaseValExp,
>
> long phInputValExp, long phCaseElemList, long phElseValExp)

#### Parameters for damex_describeCaseValExp

<table>
<colgroup>
<col style="width: 31%" />
<col style="width: 22%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hCaseValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Case value expression handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phInputValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Handle to the input expression. This value is set only when the CASE
is a simple expression.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phCaseElemList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Handle to the input expression. This value is set only when the CASE
is a simple CASE expression.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phElseValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Handle to the WHEN-THEN expression list. Use damex_getFirstCaseElem
and damex_getNextCaseElem.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#damex_getfirstcaseelem">damex_getFirstCaseElem</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#damex_getnextcaseelem">damex_getNextCaseElem</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><span id="_bookmark85"
class="anchor"></span><strong>damex_describeCol</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method retrieves the description of the column. Pass in NULL for
> any attributes you do not want.
>
> long damex_describeCol( long hCol,
>
> xo_int piTableNum,
>
> xo_int piColNum, StringBuffer pColName, xo_int piXOType,
>
> xo_int piColType, StringBuffer pUserData,
>
> xo_int piResultColNum)

#### Parameters for damex_describeCol

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 23%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hCol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piTableNum</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The table number of the table to which the column belongs.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piColNum</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The column number.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pColName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The column name; create and pass in a StringBuffer of
DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piXoType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The data type of the column.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piColType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>How the column appears in the query is specified by the setting of
one or more of the following flags: DAM_COL_IN_SCHEMA - column is
defined in the schema database. This flag applies to all columns.</p>
<p>DAM_COL_IN_RESULT - column is part of the SELECT list.</p>
<p>DAM_COL_IN_CONDITION - column is part of the WHERE clause.</p>
<p>DAM_COL_IN_UPDATE_VAL_EXP -</p>
<p>column is part of an UPDATE value expression.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pUserData</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>User data as specified in the schema. Create and pass in a
StringBuffer of DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piResultColNum</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The result column number.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>damex_describeCond</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method retrieves the description of the condition. The condition
> is one of the following formats:

- left_val_exp {IS NULL \| IS NOT NULL}

- left_val_exp {\> \| \< \| \<= \| \>= \| = \| NOT =} right_val_exp

- left_val_exp {LIKE \| NOT LIKE} right_val_exp

- left_val_exp {BETWEEN \| NOT BETWEEN} right_val_exp AND extra_val_exp

> int damex_describeCond( long hCond, xo_int piType,
>
> long phLeftValExp, long phRightValExp, long phExtraValExp)
>
> **Parameters for damex_describeCond**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 29%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hCond</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Condition handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Operator type. The value of the operator type can be a bitwise OR of
the following values:</p>
</blockquote>
<ul>
<li><blockquote>
<p>SQL_OP_GREATER</p>
</blockquote></li>
<li><blockquote>
<p>SQL_OP_SMALLER</p>
</blockquote></li>
<li><blockquote>
<p>SQL_OP_LIKE</p>
</blockquote></li>
<li><blockquote>
<p>SQL_OP_ISNULL</p>
</blockquote></li>
<li><blockquote>
<p>SQL_OP_NULL</p>
</blockquote></li>
<li><blockquote>
<p>SQL_OP_NOT</p>
</blockquote></li>
<li><blockquote>
<p>SQL_OP_BETWEEN</p>
</blockquote></li>
<li><blockquote>
<p>SQL_OP_IN</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phLeftValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the left value expression.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phRightValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the right value expression. This value is 0 when the
operator type is SQL_OP_NULL.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phExtraValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the extra value expression. This value is set only when
the operator is SQL_OP_BETWEEN.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - valid condition returned.</p>
<p>DAM_FAILURE - on error.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### damex_describeDataChainVal

> This method is used to retrieve LONGVARBINARY data passed into the
> query as a parameter. The IP code should call this method when
> damex_describeVal returns SQL_VAL_DATA_CHAIN as the data type.
>
> Object damex_describeDataChainVal( long hVal,
>
> xo_int iValStatus)

#### Parameters for damex_describeDataChainVal

> **Parameter Type Description INPUT**
>
> hVal long Handle of the value.
>
> **OUTPUT**
>
> iValStatus xo_int DAM_SUCCESS - valid value returned.
>
> DAM_FAILURE - on error. DAM_SUCCESS_WITH_RESULT_P
>
> ENDING - when more column data is pending.
>
> **RETURN**
>
> Object Byte\[ \] object containing the binary data. NULL if the
> parameter value is null.

### See also

- [damex_describeVal](#damex_describeval)

### damex_describeDeleteQuery

> This method returns the details of the DELETE query.
>
> int damex_describeDeleteQuery( long hquery,
>
> long phTable, long phSearchExp)

#### Parameters for damex_describeDeleteQuery

<table style="width:100%;">
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phTable</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the delete table.</p>
<p>Use damex_describeTable to get the table details.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> phSearchExp long The handle to the search expression in the WHERE
> clause. Use damex_describeLogicExp to get details about the logical
> search expression.
>
> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure

### See also

- [damex_describeLogicExp](#damex_describelogicexp)

- [damex_describeTable](#damex_describetable)

### damex_describeInsertQuery

> This method returns details about the INSERT query.
>
> int damex_describeInsertQuery( long hquery,
>
> long phTable, long phColList,
>
> long phInsertRowList, long phInsertQuery)

#### Parameters for damex_describeInsertQuery

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phTable</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the insert table. Use damex_describeTable to get the
table details.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phColList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the column list of the insert query. Use
damex_getFirstColInList, damex_getNextColInList to get the insert
columns and damex_describeCol to get each of the column details.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phInsertRowList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the rows containing the values specified in the INSERT
query. Use damex_getFirstInsertRow,</p>
</blockquote></td>
</tr>
</tbody>
</table>

> damex_getNextInsertRow to navigate the multiple insert value lists
> specified. If only one set of values are specified, the first call to
> damex_getNextInsertRow will return NULL. Use
>
> damex_getFirstInsertValExp and damex_getNextInsertValExp to navigate
> each of the rows.
>
> phInsertQuery long The handle to the query whose result table rows are
> to be used as values to be inserted. The value of phInsertRowList will
> be NULL when this value is returned. Use damex_describeSelectQuery to
> get query details.
>
> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure

### See also

- [damex_describeCol](#_bookmark85)

- [damex_describeSelectQuery](#damex_describeselectquery)

- [damex_describeTable](#damex_describetable)

- [damex_getFirstColInList](#damex_getfirstcolinlist)

- [damex_getFirstInsertRow](#damex_getfirstinsertrow)

- [damex_getFirstInsertValExp](#damex_getfirstinsertvalexp)

- [damex_getNextColInList](#damex_getnextcolinlist)

- [damex_getNextInsertRow](#damex_getnextinsertrow)

- [damex_getNextInsertValExp](#damex_getnextinsertvalexp)

### damex_describeLogicExp

> This method gets the description of a logical expression.
>
> int damex_describeLogicExp( long hLogExp,
>
> xo_int piType,
>
> long phLeftLogExp, long phRightLogExp, long phCond)
>
> **Parameters for damex_describeLogicExp**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 29%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hLogExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The logical expression handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The type of the logical expression: SQL_EXP_COND - simple condition
SQL_EXP_AND - AND expression SQL_EXP_OR - OR expression SQL_EXP_NOT -
NOT expression</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phLeftLogExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the left logical expression. This value is set when the
type of the input logical expression is SQL_EXP_AND, SQL_EXP_OR,
SQL_EXP_NOT.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phRightLogExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the right logical expression. This value is set when
the type of the input logical expression is SQL_EXP_AND, SQL_EXP_OR.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phCond</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the condition. This value is set when the type of the
input logical expression is SQL_EXP_COND.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
</tbody>
</table>

### damex_describeOrderByExp

> This method gets the description of the ORDER BY expression. Pass in
> NULL for any attributes you do not want.
>
> int damex_describeOrderByexp( long hValExp,
>
> xo_int piResultColNum, xo_int piSortOrder)

#### Parameters for damex_describeOrderByExp

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 32%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The ORDER BY value expression</p>
</blockquote></td>
</tr>
</tbody>
</table>

> handle.
>
> **OUTPUT**
>
> piResultColNum xo_int The result column number.
>
> piSortOrder xo_int The sort order of the ORDER BY
> column:SQL_ORDER_ASCSQL_OR DER_DESC
>
> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure

### damex_describeScalarValExp

> This method retrieves the description of the scalar value expression.
> You must pass buffers with enough memory for the scalar method name.
>
> int damex_describeScalarValExp( long hScalarValExp, StringBuffer
> pName,
>
> long phValExpList)
>
> **Parameters for damex_describeScalarValExp**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 32%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hScalarValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Scalar value expression handle returned from the call to
damex_describeValExp.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The scalar method name. Create and pass in a StringBuffer object of
DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phValExpList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the value expression list which contains the arguments
to the scalar method. This value is 0 when no arguments exist.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also

- [damex_describeValExp](#damex_describevalexp)

- [damex_describeScalarValExpEx2](#damex_describescalarvalexpex2)

### damex_describeScalarValExpEx2

> This method retrieves the description of the scalar value expression.
> You must pass buffers with enough memory for the scalar function name
> and qualifier name.
>
> int damex_describeScalarValExp( long hScalarValExp, StringBuffer
> pQualifierName, StringBuffer pName,
>
> long phValExpList)
>
> **Parameters for damex_describeScalarValExpEx2**

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 23%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hScalarValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Scalar value expression handle returned from the call to
damex_describeValExp.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pQualifierName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the function qualifier. Create and pass in a StringBuffer
object of DAM_MAX_ID_LEN+1</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The scalar method name. Create and pass in a StringBuffer object of
DAM_MAX_ID_LEN+1.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phValExpList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the value expression list which contains the arguments
to the scalar method. This value is 0 when no arguments exist.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#damex_describevalexp">damex_describeValExp</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a
href="#damex_describescalarvalexp">damex_describeScalarValExp</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### damex_describeSelectQuery

> This method returns details about the SELECT query. To get the list of
> tables on the SELECT query, use damex_getFirstTable and
> damex_getNextTable.
>
> int damex_describeSelectQuery( int hquery,
>
> xo_int piSetQuantifier, long phSelectValExpList, long phSearchExp,
>
> long phGroupValExpList, long phHavingExp,
>
> long phOrderValExpList)

#### Parameters for damex_describeSelectQuery

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 28%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piSetQuantifier</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The quantifier of the result set: SQL_SELECT_ALL - return all rows.
Do not eliminate any duplicates from the result table.</p>
<p>SQL_SELECT_DISTINCT - eliminate any duplicate rows from the result
table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phSelectValExpList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the select list. Use damex_getFirstValExp,
damex_getNextValExp to navigate the select list.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phSearchExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the search expression in the WHERE clause. Use
damex_describeLogicExp to get details of the logical search
expression.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phGroupValExpList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the value-exp list of the GROUP BY clause. Use
damex_getFirstValExp and damex_getNextValExp to navigate the GROUP BY
expressions.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phHavingExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the HAVING clause expression. Use
damex_describeLogicExp to get details about the logical HAVING
expression.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> phOrderValExpList long The handle to the value-exp list of the ORDER
> BY clause. Use damex_getFirstValExp and damex_getNextValExp to
> navigate the ORDER BY expressions.
>
> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure

### See also

- [damex_describeLogicExp](#damex_describelogicexp)

- [damex_describeValExp](#damex_describevalexp)

- [damex_getFirstTable](#damex_getfirsttable)

- [damex_getNextTable](#damex_getnexttable)

- [damex_getFirstValExp](#damex_getfirstvalexp)

- [damex_getNextValExp](#damex_getnextvalexp)

### damex_describeSelectTopClause

> This function returns details of the TOP N clauses for a SELECT query.
> The TOP clause indicates the maximum number of rows to be returned.
>
> Example queries:
>
> SELECT TOP 10 \* FROM emp; SELECT TOP 5 PERCENT \* FROM emp;
>
> int damex_describeSelectTopClause( long hquery,
>
> xo_long piNumRows,
>
> xo_int pbPercent)

#### Parameters for damex_describeSelectTopClause

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 33%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>IN</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piNumRows</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The number of rows in the TOP clause. This number should be treated
as a PERCENT if value of pbPercent is returned as TRUE.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pbPercent</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Indicates if TOP <em>N</em> number was</p>
</blockquote></td>
</tr>
</tbody>
</table>

> specified with PERCENT option. Valid value are:

- TRUE

- FALSE

> **RETURN**
>
> int DAM_SUCCESS - on success
>
> DAM_FAILURE - on failure

### damex_describeTable

> This method retrieves the description of a table. You must pass
> buffers with enough memory for each of the values you need. Pass in
> NULL for any attribute in which you have no interest.
>
> void damex_describeTable( long hTable,
>
> xo_int piTableNum, StringBuffer pCatalog, StringBuffer pSchema,
> StringBuffer pTableName, StringBuffer pTablePath, StringBuffer
> pUserData)

#### Parameters for damex_describeTable

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 33%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>htable</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The table handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piTableNum</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The table number for the table. Tables are numbered in the order in
which they appear in the FROM clause of the select query, starting at
0.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pCatalog</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The table qualifier as entered in the OA_TABLES table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pSchema</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The table owner as entered in the OA_TABLES table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pTableName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The name of the table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>pTablePath</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The path of the table specified in the path column of the OA_TABLES
table. This is an IP-specific field.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pUserData</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The Table_Userdata as entered in the OA_TABLES table. This can be
any</p>
</blockquote></td>
</tr>
</tbody>
</table>

> string that the IP wants to know about the table.
>
> **RETURN**

NONE

### damex_describeTableFunction and damex_describeTableFunctionW

> These are the pass through methods that return the attributes of the
> table function being processed. These methods are called from the IP
> to obtain the information about the table function for which ipExecute
> is called. Set the attribute value to NULL, if the attribute is not
> being used.
>
> int damex_describeTableFunction ( DAM_HTABLE hTable,
>
> int \* piTableNum, char \* pCatalog, char \* pSchema, char \*
> pTableName, char \* pTablePath, char \* pUserData,
>
> DAM_HVALEXP_LIST phValExpList, int \* piArgCount)
>
> int damex_describeTableFunctionW ( DAM_HTABLE hTable,
>
> int \* piTableNum, OAWCHAR \* pCatalog, OAWCHAR \* pSchema, OAWCHAR \*
> pTableName, OAWCHAR \* pTablePath, OAWCHAR \* pUserData,
>
> DAM_HVALEXP_LIST phValExpList, int \* piArgCount)

#### Parameters for damex_describeTableFunction and damex_describeTableFunctionW

> **Parameter Type Description INPUT**
>
> hTable DAM_HTABLE The handle of the table object for which the
> attributes are requested.
>
> **OUTPUT**
>
> piTableNum int\* The table number for the table. Tables are numbered
> in the order in which they
>
> appear in the FROM clause of the select query, starting from 0.
>
> pCatalog char\*OAWCHAR \* The table qualifier, as entered in the
> OA_TABLES table. **Note:** A buffer of 129 characters is required.
>
> pSchema char\*OAWCHAR \* The table owner as entered in the OA_TABLES
> table. **Note:** A buffer of 129 characters is required.
>
> pTableName char\*OAWCHAR \* The name of the
>
> table.
>
> **Note:** A buffer of 129 characters is required.
>
> pTablePath char\*OAWCHAR\* The path of the table
>
> specified in the path column of the OA_TABLES table. This is an
> IP-specific field.
>
> **Note:** A buffer of 256 characters is required.
>
> pUserData char\*OAWCHAR\* The table user data,
>
> as entered in the OA_TABLES table. This can be any string that the IP
> wants to know about the table.
>
> **Note:** A buffer of 256 characters is required.
>
> phValExpList DAM_HVALEXP_LIS T
>
> The handle to the parameter list of the table function.
>
> piArgCount int\* Number of parameters of the table function.
>
> **RETURN**
>
> DAM_ERROR/DAM\_ SUCCESS
>
> int DAM_ERROR - if the handle passed is not of the table function.
> DAM_SUCCESS - ON SUCCESS

### damex_describeTableJoinInfo

> This method retrieves the outer/inner join information for a join
> query. If no outer/inner join exists, a 0 is returned for phJoinExp.
>
> void damex_describeTableJoinInfo( long hTable,
>
> xo_int piJoinType, long phJoinExp)
>
> **Parameters for damex_describeTableJoinInfo**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hTable</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The table handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>piJoinType</strong></p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The type of the join: SQL_JOIN_LEFT_OUTER - left outer join</p>
<p>SQL_JOIN_RIGHT_OUTER - right outer join</p>
<p>SQL_JOIN_FULL_OUTER - full outer joinSQL_JOIN_INNER - inner join</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phJoinExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the join expression in the ON clause of the join. A 0
is returned if no outer/inner join exists.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### damex_describeUpdateQuery

> This method returns details about the UPDATE query.
>
> int damex_describeUpdateQuery( long hquery,
>
> long phTable, long phUpdateRow, long phSearchExp)
>
> **Parameters for damex_describeUpdateQuery**

<table>
<colgroup>
<col style="width: 31%" />
<col style="width: 22%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phTable</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the update table. Call damex_describeTable to get the
table details.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phUpdateRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the update row. Use damex_getFirstUpdateSet,
damex_getNextUpdateSet to get the update set handles containing the
column and value information of each column that should be updated.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phSearchExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the search expression in the WHERE clause. Use
damex_describeLogicExp to get details about the logical search
expression.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on success DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td colspan="2" rowspan="6"></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#damex_describelogicexp">damex_describeLogicExp</a></p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#damex_describetable">damex_describeTable</a></p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#damex_getfirstupdateset">damex_getFirstUpdateSet</a></p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#damex_getnextupdateset">damex_getNextUpdateSet</a></p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#damex_getnextvalexp">damex_getNextValExp</a></p>
</blockquote></li>
</ul></td>
</tr>
</tbody>
</table>

### damex_describeVal

> This method retrieves the description of the value.
>
> Object damex_describeVal( long hVal, xo_int piType, xo_int piXoType,
> xo_int piValLen, xo_long phCol, xo_long phquery,
>
> x0_int piValStatus)
>
> **Parameters for damex_describeVal**

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 20%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hVal</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The value handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The type of the value, which will be</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>one of the following:</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>SQL_VAL_NULL</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>SQL_VAL_QUERY</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>SQL_VAL_COL</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><ul>
<li><blockquote>
<p>SQL_VAL_LITERAL</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piXoType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td>The data type of the literal value. The</td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>value is contained in the Object</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>returned by this method.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piValLen</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The length of the value.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phCol</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td>The handle to the column value. Use</td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>damex_describeCol to get more</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>details.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phQuery</p>
</blockquote></td>
<td><blockquote>
<p>xo_long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the query value. Use</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>dam_getQueryFirstResultValueand</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td>dam_getQueryNextResultValue to get</td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>the values returned from the</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>subquery.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piValStatus</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Status of the method call:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_SUCCESS - on success</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_FAILURE - on failure</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>Object</p>
</blockquote></td>
<td>If piType is SQL_VAL_LITERAL, then</td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td>an Object that represents the value of</td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>the literal. The format of the data</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td>depends on the type of the literal. See</td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>Table <a href="#reference-tables">How the methods for Java</a> <a
href="#reference-tables">return a value as an object</a>.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><p><a
href="#dam_getqueryfirstresultvalue">dam_getQueryFirstResultValue</a></p></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><p><a
href="#dam_getquerynextresultvalue">dam_getQueryNextResultValue</a></p></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#_bookmark85">damex_describeCol</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### damex_describeValExp

> This method retrieves the description of the value expression. The
> value expression has one of the following
>
> formats:

- *value*

- *function*(*value*): -- functions: MIN, MAX, AVG, SUM, COUNT, VAR,
  VAR_SAMP, STDDEV, STDDEV_SAMP, STDEV, VARP, VAR_POP, STDDEVP,
  STDDEV_POP, STDEVP

- COUNT(\*)

- *val_exp* {+ \| -- \| \* \| /} *val_exp*

- *scalar_val_exp*

> int damex_describeValExp( long hValExp, xo_int piType, xo_int
> piFuncType, long phLeftValExp,
>
> long phRightValExp, long phVal,
>
> long phScalarValExp)
>
> **Parameters for damex_describeValExp**

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 29%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hValExp</p>
</blockquote></td>
<td>long</td>
<td><blockquote>
<p>The value expression handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>piType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The type of the value expression:</p>
</blockquote>
<ul>
<li><blockquote>
<p>SQL_VAL_EXP_VAL</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_ADD</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_SUBTRACT</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_MULTIPLY</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_DIVIDE</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_SCALAR</p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><blockquote>
<p>piFuncType</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>The type of the set function can be a bitwise OR of the following
values:</p>
</blockquote>
<ul>
<li><blockquote>
<p>SQL_F_COUNT_ALL</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_COUNT</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_MIN</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_MAX</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_AVG</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_SUM</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_DISTINCT</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_VAR</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_VARP</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_STDDEV</p>
</blockquote></li>
<li><blockquote>
<p>SQL_F_STDDEVP</p>
</blockquote></li>
</ul>
<blockquote>
<p>This value is 0 if no set function exists.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phLeftValExp</p>
</blockquote></td>
<td>long</td>
<td><blockquote>
<p>The handle to the left value</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 19%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="2"></th>
<th><blockquote>
<p>expression. This value is set only when the val expression type is
one of the following:</p>
</blockquote>
<ul>
<li><blockquote>
<p>SQL_VAL_EXP_ADD</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_SUBTRACT</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_MULTIPLY</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_DIVIDE</p>
</blockquote></li>
</ul>
<blockquote>
<p>Otherwise the value is 0.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>phRightValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the right value expression. This value is set only when
the value expression type is one of the following:</p>
</blockquote>
<ul>
<li><blockquote>
<p>SQL_VAL_EXP_ADD</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_SUBTRACT</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_MULTIPLY</p>
</blockquote></li>
<li><blockquote>
<p>SQL_VAL_EXP_DIVIDE</p>
</blockquote></li>
</ul>
<blockquote>
<p>Otherwise the value is 0.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phVal</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the value. This value is set only when the value
expression type is SQL_VAL_EXP_VAL. Use damex_describeVal to get the
details of the value.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phScalarValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the scalar value. This value is set only when the value
expression type is SQL_VAL_EXP_SCALAR.</p>
<p>Use damex_describeScalarValExp to get the details of the scalar
method. Use damex_getFirstValExp and damex_getNextValExp to navigate the
argument list returned by damex_describeScalarValExp.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS – on success DAM_FAILURE – on failure</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td colspan="2" rowspan="5"></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a
href="#damex_describescalarvalexp">damex_describeScalarValExp</a></p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#damex_describeval">damex_describeVal</a></p>
</blockquote></li>
</ul></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#damex_getfirstvalexp">damex_getFirstValExp</a></p>
</blockquote></li>
</ul></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#damex_getnextvalexp">damex_getNextValExp</a></p>
</blockquote></li>
</ul></td>
</tr>
</tbody>
</table>

### damex_describeValExpEx

> This method retrieves the additional details of the values expression.
> This method provides the alias name of
>
> the value expression used in select list.
>
> int damex_describeValExpEx( long hValExp, StringBuffer asColName,
> xo_int iSign)
>
> **Parameters for damex_describeValExpEx**

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 33%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hValExp</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The value expression handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>asColName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The alias name of the value expression.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iSign</p>
</blockquote></td>
<td><blockquote>
<p>xo_int</p>
</blockquote></td>
<td><blockquote>
<p>Sign of the value expression. This field is currently not in use.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>DAM_SUCCESS - on</p>
<p>success DAM_FAILURE - on</p>
<p>failure</p>
</blockquote></td>
</tr>
</tbody>
</table>

### damex_freeResultBuffer

> This method frees a result buffer object created by
> damex_allocResultBuffer.
>
> void damex_freeResultBuffer( long hstmt
>
> ResultBuffer pResBuffer)
>
> **Parameters for damex_freeResultBuffer**

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 39%" />
<col style="width: 35%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>pResBuffer</p>
</blockquote></td>
<td><blockquote>
<p>ResultBuffer</p>
</blockquote></td>
<td><blockquote>
<p>The result buffer.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also

- [damex_allocResultBuffer](#damex_allocresultbuffer)

### damex_getCol

> This method returns the column handle that matches the given column
> name in the schema definition of the table.
>
> long damex_getCol(
>
> long hTable, StringBuffer sColName)

#### Parameters for damex_getCol

<table>
<colgroup>
<col style="width: 32%" />
<col style="width: 23%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hTable</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Table handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>sColName</p>
</blockquote></td>
<td><blockquote>
<p>StringBuffer</p>
</blockquote></td>
<td><blockquote>
<p>Name of the column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the column. A 0 is returned if the column does not
exist.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>damex_getColByNum</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> This method returns the column handle that matches the given column
> number in the schema definition of the table. Columns are numbered
> starting at 0.
>
> long damex_getColByNum( long hTable,
>
> int iColNum)
>
> **Parameters for damex_getColByNum**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hTable</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Table handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iColNum</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Number of the column.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the column. A 0 is returned if the column does not
exist.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### damex_getFirstCaseElem

> This method returns a handle to the first WHEN-THEN Case element of
> the CASE expression. It is used to navigate the WHEN-THEN list in the
> CASE expression.
>
> long damex_getFirstCaseElem(long hCaseElemList)

#### Parameters for damex_getFirstCaseElem

> **Parameter Type Description INPUT**
>
> hCaseElemList long Case element list handle.
>
> **RETURN**
>
> long The handle to the first WHEN-THEN Case element. Use
> damex_describeCaseElem to get the details of the element.

### See also

- [damex_describeCaseElem](#damex_describecaseelem)

### damex_getFirstCol

> This method navigates through the columns that appear in the SELECT,
> UPDATE and WHERE clause of the SQL query or through the columns in the
> schema definition of the table. Call this method with the column types
> to be navigated and then use the damex_getNextCol method to step
> through the list.
>
> long damex_getFirstCol( long hTable,
>
> int iColType)

#### Parameters for damex_getFirstCol

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hTable</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Table handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iColType</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>Identifies the column list to navigate;</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>use a bitwise OR ( | ) of the following</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>flags to scan through columns that</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>are in multiple categories:</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>DAM_COL_IN_SCHEMA - list of all</p>
</blockquote></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td><blockquote>
<p>columns as defined in the schema</p>
</blockquote></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td><blockquote>
<p>database.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> DAM_COL_IN_RESULT - list of columns that are part of the result set.
> DAM_COL_IN_CONDITION - list of columns that are in the WHERE clause.
>
> DAM_COL_IN_UPDATE_VAL_EXP -
>
> list of all columns that are part of update value expressions.
>
> DAM_COL_IN_USE - list of columns that are either part of the result or
> are in the WHERE clause.
>
> **RETURN**
>
> long The handle to the first column that has the attributes of
> iColType. A 0 otherwise.

### See also

- [damex_getNextCol](#damex_getnextcol)

### damex_getFirstColInList

> This method returns the first column in the list. It is used for
> navigating the ORDER BY column list and the GROUP BY column list.
>
> long damex_getFirstColInList(long hColList)

#### Parameters for damex_getFirstColInList

> **Parameter Type Description INPUT**
>
> hColList long The column list handle.
>
> **RETURN**
>
> long The handle to the column. Use
>
> damex_describeCol to get column details.

### See also

- [damex_describeCol](#_bookmark85)

### damex_getFirstInsertRow

> This method returns the first row to be inserted. Use it for
> navigating the insert row list in an INSERT query.
>
> long damex_getFirstInsertRow(long hRowList)

#### Parameters for damex_getFirstInsertRow

> **Parameter Type Description INPUT**
>
> hRowList long The row list handle.
>
> **RETURN**
>
> long The handle to the insert row. Use damex_getFirstInsertValExp and
> damex_getNextInsertValExp to get the list of values in each insert
> row.

### See also

- [damex_getFirstInsertValExp](#damex_getfirstinsertvalexp)

- [damex_getNextInsertValExp](#damex_getnextinsertvalexp)

### damex_getFirstInsertValExp

> This method returns the first value in the value list of the insert
> row. Use it for navigating the value list in the insert row.
>
> long damex_getFirstInsertValExp( long hquery,
>
> long hRow)
>
> **Parameters for damex_getFirstInsertValExp**

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 24%" />
<col style="width: 44%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>Insert row handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the first value in the value list of the insert row.
Use damex_describeValExp to get the details of the value.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><ul>
<li><blockquote>
<p><a href="#damex_describevalexp">damex_describeValExp</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### damex_getFirstTable

> This method returns the first table in the FROM clause of the SELECT
> query.
>
> long damex_getFirstTable(long hquery)
>
> **Parameters for damex_getFirstTable**

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 35%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the table.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### damex_getFirstUpdateSet

> This method returns the details of the first UPDATE column-value pair
> in the UPDATE query. It is used for navigating the UPDATE value list.
>
> long damex_getFirstUpdateSet( long hquery,
>
> long hRow, long phcol)
>
> **Parameters for damex_getFirstUpdateSet**

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle. When using this API in regular IP mode, DAM_HSTMT
should be passed for the query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>hRow</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The update row handle.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><blockquote>
<p>phcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle of the column to be updated.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the update value expression. Use damex_describeValExp
to get the value details.</p>
</blockquote></td>
</tr>
</tbody>
</table>

### See also

- [damex_describeValExp](#damex_describevalexp)

### damex_getFirstValExp

> This method returns the first value expression in the list. Use it for
> navigating the SELECT value expression list and the argument value
> list for scalar methods.
>
> long damex_getFirstValExp(long hValExpList)

#### Parameters for damex_getFirstValExp

> **Parameter Type Description INPUT**
>
> hValExpList long The value expression list handle.
>
> **RETURN**
>
> long The handle to the first value
>
> expression. Use damex_describeValExp to get the value expression
> details.

### See also

- [damex_describeValExp](#damex_describevalexp)

### damex_getNextCaseElem

> This method returns a handle to the next WHEN-THEN Case element of the
> CASE expression. Use it to navigate the WHEN-THEN list in the CASE
> expression. Call it after calling damex_getFirstCaseElem.
>
> long damex_getNextCaseElem(long hCaseElemList)

#### Parameters for damex_getNextCaseElem

> **Parameter Type Description INPUT**
>
> hCaseElemList long Case element list handle.
>
> **RETURN**
>
> long The handle to the next WHEN-THEN Case element. Use
> damex_describeCaseElem to get the details of the element.

### See also

- [damex_describeCaseElem](#damex_describecaseelem)

- [damex_getFirstCaseElem](#damex_getfirstcaseelem)

### damex_getNextCol

> This method navigates through the columns in the schema definition of
> the table. Call this method after calling damex_getFirstCol.
>
> long damex_getNextCol(long hTable)

#### Parameters for damex_getNextCol

> **Parameter Type Description INPUT**
>
> hTable long The table handle.
>
> **RETURN**
>
> long The handle to the next column in the schema. A 0 is returned at
> the end.

### See also

- [damex_getFirstCol](#damex_getfirstcol)

### damex_getNextColInList

> This method returns the next column in the list. It is used for
> navigating the ORDER BY column list and the GROUP BY column list. Call
> this method after calling damex_getFirstColInList.
>
> long damex_getNextColInList(long hColList)

#### Parameters for damex_getNextColInList

> **Parameter Type Description INPUT**
>
> hColList long The column list handle.
>
> **RETURN**
>
> long The handle to the next column:Use damex_describeCol to get column
> details. A 0 is returned at the end of the list.

### See also

- [damex_describeCol](#_bookmark85)

- [damex_getFirstCol](#damex_getfirstcol)

### damex_getNextInsertRow

> This method returns the next row in the list. It is used for
> navigating the insert rows. Call this method after calling
> damex_getFirstInsertRow.
>
> long damex_getNextInsertRow(long hRowList)
>
> **Parameters for damex_getNextInsertRow**

<table>
<colgroup>
<col style="width: 31%" />
<col style="width: 22%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hRowList</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The row list handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the next insert row. A 0 is returned at the end of the
list.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>See also</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><ul>
<li><blockquote>
<p><a href="#damex_getfirstinsertrow">damex_getFirstInsertRow</a></p>
</blockquote></li>
</ul></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### damex_getNextInsertValExp

> This method returns the next value in the row. It is used for
> navigating the value list in the insert row. Call this method after
> calling damex_getFirstInsertValExp.
>
> long damex_getNextInsertValExp(long hquery)

#### Parameters for damex_getNextInsertValExp

> **Parameter Type Description INPUT**
>
> hquery long The query handle.
>
> **RETURN**
>
> long A handle to the next value in the value list of the insert row. A
> 0 is returned at the end of the list.

### See also

- [damex_getFirstValExp](#damex_getfirstvalexp)

### damex_getNextTable

> This method returns the next table in the FROM clause of the SELECT
> query. Call this method after calling damex_getFirstTable.
>
> long damex_getNextTable(long hquery)

#### Parameters for damex_getNextTable

> **Parameter Type Description INPUT**
>
> hquery long The query handle.
>
> **RETURN**
>
> long The handle to the next table in the list. A 0 is returned at the
> end of the list.

### See also

- [damex_getFirstTable](#damex_getfirsttable)

### damex_getNextUpdateSet

> This method returns the details of the next UPDATE column=value pair
> in an UPDATE query. Use it to navigate the UPDATE value list. Call
> this method after calling damex_getFirstUpdateSet.
>
> long damex_getNextUpdateSet( long hquery,
>
> long phcol)

#### Parameters for damex_getNextUpdateSet

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle. When using this method in regular IP mode, pass
DAM_HSTMT for the query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>OUTPUT</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p>phcol</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The column handle of the column to</p>
</blockquote></td>
</tr>
</tbody>
</table>

> be updated.
>
> **RETURN**
>
> long The handle to the update value
>
> expression. Use damex_describeValExp to get details of the value. A 0
> is returned at the end of the list.

### See also

- [damex_describeValExp](#damex_describevalexp)

- [damex_getFirstUpdateSet](#damex_getfirstupdateset)

### damex_getNextValExp

> This method returns the next value expression in the list. Use it to
> navigate the SELECT value expression list and the argument value list
> of scalar methods. Call this method after calling
> damex_getFirstValExp.
>
> long damex_getFirstValExp(long hValExpList)

#### Parameters for damex_getNextValExp

> **Parameter Type Description INPUT**
>
> hValExpList long The value expression list handle.
>
> **RETURN**
>
> long The handle to the value expression. Use damex_describeValExp to
> get details of the value expression. A 0 is returned at the end of the
> list.

### See also

- [damex_describeValExp](#damex_describevalexp)

- [damex_getFirstValExp](#damex_getfirstvalexp)

### damex_getQuery

> This method returns the query handle of the SQL statement. The query
> handle can be used to get complete details of the query.
>
> long damex_getQuery(long hstmt)

#### Parameters for damex_getQuery

<table>
<colgroup>
<col style="width: 36%" />
<col style="width: 23%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hstmt</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The statement handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>damex_getQueryType</strong></p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the query.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> This method returns the type of the SQL query. long
> damex_getQueryType(long hquery) **Parameters for damex_getQueryType**
>
> **Parameter Type Description INPUT**
>
> hquery long The query handle.
>
> **RETURN**
>
> long The type of the SQL query:

- DAM_SELECT - SELECT

- DAM_UPDATE - UPDATE

- DAM_INSERT - INSERT

- DAM_DELETE - DELETE

### damex_getTable

> This method returns the table handle that matches the given table name
> in the FROM clause of the SELECT query.
>
> long damex_getTable(
>
> long hquery, StringBuffer sTableName)

#### Parameters for damex_getTable

<table>
<colgroup>
<col style="width: 26%" />
<col style="width: 37%" />
<col style="width: 36%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle.</p>
</blockquote></td>
</tr>
</tbody>
</table>

> sTableName StringBuffer The name of the table.
>
> **RETURN**
>
> long The handle to the table. 0 is returned if the table does not
> exist.

### damex_getTableByNum

> This method returns the table handle that matches the given table
> number in the FROM clause of the SELECT query. Table numbers start at
> 0.
>
> long damex_getTableByNum( long hquery,
>
> int iTableNum)
>
> **Parameters for damex_getTableByNum**

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 31%" />
<col style="width: 46%" />
</colgroup>
<thead>
<tr class="header">
<th><blockquote>
<p><strong>Parameter</strong></p>
<p><strong>INPUT</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Type</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Description</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>hquery</p>
</blockquote></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The query handle.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p>iTableNum</p>
</blockquote></td>
<td><blockquote>
<p>int</p>
</blockquote></td>
<td><blockquote>
<p>The number of the table.</p>
</blockquote></td>
</tr>
<tr class="odd">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td></td>
<td><blockquote>
<p>long</p>
</blockquote></td>
<td><blockquote>
<p>The handle to the table. 0 is returned if the table does not
exist.</p>
</blockquote></td>
</tr>
</tbody>
</table>

# Setting Up the samples

> This section describes how to use the samples included as part of the
> OpenAccess SDK installation. The instructions here are for sample Java
> IPs for use with the OpenAccess SDK SQL engine.
>
> In these instructions, replace the *example* string with the sample
> you are attempting to build. For example, to use these instructions
> with example1, substitute all occurrences of *example* with example1.
> Some examples use static schema and others implement dynamic schema.
> The instructions vary depending on what type of schema the example IP
> is using.
>
> The source code and project files for the samples are located under
> the install_dir\ip\oajava\\ example directory, where *example* is
> replaced by example1, example3, or memory. During installation, the
> OpenAccessSDK900_Java service is configured with the example and
> memory data sources. Both are fully configured; you only need to
> compile the Java files associated with these samples.

## Description of the samples

> Three working samples are included to show the different features of
> the OpenAccess SDK. Each sample is a good starting point for writing
> your own IP. The samples are fully portable and can be built on any of
> the supported platforms.

### Static schema: example1

> The fully functional IP in example1 supports SELECT, INSERT, UPDATE
> and DELETE statements. It implements a single table CURVALUE for a
> memory database called MDB. The MDB database is implemented in the
> mdb.c file. The IP code is contained in the mdb.java and database.java
> files. The IP code is contained in the damip.java file. The
> installation configures a data source example that is fully configured
> to access this sample.
>
> Highlights of example1 are:

- Written in Java.

- Uses static schema.

- Exposes a single table with full capability.

### Dynamic schema: example3

> The IP in example3 is identical to example1 except that it uses a
> dynamic schema instead of a static schema. Like example1, example3
> supports SELECT, INSERT, UPDATE and DELETE statements. It implements a
> single table CURVALUE for a memory database called MDB. The MDB
> database is implemented in the mdb.c file.
>
> Highlights of this example are:

- Written in Java.

- Uses dynamic schema.

- Exposes a single table with full capability.

### Dynamic schema: memory

> The memory example incorporates many features of the OpenAccess SDK
> and is used as part of our test suites. It is included as part of the
> OpenAccess SDK installation to allow you to perform benchmarks and to
> provide an implementation example of various features. The
> installation creates a memory data source that is configured for using
> this sample. The only step required after the installation is to build
> the sample.
>
> Highlights of this example are:

- Written in Java.

- Uses dynamic schema.

- Exposes tables EMP and DEPT

- Can return any number of rows based on the ITEMS condition in the
  query. For example, the SQL query

> SELECT \* FROM emp WHERE items=1000 returns 1000 rows.

## Setting up a sample on Windows

> The following steps provide the process of setting up the schema,
> building the IP, and configuring the OpenAccessSDK900_Java service.

1.  Build the IP module:

    a.  Set CLASSPATH to install_dir\ip\oajava\oasql.jar..

> c:\\ set CLASSPATH=c:\program
> files\datadirect\oaserver70\ip\oajava\oasql.jar

b.  Change to the sample directory and compile the Java classes.

> c:\\ cd intall_dir\ip\oajava\example1 c:\\ javac \*.java

2.  Run the OpenAccess SDK Management Console and modify the
    OpenAccessSDK900_Java service to

> specify the IP module and *schema*\example as the schema path.

a.  Run the OpenAccess SDK Management Console.

b.  Expand the OpenAccessSDK900_Java service node to the Data Source
    > Settings node:

- Select example for the example1 or example3 sample.

- Select memory for the memory sample.

c.  Select the IP Parameters folder.

d.  Modify the DataSourceIPSchemaPath attribute to
    > install_dir\ip\schema\example, where

> *example* is replaced by example1, example3, or memory.

e.  Modify the DataSourceIPClass attribute to the class name of the IP:
    > oajava/example/damip, where *example* is replaced by example1,
    > example3, or memory.

f.  Save the settings and restart the OpenAccessSDK900_Java service.

<!-- -->

3.  From an OpenAccess SDK client, connect to the IP module you have
    just built:

- Use the example data source for example1 or example3. example1 and
  > example3 do not require a user name and password.

- Use the memory data source to connect to the memory sample. The memory
  > sample requires the user name pooh and the password bear.

## Setting up a sample on Linux/UNIX

> The following steps provide the process of setting up the schema,
> building the IP, and configuring the OpenAccessSDK900_Java service.
> The instructions below assume that you are using the OpenAccess SDK
> Management Console on Windows to manage the OpenAccess SDK
> installation on a Linux or UNIX
>
> machine.

1.  Build the IP module using the following commands:

    a.  cd install_dir

    b.  Set CLASSPATH to include *install_dir*/ip/oajava/oasql.jar. This
        > can be done using the installed scripts.

> If you are using a C shell:
>
> source ip/cfg/setenv.csh
>
> If you are using a Korn shell:
>
> . ./ip/cfg/setenv.sh

c.  Change to the sample directory and compile the Java classes:

> usr/\> cd *install_dir*/ip/oajava/*example*
>
> usr/\> javac \*.java

2.  Run the OpenAccess SDK Management Console on Windows and modify the
    OpenAccessSDK900_Java service running on UNIX to specify the IP
    module and schema/example as the schema path. Refer to the
    *OpenAccess SDK Administrator\'s Guide* for information about using
    the OpenAccess SDK Management Console.

    a.  Run the OpenAccess SDK Management Console.

    b.  Expand the OpenAccessSDK900_Java service to the Data Source
        > Settings node:

- Select example for the example1, example2, or example3 sample.

- Select memory for the memory sample.

  a.  Select the IP Parameters folder.

  b.  Modify DataSourceIPSchemaPath to *install_dir*/ip/schema/example,
      > where *example* is example1, example3, or memory.

  c.  Modify the DataSourceIPClass attribute to the class name of the
      > IP: oajava/example/damip, where *example* is replaced by
      > example1, example3, or memory.

  d.  Save the settings and restart the OpenAccessSDK900_Java service.

3.  From an OpenAccess SDK client, connect to the IP module you have
    just built:

- Use the example data source for example1 or example3. These samples do
  > not require a user name and password.

- Use the memory data source to connect to the memory sample. The memory
  > sample requires the user name pooh and the password bear.

# Embedding OpenAccess SDK Server in Java applications

> This document describes how the OpenAccess SDK Server can be embedded
> in a native Java application.
>
> The OpenAccess SDK Server can be run as a standalone process or it can
> be embedded into a Java process. The embeddable version of the
> OpenAccess SDK Server is supplied as a shared library (oasoa.dll or
> liboasoa.so) with an API that a Java program can use to start and stop
> the service. Code samples are provided for Java environment. If your
> environment is not described in this document, then please contact
> Progress technical support to find out if we have additional
> information in a form of a knowledgebase article.

## Java API

> The embedded server shared library exposes a native function
> OaServerControl that can be called from a Java program. The wrapper
> class oasload (found in *install_dir*\shlibsrv\oajava\server\\
> oasload.java) defines the interface that can be used to access this
> function. The OaServerControl method is invoked to start and to stop a
> service. Example classes that demonstrate the embedding of the
> OpenAccess SDK server into a Java program are located in
> *install_dir*\shlibsrv\oajava\server directory. A Java application can
> embed an OpenAccess SDK Server to run C or Java services.

### oasload.OaServerControl {#oasload.oaservercontrol}

> This function should be called from a thread in the application to
> start the specified OpenAccess SDK service that has already been
> configured and to stop a service. The required parameters are passed
> in as strings in an array of strings. When called to start a service,
> this method will not return control back to the calling thread until
> the service is stopped from a different thread. Once the service is
> started it behaves just as if it was started as a standalone server.
>
> int OaServerControl(int argc, String argv\[\], int command)

#### Parameters for OaServerControl

> **Parameter Type Description IN**
>
> argc int Number or arguments passed into the argv string array.
>
> argv\[\] char\* Array of strings that represent the arguments for the
> server_start command.
>
> argv\[0\] - a string indicating the name

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 20%" />
<col style="width: 17%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr class="header">
<th colspan="3"></th>
<th><blockquote>
<p>of the application</p>
<p>argv[1] - -datamodel - indicates that the next argument specifies the
path of the configuration file.</p>
<p>argv[2] -&lt;datamodel&gt;where</p>
<p>&lt;datamodel&gt; is replaced with the file name, including path, of
the OpenAccess SDK Server configuration file oadm.ini.</p>
<p>argv[3] - -servicename - indicates that the next argument specifies
the OpenAccess SDK service name. argv[4] -&lt;servicename&gt;</p>
<p>where &lt;servicename&gt; is replaced with the OpenAccess SDK service
name that is configured in the configuration file specified by the</p>
<p>-datamodel argument. For example, OpenAccessSDK900_C.</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><blockquote>
<p>Command</p>
</blockquote></td>
<td>int</td>
<td></td>
<td><blockquote>
<p>Command:</p>
</blockquote>
<ul>
<li><blockquote>
<p>OASERVER_START (2) - start the service using the arguments passed in
the argv string array.</p>
</blockquote></li>
<li><blockquote>
<p>OASERVER_STOP (3) - stop the service.</p>
</blockquote></li>
<li><blockquote>
<p>OASERVER_START_WITH_JOBJEC</p>
</blockquote></li>
</ul>
<blockquote>
<p>T (4) - start the service in Extended Mode . This command is valid
only for Java Service.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>RETURN</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td></td>
<td>int</td>
<td></td>
<td><ul>
<li><blockquote>
<p>0 - For OASERVER_START, service has been started. For OASERVER_STOP,
stop command has been placed in the service's queue.</p>
</blockquote></li>
<li><blockquote>
<p>All other return values for OASERVER_START and
OASERVER_START_WITH_JOBJEC</p>
</blockquote></li>
</ul>
<blockquote>
<p>T - see the following table.</p>
</blockquote></td>
</tr>
<tr class="even">
<td><blockquote>
<p><strong>Error Codes</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

> **Error Code Reason**
>
> 0 Success
>
> 10 oasoa.dll is started without the required command
>
> arguments
>
> 11 The -servicename or -n option is missing servicename
>
> argument
>
> 12 The -connectmodel or -m option is missing an argument.
>
> 13 The -sessionid option is missing an argument.
>
> 14 The -connectinfo option is missing an argument.
>
> 15 The -datamodel or -d option is missing an argument.
>
> 16 The -msgfile or -g option is missing an argument.
>
> 17 The required servicename is missing.
>
> 18 Invalid value for connection model passed
>
> 21 Cannot open the message file
>
> 22 Cannot open the configuration file
>
> 23 Configuration file has incorrect version
>
> 24 Servicename name not found in configuration file
>
> 25 ServiceMessageFile attrbute not set in configuration file
>
> 30 Failed to Build MGSS mechanism map from DM and
>
> create MGSS factory
>
> 31 Failed to initialize the EventQueue Cache
>
> 32 Failed to create the Core Service component
>
> 33 Failed to create the Scheduler component
>
> 34 Failed to create the Event Processor component
>
> 35 Failed to initialize the Service component
>
> 91 Internal error, memory allocation failed.
>
> 95 Internal error, fatal server error detected.
>
> For both Windows and Linux/UNIX, when OaServerControl returns with
> exit code 35, a related error message is written to a Service debug
> log file in the logging subdirectory.
>
> For all other exit codes:
>
> On Windows, a related error message is sent to the Windows Application
> log. Use the Event Viewer to read the Windows Application log.
>
> On Linux/UNIX, a related error message is sent to syslog marked at as
> level \'err\'(LOG_ERR) and facility \'user\'(LOGUSER). Verify the
> contents of /etc/syslog.conf where messages marked as level \'err\'
> and facility \'user\' are written.
>
> Some typical locations are shown in the following table.

#### Syslog Location

#### 

> **Operating System Location of the syslog**
>
> Linux */var/log/messages*
>
> AIX as set in */etc/syslog.conf*

## Sample For Windows

> The following sample explains how you can embed the server in a Java
> application on a Windows platform.

### Files for embedding in Java applications

> **Note:** Replace the placeholder install_dir with the directory where
> you have installed the OpenAccess SDK Server and IP SDK.
>
> install_dir\bin\oasoa.dll: OpenAccess SDK Server shared library
>
> install_dir\shlibsrv\oajava\server\oasload.java: OpenAccess SDK
> embedded Server load class
>
> install_dir\shlibsrv\oajava\server\oaserver.java: OpenAccess SDK
> embedded Server Class
>
> install_dir\shlibsrv\oajava\server\oaservertest.java: OpenAccess SDK
> embedded Server sample

### Compiling Java sample

> set OAINSTALLDIR=C:\Program Files\Progress\DataDirect\oaserver90 cd
> %OAINSTALLDIR%\shlibsrv
>
> set JVM=D:\Tools\jdk1.8%JVM%\bin\javac -d . oajava\server\\.java

### Running Java sample

> set OAINSTALLDIR=C:\Program Files\Progress\DataDirect\oaserver90 cd
> %OAINSTALLDIR%\shlibsrv
>
> set JVM=D:\Tools\jdk1.8 PATH=%OAINSTALLDIR%\bin;%PATH%
>
> %JVM%\bin\java -server -classpath . oaservertest -datamodel
> \"%OAINSTALLDIR%\cfg\oadm.ini\" -servicename \"OpenAccessSDK900_C\"
>
> When running a service for a Java IP, add the class path from
> ServiceJVMClassPath to the CLASSPATH variable.
>
> SET CLASSPATH=.;%OAINSTALLDIR%\ip\oajava\oasql.jar;%OAINSTALLDIR%\ip
>
> %JVM%\bin\java -server oaservertest
>
> -datamodel \"%OAINSTALLDIR%\cfg\oadm.ini\"
>
> -servicename \"OpenAccessSDK900_Java\"
>
> In addition, use the same JVM that was set in the ServiceJVMLocation
> attribute.

## Sample for UNIX

> The following sample explains how you can embed the server in a Java
> application on a UNIX platform.

### Files for embedding in Java applications

> **Note:** Replace the placeholder *install_dir* with the directory
> where you have installed the OpenAccess SDK Server and IP SDK.
>
> *install_dir*/bin/liboasoa.so: OpenAccess SDK Server shared library
>
> *install_dir*/shlibsrv/oajava/oaserver/oasload.java: OpenAccess SDK
> embedded Server load class
>
> *install_dir*/shlibsrv/oajava/oaserver/oaserver.java: OpenAccess SDK
> embedded Server Class
>
> *install_dir*/shlibsrv/oajava/oaserver/oaservertest.java: OpenAccess
> SDK embedded Server sample
>
> *install_dir*/shlibsrv /makefile: make file
>
> *install_dir*/shlibsrv /runsrvr_java.sh: korn shell script starting
> the Java sample

### Compiling Java sample

1.  Type cd *install_dir*

2.  If you are using a C shell, type:

> source ip/cfg/setenv.sh
>
> If you are using a Korn shell, type:
>
> . ./ip/cfg/setenv.sh

3.  Type cd *install_dir*/shlibsrv

4.  Type make javasample

### Running Java sample in install_dir/shlibsrv

> cd install_dir/shlibsrv runsrvr_java.sh
>
> By default, the runsrv_java.sh script starts the OpenAccess SDK Java
> Service. To start the OpenAccess SDK Java Service, edit this script
> and set the ServiceName service attribute to \${SERVICENAME_JAVA}:

### Remarks

- When embedding the server in Java, the values of the service
  attributes ServiceJVMClassPath and ServiceJVMOptions have no effect.
  These values must be passed directly to the Java program.

- Use the JVM that was set in ServiceJVMLocation.

## Deploying on a JBoss Application server

> The following sections describe how to deploy an application on the
> JBoss application server.

### Installing the JBoss sample on Windows

> The following procedure describes how to install the JBoss sample on
> Windows.

#### To install JBoss on Windows:

> Extract the oaserver32_embedded_win.zip file at the location where the
> OpenAccess SDK Server and IP SDK is installed, for example, c:\Program
> Files\Progress\DataDirect\oaserver90.

### Configuring JBoss

#### To configure JBoss:

1.  Deploy the OpenAccess WAR application by extracting attached
    oa_jboss_deploy.zip under the folder where JBoss is installed - for
    example, C:\Program Files\jboss-4.2.2.GA..

2.  Edit C:\Program Files\jboss-4.2.2.GA\bin\openaccess.bat to set the
    following:

    - OpenAccess SDK install folder

    - Java Home

    - JBOSS_CLASSPATH - should include the classes required by
      > OpenAccess SDK and the classes required by your IP code (for
      > Java IPs)

3.  Update PATH to include the location of the oasoa.dll file
    -install_dir\bin and run JBoss using openaccess.bat.

### Running the application

> The following procedure describes how to run the application. To run
> the application:

1.  Access https://localhost:8080/openaccess/oaservice.html.

2.  Select the Service to Start, select the **Start** option and then
    click **GO**.

3.  A status screen is displayed.

4.  You can verify from the OpenAccess SDK Management Console that the
    Service is started. Currently, the JBoss application always says the
    service is Started.

5.  To stop the Service, you must reload oaservice.html (step 1) and
    select the Stop option and click **GO**. It will show the status
    correctly indicating whether the service was stopped. You can
    confirm this by running the OpenAccess SDK Management console. If
    you are already running the Management Console, select the
    OpenAccess SDK service you are working with and press **F5** to
    refresh its status.

> **Note:** You can stop only the last service that was started.
