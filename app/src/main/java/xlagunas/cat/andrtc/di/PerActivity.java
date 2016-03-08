package xlagunas.cat.andrtc.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by xlagunas on 2/03/16.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {}