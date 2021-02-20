package nio;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class PathExamples {

    public static void main(String[] args) throws IOException {

        String p = "1/2/3/4/5/h.txt";
        Path path = Paths.get("client/1/2");
        Path path1 = Paths.get("client");
        System.out.println(path.getParent());
        System.out.println(path.toAbsolutePath().getParent());
        System.out.println(path.resolve(path1));

        WatchService service = FileSystems.getDefault()  //определяем с камим репозиторием работает . по дефолту работаем с репо в котором у нас проект
                .newWatchService();

        new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = service.take(); // block
                    List<WatchEvent<?>> events = key.pollEvents(); //key какойто событие и с помощью pollEvebt определяем его как я понял из 45 строчки
                    if (key.isValid()) {
                        for (WatchEvent<?> event : events) {
                            // sync
                            System.out.println(event.count() + " "
                                    + event.kind() + " " + event.context());
                        }
                    }
                    key.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        path.register(service,  //регистрируем события которые могут случиться с файлами
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }
}
