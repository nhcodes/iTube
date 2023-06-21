package codes.nh.itube.backend.mediastore

import android.os.Environment
import java.io.File

class MediaStoreDirectory(
    typeDirectoryName: String,
    appDirectoryName: String
) {

    //api >= 29
    val relativeDirectoryString = "$typeDirectoryName/$appDirectoryName"

    //api < 29
    val fullDirectoryFile = File(
        Environment.getExternalStoragePublicDirectory(typeDirectoryName),
        appDirectoryName
    )

}