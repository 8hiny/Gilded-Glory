package shiny.gildedglory.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface TimedComponent extends Component {

    void setDuration(int duration);
    int getDuration();
    void disable();
}
