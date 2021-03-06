/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.web.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestResponse {

    private Object payload = new ArrayList<>();
    private List<RestError> errors = new ArrayList<>();
    private Object metaData = new HashMap<>();

    public RestResponse() {
    }

    public RestResponse(Object payload) {
        this.payload = payload;
    }

    public RestResponse(Object payload, List<RestError> errors, Object metaData) {
        if (null != payload) {
            this.setPayload(payload);
        }
        if (null != errors) {
            this.setErrors(errors);
        }
        if (null != metaData) {
            this.setMetaData(metaData);
        }
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Object getMetaData() {
        return metaData;
    }

    public void setMetaData(Object metaData) {
        this.metaData = metaData;
    }

    public List<RestError> getErrors() {
        return errors;
    }

    public void setErrors(List<RestError> errors) {
        this.errors = errors;
    }

    public void addErrors(List<RestError> errors) {
        this.errors.addAll(errors);

    }

}
