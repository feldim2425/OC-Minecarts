package mods.ocminecart.common.hooks;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.minecart.MinecartEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;


@Cancelable
public class MinecartKillEvent extends MinecartEvent {

	private final DamageSource source;

	public MinecartKillEvent(EntityMinecart minecart, DamageSource source) {
		super(minecart);
		this.source = source;
	}

	public DamageSource getDamageSource() {
		return source;
	}

}
