package by.matveev.rorty.editor

import by.matveev.rorty.Cfg
import by.matveev.rorty.core.AbstractScreen
import by.matveev.rorty.core.Light
import by.matveev.rorty.core.WorldUpdater
import by.matveev.rorty.entities.Box
import by.matveev.rorty.entities.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.MouseJoint
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef
import com.badlogic.gdx.utils.SnapshotArray
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class EditorScreen : AbstractScreen(), InputProcessor {
    lateinit var world: World
    lateinit var worldUpdater: WorldUpdater
    lateinit var worldViewport: Viewport
    lateinit var debugRenderer: ShapeRenderer

    private val entities = SnapshotArray<Entity>()

    private var mouseJoint: MouseJoint? = null
    private var targetBody: Body? = null
    private var groundBody: Body? = null

    private val point = Vector3()
    private val target = Vector2()

    private val callback: QueryCallback = QueryCallback { fixture ->
        if (fixture.testPoint(point.x, point.y)) {
            targetBody = fixture.body
            false
        } else {
            true
        }
    }

    override fun show() {
        world = World(Vector2(0f, /*-9.8f*/0f), true)
        worldViewport = FitViewport(Cfg.toMeters(Cfg.WIDTH.toFloat()), Cfg.toMeters(Cfg.HEIGHT.toFloat()))
        debugRenderer = ShapeRenderer()
        worldUpdater = WorldUpdater(world)

        val bodyDef = BodyDef()
        groundBody = world.createBody(bodyDef)

        val x = Cfg.toMeters(200f)
        val y = Cfg.toMeters(200f)

        val w = Cfg.toMeters(110f)
        val h = Cfg.toMeters(110f)

        val hw = w * 0.5f
        val hh = h * 0.5f

        val shape = CircleShape()
        shape.radius = w * 0.5f

        val def = BodyDef()
        def.type = BodyDef.BodyType.DynamicBody
        def.position.set(x + hw, y + hh)

        val body = world.createBody(def)

        val fix = FixtureDef()
        fix.shape = shape
        body.createFixture(fix)

        shape.dispose()

        val box = Box(body, x, y, w, h)
        box.isEnabled = false
        entities.add(box)

        addLights(box.createLights())


        val global = Light(Light.Type.SOFT, Color.WHITE)
        global.color.a = 0.05f
        global.width = 600f
        global.height = 600f
        addLight(global)

        Gdx.input.inputProcessor = this
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)

        worldViewport.update(Cfg.toMeters(width.toFloat()).toInt(), Cfg.toMeters(height.toFloat()).toInt(), true)
    }

    override fun update(delta: Float) {
        worldUpdater.update(delta)

        for (e in entities) {
            e.update(delta)
        }

    }

    override fun draw(batch: SpriteBatch, camera: OrthographicCamera) {
        val box2dCamera = worldViewport!!.camera as OrthographicCamera
        batch.begin()
        batch.projectionMatrix = box2dCamera.combined
        for (e in entities) {
            e.draw(batch, box2dCamera)
        }
        batch.end()
    }

    override fun postDraw(batch: SpriteBatch, camera: OrthographicCamera) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        for (e in entities) {
            e.postDraw(batch, camera)
        }
        batch.end()
    }

    override fun keyDown(i: Int): Boolean {
        return false
    }

    override fun keyUp(i: Int): Boolean {
        return false
    }

    override fun keyTyped(c: Char): Boolean {
        return false
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, newParam: Int): Boolean {
        point.set(x.toFloat(), y.toFloat(), 0f)
        worldViewport.camera.unproject(point)

        targetBody = null
        world.QueryAABB(callback, point.x - 0.1f, point.y - 0.1f, point.x + 0.1f, point.y + 0.1f)

        if (targetBody === groundBody) targetBody = null

        if (targetBody != null && targetBody!!.type == BodyDef.BodyType.KinematicBody) return false

        if (targetBody != null) {
            val def = MouseJointDef()
            def.bodyA = groundBody
            def.bodyB = targetBody
            def.collideConnected = true
            def.target.set(point.x, point.y)
            def.dampingRatio = 1f
            def.maxForce = 1000.0f * targetBody!!.mass

            mouseJoint = world.createJoint(def) as MouseJoint
            targetBody!!.isAwake = true
        }

        return false
    }

    override fun touchUp(x: Int, i1: Int, i2: Int, i3: Int): Boolean {
        mouseJoint?.let {
            world.destroyJoint(mouseJoint)
            mouseJoint = null
        }
        return false
    }

    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        if (mouseJoint != null) {
            worldViewport.camera.unproject(point.set(x.toFloat(), y.toFloat(), 0f))
            mouseJoint!!.target = target.set(point.x, point.y)
        }
        return false
    }

    override fun mouseMoved(i: Int, i1: Int): Boolean {
        return false
    }

    override fun scrolled(i: Int): Boolean {
        return false
    }
}