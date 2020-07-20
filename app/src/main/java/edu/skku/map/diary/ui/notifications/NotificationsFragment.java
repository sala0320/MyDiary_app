package edu.skku.map.diary.ui.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.kakao.network.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import edu.skku.map.diary.LoginActivity;
import edu.skku.map.diary.MainActivity;
import edu.skku.map.diary.R;

public class NotificationsFragment extends Fragment {
    //private NotificationsViewModel notificationsViewModel;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            root = inflater.inflate(R.layout.fragment_notifications, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_notifications_landscape, container, false);
        }

        final TextView nicknameTV = root.findViewById(R.id.setting_nickname);
        final TextView emailTV = root.findViewById(R.id.setting_email);
        final TextView birthdayTV = root.findViewById(R.id.setting_birthday);
        final ImageView imageIV = root.findViewById(R.id.setting_image);
        final Button logoutBtn = root.findViewById(R.id.setting_logout);
        final Button signoutBtn = root.findViewById(R.id.setting_signout);

        final Intent  signoutIntent = new Intent(getContext(), LoginActivity.class);

        Intent intent = getActivity().getIntent();
        String image = intent.getStringExtra("image_Intent");
        String nickname = intent.getStringExtra("nickname_Intent");
        String email = intent.getStringExtra("email_Intent");
        String birthday = intent.getStringExtra("birthday_Intent");

        imageIV.setBackground(new ShapeDrawable(new OvalShape()));
        imageIV.setClipToOutline(true);
        nicknameTV.setText(nickname);
        emailTV.setText(email);
        birthdayTV.setText(birthday);
        Glide.with(this).load(image).into(imageIV);



        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        signoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(signoutIntent);
                    }
                });

            }
        });
        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                    .setMessage("정말 탈퇴하실건가요?ㅠㅠ")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                @Override
                                public void onFailure(ErrorResult errorResult) {
                                    int result = errorResult.getErrorCode();
                                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                        Toast.makeText(getContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "회원탈퇴에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onSessionClosed(ErrorResult errorResult) {
                                    Toast.makeText(getContext(), "로그인 세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                    startActivity(signoutIntent);
                                }
                                @Override
                                public void onNotSignedUp() {
                                    Toast.makeText(getContext(), "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                                @Override
                                public void onSuccess(Long result) {
                                    Toast.makeText(getContext(), "회원탈퇴에 성공했습니다.\n안녕히가세요", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            }
        });
        return root;
    }
}