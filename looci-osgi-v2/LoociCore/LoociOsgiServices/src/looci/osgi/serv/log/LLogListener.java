package looci.osgi.serv.log;

public interface LLogListener {

	
	public void logMessage(String logName, String logLevel, String message);
	
}
