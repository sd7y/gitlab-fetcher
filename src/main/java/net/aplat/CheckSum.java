package net.aplat;

import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

@CommandLine.Command(description = "Prints the checksum (MD5 by default) of a file to STDOUT.",
        name = "checksum", mixinStandardHelpOptions = true, version = "checksum 3.0")
class CheckSum implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The file whose checksum to calculate.")
    private File file;

    @CommandLine.Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
    private String algorithm = "SHA-1";

    public static void main(String... args) throws Exception {
        int exitCode = new CommandLine(new CheckSum()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        byte[] fileContents = Files.readAllBytes(file.toPath());
        byte[] digest = MessageDigest.getInstance(algorithm).digest(fileContents);
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(digest));
        return 0;
    }
}
