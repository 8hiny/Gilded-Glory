package shiny.gildedglory.common.item.compat;

import net.minecraft.item.Item;
import net.minecraft.resource.featuretoggle.FeatureSet;
import shiny.gildedglory.GildedGlory;

public class CompatItem extends Item {

    public CompatItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return GildedGlory.FARMERS_DELIGHT_INSTALLED;
    }
}
