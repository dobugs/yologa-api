package com.dobugs.yologaapi.support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.dobugs.yologaapi.support.exception.FileException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FileGenerator {

    private static final FileGenerator INSTANCE = new FileGenerator();

    public static FileGenerator getInstance() {
        return INSTANCE;
    }

    public File createFile(final String savedDirectory, final String fileName) {
        final File baseDirectory = createDirectory(savedDirectory);
        final String filePath = String.join("/", baseDirectory.getPath(), fileName);
        return new File(filePath);
    }

    public void write(final File file, final String message) {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            throw new FileException("파일 작성에 실패하였습니다");
        }
    }

    private File createDirectory(final String savedDirectory) {
        final File file = new File(savedDirectory);
        if (!file.exists() && !file.mkdir()) {
            throw new FileException(String.format("폴더 생성에 실패하였습니다. [%s]", file.getName()));
        }
        return file;
    }
}
