/**
LooCI Copyright (C) 2013 KU Leuven.
All rights reserved.

LooCI is an open-source software development kit for developing and maintaining networked embedded applications;
it is distributed under a dual-use software license model:

1. Non-commercial use:
Non-Profits, Academic Institutions, and Private Individuals can redistribute and/or modify LooCI code under the terms of the GNU General Public License version 3, as published by the Free Software Foundation
(http://www.gnu.org/licenses/gpl.html).

2. Commercial use:
In order to apply LooCI in commercial code, a dedicated software license must be negotiated with KU Leuven Research & Development.

Contact information:
  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
Address:
  iMinds-DistriNet, KU Leuven
  Celestijnenlaan 200A - PB 2402,
  B-3001 Leuven,
  BELGIUM. 
 */
package looci.osgi.serv.util;

/**
 * Timer that calls back the listener after a given time interval has expired
 * Timer can be repeating or non repeating
 * 
 * @author Jef Maerien
 *
 */
public class LoociTimer extends Thread{
	
	//minimal timer interval is 100 miliseconds
	public static final long MIN_INTERVAL = 100;
	public static final long SECOND_TIME = 1000;
	
	private String name = "LoociTimer";
	private ITimeListener listener;
	private long interval;
	private boolean repeating;
	private long prevExecution;
	private long nextExecution;
	private boolean active;

	
	public LoociTimer(String name, ITimeListener listener, long interval, boolean repeating){
		this(listener,interval,repeating);
		this.name = name;		
	}
	
	/**
	 * 
	 * @param listener
	 * @param interval
	 * 	interval in miliseconds
	 * @param repeating
	 */
	public LoociTimer(ITimeListener listener, long interval, boolean repeating){
		this.listener = listener;
		if(interval < MIN_INTERVAL){
			throw new IllegalArgumentException();
		}
		this.interval = interval;
		this.repeating = repeating;
	}
	
	public void startRunning(){
		active = true;
		prevExecution = System.currentTimeMillis();
		nextExecution = prevExecution + interval;
		this.start();
	}
	
	
	/**
	 * Interval in miliseconds
	 * @param timeLength
	 * 	
	 */
	public void updateInterval(long timeLength){
		if(timeLength < MIN_INTERVAL){
			throw new IllegalArgumentException();
		}
		this.interval = timeLength;
		nextExecution = prevExecution + timeLength;
		this.interrupt();
	}
	
	
	public void stopRunning(){
		active = false;
		this.interrupt();
	}
	
	public void run() {
		while(active){
			synchronized (this) {
				try {
					long diff = nextExecution-System.currentTimeMillis();
					if(diff > 0){
						this.wait(diff);
					}
				} catch (InterruptedException e) {}			
				
			}
			if(System.currentTimeMillis()>nextExecution && active){
				listener.doOnTimeEvent(this);
				
				if(repeating){
					prevExecution = System.currentTimeMillis();
					nextExecution = prevExecution + interval;
				} else{
					active = false;
				}
			}

			
		}
		
	}
	
	/**
	 * Returns the name of this timer, which can be set at creation
	 * Allows for differentiating between multiple timers.
	 */
	public String getTimerName() {
		return name;
	}
	
	

}
