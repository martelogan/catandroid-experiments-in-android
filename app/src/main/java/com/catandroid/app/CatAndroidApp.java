package com.catandroid.app;

import android.app.Application;
import android.content.Context;

import com.catandroid.app.common.components.Board;
import com.catandroid.app.common.logistics.Settings;
import com.catandroid.app.common.ui.TextureManager;

public class CatAndroidApp extends Application {

	private Settings settingsInstance;
	private TextureManager textureManagerInstance;
	private Board boardInstance;
	
	private static Context context;
	private static CatAndroidApp instance;

	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;
		
		context = getBaseContext();

		// load settings
		settingsInstance = new Settings(getBaseContext());

		setTextureManagerInstance(null);
		setBoardInstance(null);
	}
	
	public static CatAndroidApp getInstance() {
		return instance;
	}
	
	public Context getContext() {
		return context;
	}

	public void setBoardInstance(Board boardInstance) {
		this.boardInstance = boardInstance;
	}

	public Board getBoardInstance() {
		return boardInstance;
	}

	public void setTextureManagerInstance(TextureManager textureManagerInstance) {
		this.textureManagerInstance = textureManagerInstance;
	}

	public TextureManager getTextureManagerInstance() {
		return textureManagerInstance;
	}

	public Settings getSettingsInstance() {
		return settingsInstance;
	}
}
