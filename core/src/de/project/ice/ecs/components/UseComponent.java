package de.project.ice.ecs.components;

import com.badlogic.ashley.core.Entity;
import de.project.ice.inventory.Inventory;
import de.project.ice.screens.CursorScreen;

public class UseComponent implements IceComponent
{
    public Entity target = null;
    public CursorScreen.Cursor cursor = CursorScreen.Cursor.None;
    public Inventory.Item item = null;

    @Override
    public void reset()
    {
        target = null;
        item = null;
        cursor = CursorScreen.Cursor.None;
    }
}