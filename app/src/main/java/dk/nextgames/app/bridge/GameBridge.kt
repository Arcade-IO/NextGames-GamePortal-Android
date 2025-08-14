package dk.nextgames.app.ui.gameinterface

import android.webkit.JavascriptInterface
import dk.nextgames.app.viewModel.GameInterfaceViewModel

class GameBridge(
    private val vm: GameInterfaceViewModel,
    private val gameId:    String,
    private val gameTitle: String
) {
    /** JS → Kotlin : henter brugeren */
    @JavascriptInterface
    fun getUserJson(): String = vm.userJson

    /** JS → Kotlin : læg score i DB */
    @JavascriptInterface
    fun submitHighScore(rawScore: Int) {
        vm.submitHighScore(rawScore, gameId, gameTitle)
    }
}
