package com.example.hzxr.openfiredemo.net

import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider
import org.jivesoftware.smack.provider.PrivacyProvider
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider
import org.jivesoftware.smackx.provider.StreamInitiationProvider
import org.jivesoftware.smackx.provider.MultipleAddressesProvider
import org.jivesoftware.smackx.packet.SharedGroupsInfo
import org.jivesoftware.smackx.search.UserSearch
import org.jivesoftware.smackx.packet.LastActivity
import org.jivesoftware.smackx.packet.OfflineMessageInfo
import org.jivesoftware.smackx.packet.OfflineMessageRequest
import org.jivesoftware.smackx.provider.VCardProvider
import org.jivesoftware.smackx.provider.DelayInformationProvider
import org.jivesoftware.smackx.provider.MUCOwnerProvider
import org.jivesoftware.smackx.provider.MUCAdminProvider
import org.jivesoftware.smackx.provider.MUCUserProvider
import org.jivesoftware.smackx.provider.DataFormProvider
import org.jivesoftware.smackx.provider.DiscoverInfoProvider
import org.jivesoftware.smackx.provider.DiscoverItemsProvider
import org.jivesoftware.smackx.GroupChatInvitation
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider
import org.jivesoftware.smackx.packet.ChatStateExtension
import org.jivesoftware.smackx.provider.MessageEventProvider
import org.jivesoftware.smackx.provider.RosterExchangeProvider
import org.jivesoftware.smackx.PrivateDataManager
import org.jivesoftware.smack.provider.ProviderManager



/**
 * Created by Hzxr on 2018/1/4.
 */
object XmppConnection {
    val SERVER_PORT = 5222//端口
    val SERVER_HOST = "192.168.199.159"//服务器地址192.168.199.159
    val SERVER_NAME = "localhost"//域

    private var connection: XMPPConnection? = null

    private fun openConnection(){
        XMPPConnection.DEBUG_ENABLED = true
        val config = ConnectionConfiguration(SERVER_HOST, SERVER_PORT, SERVER_NAME)
        config.isReconnectionAllowed = true
        config.setSendPresence(true)
        config.isSASLAuthenticationEnabled = true
        connection = XMPPConnection(config)
        connection?.connect()
        configureConnection()
    }

    fun closeConnecttion(){
        if (connection != null){
            connection?.disconnect();
            connection = null
        }
    }

    fun getConnection(): XMPPConnection?{
        if(connection == null)
            openConnection()
        return connection
    }

    private fun configureConnection() {
        val pm = ProviderManager.getInstance()
        pm.addIQProvider("query", "jabber:iq:private",
                PrivateDataManager.PrivateDataIQProvider())
        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time",
                    Class.forName("org.jivesoftware.smackx.packet.Time"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster",
                RosterExchangeProvider())
        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event",
                MessageEventProvider())
        // Chat State
        pm.addExtensionProvider("active",
                "http://jabber.org/protocol/chatstates",
                ChatStateExtension.Provider())
        pm.addExtensionProvider("composing",
                "http://jabber.org/protocol/chatstates",
                ChatStateExtension.Provider())
        pm.addExtensionProvider("paused",
                "http://jabber.org/protocol/chatstates",
                ChatStateExtension.Provider())
        pm.addExtensionProvider("inactive",
                "http://jabber.org/protocol/chatstates",
                ChatStateExtension.Provider())
        pm.addExtensionProvider("gone",
                "http://jabber.org/protocol/chatstates",
                ChatStateExtension.Provider())
        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
                XHTMLExtensionProvider())
        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference",
                GroupChatInvitation.Provider())
        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
                DiscoverItemsProvider())
        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
                DiscoverInfoProvider())
        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", DataFormProvider())
        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
                MUCUserProvider())
        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
                MUCAdminProvider())
        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
                MUCOwnerProvider())
        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay",
                DelayInformationProvider())
        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version",
                    Class.forName("org.jivesoftware.smackx.packet.Version"))
        } catch (e: ClassNotFoundException) {
            // Not sure what's happening here.
        }
        // VCard
        pm.addIQProvider("vCard", "vcard-temp", VCardProvider())
        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
                OfflineMessageRequest.Provider())
        // Offline Message Indicator
        pm.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                OfflineMessageInfo.Provider())
        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", LastActivity.Provider())
        // User Search
        pm.addIQProvider("query", "jabber:iq:search", UserSearch.Provider())
        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                SharedGroupsInfo.Provider())
        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                MultipleAddressesProvider())
        pm.addIQProvider("si", "http://jabber.org/protocol/si",
                StreamInitiationProvider())
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
                BytestreamsProvider())
        pm.addIQProvider("query", "jabber:iq:privacy", PrivacyProvider())
        pm.addIQProvider("command", "http://jabber.org/protocol/commands",
                AdHocCommandDataProvider())
        pm.addExtensionProvider("malformed-action",
                "http://jabber.org/protocol/commands",
                AdHocCommandDataProvider.MalformedActionError())
        pm.addExtensionProvider("bad-locale",
                "http://jabber.org/protocol/commands",
                AdHocCommandDataProvider.BadLocaleError())
        pm.addExtensionProvider("bad-payload",
                "http://jabber.org/protocol/commands",
                AdHocCommandDataProvider.BadPayloadError())
        pm.addExtensionProvider("bad-sessionid",
                "http://jabber.org/protocol/commands",
                AdHocCommandDataProvider.BadSessionIDError())
        pm.addExtensionProvider("session-expired",
                "http://jabber.org/protocol/commands",
                AdHocCommandDataProvider.SessionExpiredError())
    }
}