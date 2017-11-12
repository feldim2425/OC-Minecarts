package mods.ocminecart.integration.jei;


import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;

@JEIPlugin
public class JeiAddon implements IModPlugin {

	private static IJeiRuntime runtime;

	public static IJeiRuntime getRuntime() {
		return runtime;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {

	}

	@Override
	public void register(IModRegistry registry) {

	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
		JeiAdapter.enableAdapter();
	}
}
