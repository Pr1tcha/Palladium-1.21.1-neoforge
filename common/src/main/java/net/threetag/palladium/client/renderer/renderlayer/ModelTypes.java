package net.threetag.palladium.client.renderer.renderlayer;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.client.model.BasicModel;
import net.threetag.palladium.client.model.CapedHumanoidModel;
import net.threetag.palladium.client.model.ImprovedHumanoidModel;
import net.threetag.palladium.client.model.ThrusterHumanoidModel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class ModelTypes {

    private static final Map<ResourceLocation, Model> MODELS = new HashMap<>();
    public static final Model HUMANOID;

    static {
        register(Palladium.id("basic"), new Model(BasicModel::new, (en, model) -> true));
        HUMANOID = register(ResourceLocation.parse("humanoid"), new Model(ImprovedHumanoidModel::new, (en, model) -> model instanceof HumanoidModel));
        register(ResourceLocation.parse("player"), new Model(modelPart -> new PlayerModel<>(modelPart, false), (en, model) -> model instanceof HumanoidModel));
        register(ResourceLocation.parse("pig"), new Model(PigModel::new, (en, model) -> model instanceof PigModel));
        register(ResourceLocation.parse("wolf"), new Model(WolfModel::new, (en, model) -> model instanceof WolfModel));
        register(ResourceLocation.parse("cat"), new Model(CatModel::new, (en, model) -> model instanceof CatModel));
        register(ResourceLocation.parse("horse"), new Model(HorseModel::new, (en, model) -> model instanceof HorseModel));
        register(ResourceLocation.parse("sheep"), new Model(SheepModel::new, (en, model) -> model instanceof SheepModel));
        register(ResourceLocation.parse("cow"), new Model(CowModel::new, (en, model) -> model instanceof CowModel));
        register(ResourceLocation.parse("chicken"), new Model(ChickenModel::new, (en, model) -> model instanceof ChickenModel));
        register(ResourceLocation.parse("villager"), new Model(VillagerModel::new, (en, model) -> model instanceof VillagerModel));
        register(ResourceLocation.parse("illager"), new Model(IllagerModel::new, (en, model) -> model instanceof IllagerModel));
        register(ResourceLocation.parse("axolotl"), new Model(AxolotlModel::new, (en, model) -> model instanceof AxolotlModel));
        register(ResourceLocation.parse("bat"), new Model(BatModel::new, (en, model) -> model instanceof BatModel));
        register(ResourceLocation.parse("bee"), new Model(BeeModel::new, (en, model) -> model instanceof BeeModel));
        register(ResourceLocation.parse("blaze"), new Model(BlazeModel::new, (en, model) -> model instanceof BlazeModel));
        register(ResourceLocation.parse("spider"), new Model(SpiderModel::new, (en, model) -> model instanceof SpiderModel));
        register(ResourceLocation.parse("cod"), new Model(CodModel::new, (en, model) -> model instanceof CodModel));
        register(ResourceLocation.parse("creeper"), new Model(CreeperModel::new, (en, model) -> model instanceof CreeperModel));
        register(ResourceLocation.parse("dolphin"), new Model(DolphinModel::new, (en, model) -> model instanceof DolphinModel));
        register(ResourceLocation.parse("guardian"), new Model(GuardianModel::new, (en, model) -> model instanceof GuardianModel));
        register(ResourceLocation.parse("enderman"), new Model(EndermanModel::new, (en, model) -> model instanceof EndermanModel));
        register(ResourceLocation.parse("endermite"), new Model(EndermiteModel::new, (en, model) -> model instanceof EndermiteModel));
        register(ResourceLocation.parse("fox"), new Model(FoxModel::new, (en, model) -> model instanceof FoxModel));
        register(ResourceLocation.parse("ghast"), new Model(GhastModel::new, (en, model) -> model instanceof GhastModel));
        register(ResourceLocation.parse("goat"), new Model(GoatModel::new, (en, model) -> model instanceof GoatModel));
        register(ResourceLocation.parse("hoglin"), new Model(HoglinModel::new, (en, model) -> model instanceof HoglinModel));
        register(ResourceLocation.parse("iron_golem"), new Model(IronGolemModel::new, (en, model) -> model instanceof IronGolemModel));
        register(ResourceLocation.parse("llama"), new Model(LlamaModel::new, (en, model) -> model instanceof LlamaModel));
        register(ResourceLocation.parse("magma_cube"), new Model(LavaSlimeModel::new, (en, model) -> model instanceof LavaSlimeModel));
        register(ResourceLocation.parse("panda"), new Model(PandaModel::new, (en, model) -> model instanceof PandaModel));
        register(ResourceLocation.parse("parrot"), new Model(ParrotModel::new, (en, model) -> model instanceof ParrotModel));
        register(ResourceLocation.parse("phantom"), new Model(PhantomModel::new, (en, model) -> model instanceof PhantomModel));
        register(ResourceLocation.parse("piglin"), new Model(PiglinModel::new, (en, model) -> model instanceof PiglinModel));
        register(ResourceLocation.parse("polar_bear"), new Model(PolarBearModel::new, (en, model) -> model instanceof PolarBearModel));
        register(ResourceLocation.parse("rabbit"), new Model(RabbitModel::new, (en, model) -> model instanceof RabbitModel));
        register(ResourceLocation.parse("ravager"), new Model(RavagerModel::new, (en, model) -> model instanceof RavagerModel));
        register(ResourceLocation.parse("salmon"), new Model(SalmonModel::new, (en, model) -> model instanceof SalmonModel));
        register(ResourceLocation.parse("shulker"), new Model(ShulkerModel::new, (en, model) -> model instanceof ShulkerModel));
        register(ResourceLocation.parse("silverfish"), new Model(SilverfishModel::new, (en, model) -> model instanceof SilverfishModel));
        register(ResourceLocation.parse("slime"), new Model(SlimeModel::new, (en, model) -> model instanceof SlimeModel));
        register(ResourceLocation.parse("snow_golem"), new Model(SnowGolemModel::new, (en, model) -> model instanceof SnowGolemModel));
        register(ResourceLocation.parse("squid"), new Model(SquidModel::new, (en, model) -> model instanceof SquidModel));
        register(ResourceLocation.parse("strider"), new Model(StriderModel::new, (en, model) -> model instanceof StriderModel));
        register(ResourceLocation.parse("turtle"), new Model(TurtleModel::new, (en, model) -> model instanceof TurtleModel));
        register(ResourceLocation.parse("vex"), new Model(VexModel::new, (en, model) -> model instanceof VexModel));
        register(ResourceLocation.parse("wither"), new Model(WitherBossModel::new, (en, model) -> model instanceof WitherBossModel));
        register(ResourceLocation.parse("camel"), new Model(CamelModel::new, (en, model) -> model instanceof CamelModel));
        register(ResourceLocation.parse("frog"), new Model(FrogModel::new, (en, model) -> model instanceof FrogModel));

        register(Palladium.id("caped_humanoid"), new Model(CapedHumanoidModel::new, (en, model) -> model instanceof HumanoidModel));
        register(Palladium.id("thruster_humanoid"), new Model(ThrusterHumanoidModel::new, (en, model) -> model instanceof HumanoidModel));
    }

    public static Model register(ResourceLocation id, Model model) {
        MODELS.put(id, model);
        return model;
    }

    public static Model get(ResourceLocation id) {
        return MODELS.get(id);
    }

    public record Model(
            Function<ModelPart, EntityModel<?>> modelFunction,
            BiPredicate<Entity, EntityModel<?>> predicate) {

        public EntityModel<?> getModel(ModelPart modelPart) {
            return this.modelFunction.apply(modelPart);
        }

        public boolean fitsEntity(Entity entity, EntityModel<?> parentModel) {
            return this.predicate.test(entity, parentModel);
        }
    }

}
