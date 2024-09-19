package com.shopme.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.user.repository.UserRepository;
import com.shopme.common.models.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepo;

	@Test
	public void testListUserByPage() {
		int pageNum = 1;
		int pageSize = 5;

		Pageable pageable = PageRequest.of(pageNum, pageSize);
		Page<User> page = userRepo.findAll(pageable);
		page.getContent().forEach(user -> System.out.println(user));
	}

	@Test
	public void testSearchfunction() {
		String keyword = String.valueOf(1);
		int pageNum = 0;
		int pageSize = 5;
		Sort sort = Sort.by("firstName").ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		Page<User> page = userRepo.findAll(keyword, pageable);
		System.out.println(page.getContent());
	}
}
