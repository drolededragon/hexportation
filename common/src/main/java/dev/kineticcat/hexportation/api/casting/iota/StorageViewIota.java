package dev.kineticcat.hexportation.fabric.api.casting.iota;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.NBTBuilder;
import dev.kineticcat.hexportation.fabric.api.Storage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import javax.print.attribute.standard.MediaSize;
import java.util.Objects;

public class StorageViewIota extends Iota {

    public record GenericStorageView(Storage.Modes mode, Long amount, Long capacity, String InternalName, Component DisplayName) {
        private static final String MODE_TAG = "Mode";
        private static final String AMOUNT_TAG = "Amount";
        private static final String CAPACITY_TAG = "Capacity";
        private static final String NAME_TAG = "Name";
        private static final String DISPLAY_TAG = "Display";
        public GenericStorageView() {this(null, null, null, null, null);}
        public GenericStorageView(StorageView<ItemVariant> view, Long kys) {
            this(
                    Storage.Modes.ITEM,
                    view.getAmount(),
                    view.getCapacity(),
                    view.getResource().getItem().toString(),
                    view.getResource().toStack().getDisplayName()
            );
        }
        public GenericStorageView(StorageView<FluidVariant> view, Double kys) {
            this(
                    Storage.Modes.FLUID,
                    view.getAmount(),
                    view.getCapacity(),
                    view.getResource().getFluid().toString(),
                    FluidVariantAttributes.getName(FluidVariant.of(view.getResource().getFluid(), view.getResource().getNbt()))
            );
        }
        public GenericStorageView(EnergyStorage view) {
            this(
                    Storage.Modes.ENERGY,
                    view.getAmount(),
                    view.getCapacity(),
                    "energy",
                    Component.translatable("hexportation.energyname")
            );
        }

        public CompoundTag serialise() {
            CompoundTag ctag = new CompoundTag();
            ctag.putString(MODE_TAG, mode.name());
            ctag.putLong(AMOUNT_TAG, amount);
            ctag.putLong(CAPACITY_TAG, capacity);
            ctag.putString(NAME_TAG, InternalName);
            ctag.put(DISPLAY_TAG, StringTag.valueOf(Component.Serializer.toJson(DisplayName)));
            return ctag;
        }
        public GenericStorageView(CompoundTag ctag) {
            this(
                    Storage.Modes.valueOf(ctag.getString(MODE_TAG)),
                    ctag.getLong(AMOUNT_TAG),
                    ctag.getLong(CAPACITY_TAG),
                    ctag.getString(NAME_TAG),
                    Component.Serializer.fromJson(ctag.getString(DISPLAY_TAG))
            );
        }
    }

    public StorageViewIota(GenericStorageView view) {
        super(HexportationIotaTypes.STORAGEVIEW, view);
    }
    public StorageViewIota(StorageView<ItemVariant> view, Long kys) {
        super(HexportationIotaTypes.STORAGEVIEW, new GenericStorageView(view, kys));
    }
    public StorageViewIota(StorageView<FluidVariant> view, Double kys) {
        super(HexportationIotaTypes.STORAGEVIEW, new GenericStorageView(view, kys));
    }
    public StorageViewIota(EnergyStorage view) {
        super(HexportationIotaTypes.STORAGEVIEW, new GenericStorageView(view));
    }
    public GenericStorageView getView() { return (GenericStorageView) payload;}

    @Override
    public boolean isTruthy() {
        return getView().mode != null;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that)
                && that instanceof StorageViewIota thon
                && Objects.equals(getView().InternalName, thon.getView().InternalName);
    }

    @Override
    public @NotNull Tag serialize() {
        return getView().serialise();
    }

    public static IotaType<StorageViewIota> TYPE = new IotaType<StorageViewIota>() {
        @Nullable
        @Override
        public StorageViewIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
            return new StorageViewIota(new GenericStorageView((CompoundTag) tag));
        }

        @Override
        public Component display(Tag tag) {
            GenericStorageView view = new GenericStorageView((CompoundTag) tag);
            MutableComponent out = ((MutableComponent) view.DisplayName);
            Long amt = view.amount;
            Long cap = view.capacity;
            // convert droplets to millibuckets
            if (view.mode == Storage.Modes.FLUID) {
                amt = amt/ FluidConstants.BUCKET * 1000;
                cap = cap/ FluidConstants.BUCKET * 1000;
            }
            out.append(" ").append(amt.toString()).append("/").append(cap.toString());
            switch (view.mode) {
                case ITEM -> {}
                case FLUID -> {
                    out.append(" mB");
                }
                case ENERGY -> {
                    out.append("E");
                }
            }
            return out.withStyle(ChatFormatting.LIGHT_PURPLE);
        }

        @Override
        public int color() {
            return 0;
        }
    };
}
