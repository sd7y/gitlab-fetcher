package net.aplat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class ProcessBuilderTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        // https://stackoverflow.com/questions/14165517/processbuilder-forwarding-stdout-and-stderr-of-started-processes-without-blocki
        new ProcessBuilder("bash", "-c", "echo 123 && sleep 1 && echo 456 >&2 && echo 789")
                .inheritIO()
                .start()
                .waitFor();
        process(new ProcessBuilder("bash", "-c", "echo 0123 && sleep 1 && echo 0456 >&2 && echo 0789")
                .inheritIO()
                .start(), System.out::println, System.err::println);

        process(new ProcessBuilder("bash", "-c", "echo test2 && sleep 1 && echo test2 end >&2 && echo after error")
                .redirectErrorStream(true).start(), System.out::println, System.err::println);
        System.out.println("xxxxx");
    }
    private static int process(Process process, Consumer<String> stdout, Consumer<String> stderr) throws InterruptedException {
        handle(process.getInputStream(), stdout);
        handle(process.getErrorStream(), stderr);
        return process.waitFor();
    }

    private static void handle(InputStream inputStream, Consumer<String> consumer) {
//        new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    consumer.accept(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }).start();
    }

}
