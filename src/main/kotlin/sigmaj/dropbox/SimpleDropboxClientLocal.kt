package sigmaj.dropbox

import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.dropbox.core.v2.files.Metadata
import java.io.*
import java.nio.charset.Charset

object SimpleDropboxClientLocal : SimpleDropboxClientInterface {

    private lateinit var root: String

    /**
     * Prepare the dropbox client to access files
     * Must be called before any other function
     * @param root: root directory of local dropbox folder
     */
    fun init(root: String) {
        SimpleDropboxClientLocal.root = root
    }

    override fun getFilesInDirectory(path: String): Collection<Metadata> {
        val dir = File(root + path)
        val allEntries = mutableSetOf<Metadata>()
        for (f in dir.listFiles()) {
            if(f.isDirectory) {
                //String pathLower, String pathDisplay, String parentSharedFolderId, String sharedFolderId, FolderSharingInfo sharingInfo, List<PropertyGroup> propertyGroups)
                val builder = FolderMetadata.newBuilder(f.name)
                val newEntry = builder.withPathLower(path).withPathDisplay(path).build()
                allEntries.add(newEntry)
            }
            else {
                //String pathLower, String pathDisplay, String parentSharedFolderId, String sharedFolderId, FolderSharingInfo sharingInfo, List<PropertyGroup> propertyGroups)
                val builder = FileMetadata.newBuilder(f.name)
                val newEntry = builder.withPathLower(path).withPathDisplay(path).build()
                allEntries.add(newEntry)
            }
        }
        return allEntries
    }

    override fun getFile(path: String, destination: OutputStream) {
        val f = File(root + path)
        val istream = f.inputStream()
        val data = ByteArray(8192)
        var bytesRead = istream.read(data, 0, data.size)
        while (bytesRead > 0) {
            destination.write(data, 0, bytesRead)
            destination.flush()
            bytesRead = istream.read(data, 0, data.size)
        }
        istream.close()
    }

    override fun getTextFile(path: String, charset: Charset): String {
        val stream = ByteArrayOutputStream()
        getFile(path, stream)
        return String(stream.toByteArray(), charset)
    }

    override fun uploadFile(path: String, source: InputStream) {
        val f = File(root + path)
        val ostream = f.outputStream()
        val data = ByteArray(8192)
        var bytesRead = source.read(data, 0, data.size)
        while (bytesRead > 0) {
            ostream.write(data, 0, bytesRead)
            ostream.flush()
            bytesRead = source.read(data, 0, data.size)
        }
        ostream.close()
    }

    override fun uploadTextFile(path: String, contents: String, charset: Charset) {
        val stream = ByteArrayInputStream(contents.toByteArray())
        uploadFile(path, stream)
    }

    override fun fileExists(path: String): Boolean {
        val f = File(root + path)
        return f.exists() && f.isFile
    }

    override fun folderExists(path: String): Boolean {
        val f = File(root + path)
        return f.exists() && f.isDirectory
    }

}