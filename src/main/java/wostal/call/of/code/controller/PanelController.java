package wostal.call.of.code.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import wostal.call.of.code.dto.ConversationDto;
import wostal.call.of.code.dto.UserWithoutPassword;
import wostal.call.of.code.entity.Conversation;
import wostal.call.of.code.entity.User;
import wostal.call.of.code.service.ConversationService;
import wostal.call.of.code.service.MessageService;
import wostal.call.of.code.service.MyUserPrincipal;
import wostal.call.of.code.service.UserService;

@Controller
@RequestMapping("/panel")
public class PanelController {

	@Autowired
	private ConversationService conversationService;

	@Autowired
	private UserService userService;

	@Autowired
	private MessageService messageService;

	@RequestMapping(value = { "/main" }, method = RequestMethod.GET)
	public ModelAndView main(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
		model.addAttribute("conferences", conversationService.getUserConferences(user));
		model.addAttribute("idUser", user.getId());
		model.addAttribute("uName", user.getNick());
		return new ModelAndView("index", "users", userService.getContacts(user));
	}

	@RequestMapping(value = "/createConversation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String createConversation(@RequestBody ConversationDto conversationDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
		Conversation conversation;
		try {
			conversation = conversationService.createConversation(user, conversationDto);
			return conversation.getUuid();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@RequestMapping(path = { "/openConversation/{uuidConversation}" })
	public String openConversation(@PathVariable String uuidConversation, HttpServletRequest request, Model model) {
		Conversation conversation = conversationService.get(uuidConversation);
		if (conversation == null)
			return "error";
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
		List<UserWithoutPassword> users = userService.getConversationUsers(conversation.getId());
		boolean found = false;
		for (UserWithoutPassword u : users) {
			if (user.getId() == u.getId()) {
				found = true;
				break;
			}
		}
		if (found) {
			if (conversation.getName().equals(user.getNick())) {
				if (users.size() == 2) {
					if (users.get(0).getNick().equals(user.getNick())) {
						conversation.setName(users.get(1).getNick());
					} else {
						conversation.setName(users.get(0).getNick());
					}
				} else {
					conversation.setName(users.toString());
				}
			}
			model.addAttribute("conversation", conversation);
			ObjectMapper mapper = new ObjectMapper();
			try {
				model.addAttribute("messages",
						mapper.writeValueAsString(messageService.getMessages(0, 10, conversation.getId())));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			model.addAttribute("user", user);
			return "conversation";
		} else {
			return "error";
		}
	}
	
	@RequestMapping(value = { "/removeUser" }, method = RequestMethod.POST)
	public String removeUserFromConversation(@RequestBody String uuidConversation, HttpServletRequest request, Model model) {
		Conversation conversation = conversationService.get(uuidConversation);
		if (conversation == null)
			return "error";
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
		try {
			conversationService.deleteUserFromConversation(conversation, user);			
			model.addAttribute("conferences", conversationService.getUserConferences(user));
			return "conferenceList";
		}catch(Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	@RequestMapping(path = { "/getConferences" }, method=RequestMethod.GET)
	public String getConferences(HttpServletRequest request, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = ((MyUserPrincipal) authentication.getPrincipal()).getUser();
		model.addAttribute("conferences", conversationService.getUserConferences(user));
		return "conferenceList";
	}

}