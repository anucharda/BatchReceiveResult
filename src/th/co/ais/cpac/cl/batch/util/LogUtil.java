package th.co.ais.cpac.cl.batch.util;

import th.co.ais.cpac.cl.common.UtilityLogger;

public class LogUtil {
	public static void initialLogger() throws Exception{
		UtilityLogger.initailLogger(FileUtil.getLogPath());
	}

}
