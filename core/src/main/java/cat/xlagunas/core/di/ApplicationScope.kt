package cat.xlagunas.core.di

import javax.inject.Qualifier

/**
 * Qualifies the application-wide CoroutineScope used by work that outlives any screen.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope
