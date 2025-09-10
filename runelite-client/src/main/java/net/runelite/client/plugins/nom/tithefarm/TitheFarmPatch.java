package net.runelite.client.plugins.nom.tithefarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.GameObject;

@RequiredArgsConstructor
@Getter
public class TitheFarmPatch
{
    private final GameObject gameObject;

    @Setter
    private State state = State.EMPTY;

    public enum State
    {
        EMPTY,
        SEEDLING,
        MIDLING,
        GROWN,
        HARVESTABLE,
        WATERED,
        DISEASED
    }
}