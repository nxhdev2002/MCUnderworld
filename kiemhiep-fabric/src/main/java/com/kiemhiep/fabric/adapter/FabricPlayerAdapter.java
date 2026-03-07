package com.kiemhiep.fabric.adapter;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.platform.World;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Fabric adapter for ServerPlayerEntity.
 */
public class FabricPlayerAdapter implements PlayerAdapter {
    private final ServerPlayerEntity player;

    public FabricPlayerAdapter(ServerPlayerEntity player) {
        this.player = player;
    }

    public ServerPlayerEntity getHandle() {
        return player;
    }

    @Override
    public UUID getUuid() {
        return player.getUuid();
    }

    @Override
    public String getName() {
        return player.getName().getString();
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(Text.literal(message), false);
    }

    @Override
    public void sendMessage(String message, String... placeholders) {
        String formatted = message;
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                formatted = formatted.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        player.sendMessage(Text.literal(formatted), false);
    }

    @Override
    public void sendActionBar(String message) {
        player.sendMessage(Text.literal(message).formatted(Formatting.GOLD), true);
    }

    @Override
    public Location getLocation() {
        return new Location(
                new World(player.getWorld().getRegistryKey().getValue().toString()),
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYaw(),
                player.getPitch()
        );
    }

    @Override
    public void teleport(Location location) {
        var world = player.getWorld();
        // TODO: Get correct world from location
        player.teleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
    }

    @Override
    public CompletableFuture<Boolean> teleportAsync(Location location) {
        return CompletableFuture.supplyAsync(() -> {
            player.teleport(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            return true;
        });
    }

    @Override
    public int getHealth() {
        return (int) player.getHealth();
    }

    @Override
    public void setHealth(int health) {
        player.setHealth(health);
    }

    @Override
    public int getMaxHealth() {
        return (int) player.getMaxHealth();
    }

    @Override
    public boolean isOnline() {
        return !player.isDisconnected();
    }

    @Override
    public boolean isOp() {
        return player.hasPermissionLevel(2);
    }

    @Override
    public World getWorld() {
        return new World(player.getWorld().getRegistryKey().getValue().toString());
    }

    @Override
    public void giveItem(String material, int amount) {
        // TODO: Convert material string to ItemStack
        ItemStack stack = ItemStack.EMPTY;
        player.giveItemStack(stack);
    }

    @Override
    public void removeItem(String material, int amount) {
        // TODO: Implement item removal
    }

    @Override
    public boolean hasItem(String material, int amount) {
        // TODO: Implement item check
        return false;
    }

    @Override
    public void addEffect(String effectName, int duration, int amplifier) {
        // TODO: Implement status effects
    }

    @Override
    public void removeEffect(String effectName) {
        // TODO: Implement status effect removal
    }

    @Override
    public boolean hasEffect(String effectName) {
        // TODO: Implement status effect check
        return false;
    }

    @Override
    public int getPotionEffectDuration(String effectName) {
        // TODO: Implement
        return 0;
    }

    @Override
    public void setGameMode(String gameMode) {
        // TODO: Implement game mode change
    }

    @Override
    public String getGameMode() {
        return player.interactionManager.getGameMode().getName();
    }

    @Override
    public void kick(String reason) {
        player.networkHandler.disconnect(Text.literal(reason));
    }

    @Override
    public void ban(String reason) {
        // TODO: Implement ban
        kick(reason);
    }

    @Override
    public void playSound(String soundName) {
        // TODO: Implement sound playing
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        // TODO: Implement title sending
    }

    @Override
    public int getExperience() {
        return player.experienceProgress;
    }

    @Override
    public void setExperience(int experience) {
        player.experienceProgress = experience;
    }

    @Override
    public int getLevel() {
        return player.experienceLevel;
    }

    @Override
    public void setLevel(int level) {
        player.experienceLevel = level;
    }

    @Override
    public int getFoodLevel() {
        return player.getHungerManager().getFoodLevel();
    }

    @Override
    public void setFoodLevel(int foodLevel) {
        player.getHungerManager().setFoodLevel(foodLevel);
    }

    @Override
    public Object getRawPlayer() {
        return player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermissionLevel(2);
    }

    @Override
    public Object getWeaponInMainHand() {
        return player.getStackInHand(Hand.MAIN_HAND);
    }
}
