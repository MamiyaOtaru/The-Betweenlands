package thebetweenlands.common.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMortar;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockMortar extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockMortar() {
        super(Material.ROCK);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(BLCreativeTabs.HERBLORE);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
            return true;
        if (worldIn.getTileEntity(pos) instanceof TileEntityMortar) {
            TileEntityMortar tile = (TileEntityMortar) worldIn.getTileEntity(pos);

            if (playerIn.getHeldItem(hand) == null && !playerIn.isSneaking()) {
                tile.manualGrinding = true;
                worldIn.notifyBlockUpdate(pos, state, state, 3);
                return true;
            }

            if (playerIn.getHeldItem(hand) != null) {
                if (playerIn.getHeldItem(hand).getItem() == ItemRegistry.PESTLE) {
                    if (tile.getStackInSlot(1) == null) {
                        tile.setInventorySlotContents(1, playerIn.getHeldItem(hand));
                        tile.hasPestle = true;
                        playerIn.setHeldItem(hand, null);
                    }
                    return true;
                }
                if (playerIn.getHeldItem(hand).getItem() == ItemRegistry.LIFE_CRYSTAL) {
                    if (tile.getStackInSlot(3) == null) {
                        tile.setInventorySlotContents(3, playerIn.getHeldItem(hand));
                        playerIn.setHeldItem(hand, null);
                    }
                    return true;
                }
            }

            if (tile != null && playerIn.isSneaking())
                playerIn.openGui(TheBetweenlands.INSTANCE, CommonProxy.GUI_PESTLE_AND_MORTAR, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        IInventory tile = (IInventory) world.getTileEntity(pos);
        if (tile != null)
            for (int i = 0; i < tile.getSizeInventory(); i++) {
                ItemStack stack = tile.getStackInSlot(i);
                if (stack != null) {
                    if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops")) {
                        float f = 0.7F;
                        double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
                        entityitem.setPickupDelay(10);
                        world.spawnEntityInWorld(entityitem);
                    }
                }
            }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
        TileEntityMortar tile = (TileEntityMortar) world.getTileEntity(pos);
        if (tile.progress > 0 && rand.nextInt(3) == 0) {
            float f = pos.getX() + 0.5F;
            float f1 = pos.getY() + 1.1F + rand.nextFloat() * 6.0F / 16.0F;
            float f2 = pos.getZ() + 0.5F;
            world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, f, f1, f2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityMortar();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }
}