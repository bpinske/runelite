package net.runelite.client.plugins.nom.combatutility;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@AllArgsConstructor
@Getter
public class SquareToDraw
{
    private final Rectangle bounds;
    private final int size;
    private final Color color;
}