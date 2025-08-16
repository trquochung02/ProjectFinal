package com.team3.utils;

import jakarta.servlet.http.HttpServletRequest;

public class GetSiteUrl {

    public static String getSiteUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }
}
