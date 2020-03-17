package wostal.call.of.code.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wostal.call.of.code.abstracts.AbstractService;
import wostal.call.of.code.dao.ConversationDaoImpl;
import wostal.call.of.code.dao.MessageDaoImpl;
import wostal.call.of.code.dao.UserDaoImpl;
import wostal.call.of.code.dto.ConversationDto;
import wostal.call.of.code.dto.UserWithoutPassword;
import wostal.call.of.code.entity.Conversation;
import wostal.call.of.code.entity.User;

@Service
public class ConversationService extends AbstractService<Conversation, ConversationDaoImpl> {

	@Autowired
	private ConversationDaoImpl dao;

	@Autowired
	private UserDaoImpl userDao;

	@Autowired
	private MessageDaoImpl messageDao;

	@PostConstruct
	public void initialize() {
		super.dao = dao;
	}
	
	public List<Conversation> getUserConferences(User user) {
		return dao.getUserConferences(user);
	}

	public Long addUserToConversation(Long idUser, Long idConversation) {
		return dao.addUserToConversation(idUser, idConversation);
	}

	public Conversation get(String uuid) {
		return dao.get(uuid);
	}

	@Transactional
	public Conversation createConversation(User user, ConversationDto conversationDto) throws Exception {
		String conversationName = getConversationName(user, conversationDto);
		conversationDto.setName(conversationName);
		addUserIfNotInConversation(user, conversationDto);
		String uuid = createConversationUUID(conversationDto);
		Conversation conversation = dao.get(uuid);
		if (conversation == null) {
			conversation = createConversation(uuid, conversationName, conversationDto);
			Long idConversation = dao.create(conversation);
			for (String nick : conversationDto.getNicks()) {
				User u = userDao.get(nick);
				if(u==null) throw new Exception("Nie znaleziono u¿ytkownika do konwersacji: " + nick);
				addUserToConversation(u.getId(), idConversation);
			}
			return conversation;
		} else {
			if (conversation.getName().equals(user.getNick()))
				conversation.setName(conversationName);
			return conversation;
		}
	}

	private String getConversationName(User user, ConversationDto conversationDto) {
		if(conversationDto.getName()!=null && !conversationDto.getName().equals("")) {
			return conversationDto.getName();
		}
		if (conversationDto.getNicks().size() == 1) {
			return conversationDto.getNicks().first();
		}
		String name = "{"+user.getNick();
		for(String nick : conversationDto.getNicks()) {
			name+=", "+nick;
		}
		name+="}";
		return name;
	}

	private void addUserIfNotInConversation(User user, ConversationDto conversationDto) {
		boolean found = false;
		for (String n : conversationDto.getNicks()) {
			if (n.equals(user.getNick())) {
				found = true;
				break;
			}
		}
		if (!found) {
			conversationDto.getNicks().add(user.getNick());
		}
	}

	private String createConversationUUID(ConversationDto conversationDto) {
		String nicksString = "";
		for (String nick : conversationDto.getNicks()) {
			nicksString += nick;
		}
		if(conversationDto.getNicks().size()>2 ) {
			nicksString+=conversationDto.getName();
		}
		return DigestUtils.md5Hex(nicksString).toUpperCase();
	}
	
	private Conversation createConversation(String uuid, String conversationName, ConversationDto conversationDto) {
		System.out.println("is conference: " + conversationDto.isAsConference());
		Conversation conversation = new Conversation();
		conversation.setUuid(uuid);
		conversation.setAsConference(conversationDto.isAsConference());
		if (conversationDto.getName() == null)
			conversation.setName(conversationName);
		else {
			conversation.setName(conversationDto.getName());
		}
		return conversation;
	}

	@Transactional
	public void deleteUserFromConversation(Conversation conversation, User user) {
		dao.deleteUserFromConversation(conversation, user.getId());
		List<UserWithoutPassword> users = userDao.getConversationUsers(conversation.getId());
		if(users.size()<2) {
			messageDao.delete(conversation);
			dao.delete(conversation);
		}
	}
}
