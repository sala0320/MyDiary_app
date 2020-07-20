package edu.skku.map.diary.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.skku.map.diary.R;

import static android.view.View.GONE;

public class MemoAdapter extends BaseAdapter {
    private StorageReference mStorageRef;
    LayoutInflater inflater;
    private ArrayList<MemoItem> items;

    TextView timeTV, memoTV;

    public MemoAdapter (Context context, ArrayList<MemoItem> content) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = content;
    }

    @Override
    public int getCount() {
        Log.d("content_num",Integer.toString(items.size()));
        return items.size();
    }

    @Override
    public MemoItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.memo_layout, viewGroup, false);
        }

        MemoItem item = items.get(i);
        Log.d("Adapter",item.getMemo() + item.getTime());

        memoTV = (TextView)view.findViewById(R.id.memo_memo);
        timeTV = (TextView)view.findViewById(R.id.addtime);

        memoTV.setText(item.getMemo());
        memoTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        timeTV.setText(item.getTime());

    return view;
    }


}
