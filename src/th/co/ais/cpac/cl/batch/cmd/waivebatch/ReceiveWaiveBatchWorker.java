package th.co.ais.cpac.cl.batch.cmd.waivebatch;

import java.io.File;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.PropertiesReader;
import th.co.ais.cpac.cl.common.Context;

public class ReceiveWaiveBatchWorker {

	public static void main(String[] args) {
		try{
			Context context = new Context();
			context.initailLogger("LoggerReceiveSFFWorker", "ReceiveWaiveBatchWorker");
			// TODO Auto-generated method stub
			context.getLogger().info("----------------------- Start ReceiveWaiveBatchWorker -----------------------");
			context.getLogger().info("Load configure....");
			PropertiesReader reader = new PropertiesReader("th.co.ais.cpac.cl.batch.properties.resource", "SystemConfigPath");
			// suspendJobType=S,terminateJobType=T,reconnectJobType=R
			String jobType = args[0];// From Parameter
			boolean doProcess=true;
			String inboundSyncPath="" ;
			String inboundDataPath="" ;
			String processPath="";
			if(ConstantsBatchReceiveResult.waiveBatchJobType.equals(jobType)){
				inboundSyncPath= reader.get("waiveBatch.inboundSyncPath");
				inboundDataPath =reader.get("waiveBatch.inboundDataPath");
				processPath = reader.get("waiveBatch.processPath");
			}else{
				doProcess=false;
				context.getLogger().info("not found job type : "+jobType);
			}
			
			if(doProcess){
				File[] files = FileUtil.getAllFilesThatMatchFilenameExtensionAscendingOrder(inboundSyncPath, "sync");

				if(files!=null && files.length>0){
					context.getLogger().info("Sync file size --> "+files.length);
					//Loop for do all sync file.
					for(int i=0; i<files.length; i++){
						String doPath = processPath;
						String syncFile = files[i].getPath();
						context.getLogger().info("Sync file is --> "+syncFile);
						String syncFileName = files[i].getName();
						String dataFileName = syncFileName.replace(".sync", ".dat");
						context.getLogger().info("Data File Name --> "+dataFileName);
						File source = new File(inboundDataPath+"/"+dataFileName);
						if(source.exists()){
							File dest = new File(processPath+"/"+dataFileName);
							FileUtil.copyFile(source, dest);
							context.getLogger().info("Copy file to process directory successed --> "+dataFileName);
							//Delete source file after copy to process directory completed.
							if(source.delete()){
								context.getLogger().info("Delete file from inbound directory successed --> "+source.getParent());
							}else{
								throw new Exception("Error occur delete file from inbound directory --> "+source.getParent());
							}
						}
						File sourceSyncFile = new File(syncFile);
						File destSyncFile = new File(processPath+"/"+syncFileName);
						FileUtil.copyFile(sourceSyncFile, destSyncFile);
						//Delete source file after copy to process directory completed.
						if(sourceSyncFile.delete()){
							context.getLogger().info("Delete file from inbound directory successed --> "+sourceSyncFile.getParent());
						}else{
							throw new Exception("Error occur delete file from inbound directory --> "+sourceSyncFile.getParent());
						}
						//New thread for execute process.
						new Thread ( () -> execute(context, doPath, syncFileName, dataFileName) ).start();
					}

				}else{
					context.getLogger().info("Not found sync file in directory --> "+inboundSyncPath);
				}
			}
	
			context.getLogger().info("End ReceiveWaiveBatchWorker....");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void execute(Context context, String processPath,String syncFileName,String inboundFileName) {
		context.getLogger().info("Start UpdateWaiveBatchResultProcess Execute....");
		// Check จากชื่อไฟล์ว่าเป็น batch suspense/reconnect/terminate

		context.getLogger().info("Trigger UpdateWaiveBatchResultProcess....");
		new UpdateWaiveBatchResultProcess().executeProcess(context, processPath,syncFileName,inboundFileName); /// ?????????????????
		context.getLogger().info("End UpdateWaiveBatchResultProcess Execute....");
	}

}
