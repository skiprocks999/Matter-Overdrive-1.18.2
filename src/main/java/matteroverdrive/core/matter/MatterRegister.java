/*
Based off of example by Commable under MIT License 

See https://github.com/Commoble/databuddy/blob/1.18.x/src/main/java/commoble/databuddy/data/MergeableCodecDataManager.java
for full details

 */

package matteroverdrive.core.matter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.References;
import matteroverdrive.core.packet.type.PacketClientMatterValues;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

public class MatterRegister extends SimplePreparableReloadListener<Map<ResourceLocation, JsonObject>> {

	protected static final String JSON_EXTENSION = ".json";
	protected static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();
	
	private HashMap<Item, Integer> SERVER_VALUES = new HashMap<>();
	private HashMap<TagKey<Item>, Integer> parsedTags = new HashMap<>();
	private static final Gson GSON = new Gson();
	public static MatterRegister INSTANCE = null;
	
	private final String folderName;
	private final Logger logger;
	
	private HashMap<Item, Integer> CLIENT_VALUES = new HashMap<>();
	
	public MatterRegister() {
		folderName = "matter";
		logger = MatterOverdrive.LOGGER;
	}
	
	@Nullable
	//TODO move the tag loading to ServerStartedEvent if possible
	public Integer getServerMatterValue(Item item) {
		return SERVER_VALUES.get(item);
	}
	
	@Nullable
	public Integer getClientMatterValue(Item item) {
		return CLIENT_VALUES.get(item);
	}
	
	@Override
	protected Map<ResourceLocation, JsonObject> prepare(final ResourceManager resourceManager, final ProfilerFiller profiler) {
		final Map<ResourceLocation, List<JsonObject>> map = Maps.newHashMap();

		for (ResourceLocation resourceLocation : resourceManager.listResources(this.folderName, MatterRegister::isStringJsonFile)) {
			final String namespace = resourceLocation.getNamespace();
			final String filePath = resourceLocation.getPath();
			final String dataPath = filePath.substring(this.folderName.length() + 1, filePath.length() - JSON_EXTENSION_LENGTH);
			
			final ResourceLocation jsonIdentifier = new ResourceLocation(namespace, dataPath);
			final List<JsonObject> unmergedRaws = new ArrayList<>();

			try {
				for (Resource resource : resourceManager.getResources(resourceLocation)) {
					try (
						final InputStream inputStream = resource.getInputStream();
						final Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
					) {
						final JsonObject jsonElement = (JsonObject) GsonHelper.fromJson(GSON, reader, JsonElement.class);
						unmergedRaws.add(jsonElement);
					} catch(RuntimeException | IOException exception) {
						this.logger.error("Data loader for {} could not read data {} from file {} in data pack {}", this.folderName, jsonIdentifier, resourceLocation, resource.getSourceName(), exception); 
					} finally {
						IOUtils.closeQuietly(resource);
					}
				}
			} catch (IOException exception) {
				this.logger.error("Data loader for {} could not read data {} from file {}", this.folderName, jsonIdentifier, resourceLocation, exception);
			}
			map.put(jsonIdentifier, unmergedRaws);
		}
		
		JsonObject merged = new JsonObject();
		map.forEach((resource, list) -> {
			list.forEach(object -> {
				object.entrySet().forEach(h -> {
					merged.addProperty(h.getKey(), h.getValue().getAsInt());
				});
			});
		});
		Map<ResourceLocation, JsonObject> combined = new HashMap<>();
		combined.put(new ResourceLocation(References.ID, "combinedmattervals"), merged);
		
		return combined;
	}
	
	private static boolean isStringJsonFile(final String filename) {
		return filename.endsWith(JSON_EXTENSION);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonObject> object, ResourceManager manager, ProfilerFiller profiler) {
		SERVER_VALUES.clear();
		parsedTags.clear();
		object.forEach((location, element) -> {
			element.entrySet().forEach(h -> {
				String key = h.getKey();
				if(key.charAt(0) == '#') {
					key = key.substring(1);
					String[] split = key.split(":");
					parsedTags.put(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(split[0], split[1])), h.getValue().getAsInt());
				} else {
					Item item = ForgeRegistries.ITEMS.getHolder(new ResourceLocation(key)).get().value();
					if(!SERVER_VALUES.containsKey(item)) {
						SERVER_VALUES.put(item, h.getValue().getAsInt());
					}
				}
			});
		});
	}
	
	public void generateTagValues() {
		parsedTags.forEach((key, val) -> {
			Ingredient ing = Ingredient.of(key);
			for(ItemStack stack : ing.getItems()) {
				Item itm = stack.getItem();
				if(!SERVER_VALUES.containsKey(itm)) {
					SERVER_VALUES.put(itm, val);
				}
			}
		});
		parsedTags.clear();
	}
	
	public MatterRegister subscribeAsSyncable(final SimpleChannel channel) {
		MinecraftForge.EVENT_BUS.<OnDatapackSyncEvent>addListener(event -> {
			channel.send(PacketDistributor.ALL.noArg(), new PacketClientMatterValues(SERVER_VALUES));
		});
		return this;
	}
	
	public void setClientValues(HashMap<Item, Integer> valueMap) {
		this.CLIENT_VALUES = valueMap;
	}
	
}
