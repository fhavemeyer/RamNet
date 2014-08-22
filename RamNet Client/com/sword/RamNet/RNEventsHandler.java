package com.sword.RamNet;

import cpw.mods.fml.client.FMLClientHandler;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraft.util.ChatMessageComponent;

public class RNEventsHandler {
	private RNClient client;
	Pattern snitchMessage = Pattern.compile("([a-zA-Z0-9_]{1,16}) (entered|logged in to|logged out in|) snitch at \\S* \\[(\\-?\\d{1,5}) (\\d{1,2}) (\\-?\\d{1,5})\\]");
	
	public RNEventsHandler() {
		try {
			client = new RNClient("127.0.0.1", 9002);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	@ForgeSubscribe
	public void chatReceived(ClientChatReceivedEvent event) {
		Matcher m = snitchMessage.matcher(event.message);
		
		if (m.find()) {
			String username = m.group(1);
			String type = m.group(2);
			double x = Double.parseDouble(m.group(3));
			double y = Double.parseDouble(m.group(4));
			double z = Double.parseDouble(m.group(5));
			
			RNSnitchCoords coords = new RNSnitchCoords(0, x, y, z);

			// Can I assume everyone is running java 7? I don't think I can, so I'll use if-then here
			if (type.equals("entered")) {
				client.sendSnitchEntry(username, getServer(), coords);
			} else if (type.equals("logged in to")) {
				client.sendSnitchLogin(username, getServer(), coords);
				System.out.println("logged into snitch field");
			} else if (type.equals("logged out in")) {
				client.sendSnitchLogout(username, getServer(), coords);
				System.out.println("logged out in snitch field");
			}
		}
	}
	
	@ForgeSubscribe
	public void onPlayerLogin(PlayerLoginEvent event) {
		System.out.println(event.playerName + " logged in!");
		client.sendPlayerLogin(event.playerName, getServer());
	}
	
	@ForgeSubscribe
	public void onPlayerLogout(PlayerLogoutEvent event) {
		System.out.println(event.playerName + " logged out!");
		client.sendPlayerLogout(event.playerName, getServer());
	}
	
	public String getServer() {
		return "Civcraft";
	}
}
