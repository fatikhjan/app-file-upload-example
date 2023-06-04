package uz.pdp.app_file_upload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.app_file_upload.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity,Integer> {
}
