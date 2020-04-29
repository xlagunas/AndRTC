package cat.xlagunas.viv.push

import cat.xlagunas.contact.ui.ContactsMessageProcessor
import cat.xlagunas.push.MessageProcessor
import cat.xlagunas.push.MessageType
import cat.xlagunas.push.PushMessageProcessorKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PushMessageProcessorsModule {

    @Binds
    @IntoMap
    @PushMessageProcessorKey(MessageType.REQUEST_FRIENDSHIP)
    abstract fun provideContactsMessageProcessor(contactsMessageProcessor: ContactsMessageProcessor): MessageProcessor

    // TODO DIFFERENT KEYS RETURN SAME INSTANCE SO FAR UNTIL DIFFERENT ACTIONS ARE IMPLEMENTED

    @Binds
    @IntoMap
    @PushMessageProcessorKey(MessageType.ACCEPT_FRIENDSHIP)
    abstract fun provideAcceptedContactsMessageProcessor(contactsMessageProcessor: ContactsMessageProcessor): MessageProcessor

    @Binds
    @IntoMap
    @PushMessageProcessorKey(MessageType.REJECT_FRIENDSHIP)
    abstract fun provideRejectContactsMessageProcessor(contactsMessageProcessor: ContactsMessageProcessor): MessageProcessor
}