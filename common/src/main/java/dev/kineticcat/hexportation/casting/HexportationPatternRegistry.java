package dev.kineticcat.hexportation.fabric.casting;


import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.castables.OperationAction;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import dev.kineticcat.hexportation.Hexportation;
import dev.kineticcat.hexportation.fabric.api.casting.iota.StorageViewIota;
import dev.kineticcat.hexportation.fabric.casting.actions.*;
import kotlin.jvm.functions.Function1;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ram.talia.moreiotas.api.casting.iota.StringIota;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class HexportationPatternRegistry {
    public static final Logger LOGGER = LogManager.getLogger(Hexportation.MOD_ID);
    private static final Map<ResourceLocation, ActionRegistryEntry> PATTERNS = new LinkedHashMap<>();

    public static final HexPattern MAKE_CONDUIT = make("eadewewedaedwqeadewewedaedww", HexDir.NORTH_EAST, "make_conduit",
            OpMakeConduit.INSTANCE);
    public static final HexPattern SEND_ITEM = make("qadeeedewe", HexDir.SOUTH_EAST, "send_item",
            OpSendThing.INSTANCE);
    public static final HexPattern GET_SOURCE_INV = make("eeeeedwwadeeede", HexDir.NORTH_EAST, "get_source_inv",
            new OpGetInvData(false));
    public static final HexPattern GET_SINK_INV = make("deeedeqeeeeedw", HexDir.NORTH_WEST, "get_sink_inv",
            new OpGetInvData(true));
    public static final HexPattern SEND_ITEM_FILT = make("deeedewadeeede", HexDir.NORTH_WEST, "send_item_filt",
            OpSendThingFiltered.INSTANCE);
    public static final HexPattern GET_VIEW_AMOUNT = make("adeeedewq", HexDir.EAST, "get_view_amount",
            new OpGetViewData(view -> new DoubleIota(view.amount().doubleValue())));
    public static final HexPattern GET_VIEW_CAPACITY = make("adeeedeww", HexDir.EAST, "get_view_capacity",
            new OpGetViewData(view -> new DoubleIota(view.capacity().doubleValue())));
    public static final HexPattern GET_VIEW_NAME = make("adeeedewe", HexDir.EAST, "get_view_name",
            new OpGetViewData(view -> StringIota.makeUnchecked(view.InternalName())));
    public static final HexPattern SLURP = make("aqaawdaqqqaqw", HexDir.SOUTH_EAST, "slurp",
            OpSlurp.INSTANCE);
    public static final HexPattern SPIT = make("daqqqaqeaqaa", HexDir.EAST, "spit",
            OpSpit.INSTANCE);
    public static final HexPattern TRANSPLACE = make("aqaawdaqqqaqeaqaa", HexDir.SOUTH_EAST, "transplace",
            OpTransplace.INSTANCE);
    
    public static void init() {
        for (Map.Entry<ResourceLocation, ActionRegistryEntry> entry : PATTERNS.entrySet()) {
            Registry.register(HexActions.REGISTRY, entry.getKey(), entry.getValue());
        }
    }

    private static HexPattern make(String signature, HexDir dir, String name, Action act ) {
        PATTERNS.put(
                new ResourceLocation(Hexportation.MOD_ID, name),
                new ActionRegistryEntry(HexPattern.fromAngles(signature, dir), act)
        );
        return HexPattern.fromAngles(signature, dir);
    }
    private static HexPattern make(String signature, HexDir dir, String name) {
        HexPattern pattern = HexPattern.fromAngles(signature, dir);
        PATTERNS.put(
                new ResourceLocation(Hexportation.MOD_ID, name),
                new ActionRegistryEntry(pattern, new OperationAction(pattern))
        );
        return pattern;
    }
}
