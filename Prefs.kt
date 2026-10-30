
package com.oysha.tv
import android.content.Context
class Prefs(ctx: Context) {
    private val sp = ctx.getSharedPreferences("oysha_tv", Context.MODE_PRIVATE)
    fun savePlaylist(link: String) {
        val set = sp.getStringSet("links", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        set.add(link)
        sp.edit().putStringSet("links", set).apply()
    }
    fun getPlaylists(): List<String> = sp.getStringSet("links", emptySet())?.toList() ?: emptyList()
}
