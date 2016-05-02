package de.project.ice.hotspot.hotspots.scene1pan

import de.project.ice.IceGame
import de.project.ice.hotspot.Use


class KaiStatue : Use.Adapter(){

    override fun use(game: IceGame, hotspotId: String) {

        game.showAndiMessages("s1_panorama_kai_statue_use")
    }

    override fun look(game: IceGame, hotspotId: String) {
        game.showAndiMessages("s1_panorama_kai_statue_desc")
    }
}