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
package org.entando.entando.web.guifragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.role.Permission;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.entando.entando.aps.system.exception.RestRourceNotFoundException;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentService;
import org.entando.entando.aps.system.services.guifragment.model.GuiFragmentDto;
import org.entando.entando.aps.system.services.guifragment.model.GuiFragmentDtoSmall;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.common.model.RestResponse;
import org.entando.entando.web.guifragment.model.GuiFragmentRequestBody;
import org.entando.entando.web.guifragment.validator.GuiFragmentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/fragments")
public class GuiFragmentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IGuiFragmentService guiFragmentService;

    @Autowired
    private GuiFragmentValidator guiFragmentValidator;

    protected IGuiFragmentService getGuiFragmentService() {
        return guiFragmentService;
    }

    public void setGuiFragmentService(IGuiFragmentService guiFragmentService) {
        this.guiFragmentService = guiFragmentService;
    }

    protected GuiFragmentValidator getGuiFragmentValidator() {
        return guiFragmentValidator;
    }

    public void setGuiFragmentValidator(GuiFragmentValidator guiFragmentValidator) {
        this.guiFragmentValidator = guiFragmentValidator;
    }


    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getGuiFragments(RestListRequest requestList) throws JsonProcessingException {
        this.getGuiFragmentValidator().validateRestListRequest(requestList, GuiFragmentDto.class);
        PagedMetadata<GuiFragmentDtoSmall> result = this.getGuiFragmentService().getGuiFragments(requestList);
        this.getGuiFragmentValidator().validateRestListResult(requestList, result);
        logger.debug("Main Response -> {}", result);
        return new ResponseEntity<>(new RestResponse(result.getBody(), null, result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/{fragmentCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getGuiFragment(@PathVariable String fragmentCode) {
        GuiFragmentDto fragment = this.getGuiFragmentService().getGuiFragment(fragmentCode);
        return new ResponseEntity<>(new RestResponse(fragment), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> addGuiFragment(@Valid @RequestBody GuiFragmentRequestBody guiFragmentRequest, BindingResult bindingResult) throws ApsSystemException {
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        this.getGuiFragmentValidator().validate(guiFragmentRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        GuiFragmentDto fragment = this.getGuiFragmentService().addGuiFragment(guiFragmentRequest);
        return new ResponseEntity<>(new RestResponse(fragment), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/{fragmentCode}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> updateGuiFragment(@PathVariable String fragmentCode, @Valid @RequestBody GuiFragmentRequestBody guiFragmentRequest, BindingResult bindingResult) {
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        int result = this.getGuiFragmentValidator().validateBody(fragmentCode, guiFragmentRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            if (404 == result) {
                throw new RestRourceNotFoundException(GuiFragmentValidator.ERRCODE_FRAGMENT_DOES_NOT_EXISTS, "fragment", fragmentCode);
            } else {
                throw new ValidationGenericException(bindingResult);
            }
        }
        GuiFragmentDto fragment = this.getGuiFragmentService().updateGuiFragment(guiFragmentRequest);
        return new ResponseEntity<>(new RestResponse(fragment), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/{fragmentCode}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> deleteGuiFragment(@PathVariable String fragmentCode) throws ApsSystemException {
        logger.info("deleting {}", fragmentCode);
        this.getGuiFragmentService().removeGuiFragment(fragmentCode);
        Map<String, String> result = new HashMap<>();
        result.put("code", fragmentCode);
        return new ResponseEntity<>(new RestResponse(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.SUPERUSER)
    @RequestMapping(value = "/info/plugins", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getPluginCodes() throws ApsSystemException {
        logger.info("loading plugin list");
        List<String> plugins = this.getGuiFragmentService().getPluginCodes();

        return new ResponseEntity<>(new RestResponse(plugins), HttpStatus.OK);
    }

}
