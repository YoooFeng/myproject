package com.iscas.yf.IntelliPipeline.dao;


import com.iscas.yf.IntelliPipeline.entity.user.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserDAO extends PagingAndSortingRepository<User, Long>{

}
