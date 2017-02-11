package th.co.ais.cpac.cl.batch.cmd.waivebatch;

import th.co.ais.cpac.cl.common.Context;

public class ReceiveWaiveBatchWorker {

	public static void main(String[] args) {
		 Context context = new Context();
		 context.initailLogger("LoggerReceiveWaiveBatchWorker", "XXXX|YYYYY");
		// TODO Auto-generated method stub
		 context.getLogger().info("Start ReceiveWaiveBatchWorker....");
		//suspendJobType=S,terminateJobType=T,reconnectJobType=R
		 String syncFileName="";//Sync File Name
		 execute(context,syncFileName);///?????????????????
		 context.getLogger().info("End ReceiveWaiveBatchWorker....");
	}
	
	public static void execute(Context context,String syncFileName){
		 context.getLogger().info("Start UpdateSFFResultProcess Execute....");
		 //Check จากชื่อไฟล์ว่าเป็น batch suspense/reconnect/terminate
		
		 context.getLogger().info("Trigger Update SSF Result Process....");
		 new UpdateWaiveBatchResultProcess().executeProcess(context,syncFileName); ///?????????????????
		 context.getLogger().info("End ReceiveWaiveBatchWorker Execute....");
	}
	

}
