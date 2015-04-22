package looci.osgi.serv.log;

public class LlogFullSyso implements LLogListener {

	@Override
	public void logMessage(String logName, String logLevel, String message) {
		System.out.println(logName+"-:"+message);
	}

}
