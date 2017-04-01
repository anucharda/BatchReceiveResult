package th.co.ais.cpac.cl.batch.cmd.waivebatch;

import th.co.ais.cpac.cl.common.Context;

public class ReceiveWaiveBatchWorkerThread implements Runnable{
	Context context;
	String processPath;
	String syncFileName;
	String inboundFileName;

	
	public ReceiveWaiveBatchWorkerThread(Context context, String processPath, String syncFileName,
			String inboundFileName) {
		super();
		this.context = context;
		this.processPath = processPath;
		this.syncFileName = syncFileName;
		this.inboundFileName = inboundFileName;
	}

	@Override
	public void run() {
		context.getLogger().info("Start UpdateWaiveBatchResultProcess Execute....");
		// Check จากชื่อไฟล์ว่าเป็น batch suspense/reconnect/terminate
		context.getLogger().info("Trigger UpdateWaiveBatchResultProcess....");
		new UpdateWaiveBatchResultProcess().executeProcess(context, processPath,syncFileName,inboundFileName); 
		context.getLogger().info("End UpdateWaiveBatchResultProcess Execute....");
	}
}
