package com.sword.RamNet;

public class RNPlayerLocation {
	private double x;
	private double y;
	private double z;
	private int worldID;
	private String playerName;
	
	public RNPlayerLocation(String playerName, double x, double y, double z) {
		this.playerName = playerName;
		this.x = x;
		this.y = y;
		this.z = z;
		
		// Default this for now
		this.worldID = 0;
	}
}
