package matteroverdrive.core.datagen.server;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.core.datagen.utils.AbstractLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;

public class LootTablesProvider extends AbstractLootTableProvider {

	public LootTablesProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void addTables() {
		for (Block block : DeferredRegisters.TRITANIUM_CRATES.<Block>getAllObjects()) {
			lootTables.put(block, itemOnlyTable(block.getRegistryName().getPath(), block,
					DeferredRegisters.TILE_TRITANIUM_CRATE.get()));
		}
		Block solarPanel = DeferredRegisters.BLOCK_SOLAR_PANEL.get();
		lootTables.put(solarPanel, itemAndEnergyTable(solarPanel.getRegistryName().getPath(), solarPanel,
				DeferredRegisters.TILE_SOLAR_PANEL.get()));
		Block matterDecomposer = DeferredRegisters.BLOCK_MATTER_DECOMPOSER.get();
		lootTables.put(matterDecomposer, itemEnergyMatterTable(matterDecomposer.getRegistryName().getPath(),
				matterDecomposer, DeferredRegisters.TILE_MATTER_DECOMPOSER.get()));
	}

}
