package org.univorleans.coq.coqdep;

import org.junit.Test;
import org.univorleans.coq.jps.builder.CoqProjectDependencies;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dabrowski on 17/02/2016.
 */
public class UtilTest {

    @Test
    public void testExtractDependencies() throws Exception {
/*
        List<String> lines = new ArrayList<>();
        lines.add("Test.vo Test.glob Test.v.beautified: Test.v");
        lines.add("Pck/Test2.vo Pck/Test2.glob Pck/Test2.v.beautified: Pck/Test2.v Test4.vo");
        lines.add("Test3.vo Test3.glob Test3.v.beautified: Test3.v Pck/Test2.vo Test4.vo");
        lines.add("Test4.vo Test4.glob Test4.v.beautified: Test4.v");

        CoqProjectDependencies dependencies = Util.extractDependencies(lines);
        assertTrue(dependencies.dependencies.get("Test.v").size() == 0);

        assertTrue(dependencies.dependencies.get("Pck/Test2.v").size() == 1);
        assertTrue(dependencies.dependencies.get("Pck/Test2.v").contains("Test3.v"));

        assertTrue(dependencies.dependencies.get("Test4.v").size() == 2);
        assertTrue(dependencies.dependencies.get("Test4.v").contains("Pck/Test2.v"));
        assertTrue(dependencies.dependencies.get("Test4.v").contains("Test3.v"));
*/

    }
}