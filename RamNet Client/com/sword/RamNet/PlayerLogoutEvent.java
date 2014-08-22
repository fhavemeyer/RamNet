package com.sword.RamNet;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class PlayerLogoutEvent extends Event {
	public String playerName;
	
	public PlayerLogoutEvent(String username) {
		this.playerName = username;
	}
}
