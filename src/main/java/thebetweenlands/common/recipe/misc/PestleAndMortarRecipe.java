package thebetweenlands.common.recipe.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;

public class PestleAndMortarRecipe implements IPestleAndMortarRecipe {
    private static final List<IPestleAndMortarRecipe> recipes = new ArrayList<IPestleAndMortarRecipe>();

    /**
     *
     * @param output
     *            what will be produced by the recipe
     * @param input
     *            the input item for the recipe
     */
    public static void addRecipe(ItemStack output, ItemStack input) {
        recipes.add(new PestleAndMortarRecipe(output, input));
    }
    
    public static void addRecipe(IPestleAndMortarRecipe recipe) {
        recipes.add(recipe);
    }

    public static void removeRecipe(IPestleAndMortarRecipe recipe) {
    	recipes.remove(recipe);
    }
    
    public static ItemStack getResult(ItemStack input) {
        for (IPestleAndMortarRecipe recipe : recipes) {
            if (recipe.matchesInput(input)) {
                return recipe.getOutput(input);
            }
        }
        return null;
    }

    /*public static ItemStack getInput(ItemStack output) {
        for (IPestleAndMortarRecipe recipe : recipes) {

        }
        return null;
    }*/

    public static List<IPestleAndMortarRecipe> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    private final ItemStack output;
    private final ItemStack input;

    public PestleAndMortarRecipe(ItemStack output, ItemStack input) {
        this.output = ItemStack.copyItemStack(output);
        this.input = ItemStack.copyItemStack(input);

        if (input instanceof ItemStack)
            input = ItemStack.copyItemStack((ItemStack) input);

        else
            throw new IllegalArgumentException("Input must be an ItemStack");
    }

    public ItemStack getInputs() {
        return ItemStack.copyItemStack(input);
    }

    public ItemStack getOutput() {
        return ItemStack.copyItemStack(output);
    }

    public boolean matches(ItemStack stacks) {
        if (stacks != null)
            if (areStacksTheSame(getInputs(), stacks)) {
                stacks = null;
                return true;
            }
        return false;
    }

    public boolean matchesOutput(ItemStack stacks) {
        if (stacks != null)
            if (areStacksTheSame(getOutput(), stacks)) {
                stacks = null;
                return true;
            }
        return false;
    }

    private boolean areStacksTheSame(ItemStack stack, ItemStack target) {
        return doesInputMatch(stack, target, false);
    }

    public static boolean doesInputMatch(ItemStack input, ItemStack toCheck, boolean matchSize) {
        if (input == null || toCheck == null)
            return false;

        if (input.getItem() == toCheck.getItem())
            if (input.getItemDamage() == OreDictionary.WILDCARD_VALUE || input.getItemDamage() == toCheck.getItemDamage())
                if (!matchSize || input.stackSize == toCheck.stackSize) {
                    if (input.hasTagCompound() && toCheck.hasTagCompound())
                        return input.getTagCompound().equals(toCheck.getTagCompound());
                    return input.hasTagCompound() == toCheck.hasTagCompound();
                }
        return false;
    }

	@Override
	public ItemStack getOutput(ItemStack input) {
		return this.output;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return doesInputMatch(this.input, stack, false);
	}
}
