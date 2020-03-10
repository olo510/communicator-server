package wostal.call.of.code.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
	@Autowired
	ResourceLoader resourceLoader;

	public File loadAsResource(String filePath) {
		try {
			return new File(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean store(String folderName, MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				if (!new File(folderName).exists()) {
					new File(folderName).mkdir();
				}
				String orgName = file.getOriginalFilename();
				String filePath = folderName + File.separator + orgName;
				File dest = new File(filePath);
				file.transferTo(dest);
				return true;
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		return false;
	}
	
	public String encodeUrl(MultipartFile file) {
		String orgName;
		try {
			orgName = URLEncoder.encode(file.getOriginalFilename(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			orgName = file.getOriginalFilename();
		}
		return orgName;
	}

}
