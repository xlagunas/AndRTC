package cat.xlagunas.andrtc.view.util;

import android.text.TextUtils;

import javax.inject.Inject;

import cat.xlagunas.andrtc.di.UserScope;


/**
 * Created by xlagunas on 9/03/16.
 */

@UserScope
public class FieldValidator {

    @Inject
    public FieldValidator(){

    }

    public boolean isValidTextField(String textValue){
        return !TextUtils.isEmpty(textValue);
    }

    public boolean isValidFullName(String textValue){
        return !TextUtils.isEmpty(textValue) && textValue.trim().contains(" ");
    }

    public boolean isValidPassword(String textValue){
        return !TextUtils.isEmpty(textValue) && textValue.length() >= 6;
    }

    public boolean isValidUsername(String textValue){
        return !TextUtils.isEmpty(textValue) && textValue.length() >= 6;
    }


    public boolean isValidEmail(String textValue) {
        return !TextUtils.isEmpty(textValue) && android.util.Patterns.EMAIL_ADDRESS.matcher(textValue).matches();
    }
}
