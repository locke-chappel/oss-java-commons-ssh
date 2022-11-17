package com.github.lc.oss.commons.ssh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.lc.oss.commons.testing.AbstractMockTest;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHTest extends AbstractMockTest {
    private static class TestClass extends SSH {
        private JSch jsch = Mockito.mock(JSch.class);

        @Override
        protected JSch getJSch() {
            return this.jsch;
        }
    }

    @Test
    public void test_getJSch() {
        SSH ssh = new SSH();

        final JSch result1 = ssh.getJSch();
        final JSch result2 = ssh.getJSch();
        Assertions.assertNotNull(result1);
        Assertions.assertNotNull(result2);
        Assertions.assertNotSame(result1, result2);
    }

    @Test
    public void test_readFile_error() {
        SSH ssh = new TestClass();

        try {
            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    throw new JSchException("BOOM!");
                }
            }).when(ssh.getJSch()).setKnownHosts("knownHosts");
        } catch (JSchException e) {
            Assertions.fail("Unexpected exception");
        }

        try {
            ssh.readFile("user", "host", 22, "knownHosts", "privateKey", "file");
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("Error creating SSH session", ex.getMessage());
        }
    }

    @Test
    public void test_readFile_error_v2() {
        final Session session = Mockito.mock(Session.class);

        SSH ssh = new TestClass();

        try {
            Mockito.doAnswer(new Answer<Session>() {
                @Override
                public Session answer(InvocationOnMock invocation) throws Throwable {
                    return session;
                }
            }).when(ssh.getJSch()).getSession("user", "host", 22);

            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    throw new JSchException("BOOM!");
                }
            }).when(session).connect(ssh.getSessionConnectTimeout());
        } catch (JSchException e) {
            Assertions.fail("Unexpected exception");
        }

        try {
            ssh.readFile("user", "host", 22, "knownHosts", "privateKey", "file");
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("Error reading file over SFTP", ex.getMessage());
        }
    }

    @Test
    public void test_readFile() {
        final Session session = Mockito.mock(Session.class);
        final Channel channel = Mockito.mock(ChannelSftp.class);

        SSH ssh = new TestClass();

        try {
            Mockito.doAnswer(new Answer<Session>() {
                @Override
                public Session answer(InvocationOnMock invocation) throws Throwable {
                    return session;
                }
            }).when(ssh.getJSch()).getSession("user", "host", 22);

            Mockito.doAnswer(new Answer<Channel>() {
                @Override
                public Channel answer(InvocationOnMock invocation) throws Throwable {
                    return channel;
                }
            }).when(session).openChannel("sftp");
        } catch (JSchException e) {
            Assertions.fail("Unexpected exception");
        }

        ByteArrayOutputStream result = ssh.readFile("user", "host", 22, "knownHosts", "privateKey", "file");
        Assertions.assertNotNull(result);
    }

    @Test
    public void test_writeFile_error() {
        SSH ssh = new TestClass();

        try {
            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    throw new JSchException("BOOM!");
                }
            }).when(ssh.getJSch()).setKnownHosts("knownHosts");
        } catch (JSchException e) {
            Assertions.fail("Unexpected exception");
        }

        try {
            ssh.writeFile("user", "host", 22, "knownHosts", "privateKey", "file", new ByteArrayInputStream(new byte[0]));
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("Error creating SSH session", ex.getMessage());
        }
    }

    @Test
    public void test_writeFile_error_v2() {
        final Session session = Mockito.mock(Session.class);

        SSH ssh = new TestClass();

        try {
            Mockito.doAnswer(new Answer<Session>() {
                @Override
                public Session answer(InvocationOnMock invocation) throws Throwable {
                    return session;
                }
            }).when(ssh.getJSch()).getSession("user", "host", 22);

            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    throw new JSchException("BOOM!");
                }
            }).when(session).connect(ssh.getSessionConnectTimeout());
        } catch (JSchException e) {
            Assertions.fail("Unexpected exception");
        }

        try {
            ssh.writeFile("user", "host", 22, "knownHosts", "privateKey", "file", new ByteArrayInputStream(new byte[0]));
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertEquals("Error writing file over SFTP", ex.getMessage());
        }
    }

    @Test
    public void test_writeFile() {
        final Session session = Mockito.mock(Session.class);
        final Channel channel = Mockito.mock(ChannelSftp.class);

        SSH ssh = new TestClass();

        try {
            Mockito.doAnswer(new Answer<Session>() {
                @Override
                public Session answer(InvocationOnMock invocation) throws Throwable {
                    return session;
                }
            }).when(ssh.getJSch()).getSession("user", "host", 22);

            Mockito.doAnswer(new Answer<Channel>() {
                @Override
                public Channel answer(InvocationOnMock invocation) throws Throwable {
                    return channel;
                }
            }).when(session).openChannel("sftp");
        } catch (JSchException e) {
            Assertions.fail("Unexpected exception");
        }

        ssh.writeFile("user", "host", 22, "knownHosts", "privateKey", "file", new ByteArrayInputStream(new byte[0]));
    }
}
