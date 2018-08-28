import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.DeleteErrorException
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import java.io.*

class SimpleDropboxClientDesktopTest {

    var client = SimpleDropboxClientDesktop

    @Before
    fun prepareClient() {
        val accessToken = javaClass.getResourceAsStream("authToken.txt").bufferedReader().readText()
        client.init(accessToken, "Kotlin Dropbox Wrapper Test")
    }

    @After
    fun deleteUploadedTestFiles() {
        try {
            val accessToken = javaClass.getResourceAsStream("authToken.txt").bufferedReader().readText()
            val requestConfig = DbxRequestConfig("Kotlin Dropbox Wrapper Test")
            val rawClient = DbxClientV2(requestConfig, accessToken)
            rawClient.files().deleteV2("/dbxtests/testfile-u")
        } catch (e: DeleteErrorException) {
            print(e)
        }
    }

    @Test
    fun testDownloadFile() {
        val fileStream = ByteArrayOutputStream()
        val lorem = "Lorem ipsum dolor sit amet"
        client.getFile("/dbxtests/testfile-d", fileStream)
        val loremDL = String(fileStream.toByteArray())
        Assert.assertEquals(lorem, loremDL)

        fileStream.reset()

        val theChaos = javaClass.getResourceAsStream("theChaos.txt").bufferedReader().readText()
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

        val theChaos = javaClass.getResourceAsStream("theChaos.txt").bufferedReader().readText()
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