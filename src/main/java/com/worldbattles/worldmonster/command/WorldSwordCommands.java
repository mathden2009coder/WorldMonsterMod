package com.worldbattles.worldmonster.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.worldbattles.worldmonster.item.WorldSwordItem;
import com.worldbattles.worldmonster.registry.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "worldmonster", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WorldSwordCommands {
    private WorldSwordCommands() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("worldsword")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("give")
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 5))
                                .executes(context -> give(context, context.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(context, "level"))))
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 5))
                                        .executes(context -> give(context, EntityArgument.getPlayer(context, "target"),
                                                IntegerArgumentType.getInteger(context, "level")))))));
    }

    private static int give(CommandContext<CommandSourceStack> context, ServerPlayer target, int level) {
        ItemStack sword = new ItemStack(ModItems.WORLD_SWORD.get());
        WorldSwordItem.setProgress(sword, level, WorldSwordItem.getThreshold(level));
        if (!target.getInventory().add(sword)) target.drop(sword, false);
        context.getSource().sendSuccess(() -> Component.translatable(
                "command.worldmonster.world_sword.given", level, target.getDisplayName()), true);
        return 1;
    }
}