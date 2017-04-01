package th.co.ais.cpac.cl.batch.cmd.writeoff;

import th.co.ais.cpac.cl.common.Context;

public class ReceiveWriteOffBatchWorkerThread implements Runnable{
	Context context;
	String processPath;
	String ackFileName;
	String inboundFileName;

	public ReceiveWriteOffBatchWorkerThread(Context context, String processPath, String ackFileName,
			String inboundFileName) {
		super();
		this.context = context;
		this.processPath = processPath;
		this.ackFileName = ackFileName;
		this.inboundFileName = inboundFileName;
	}

	@Override
	public void run() {
		 context.getLogger().info("Start UpdateWriteOffResultProcess Execute....");		
		 context.getLogger().info("Trigger UpdateWriteOffResultProcess....");
		 new UpdateWriteOffResultProcess().executeProcess(context,processPath,ackFileName,inboundFileName); 
		 context.getLogger().info("End UpdateWriteOffResultProcess Execute....");
	}

}
