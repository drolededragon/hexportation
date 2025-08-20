package dev.kineticcat.hexportation.fabric.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import dev.kineticcat.hexportation.api.transfer.EnergyStorage;

/**
 * Fabric implementation that delegates to actual Team Reborn Energy EnergyStorage.
 */
public class EnergyStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    public static final EnergyStorage.SidedStorage INSTANCE = new EnergyStorage.SidedStorage() {
        @Override
        public EnergyStorage find(ServerLevel level, BlockPos pos, Direction direction) {
            team.reborn.energy.api.EnergyStorage teamRebornStorage = 
                team.reborn.energy.api.EnergyStorage.SIDED.find(level, pos, direction);
            
            if (teamRebornStorage == null) {
                return null;
            }
            
            return new FabricEnergyWrapper(teamRebornStorage);
        }
    };
    
    /**
     * Wrapper class that extends our EnergyStorage abstract class using Team Reborn Energy.
     */
    public static class FabricEnergyWrapper extends EnergyStorage {
        private final team.reborn.energy.api.EnergyStorage teamRebornStorage;
        
        public FabricEnergyWrapper(team.reborn.energy.api.EnergyStorage teamRebornStorage) {
            this.teamRebornStorage = teamRebornStorage;
        }
        
        @Override
        public long getAmount() {
            return teamRebornStorage.getAmount();
        }
        
        @Override
        public long getCapacity() {
            return teamRebornStorage.getCapacity();
        }
        
        public team.reborn.energy.api.EnergyStorage getTeamRebornStorage() {
            return teamRebornStorage;
        }
    }
}