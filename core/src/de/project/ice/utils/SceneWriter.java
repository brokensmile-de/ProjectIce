package de.project.ice.utils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.XmlWriter;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import de.project.ice.ecs.IceEngine;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class SceneWriter {
    public static void serializeScene(@NotNull IceEngine engine, @NotNull XmlWriter xml) throws IOException {
        xml.element("scene").element("entities");

        for (Entity entity : engine.getEntities()) {
            xml.element("entity");
            xml.element("components");
            for (Component component : entity.getComponents()) {
                xml.element(component.getClass().getSimpleName());
                for (Field f : ClassReflection.getFields(component.getClass())) {
                    if (f.isFinal())
                        continue;

                    xml.element(f.getName());
                    try {
                        serializeObject(xml, f.get(component));
                    } catch (ReflectionException e) {
                        e.printStackTrace();
                    }
                    xml.pop();
                }
                xml.pop();
            }
            xml.pop();
            xml.pop();
        }

        xml.pop();
        xml.pop();
        xml.close();
    }

    private static void serializeObject(XmlWriter xml, Object o) throws IOException {
        Class type = o.getClass();
        xml.attribute("type", type.getSimpleName());
        if (Float.class.equals(type) || type == float.class ||
                Double.class.equals(type) || type == double.class ||
                Short.class.equals(type) || type == short.class ||
                Integer.class.equals(type) || type == int.class ||
                Long.class.equals(type) || type == long.class ||
                String.class.equals(type)) {
            xml.text(o);
        } else if (Vector2.class.equals(type)) {
            Vector2 vec = (Vector2) o;
            xml.attribute("x", vec.x);
            xml.attribute("y", vec.y);
        } else if (Vector3.class.equals(type)) {
            Vector3 vec = (Vector3) o;
            xml.attribute("x", vec.x);
            xml.attribute("y", vec.y);
            xml.attribute("z", vec.z);
        } else if (Assets.AnimationHolder.class.equals(type)) {
            Assets.AnimationHolder holder = (Assets.AnimationHolder) o;
            Animation animation = holder.data;
            xml.attribute("frameDuration", animation.getFrameDuration());
            xml.attribute("mode", animation.getPlayMode());
            xml.text(holder.name);
        } else if (Assets.TextureRegionHolder.class.equals(type)) {
            Assets.TextureRegionHolder holder = (Assets.TextureRegionHolder) o;
            xml.text(holder.name);
        } else if (IntMap.class.equals(type)) {
            for (IntMap.Entry<Object> entry : (IntMap<Object>) o) {
                xml.attribute("type", "IntMap");
                if (entry.value != null) {
                    xml.attribute("subType", entry.value.getClass().getSimpleName());
                    break;
                }
            }
            for (IntMap.Entry<Object> entry : (IntMap<Object>) o) {
                xml.element("_" + entry.key);
                serializeObject(xml, entry.value);
                xml.pop();
            }
        } else if (OrthographicCamera.class.equals(type)) {
            OrthographicCamera cam = (OrthographicCamera) o;
            xml.attribute("viewportWidth", cam.viewportWidth);
            xml.attribute("viewportHeight", cam.viewportHeight);
            xml.element("pos");
            serializeObject(xml, cam.position);
            xml.pop();
        } else {
            xml.attribute("type", "null");
        }
    }
}
