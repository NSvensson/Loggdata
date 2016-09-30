/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logaggregatorclient;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author josanbir
 */
public class LogHandlerTest {
    
    public LogHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of read method, of class LogHandler.
     */
    @Test
    public void testRead_3args() {
        System.out.println("read");
        String source_URI = "";
        String service = "";
        String last_line = "";
        LogHandler.read(source_URI, service, last_line);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of packLog method, of class LogHandler.
     */
    @Test
    public void testPackLog() {
        System.out.println("packLog");
        String[][] stringArray = null;
        LogHandler.packLog(stringArray);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of read method, of class LogHandler.
     */
    @Test
    public void testRead_String_String() {
        System.out.println("read");
        String source_URI = "";
        String service = "";
        LogHandler instance = new LogHandler();
        instance.read(source_URI, service);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
