package com.sword.RamNet;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class PlayerLoginEvent extends Event {
	public String playerName;
	
	public PlayerLoginEvent(String username) {
		this.playerName = username;
	}
}
