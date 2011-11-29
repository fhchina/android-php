package com.app.NAMESPACE.base;

import java.util.HashMap;
import com.app.NAMESPACE.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

import com.app.NAMESPACE.dialog.BasicDialog;
import com.app.NAMESPACE.util.AppUtil;

public class BaseApp extends Activity {
	
	protected BaseHandler handler;
	protected BaseTaskPool taskPool;
	protected boolean showLoadBar = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// async task handler
		this.handler = new BaseHandler () {
			@Override
			public void handleMessage (Message msg) {
				super.handleMessage(msg);
				try {
					int taskId;
					String result;
					switch (msg.what) {
						case BaseTask.TASK_COMPLETE:
							taskId = msg.getData().getInt("task");
							result = msg.getData().getString("data");
							onTaskComplete(taskId, AppUtil.getMessage(result));
							break;
						case BaseTask.SHOW_TOAST:
							result = msg.getData().getString("data");
							toast(result);
							break;
						case BaseTask.SHOW_LOADBAR:
							showLoadBar();
							break;
						case BaseTask.HIDE_LOADBAR:
							hideLoadBar();
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					toast(e.getMessage());
				}
			}
		};
		
		// init task pool
		taskPool = new BaseTaskPool();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// util method
	
	public void toast (String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void forward (Class<?> classObj) {
		Intent intent = new Intent();
		intent.setClass(this, classObj);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
		this.finish();
	}
	
	public void forward (Class<?> classObj, Bundle params) {
		Intent intent = new Intent();
		intent.setClass(this, classObj);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtras(params);
		this.startActivity(intent);
		this.finish();
	}
	
	public void openWindow (Class<?> classObj) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setClass(this, classObj);
        startActivity(intent);
	}
	
	public void openWindow (Class<?> classObj, Bundle params) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setClass(this, classObj);
        intent.putExtras(params);
        startActivity(intent);
	}
	
	public Context getContext () {
		return this;
	}
	
	public LayoutInflater getLayout () {
		return (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public View getViewById (int layoutId, int itemId) {
		return getLayout().inflate(layoutId, null).findViewById(itemId);
	}
	
	public BaseTaskPool getTaskPool () {
		return this.taskPool;
	}
	
	public void showLoadBar () {
		this.findViewById(R.id.main_load_bar).setVisibility(View.VISIBLE);
		this.findViewById(R.id.main_load_bar).bringToFront();
		showLoadBar = true;
	}
	
	public void hideLoadBar () {
		this.findViewById(R.id.main_load_bar).setVisibility(View.GONE);
	}
	
	public void openDialog(Bundle params) {
		new BasicDialog(this, params).show();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// logic method
	
	public void doFinish () {
		this.finish();
	}
	
	public void doLogout () {
		BaseAuth.setLogin(false);
	}
	
	public void doEdit (int action, String value) {
		Bundle b = new Bundle();
		b.putInt("action", action);
		b.putString("value", value);
		Intent intent = new Intent();
		intent.setAction(C.intent.action.EDIT);
		intent.putExtras(b);
		this.startActivity(intent);
	}
	
	public void sendMessage (int what) {
		Message m = new Message();
		m.what = what;
		handler.sendMessage(m);
	}
	
	public void sendMessage (int what, String data) {
		Bundle b = new Bundle();
		b.putString("data", data);
		Message m = new Message();
		m.what = what;
		m.setData(b);
		handler.sendMessage(m);
	}
	
	public void sendMessage (int what, int taskId, String data) {
		Bundle b = new Bundle();
		b.putInt("task", taskId);
		b.putString("data", data);
		Message m = new Message();
		m.what = what;
		m.setData(b);
		handler.sendMessage(m);
	}
	
	public void doTaskAsync (int taskId, int delayTime) {
		taskPool.addTask(taskId, new BaseTask(){
			@Override
			public void onComplete () {
				sendMessage(BaseTask.TASK_COMPLETE, this.getId(), null);
			}
			@Override
			public void onError (String error) {
				sendMessage(BaseTask.SHOW_TOAST, C.err.network);
			}
		}, delayTime);
	}
	
	public void doTaskAsync (int taskId, BaseTask baseTask, int delayTime) {
		taskPool.addTask(taskId, baseTask, delayTime);
	}
	
	public void doTaskAsync (int taskId, String taskUrl) {
		showLoadBar();
		taskPool.addTask(taskId, taskUrl, new BaseTask(){
			@Override
			public void onComplete (String httpResult) {
				if (httpResult != null) {
					sendMessage(BaseTask.TASK_COMPLETE, this.getId(), httpResult);
				} else {
					sendMessage(BaseTask.SHOW_TOAST, C.err.message);
				}
			}
			@Override
			public void onError (String error) {
				sendMessage(BaseTask.SHOW_TOAST, C.err.network);
			}
		}, 0);
	}
	
	public void doTaskAsync (int taskId, String taskUrl, HashMap<String, String> taskArgs) {
		showLoadBar();
		taskPool.addTask(taskId, taskUrl, taskArgs, new BaseTask(){
			@Override
			public void onComplete (String httpResult) {
				if (httpResult != null) {
					sendMessage(BaseTask.TASK_COMPLETE, this.getId(), httpResult);
				} else {
					sendMessage(BaseTask.SHOW_TOAST, C.err.message);
				}
			}
			@Override
			public void onError (String error) {
				sendMessage(BaseTask.SHOW_TOAST, C.err.network);
			}
		}, 0);
	}
	
	public void onTaskComplete (int taskId) {
		if (showLoadBar) {
			hideLoadBar();
		}
	}
	
	public void onTaskComplete (int taskId, BaseMessage message) {
		if (showLoadBar) {
			hideLoadBar();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// common classes
	
	public class BitmapViewBinder implements ViewBinder {
		// 
		public boolean setViewValue(View view, Object data, String textRepresentation) {
			if ((view instanceof ImageView) & (data instanceof Bitmap)) {
				ImageView iv = (ImageView) view;
				Bitmap bm = (Bitmap) data;
				iv.setImageBitmap(bm);
				return true;
			}
			return false;
		}
	}
}