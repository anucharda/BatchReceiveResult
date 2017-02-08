/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.ais.cpac.cl.batch;

import th.co.ais.cpac.cl.batch.cnf.CNFDatabase;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;

/**
 *
 * @author Sirirat
 */
public class Constants {

	public static String dbPath = "c:\\cpac\\database.properties";

	public static String suspendOrderType = "Suspend-Debt";
	public static int suspendOrderActionID = 10;//dummy 
	public static String terminateOrderType = "Disconnect-Terminate";
	public static int terminateOrderActionID = 4;
	
	public static String reconnectType = "Reconnect-Debt";
	public static int reconnectOrderID = 12;
	public static String END_LINE = "";

	public static int suspendInprogressStatus = 3;
	public static int inboundSuccessStatus = 4;
	public static int inboundIncompleteStatus = 5;
	public static int inboundFailStatus = 5;


	public static DBConnectionPools getDBConnectionPools(UtilityLogger logger) {
		CNFDatabase cnf = new CNFDatabase();
		return new DBConnectionPools<>(cnf, logger);
	}
}
