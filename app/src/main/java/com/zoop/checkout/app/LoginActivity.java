package com.zoop.checkout.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zoop.checkout.app.API.LoginService;
import com.zoop.checkout.app.API.RetrofitInstance;
import com.zoop.checkout.app.Model.APIKey;
import com.zoop.checkout.app.Model.APIKeys;
import com.zoop.checkout.app.Model.LoginRequest;
import com.zoop.checkout.app.Model.Marketplace;
import com.zoop.checkout.app.Model.Permission;
import com.zoop.checkout.app.Model.User;
import com.zoop.checkout.app.Model.UserPermissions;
import com.zoop.zoopandroidsdk.ZoopAPI;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.Configuration;
import com.zoop.zoopandroidsdk.commons.UFUC;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopSessionHTTPJSONResponseException;
import com.zoop.zoopandroidsdk.sessions.Retrofit;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsCheckout;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;

import org.json.JSONObject;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This source file does not use the Zoop Android SDK/ API. This is part of the demo application.
 * It is, however, a good example of how to make calls to the Zoop API/ Zoop Gateway.
 * @author Rodrigo Miranda (rodrigo@pagzoop.com)
 */
public class LoginActivity extends AppCompatActivity {
	public static Context context;
	//	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	private UserLoginTask mAuthTask = null;
	// Values for email and password at the time of the login attempt.
	private String sUsername;
	private String sPassword;
	//String sUsername = ((EditText) findViewById(R.id.email)).getText().toString();
	//String sPassword = ((EditText) findViewById(R.id.password)).getText().toString();
	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	String[] sellersNameList;
	String[] sellersIdList;
	String sIntentParamUser = null;
	String sIntentParamPassword = null;
	Bundle params;
	String Value;
	String  paymentOption;
	Integer number_installments;
	String loginErrorMessage = "";
	private void setLoginErrorMessage(String message) {
		loginErrorMessage = message;
		mPasswordView.setError(message);
		mPasswordView.requestFocus();
		showProgress(false);
	}
	private String getLoginErrorMessage() {
		return loginErrorMessage;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();

		// can be ignored by Zoop Android SDK/ API Developer.
		// These are used to bootstrap the common Zoop Libraries used.
		///Context context = getApplicationContext();
		//Preferences.initialize(context);
		//Preferences demoPreferences = Preferences.getInstance();
		try {
			ZoopAPI.basicInitialize(getApplication(), ApplicationConfiguration.APPLICATION_ID);
		} catch (Exception e) {
			ZLog.exception(300033, e);
		}
		//Preferences.initialize(getApplicationContext());
		setContentView(R.layout.activity_login_new);
		// Set up the login form.
//		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
//		mEmailView.setText(mEmail);
		Intent intent = getIntent();
		params = intent.getExtras();
		if (params != null) {
			mLoginFormView = findViewById(R.id.login_form);
			mLoginStatusView = findViewById(R.id.login_status);
			mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
			sIntentParamUser = params.getString("user");
			sIntentParamPassword = params.getString("password");
			Value = params.getString("Value");
			paymentOption = params.getString("payment_type");
			if (params.getInt("number_installments") > 0) {
				number_installments = params.getInt("number_installments");
			}
			attemptLogin();
		}
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		findViewById(R.id.sign_in_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("sign_in", null);
						attemptLogin();
						Extras.hideKeyboard(LoginActivity.this);
					}
				});
		//TextView) findViewById(R.id.textViewVersionInfo)).setText(Extras.getVersionString(getResources())+"-"+ com.zoop.api.Version.getVersion());
		((View) findViewById(R.id.bannerZoop)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ZLog.t(300038);
			}
		});

		if (Configuration.DEBUG_MODE) {
			((EditText) findViewById(R.id.email)).setText("teste@pagzoop.com");
			((EditText) findViewById(R.id.password)).setText("test1234");
		}
		TextView tNewAccount=(TextView)findViewById(R.id.tNew_account);
		tNewAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckoutApplication.getFirebaseAnalytics().logEvent("sign_up", null);
			}
		});
		String sUrlNewAccount=ApplicationConfiguration.WEB_PORTAL_URL_WITH_SLUG+"#signin";
		tNewAccount.setText( Html.fromHtml("<a href=\""+sUrlNewAccount+"\">Novo por aqui? Crie sua conta</a> "));
		tNewAccount.setMovementMethod(LinkMovementMethod.getInstance());

		if (ApplicationConfiguration.LOGIN_API.equals("Compufour")) {
			tNewAccount.setVisibility(View.GONE);
		}
	}


	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
		if (params != null) {
			sUsername = sIntentParamUser;
			sPassword = sIntentParamPassword;
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(getResources().getString(R.string.login_label_signing_in));
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		} else {
			boolean cancel = false;
			View focusView = null;
			// Reset errors.
			mEmailView.setError(null);
			mPasswordView.setError(null);
			// Store values at the time of the login attempt.
			sUsername = mEmailView.getText().toString();
			sPassword = mPasswordView.getText().toString();
			// Check for a valid password.
			if (TextUtils.isEmpty(sPassword)) {
				mPasswordView.setError(getResources().getString(R.string.message_password_is_mandatory));
				focusView = mPasswordView;
				cancel = true;
			} else if (sPassword.length() < 4) {
				mPasswordView.setError(getResources().getString(R.string.error_message_invalid_username_or_password));
				focusView = mPasswordView;
				cancel = true;
			}
			// Check for a valid email address.
			if (TextUtils.isEmpty(sUsername)) {
				// ATTENTION TO THE MESSAGE: The message below allows for a valid user@subdomain for simple taxi driver login
				mEmailView.setError(getResources().getString(R.string.message_username_must_be_filled));
				focusView = mEmailView;
				cancel = true;
			}
		/*
		else if (!mEmail.contains("@")) {
			mEmailView.setError("Endereço de e-mail inválido");
			focusView = mEmailView;
			cancel = true;
		}
		*/
			if (cancel) {
				// There was an error; don't attempt login and focus the first
				// form field with an error.
				focusView.requestFocus();
			} else {
				// Show a progress spinner, and kick off a background task to
				// perform the user login attempt.
				mLoginStatusMessageView.setText(getResources().getString(R.string.login_label_signing_in));
				showProgress(true);

				LoginRequest loginRequest = new LoginRequest(sUsername, sPassword);

				final LoginService service = RetrofitInstance.getRetrofitInstance().create(LoginService.class);
				Call<User> call = service.login(loginRequest);

//				if ((ApplicationConfiguration.LOGIN_API.equals("Compufour")) || BuildConfig.FLAVOR.equals("medicinae")){
//					mAuthTask = new UserLoginTask();
//					mAuthTask.execute((Void) null);
//				}else {
					call.enqueue(new Callback<User>() {
						@Override
						public void onResponse(Call<User> call, Response<User> response) {
							final User user = response.body();
							try{
								Call<UserPermissions> userPermissionsCall = service.getUserPermissions(user.getId(), "Bearer " + user.getToken());
								userPermissionsCall.enqueue(new Callback<UserPermissions>() {
									@Override
									public void onResponse(Call<UserPermissions> call, Response<UserPermissions> response) {
										UserPermissions permissions = response.body();
										try{
											if (permissions != null){
												Permission permission = permissions.getPermissions().get(0);
												final String marketplaceId = permission.getMarketplaceId();
												String _sellerId = "";


												for (Permission perm: permissions.getPermissions()) {
													String seller = perm.getSellerId();
													if (!seller.equals("*") ) {
														_sellerId = seller;
													}
												}

												if (_sellerId.isEmpty()){
													Call<Marketplace> marketplaceCall = service.getMarketplace(marketplaceId, "Bearer " + user.getToken());
													marketplaceCall.enqueue(new Callback<Marketplace>() {
														@Override
														public void onResponse(Call<Marketplace> call, Response<Marketplace> response) {
															Marketplace _marketplace = response.body();
															String _sellerId = _marketplace.getSeller().getId();

															if (_sellerId.isEmpty()) {
																setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
															} else {
																getAPIKeys(marketplaceId, _sellerId, user.getToken(), user.getFirstName(), user.getLastName());
															}
														}

														@Override
														public void onFailure(Call<Marketplace> call, Throwable t) {
															setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
														}
													});
												} else {
													getAPIKeys(marketplaceId, _sellerId, user.getToken(), user.getFirstName(), user.getLastName());
												}
											} else {
												throw new Exception("");
											}
										}
										catch (Exception e){
											setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
										}
									}

									@Override
									public void onFailure(Call<UserPermissions> call, Throwable t) {
										Log.d("ERROR", t.getMessage());
										setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
									}
								});
							}catch (Exception e){
								mAuthTask = null;
								showProgress(false);
								setLoginErrorMessage(getResources().getString(R.string.login_error));
								return;
							}
						}
						@Override
						public void onFailure(Call<User> call, Throwable t) {
							Log.d("ERROR", "ERROR");
							setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
						}
					});
				}

//			}
		}
	}

	private void getAPIKeys (final String marketplaceId, final String sellerId, final String token, final String firstName, final String lastName) {
		final LoginService service = RetrofitInstance.getRetrofitInstance().create(LoginService.class);
		Call<APIKeys> apiKeysCall = service.getAPIKeys(marketplaceId, "Bearer " + token);
		apiKeysCall.enqueue(new Callback<APIKeys>() {
			@Override
			public void onResponse(Call<APIKeys> call, Response<APIKeys> response) {
				APIKeys apiKeys = response.body();
				final APIKey apiKey = apiKeys.getApiKeys().get(0);



				Call<Object> sellerCall = service.getSeller(marketplaceId, sellerId, "Bearer " + token + " " + marketplaceId);
				sellerCall.enqueue(new Callback<Object>() {
					@Override
					public void onResponse(Call<Object> call, Response<Object> response) {
						final String seller = new Gson().toJson(response.body());

						try {
							if (response.body() == null) {
								throw new Exception("");
							}
						}catch (Exception e) {
							mAuthTask = null;
							showProgress(false);
							setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
							return;
						}

						Call<Object> subscriptionCall = service.getSubscriptions(marketplaceId, sellerId ,"Bearer" + token + " " + marketplaceId);
						subscriptionCall.enqueue(new Callback<Object>() {
							@Override
							public void onResponse(Call<Object> call, Response<Object> response) {
								final String joPlanSubscription = new Gson().toJson(response.body());

								Call<Object> planCall = service.getPlans(marketplaceId, "Bearer" + token + " " + marketplaceId);
								planCall.enqueue(new Callback<Object>() {
									@Override
									public void onResponse(Call<Object> call, Response<Object> response) {
										final String joPlan = new Gson().toJson(response.body());
										saveLoginDataToAPISettings(apiKey.getPublishableKey(), marketplaceId, firstName, lastName, sellerId, seller, joPlanSubscription, joPlan);
									}

									@Override
									public void onFailure(Call<Object> call, Throwable t) {
										setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
									}
								});

							}

							@Override
							public void onFailure(Call<Object> call, Throwable t) {
								setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
							}
						});
					}

					@Override
					public void onFailure(Call<Object> call, Throwable t) {
						setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
					}
				});
			}

			@Override
			public void onFailure(Call<APIKeys> call, Throwable t) {
				setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
			}
		});
	}

	private void saveLoginDataToAPISettings (String publishableKey, String marketplaceId, String firstName, String lastName, String sellerId, String seller, String PlanSubscription, String Plan) {
		try {
			JSONObject joSeller = new JSONObject(seller);
			JSONObject joPlanSubscriptions = new JSONObject(PlanSubscription) ;
			JSONObject joplan = new JSONObject(Plan);

			APIParameters ap = APIParameters.getInstance();
			ap.putGlobalStringParameter("publishableKey", publishableKey);
//        ap.putGlobalStringParameter("sCheckoutPublicKey",sCheckoutPublicKey);
			ap.putGlobalStringParameter("marketplaceId", marketplaceId);
			if (joSeller.has("phone_number")) ap.putGlobalStringParameter("phoneddd", joSeller.getString("phone_number"));
			if (joSeller.has("business_phone")) ap.putGlobalStringParameter("phoneddd", joSeller.getString("business_phone"));


			if ((firstName != null) && (lastName != null)) {
				ap.putGlobalStringParameter("firstname", firstName);
				ap.putGlobalStringParameter("lastname", lastName);
			} else{
				if (joSeller.has("first_name")) ap.putGlobalStringParameter("firstname", joSeller.getString("first_name"));
				if (joSeller.has("last_name")) ap.putGlobalStringParameter("lastname", joSeller.getString("last_name"));
				if (joSeller.has("business_name")) ap.putGlobalStringParameter("firstname", joSeller.getString("business_name"));
				if (joSeller.has("business_name")) ap.putGlobalStringParameter("lastname", "    ");
			}

			ap.putGlobalStringParameter("Seller", seller);
			ap.putGlobalStringParameter("sellerId", sellerId);

			ap.putGlobalStringParameter("currentLoggedinUsername", sUsername);
			ap.putGlobalStringParameter("currentLoggedinSecurityToken", sPassword);
			try {
				APIParameters.getInstance().processAPIParametersInitialization(sellerId);

				CallUpdateApiParameters.getInstance().initializeApiParameters(LoginActivity.this);

			} catch (Exception e) {
				e.printStackTrace();
			}
			String statusJoSeller = joSeller.getString("status");
			if((statusJoSeller.equals("active"))||(statusJoSeller.equals("enabled"))) {
				ap.putBooleanParameter("seller_activate", true);
			}else{
				ap.putBooleanParameter("seller_activate", false);

			}
			String sSellerStatus = joSeller.getString("status");
			String sSellerStatusRegex = ap.getStringParameter(APISettingsConstants.ZoopCheckout_RegexAcceptedSellerStatuses);
			Preferences.getInstance().setApplicationSellerAttributes(joSeller);
			if (sSellerStatus.matches(sSellerStatusRegex)) {
			}
			else {
				// ToDo: Add message retrieved from server
				ChargeActivity.addMessageUponLogin(ChargeActivity.SHOW_MESSAGE_SELLER_IS_NOT_READY_FOR_CHARGING_CUSTOMERS);
			}
//            String sPlanURL = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + marketplaceId + "/plans");
//            JSONObject joPlan= ZoopSessionsPayments.getInstance().syncGet(sPlanURL, publishableKey,LoginActivity.this);
//            String sPlanSubscriptionsUrl = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + marketplaceId + "/sellers/" + sellerId + "/subscriptions");
//            JSONObject joPlanSubscriptions= ZoopSessionsPayments.getInstance().syncGet(sPlanSubscriptionsUrl, publishableKey,LoginActivity.this);
			ap.putStringParameter("plan", joplan.toString());
			ap.putStringParameter("planSubscriptions", joPlanSubscriptions.toString());
			if(joPlanSubscriptions.getJSONArray("items").length()<1){
				int length=joplan.getJSONArray("items").length();
				for(int i=0;i<length;i++){
					if(joplan.getJSONArray("items").getJSONObject(i).getBoolean("is_default_per_transaction")){
///*
						ap.putStringParameter("planSubscription", joplan.getJSONArray("items").getJSONObject(i).toString());
//*/
						ap.putStringParameter("planSubscriptionId", joplan.getJSONArray("items").getJSONObject(i).getString("id").toString());
					}
				}
			}else {
				//ap.putStringParameter("planSubscription", joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getJSONObject("plan").toString());
				ap.putStringParameter("planSubscriptionId", joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getJSONObject("plan").getString("id"));
			}
//
			if (params != null) {
				Intent chargeIntent = new Intent(LoginActivity.this, ChargeActivity.class);
				chargeIntent.putExtra("Value",Value);
				chargeIntent.putExtra("Tipo_pagamento",paymentOption);
				if (paymentOption.equalsIgnoreCase("credito_parcel")) {
					chargeIntent.putExtra("number_installments",number_installments);

				}
				startActivityForResult(chargeIntent, 2);
			}
			else {
				Extras.checkZoopTerminalsAndGoToNextStep(LoginActivity.this);

			}
		} catch (Exception e) {
			e.printStackTrace();
			setLoginErrorMessage(getResources().getString(R.string.login_connection_error));

		}

	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);
			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		}
		else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				JSONObject joUser = null;
				if (sIntentParamUser != null) {
					sUsername = sIntentParamUser;
					sPassword = sIntentParamPassword;
				}
				String sAdditionalLoginString = null;
				// if the username has the test prefix, we use the Checkout API test public key
				String sCheckoutPublicKey ;
				// If there is any server suffix, separate it and send as 2 parameters
				String regexGetServerSuffixEnvironmentMagic =  APIParameters.getInstance().getStringParameter(APISettingsConstants.ZoopCheckout_AlternateServerSuffixRegex);
				if (regexGetServerSuffixEnvironmentMagic != null) {
					Matcher m = Pattern.compile(regexGetServerSuffixEnvironmentMagic).matcher(sUsername);
					if (m.find()) {
						sAdditionalLoginString = m.group(1);
						APIParameters.getInstance().putStringParameter(Integer.toString(APISettingsConstants.ZoopCheckout_AdditionalLoggedInString), sAdditionalLoginString);
					}
				}

				if (sUsername.indexOf("#") > 0) { // If there is a additional login string (#xxx#)
					sUsername = sUsername.substring(0, sUsername.indexOf("#"));
				}
				sCheckoutPublicKey = Preferences.getInstance().loadUFUAndGetCheckoutPublicKey(sUsername, sAdditionalLoginString);
				int atPosition = sUsername.indexOf('@');
				if (atPosition > 0) {
					if (-1 == sUsername.indexOf(".", atPosition)) {
						sUsername = sUsername+"."+ApplicationConfiguration.LOGIN_DOMAIN_SUFFIX_AUTO_APPEND;
					}
				}
				else {
					sUsername = sUsername+"@"+ApplicationConfiguration.LOGIN_DOMAIN_SUFFIX_AUTO_APPEND;
				}
				Map<String, String> requestParams = new HashMap<>();

				APIParameters ap = APIParameters.getInstance();
				APIParameters.getInstance().putStringParameter("sCheckoutPublicKey", sCheckoutPublicKey);
				String sMarketplaceId=null;
				JSONObject joContent=null;
				JSONObject joMerchant=null;
				String publishableKey=null;
				String sFirstName=null;
				String slastName=null;
				String merchant=null;
				String sSellerId=null;
				String phoneNumber=null;
				JSONObject joResponse = null;
				try {
					String sURL;
					if(ApplicationConfiguration.LOGIN_API.equals("Compufour")){
						sURL = "rpc/v1/application.authenticate.json";
						String baseUrl="https://app.compufacil.com.br";
						HashMap<String, String> compufourParams = new HashMap<>();
						compufourParams.put("login", sUsername);
						compufourParams.put("password", sPassword);
						joResponse = Retrofit.getInstance().syncPost(sURL, baseUrl, sCheckoutPublicKey, null,LoginActivity.this, compufourParams);
					} else {
						requestParams.put("email", sUsername);
						requestParams.put("password", sPassword);
						if (BuildConfig.FLAVOR.equals("medicinae")) {
							sURL = getResources().getString(R.string.medicinae_login_url);
							String baseUrl= getResources().getString(R.string.medicinae_base_url);
							joResponse = Retrofit.getInstance().syncPost(sURL, baseUrl, sCheckoutPublicKey, requestParams, LoginActivity.this, null);
						} else {
							sURL = Extras.getInstance().getCheckoutWebservicesBaseURL() + "/sessions";

							joResponse = ZoopSessionsCheckout.getInstance().syncPost(sURL, sCheckoutPublicKey, requestParams, LoginActivity.this);
						}
					}
				} catch (ZoopSessionHTTPJSONResponseException zhe) {
					ZLog.exception(300009, zhe);
					//{"status":false,"message":"O usuário não foi encontrado.","code":401}
					if (zhe.getJSONOutputObject().has("message")) {
						setLoginErrorMessage(zhe.getJSONOutputObject().getJSONObject("error").getString("message"));
					}
					else {
						setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
					}
					return false;
				}
				catch (Exception e) {
					ZLog.exception(300010, e);
					setLoginErrorMessage(getResources().getString(R.string.login_connection_error));
					return false;
				}
				if (joResponse.getString("status").equals("false")||joResponse.getString("status").equals("0")) {
					setLoginErrorMessage(joResponse.getString("message"));
					return false;
				}
				else {
					try {
						if(ApplicationConfiguration.LOGIN_API.equals("Checkout")) {

							sMarketplaceId = joResponse.getJSONObject("content").getJSONArray("marketplace").getString(0);
							joContent = joResponse.getJSONObject("content");
							joUser = joContent.getJSONObject("user");
							joMerchant = joUser.getJSONObject("merchant");
							publishableKey = joContent.getJSONArray("publishable_key").getString(0);
							sFirstName = joUser.getString("firstName");
							slastName = joUser.getString("lastName");
							merchant = joResponse.getJSONObject("content").getString("merchant");

							// Used to debug a PENDING seller_id and messages
							//if (Configuration.DEBUG_MODE) {
							//	sSellerId = "4b07b67cc27646a8936304e3b124a53d";
							//}
							//else {

							sSellerId = joMerchant.getString("sellerId");

							phoneNumber=joUser.getString("phone").substring(1,3);
						}else if(ApplicationConfiguration.LOGIN_API.equals("Compufour")){
							sMarketplaceId = "0569cf0b4bc84479bb1835d1c4a62858";
							publishableKey = joResponse.getJSONObject("remote_payment_source").getString("publishable_key");
							sSellerId = joResponse.getJSONObject("remote_payment_source").getString("seller_id");

						}




						String regexUsersToRemoveInstallments = ap.getStringParameter(APISettingsConstants.PaymentType_UsernamesToRemoveRegex);
						if (true == APIParameters.getInstance().getBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, true)) {
							if (sUsername.matches(regexUsersToRemoveInstallments)) {
								ap.putBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, false);
							} else {
								ap.putBooleanParameter(APISettingsConstants.PaymentType_showCreditWithInstallments, true);
							}
						}

						// Get the first seller in the list.
						// For simple testers, there will always be a seller selected.
						//sellerId = "50b44c1890f44de58a76c722b37e4362"; // U Street Café
						//Zoop Seller ID produção - sellerId = "6fbf7ec8fa7c4429a3b03eb85dd43364";
						String sSellersURL = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/sellers/" + sSellerId);
						JSONObject joSeller = ZoopSessionsPayments.getInstance().syncGet(sSellersURL, publishableKey,LoginActivity.this);
						if (Configuration.DEBUG_MODE) {
							//   ap.dumpAPISettings();
						}
						try {
							if(ApplicationConfiguration.LOGIN_API.equals("Checkout")) {
								ap.putStringParameter("merchant", merchant);
								ap.putGlobalStringParameter("merchant", merchant);


							}else if(ApplicationConfiguration.LOGIN_API.equals("Compufour")) {
								if(joSeller.getString("type").equals("business")){
									sFirstName=joSeller.getJSONObject("owner").getString("first_name");
									slastName=joSeller.getJSONObject("owner").getString("last_name");
									phoneNumber=joSeller.getJSONObject("owner").getString("phone_number").substring(1,3);
								}else if(joSeller.getString("type").equals("individual")){
									sFirstName=joSeller.getString("first_name");
									slastName=joSeller.getString("last_name");
									phoneNumber=joSeller.getString("phone_number").substring(1,3);
								}

							}

							ap.putGlobalStringParameter("publishableKey", publishableKey);
							ap.putGlobalStringParameter("sCheckoutPublicKey",sCheckoutPublicKey);
							ap.putGlobalStringParameter("marketplaceId", sMarketplaceId);
							ap.putGlobalStringParameter("phoneddd",phoneNumber);
							ap.putGlobalStringParameter("firstname", sFirstName);
							ap.putGlobalStringParameter("lastname", slastName);
							ap.putGlobalStringParameter("Seller", joSeller.toString());
							ap.putGlobalStringParameter("sellerId", sSellerId);

							ap.putGlobalStringParameter("currentLoggedinUsername", sUsername);
							ap.putGlobalStringParameter("currentLoggedinSecurityToken", sPassword);
							try {
								APIParameters.getInstance().processAPIParametersInitialization(sSellerId);

								CallUpdateApiParameters.getInstance().initializeApiParameters(LoginActivity.this);

							} catch (Exception e) {
								e.printStackTrace();
							}
							String statusJoSeller=joSeller.getString("status");
							if((statusJoSeller.equals("active"))||(statusJoSeller.equals("enabled"))) {
								ap.putBooleanParameter("seller_activate", true);
							}else{
								ap.putBooleanParameter("seller_activate", false);

							}
							String sSellerStatus = joSeller.getString("status");
							String sSellerStatusRegex = ap.getStringParameter(APISettingsConstants.ZoopCheckout_RegexAcceptedSellerStatuses);
							Preferences.getInstance().setApplicationSellerAttributes(joSeller);
							if (sSellerStatus.matches(sSellerStatusRegex)) {
							}
							else {
								// ToDo: Add message retrieved from server
								ChargeActivity.addMessageUponLogin(ChargeActivity.SHOW_MESSAGE_SELLER_IS_NOT_READY_FOR_CHARGING_CUSTOMERS);
							}
							String sPlanURL = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/plans");
							JSONObject joPlan= ZoopSessionsPayments.getInstance().syncGet(sPlanURL, publishableKey,LoginActivity.this);
							String sPlanSubscriptionsUrl = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/sellers/" + sSellerId + "/subscriptions");
							JSONObject joPlanSubscriptions= ZoopSessionsPayments.getInstance().syncGet(sPlanSubscriptionsUrl, publishableKey,LoginActivity.this);
							ap.putStringParameter("plan",joPlan.toString());
							ap.putStringParameter("planSubscriptions", joPlanSubscriptions.toString());
							if(joPlanSubscriptions.getJSONArray("items").length()<1){
								int length=joPlan.getJSONArray("items").length();
								for(int i=0;i<length;i++){
									if(joPlan.getJSONArray("items").getJSONObject(i).getBoolean("is_default_per_transaction")){
/*
										ap.putStringParameter("planSubscription", joPlan.getJSONArray("items").getJSONObject(i).toString());
*/
										ap.putStringParameter("planSubscriptionId", joPlan.getJSONArray("items").getJSONObject(i).getString("id").toString());
									}
								}
							}else {
								//ap.putStringParameter("planSubscription", joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getJSONObject("plan").toString());
								ap.putStringParameter("planSubscriptionId", joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getJSONObject("plan").getString("id"));
							}
						}
						catch (Exception e) {
							ZLog.exception(677292, e);
						}
					}
					catch (Exception e) {
						ZLog.exception(300033, e);
						setLoginErrorMessage(getResources().getString(R.string.could_not_retrieve_seller));
						return false;
					}
					return true;
				}

			} catch (Exception e) {
//				setLoginErrorMessage(getResources().getString(R.string.login_unexpected_error));
				e.printStackTrace();
				L.e("Error validating network login", e);
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			if (success) {
				if (params != null) {
					Intent chargeIntent = new Intent(LoginActivity.this, ChargeActivity.class);
					chargeIntent.putExtra("Value",Value);
					chargeIntent.putExtra("Tipo_pagamento",paymentOption);
					if (paymentOption.equalsIgnoreCase("credito_parcel")) {
						chargeIntent.putExtra("number_installments",number_installments);

					}
					startActivityForResult(chargeIntent, 2);
				}
				else {
					Extras.checkZoopTerminalsAndGoToNextStep(LoginActivity.this);

				}
			}
			else {
				//getResources().getString(R.string.error_message_invalid_username_or_password)
				mPasswordView.setError(getLoginErrorMessage());
				mPasswordView.requestFocus();
				showProgress(false);
			}
		}




		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==2)
		{
			String message = data.getStringExtra("MESSAGE");
			Intent intent = new Intent();
			Boolean success = data.getBooleanExtra("success",false);
			intent.putExtra("MESSAGE",message);
			intent.putExtra("success",success);

			setResult(2, intent);
			finish();

		}
	}

}
