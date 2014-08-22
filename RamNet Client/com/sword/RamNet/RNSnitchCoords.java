package com.sword.RamNet;

public class RNSnitchCoords {
	static final String STRING_SEPARATOR = ":";
	
	int world;
	double x;
	double y;
	double z;
	
	public RNSnitchCoords(int world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(world).append(STRING_SEPARATOR);
		sb.append(x).append(STRING_SEPARATOR);
		sb.append(y).append(STRING_SEPARATOR);
		sb.append(z);
		
		return sb.toString();
	}
}
