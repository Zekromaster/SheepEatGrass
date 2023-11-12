package net.zekromaster.sheepeatgrass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A registry keeping track of what items can be eaten by sheep and what they turn into.
 * It doesn't have a private constructor, if someone wants to make a new instance of this,
 * i.e. for their own mob's grass-eating behaviour, they can do so.
 *
 * @author Zekromaster
 */
public class SheepEatingRegistry {

    /**
     * The singleton instance of this registry.
     */
    public static final SheepEatingRegistry INSTANCE = new SheepEatingRegistry();

    private final Map<SimpleBlockReference, Value> registry = new HashMap<>();


    /**
     * Adds a new entry to the registry.
     * @param block The block that can be eaten. If its metadata is {@link SimpleBlockReference#METADATA_WILDCARD}, it will match any
     *              block with the same id when calling {@link SheepEatingRegistry#get(EatingLocation, SimpleBlockReference)}.
     * @param location Where the block has to be to be eaten (same block as the sheep, or underneath it).
     * @param output The block that the eaten block turns into. Its metadata CANNOT BE {@link SimpleBlockReference#METADATA_WILDCARD}.
     */
    public void add(SimpleBlockReference block, EatingLocation location, SimpleBlockReference output) throws IllegalArgumentException {
        if (output.metadata() == SimpleBlockReference.METADATA_WILDCARD) {
            throw new IllegalArgumentException("Output metadata can't be wildcard");
        }

        this.registry.put(block, new Value(location, output));
    }

    /**
     * Gets the block that the given block turns into when eaten at the given location.
     * @param location Where the block has to be to be eaten (same block as the sheep, or underneath it).
     * @param block The block that can be eaten. Its metadata CANNOT BE {@link SimpleBlockReference#METADATA_WILDCARD}.
     * @return The block that the eaten block turns into, or {@link Optional#empty()} if the given block can't be eaten
     *         at the given location.
     */
    public Optional<SimpleBlockReference> get(EatingLocation location, SimpleBlockReference block) throws IllegalArgumentException {
        if (block.metadata() == SimpleBlockReference.METADATA_WILDCARD) {
            throw new IllegalArgumentException("Output metadata can't be wildcard");
        }

        return this.registry
            .entrySet()
            .stream()
            .filter((Map.Entry<SimpleBlockReference, Value> entry) -> entry.getKey().matches(block))
            .findFirst()
            .map(Map.Entry::getValue)
            .filter(v -> v.location() == location)
            .map(Value::block);
    }

    private record Value(
        EatingLocation location,
        SimpleBlockReference block
    ) { }

    /**
     * Represents the location of a block being eaten.
     */
    public enum EatingLocation {
        SAME_BLOCK,
        UNDERNEATH
    }
}
