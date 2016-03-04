package de.project.ice.utils


import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntMap
import com.badlogic.gdx.utils.XmlReader
import com.badlogic.gdx.utils.XmlReader.Element
import com.badlogic.gdx.utils.reflect.ReflectionException
import de.project.ice.ecs.IceEngine
import de.project.ice.ecs.systems.RenderingSystem
import de.project.ice.pathlib.PathArea
import de.project.ice.pathlib.Shape
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField
import kotlin.reflect.memberProperties

object SceneLoader {
    @Throws(IOException::class, LoadException::class)
    fun loadScene(engine: IceEngine, `in`: InputStream): SceneProperties {
        return loadScene(engine, XmlReader().parse(`in`))
    }

    @Throws(IOException::class, LoadException::class)
    fun loadScene(engine: IceEngine, `in`: String): SceneProperties {
        return loadScene(engine, XmlReader().parse(`in`))
    }

    @Throws(IOException::class, LoadException::class)
    fun loadScene(engine: IceEngine, `in`: Reader): SceneProperties {
        return loadScene(engine, XmlReader().parse(`in`))
    }

    @Throws(IOException::class, LoadException::class)
    fun loadScene(engine: IceEngine, `in`: FileHandle): SceneProperties {
        return loadScene(engine, XmlReader().parse(`in`))
    }


    @Throws(LoadException::class)
    private fun loadScene(engine: IceEngine, scene: Element?): SceneProperties {
        val builder = ScenePropertiesBuilder().engine(engine)

        if (scene == null || scene.name != "scene") {
            throw LoadException("Invalid scene file (Not a scene file)")
        }

        val sceneName: String
        try {
            sceneName = scene.getAttribute("name")
            builder.name(sceneName)
        } catch (ignore: Exception) {
            throw LoadException("Invalid scene file (Missing scene name)")
        }

        val spritesheets = scene.getChildByName("spritesheets")
        if (spritesheets != null) {
            for (i in 0..spritesheets.childCount - 1) {
                val child = spritesheets.getChild(i)
                if (child.name == "spritesheet") {
                    builder.spritesheet(child.text)
                    Assets.loadAtlas(child.text)
                }
            }
            Assets.finishAll()
        }

        val entities = scene.getChildByName("entities") ?: throw LoadException("Invalid scene file (No entities entry)")

        for (i in 0..entities.childCount - 1) {
            val child = entities.getChild(i)
            if (child.name.endsWith("entity")) {
                engine.addEntity(loadEntity(engine, child))
            }
        }


        val sounds = scene.getChildByName("sounds")
        if (sounds != null) {
            for (i in 0..sounds.childCount - 1) {
                val child = sounds.getChild(i)
                if (child.name == "sound") {
                    builder.sound(child.text)
                    engine.soundSystem.loadSound(child.text)
                }
            }
        }

        val music = scene.getChildByName("music")
        if (music != null && music.text != null) {
            builder.music(music.text)
            engine.soundSystem.playMusic(music.text, true)
        }
        return builder.create()
    }

    @Throws(LoadException::class)
    private fun loadEntity(engine: IceEngine, element: Element): Entity {
        val components = element.getChildByName("components") ?: throw LoadException("Invalid scene file (Missing components entry for entity)")

        val entity = engine.createEntity()

        for (i in 0..components.childCount - 1) {
            entity.add(loadComponent(engine, components.getChild(i)))
        }

        return entity
    }

    @SuppressWarnings("unchecked")
    @Throws(LoadException::class)
    private fun loadComponent(engine: IceEngine, element: Element): Component {
        val componentClass: KClass<out Component>
        try {
            val javaClass = Class.forName("de.project.ice.ecs.components." + element.name) as Class<out Component>
            componentClass = javaClass.kotlin
        } catch (e: ReflectionException) {
            throw LoadException("Invalid scene file (Unknown Component: " + element.name + ")", e)
        }

        val component = engine.createComponent(componentClass.java)

        val properties = HashMap<String, KProperty<Any?>>()
        for (property in componentClass.memberProperties) {
            properties.put(property.name, property)
        }

        for (i in 0..element.childCount - 1) {
            val childElement = element.getChild(i)
            val type = childElement.getAttribute("type", "null")
            if (type.equals("null", ignoreCase = true)) {
                continue
            }


            if (!properties.containsKey(childElement.name)) {
                throw LoadException("Invalid scene file (Component ${element.name} does not have a ${childElement.name} field)")
            }

            val f = properties[childElement.name]?.javaField
                    ?: throw LoadException("Invalid scene file (Component ${element.name}: error accessing ${childElement.name} field)")

            try {
                f.isAccessible = true
                f.set(component, loadValue(type, childElement))
            } catch (e: ReflectionException) {
                throw LoadException("Invalid scene file (Couldn't set field ${childElement.name} of component ${element.name})", e)
            }

        }
        return component
    }

    @Throws(LoadException::class)
    private fun loadValue(type: String, element: Element): Any {
        if (type.equals("Float", ignoreCase = true)) {
            return loadFloat(element)
        } else if (type.equals("Integer", ignoreCase = true)) {
            return loadInt(element)
        } else if (type.equals("Boolean", ignoreCase = true)) {
            return loadBoolean(element)
        } else if (type.equals("String", ignoreCase = true)) {
            return if (element.text == null) "" else element.text.replace("&gt", ">").replace("&lt", "<").replace("&amp", "&")
        } else if (type.equals("OrthographicCamera", ignoreCase = true)) {
            return loadOrthographicCamera(element)
        } else if (type.equals("Vector2", ignoreCase = true)) {
            return loadVector2(element)
        } else if (type.equals("Vector3", ignoreCase = true)) {
            return loadVector3(element)
        } else if (type.equals("IntMap", ignoreCase = true)) {
            return loadIntMap(element)
        } else if (type.equals("TextureRegion", ignoreCase = true)) {
            return loadTextureRegion(element)
        } else if (type.equals("Animation", ignoreCase = true)) {
            return loadAnimation(element)
        } else if (type.equals("PathArea", ignoreCase = true)) {
            return loadPathArea(element)
        } else {
            throw LoadException("Invalid scene file (Unknown Component type: $type)")
        }
    }

