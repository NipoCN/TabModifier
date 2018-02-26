package xin.omen.tabmodifier.config;

import xin.omen.tabmodifier.TabModifier;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class Config {
	
	private static Config confinstance;
	
	public static Config getInstance(){
		if (confinstance == null){
			confinstance = new Config();
		}
		return confinstance;
	}
	
	Path ConfigFile = Paths.get(TabModifier.getInstance().getConfigDir() + "/config.conf");
	ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(ConfigFile).build();
	private CommentedConfigurationNode configNode;
	
	public void loadConfig(){
		if (!Files.exists(ConfigFile)){
			try{
				Files.createFile(ConfigFile);
				load();
				setup();
				save();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		else{
			load();
		}
	}
	
	public void load(){
		try{
			configNode = loader.load();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public CommentedConfigurationNode get(){
		return configNode;
	}
	
	public void setup(){
		get().getNode("Default", "prefix").setValue("&a[default]");
		get().getNode("Default", "suffix").setValue("&9[default]");
		get().getNode("Default").setComment("set prefix and suffix when user/group's unique prefix/suffix is missing/unset");
	}
	
	public void save(){
		try{
			loader.save(configNode);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public String getprefix(){
		return get().getNode("Default", "prefix").getString();
	}
	
	public String getsuffix(){
		return get().getNode("Default", "suffix").getString();
	}
}
