package ch.cyberduck.core.local;

import ch.cyberduck.core.AbstractTestCase;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Permission;
import ch.cyberduck.core.exception.AccessDeniedException;

import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 * @version $Id$
 */
public class FinderLocalTest extends AbstractTestCase {

    @Test
    public void testEqual() throws Exception {
        final String name = UUID.randomUUID().toString();
        FinderLocal l = new FinderLocal(System.getProperty("java.io.tmpdir"), name);
        assertEquals(new FinderLocal(System.getProperty("java.io.tmpdir"), name), l);
        LocalTouchFactory.get().touch(l);
        assertEquals(new FinderLocal(System.getProperty("java.io.tmpdir"), name), l);
        final FinderLocal other = new FinderLocal(System.getProperty("java.io.tmpdir"), name + "-");
        assertNotSame(other, l);
        LocalTouchFactory.get().touch(other);
        assertNotSame(other, l);
    }

    @Test(expected = AccessDeniedException.class)
    public void testReadNoFile() throws Exception {
        final String name = UUID.randomUUID().toString();
        FinderLocal l = new FinderLocal(System.getProperty("java.io.tmpdir"), name);
        l.getInputStream();
    }

    @Test
    public void testList() throws Exception {
        assertFalse(new FinderLocal("profiles").list().isEmpty());
    }

    @Test
    public void testTilde() throws Exception {
        assertEquals(System.getProperty("user.home") + "/f", new FinderLocal("~/f").getAbsolute());
        assertEquals("~/f", new FinderLocal("~/f").getAbbreviatedPath());
    }

    @Test
    public void testDisplayName() throws Exception {
        assertEquals("f/a", new FinderLocal(System.getProperty("java.io.tmpdir"), "f:a").getDisplayName());
    }

    @Test
    public void testWriteUnixPermission() throws Exception {
        this.repeat(new Callable<Local>() {
            @Override
            public Local call() throws Exception {
                Local l = new FinderLocal(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
                new DefaultLocalTouchFeature().touch(l);
                final Permission permission = new Permission(644);
                l.attributes().setPermission(permission);
                assertEquals(permission, l.attributes().getPermission());
                l.delete();
                return l;
            }
        }, 10);
    }

    @Test
    public void testMkdir() {
        FinderLocal l = new FinderLocal(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        l.mkdir();
        assertTrue(l.exists());
        l.mkdir();
        assertTrue(l.exists());
        l.delete();
    }

    @Test
    public void testToUrl() throws Exception {
        assertEquals("file:/c/file", new FinderLocal("/c/file").toURL());
    }

    @Test
    public void testBookmark() throws Exception {
        FinderLocal l = new FinderLocal(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        assertNull(l.getBookmark());
        LocalTouchFactory.get().touch(l);
        assertNotNull(l.getBookmark());
        assertEquals(l.getBookmark(), l.getBookmark());
        assertSame(l.getBookmark(), l.getBookmark());
    }

    @Test
    public void testBookmarkSaved() throws Exception {
        FinderLocal l = new FinderLocal(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        assertNull(l.getBookmark());
        l.setBookmark("a");
        assertEquals("a", l.getBookmark());
        assertNotNull(l.getOutputStream(false));
        assertNotNull(l.getInputStream());
    }
}
