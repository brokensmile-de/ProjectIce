package de.project.ice.hotspot.hotspots.scene2

import de.project.ice.IceGame
import de.project.ice.hotspot.Use


class Fluss_gefroren : Use.Adapter() {
    override fun look(game: IceGame) {
        game.showMessages("s2_river_frozen_desc");
    }
}