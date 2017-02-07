package com.usr.net.bean;

import java.util.LinkedList;
import java.util.Queue;


public class SendMsgThread extends Thread {
	//	private IOTComm iotComm;
	// 发送消息的队列
	private Queue<byte[]> sendMsgQuene = new LinkedList<>();
	private boolean isSend = true;
	private OnSendListener onSendListener;
	public SendMsgThread() {
	}

	public synchronized void putMsg(byte[] data) {
		// 唤醒线程
		if (sendMsgQuene.size() == 0)
			notify();
		sendMsgQuene.offer(data);
	}

	public synchronized void clear(){
		sendMsgQuene.clear();
	}

	public void run() {
		synchronized (this) {
			while (isSend) {
				// 当队列里的消息发送完毕后，线程等待
				while (sendMsgQuene.size() > 0) {
					byte[] data = sendMsgQuene.poll();
					if(data != null && onSendListener!= null){
						onSendListener.onSend(data);
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setOnSendListener(OnSendListener onSendListener) {
		this.onSendListener = onSendListener;
	}

	public interface OnSendListener{
		public void onSend(byte[] data);
	}

	public void stopSend(){
		isSend = false;
	}
}