package shiny.gildedglory.client.use_action;

public interface BaseUseAction {

    /**
     * An empty UseAction. Used to revert to vanilla logic.
     */
    public static final BaseUseAction USE_VANILLA = new BaseUseAction() {
        @Override
        public Value value() {
            return Value.EMPTY;
        }
    };

    Value value();

    enum Value {
        VANILLA,
        CUSTOM,
        EMPTY
    }
}
