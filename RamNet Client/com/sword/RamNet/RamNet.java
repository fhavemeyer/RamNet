package com.sword.RamNet;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;


@Mod(modid="RamNet", name="RamNet", version="0.1.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class RamNet {
	@Instance(value="RamNet")
	public static RamNet ramNet;
	
	@SidedProxy(clientSide="com.sword.RamNet.client.ClientProxy",
				serverSide="com.sword.RamNet.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		System.out.println("pre-init");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RNEventsHandler());
		TickRegistry.registerTickHandler(new RNTickHandler(), Side.CLIENT);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
