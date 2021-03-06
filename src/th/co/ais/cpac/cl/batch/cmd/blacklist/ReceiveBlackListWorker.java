package th.co.ais.cpac.cl.batch.cmd.blacklist;

import java.io.File;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.ConstantsBusinessUtil;
import th.co.ais.cpac.cl.batch.cnf.CNFDatabase;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchPathInfo;
import th.co.ais.cpac.cl.batch.util.BatchReceiveUtil;
import th.co.ais.cpac.cl.batch.util.BatchUtil;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.LogUtil;
import th.co.ais.cpac.cl.batch.util.PropertiesReader;
import th.co.ais.cpac.cl.common.Context;

public class ReceiveBlackListWorker {

	public static void main(String[] args) throws Exception {
		Context context = new Context();
		int limitConcurrentThread = 5;
		try{
			context.initailLogger("LoggerReceive", "ReceiveBlackListWorker");
			// TODO Auto-generated method stub
			context.getLogger().info("----------------------- Start ReceiveBlackListWorker -----------------------");
			context.getLogger().info("Load configure....");
			PropertiesReader reader = new PropertiesReader("th.co.ais.cpac.cl.batch.properties.resource","SystemConfigPath");
		
			String jobType=args[0];//From Parameter
			BigDecimal batchTypeId=BatchUtil.getBatchTypeId(jobType);
			int environment=BatchUtil.getEnvionment();

			//Find sync file on path.
			String inboundSyncPath="" ;
			String inboundDataPath="" ;
			String processPath="";
			boolean doProcess=true;
			System.out.println("jobType ->"+jobType);
			if(ConstantsBusinessUtil.blacklistJobType.equals(jobType)){
				CNFDatabase cc = new CNFDatabase(FileUtil.getDBPath());
				CLBatchPathInfo pathResult=BatchReceiveUtil.getBatchPath(context,batchTypeId,environment);
				inboundSyncPath= pathResult.getPathInbound();
				inboundDataPath =pathResult.getPathInbound();
				processPath = reader.get("blacklist.processPath");
			}else{
				doProcess=false;
				context.getLogger().info("not found job type : "+jobType);
			}
			context.getLogger().info("inboundSyncPath : "+inboundSyncPath);
			if(doProcess){
				File[] files = FileUtil.getAllFilesThatMatchFilenameExtensionAscendingOrder(inboundSyncPath, "sync");

				String syncFileName = "";

				if(files!=null && files.length>0){
					context.getLogger().info("Sync file size --> "+files.length);
					int totalThread=Integer.parseInt(reader.get("sff.total.thread"));
					int loopCnt=files.length;
					if(files.length>totalThread){
						loopCnt=totalThread;
					}
					
					try{
						limitConcurrentThread=Integer.parseInt(reader.get("sff.limit.concurrent.thread"));
						if(limitConcurrentThread==0){ //Set 0 is same total thread.
							limitConcurrentThread=loopCnt;
						}
					}catch(Exception e){
						limitConcurrentThread=loopCnt;
					}
					ExecutorService executorService = Executors.newFixedThreadPool(limitConcurrentThread);
				
					//Loop for do all sync file.
					for(int i=0; i<loopCnt; i++){
						String doPath = processPath;
						String syncFile = files[i].getPath();
						syncFileName = files[i].getName();
						context.getLogger().info("Sync file is --> "+syncFile);
						
						String[] fileNames=new String[1];
						fileNames[0]=syncFileName.replaceAll(".sync",ConstantsBatchReceiveResult.blacklistExt);
						
						for(int j=0; j<fileNames.length; j++){
							File source = new File(inboundDataPath+"/"+fileNames[j]);
							if(source.exists()){
								File dest = new File(processPath+"/"+fileNames[j]);
								FileUtil.copyFile(source, dest);
								context.getLogger().info("Copy file to process directory successed --> "+fileNames[j]);
								//Delete source file after copy to process directory completed.
								if(source.delete()){
									context.getLogger().info("Delete file from inbound directory successed --> "+source.getParent());
								}else{
									throw new Exception("Error occur delete file from inbound directory --> "+source.getParent());
								}
							}else{
								//Skip to next file.
								continue;
							}
						}
						//Copy sync file to process directory.
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
						executorService.execute(new ReceiveBlackListWorkerThread(context, jobType, fileNames[0], processPath));
						//new Thread ( () -> execute(context, jobType,fileNames[0],doPath) ).start();
						//execute(context, jobType,fileNames,doPath);
					}
					executorService.shutdown();
					while(!executorService.isTerminated()){
						//sleep(500);
					}
				}else{
					context.getLogger().info("Not found sync file in directory --> "+inboundSyncPath);
				}
			}

			
			context.getLogger().info("----------------------- End ReceiveSFFWorker ----------------------- ");
		}catch(Exception e){
			e.printStackTrace();
			context.getLogger().error(  "Error->"+e.getMessage()+": "+e.getCause().toString() ,e);
		}
	}
	
//	public static void execute(Context context,String jobType,String fileName,String processPath){
//		 context.getLogger().info("Start UpdateSFFResultProcess Execute....");
//		 context.getLogger().info("Trigger Update SSF Result Process....");
//		 new UpdateBlacklistResultProcess().executeProcess(context,jobType,fileName,processPath);
//		 context.getLogger().info("End UpdateSFFResultProcess Execute....");
//	}
	
}
