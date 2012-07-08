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
package nl.kb.magicfile;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


public final class MagicFile {
    private static final String LIBRARY_NAME = "libmagicjbind.so";
    private static final int MAX_CHECK_SIZE = 4096;

    private byte[] currentBytes = new byte[MAX_CHECK_SIZE];
    private boolean byteBufferFilled = false;
    private File currentFile = null;

    static {
        InputStream is = MagicFile.class.getResourceAsStream("/" + LIBRARY_NAME);
        try {
            File temp = File.createTempFile(LIBRARY_NAME, "");
            FileOutputStream fos = new FileOutputStream(temp);
            fos.write(IOUtils.toByteArray(is));
            fos.close();
            is.close();
            System.load(temp.getAbsolutePath());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }


    private synchronized static native String checkText(String path);
    private synchronized static native String checkMime(String path);
    private synchronized static native String checkEncoding(String path);
    private synchronized static native String checkTextStream(byte[] buffer) throws IOException;
    private synchronized static native String checkMimeStream(byte[] buffer) throws IOException;
    private synchronized static native String checkEncodingStream(byte[] buffer) throws IOException;

    private static byte[] readBytes(InputStream is) throws IOException {
        byte[] bytes = new byte[MAX_CHECK_SIZE];
        IOUtils.read(is, bytes);
        is.close();
        return bytes;
    }

    private static String characterize(Check check, byte[] bytes) throws IOException {
        switch(check) {
            case TEXT: return checkTextStream(bytes);
            case MIMETYPE: return checkMimeStream(bytes);
            default: return checkEncodingStream(bytes);
        }
    }

    public static String characterize(Check check, File file) throws FileNotFoundException {
        if(file == null) {
            throw new NullPointerException("No file specified");
        } else if(!file.exists() || !file.canRead()) {
            throw new FileNotFoundException("File cannot be read: " + file.getAbsolutePath());
        }
        switch(check) {
            case ENCODING:
                return checkEncoding(file.getAbsolutePath());
            case MIMETYPE:
                return checkMime(file.getAbsolutePath());
            default:
                return checkText(file.getAbsolutePath());
        }
    }

    public static Map<Check, String> characterize(Iterable<Check> checks, File file) throws FileNotFoundException {
        Map<Check, String> results = new HashMap<Check, String>();
        for(Check check : checks) { results.put(check, characterize(check,file)); }
        return results;
    }

    public static Map<Check, String> characterize(Iterable<Check> checks, InputStream is) throws IOException {
        Map<Check, String> results = new HashMap<Check, String>();
        byte[] bytes = readBytes(is);
        for(Check check : checks) { results.put(check, characterize(check, bytes)); }
        return results;
    }

    public static String checkText(File file) throws FileNotFoundException { return characterize(Check.TEXT, file); }
    public static String checkMime(File file) throws FileNotFoundException { return characterize(Check.MIMETYPE, file); }
    public static String checkEncoding(File file) throws FileNotFoundException { return characterize(Check.ENCODING, file); }


    public static String characterize(Check check, InputStream is) throws IOException {
        if(is == null || is.available() <= 0) {
            throw new IOException("At end of stream or stream is closed");
        }
        return characterize(check, readBytes(is));
    }

    public static String checkText(InputStream is) throws IOException { return characterize(Check.TEXT, is); }
    public static String checkMime(InputStream is) throws IOException { return characterize(Check.MIMETYPE, is); }
    public static String checkEncoding(InputStream is) throws IOException { return characterize(Check.ENCODING, is); }

    private void setInput(File f) throws FileNotFoundException {
        if(f.exists() && f.canRead()) {
            currentFile = f;
        } else {
            throw new FileNotFoundException("Unable to read file: " + f.getAbsolutePath());
        }
    }

    private void setInput(InputStream is) throws IOException {
        currentBytes = readBytes(is);
        byteBufferFilled = true;
        currentFile = null;
    }

    public MagicFile(InputStream is) throws IOException { setInput(is); }
    public MagicFile(File f) throws FileNotFoundException { setInput(f); }
    public MagicFile(String filename) throws FileNotFoundException { setInput(new File(filename)); }

    public String characterize(Check check) throws IOException {
        if(currentFile != null) { return characterize(check, currentFile); }
        else if(byteBufferFilled) { return characterize(check, currentBytes); }
        else { throw new IOException("No input-stream or file set"); }
    }

    public Map<Check, String> characterize(Iterable<Check> checks) throws IOException {
        Map<Check, String> results = new HashMap<Check, String>();
        for(Check check : checks) { results.put(check, characterize(check)); }
        return results;
    }

    public String checkText() throws IOException { return characterize(Check.TEXT); }
    public String checkMime() throws IOException { return characterize(Check.MIMETYPE); }
    public String checkEncoding() throws IOException { return characterize(Check.ENCODING); }

    public static void main(String args[]) {
        System.out.println("MagicFile binding for libmagic...");
        if(args.length > 0 && new File(args[0]).exists() && new File(args[0]).canRead()) {
            try {
                System.out.println("Characteristics for: " + args[0]);
                System.out.println("Textual representation: " + checkText(new File(args[0])));
                System.out.println("Magic mime type: " + checkMime(new File(args[0])));
                System.out.println("Encoding: " + checkEncoding(new File(args[0])));
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("First argument should be the path to an existing filename.");
        }
    }
}
