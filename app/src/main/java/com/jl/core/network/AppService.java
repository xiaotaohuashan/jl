package com.jl.core.network;

import com.jl.core.network.httpInterface.NetInterface;

public class AppService {

    private NetInterface loginInterface;

    /**
     * 登录
     * @return
     */
    public NetInterface getLoginService() {
        if(loginInterface == null) {
            loginInterface = new NetInterface(ApiConstants.BASE_URL);
        }
        return loginInterface;
    }

}
