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
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;


public final class MagicFile {
    private static final String LIBRARY_NAME="magicjbind";

    private synchronized static native String checkText(String path);
    private synchronized static native String checkMime(String path);
    private synchronized static native String checkEncoding(String path);
    private synchronized static native String checkTextStream(byte[] buffer) throws IOException;
    private synchronized static native String checkMimeStream(byte[] buffer) throws IOException;
    private synchronized static native String checkEncodingStream(byte[] buffer) throws IOException;

    private byte[] currentBytes = null;

    public void setInput(InputStream is) throws IOException {
        currentBytes = IOUtils.toByteArray(is);
    }

    static {
        System.loadLibrary(LIBRARY_NAME);
    }

    public static String checkText(File file) throws FileNotFoundException {
        if(file.exists() && file.canRead()) {
            return checkText(file.getAbsolutePath());
        } else {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
    }

    public static String checkMime(File file) throws FileNotFoundException {
        if(file.exists() && file.canRead()) {
            return checkMime(file.getAbsolutePath());
        } else {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
    }

    public static String checkEncoding(File file) throws FileNotFoundException {
        if(file.exists()) {
            return checkEncoding(file.getAbsolutePath());
        } else {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
    }

    public static String checkText(InputStream is) throws IOException {
        if(is.available() > 0) {
            return checkTextStream(IOUtils.toByteArray(is));
        } else {
            throw new IOException("At end of stream");
        }
    }

    public static String checkMime(InputStream is) throws IOException {
        if(is.available() > 0) {
            return checkMimeStream(IOUtils.toByteArray(is));
        } else {
            throw new IOException("At end of stream");
        }
    }

    public static String checkEncoding(InputStream is) throws IOException {
        if(is.available() > 0) {
            return checkEncodingStream(IOUtils.toByteArray(is));
        } else {
            throw new IOException("At end of stream");
        }
    }

    public String checkText() throws IOException {
        if(currentBytes == null)
            throw new IOException("No input stream set");
        return checkTextStream(currentBytes);
    }

    public String checkMime() throws IOException {
        if(currentBytes == null)
            throw new IOException("No input stream set");
        return checkMimeStream(currentBytes);
    }

    public String checkEncoding() throws IOException {
        if(currentBytes == null)
            throw new IOException("No input stream set");
        return checkEncodingStream(currentBytes);
    }

    public static void main(String args[]) {
        System.out.println("MagicFile binding for libmagic...");
        if(args.length > 0 && new File(args[0]).exists() && new File(args[0]).canRead()) {
            try {
                System.out.println("Characteristics for: " + args[0]);
                System.out.println(checkText(new File(args[0])));
                System.out.println(checkMime(new File(args[0])));
                System.out.println(checkEncoding(new File(args[0])));
                System.out.println("When streaming the bytes: " + args[0]);
                System.out.println(checkText(new FileInputStream(args[0])));
                System.out.println(checkMime(new FileInputStream(args[0])));
                System.out.println(checkEncoding(new FileInputStream(args[0])));

                System.out.println("\n===\nTesting native exceptions");
                InputStream is = new FileInputStream(args[0]);
                checkMimeStream(IOUtils.toByteArray(is));
                int checks = 0;
                try {
                    System.out.println(checkTextStream(IOUtils.toByteArray(is)));
                } catch(IOException e) {
                    checks++;
                    System.out.print("That's 1 --");
                }
                try {
                    System.out.println(checkEncodingStream(IOUtils.toByteArray(is)));
                } catch(IOException e) {
                    checks++;
                    System.out.print(" 2 --");
                }
                try {
                    System.out.println(checkMimeStream(IOUtils.toByteArray(is)));
                } catch(IOException e) {
                    checks++;
                    System.out.println(" 3");
                }
                if(checks < 3) {
                    System.err.println("WARN: not all expected native exceptions were thrown " + checks + "/3");
                } else {
                    System.out.println("OK");
                }

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
