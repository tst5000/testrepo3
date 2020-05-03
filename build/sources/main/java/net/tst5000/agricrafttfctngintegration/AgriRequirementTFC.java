package net.tst5000.agricrafttfctngintegration;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriCondition;
import com.agricraft.agricore.plant.AgriRequirement;
import com.agricraft.agricore.plant.AgriSoil;
import com.agricraft.agricore.registry.AgriSoils;
import com.agricraft.agricore.util.TypeHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgriRequirementTFC extends AgriRequirement {
    private final int min_light;
    private final int max_light;
    private final float tempMinAlive;
    private final float tempMinGrow;
    private final float tempMaxGrow;
    private final float tempMaxAlive;
    private final float rainMinAlive;
    private final float rainMinGrow;
    private final float rainMaxGrow;
    private final float rainMaxAlive;
    private final List<String> soils;
    private final List<AgriCondition> conditions;

    public AgriRequirementTFC() {
        this.min_light = 10;
        this.max_light = 16;
        this.tempMinAlive = 0f;
        this.tempMinGrow = 4f;
        this.tempMaxGrow = 35f;
        this.tempMaxAlive = 40f;
        this.rainMinAlive = 50f;
        this.rainMinGrow = 100f;
        this.rainMaxGrow = 400f;
        this.rainMaxAlive = 450f;
        this.soils = new ArrayList();
        this.conditions = new ArrayList();
    }

    public AgriRequirementTFC(int min_light, int max_light, float tempMinAlive, float tempMinGrow, float tempMaxGrow, float tempMaxAlive, float rainMinAlive, float rainMinGrow, float rainMaxGrow, float rainMaxAlive, List<String> soils, List<AgriCondition> conditions) {
        this.min_light = min_light;
        this.max_light = max_light;
        this.tempMinAlive = tempMinAlive;
        this.tempMinGrow = tempMinGrow;
        this.tempMaxGrow = tempMaxGrow;
        this.tempMaxAlive = tempMaxAlive;
        this.rainMinAlive = rainMinAlive;
        this.rainMinGrow = rainMinGrow;
        this.rainMaxGrow = rainMaxGrow;
        this.rainMaxAlive = rainMaxAlive;
        this.soils = new ArrayList(soils);
        this.conditions = conditions;
    }

    public int getMinLight() {
        return this.min_light;
    }

    public int getMaxLight() {
        return this.max_light;
    }

    public float getTempMinAlive() {
        return this.tempMinAlive;
    }

    public float getTempMinGrow() {
        return this.tempMinGrow;
    }

    public float getTempMaxGrow() {
        return this.tempMaxGrow;
    }

    public float getTempMaxAlive() {
        return this.tempMaxAlive;
    }

    public float getRainMinAlive() {
        return this.rainMinAlive;
    }

    public float getRainMinGrow() {
        return this.rainMinGrow;
    }

    public float getRainMaxGrow() {
        return this.rainMaxGrow;
    }

    public float getRainMaxAlive() {
        return this.rainMaxAlive;
    }

    public List<AgriCondition> getConditions() {
        return new ArrayList(this.conditions);
    }

    public boolean validate() {
        this.soils.removeIf((soil) -> {
            if (!AgriCore.getSoils().hasSoil(soil)) {
                AgriCore.getCoreLogger().info("Invalid Requirement: Invalid Soil: {0}! Removing!", new Object[]{soil});
                return true;
            } else {
                return false;
            }
        });
        Iterator var1 = this.conditions.iterator();

        AgriCondition condition;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            condition = (AgriCondition)var1.next();
        } while(condition.validate());

        AgriCore.getCoreLogger().info("Invalid Requirement: Invalid Condition!", new Object[]{condition});
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRequirement:");
        sb.append("\n\t- Light:");
        sb.append("\n\t\t- Min: ").append(this.min_light);
        sb.append("\n\t\t- Max: ").append(this.max_light);
        sb.append("\n\t- Temp:");
        sb.append("\n\t\t- MinGrow: ").append(this.tempMinGrow);
        sb.append("\n\t\t- MaxGrow: ").append(this.tempMaxGrow);
        sb.append("\n\t\t- MinAlive: ").append(this.tempMinAlive);
        sb.append("\n\t\t- MaxAlive: ").append(this.tempMaxAlive);
        sb.append("\n\t- Rain:");
        sb.append("\n\t\t- MinGrow: ").append(this.rainMinGrow);
        sb.append("\n\t\t- MaxGrow: ").append(this.rainMaxGrow);
        sb.append("\n\t\t- MinAlive: ").append(this.rainMinAlive);
        sb.append("\n\t\t- MaxAlive: ").append(this.rainMaxAlive);
        sb.append("\n\t- Soil:");
        this.soils.forEach((e) -> {
            sb.append("\n\t\t- AgriSoil: ").append(e);
        });
        sb.append("\n\t- Conditions:");
        this.conditions.forEach((e) -> {
            sb.append("\n\t\t- ").append(e.toString().replaceAll("\n", "\n\t\t").trim());
        });
        return sb.toString();
    }
}
