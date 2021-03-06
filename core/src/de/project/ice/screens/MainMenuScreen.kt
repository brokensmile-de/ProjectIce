package de.project.ice.screens

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.ScreenViewport
import de.project.ice.IceGame
import de.project.ice.config.Config
import de.project.ice.utils.DefaultSkin
import de.project.ice.utils.DelegatingBlockingInputProcessor
import java.util.*


open class MainMenuScreen(game: IceGame) : BaseScreenAdapter(game) {
    private val stage = Stage()
    private val skin = DefaultSkin
    private val root = Table()
    private val menuLayout = VerticalGroup()
    private val buttons = HashMap<String,TextButton>()

    private val buttonGroups = HashMap<String, HorizontalGroup>()

    override val inputProcessor: InputProcessor = DelegatingBlockingInputProcessor(stage)


    init {

        if (Config.RENDER_DEBUG) {
            stage.setDebugAll(true)
        }
        stage.viewport = ScreenViewport()

        stage.addActor(root)

        root.setFillParent(true)

        val logo = Image(skin, "logo")
        root.add(logo)

        root.row()

        menuLayout.fill()
        menuLayout.space(5f)
        root.add(menuLayout)

        createMenuButton(ButtonProperties(BUTTON_NEW_GAME_ID), game.strings.get("menu_new_game"), object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.startNewGame()
                game.removeScreen(this@MainMenuScreen)
                return true
            }
        })
        createMenuButton(ButtonProperties(BUTTON_SAVE_LOAD_ID), game.strings.get("menu_load_game"), object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (game.hasSave()) {
                    game.load(blendScreen = true)
                    game.removeScreen(this@MainMenuScreen)
                } else {
                    game.showToastMessages("general_load_nosave")
                }
                return true
            }
        })
        createMenuButton(ButtonProperties(BUTTON_SETTINGS_ID), game.strings.get("menu_options"), object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                println("Settings")
                return true
            }
        })
        createMenuButton(ButtonProperties(BUTTON_EXIT_ID), game.strings.get("menu_quit"), object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.exit()
                return true
            }
        })
    }

    override val priority: Int
        get() = 100


    protected fun createMenuButtonAfter(properties: ButtonProperties, text: String, listener: InputListener?, idAfter: String) {


        buttonGroups[properties.id] = createButtonGroup().apply {

            val button = createButton(properties, text)
            if (listener != null) {
                button.addListener(listener)
            }


            this.addActor(button)
            buttons.put(properties.id, button)

            menuLayout.addActorAfter(buttonGroups[idAfter],this)
        }



    }

    protected fun insertMenuButton(properties: ButtonProperties, text: String, listener: InputListener?, idGroup: String) {

        buttonGroups[idGroup]?.apply {

            val button = createButton(properties, text)
            if (listener != null) {
                button.addListener(listener)
            }

            this.addActor(button)
            buttons.put(properties.id, button)
        }

    }

    protected fun createMenuButton(properties: ButtonProperties, text: String, listener: InputListener?) {


        buttonGroups[properties.id] = createButtonGroup().apply {

            val button = createButton(properties, text)
            if (listener != null) {
                button.addListener(listener)
            }


            this.addActor(button)
            buttons.put(properties.id, button)

            menuLayout.addActor(this)
        }

    }

    private fun createButtonGroup():HorizontalGroup{

        return HorizontalGroup().apply {

            this.space(5.0f)

            this.center()


        }

    }

    private fun createButton(properties: ButtonProperties, text: String) : TextButton {

        return object:TextButton(text, skin, properties.skin){
            override fun getPrefWidth(): Float = 303f
            override fun getPrefHeight(): Float = 124f
        }
    }


    protected fun getButton(id: String): TextButton {
        return buttons[id]!!
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun update(delta: Float) {
        stage.act(delta)
    }

    override fun render() {
        stage.viewport.apply()
        stage.draw()
    }

    override fun dispose() {
    }

    protected  data class ButtonProperties(val id: String, val skin: String = "menuButton")
    companion object {
        val BUTTON_NEW_GAME_ID = "BTN_NEW_GAME"
        val BUTTON_SAVE_LOAD_ID = "BTN_SAVE_LOAD"
        val BUTTON_EXIT_ID = "BTN_EXIT"
        val BUTTON_SETTINGS_ID = "BTN_SETTINGS"
    }
}
