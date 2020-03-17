package wostal.call.of.code.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import wostal.call.of.code.dto.UserWithoutPassword;
import wostal.call.of.code.entity.Conversation;
import wostal.call.of.code.entity.Message;
import wostal.call.of.code.entity.User;
import wostal.call.of.code.service.ConversationService;
import wostal.call.of.code.service.MessageService;
import wostal.call.of.code.service.MyUserPrincipal;
import wostal.call.of.code.service.UserService;

@Controller
public class MessageController {

	private SimpMessagingTemplate template;

	@Autowired
	UserService userService;

	@Autowired
	MessageService messageService;
	
	@Autowired
	ConversationService conversationService;
	
	@Autowired
	public MessageController(SimpMessagingTemplate template) {
		this.template = template;
	}

	@MessageMapping("/message/send")
	public void createConversation(Message message) throws Exception {
		User user = userService.get(message.getIdUser());
		if(user==null) return;
		Conversation conversation = conversationService.get(message.getUuidConversation());
		if(conversation==null) return;
		message.setIdConversation(conversation.getId());
		message.setNick(user.getNick());
		messageService.create(message);
		List<UserWithoutPassword> users = userService.getConversationUsers(conversation.getUuid());
		for(UserWithoutPassword u : users) {
			this.template.convertAndSend("/message/receiv/" + u.getId(), message);
		}
		//this.template.convertAndSend("/message/receiv/" + conversation.getUuid(), message);
	}
	
	@RequestMapping(path = { "/messages/get/{uuidConversation}/{offset}" })
	@ResponseBody
	public String openConversation(@PathVariable String uuidConversation, @PathVariable Integer offset, HttpServletRequest request, Model model) {
		Conversation conversation = conversationService.get(uuidConversation);
		if(conversation==null) return "error";
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
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(messageService.getMessages(offset, 10, conversation.getId()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return "";	
	}
	
	@RequestMapping(path = { "/messages/search/{uuidConversation}/{content}" })
	@ResponseBody
	public String searchMessage(@PathVariable String uuidConversation, @PathVariable String content, HttpServletRequest request, Model model) {
		Conversation conversation = conversationService.get(uuidConversation);
		if(conversation==null) return "error";
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
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(messageService.search(conversation.getId(), content));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return "";
		
	}

}