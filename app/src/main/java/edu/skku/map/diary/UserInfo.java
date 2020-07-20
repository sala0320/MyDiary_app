package edu.skku.map.diary;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {
    public String image;
    public String nickname;
    public String email;
    public String birthday;

    public UserInfo(){

    }
    public UserInfo(String image, String nickname, String email, String birthday){
        this.image = image;
        this.nickname = nickname;
        this.email = email;
        this.birthday = birthday;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("image",image);
        result.put("nickname",nickname);
        result.put("email",email);
        result.put("birthday",birthday);
        return result;
    }
}
