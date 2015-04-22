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
package looci.osgi.serv.constants;

import java.util.ArrayList;


public class EventTypeRepository {

    
    private static EventTypeRepository instance = null;

	public short getEventTypeFromString(String type){
		EventType myType = getInstance().getType(type);
		if(myType != null){
			return myType.getType();
		} else{
			return Short.parseShort(type);
		}		
	}
	
	public String getEventStringFromType(short type){
		EventType myType = getInstance().getType(type);
		if(myType != null){
			return myType.getName();
		} else{
			return Short.toString(type);
		}
	}
    
    
    public static EventTypeRepository getInstance(){
    	if(instance == null){
    		instance = new EventTypeRepository();
    	}
    	return instance;
    }
    
    
    private ArrayList<EventType> eventTypes;
    
    private EventTypeRepository(){
    	eventTypes = new ArrayList<EventType>();
    	loadEventTypes();
    }

	private void loadEventTypes() {
		addEventType(new EventType(EventTypes.ANY_EVENT, "any_ev"));	
		addEventType(new EventType(EventTypes.SWITCH_READING, "switch_ev"));
		addEventType(new EventType(EventTypes.BUTTON_READING, "button_ev"));
		addEventType(new EventType(EventTypes.TEMP_READING, "temp_ev"));
		addEventType(new EventType(EventTypes.DO_OP_EVENT, "do_ev"));		
		addEventType(new EventType(EventTypes.ON_OFF_EVENT, "on_off_ev"));
		addEventType(new EventType(EventTypes.STRING_EVENT, "string_ev"));	
	}
	
	public String[] getAvailableEventTypes(){
		String[] eventTypeStrings =new String[eventTypes.size()];
		for(int i =0 ; i < eventTypes.size(); i ++){
			eventTypeStrings[i] = eventTypes.get(i).getName();
		}
		return eventTypeStrings;
	}
	
	public void addEventType(EventType type){
		eventTypes.remove(type);
		eventTypes.add(type);
	}
	
	public EventType getType(String type){
		for(int i =0 ; i < eventTypes.size(); i++){
			if(eventTypes.get(i).getName().equals(type)){
				return eventTypes.get(i);
			}
		}
		return null;
	}
	
	public EventType getType(short type){
		for(int i =0 ; i < eventTypes.size(); i++){
			if(eventTypes.get(i).getType() == type){
				return eventTypes.get(i);
			}
		}
		return null;
	}
	
	public boolean removeType(short type){
		return eventTypes.remove(new EventType(type, ""));
	}
	
	
	public static String printEventArray(short[] array){
		String res = "[";
				
		res += (array.length>=1)?""+getInstance().getEventStringFromType(array[0]):"";
		for (int i = 1; i < array.length; i++) {
			res = res + "," + getInstance().getEventStringFromType(array[i]);
		}
		res = res + "]";
		return res;
	}

	public ArrayList<EventType> getEventTypeList() {
		return eventTypes;
	}	
    
    
}
