package matteroverdrive.core.packet.type;

import java.util.HashMap;
import java.util.function.Supplier;

import matteroverdrive.MatterOverdrive;
import matteroverdrive.core.matter.MatterRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class PacketClientMatterValues {

	private final HashMap<Item, Integer> values;
	
	public PacketClientMatterValues(HashMap<Item, Integer> values) {
		this.values = values;
	}
	
	public static void handle(PacketClientMatterValues message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
				MatterRegister.INSTANCE.setClientValues(message.values);
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketClientMatterValues pkt, FriendlyByteBuf buf) {
		pkt.values.forEach((item, val) -> {
			buf.writeItem(new ItemStack(item));
			buf.writeInt(val);
		});
	}

	public static PacketClientMatterValues decode(FriendlyByteBuf buf) {
		HashMap<Item, Integer> vals = new HashMap<>();
		ItemStack stack = buf.readItem();
		MatterOverdrive.LOGGER.info("decoding");
		while(!stack.isEmpty()) {
			if(!stack.isEmpty()) {
				vals.put(stack.getItem(), buf.readInt());
				MatterOverdrive.LOGGER.info(stack.toString());
			}
			stack = buf.readItem();
		}
		return new PacketClientMatterValues(vals);
	}
	
}
