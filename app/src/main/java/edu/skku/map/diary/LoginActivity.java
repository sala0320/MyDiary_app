package edu.skku.map.diary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends Activity {
    Button loginBtn;
    LoginButton kakaoBtn;
    String image = "", nickname = "", email = "", birthday = "";
    Intent loginIntent;
    public static int log = 0;
    public SessionCallback callback;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance().getReference();
        loginBtn = (Button) findViewById(R.id.login_button);
        kakaoBtn = (LoginButton) findViewById(R.id.kakao_button);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kakaoBtn.performClick();
                callback = new SessionCallback();
                Session.getCurrentSession().addCallback(callback);
                Session.getCurrentSession().checkAndImplicitOpen();
                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("TAG", "세션연결");
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
            Log.d("TAG", "세션연결실패");
            setContentView(R.layout.activity_login); // 세션 연결이 실패했을때
        }                                            // 로그인화면을 다시 불러옴
    }

    protected void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.id");
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSuccess(MeV2Response result) {  //성공 시 userProfile 형태로 반환
                loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                image = result.getKakaoAccount().getProfile().getProfileImageUrl();
                nickname = result.getKakaoAccount().getProfile().getNickname();
                email = result.getKakaoAccount().getEmail();
                birthday = result.getKakaoAccount().getBirthday().substring(0, 2) + "월 " + result.getKakaoAccount().getBirthday().substring(2, 4) + "일";
                loginIntent.putExtra("image_Intent", image);
                loginIntent.putExtra("nickname_Intent", nickname);
                loginIntent.putExtra("email_Intent", email);
                loginIntent.putExtra("birthday_Intent", birthday);
                saveShared(image, nickname);
                postFirebaseDatabase(true);
                startActivity(loginIntent);
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.d("error", errorResult.getErrorMessage());
                redirectLoginActivity();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("TAG", "닫힘" + errorResult.getErrorMessage());
                redirectLoginActivity();
            }

        });
    }

    public void postFirebaseDatabase(boolean add) {
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        Log.d("TAG", "파이어베이스" + image + "\t" + nickname);
        if (add) {
            UserInfo post = new UserInfo(image, nickname, email, birthday);
            postValues = post.toMap();
        }
        childUpdates.put(nickname, postValues);
        database.updateChildren(childUpdates);
        clearET();
    }

    public void clearET() {
        nickname = "";
        image = "";
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void saveShared(String image, String nickname) {
        Log.d("TAG", "saveshare" + image + nickname);
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", image);
        editor.putString("nickname", nickname);
        editor.apply();
    }

    private void loadShared() {
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        image = pref.getString("id", "");
        nickname = pref.getString("nickname", "");
        Log.d("TAG", "loadshare" + image + nickname);
    }

    public void onBackPressed() {
        finish();
    }
}
