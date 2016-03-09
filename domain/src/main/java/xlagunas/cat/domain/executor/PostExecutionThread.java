package xlagunas.cat.domain.executor;

import rx.Scheduler;

/**
 * Created by xlagunas on 26/02/16.
 */
public interface PostExecutionThread {

    Scheduler getScheduler();
}
