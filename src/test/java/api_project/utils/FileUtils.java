package api_project.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {
    /**
     * Загружает файл по указанному URL и сохраняет его во временную директорию.
     *
     * @param fileUrl  — ссылка на файл, который нужно скачать
     * @param fileName — имя файла, под которым он будет сохранён локально
     * @return объект File, указывающий на сохранённый временный файл
     * @throws IOException если при загрузке или записи файла произошла ошибка
     */
    public static File downLoadFileFromUrl(String fileUrl, String fileName) throws IOException {
        // Создаём объект URL для загрузки файла
        URL url = new URL(fileUrl);
        // Определяем путь временной директории и имя файла
        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        // Открываем поток для чтения данных по URL
        try (InputStream inputStream = url.openStream()) {
            // Копируем содержимое потока в локальный временный файл, заменяя при необходимости существующий
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        // Возвращаем объект файла, чтобы его можно было использовать в тестах или других методах
        return tempFile;
    }
}
