
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.sharks.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

import net.mcreator.sharks.entity.WhitetipSharkEntity;
import net.mcreator.sharks.entity.WhaleSharkEntity;
import net.mcreator.sharks.entity.TigerSharkEntity;
import net.mcreator.sharks.entity.ThresherSharkEntity;
import net.mcreator.sharks.entity.ThalassogerEntity;
import net.mcreator.sharks.entity.ShrakEntity;
import net.mcreator.sharks.entity.SharkMinionEntity;
import net.mcreator.sharks.entity.SeekingArrowEntity;
import net.mcreator.sharks.entity.SeekerSharkProjectileEntity;
import net.mcreator.sharks.entity.SeaLionEntity;
import net.mcreator.sharks.entity.SawsharkEntity;
import net.mcreator.sharks.entity.RollParticleEntity;
import net.mcreator.sharks.entity.RemoraEntity;
import net.mcreator.sharks.entity.PilotFishEntity;
import net.mcreator.sharks.entity.NurseSharkEntity;
import net.mcreator.sharks.entity.MutatedEggProjectileEntity;
import net.mcreator.sharks.entity.MegalodonEntity;
import net.mcreator.sharks.entity.MakoSharkEntity;
import net.mcreator.sharks.entity.LeopardSharkEntity;
import net.mcreator.sharks.entity.LemonSharkEntity;
import net.mcreator.sharks.entity.LandSharkEntity;
import net.mcreator.sharks.entity.KrillEntity;
import net.mcreator.sharks.entity.GreenlandSharkEntity;
import net.mcreator.sharks.entity.GreaterAxodileEntity;
import net.mcreator.sharks.entity.GoblinSharkEntity;
import net.mcreator.sharks.entity.CookiecutterSharkEntity;
import net.mcreator.sharks.entity.BullSharkEntity;
import net.mcreator.sharks.entity.BonnetheadSharkEntity;
import net.mcreator.sharks.entity.BlueSharkEntity;
import net.mcreator.sharks.entity.BlacktipReefSharkEntity;
import net.mcreator.sharks.entity.BaskingSharkEntity;
import net.mcreator.sharks.entity.BarracudaEntity;
import net.mcreator.sharks.entity.AxodileEntity;
import net.mcreator.sharks.BenssharksMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BenssharksModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BenssharksMod.MODID);
	public static final RegistryObject<EntityType<ShrakEntity>> GREATWHITE_SHARK = register("greatwhite_shark",
			EntityType.Builder.<ShrakEntity>of(ShrakEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(ShrakEntity::new)

					.sized(1f, 0.6f));
	public static final RegistryObject<EntityType<NurseSharkEntity>> NURSE_SHARK = register("nurse_shark",
			EntityType.Builder.<NurseSharkEntity>of(NurseSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(NurseSharkEntity::new)

					.sized(1.15f, 0.7f));
	public static final RegistryObject<EntityType<RemoraEntity>> REMORA = register("remora",
			EntityType.Builder.<RemoraEntity>of(RemoraEntity::new, MobCategory.WATER_AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(RemoraEntity::new)

					.sized(0.5f, 0.25f));
	public static final RegistryObject<EntityType<TigerSharkEntity>> TIGER_SHARK = register("tiger_shark",
			EntityType.Builder.<TigerSharkEntity>of(TigerSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(TigerSharkEntity::new)

					.sized(1f, 0.5f));
	public static final RegistryObject<EntityType<AxodileEntity>> AXODILE = register("axodile",
			EntityType.Builder.<AxodileEntity>of(AxodileEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(AxodileEntity::new)

					.sized(1f, 0.6f));
	public static final RegistryObject<EntityType<BlueSharkEntity>> BLUE_SHARK = register("blue_shark",
			EntityType.Builder.<BlueSharkEntity>of(BlueSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(BlueSharkEntity::new)

					.sized(1f, 0.7f));
	public static final RegistryObject<EntityType<BonnetheadSharkEntity>> BONNETHEAD_SHARK = register("bonnethead_shark",
			EntityType.Builder.<BonnetheadSharkEntity>of(BonnetheadSharkEntity::new, MobCategory.WATER_AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(BonnetheadSharkEntity::new)

					.sized(1f, 0.7f));
	public static final RegistryObject<EntityType<MakoSharkEntity>> MAKO_SHARK = register("mako_shark",
			EntityType.Builder.<MakoSharkEntity>of(MakoSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(MakoSharkEntity::new)

					.sized(1f, 0.6f));
	public static final RegistryObject<EntityType<CookiecutterSharkEntity>> COOKIECUTTER_SHARK = register("cookiecutter_shark",
			EntityType.Builder.<CookiecutterSharkEntity>of(CookiecutterSharkEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3)
					.setCustomClientFactory(CookiecutterSharkEntity::new)

					.sized(0.5f, 0.25f));
	public static final RegistryObject<EntityType<BlacktipReefSharkEntity>> BLACKTIP_REEF_SHARK = register("blacktip_reef_shark",
			EntityType.Builder.<BlacktipReefSharkEntity>of(BlacktipReefSharkEntity::new, MobCategory.WATER_AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(BlacktipReefSharkEntity::new)

					.sized(1f, 0.7f));
	public static final RegistryObject<EntityType<BaskingSharkEntity>> BASKING_SHARK = register("basking_shark",
			EntityType.Builder.<BaskingSharkEntity>of(BaskingSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(128).setUpdateInterval(3).setCustomClientFactory(BaskingSharkEntity::new)

					.sized(1f, 0.7f));
	public static final RegistryObject<EntityType<BullSharkEntity>> BULL_SHARK = register("bull_shark",
			EntityType.Builder.<BullSharkEntity>of(BullSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BullSharkEntity::new)

					.sized(1.15f, 0.7f));
	public static final RegistryObject<EntityType<MegalodonEntity>> MEGALODON = register("megalodon",
			EntityType.Builder.<MegalodonEntity>of(MegalodonEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(MegalodonEntity::new)

					.sized(1.55f, 0.7f));
	public static final RegistryObject<EntityType<LandSharkEntity>> LAND_SHARK = register("land_shark",
			EntityType.Builder.<LandSharkEntity>of(LandSharkEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(LandSharkEntity::new)

					.sized(0.6f, 0.85f));
	public static final RegistryObject<EntityType<LemonSharkEntity>> LEMON_SHARK = register("lemon_shark",
			EntityType.Builder.<LemonSharkEntity>of(LemonSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(LemonSharkEntity::new)

					.sized(1.15f, 0.7f));
	public static final RegistryObject<EntityType<ThalassogerEntity>> THALASSOGER = register("thalassoger",
			EntityType.Builder.<ThalassogerEntity>of(ThalassogerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ThalassogerEntity::new)

					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<SeekerSharkProjectileEntity>> SEEKER_SHARK_PROJECTILE = register("seeker_shark_projectile", EntityType.Builder.<SeekerSharkProjectileEntity>of(SeekerSharkProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(SeekerSharkProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.25f, 0.25f));
	public static final RegistryObject<EntityType<KrillEntity>> KRILL = register("krill",
			EntityType.Builder.<KrillEntity>of(KrillEntity::new, MobCategory.WATER_AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(KrillEntity::new)

					.sized(0.5f, 0.4f));
	public static final RegistryObject<EntityType<WhaleSharkEntity>> WHALE_SHARK = register("whale_shark",
			EntityType.Builder.<WhaleSharkEntity>of(WhaleSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(128).setUpdateInterval(3).setCustomClientFactory(WhaleSharkEntity::new)

					.sized(1.5f, 0.7f));
	public static final RegistryObject<EntityType<SeekingArrowEntity>> SEEKING_ARROW = register("seeking_arrow",
			EntityType.Builder.<SeekingArrowEntity>of(SeekingArrowEntity::new, MobCategory.MISC).setCustomClientFactory(SeekingArrowEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.25f, 0.25f));
	public static final RegistryObject<EntityType<PilotFishEntity>> PILOT_FISH = register("pilot_fish",
			EntityType.Builder.<PilotFishEntity>of(PilotFishEntity::new, MobCategory.WATER_AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(PilotFishEntity::new)

					.sized(0.5f, 0.3f));
	public static final RegistryObject<EntityType<GreaterAxodileEntity>> GREATER_AXODILE = register("greater_axodile",
			EntityType.Builder.<GreaterAxodileEntity>of(GreaterAxodileEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(GreaterAxodileEntity::new)

					.sized(1f, 0.6f));
	public static final RegistryObject<EntityType<RollParticleEntity>> ROLL_PARTICLE = register("roll_particle", EntityType.Builder.<RollParticleEntity>of(RollParticleEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(RollParticleEntity::new).fireImmune().sized(0.1f, 0.1f));
	public static final RegistryObject<EntityType<GreenlandSharkEntity>> GREENLAND_SHARK = register("greenland_shark",
			EntityType.Builder.<GreenlandSharkEntity>of(GreenlandSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(GreenlandSharkEntity::new)

					.sized(1f, 0.6f));
	public static final RegistryObject<EntityType<WhitetipSharkEntity>> WHITETIP_SHARK = register("whitetip_shark",
			EntityType.Builder.<WhitetipSharkEntity>of(WhitetipSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(WhitetipSharkEntity::new)

					.sized(1.15f, 0.7f));
	public static final RegistryObject<EntityType<BarracudaEntity>> BARRACUDA = register("barracuda",
			EntityType.Builder.<BarracudaEntity>of(BarracudaEntity::new, MobCategory.WATER_AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BarracudaEntity::new)

					.sized(0.7f, 0.4f));
	public static final RegistryObject<EntityType<LeopardSharkEntity>> LEOPARD_SHARK = register("leopard_shark",
			EntityType.Builder.<LeopardSharkEntity>of(LeopardSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(LeopardSharkEntity::new)

					.sized(1.15f, 0.7f));
	public static final RegistryObject<EntityType<GoblinSharkEntity>> GOBLIN_SHARK = register("goblin_shark",
			EntityType.Builder.<GoblinSharkEntity>of(GoblinSharkEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(GoblinSharkEntity::new)

					.sized(1f, 0.6f));
	public static final RegistryObject<EntityType<SawsharkEntity>> SAWSHARK = register("sawshark",
			EntityType.Builder.<SawsharkEntity>of(SawsharkEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32).setUpdateInterval(3).setCustomClientFactory(SawsharkEntity::new)

					.sized(1.15f, 0.7f));
	public static final RegistryObject<EntityType<SharkMinionEntity>> SHARK_MINION = register("shark_minion",
			EntityType.Builder.<SharkMinionEntity>of(SharkMinionEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SharkMinionEntity::new)

					.sized(0.4f, 0.875f));
	public static final RegistryObject<EntityType<MutatedEggProjectileEntity>> MUTATED_EGG_PROJECTILE = register("mutated_egg_projectile", EntityType.Builder.<MutatedEggProjectileEntity>of(MutatedEggProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(MutatedEggProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<SeaLionEntity>> SEA_LION = register("sea_lion",
			EntityType.Builder.<SeaLionEntity>of(SeaLionEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SeaLionEntity::new)

					.sized(0.85f, 0.67f));
	public static final RegistryObject<EntityType<ThresherSharkEntity>> THRESHER_SHARK = register("thresher_shark",
			EntityType.Builder.<ThresherSharkEntity>of(ThresherSharkEntity::new, MobCategory.WATER_CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ThresherSharkEntity::new)

					.sized(1.35f, 0.875f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			ShrakEntity.init();
			NurseSharkEntity.init();
			RemoraEntity.init();
			TigerSharkEntity.init();
			AxodileEntity.init();
			BlueSharkEntity.init();
			BonnetheadSharkEntity.init();
			MakoSharkEntity.init();
			CookiecutterSharkEntity.init();
			BlacktipReefSharkEntity.init();
			BaskingSharkEntity.init();
			BullSharkEntity.init();
			MegalodonEntity.init();
			LandSharkEntity.init();
			LemonSharkEntity.init();
			ThalassogerEntity.init();
			KrillEntity.init();
			WhaleSharkEntity.init();
			PilotFishEntity.init();
			GreaterAxodileEntity.init();
			RollParticleEntity.init();
			GreenlandSharkEntity.init();
			WhitetipSharkEntity.init();
			BarracudaEntity.init();
			LeopardSharkEntity.init();
			GoblinSharkEntity.init();
			SawsharkEntity.init();
			SharkMinionEntity.init();
			SeaLionEntity.init();
			ThresherSharkEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(GREATWHITE_SHARK.get(), ShrakEntity.createAttributes().build());
		event.put(NURSE_SHARK.get(), NurseSharkEntity.createAttributes().build());
		event.put(REMORA.get(), RemoraEntity.createAttributes().build());
		event.put(TIGER_SHARK.get(), TigerSharkEntity.createAttributes().build());
		event.put(AXODILE.get(), AxodileEntity.createAttributes().build());
		event.put(BLUE_SHARK.get(), BlueSharkEntity.createAttributes().build());
		event.put(BONNETHEAD_SHARK.get(), BonnetheadSharkEntity.createAttributes().build());
		event.put(MAKO_SHARK.get(), MakoSharkEntity.createAttributes().build());
		event.put(COOKIECUTTER_SHARK.get(), CookiecutterSharkEntity.createAttributes().build());
		event.put(BLACKTIP_REEF_SHARK.get(), BlacktipReefSharkEntity.createAttributes().build());
		event.put(BASKING_SHARK.get(), BaskingSharkEntity.createAttributes().build());
		event.put(BULL_SHARK.get(), BullSharkEntity.createAttributes().build());
		event.put(MEGALODON.get(), MegalodonEntity.createAttributes().build());
		event.put(LAND_SHARK.get(), LandSharkEntity.createAttributes().build());
		event.put(LEMON_SHARK.get(), LemonSharkEntity.createAttributes().build());
		event.put(THALASSOGER.get(), ThalassogerEntity.createAttributes().build());
		event.put(KRILL.get(), KrillEntity.createAttributes().build());
		event.put(WHALE_SHARK.get(), WhaleSharkEntity.createAttributes().build());
		event.put(PILOT_FISH.get(), PilotFishEntity.createAttributes().build());
		event.put(GREATER_AXODILE.get(), GreaterAxodileEntity.createAttributes().build());
		event.put(ROLL_PARTICLE.get(), RollParticleEntity.createAttributes().build());
		event.put(GREENLAND_SHARK.get(), GreenlandSharkEntity.createAttributes().build());
		event.put(WHITETIP_SHARK.get(), WhitetipSharkEntity.createAttributes().build());
		event.put(BARRACUDA.get(), BarracudaEntity.createAttributes().build());
		event.put(LEOPARD_SHARK.get(), LeopardSharkEntity.createAttributes().build());
		event.put(GOBLIN_SHARK.get(), GoblinSharkEntity.createAttributes().build());
		event.put(SAWSHARK.get(), SawsharkEntity.createAttributes().build());
		event.put(SHARK_MINION.get(), SharkMinionEntity.createAttributes().build());
		event.put(SEA_LION.get(), SeaLionEntity.createAttributes().build());
		event.put(THRESHER_SHARK.get(), ThresherSharkEntity.createAttributes().build());
	}
}
