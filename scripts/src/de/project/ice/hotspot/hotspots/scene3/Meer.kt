package de.project.ice.hotspot.hotspots.scene3

import de.project.ice.IceGame
import de.project.ice.hotspot.Use


class Meer : Use.Adapter() {
    override fun look(game: IceGame, hotspotId: String) {
        game.showAndiMessages("s3_ocean_desc");
    }
}