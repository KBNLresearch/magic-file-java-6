/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package nl.kb.magicfiletest;

import nl.kb.magicfile.Check;
import nl.kb.magicfile.MagicFile;
import junit.framework.TestCase;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

public class MagicFileTest extends TestCase {
    private static String testFilePath;
    private static final String BOGUS_FILE_PATH = "thisFileProbablyDoesNot.exist";

    public void setUp() {
        testFilePath = this.getClass().getResource("/checkme.txt").getPath();
    }

    public void testFileText() throws Exception {
        System.out.print("Asserting that file " + testFilePath + " is characterised as ASCII text ");
        String result = MagicFile.checkText(new File(testFilePath));
        assertTrue(result.contains("ASCII text"));
        System.out.println("-- result: '" + result + "'-- OK");
    }

    public void testFileNotFoundException() throws Exception {
        System.out.print("Asserting that file " + BOGUS_FILE_PATH + " throws a FileNotFoundException ");
        boolean thrown = false;
        try {
            MagicFile.checkText(new File(BOGUS_FILE_PATH));
        } catch(FileNotFoundException e) {
            thrown = true;
        }
        assertTrue(thrown);
        System.out.println("-- OK");
    }

    public void testFileMime() throws Exception {
        System.out.print("Asserting that file " + testFilePath + " is characterised with MIME type text/plain ");
        String result = MagicFile.checkMime(new File(testFilePath));
        assertTrue(result.contains("text/plain"));
        System.out.println(" result: '" + result + "' -- OK");
    }

    public void testFileEncoding() throws Exception {
        System.out.print("Asserting that file " + testFilePath + " is characterised with correct encoding ");
        String result = MagicFile.checkEncoding(new File(testFilePath));
        assertTrue(result.contains("ascii"));
        System.out.println("-- result: '" + result + "' -- OK");
    }

    public void testFileNameInInstance() throws Exception {
        MagicFile m = new MagicFile(testFilePath);
        System.out.println("Asserting that an instance will work with an existing filename");
        System.out.println("Result A: " + m.checkText());
        System.out.println("Result B: " + m.checkMime());
        System.out.println("Result C: " + m.checkEncoding());
        System.out.println("OK");
    }

    public void testFileInInstance() throws Exception {
        MagicFile m = new MagicFile(new File(testFilePath));
        System.out.println("Asserting that an instance will work with existing file");
        System.out.println("Result A: " + m.checkText());
        System.out.println("Result B: " + m.checkMime());
        System.out.println("Result C: " + m.checkEncoding());
        System.out.println("OK");
    }


    public void testBytesInInstance() throws Exception {
        MagicFile m = new MagicFile(new FileInputStream(testFilePath));
        System.out.println("Asserting stream gets loaded correctly in buffer for multiple checks ");
        System.out.println("Result A: " + m.checkText());
        System.out.println("Result B: " + m.checkMime());
        System.out.println("Result C: " + m.checkEncoding());
        System.out.println("OK");
    }

    public void testBytes() throws Exception {
        System.out.println("Asserting that a stream can be characterised directly once");
        InputStream is = new FileInputStream(testFilePath);
        System.out.println("Result: " + MagicFile.checkText(is));
        System.out.println("OK");
        System.out.println("Asserting that an IOException gets thrown when trying to access the same stream twice");
        int thrown = 0;
        try {
            System.out.println("Should fail: " + MagicFile.checkEncoding(is));
        } catch(IOException e) {
            thrown++;
        }
        try {
            System.out.println("Should fail: " + MagicFile.checkMime(is));
        } catch(IOException e) {
            thrown++;
        }
        assertEquals(thrown, 2);
        System.out.println("OK");
    }

    public void testComprehensive() throws Exception {
        System.out.print("Asserting that we can characterize all at once by file");
        Map<Check, String> results = MagicFile.characterize(Arrays.asList(new Check[]{Check.ENCODING, Check.MIMETYPE, Check.TEXT}), new File(testFilePath));
        assertTrue(results.get(Check.ENCODING).contains("ascii"));
        assertTrue(results.get(Check.MIMETYPE).contains("text/plain"));
        assertTrue(results.get(Check.TEXT).contains("ASCII text"));
        System.out.println(" -- OK");
    }

    public void testComprehensiveStream() throws Exception {
        System.out.print("Asserting that we can characterize all at once by stream");
        Map<Check, String> results = MagicFile.characterize(Arrays.asList(new Check[]{Check.ENCODING, Check.MIMETYPE, Check.TEXT}), new FileInputStream(testFilePath));
        assertTrue(results.get(Check.MIMETYPE).contains("text/plain"));
        assertTrue(results.get(Check.TEXT).contains("ASCII text"));
        System.out.println(" -- OK");
    }

    public void testComprehensiveInstance() throws Exception {
        System.out.print("Asserting that we can characterize all at once by stream in an instance");
        MagicFile m = new MagicFile(new FileInputStream(testFilePath));
        Map<Check, String> results = m.characterize(Arrays.asList(new Check[]{Check.ENCODING, Check.MIMETYPE, Check.TEXT}));
        assertTrue(results.get(Check.MIMETYPE).contains("text/plain"));
        assertTrue(results.get(Check.TEXT).contains("ASCII text"));
        System.out.println(" -- OK");
    }
}
