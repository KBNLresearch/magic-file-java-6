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
import java.io.InputStream;
import java.io.File;
import java.io.FileNotFoundException;


public final class MagicFile {
    private static final String LIBRARY_NAME = "magicjbind";
    private static final int MAX_CHECK_SIZE = 4096;

    private synchronized static native String checkText(String path);
    private synchronized static native String checkMime(String path);
    private synchronized static native String checkEncoding(String path);
    private synchronized static native String checkTextStream(byte[] buffer) throws IOException;
    private synchronized static native String checkMimeStream(byte[] buffer) throws IOException;
    private synchronized static native String checkEncodingStream(byte[] buffer) throws IOException;

    private byte[] currentBytes = new byte[MAX_CHECK_SIZE];
    private boolean byteBufferFilled = false;
    private File currentFile = null;

    public MagicFile(InputStream is) throws IOException { setInput(is); }
    public MagicFile(File f) throws FileNotFoundException { setInput(f); }
    public MagicFile(String filename) throws FileNotFoundException { setInput(new File(filename));}

    public void setInput(File f) throws FileNotFoundException {
        if(f.exists() && f.canRead()) {
            currentFile = f;
        } else {
            throw new FileNotFoundException("Unable to read file: " + f.getAbsolutePath());
        }
    }

    public void setInput(InputStream is) throws IOException {
        IOUtils.read(is, currentBytes);
        byteBufferFilled = true;
        currentFile = null;
    }

    static {
        System.loadLibrary(LIBRARY_NAME);
    }

    public static String checkText(File file) throws FileNotFoundException {
        if(file != null && file.exists() && file.canRead()) {
            return checkText(file.getAbsolutePath());
        } else {
            throw new FileNotFoundException("File cannot be read: " + (file != null ? file.getAbsolutePath() : ""));
        }
    }

    public static String checkMime(File file) throws FileNotFoundException {
        if(file != null && file.exists() && file.canRead()) {
            return checkMime(file.getAbsolutePath());
        } else {
            throw new FileNotFoundException("File cannot be read: " + (file != null ? file.getAbsolutePath() : ""));
        }
    }

    public static String checkEncoding(File file) throws FileNotFoundException {
        if(file != null && file.exists() && file.canRead()) {
            return checkEncoding(file.getAbsolutePath());
        } else {
            throw new FileNotFoundException("File cannot be read: " + (file != null ? file.getAbsolutePath() : ""));
        }
    }

    public static String checkText(InputStream is) throws IOException {
        if(is != null && is.available() > 0) {
            byte[] bytes = new byte[MAX_CHECK_SIZE];
            IOUtils.read(is, bytes);
            String characterisation = checkTextStream(bytes);
            is.close();
            return characterisation;
        } else {
            throw new IOException("At end of stream or stream is closed");
        }
    }

    public static String checkMime(InputStream is) throws IOException {
        if(is != null && is.available() > 0) {
            byte[] bytes = new byte[MAX_CHECK_SIZE];
            IOUtils.read(is, bytes);
            String characterisation = checkMimeStream(bytes);
            is.close();
            return characterisation;
        } else {
            throw new IOException("At end of stream or stream is closed");
        }
    }

    public static String checkEncoding(InputStream is) throws IOException {
        if(is != null && is.available() > 0) {
            byte[] bytes = new byte[MAX_CHECK_SIZE];
            IOUtils.read(is, bytes);
            String characterisation = checkEncodingStream(bytes);
            is.close();
            return characterisation;
        } else {
            throw new IOException("At end of stream or stream is closed");
        }
    }

    public String checkText() throws IOException {
        if(currentFile != null) { return checkText(currentFile); }
        if(!byteBufferFilled)
            throw new IOException("No input-stream or file set");
        return checkTextStream(currentBytes);
    }

    public String checkMime() throws IOException {
        if(currentFile != null) { return checkMime(currentFile); }
        if(!byteBufferFilled)
            throw new IOException("No input-stream or file set");
        return checkMimeStream(currentBytes);
    }

    public String checkEncoding() throws IOException {
        if(currentFile != null) { return checkEncoding(currentFile); }
        if(!byteBufferFilled)
            throw new IOException("No input-stream or file set");
        return checkEncodingStream(currentBytes);
    }

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
