package com.jl.core.gateway;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class Gateway {

    private boolean _opened = false;
    private Uri _uri = null;
    private String _webViewClassName = null;
    private String _launchViewClassName = null;
    private String _mainActivityClassName = null;
    private boolean _isLaunchCompleted = false;

    private static class Holder {
        private static Gateway gateway = new Gateway();
    }

    private Gateway(){
    }

    public static Gateway getInstance(){
        return Holder.gateway;
    }

    public void shouldOpenActivity(Activity currentActivity){
        if(!_opened && _uri != null){
            if (_mainActivityClassName == currentActivity.getClass().getName()){
                _isLaunchCompleted = true;
                openWebview(currentActivity);
            }
        }
    }

    private void openWebview(Activity currentActivity){
        try{
            // 有其它规则判断，继续在这里添加
            // TODO: 需要判断 host是否合法，否则不打开
            if(_webViewClassName != null){
                Class<?> c = Class.forName(_webViewClassName);
                Intent intent = new Intent(currentActivity, c);
                intent.putExtra("uri", _uri.toString().replace("banma://", "http://"));
                currentActivity.startActivity(intent);
                _opened = true;
                _uri = null;
            }
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void startLaunchActivity(Activity ghostActivity){
        if(_isLaunchCompleted){
            openWebview(ghostActivity);
            return;
        }
        if (_launchViewClassName != null){
            try{
                Class<?> c = Class.forName(_launchViewClassName);
                Intent intent = new Intent(ghostActivity, c);
                ghostActivity.startActivity(intent);
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    public void registWebViewActivity(Class cl) {
        _webViewClassName = cl.getName();
    }

    public void registLaunchViewActivity(Class cl){
        _launchViewClassName = cl.getName();
    }

    public void registMainActivity(Class cl){
        _mainActivityClassName = cl.getName();
    }

    public void setUri(Uri uri) {
        _opened = false;
        _uri = uri;
    }
}
