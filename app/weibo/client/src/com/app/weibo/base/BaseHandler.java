package com.app.weibo.base;

import com.app.weibo.util.AppUtil;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class BaseHandler extends Handler {
	
	protected BaseApp app;
	
	public BaseHandler (BaseApp app) {
		this.app = app;
	}
	
	public BaseHandler (Looper looper) {
		super(looper);
	}
	
	@Override
	public void handleMessage(Message msg) {
		try {
			int taskId;
			String result;
			switch (msg.what) {
				case BaseTask.TASK_COMPLETE:
					app.hideLoadBar();
					taskId = msg.getData().getInt("task");
					result = msg.getData().getString("data");
					app.onTaskComplete(taskId, AppUtil.getMessage(result));
					break;
				case BaseTask.SHOW_TOAST:
					app.hideLoadBar();
					result = msg.getData().getString("data");
					app.toast(result);
					break;
				case BaseTask.SHOW_LOADBAR:
					app.showLoadBar();
					break;
				case BaseTask.HIDE_LOADBAR:
					app.hideLoadBar();
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			app.toast(e.getMessage());
		}
	}
	
}