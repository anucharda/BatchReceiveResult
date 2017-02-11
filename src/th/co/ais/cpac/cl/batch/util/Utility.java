package th.co.ais.cpac.cl.batch.util;

import th.co.ais.cpac.cl.batch.Constants;

public class Utility {
	public static String getusername(String jobType) {
		if (Constants.suspendJobType.equals(jobType)) {
			return Constants.suspendUsername;
		} else if (Constants.terminateJobType.equals(jobType)) {
			return Constants.terminateUsername;
		} else if (Constants.reconnectJobType.equals(jobType)) {
			return Constants.reconnectUsername;
		}else if (Constants.waiveBatchJobType.equals(jobType)) {
			return Constants.waiveBatchUsername;
		}else if (Constants.writeOffJobType.equals(jobType)) {
			return Constants.writeOffUsername;
		}else{
			return "undefined";
		}
	}
	public static int getActionID(String jobType) {
		if (Constants.suspendJobType.equals(jobType)) {
			return Constants.suspendOrderActionID;
		} else if (Constants.terminateJobType.equals(jobType)) {
			return Constants.terminateOrderActionID;
		} else if (Constants.reconnectJobType.equals(jobType)) {
			return Constants.reconnectOrderID;
		}else if (Constants.waiveBatchJobType.equals(jobType)) {
			return Constants.waiveBatchOrderID;
		}else if (Constants.writeOffJobType.equals(jobType)) {
			return Constants.writeOffOrderID;
		}else{
			return 0;
		}
	}
	public static String getJobName(String jobType) {
		if (Constants.suspendJobType.equals(jobType)) {
			return "Suspend Job";
		} else if (Constants.terminateJobType.equals(jobType)) {
			return "Terminate Job";
		} else if (Constants.reconnectJobType.equals(jobType)) {
			return "Reconnect Job";
		} else if (Constants.waiveBatchJobType.equals(jobType)) {
			return "Waive Batch Job";
		} else if (Constants.writeOffJobType.equals(jobType)) {
			return "Write Off Job";
		}else {
			return "Other Job Undefine";
		}
	}

}
