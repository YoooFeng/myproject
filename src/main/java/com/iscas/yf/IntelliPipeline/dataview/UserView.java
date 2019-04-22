package com.iscas.yf.IntelliPipeline.dataview;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.iscas.yf.IntelliPipeline.entity.user.User;

import java.util.List;

public class UserView {
    public static class Item{
        public Long id;
        public String username;
        public String email;
        public String password;
    }

    private static Function<User, Item> ITEM_VIEW_TRANSFORMER = new Function<User, Item>() {
        @Override
        public Item apply(User user) {
            if(user == null) return null;
            Item userItem = new Item();
            userItem.id = user.getId();
            userItem.username = user.getUsername();
            userItem.password = user.getPassword();
            return userItem;
        }
    };


    public static Item detailedViewOf(User input){
        return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
    }

    public static List<Item> detailedViewListOf(List<User> input){
        return input == null ? ImmutableList.<Item> of()
                : Lists.transform(input, ITEM_VIEW_TRANSFORMER);
    }
}
