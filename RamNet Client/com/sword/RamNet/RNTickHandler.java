package com.sword.RamNet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class RNTickHandler implements ITickHandler {
	private ArrayList<GuiPlayerInfo> oldPlayersList = null;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// Is the world loaded?
		if (Minecraft.getMinecraft().theWorld != null) {
			ArrayList<GuiPlayerInfo> onlinePlayers = (ArrayList<GuiPlayerInfo>)Minecraft.getMinecraft().thePlayer.sendQueue.playerInfoList;
			
			for (int i = 0; i < Minecraft.getMinecraft().theWorld.loadedEntityList.size(); i++) {
				if (Minecraft.getMinecraft().theWorld.loadedEntityList.get(i) instanceof EntityOtherPlayerMP) {
					EntityOtherPlayerMP p = (EntityOtherPlayerMP)Minecraft.getMinecraft().theWorld.loadedEntityList.get(i);
					//System.out.println(p.username);
				}
			}
			
			if (oldPlayersList != null) {
				ArrayList<GuiPlayerInfo> playersJoined = new ArrayList<GuiPlayerInfo>(onlinePlayers);
				
				// Newly logged in players
				playersJoined.removeAll(oldPlayersList);
				oldPlayersList.removeAll(onlinePlayers);

				
				// Logged in list
				for (GuiPlayerInfo g : playersJoined) {
					PlayerLoginEvent login = new PlayerLoginEvent(g.name);
					MinecraftForge.EVENT_BUS.post(login);
				}
				
				// Logged out list
				for (GuiPlayerInfo g : oldPlayersList) {
					PlayerLogoutEvent logout = new PlayerLogoutEvent(g.name);
					MinecraftForge.EVENT_BUS.post(logout);
				}
			}
			
			// Update the old player list so our future comparisons work!
			oldPlayersList = new ArrayList<GuiPlayerInfo>(onlinePlayers);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

}
