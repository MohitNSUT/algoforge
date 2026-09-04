package com.algoforge.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CompilerService {

    public Map<String, Object> executeCode(String language, String version, String code) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> runInfo = new HashMap<>();

        try {
            Path tempDir = Files.createTempDirectory("algoforge_exec_" + UUID.randomUUID().toString());
            File dir = tempDir.toFile();

            ProcessBuilder pb = null;

            if (language.equals("python")) {
                File pyFile = new File(dir, "main.py");
                Files.writeString(pyFile.toPath(), code);
                pb = new ProcessBuilder("python3", "main.py");
            } else if (language.equals("javascript") || language.equals("js")) {
                File jsFile = new File(dir, "main.js");
                Files.writeString(jsFile.toPath(), code);
                pb = new ProcessBuilder("node", "main.js");
            } else if (language.equals("java")) {
                File javaFile = new File(dir, "Main.java");
                Files.writeString(javaFile.toPath(), code);
                pb = new ProcessBuilder("java", "Main.java");
            } else if (language.equals("cpp") || language.equals("c++")) {
                File cppFile = new File(dir, "main.cpp");
                Files.writeString(cppFile.toPath(), code);

                // Create bits/stdc++.h for macOS clang compatibility
                File bitsDir = new File(dir, "bits");
                bitsDir.mkdirs();
                File stdcFile = new File(bitsDir, "stdc++.h");
                Files.writeString(stdcFile.toPath(),
                        "#include <iostream>\n#include <vector>\n#include <string>\n" +
                                "#include <map>\n#include <set>\n#include <queue>\n#include <stack>\n" +
                                "#include <algorithm>\n#include <cmath>\n#include <cstring>\n" +
                                "#include <numeric>\n#include <bitset>\n#include <unordered_map>\n" +
                                "#include <unordered_set>\n#include <deque>\n#include <list>\n" +
                                "#include <iomanip>\nusing namespace std;\n");

                // Compile first (include current dir for bits)
                ProcessBuilder compilePb = new ProcessBuilder("g++", "main.cpp", "-I.", "-o", "main");
                compilePb.directory(dir);
                Process compileProcess = compilePb.start();
                compileProcess.waitFor(5, TimeUnit.SECONDS);

                if (compileProcess.exitValue() != 0) {
                    String compileError = new String(compileProcess.getErrorStream().readAllBytes());
                    Map<String, Object> compileInfo = new HashMap<>();
                    compileInfo.put("code", compileProcess.exitValue());
                    compileInfo.put("output", compileError);
                    response.put("compile", compileInfo);
                    return response;
                }

                pb = new ProcessBuilder("./main");
            } else {
                throw new IllegalArgumentException("Unsupported language: " + language);
            }

            pb.directory(dir);
            Process process = pb.start();

            boolean finished = process.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                runInfo.put("code", 124);
                runInfo.put("output", "Execution timed out (5s limit).");
            } else {
                runInfo.put("code", process.exitValue());
                String output = new String(process.getInputStream().readAllBytes());
                String error = new String(process.getErrorStream().readAllBytes());
                runInfo.put("output", output.isEmpty() ? error : output);
            }

            response.put("run", runInfo);

            // Cleanup
            for (File f : dir.listFiles())
                f.delete();
            dir.delete();

        } catch (Exception e) {
            Map<String, Object> compileInfo = new HashMap<>();
            compileInfo.put("code", 1);
            compileInfo.put("output", "Server Execution Error: " + e.getMessage());
            response.put("compile", compileInfo);
        }

        return response;
    }
}
