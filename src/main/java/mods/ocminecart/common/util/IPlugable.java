package mods.ocminecart.common.util;

import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;

public interface IPlugable {
	
	public void onPlugMessage(Plug plug, Message message);
	
	public void onPlugConnect(Plug plug, Node node);
	
	public void onPlugDisconnect(Plug plug, Node node);
	
}
