package net.aplat;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.RuntimeUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ShellProcessor {
    public static String exec(String scriptContent) throws IOException {
        Path path = Files.createTempFile("temp_script_", ".sh");
        new FileWriter(path.toAbsolutePath().toFile()).write(scriptContent);
        String result = RuntimeUtil.execForStr("bash -x " + path.toAbsolutePath().toFile().getAbsolutePath());
        Files.delete(path);
        return result;
    }
}
