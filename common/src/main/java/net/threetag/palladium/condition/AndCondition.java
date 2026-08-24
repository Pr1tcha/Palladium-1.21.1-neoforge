package net.threetag.palladium.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.property.ConditionArrayProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;

public class AndCondition extends Condition {

    public final Condition[] conditions;

    public AndCondition(Condition[] conditions) {
        this.conditions = conditions;
    }

    @Override
    public void registerAbilityProperties(AbilityInstance entry, PropertyManager manager) {
        for (Condition condition : this.conditions) {
            condition.registerAbilityProperties(entry, manager);
        }
    }

    @Override
    public void init(LivingEntity entity, AbilityInstance entry, PropertyManager manager) {
        for (Condition condition : this.conditions) {
            condition.init(entity, entry, manager);
        }
    }

    @Override
    public boolean active(DataContext context) {
        for (Condition condition : this.conditions) {
            if (!condition.active(context)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return ConditionSerializers.AND.get();
    }

    public static class Serializer extends ConditionSerializer {

        public static final PalladiumProperty<Condition[]> CONDITIONS = new ConditionArrayProperty("conditions").configurable("Array of conditions, all of which must be active");

        public Serializer() {
            this.withProperty(CONDITIONS, new Condition[0]);
        }

        @Override
        public Condition make(JsonObject json) {
            return new AndCondition(this.getProperty(json, CONDITIONS));
        }

        @Override
        public String getDocumentationDescription() {
            return "A condition that is active if all of the conditions in the array are active.";
        }
    }
}
