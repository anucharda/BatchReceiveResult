/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.ais.cpac.cl.batch;

import th.co.ais.cpac.cl.batch.cnf.CNFDatabase;
import th.co.ais.cpac.cl.batch.cnf.PMDatabase;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;

/**
 *
 * @author Sirirat
 */
public class Constants {

	public static String dbPath = "c:\\cpac\\database.properties";

	public static int suspendOrderActionID = 10;//dummy 
	public static int terminateOrderActionID = 4;
	public static int reconnectOrderID = 12;
	public static int waiveBatchOrderID = 8;
	public static int writeOffOrderID = 9;
	public static String END_LINE = "";
	
	public static String suspendJobType="SD";
	public static String terminateJobType="DT";
	public static String reconnectJobType="RC";
	public static String waiveBatchJobType="WA";
	public static String writeOffJobType ="WO";
	public static int batchInprogressStatus = 1;
	public static int batchReceiveStatus = 2;
	public static int batchCompleteStatus = 3;
	
	public static String suspendUsername = "Suspend User";
	public static String terminateUsername = "Terminate User";
	public static String reconnectUsername = "Reconnect User";
	public static String waiveBatchUsername = "Waive Batch User";
	public static String writeOffUsername = "Write Off Batch User";
	
	public static int actInprogressStatus = 3;
	public static int actSuccessStatus = 4;
	public static int actIncompleteStatus = 5;
	public static int actFailStatus = 6;
	public static int treatSuccessStatus = 4;
	public static int treatIncompleteStatus = 5;
	public static int treatFailStatus = 6;
	public static String adjCompleteStatus = "CP";
	public static String adjFailStatus = "ER";
	public static String PIPE ="\\|";
	public static String sffOKExt =".ok";
	public static String sffErrExt =".err";
	
	public static String cldbConfPath ="C:\\cpac\\database.properties";
	public static String pmdbConfPath ="C:\\cpac\\pmDatabase.properties";

	public static DBConnectionPools getDBConnectionPools(UtilityLogger logger) {
		CNFDatabase cnf = new CNFDatabase();
		return new DBConnectionPools<>(cnf, logger);
	}
	public static DBConnectionPools getPMDBConnectionPools(UtilityLogger logger) {
		PMDatabase cnf = new PMDatabase();
		return new DBConnectionPools<>(cnf, logger);
	}
}
