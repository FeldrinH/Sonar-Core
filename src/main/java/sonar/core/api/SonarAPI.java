package sonar.core.api;

import cpw.mods.fml.common.Loader;

/** Use this for all your interaction with the mod. This will be initilized by Sonar Core if it is loaded. Make sure you only register stuff once Sonar Core is loaded therefore in the FMLPostInitializationEvent */
public final class SonarAPI {

	public static final String MODID = "SonarCore";
	public static final String NAME = "SonarAPI";
	public static final String VERSION = "1.0";

	private static RegistryWrapper registry = new RegistryWrapper();
	private static FluidWrapper fluids = new FluidWrapper();
	private static InventoryWrapper inventories = new InventoryWrapper();

	public static void init() {
		if (Loader.isModLoaded("SonarCore")) {
			try {
				registry = (RegistryWrapper) Class.forName("sonar.core.SonarRegistry").newInstance();
				fluids = (FluidWrapper) Class.forName("sonar.core.helpers.FluidHelper").newInstance();
				inventories = (InventoryWrapper) Class.forName("sonar.core.helpers.InventoryHelper").newInstance();
			} catch (Exception exception) {
				System.err.println(NAME + " : FAILED TO INITILISE API" + exception.getMessage());
			}
		}
	}

	public static RegistryWrapper getRegistry() {
		return registry;
	}

	public static FluidWrapper getFluidHelper() {
		return fluids;
	}

	public static InventoryWrapper getItemHelper() {
		return inventories;
	}
}
