package net.tst5000.agricrafttfctngintegration;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.MathHelper;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.misc.IAgriDisplayable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.dries007.tfc.objects.te.TECropBase;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import static com.infinityraider.agricraft.tiles.TileEntityCrop.canOvertake;

public class TileEntityCropTFC extends TileEntityBase implements IAgriCrop, IDebuggable, IAgriDisplayable {
    private AgriSeed seed;
    private int growthStage;
    private boolean crossCrop = false;

    public MethodResult onGrowthTick() { // TODO: modify behavior to match TFC
        if (this.isRemote()) {
            return MethodResult.PASS;
        }

        TECropBase te = Helpers.getTE(world, pos, TECropBase.class);

        if (this.isCrossCrop() && (double) AgriCraftConfig.crossOverChance > this.getRandom().nextDouble()) {
            this.crossOver();
            return MethodResult.SUCCESS;
        } else if (!this.hasSeed()) {
            this.spawn();
            return MethodResult.SUCCESS;
        } else if (this.isMature()) {
            this.spread();
            return MethodResult.SUCCESS;
        } else if (te != null) {
            while((float)te.getTicksSinceUpdate() > this.seed.getPlant().) {

            }
            this.applyGrowthTick();
            return MethodResult.SUCCESS;
        } else {
            return MethodResult.FAIL;
        }
    }

    public void crossOver() {
        AgriApi.getMutationEngine().attemptCross(this, this.getWorld().rand);
    }

    public boolean spawn() {
        if (this.isRemote()) {
            return false;
        } else if (this.hasSeed()) {
            return false;
        } else {
            Iterator var1 = AgriApi.getPlantRegistry().all().iterator();

            IAgriPlant p;
            do {
                if (!var1.hasNext()) {
                    return false;
                }

                p = (IAgriPlant)var1.next();
            } while(p.getSpawnChance() <= this.getRandom().nextDouble() || !this.isFertile(p));

            this.setCrossCrop(false);
            this.setSeed(new AgriSeed(p, new PlantStats()));
            return true;
        }
    }

