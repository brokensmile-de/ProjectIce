/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.project.ice.ecs.systems;

/**
 * A simple {@link IceSystem} that does not run its update logic every call to {@link IceSystem#update(float)}, but after a
 * given interval. The actual logic should be placed in {@link IntervalIceSystem#updateInterval()}.
 *
 * @author David Saltares
 */
public abstract class IntervalIceSystem extends IceSystem
{
    private float interval;
    private float accumulator;

    /**
     * @param interval time in seconds between calls to {@link IntervalIceSystem#updateInterval()}.
     */
    public IntervalIceSystem(float interval)
    {
        this(interval, 0);
    }

    /**
     * @param interval time in seconds between calls to {@link IntervalIceSystem#updateInterval()}.
     * @param priority
     */
    public IntervalIceSystem(float interval, int priority)
    {
        super(priority);
        this.interval = interval;
        this.accumulator = 0;
    }

    @Override
    public void update(float deltaTime)
    {
        accumulator += deltaTime;

        while (accumulator >= interval)
        {
            accumulator -= interval;
            updateInterval();
        }
    }

    /**
     * The processing logic of the system should be placed here.
     */
    protected abstract void updateInterval();
}
