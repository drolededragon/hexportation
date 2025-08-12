package dev.kineticcat.hexportation.fabric.api.casting.iota;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.HexUtils;
import kotlin.Pair;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.Objects;

public class ConduitIota extends Iota {

    public record Conduit(BlockPos source, Direction sourceDir, BlockPos sink, Direction sinkDir) {
        private static final String SOURCE_TAG = "Source";
        private static final String SOURCE_DIR_TAG = "SourceDir";
        private static final String SINK_TAG = "Sink";
        private static final String SINK_DIR_TAG = "SinkDir";
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Conduit conduit = (Conduit) o;
            return Objects.equals(source, conduit.source)
                    && sourceDir == conduit.sourceDir
                    && Objects.equals(sink, conduit.sink)
                    && sinkDir == conduit.sinkDir;
        }
        private static CompoundTag serialisePos(BlockPos pos) {
            CompoundTag ctag = new CompoundTag();
            ctag.putInt("X", pos.getX());
            ctag.putInt("Y", pos.getY());
            ctag.putInt("Z", pos.getZ());
            return ctag;
        }
        public CompoundTag serialise() {
            CompoundTag ctag = new CompoundTag();
            ctag.put(SOURCE_TAG, serialisePos(source));
            ctag.putString(SOURCE_DIR_TAG, sourceDir.getSerializedName());
            ctag.put(SINK_TAG, serialisePos(sink));
            ctag.putString(SINK_DIR_TAG, sinkDir.getSerializedName());
            return ctag;
        }
        private static BlockPos deserialisePos(CompoundTag ctag) {
            return new BlockPos(
                    ctag.getInt("X"),
                    ctag.getInt("Y"),
                    ctag.getInt("Z")
            );
        }
        public Conduit(CompoundTag ctag) {
            this(
                    deserialisePos((CompoundTag) ctag.get(SOURCE_TAG)),
                    Direction.byName(ctag.getString(SOURCE_DIR_TAG)),
                    deserialisePos((CompoundTag) ctag.get(SINK_TAG)),
                    Direction.byName(ctag.getString(SINK_DIR_TAG))
            );
        }
        public ConduitIota asIota() { return new ConduitIota(this); }

        public Pair<Storage<ItemVariant>, Storage<ItemVariant>> getItemStoragesOrNull(ServerLevel sLevel) {
            Storage<ItemVariant> sourceStorage = ItemStorage.SIDED.find(sLevel, source, sourceDir);
            Storage<ItemVariant> sinkStorage = ItemStorage.SIDED.find(sLevel, sink, sinkDir);
            return new Pair<>(sourceStorage, sinkStorage);
        }
        public Pair<Storage<FluidVariant>, Storage<FluidVariant>> getFluidStoragesOrNull(ServerLevel sLevel) {
            Storage<FluidVariant> sourceStorage = FluidStorage.SIDED.find(sLevel, source, sourceDir);
            Storage<FluidVariant> sinkStorage = FluidStorage.SIDED.find(sLevel, sink, sinkDir);
            return new Pair<>(sourceStorage, sinkStorage);
        }
        public Pair<EnergyStorage, EnergyStorage> getEnergyStoragesOrNull(ServerLevel sLevel) {
            EnergyStorage sourceStorage = EnergyStorage.SIDED.find(sLevel, source, sourceDir);
            EnergyStorage sinkStorage = EnergyStorage.SIDED.find(sLevel, sink, sinkDir);
            if (sourceStorage != null && sinkStorage != null) return new Pair<>(sourceStorage, sinkStorage);
            else return null;
        }

    }

    public ConduitIota(BlockPos source, Direction sourceDir, BlockPos sink, Direction sinkDir) {
        super(HexportationIotaTypes.CONDUIT, new Conduit(source, sourceDir, sink, sinkDir));
    }
    public ConduitIota(Conduit conduit) {
        super(HexportationIotaTypes.CONDUIT, conduit);
    }
    public Conduit getConduit() {
        return (Conduit) payload;
    }
    @Override
    public boolean isTruthy() {
        return true;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that)
                && that instanceof ConduitIota iota
                && tolerates(getConduit(), iota.getConduit());
    }

    public static boolean tolerates(Conduit A, Conduit B) {
        return A.equals(B);
    }

    @Override
    public @NotNull Tag serialize() {
        return getConduit().serialise();
    }
    public static IotaType<ConduitIota> TYPE = new IotaType<ConduitIota>() {
        @Override
        public ConduitIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
            return ConduitIota.deserialize(tag);
        }

        @Override
        public Component display(Tag tag) {
            return ConduitIota.display(ConduitIota.deserialize(tag).getConduit());
        }

        @Override
        public int color() {
            return 16755200;
        }
    };

    public static ConduitIota deserialize(Tag tag) {
        CompoundTag ctag = HexUtils.downcast(tag, CompoundTag.TYPE);
        return new Conduit((CompoundTag) tag).asIota();
    }

    public static Component display(Conduit conduit) {
        return Component.literal("(%d, %d, %d) [%s] -> (%d, %d, %d) [%s]".formatted(
                conduit.source.getX(),
                conduit.source.getY(),
                conduit.source.getZ(),
                conduit.sourceDir,
                conduit.sink.getX(),
                conduit.sink.getY(),
                conduit.sink.getZ(),
                conduit.sinkDir
        )).withStyle(ChatFormatting.GOLD);
    }

    public List<Iota> asActionResult() { return List.of(this); }
}
