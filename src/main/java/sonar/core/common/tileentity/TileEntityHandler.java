package sonar.core.common.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.network.sync.ISyncPart;

public abstract class TileEntityHandler extends TileEntitySonar implements ITileHandler {

	public void updateEntity() {
		super.updateEntity();
		this.getTileHandler().update(this);
		this.markDirty();
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		this.getTileHandler().readData(nbt, type);
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		this.getTileHandler().writeData(nbt, type);
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		this.getTileHandler().addSyncParts(parts);
	}
}
