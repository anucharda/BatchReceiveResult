package th.co.ais.cpac.cl.batch.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;

public class Utility {
	public static String getusername(String jobType) {
		if (ConstantsBatchReceiveResult.suspendJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.suspendUsername;
		} else if (ConstantsBatchReceiveResult.terminateJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.terminateUsername;
		} else if (ConstantsBatchReceiveResult.reconnectJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.reconnectUsername;
		} else if (ConstantsBatchReceiveResult.waiveBatchJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.waiveBatchUsername;
		} else if (ConstantsBatchReceiveResult.writeOffJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.writeOffUsername;
		} else if (ConstantsBatchReceiveResult.blacklistJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.blacklistUsername;
		} else {
			return "undefined";
		}
	}

	public static int getActionID(String jobType) {
		if (ConstantsBatchReceiveResult.suspendJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.suspendOrderActionID;
		} else if (ConstantsBatchReceiveResult.terminateJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.terminateOrderActionID;
		} else if (ConstantsBatchReceiveResult.reconnectJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.reconnectOrderID;
		} else if (ConstantsBatchReceiveResult.waiveBatchJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.waiveBatchOrderID;
		} else if (ConstantsBatchReceiveResult.writeOffJobType.equals(jobType)) {
			return ConstantsBatchReceiveResult.writeOffOrderID;
		} else {
			return 0;
		}
	}

	public static String getJobName(String jobType) {
		if (ConstantsBatchReceiveResult.suspendJobType.equals(jobType)) {
			return "Suspend Job";
		} else if (ConstantsBatchReceiveResult.terminateJobType.equals(jobType)) {
			return "Terminate Job";
		} else if (ConstantsBatchReceiveResult.reconnectJobType.equals(jobType)) {
			return "Reconnect Job";
		} else if (ConstantsBatchReceiveResult.waiveBatchJobType.equals(jobType)) {
			return "Waive Batch Job";
		} else if (ConstantsBatchReceiveResult.writeOffJobType.equals(jobType)) {
			return "Write Off Job";
		} else {
			return "Other Job Undefine";
		}
	}

	public static Date convertStringToDate(String dateStr) {
		Date date;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("Failed to parse date: ", e);
		}
		return date;
	}
}
