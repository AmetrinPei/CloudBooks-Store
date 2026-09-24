package com.cloudbooks.repository;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
/**
 * @description://TODO create:2026/9/23 14:17
 * author:lenovo
 * version：V1.0
 **/
public final class FileStorage {
    private FileStorage() {}
    @SuppressWarnings("unchecked")
    public static <T> List<T> load(Path file){
        if(!Files.exists(file)){return new ArrayList<>();}
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))){
            return (List<T>)in.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("读取数据文件失败（" + file + "）：" + e.getMessage());
            return new ArrayList<>();
        }
    }
    public static void persist(Path file,List<?> data){
        try{
            if(file.getParent()!=null) Files.createDirectories(file.getParent());
            try(ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))){
                out.writeObject(new ArrayList<>(data));
            }

        }catch (IOException e){
            throw new UncheckedIOException("保存数据文件失败：" + file,e);
        }
    }
}
