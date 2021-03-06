package matteroverdrive.core.datagen.client;

import matteroverdrive.DeferredRegisters;
import matteroverdrive.References;
import matteroverdrive.common.block.type.BlockColors;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class OverdriveBlockStateProvider extends BlockStateProvider {

	private ExistingModelFile tritaniumPlatingFile;
	private ExistingModelFile coloredTritaniumPlatingFile;
	private ExistingModelFile floorTileFile;
	private ExistingModelFile floorTilesFile;

	public OverdriveBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, References.ID, exFileHelper);
		tritaniumPlatingFile = new ExistingModelFile(new ResourceLocation(References.ID + ":block/tritanium_plating"),
				exFileHelper);
		coloredTritaniumPlatingFile = new ExistingModelFile(
				new ResourceLocation(References.ID + ":block/tritanium_plating_colorless"), exFileHelper);
		floorTileFile = new ExistingModelFile(new ResourceLocation(References.ID + ":block/floor_tile_colorless"),
				exFileHelper);
		floorTilesFile = new ExistingModelFile(new ResourceLocation(References.ID + ":block/floor_tiles_colorless"),
				exFileHelper);

	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(DeferredRegisters.BLOCK_REGULAR_TRITANIUM_PLATING.get(), tritaniumPlatingFile);
		for (BlockColors color : BlockColors.values()) {
			simpleBlock(DeferredRegisters.BLOCK_COLORED_TRITANIUM_PLATING.get(color).get(),
					coloredTritaniumPlatingFile);
			simpleBlock(DeferredRegisters.BLOCK_FLOOR_TILE.get(color).get(), floorTileFile);
			simpleBlock(DeferredRegisters.BLOCK_FLOOR_TILES.get(color).get(), floorTilesFile);
		}
		simpleBlock(DeferredRegisters.BLOCK_SOLAR_PANEL.get());
		simpleBlock(DeferredRegisters.BLOCK_CHARGER_CHILD.get());

		simpleBlock(DeferredRegisters.BLOCK_INDUSTRIAL_GLASS.get());
		simpleBlock(DeferredRegisters.BLOCK_VENT_OPEN.get());
		simpleBlock(DeferredRegisters.BLOCK_VENT_CLOSED.get());
	}

}
