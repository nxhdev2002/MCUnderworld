package com.kiemhiep.api.platform;

import java.util.UUID;

/**
 * Effect player interface for playing sounds and particles.
 */
public interface EffectPlayer {

    /**
     * Play a sound for a player.
     *
     * @param playerAdapter The player to play the sound for
     * @param sound         The sound name
     * @param volume        The volume (0.0 - 1.0)
     * @param pitch         The pitch (0.5 - 2.0)
     */
    void playSound(PlayerAdapter playerAdapter, String sound, float volume, float pitch);

    /**
     * Play a sound at a location.
     *
     * @param worldAdapter The world to play the sound in
     * @param x            The X coordinate
     * @param y            The Y coordinate
     * @param z            The Z coordinate
     * @param sound        The sound name
     * @param volume       The volume (0.0 - 1.0)
     * @param pitch        The pitch (0.5 - 2.0)
     */
    void playSound(WorldAdapter worldAdapter, double x, double y, double z, String sound, float volume, float pitch);

    /**
     * Spawn particles at a location.
     *
     * @param worldAdapter  The world to spawn particles in
     * @param x             The X coordinate
     * @param y             The Y coordinate
     * @param z             The Z coordinate
     * @param particleType  The particle type
     * @param count         The number of particles
     */
    void spawnParticle(WorldAdapter worldAdapter, double x, double y, double z, String particleType, int count);

    /**
     * Spawn particles with offset.
     *
     * @param worldAdapter  The world to spawn particles in
     * @param x             The X coordinate
     * @param y             The Y coordinate
     * @param z             The Z coordinate
     * @param particleType  The particle type
     * @param count         The number of particles
     * @param offsetX       X offset
     * @param offsetY       Y offset
     * @param offsetZ       Z offset
     */
    void spawnParticle(WorldAdapter worldAdapter, double x, double y, double z, String particleType, int count,
                       double offsetX, double offsetY, double offsetZ);

    /**
     * Spawn colored particles (for redstone).
     *
     * @param worldAdapter  The world to spawn particles in
     * @param x             The X coordinate
     * @param y             The Y coordinate
     * @param z             The Z coordinate
     * @param particleType  The particle type
     * @param count         The number of particles
     * @param offsetX       X offset
     * @param offsetY       Y offset
     * @param offsetZ       Z offset
     * @param extra         Extra data
     * @param r             Red color (0-255)
     * @param g             Green color (0-255)
     * @param b             Blue color (0-255)
     */
    void spawnParticle(WorldAdapter worldAdapter, double x, double y, double z, String particleType, int count,
                       double offsetX, double offsetY, double offsetZ, double extra, int r, int g, int b);

    /**
     * Send a title to a player.
     *
     * @param playerAdapter The player to send the title to
     * @param title         The main title
     * @param subtitle      The subtitle
     * @param fadeIn        Fade in time in ticks
     * @param stay          Stay time in ticks
     * @param fadeOut       Fade out time in ticks
     */
    void sendTitle(PlayerAdapter playerAdapter, String title, String subtitle, int fadeIn, int stay, int fadeOut);

    /**
     * Send an action bar message to a player.
     *
     * @param playerAdapter The player to send the message to
     * @param message       The message
     */
    void sendActionBar(PlayerAdapter playerAdapter, String message);

    /**
     * Spawn a lightning bolt at a location.
     *
     * @param worldAdapter The world
     * @param x            The X coordinate
     * @param y            The Y coordinate
     * @param z            The Z coordinate
     */
    void spawnLightning(WorldAdapter worldAdapter, double x, double y, double z);
}
