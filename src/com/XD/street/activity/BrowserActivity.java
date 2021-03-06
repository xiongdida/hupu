package com.XD.street.activity;

import java.net.URLEncoder;

import com.XD.street.R;
import com.XD.street.model.LoginModel;
import com.XD.street.utils.dataUtils;
import com.XD.street.view.LoadingView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class BrowserActivity extends Activity{

	private WebView mWebView;
	private ImageButton mPrevious;
	private ImageButton mForward;
	private LoadingView mLoading;
	private int mCount;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser_frame);

		Intent intent = getIntent();
		String url = intent.getStringExtra("url");

		mWebView = (WebView) findViewById(R.id.browser_webview);
		mPrevious = (ImageButton) findViewById(R.id.browser_prev);
		mForward = (ImageButton) findViewById(R.id.browser_forward);

		mCount = 0;
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		// handle cookie
		try {
			String token = LoginModel.getInstance(this).getToken();
			if (token != null) {
				CookieSyncManager.createInstance(this);
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setCookie("http://www.hupu.com", "u="
						+ URLEncoder.encode(token, "utf-8") + ";"
						+ " path=/; domain=.hupu.com;");
				CookieSyncManager.getInstance().sync();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				System.out.println("BrowserActivity: onPageFinished");
				super.onPageFinished(view, url);
				mCount--;
				if (mCount <= 0) {
					mCount = 0;
					mLoading.hide();
					refreshBottomItems();
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				mCount++;
				if (!mLoading.isLoading) {
					mLoading.show();
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				mCount--;
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

		});
		mLoading = new LoadingView(this);
		mWebView.loadUrl(url);
	}

	private void refreshBottomItems() {
		mPrevious.setEnabled(false);
		mForward.setEnabled(false);
		if (mWebView.canGoBack()) {
			mPrevious.setEnabled(true);
		}
		if (mWebView.canGoForward()) {
			mForward.setEnabled(true);
		}
	}

	public void OnBottomItemClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.browser_back:
				finish();
				break;
			case R.id.browser_prev:
				mWebView.goBack();
				break;
			case R.id.browser_forward:
				mWebView.goForward();
				break;
			case R.id.browser_refresh:
				mWebView.reload();
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
