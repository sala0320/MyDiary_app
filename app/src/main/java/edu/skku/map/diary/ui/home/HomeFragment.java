package edu.skku.map.diary.ui.home;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.skku.map.diary.MainActivity;
import edu.skku.map.diary.R;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeFragment extends Fragment {
    private DatabaseReference mPostReference;
    private DatabaseReference mDelReference;

    String nickname = "", clickday = "";
    ImageButton addbtn, deletbtn;
    ListView memolist;
    MemoAdapter adapter;
    ArrayList<MemoItem> data;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            root = inflater.inflate(R.layout.fragment_home, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_home_landscape, container, false);
        }

        mPostReference = FirebaseDatabase.getInstance().getReference("memo_list");
        Intent intent = getActivity().getIntent();
        nickname = intent.getStringExtra("nickname_Intent");
        memolist = (ListView) root.findViewById(R.id.memo_list);
        memolist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("정말로 삭제하시겠습니까?");
                builder.setMessage("");
                builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickday = data.get(position).getTime();
                        mDelReference = FirebaseDatabase.getInstance().getReference("memo_list").child(nickname).child(clickday);
                        mDelReference.removeValue();
                        data.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNeutralButton("취소", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

        data = new ArrayList<MemoItem>();
        adapter = new MemoAdapter(getContext(),data);
        memolist.setAdapter(adapter);
        getFirebaseDatabase();

        addbtn = (ImageButton) root.findViewById(R.id.addmemo_button);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getContext(), AddMemoActivity.class);
                addIntent.putExtra("nameIntent",nickname );
                startActivity(addIntent);
            }
        });

        return root;
    }

    public void getFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    MemoInfo get = postSnapshot.getValue(MemoInfo.class);
                    Log.d("third_key", get.time + get.memo);
                    MemoItem memo = new MemoItem(get.time, get.memo);
                    data.add(memo);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child(nickname).addValueEventListener(postListener);
    }
    public void deleteFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    MemoInfo get = postSnapshot.getValue(MemoInfo.class);
                    Log.d("third_key", get.time + get.memo);
                    MemoItem memo = new MemoItem(get.time, get.memo);
                    data.add(memo);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child(nickname).addValueEventListener(postListener);
    }
}