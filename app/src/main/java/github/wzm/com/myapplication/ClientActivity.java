package github.wzm.com.myapplication;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import github.wzm.com.myaidl.MyRemoteService;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = ClientActivity.class.getSimpleName();
    private MyRemoteService mMyRemoteService;
    private TextView mGet_tv;
    private TextView mSet_tv;
    private TextView mBind_tv;
    private TextView mUnbind_tv;
    private boolean isBind = false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyRemoteService = MyRemoteService.Stub.asInterface(service);
            isBind = true;
            Log.i(TAG, "onServiceConnected: true");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        mGet_tv = (TextView) findViewById(R.id.get_tv);
        mSet_tv = (TextView) findViewById(R.id.set_tv);
        mBind_tv = (TextView) findViewById(R.id.bind_tv);
        mUnbind_tv = (TextView) findViewById(R.id.unbind_tv);
        mGet_tv.setOnClickListener(this);
        mSet_tv.setOnClickListener(this);
        mBind_tv.setOnClickListener(this);
        mUnbind_tv.setOnClickListener(this);
        initView();
        boolean proessRunning = isProessRunning(this, "github.wzm.com.myaidl.RemoteService");
        if(proessRunning==false){
            Log.i("ssss==","proessRunning"+proessRunning);
        }
    }

    private void initView() {
        
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bind_tv:
                startService();
                break;
            case R.id.unbind_tv:
                endService();
                break;
            case R.id.get_tv:
                if (isBind) {
                    //通过AIDL通信
                    try {
                        Log.i(TAG, "onClick: 连接成功");
                        mMyRemoteService.getBookName();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.i(TAG, "onClick: 连接失败");
                }
                break;
            case R.id.set_tv:
                if (isBind) {
                    try {
                        mMyRemoteService.setBookNumber(23);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.i(TAG, "onClick: 连接失败");
                }
                break;
        }
    }


    /**
     * 开始服务
     */
    private void startService() {
  /*      Intent service = new Intent();
        service.setPackage("com.vboss.okline");
        service.setAction("com.vboss.okline.aidl.ExpressPresenterAidlService");
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);
*/
        Intent services = new Intent();
    /*    services.setPackage("github.wzm.com.myaidl");
        services.setAction("github.wzm.com.myaidl.RemoteService");*/
        services.setComponent(new ComponentName("github.wzm.com.myaidl","github.wzm.com.myaidl.RemoteService"));

        bindService(services, mServiceConnection, BIND_AUTO_CREATE);
        Toast.makeText(this,"走了开启远程服务",Toast.LENGTH_SHORT).show();
        // Toast.makeText(this, "绑定服务成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 停止服务
     */
    private void endService() {
        unbindService(mServiceConnection);
        Toast.makeText(this, "解除绑定服务成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 判断进程是否开启
     * @param context
     * @param proessName
     * @return
     */
    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : runningAppProcesses){
            if(info.processName.equals(proessName)){
                //Log.i("Service2进程", ""+info.processName);
                isRunning = true;
            }
        }

        return isRunning;
    }
}
