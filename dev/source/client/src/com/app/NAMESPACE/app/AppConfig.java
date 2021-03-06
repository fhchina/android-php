package com.app.NAMESPACE.app;

import java.util.ArrayList;
import java.util.HashMap;

import com.app.NAMESPACE.R;
import com.app.NAMESPACE.auth.AuthApp;
import com.app.NAMESPACE.base.BaseApp;
import com.app.NAMESPACE.base.BaseHandler;
import com.app.NAMESPACE.base.BaseMessage;
import com.app.NAMESPACE.base.BaseTask;
import com.app.NAMESPACE.base.C;
import com.app.NAMESPACE.list.SimpleList;
import com.app.NAMESPACE.model.Config;
import com.app.NAMESPACE.model.Customer;
import com.app.NAMESPACE.util.AppCache;
import com.app.NAMESPACE.util.AppUtil;
import com.app.NAMESPACE.util.UIUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AppConfig extends AuthApp {
	
	private ListView listConfig;
	private ImageView faceImage;
	private String faceImageUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_config);
		
		// set handler
		this.setHandler(new ConfigHandler(this));
		
		// tab button
		ImageButton ib = (ImageButton) this.findViewById(R.id.main_tab_3);
		ib.setImageResource(R.drawable.tab_conf_2);
		
		// init views
		listConfig = (ListView) findViewById(R.id.app_config_list_main);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// config list
		final ArrayList<Config> dataList = new ArrayList<Config>();
		dataList.add(new Config(getResources().getString(R.string.config_face), customer.getFace()));
		dataList.add(new Config(getResources().getString(R.string.config_sign), customer.getSign()));
		String[] from = {Config.COL_NAME};
		int[] to = {R.id.tpl_list_menu_text_name};
		listConfig.setAdapter(new SimpleList(this, AppUtil.dataToList(dataList, from), R.layout.tpl_list_menu, from, to));
		listConfig.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				// change face
				if (pos == 0) {
					overlay(AppSetFace.class);
				// edit customer info
				} else {
					Bundle data = new Bundle();
					data.putInt("action", C.action.edittext.CONFIG);
					data.putString("value", dataList.get(pos).getValue());
					doEditText(data);
				}
			}
		});
		
		// prepare customer data
		HashMap<String, String> cvParams = new HashMap<String, String>();
		cvParams.put("customerId", customer.getId());
		this.doTaskAsync(C.task.customerView, C.api.customerView, cvParams);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// async task callback methods
	
	@Override
	public void onTaskComplete(int taskId, BaseMessage message) {
		super.onTaskComplete(taskId, message);
		switch (taskId) {
			case C.task.customerView:
				try {
					final Customer customer = (Customer) message.getResult("Customer");
					TextView textTop = (TextView) this.findViewById(R.id.tpl_list_info_text_top);
					TextView textBottom = (TextView) this.findViewById(R.id.tpl_list_info_text_bottom);
					textTop.setText(customer.getSign());
					textBottom.setText(UIUtil.getCustomerInfo(this, customer));
					// load face image async
					faceImage = (ImageView) this.findViewById(R.id.tpl_list_info_image_face);
					faceImageUrl = customer.getFaceurl();
					loadImage(faceImageUrl);
				} catch (Exception e) {
					e.printStackTrace();
					toast(e.getMessage());
				}
				break;
		}
	}
	
	@Override
	public void onNetworkError (int taskId) {
		super.onNetworkError(taskId);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// other methods
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.forward(AppIndex.class);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// inner classes
	
	private class ConfigHandler extends BaseHandler {
		public ConfigHandler(BaseApp app) {
			super(app);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				switch (msg.what) {
					case BaseTask.LOAD_IMAGE:
						Bitmap face = AppCache.getImage(faceImageUrl);
						faceImage.setImageBitmap(face);
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				app.toast(e.getMessage());
			}
		}
	}
}