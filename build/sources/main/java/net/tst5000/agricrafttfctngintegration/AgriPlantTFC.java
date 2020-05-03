package net.tst5000.agricrafttfctngintegration;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriSerializable;
import com.agricraft.agricore.lang.AgriString;
import com.agricraft.agricore.plant.AgriProductList;
import com.agricraft.agricore.plant.AgriRequirement;
import com.agricraft.agricore.plant.AgriStack;
import com.agricraft.agricore.plant.AgriTexture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AgriPlantTFC implements AgriSerializable {
    private String path;
    private final boolean enabled;
    private final String id;
    private final String plant_name;
    private final String seed_name;
    private final List<AgriStack> seed_items;
    private final AgriString description;
    private final float growth_time;
    private final double growth_chance;
    private final double growth_bonus;
    private final boolean bonemeal;
    private final int tier;
    private final boolean weedable;
    private final boolean aggressive;
    private final double spread_chance;
    private final double spawn_chance;
    private final double grass_drop_chance;
    private final double seed_drop_chance;
    private final double seed_drop_bonus;
    private final AgriProductList products;
    private final AgriRequirementTFC requirement;
    private final AgriTexture texture;

    public AgriPlantTFC() {
        this.enabled = false;
        this.path = "default/weed_plant.json";
        this.id = "weed_plant";
        this.plant_name = "Weed";
        this.seed_name = "Weed Seeds";
        this.seed_items = new ArrayList();
        this.description = new AgriString("An annoying plant.");
        this.bonemeal = true;
        this.tier = 1;
        this.growth_time = 0.5F;
        this.growth_chance = 0.9D;
        this.growth_bonus = 0.025D;
        this.weedable = false;
        this.aggressive = false;
        this.spread_chance = 0.1D;
        this.spawn_chance = 0.0D;
        this.grass_drop_chance = 0.0D;
        this.seed_drop_chance = 1.0D;
        this.seed_drop_bonus = 0.0D;
        this.products = new AgriProductList();
        this.requirement = new AgriRequirementTFC();
        this.texture = new AgriTexture();
    }

    public AgriPlantTFC(String id, String plant_name, String seed_name, List<AgriStack> seed_items, AgriString description, boolean bonemeal, int tier, float growth_time, double growth_chance, double growth_bonus, boolean weedable, boolean agressive, double spread_chance, double spawn_chance, double grass_drop_chance, double seed_drop_chance, double seed_drop_bonus, AgriProductList products, AgriRequirementTFC requirement, AgriTexture texture, String path, boolean enabled) {
        this.enabled = enabled;
        this.path = path;
        this.id = id;
        this.plant_name = plant_name;
        this.seed_name = seed_name;
        this.seed_items = seed_items;
        this.description = description;
        this.bonemeal = bonemeal;
        this.tier = tier;
        this.growth_time = growth_time;
        this.growth_chance = growth_chance;
        this.growth_bonus = growth_bonus;
        this.weedable = weedable;
        this.aggressive = agressive;
        this.spread_chance = spread_chance;
        this.spawn_chance = spawn_chance;
        this.grass_drop_chance = grass_drop_chance;
        this.seed_drop_chance = seed_drop_chance;
        this.seed_drop_bonus = seed_drop_bonus;
        this.products = products;
        this.requirement = requirement;
        this.texture = texture;
    }

    public String getId() {
        return this.id;
    }

    public String getPlantName() {
        return this.plant_name;
    }

    public String getSeedName() {
        return this.seed_name;
    }

    public Collection<AgriStack> getSeedItems() {
        return this.seed_items;
    }

    public int getGrowthStages() {
        return this.texture.getGrowthStages();
    }

    public AgriString getDescription() {
        return this.description;
    }

    public AgriProductList getProducts() {
        return this.products;
    }

    public AgriRequirementTFC getRequirement() {
        return this.requirement;
    }

    public AgriTexture getTexture() {
        return this.texture;
    }

    public int getTier() {
        return this.tier;
    }

    public boolean canBonemeal() {
        return this.bonemeal;
    }

    public boolean isWeedable() {
        return this.weedable;
    }

    public boolean isAgressive() {
        return this.aggressive;
    }

    public double getSpawnChance() {
        return this.spawn_chance;
    }

    public double getSpreadChance() {
        return this.spread_chance;
    }

    public float getGrowthTime() {
        return this.growth_time;
    }

    public double getGrowthChance() {
        return this.growth_chance;
    }

    public double getGrowthBonus() {
        return this.growth_bonus;
    }

    public double getGrassDropChance() {
        return this.grass_drop_chance;
    }

    public double getSeedDropChance() {
        return this.seed_drop_chance;
    }

    public double getSeedDropBonus() {
        return this.seed_drop_bonus;
    }

    public boolean validate() {
        if (!this.enabled) {
            AgriCore.getCoreLogger().debug("Disabled Plant: {0}!", new Object[]{this.id});
            return false;
        } else if (!this.requirement.validate()) {
            AgriCore.getCoreLogger().debug("Invalid Plant: {0}! Invalid Requirement!", new Object[]{this.id});
            return false;
        } else if (!this.products.validate()) {
            AgriCore.getCoreLogger().debug("Invalid Plant: {0}! Invalid Product!", new Object[]{this.id});
            return false;
        } else if (!this.texture.validate()) {
            AgriCore.getCoreLogger().debug("Invalid Plant: {0}! Invalid Texture!", new Object[]{this.id});
            return false;
        } else {
            this.seed_items.removeIf((s) -> {
                return !s.validate();
            });
            return true;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(this.id).append(":\n");
        sb.append("\t- Plant Name: ").append(this.plant_name).append("\n");
        sb.append("\t- Seed Name: ").append(this.seed_name).append("\n");
        sb.append("\t- Bonemeal: ").append(this.bonemeal).append("\n");
        sb.append("\t- Growth Time: ").append(this.growth_time).append("\n");
        sb.append("\t- Growth Chance: ").append(this.growth_chance).append("\n");
        sb.append("\t- Growth Bonus: ").append(this.growth_bonus).append("\n");
        sb.append("\t- Seed Drop Chance: ").append(this.seed_drop_chance).append("\n");
        sb.append("\t- Seed Drop Bonus: ").append(this.seed_drop_bonus).append("\n");
        sb.append("\t- Grass Drop Chance: ").append(this.grass_drop_chance).append("\n");
        sb.append("\t- ").append(this.products.toString().replaceAll("\n", "\n\t").trim()).append("\n");
        sb.append("\t- ").append(this.requirement.toString().replaceAll("\n", "\n\t").trim()).append("\n");
        return sb.toString();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}


