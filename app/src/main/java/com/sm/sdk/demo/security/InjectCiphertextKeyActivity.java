package com.sm.sdk.demo.security;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.sm.sdk.demo.BaseAppCompatActivity;
import com.sm.sdk.demo.MyApplication;
import com.sm.sdk.demo.R;
import com.sm.sdk.demo.utils.ByteUtil;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;

public class InjectCiphertextKeyActivity extends BaseAppCompatActivity {
    private EditText mEditTargetPkgName;
    private EditText mEditKeyIndex;
    private EditText mEditKeyValue;
    private EditText mEditCheckValue;
    private EditText mEditEncryptIndex;

    private int mKeyType = AidlConstantsV2.Security.KEY_TYPE_TMK;
    private int mKeyAlgType = AidlConstantsV2.Security.KEY_ALG_TYPE_3DES;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_save_key_cipher_text);
        initToolbarBringBack(R.string.security_inject_cipher_text_key);
        initView();
    }

    private void initView() {
        RadioGroup keyTypeRadioGroup = findViewById(R.id.key_type);
        keyTypeRadioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.rb_tmk:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_TMK;
                            break;
                        case R.id.rb_pik:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_PIK;
                            break;
                        case R.id.rb_tdk:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_TDK;
                            break;
                        case R.id.rb_mak:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_MAK;
                            break;
                        case R.id.rb_rec_key:
                            mKeyType = AidlConstantsV2.Security.KEY_TYPE_REC;
                            break;
                    }
                }
        );

        RadioGroup keyAlgTypeRadioGroup = findViewById(R.id.key_alg_type);
        keyAlgTypeRadioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    switch (checkedId) {
                        case R.id.rb_3des:
                            mKeyAlgType = AidlConstantsV2.Security.KEY_ALG_TYPE_3DES;
                            break;
                        case R.id.rb_sm4:
                            mKeyAlgType = AidlConstantsV2.Security.KEY_ALG_TYPE_SM4;
                            break;
                        case R.id.rb_aes:
                            mKeyAlgType = AidlConstantsV2.Security.KEY_ALG_TYPE_AES;
                            break;
                    }
                }
        );

        mEditTargetPkgName = findViewById(R.id.target_pkg_name);
        mEditKeyIndex = findViewById(R.id.key_index);
        mEditKeyValue = findViewById(R.id.key_value);
        mEditCheckValue = findViewById(R.id.check_value);
        mEditEncryptIndex = findViewById(R.id.encrypt_index);

        mEditTargetPkgName.setText("com.sunmi.sdk.demov2");
        findViewById(R.id.target_pkg_lay).setVisibility(View.VISIBLE);
        findViewById(R.id.mb_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.mb_ok:
                injectCipherTextKey();
                break;
        }
    }

    private void injectCipherTextKey() {
        try {
            String targetPkgName = mEditTargetPkgName.getText().toString().trim();
            String keyValueStr = mEditKeyValue.getText().toString().trim();
            String keyIndexStr = mEditKeyIndex.getText().toString().trim();
            String checkValueStr = mEditCheckValue.getText().toString().trim();
            String encryptIndexStr = mEditEncryptIndex.getText().toString().trim();

            if (TextUtils.isEmpty(targetPkgName)) {
                showToast(R.string.security_target_pkg_name_hint);
                mEditTargetPkgName.requestFocus();
                return;
            }
            if (keyValueStr.length() == 0 || keyValueStr.length() % 8 != 0) {
                showToast(R.string.security_key_value_hint);
                return;
            }

            if (checkValueStr.length() != 0) {
                if (mKeyAlgType == AidlConstantsV2.Security.KEY_ALG_TYPE_SM4) {
                    if (checkValueStr.length() > 32 || checkValueStr.length() % 4 != 0) {
                        showToast(R.string.security_check_value_hint);
                        return;
                    }
                } else {
                    if (checkValueStr.length() > 16 || checkValueStr.length() % 4 != 0) {
                        showToast(R.string.security_check_value_hint);
                        return;
                    }
                }
            }

            int encryptIndex;
            try {
                encryptIndex = Integer.parseInt(encryptIndexStr);
                if (encryptIndex < 0 || encryptIndex > 199) {
                    showToast(R.string.security_mksk_key_index_hint);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(R.string.security_mksk_key_index_hint);
                return;
            }

            int keyIndex;
            try {
                keyIndex = Integer.parseInt(keyIndexStr);
                if (keyIndex < 0 || keyIndex > 199) {
                    showToast(R.string.security_mksk_key_index_hint);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(R.string.security_mksk_key_index_hint);
                return;
            }

            byte[] keyValue = ByteUtil.hexStr2Bytes(keyValueStr);
            byte[] checkValue = ByteUtil.hexStr2Bytes(checkValueStr);
            addStartTimeWithClear("injectCiphertextKey()");
            int result = MyApplication.app.securityOptV2.injectCiphertextKey(targetPkgName, mKeyType, keyValue, checkValue, encryptIndex, mKeyAlgType, keyIndex);
            addEndTime("injectCiphertextKey()");
            toastHint(result);
            showSpendTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
