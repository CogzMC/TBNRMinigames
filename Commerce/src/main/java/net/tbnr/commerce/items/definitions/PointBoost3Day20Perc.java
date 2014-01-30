package net.tbnr.commerce.items.definitions;

import net.tbnr.commerce.items.CommerceItemAPI;
import net.tbnr.commerce.items.CommerceItemMeta;
import net.tbnr.commerce.items.Tier;
import net.tbnr.gearz.GearzException;
import net.tbnr.gearz.player.GearzPlayer;

@CommerceItemMeta(
        humanName = "20% Point Boost (3 Days)",
        key = "3_20_point_boost",
        tier = Tier.Standard
)
public final class PointBoost3Day20Perc extends PointBoost{
    public PointBoost3Day20Perc(GearzPlayer player, CommerceItemAPI api) throws GearzException {
        super(player, api);
    }

    @Override
    public int percentageBoost() {
        return 20;
    }

    @Override
    public int daysLength() {
        return 3;
    }
}
