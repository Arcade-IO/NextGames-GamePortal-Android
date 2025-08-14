package dk.nextgames.app.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dk.nextgames.app.ui.games.Game           // ← model
import dk.nextgames.app.ui.games.GamesPage
import dk.nextgames.app.ui.gameinterface.GameInterfacePage
import dk.nextgames.app.ui.leaderboard.LeaderboardPage
import dk.nextgames.app.ui.login.LoginScreen
import dk.nextgames.app.ui.navigation.NAVIGATIONPAGE
import dk.nextgames.app.ui.settings.SettingsPage
import dk.nextgames.app.ui.userpage.UserPage

object Routes {
    const val LOGIN          = "login"
    const val HUB            = "NavigationPage"
    const val USER           = "user"
    const val SETTINGS       = "settings"
    const val GAMES          = "games"

    const val GAME           = "game"        // ?url=&id=&title=
    const val LEADERBOARD    = "leaderboard"
}

@Composable
fun NextGamesNav() {

    val nav = rememberNavController()

    NavHost(nav, startDestination = Routes.LOGIN) {

        /* ---------- Login ---------- */
        composable(Routes.LOGIN) {
            LoginScreen {
                nav.navigate(Routes.HUB) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }

        /* ---------- Hub ---------- */
        composable(Routes.HUB) {
            NAVIGATIONPAGE(
                onNavigate = nav::navigate,
                onLogout   = {
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        /* ---------- Simple ---------- */
        composable(Routes.USER)      { UserPage(onBack = { nav.popBackStack() }) }
        composable(Routes.SETTINGS)  { SettingsPage(onBack = { nav.popBackStack() }) }
        composable(Routes.LEADERBOARD) {
            LeaderboardPage(onBack = { nav.popBackStack() })
        }

        /* ---------- Games ---------- */
        composable(Routes.GAMES) {
            GamesPage(
                onBack = { nav.popBackStack() },
                onGameClicked = { game: Game ->                     // ① matcher GamesPage
                    val encUrl   = Uri.encode(game.netlifyUrl)
                    val encTitle = Uri.encode(game.title)

                    nav.navigate(
                        "${Routes.GAME}?" +
                                "url=$encUrl&" +
                                "id=${game.id}&" +
                                "title=$encTitle"
                    )
                }
            )
        }

        /* ---------- WebView ---------- */
        composable(
            route =
                "${Routes.GAME}?url={url}&id={id}&title={title}",
            arguments = listOf(
                navArgument("url")   { type = NavType.StringType },
                navArgument("id")    { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { back ->
            val url   = back.arguments?.getString("url")   ?: ""
            val id    = back.arguments?.getString("id")    ?: ""
            val title = Uri.decode(back.arguments?.getString("title") ?: "")

            GameInterfacePage(
                encodedUrl = url,
                gameId     = id,
                gameTitle  = title,
                onBack     = { nav.popBackStack() }
            )
        }
    }
}
