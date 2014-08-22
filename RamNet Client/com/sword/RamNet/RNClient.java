package com.sword.RamNet;

import java.io.*;
import java.net.*;
import org.apache.commons.lang3.StringUtils;

public class RNClient implements RNPacketTags {
	final DatagramSocket clientSocket = new DatagramSocket();
	private static final String SEPARATOR = "|";
	InetAddress serverAddress;
	int serverPort;
	
	public RNClient(String server, int port) throws UnknownHostException, SocketException {
		serverAddress = InetAddress.getByName(server);
		serverPort = port;
	}
	
	private void sendData(byte[] data) {
		if (data != null && data.length > 0) {
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, serverPort);
			try {
				clientSocket.send(sendPacket);
				//clientSocket.close();
			} catch (IOException e) {
				System.out.println("Could not send data packet.");
				e.printStackTrace();
			}
		}
	}
	
	public void sendSnitchEntry(String player, String minecraftServer, RNSnitchCoords coords) {
		sendSnitchData(SNITCH_ENTRY, player, minecraftServer, coords);
	}
	
	public void sendSnitchLogin(String player, String minecraftServer, RNSnitchCoords coords) {
		sendSnitchData(SNITCH_LOGIN, player, minecraftServer, coords);
	}
	
	public void sendSnitchLogout(String player, String minecraftServer, RNSnitchCoords coords) {
		sendSnitchData(SNITCH_LOGOUT, player, minecraftServer, coords);
	}
	
	public void sendSnitchData(String messageTag, String player, String minecraftServer, RNSnitchCoords coords) {
		StringBuilder sb = new StringBuilder();
		sb.append(messageTag).append(SEPARATOR).append(player).append(SEPARATOR).append(coords.toString()).append(SEPARATOR).append(minecraftServer);
		sendData(sb.toString().getBytes());
		System.out.println(sb.toString());
	}
	
	public void sendPlayerLogout(String player, String minecraftServer) {
		StringBuilder sb = new StringBuilder();
		sb.append(PLAYER_LOGOUT).append(SEPARATOR).append(player).append(SEPARATOR).append(minecraftServer);
		sendData(sb.toString().getBytes());
	}
	
	public void sendPlayerLogin(String player, String minecraftServer) {
		StringBuilder sb = new StringBuilder();
		sb.append(PLAYER_LOGIN).append(SEPARATOR).append(player).append(SEPARATOR).append(minecraftServer);
		sendData(sb.toString().getBytes());
	}
	
	public void sendPlayerPosition(String player, String minecraftServer, int reportType, RNSnitchCoords coords) {
		StringBuilder sb = new StringBuilder();
		sb.append(POSITION_REPORT).append(SEPARATOR).append(player).append(SEPARATOR).append(reportType).append(coords.toString()).append(SEPARATOR).append(minecraftServer);
		sendData(sb.toString().getBytes());
	}
}
