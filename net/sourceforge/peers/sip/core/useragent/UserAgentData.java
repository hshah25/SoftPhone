package net.sourceforge.peers.sip.core.useragent;

import java.util.List;

import net.sourceforge.peers.Config;
import net.sourceforge.peers.Logger;
import net.sourceforge.peers.media.Echo;
import net.sourceforge.peers.media.MediaManager;
import net.sourceforge.peers.media.SoundManager;
import net.sourceforge.peers.sdp.SDPManager;
import net.sourceforge.peers.sip.transaction.TransactionManager;
import net.sourceforge.peers.sip.transactionuser.DialogManager;
import net.sourceforge.peers.sip.transport.TransportManager;

public class UserAgentData {
	private String peersHome;
	private Logger logger;
	private Config config;
	private List<String> peers;
	private Echo echo;
	private UAC uac;
	private UAS uas;
	private ChallengeManager challengeManager;
	private DialogManager dialogManager;
	private TransactionManager transactionManager;
	private TransportManager transportManager;
	private SipListener sipListener;
	private SDPManager sdpManager;
	private SoundManager soundManager;
	private MediaManager mediaManager;

	public UserAgentData() {
	}

	public String getPeersHome() {
		return peersHome;
	}

	public void setPeersHome(String peersHome) {
		this.peersHome = peersHome;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public List<String> getPeers() {
		return peers;
	}

	public void setPeers(List<String> peers) {
		this.peers = peers;
	}

	public Echo getEcho() {
		return echo;
	}

	public void setEcho(Echo echo) {
		this.echo = echo;
	}

	public UAC getUac() {
		return uac;
	}

	public void setUac(UAC uac) {
		this.uac = uac;
	}

	public UAS getUas() {
		return uas;
	}

	public void setUas(UAS uas) {
		this.uas = uas;
	}

	public ChallengeManager getChallengeManager() {
		return challengeManager;
	}

	public void setChallengeManager(ChallengeManager challengeManager) {
		this.challengeManager = challengeManager;
	}

	public DialogManager getDialogManager() {
		return dialogManager;
	}

	public void setDialogManager(DialogManager dialogManager) {
		this.dialogManager = dialogManager;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public TransportManager getTransportManager() {
		return transportManager;
	}

	public void setTransportManager(TransportManager transportManager) {
		this.transportManager = transportManager;
	}

	public SipListener getSipListener() {
		return sipListener;
	}

	public void setSipListener(SipListener sipListener) {
		this.sipListener = sipListener;
	}

	public SDPManager getSdpManager() {
		return sdpManager;
	}

	public void setSdpManager(SDPManager sdpManager) {
		this.sdpManager = sdpManager;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}

	public void setSoundManager(SoundManager soundManager) {
		this.soundManager = soundManager;
	}

	public MediaManager getMediaManager() {
		return mediaManager;
	}

	public void setMediaManager(MediaManager mediaManager) {
		this.mediaManager = mediaManager;
	}
}