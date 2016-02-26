package xlagunas.cat.andrtc;

import javax.inject.Singleton;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import xlagunas.cat.domain.executor.PostExecutionThread;

/**
 * Created by xlagunas on 26/02/16.
 */

/**
 * MainThread (UI Thread) implementation based on a {@link rx.Scheduler}
 * which will execute actions on the Android UI thread
 * To avoid dependencies on Android sdk on the domain layer this is needed
 */
@Singleton
public class UIThread implements PostExecutionThread{

    public UIThread(){}


    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
