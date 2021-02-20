package nio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class    FilesExamples {

    public static void main(String[] args) throws IOException {

        StandardCopyOption co;
        StandardOpenOption oo;
        StandardCharsets sc;
        // CREATE - пересоздание
        // APPEND - дописывание

        Files.write(Paths.get("client/2.txt"),
                "Hello world!".getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND); //оч важно ,мы можем без последнего параметра но тогда будет удаляться текст и на его место писаться тот что задали
        // а с третим параметром мы можем выбрать каким образом будем работать

        Files.copy(    //в копи вообще и инпутстрим можно отдавать
                Paths.get("client/2.txt"),
                Paths.get("client/4.txt"),
                StandardCopyOption.REPLACE_EXISTING);  //ну тип данные из одного в другой копируем тут выбираем каким образом

        Files.walkFileTree(Paths.get("./"), new HashSet<>(),1, //этот хешсет нужен для определения глубины прохождения

                //проходим по всем репозиториям начиная с какого то
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        System.out.println(file);
                        return super.visitFile(file, attrs);
                    }
                });
    }
}
