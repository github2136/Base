package com.github2136.basemvvm

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.NonNull
import com.orhanobut.logger.LogStrategy
import org.jetbrains.annotations.Nullable
import java.io.File
import java.io.FileWriter
import java.io.IOException

class DiskLog(@NonNull val handler: Handler) : LogStrategy {

    override fun log(level: Int, @Nullable tag: String?, @NonNull message: String) {
        checkNotNull(message)

        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(level, message))
    }

    internal class WriteHandler(@NonNull looper: Looper, @NonNull folder: String, private val maxFileSize: Int) :
        Handler(checkNotNull(looper)) {

        @NonNull
        private val folder: String = checkNotNull(folder)

        override fun handleMessage(@NonNull msg: Message) {
            val content = msg.obj as String

            var fileWriter: FileWriter? = null
            val logFile = getLogFile(folder, "logs")

            try {
                fileWriter = FileWriter(logFile, true)

                writeLog(fileWriter, content)

                fileWriter.flush()
                fileWriter.close()
            } catch (e: IOException) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush()
                        fileWriter.close()
                    } catch (e1: IOException) { /* fail silently */
                    }

                }
            }

        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        @Throws(IOException::class)
        private fun writeLog(@NonNull fileWriter: FileWriter, @NonNull content: String) {
            checkNotNull(fileWriter)
            checkNotNull(content)

            fileWriter.append(content)
        }

        private fun getLogFile(@NonNull folderName: String, @NonNull fileName: String): File {
            checkNotNull(folderName)
            checkNotNull(fileName)

            val folder = File(folderName)
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs()
            }

            var newFileCount = 0
            var newFile: File
            var existingFile: File? = null

            newFile = File(folder, String.format("%s_%s.csv", fileName, newFileCount))
            while (newFile.exists()) {
                existingFile = newFile
                newFileCount++
                newFile = File(folder, String.format("%s_%s.csv", fileName, newFileCount))
            }

            return if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    newFile
                } else existingFile
            } else newFile

        }
    }
}
