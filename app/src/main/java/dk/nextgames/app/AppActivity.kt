package dk.nextgames.app         // samme root-package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dk.nextgames.app.navigation.NextGamesNav
import dk.nextgames.app.ui.theme.BackgroundBox
import dk.nextgames.app.ui.theme.NextGamesTheme

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BackgroundBox {              // 1) fodrer LocalIsDarkBackground
                NextGamesTheme {         // 2) vælger farver ud fra value
                    NextGamesNav()       // 3) alt UI
                }
            }
        }
    }
}
