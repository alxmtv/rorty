package by.matveev.rorty.core

import com.badlogic.gdx.physics.box2d.World

class WorldUpdater(private val world: World) {
    private val timeStep = 1f / 60f;
    private val velocityIterations = 8
    private val positionIterations = 3
    private var accumulator: Float = 0f
    fun update(dt: Float) {
        val frameTime = Math.min(dt, 0.25f)
        accumulator += frameTime
        while (accumulator >= timeStep) {
            world.step(timeStep, velocityIterations, positionIterations)
            accumulator -= timeStep
        }
    }
}