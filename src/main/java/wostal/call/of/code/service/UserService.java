package wostal.call.of.code.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import wostal.call.of.code.abstracts.AbstractService;
import wostal.call.of.code.dao.RoleDaoImpl;
import wostal.call.of.code.dao.UserDaoImpl;
import wostal.call.of.code.dto.UserWithoutPassword;
import wostal.call.of.code.entity.Conversation;
import wostal.call.of.code.entity.Role;
import wostal.call.of.code.entity.User;

@Service
public class UserService extends AbstractService<User, UserDaoImpl> {

	@Autowired
	private UserDaoImpl dao;
	
	@Autowired
	private ConversationService conversationService;

	@Autowired
	private RoleDaoImpl roleDao;

	@PostConstruct
	public void initialize() {
		super.dao = dao;
	}

	public User get(String email) {
		return dao.get(email);
	}
	
	public User userExist(String nick, String password) {
		User user = dao.get(nick);
		if(user==null) return null;
		if(new BCryptPasswordEncoder().matches(password, user.getPassword())) {
			return user;
		}
		return null;
	}

	public List<Role> getUserRoles(User user) {
		return roleDao.getUserRoles(user);
	}
	
	public List<UserWithoutPassword> getContacts(User user) {
		return dao.getContacts(user);
	}
	
	public List<UserWithoutPassword> getConversationUsers(Long idConversation) {
		return dao.getConversationUsers(idConversation);
	}
	
	public List<UserWithoutPassword> getConversationUsers(String uuidConversation) {
		Conversation conversation = conversationService.get(uuidConversation);
		return dao.getConversationUsers(conversation.getId());
	}

	public Long addRoleToUser(User user, String role) {
		return roleDao.addRoleToUser(user, role);
	}

	public Long addRoleToUser(Long idUser, String role) {
		return roleDao.addRoleToUser(idUser, role);
	}

}
