package edu.skku.map.diary.ui.home;

public class MemoItem {
    public String time;
    public String memo;

    public MemoItem(String time, String memo) {
        this.time = time;
        this.memo = memo;
    }
    public MemoItem(){

    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
