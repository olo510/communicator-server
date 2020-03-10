package wostal.call.of.code.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import wostal.call.of.code.entity.Conversation;
import wostal.call.of.code.entity.User;
import wostal.call.of.code.service.ConversationService;
import wostal.call.of.code.service.MyUserPrincipal;
import wostal.call.of.code.service.StorageService;
import wostal.call.of.code.service.UserService;

@Controller
@RequestMapping("/files")
public class FileUploadController {

	@Autowired
	private StorageService storageService;

	@Autowired
	ConversationService conversationService;
	
	@Autowired
	UserService userService;

	@GetMapping(value = "/{uuid}/{filename:.+}")
	public @ResponseBody void serveFile(@PathVariable String uuid, @PathVariable String filename, HttpServletRequest request, HttpServletResponse response) {
		if(!filename.contains("..")) {
			try {
				filename = URLDecoder.decode(filename, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			Conversation conversation = conversationService.get(uuid);
			if(conversation!=null) {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
				List<User> users = userService.getConversationUsers(conversation.getId());
				boolean found = false;
				for(User u : users) {
					if(user.getId()==u.getId()) {
						found =true;
						break;
					}
				}
				if(found) {
					String uploadsDir = File.separator + "uploads" + File.separator + uuid;
			        String folderName =  request.getServletContext().getRealPath(uploadsDir);
			        String filePath = folderName+ File.separator + filename;
					File file = storageService.loadAsResource(filePath);
					InputStream targetStream;
					response.reset();
				    response.setBufferSize((int)file.getTotalSpace());
//				    response.setContentType("image/png");
					try {
						targetStream = new FileInputStream(file);
						response.getOutputStream().write(IOUtils.toByteArray(targetStream));
						return;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("uuid") String uuid, HttpServletRequest request) {
		String filename = storageService.encodeUrl(file);
		Conversation conversation = conversationService.get(uuid);
		if(conversation!=null) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
			List<User> users = userService.getConversationUsers(conversation.getId());
			boolean found = false;
			for(User u : users) {
				if(user.getId()==u.getId()) {
					found =true;
					break;
				}
			}
			if(found) {
				String uploadsDir = File.separator + "uploads" + File.separator + uuid;
		        String folderName = request.getServletContext().getRealPath(uploadsDir);
		        if(storageService.store(folderName, file)) {
		        	return request.getRequestURL().toString().replace("upload/", uuid+"/"+filename);
		        }
			}
		}
		return null;
		
	}

}