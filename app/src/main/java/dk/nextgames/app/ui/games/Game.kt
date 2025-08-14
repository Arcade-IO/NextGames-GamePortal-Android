// app/src/main/java/dk/nextgames/app/data/Game.kt
package dk.nextgames.app.ui.games

data class Game(
    val id:        String = "",
    val title:     String = "",
    val description: String = "",
    val imageUrl:  String = "",
    val netlifyUrl:String = "",
    val platform:  String = "",       // "Android", "Web" … 
    val createdAt: String = ""
)