    private fun loadBoolean(element: Element): Boolean {
        return element.text.trim { it <= ' ' } == "true"
    }

    private fun loadPathArea(element: Element): PathArea {
        val pathArea = PathArea()
        val shapeelement = element.getChildByName("Shape")
        pathArea.shape = loadShape(element.getChildByName("shape"))

        val holes = element.getChildByName("holes")
        for (i in 0..holes.childCount - 1) {
            pathArea.holes.add(loadShape(holes.getChild(i)))
        }
        return pathArea
    }


    private fun loadShape(element: Element): Shape {
        val shape = Shape()
        val vertices = element.getChildByName("vertices")
        for (i in 0..vertices.childCount - 1) {
            shape.vertices.add(loadVector2(vertices.getChild(i)))
        }

        shape.closed = element.getBoolean("closed")

        return shape
    }

    private fun loadFloat(element: Element): Float {
        return java.lang.Float.parseFloat(element.text)
    }


    private fun loadInt(element: Element): Int {
        return Integer.parseInt(element.text)
    }

    private fun loadTextureRegion(element: Element): Assets.Holder.TextureRegion {
        return Assets.findRegion(element.text)
    }

    private fun loadAnimation(element: Element): Assets.Holder.Animation {
        return Assets.createAnimation(element.text, element.getFloatAttribute("frameDuration", 0f), Animation.PlayMode.valueOf(element.getAttribute("mode", "NORMAL")))
    }

    @SuppressWarnings("unchecked")
    @Throws(LoadException::class)
    private fun loadIntMap(element: Element): IntMap<Any> {
        val subType = element.getAttribute("subType", "null")

        val map = IntMap<Any>()

        if ("null".equals(subType, ignoreCase = true)) {
            return map
        }

        for (i in 0..element.childCount - 1) {
            val childElement = element.getChild(i)
            val key = Integer.parseInt(childElement.name.substring(1))
            val value = loadValue(subType, childElement)
            map.put(key, value)
        }

        return map
    }

    @Throws(LoadException::class)
    private fun loadOrthographicCamera(element: Element): OrthographicCamera {
        val posElement = element.getChildByName("pos")
        val pos: Vector3
        if (posElement == null) {
            pos = Vector3(0f, 0f, 0f)
        } else {
            pos = loadVector3(posElement)
        }

        val camera = OrthographicCamera(RenderingSystem.FRUSTUM_WIDTH, RenderingSystem.FRUSTUM_HEIGHT)
        camera.position.set(pos)
        camera.viewportWidth = element.getFloatAttribute("viewportWidth", RenderingSystem.FRUSTUM_WIDTH)
        camera.viewportHeight = element.getFloatAttribute("viewportHeight", RenderingSystem.FRUSTUM_HEIGHT)
        return camera
    }

    private fun loadVector3(element: Element): Vector3 {
        return Vector3(element.getFloatAttribute("x", 0f), element.getFloatAttribute("y", 0f), element.getFloatAttribute("z", 0f))
    }

    private fun loadVector2(element: Element): Vector2 {
        return Vector2(element.getFloatAttribute("x", 0f), element.getFloatAttribute("y", 0f))
    }

    class LoadException : Exception {
        constructor() {
        }

        constructor(message: String) : super(message) {
        }

        constructor(message: String, cause: Throwable) : super(message, cause) {
        }

        constructor(cause: Throwable) : super(cause) {
        }
    }

    class SceneProperties internal constructor(private val name: String, private val spritesheets: Array<String>, private val sounds: Array<String>, private val music: String, private val onloadScript: String, private val engine: IceEngine) {

        fun name(): String {
            return name
        }

        fun spritesheets(): Array<String> {
            return spritesheets
        }

        fun sounds(): Array<String> {
            return sounds
        }

        fun music(): String {
            return music
        }

        fun onloadScript(): String {
            return onloadScript
        }

        fun engine(): IceEngine {
            return engine
        }
    }

    class ScenePropertiesBuilder {
        private var name = ""
        private var spritesheets = Array<String>()
        private var sounds = Array<String>()
        private var music = ""
        private var onloadScript = ""
        private var engine: IceEngine? = null

        fun name(name: String): ScenePropertiesBuilder {
            this.name = name
            return this
        }

        fun spritesheets(spritesheets: Array<String>): ScenePropertiesBuilder {
            this.spritesheets = spritesheets
            return this
        }

        fun sounds(sounds: Array<String>): ScenePropertiesBuilder {
            this.sounds = sounds
            return this
        }

        fun spritesheet(spritesheet: String): ScenePropertiesBuilder {
            this.spritesheets.add(spritesheet)
            return this
        }

        fun sound(sound: String): ScenePropertiesBuilder {
            this.sounds.add(sound)
            return this
        }

        fun music(music: String): ScenePropertiesBuilder {
            this.music = music
            return this
        }

        fun onloadScript(onloadScript: String): ScenePropertiesBuilder {
            this.onloadScript = onloadScript
            return this
        }

        fun engine(engine: IceEngine): ScenePropertiesBuilder {
            this.engine = engine
            return this
        }

        fun create(): SceneProperties {
            return SceneProperties(name, spritesheets, sounds, music, onloadScript, engine!!)
        }
    }
}