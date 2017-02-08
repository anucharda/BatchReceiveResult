package th.co.ais.cpac.cl.batch.cmd;

import java.util.ArrayList;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.bean.UpdateResultSSFBean;
import th.co.ais.cpac.cl.common.Context;

public class ReceiveSSFWorker {

	public static void main(String[] args) {
		 Context context = new Context();
		 context.initailLogger("LoggerWorkerReceive", "XXXX|YYYYY");
		// TODO Auto-generated method stub
		 context.getLogger().debug("Start WorkerReceive....");
		 execute(context);
		 context.getLogger().debug("End WorkerReceive....");
	}
	
	public static void execute(Context context){
		 context.getLogger().debug("Start Execute....");
		 //Check จากชื่อไฟล์ว่าเป็น batch suspense/reconnect/terminate
		
		 context.getLogger().debug("Trigger Update SSF Result Process....");
		 new UpdateSSFResultProcess().executeProcess(context);
		 context.getLogger().debug("End Execute....");
	}
	

}
