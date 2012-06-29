Experimental Java binding for libmagic file characterisation
======================

This is an experimental package. Sources for the binding were tested by compiling them on Ubuntu 10.04 LTS against headers from the libmagic-dev package version 5.03.

Compiled bindings proved to run without error on both libmagic v5.03 and v5.11.

Compiling and Packaging
-------
A Makefile is included which also makes use of maven lifecycle goals to test the binding. The quickest way to compile and install the sources is by running the commands below.

- Set the library path, so the jvm can locate the compiled .so for testing::

    $ export LD_LIBRARY_PATH=`pwd`:$LD_LIBRARY_PATH

- Make sure your java home is set to the correct VM, if not, something like::

    $ export JAVA_HOME=/usr/lib/jvm/java-6-sun-1.6.0.26/

- Run make (this will execute the 'package' goal of maven as well)::

    $ make

- If at this stage no errors have occured, the binding was succesfully compiled and the junit tests will have succeeded as well, so now you can call::

    $ sudo make install

- The binding has been installed to /usr/lib/, which means you can run::

    $ mvn install

Completing the above steps succesfully means you can now include the jar in any java project running on the system, provided that the file libmagicjbind.so can be found in one of the paths specified in the jvm's java.library.path system property (keeping it in /usr/lib/ will probably ensure this)::

		<dependency>
			<groupId>nl.kb</groupId>
			<artifactId>magicfile</artifactId>
			<version>0.1.0</version>
		</dependency>


You could also package a 'standalone' jar (which has the commons-io jar included) by running::

		$ make standalone
		$ java -jar target/magicfile-0.1.0-jar-with-dependencies.jar /path/to/file



Available make targets
------------

- 'all': defaults to the 'package' target

- 'clean': removes the .so file and calls 'mvn clean'

- 'compile': compiles the sources against libmagic. Not that compilation expects the 'include' and 'include/linux' directories to exist in your jvm's root.

- 'test': runs 'compile' and mvn test

- 'package': runs 'compile' and mvn package

- 'standalone': builds a jar with the commons-io dependency included

- 'testrun': runs 'package' and then tries to run the packaged jar

- 'install': copies the compiled binding to /usr/lib/

- 'uninstall': removes the compiled binding from /usr/lib/

Prerequisites
-------
The complete list of preconditions:

- Ubuntu 10.04 LTS

- libmagic-dev >= 5.03

- libmagic >= 5.03

- Java 6 Sun jdk: version 1.6.0.26

- make

- Maven >= 2.x

