# ToggleView
开关切换控件

## 截图
![images](https://github.com/Wiser-Wong/ToggleView/blob/master/images/toggle.gif)

## 环境配置
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    
    dependencies {
	        implementation 'com.github.Wiser-Wong:ToggleView:1.0'
	}
  
## 使用控件

    //打开开关
    toggleView.openToggle();
    //关上开关
    toggleView.closeToggle();

    //开关监听
    toggleView.setOnToggleListener(this);

    <com.wiser.toggle.ToggleView
        android:id="@+id/toggleView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:padding="10dp"
        app:toggleBackgroundCloseColor="#80cccccc"
        app:toggleBackgroundOpenColor="#ffff00"
        app:toggleBarColor="#ffffff"
        app:toggleBarPadding="3dp"
        app:toggleBarSrc="@mipmap/chat_toggle_btn"
        app:toggleFrameColor="#ff00ff"
        app:toggleFrameWidth="1dp"
        app:toggleIsFrame="false"
        app:toggleIsHasShadow="true"
        app:toggleRadius="13dp"
        app:toggleShadowColor="@color/colorAccent"
        app:toggleShadowPadding="10dp" />
        
## 操作手册

* toggleBackgroundCloseColor：开关关闭背景颜色
* toggleBackgroundOpenColor：开关打开背景颜色
* toggleBarColor：开关Bar颜色
* toggleBarPadding：开关Bar与边框距离
* toggleBarSrc：开关Bar图片Drawable或mipmap下图片资源
* toggleFrameColor：开关边框颜色
* toggleFrameWidth：开关边框宽度
* toggleIsFrame：是否有边框 默认没有
* toggleIsHasShadow：是否Bar有阴影
* toggleRadius：开关Bar半径大小
* toggleShadowColor：Bar阴影颜色
* toggleShadowPadding：Bar阴影距离
