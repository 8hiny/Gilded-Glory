package shiny.gildedglory.client.pose;

public interface ArmPose {

    /**
     * An empty ArmPose. Used to revert to vanilla pose logic.
     */
    public static final ArmPose USE_VANILLA = new ArmPose() {
        @Override
        public boolean twoHanded() {
            return false;
        }

        @Override
        public Value value() {
            return Value.EMPTY;
        }
    };

    boolean twoHanded();
    Value value();

    enum Value {
        VANILLA,
        CUSTOM,
        EMPTY
    }
}
