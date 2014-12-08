package me.capit.urbanization.command;

import me.capit.urbanization.CommandController.CResponse;

import org.bukkit.command.CommandSender;

public class CMDAdmin implements UrbanizationCommandParser {

	@Override
	public CResponse parseCommand(String[] args, CommandSender s) {
		// TODO!
		return CResponse.PLUGIN_ERROR;
	}

}
