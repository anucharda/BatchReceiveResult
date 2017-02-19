package th.co.ais.cpac.cl.batch.template;

import java.io.File;

import th.co.ais.cpac.cl.batch.cnf.CNFDatabase;
import th.co.ais.cpac.cl.common.Context;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;

public abstract class ProcessTemplate {
	protected abstract String getPathDatabase();
	protected CNFDatabase database;
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
		context.initailLogger("LoggerReceive", System.currentTimeMillis() + "");

		DBConnectionPools<CNFDatabase, UtilityLogger> pool = new DBConnectionPools<>(database, context.getLogger());
		pool.buildeDataSource();

		if (!pool.poolActive()) {
			System.out.println("Database connection pool error.");
			return false;
		}
		return true;
	}
	


}
