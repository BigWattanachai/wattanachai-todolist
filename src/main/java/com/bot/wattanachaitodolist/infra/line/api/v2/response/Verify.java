package com.bot.wattanachaitodolist.infra.line.api.v2.response;

public final class Verify {

    public final String scope;
    public final String client_id;
    public final Integer expires_in;

    public Verify(String scope, String client_id, Integer expires_in) {
        this.scope = scope;
        this.client_id = client_id;
        this.expires_in = expires_in;
    }

}
