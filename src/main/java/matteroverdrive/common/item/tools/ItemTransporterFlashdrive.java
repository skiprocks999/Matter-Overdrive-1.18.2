package matteroverdrive.common.item.tools;

import java.util.List;

import matteroverdrive.References;
import matteroverdrive.common.item.utils.OverdriveItem;
import matteroverdrive.core.utils.UtilsNbt;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class ItemTransporterFlashdrive extends OverdriveItem {

	public ItemTransporterFlashdrive() {
		super(new Item.Properties().stacksTo(1).tab(References.MAIN));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if(!world.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);
			if(player.isShiftKeyDown()) {
				stack.getOrCreateTag().remove(UtilsNbt.BLOCK_POS);
			} else {
				HitResult trace = getPlayerPOVHitResult(world, player, net.minecraft.world.level.ClipContext.Fluid.ANY);
				if (trace.getType() != Type.MISS && trace.getType() != Type.ENTITY) {
					BlockHitResult blockTrace = (BlockHitResult) trace;
					BlockPos pos = blockTrace.getBlockPos();
					CompoundTag tag = stack.getOrCreateTag();
					tag.remove(UtilsNbt.BLOCK_POS);
					tag.put(UtilsNbt.BLOCK_POS, NbtUtils.writeBlockPos(pos));
					return InteractionResultHolder.success(player.getItemInHand(hand));
				}
			}
		}
		return super.use(world, player, hand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltips, TooltipFlag advanced) {
		super.appendHoverText(stack, world, tooltips, advanced);
		if(stack.hasTag() && stack.getTag().contains(UtilsNbt.BLOCK_POS)) {
			tooltips.add(new TextComponent(NbtUtils.readBlockPos(stack.getTag().getCompound(UtilsNbt.BLOCK_POS)).toShortString()).withStyle(ChatFormatting.GRAY));
		}
	}

}