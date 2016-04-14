package de.project.ice.hotspot.hotspots.scene2

import de.project.ice.IceGame
import de.project.ice.hotspot.Use


class Fluss : Use.Adapter() {
    override fun look(game: IceGame, hotspotId: String) {
        game.showMessages("s2_river_desc");
    }
}