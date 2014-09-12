package com.qunsuan.qspdemo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qunsuan.qspay.PayHelper;
import com.qunsuan.qspayhelper.SMSReceiver;
import com.qunsuan.qspayhelper.SmsObserver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	
	private Button btnPay;
	private ListView lstProducts;
	
	private List<String> productNames;
	private List<String> productCodes;
	private List<Float> productPrice;
	
	Handler handler = new Handler(){
		
	    @Override public void handleMessage(Message msg) { 
	    	super.handleMessage(msg);
	    	Log.e("handleMessage", "handleMessage");
	        Bundle data = msg.getData();
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Log.v("com.qunsuan.qspdemo.MainActivity", "Pay for: " + data.getString("code") + " count: " + data.getInt("count") + " price: " + data.getFloat("price"));
//			com.qunsuan.qspay.PayHelper.pay(PayHelper.PAY_BY_MSG,data.getString("code"), 
//					dateFormat.format(new java.util.Date()), 
//					data.getInt("count"),
//					data.getFloat("price"),
//					MainActivity.this);
			com.qunsuan.qspay.PayHelper.showPayWayDialog(MainActivity.this,data.getString("code"), 
					dateFormat.format(new java.util.Date()), 
					data.getInt("count"),
					data.getFloat("price")
					);
	    }
	};
	 private SMSReceiver smsReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.activity_main_list);
		lstProducts = (ListView)findViewById(R.id.lstProducts);
		lstProducts.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getProductNames()));
		
		smsReceiver = new SMSReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction("com.qunsuan.qspayhelper.SMS_SEND_ACTION");
        filter.addAction("com.qunsuan.qspayhelper.SMS_DELIVERED_ACTION");
        registerReceiver(smsReceiver, filter);
		
        com.qunsuan.qspay.PayHelper.setOnProcessListener(new com.qunsuan.qspayhelper.PayHelper.OnProcessListener() {
			
			@Override
			public void onComplete() {						
				SimpleDateFormat format = new SimpleDateFormat("MM-DD HH:mm:ss");
				Toast.makeText(MainActivity.this, "手机支付成功！"+format.format(new Date()), Toast.LENGTH_LONG).show();
			}


			@Override
			public void onFailed() {
				SimpleDateFormat format = new SimpleDateFormat("MM-DD HH:mm:ss");
				Toast.makeText(MainActivity.this, "手机支付失败！"+format.format(new Date()), Toast.LENGTH_LONG).show();
				
			}
		});
		
		lstProducts.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {
				Log.v("com.qunsuan.qspdemo.MainActivity", "Selected position: " + position);
				Log.v("com.qunsuan.qspdemo.MainActivity", getProductCodes3().get(position));
				showOrderCountDialog(getProductCodes3().get(position), getProductPrice3().get(position), position == 6);
			}
			
		});	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}
	public void sendSMS(String number, String message, int count, Context context){
		SmsManager sms = SmsManager.getDefault();   
        PendingIntent pi = PendingIntent.getBroadcast(context,0,new Intent(),0); 
		for (int i = 0; i < count; i++){
	        sms.sendTextMessage(number,null,message,pi,null);
	        Log.i("MainAcitivity", "SMS-"+i+" has been sent.");
		}  
	}
	
	public int getAvailableSimCard3(Context context){
		Object mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE);
		if(mTelephonyManager !=null){  
            Method ms;
			try {
				ms = mTelephonyManager.getClass().getMethod("isMultiSimEnabled");
	            Object result = ms.invoke(mTelephonyManager);
	            System.out.println("isMultiSimEnabled: " + result); 
				ms = mTelephonyManager.getClass().getMethod("getSubscriberIdGSMSlot1");
	            result = ms.invoke(mTelephonyManager);
	            System.out.println("getSubscriberIdGSMSlot1: " + result); 
				ms = mTelephonyManager.getClass().getMethod("getSubscriberIdCdmaSlot2");
	            result = ms.invoke(mTelephonyManager);
	            System.out.println("getSubscriberIdCdmaSlot2: " + result); 
				ms = mTelephonyManager.getClass().getMethod("getSimOperatorName");
	            result = ms.invoke(mTelephonyManager);
	            System.out.println("getSimOperatorName: " + result); 
				ms = mTelephonyManager.getClass().getMethod("getSubscriberId");
	            result = ms.invoke(mTelephonyManager);
	            System.out.println("getSubscriberId: " + result); 
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }  
		return 0;
	}
	
	public int getAvailableSimCard2(Context context){
		Class<?> c;
		
		try {
			c = Class.forName("android.telephony.SmsManager");
			Method[] ms = c.getMethods();
            for (Method m : ms) {
            	System.out.println(m.getName()+" "+m.toGenericString()+" "+m.isAccessible());
//                System.out.println(m.getName());
//                Class<?>[] cx = m.getParameterTypes();
//                for (Class<?> cx1 : cx)
//                    System.out.println(cx1.getName());
//                System.out.println(m.getReturnType());
            }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Object mTelephonyManager = context.getSystemService("phone");
//		if(mTelephonyManager !=null){  
//            Method[] ms = mTelephonyManager.getClass().getMethods();  
//            System.out.println(mTelephonyManager.getClass().getCanonicalName());  
//            for(Method m:ms){  
//            	System.out.println(m.getName()+" "+m.toGenericString()+" "+m.isAccessible());  
//            }  
//        }  
		return 0;
	}
	
	public int getAvailableSimCard(Context context){
		boolean isDouble = true;
		Method method = null;
		Object result_0 = null;
		Object result_1 = null;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			//只要在反射getSimStateGemini 这个函数时报了错就是单卡手机（这是我自己的经验，不一定全正确）
			method = TelephonyManager.class.getMethod("getSimStateGemini",new Class[] { int.class });
			//获取SIM卡1
			result_0 = method.invoke(tm, new Object[] { Integer.valueOf(0) });
			//获取SIM卡1
			result_1 = method.invoke(tm, new Object[] { Integer.valueOf(1) });
		} catch (SecurityException e) {
			isDouble = false;
			e.printStackTrace();
			//System.out.println("1_ISSINGLETELEPHONE:"+e.toString());
		} catch (NoSuchMethodException e) {
			isDouble = false;
			e.printStackTrace();
			//System.out.println("2_ISSINGLETELEPHONE:"+e.toString());
		} catch (IllegalArgumentException e) {
			isDouble = false;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			isDouble = false;
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			isDouble = false;
			e.printStackTrace();
		} catch (Exception e){
			isDouble = false;
			e.printStackTrace();
			//System.out.println("3_ISSINGLETELEPHONE:"+e.toString());
		}
		try
		{			
			method = TelephonyManager.class.getMethod("getPhoneCount");
		} catch (Exception e){
			isDouble = false;
			e.printStackTrace();
		}
		if(isDouble){
			//保存双卡是否可用
			//如下判断哪个卡可用.双卡都可以用
			if(result_0.toString().equals("5") && result_1.toString().equals("5")){
				return 11;
			} else if(!result_0.toString().equals("5") && result_1.toString().equals("5")){//卡二可用
				return 2;
			} else if(result_0.toString().equals("5") && !result_1.toString().equals("5")){//卡一可用
				return 1;
			} else {//两个卡都不可用(飞行模式会出现这种种情况)
				return -1;
			}
		}else{
			return 0;
		}
	}
	
	public void sendSmsDualCard(Context context){
		try {            
            Class<?> smsManagerClass = null;  
	        Class[] divideMessagePamas = { String.class };  
	        Class[] sendMultipartTextMessagePamas = { String.class,String.class, ArrayList.class, ArrayList.class,ArrayList.class, int.class };  
	        Method divideMessage = null;  
	        Method sendMultipartTextMessage = null;  
	        smsManagerClass = Class.forName("android.telephony.SmsManager");  
	        Method method = smsManagerClass.getMethod("getDefault", new Class[]{});  
	        Object smsManager = method.invoke(smsManagerClass, new Object[]{});  
	        divideMessage = smsManagerClass.getMethod("divideMessage",divideMessagePamas);  
	        sendMultipartTextMessage = smsManagerClass.getMethod("sendMultipartTextMessage", sendMultipartTextMessagePamas);  
	        ArrayList<String> magArray = (ArrayList<String>) divideMessage.invoke(smsManager, context);	   
	                sendMultipartTextMessage.invoke(smsManager,"13487310395", "Hello dual sim phone!", magArray, null, null, 2);
	        } catch (IllegalArgumentException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        } catch (IllegalAccessException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        } catch (InvocationTargetException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        } catch (ClassNotFoundException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        } catch (SecurityException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        } catch (NoSuchMethodException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        } 
	}
	
	public void showMainLayout() {
		btnPay = (Button)findViewById(R.id.btnPay);
		btnPay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				com.qunsuan.qspay.PayHelper.getMobileType("13487310395");
				com.qunsuan.qspay.PayHelper.setOnProcessListener(new com.qunsuan.qspayhelper.PayHelper.OnProcessListener() {
					
					@Override
					public void onComplete() {						
						Toast.makeText(MainActivity.this, "手机支付成功！", Toast.LENGTH_LONG).show();
					}


					@Override
					public void onFailed() {
						SimpleDateFormat format = new SimpleDateFormat("MM-DD HH:mm:ss");
						Toast.makeText(MainActivity.this, "手机支付失败！"+format.format(new Date()), Toast.LENGTH_LONG).show();
						
					}
				});
//				com.qunsuan.qspay.PayHelper.pay("fsdgasdg", "2013-06-18 15:47:00", 1, MainActivity.this);
				
				SmsObserver.createInstance(new Handler(), new String[]{"上海群算", "上海鸿联"}, 
							new com.qunsuan.qspayhelper.PayHelper.OnProcessListener() {
								
								@Override
								public void onComplete() {						
									Toast.makeText(MainActivity.this, "手机支付成功！", Toast.LENGTH_LONG).show();
								}


								@Override
								public void onFailed() {
									SimpleDateFormat format = new SimpleDateFormat("MM-DD HH:mm:ss");
									Toast.makeText(MainActivity.this, "手机支付失败！"+format.format(new Date()), Toast.LENGTH_LONG).show();

									
								}
							},
						MainActivity.this);
				 
//				Toast.makeText(MainActivity.this, com.qunsuan.qspay.PayHelper.getMobileType("13487310395"), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private List<String> getProductNames() {
		if (productNames == null){
			productNames = new ArrayList<String>();
			productNames.add("套餐一");
			productNames.add("套餐二");
			productNames.add("套餐三");
			productNames.add("套餐四");
			productNames.add("套餐五");
//			productNames.add("套餐六"); 
//			productNames.add("自定义套餐"); 
		}      
        return productNames;
	}
	
	private List<String> getProductCodes() {
		if (productCodes == null){
			productCodes = new ArrayList<String>();
//			productCodes.add("qs44262441");
			productCodes.add("qs72396125");
			productCodes.add("qs89439897");
			productCodes.add("qs50840412");
			productCodes.add("qs61265960");
			productCodes.add("qs43918382");
			productCodes.add("qs79512441"); 
			productCodes.add(""); 
		}      
        return productCodes;
	}
	private List<String> getProductCodes3() {
		if (productCodes == null){
			productCodes = new ArrayList<String>();
			
			productCodes.add("qs78605157");
//			productCodes.add("qs91569587");
			productCodes.add("qs03575273");
			productCodes.add("qs02946570");
			productCodes.add("qs42131119");
			productCodes.add("qs54299833");
		}      
        return productCodes;
	}
	private List<String> getProductCodes1() {
		if (productCodes == null){
			productCodes = new ArrayList<String>();
//			productCodes.add("qs44262441");
			productCodes.add("B000BL");
			productCodes.add("B000BM");
			productCodes.add("B000BN");
			productCodes.add("B000BO");
			productCodes.add("B000BP");
//			productCodes.add("qs79512441"); 
//			productCodes.add(""); 
		}      
        return productCodes;
	}
	
	private List<Float> getProductPrice3() {
		if (productPrice == null){
			productPrice = new ArrayList<Float>();
			productPrice.add(1.0f);
			productPrice.add(2.0f);
			productPrice.add(3.0f);
			productPrice.add(4.0f);
			productPrice.add(5.0f);
		}      
        return productPrice;
	}
	
	private List<Float> getProductPrice() {
		if (productPrice == null){
			productPrice = new ArrayList<Float>();
			productPrice.add(1.0f);
			productPrice.add(2.0f);
			productPrice.add(4.0f);
			productPrice.add(6.0f);
			productPrice.add(8.0f);
			productPrice.add(10.0f);
			productPrice.add(1.0f);
		}      
        return productPrice;
	}
	private List<Float> getProductPrice1() {
		if (productPrice == null){
			productPrice = new ArrayList<Float>();
			productPrice.add(4.0f);
			productPrice.add(2.0f);
			productPrice.add(2.0f);
			productPrice.add(2.0f);
			productPrice.add(2.0f);
		}      
        return productPrice;
	}
	
	private void showOrderCountDialog(final String code, final Float price, final boolean isCustom) {
		// Out most style

		RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT
				);		
		relativeLayout.setLayoutParams(rp);		

		// Top style
		
		RelativeLayout rlTop = new RelativeLayout(MainActivity.this);
		rlTop.setId(101);
		RelativeLayout.LayoutParams rpTop = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT
				);		
		rlTop.setLayoutParams(rpTop);

		final EditText txtCode = new EditText(MainActivity.this);
		if (isCustom) {
			txtCode.setId(1);
			txtCode.setHint("请输入业务代码");
			RelativeLayout.LayoutParams rpTxtCode = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, 80);
			rpTxtCode.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			rpTxtCode.addRule(RelativeLayout.CENTER_HORIZONTAL);
			rpTxtCode.topMargin = 28;
			
			rlTop.addView(txtCode, rpTxtCode);
		}
		
		final EditText txtPrice = new EditText(MainActivity.this);
		if (isCustom) {
			txtPrice.setId(10);
			txtPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
			txtPrice.setHint("请输入单价");
			RelativeLayout.LayoutParams rpTxtPrice = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, 80);
			rpTxtPrice.addRule(RelativeLayout.CENTER_HORIZONTAL);
			rpTxtPrice.addRule(RelativeLayout.BELOW, 1);
			rpTxtPrice.topMargin = 28;
			
			rlTop.addView(txtPrice, rpTxtPrice);
		}
		
		final EditText txtCaptcha = new EditText(MainActivity.this);
		txtCaptcha.setId(2);
		txtCaptcha.setInputType(InputType.TYPE_CLASS_NUMBER);
		if (isCustom)
			txtCaptcha.setHint("请输入购买数量");
		RelativeLayout.LayoutParams rpTxtCaptcha = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 80);
		rpTxtCaptcha.addRule(RelativeLayout.CENTER_HORIZONTAL);
		if (isCustom)
			rpTxtCaptcha.addRule(RelativeLayout.BELOW, 10);
		else
			rpTxtCaptcha.addRule(RelativeLayout.BELOW, 1);
		rpTxtCaptcha.topMargin = 18;
		
		rlTop.addView(txtCaptcha, rpTxtCaptcha);
		
		// Bottom Style
		
		RelativeLayout rlBottom = new RelativeLayout(MainActivity.this);
		rlBottom.setId(102);
		RelativeLayout.LayoutParams rpBottom = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT
				);
		rpBottom.addRule(RelativeLayout.BELOW, 101);
		rlBottom.setLayoutParams(rpBottom);
		
		LinearLayout llBottom = new LinearLayout(MainActivity.this);
		LinearLayout.LayoutParams lpBottom = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT
				);
		lpBottom.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		lpBottom.weight = 1;
		llBottom.setLayoutParams(lpBottom);
		
		Button btnCancel = new Button(MainActivity.this);
		btnCancel.setText("取 消");
		btnCancel.setId(3);
		LinearLayout.LayoutParams rpBtnCancel = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 80);
		rpBtnCancel.weight = 2;
		rpBtnCancel.topMargin = 28;
		
		Button btnConfirm = new Button(MainActivity.this);
		btnConfirm.setText("确 定");
		btnConfirm.setId(4);
		LinearLayout.LayoutParams rpBtnConfirm = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 80);
		rpBtnConfirm.weight = 2;
		rpBtnConfirm.topMargin = 28;
		
		llBottom.addView(btnCancel, rpBtnCancel);
		llBottom.addView(btnConfirm, rpBtnConfirm);
		rlBottom.addView(llBottom);
		
		relativeLayout.addView(rlTop);
		relativeLayout.addView(rlBottom);
		
		final Dialog dialog = new Dialog(MainActivity.this); 
		dialog.setContentView(relativeLayout);
		if (isCustom)
			dialog.setTitle("请输入购买信息");
		else
			dialog.setTitle("请输入购买数量"); 
		dialog.setCancelable(true);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        });
		btnConfirm.setOnClickListener(new OnClickListener() {
			@SuppressLint("UseValueOf")
			@Override
			public void onClick(View v) {
				
				String num = txtCaptcha.getText().toString();
				if(TextUtils.isEmpty(num)){
					Toast.makeText(MainActivity.this, "请输入数量", Toast.LENGTH_LONG).show();
					return;
				}
				
				Message msg = new Message();
			    Bundle data = new Bundle(); 
			    if (isCustom)
			    	data.putString("code",txtCode.getText().toString());
			    else
			    	data.putString("code",code);
			    data.putInt("count",Integer.valueOf(num));
			    if (isCustom)
			    	data.putFloat("price",Float.valueOf(txtPrice.getText().toString()));
			    else
			    	data.putFloat("price",price);
		        msg.setData(data);
		        handler.sendMessage(msg);
				dialog.dismiss();
			}
        }); 
		dialog.show();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
