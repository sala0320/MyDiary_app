package edu.skku.map.diary.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.skku.map.diary.MainActivity;
import edu.skku.map.diary.R;

public class AddMemoActivity extends AppCompatActivity{
    private DatabaseReference mPostReference;
    String addtime = "", name = "";
    String memo = "";
    EditText memoET, timeET;
    Button addbtn;

    ListView memolist;
    ArrayList<MemoItem> data;
    MemoAdapter adapter;
    long now;
    SimpleDateFormat dateFormat;
    Date nowDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_memopopup);

        Intent intent = getIntent();
        name = intent.getStringExtra("nameIntent");

        now = System.currentTimeMillis();
        nowDate = new Date();
        dateFormat = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분ss초");
        addtime = dateFormat.format(nowDate);

        memoET = (EditText) findViewById(R.id.add_memo);
        addbtn = (Button) findViewById(R.id.add);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memo = memoET.getText().toString();
                postFirebaseDatabase(true);
                finish();
            }
        });
        data = new ArrayList<MemoItem>();
    }
    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        if(add){
            MemoInfo post = new MemoInfo(addtime,memo);
            postValues = post.toMap();
        }
        childUpdates.put("/memo_list/" + name + "/" + addtime, postValues);//id_list/ID 에 동시에 postvalue넣기
        mPostReference.updateChildren(childUpdates);
        clearET();
    }
    public void clearET() {
        memoET.setText("");
        addtime = "";
        memo = "";
    }
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}
