import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.dropbox.core.v2.files.Metadata
import com.dropbox.core.v2.files.WriteMode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object SimpleDropboxClientWeb : SimpleDropboxClientInterface {

    private lateinit var client: DbxClientV2

    override fun init(appKey: String, identifier: String?) {
        val requestConfig = DbxRequestConfig(identifier)
        client = DbxClientV2(requestConfig, appKey)
    }

    override fun getFilesInDirectory(path: String): Collection<Metadata> {
        var result = client.files().listFolder(path)
        val allEntries = mutableSetOf<Metadata>()
        do {
            allEntries.addAll(result.entries)
            result = client.files().listFolderContinue(result.cursor)
        } while(result.hasMore)
        return allEntries.toSet()

    }

    override fun getFile(path: String, destination: OutputStream) {
        val downloader = client.files().download(path)
        downloader.download(destination)
    }

    override fun getTextFile(path: String, charset: Charset): String {
        val stream = ByteArrayOutputStream()
        val downloader = client.files().download(path)
        downloader.download(stream)
        return String(stream.toByteArray(), charset)
    }

    override fun uploadFile(path: String, source: InputStream) {
        client.files()
                .uploadBuilder(path)
                .withMode(WriteMode.OVERWRITE)
                .uploadAndFinish(source)
    }

    override fun uploadTextFile(path: String, contents: String, charset: Charset) {
        uploadFile(path, ByteArrayInputStream(contents.toByteArray(charset)))
    }

    override fun fileExists(path: String): Boolean {
        try {
            val meta = client.files().getMetadata(path)
            return meta is FileMetadata
        } catch (e: Throwable) {
            print(e)
            return false
        }
    }

    override fun folderExists(path: String): Boolean {
        try {
            val meta = client.files().getMetadata(path)
            return meta is FolderMetadata
        } catch (e: Throwable) {
            print(e)
            return false
        }
    }
}