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
public class ConstantsBatchReceiveResult {
	public static int batchInprogressStatus = 1;
	public static int batchReceiveStatus = 2;
	public static int batchCompleteStatus = 3;

	public static int actInprogressStatus = 3;
	public static int actSuccessStatus = 4;
	public static int actIncompleteStatus = 5;
	public static int actFailStatus = 6;
	public static int treatProgressStatus = 3;
	public static int treatSuccessStatus = 4;
	public static int treatIncompleteStatus = 5;
	public static int treatFailStatus = 6;
	public static String adjCompleteStatus = "CP";
	public static String adjFailStatus = "ER";
	public static String PIPE ="\\|";
	public static String sffOKExt =".ok";
	public static String sffErrExt =".err";
	public static String blacklistExt =".dat";
	public static String writeOffSuccess ="S";
	public static String writeOffFail ="E";
	public static String blacklistFailReason="Not Found Result in SFF_BLACKLIST";

	public static DBConnectionPools getDBConnectionPools(UtilityLogger logger) {
		CNFDatabase cnf = new CNFDatabase();
		return new DBConnectionPools<>(cnf, logger);
	}
	  public enum Environment {
		    PROD(1),
		    DEV(2),
		    SIT(3),
		    UnKnow(-9999);
		    private final int code;

		    private Environment(int code) {
		      this.code = code;
		    }

		    public int getCode() {
		      return code;
		    }
		  }

		  public static final Environment mapEnvironment(int code) {
		    if (Environment.PROD.getCode() == code) {
		      return Environment.PROD;
		    } else if (Environment.DEV.getCode() == code) {
		      return Environment.DEV;
		    } else if (Environment.SIT.getCode() == code) {
		      return Environment.SIT;
		    }
		    return Environment.UnKnow;
		  }
}
