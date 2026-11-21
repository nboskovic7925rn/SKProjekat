package repo.impl.network

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier

object SftpConnection {

    fun createStorage(params: Map<String,String>): SftpStorage{
        val host = params["host"] ?: throw IllegalArgumentException("Nedostaje parametar host (adresa servis)")
        val user = params["user"] ?: throw IllegalArgumentException("Nedostaje parametar user (korisinik)")
        val paswrd = params["password"] ?: throw IllegalArgumentException("Nedostaje parametar password (lozinka)")
        val root = params["root"] ?: throw IllegalArgumentException("Nedostaje parametar root (koren repozitorijuma)")
        val portovi = params["port"]?.toInt() ?: 22

        val ssh = SSHClient()
        ssh.addHostKeyVerifier(PromiscuousVerifier())
        ssh.connect(host,portovi)
        ssh.authPassword(user,paswrd)
        return SftpStorage(ssh.newSFTPClient(),root)
    }
}