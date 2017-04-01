/**
 * 
 */
package th.co.ais.cpac.cl.batch.cmd.blacklist;

import th.co.ais.cpac.cl.common.Context;

public class ReceiveBlackListWorkerThread implements Runnable{
	Context context;
	String jobType;
	String fileName;
	String processPath;
	
	public ReceiveBlackListWorkerThread(Context context, String jobType, String fileName, String processPath) {
		super();
		this.context = context;
		this.jobType = jobType;
		this.fileName = fileName;
		this.processPath = processPath;
	}

	@Override
	public void run() {
		 context.getLogger().info("Start UpdateSFFResultProcess Execute....");
		 context.getLogger().info("Trigger Update SSF Result Process....");
		 new UpdateBlacklistResultProcess().executeProcess(context,jobType,fileName,processPath);
		 context.getLogger().info("End UpdateSFFResultProcess Execute....");
	}
	
	
}
