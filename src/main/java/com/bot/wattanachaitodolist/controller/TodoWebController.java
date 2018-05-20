package com.bot.wattanachaitodolist.controller;

import com.bot.wattanachaitodolist.infra.line.api.v2.response.AccessToken;
import com.bot.wattanachaitodolist.infra.utils.CommonUtils;
import com.bot.wattanachaitodolist.service.LineAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
@Slf4j
public class TodoWebController {
    private static final String LINE_WEB_LOGIN_STATE = "lineWebLoginState";
    static final String ACCESS_TOKEN = "accessToken";
    private static final String NONCE = "nonce";
    private LineAPIService lineAPIService;

    @Autowired
    public TodoWebController(LineAPIService lineAPIService) {
        this.lineAPIService = lineAPIService;
    }

    @RequestMapping("/")
    public String login() {
        return "todolist/login";
    }

    @RequestMapping("/auth")
    public String auth(
            HttpSession httpSession,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "errorCode", required = false) String errorCode,
            @RequestParam(value = "errorMessage", required = false) String errorMessage) {

        if (error != null || errorCode != null || errorMessage != null) {
            return "redirect:/loginCancel";
        }

        if (!state.equals(httpSession.getAttribute(LINE_WEB_LOGIN_STATE))) {
            return "redirect:/sessionError";
        }

        httpSession.removeAttribute(LINE_WEB_LOGIN_STATE);
        AccessToken token = lineAPIService.accessToken(code);

        httpSession.setAttribute(ACCESS_TOKEN, token);
        return "redirect:/todos";
    }

    @RequestMapping("/todos")
    public String success(HttpSession httpSession, Model model) {


        AccessToken token = (AccessToken) httpSession.getAttribute(ACCESS_TOKEN);
        if (token == null) {
            return "redirect:/";
        }

        if (!lineAPIService.verifyIdToken(token.id_token, (String) httpSession.getAttribute(NONCE))) {
            // verify failed
            return "redirect:/";
        }

        return "todolist/todos";
    }

    @RequestMapping(value = "/gotoauthpage")
    public String goToAuthPage(HttpSession httpSession) {
        final String state = CommonUtils.getToken();
        final String nonce = CommonUtils.getToken();
        httpSession.setAttribute(LINE_WEB_LOGIN_STATE, state);
        httpSession.setAttribute(NONCE, nonce);
        final String url = lineAPIService.getLineWebLoginUrl(state, nonce, Arrays.asList("openid", "profile"));
        return "redirect:" + url;
    }
}
