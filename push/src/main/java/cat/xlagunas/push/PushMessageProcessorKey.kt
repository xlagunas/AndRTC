package cat.xlagunas.push

import cat.xlagunas.push.MessageType
import dagger.MapKey

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class PushMessageProcessorKey(val value: MessageType)