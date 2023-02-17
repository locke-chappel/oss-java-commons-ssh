package io.github.lc.oss.commons.ssh;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SSH {
    private static final int TIMEOUT = 5000;

    public ByteArrayOutputStream readFile(String user, String host, int port, String knownHostsPath, String privateKeyPath, String path) {
        Session session = null;
        ChannelSftp channel = null;

        try {
            session = this.createSession(user, host, port, knownHostsPath, privateKeyPath);
            session.connect(this.getSessionConnectTimeout());
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(this.getChannelConnectTimeout());
            ByteArrayOutputStream file = new ByteArrayOutputStream();
            channel.get(path, file);
            return file;
        } catch (JSchException | SftpException ex) {
            throw new RuntimeException("Error reading file over SFTP", ex);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }

            if (session != null) {
                session.disconnect();
            }
        }
    }

    public void writeFile(String user, String host, int port, String knownHostsPath, String privateKeyPath, String path, InputStream data) {
        Session session = null;
        ChannelSftp channel = null;

        try {
            session = this.createSession(user, host, port, knownHostsPath, privateKeyPath);
            session.connect(this.getSessionConnectTimeout());
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(this.getChannelConnectTimeout());
            channel.put(data, path);
        } catch (JSchException | SftpException ex) {
            throw new RuntimeException("Error writing file over SFTP", ex);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }

            if (session != null) {
                session.disconnect();
            }
        }
    }

    protected Session createSession(String user, String host, int port, String knownHostsPath, String privateKeyPath) {
        try {
            JSch jsch = this.getJSch();
            jsch.setKnownHosts(knownHostsPath);
            jsch.addIdentity(privateKeyPath);
            return jsch.getSession(user, host, port);
        } catch (JSchException ex) {
            throw new RuntimeException("Error creating SSH session", ex);
        }
    }

    protected JSch getJSch() {
        return new JSch();
    }

    protected int getSessionConnectTimeout() {
        return SSH.TIMEOUT;
    }

    protected int getChannelConnectTimeout() {
        return SSH.TIMEOUT;
    }
}
