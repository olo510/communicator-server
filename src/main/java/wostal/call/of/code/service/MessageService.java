package wostal.call.of.code.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wostal.call.of.code.abstracts.AbstractService;
import wostal.call.of.code.dao.MessageDaoImpl;
import wostal.call.of.code.entity.Message;

@Service
public class MessageService extends AbstractService<Message, MessageDaoImpl> {

	@Autowired
	private MessageDaoImpl dao;
	
	
	@PostConstruct
	public void initialize() {
		super.dao = dao;
	}
	
	public List<Message> getMessages(int offset, int limit, Long idConversation) {
		return dao.getMessages(offset, limit, idConversation);
	}
	
	public List<Message> search(Long idConversation, String content){
		return dao.search(idConversation, content);
	}

}