    public boolean spread() {
        if (this.isRemote()) {
            return false;
        } else if (this.seed == null) {
            return false;
        } else {
            IAgriPlant plant = this.seed.getPlant();
            Iterator var2 = WorldHelper.getTileNeighbors(this.getWorld(), this.pos, IAgriCrop.class).iterator();

            while(var2.hasNext()) {
                IAgriCrop crop = (IAgriCrop)var2.next();
                if (plant.getSpreadChance() > this.getRandom().nextDouble() && plant.getGrowthRequirement().hasValidSoil(this.getWorld(), crop.getCropPos())) {
                    AgriSeed other = crop.getSeed();
                    if (other == null) {
                        if (!crop.isCrossCrop()) {
                            crop.setSeed(this.seed);
                            return true;
                        }
                    } else if (canOvertake(this.seed, other, this.getRandom())) {
                        crop.setCrossCrop(false);
                        crop.setSeed(this.seed);
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public void applyGrowthTick() {
        if (!this.isRemote()) {
            if (this.hasSeed()) {
                if (!this.isMature()) {
                    if (this.isFertile()) {
                        this.setGrowthStage(this.growthStage + 1);
                    }
                }
            }
        }
    }

    public BlockPos getCropPos() {
        return this.getPos();
    }

    public World getCropWorld() {
        return this.getWorld();
    }

    public int getGrowthStage() {
        return this.growthStage;
    }

    public boolean setGrowthStage(int stage) {
        if (this.isRemote()) {
            return false;
        } else {
            if (this.hasSeed()) {
                stage = MathHelper.inRange(stage, 0, this.seed.getPlant().getGrowthStages() - 1);
            } else if (stage != 0) {
                AgriCore.getCoreLogger().warn("Can't set a non-zero growth stage when a crop has no seed!", new Object[0]);
                stage = 0;
            }

            if (stage != this.growthStage) {
                this.growthStage = stage;
                this.markForUpdate();
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isCrossCrop() {
        return crossCrop;
    }

    public boolean setCrossCrop(boolean status) {
        if (this.isRemote()) {
            return false;
        } else if (this.hasSeed()) {
            return false;
        } else if (this.crossCrop == status) {
            return false;
        } else {
            this.crossCrop = status;
            SoundType type = Blocks.PLANKS.getSoundType((IBlockState)null, (World)null, (BlockPos)null, (Entity)null);
            this.getWorld().playSound((EntityPlayer)null, (double)((float)this.xCoord() + 0.5F), (double)((float)this.yCoord() + 0.5F), (double)((float)this.zCoord() + 0.5F), type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
            this.markForUpdate();
            return true;
        }
    }

    public boolean isFertile(@Nullable IAgriPlant plant) { // TODO: modify getGrowthRequirement
        return plant != null && this.getWorld().isAirBlock(this.pos.up()) && plant.getGrowthRequirement().isMet(this.getWorld(), this.pos);
    }

    public boolean isMature() {
        return this.hasSeed() && this.getGrowthStage() + 1 >= this.getSeed().getPlant().getGrowthStages();
    }

    @Nonnull
    public Optional<IAgriSoil> getSoil() {
        IBlockState state = this.getWorld().getBlockState(this.pos.down());
        return AgriApi.getSoilRegistry().get(state);
    }

    @Nonnull
    public MethodResult onApplyCrops(@Nullable EntityPlayer player) {
        if (this.isRemote()) {
            return MethodResult.PASS;
        } else if (!this.isCrossCrop() && !this.hasSeed()) {
            boolean wasSet = this.setCrossCrop(true);
            return wasSet ? MethodResult.SUCCESS : MethodResult.FAIL;
        } else {
            return MethodResult.FAIL;
        }
    }

    @Nonnull
    public MethodResult onApplySeeds(@Nonnull AgriSeed seed, @Nullable EntityPlayer player) {
        Preconditions.checkNotNull(seed, "`7Cannot apply a null seed!`r");
        if (this.isRemote()) {
            return MethodResult.PASS;
        } else if (!this.isCrossCrop() && !this.hasSeed()) {
            if (!seed.getPlant().getGrowthRequirement().hasValidSoil(this.getWorld(), this.pos)) {
                MessageUtil.messagePlayer(player, "`7The soil is not valid for this seed. You can't plant it here.`r", new Object[0]);
                return MethodResult.FAIL;
            } else {
                if (!seed.getPlant().getGrowthRequirement().hasValidConditions(this.getWorld(), this.pos)) {
                    MessageUtil.messagePlayer(player, "`7Caution: This plant has additional requirements that are unmet.`r", new Object[0]);
                }

                if (!seed.getPlant().getGrowthRequirement().hasValidLight(this.getWorld(), this.pos)) {
                    MessageUtil.messagePlayer(player, "`7Caution: This plant won't grow with the current light level.`r", new Object[0]);
                }

                this.setSeed(seed);
                return MethodResult.SUCCESS;
            }
        } else {
            return MethodResult.FAIL;
        }
    }

    public void getDrops(@Nonnull Consumer<ItemStack> consumer, boolean includeCropSticks, boolean includeSeeds, boolean includeProducts) {
        Preconditions.checkNotNull(consumer);
        if (includeCropSticks) {
            consumer.accept(new ItemStack(AgriItems.getInstance().CROPS, this.isCrossCrop() ? 2 : 1));
        }

        if (this.hasSeed()) {
            if (includeSeeds && this.seed.getPlant().getSeedDropChanceBase() + (double)this.growthStage * this.seed.getPlant().getSeedDropChanceBonus() > this.getRandom().nextDouble()) {
                consumer.accept(this.getSeed().toStack());
            }

            if (includeProducts && this.isMature()) {
                for(int trials = (this.seed.getStat().getGain() + 3) / 3; trials > 0; --trials) {
                    this.seed.getPlant().getHarvestProducts(consumer, this, this.seed.getStat(), this.getRandom());
                }
            }
        }

    }

    @Nonnull
    public MethodResult onBroken(@Nonnull Consumer<ItemStack> consumer, @Nullable EntityPlayer player) {
        Preconditions.checkNotNull(consumer);
        if (this.isRemote()) {
            return MethodResult.PASS;
        } else {
            if (player == null || !player.isCreative()) {
                this.getDrops(consumer, true, true, true);
            }

            this.getWorld().removeTileEntity(this.pos);
            this.getWorld().setBlockToAir(this.pos);
            return MethodResult.SUCCESS;
        }
    }

    public void readTileNBT(@Nonnull NBTTagCompound tag) {
        Preconditions.checkNotNull(tag);
        IAgriStat stat = (IAgriStat)AgriApi.getStatRegistry().valueOf(tag).orElse(null);
        IAgriPlant plant = (IAgriPlant)AgriApi.getPlantRegistry().get(tag.getString("agri_seed")).orElse(null);
        if (stat != null && plant != null) {
            this.seed = new AgriSeed(plant, stat);
        } else {
            this.seed = null;
        }

        this.growthStage = tag.getInteger("agri_meta");
        this.crossCrop = tag.getBoolean("agri_cross_crop");
    }

    public void writeTileNBT(@Nonnull NBTTagCompound tag) {
        Preconditions.checkNotNull(tag);
        tag.setBoolean("agri_cross_crop", this.crossCrop);
        tag.setInteger("agri_meta", this.growthStage);
        if (this.hasSeed()) {
            this.getSeed().getStat().writeToNBT(tag);
            tag.setString("agri_seed", this.getSeed().getPlant().getId());
        }
    }

    public boolean hasSeed() {
        return this.seed != null;
    }

    public AgriSeed getSeed() {
        return this.seed;
    }

    public boolean acceptsSeed(@Nullable AgriSeed seed) {
        return !this.crossCrop && (this.seed == null || seed == null);
    }

    public boolean setSeed(@Nullable AgriSeed seed) {
        if (Objects.equal(this.seed, seed)) {
            return false;
        } else {
            this.seed = seed;
            this.growthStage = 0;
            this.markForUpdate();
            return true;
        }
    }

    public boolean acceptsFertilizer(@Nullable IAgriFertilizer fertilizer) {
        if (fertilizer == null) {
            return false;
        } else if (!this.crossCrop) {
            return !this.hasSeed() ? true : this.getSeed().getPlant().isFertilizable();
        } else {
            return AgriCraftConfig.fertilizerMutation && fertilizer.canTriggerMutation();
        }
    }

    @Nonnull
    public MethodResult onApplyFertilizer(@Nullable IAgriFertilizer fertilizer, @Nonnull Random rand) {
        Preconditions.checkNotNull(rand);
        if (this.isRemote()) {
            return MethodResult.PASS;
        } else if (!this.acceptsFertilizer(fertilizer)) {
            AgriCore.getCoreLogger().warn("onApplyFertilizer should not be called if acceptFertilizer is false!", new Object[0]);
            return MethodResult.FAIL;
        } else {
            this.onGrowthTick();
            return MethodResult.SUCCESS;
        }
    }

    @Nonnull
    public MethodResult onHarvest(@Nonnull Consumer<ItemStack> consumer, @Nullable EntityPlayer player) { // TODO: modify code to fit TFC behavior (skills)
        Preconditions.checkNotNull(consumer);
        if (this.isRemote()) {
            return MethodResult.PASS;
        } else if (this.isCrossCrop()) {
            if (this.setCrossCrop(false)) {
                consumer.accept(new ItemStack(AgriItems.getInstance().CROPS, 1));
            }

            return MethodResult.SUCCESS;
        } else if (this.canBeHarvested()) {
            this.getDrops(consumer, false, false, true);
            this.setGrowthStage(0);
            return MethodResult.SUCCESS;
        } else {
            return MethodResult.PASS;
        }
    }

    public boolean onRaked(@Nonnull Consumer<ItemStack> consumer, @Nullable EntityPlayer player) {
        Preconditions.checkNotNull(consumer);
        if (!this.isRemote() && this.canBeRaked()) {
            this.getDrops(consumer, false, AgriCraftConfig.enableRakingSeedDrops, AgriCraftConfig.enableRakingItemDrops);
            this.setSeed((AgriSeed)null);
            return true;
        } else {
            return false;
        }
    }

    public void addServerDebugInfo(@Nonnull Consumer<String> consumer) {
        Preconditions.checkNotNull(consumer);
        consumer.accept("CROP:");
        if (this.crossCrop) {
            consumer.accept(" - This is a crosscrop");
        } else if (this.hasSeed()) {
            IAgriPlant plant = this.getSeed().getPlant();
            IAgriStat stat = this.getSeed().getStat();
            if (plant.isWeed()) {
                consumer.accept(" - This crop has weeds");
            } else {
                consumer.accept(" - This crop has a plant");
            }

            consumer.accept(" - Plant: " + plant.getPlantName());
            consumer.accept(" - Id: " + plant.getId());
            consumer.accept(" - Stage: " + this.getGrowthStage());
            consumer.accept(" - Stages: " + plant.getGrowthStages());
            consumer.accept(" - Meta: " + this.getGrowthStage());
            consumer.accept(" - Growth: " + stat.getGrowth());
            consumer.accept(" - Gain: " + stat.getGain());
            consumer.accept(" - Strength: " + stat.getStrength());
            consumer.accept(" - Fertile: " + this.isFertile());
            consumer.accept(" - Mature: " + this.isMature());
            consumer.accept(" - AgriSoil: " + (String)plant.getGrowthRequirement().getSoils().stream().findFirst().map(IAgriSoil::getId).orElse("None"));
        } else {
            consumer.accept(" - This crop has no plant");
        }
    }

    public void addDisplayInfo(@Nonnull Consumer<String> information) {
        Preconditions.checkNotNull(information);
        information.accept("Soil: " + (String)this.getSoil().map(IAgriSoil::getName).orElse("Unknown"));
        if (this.hasSeed()) {
            IAgriPlant plant = this.getSeed().getPlant();
            IAgriStat stat = this.getSeed().getStat();
            information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.seed") + ": " + plant.getSeedName());
            if (this.isMature()) {
                information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.growth") + ": " + AgriCore.getTranslator().translate("agricraft_tooltip.mature"));
            } else {
                information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.growth") + ": " + (int)(100.0D * (double)(this.getGrowthStage() + 1) / (double)plant.getGrowthStages()) + "%");
            }

            if (stat.isAnalyzed()) {
                stat.addStats(information);
            } else {
                information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.analyzed"));
            }

            information.accept(AgriCore.getTranslator().translate(this.isFertile() ? "agricraft_tooltip.fertile" : "agricraft_tooltip.notFertile"));
        } else {
            information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.empty"));
        }
    }

}
