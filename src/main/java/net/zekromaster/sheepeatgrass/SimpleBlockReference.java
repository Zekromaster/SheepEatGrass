package net.zekromaster.sheepeatgrass;

/**
 * Simple utility representation of a block id + meta.
 */
public record SimpleBlockReference(
    int id,
    int metadata
) {

    /**
     * For matching purposes, it will match any metadata.
     */
    public static int METADATA_WILDCARD = -1;

    /**
     * Check if the two blocks are to be considered equal, or one of the two is a super-set of the other.
     * Will ignore metadata if one of the two blocks has a metadata value of {@link SimpleBlockReference#METADATA_WILDCARD}.
     * @param block The block to compare to.
     * @return Whether the two blocks are to be considered equal, or one of the two is a super-set of the other.
     *
     */
    public boolean matches(SimpleBlockReference block) {
        return
            this.id == block.id && (
                this.metadata == METADATA_WILDCARD
                    || block.metadata == METADATA_WILDCARD
                    || this.metadata == block.metadata
            );
    }

}
