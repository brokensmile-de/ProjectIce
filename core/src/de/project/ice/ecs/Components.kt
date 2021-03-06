package de.project.ice.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import de.project.ice.ecs.components.*

object Components {
    @JvmField val animation = ComponentMapper.getFor(AnimationComponent::class.java)
    @JvmField val camera = ComponentMapper.getFor(CameraComponent::class.java)
    @JvmField val texture = ComponentMapper.getFor(TextureComponent::class.java)
    @JvmField val transform = ComponentMapper.getFor(TransformComponent::class.java)
    @JvmField val disabled = ComponentMapper.getFor(DisabledComponent::class.java)
    @JvmField val script = ComponentMapper.getFor(ScriptComponent::class.java)
    @JvmField val control = ComponentMapper.getFor(ControlComponent::class.java)
    @JvmField val move = ComponentMapper.getFor(MoveComponent::class.java)
    @JvmField val breath = ComponentMapper.getFor(BreathComponent::class.java)
    @JvmField val name = ComponentMapper.getFor(NameComponent::class.java)
    @JvmField val hotspot = ComponentMapper.getFor(HotspotComponent::class.java)
    @JvmField val walkarea = ComponentMapper.getFor(WalkAreaComponent::class.java)
    @JvmField val use = ComponentMapper.getFor(UseComponent::class.java)
    @JvmField val pathplanning = ComponentMapper.getFor(PathPlanningComponent::class.java)
    @JvmField val walking = ComponentMapper.getFor(WalkingComponent::class.java)
    @JvmField val distanceScale = ComponentMapper.getFor(DistanceScaleComponent::class.java)
    @JvmField val timeout = ComponentMapper.getFor(TimeoutComponent::class.java)
    @JvmField val blend = ComponentMapper.getFor(BlendComponent::class.java)
    @JvmField val speak = ComponentMapper.getFor(SpeakComponent::class.java)
}

fun Entity.hasComponent(mapper: ComponentMapper<out IceComponent>): Boolean = mapper.has(this)

fun <T : IceComponent> Entity.getComponents(mapper: ComponentMapper<T>): T = mapper.get(this) ?: throw RuntimeException()
fun <T : IceComponent, U : IceComponent> Entity.getComponents(mapper1: ComponentMapper<T>, mapper2: ComponentMapper<U>): Pair<T, U>
        = Pair(mapper1.get(this) ?: throw RuntimeException(), mapper2.get(this) ?: throw RuntimeException())
fun <T : IceComponent, U : IceComponent, V: IceComponent> Entity.getComponents(mapper1: ComponentMapper<T>, mapper2: ComponentMapper<U>, mapper3: ComponentMapper<V>): Triple<T, U, V>
        = Triple(mapper1.get(this) ?: throw RuntimeException(), mapper2.get(this) ?: throw RuntimeException(), mapper3.get(this) ?: throw RuntimeException())
