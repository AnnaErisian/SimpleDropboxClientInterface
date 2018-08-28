import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.dropbox.core.v2.files.Metadata
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

object SimpleDropboxClientLocal : SimpleDropboxClientInterface {

    private lateinit var root: String

    /**
     * Prepare the dropbox client to access files
     * Must be called before any other function
     * @param root: root directory of local dropbox folder
     */
    fun init(root: String) {
        this.root = root
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTextFile(path: String, charset: Charset): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uploadFile(path: String, source: InputStream) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun uploadTextFile(path: String, contents: String, charset: Charset) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fileExists(path: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun folderExists(path: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}