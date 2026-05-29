package ru.bpm140.rattlecomputing;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import ru.bpm140.rattlecomputing.blockentities.McuBlockEntity;
import ru.bpm140.rattlecomputing.blocks.McuBlock;
import ru.bpm140.rattlecomputing.items.CartridgeItem;
import ru.bpm140.rattlecomputing.menus.McuBlockMenu;
import net.minecraft.world.level.Level;
import ru.bpm140.rattlecomputing.packets.FirmwareUploadPacket;
import ru.bpm140.rattlecomputing.packets.SelfDestructPacket;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Rattlecomputing.MODID)
public class Rattlecomputing {
    public static final String MODID = "rattlecomputing";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MODID);

    public static final Supplier<MenuType<McuBlockMenu>> MCU_BLOCK_MENU =
            MENUS.register("mcu_menu",
                    () -> IMenuTypeExtension.create((id, inv, buf) -> {
                        BlockPos pos = buf.readBlockPos();
                        BlockEntity be = inv.player.level().getBlockEntity(pos);

                        return new McuBlockMenu(id, inv, (McuBlockEntity) be);
                    })
            );

    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> CARTRIDGE_ITEM = ITEMS.register("cartridge", () -> new CartridgeItem(new Item.Properties().stacksTo(1)));

    public static final DeferredBlock<Block> MCU_BLOCK =
            BLOCKS.register("mcu_block",
                    () -> new McuBlock(BlockBehaviour.Properties.of()
                            .strength(2.0f)
                            .sound(SoundType.STONE)
                    )
            );

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final Supplier<BlockEntityType<McuBlockEntity>> MCU_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("mcu_block",
                    () -> BlockEntityType.Builder.of(
                            McuBlockEntity::new,
                            MCU_BLOCK.get()
                    ).build(null));

    public static final DeferredItem<Item> MCU_BLOCK_ITEM =
            ITEMS.register("mcu_block",
                    () -> new BlockItem(MCU_BLOCK.get(),
                            new Item.Properties()
                    )
            );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RATTLE_COMPUTING_TAB = CREATIVE_MODE_TABS.register("rattle_computing_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.rattlecomputing")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> EXAMPLE_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
        output.accept(MCU_BLOCK_ITEM.get().getDefaultInstance());
        output.accept(CARTRIDGE_ITEM.get().getDefaultInstance());
    }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Rattlecomputing(IEventBus modEventBus, ModContainer modContainer) {
        System.setProperty("java.awt.headless", "false");
        // Register the commonSetup method for modloadingн
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        MENUS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (rattlecomputing) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerPayloads);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                SelfDestructPacket.TYPE,
                SelfDestructPacket.CODEC,
                (msg, ctx) -> {
                    ctx.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) ctx.player();
                        ServerLevel level = player.serverLevel();
                        BlockPos pos = msg.pos();
                        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4.0f, Level.ExplosionInteraction.BLOCK);
                    });
                }
        );

        registrar.playToServer(FirmwareUploadPacket.TYPE, FirmwareUploadPacket.CODEC, (msg, ctx) -> {
            ctx.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) ctx.player();
                ServerLevel level = player.serverLevel();
                ItemStack stack = player.getMainHandItem();
                var item = stack.getItem();

                if (item instanceof CartridgeItem) {
                    Path worldDir = level.getServer().getWorldPath(LevelResource.ROOT).resolve("rattlecomputing").resolve("firmware");
                    try {
                        Files.createDirectories(worldDir);
                        String randomFileName = UUID.randomUUID().toString();
                        Path file = worldDir.resolve(randomFileName);
                        Path originalPath = Path.of(msg.originalPath());
                        Files.write(file, msg.data());
                        CartridgeItem.setFirmware(stack, msg.originalPath(), file.toString(),
                                                  originalPath.getFileName().toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
