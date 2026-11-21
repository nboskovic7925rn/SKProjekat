package repo.impl.network

import net.schmizz.sshj.sftp.OpenMode
import net.schmizz.sshj.sftp.SFTPClient
import repo.engine.Storage
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.EnumSet

class SftpStorage(private val sftp: SFTPClient, private val root: String) : Storage{

    private fun puna(path: String): String{
        val punaPutanja = root
        if (path.isEmpty()){
            return punaPutanja
        }
        else{
            return "$punaPutanja/$path"
        }
    }
    override fun exists(path: String): Boolean {
        val podaci = sftp.statExistence(puna(path))
        if(podaci == null){
            return false
        }
        else{
            return true
        }
    }

    override fun read(path: String): InputStream {

        val fajl = sftp.open(puna(path)).use {
            it.RemoteFileInputStream().readBytes()
        }
        return ByteArrayInputStream(fajl)
    }

    override fun write(path: String, data: InputStream) {
        sftp.mkdirs(puna(path).substringBeforeLast("/"))
        sftp.open(puna(path),EnumSet.of(OpenMode.WRITE, OpenMode.CREAT, OpenMode.TRUNC)).use {
            it.RemoteFileOutputStream().use {
                izlaz -> data.copyTo(izlaz)
            }


        }

    }

    override fun list(path: String): List<String> {
        if(!exists(path)){
            return emptyList()
        }
        else{
            val lista = sftp.ls(puna(path))
            val fajlovi = mutableListOf<String>()
            for(fajl in lista){
                val naziv = fajl.name
                fajlovi.add((naziv))
            }
            return fajlovi
        }
    }

    override fun createDirectory(path: String) {
        sftp.mkdirs(puna(path))
    }
}