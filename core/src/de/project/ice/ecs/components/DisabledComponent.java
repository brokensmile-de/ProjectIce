package de.project.ice.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class DisabledComponent extends Component implements Pool.Poolable
{
    @Override
    public void reset()
    {

    }
}
