/*
 * CraftBook Copyright (C) 2010-2016 sk89q <http://www.sk89q.com>
 * CraftBook Copyright (C) 2011-2016 me4502 <http://www.me4502.com>
 * CraftBook Copyright (C) Contributors
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package com.sk89q.craftbook.sponge.st;

import com.me4502.modularframework.exception.ModuleNotInstantiatedException;
import com.me4502.modularframework.module.ModuleWrapper;
import com.sk89q.craftbook.core.CraftBookAPI;
import com.sk89q.craftbook.sponge.CraftBookPlugin;
import com.sk89q.craftbook.sponge.mechanics.types.SpongeBlockMechanic;
import com.sk89q.craftbook.sponge.mechanics.types.SpongeMechanic;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;
import org.spongepowered.api.event.world.chunk.UnloadChunkEvent;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class SelfTriggerManager {

    public static boolean isInitialized = false;

    private static Map<Location, SelfTriggeringMechanic> selfTriggeringMechanics = new HashMap<>();

    public static void initialize() {
        Sponge.getGame().getScheduler().createTaskBuilder().intervalTicks(2L).execute(new SelfTriggerClock()).submit(CraftBookPlugin.inst());
        Sponge.getGame().getEventManager().registerListeners(CraftBookPlugin.inst(), new SelfTriggerManager());

        isInitialized = true;

        for(World world : Sponge.getGame().getServer().getWorlds()) {
            for(Chunk chunk : world.getLoadedChunks()) {
                registerAll(chunk);
            }
        }
    }

    public static void unload() {
        selfTriggeringMechanics.clear();
    }

    public static void register(SelfTriggeringMechanic mechanic, Location location) {
        selfTriggeringMechanics.put(location, mechanic);
    }

    private static void registerAll(Chunk chunk) {
        for(TileEntity tileEntity : chunk.getTileEntities()) {
            for (ModuleWrapper module : CraftBookPlugin.<CraftBookPlugin>inst().moduleController.getModules()) {
                if(!module.isEnabled()) continue;
                try {
                    SpongeMechanic mechanic = (SpongeMechanic) module.getModule();
                    if (mechanic instanceof SpongeBlockMechanic && mechanic instanceof SelfTriggeringMechanic) {
                        if (((SpongeBlockMechanic) mechanic).isValid(tileEntity.getLocation()))
                            register((SelfTriggeringMechanic) mechanic, tileEntity.getLocation());
                    }
                } catch (ModuleNotInstantiatedException e) {
                    CraftBookAPI.<CraftBookPlugin>inst().getLogger().error("Failed to register self-triggering mechanic for module: " + module.getName(), e);
                }
            }
        }
    }

    private static void unregisterAll(Extent extent) {
        new HashSet<>(selfTriggeringMechanics.keySet()).stream().filter(loc -> loc.inExtent(extent)).forEach(selfTriggeringMechanics::remove);
    }

    static void think() {
        for (Entry<Location, SelfTriggeringMechanic> entry : selfTriggeringMechanics.entrySet()) {
            entry.getValue().onThink(entry.getKey());
        }
    }

    @Listener
    public void onChunkLoad(LoadChunkEvent event) {
        registerAll(event.getTargetChunk());
    }

    @Listener
    public void onChunkUnload(UnloadChunkEvent event) {
        unregisterAll(event.getTargetChunk());
    }

    @Listener
    public void onWorldUnload(UnloadWorldEvent event) {
        unregisterAll(event.getTargetWorld());
    }
}
