package com.bot.wattanachaitodolist.infra.line.api.v2.response;

import lombok.Builder;

@Builder
public final class AccessToken {

    public final String scope;
    public final String access_token;
    public final String token_type;
    public final Integer expires_in;
    public final String refresh_token;
    public final String id_token;

    public AccessToken(String scope, String access_token, String token_type, Integer expires_in, String
            refresh_token, String id_token) {
        this.scope = scope;
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.id_token = id_token;
    }

}
