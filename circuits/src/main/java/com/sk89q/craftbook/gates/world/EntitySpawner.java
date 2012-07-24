// $Id$
/*
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.craftbook.gates.world;

import com.sk89q.craftbook.ic.*;
import com.sk89q.craftbook.util.SignUtil;
import com.sk89q.worldedit.blocks.BlockType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntitySpawner extends AbstractIC {

	private EntityType entityType = EntityType.PIG;
	private EntityType entityRider;
	private boolean spawnRider = false;
	private int amount = 1;
	private Block center;

    public EntitySpawner(Server server, Sign sign) {
        super(server, sign);
	    load();
    }

	private void load() {
		entityType = EntityType.fromName(getSign().getLine(2).trim());
		String line = getSign().getLine(3).trim();
		// parse the amount or rider type
		try {
			amount = Integer.parseInt(line);
		} catch (NumberFormatException e) {
			entityRider = EntityType.fromName(line);
			spawnRider = entityRider != null;
		}
		// lets calculate the next possible block to spawn a mob
		center = SignUtil.getBackBlock(getSign().getBlock());
		while (center.getType() != Material.AIR && center.getRelative(BlockFace.UP).getType() != Material.AIR) {
			if (!(center.getY() < center.getWorld().getMaxHeight())) {
				break;
			}
			center = center.getRelative(BlockFace.UP);
		}
	}

    @Override
    public String getTitle() {

        return "Creature Spawner";
    }

    @Override
    public String getSignTitle() {

        return "CREATURE SPAWNER";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0)) {
            if (entityType != null) {
	            if (spawnRider) {
		            // spawn the entity plus rider
		            Entity entity = center.getWorld().spawnEntity(center.getLocation(), entityType);
		            entity.setPassenger(center.getWorld().spawnEntity(center.getLocation(), entityRider));
	            } else {
		            // spawn amount of mobs
		            for (int i = 0; i < amount; i++) {
			            center.getWorld().spawnEntity(center.getLocation(), entityType);
		            }
	            }
            }
        }
    }

    public static class Factory extends AbstractICFactory implements RestrictedIC {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(Sign sign) {

            return new EntitySpawner(getServer(), sign);
        }
    }
}
