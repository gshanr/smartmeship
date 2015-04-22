package looci.osgi.serv.log;

public class LLogSyso implements LLogListener {

	
	@Override
	public void logMessage(String logName, String logLevel, String message) {
		System.out.println(message);
	}

	
}
