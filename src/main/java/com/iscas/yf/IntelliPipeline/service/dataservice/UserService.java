package com.iscas.yf.IntelliPipeline.service.dataservice;

import com.iscas.yf.IntelliPipeline.dataview.UserView;
import com.iscas.yf.IntelliPipeline.entity.user.User;

public interface UserService {

    public User createUser(UserView.Item userItem);

}
