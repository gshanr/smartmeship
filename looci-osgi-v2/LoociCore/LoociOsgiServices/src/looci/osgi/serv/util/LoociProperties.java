package looci.osgi.serv.util;

import java.io.FileReader;
import java.util.Properties;

import looci.osgi.serv.log.LLog;

public class LoociProperties {

	private Properties props;
	
	private LoociProperties(){
		
		
		
		props = new Properties();
		String loociConfig = null;
		try{
			loociConfig = System.getProperty("loociConfig");
			LLog.out(this, "loociConfig is: "+loociConfig);
		}catch(Exception e){
			LLog.out(this, "loociConfig not found");
		}
		if(loociConfig == null){
			try {
				props.load(new FileReader("looci/loociConfig.txt"));
				LLog.out(this,"config read from default file");
			} catch (Exception e) {
				LLog.out(this,"failed to config read from default file");
			} 
		} else{
			try {
				props.load(new FileReader("looci/"+loociConfig));
				LLog.out(this,"config read from file "+loociConfig);
			} catch (Exception e) {
				LLog.out(this,"failed config read from file "+loociConfig);
			}
		}
	}
	
	private String getProperty(String property){
		if(property == null){
			return null;
		}
		if(props == null){
			return null;
		}
		return props.getProperty(property);
	}
	
	
	private static LoociProperties instance;
	
	public static LoociProperties getI(){
		if(instance == null){
			instance = new LoociProperties();
		}
		return instance;
	}
	
	public static String getProp(String property){
		return getI().getProperty(property);
	}
	
}
