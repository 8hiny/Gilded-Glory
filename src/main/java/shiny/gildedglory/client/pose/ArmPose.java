package shiny.gildedglory.client.pose;

public interface ArmPose {

    boolean twoHanded();
    Value value();

    enum Value {
        VANILLA,
        CUSTOM
    }
}
