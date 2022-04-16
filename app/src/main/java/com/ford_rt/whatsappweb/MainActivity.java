package com.ford_rt.whatsappweb;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends AppCompatActivity {
    private final CustomNavigationDelegate navigationDelegate = new CustomNavigationDelegate();
    private static GeckoRuntime sRuntime;
    GeckoSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, YourService.class));
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        GeckoView view = findViewById(R.id.geckoview);
        session = new GeckoSession();

        final ExamplePermissionDelegate permission = new ExamplePermissionDelegate();
        session.setPermissionDelegate(permission);

        session.setNavigationDelegate(navigationDelegate);

        session.getSettings().setUserAgentOverride("Mozilla/5.0 (X11; Linux armv7l) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.197 Safari/537.36");

        session.setContentDelegate(new GeckoSession.ContentDelegate() {});

        if (sRuntime == null) sRuntime = GeckoRuntime.create(this);
        session.open(sRuntime);

        view.setSession(session);

        session.loadUri("https://web.whatsapp.com/");
    }

    @Override
    public void onBackPressed() {
        if (navigationDelegate.canGoBack) {
            session.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

class CustomNavigationDelegate implements GeckoSession.NavigationDelegate {
    public boolean canGoBack = false;

    @Override
    public void onCanGoBack(@NonNull GeckoSession session, boolean canGoBack) {
        this.canGoBack = canGoBack;
    }
}

class ExamplePermissionDelegate implements GeckoSession.PermissionDelegate {
    private GeckoSession.PermissionDelegate.Callback mCallback;

    public void onRequestPermissionsResult(final String[] permissions, final int[] grantResults) {
        if (mCallback == null) return;
        final Callback cb = mCallback;
        mCallback = null;

        for (final int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                // At least one permission was not granted.
                cb.reject();
                return;
            }
        }

        cb.grant();
    }

    @Override
    public void onContentPermissionRequest(@NonNull GeckoSession session, String uri, int type, @NonNull Callback callback) {
        if (type == PERMISSION_DESKTOP_NOTIFICATION) {
            callback.grant();
        } else {
            callback.reject();
        }
    }
}

class YourService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
        return super.onStartCommand(intent, flags, startId);
    }
}