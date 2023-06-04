package uz.pdp.app_file_upload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.app_file_upload.entity.FileEntity;
import uz.pdp.app_file_upload.repository.FileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Controller
public class FileDownloadController {

    private static final String SYSTEM_FILE_LOCATION_PATH = "D:\\projects\\demo\\app_file_upload\\src\\main\\resources\\newPackage";


    @GetMapping("/getAllPhotos")
    public String getAllFiles(Model model) {
        Path folderPath = Paths.get(SYSTEM_FILE_LOCATION_PATH);
        List<File> fileList = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(folderPath)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                fileList.add(path.toFile());
            });
            if (!fileList.isEmpty()) {
                model.addAttribute("fileList", fileList);
                return "redirect:showFiles.html";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:index.html";
    }

    @Autowired
    private FileRepository fileRepository;


    @PostMapping("/save/toSystem")
    public String saveFileToSystem(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String fileName = file.getOriginalFilename();
        Path fileLocationPath = Paths.get(SYSTEM_FILE_LOCATION_PATH);

        if (!Files.exists(fileLocationPath)) {
            Files.createDirectories(fileLocationPath);
        }

        String s = UUID.randomUUID().toString();

        assert fileName != null;
        String[] split = fileName.split("\\.");
        String qisqartmasi = split[1];
        Path filePath = fileLocationPath.resolve(split[0] + s + "." + qisqartmasi);

        Files.write(filePath, file.getBytes());

        return "redirect:index.html";

    }


    @PostMapping("/save/toDataBase")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            FileEntity fileEntity = new FileEntity(file.getOriginalFilename(), file.getBytes());
            fileRepository.save(fileEntity);
            model.addAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            model.addAttribute("error", "Error uploading file: " + e.getMessage());
        }
        return "redirect:index.html";
    }
}

