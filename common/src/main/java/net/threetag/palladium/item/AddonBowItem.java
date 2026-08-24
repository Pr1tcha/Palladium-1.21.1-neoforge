package net.threetag.palladium.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.addonpack.parser.ItemParser;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.json.GsonUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class AddonBowItem extends BowItem implements IAddonItem {

    private List<Component> tooltipLines;
    private RenderLayerContainer renderLayerContainer = null;
    private final AddonAttributeContainer attributeContainer = new AddonAttributeContainer();
    private boolean shouldRenderModel = true;
    private final float velocity, inaccuracy;
    private final int useDuration;
    private final Predicate<ItemStack> projectiles;
    @Nullable
    private final Predicate<ItemStack> heldProjectiles;

    public AddonBowItem(float velocity, float inaccuracy, int useDuration, Predicate<ItemStack> projectiles, @Nullable Predicate<ItemStack> heldProjectiles, Properties properties) {
        super(properties);
        this.velocity = velocity;
        this.inaccuracy = inaccuracy;
        this.useDuration = useDuration;
        this.projectiles = projectiles;
        this.heldProjectiles = heldProjectiles;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.useDuration;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return this.projectiles;
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.heldProjectiles == null ? this.getAllSupportedProjectiles() : this.heldProjectiles;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player) {
            ItemStack ammo = player.getProjectile(stack);
            if (ammo.isEmpty()) {
                return;
            }

            int charge = this.getUseDuration(stack, livingEntity) - timeCharged;
            charge = EventHooks.onArrowLoose(stack, level, player, charge, true);
            if (charge < 0) {
                return;
            }

            float power = getPowerForTime(charge);
            if (power < 0.1F) {
                return;
            }

            List<ItemStack> projectiles = draw(stack, ammo, player);
            if (level instanceof ServerLevel serverLevel && !projectiles.isEmpty()) {
                this.shoot(serverLevel, player, player.getUsedItemHand(), stack, projectiles, power * this.velocity, this.inaccuracy, power == 1.0F, null);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                    1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
        if (ammo.getItem() instanceof AddonProjectileItem projectileItem) {
            Projectile projectile = projectileItem.createProjectile(level, ammo, shooter);
            if (projectile != null) {
                return projectile;
            }
        }
        return super.createProjectile(level, shooter, weapon, ammo, isCrit);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, context, tooltipComponents, isAdvanced);
        if (this.tooltipLines != null) {
            tooltipComponents.addAll(this.tooltipLines);
        }
    }

    @Override
    public net.minecraft.world.item.component.ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return this.attributeContainer.apply(super.getDefaultAttributeModifiers(stack));
    }

    @Override
    public AddonAttributeContainer getAttributeContainer() {
        return this.attributeContainer;
    }

    @Override
    public void setTooltip(List<Component> lines) {
        this.tooltipLines = lines;
    }

    @Override
    public void setRenderLayerContainer(RenderLayerContainer container) {
        this.renderLayerContainer = container;
    }

    @Override
    public RenderLayerContainer getRenderLayerContainer() {
        return this.renderLayerContainer;
    }

    @Override
    public void setShouldRenderModel(boolean shouldRender) {
        this.shouldRenderModel = shouldRender;
    }

    @Override
    public boolean shouldRenderModel() {
        return this.shouldRenderModel;
    }

    public static class Parser implements ItemParser.ItemTypeSerializer {

        @Override
        public IAddonItem parse(JsonObject json, Properties properties) {
            float velocity = GsonHelper.getAsFloat(json, "velocity", 3F);
            float inaccuracy = GsonHelper.getAsFloat(json, "inaccuracy", 1F);
            int useDuration = GsonHelper.getAsInt(json, "use_duration", 72000);
            TagKey<Item> projectiles = TagKey.create(Registries.ITEM, GsonUtil.getAsResourceLocation(json, "projectiles", ResourceLocation.parse("minecraft:arrows")));
            TagKey<Item> heldProjectiles = json.has("held_projectiles") ? TagKey.create(Registries.ITEM, GsonUtil.getAsResourceLocation(json, "held_projectiles")) : null;

            return new AddonBowItem(velocity, inaccuracy, useDuration, stack -> stack.is(projectiles), heldProjectiles == null ? null : stack -> stack.is(heldProjectiles), properties);
        }

        @Override
        public void generateDocumentation(JsonDocumentationBuilder builder) {
            builder.setTitle("Bow");

            builder.addProperty("velocity", Float.class)
                    .description("Velocity multiplier for the shot projectile")
                    .fallback(3F).exampleJson(new JsonPrimitive(3F));

            builder.addProperty("inaccuracy", Float.class)
                    .description("Inaccuracy for the shot projectile")
                    .fallback(1F).exampleJson(new JsonPrimitive(1F));

            builder.addProperty("use_duration", Integer.class)
                    .description("Amount of ticks the bow can be used for")
                    .fallback(72000).exampleJson(new JsonPrimitive(72000));

            builder.addProperty("projectiles", ResourceLocation.class)
                    .description("Item tag which contains all items that can be shot. By default all Minecraft arrows")
                    .fallback(ResourceLocation.parse("arrows")).exampleJson(new JsonPrimitive("minecraft:arrows"));

            builder.addProperty("held_projectiles", ResourceLocation.class)
                    .description("Item tag which contains all items that can be shot by being in the off hand. Can be left out to fallback to the 'projectiles' option")
                    .fallback(null).exampleJson(new JsonPrimitive("minecraft:arrows"));
        }

        @Override
        public ResourceLocation getId() {
            return Palladium.id("bow");
        }
    }
}
