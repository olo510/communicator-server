package wostal.call.of.code.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wostal.call.of.code.abstracts.AbstractException;
import wostal.call.of.code.dto.AuthorizeDto;
import wostal.call.of.code.dto.ConversationDto;
import wostal.call.of.code.dto.Response;
import wostal.call.of.code.dto.UserWithoutPassword;
import wostal.call.of.code.entity.Conversation;
import wostal.call.of.code.entity.User;
import wostal.call.of.code.service.ConversationService;
import wostal.call.of.code.service.ExceptionHandler;
import wostal.call.of.code.service.MessageService;
import wostal.call.of.code.service.MyUserPrincipal;
import wostal.call.of.code.service.UserService;

@RestController
@Validated
public class RestApiController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ConversationService conversationService;

	@Autowired
	private MessageService messageService;
	
	
	@PostMapping(value = { "/authorize" }, produces = "text/plain;charset=UTF-8")
	public String authotize(@RequestBody AuthorizeDto authorizeDto, HttpServletResponse response) {
		try {
			if(userService.userExist(authorizeDto.nick, authorizeDto.password)) {
				return "ok";				
			}else {
				throw new AbstractException("U¿ytkownik nie istnieje");
			}
		}catch(Exception e) {
			response.setStatus(500);
			return ExceptionHandler.handle(e);
		}
	}

	@GetMapping("/contacts")
	public Response getContacts() {
		Response response = new Response();
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
			response.body = userService.getContacts(user);
			response.code=200;
			response.message="ok";
		}catch(Exception e) {
			response.code=500;
			response.message = ExceptionHandler.handle(e);
		}
		return response;
	}
	
	@GetMapping("/conferences")
	public Response getConferences() {
		Response response = new Response();
		try{
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
			response.body = conversationService.getUserConferences(user); 
			response.code=200;
			response.message="ok";
		}catch(Exception e) {
			response.code=500;
			response.message = ExceptionHandler.handle(e);
		}
		return response;
	}
	
	@PostMapping(value = { "/conferences/users/remove" }, produces = "text/plain;charset=UTF-8")
	public String removeUserFromConversation(@RequestBody String uuidConversation, HttpServletResponse response) {
		try {
			Conversation conversation = conversationService.get(uuidConversation);
			if (conversation == null) throw new AbstractException("Nie znaleziono konwersacji o podanym UUID");
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
			conversationService.deleteUserFromConversation(conversation, user);			
			return "ok";
		}catch(Exception e) {
			response.setStatus(500);
			return ExceptionHandler.handle(e);
		}
	}
	
	@PostMapping(value = "/conferences/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "text/plain;charset=UTF-8")
	public String createConversation(@RequestBody ConversationDto conversationDto, HttpServletResponse response) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
			Conversation conversation;
			conversation = conversationService.createConversation(user, conversationDto);
			return conversation.getUuid();
		} catch (Exception e) {
			response.setStatus(500);
			return ExceptionHandler.handle(e);
		}
	}
	
	@GetMapping(path = { "/messages/get/{uuidConversation}/{offset}" }, produces = "text/plain;charset=UTF-8")
	public Response getMessages(@PathVariable String uuidConversation, @PathVariable Integer offset) {
		Response response = new Response();
		try {
			Conversation conversation = conversationService.get(uuidConversation);
			if(conversation==null) throw new AbstractException("Nie znaleziono konwersacji o podanym UUID");
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
			List<UserWithoutPassword> users = userService.getConversationUsers(conversation.getId());
			boolean found = false;
			for(UserWithoutPassword u : users) {
				if(user.getId()==u.getId()) {
					found =true;
					break;
				}
			}
			if(found) {
				response.body = messageService.getMessages(offset, 10, conversation.getId());
			}else {
				throw new AbstractException("Nie jesteœ uczestnikiem danej konwersacji");
			}			
		}catch(Exception e) {
			response.code=500;
			response.message = ExceptionHandler.handle(e);
		}
		return response;
	}
	
	@GetMapping(path = { "/messages/search/{uuidConversation}/{content}" })
	public Response searchMessage(@PathVariable String uuidConversation, @PathVariable String content) {
		Response response = new Response();
		try {
			Conversation conversation = conversationService.get(uuidConversation);
			if(conversation==null) throw new AbstractException("Nie znaleziono konwersacji o podanym UUID");
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
			List<UserWithoutPassword> users = userService.getConversationUsers(conversation.getId());
			boolean found = false;
			for(UserWithoutPassword u : users) {
				if(user.getId()==u.getId()) {
					found =true;
					break;
				}
			}
			if(found) {
				response.body = messageService.search(conversation.getId(), content);
			}else {
				throw new AbstractException("Nie jesteœ uczestnikiem danej konwersacji");
			}			
		}catch(Exception e) {
			response.code=500;
			response.message = ExceptionHandler.handle(e);
		}
		return response;
	}

}
