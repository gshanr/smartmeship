package looci.osgi.serv.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import looci.osgi.serv.util.XString;


/**
 * Looci logger
 */
public class LLog {
	
	
	private HashMap<String, List<LLogListener>> logListeners;
	private List<LLogListener> allListener;
	
	private static LLog instance = null;;
	
	public static LLog getI(){
		if(instance == null){
			instance = new LLog();
		}
		return instance;
	}
	
	private LLog(){
		logListeners = new HashMap<String, List<LLogListener>>();
		allListener = new ArrayList<LLogListener>();		
		try {
			File f = new File("looci/logConfig.txt");
			if(f.exists()){
				System.out.println("logger initalising from file");
				Scanner logger = new Scanner( f);
				LLogListener llog = new LLogSyso();
				LLogListener llogAll = new LlogFullSyso();
				while(logger.hasNext()){
					String next = logger.next();
					String[] parts = XString.split(next, ":");
					boolean printAll = false;
					if(parts.length == 2 && parts[1].equals("y")){
						printAll = true;
					}						
					if(parts[0].equals("ALL")){
						if(printAll){
							allListener.add(llogAll);
						}else{
							allListener.add(llog);
						}
					} else{
						if(printAll){
							addListener(parts[0], llogAll);
						}else{
							addListener(parts[0], llog);
						}
					}
				}	

			} else{
				System.out.println("logger found no file, logging all to syso");
				allListener.add(new LLogSyso());
			}
		} catch (FileNotFoundException e1) {
		}
		
	}
	
	public void notify(String src, String msg){
		List<LLogListener> thislogList = logListeners.get(src);
		if(thislogList != null){
			notifyList(thislogList, src, msg);
		}
		notifyList(allListener,src,msg);
	}
	
	private void notifyList(List<LLogListener> listeners, String src, String msg){
		for(LLogListener l : listeners){
			l.logMessage(src, "", msg);
		}
	}
	
	public void addListener(String src, LLogListener listener){
		if(src == null){
			allListener.add(listener);
		} else{
			List<LLogListener> add = logListeners.get(src);
			if(add == null){
				add = new ArrayList<LLogListener>();
				logListeners.put(src,add);
			}
			add.add(listener);
		}
	}
	
	public void removeListener(String src, LLogListener listener){
		if(src == null){
			allListener.remove(listener);
		} else{
			List<LLogListener> add = logListeners.get(src);
			if(add != null){
				add.remove(listener);
				if(add.size() == 0){
					logListeners.remove(src);
				}
			}
		}
	}
	
	
		
	public static void out(String src, String msg){
		getI().notify(src, msg);
	}
	
	public static void out(Object object, String msg){
		getI().notify(object.getClass().getCanonicalName(),msg);
	}
	
	public static void listenStr(String src, LLogListener listener){
		getI().addListener(src, listener);
	}
	
	@SuppressWarnings("rawtypes")
	public static void listen(Class src, LLogListener listener){
		if(src != null){
			getI().addListener(src.getCanonicalName(), listener);
		} else{
			getI().addListener(null,listener);
		}
	}	
	
	public static void reboot(){
		instance = new LLog();
	}
}
