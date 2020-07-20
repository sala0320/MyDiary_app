package edu.skku.map.diary.ui.home;

import java.util.HashMap;
import java.util.Map;

public class MemoInfo {
    public String time;
    public String memo;

    public MemoInfo(){

    }
    public MemoInfo(String time, String memo) {
        this.time = time;
        this.memo = memo;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("memo", memo);
        result.put("time", time);
        return result;
    }
}
