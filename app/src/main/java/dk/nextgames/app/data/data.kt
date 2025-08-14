package dk.nextgames.app.data

data class UserProfile(
    val uid:   String = "",
    val name:  String = "",
    val email: String = ""
)


data class GameStub(
    val id: String = "",
    val name: String = ""
)


data class HighscoreEntry(
    val displayName: String = "",
    val gameTitle:   String = "",
    val score:       Int    = 0
)
