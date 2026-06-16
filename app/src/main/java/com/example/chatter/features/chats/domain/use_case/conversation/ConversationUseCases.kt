package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.core.domain.use_case.FormateTime

data class ConversationUseCases(
    val getChatState: GetChatState,
    val observeMemberInfos: ObserveMemberInfos,
    val sendMemberInfo: SendMemberInfo,
    val chatClose: ChatClose,
    val sendMessage: SendMessage,
    val getMessages: GetMessages,
    val observeMessage: ObserveMessage,
    val sendClosedChatMsg: SendClosedChatMsg,
    val getCallees: GetCallees,
    val markMessagesAsRead: MarkMessagesAsRead,
    val chatOpen: ChatOpen,
    val initChatSession: InitChatSession,
    val updateContactLastSeen: UpdateContactLastSeen,
    val saveIncomingMessage: SaveIncomingMessage,
    val formateTime: FormateTime,
)
