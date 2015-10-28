package thebetweenlands.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.utils.CorrodibleItemHelper;

class PurifierRecipeCorrodible extends PurifierRecipe {
	@Override
	public boolean matches(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ICorrodible && CorrodibleItemHelper.getCorrosion(stack) > 0;
	}

	@Override
	public ItemStack getOutput(ItemStack input) {
		ItemStack output = ItemStack.copyItemStack(input);
		output.stackSize = 1;
		output.setTagInfo("Decay", new NBTTagInt(0));
		return output;
	}
}