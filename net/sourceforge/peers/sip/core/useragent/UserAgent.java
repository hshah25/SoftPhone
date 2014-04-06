/*
    This file is part of Peers, a java SIP softphone.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Copyright 2007, 2008, 2009, 2010 Yohann Martineau 
*/

package net.sourceforge.peers.sip.core.useragent;

import java.io.File;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.peers.Config;
import net.sourceforge.peers.Logger;
import net.sourceforge.peers.XmlConfig;
import net.sourceforge.peers.media.Echo;
import net.sourceforge.peers.media.MediaManager;
import net.sourceforge.peers.media.MediaMode;
import net.sourceforge.peers.media.SoundManager;
import net.sourceforge.peers.sdp.SDPManager;
import net.sourceforge.peers.sip.Utils;
import net.sourceforge.peers.sip.core.useragent.handlers.ByeHandler;
import net.sourceforge.peers.sip.core.useragent.handlers.CancelHandler;
import net.sourceforge.peers.sip.core.useragent.handlers.InviteHandler;
import net.sourceforge.peers.sip.core.useragent.handlers.OptionsHandler;
import net.sourceforge.peers.sip.core.useragent.handlers.RegisterHandler;
import net.sourceforge.peers.sip.syntaxencoding.SipURI;
import net.sourceforge.peers.sip.transaction.Transaction;
import net.sourceforge.peers.sip.transaction.TransactionManager;
import net.sourceforge.peers.sip.transactionuser.DialogManager;
import net.sourceforge.peers.sip.transport.SipMessage;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;
import net.sourceforge.peers.sip.transport.TransportManager;


public class UserAgent {

    public final static String CONFIG_FILE = "conf" + File.separator + "peers.xml";
    public final static int RTP_DEFAULT_PORT = 8000;

    private int cseqCounter;
    private UserAgentData data = new UserAgentData();

	public UserAgent(SipListener sipListener, String peersHome,
            Logger logger) throws SocketException {
        this.data.setSipListener(sipListener);
        if (peersHome == null) {
            this.data.setPeersHome(Utils.DEFAULT_PEERS_HOME);
        } else {
            this.data.setPeersHome(peersHome);
        }
        if (logger == null) {
            logger = new Logger(this.data.getPeersHome());
        } else {
            this.data.setLogger(logger);
        }
        data.setConfig(new XmlConfig(this.data.getPeersHome() + File.separator
                + CONFIG_FILE, this.data.getLogger()));
        
        cseqCounter = 1;
        
        StringBuffer buf = new StringBuffer();
        buf.append("starting user agent [");
        buf.append("myAddress: ");
        buf.append(data.getConfig().getLocalInetAddress().getHostAddress()).append(", ");
        buf.append("sipPort: ");
        buf.append(data.getConfig().getSipPort()).append(", ");
        buf.append("userpart: ");
        buf.append(data.getConfig().getUserPart()).append(", ");
        buf.append("domain: ");
        buf.append(data.getConfig().getDomain()).append("]");
        logger.info(buf.toString());

        //transaction user
        
        data.setDialogManager(new DialogManager(logger));
        
        //transaction
        
        data.setTransactionManager(new TransactionManager(logger));
        
        //transport
        
        data.setTransportManager(new TransportManager(data.getTransactionManager(), data.getConfig(),
                logger));
        
        data.getTransactionManager().setTransportManager(data.getTransportManager());
        
        //core
        
        InviteHandler inviteHandler = new InviteHandler(this,
                data.getDialogManager(),
                data.getTransactionManager(),
                data.getTransportManager(),
                logger);
        CancelHandler cancelHandler = new CancelHandler(this,
                data.getDialogManager(),
                data.getTransactionManager(),
                data.getTransportManager(),
                logger);
        ByeHandler byeHandler = new ByeHandler(this,
                data.getDialogManager(),
                data.getTransactionManager(),
                data.getTransportManager(),
                logger);
        OptionsHandler optionsHandler = new OptionsHandler(this,
                data.getTransactionManager(),
                data.getTransportManager(),
                logger);
        RegisterHandler registerHandler = new RegisterHandler(this,
                data.getTransactionManager(),
                data.getTransportManager(),
                logger);
        
        InitialRequestManager initialRequestManager =
            new InitialRequestManager(
                this,
                inviteHandler,
                cancelHandler,
                byeHandler,
                optionsHandler,
                registerHandler,
                data.getDialogManager(),
                data.getTransactionManager(),
                data.getTransportManager(),
                logger);
        MidDialogRequestManager midDialogRequestManager =
            new MidDialogRequestManager(
                this,
                inviteHandler,
                cancelHandler,
                byeHandler,
                optionsHandler,
                registerHandler,
                data.getDialogManager(),
                data.getTransactionManager(),
                data.getTransportManager(),
                logger);
        
        data.setUas(new UAS(this,
                initialRequestManager,
                midDialogRequestManager,
                data.getDialogManager(),
                data.getTransactionManager(),
                data.getTransportManager()));
        data.setUac(new UAC(this,
                initialRequestManager,
                midDialogRequestManager,
                data.getDialogManager(),
                data.getTransactionManager(),
                data.getTransportManager(),
                logger));

        data.setChallengeManager(new ChallengeManager(data.getConfig(),
                initialRequestManager,
                midDialogRequestManager,
                data.getDialogManager(),
                logger));
        registerHandler.setChallengeManager(data.getChallengeManager());
        inviteHandler.setChallengeManager(data.getChallengeManager());
        byeHandler.setChallengeManager(data.getChallengeManager());

        data.setPeers(new ArrayList<String>());
        //dialogs = new ArrayList<Dialog>();

        data.setSdpManager(new SDPManager(this, logger));
        inviteHandler.setSdpManager(data.getSdpManager());
        optionsHandler.setSdpManager(data.getSdpManager());
        data.setSoundManager(new SoundManager(data.getConfig().isMediaDebug(), logger,
                this.data.getPeersHome()));
        data.setMediaManager(new MediaManager(this, logger));
    }

