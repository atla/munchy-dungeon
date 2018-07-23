package com.leihwelt.munchyd.events;

public class SelectMunchkinByIdEvent {

	private int id;
	
	public SelectMunchkinByIdEvent (int id){
		this.id = id;
	}
	
	public int getId (){
		return id;
	}
	
	
}
