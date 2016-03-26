package sonar.core.handlers.inventories;

import java.util.ArrayList;
import java.util.List;

import logisticspipes.api.ILPPipe;
import logisticspipes.api.ILPPipeTile;
import logisticspipes.api.IRequestAPI;
import logisticspipes.api.IRequestAPI.SimulationResult;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.ActionType;
import sonar.core.api.InventoryHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;
import cpw.mods.fml.common.Loader;

public class LPInventoryProvider extends InventoryHandler {

	public static String name = "LP-Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, ForgeDirection dir) {
		return tile instanceof ILPPipeTile;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, ForgeDirection dir) {
		List<ItemStack> items = getStackList(tile);
		if (slot < items.size()) {
			return new StoredItemStack(items.get(slot));
		}
		return null;
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, ForgeDirection dir) {
		List<ItemStack> items = getStackList(tile);
		if (items == null || items.isEmpty()) {
			return StorageSize.EMPTY;
		}
		long maxStorage = 0;
		for (ItemStack stack : items) {
			SonarAPI.getItemHelper().addStackToList(storedStacks, new StoredItemStack(stack));
			maxStorage +=stack.stackSize;
		}
		return new StorageSize(maxStorage,maxStorage);
	}

	public List<ItemStack> getStackList(TileEntity tile) {
		if (tile instanceof ILPPipeTile) {
			ILPPipe pipe = ((ILPPipeTile) tile).getLPPipe();
			if (pipe instanceof IRequestAPI) {
				IRequestAPI request = (IRequestAPI) pipe;
				List<ItemStack> items = request.getProvidedItems();
				return items;
			}
		}
		return new ArrayList();
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("LogisticsPipes");
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, ForgeDirection dir, ActionType action) {
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof ILPPipeTile) {
			ILPPipe pipe = ((ILPPipeTile) tile).getLPPipe();
			if (pipe instanceof IRequestAPI) {
				IRequestAPI request = (IRequestAPI) pipe;
				if (!action.shouldSimulate()) {
					List<ItemStack> missing = request.performRequest(remove.getFullStack());
					if (missing == null) {
						return null;
					}
					long removed = remove.stored;
					for (ItemStack stack : missing) {
						removed -= stack.stackSize;
					}
					remove.setStackSize(removed);
				} else {
					SimulationResult result = request.simulateRequest(remove.getFullStack());
					if (result.missing == null) {
						return null;
					}
					long removed = remove.stored;
					for (ItemStack stack : result.missing) {
						removed -= stack.stackSize;
					}
					remove.setStackSize(removed);
				}
			}
		}
		return remove;
	}
}
