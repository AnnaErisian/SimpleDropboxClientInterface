import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.Metadata
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object SimpleDropboxClientDesktop : SimpleDropboxClientInterface {

    private lateinit var client: DbxClientV2

    override fun init(appKey: String, identifier: String?) {
        val requestConfig = DbxRequestConfig(identifier)
        client = DbxClientV2(requestConfig, appKey)
    }

    override fun getFilesInDirectory(path: String): Collection<Metadata> {
        var result = client.files().listFolder(path)
        val allEntries = mutableSetOf<Metadata>()
        while(result.hasMore) {
            allEntries.plus(result.entries)
            result = client.files().listFolderContinue(result.cursor)
        }
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uploadTextFile(path: String, contents: String, charset: Charset) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}