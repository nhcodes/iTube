package codes.nh.itube.backend.library

import android.net.Uri
import org.json.JSONObject

class Song(
    val contentUri: Uri,
    val fileName: String
) {

    val artist: String?
    val title: String

    init {
        val parts = fileName.split(" - ", limit = 2)
        if (parts.size == 2) {
            artist = parts[0]
            title = parts[1]
        } else {
            artist = null
            title = fileName
        }
    }

    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("contentUri", contentUri.toString())
        json.put("fileName", fileName)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): Song {
            return Song(
                Uri.parse(json.getString("contentUri")),
                json.getString("fileName")
            )
        }
    }
}