package com.lesports.bike.settings.control;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;

import com.lesports.bike.settings.utils.L;

import bike.os.fingerprint.FingerprintManager;
import bike.os.fingerprint.FingerprintManager.EnrollSession;
import bike.os.fingerprint.FingerprintManager.VerifySession;
import bike.os.fingerprint.IEnrollCallback;
import bike.os.fingerprint.IVerifyCallback;


/**
 * Created by gaowei3 on 2016/6/2.
 */
public class FingerprintControl {

    private FingerprintManager mFingerprintManager;
    private Context mContext;
    private static FingerprintControl instance;
    private FPControlCallback mFPControlCallback;

    private EnrollSession mEnrollSession;
    private VerifySession mVerifySession;

    private FingerprintControl(Context context) {
        this.mContext = context;
        mFingerprintManager =(FingerprintManager) context.getSystemService("fingerprint");
    }

    public static FingerprintControl getInstance(Context context) {
        if (instance == null) {
            instance = new FingerprintControl(context.getApplicationContext());
        }
        return instance;
    }

    public void register() {
        mVerifySession = mFingerprintManager.newVerifySession(mVerifyCallBack);
        mVerifySession.enter();
        if (mEnrollSession != null) {
            mEnrollSession.reset();
        }
        mEnrollSession = mFingerprintManager.newEnrollSession("", mEnrollCallback);
        mEnrollSession.enter();
    }

    public boolean[] loadFingerprints() {
        int fpFlag = mFingerprintManager.query("");
        if ((fpFlag >> 16) <= 0) {
            return new boolean[]{};
        }
        int count = (fpFlag >> 16 & 0xFFFF);
        boolean[] register = new boolean[count];
        for (int i = 0; i < count; i++) {
            register[i] = ((fpFlag >> i) & 0x1) > 0;
        }
        return register;
    }

    public int deleteFingerprint(int position) {
        return mFingerprintManager.delete(position, "");
    }

    private IVerifyCallback mVerifyCallBack = new IVerifyCallback.Stub() {
        @Override
        public boolean handleMessage(int msg, int arg0, int arg1, byte[] data)
                throws RemoteException {
            return false;
        }
    };

    private IEnrollCallback mEnrollCallback = new IEnrollCallback.Stub() {
        @Override
        public boolean handleMessage(int msg, int arg1, int arg2, byte[] data) throws RemoteException {
            L.d(String.format("msg = %d , arg1 = %d ,arg2 = %d", msg, arg1, arg2));
            mHandler.sendMessage(mHandler.obtainMessage(msg, arg1, arg2, data));
            return false;
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int progress = msg.arg1;
            switch (msg.what) {
                case FingerprintType.MSG_TYPE_REGISTER_PIECE:   //17
                case FingerprintType.MSG_TYPE_REGISTER_NO_EXTRAINFO:   //19
                    if (progress >= 100) {
                        mEnrollSession.save(msg.arg2);
                    }
                    mFPControlCallback.onEnrollProgress(progress);
                    break;
            }
        }
    };

    public void cancelEnroll() {
        if (mEnrollSession != null) {
            mEnrollSession.exit();
        }
    }

    public void setCallback(FPControlCallback fpControlCallback) {
        mFPControlCallback = fpControlCallback;
    }
    public interface FPControlCallback {
        void onEnrollProgress(int progress);
    }

    public boolean hasFingerPrint() {
        String info = mFingerprintManager.getInformation();
        //GFx16M_1.4.04,1.06.33_2,GFCD_GF816M_1.00.07有指纹
        //￿￿ﯮ/.ff,1.06.33_2,GFCD_GF816M_1.00.07没指纹,前面确实是乱码
        if (info != null && info.startsWith("GFx")) {
            return true;
        } else {
            return false;
        }
    }
}
