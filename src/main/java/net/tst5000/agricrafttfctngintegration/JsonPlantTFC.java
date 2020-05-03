package net.tst5000.agricrafttfctngintegration;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.render.RenderMethod;
import com.infinityraider.agricraft.api.v1.requirement.BlockCondition;
import com.infinityraider.agricraft.api.v1.requirement.IGrowthReqBuilder;
import com.infinityraider.agricraft.api.v1.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.util.BlockRange;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import com.infinityraider.agricraft.core.JsonSoil;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthReqBuilder;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.renderers.PlantRenderer;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class JsonPlantTFC implements IAgriPlant {
    private final AgriPlantTFC plant;
    private final List<FuzzyStack> seedItems;
    private final IGrowthRequirement growthRequirement;

    public JsonPlantTFC(AgriPlantTFC plant) {
        this.plant = Objects.requireNonNull(plant, "A JSONPlant may not consist of a null AgriPlant! Why would you even try that!?");
        this.seedItems = initSeedItemsListJSON(plant);
        this.growthRequirement = initGrowthRequirementJSON(plant);
    }

    public boolean isWeed() {
        return this.plant.isWeedable();
    }

    public boolean isAggressive() {
        return this.plant.isAgressive();
    }

    public boolean isFertilizable() {
        return this.plant.canBonemeal();
    }

    public String getId() {
        return this.plant.getId();
    }

    public String getPlantName() {
        return this.plant.getPlantName();
    }

    public String getSeedName() {
        return this.plant.getSeedName();
    }

    public Collection<FuzzyStack> getSeedItems() {
        return this.seedItems;
    }

    public final ItemStack getSeed() {
        ItemStack stack = (ItemStack)this.getSeedItems().stream().map((s) -> {
            return s.toStack();
        }).findFirst().orElse(new ItemStack(AgriItems.getInstance().AGRI_SEED));
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("agri_seed", this.getId());
        (new PlantStats()).writeToNBT(tag);
        stack.setTagCompound(tag);
        return stack;
    }

    public String getInformation() {
        return this.plant.getDescription().toString();
    }

    public double getSpreadChance() {
        return this.plant.getSpreadChance();
    }

    public double getSpawnChance() {
        return this.plant.getSpawnChance();
    }

    public double getGrassDropChance() {
        return this.plant.getGrassDropChance();
    }

    public float getGrowthTimeBase() {
        return this.plant.getGrowthTime();
    }

    public double getGrowthChanceBase() {
        return this.plant.getGrowthChance();
    }

    public double getGrowthChanceBonus() {
        return this.plant.getGrowthBonus();
    }

    public double getSeedDropChanceBase() {
        return this.plant.getSeedDropChance();
    }

    public double getSeedDropChanceBonus() {
        return this.plant.getSeedDropBonus();
    }

    public int getGrowthStages() {
        return this.plant.getGrowthStages();
    }

    public void getPossibleProducts(Consumer<ItemStack> products) {
        this.plant.getProducts().getAll().stream().map((p) -> {
            return p.toStack(FuzzyStack.class);
        }).filter(Optional::isPresent).map((p) -> {
            return ((FuzzyStack)p.get()).toStack();
        }).forEach(products);
    }

    public void getHarvestProducts(Consumer<ItemStack> products, IAgriCrop crop, IAgriStat stat, Random rand) {
        this.plant.getProducts().getRandom(rand).stream().map((p) -> {
            return p.toStack(FuzzyStack.class, rand);
        }).filter(Optional::isPresent).map((p) -> {
            return ((FuzzyStack)p.get()).toStack();
        }).forEach(products);
    }

    public IGrowthRequirement getGrowthRequirement() {
        return this.growthRequirement;
    }

    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return 0.8125F;
    }

    @SideOnly(Side.CLIENT)
    public RenderMethod getRenderMethod() {
        switch(this.plant.getTexture().getRenderType()) {
            case HASH:
            default:
                return RenderMethod.HASHTAG;
            case CROSS:
                return RenderMethod.CROSSED;
        }
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getPrimaryPlantTexture(int growthStage) {
        return new ResourceLocation(this.plant.getTexture().getPlantTexture(growthStage));
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getSecondaryPlantTexture(int growthStage) {
        return null;
    }

    public ResourceLocation getSeedTexture() {
        return new ResourceLocation(this.plant.getTexture().getSeedTexture());
    }

    @SideOnly(Side.CLIENT)
    public List<BakedQuad> getPlantQuads(IExtendedBlockState state, int growthStage, EnumFacing direction, Function<ResourceLocation, TextureAtlasSprite> textureToIcon) {
        if (textureToIcon instanceof ITessellator) {
            PlantRenderer.renderPlant((ITessellator)textureToIcon, this, growthStage);
        }

        return Collections.emptyList();
    }

    public static final List<FuzzyStack> initSeedItemsListJSON(AgriPlantTFC plant) {
        List<FuzzyStack> seeds = new ArrayList(plant.getSeedItems().size());
        plant.getSeedItems().stream().map((i) -> {
            return i.toStack(FuzzyStack.class);
        }).filter(Optional::isPresent).map(Optional::get).forEach(seeds::add);
        if (seeds.isEmpty()) {
            seeds.add(new FuzzyStack(new ItemStack(AgriItems.getInstance().AGRI_SEED)));
        }

        return seeds;
    }

    public static final IGrowthRequirement initGrowthRequirementJSON(AgriPlantTFC plant) {
        IGrowthReqBuilder builder = new GrowthReqBuilder();
        if (plant == null) {
            return builder.build();
        } else {
            if (plant.getRequirement().getSoils().isEmpty()) {
                AgriCore.getLogger("agricraft").warn("Plant: \"{0}\" has no valid soils to plant on!", new Object[]{plant.getPlantName()});
            }

            plant.getRequirement().getSoils().stream().map(JsonSoil::new).forEach(builder::addSoil);
            plant.getRequirement().getConditions().forEach((obj) -> {
                Optional<FuzzyStack> stack = obj.toStack(FuzzyStack.class);
                if (stack.isPresent()) {
                    builder.addCondition(new BlockCondition((FuzzyStack)stack.get(), new BlockRange(obj.getMinX(), obj.getMinY(), obj.getMinZ(), obj.getMaxX(), obj.getMaxY(), obj.getMaxZ())));
                }

            });
            builder.setMinLight(plant.getRequirement().getMinLight());
            builder.setMaxLight(plant.getRequirement().getMaxLight());
            return builder.build();
        }
    }

    public boolean equals(Object obj) {
        return obj instanceof IAgriPlant && this.getId().equals(((IAgriPlant)obj).getId());
    }

    public int hashCode() {
        return this.getId().hashCode();
    }
}
