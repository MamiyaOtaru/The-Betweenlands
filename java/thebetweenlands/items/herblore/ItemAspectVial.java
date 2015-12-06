package thebetweenlands.items.herblore;

import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.herblore.aspects.IAspect;
import thebetweenlands.herblore.aspects.ItemAspect;
import thebetweenlands.utils.AtlasIcon;

public class ItemAspectVial extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon iconLiquid, iconVialOrange;

	@SideOnly(Side.CLIENT)
	private IIcon[] aspectIcons;

	public ItemAspectVial() {
		this.setUnlocalizedName("item.thebetweenlands.aspectVial");

		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);

		this.setTextureName("thebetweenlands:strictlyHerblore/misc/vialGreen");
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		List<ItemAspect> itemAspects = AspectRecipes.REGISTRY.getItemAspects(stack);
		if(itemAspects.size() >= 1) {
			ItemAspect aspect = itemAspects.get(0);
			return super.getItemStackDisplayName(stack) + " - " + aspect.aspect.getName() + " (" + aspect.amount + ")";
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		this.iconLiquid = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/vialLiquid");
		this.iconVialOrange = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/vialOrange");
		IIcon aspectIconAtlas = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/aspectMap");
		this.aspectIcons = new IIcon[AspectRegistry.ASPECT_TYPES.size()];
		for(IAspect aspect : AspectRegistry.ASPECT_TYPES) {
			this.aspectIcons[aspect.getIconIndex()] = new AtlasIcon(aspectIconAtlas, aspect.getIconIndex(), 4);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		switch(pass){
		case 0:
			return 0xFF80EEFF;
		case 2:
			return 0xFFFFFFFF;
		}
		return 0xFFFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
		return pass == 0 ? this.iconLiquid : super.getIconFromDamageForRenderPass(damage, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return damage % 2 == 0 ? this.itemIcon : this.iconVialOrange;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass) {
		if(pass == 2) {
			List<ItemAspect> itemAspects = AspectRecipes.REGISTRY.getItemAspects(stack);
			if(itemAspects.size() >= 1) {
				ItemAspect aspect = itemAspects.get(0);
				return this.aspectIcons[aspect.aspect.getIconIndex()];
			}
		}
		return super.getIcon(stack, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0)); //green
		list.add(new ItemStack(item, 1, 1)); //orange

		//Add all aspects
		for(IAspect aspect : AspectRegistry.ASPECT_TYPES) {
			ItemAspect itemAspect = new ItemAspect(aspect, 4.0F);
			ItemStack stackGreen = new ItemStack(item, 1, 0);
			AspectRecipes.REGISTRY.addItemAspects(stackGreen, itemAspect);
			list.add(stackGreen);
			ItemStack stackOrange = new ItemStack(item, 1, 1);
			AspectRecipes.REGISTRY.addItemAspects(stackOrange, itemAspect);
			list.add(stackOrange);
		}
	}


	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderPasses(int metadata) {
		return 3;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			switch(stack.getItemDamage()) {
			case 0:
				return "item.thebetweenlands.aspectVial.green";
			case 1:
				return "item.thebetweenlands.aspectVial.orange";
			}
		} catch (Exception e) { }
		return "item.thebetweenlands.unknown";
	}
}