    public void close() {
        data.getTransportManager().closeTransports();
        data.getConfig().setPublicInetAddress(null);
    }

 
    
//    public List<Dialog> getDialogs() {
//        return dialogs;
//    }

    public List<String> getPeers() {
        return data.getPeers();
    }

//    public Dialog getDialog(String peer) {
//        for (Dialog dialog : dialogs) {
//            String remoteUri = dialog.getRemoteUri();
//            if (remoteUri != null) {
//                if (remoteUri.contains(peer)) {
//                    return dialog;
//                }
//            }
//        }
//        return null;
//    }

    public String generateCSeq(String method) {
        StringBuffer buf = new StringBuffer();
        buf.append(cseqCounter++);
        buf.append(' ');
        buf.append(method);
        return buf.toString();
    }
    
    public boolean isRegistered() {
        return data.getUac().getInitialRequestManager().getRegisterHandler()
            .isRegistered();
    }

    public UAS getUas() {
        return data.getUas();
    }

    public UAC getUac() {
        return data.getUac();
    }

    public DialogManager getDialogManager() {
        return data.getDialogManager();
    }
    
    public int getSipPort() {
        return data.getConfig().getSipPort();
    }

    public int getRtpPort() {
        return data.getConfig().getRtpPort();
    }

    public String getDomain() {
        return data.getConfig().getDomain();
    }

    public String getUserpart() {
        return data.getConfig().getUserPart();
    }

    public MediaMode getMediaMode() {
        return data.getConfig().getMediaMode();
    }

    public boolean isMediaDebug() {
        return data.getConfig().isMediaDebug();
    }

    public SipURI getOutboundProxy() {
        return data.getConfig().getOutboundProxy();
    }

    public Echo getEcho() {
        return data.getEcho();
    }

    public void setEcho(Echo echo) {
        this.data.setEcho(echo);
    }

    public SipListener getSipListener() {
        return data.getSipListener();
    }

    public SoundManager getSoundManager() {
        return data.getSoundManager();
    }

    public MediaManager getMediaManager() {
        return data.getMediaManager();
    }

    public Config getConfig() {
        return data.getConfig();
    }

    public String getPeersHome() {
        return data.getPeersHome();
    }

}
