/*
 * Copyright (c) 2018 Fondesa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fondesa.kpermissions.builder

import com.fondesa.kpermissions.controller.DefaultPermissionLifecycleController
import com.fondesa.kpermissions.controller.PermissionLifecycleController
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonceGenerator
import com.fondesa.kpermissions.request.runtime.nonce.RationalePermissionNonceGenerator

/**
 * Created by antoniolig on 06/01/18.
 */
abstract class BasePermissionRequestBuilder : PermissionRequestBuilder {

    private var permissions: Array<out String>? = null
    private var lifecycleController: PermissionLifecycleController? = null
    private var nonceGenerator: PermissionNonceGenerator? = null
    private var runtimeHandlerProvider: RuntimePermissionHandlerProvider? = null

    override fun permissions(vararg permissions: String): PermissionRequestBuilder = apply {
        this.permissions = permissions
    }

    override fun lifecycleController(lifecycleController: PermissionLifecycleController): PermissionRequestBuilder = apply {
        this.lifecycleController = lifecycleController
    }

    override fun nonceGenerator(nonceGenerator: PermissionNonceGenerator): PermissionRequestBuilder = apply {
        this.nonceGenerator = nonceGenerator
    }

    override fun runtimeHandlerProvider(runtimeHandlerProvider: RuntimePermissionHandlerProvider): PermissionRequestBuilder = apply {
        this.runtimeHandlerProvider = runtimeHandlerProvider
    }

    override fun build(): PermissionRequest {
        val permissions = permissions
        if (permissions == null || permissions.isEmpty()) {
            // Throw an exception if there isn't any permission specified.
            throw IllegalArgumentException("You have to specify at least one permission.")
        }

        // Instantiate the default controller if a custom one isn't set.
        val controller = lifecycleController ?: DefaultPermissionLifecycleController()

        // Instantiate the default NonceGenerator if a custom one isn't set.
        val nonceGenerator = nonceGenerator ?: RationalePermissionNonceGenerator()

        // Get the runtime handler.
        val runtimeHandlerProvider = runtimeHandlerProvider
                ?: throw IllegalArgumentException("A runtime handler is necessary to request the permissions.")

        return createRequest(permissions,
                controller,
                nonceGenerator,
                runtimeHandlerProvider)
    }

    override fun send(): PermissionRequest {
        // Build the request.
        val request = build()
        // Send it directly.
        request.send()
        return request
    }

    abstract fun createRequest(permissions: Array<out String>,
                               lifecycleController: PermissionLifecycleController,
                               nonceGenerator: PermissionNonceGenerator,
                               runtimeHandlerProvider: RuntimePermissionHandlerProvider): PermissionRequest
}