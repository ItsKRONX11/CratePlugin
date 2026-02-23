package com.hazebyte.crate.cratereloaded.component.model;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.cratereloaded.CorePlugin;
import com.hazebyte.crate.cratereloaded.model.CrateImpl;
import com.hazebyte.crate.cratereloaded.model.CrateV2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
@Builder
@AllArgsConstructor
public class CrateOpenRequest {
    @NonNull
    private Player player;

    @NonNull
    private Location location;

    // Legacy field (for backwards compatibility)
    private Crate crate;

    // V2 field (primary)
    private CrateV2 crateV2;

    /**
     * Gets CrateV2, converting from legacy Crate if needed.
     * Prioritizes crateV2 field if set.
     */
    public CrateV2 getCrateV2OrConvert() {
        if (crateV2 != null) {
            return crateV2;
        }
        if (crate != null) {
            CrateImpl impl = (CrateImpl) crate;
        }
        throw new IllegalStateException("CrateOpenRequest must have either crate or crateV2 set");
    }

    /**
     * Gets legacy Crate, converting from CrateV2 if needed.
     * Prioritizes crate field if set (for backwards compatibility).
     */
    public Crate getCrateOrConvert() {
        if (crate != null) {
            return crate;
        }
        if (crateV2 != null) {
            return CorePlugin.CRATE_MAPPER.toImplementation(crateV2);
        }
        throw new IllegalStateException("CrateOpenRequest must have either crate or crateV2 set");
    }
}
