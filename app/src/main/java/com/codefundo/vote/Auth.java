package com.codefundo.vote;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Auth extends Activity {
	 EditText et1;
	 EditText et2;
	 Spinner gender;
	 Button b;
	 DatePicker dp;
	 private int year;
	 private int month;
	 private int day;
	 //private String aadhaarnum;
	//private boolean validateVerhoeff=false;
	 class AuthRequestReceive extends AsyncTask<String, Void, String> {
			
			private String ci;
			private byte[] symmkey;
			private PublicKey publickey;
			private String xml;
			private Date certexp;
			private String pid;
			private byte[] encSessionKey;
			private String authResponse;
			private String output;
			private byte[] pidbytes;
			private boolean otpres=false;
			
			private byte[] generateSessionKey() throws Exception {
				KeyGenerator kgen = KeyGenerator.getInstance("AES", "BC");
				kgen.init(256);
				SecretKey key = kgen.generateKey();
				symmkey = key.getEncoded();
				
				return symmkey;
			}

			private String makeOTPRequestString(String s) throws Exception {
		    	String xml;
		    	return xml="<Auth uid=\""+s+"\" tid=\"public\" sa=\"public\" ver=\"1.6\" ac=\"public\" "
		    			+ " txn=\""+getTimeStamp(s)+"\" lk=\"MKHmkuz-MgLYvA54PbwZdo9eC3D5y7SVozWwpNgEPysVqLs_aJgAVOI\">"+
	 "<Uses pi=\"y\" pa=\"n\" pfa=\"n\" bio=\"n\" pin=\"n\" otp=\"n\"/>"+
	 "<Meta udc=\"PriyaTest\" pip=\"127.0.0.1\" fdc=\"NC\" idc=\"NA\" lot=\"P\" lov=\"110092\" />"
	 //Add the encrypted session key
	+"<Skey ci=\""+ci+"\">"+generateRSASessionKey(symmkey)+"</Skey>"
	 +"<Data type=\"X\">"+generateData(symmkey,pidbytes)+"</Data>"
	+"<Hmac>"+generateHMAC(symmkey,pidbytes)+"</Hmac></Auth>";
		    	}
			private String generateRSASessionKey(byte[] data) throws IOException, GeneralSecurityException {
				// encrypt the session key with the public key
				try{
					Cipher pkCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				
				pkCipher.init(Cipher.ENCRYPT_MODE, publickey);
				 encSessionKey = pkCipher.doFinal(data);
				 
				}
				catch(Exception e)
				{
				e.printStackTrace();	
				}
				return Base64.encodeToString(encSessionKey, Base64.DEFAULT);
			}

		
			private String generateData(byte[] raw, byte[] clear) throws Exception {
				//generate the Data element
		        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

		        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
		        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		        byte[] encrypted = cipher.doFinal(clear);
		        return Base64.encodeToString(encrypted, Base64.DEFAULT);
		    }
			private String generateHMAC(byte[] raw, byte[] clear) throws Exception
			{
				MessageDigest md = MessageDigest.getInstance("SHA-256");
			

				md.update(clear); 
				byte[] digest = md.digest();
				return generateData(raw,digest);
				//The HMAC can now be encrypted with the same method as generation of Data Block
				
			}
			private String getTimeStamp(String s) {
				// TODO Auto-generated method stub
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMMddhhmmss"+"."+"SSS");
				String txn = s+ dateFormat.format(new Date());
				return txn;
				
			}
			private String getTimeStampn()
			{
				SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			    return date.format(new Date());
			}
			private void makePIDBlock(String name, String gender) throws UnsupportedEncodingException {
				
				 pid="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
				 		 "<Pid ts=\""+getTimeStampn()+"\" ver=\"1.0\">"
						+"<Demo><Pi ms=\"P\" mv=\"60\" name=\""+name+"\" gender=\""+gender+"\"/></Demo>"
						+"</Pid>";
				 pidbytes=pid.getBytes("UTF-8");
				/*
				 * 
				 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
				 * <Pid ts="2015-05-27T13:18:14" xmlns="http://www.uidai.gov.in/authentication/uid-auth-request-data/1.0">
				 * <Demo><Pi ms="P" mv="100" name="Jaideep Walia" /></Demo></Pid>
				 */
				
				
			}
			private void getCertificateIdentifier() {
				SimpleDateFormat ciDateFormat = new SimpleDateFormat("yyyyMMdd");
				ciDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			 ci= ciDateFormat.format(this.certexp);
			
			}
			
			@Override
			protected String doInBackground(String... params) {
				//android.os.Debug.waitForDebugger();
				try{
					InputStream reader=getAssets().open("uidai_auth_stage.cer");
						
					
					
						CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
						
						X509Certificate cert = (X509Certificate) certFactory.generateCertificate(reader);
						publickey = cert.getPublicKey();
						certexp = cert.getNotAfter();
						getCertificateIdentifier();
						
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("Could not intialize encryption module", e);
					} 
				try {
					generateSessionKey();
				} 
				catch(Exception e)
				{
					e.printStackTrace();
				}
				try {
					makePIDBlock(params[1],params[2]);
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
					try {
						 xml=makeOTPRequestString(params[0]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					sendAuthRequest(params[0]);
				
			
				
				
				
				try {
					parseXML();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				return "onPostExecute";
				
			}
		
			
			protected void onPostExecute(String s)
			{
				
				//Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
				
				
			}
			protected void sendAuthRequest(String s)
			{
				
				
			
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("http://192.168.42.74:8080/uidwebservice/webresources/MyPath/");
				stringBuilder.append(s);
				
						
				String url=stringBuilder.toString();
				
				HttpClient httpclient=new DefaultHttpClient();
				HttpPost httppost=new HttpPost(url);
				
				
			
				try{
					StringEntity se=new StringEntity(xml,HTTP.UTF_8);
					se.setContentType("text/xml");
					httppost.setEntity(se);
					HttpResponse httpresponse=httpclient.execute(httppost);
					HttpEntity authresentity=httpresponse.getEntity();
					authResponse= EntityUtils.toString(authresentity);
					
					
				}
				catch(final Exception e)
				{
					Handler handler=new Handler(getBaseContext().getMainLooper());
					handler.post(new Runnable(){
						public void run(){
							Toast.makeText(getBaseContext(),"Please check your data connection.", Toast.LENGTH_LONG).show();
						}
					});
					
					
				}
			}
			protected void parseXML() throws XmlPullParserException, IOException
			{
                XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp=factory.newPullParser();
				xpp.setInput(new StringReader(authResponse));
				int event=xpp.getEventType();
				String ret="";
				String name;
				int errorcode;
				while(event!= XmlPullParser.END_DOCUMENT)
				{
					 name =xpp.getName();
					switch(event)
					{
					case XmlPullParser.END_TAG:
						break;
					case XmlPullParser.START_TAG:
						if(name.equals("AuthRes")){
							ret=xpp.getAttributeValue(null,"ret");
							int count=xpp.getAttributeCount();
							if("y".equals(ret))
							{
								output="Authentication Successful !";
							}
							else
								if("n".equals(ret))
								{
									output="ERROR!\n";
									errorcode= Integer.parseInt(xpp.getAttributeValue(null,"err"));
									 new ErrorCodesAuth();
									output+= errorcode + " "+ErrorCodesAuth.errcodes.get((Integer.valueOf(errorcode)));}
							}
						break;
					}
					event=xpp.next();
					}
				if("".equals(output))
					output="ERROR! Check your internet connection";
				Handler handler=new Handler(getBaseContext().getMainLooper());
				handler.post(new Runnable(){
					public void run(){
						Toast.makeText(getBaseContext(),output, Toast.LENGTH_LONG).show();
					}
				});
				}
			
		}
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_auth);
		et1=(EditText)findViewById(R.id.et1);
		et2=(EditText)findViewById(R.id.et2);
		
		gender = (Spinner) findViewById(R.id.rg1);
		 b=(Button)findViewById(R.id.btn1);
		
		 
		    b.setOnClickListener(new OnClickListener() {
		    	  @Override
			      public void onClick(View v) {
		    		  String gen ="";
		    		  gen=gender.getSelectedItem().toString();
		    		  if("MALE".equals(gen))
		    			  gen="M";
		    		  else
		    			  if("FEMALE".equals(gen))
		    				  gen="F";
		    			  else
		    				  if("TRANSGENDER".equals(gen))
		    					  gen="T";
		    		  
		    				  else
		    				  {
		    					  Toast.makeText(getBaseContext(),"Please fill your gender", Toast.LENGTH_SHORT).show();
		    					 
		    				  }
		    		  String name=et2.getText().toString();
		  			String uid=et1.getText().toString();
		  			int length=uid.length();
					
					if(length==12)
					{
					boolean i=Verhoeff.validateVerhoeff(uid);
					if(i)
					{
						
						
						//good
						try{
							if(!("".equals(gen)))
							
						
						new AuthRequestReceive().execute(uid,name,gen);
						
						
						}
						catch(Exception e)
						{
							Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
						}
						
					}
					else
					{
						Toast.makeText(getBaseContext(),"Enter a valid Aadhaar number",Toast.LENGTH_SHORT).show();
						
					}
					
					}
					else//everything is good
					{
						
						Toast.makeText(getBaseContext(),"Enter a valid 12-digit Aadhaar number",Toast.LENGTH_SHORT).show();
						
					}
					
					
					
					
				}
			        
			     
			   });	
			
		
	

	
}
}
