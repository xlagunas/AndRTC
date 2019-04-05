package cat.xlagunas.viv.push

import cat.xlagunas.push.MessageProcessor
import cat.xlagunas.push.MessageType
import cat.xlagunas.push.PushMessageProcessorKey
import cat.xlagunas.push.PushTokenProvider
import cat.xlagunas.push.PushTokenProviderImpl
import cat.xlagunas.push.PushTokenRepository
import cat.xlagunas.push.PushTokenRepositoryImpl
import cat.xlagunas.viv.contact.CallMessageProcessor
import cat.xlagunas.viv.contact.ContactsMessageProcessor
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PushModule {

    @Binds
    @IntoMap
    @PushMessageProcessorKey(MessageType.CREATE_CALL)
    abstract fun provideCallsMessageProcessor(callMessageProcessor: CallMessageProcessor): MessageProcessor

    @Binds
    @IntoMap
    @PushMessageProcessorKey(MessageType.REQUEST_FRIENDSHIP)
    abstract fun provideContactsMessageProcessor(contactsMessageProcessor: ContactsMessageProcessor): MessageProcessor

    //TODO DIFFERENT KEYS RETURN SAME INSTANCE SO FAR UNTIL DIFFERENT ACTIONS ARE IMPLEMENTED

    @Binds
    @IntoMap
    @PushMessageProcessorKey(MessageType.ACCEPT_FRIENDSHIP)
    abstract fun provideAcceptedContactsMessageProcessor(contactsMessageProcessor: ContactsMessageProcessor): MessageProcessor

    @Binds
    @IntoMap
    @PushMessageProcessorKey(MessageType.REJECT_FRIENDSHIP)
    abstract fun provideRejectContactsMessageProcessor(contactsMessageProcessor: ContactsMessageProcessor): MessageProcessor

    @Binds
    abstract fun providePushTokenRepository(pushTokenRepository: PushTokenRepositoryImpl): PushTokenRepository

    @Binds
    abstract fun providePushTokenProvider(pushTokenProvider: PushTokenProviderImpl): PushTokenProvider
}