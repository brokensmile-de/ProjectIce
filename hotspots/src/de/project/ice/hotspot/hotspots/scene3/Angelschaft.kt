package de.project.ice.hotspot.hotspots.scene3

import de.project.ice.IceGame
import de.project.ice.hotspot.Use


class Angelschaft : Use.Take("Angelschaft","Angelschaft") {
    override fun look(game: IceGame) {
        game.showMessages("s3_shaft_desc");
    }

    override fun take(game: IceGame) {
        super.take(game)
    }
}