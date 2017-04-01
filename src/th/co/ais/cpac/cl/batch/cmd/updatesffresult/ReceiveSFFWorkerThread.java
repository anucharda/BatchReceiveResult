/**
 * 
 */
package th.co.ais.cpac.cl.batch.cmd.updatesffresult;

import th.co.ais.cpac.cl.common.Context;

public class ReceiveSFFWorkerThread implements Runnable {
	Context context;
	String jobType;
	String[] fileNames;
	String processPath;
	
	public ReceiveSFFWorkerThread(Context context, String jobType, String[] fileNames, String processPath) {
		super();
		this.context = context;
		this.jobType = jobType;
		this.fileNames = fileNames;
		this.processPath = processPath;
	}

	@Override
	public void run() {
		 context.getLogger().info("Start UpdateSFFResultProcess Execute....");
		 context.getLogger().info("Trigger Update SSF Result Process....");
		 new UpdateSFFResultProcess().executeProcess(context,jobType,fileNames,processPath); 
		 context.getLogger().info("End UpdateSFFResultProcess Execute....");
	}
	
}
