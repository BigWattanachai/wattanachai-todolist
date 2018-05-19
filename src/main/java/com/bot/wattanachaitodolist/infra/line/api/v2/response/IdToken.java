package com.bot.wattanachaitodolist.infra.line.api.v2.response;

import lombok.Builder;

@Builder
public final class IdToken {
    public final String iss;
    public final String sub;
    public final String aud;
    public final Long exp;
    public final Long iat;
    public final String nonce;
    public final String name;
    public final String picture;

    public IdToken(String iss, String sub, String aud, Long exp, Long iat, String nonce, String name, String picture) {
        this.iss = iss;
        this.sub = sub;
        this.aud = aud;
        this.exp = exp;
        this.iat = iat;
        this.nonce = nonce;
        this.name = name;
        this.picture = picture;
    }
}

