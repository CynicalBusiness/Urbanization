package me.capit.urbanization.command;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;

import me.capit.urbanization.CommandController.CResponse;

public interface UrbanizationCommandParser {
	public class UrbanizationCommands{
		private static Map<String,UrbanizationCommandParser> parsers = 
				new TreeMap<String,UrbanizationCommandParser>();
		public static void registerCommandParser(String cmd, Class<? extends UrbanizationCommandParser> c){
			try {
				parsers.put(cmd, c.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		public static UrbanizationCommandParser getParser(String cmd){
			return parsers.get(cmd);
		}
		
		public static CResponse executeCommand(String cmd, String[] args, CommandSender s){
			return getParser(cmd)!=null ? getParser(cmd).parseCommand(args, s) : CResponse.FAILED_ARGUMENTS;
		}
	}
	
	public CResponse parseCommand(String[] args, CommandSender s);
}
