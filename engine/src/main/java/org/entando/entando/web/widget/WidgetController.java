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
package org.entando.entando.web.widget;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.role.Permission;
import org.entando.entando.aps.system.services.widgettype.IWidgetService;
import org.entando.entando.aps.system.services.widgettype.model.WidgetDto;
import org.entando.entando.aps.system.services.widgettype.model.WidgetInfoDto;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.common.model.RestResponse;
import org.entando.entando.web.widget.model.WidgetRequest;
import org.entando.entando.web.widget.validator.WidgetValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WidgetController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IWidgetService widgetService;

    @Autowired
    private WidgetValidator widgetValidator;

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/widgets/{widgetCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<WidgetDto>> getWidget(@PathVariable String widgetCode) {
        logger.trace("getWidget by code {}", widgetCode);
        WidgetDto group = this.widgetService.getWidget(widgetCode);
        return new ResponseEntity<>(new RestResponse(group), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/widgets/{widgetCode}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE, name = "widget")
    public ResponseEntity<RestResponse> deleteWidget(@PathVariable String widgetCode) throws ApsSystemException {
        logger.info("deleting widget {}", widgetCode);
        this.widgetService.removeWidget(widgetCode);
        Map<String, String> result = new HashMap<>();
        result.put("code", widgetCode);
        return new ResponseEntity<>(new RestResponse(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/widgets/{widgetCode}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, name = "widget")
    public ResponseEntity<RestResponse<WidgetDto>> updateWidget(@PathVariable String widgetCode, @Valid @RequestBody WidgetRequest widgetRequest, BindingResult bindingResult) {
        logger.trace("update widget. Code: {} and body {}: ", widgetCode, widgetRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.widgetValidator.validateWidgetCode(widgetCode, widgetRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        WidgetDto widgetDto = this.widgetService.updateWidget(widgetCode, widgetRequest);
        return new ResponseEntity<>(new RestResponse(widgetDto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/widgets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, name = "widget")
    public ResponseEntity<RestResponse<WidgetDto>> addWidget(@Valid @RequestBody WidgetRequest widgetRequest, BindingResult bindingResult) throws ApsSystemException {
        logger.trace("add widget. body {}: ", widgetRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        this.widgetValidator.validate(widgetRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        WidgetDto widgetDto = this.widgetService.addWidget(widgetRequest);
        return new ResponseEntity<>(new RestResponse(widgetDto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/widgets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<List<WidgetDto>>> getWidgets(RestListRequest requestList) {
        logger.trace("get widget list {}", requestList);
        this.getWidgetValidator().validateRestListRequest(requestList, WidgetDto.class);
        PagedMetadata<WidgetDto> result = this.widgetService.getWidgets(requestList);
        this.getWidgetValidator().validateRestListResult(requestList, result);
        return new ResponseEntity<>(new RestResponse(result.getBody(), null, result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/widgets/{widgetCode}/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<WidgetInfoDto>> getWidgetInfo(@PathVariable String widgetCode) {
        logger.trace("getWidgetInfo by code {}", widgetCode);
        WidgetInfoDto info = this.widgetService.getWidgetInfo(widgetCode);
        return new ResponseEntity<>(new RestResponse(info), HttpStatus.OK);
    }

    public IWidgetService getWidgetService() {
        return widgetService;
    }

    public void setWidgetService(IWidgetService widgetService) {
        this.widgetService = widgetService;
    }

    public WidgetValidator getWidgetValidator() {
        return widgetValidator;
    }

    public void setWidgetValidator(WidgetValidator widgetValidator) {
        this.widgetValidator = widgetValidator;
    }
}
