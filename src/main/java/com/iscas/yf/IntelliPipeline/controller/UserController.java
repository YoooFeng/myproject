// package com.iscas.yf.IntelliPipeline.controller;
//
// import com.iscas.yf.IntelliPipeline.dataview.UserView;
// import com.iscas.yf.IntelliPipeline.entity.user.User;
// import com.iscas.yf.IntelliPipeline.service.dataservice.UserService;
// import org.apache.shiro.crypto.hash.Sha256Hash;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.util.UriComponentsBuilder;
//
// import java.net.URI;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// @Controller
// @RequestMapping("/user")
// public class UserController {
//
//     protected static final String MAX_LONG_AS_STRING = "9223372036854775807";
//
//     @Autowired
//     private UserService userService;
//
//     @RequestMapping(path = "/all", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//     public ResponseEntity<List<User>> getUserList(
//             @RequestParam(value="offset", defaultValue = "0") long offset,
//             @RequestParam(value="limit", defaultValue = MAX_LONG_AS_STRING) long limit
//     ) {
//
//         Map<String, Object> param = new HashMap<String, Object>();
//         param.put("offset", offset);
//         param.put("limit", limit);
//
//         List<User> userList = userService.getAllUsers();
//         if(userList.size() != 0) {
//             return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
//         }
//         return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
//     }
//
//     @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
//     public ResponseEntity<User> getUserById(@PathVariable Long id) {
//
//         User user = userService.findById(id);
//
//         if(user == null) {
//             return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
//         }
//         return new ResponseEntity<User>(userService.findById(id), HttpStatus.OK);
//     }
//
//     @RequestMapping(path = "/{id}", method = RequestMethod.DELETE, produces = "application/json; charset=utf-8")
//     public ResponseEntity<User> deleteUser(@PathVariable Long id) {
//         User user = userService.findById(id);
//         if(user == null) {
//             return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
//         }
//         userService.deleteUser(id);
//
//         return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
//     }
//
//     @RequestMapping(path = "/new",method = RequestMethod.POST, consumes = "application/json; charset=utf-8")
//     public ResponseEntity<User> createUser(@RequestBody UserView.Item item, UriComponentsBuilder ucb) {
//
//         User user = new User();
//         user.setEmail(item.email);
//         user.setUsername(item.username);
//         user.setPassword( new Sha256Hash(item.password).toHex());
//
//         // 新建用户
//         User saved = userService.createUser( user );
//
//         HttpHeaders headers = new HttpHeaders();
//
//         URI locationURI = ucb.path("/user/").path(String.valueOf(saved.getId())).build().toUri();
//         headers.setLocation(locationURI);
//
//         ResponseEntity<User> responseEntity = new ResponseEntity<User>(saved, headers, HttpStatus.CREATED);
//
//         return responseEntity;
//     }
//
//     @RequestMapping(path = "/update", method = RequestMethod.PUT, produces = "application/json; charset=utf-8")
//     public ResponseEntity<User> updateUser(@RequestBody UserView.Item item) {
//
//         // 找到当前登录的用户
//         User currentUser = userService.getCurrentUser();
//
//         if(currentUser == null) {
//             return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
//         }
//
//         userService.updateUser(item);
//
//         return new ResponseEntity<User>(currentUser ,HttpStatus.OK);
//     }
//
// }
