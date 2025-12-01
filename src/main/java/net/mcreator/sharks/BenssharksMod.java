package net.mcreator.sharks;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.sharks.worldgen.SharkBiomeModifiers;
import net.mcreator.sharks.init.BenssharksModTabs;
import net.mcreator.sharks.init.BenssharksModSounds;
import net.mcreator.sharks.init.BenssharksModPotions;
import net.mcreator.sharks.init.BenssharksModMobEffects;
import net.mcreator.sharks.init.BenssharksModItems;
import net.mcreator.sharks.init.BenssharksModEntities;
import net.mcreator.sharks.init.BenssharksModEnchantments;
import net.mcreator.sharks.init.BenssharksModBlocks;
import net.mcreator.sharks.init.BenssharksModBlockEntities;
import net.mcreator.sharks.configuration.SpawnsConfiguration;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod("benssharks")
public class BenssharksMod {
	public static final Logger LOGGER = LogManager.getLogger(BenssharksMod.class);
	public static final String MODID = "benssharks";

	public BenssharksMod() {
		// Start of user code block mod constructor
		// 1. Registers your new Biome Modifier system
		SharkBiomeModifiers.register(FMLJavaModLoadingContext.get().getModEventBus());
		// 2. Registers your config file so it actually loads! This was missing.
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SpawnsConfiguration.SPEC);
		// End of user code block mod constructor
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		BenssharksModSounds.REGISTRY.register(bus);
		BenssharksModBlocks.REGISTRY.register(bus);
		BenssharksModBlockEntities.REGISTRY.register(bus);
		BenssharksModItems.REGISTRY.register(bus);
		BenssharksModEntities.REGISTRY.register(bus);
		BenssharksModEnchantments.REGISTRY.register(bus);
		BenssharksModTabs.REGISTRY.register(bus);
		BenssharksModMobEffects.REGISTRY.register(bus);
		BenssharksModPotions.REGISTRY.register(bus);
		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}
