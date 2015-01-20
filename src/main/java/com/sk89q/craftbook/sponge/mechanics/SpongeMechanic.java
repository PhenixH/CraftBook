package com.sk89q.craftbook.sponge.mechanics;

import org.spongepowered.api.block.BlockLoc;

import com.sk89q.craftbook.core.Mechanic;
import com.sk89q.craftbook.core.mechanics.MechanicData;
import com.sk89q.craftbook.core.util.CraftBookException;

public abstract class SpongeMechanic implements Mechanic {

    @Override
    public void onInitialize() throws CraftBookException {

    }

    @Override
    public MechanicData getData(BlockLoc location) {

        //TODO
        return null;
    }
}
