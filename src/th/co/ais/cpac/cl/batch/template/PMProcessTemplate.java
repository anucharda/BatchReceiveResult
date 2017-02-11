package th.co.ais.cpac.cl.batch.template;

import java.io.File;

import th.co.ais.cpac.cl.batch.cnf.CNFDatabase;
import th.co.ais.cpac.cl.batch.cnf.PMDatabase;
import th.co.ais.cpac.cl.common.Context;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;

public abstract class PMProcessTemplate {
	protected abstract String getPathDatabase();
	protected abstract String getPMPathDatabase();
	protected CNFDatabase database;
	protected PMDatabase pmDatabase;
	protected Context context;

	protected DBConnectionPools getConnection(Context ctx) {
		DBConnectionPools<CNFDatabase, UtilityLogger> connections = new DBConnectionPools<>(database, ctx.getLogger());
		return connections;
	}

	public boolean execute() {
		try {
			if (!initial()) {
				return false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

	protected boolean initial() {
		String fileConfig=null;
		 if (getPathDatabase() != null) {
			 fileConfig = getPathDatabase();
		 }

		if (fileConfig == null) {
			System.out.println("File Configuration not found.");
			return false;
		}

		File f = new File(fileConfig);
		if (!f.isFile() || !f.canRead()) {
			System.out.println("File configuration can read.");
			return false;
		}
		database = new CNFDatabase(fileConfig);

		context = new Context();
		context.initailLogger("LoggerMasterBatchInfo", System.currentTimeMillis() + "");

		DBConnectionPools<CNFDatabase, UtilityLogger> pool = new DBConnectionPools<>(database, context.getLogger());
		pool.buildeDataSource();

		if (!pool.poolActive()) {
			System.out.println("Database connection pool error.");
			return false;
		}
		/*****Get PMDB*********/
		 if (getPMPathDatabase() != null) {
			 fileConfig = getPMPathDatabase();
		 }

		if (fileConfig == null) {
			System.out.println("File Configuration not found.");
			return false;
		}

		f = new File(fileConfig);
		if (!f.isFile() || !f.canRead()) {
			System.out.println("File configuration can read.");
			return false;
		}
		pmDatabase = new PMDatabase(fileConfig);

		context = new Context();
		context.initailLogger("LoggerMasterBatchInfo", System.currentTimeMillis() + "");

		DBConnectionPools<PMDatabase, UtilityLogger> pmPool = new DBConnectionPools<>(pmDatabase, context.getLogger());
		pmPool.buildeDataSource();

		if (!pmPool.poolActive()) {
			System.out.println("Database connection pool error.");
			return false;
		}
		/*****Get PMDB*********/
		return true;
	}
	


}
