package org.univorleans.coq.toplevel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Created by dabrowski on 27/01/2016.
 */
public class CoqtopUtilTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    File coqtop, dir1, dir2, dir3;
    File[] empty;
    File[] include;

    @Before
    public void setUp() throws Exception {

        coqtop = testFolder.newFile("coqtop");
        dir1 = testFolder.newFile("dir1");
        dir2 = testFolder.newFile("dir2");
        dir3 = testFolder.newFile("dir3");
        empty = new File[0];
        include = new File[]{dir1, dir2, dir3};
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMakeCommand1() throws Exception {

        String[] cmd = CoqtopUtil.makeCommand(coqtop, include);

        String[] expected = new String[]{
                coqtop.getPath(), "-emacs",
                "-I", dir1.getPath(), "-I", dir2.getPath(), "-I", dir3.getPath()
        };

        assertTrue (cmd.length == expected.length);
        for(int i = 0; i < cmd.length; i++) {
            System.out.println(cmd[i]);
            assertTrue (cmd[i].equals(expected[i]));
        }
    }

    @Test
    public void testMakeCommand2() throws Exception {

        String[] cmd = CoqtopUtil.makeCommand(coqtop, empty);

        String[] expected = new String[]{ coqtop.getPath(), "-emacs" };

        assertTrue (cmd.length == expected.length);
        for(int i = 0; i < cmd.length; i++) {
            System.out.println(cmd[i]);
            assertTrue (cmd[i].equals(expected[i]));
        }
    }


}