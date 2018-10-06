package sigmaj.dropbox

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.DeleteErrorException
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.*

class SimpleDropboxClientLocalTest {

    var client = SimpleDropboxClientLocal

    private val rootLocalPath = System.getProperty("user.home") + "\\Dropbox"

    @Before
    fun prepareClient() {
        client.init(rootLocalPath)
    }

    @After
    fun deleteUploadedTestFiles() {
        val f = File(rootLocalPath + "/dbxtests/testfile-u")
        f.delete()
    }
    @Test
    fun testDownloadFile() {
        val fileStream = ByteArrayOutputStream()
        val lorem = "Lorem ipsum dolor sit amet"
        client.getFile("/dbxtests/testfile-d", fileStream)
        val loremDL = String(fileStream.toByteArray())
        Assert.assertEquals(lorem, loremDL)

        fileStream.reset()

        val theChaos = javaClass.getResourceAsStream("/theChaos.txt").bufferedReader().readText()
        client.getFile("/dbxtests/testfile-d2", fileStream)
        val chaosDL = String(fileStream.toByteArray())
        Assert.assertEquals(chaosDL, theChaos)
    }

    @Test
    fun testDownloadTextFile() {
        val fileStream = ByteArrayOutputStream()
        val lorem = "Lorem ipsum dolor sit amet"
        val loremDL = client.getTextFile("/dbxtests/testfile-d")
        Assert.assertEquals(lorem, loremDL)

        fileStream.reset()

        val theChaos = javaClass.getResourceAsStream("/theChaos.txt").bufferedReader().readText()
        val chaosDL = client.getTextFile("/dbxtests/testfile-d2")
        Assert.assertEquals(chaosDL, theChaos)
    }

    @Test
    fun testGetFilesInDir() {
        val filesInDir = client.getFilesInDirectory("/dbxtests/testListDir")
        Assert.assertEquals(36, filesInDir.size )
        //assert that the files '0', '1', '9', 'a', and 'z' are present
        Assert.assertTrue(filesInDir.filter { it.name == "0" }.size == 1)
        Assert.assertTrue(filesInDir.filter { it.name == "1" }.size == 1)
        Assert.assertTrue(filesInDir.filter { it.name == "9" }.size == 1)
        Assert.assertTrue(filesInDir.filter { it.name == "a" }.size == 1)
        Assert.assertTrue(filesInDir.filter { it.name == "z" }.size == 1)
    }

    @Test
    fun testUploadFile() {
        val lorem = "Lorem ipsum dolor sit amet"
        val ips = ByteArrayInputStream(lorem.toByteArray())
        client.uploadFile("/dbxtests/testfile-u", ips)
        val loremDL = client.getTextFile("/dbxtests/testfile-u")
        Assert.assertEquals(lorem, loremDL)
    }

    @Test
    fun testUploadBigTextFile() {
        val theChaos = javaClass.getResourceAsStream("/theChaos.txt").bufferedReader().readText()
        client.uploadTextFile("/dbxtests/testfile-u", theChaos)
        val loremDL = client.getTextFile("/dbxtests/testfile-u")
        Assert.assertEquals(theChaos, loremDL)
    }

    @Test
    fun testUploadTextFile() {
        val lorem = "Lorem ipsum dolor sit amet"
        client.uploadTextFile("/dbxtests/testfile-u", lorem)
        val loremDL = client.getTextFile("/dbxtests/testfile-u")
        Assert.assertEquals(lorem, loremDL)
    }

    @Test
    fun testFileExists() {
        Assert.assertFalse(client.fileExists("/dbxtests/notrealfile"))
        Assert.assertTrue(client.fileExists("/dbxtests/realfile"))
        Assert.assertFalse(client.fileExists("/dbxtests/realfolder"))
        Assert.assertFalse(client.fileExists("/dbxtests/notrealfolder"))
    }

    @Test
    fun testFolderExists() {
        Assert.assertFalse(client.folderExists("/dbxtests/notrealfile"))
        Assert.assertFalse(client.folderExists("/dbxtests/realfile"))
        Assert.assertTrue(client.folderExists("/dbxtests/realfolder"))
        Assert.assertFalse(client.folderExists("/dbxtests/notrealfolder"))
    }
}