package shiny.gildedglory.common.item.compat;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.featuretoggle.FeatureSet;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.item.ConsumableItem;

public class CompatConsumableItem extends ConsumableItem {

    public CompatConsumableItem(Settings settings, int maxUseTime, Item resultItem) {
        super(settings, maxUseTime, resultItem);
    }

    public CompatConsumableItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return GildedGlory.FARMERS_DELIGHT_INSTALLED;
    }
}
