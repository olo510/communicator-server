package wostal.call.of.code.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import wostal.call.of.code.dao.CreateDBDao;

@Component
class InitDB {

	@Autowired
	private CreateDBDao createDBDao;
	
    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    void contextRefreshedEvent() {
    	createDBDao.create();
//        User user = new User();
//        user.setNick("user3");
//        user.setPassword(new BCryptPasswordEncoder().encode("user3"));
//        Long idUser = userService.create(user);
//        userService.addRoleToUser(idUser, "ROLE_USER");
//        User user2 = new User();
//        user2.setNick("user4");
//        user2.setPassword(new BCryptPasswordEncoder().encode("user4"));
//        Long idUser2 = userService.create(user2);
//        userService.addRoleToUser(idUser2, "ROLE_USER");
        
    }
}