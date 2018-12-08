package me.nipo.tabmodifier.config;

import java.io.File;

import me.nipo.tabmodifier.tabmodifier;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Config {
	
	private static CommentedConfigurationNode configNode;
	
	public static void buildConfig(ConfigurationLoader<CommentedConfigurationNode> loader, File ConfigFile){
		if (!ConfigFile.exists()){
			try{
				ConfigFile.createNewFile();
				configNode = loader.load();
				setupConfig();
				loader.save(configNode);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			try{
				configNode = loader.load();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private static void setupConfig(){
		configNode.getNode("InitialValue", "Prefix").setValue("&d[&bMC&d]");
		configNode.getNode("InitialValue", "Suffix").setValue("&9[&b0&9]");
		configNode.getNode("InitialValue").setComment("If players' prefix is not set yet, plugin will use initial value instead");
		configNode.getNode("showPrefix").setValue("true");
		configNode.getNode("showSuffix").setValue("true");
		configNode.getNode("showDisplayName").setValue("true");
		configNode.getNode("showHeader").setValue("true");
		configNode.getNode("showFooter").setValue("true");
		configNode.getNode("HeaderValue").setValue("&a&lThis is &b&lHeader");
		configNode.getNode("FooterValue").setValue("&a&lThis is &d&lFooter\n&b&lLook, the SecondLine");
		configNode.getNode("UpdateInterval", "Header&Footer").setValue(5);
		configNode.getNode("UpdateInterval", "NameList").setValue(10);
		configNode.getNode("UpdateInterval", "Header&Footer").setComment("How long, in seconds, should plugin update tablist's header & footer");
		configNode.getNode("UpdateInterval", "NameList").setComment("How long, in seconds, should plugin update tablist's playernames");
		configNode.getNode("RefreshDelay").setValue(5);
		configNode.getNode("RefreshDelay").setComment("How long, in ticks, should plugin wait before refresh tablist when player join server, set to 0 to disable delay");
	}
	
	public static String getInitPrefix(){
		return configNode.getNode("InitialValue", "Prefix").getString("&a[&bMC&a]");
	}
	
	public static String getInitSuffix(){
		return configNode.getNode("InitialValue", "Suffix").getString("&a[&b0&a]");
	}
	
	public static boolean showPrefix(){
		return configNode.getNode("showPrefix").getBoolean(true);
	}
	
	public static boolean showSuffix(){
		return configNode.getNode("showSuffix").getBoolean(true);
	}
	
	public static boolean showDisplayName(){
		return configNode.getNode("showDisplayName").getBoolean(true);
	}
	
	public static boolean showHeader(){
		return configNode.getNode("showHeader").getBoolean(true);
	}
	
	public static boolean showFooter(){
		return configNode.getNode("showFooter").getBoolean(true);
	}
	
	public static String getheaderValue(){
		return configNode.getNode("HeaderValue").getString("This is &b&lHeader");
	}
	
	public static String getfooterValue(){
		return configNode.getNode("FooterValue").getString("This is &d&lFooter");
	}
	
	public static int getHFUpdateinterval(){
		return configNode.getNode("UpdateInterval", "Header&Footer").getInt(30);
	}
	
	public static int getNameUpdateInterval(){
		return configNode.getNode("UpdateInterval", "NameList").getInt(60);
	}
	
	public static int getDelay(){
		return configNode.getNode("RefreshDelay").getInt();
	}
	
	public static void setHeader(String header){
		configNode.getNode("HeaderValue").setValue(header);
	}
	public static void setFooter(String footer){
		configNode.getNode("FooterValue").setValue(footer);
	}
	
	public static void saveConfig(){
		try{
			tabmodifier.getInstance().getLoader().save(configNode);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void reloadConfig(){
		try{
			configNode = tabmodifier.getInstance().getLoader().load();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
