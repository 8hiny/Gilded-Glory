package shiny.gildedglory.common.item.custom;

import net.minecraft.item.Item;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class HiddenItem extends Item {

    public HiddenItem() {
        super(new Settings());
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return false;
    }
}
