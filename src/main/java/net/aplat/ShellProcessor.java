package net.aplat;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ShellProcessor {

    private ShellProcessor() {
    }

    public static Result exec(String scriptContent) throws IOException, InterruptedException {
        Path path = Files.createTempFile("temp_script_", ".sh");
        new FileWriter(path.toAbsolutePath().toFile()).write(scriptContent);
        Process process = RuntimeUtil.exec("bash -x " + path.toAbsolutePath().toFile().getAbsolutePath());
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
        private String output;

        public Result(int exitValue, String output) {
            this.exitValue = exitValue;
            this.output = output;
        }

        public int getExitValue() {
            return exitValue;
        }

        public void setExitValue(int exitValue) {
            this.exitValue = exitValue;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        @Override
        public String toString() {
            return getOutput();
        }
    }
}
