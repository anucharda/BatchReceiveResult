package th.co.ais.cpac.cl.batch.cmd.writeoff;

import th.co.ais.cpac.cl.common.Context;

public class ReceiveWriteOffBatchWorker {

	public static void main(String[] args) {
		 Context context = new Context();
		 context.initailLogger("LoggerReceiveWriteOffBatchWorker", "XXXX|YYYYY");
		// TODO Auto-generated method stub
		 context.getLogger().info("Start ReceiveWriteOffBatchWorker....");
		//suspendJobType=S,terminateJobType=T,reconnectJobType=R
		 String ackFileName="";//Sync File Name
		 execute(context,ackFileName);///?????????????????
		 context.getLogger().info("End ReceiveWriteOffBatchWorker....");
	}
	
	public static void execute(Context context,String ackFileName){
		 context.getLogger().info("Start UpdateWriteOffResultProcess Execute....");
		 //Check จากชื่อไฟล์ว่าเป็น batch suspense/reconnect/terminate
		
		 context.getLogger().info("Trigger UpdateWriteOffResultProcess....");
		 new UpdateWriteOffResultProcess().executeProcess(context,ackFileName); ///?????????????????
		 context.getLogger().info("End UpdateWriteOffResultProcess Execute....");
	}
	

}
