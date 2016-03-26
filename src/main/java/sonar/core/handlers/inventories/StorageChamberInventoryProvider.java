package sonar.core.handlers.inventories;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.ActionType;
import sonar.core.api.InventoryHandler;
import sonar.core.api.StoredItemStack;

public class StorageChamberInventoryProvider extends InventoryHandler {

	public static String name = "Storage Chamber";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile instanceof TileEntityStorageChamber;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		if (!(slot < 14)) {
			return null;
		}
		if (tile instanceof TileEntityStorageChamber) {
			TileEntityStorageChamber chamber = (TileEntityStorageChamber) tile;
			if (chamber != null) {
				if (chamber.getSavedStack() != null) {
					ItemStack stack = chamber.getFullStack(slot);
					if (stack != null) {
						return new StoredItemStack(stack);

					}
				}
			}
		}
		return null;
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		if (tile instanceof TileEntityStorageChamber) {
			TileEntityStorageChamber chamber = (TileEntityStorageChamber) tile;
			if (chamber != null) {
				if (chamber.getSavedStack() != null) {
					long stored = 0;
					long maxStorage = 0;
					for (int i = 0; i < 14; i++) {
						ItemStack stack = chamber.getFullStack(i);
						if (stack != null) {
							stored += stack.stackSize;
							storedStacks.add(new StoredItemStack(stack));
						}
						maxStorage+=chamber.maxSize;
					}
					return new StorageSize(stored,maxStorage);
				}
			}
		}
		return StorageSize.EMPTY;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("Calculator");
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		TileEntityStorageChamber chamber = (TileEntityStorageChamber) tile;
		if (chamber.getSavedStack() != null) {
			if (chamber.getCircuitType(add.getItemStack()) == chamber.getCircuitType(chamber.getSavedStack())) {
				int stored = chamber.getStored()[add.getItemDamage()];
				if (stored == chamber.maxSize) {
					return add;
				}
				if (stored + add.getStackSize() <= chamber.maxSize) {
					if (!action.shouldSimulate())
						chamber.increaseStored(add.getItemDamage(), (int) add.getStackSize());
					return null;
				} else {
					if (!action.shouldSimulate())
						chamber.setStored(add.getItemDamage(), chamber.maxSize);
					add.stored -= chamber.maxSize - stored;
					return add;
				}
			}
		} else if (chamber.getCircuitType(add.getItemStack()) != null) {

			if (!action.shouldSimulate())
				chamber.setSavedStack(add.getItemStack().copy());

			if (add.getStackSize() <= chamber.maxSize) {

				if (!action.shouldSimulate())
					chamber.stored[add.getItemDamage()] += add.getStackSize();
				return null;
			} else {
				if (!action.shouldSimulate())
					chamber.stored[add.getItemDamage()] = chamber.maxSize;
				add.stored -= chamber.maxSize;
				return add;
			}
		}

		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		TileEntityStorageChamber chamber = (TileEntityStorageChamber) tile;
		if (chamber.getSavedStack() != null) {
			if (chamber.getCircuitType(remove.getItemStack()) == chamber.getCircuitType(chamber.getSavedStack())) {
				int stored = chamber.stored[remove.getItemDamage()];
				if (stored != 0) {
					if (stored <= remove.getStackSize()) {
						ItemStack stack = chamber.getFullStack(remove.getItemDamage());
						if (!action.shouldSimulate()) {
							chamber.stored[remove.getItemDamage()] = 0;
							chamber.resetSavedStack(remove.getItemDamage());
						}
						remove.stored -= stack.stackSize;

					} else {
						ItemStack stack = chamber.getSlotStack(remove.getItemDamage(), (int) remove.getStackSize());
						if (!action.shouldSimulate())
							chamber.stored[remove.getItemDamage()] -= remove.getStackSize();

						return null;
					}
				}
			}
		}

		return remove;
	}

}
