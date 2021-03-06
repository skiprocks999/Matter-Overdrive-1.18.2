package matteroverdrive.core.command;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;

import matteroverdrive.References;
import matteroverdrive.core.matter.MatterRegister;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandGenerateMatterValues {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

		dispatcher.register(Commands.literal(References.ID).requires(source -> source.hasPermission(2))
				.then(Commands.literal("genmatterfile").executes(source -> generateMatterFile(source.getSource()))));

	}

	private static int generateMatterFile(CommandSourceStack source) {

		source.sendSuccess(new TranslatableComponent("command.matteroverdrive.startmattercalc"), true);

		RecipeManager manager = source.getRecipeManager();
		HashMap<Item, Double> generatedValues = new HashMap<>();

		List<BiConsumer<HashMap<Item, Double>, RecipeManager>> consumers = MatterRegister.getConsumers();
		for (int i = 0; i < 300; i++) {
			for (BiConsumer<HashMap<Item, Double>, RecipeManager> consumer : consumers) {
				consumer.accept(generatedValues, manager);
			}
			/*
			 * BrewingRecipeRegistry.getRecipes().forEach(recipe -> { if(recipe instanceof
			 * BrewingRecipe brewing) { ItemStack result = brewing.getOutput(); if
			 * (MatterRegister.INSTANCE.getServerMatterValue(result.getItem()) == null) {
			 * List<Ingredient> ings = new ArrayList<>(); ings.add(brewing.getIngredient());
			 * ings.add(brewing.getInput()); int sum = 0; boolean failed = false; for
			 * (Ingredient ing : ings) { for (ItemStack stack : ing.getItems()) { Integer
			 * value = MatterRegister.INSTANCE.getServerMatterValue(stack.getItem()); if
			 * (value == null) { value = generatedValues.get(stack.getItem()); } if (value
			 * != null && !generatedValues.containsKey(result.getItem())) { sum += value *
			 * stack.getCount(); failed = false; break; } failed = true; } } if (!failed) {
			 * int matterValue = (int) Math.ceil((double) sum / (double) result.getCount() /
			 * 3.0); generatedValues.put(result.getItem(), matterValue); } } } });
			 */
		}

		// now we sort them alphabetically
		List<Pair<String, Double>> sorted = new ArrayList<>();
		List<String> names = new ArrayList<>();
		generatedValues.keySet().forEach(item -> {
			names.add(item.getRegistryName().toString());
		});
		Collections.sort(names);
		names.forEach(string -> {
			String[] split = string.split(":");
			sorted.add(Pair.of(string, generatedValues
					.get(ForgeRegistries.ITEMS.getHolder(new ResourceLocation(split[0], split[1])).get().value())));
		});

		JsonObject json = new JsonObject();

		sorted.forEach(entry -> {
			if (entry.getSecond() > 0) {
				json.addProperty(entry.getFirst(), entry.getSecond());
			}
		});

		Path path = Paths.get("Matter Overdrive/generated.json");
		try {
			String s = GSON.toJson(json);

			if (!Files.exists(path.getParent())) {
				Files.createDirectories(path.getParent());
			}

			BufferedWriter bufferedwriter = Files.newBufferedWriter(path);

			try {
				bufferedwriter.write(s);
			} catch (Throwable throwable1) {
				if (bufferedwriter != null) {
					try {
						bufferedwriter.close();
					} catch (Throwable throwable) {
						throwable1.addSuppressed(throwable);
					}
				}

				throw throwable1;
			}

			if (bufferedwriter != null) {
				bufferedwriter.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		source.sendSuccess(new TranslatableComponent("command.matteroverdrive.endmattercalc"), true);
		return 1;
	}

}
