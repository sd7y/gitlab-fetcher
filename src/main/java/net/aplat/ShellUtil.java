package net.aplat;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class ShellUtil {
    private static final Log logger = Log.get();

    private ShellUtil() {
    }

    public static int exec(String... command) throws IOException, InterruptedException {
        return new ProcessBuilder(command).inheritIO().start().waitFor();
    }

    public static int exec(Consumer<String> stdout, Consumer<String> stderr, String... command) throws IOException, InterruptedException {
        return process(new ProcessBuilder(command).start(), stdout, stderr);
    }

    private static int process(Process process, Consumer<String> stdout, Consumer<String> stderr) throws InterruptedException {
        outputHandler(process.getInputStream(), stdout);
        outputHandler(process.getErrorStream(), stderr);
        return process.waitFor();
    }

    private static void outputHandler(InputStream inputStream, Consumer<String> consumer) {
        new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    consumer.accept(line);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }).start();
    }

    public static Result exec(String scriptContent) throws IOException, InterruptedException {
        Path path = Files.createTempFile("temp_script_", ".sh");
        new FileWriter(path.toAbsolutePath().toFile()).write(scriptContent);
        Process process = new ProcessBuilder("bash", "-x", path.toAbsolutePath().toFile().getAbsolutePath())
                .redirectErrorStream(true)
                .start();
        String result;
        try (InputStream in = process.getInputStream()) {
            result = IoUtil.read(in, CharsetUtil.systemCharset());
        } finally {
            process.destroy();
        }
        int exitValue = process.waitFor();
        Files.delete(path);
        return new Result(exitValue, result);
    }

    public static class Result {
        private int exitValue;
        private String stdout;
        private String stderr;

        public Result(int exitValue, String stdout) {
            this.exitValue = exitValue;
            this.stdout = stdout;
        }

        public Result(int exitValue, String stdout, String stderr) {
            this.exitValue = exitValue;
            this.stdout = stdout;
            this.stderr = stderr;
        }

        public int getExitValue() {
            return exitValue;
        }

        public void setExitValue(int exitValue) {
            this.exitValue = exitValue;
        }

        public String getStdout() {
            return stdout;
        }

        public void setStdout(String stdout) {
            this.stdout = stdout;
        }

        public String getStderr() {
            return stderr;
        }

        public void setStderr(String stderr) {
            this.stderr = stderr;
        }

        @Override
        public String toString() {
            return getStdout();
        }
    }
}
