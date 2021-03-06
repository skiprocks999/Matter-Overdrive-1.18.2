package matteroverdrive.core.datagen;

import matteroverdrive.References;
import matteroverdrive.core.datagen.client.OverdriveBlockModelsProvider;
import matteroverdrive.core.datagen.client.OverdriveBlockStateProvider;
import matteroverdrive.core.datagen.client.OverdriveLangKeyProvider;
import matteroverdrive.core.datagen.client.OverdriveItemModelsProvider;
import matteroverdrive.core.datagen.server.LootTablesProvider;
import matteroverdrive.core.datagen.server.MatterValueGenerator;
import matteroverdrive.core.datagen.server.MinableTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {

		DataGenerator generator = event.getGenerator();

		if (event.includeServer()) {
			generator.addProvider(new MinableTagsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(new LootTablesProvider(generator));
			generator.addProvider(new MatterValueGenerator(generator));
		}
		if (event.includeClient()) {
			generator.addProvider(new OverdriveBlockStateProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(new OverdriveBlockModelsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(new OverdriveItemModelsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(new OverdriveLangKeyProvider(generator, "en_us"));
		}
	}

}
