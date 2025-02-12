package ru.vsu.cs.platon.docs.service.file;

public interface FileService {

    String saveFile(byte[] content);
    byte[] getFile(String path);
}
