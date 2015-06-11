package de.project.ice.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import de.project.ice.ecs.Components;
import de.project.ice.ecs.Families;
import de.project.ice.ecs.components.AnimationComponent;
import de.project.ice.ecs.components.StateComponent;
import de.project.ice.ecs.components.TextureComponent;
import de.project.ice.utils.Assets;

public class AnimationSystem extends IteratingIceSystem {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_IDLE = 1;
    public static final int ANIMATION_WALK = 2;
    public static final int ANIMATION_USER = Integer.MAX_VALUE/2;

    public AnimationSystem () {
        super(Families.animated);

    }

    @Override
    public void processEntity (Entity entity, float deltaTime) {
        TextureComponent tex = Components.texture.get(entity);
        AnimationComponent anim = Components.animation.get(entity);
        StateComponent state = Components.state.get(entity);

        Assets.AnimationHolder animation = anim.animations.get(state.getAnimation());
        if (animation != null && animation.data != null) {
            tex.region.data = animation.data.getKeyFrame(state.time);
            tex.region.name = animation.name;
            //Gdx.app.log("Animation", "" + state.time);
        }
    }
}
